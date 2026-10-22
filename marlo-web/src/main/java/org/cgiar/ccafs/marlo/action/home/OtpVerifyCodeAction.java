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

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpUserManager;
import org.cgiar.ccafs.marlo.data.manager.CustomParameterManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.OtpChallengeManager;
import org.cgiar.ccafs.marlo.data.manager.OtpChallengeManager.MismatchOutcome;
import org.cgiar.ccafs.marlo.data.manager.ParameterManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.OtpChallenge;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.OtpAuthenticationToken;
import org.cgiar.ccafs.marlo.security.ShiroRequestSessionCacheResetter;
import org.cgiar.ccafs.marlo.security.otp.ClientIpResolver;
import org.cgiar.ccafs.marlo.security.otp.OtpCodes;
import org.cgiar.ccafs.marlo.security.otp.OtpDomainPolicy;
import org.cgiar.ccafs.marlo.security.otp.OtpEligibility;
import org.cgiar.ccafs.marlo.security.otp.OtpLog;
import org.cgiar.ccafs.marlo.security.otp.OtpLog.VerifyOutcome;
import org.cgiar.ccafs.marlo.security.otp.OtpPending;
import org.cgiar.ccafs.marlo.security.otp.OtpRateLimiter;
import org.cgiar.ccafs.marlo.security.otp.OtpRateLimiter.Decision;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.time.Clock;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import com.opensymphony.xwork2.ActionContext;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.session.Session;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.action.ParameterNameAware;
import org.apache.struts2.dispatcher.SessionMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Verify a sign-in code (CHG-OTP-LOGIN-001-T11, FN-011 to FN-016, SEC-004 to SEC-007, SEC-010, OPS-001; design 5.2,
 * DD-2, DD-3, DD-5): the second pre-authentication action of the Center email one-time-code sign-in. It extends
 * {@link LoginAction} to reach the shared {@code finishLogin} tail, so a code sign-in lands exactly where a password
 * sign-in does.
 * <p>
 * It answers a small JSON map, {@link #getResponse()}, with a {@code status} from the closed vocabulary of design 4:
 * {@code ok} (with the {@code redirect}), {@code mismatch}, {@code exhausted}, {@code invalid}, {@code rateLimited},
 * {@code unavailable} and {@code failed} (with the generic login redirect). {@link #execute()} always returns
 * {@code SUCCESS}, never throws, and writes exactly one {@code auth.otp.verify} line through {@link OtpLog}.
 * Everything runs in {@code execute()}: the class does not override {@code validate()}, because a transactional call
 * there would commit what {@code prepare()} left half-edited.
 * <p>
 * <b>Order</b> (design 5.2):
 * <ol>
 * <li>step 0: only a POST is served (T22). Any other method, or no request at all, answers {@code unavailable}
 * and logs {@code bad_method}: no attempt is counted, nothing is consumed, the session is unchanged and the limiter is
 * not hit;</li>
 * <li>the limiter counts the pending email and the address, before any lookup (SEC-010, JD-3);</li>
 * <li>the code must be exactly six ASCII digits, else {@code mismatch}, spending no attempt;</li>
 * <li>no {@link OtpPending} in the Shiro session answers {@code mismatch} (FN-016);</li>
 * <li>the row is read by the pending nonce. A missing or consumed row is {@code invalid}, an exhausted one
 * {@code exhausted}, an expired one {@code invalid}. These Java checks only ever <b>refuse</b> early: acceptance
 * comes only from the conditional update of step 7, because the Java read of a {@code DATETIME} can shift on a
 * non-UTC JVM;</li>
 * <li>the MAC of the code is compared in constant time through {@link OtpCodes#matches};</li>
 * <li>a mismatch is counted by {@code registerMismatch}, which answers {@code mismatch}, {@code exhausted} or, when a
 * concurrent consume or supersede won the race, {@code invalid} (RJ-3);</li>
 * <li>a match is consumed by a conditional update, once (FN-013);</li>
 * <li>the pending challenge is removed and {@link OtpEligibility} runs again, uncached (FN-015). Any failure of the
 * predicate answers {@code invalid}, like a refusal: whether it throws can depend on the account (SEC-006). A decoy
 * that guessed its random code ends here;</li>
 * <li>the Cognito callback sequence: the loaded user, {@code agree_terms}, {@code saveLastLogin}, a detached
 * {@code user} for the tail, the session stop, {@code Subject.login} with the only {@link OtpAuthenticationToken}
 * built in the code base, the reset of the request session cache and a fresh session view (SEC-005);</li>
 * <li>{@code finishLogin} with the <b>loaded</b> entity, never the detached field.</li>
 * </ol>
 * No transaction is opened here. {@code findByNonce} is a plain read; {@code registerMismatch} and {@code consume}
 * are each their own {@code @Transactional} call, so the re-read inside {@code registerMismatch} never shares a
 * snapshot with the read of step 4. The helpers are container singletons injected through the constructor, never
 * built per request. Nothing logs a store exception's message: it can carry the nonce, so only the class name is
 * written.
 */
public class OtpVerifyCodeAction extends LoginAction implements ParameterNameAware {

  private static final long serialVersionUID = 1L;

  private static final Logger LOG = LoggerFactory.getLogger(OtpVerifyCodeAction.class);

  static final String STATUS_OK = "ok";
  static final String STATUS_MISMATCH = "mismatch";
  static final String STATUS_EXHAUSTED = "exhausted";
  static final String STATUS_INVALID = "invalid";
  static final String STATUS_FAILED = "failed";
  static final String KEY_STATUS = OtpRequestCodeAction.KEY_STATUS;
  static final String KEY_REDIRECT = "redirect";

  /** Design 5.2 step 2: ASCII digits only; {@code \d} and {@code Long.parseLong} would also take other scripts. */
  private static final Pattern SIX_DIGITS = Pattern.compile("[0-9]{6}");

  private final OtpRateLimiter limiter;
  private final OtpCodes codes;
  private final OtpEligibility eligibility;
  private final OtpChallengeManager challengeManager;
  // Own copy: LoginAction's GlobalUnitManager field is private.
  private final GlobalUnitManager globalUnitManager;
  private final ClientIpResolver ipResolver;
  private final Clock clock;

  // The only request parameter. A plain string on purpose: a conversion error would end the request before
  // execute(), and so before the limiter, whereas the shape check of design 5.2 step 2 must run after it.
  private String code;

  // Result. It starts as "unavailable", so an interrupted request that never reaches execute() still serialises a
  // well-formed answer.
  private Map<String, Object> response = status(OtpRequestCodeAction.STATUS_UNAVAILABLE);

  // What the single log line of the call needs.
  private String domain = "";
  private Long unitId;
  private String acronym = "";
  private boolean acronymResolved;
  private boolean eligibilityFailed;

  /**
   * The only constructor the container may use; the one below is the test seam. The managers feed the inherited
   * {@code finishLogin} tail.
   */
  @Inject
  public OtpVerifyCodeAction(APConfig config, UserManager userManager, GlobalUnitManager globalUnitManager,
    CrpUserManager crpUserManager, CustomParameterManager customParameterManager, ParameterManager parameterManager,
    OtpRateLimiter limiter, OtpCodes codes, OtpEligibility eligibility, OtpChallengeManager challengeManager) {
    this(config, userManager, globalUnitManager, crpUserManager, customParameterManager, parameterManager, limiter,
      codes, eligibility, challengeManager, new ClientIpResolver(config), Clock.systemUTC());
  }

  OtpVerifyCodeAction(APConfig config, UserManager userManager, GlobalUnitManager globalUnitManager,
    CrpUserManager crpUserManager, CustomParameterManager customParameterManager, ParameterManager parameterManager,
    OtpRateLimiter limiter, OtpCodes codes, OtpEligibility eligibility, OtpChallengeManager challengeManager,
    ClientIpResolver ipResolver, Clock clock) {
    super(config, userManager, globalUnitManager, crpUserManager, customParameterManager, parameterManager,
      eligibility);
    this.limiter = limiter;
    this.codes = codes;
    this.eligibility = eligibility;
    this.challengeManager = challengeManager;
    this.globalUnitManager = globalUnitManager;
    this.ipResolver = ipResolver;
    this.clock = clock;
  }

  private static long elapsedMs(long startedNanos) {
    return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedNanos);
  }

  /** Only the class name is written: the message of a store exception can carry the nonce. */
  private static void logFault(String stage, Throwable failure) {
    LOG.warn("auth.otp.fault stage={} cause={}", stage, failure.getClass().getSimpleName());
  }

  private static Map<String, Object> status(String status) {
    Map<String, Object> answer = new LinkedHashMap<String, Object>();
    answer.put(KEY_STATUS, status);
    return answer;
  }

  /**
   * Design 5.2 step 0: whether the call is a POST. A missing request is not one, and the method is compared ignoring
   * case, the way {@code ValidateUserAction} does.
   */
  private boolean isPostRequest() {
    return this.request != null && "POST".equalsIgnoreCase(this.request.getMethod());
  }

  /** Design 5.2 step 1: the resolved client address, never {@code null} (an unknown peer shares one bucket). */
  private String clientIp() {
    return this.request == null ? this.ipResolver.resolve((String) null, null) : this.ipResolver.resolve(this.request);
  }

  /**
   * RJ-9: refuses every request parameter but {@code code}, by exact name, so a dotted or OGNL name, and the setters
   * inherited from {@link LoginAction} and {@code BaseAction}, can never be bound.
   */
  @Override
  public boolean acceptableParameterName(String parameterName) {
    return "code".equals(parameterName);
  }

  @Override
  public String execute() {
    long started = System.nanoTime();
    VerifyOutcome outcome;
    try {
      // Step 0 (design 5.2, T22): only a POST is served. A refusal counts no attempt, consumes nothing, leaves the
      // session as it is and never reaches the limiter, so a GET carrying ?code= can neither guess nor sign in.
      outcome = this.isPostRequest() ? this.process() : this.refuse(OtpRequestCodeAction.STATUS_UNAVAILABLE,
        VerifyOutcome.BAD_METHOD);
    } catch (RuntimeException e) {
      // Last line of defence: whatever failed, the caller gets a well-formed answer and never a 500.
      logFault("verify", e);
      this.response = status(OtpRequestCodeAction.STATUS_UNAVAILABLE);
      outcome = VerifyOutcome.INTERNAL_ERROR;
    }
    OtpLog.verify(outcome, this.acronymForLog(), this.domain, elapsedMs(started));
    return SUCCESS;
  }

  /**
   * Builds a session view bound to whatever Shiro session is current <b>at the moment this is called</b>. The
   * {@link SessionMap} Struts installed before {@code execute()} captured the pre-auth session once, and that session
   * is stopped by the time {@code finishLogin} writes, so the tail must write through a fresh view of the session
   * {@code Subject.login} just created (the defect CognitoCallbackAction fixed in T09).
   *
   * @return a working, empty session view bound to the session that is current right now
   */
  protected Map<String, Object> freshSessionMap() {
    // ServletActionContext.getRequest() derefs ActionContext.getContext() unconditionally in this Struts version
    // and NPEs when none is bound, so the context is checked first.
    if (ActionContext.getContext() == null) {
      return new HashMap<String, Object>();
    }
    HttpServletRequest servletRequest = ServletActionContext.getRequest();
    if (servletRequest != null) {
      return new SessionMap(servletRequest);
    }
    return new HashMap<String, Object>();
  }

  /**
   * @return the answer: {@code {status, redirect?}}. It is the very map the JSON result serialises (design 4), and it
   *         is never {@code null}
   */
  public Map<String, Object> getResponse() {
    return this.response;
  }

  public void setCode(String code) {
    this.code = code;
  }

  /** The Global Unit acronym for the log line, resolved once and never thrown. */
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

  /** The generic refusal after the challenge was consumed: Cognito's category, the one the login page renders. */
  private VerifyOutcome failed() {
    this.response = status(STATUS_FAILED);
    this.response.put(KEY_REDIRECT, this.getBaseUrl() + "/login.do?authError=" + AUTH_ERROR_FAILED);
    return VerifyOutcome.FAILED;
  }

  /**
   * Design 5.2 step 8. A failure of the predicate is mapped to "not eligible", the same answer an ineligible account
   * gets: whether the predicate throws can depend on the account, so any other answer would be an oracle (SEC-006).
   * An {@code Error} is caught too, as the mail hand-off of the request action does. The server log still records it
   * as {@code internal_error}.
   */
  private boolean isEligible(OtpPending pending) {
    try {
      return this.eligibility.isEligible(pending.getEmail(), pending.getGlobalUnitId());
    } catch (RuntimeException | Error e) {
      logFault("eligibility", e);
      this.eligibilityFailed = true;
      return false;
    }
  }

  /**
   * @return the pending challenge of the Shiro session, or {@code null} when there is no session or the value is not
   *         an {@link OtpPending}. It reads, never creates, a session
   */
  private OtpPending pending() {
    Session session = SecurityUtils.getSubject().getSession(false);
    Object raw = session == null ? null : session.getAttribute(APConstants.OTP_PENDING_CHALLENGE);
    return raw instanceof OtpPending ? (OtpPending) raw : null;
  }

  /**
   * The whole call, up to the answer.
   *
   * @return the outcome for the log line; {@link #getResponse()} holds the answer
   */
  private VerifyOutcome process() {
    OtpPending pending = this.pending();
    if (pending != null) {
      String pendingDomain = OtpDomainPolicy.domainOf(pending.getEmail());
      this.domain = pendingDomain == null ? "" : pendingDomain;
      this.unitId = Long.valueOf(pending.getGlobalUnitId());
    }

    // Step 1. The email key comes from the session, so no lookup precedes the counting (JD-3). With no pending
    // challenge only the address is counted. An unconfigured secret makes the limiter answer unavailable.
    Decision decision = this.limiter.checkVerifyCode(pending == null ? null : pending.getEmail(), this.clientIp());
    if (decision == Decision.LIMITED) {
      return this.refuse(OtpRequestCodeAction.STATUS_RATE_LIMITED, VerifyOutcome.RATE_LIMITED);
    }
    if (decision != Decision.ALLOWED) {
      return this.refuse(OtpRequestCodeAction.STATUS_UNAVAILABLE, VerifyOutcome.INTERNAL_ERROR);
    }

    // Step 2. A bad shape spends no attempt and reads no row.
    if (this.code == null || !SIX_DIGITS.matcher(this.code).matches()) {
      return this.refuse(STATUS_MISMATCH, VerifyOutcome.MISMATCH);
    }

    // Step 3 (FN-016).
    if (pending == null) {
      return this.refuse(STATUS_MISMATCH, VerifyOutcome.MISMATCH);
    }

    // Step 4. These checks only refuse early; expiry is accepted only by the conditional update of step 7.
    String nonce = pending.getNonce();
    OtpChallenge challenge = this.challengeManager.findByNonce(nonce);
    if (challenge == null || challenge.getConsumedAt() != null) {
      return this.refuse(STATUS_INVALID, VerifyOutcome.INVALID);
    }
    if (challenge.getAttempts() >= OtpChallenge.MAX_ATTEMPTS) {
      return this.refuse(STATUS_EXHAUSTED, VerifyOutcome.EXHAUSTED);
    }
    Instant now = this.clock.instant();
    if (challenge.getExpiresAt() == null || now.isAfter(challenge.getExpiresAt())) {
      return this.refuse(STATUS_INVALID, VerifyOutcome.INVALID);
    }

    // Step 5. Constant time, through the one comparison OtpCodes offers.
    boolean matches = this.codes.matches(challenge.getCodeHmac(), nonce, this.code);

    // Step 6. Decoys are real rows, so they take this very path (DD-5).
    if (!matches) {
      return this.registerMismatch(nonce);
    }

    // Step 7 (FN-013). The store decides, atomically.
    if (!this.challengeManager.consume(nonce)) {
      return this.refuse(STATUS_INVALID, VerifyOutcome.INVALID);
    }

    // Step 8 (FN-015). The pending challenge goes first: a consumed challenge is never retried.
    SecurityUtils.getSubject().getSession().removeAttribute(APConstants.OTP_PENDING_CHALLENGE);
    if (!this.isEligible(pending)) {
      this.response = status(STATUS_INVALID);
      return this.eligibilityFailed ? VerifyOutcome.INTERNAL_ERROR : VerifyOutcome.NOT_ELIGIBLE;
    }

    return this.signIn(pending);
  }

  /** Design 5.2 step 6: the wrong code is counted by the store, which also tells what a lost race means. */
  private VerifyOutcome registerMismatch(String nonce) {
    MismatchOutcome counted = this.challengeManager.registerMismatch(nonce);
    if (counted == MismatchOutcome.MISMATCH) {
      return this.refuse(STATUS_MISMATCH, VerifyOutcome.MISMATCH);
    }
    if (counted == MismatchOutcome.EXHAUSTED) {
      return this.refuse(STATUS_EXHAUSTED, VerifyOutcome.EXHAUSTED);
    }
    return this.refuse(STATUS_INVALID, VerifyOutcome.INVALID);
  }

  private VerifyOutcome refuse(String status, VerifyOutcome outcome) {
    this.response = status(status);
    return outcome;
  }

  /**
   * Design 5.2 steps 9 and 10: the complete Cognito callback sequence, then the shared tail.
   * <p>
   * The user is loaded here, after the eligibility re-check, and the <b>loaded</b> entity goes to
   * {@code finishLogin}: gate 4 reads its id. The inherited {@code user} field holds a detached stand-in carrying only
   * the email, because {@code finishLogin} dereferences it on its refusal paths (P-27). This is the only place in the
   * code base that builds an {@link OtpAuthenticationToken}, and it does so only after the consume and the re-check.
   */
  private VerifyOutcome signIn(OtpPending pending) {
    GlobalUnit loggedUnit = this.globalUnitManager.getGlobalUnitById(pending.getGlobalUnitId());
    User loggedUser = this.getUserManager().getUserByEmail(pending.getEmail());
    if (loggedUser == null || loggedUser.getId() == null) {
      // The eligibility re-check just found this row; reaching null here means it was deleted in between. A null
      // here must refuse, not NPE deeper in finishLogin (RJ-2). The verify line records it as outcome=failed.
      return this.failed();
    }

    // Record, never revoke (as at ValidateUserAction). saveLastLogin is the write path that persists agree_terms; the
    // tail calls it again, the same double call Cognito makes.
    if (pending.isAgree()) {
      loggedUser.setAgreeTerms(Boolean.TRUE);
    }
    this.getUserManager().saveLastLogin(loggedUser);

    User detachedUser = new User();
    detachedUser.setEmail(pending.getEmail());
    this.setUser(detachedUser);

    // Rotate the session (SEC-005): the pre-auth session is never the authenticated one.
    SecurityUtils.getSubject().getSession().stop();
    try {
      SecurityUtils.getSubject().login(new OtpAuthenticationToken(loggedUser.getId()));
    } catch (AuthenticationException e) {
      logFault("login", e);
      return this.failed();
    }

    // Drop the request-level cache of the OLD, now-stopped session before anything reads the session again; it runs
    // strictly after Subject.login() has created the new one.
    if (ActionContext.getContext() != null && ServletActionContext.getRequest() != null) {
      ShiroRequestSessionCacheResetter.clearCachedSession(ServletActionContext.getRequest());
    }

    // Re-point this action's session view, and ActionContext's, at the session login just created.
    Map<String, Object> freshSession = this.freshSessionMap();
    this.setSession(freshSession);
    if (ActionContext.getContext() != null) {
      ActionContext.bind(ActionContext.getContext().withSession(freshSession));
    }

    String tail = this.finishLogin(loggedUser, loggedUnit, pending.getReturnUrl());
    if (SUCCESS.equals(tail)) {
      // The target of the success result of login and cognitoCallback (struts-home.xml): ${crpSession}/crpDashboard.
      // The tail has just put this very unit into the session, so its acronym is that crpSession.
      this.response = status(STATUS_OK);
      this.response.put(KEY_REDIRECT, this.getBaseUrl() + "/" + loggedUnit.getAcronym() + "/crpDashboard.do");
      return VerifyOutcome.OK;
    }
    if (LOGIN.equals(tail)) {
      // A type-2 unit's centerDashboard.do, or the validated same-origin .do return URL: the tail's own url.
      this.response = status(STATUS_OK);
      this.response.put(KEY_REDIRECT, this.getUrl());
      return VerifyOutcome.OK;
    }
    // INPUT: gate 4, an unresolvable unit or an unmapped unit type. Every side effect already ran in the tail.
    return this.failed();
  }
}
