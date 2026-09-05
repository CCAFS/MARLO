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

import org.cgiar.ccafs.marlo.action.home.CognitoLoginAction.PendingAuthorization;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CustomParameterManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.ParameterManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CustomParameter;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitType;
import org.cgiar.ccafs.marlo.data.model.Parameter;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.opensymphony.xwork2.Action;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.util.ThreadContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Covers CHG-COGNITO-AUTH-001-T08: {@code CognitoLoginAction}, the authorize redirect.
 * <p>
 * Every collaborator is a hand-rolled double: MARLO has no mocking framework ({@code DEC-005} is
 * {@code PENDING}), matching {@code LoginActionFinishLoginTest} and {@code CognitoAssertionTest}. Tests drive
 * the protected {@link CognitoLoginAction#authorize(String)} seam directly -- the same split T01 used for
 * {@code finishLogin} -- rather than {@code execute()}, so no live Struts request needs to be faked for the
 * five scenarios T08 names. What that split does <b>not</b> cover is recorded in this class's trailing
 * javadoc note and in the implementer's final report: it proves nothing about the Struts interceptor stack
 * that must actually route a request to {@code execute()} in production (see
 * {@code CognitoUnloggedStackReachabilityTest} for that half).
 * <p>
 * <b>Documented deviation from one sentence in design.md 13.1.</b> That section says this action "does not
 * yet know who the user is" and so cannot check {@code is_cgiar_user}. Every test here supplies an
 * {@code email} parameter (mirroring the value the login wizard's {@code crpByEmail.do} step already
 * collected) precisely so it CAN, because {@code tasks.md} T08 scopes that check into this action and names a
 * test for it. Both readings cannot be simultaneously true; this implementation follows {@code tasks.md}'s
 * literal scope and test list, flagged here and in the implementer's report rather than silently picked.
 */
public class CognitoLoginActionTest {

  private static final String VALID_EMAIL = "priya.cgiar@cgiar.org";
  private static final long GLOBAL_UNIT_ID = 55L;
  private static final Pattern BASE64_URL_43 = Pattern.compile("^[A-Za-z0-9_-]{43}$");
  private static final Pattern PKCE_VERIFIER_CHARSET = Pattern.compile("^[A-Za-z0-9._~-]{43,128}$");

  private RecordingUserManager userManager;
  private StubCustomParameterManager customParameterManager;
  private StubParameterManager parameterManager;
  private NoOpGlobalUnitManager crpManager;

  private static GlobalUnit globalUnit() {
    GlobalUnitType type = new GlobalUnitType();
    type.setId(Long.valueOf(1L));
    GlobalUnit globalUnit = new GlobalUnit();
    globalUnit.setId(Long.valueOf(GLOBAL_UNIT_ID));
    globalUnit.setAcronym("TESTCRP");
    globalUnit.setGlobalUnitType(type);
    return globalUnit;
  }

  private static User cgiarUser() {
    User user = new User();
    user.setId(Long.valueOf(9001L));
    user.setEmail(VALID_EMAIL);
    user.setCgiarUser(true);
    user.setActive(true);
    return user;
  }

  private static User localUser() {
    User user = new User();
    user.setId(Long.valueOf(9002L));
    user.setEmail("jane.local@cgiar.org");
    user.setCgiarUser(false);
    user.setActive(true);
    return user;
  }

  private static Parameter catalogRow(String defaultValue) {
    Parameter parameter = new Parameter();
    parameter.setKey(APConstants.COGNITO_AUTH_ACTIVE);
    parameter.setDefaultValue(defaultValue);
    parameter.setFormat(Integer.valueOf(1));
    parameter.setCategory(Integer.valueOf(2));
    return parameter;
  }

  private static CustomParameter activeOverride(String value) {
    CustomParameter override = new CustomParameter();
    override.setValue(value);
    override.setActive(true);
    override.setParameter(catalogRow("false"));
    return override;
  }

  /** Parses {@code url}'s query string into a decoded key/value map. Test-only, not production code. */
  private static Map<String, String> queryParams(String url) throws Exception {
    Map<String, String> params = new HashMap<String, String>();
    String query = url.substring(url.indexOf('?') + 1);
    for (String pair : query.split("&")) {
      int eq = pair.indexOf('=');
      String key = pair.substring(0, eq);
      String value = URLDecoder.decode(pair.substring(eq + 1), StandardCharsets.UTF_8.name());
      params.put(key, value);
    }
    return params;
  }

  /**
   * CHG-COGNITO-AUTH-001-T19 test helper. {@code sameOriginOrNull} is private, so tests 1, 2, 3 and 6 below
   * reach it through reflection to isolate the helper's own logic. Test 7 deliberately does <b>not</b> use
   * this helper -- see its javadoc for why that distinction is the point of the task.
   */
  private static String invokeSameOriginOrNull(CognitoLoginAction action, String candidate) throws Exception {
    Method method = CognitoLoginAction.class.getDeclaredMethod("sameOriginOrNull", String.class);
    method.setAccessible(true);
    return (String) method.invoke(action, candidate);
  }

  @Before
  public void setUp() {
    SecurityUtils.setSecurityManager(new DefaultSecurityManager());
    this.userManager = new RecordingUserManager();
    this.userManager.register(cgiarUser());
    this.userManager.register(localUser());
    this.customParameterManager = new StubCustomParameterManager();
    this.parameterManager = new StubParameterManager();
    this.parameterManager.catalogRow = catalogRow("false");
    this.crpManager = new NoOpGlobalUnitManager();
    this.crpManager.unit = globalUnit();
  }

  @After
  public void tearDown() {
    SecurityUtils.setSecurityManager(null);
    ThreadContext.remove();
  }

  private TestableCognitoLoginAction newAction(APConfig config) {
    TestableCognitoLoginAction action =
      new TestableCognitoLoginAction(config, this.userManager, this.crpManager, this.customParameterManager,
        this.parameterManager);
    this.userManager.action = action;
    return action;
  }

  private TestableCognitoLoginAction eligibleAction() {
    this.customParameterManager.override = activeOverride("true");
    TestableCognitoLoginAction action = this.newAction(new ConfiguredApConfig());
    action.setGlobalUnitId(Long.valueOf(GLOBAL_UNIT_ID));
    action.setEmail(VALID_EMAIL);
    action.setAgree(Boolean.TRUE);
    return action;
  }

  /**
   * T08 test 1. The client's {@code cognitoEnabled} is a rendering hint only (design.md 9.2); this action's
   * own re-check must refuse a Global Unit whose flag is off regardless of anything else in the request.
   * <b>This is the task's named "Fails when" clause</b>: with the flag re-check removed, this exact scenario
   * must instead produce a redirect. See the implementer's report for the mutation run against this test.
   */
  @Test
  public void aRequestNamingAGlobalUnitWhoseFlagIsOffIsRefused() {
    // Flag resolves to false: no active override, and the catalog default (set in setUp()) is "false".
    TestableCognitoLoginAction action = this.newAction(new ConfiguredApConfig());
    action.setGlobalUnitId(Long.valueOf(GLOBAL_UNIT_ID));
    action.setEmail(VALID_EMAIL);
    action.setAgree(Boolean.TRUE);

    String result = action.authorize(null);

    assertEquals(Action.INPUT, result);
    assertNull("no redirect target may be produced for a disabled Global Unit", action.getAuthorizeUrl());
    assertFalse("terms must not be recorded on a refused attempt", this.userManager.saveUserCalled);
  }

  /**
   * T08 test 2. {@code is_cgiar_user = 0} must be refused even though the Global Unit's flag is on -- the
   * documented deviation from design.md 13.1 (see class javadoc) is what makes this check possible here at
   * all.
   */
  @Test
  public void aRequestForANonCgiarAccountIsRefused() {
    this.customParameterManager.override = activeOverride("true");
    TestableCognitoLoginAction action = this.newAction(new ConfiguredApConfig());
    action.setGlobalUnitId(Long.valueOf(GLOBAL_UNIT_ID));
    action.setEmail(localUser().getEmail());
    action.setAgree(Boolean.TRUE);

    String result = action.authorize(null);

    assertEquals(Action.INPUT, result);
    assertNull(action.getAuthorizeUrl());
    assertFalse("terms must not be recorded on a refused attempt", this.userManager.saveUserCalled);
  }

  /**
   * T08 test 3. <b>Not evidence when asserted by "the two values differ"</b> -- a broken generator can differ
   * by luck. This asserts the length and character space of {@code state}, {@code nonce} and the PKCE
   * verifier, and that 100 invocations of the real {@link java.security.SecureRandom}-backed generator
   * produce 100 distinct values for each.
   */
  @Test
  public void stateNonceAndVerifierAreEachUnguessableAcrossOneHundredInvocations() throws Exception {
    Set<String> states = new HashSet<String>();
    Set<String> nonces = new HashSet<String>();
    Set<String> challenges = new HashSet<String>();
    Set<String> verifiers = new HashSet<String>();

    for (int i = 0; i < 100; i++) {
      TestableCognitoLoginAction action = this.eligibleAction();
      String result = action.authorize(null);
      assertEquals(Action.SUCCESS, result);

      Map<String, String> params = queryParams(action.getAuthorizeUrl());
      String state = params.get("state");
      String nonce = params.get("nonce");
      String challenge = params.get("code_challenge");

      assertTrue("state must be base64url, 43 chars: " + state, BASE64_URL_43.matcher(state).matches());
      assertTrue("nonce must be base64url, 43 chars: " + nonce, BASE64_URL_43.matcher(nonce).matches());
      assertTrue("code_challenge must be base64url, 43 chars: " + challenge,
        BASE64_URL_43.matcher(challenge).matches());

      Session shiroSession = SecurityUtils.getSubject().getSession();
      PendingAuthorization pending =
        (PendingAuthorization) shiroSession.getAttribute(APConstants.COGNITO_PENDING_AUTHORIZATION);
      assertTrue("the PKCE verifier must meet RFC 7636's length (43-128) and charset rules: "
        + pending.getVerifier(), PKCE_VERIFIER_CHARSET.matcher(pending.getVerifier()).matches());

      states.add(state);
      nonces.add(nonce);
      challenges.add(challenge);
      verifiers.add(pending.getVerifier());
    }

    assertEquals("100 invocations must produce 100 distinct state values", 100, states.size());
    assertEquals("100 invocations must produce 100 distinct nonce values", 100, nonces.size());
    assertEquals("100 invocations must produce 100 distinct code_challenge values", 100, challenges.size());
    assertEquals("100 invocations must produce 100 distinct PKCE verifiers", 100, verifiers.size());
  }

  /**
   * T08 test 4. The authorize URL carries {@code state}, {@code nonce}, {@code code_challenge} and
   * {@code code_challenge_method=S256}, plus the client id, redirect URI and {@code response_type=code} an
   * authorization-code + PKCE request requires (design.md 13.2). Also confirms DD-4's binding: the same
   * Global Unit, return URL, nonce and verifier are readable back from the server-side Shiro session, keyed
   * by {@code state} -- never carried in the redirect URI itself.
   */
  @Test
  public void theAuthorizeUrlCarriesStateNonceAndPkceParameters() throws Exception {
    TestableCognitoLoginAction action = this.eligibleAction();

    String result = action.authorize("https://marlo.example.org/testcrp/projectList.do");

    assertEquals(Action.SUCCESS, result);
    String url = action.getAuthorizeUrl();
    assertTrue("must redirect to the configured Cognito domain's authorize endpoint",
      url.startsWith("https://test-pool.auth.us-east-1.amazoncognito.com/oauth2/authorize?"));

    Map<String, String> params = queryParams(url);
    assertEquals("code", params.get("response_type"));
    assertEquals("test-client-id", params.get("client_id"));
    assertEquals("https://marlo.example.org/cognitoCallback.do", params.get("redirect_uri"));
    assertEquals("S256", params.get("code_challenge_method"));
    assertTrue(params.containsKey("state"));
    assertTrue(params.containsKey("nonce"));
    assertTrue(params.containsKey("code_challenge"));

    // DD-4: the round-trip values are bound server-side under fixed keys -- never carried in the URI.
    //
    // Asserted against the PARSED parameter values, not the raw URL. A substring search for "55" across three
    // 43-character base64url tokens matches by chance in roughly 3% of runs, and a random red in an
    // authentication suite teaches the next reader to re-run rather than investigate -- worse than no
    // assertion at all.
    assertFalse("the redirect URI must not carry the Global Unit id",
      params.containsValue(String.valueOf(GLOBAL_UNIT_ID)));
    assertFalse("the redirect URI must not carry the return URL", params.containsValue("testcrp"));
    Session shiroSession = SecurityUtils.getSubject().getSession();
    PendingAuthorization pending =
      (PendingAuthorization) shiroSession.getAttribute(APConstants.COGNITO_PENDING_AUTHORIZATION);
    assertEquals(Long.valueOf(GLOBAL_UNIT_ID), pending.getGlobalUnitId());
    assertEquals("https://marlo.example.org/testcrp/projectList.do", pending.getReturnUrl());
    assertEquals(params.get("nonce"), pending.getNonce());
  }

  /**
   * T08 test 5, <b>rewritten after the independent audit</b>.
   * <p>
   * It originally asserted that terms were <i>persisted</i> here, which is what design.md §5.4 literally
   * asked for. The audit established that doing so is a defect: this endpoint is unauthenticated and
   * {@code email} is unverified, so
   * {@code GET /cognitoLogin.do?email=victim@cgiar.org&agree=false} let anyone <b>revoke</b> a third party's
   * compliance record. §5.4 was amended and the write moved to the callback, where the ID token has proved
   * identity.
   * <p>
   * <b>The check stayed, and it matters:</b> on the local path the accept checkbox carries HTML5
   * {@code required} inside the form, but DD-2 puts this path's control <i>outside</i> the form, so nothing
   * in the browser stops an unaccepted submission. Without this guard a CGIAR user completes sign-in having
   * declined — the same compliance regression §5.4 exists to prevent, arriving by a different door.
   */
  @Test
  public void anUnacceptedOrRevokedTermsRequestIsRefusedAndWritesNothing() {
    for (Boolean submitted : new Boolean[] {Boolean.FALSE, null}) {
      TestableCognitoLoginAction action = this.eligibleAction();
      action.setAgree(submitted);

      String result = action.authorize(null);

      assertEquals("agree=" + submitted + " must not start the flow", Action.INPUT, result);
      assertNull("no authorize URL may be built", action.getAuthorizeUrl());
      assertFalse("this endpoint must never write users.agree_terms -- it cannot prove who the email is",
        this.userManager.saveUserCalled);
    }
  }

  /**
   * The open-redirect guard (audit finding 4). {@code returnUrl} arrives in the {@code Referer} of a GET
   * navigation, so a page the attacker controls chooses it — and can set {@code Referrer-Policy: unsafe-url}
   * to defeat the browser's default truncation. It is bound into the session and later handed to
   * {@code finishLogin}, whose only test is {@code urlAction.contains(".do")} — which a host like
   * {@code https://evil.do/} satisfies on its origin alone. The victim would authenticate for real at the
   * CGIAR IdP and be redirected off-site immediately after.
   * <p>
   * DD-4 requires the return URL to be <b>not attacker-controllable</b>. Binding it server-side only stops it
   * being changed on the way back; this is the half that stops it being chosen at mint time.
   */
  @Test
  public void anOffSiteReturnUrlIsDiscardedAndAnOnSiteOneSurvives() {
    String[] hostile = {"https://evil.do/anything.do", "https://marlo.example.org.evil.com/x.do",
      "http://marlo.example.org/x.do", "//evil.example/x.do"};

    for (String candidate : hostile) {
      TestableCognitoLoginAction action = this.eligibleAction();

      assertEquals(Action.SUCCESS, action.authorize(candidate));

      PendingAuthorization pending = (PendingAuthorization) SecurityUtils.getSubject().getSession()
        .getAttribute(APConstants.COGNITO_PENDING_AUTHORIZATION);
      assertNull("an off-site return URL must not survive into the session: " + candidate,
        pending.getReturnUrl());
    }

    TestableCognitoLoginAction action = this.eligibleAction();
    assertEquals(Action.SUCCESS, action.authorize("https://marlo.example.org/testcrp/projectList.do"));
    PendingAuthorization pending = (PendingAuthorization) SecurityUtils.getSubject().getSession()
      .getAttribute(APConstants.COGNITO_PENDING_AUTHORIZATION);
    assertEquals("a same-origin deep link must survive", "https://marlo.example.org/testcrp/projectList.do",
      pending.getReturnUrl());
  }

  /** The happy path must still mint state, and must still write nothing to the user's row. */
  @Test
  public void anAcceptedRequestMintsStateAndWritesNothing() {
    TestableCognitoLoginAction action = this.eligibleAction();

    String result = action.authorize(null);

    assertEquals(Action.SUCCESS, result);
    assertFalse("terms are recorded by the callback, not here", this.userManager.saveUserCalled);
  }

  /**
   * CHG-COGNITO-AUTH-001-T19 test 1 (V-4). The exact defect shape from execution.md 34: a same-origin
   * {@code cognitoCallback.do?code=...&state=...} -- carrying the query string a real callback redirect always
   * has -- must be rejected by the helper, not merely by the origin check it already passes.
   */
  @Test
  public void aCallbackUrlWithAQueryStringIsRejected() throws Exception {
    TestableCognitoLoginAction action = this.eligibleAction();

    String result =
      invokeSameOriginOrNull(action, "https://marlo.example.org/cognitoCallback.do?code=abc123&state=xyz789");

    assertNull("a same-origin callback URL must be rejected even though it is same-origin", result);
  }

  /**
   * CHG-COGNITO-AUTH-001-T19 test 2 (V-4). {@code cognitoLogin.do} itself -- the endpoint that mints the
   * pending authorization in the first place -- must never be accepted as a destination to return to.
   */
  @Test
  public void theLoginEndpointItselfIsRejected() throws Exception {
    TestableCognitoLoginAction action = this.eligibleAction();

    String result = invokeSameOriginOrNull(action, "https://marlo.example.org/cognitoLogin.do");

    assertNull(result);
  }

  /**
   * CHG-COGNITO-AUTH-001-T19 test 3 (V-4). The comparison is case-insensitive: a differently-cased
   * authentication endpoint must be rejected exactly as the lowercase form is, for both {@code cognitoCallback}
   * and {@code validateUser}.
   */
  @Test
  public void aCaseVariantAuthenticationEndpointIsRejected() throws Exception {
    TestableCognitoLoginAction action = this.eligibleAction();

    assertNull(invokeSameOriginOrNull(action, "https://marlo.example.org/CognitoCallback.DO?code=abc"));
    assertNull(invokeSameOriginOrNull(action, "https://marlo.example.org/VALIDATEUSER.do"));
  }

  /**
   * CHG-COGNITO-AUTH-001-T19 test 4 (V-4). A legitimate same-origin deep link -- the exact functionality this
   * task must not break -- is still accepted, unchanged, by the helper.
   */
  @Test
  public void aLegitimateSameOriginDeepLinkIsStillAccepted() throws Exception {
    TestableCognitoLoginAction action = this.eligibleAction();
    String candidate = "https://marlo.example.org/aiccra/projects.do?id=1";

    String result = invokeSameOriginOrNull(action, candidate);

    assertEquals("a legitimate deep link must survive unchanged", candidate, result);
  }

  /**
   * CHG-COGNITO-AUTH-001-T19 test 5 (V-4). Off-site rejection is untouched by this task -- this is the same
   * guarantee {@link #anOffSiteReturnUrlIsDiscardedAndAnOnSiteOneSurvives()} already proves at the
   * {@code authorize(...)} level; this asserts it at the helper level too, including an external URL that
   * happens to end in an authentication-endpoint-looking path.
   */
  @Test
  public void anExternalOriginIsStillRejected() throws Exception {
    TestableCognitoLoginAction action = this.eligibleAction();

    assertNull(invokeSameOriginOrNull(action, "https://evil.example/anything.do"));
    assertNull(invokeSameOriginOrNull(action, "https://evil.example/cognitoCallback.do"));
  }

  /**
   * CHG-COGNITO-AUTH-001-T19 test 6 (V-4). {@code null} and a blank {@code Referer} must still return
   * {@code null} -- unchanged pre-existing behaviour, re-asserted here so the new rejection branch cannot be
   * reached for either input.
   */
  @Test
  public void nullAndBlankReturnUrlsStillReturnNull() throws Exception {
    TestableCognitoLoginAction action = this.eligibleAction();

    assertNull(invokeSameOriginOrNull(action, null));
    assertNull(invokeSameOriginOrNull(action, "   "));
  }

  /**
   * CHG-COGNITO-AUTH-001-T19 test 7 (V-4) -- <b>the point of this task.</b> Every test above reaches
   * {@code sameOriginOrNull} through reflection, isolating the helper from the real seam every caller actually
   * uses. execution.md 27 names that exact shape as the pattern this spec has shipped nine times before:
   * correct in isolation, dead through the real framework, certified green by a double gentler than
   * production. This test instead drives {@link CognitoLoginAction#authorize(String)} -- the seam
   * {@code authorize}'s own comment (:209-211) says every caller and every test must use -- and reads the
   * result back out of the very {@link PendingAuthorization} {@code CognitoCallbackAction} consumes in
   * production, proving the rejection is wired all the way through, not merely correct on paper.
   */
  @Test
  public void anAuthenticationEndpointReturnUrlIsRejectedThroughTheRealAuthorizeSeam() {
    TestableCognitoLoginAction action = this.eligibleAction();

    String result = action.authorize("https://marlo.example.org/cognitoCallback.do?code=abc123&state=xyz789");

    assertEquals(Action.SUCCESS, result);
    PendingAuthorization pending = (PendingAuthorization) SecurityUtils.getSubject().getSession()
      .getAttribute(APConstants.COGNITO_PENDING_AUTHORIZATION);
    assertNull("V-4: a return URL resolving to the callback endpoint must be stored as null, not the poisoned "
      + "callback URL -- null is what makes finishLogin fall through to the dashboard route instead of "
      + "redirecting back into an already-consumed, single-use PendingAuthorization",
      pending.getReturnUrl());
  }

  // ---------------------------------------------------------------------------------------------------------
  // CHG-COGNITO-AUTH-001-T19 -- declared coverage extension (approved by the user 2026-09-05, tasks.md).
  //
  // T19's production code is audited PASS and is not touched here. execution.md 35.2 found that test 8's
  // mutation (delete the whole guard) is the only one of seven the original eight tests catch: six weaker
  // mutations -- each one narrower than "delete the guard" -- left the suite entirely green, and four of them
  // reopen bypass classes T19's own "Fails when" list already names. The tests below close that gap, one per
  // named mutation. Every one of them is expected to pass against the CURRENT, unmodified
  // CognitoLoginAction -- if any had failed, the rule this extension carries is to stop and report the
  // discrepancy rather than adjust production code to fit the test.
  // ---------------------------------------------------------------------------------------------------------

  /**
   * T19 coverage extension, E1. {@code getPath()} percent-decodes; {@code getRawPath()} does not. A candidate
   * whose final segment is the callback path written as {@code cognitoCallback%2Edo} -- {@code %2E} being the
   * percent-encoded {@code .} -- must still be recognized as {@code cognitoCallback.do} and rejected. Swapping
   * {@code getPath()} for {@code getRawPath()} in {@link CognitoLoginAction#isAuthenticationEndpoint(String)}
   * would let this candidate bypass silently -- confirmed against a scratch harness before this test was
   * written: {@code new URI("https://marlo.example.org/cognitoCallback%2Edo").normalize().getPath()} yields
   * {@code /cognitoCallback.do}.
   */
  @Test
  public void aPercentEncodedDotInTheCallbackPathIsStillRejected() throws Exception {
    TestableCognitoLoginAction action = this.eligibleAction();

    String result = invokeSameOriginOrNull(action, "https://marlo.example.org/cognitoCallback%2Edo");

    assertNull("a percent-encoded '.' must decode before the endpoint comparison, exactly as a literal '.' "
      + "would: getPath() must be used, never getRawPath()", result);
  }

  /**
   * T19 coverage extension, E2, exactly as named in the task's table. Still rejected today, and this test
   * still asserts that -- but it does <b>not</b> kill the mutation the table names it against.
   * <p>
   * <b>Verified with a standalone mutation harness before writing this test</b>: the final path segment
   * this method compares is extracted with {@code lastIndexOf('/')} over the (possibly normalized) path
   * string. For {@code .../x/../cognitoCallback.do}, that final segment is {@code cognitoCallback.do}
   * whether or not {@link URI#normalize()} runs -- the leading {@code x/../} only ever affects segments
   * BEFORE the last one, never the last segment's own text. Deleting {@code .normalize()} and re-running
   * this exact candidate through the harness still rejects it; the mutation survives this test completely
   * unnoticed.
   * <p>
   * The candidate that genuinely distinguishes the two is
   * {@link #aTraversalThatCancelsBackIntoTheCallbackIsStillRejected()} below, where the {@code ../} sits
   * <b>after</b> the callback segment and cancels a segment that follows it -- only {@code .normalize()}
   * resolves that back down to {@code /cognitoCallback.do}. Both tests are kept: this one because the task
   * named it explicitly and it is still a true, if non-load-bearing, assertion; the other because it is the
   * one that actually reddens under this row's mutation.
   */
  @Test
  public void aDotDotTraversalPrefixBeforeTheCallbackIsStillRejected() throws Exception {
    TestableCognitoLoginAction action = this.eligibleAction();

    String result = invokeSameOriginOrNull(action, "https://marlo.example.org/x/../cognitoCallback.do");

    assertNull("still correctly rejected today, but -- see this test's javadoc -- deleting .normalize() does "
      + "NOT turn this assertion red; it is not the mutation-killing case despite being named as one",
      result);
  }

  /**
   * T19 coverage extension, E2 corrected. This is the candidate that actually reddens when
   * {@link URI#normalize()} is deleted: the {@code ../} sits AFTER the callback segment
   * ({@code /cognitoCallback.do/foo/..}), so a browser resolving it lands back on
   * {@code /cognitoCallback.do/} -- exactly what {@code URI#normalize()} computes
   * ({@code /cognitoCallback.do/foo/..} normalizes to {@code /cognitoCallback.do/}). Without normalization,
   * the raw final segment is the bare token {@code ..}, which matches nothing in the closed set, so the
   * candidate would be silently ACCEPTED -- a same-origin URL that resolves straight back to the callback
   * endpoint, stored as a legitimate return URL.
   */
  @Test
  public void aTraversalThatCancelsBackIntoTheCallbackIsStillRejected() throws Exception {
    TestableCognitoLoginAction action = this.eligibleAction();

    String result = invokeSameOriginOrNull(action, "https://marlo.example.org/cognitoCallback.do/foo/..");

    assertNull("a trailing '../' that resolves back onto the callback endpoint must be rejected -- this is "
      + "what URI#normalize() actually protects, proven by deleting it and watching this assertion redden",
      result);
  }

  /**
   * T19 coverage extension, E3. A trailing {@code ;jsessionid=...} matrix parameter -- a real artifact of
   * URL-rewriting session tracking, not a contrived input -- must be stripped from the final path segment
   * before the comparison, exactly as the callback path's own javadoc documents. Deleting that strip would
   * let {@code cognitoCallback.do;jsessionid=ABC} bypass, because the raw last segment would no longer equal
   * any entry in {@link CognitoLoginAction#AUTHENTICATION_ENDPOINT_PATHS}.
   */
  @Test
  public void aTrailingJsessionidMatrixParameterOnTheCallbackIsStillRejected() throws Exception {
    TestableCognitoLoginAction action = this.eligibleAction();

    String result = invokeSameOriginOrNull(action, "https://marlo.example.org/cognitoCallback.do;jsessionid=ABC");

    assertNull("a trailing ';jsessionid=...' matrix parameter must be stripped before the endpoint comparison",
      result);
  }

  /**
   * T19 coverage extension, E4. A trailing {@code /} on the callback path must not let it escape the
   * comparison as an empty final segment. Deleting the trailing-slash trim loop would leave {@code lastSlash}
   * pointing at the segment boundary right before the trailing slash, producing an empty {@code lastSegment}
   * that matches nothing in the closed set.
   */
  @Test
  public void aTrailingSlashOnTheCallbackPathIsStillRejected() throws Exception {
    TestableCognitoLoginAction action = this.eligibleAction();

    String result = invokeSameOriginOrNull(action, "https://marlo.example.org/cognitoCallback.do/");

    assertNull("a trailing '/' must not defeat the endpoint comparison", result);
  }

  /**
   * T19 coverage extension, E5. A same-origin candidate that {@link URI#URI(String)} cannot parse at all --
   * here, a raw, un-encoded space in the path, which the same-origin string comparison above does not itself
   * reject because it runs before any URI parsing -- must be rejected as unparseable, not merely swallowed
   * into an accept. Confirmed against a scratch harness: this exact input throws
   * {@code URISyntaxException: Illegal character in path}. If the catch block's {@code return true} were
   * changed to {@code return false}, an unparseable candidate would be treated as NOT an authentication
   * endpoint and would be accepted and stored verbatim -- the one input class this guard exists to fail
   * closed on, per the method's own javadoc ("null is the safe answer for anything unparseable").
   */
  @Test
  public void anUnparseableSameOriginCandidateIsRejectedNotAccepted() throws Exception {
    TestableCognitoLoginAction action = this.eligibleAction();

    String result = invokeSameOriginOrNull(action, "https://marlo.example.org/cognito Callback.do");

    assertNull("a URI the parser rejects outright must be treated as an authentication endpoint, per "
      + "isAuthenticationEndpoint's own javadoc contract", result);
  }

  /**
   * T19 coverage extension, E6. {@code AUTHENTICATION_ENDPOINT_PATHS} holds lowercase-ASCII entries and the
   * comparison lowercases the candidate with {@link Locale#ROOT} before matching. This test genuinely
   * exercises the locale rather than merely varying the candidate's case: {@code "COGNITOLOGIN.DO"} contains
   * two uppercase {@code I} characters (from {@code cognIto} and {@code logIn}), and under the Turkish
   * default locale {@code String#toLowerCase()} (no {@link Locale} argument) maps {@code 'I'} to the dotless
   * {@code 'ı'} rather than {@code 'i'} -- confirmed against a scratch harness:
   * {@code "COGNITOLOGIN.DO".toLowerCase()} under {@code new Locale("tr", "TR")} as the JVM default yields
   * {@code "cognıtologın.do"}, which does not equal the set's {@code "cognitologin.do"} entry.
   * <p>
   * The JVM's default locale is process-global state. It is changed here deliberately, and restored in a
   * {@code finally} block so no other test in this suite -- or in the same JVM run -- observes the change.
   * <p>
   * <b>What this proves and does not prove.</b> With the production code unchanged (it calls
   * {@code toLowerCase(Locale.ROOT)} explicitly), this candidate is rejected regardless of the JVM default
   * locale, and this test asserts exactly that. If a future change replaced {@code Locale.ROOT} with the
   * no-argument overload, this test would go red under a Turkish default locale and green under most others
   * -- which is why the mutation table names it "Locale.ROOT -> default locale" rather than "case-insensitive
   * comparison removed": a naive case-variant test using only the JVM's already-installed default locale
   * would not have caught that mutation, since most environments (including this one, absent this override)
   * default to a locale where 'I'.toLowerCase() already yields 'i'.
   */
  @Test
  public void aTurkishLocaleCaseVariantIsStillRejectedBecauseTheComparisonUsesLocaleRoot() throws Exception {
    Locale previousDefault = Locale.getDefault();
    Locale.setDefault(new Locale("tr", "TR"));
    try {
      TestableCognitoLoginAction action = this.eligibleAction();

      String result = invokeSameOriginOrNull(action, "https://marlo.example.org/COGNITOLOGIN.DO");

      assertNull("Locale.ROOT must reject this endpoint regardless of the JVM's default locale", result);
    } finally {
      Locale.setDefault(previousDefault);
    }
  }

  /**
   * T19 coverage extension, E7 -- <b>not optional, and not symmetry for its own sake.</b> This is the single
   * case that distinguishes this implementation from a {@code contains()} substring check: a substring match
   * would find the literal {@code cognitoCallback.do} inside this candidate and reject it, while the correct,
   * URI-normalizing implementation resolves the {@code ../} traversal PAST the callback segment down to
   * {@code /projects.do} and must ACCEPT it. Without this test the suite cannot tell the two implementations
   * apart. Confirmed against a scratch harness:
   * {@code new URI("https://marlo.example.org/cognitoCallback.do/../projects.do").normalize().getPath()}
   * yields {@code /projects.do}, whose last segment is not in the closed set.
   */
  @Test
  public void aTraversalThatResolvesPastTheCallbackToALegitimateDeepLinkIsAccepted() throws Exception {
    TestableCognitoLoginAction action = this.eligibleAction();
    String candidate = "https://marlo.example.org/cognitoCallback.do/../projects.do";

    String result = invokeSameOriginOrNull(action, candidate);

    assertEquals("a traversal that resolves to a legitimate path must survive, exactly like any other deep "
      + "link -- this is what tells this implementation apart from a plain contains() check", candidate, result);
  }

  /**
   * T19 coverage extension -- the {@code getBaseUrl()} double, varied rather than left fixed.
   * {@code TestableCognitoLoginAction.getBaseUrl()} normally returns one constant value, which hides a real
   * production risk recorded in execution.md 35.3: {@code APConfig.getBaseUrl()} returns {@code null} on an
   * unconfigured {@code BASE_URL}, and a caller with no configured base would silently discard every deep
   * link. This test varies the value THROUGH the double instead of relying on the fixed one.
   * <p>
   * <b>What this proves and does not prove.</b> It proves that {@code sameOriginOrNull} itself fails closed
   * -- rejects everything, including a same-origin-looking candidate -- when {@code getBaseUrl()} returns
   * {@code null}. It does <b>not</b> prove anything about whether {@code APConfig.getBaseUrl()} actually
   * returns {@code null} in a given deployment, nor about the http/https mismatch risk execution.md 35.3
   * names: that is real {@code APConfig} behaviour reading a Spring-injected property, and no unit test
   * against this hand-rolled double can exercise it. Asserting otherwise would be exactly the "certified green
   * by a double gentler than production" pattern this spec has hit repeatedly.
   */
  @Test
  public void aNullBaseUrlDiscardsEveryReturnUrlIncludingALegitimateDeepLink() throws Exception {
    TestableCognitoLoginAction action = this.eligibleAction();
    action.setStubBaseUrl(null);

    assertNull("a null base URL must fail closed on an otherwise-legitimate deep link",
      invokeSameOriginOrNull(action, "https://marlo.example.org/aiccra/projects.do?id=1"));
    assertNull("a null base URL must also fail closed on an off-site URL (belt and braces, not a new gap)",
      invokeSameOriginOrNull(action, "https://evil.example/anything.do"));
  }

  /**
   * T19 coverage extension -- the {@code getBaseUrl()} double, varied with a trailing slash. Same caveat as
   * {@link #aNullBaseUrlDiscardsEveryReturnUrlIncludingALegitimateDeepLink()}: this proves the trailing-slash
   * normalization branch inside {@code sameOriginOrNull} itself, nothing about {@code APConfig}'s real value.
   */
  @Test
  public void aBaseUrlWithATrailingSlashStillAcceptsADeepLinkAndRejectsOffSite() throws Exception {
    TestableCognitoLoginAction action = this.eligibleAction();
    action.setStubBaseUrl("https://marlo.example.org/");

    assertEquals("a legitimate deep link must still be accepted when the configured base carries a trailing "
      + "slash", "https://marlo.example.org/aiccra/projects.do?id=1",
      invokeSameOriginOrNull(action, "https://marlo.example.org/aiccra/projects.do?id=1"));
    assertNull("an off-site URL must still be rejected when the configured base carries a trailing slash",
      invokeSameOriginOrNull(action, "https://evil.example/anything.do"));
  }

  /**
   * T19 coverage extension -- the {@code getBaseUrl()} double, varied WITHOUT a trailing slash. This is the
   * form every other test in this class already depends on via {@link TestableCognitoLoginAction}'s default;
   * stated here explicitly, alongside its trailing-slash counterpart above, so the pairing named in the task
   * ("with and without a trailing slash") exists as two named tests rather than one implicit default.
   */
  @Test
  public void aBaseUrlWithoutATrailingSlashStillAcceptsADeepLinkAndRejectsOffSite() throws Exception {
    TestableCognitoLoginAction action = this.eligibleAction();
    action.setStubBaseUrl("https://marlo.example.org");

    assertEquals("a legitimate deep link must still be accepted when the configured base carries no trailing "
      + "slash", "https://marlo.example.org/aiccra/projects.do?id=1",
      invokeSameOriginOrNull(action, "https://marlo.example.org/aiccra/projects.do?id=1"));
    assertNull("an off-site URL must still be rejected when the configured base carries no trailing slash",
      invokeSameOriginOrNull(action, "https://evil.example/anything.do"));
  }

  /**
   * CHG-COGNITO-AUTH-001-T15, test 1. When {@code cognito.identity.provider} is configured, the authorize URL
   * must carry {@code identity_provider} so Cognito routes straight to the corporate IdP instead of its own
   * Hosted UI provider-selection screen (execution.md {@code 28.4}).
   */
  @Test
  public void identityProviderParameterIsIncludedWhenConfigured() throws Exception {
    this.customParameterManager.override = activeOverride("true");
    TestableCognitoLoginAction action = this.newAction(new ConfiguredApConfigWithProvider("CGIAR-AzureAD"));
    action.setGlobalUnitId(Long.valueOf(GLOBAL_UNIT_ID));
    action.setEmail(VALID_EMAIL);
    action.setAgree(Boolean.TRUE);

    String result = action.authorize(null);

    assertEquals(Action.SUCCESS, result);
    Map<String, String> params = queryParams(action.getAuthorizeUrl());
    assertEquals("CGIAR-AzureAD", params.get("identity_provider"));
  }

  /**
   * CHG-COGNITO-AUTH-001-T15, test 2. <b>Not evidence when asserted with {@code CGIAR-AzureAD}</b> -- that
   * string url-encodes to itself, so a test using it would pass whether or not {@code urlEncode(...)} is
   * actually called. The provider name here carries a space, which {@link java.net.URLEncoder} turns into
   * {@code +}; the assertion is made against the RAW, un-decoded URL for exactly that reason -- routing the
   * raw value through this suite's {@code queryParams} decode helper would silently undo a dropped encoding
   * step and let the test pass regardless (a decoded literal space is indistinguishable from a decoded
   * {@code +}-turned-back-into-space).
   */
  @Test
  public void identityProviderValueIsUrlEncoded() {
    this.customParameterManager.override = activeOverride("true");
    TestableCognitoLoginAction action = this.newAction(new ConfiguredApConfigWithProvider("CGIAR AD"));
    action.setGlobalUnitId(Long.valueOf(GLOBAL_UNIT_ID));
    action.setEmail(VALID_EMAIL);
    action.setAgree(Boolean.TRUE);

    String result = action.authorize(null);

    assertEquals(Action.SUCCESS, result);
    String url = action.getAuthorizeUrl();
    assertTrue("the space must be encoded (URLEncoder turns it into '+'): " + url,
      url.contains("&identity_provider=CGIAR+AD"));
    assertFalse("the raw, un-encoded provider name must never appear in the redirect: " + url,
      url.contains("&identity_provider=CGIAR AD"));
  }

  /**
   * CHG-COGNITO-AUTH-001-T15, test 3. An absent {@code cognito.identity.provider} (the state of every
   * environment today -- design.md 9.3's phase-0 default) must omit the parameter entirely, not append it
   * with an empty value. {@code identity_provider=} with no value is not the same as an absent parameter, and
   * Cognito may treat it as an unknown provider rather than falling back to its own selection screen.
   */
  @Test
  public void identityProviderParameterIsOmittedWhenPropertyIsEmpty() {
    TestableCognitoLoginAction action = this.eligibleAction();

    String result = action.authorize(null);

    assertEquals(Action.SUCCESS, result);
    String url = action.getAuthorizeUrl();
    assertFalse("no identity_provider parameter -- present or empty -- may appear when unconfigured: " + url,
      url.contains("identity_provider"));
  }

  /**
   * Advisory from the T15 audit. {@link ConfiguredApConfigWithProvider} returns its constructor argument
   * verbatim, bypassing {@code APConfig.cognitoSetting(...)} entirely -- so nothing proved that a
   * whitespace-only configured value (a plausible operator typo, e.g. {@code cognito.identity.provider=   })
   * trims to empty and is therefore treated as unset rather than appended as a blank-looking provider name.
   * <p>
   * This drives the field Spring's {@code @Value} would populate via reflection into a REAL {@link APConfig},
   * so the actual production trimming path in {@code cognitoSetting(...)} is what gets exercised, not a test
   * double standing in for it.
   */
  @Test
  public void aWhitespaceOnlyConfiguredValueTrimsToEmptyAndIsOmitted() throws Exception {
    ConfiguredApConfig config = new ConfiguredApConfig();
    Field identityProviderField = APConfig.class.getDeclaredField("COGNITO_IDENTITY_PROVIDER");
    identityProviderField.setAccessible(true);
    identityProviderField.set(config, "   ");

    assertEquals("cognitoSetting(...) must trim a whitespace-only value to empty, matching every other "
      + "Cognito getter's contract", "", config.getCognitoIdentityProvider());

    this.customParameterManager.override = activeOverride("true");
    TestableCognitoLoginAction action = this.newAction(config);
    action.setGlobalUnitId(Long.valueOf(GLOBAL_UNIT_ID));
    action.setEmail(VALID_EMAIL);
    action.setAgree(Boolean.TRUE);

    String result = action.authorize(null);

    assertEquals(Action.SUCCESS, result);
    assertFalse("a whitespace-only configured value must be treated as unset, not appended: "
      + action.getAuthorizeUrl(), action.getAuthorizeUrl().contains("identity_provider"));
  }

  /**
   * CHG-COGNITO-AUTH-001-T15, test 4. Appending {@code identity_provider} must not disturb any of the eight
   * parameters the live authorize redirect captured in execution.md {@code 28.4} already carries.
   * <p>
   * <b>This proves presence, exact value, and exact count -- not order.</b> {@code queryParams} parses into a
   * {@link HashMap}, so any permutation of the same nine parameters yields an identical map and an identical
   * pass here; a genuine drop is still caught by the {@code params.size() == 9} assertion below. That is a
   * deliberate scope, not an oversight: OAuth 2.0 authorization requests do not carry ordering semantics, and
   * pinning one here would assert a constraint the protocol does not impose (audit finding 2, corrected after
   * the T15 review pass).
   */
  @Test
  public void everyOtherAuthorizeParameterIsUnchangedByIdentityProvider() throws Exception {
    this.customParameterManager.override = activeOverride("true");
    TestableCognitoLoginAction action = this.newAction(new ConfiguredApConfigWithProvider("CGIAR-AzureAD"));
    action.setGlobalUnitId(Long.valueOf(GLOBAL_UNIT_ID));
    action.setEmail(VALID_EMAIL);
    action.setAgree(Boolean.TRUE);

    String result = action.authorize("https://marlo.example.org/testcrp/projectList.do");

    assertEquals(Action.SUCCESS, result);
    String url = action.getAuthorizeUrl();
    assertTrue("must still redirect to the configured Cognito domain's authorize endpoint: " + url,
      url.startsWith("https://test-pool.auth.us-east-1.amazoncognito.com/oauth2/authorize?"));

    Map<String, String> params = queryParams(url);
    assertEquals("code", params.get("response_type"));
    assertEquals("test-client-id", params.get("client_id"));
    assertEquals("https://marlo.example.org/cognitoCallback.do", params.get("redirect_uri"));
    assertEquals("openid email", params.get("scope"));
    assertTrue(params.containsKey("state"));
    assertTrue(params.containsKey("nonce"));
    assertTrue(params.containsKey("code_challenge"));
    assertEquals("S256", params.get("code_challenge_method"));
    assertEquals("CGIAR-AzureAD", params.get("identity_provider"));
    assertEquals("exactly nine parameters must be present -- the eight baseline plus identity_provider", 9,
      params.size());
  }

  /**
   * Not one of T08's five named tests, but directly required by the launcher's own instructions: an
   * unconfigured environment (every {@link APConfig} Cognito getter returns {@code ""}, never {@code null} --
   * design.md 9.3) must fail closed rather than build a broken redirect.
   */
  @Test
  public void anUnconfiguredEnvironmentFailsClosedInsteadOfBuildingABrokenRedirect() {
    this.customParameterManager.override = activeOverride("true");
    TestableCognitoLoginAction action = this.newAction(new APConfig());
    action.setGlobalUnitId(Long.valueOf(GLOBAL_UNIT_ID));
    action.setEmail(VALID_EMAIL);
    action.setAgree(Boolean.TRUE);

    String result = action.authorize(null);

    assertEquals(Action.INPUT, result);
    assertNull(action.getAuthorizeUrl());
    assertFalse("no state may be minted on a configuration failure", this.userManager.saveUserCalled);
  }

  /** Always resolves to the one configured Global Unit; explodes on any other id (test-authoring error). */
  private static final class NoOpGlobalUnitManager implements GlobalUnitManager {

    private GlobalUnit unit;

    @Override
    public List<GlobalUnit> crpUsers(String email) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public void deleteGlobalUnit(long globalUnitId) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public boolean existGlobalUnit(long globalUnitID) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public List<GlobalUnit> findAll() {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public GlobalUnit findGlobalUnitByAcronym(String acronym) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public GlobalUnit findGlobalUnitBySMOCode(String smoCode) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public GlobalUnit getGlobalUnitById(long globalUnitID) {
      if (this.unit != null && this.unit.getId().longValue() == globalUnitID) {
        return this.unit;
      }
      return null;
    }

    @Override
    public GlobalUnit saveGlobalUnit(GlobalUnit globalUnit) {
      throw new UnsupportedOperationException("not needed by this suite");
    }
  }

  /** Records every call to {@code saveUser}, and whether the redirect had already been built at that point. */
  private static final class RecordingUserManager implements UserManager {

    private final Map<String, User> byEmail = new HashMap<String, User>();
    private CognitoLoginAction action;
    private boolean saveUserCalled;
    private boolean authorizeUrlWasNullAtSaveTime;
    private Boolean savedAgreeTerms;

    void register(User user) {
      this.byEmail.put(user.getEmail(), user);
    }

    @Override
    public User getActiveSuperAdminUserByUsernameOccurrence() {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public List<String> getCenterPermission(int userId, String crp) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public List<String> getPermission(int userId, String crp) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public User getUser(Long userId) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public User getUserByEmail(String email) {
      return this.byEmail.get(email);
    }

    @Override
    public User getUserByUsername(String username) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public User login(String email, String password) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public boolean saveLastLogin(User user) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public User saveUser(User user) {
      this.saveUserCalled = true;
      this.authorizeUrlWasNullAtSaveTime = this.action == null || this.action.getAuthorizeUrl() == null;
      this.savedAgreeTerms = user.getAgreeTerms();
      return user;
    }

    @Override
    public List<User> searchUser(String searchValue) {
      throw new UnsupportedOperationException("not needed by this suite");
    }
  }

  /** Resolves only the {@code cognito_auth_active} key; anything else is a test-authoring error. */
  private static final class StubCustomParameterManager implements CustomParameterManager {

    private CustomParameter override;

    @Override
    public void deleteCustomParameter(long customParameterId) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public boolean existCustomParameter(long customParameterID) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public List<CustomParameter> findAll() {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public List<CustomParameter> getAllCustomParametersByGlobalUnitId(long globalUnitId) {
      return new ArrayList<CustomParameter>();
    }

    @Override
    public CustomParameter getCustomParameterById(long customParameterID) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public CustomParameter getCustomParameterByParameterKeyAndGlobalUnitId(String paramaterKey, long globalUnitId) {
      if (!APConstants.COGNITO_AUTH_ACTIVE.equals(paramaterKey)) {
        throw new IllegalStateException("test authoring error: unexpected key " + paramaterKey);
      }
      return this.override;
    }

    @Override
    public CustomParameter saveCustomParameter(CustomParameter customParameter) {
      throw new UnsupportedOperationException("not needed by this suite");
    }
  }

  /** Resolves only the {@code cognito_auth_active} key; anything else is a test-authoring error. */
  private static final class StubParameterManager implements ParameterManager {

    private Parameter catalogRow;

    @Override
    public void deleteParameter(long parameterId) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public boolean existParameter(long parameterID) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public List<Parameter> findAll() {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public Parameter getParameterById(long parameterID) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public Parameter getParameterByKey(String key, long globalUnitId) {
      if (!APConstants.COGNITO_AUTH_ACTIVE.equals(key)) {
        throw new IllegalStateException("test authoring error: unexpected key " + key);
      }
      return this.catalogRow;
    }

    @Override
    public Parameter saveParameter(Parameter parameter) {
      throw new UnsupportedOperationException("not needed by this suite");
    }
  }

  /** Overrides only {@code getText}, which otherwise needs a live Struts container. */
  private static final class TestableCognitoLoginAction extends CognitoLoginAction {

    private static final long serialVersionUID = 1L;

    /**
     * CHG-COGNITO-AUTH-001-T19 coverage extension. Mutable so a test can vary the base URL THROUGH this
     * double -- a null value, a trailing slash, no trailing slash -- instead of relying on the one fixed
     * value every other test in this class depends on. Defaults to that same fixed value, so nothing above
     * this field's introduction changes behaviour.
     */
    private String baseUrl = "https://marlo.example.org";

    TestableCognitoLoginAction(APConfig config, UserManager userManager, GlobalUnitManager crpManager,
      CustomParameterManager customParameterManager, ParameterManager parameterManager) {
      super(config, userManager, crpManager, customParameterManager, parameterManager);
    }

    /**
     * The origin the open-redirect guard compares against. Production reads it from APConfig; the stub
     * config here does not carry one, and without this override every return URL -- including a legitimate
     * same-origin deep link -- is discarded, which would make the guard look correct while being useless.
     */
    @Override
    public String getBaseUrl() {
      return this.baseUrl;
    }

    @Override
    public String getText(String aTextName) {
      return aTextName;
    }

    /** CHG-COGNITO-AUTH-001-T19 coverage extension: overrides the default base URL for one test. */
    void setStubBaseUrl(String baseUrl) {
      this.baseUrl = baseUrl;
    }
  }

  /** A fully-configured Cognito environment -- the opposite of design.md 9.3's phase-0 default. */
  private static class ConfiguredApConfig extends APConfig {

    @Override
    public String getCognitoCallbackUrl() {
      return "https://marlo.example.org/cognitoCallback.do";
    }

    @Override
    public String getCognitoClientId() {
      return "test-client-id";
    }

    @Override
    public String getCognitoDomain() {
      return "test-pool.auth.us-east-1.amazoncognito.com";
    }
  }

  /**
   * {@link ConfiguredApConfig} with {@code cognito.identity.provider} also set -- CHG-COGNITO-AUTH-001-T15.
   */
  private static final class ConfiguredApConfigWithProvider extends ConfiguredApConfig {

    private final String identityProvider;

    ConfiguredApConfigWithProvider(String identityProvider) {
      this.identityProvider = identityProvider;
    }

    @Override
    public String getCognitoIdentityProvider() {
      return this.identityProvider;
    }
  }
}
