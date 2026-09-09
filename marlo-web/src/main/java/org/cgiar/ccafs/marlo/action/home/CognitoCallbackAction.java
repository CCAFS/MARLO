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
import org.cgiar.ccafs.marlo.data.manager.ParameterManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.CognitoAssertion;
import org.cgiar.ccafs.marlo.security.CognitoAuthenticationToken;
import org.cgiar.ccafs.marlo.security.CognitoIdentityMapper;
import org.cgiar.ccafs.marlo.security.CognitoTokenValidator;
import org.cgiar.ccafs.marlo.security.ShiroRequestSessionCacheResetter;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opensymphony.xwork2.ActionContext;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.session.Session;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.SessionMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CHG-COGNITO-AUTH-001-T09: the redirect target Cognito returns to. Validates, gates, rotates the session,
 * establishes the login, and hands off to the shared tail every authentication path uses.
 * <p>
 * <b>The eight-step ordering (design.md 13.3) is the design, not an implementation detail:</b>
 * <ol>
 * <li>Consume {@code state} -- read then <b>delete</b> the pending authorization {@link
 * CognitoLoginAction.PendingAuthorization} minted by {@code cognitoLogin.do}, atomically, from the same
 * pre-auth Shiro session. A missing entry or a mismatched {@code state} refuses before anything else runs,
 * which is what makes a replayed {@code state} fail on "no such entry" rather than on a later check.</li>
 * <li>Exchange the authorization code for tokens at the pool's {@code /oauth2/token} endpoint (the
 * {@link TokenExchangeClient} seam below) -- the only step this class adds that is not already covered by
 * an existing collaborator.</li>
 * <li>Validate the returned ID token with {@link CognitoTokenValidator#validate(String, String)} (T05,
 * SEC-001 in full) against the {@code nonce} read at step 1.</li>
 * <li>Map the validated {@link CognitoAssertion} to a {@code users} row and apply gates 1-3 with
 * {@link CognitoIdentityMapper#map(CognitoAssertion)} (T07). <b>SEC-006 is enforced here, at the rendering
 * site</b>: a rejection is rendered through {@link CognitoIdentityMapper.RejectionReason#toMessageKey()},
 * never {@code name()} -- {@code ACCOUNT_NOT_FOUND} and {@code NOT_CGIAR_ACCOUNT} must reach the browser
 * as the identical message, and only {@code toMessageKey()} guarantees that.</li>
 * <li>Capture the Global Unit and return URL bound at mint time into locals -- <b>never</b> from a
 * {@code globalUnitId} on the callback URL itself (FN-003's {@code MUST NOT} trust the returned value; a
 * tampered query parameter is never read by this class at all, so there is nothing for it to override).
 * This is also where {@code users.agree_terms} is persisted -- see the class-level note below.</li>
 * <li>{@code subject.getSession().stop()} -- the pre-auth session, holding nothing else of value once step
 * 1 has already removed the pending authorization, is destroyed (SEC-003 / D-8). <b>Must run after step
 * 1</b>: stopping first would discard the entry step 1 needs to read, which is exactly the mutation this
 * task's own <i>Fails when</i> clause requires proving.</li>
 * <li>{@code Subject.login(new CognitoAuthenticationToken(assertion, userId))} -- a new session is created.
 * The realm dispatches on {@code instanceof} (T06) and returns {@code SimpleAuthenticationInfo(userId)}
 * with no further I/O (DD-5). <b>Immediately afterward, this action's session view is re-pointed at that
 * new session</b> -- see {@link #freshSessionMap()}'s javadoc for why that is load-bearing, not tidiness.</li>
 * <li>{@link LoginAction#finishLogin(User, GlobalUnit, String)} (T01) -- the shared tail every
 * authentication path uses, so session attributes, custom parameters, the {@code crp_users} membership
 * gate (gate 4), {@code saveLastLogin}, and post-login routing cannot drift between the local and Cognito
 * paths.</li>
 * </ol>
 * <p>
 * <b>DD-6: the inherited {@code user} field is populated with a detached, non-Hibernate-managed
 * {@link User} carrying only the email</b>, before {@link #finishLogin(User, GlobalUnit, String)} is
 * called. That field is dereferenced inside the inherited tail (the success log line, and both
 * {@code user.setPassword(null)} calls on the membership-failure and no-crp branches); a callback has no
 * Struts-populated form to have set it, so without this step every one of those paths would NPE.
 * {@code new User()} is inherently detached -- it never passed through a Hibernate session -- so DD-6's
 * requirement is satisfied by construction, not by an explicit detach call.
 * <p>
 * <b>{@code users.agree_terms} -- ALSO OWNS, per tasks.md T09.</b> {@code CognitoLoginAction} (T08) only
 * <i>checks</i> that the terms were accepted before it will mint a {@link
 * CognitoLoginAction.PendingAuthorization} at all -- design.md 5.4 was amended specifically because writing
 * the acceptance there, against an unverified {@code email} query parameter, let anyone revoke a third
 * party's compliance record. Reaching this point in the callback with a {@link
 * CognitoLoginAction.PendingAuthorization} in hand therefore already implies the acceptance happened, and
 * the ID token has now proved <i>who</i> accepted it -- which is the fact T08 could never establish. The
 * write goes through {@link UserManager#saveLastLogin(User)}, never {@code saveUser(...)}: {@code
 * UserMySQLDAO.saveUser} routes through {@code AbstractMarloDAO.update(T)}, which returns before {@code
 * merge()} for an entity the Hibernate session already contains, and {@code saveLastLogin} carries
 * {@code @Transactional}, without which this OSIV session's {@code FlushMode.MANUAL} setup never flushes
 * the change to the database at all -- identical to T07's and T08's audits on this exact path.
 * <p>
 * <b>What the tests can and cannot prove about that write</b> is recorded at the top of
 * {@code CognitoCallbackActionTest}: this test tree has no schema-backed Hibernate harness (no mocking
 * framework, no {@code SessionFactory} in any existing test, same gap T07 recorded and left open). Proving
 * the row changed against a real schema is called out there as a gap for the Leader to close, the way T02's
 * migration was verified against a live database -- it is not discharged by a call-recording double here.
 */
public class CognitoCallbackAction extends LoginAction {

  /**
   * Exchanges an authorization code for tokens at the pool's {@code /oauth2/token} endpoint (design.md
   * 13.3 step 2) -- the one I/O boundary in this class besides {@link CognitoTokenValidator}. Injected so
   * tests can substitute a fixture with no network, matching the {@code JwksSource} seam T05 established
   * for exactly the same reason.
   */
  protected interface TokenExchangeClient {

    /**
     * @param authorizationCode the code Cognito returned on the callback query string
     * @param redirectUri the exact callback URI configured for this pool's app client
     * @param codeVerifier the PKCE verifier minted at {@code cognitoLogin.do}
     * @return an accepted result carrying the raw ID token, or a rejected one. Never {@code null}. A
     *         conforming implementation never logs {@code authorizationCode}, {@code codeVerifier}, or the
     *         returned token
     */
    ExchangeResult exchange(String authorizationCode, String redirectUri, String codeVerifier);
  }

  /**
   * The outcome of a {@link TokenExchangeClient#exchange(String, String, String)} call: exactly one of an
   * ID token or a rejection, never both, following the {@code Result} convention {@link
   * CognitoTokenValidator.Result} and {@link CognitoIdentityMapper.Result} already use in this spec.
   */
  protected static final class ExchangeResult {

    private final String idToken;
    private final boolean accepted;

    private ExchangeResult(String idToken, boolean accepted) {
      this.idToken = idToken;
      this.accepted = accepted;
    }

    static ExchangeResult accepted(String idToken) {
      if (idToken == null || idToken.trim().isEmpty()) {
        throw new IllegalArgumentException("idToken is required for an accepted result");
      }
      return new ExchangeResult(idToken, true);
    }

    static ExchangeResult rejected() {
      return new ExchangeResult(null, false);
    }

    String getIdToken() {
      if (!this.accepted) {
        throw new IllegalStateException("no idToken on a rejected result; call isAccepted() first");
      }
      return this.idToken;
    }

    boolean isAccepted() {
      return this.accepted;
    }
  }

  /**
   * Wraps {@link HttpResponse.BodySubscribers#ofString(java.nio.charset.Charset)}, cancelling the upstream
   * subscription and failing closed once more than {@code limit} bytes have arrived.
   * <p>
   * <b>Why this exists, and why a {@code Content-Length} check alone is not enough</b> (review finding,
   * Issue 3): a chunked response carries no {@code Content-Length} header at all, so the only bound that
   * actually holds against a misbehaving or compromised token endpoint is one applied while the bytes are
   * still arriving -- exactly the discipline {@code CognitoTokenValidatorImpl} already applies to its own
   * JWKS fetch ({@code SIZE_LIMIT_BYTES}). A prior version of this class's javadoc claimed that discipline
   * was already mirrored here; it was not -- only the connect/read timeouts were. This class is what makes
   * the claim true.
   */
  private static final class BoundedStringBodySubscriber implements HttpResponse.BodySubscriber<String> {

    private final HttpResponse.BodySubscriber<String> delegate =
      HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8);
    private final long limit;
    private long received;
    private Flow.Subscription subscription;

    BoundedStringBodySubscriber(long limit) {
      this.limit = limit;
    }

    @Override
    public CompletionStage<String> getBody() {
      return this.delegate.getBody();
    }

    @Override
    public void onComplete() {
      this.delegate.onComplete();
    }

    @Override
    public void onError(Throwable throwable) {
      this.delegate.onError(throwable);
    }

    @Override
    public void onNext(List<ByteBuffer> item) {
      for (ByteBuffer buffer : item) {
        this.received += buffer.remaining();
      }
      if (this.received > this.limit) {
        this.subscription.cancel();
        this.delegate.onError(new IOException("Cognito token response exceeded " + this.limit + " bytes"));
        return;
      }
      this.delegate.onNext(item);
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
      this.subscription = subscription;
      this.delegate.onSubscribe(subscription);
    }
  }

  /**
   * The production {@link TokenExchangeClient}: a single bounded HTTPS POST, exactly matching design.md
   * 12's "exactly one outbound MARLO-to-AWS call per login" measure for this step. Connect/read timeouts
   * mirror {@code CognitoTokenValidatorImpl}'s JWKS fetch bounds (NF-002); the response body is separately
   * capped by {@link BoundedStringBodySubscriber} (Issue 3) -- three bounds in total, not two.
   */
  private static final class HttpTokenExchangeClient implements TokenExchangeClient {

    /**
     * One client, reused for the life of the JVM. Built once rather than per call: each {@link HttpClient}
     * owns a background selector thread, so minting one per login would leak a thread per authentication
     * attempt (review advisory).
     */
    private static final HttpClient HTTP_CLIENT =
      HttpClient.newBuilder().connectTimeout(Duration.ofMillis(CONNECT_TIMEOUT_MS)).build();

    private final APConfig config;

    HttpTokenExchangeClient(APConfig config) {
      this.config = config;
    }

    private static HttpResponse.BodyHandler<String> boundedBodyHandler(long limit) {
      return responseInfo -> {
        Optional<String> contentLength = responseInfo.headers().firstValue("Content-Length");
        if (contentLength.isPresent()) {
          try {
            if (Long.parseLong(contentLength.get()) > limit) {
              return HttpResponse.BodySubscribers.replacing("");
            }
          } catch (NumberFormatException e) {
            // Fall through to the streaming cap below -- an unparsable header is not a reason to trust it.
          }
        }
        return new BoundedStringBodySubscriber(limit);
      };
    }

    private static String formEncode(String value) {
      return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    @Override
    public ExchangeResult exchange(String authorizationCode, String redirectUri, String codeVerifier) {
      String domain = this.config.getCognitoDomain();
      String clientId = this.config.getCognitoClientId();
      if (domain.isEmpty() || clientId.isEmpty()) {
        // Phase-0 / unconfigured environment (design.md 9.3): fail closed rather than build a request
        // against an empty host.
        return ExchangeResult.rejected();
      }
      StringBuilder body = new StringBuilder("grant_type=authorization_code").append("&client_id=")
        .append(formEncode(clientId)).append("&code=").append(formEncode(authorizationCode))
        .append("&redirect_uri=").append(formEncode(redirectUri)).append("&code_verifier=")
        .append(formEncode(codeVerifier));
      String clientSecret = this.config.getCognitoClientSecret();
      if (!clientSecret.isEmpty()) {
        body.append("&client_secret=").append(formEncode(clientSecret));
      }
      try {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://" + domain + "/oauth2/token"))
          .timeout(Duration.ofMillis(READ_TIMEOUT_MS)).header("Content-Type", "application/x-www-form-urlencoded")
          .header("Accept", "application/json")
          .POST(HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8)).build();
        HttpResponse<String> response = HTTP_CLIENT.send(request, boundedBodyHandler(MAX_RESPONSE_BYTES));
        if (response.statusCode() != 200) {
          LOG.warn("Cognito token exchange rejected: HTTP {}", Integer.valueOf(response.statusCode()));
          return ExchangeResult.rejected();
        }
        JsonNode json = OBJECT_MAPPER.readTree(response.body());
        JsonNode idTokenNode = json.get("id_token");
        if (idTokenNode == null || idTokenNode.asText("").isEmpty()) {
          LOG.warn("Cognito token exchange rejected: no id_token in the response");
          return ExchangeResult.rejected();
        }
        return ExchangeResult.accepted(idTokenNode.asText());
      } catch (IOException e) {
        LOG.warn("Cognito token exchange failed ({})", e.getClass().getSimpleName());
        return ExchangeResult.rejected();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        LOG.warn("Cognito token exchange interrupted");
        return ExchangeResult.rejected();
      } catch (IllegalArgumentException e) {
        // URI.create(...) throws this on a malformed cognito.domain -- a configuration defect, not a
        // network failure, but it must still fail closed rather than surface as an unhandled 500 (review
        // advisory).
        LOG.warn("Cognito token exchange failed: malformed endpoint ({})", e.getClass().getSimpleName());
        return ExchangeResult.rejected();
      }
    }
  }

  private static final long serialVersionUID = 1L;

  private static final Logger LOG = LoggerFactory.getLogger(CognitoCallbackAction.class);

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  /** Mirrors {@code CognitoTokenValidatorImpl}'s JWKS bounds (NF-002) -- see the class javadoc. */
  private static final int CONNECT_TIMEOUT_MS = 2000;
  private static final int READ_TIMEOUT_MS = 2000;

  /** The third bound (Issue 3): a token response has no business being larger than this. */
  private static final long MAX_RESPONSE_BYTES = 64L * 1024L;

  private static final String GENERIC_FAILURE_KEY = "login.error.cognitoFailed";
  private static final String UNAVAILABLE_KEY = "login.error.cognitoUnavailable";

  // Own copy: LoginAction's GlobalUnitManager field is private, and this class needs to resolve the
  // session-bound Global Unit id (step 5) independently of the inherited tail.
  private final GlobalUnitManager crpManager;
  private final CognitoTokenValidator tokenValidator;
  private final CognitoIdentityMapper identityMapper;
  private final TokenExchangeClient tokenExchangeClient;

  // Request parameters -- exactly what Cognito appends to the callback redirect. No globalUnitId parameter
  // exists on this action at all (FN-003): there is nothing here for a tampered query value to override.
  // NOTE: this class still inherits LoginAction's own bindable setters (setGlobalUnit, setCrp, setUser,
  // setUrl) -- FN-003 holds only because callback(...) never READS those inherited fields, not because
  // they are absent from the Struts params interceptor's view. See CognitoCallbackActionTest's behavioral
  // FN-003 test (review Issue 2).
  private String code;
  private String state;
  private String error;

  // @Inject
  public CognitoCallbackAction(APConfig config, UserManager userManager, GlobalUnitManager crpManager,
    CrpUserManager crpUserManager, CustomParameterManager customParameterManager, ParameterManager parameterManager,
    CognitoTokenValidator tokenValidator, CognitoIdentityMapper identityMapper) {
    this(config, userManager, crpManager, crpUserManager, customParameterManager, parameterManager, tokenValidator,
      identityMapper, new HttpTokenExchangeClient(config));
  }

  /**
   * Test/advanced constructor: the {@link TokenExchangeClient} is supplied directly, so this class can be
   * exercised with no network at all -- matching {@code CognitoTokenValidatorImpl}'s two-constructor
   * pattern (T05) for the same reason.
   */
  protected CognitoCallbackAction(APConfig config, UserManager userManager, GlobalUnitManager crpManager,
    CrpUserManager crpUserManager, CustomParameterManager customParameterManager, ParameterManager parameterManager,
    CognitoTokenValidator tokenValidator, CognitoIdentityMapper identityMapper,
    TokenExchangeClient tokenExchangeClient) {
    // CHG-COGNITO-AUTH-001-T11b: LoginAction's constructor gained a ParameterManager parameter to resolve
    // cognito_auth_active through the shared CognitoAuthSpecificity resolver (PS-16). This class never calls
    // LoginAction#login() (it authenticates via Subject.login(CognitoAuthenticationToken) instead), so the
    // parameter is never read here -- it exists solely so this subclass keeps compiling against its parent.
    super(config, userManager, crpManager, crpUserManager, customParameterManager, parameterManager);
    this.crpManager = crpManager;
    this.tokenValidator = tokenValidator;
    this.identityMapper = identityMapper;
    this.tokenExchangeClient = tokenExchangeClient;
  }

  /**
   * The eight-step ordering, exposed as a seam so tests can drive it with no live Struts request -- the
   * same split T01 established for {@code finishLogin} and T08 established for {@code authorize(String)}.
   *
   * @param returnedCode the {@code code} query parameter, or {@code null} when the IdP returned an error
   *        instead
   * @param returnedState the {@code state} query parameter, compared against the value minted at
   *        {@code cognitoLogin.do}
   * @param idpError the {@code error} query parameter Cognito appends when the user cancels or the IdP
   *        denies, or {@code null} on a normal return
   * @return the Struts result name
   */
  protected String callback(String returnedCode, String returnedState, String idpError) {
    // Step 1: read-and-DELETE, atomically, from the pre-auth Shiro session. Removed unconditionally, before
    // any other check, so a replayed `state` -- valid or not -- always finds nothing the second time.
    Session shiroSession = SecurityUtils.getSubject().getSession();
    Object pendingRaw = shiroSession.getAttribute(APConstants.COGNITO_PENDING_AUTHORIZATION);
    shiroSession.removeAttribute(APConstants.COGNITO_PENDING_AUTHORIZATION);

    if (!(pendingRaw instanceof CognitoLoginAction.PendingAuthorization)) {
      LOG.info("Cognito callback refused: no pending authorization for this session");
      return this.refuse(GENERIC_FAILURE_KEY);
    }
    CognitoLoginAction.PendingAuthorization pending = (CognitoLoginAction.PendingAuthorization) pendingRaw;

    if (returnedState == null || !pending.getState().equals(returnedState)) {
      LOG.info("Cognito callback refused: state mismatch");
      return this.refuse(GENERIC_FAILURE_KEY);
    }

    if (idpError != null && !idpError.trim().isEmpty()) {
      // FN-005: the user cancelled, or the IdP denied. The raw provider error is never logged or shown --
      // only the fact that one occurred.
      LOG.info("Cognito callback refused: the identity provider returned an error");
      return this.refuse(GENERIC_FAILURE_KEY);
    }

    if (returnedCode == null || returnedCode.trim().isEmpty()) {
      LOG.info("Cognito callback refused: no authorization code was returned");
      return this.refuse(GENERIC_FAILURE_KEY);
    }

    // Step 2: exchange the code. FN-005's "Cognito is unreachable" scenario lands here -- a distinct,
    // service-unavailable message, never the generic one, and this class touches nothing the local login
    // path depends on either way (NF-002).
    ExchangeResult exchange =
      this.tokenExchangeClient.exchange(returnedCode, this.config.getCognitoCallbackUrl(), pending.getVerifier());
    if (!exchange.isAccepted()) {
      LOG.warn("Cognito callback refused: token exchange failed");
      return this.refuse(UNAVAILABLE_KEY);
    }

    // Step 3: validate. SEC-001 in full (T05) -- signature, iss, aud, exp, nonce, token_use.
    CognitoTokenValidator.Result validation = this.tokenValidator.validate(exchange.getIdToken(), pending.getNonce());
    if (!validation.isAccepted()) {
      LOG.info("Cognito callback refused: token validation failed ({})", validation.getRejectionReason());
      return this.refuse(GENERIC_FAILURE_KEY);
    }
    CognitoAssertion assertion = validation.getAssertion();

    // Step 4: map claim -> users row, gates 1-3 (T07). SEC-006: rendered through toMessageKey(), never
    // name() -- ACCOUNT_NOT_FOUND and NOT_CGIAR_ACCOUNT must reach the browser identically.
    CognitoIdentityMapper.Result mapping = this.identityMapper.map(assertion);
    if (!mapping.isAccepted()) {
      LOG.info("Cognito callback refused: identity mapping rejected ({})", mapping.getRejectionReason());
      return this.refuse(mapping.getRejectionReason().toMessageKey());
    }
    Long userId = mapping.getUserId();

    // Step 5: capture the carry-forward values into locals -- the Global Unit and return URL bound at MINT
    // time, never from anything on this callback's own query string, and never from this.globalUnit /
    // this.crp -- LoginAction's own bindable fields, which callback(...) deliberately never reads (FN-003).
    // This is also where users.agree_terms is persisted -- see the class javadoc for why this is the
    // correct point and the correct method.
    GlobalUnit loggedCrp = this.crpManager.getGlobalUnitById(pending.getGlobalUnitId().longValue());
    String returnUrl = pending.getReturnUrl();

    User loggedUser = this.getUserManager().getUser(userId);
    if (loggedUser == null) {
      // Gate 1 just confirmed this row exists; reaching null here means it was deleted in the window
      // between that read and this one. Vanishingly unlikely, but a null here must refuse, not NPE deeper
      // in finishLogin (review advisory).
      LOG.warn("Cognito callback refused: the resolved user vanished between the identity gate and this read");
      return this.refuse(GENERIC_FAILURE_KEY);
    }
    loggedUser.setAgreeTerms(Boolean.TRUE);
    this.applyNamesFromToken(loggedUser, assertion);
    this.getUserManager().saveLastLogin(loggedUser);

    // DD-6: the inherited `user` field the shared tail dereferences must be non-null, detached, and carry
    // only the email -- see the class javadoc for exactly which lines would otherwise NPE.
    User detachedUser = new User();
    detachedUser.setEmail(assertion.getEmail());
    this.setUser(detachedUser);

    // Step 6: rotate the session (SEC-003 / D-8). MUST run after step 1 -- this task's own Fails when
    // clause requires proving that moving it earlier reddens the state-consumption tests.
    SecurityUtils.getSubject().getSession().stop();

    // Step 7: establish the new session. The realm dispatches on instanceof (T06) with no I/O (DD-5).
    // Guarded: an AuthenticationException here (an unexpected realm rejection) must render the same generic
    // refusal every other failure path uses, not surface as an unhandled 500 (review advisory).
    try {
      SecurityUtils.getSubject().login(new CognitoAuthenticationToken(assertion, userId));
    } catch (AuthenticationException e) {
      LOG.warn("Cognito callback refused: the realm rejected the resolved identity ({})",
        e.getClass().getSimpleName());
      return this.refuse(GENERIC_FAILURE_KEY);
    }

    // CHG-COGNITO-AUTH-001-T16 (V-2): drop the request-level cache of the OLD, now-stopped session wrapper
    // BEFORE anything reads the request's session again. Must run strictly after Subject.login() has
    // established the new session -- see ShiroRequestSessionCacheResetter's javadoc for why this ordering
    // is the fix, not tidiness.
    // ActionContext.getContext() is checked FIRST -- ServletActionContext.getRequest() derefs it
    // unconditionally in this Struts version and NPEs when none is bound (see freshSessionMap()'s javadoc,
    // and this class's own noRequestBoundAtAllDoesNotThrow test).
    if (ActionContext.getContext() != null && ServletActionContext.getRequest() != null) {
      ShiroRequestSessionCacheResetter.clearCachedSession(ServletActionContext.getRequest());
    }

    // CRITICAL FIX (review Issue 1): re-point this action's session view -- and ActionContext's -- at the
    // session Subject.login(...) just created, never the one step 6 stopped. See freshSessionMap()'s
    // javadoc for exactly why the OLD SessionMap is unusable from here on in production.
    Map<String, Object> freshSession = this.freshSessionMap();
    this.setSession(freshSession);
    // Struts 6.x's ActionContext is an immutable value (withSession(...) returns a NEW instance rather
    // than mutating in place -- there is no setSession(Map) here, unlike BaseAction). Rebinding is what
    // makes the swap visible to whatever runs after this action returns (result rendering, ${crpSession}).
    if (ActionContext.getContext() != null) {
      ActionContext.bind(ActionContext.getContext().withSession(freshSession));
    }

    LOG.info("Cognito sign-in succeeded for Global Unit {}",
      loggedCrp == null ? "<unknown>" : loggedCrp.getAcronym());

    // Step 8: the shared tail (T01). Applies gate 4 (crp_users membership), session attributes, custom
    // parameters, saveLastLogin, and post-login routing -- identical to the local path.
    String tail = this.finishLogin(loggedUser, loggedCrp, returnUrl);
    if (INPUT.equals(tail)) {
      // CHG-COGNITO-AUTH-001-T21 (V-7): three of finishLogin's own refusals -- gate 4 (:393, not a
      // crp_users member), an unresolvable Global Unit (:402, loggedCrp == null), and an unmapped
      // GlobalUnitType (:447, the default: arm) -- still return INPUT, which would render login.ftl IN
      // PLACE at cognitoCallback.do?code=...&state=..., the same parked-URL defect T20 closed for the nine
      // refuse() branches above. finishLogin is LoginAction's shared tail (T01) and is not touched here --
      // every side effect it performs (the log line, the field error, setCrpSession, getSession().clear(),
      // Subject.logout(), user.setPassword(null)) already ran before this line returned, so nothing here
      // re-runs, reorders, or re-decides any of them. This adaptation only converts WHERE the browser
      // lands; it does not fix route C's pre-existing session/state inconsistency (execution.md 40.1) nor
      // route A's lost setCrpSession context (execution.md 40.4) -- both remain open, by design.
      // CHG-COGNITO-AUTH-001-T22 (V-6): routes A, B and C all collapse into the single generic public
      // category -- route C in particular computes NO message of its own (execution.md 43.1/43.2), so
      // AUTH_ERROR_FAILED is the only literal that could ever be correct here.
      this.setUrl(this.getBaseUrl() + "/login.do?authError=" + AUTH_ERROR_FAILED);
      return LOGIN;
    }
    // Any other result -- SUCCESS, or the LOGIN a type-2 Global Unit returns with its own
    // centerDashboard.do url (LoginAction:434) -- passes through unchanged. Overwriting a url the tail
    // already set would send a centre user to the login page instead of their dashboard.
    return tail;
  }

  @Override
  public String execute() throws Exception {
    return this.callback(this.code, this.state, this.error);
  }

  /**
   * Builds a session view bound to whatever Shiro session is current <b>at the moment this is called</b> --
   * used to re-point this action's session field at the session {@code Subject.login(...)} just created at
   * step 7, never the one {@code session.stop()} destroyed at step 6.
   * <p>
   * <b>Why this exists at all -- verified against this checkout, not assumed.</b> {@code web.xml} maps the
   * Shiro filter to {@code /*} ahead of Struts, and {@code MarloShiroConfiguration} wires a native {@code
   * DefaultWebSessionManager}, so in production {@code BaseAction.getSession()} is Struts' own {@link
   * SessionMap} -- and decompiling {@code SessionMap} (struts2-core 6.8.0, the version this POM pins)
   * confirms its constructor calls {@code request.getSession(false)} <b>exactly once</b>, storing the
   * result. The {@link SessionMap} Struts installed on this action before {@code execute()} ran therefore
   * still points at the session this method's caller stops at step 6 -- every write through it after that
   * point throws {@code IllegalStateException} via {@code ShiroHttpSession}, on both the success path
   * ({@code LoginAction}'s {@code getSession().put(SESSION_USER, ...)}) and the membership-failure path
   * ({@code getSession().clear()}). This was the review's Issue 1, and every one of the ten tests in the
   * first submission passed anyway, because {@code CognitoCallbackActionTest} seeded a plain, never-stopped
   * {@code HashMap} -- gentler than production, and structurally incapable of expressing the defect. See
   * {@code theOldSessionIsNeverReusedAfterLoginSucceeds} for the corrected regression test.
   * <p>
   * With a live request (production), a fresh {@link SessionMap} correctly recaptures whatever session
   * {@code request.getSession(false)} resolves to <b>right now</b> -- the new one, since the Shiro-wrapped
   * request (installed ahead of Struts by {@code web.xml}) delegates straight to {@code Subject.getSession()}.
   * With no live request (this class's own unit tests bind none at all -- see
   * {@code noRequestBoundAtAllDoesNotThrow}), a plain map is enough: nothing in this test tree exercises
   * container-level session persistence, only that the OLD, now-invalidated map is no longer the one {@code
   * finishLogin} writes through.
   *
   * @return a working, empty session view bound to the session that is current right now
   */
  protected Map<String, Object> freshSessionMap() {
    // ServletActionContext.getRequest() does not null-check internally in this Struts version -- it derefs
    // ActionContext.getContext() unconditionally and NPEs when none is bound, exactly the no-live-request
    // shape every test in this class that has no ServletActionContext bound (this class's tests included).
    if (ActionContext.getContext() == null) {
      return new HashMap<>();
    }
    HttpServletRequest request = ServletActionContext.getRequest();
    if (request != null) {
      return new SessionMap(request);
    }
    return new HashMap<>();
  }

  public String getCode() {
    return this.code;
  }

  public String getError() {
    return this.error;
  }

  public String getState() {
    return this.state;
  }

  /**
   * A2-2462: writes {@code first_name} / {@code last_name} from the validated token, under a two-part rule.
   * <p>
   * <b>A field is written when the claim is present AND either of these holds:</b>
   * <ol>
   * <li><b>This is the account's first sign-in</b> ({@code users.last_login} is still null). Whatever the
   * row carries was typed by an administrator at creation time and is <em>provisional</em>; the corporate
   * directory is the authority, and this is the one moment it gets to say so. Both fields are replaced,
   * populated or not.</li>
   * <li><b>The stored field is blank</b>, on any sign-in. A row that ends up incomplete later still gets
   * completed rather than staying empty.</li>
   * </ol>
   * <p>
   * <b>Why first sign-in and not "blank only".</b> The earlier revision of this method wrote only into a
   * blank field, and measured against the development database on 2026-09-09 that condition was true for
   * <b>0 of 3,599</b> accounts. Worse, it could not become true in the flow this feature exists for: the
   * create-user dialog requires a first and last name, so an administrator always fills them and the
   * blank test never fires. First sign-in is the trigger that makes the corporate value actually land --
   * exactly once, on real data.
   * <p>
   * <b>After that first sign-in the values are never refreshed again.</b> A later change to the corporate
   * name is not picked up. That is deliberate: it protects a deliberate administrative correction, and the
   * alternative -- refreshing on every sign-in -- would silently undo one.
   * <p>
   * <b>Both claims are optional.</b> They arrive only because {@code CognitoLoginAction} requests the
   * {@code profile} scope; a pool that stops emitting them must leave the field as it is, never write a
   * blank over it. Hence the guard on the incoming value as well as on the stored one.
   * <p>
   * <b>The first-sign-in test must be read before {@code saveLastLogin} runs</b>, which is why this sits
   * where it does: the row was loaded a few lines above and still carries the database's {@code last_login},
   * and the caller's {@code saveLastLogin} on the next line is what sets it. Moving this call below that
   * one makes the test false for every account, forever, without failing anything visibly.
   * <p>
   * <b>This does not persist by itself</b> -- the caller's {@code saveLastLogin} does, and here it is the
   * only thing that can. {@code saveUser} carries no {@code @Transactional}, so an UPDATE through it reaches
   * the database only when something <em>later in the same request</em> opens and commits a transaction,
   * whose flush covers the whole persistence context. That is why {@code saveUser} does persist on the local
   * login path, where {@code finishLogin}'s {@code saveLastLogin} commits afterwards. <b>In this callback
   * there is no afterwards</b>: {@code saveLastLogin} here <em>is</em> the last persistence step, so a
   * {@code saveUser} at this point would be flushed by nothing and lose the write in silence.
   *
   * @param loggedUser the Hibernate-managed row for the authenticated account
   * @param validatedAssertion the assertion whose signature, issuer, audience, expiry and nonce have all
   *        already passed -- an unverified claim must never reach a write
   */
  private void applyNamesFromToken(User loggedUser, CognitoAssertion validatedAssertion) {
    // Read before the caller's saveLastLogin stamps it -- see the javadoc.
    boolean firstSignIn = loggedUser.getLastLogin() == null;
    boolean written = false;
    if (!this.isBlank(validatedAssertion.getGivenName())
      && (firstSignIn || this.isBlank(loggedUser.getFirstName()))) {
      loggedUser.setFirstName(validatedAssertion.getGivenName());
      written = true;
    }
    if (!this.isBlank(validatedAssertion.getFamilyName())
      && (firstSignIn || this.isBlank(loggedUser.getLastName()))) {
      loggedUser.setLastName(validatedAssertion.getFamilyName());
      written = true;
    }
    if (written) {
      // The names themselves are personal data and are deliberately not logged -- the user id locates the
      // row, and this line lands in the same log as every other Cognito event.
      LOG.info("Cognito callback applied the directory name for user {} (first sign-in: {})",
        loggedUser.getId(), Boolean.valueOf(firstSignIn));
    }
  }

  /**
   * @param value the value to test
   * @return {@code true} when {@code value} is {@code null}, empty, or whitespace only
   */
  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }

  private String refuse(String i18nKey) {
    // CHG-COGNITO-AUTH-001-T20 (V-5): kept computed for T22 (V-6, execution.md 43) -- addFieldError's
    // "loginMessage" never survives the redirect below (a new request handles /login.do) and is not what
    // renders the visible message; it stays here only because deleting it would erase the granular
    // branch-to-key mapping this method still needs to decide the PUBLIC category two lines down.
    this.addFieldError("loginMessage", this.getText(i18nKey));
    // CHG-COGNITO-AUTH-001-T22 (V-6): collapse the granular key into exactly one of the two public
    // categories LoginAction exposes -- UNAVAILABLE_KEY is the only i18n key this class ever passes here
    // that means "infrastructure, not account"; every other key (cognitoNotEligible, inactive,
    // invalidUserCrp, cognitoFailed) is account-shaped and MUST NOT be distinguishable, per SEC-006.
    String publicAuthError = UNAVAILABLE_KEY.equals(i18nKey) ? AUTH_ERROR_UNAVAILABLE : AUTH_ERROR_FAILED;
    // V-5: redirect to the canonical login URL instead of rendering login.ftl in place at
    // cognitoCallback.do?code=...&state=... -- that parked URL, with the authorization code and state
    // still in it, is what poisoned the Referer and produced V-4. Reuses the existing "login" result
    // already mapped at struts-home.xml (redirect ${url}) -- the same this.url = getBaseUrl() + "/..."
    // concatenation LoginAction already uses (LoginAction:312, :434). No new result, no new mechanism.
    // The appended parameter SELECTS one of two literals; it never carries the caller's own text.
    this.setUrl(this.getBaseUrl() + "/login.do?authError=" + publicAuthError);
    return LOGIN;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public void setError(String error) {
    this.error = error;
  }

  public void setState(String state) {
    this.state = state;
  }
}
