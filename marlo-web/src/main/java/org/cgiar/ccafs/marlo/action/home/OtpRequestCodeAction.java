/*****************************************************************
 * This file is part of Managing Agricultural Research for Learning &
 * Outcomes Platform (MARLO).
 * MARLO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * MARLO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with MARLO. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/

package org.cgiar.ccafs.marlo.action.home;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CustomParameterManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.OtpChallengeManager;
import org.cgiar.ccafs.marlo.data.model.CustomParameter;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.security.ReturnUrls;
import org.cgiar.ccafs.marlo.security.otp.ClientIpResolver;
import org.cgiar.ccafs.marlo.security.otp.OtpCodes;
import org.cgiar.ccafs.marlo.security.otp.OtpDomainPolicy;
import org.cgiar.ccafs.marlo.security.otp.OtpEligibility;
import org.cgiar.ccafs.marlo.security.otp.OtpEligibility.Basis;
import org.cgiar.ccafs.marlo.security.otp.OtpKeys;
import org.cgiar.ccafs.marlo.security.otp.OtpLog;
import org.cgiar.ccafs.marlo.security.otp.OtpLog.RequestOutcome;
import org.cgiar.ccafs.marlo.security.otp.OtpPending;
import org.cgiar.ccafs.marlo.security.otp.OtpRateLimiter;
import org.cgiar.ccafs.marlo.security.otp.OtpRateLimiter.Decision;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.OtpMailSender;
import org.cgiar.ccafs.marlo.utils.UnitTextProvider;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;

import com.opensymphony.xwork2.TextProvider;
import org.apache.shiro.SecurityUtils;
import org.apache.struts2.action.ParameterNameAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Request a sign-in code (CHG-OTP-LOGIN-001-T10, FN-001, FN-004 to FN-008, SEC-006, SEC-008, SEC-010, OPS-001,
 * NF-001; design 5.1 and DD-5): the first pre-authentication action of the Center email one-time-code sign-in.
 * <p>
 * It answers a small JSON map, {@link #getResponse()}, with a {@code status} from the closed vocabulary of design 4:
 * {@code sent} (with the masked {@code destination}), {@code domainNotEnabled}, {@code termsRequired},
 * {@code rateLimited} and {@code unavailable}. {@link #execute()} always returns {@code SUCCESS}, never throws, and
 * writes exactly one {@code auth.otp.request} line through {@link OtpLog}. Everything runs in {@code execute()}: the
 * class does not override {@code validate()}, because a transactional call there would commit what {@code prepare()}
 * left half-edited.
 * <p>
 * <b>Order.</b> No step before the eligibility check reads account state, so the steps before it answer identically
 * for every account (JD-1):
 * <ol>
 * <li>step 0: only a POST is served (T22). Any other method, or no request at all, answers {@code unavailable}
 * and logs {@code bad_method}, before the limiter, the session or any store is touched;</li>
 * <li>an unconfigured secret answers {@code domainNotEnabled}. It comes first after step 0 because the limiter
 * cannot count without keys and would answer {@code unavailable} instead (SEC-008);</li>
 * <li>the limiter counts the email, the address and the 30-second cooldown, before any lookup (SEC-010, FN-008);</li>
 * <li>the shape of the email and the unit id, then whether the code is offered on the unit through the uncached read:
 * the unit's switch is on and the domain is not excluded (FN-001, FN-006, DD-13), or the unit's password switch is
 * off, which offers the code to every account that is not a Cognito account whatever the code switch and the
 * exclusion say (CHG-PWD-SWITCH-001 FN-004, SEC-004). The step is account-blind, so it cannot tell a Cognito account
 * from another one. Then the terms: each refusal answers a status that names no account state;</li>
 * <li>{@link OtpEligibility} decides eligibility, a Cognito account that the unit offers no code of its own included
 * (FN-005, SEC-004). A failure of the predicate is not an answer of its own: whether it throws can depend on the
 * account, so it is treated as "not eligible" and the neutral {@code sent} is answered (SEC-006);</li>
 * <li>the text provider of the unit's custom i18n file is built for <b>every</b> request that gets this far, eligible
 * or not, so the read of {@code crp_custom_file} adds work to neither path alone (SEC-006, T20);</li>
 * <li>a challenge row is issued for <b>every</b> account that reaches this step, with a fresh random code, and
 * supersedes the email's open rows in the same transaction (FN-007, DD-5). Only an eligible account's code is
 * handed to the mailer, and only to the normalised email held in the {@link OtpPending}; an ineligible account's
 * code is discarded unsent;</li>
 * <li>the purge of old rows runs in its own transaction after the issue and never affects the answer (OPS-003);</li>
 * <li>a fresh {@link OtpPending} replaces any earlier one in the Shiro session, under
 * {@code APConstants.OTP_PENDING_CHALLENGE}, as Cognito's pending authorization does. A failed issue, or a session
 * that cannot be written, answers {@code unavailable}.</li>
 * </ol>
 * The helpers ({@link OtpKeys}, {@link OtpRateLimiter}, {@link OtpCodes}, {@link OtpEligibility}, the mailer) are
 * container singletons injected through the constructor, never built per request. The expiry and the purge cutoff
 * come from the same clock as {@code now}. Nothing logs a store exception's message: a duplicate-nonce error carries
 * the nonce, so only the class name is written.
 */
public class OtpRequestCodeAction extends BaseAction implements ParameterNameAware {

  /** The send seam: the production one is {@link OtpMailSender#sendAsync}; tests substitute a recorder. */
  @FunctionalInterface
  interface CodeSender {

    void send(TextProvider texts, String assetsUrl, String recipient, String code, String guAcronym,
      Long globalUnitId);
  }

  private static final long serialVersionUID = 1L;

  private static final Logger LOG = LoggerFactory.getLogger(OtpRequestCodeAction.class);

  static final String STATUS_SENT = "sent";
  static final String STATUS_DOMAIN_NOT_ENABLED = "domainNotEnabled";
  static final String STATUS_TERMS_REQUIRED = "termsRequired";
  static final String STATUS_RATE_LIMITED = "rateLimited";
  static final String STATUS_UNAVAILABLE = "unavailable";
  static final String KEY_STATUS = "status";
  static final String KEY_DESTINATION = "destination";

  /**
   * FN-010: the challenge lifetime. The email states {@code OtpMailSender.CODE_VALIDITY_MINUTES}; a test locks the
   * two together, so the copy and the real expiry cannot drift.
   */
  static final Duration CODE_VALIDITY = Duration.ofMinutes(5);

  /** OPS-003: challenges past their expiry by more than this are purged. */
  static final Duration PURGE_AFTER_EXPIRY = Duration.ofHours(1);

  /** Design 5.1 step 2: the longest accepted email, in characters of the normalised form. */
  static final int MAX_EMAIL_LENGTH = 254;

  /** RJ-9: the only request parameters this action binds. Every inherited setter stays unreachable from a request. */
  private static final Set<String> ACCEPTED_PARAMETERS =
    Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("email", "globalUnitId", "agree")));

  private static final String REFERER_HEADER = "Referer";
  private static final String MAC_ALGORITHM = "HmacSHA256";

  /** ASCII digits only: {@code Long.parseLong} would also accept the digits of other scripts. */
  private static final Pattern NUMERIC_ID = Pattern.compile("[0-9]{1,18}");

  private final OtpKeys keys;
  private final OtpRateLimiter limiter;
  private final OtpCodes codes;
  private final OtpEligibility eligibility;
  private final OtpChallengeManager challengeManager;
  private final GlobalUnitManager globalUnitManager;
  private final CustomParameterManager customParameterManager;
  private final CodeSender codeSender;
  private final ClientIpResolver ipResolver;
  private final Clock clock;

  // Request parameters. Plain strings on purpose: a conversion error would end the request before execute(), and so
  // before the limiter, whereas the shape check of design 5.1 step 2 must run after it.
  private String email;
  private String globalUnitId;
  private String agree;

  // Result. It starts as "unavailable", so an interrupted request that never reaches execute() still serialises a
  // well-formed answer.
  private Map<String, Object> response = status(STATUS_UNAVAILABLE);

  // What the single log line of the call needs.
  private String domain = "";
  private Long unitId;
  private String acronym = "";
  private boolean acronymResolved;
  private boolean eligibilityFailed;
  private boolean viaPasswordOff;

  /**
   * The only constructor the container may use; the one below is the test seam. The mailer is a container singleton,
   * so its 2-thread executor and its queue of 100 are shared by every request.
   */
  @Inject
  public OtpRequestCodeAction(APConfig config, OtpKeys keys, OtpRateLimiter limiter, OtpCodes codes,
    OtpEligibility eligibility, OtpChallengeManager challengeManager, GlobalUnitManager globalUnitManager,
    CustomParameterManager customParameterManager, OtpMailSender mailSender) {
    this(config, keys, limiter, codes, eligibility, challengeManager, globalUnitManager, customParameterManager,
      mailSender::sendAsync, new ClientIpResolver(config), Clock.systemUTC());
  }

  OtpRequestCodeAction(APConfig config, OtpKeys keys, OtpRateLimiter limiter, OtpCodes codes,
    OtpEligibility eligibility, OtpChallengeManager challengeManager, GlobalUnitManager globalUnitManager,
    CustomParameterManager customParameterManager, CodeSender codeSender, ClientIpResolver ipResolver, Clock clock) {
    super(config);
    this.keys = keys;
    this.limiter = limiter;
    this.codes = codes;
    this.eligibility = eligibility;
    this.challengeManager = challengeManager;
    this.globalUnitManager = globalUnitManager;
    this.customParameterManager = customParameterManager;
    this.codeSender = codeSender;
    this.ipResolver = ipResolver;
    this.clock = clock;
  }

  private static Map<String, Object> status(String status) {
    Map<String, Object> answer = new LinkedHashMap<String, Object>();
    answer.put(KEY_STATUS, status);
    return answer;
  }

  private static long elapsedMs(long startedNanos) {
    return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedNanos);
  }

  /** Only the class name is written: the message of a store or mail exception can carry the nonce or the address. */
  private static void logFault(String stage, Throwable failure) {
    LOG.warn("auth.otp.fault stage={} cause={}", stage, failure.getClass().getSimpleName());
  }

  /**
   * @return the id as a number, or {@code null} when it is not 1 to 18 ASCII digits
   */
  private static Long parseUnitId(String raw) {
    String trimmed = raw == null ? "" : raw.trim();
    return NUMERIC_ID.matcher(trimmed).matches() ? Long.valueOf(trimmed) : null;
  }

  /** HMAC-SHA256 of the normalised email under {@code k_email}, as lower-case hex (SEC-002, design 3). */
  private String emailMac(String normalisedEmail) {
    byte[] key = this.keys.emailKey();
    try {
      Mac mac = Mac.getInstance(MAC_ALGORITHM);
      mac.init(new SecretKeySpec(key, MAC_ALGORITHM));
      return HexFormat.of().formatHex(mac.doFinal(normalisedEmail.getBytes(StandardCharsets.UTF_8)));
    } catch (GeneralSecurityException e) {
      // HmacSHA256 is a mandatory JCA algorithm and the key is non-empty: unreachable on a conforming JRE.
      throw new IllegalStateException("HmacSHA256 is unavailable", e);
    } finally {
      Arrays.fill(key, (byte) 0);
    }
  }

  /**
   * RJ-9: refuses every request parameter but {@code email}, {@code globalUnitId} and {@code agree}, by exact name,
   * so a dotted or OGNL name, and the setters inherited from {@link BaseAction}, can never be bound.
   */
  @Override
  public boolean acceptableParameterName(String parameterName) {
    return parameterName != null && ACCEPTED_PARAMETERS.contains(parameterName);
  }

  @Override
  public String execute() {
    long started = System.nanoTime();
    RequestOutcome outcome;
    try {
      // Step 0 (design 5.1, T22): only a POST is served, and a refusal changes nothing, so the code and the email
      // can never travel in a URL that an access log records.
      outcome = this.isPostRequest() ? this.process() : this.refuse(STATUS_UNAVAILABLE, RequestOutcome.BAD_METHOD);
    } catch (RuntimeException | Error e) {
      // Last line of defence: whatever failed, the caller gets a well-formed answer and never a 500.
      logFault("request", e);
      this.response = status(STATUS_UNAVAILABLE);
      outcome = RequestOutcome.INTERNAL_ERROR;
    }
    OtpLog.request(outcome, this.acronymForLog(), this.domain, elapsedMs(started), this.viaPasswordOff);
    return SUCCESS;
  }

  /**
   * @return the answer: {@code {status, destination?}}. It is the very map the JSON result serialises (design 4), and
   *         it is never {@code null}
   */
  public Map<String, Object> getResponse() {
    return this.response;
  }

  public void setAgree(String agree) {
    this.agree = agree;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setGlobalUnitId(String globalUnitId) {
    this.globalUnitId = globalUnitId;
  }

  /** The Global Unit acronym for the log line and the delivery line, resolved once and never thrown. */
  private String acronymForLog() {
    if (this.unitId == null) {
      return "";
    }
    if (!this.acronymResolved) {
      this.acronymResolved = true;
      try {
        GlobalUnit unit = this.globalUnitManager.getGlobalUnitById(this.unitId.longValue());
        this.acronym = unit == null || unit.getAcronym() == null ? "" : unit.getAcronym();
      } catch (RuntimeException e) {
        logFault("acronym", e);
      }
    }
    return this.acronym;
  }

  /**
   * Design 5.1 step 0: whether the call is a POST. A missing request is not one, and the method is compared ignoring
   * case, the way {@code ValidateUserAction} does. It reads nothing but the request line, so it answers identically for
   * every account.
   */
  private boolean isPostRequest() {
    return this.request != null && "POST".equalsIgnoreCase(this.request.getMethod());
  }

  /** Design 5.1 step 1: the resolved client address, never {@code null} (an unknown peer shares one bucket). */
  private String clientIp() {
    return this.request == null ? this.ipResolver.resolve((String) null, null) : this.ipResolver.resolve(this.request);
  }

  /**
   * Design 5.1 step 4: whether the code is offered on the unit, read uncached (DD-12, DD-13): the unit's switch is on
   * and the submitted email's domain is not on its exclusion list, or the unit's password switch is off
   * (CHG-PWD-SWITCH-001 FN-004). It reads nothing about the account, so a Cognito account passes it too and is
   * refused by {@link #isEligible(Long)} with the neutral answer.
   */
  private boolean isCodeOffered(Long unit) {
    return this.eligibility.isOffered(this.email, unit.longValue());
  }

  /**
   * Design 5.1 step 6. A failure of the predicate is mapped to "not eligible", the same answer an ineligible account
   * gets: whether the predicate throws can depend on the account (a CGIAR user on a unit with no type, for
   * example), so any other answer would be an oracle (SEC-006). The server log still records it as
   * {@code internal_error}.
   */
  private boolean isEligible(Long unit) {
    try {
      Basis basis = this.eligibility.eligibilityBasis(this.email, unit.longValue());
      // Server log only (design 11): the line says the code was issued because the password is off. The answer to the
      // caller is the same neutral map either way, and OtpLog writes the suffix for outcome=sent alone.
      this.viaPasswordOff = basis == Basis.PASSWORD_SWITCH_OFF;
      return basis != Basis.NONE;
    } catch (RuntimeException | Error e) {
      logFault("eligibility", e);
      this.eligibilityFailed = true;
      return false;
    }
  }

  /**
   * Design 5.1 step 8: the purge runs after the issue committed, in a transaction of its own, so it never waits for a
   * lock this request held. A failure is logged and ignored.
   */
  private void purge(Instant now) {
    try {
      this.challengeManager.purgeExpired(now.minus(PURGE_AFTER_EXPIRY));
    } catch (RuntimeException e) {
      logFault("purge", e);
    }
  }

  /**
   * The whole call, up to the answer.
   *
   * @return the outcome for the log line; {@link #getResponse()} holds the answer
   */
  private RequestOutcome process() {
    String shapeDomain = OtpDomainPolicy.domainOf(this.email);
    this.domain = shapeDomain == null ? "" : shapeDomain;

    if (!this.keys.isConfigured()) {
      return this.refuse(STATUS_DOMAIN_NOT_ENABLED, RequestOutcome.UNCONFIGURED);
    }

    // Step 1. The bucket value is the normalised form, which is empty for an address the policy rejects: such
    // requests share one bucket and still never reach a lookup. It is not used for anything else before step 2.
    Decision decision =
      this.limiter.checkRequestCode(OtpDomainPolicy.normaliseEmail(this.email), this.clientIp());
    if (decision == Decision.LIMITED) {
      return this.refuse(STATUS_RATE_LIMITED, RequestOutcome.RATE_LIMITED);
    }
    if (decision != Decision.ALLOWED) {
      return this.refuse(STATUS_UNAVAILABLE, RequestOutcome.INTERNAL_ERROR);
    }

    // Step 2.
    Long unit = parseUnitId(this.globalUnitId);
    if (shapeDomain == null || unit == null
      || OtpDomainPolicy.normaliseEmail(this.email).length() > MAX_EMAIL_LENGTH) {
      return this.refuse(STATUS_DOMAIN_NOT_ENABLED, RequestOutcome.DENIED_DOMAIN);
    }
    this.unitId = unit;

    // Step 4. An unknown unit has no switch, so its password reads as on, and it ends here too.
    if (!this.isCodeOffered(unit)) {
      return this.refuse(STATUS_DOMAIN_NOT_ENABLED, RequestOutcome.DENIED_DOMAIN);
    }

    // Step 5.
    boolean agreed = "true".equalsIgnoreCase(this.agree);
    if (!agreed) {
      return this.refuse(STATUS_TERMS_REQUIRED, RequestOutcome.TERMS_REQUIRED);
    }

    // Step 6. The shape check above proved the email has a domain, so its normalised form is a real address.
    String normalisedEmail = OtpDomainPolicy.normaliseEmail(this.email);
    boolean eligible = this.isEligible(unit);
    // Built for every request that gets here, so the read of the unit's custom file is not eligible-only (SEC-006).
    TextProvider texts = this.unitTexts(unit);

    // Step 7. A row for every account; the code is real, and it is mailed only for an eligible one.
    String nonce = this.codes.newNonce();
    String code = this.codes.newCode();
    String codeMac = this.codes.mac(nonce, code);
    Instant now = this.clock.instant();
    OtpPending pending = new OtpPending(nonce, normalisedEmail, unit.longValue(), this.returnUrl(), agreed);
    try {
      this.challengeManager.issue(this.emailMac(pending.getEmail()), unit.longValue(), nonce, codeMac,
        now.plus(CODE_VALIDITY));
    } catch (RuntimeException e) {
      // A lock or a deadlock is a store failure: unavailable, nothing mailed, nothing written to the session.
      logFault("issue", e);
      return this.refuse(STATUS_UNAVAILABLE, RequestOutcome.INTERNAL_ERROR);
    }
    if (eligible) {
      this.handOff(texts, pending.getEmail(), code);
    }

    // Step 8.
    this.purge(now);

    // Step 9.
    try {
      SecurityUtils.getSubject().getSession().setAttribute(APConstants.OTP_PENDING_CHALLENGE, pending);
    } catch (RuntimeException e) {
      logFault("session", e);
      return this.refuse(STATUS_UNAVAILABLE, RequestOutcome.INTERNAL_ERROR);
    }

    // Step 10. The same map for every account state, apart from the masked email the caller itself submitted.
    this.response = status(STATUS_SENT);
    this.response.put(KEY_DESTINATION, OtpDomainPolicy.mask(this.email));
    if (this.eligibilityFailed) {
      return RequestOutcome.INTERNAL_ERROR;
    }
    return eligible ? RequestOutcome.SENT : RequestOutcome.NEUTRAL;
  }

  /**
   * The text provider of the code email: the unit's custom i18n file over {@code global.properties}, built for this
   * email only (FN-017, UI-005, T20). No Global Unit is in the session before sign-in, so the file is read from the
   * unit's own {@code crp_custom_file} parameter, the value {@code InternationalitazionFileInterceptor} reads from the
   * session after sign-in. The request's own provider, the shared Struts provider and the session are not touched.
   * A failure is logged and answered with the request's own provider, which resolves {@code global.properties}: the
   * email is then sent unbranded, and the answer and the timing do not change (SEC-006).
   */
  private TextProvider unitTexts(Long unit) {
    try {
      CustomParameter parameter = this.customParameterManager
        .getCustomParameterByParameterKeyAndGlobalUnitId(APConstants.CRP_CUSTOM_FILE, unit.longValue());
      return UnitTextProvider.forCustomFile(parameter == null ? null : parameter.getValue());
    } catch (RuntimeException e) {
      logFault("texts", e);
      return this;
    }
  }

  /**
   * Hands the code to the mailer. The recipient is the normalised email held in the pending challenge, never the raw
   * request parameter. {@code sendAsync} does not throw by contract, but an {@code Error} from it must not turn the
   * neutral answer into a server error, and a failure here reveals nothing: the answer was already {@code sent} for
   * every account.
   */
  private void handOff(TextProvider texts, String recipient, String code) {
    try {
      this.codeSender.send(texts, this.getBaseUrlCdn(), recipient, code, this.acronymForLog(), this.unitId);
    } catch (RuntimeException | Error e) {
      logFault("mail", e);
    }
  }

  private RequestOutcome refuse(String status, RequestOutcome outcome) {
    this.response = status(status);
    return outcome;
  }

  /** Design 5.1 step 9: the Referer, kept only when it is a same-origin link that is not an authentication endpoint. */
  private String returnUrl() {
    String referer = this.request == null ? null : this.request.getHeader(REFERER_HEADER);
    return ReturnUrls.sameOriginOrNull(referer, this.getBaseUrl());
  }
}
