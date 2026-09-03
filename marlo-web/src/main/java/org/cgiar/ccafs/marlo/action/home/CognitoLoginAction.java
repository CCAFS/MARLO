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
import org.cgiar.ccafs.marlo.data.manager.ParameterManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.CognitoAuthSpecificity;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.LogSanitizer;

import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CHG-COGNITO-AUTH-001-T08: the entry point to the federated login. Decides whether a person may start a
 * Cognito authorization-code flow at all, and mints the security values the callback
 * ({@code CognitoCallbackAction}, T09) later checks.
 * <p>
 * <b>Three checks gate the redirect, and all three fail closed:</b>
 * <ol>
 * <li>Configuration -- {@link APConfig}'s Cognito getters return {@code ""}, never {@code null}, on an
 * unconfigured environment (design.md 9.3). A blank domain, client id or callback URL must never reach a
 * half-built authorize URL; it is refused before any token is minted.</li>
 * <li>The Global Unit's {@code cognito_auth_active} specificity, resolved server-side from
 * {@link CustomParameterManager} / {@link ParameterManager} with {@code COALESCE(custom value,
 * parameters.default_value)} semantics -- <b>never</b> from a client-supplied flag. design.md 9.2 states this
 * table three times on purpose: {@code crpByEmail.do}'s {@code cognitoEnabled} is a rendering hint only, and
 * this action's re-check is authoritative.</li>
 * <li>{@code users.is_cgiar_user}, resolved by looking the submitted email up through {@link UserManager}.
 * <b>This is a documented deviation from one sentence in design.md 13.1</b>, which states this action "does
 * not yet know who the user is" and therefore cannot check {@code is_cgiar_user} -- but {@code tasks.md} T08
 * both scopes this check into this action and names a test for it. Accepting an email parameter (exactly the
 * value the step-1 {@code crpByEmail.do} exchange already collected) makes the check possible without
 * contradicting anything else in the design: {@code CognitoCallbackAction} (T09) still re-applies the same
 * gate, authoritatively, once the ID token has resolved the caller's real identity, so this is defense in
 * depth rather than a replacement for T09's gate.</li>
 * </ol>
 * <p>
 * <b>Terms acceptance (design.md 5.4, amended during T08)</b> is <b>checked here and written in the
 * callback</b>. This endpoint is unauthenticated and {@code email} is unverified, so a write here would let
 * anyone set — or revoke — a third party's compliance record. The check still belongs here: DD-2 puts the
 * accept control outside the form on this path, so HTML5 {@code required} cannot fire, and without a guard a
 * user would complete sign-in having declined.
 * <p>
 * <b>Round-trip state (DD-4).</b> {@code state}, {@code nonce} and the PKCE verifier are generated with
 * {@link SecureRandom}, never a general-purpose {@link java.util.Random}. The values a callback needs are
 * bound to the Shiro session — not the redirect URI, not a cookie — under one <b>fixed</b> key, so one caller
 * holds one pending authorization, and its state can never be written apart from its payload. DD-4's
 * Concurrency clause rejected a {@code state}-keyed map "as unbounded session growth"; last-writer-wins across
 * tabs is the accepted cost, and the loser sees a state mismatch and retries.
 * <p>
 * <b>The return URL is validated at mint time</b>, not merely bound — see {@link #sameOriginOrNull(String)}.
 */
public class CognitoLoginAction extends BaseAction {

  /**
   * Everything a federated round trip must survive on -- including its own {@code state} -- bound to the
   * pre-auth Shiro session under a single
   * <b>fixed</b> key (design.md DD-4). Deliberately <b>not</b> carried in the redirect URI or a cookie: either
   * would let a caller choose the Global Unit they return with, turning a UX convenience into an
   * access-control input.
   * <p>
   * {@link Serializable} for the same reason {@code CognitoAssertion} is (see its javadoc): nothing in MARLO
   * serializes a session attribute today, but a value bound into Shiro's session must honor the contract
   * regardless of whether the current wiring happens to exercise it.
   * <p>
   * {@code CognitoCallbackAction} (T09) is the intended reader: it reads this entry under its fixed
   * {@code APConstants} key, compares the returned state against {@link #getState()}, and <b>removes</b> it on
   * first use — a {@code state} must not be replayable.
   */
  public static final class PendingAuthorization implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String state;
    private final Long globalUnitId;
    private final String returnUrl;
    private final String nonce;
    private final String verifier;

    /**
     * @param state the opaque value the callback must present to claim this authorization. Required, and
     *        carried <b>inside</b> this object so state and payload can never be written apart
     * @param globalUnitId the Global Unit this authorization request was minted for. Required
     * @param returnUrl the deep link to return to after login, or {@code null} when there is none
     * @param nonce the OIDC nonce bound to the authorize request. Required
     * @param verifier the PKCE code verifier bound to the authorize request. Required
     * @throws IllegalArgumentException if a required value is missing
     */
    public PendingAuthorization(String state, Long globalUnitId, String returnUrl, String nonce,
      String verifier) {
      if (state == null || state.isEmpty()) {
        throw new IllegalArgumentException("state is required");
      }
      if (globalUnitId == null) {
        throw new IllegalArgumentException("globalUnitId is required");
      }
      if (nonce == null || nonce.isEmpty()) {
        throw new IllegalArgumentException("nonce is required");
      }
      if (verifier == null || verifier.isEmpty()) {
        throw new IllegalArgumentException("verifier is required");
      }
      this.state = state;
      this.globalUnitId = globalUnitId;
      this.returnUrl = returnUrl;
      this.nonce = nonce;
      this.verifier = verifier;
    }

    public String getState() {
      return this.state;
    }

    public Long getGlobalUnitId() {
      return this.globalUnitId;
    }

    public String getNonce() {
      return this.nonce;
    }

    public String getReturnUrl() {
      return this.returnUrl;
    }

    public String getVerifier() {
      return this.verifier;
    }
  }

  private static final long serialVersionUID = 1L;

  private static final Logger LOG = LoggerFactory.getLogger(CognitoLoginAction.class);

  /** RFC 7636 requires 43-128 characters; 32 random bytes base64url-encode to exactly 43. */
  private static final int VERIFIER_BYTES = 32;
  private static final int STATE_BYTES = 32;
  private static final int NONCE_BYTES = 32;

  private static final String OAUTH_SCOPE = "openid email";

  private static final SecureRandom SECURE_RANDOM = new SecureRandom();

  // Managers
  private final UserManager userManager;
  private final GlobalUnitManager crpManager;
  private final CustomParameterManager customParameterManager;
  private final ParameterManager parameterManager;

  // Request parameters
  private String email;
  private Long globalUnitId;
  private Boolean agree;

  // Result
  private String authorizeUrl;

  // @Inject
  public CognitoLoginAction(APConfig config, UserManager userManager, GlobalUnitManager crpManager,
    CustomParameterManager customParameterManager, ParameterManager parameterManager) {
    super(config);
    this.userManager = userManager;
    this.crpManager = crpManager;
    this.customParameterManager = customParameterManager;
    this.parameterManager = parameterManager;
  }

  /**
   * Server-side re-check, state minting and redirect. Split from {@link #execute()} so the core logic can be
   * exercised without a live Struts request -- exactly the seam T01 established for {@code finishLogin}, and
   * for the same reason: a federated flow's caller (here, a test; there, {@code CognitoCallbackAction}) should
   * not need a servlet container to drive it.
   *
   * @param returnUrl the deep link to bind into the pending authorization, or {@code null} when there is none
   * @return {@link #SUCCESS} with {@link #getAuthorizeUrl()} populated, or {@link #INPUT} with a field error
   */
  protected String authorize(String returnUrl) {
    // Validated HERE, not in execute(): authorize(String) is the seam every caller and every test uses, so a
    // guard on one entry point would be bypassable by all the others. The check belongs with the value's use.
    String safeReturnUrl = this.sameOriginOrNull(returnUrl);

    // CHG-COGNITO-AUTH-001-T14 (OPS-001 / design.md 11): "attempt started" -- the first event design.md 11
    // names, logged before every gate below (including the environment check) so a rejection at any of
    // them can be correlated back to this one line. Resolved here, once, and reused as `globalUnit` below
    // rather than queried twice. "Resolved mode" is the literal COGNITO: this endpoint IS the Cognito path
    // by construction (cognitoLogin.do) -- the wizard's client-side `mode` is never read or trusted here
    // (design.md 9.2), so this is a fact about which endpoint was hit, not a value the caller told MARLO.
    GlobalUnit requestedGlobalUnit =
      this.globalUnitId == null ? null : this.crpManager.getGlobalUnitById(this.globalUnitId);
    LOG.info("Cognito login attempt started for {} (Global Unit {}, mode COGNITO)",
      LogSanitizer.sanitizeForLog(this.email),
      requestedGlobalUnit == null ? "<unresolved>" : requestedGlobalUnit.getAcronym());

    if (!this.isCognitoConfigured()) {
      LOG.warn("Cognito login requested but the environment is not configured (design.md 9.3)");
      return this.refuse("login.error.cognitoUnavailable");
    }

    if (this.globalUnitId == null) {
      // CHG-COGNITO-AUTH-001-T14 (OPS-001): this branch had no log line at all before.
      LOG.info("Cognito login refused: no Global Unit was selected");
      return this.refuse("login.error.cognitoNotEligible");
    }
    GlobalUnit globalUnit = requestedGlobalUnit;
    if (globalUnit == null) {
      LOG.info("Cognito login refused: the selected Global Unit could not be resolved");
      return this.refuse("login.error.cognitoNotEligible");
    }

    // Authoritative re-check (design.md 9.2). The client's cognitoEnabled is a rendering hint only; a
    // crafted request naming a Global Unit whose flag is off is refused here regardless of what it claims.
    if (!this.isCognitoActiveFor(globalUnit)) {
      LOG.info("Cognito login refused: {} is off for Global Unit {}", APConstants.COGNITO_AUTH_ACTIVE,
        globalUnit.getAcronym());
      return this.refuse("login.error.cognitoNotEligible");
    }

    String normalizedEmail = this.email == null ? null : this.email.trim().toLowerCase();
    User user = normalizedEmail == null || normalizedEmail.isEmpty() ? null
      : this.userManager.getUserByEmail(normalizedEmail);
    if (user == null || !user.isCgiarUser()) {
      LOG.info("Cognito login refused: the submitted account is not a CGIAR-authenticated account");
      return this.refuse("login.error.cognitoNotEligible");
    }

    // The terms must be ACCEPTED to start the flow -- checked, never written here. design.md 5.4 was
    // amended during T08: this endpoint is unauthenticated and `email` is unverified (13.1 correction), so
    // writing users.agree_terms here would let anyone set, or REVOKE, a third party's compliance record.
    // The write moved to the callback, where the ID token has proved who the person actually is.
    //
    // The check itself still belongs here. On the local path the checkbox carries HTML5 `required` inside
    // the form; on this path DD-2 puts the control OUTSIDE the form, so `required` cannot fire and nothing
    // in the browser stops an unaccepted submission. Without this guard a CGIAR user completes sign-in
    // having declined the terms -- the compliance regression 5.4 exists to prevent, in a new shape.
    if (!Boolean.TRUE.equals(this.agree)) {
      LOG.info("Cognito login refused: the terms were not accepted");
      return this.refuse("login.error.cognitoNotEligible");
    }

    String state = this.randomUrlSafeToken(STATE_BYTES);
    String nonce = this.randomUrlSafeToken(NONCE_BYTES);
    String verifier = this.randomUrlSafeToken(VERIFIER_BYTES);
    String codeChallenge = this.codeChallengeOf(verifier);

    // DD-4, and specifically its Concurrency clause: FIXED session keys, not a state-keyed map. An earlier
    // revision stored each attempt under "cognito.pending." + state, which is exactly the keyed map DD-4
    // rejected "as unbounded session growth" -- nothing removes an entry but a callback bearing that state,
    // so an anonymous caller could loop this endpoint on one cookie and grow a heap-resident session for the
    // full 30-minute timeout. Single-slot means last-writer-wins across tabs, which DD-4 accepted: the loser
    // sees a state mismatch and retries.
    // ONE attribute, and the state travels inside it. Two separate setAttribute calls can interleave between
    // two tabs on one session into authorization_A + state_B: the callback then matches B's state, loads A's
    // nonce and verifier, and the token exchange fails at Cognito. That fails closed, but it is not the
    // "last-writer-wins" DD-4 accepted -- under a single object the pair is always consistent.
    Session shiroSession = SecurityUtils.getSubject().getSession();
    shiroSession.setAttribute(APConstants.COGNITO_PENDING_AUTHORIZATION,
      new PendingAuthorization(state, this.globalUnitId, safeReturnUrl, nonce, verifier));

    this.authorizeUrl = this.buildAuthorizeUrl(state, nonce, codeChallenge);
    return SUCCESS;
  }

  private String buildAuthorizeUrl(String state, String nonce, String codeChallenge) {
    StringBuilder url = new StringBuilder("https://").append(this.config.getCognitoDomain()).append("/oauth2/authorize")
      .append("?response_type=code").append("&client_id=").append(this.urlEncode(this.config.getCognitoClientId()))
      .append("&redirect_uri=").append(this.urlEncode(this.config.getCognitoCallbackUrl())).append("&scope=")
      .append(this.urlEncode(OAUTH_SCOPE)).append("&state=").append(this.urlEncode(state)).append("&nonce=")
      .append(this.urlEncode(nonce)).append("&code_challenge=").append(this.urlEncode(codeChallenge))
      .append("&code_challenge_method=S256");
    // CHG-COGNITO-AUTH-001-T15: omitted entirely when unset, so Cognito falls back to its own Hosted UI
    // provider-selection screen -- an empty-valued parameter is not the same as an absent one.
    String identityProvider = this.config.getCognitoIdentityProvider();
    if (!identityProvider.isEmpty()) {
      url.append("&identity_provider=").append(this.urlEncode(identityProvider));
    }
    return url.toString();
  }

  private String codeChallengeOf(String verifier) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(verifier.getBytes(StandardCharsets.US_ASCII));
      return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
    } catch (NoSuchAlgorithmException e) {
      // SHA-256 is a guaranteed algorithm on every JVM implementation; this cannot happen in practice.
      throw new IllegalStateException("SHA-256 is not available", e);
    }
  }

  @Override
  public String execute() throws Exception {
    String returnUrl =
      ServletActionContext.getRequest() == null ? null : ServletActionContext.getRequest().getHeader("Referer");
    return this.authorize(returnUrl);
  }

  /**
   * Keeps a deep link only when it points back into this MARLO instance.
   * <p>
   * <b>This is an open-redirect guard, not tidiness.</b> {@code returnUrl} arrives in the {@code Referer}
   * header of a GET navigation, so the page that links here chooses it — including a page the attacker
   * controls, which can also set {@code Referrer-Policy: unsafe-url} to defeat the browser's default
   * truncation. It is then bound into the session and handed to {@code finishLogin}, whose only test is
   * {@code urlAction.contains(".do")} — satisfied by a host such as {@code https://evil.do/}. The victim
   * would authenticate for real at the CGIAR IdP and be redirected off-site immediately afterwards.
   * <p>
   * DD-4's problem statement requires the return URL to be <b>not attacker-controllable</b>. Binding it
   * server-side stops it being changed on the way back; it does nothing about the value chosen when it is
   * minted. That is what this closes. The local path is not exposed the same way — {@code login.do} is a
   * POST from MARLO's own form, so its {@code Referer} cannot be chosen without the victim's password.
   *
   * @param candidate the raw {@code Referer} value, possibly {@code null}
   * @return the value when it is same-origin with this MARLO instance, otherwise {@code null}
   */
  private String sameOriginOrNull(String candidate) {
    if (candidate == null || candidate.trim().isEmpty()) {
      return null;
    }
    String baseUrl = this.getBaseUrl();
    if (baseUrl == null || baseUrl.trim().isEmpty()) {
      return null;
    }
    // Compared on the origin, and only after a "/" is appended to both, so that a look-alike host such as
    // https://marlo.example.org.evil.com/ cannot pass a bare startsWith against https://marlo.example.org.
    String origin = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
    String normalized = candidate.trim();
    if (normalized.equals(baseUrl) || normalized.startsWith(origin)) {
      return normalized;
    }
    LOG.info("Cognito login: discarding an off-site return URL");
    return null;
  }

  public Boolean getAgree() {
    return this.agree;
  }

  /**
   * @return the full {@code https://<domain>/oauth2/authorize?...} URL to redirect to, populated only when
   *         {@link #authorize(String)} returned {@link #SUCCESS}
   */
  public String getAuthorizeUrl() {
    return this.authorizeUrl;
  }

  public String getEmail() {
    return this.email;
  }

  public Long getGlobalUnitId() {
    return this.globalUnitId;
  }

  /**
   * @return {@code true} when every Cognito setting this action needs to build a redirect is present.
   *         {@link APConfig}'s Cognito getters return {@code ""}, never {@code null}, so a phase-0 environment
   *         (design.md 9.3, 14) is detected here and fails closed instead of producing a broken redirect
   */
  private boolean isCognitoConfigured() {
    return !this.config.getCognitoDomain().isEmpty() && !this.config.getCognitoClientId().isEmpty()
      && !this.config.getCognitoCallbackUrl().isEmpty();
  }

  /**
   * Resolves {@code cognito_auth_active} for {@code globalUnit} (design.md 9.2). Delegates to
   * {@link CognitoAuthSpecificity}, the single shared resolver both this action and {@code
   * CrpByUserEmailAction} call (PS-16, design.md 9.2's advisory carried into T10) -- so the "rendering
   * hint" and "authoritative" readings of this flag can never drift apart from each other.
   */
  private boolean isCognitoActiveFor(GlobalUnit globalUnit) {
    return CognitoAuthSpecificity.isActiveFor(globalUnit, this.customParameterManager, this.parameterManager);
  }

  /**
   * Generates a cryptographically random, URL-safe token with {@link SecureRandom} -- never
   * {@link java.util.Random}, which two calls could make look different "by luck" without being
   * unguessable. Base64url without padding keeps every character in RFC 7636's allowed set.
   *
   * @param numBytes how many random bytes to draw before encoding
   * @return a base64url (no padding) encoding of {@code numBytes} random bytes
   */
  private String randomUrlSafeToken(int numBytes) {
    byte[] raw = new byte[numBytes];
    SECURE_RANDOM.nextBytes(raw);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(raw);
  }

  private String refuse(String i18nKey) {
    this.addFieldError("loginMessage", this.getText(i18nKey));
    return INPUT;
  }

  public void setAgree(Boolean agree) {
    this.agree = agree;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setGlobalUnitId(Long globalUnitId) {
    this.globalUnitId = globalUnitId;
  }

  private String urlEncode(String value) {
    return URLEncoder.encode(value, StandardCharsets.UTF_8);
  }
}
