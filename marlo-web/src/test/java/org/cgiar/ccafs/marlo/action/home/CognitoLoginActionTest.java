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
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
      return "https://marlo.example.org";
    }

    @Override
    public String getText(String aTextName) {
      return aTextName;
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
