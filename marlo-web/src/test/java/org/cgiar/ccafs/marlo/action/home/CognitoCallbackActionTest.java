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

import org.cgiar.ccafs.marlo.action.home.CognitoCallbackAction.ExchangeResult;
import org.cgiar.ccafs.marlo.action.home.CognitoCallbackAction.TokenExchangeClient;
import org.cgiar.ccafs.marlo.action.home.CognitoLoginAction.PendingAuthorization;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpUserManager;
import org.cgiar.ccafs.marlo.data.manager.CustomParameterManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.ParameterManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CrpUser;
import org.cgiar.ccafs.marlo.data.model.CustomParameter;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Parameter;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitType;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.APCustomRealm;
import org.cgiar.ccafs.marlo.security.CognitoAuthenticationToken;
import org.cgiar.ccafs.marlo.security.CognitoIdentityMapper;
import org.cgiar.ccafs.marlo.security.CognitoTokenValidator;
import org.cgiar.ccafs.marlo.security.authentication.Authenticator;
import org.cgiar.ccafs.marlo.security.impl.CognitoIdentityMapperImpl;
import org.cgiar.ccafs.marlo.security.impl.CognitoTokenValidatorImpl;
import org.cgiar.ccafs.marlo.security.impl.CognitoTokenValidatorImpl.JwksSource;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.util.ThreadContext;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.struts2.ServletActionContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Covers CHG-COGNITO-AUTH-001-T09: {@code CognitoCallbackAction}, the eight-step ordering of design.md
 * 13.3, and the {@code users.agree_terms} write it ALSO OWNS per {@code tasks.md}.
 * <p>
 * <b>"Integration" in this suite means action-level with doubles, matching what T08's suite means by the
 * same word</b> -- not container-level coverage. There is no Struts/Tomcat/Hibernate test harness in this
 * repository, so every collaborator below the action is either the real production class ({@link
 * CognitoTokenValidatorImpl}, {@link CognitoIdentityMapperImpl}, the real {@link APCustomRealm} through a
 * real {@code Subject.login(...)}) or a hand-rolled double (MARLO has no mocking framework; {@code DEC-005}
 * is {@code PENDING}), matching {@code CognitoLoginActionTest} and {@code APCustomRealmDispatchTest}.
 * <p>
 * <b>The <i>Not evidence when</i> clause -- run for real, not mocked.</b> Every test below drives the real
 * {@link CognitoTokenValidatorImpl} against a real RSA-signed ID token (nimbus, generated in-process,
 * exactly {@code CognitoTokenValidatorTest}'s fixture pattern) and the real {@link CognitoIdentityMapperImpl}
 * against a hand-rolled {@link UserManager}. SEC-001 (token validation) and SEC-003 (session rotation) are
 * therefore exercised <b>together</b>, in the same call, through the same action -- not verified in
 * isolation the way a validator-only or realm-only suite would.
 * <p>
 * <b>Mutation proof (this task's own <i>Fails when</i> clause), run manually and recorded in the
 * implementer's report, not encoded as a standing {@code @Test} that would fail on purpose:</b> moving
 * {@code subject.getSession().stop()} to the first line of {@code callback(...)} must redden the
 * state-consumption tests below, because the pre-auth session -- and the pending authorization inside it --
 * is destroyed before step 1 ever reads it.
 */
public class CognitoCallbackActionTest {

  private static final String ISSUER = "https://cognito-idp.us-east-1.amazonaws.com/us-east-1_TESTPOOL";
  private static final String AUDIENCE = "test-client-id";
  private static final String CGIAR_EMAIL = "priya.cgiar@cgiar.org";
  private static final long GLOBAL_UNIT_ID = 55L;

  private RSAKey signingKey;
  private JWKSet jwks;
  private CognitoTokenValidatorImpl realValidator;
  private RecordingUserManager userManager;
  private CognitoIdentityMapperImpl realIdentityMapper;
  private MembershipControllableCrpUserManager crpUserManager;
  private FixedGlobalUnitManager crpManager;
  private RecordingTokenExchangeClient exchangeClient;

  private static GlobalUnit globalUnit(long id, int typeId) {
    GlobalUnitType type = new GlobalUnitType();
    type.setId(Long.valueOf(typeId));
    GlobalUnit globalUnit = new GlobalUnit();
    globalUnit.setId(Long.valueOf(id));
    globalUnit.setAcronym("TESTCRP" + id);
    globalUnit.setGlobalUnitType(type);
    return globalUnit;
  }

  private static User cgiarUser(long id) {
    User user = new User();
    user.setId(Long.valueOf(id));
    user.setEmail(CGIAR_EMAIL);
    user.setCgiarUser(true);
    user.setActive(true);
    return user;
  }

  private static RSAKey generateRsaKey(String kid) throws JOSEException {
    return new RSAKeyGenerator(2048).keyID(kid).generate();
  }

  @Before
  public void setUp() throws Exception {
    this.signingKey = generateRsaKey("kid-t09");
    this.jwks = new JWKSet(Collections.singletonList(this.signingKey.toPublicJWK()));
    JwksSource jwksSource = () -> this.jwks;
    this.realValidator = new CognitoTokenValidatorImpl(ISSUER, AUDIENCE, jwksSource);
    this.userManager = new RecordingUserManager();
    this.realIdentityMapper = new CognitoIdentityMapperImpl(this.userManager);
    this.crpUserManager = new MembershipControllableCrpUserManager();
    this.crpManager = new FixedGlobalUnitManager();
    this.crpManager.register(globalUnit(GLOBAL_UNIT_ID, 1));
    this.exchangeClient = new RecordingTokenExchangeClient();

    SecurityUtils.setSecurityManager(new DefaultSecurityManager(this.realmWithNoIo()));
  }

  @After
  public void tearDown() {
    SecurityUtils.setSecurityManager(null);
    ThreadContext.remove();
    // CHG-COGNITO-AUTH-001-T16: only the new post-action-access test below binds an ActionContext /
    // ServletActionContext request; clearing unconditionally here is a no-op for every other test and
    // prevents that ThreadLocal binding from leaking into whichever test method runs next.
    ActionContext.clear();
  }

  /** Same shape as {@code APCustomRealmDispatchTest}: the Cognito dispatch performs no I/O. */
  private APCustomRealm realmWithNoIo() {
    return new APCustomRealm(new ExplodingAuthenticator(), new ExplodingAuthenticator(), new ExplodingUserManager(),
      new APConfig());
  }

  private TestableCognitoCallbackAction newAction() {
    TestableCognitoCallbackAction action = new TestableCognitoCallbackAction(new APConfig(), this.userManager,
      this.crpManager, this.crpUserManager, new NoCustomParametersManager(), new NoOpParameterManager(),
      this.realValidator, this.realIdentityMapper, this.exchangeClient);
    action.setSession(new HashMap<String, Object>());
    return action;
  }

  private String validIdToken(String nonce, String email) throws JOSEException {
    Instant now = Instant.now();
    JWTClaimsSet claims = new JWTClaimsSet.Builder().issuer(ISSUER).audience(AUDIENCE).subject("sub-priya")
      .claim("email", email).claim("cognito:username", "priyac").claim("token_use", "id").claim("nonce", nonce)
      .issueTime(Date.from(now.minusSeconds(5))).expirationTime(Date.from(now.plusSeconds(3600))).build();
    SignedJWT jwt = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(this.signingKey.getKeyID()).build(),
      claims);
    jwt.sign(new RSASSASigner(this.signingKey));
    return jwt.serialize();
  }

  private PendingAuthorization seedPending(TestableCognitoCallbackAction action, String state, long globalUnitId,
    String returnUrl, String nonce) {
    PendingAuthorization pending =
      new PendingAuthorization(state, Long.valueOf(globalUnitId), returnUrl, nonce, "verifier-abc");
    SecurityUtils.getSubject().getSession().setAttribute(APConstants.COGNITO_PENDING_AUTHORIZATION, pending);
    return pending;
  }

  /**
   * T09 test 1 (the ALSO OWNS clause). {@code saveUser} is wired to explode -- see
   * {@link RecordingUserManager#saveUser(User)} -- so reaching a passing assertion here is itself proof the
   * write went through {@code saveLastLogin}, never {@code saveUser}.
   * <p>
   * <b>What this test does NOT prove, stated plainly per the launcher's instruction.</b> This test tree has
   * no schema-backed Hibernate harness (no mocking framework, no {@code SessionFactory} in any existing
   * test -- the same gap {@code CognitoIdentityMappingTest} recorded for T07's username write, which goes
   * through the identical method). A call-recording double proves a call was made with the right argument;
   * it cannot prove a database row changed. That proof is left as an open gap for verification against a
   * real schema, the way T02's migration was verified -- not discharged here.
   */
  @Test
  public void agreeTermsIsPersistedThroughSaveLastLoginNotSaveUser() throws Exception {
    this.userManager.register(cgiarUser(9001L));
    TestableCognitoCallbackAction action = this.newAction();
    PendingAuthorization pending = this.seedPending(action, "state-1", GLOBAL_UNIT_ID, null, "nonce-1");
    this.crpUserManager.isMember = true;
    this.exchangeClient.idTokenToReturn = this.validIdToken(pending.getNonce(), CGIAR_EMAIL);

    String result = action.callback("auth-code-1", "state-1", null);

    assertEquals(Action.SUCCESS, result);
    assertTrue("saveLastLogin must have been called", this.userManager.saveLastLoginCallCount > 0);
    assertEquals("agree_terms must be TRUE on the persisted row", Boolean.TRUE,
      this.userManager.lastSavedUser.getAgreeTerms());
    assertEquals(Long.valueOf(9001L), this.userManager.lastSavedUser.getId());
  }

  /**
   * <b>Regression test for the audit's Issue 1 (CRITICAL).</b> In production, {@code BaseAction.getSession()}
   * is Struts' own {@code SessionMap}, whose constructor captures {@code request.getSession(false)} exactly
   * once -- so the map installed before {@code execute()} ran still points at the session step 6 stops. Every
   * write through it afterward ({@code finishLogin}'s {@code getSession().put(...)}) throws {@code
   * IllegalStateException} via {@code ShiroHttpSession}, and every successful Cognito login would 500.
   * <p>
   * <b>The first submission's ten tests could not catch this</b> because every one of them seeded
   * {@code action.setSession(new HashMap<>())} -- a plain map with no concept of "stopped", gentler than
   * production. This test seeds {@link InvalidatedSessionMap} instead: it throws {@code
   * IllegalStateException} from every accessor, standing in for the already-invalidated pre-auth session.
   * <p>
   * <b>Proven to bite, not merely written.</b> With {@link CognitoCallbackAction#freshSessionMap()}'s
   * result no longer installed before {@code finishLogin} is called, this test reddens with exactly the
   * {@code IllegalStateException} production would throw -- see the implementer's report for that run,
   * captured before the fix was restored.
   */
  @Test
  public void theOldSessionIsNeverReusedAfterLoginSucceeds() throws Exception {
    this.userManager.register(cgiarUser(9001L));
    TestableCognitoCallbackAction action = this.newAction();
    action.setSession(new InvalidatedSessionMap());
    PendingAuthorization pending = this.seedPending(action, "state-1b", GLOBAL_UNIT_ID, null, "nonce-1b");
    this.crpUserManager.isMember = true;
    this.exchangeClient.idTokenToReturn = this.validIdToken(pending.getNonce(), CGIAR_EMAIL);

    String result;
    try {
      result = action.callback("auth-code-1b", "state-1b", null);
    } catch (IllegalStateException e) {
      fail("finishLogin must never write through the session step 6 already stopped: " + e);
      return;
    }

    assertEquals(Action.SUCCESS, result);
    // The membership-failure path shares the same defect shape (LoginAction's getSession().clear()) --
    // covered too, so a fix that only patches the success branch does not pass this suite.
  }

  /**
   * Same defect, the membership-failure branch. {@code finishLogin}'s non-member path calls {@code
   * getSession().clear()} -- if the fix only re-points the session before the success branch, this reddens
   * exactly the way the success-path test above does.
   */
  @Test
  public void theOldSessionIsNeverReusedOnTheMembershipFailureBranchEither() throws Exception {
    this.userManager.register(cgiarUser(9001L));
    TestableCognitoCallbackAction action = this.newAction();
    action.setSession(new InvalidatedSessionMap());
    PendingAuthorization pending = this.seedPending(action, "state-1c", GLOBAL_UNIT_ID, null, "nonce-1c");
    this.crpUserManager.isMember = false;
    this.exchangeClient.idTokenToReturn = this.validIdToken(pending.getNonce(), CGIAR_EMAIL);

    String result;
    try {
      result = action.callback("auth-code-1c", "state-1c", null);
    } catch (IllegalStateException e) {
      fail("finishLogin's membership-failure branch must never write through the stopped session either: " + e);
      return;
    }

    assertEquals(Action.INPUT, result);
  }

  /**
   * CHG-COGNITO-AUTH-001-T16 test 4 (post-action access) -- V-2, reproduced against the live Cognito pool
   * ({@code execution.md} 28) and fixed by {@link org.cgiar.ccafs.marlo.security.ShiroRequestSessionCacheResetter}.
   * <p>
   * {@code InvalidatedSessionMap} above proves {@code SessionMap} is re-pointed correctly (T09's fix); it says
   * nothing about the SEPARATE cache {@link ShiroHttpServletRequest} keeps on the request object itself, which
   * is exactly the mechanism {@code SessionMap}'s own constructor reads from (T16's <i>Not evidence when</i>
   * clause). This test captures a REAL {@link ShiroHttpServletRequest} -- bound through
   * {@link ServletActionContext} before {@code callback(...)} runs, precisely how Struts' CSP interceptor
   * captures its own request reference into a {@code PreResultListener} lambda before the action executes --
   * runs the full callback, and then reads through that SAME captured reference afterwards, matching the real
   * failure path {@code DefaultCspSettings.addCspHeadersWithSession} takes.
   */
  @Test
  public void postActionAccessThroughTheRealShiroWrappedRequestDoesNotThrow() throws Exception {
    this.userManager.register(cgiarUser(9001L));
    TestableCognitoCallbackAction action = this.newAction();

    ShiroHttpServletRequest shiroRequest = new ShiroHttpServletRequest(newFakeHttpServletRequest(), null, false);
    ActionContext.of(new HashMap<String, Object>()).bind();
    ServletActionContext.setRequest(shiroRequest);
    // Forces the request to cache a wrapper of the PRE-AUTH session -- exactly what Struts' own SessionMap
    // construction does before execute() runs (T09's javadoc), and the state this callback's step 1 already
    // depends on (the pending authorization lives in that same pre-auth session).
    assertNotNull("forcing the cache must succeed", shiroRequest.getSession(true));

    PendingAuthorization pending = this.seedPending(action, "state-t16", GLOBAL_UNIT_ID, null, "nonce-t16");
    this.crpUserManager.isMember = true;
    this.exchangeClient.idTokenToReturn = this.validIdToken(pending.getNonce(), CGIAR_EMAIL);

    String result = action.callback("auth-code-t16", "state-t16", null);

    assertEquals(Action.SUCCESS, result);

    // The real CSP path, through the SAME request reference captured before the action ran: getSession()
    // (create=true, matching addCspHeadersWithSession's own call) then setAttribute -- must not throw.
    HttpSession sessionAfter = shiroRequest.getSession();
    assertNotNull(sessionAfter);
    try {
      sessionAfter.setAttribute("cspProbe", "value");
    } catch (IllegalStateException e) {
      fail("a post-action access through the captured request must not hit the stale, stopped session: " + e);
    }
    assertEquals("value", sessionAfter.getAttribute("cspProbe"));
  }

  /**
   * Review finding F3. The test above only covers the success branch. Two post-{@code stop()} paths had no
   * real-{@link ShiroHttpServletRequest} coverage; this closes the gate-4 (membership) one.
   * <p>
   * {@code finishLogin}'s non-member branch ({@code LoginAction:383-391}) calls {@code this.getSession().clear()}
   * then {@code SecurityUtils.getSubject().logout()} -- <b>after</b> this task's helper already ran and
   * {@code freshSessionMap()} already re-pointed the request's cache at the correct, freshly-authenticated
   * session. {@code Subject.logout()} stops that session in turn, so the request is left holding a cache of a
   * session that is <i>now</i> gone too -- a second rotation this task's fix was never asked to cover. The
   * auditor deliberately did not assert a 500 here on an incomplete model (MARLO's own {@code logout()} action
   * does the identical {@code clear()} + {@code logout()} sequence and is not known to 500), so this closes
   * the evidence gap rather than assuming the residual is real.
   */
  @Test
  public void postActionAccessOnTheNonMemberBranchDoesNotThrow() throws Exception {
    this.userManager.register(cgiarUser(9002L));
    TestableCognitoCallbackAction action = this.newAction();

    ShiroHttpServletRequest shiroRequest = new ShiroHttpServletRequest(newFakeHttpServletRequest(), null, false);
    ActionContext.of(new HashMap<String, Object>()).bind();
    ServletActionContext.setRequest(shiroRequest);
    assertNotNull("forcing the cache must succeed", shiroRequest.getSession(true));

    PendingAuthorization pending = this.seedPending(action, "state-t16-f3", GLOBAL_UNIT_ID, null, "nonce-t16-f3");
    this.crpUserManager.isMember = false;
    this.exchangeClient.idTokenToReturn = this.validIdToken(pending.getNonce(), CGIAR_EMAIL);

    String result = action.callback("auth-code-t16-f3", "state-t16-f3", null);

    assertEquals("the non-member branch must still be reached (gate 4)", Action.INPUT, result);

    // The real CSP path, through the SAME request reference captured before the action ran, AFTER
    // finishLogin's own getSession().clear() + Subject.logout() ran on the non-member branch.
    HttpSession sessionAfter = shiroRequest.getSession();
    assertNotNull(sessionAfter);
    try {
      sessionAfter.setAttribute("cspProbe", "value");
    } catch (IllegalStateException e) {
      fail("F3: post-action access on the non-member branch threw -- this is a NEW defect, report it, do "
        + "not fix the production path: " + e);
    }
    assertEquals("value", sessionAfter.getAttribute("cspProbe"));
  }

  /**
   * A minimal, real {@link HttpServletRequest} built as a JDK dynamic proxy over an attribute map -- the same
   * technique {@code LoginActionFinishLoginTest#setReferer} already uses in this repository (MARLO has no
   * mocking framework; DEC-005 is PENDING). The attribute store must be real, not stubbed: {@code
   * ShiroHttpServletRequest.getSession(true)} calls {@code this.setAttribute(REFERENCED_SESSION_IS_NEW, TRUE)}
   * on a freshly-created session (bytecode confirmed -- see
   * {@code ShiroRequestSessionCacheResetter}'s class javadoc), which {@code HttpServletRequestWrapper}
   * delegates straight through to the wrapped request.
   */
  private static HttpServletRequest newFakeHttpServletRequest() {
    final Map<String, Object> attributes = new HashMap<String, Object>();
    InvocationHandler handler = new InvocationHandler() {

      @Override
      public Object invoke(Object proxy, Method method, Object[] args) {
        String name = method.getName();
        if ("setAttribute".equals(name)) {
          attributes.put((String) args[0], args[1]);
          return null;
        }
        if ("getAttribute".equals(name)) {
          return attributes.get(args[0]);
        }
        if ("removeAttribute".equals(name)) {
          attributes.remove(args[0]);
          return null;
        }
        if ("equals".equals(name)) {
          return Boolean.valueOf(proxy == args[0]);
        }
        if ("hashCode".equals(name)) {
          return Integer.valueOf(System.identityHashCode(proxy));
        }
        if ("toString".equals(name)) {
          return "FakeHttpServletRequest";
        }
        Class<?> returnType = method.getReturnType();
        if (returnType == boolean.class) {
          return Boolean.FALSE;
        }
        if (returnType == int.class) {
          return Integer.valueOf(0);
        }
        if (returnType == long.class) {
          return Long.valueOf(0L);
        }
        return null;
      }
    };
    return (HttpServletRequest) Proxy.newProxyInstance(CognitoCallbackActionTest.class.getClassLoader(),
      new Class<?>[] {HttpServletRequest.class}, handler);
  }

  /**
   * T09 test 2. {@code state} is minted at {@code cognitoLogin.do} and read back here -- the resolved
   * Global Unit must be the one bound at that time, never anything the callback URL itself could name
   * (FN-003). Runs the real validator and the real identity mapper together, closing this task's own
   * <i>Not evidence when</i> clause.
   */
  @Test
  public void validRoundTripScopesTheSessionToTheGlobalUnitBoundAtMintTime() throws Exception {
    this.userManager.register(cgiarUser(9001L));
    TestableCognitoCallbackAction action = this.newAction();
    PendingAuthorization pending = this.seedPending(action, "state-2", GLOBAL_UNIT_ID, null, "nonce-2");
    this.crpUserManager.isMember = true;
    this.exchangeClient.idTokenToReturn = this.validIdToken(pending.getNonce(), CGIAR_EMAIL);

    String result = action.callback("auth-code-2", "state-2", null);

    assertEquals(Action.SUCCESS, result);
    GlobalUnit sessionCrp = (GlobalUnit) action.getSession().get(APConstants.SESSION_CRP);
    assertEquals(Long.valueOf(GLOBAL_UNIT_ID), sessionCrp.getId());
  }

  /**
   * T09 test 3 (FN-003), <b>rewritten after the audit's Issue 2</b>.
   * <p>
   * The original version asserted {@code CognitoCallbackAction} has no {@code setGlobalUnitId} -- true, but
   * vacuous: no class in this hierarchy was ever going to have that name, since it belongs only to the
   * sibling {@code CognitoLoginAction}. It proved nothing about a regression in {@code this} class and
   * would have passed for almost any class in the repository.
   * <p>
   * What actually protects FN-003 is that {@code callback(...)} never reads the Struts-bindable fields this
   * class inherits from {@link LoginAction} -- {@code setGlobalUnit(Long)}, {@code setCrp(String)} -- which
   * {@code defaultStack}'s params interceptor <b>will</b> populate from a crafted callback query string in
   * production. This test binds hostile values into both before calling {@code callback(...)} and asserts
   * the resolved Global Unit is still the one bound server-side at {@code cognitoLogin.do}, never {@code
   * 999} or {@code "EVIL"} -- a regression that silently started reading {@code this.globalUnit} instead of
   * {@code pending.getGlobalUnitId()} would redden this, where the structural version could not.
   */
  @Test
  public void tamperedInheritedGlobalUnitAndCrpFieldsAreIgnored() throws Exception {
    this.userManager.register(cgiarUser(9001L));
    TestableCognitoCallbackAction action = this.newAction();
    action.setGlobalUnit(Long.valueOf(999L));
    action.setCrp("EVIL");
    PendingAuthorization pending = this.seedPending(action, "state-3", GLOBAL_UNIT_ID, null, "nonce-3");
    this.crpUserManager.isMember = true;
    this.exchangeClient.idTokenToReturn = this.validIdToken(pending.getNonce(), CGIAR_EMAIL);

    String result = action.callback("auth-code-3", "state-3", null);

    assertEquals(Action.SUCCESS, result);
    GlobalUnit sessionCrp = (GlobalUnit) action.getSession().get(APConstants.SESSION_CRP);
    assertEquals("the session-bound Global Unit, not the tampered inherited fields, must win",
      Long.valueOf(GLOBAL_UNIT_ID), sessionCrp.getId());
  }

  /**
   * T09 test 4 (SEC-003 / D-8). The pre-auth session is stopped and a new one created between steps 5 and
   * 6 -- the session id must differ before and after a successful callback.
   */
  @Test
  public void sessionIdDiffersBeforeAndAfterASuccessfulCallback() throws Exception {
    this.userManager.register(cgiarUser(9001L));
    TestableCognitoCallbackAction action = this.newAction();
    PendingAuthorization pending = this.seedPending(action, "state-4", GLOBAL_UNIT_ID, null, "nonce-4");
    this.crpUserManager.isMember = true;
    this.exchangeClient.idTokenToReturn = this.validIdToken(pending.getNonce(), CGIAR_EMAIL);
    Serializable sessionIdBefore = SecurityUtils.getSubject().getSession().getId();

    String result = action.callback("auth-code-4", "state-4", null);

    assertEquals(Action.SUCCESS, result);
    Serializable sessionIdAfter = SecurityUtils.getSubject().getSession().getId();
    assertNotEquals("the session id must be rotated on a successful Cognito login", sessionIdBefore, sessionIdAfter);
  }

  /**
   * T09 test 5. Replaying a consumed {@code state} must refuse on the missing entry, not merely fail some
   * later check -- proving step 1's read-and-delete is atomic and unconditional.
   */
  @Test
  public void replayingAConsumedStateIsRefusedOnTheMissingEntry() throws Exception {
    this.userManager.register(cgiarUser(9001L));
    TestableCognitoCallbackAction action = this.newAction();
    PendingAuthorization pending = this.seedPending(action, "state-5", GLOBAL_UNIT_ID, null, "nonce-5");
    this.crpUserManager.isMember = true;
    this.exchangeClient.idTokenToReturn = this.validIdToken(pending.getNonce(), CGIAR_EMAIL);

    String first = action.callback("auth-code-5", "state-5", null);
    assertEquals(Action.SUCCESS, first);

    // Second attempt: same state, no pending authorization left to consume (the successful call above
    // already removed it and rotated the session). A fresh action instance, but the assertion is about the
    // session-bound state, not the action.
    TestableCognitoCallbackAction replay = this.newAction();
    String second = replay.callback("auth-code-5", "state-5", null);

    // CHG-COGNITO-AUTH-001-T20 (V-5), branch :420 ("no pending authorization"). This is also the exact V-4
    // shape (tasks.md T20): a replayed callback must stay refused AND now redirect, never render
    // cognitoCallback.do in place with the stale code/state still on the URL.
    assertEquals(Action.LOGIN, second);
    assertTrue("a replayed callback must still redirect to the canonical login URL",
      replay.getUrl().endsWith("/login.do"));
  }

  /**
   * T09 test 6 (C-3's failure path). Not a member of the selected Global Unit's {@code crp_users} -> the
   * inherited tail's existing membership branch fires: {@code login.error.invalidUserCrp}, the session is
   * cleared, and -- because DD-6 populated the inherited {@code user} field with a detached, email-only
   * {@link User} -- neither {@code user.getEmail()} (the success/failure log lines) nor
   * {@code user.setPassword(null)} (called twice on this branch) throws.
   */
  @Test
  public void userNotInCrpUsersIsRefusedWithInvalidUserCrpAndClearsTheSessionWithNoNpe() throws Exception {
    this.userManager.register(cgiarUser(9001L));
    TestableCognitoCallbackAction action = this.newAction();
    PendingAuthorization pending = this.seedPending(action, "state-6", GLOBAL_UNIT_ID, null, "nonce-6");
    this.crpUserManager.isMember = false;
    this.exchangeClient.idTokenToReturn = this.validIdToken(pending.getNonce(), CGIAR_EMAIL);

    String result;
    try {
      result = action.callback("auth-code-6", "state-6", null);
    } catch (NullPointerException e) {
      fail("a member-less callback must not NPE: " + e);
      return;
    }

    assertEquals(Action.INPUT, result);
    assertTrue("finishLogin's membership-failure branch clears the session", action.getSession().isEmpty());
    assertTrue("the refusal message must be the invalidUserCrp key",
      action.getFieldErrors().get("loginMessage").contains("login.error.invalidUserCrp"));
  }

  /**
   * T09 test 7 (FN-004). A {@code .do} return URL carried in state lands there ({@code LOGIN} + the url); a
   * {@code null} one falls through to the routing switch, which for a type-1 Global Unit is {@code SUCCESS}
   * (the dashboard).
   */
  @Test
  public void deepLinkFromStateLandsThereAndANullOneFallsThroughToTheDashboard() throws Exception {
    this.userManager.register(cgiarUser(9001L));
    this.crpUserManager.isMember = true;

    TestableCognitoCallbackAction deepLinkAction = this.newAction();
    PendingAuthorization deepLinkPending = this.seedPending(deepLinkAction, "state-7a", GLOBAL_UNIT_ID,
      "https://marlo.example.org/testcrp/projectList.do", "nonce-7a");
    this.exchangeClient.idTokenToReturn = this.validIdToken(deepLinkPending.getNonce(), CGIAR_EMAIL);
    String deepLinkResult = deepLinkAction.callback("auth-code-7a", "state-7a", null);
    assertEquals(Action.LOGIN, deepLinkResult);
    assertEquals("https://marlo.example.org/testcrp/projectList.do", deepLinkAction.getUrl());

    TestableCognitoCallbackAction dashboardAction = this.newAction();
    PendingAuthorization dashboardPending =
      this.seedPending(dashboardAction, "state-7b", GLOBAL_UNIT_ID, null, "nonce-7b");
    this.exchangeClient.idTokenToReturn = this.validIdToken(dashboardPending.getNonce(), CGIAR_EMAIL);
    String dashboardResult = dashboardAction.callback("auth-code-7b", "state-7b", null);
    assertEquals(Action.SUCCESS, dashboardResult);
    assertNull(dashboardAction.getUrl());
  }

  /**
   * T09 test 8 (C-4, carried from T01 to this new caller). Unlike {@code CognitoLoginAction}, this class
   * never reads the {@code Referer} header at all -- the return URL travels in server-side state (DD-4) --
   * so no {@code ServletActionContext} / live request is bound anywhere in this test, and the callback must
   * still complete without throwing.
   */
  @Test
  public void noRequestBoundAtAllDoesNotThrow() throws Exception {
    this.userManager.register(cgiarUser(9001L));
    TestableCognitoCallbackAction action = this.newAction();
    PendingAuthorization pending = this.seedPending(action, "state-8", GLOBAL_UNIT_ID, null, "nonce-8");
    this.crpUserManager.isMember = true;
    this.exchangeClient.idTokenToReturn = this.validIdToken(pending.getNonce(), CGIAR_EMAIL);

    String result;
    try {
      result = action.callback("auth-code-8", "state-8", null);
    } catch (RuntimeException e) {
      fail("no request bound at all must not throw: " + e);
      return;
    }

    assertEquals(Action.SUCCESS, result);
  }

  /**
   * T09 test 9 (NF-002 -- isolation). A failed token exchange -- standing in for an unreachable Cognito
   * domain, since production's real {@code TokenExchangeClient} has its own bounded timeouts that turn any
   * network failure into exactly this outcome -- must refuse with the distinct service-unavailable message
   * and must not throw.
   * <p>
   * <b>What this proves and what it does not.</b> It proves this action fails closed and gracefully when
   * Cognito is unreachable. It does not re-prove that {@code login.do} keeps working -- that is
   * {@code LoginActionFinishLoginTest}'s suite, unchanged by this diff (this class adds a new sibling
   * action; it does not modify {@code LoginAction.login()} or {@code finishLogin} at all). Isolation here is
   * structural (two independent call paths, no shared mutable state) rather than an end-to-end assertion no
   * container in this repository can make.
   */
  @Test
  public void anUnreachableCognitoFailsClosedWithTheServiceUnavailableMessage() throws Exception {
    this.userManager.register(cgiarUser(9001L));
    TestableCognitoCallbackAction action = this.newAction();
    this.seedPending(action, "state-9", GLOBAL_UNIT_ID, null, "nonce-9");
    this.exchangeClient.shouldFail = true;

    String result = action.callback("auth-code-9", "state-9", null);

    // CHG-COGNITO-AUTH-001-T20 (V-5), branch :448 ("token exchange failed"). The field error is still
    // computed and kept (inert today -- see refuse()'s own comment, V-6, execution.md 37.2), but the
    // result is now a redirect to the canonical login URL, never a rendered view at cognitoCallback.do.
    assertEquals(Action.LOGIN, result);
    assertTrue("the redirect target must be the canonical login URL", action.getUrl().endsWith("/login.do"));
    assertTrue(action.getFieldErrors().get("loginMessage").contains("login.error.cognitoUnavailable"));
  }

  /**
   * Not one of the nine named scenarios, but explicitly required by the launcher's brief: SEC-006 must be
   * enforced at THIS rendering site. {@link CognitoIdentityMapper.RejectionReason#ACCOUNT_NOT_FOUND} (no
   * {@code users} row at all) and {@link CognitoIdentityMapper.RejectionReason#NOT_CGIAR_ACCOUNT} (a row
   * exists but {@code is_cgiar_user = 0}) must reach the browser as the byte-identical message -- asserted
   * here against what this action actually renders, not against the enum in isolation (T07 already covers
   * that; this closes the gap T07's own audit named: "T07 enables indistinguishable refusals but cannot
   * stop T09 from rendering {@code getRejectionReason().name()}").
   */
  @Test
  public void gateOneAndGateTwoRejectionsRenderTheIdenticalMessage() throws Exception {
    // Gate 1: no users row at all for this email.
    TestableCognitoCallbackAction gate1Action = this.newAction();
    PendingAuthorization gate1Pending = this.seedPending(gate1Action, "state-g1", GLOBAL_UNIT_ID, null, "nonce-g1");
    this.exchangeClient.idTokenToReturn = this.validIdToken(gate1Pending.getNonce(), "unknown@cgiar.org");
    String gate1Result = gate1Action.callback("auth-code-g1", "state-g1", null);
    // CHG-COGNITO-AUTH-001-T20 (V-5), branch :464 ("identity mapping rejected"). SEC-006's message-identity
    // guarantee is unaffected by the redirect -- the field error is still computed identically; only the
    // result/routing changed.
    assertEquals(Action.LOGIN, gate1Result);
    assertTrue("gate 1's redirect target must be the canonical login URL", gate1Action.getUrl().endsWith("/login.do"));
    String gate1Message = gate1Action.getFieldErrors().get("loginMessage").get(0);

    // Gate 2: a row exists but is_cgiar_user = 0.
    User localAccount = new User();
    localAccount.setId(Long.valueOf(9002L));
    localAccount.setEmail(CGIAR_EMAIL);
    localAccount.setCgiarUser(false);
    localAccount.setActive(true);
    this.userManager.register(localAccount);
    TestableCognitoCallbackAction gate2Action = this.newAction();
    PendingAuthorization gate2Pending = this.seedPending(gate2Action, "state-g2", GLOBAL_UNIT_ID, null, "nonce-g2");
    this.exchangeClient.idTokenToReturn = this.validIdToken(gate2Pending.getNonce(), CGIAR_EMAIL);
    String gate2Result = gate2Action.callback("auth-code-g2", "state-g2", null);
    assertEquals(Action.LOGIN, gate2Result);
    assertTrue("gate 2's redirect target must be the canonical login URL", gate2Action.getUrl().endsWith("/login.do"));
    String gate2Message = gate2Action.getFieldErrors().get("loginMessage").get(0);

    assertEquals("gate 1 and gate 2 refusals must be indistinguishable as rendered by this action", gate1Message,
      gate2Message);
    assertEquals("login.error.cognitoNotEligible", gate1Message);
  }

  // ---------------------------------------------------------------------------------------------------
  // CHG-COGNITO-AUTH-001-T20 (V-5): a refused callback must leave the callback URL. execution.md 37.1's
  // nine branches. Four are already covered above, updated from the old Action.INPUT contract:
  //   :420 (no pending authorization)  -- replayingAConsumedStateIsRefusedOnTheMissingEntry
  //   :426 (state mismatch)            -- CognitoLogHygieneTest#callbackRejectionPathIsAlsoSweptForSecrets
  //   :448 (token exchange failed)     -- anUnreachableCognitoFailsClosedWithTheServiceUnavailableMessage
  //   :464 (identity mapping rejected) -- gateOneAndGateTwoRejectionsRenderTheIdenticalMessage
  // The five below close the remaining branches, each asserting the result is LOGIN and that the url ends
  // with "/login.do" -- not merely that it is no longer INPUT.
  // ---------------------------------------------------------------------------------------------------

  /** Branch :433 -- the identity provider itself returned an error (user cancelled, or IdP denied). */
  @Test
  public void theIdentityProviderReturnedAnErrorRefusesAndRedirects() throws Exception {
    this.userManager.register(cgiarUser(9011L));
    TestableCognitoCallbackAction action = this.newAction();
    this.seedPending(action, "state-433", GLOBAL_UNIT_ID, null, "nonce-433");

    // idpError is non-empty; returnedCode is null exactly as Cognito would send it on a denial -- the
    // idpError check (:429) must fire first, on branch :433, never falling through to :438.
    String result = action.callback(null, "state-433", "access_denied");

    assertEquals("branch :433 (IdP returned an error) must redirect, not render", Action.LOGIN, result);
    assertTrue("branch :433's redirect target must be the canonical login URL", action.getUrl().endsWith("/login.do"));
    assertFalse("branch :433 must never target cognitoCallback.do again (no loop)",
      action.getUrl().contains("cognitoCallback"));
  }

  /** Branch :438 -- no authorization code was returned, and no IdP error either. */
  @Test
  public void noAuthorizationCodeReturnedRefusesAndRedirects() throws Exception {
    this.userManager.register(cgiarUser(9012L));
    TestableCognitoCallbackAction action = this.newAction();
    this.seedPending(action, "state-438", GLOBAL_UNIT_ID, null, "nonce-438");

    String result = action.callback(null, "state-438", null);

    assertEquals("branch :438 (no authorization code) must redirect, not render", Action.LOGIN, result);
    assertTrue("branch :438's redirect target must be the canonical login URL", action.getUrl().endsWith("/login.do"));
  }

  /** Branch :455 -- the exchanged ID token fails validation (here: a nonce that does not match the pending one). */
  @Test
  public void tokenValidationFailedRefusesAndRedirects() throws Exception {
    this.userManager.register(cgiarUser(9013L));
    TestableCognitoCallbackAction action = this.newAction();
    PendingAuthorization pending = this.seedPending(action, "state-455", GLOBAL_UNIT_ID, null, "nonce-455");
    this.crpUserManager.isMember = true;
    // The embedded nonce deliberately does not match pending.getNonce() -- the real validator (SEC-001)
    // must reject this on the nonce check, landing on branch :455, not on a signature/claims defect.
    this.exchangeClient.idTokenToReturn = this.validIdToken("not-the-pending-nonce", CGIAR_EMAIL);

    String result = action.callback("auth-code-455", "state-455", null);

    assertEquals("branch :455 (token validation failed) must redirect, not render", Action.LOGIN, result);
    assertTrue("branch :455's redirect target must be the canonical login URL", action.getUrl().endsWith("/login.do"));
  }

  /**
   * Branch :482 -- gate 1 (identity mapping) resolves a {@code userId}, but the row is gone by the time this
   * class re-reads it with {@code getUserManager().getUser(userId)}. A different method region from the other
   * eight (tasks.md T20's own "Fails when" flag) -- it sits after step 5's Global Unit/return-URL capture,
   * still before session rotation.
   */
  @Test
  public void resolvedUserVanishedBetweenTheIdentityGateAndThisReadRefusesAndRedirects() throws Exception {
    UserVanishesAfterMappingUserManager vanishingUserManager = new UserVanishesAfterMappingUserManager();
    vanishingUserManager.register(cgiarUser(9014L));
    CognitoIdentityMapperImpl mapperOverVanishingManager = new CognitoIdentityMapperImpl(vanishingUserManager);
    TestableCognitoCallbackAction action = new TestableCognitoCallbackAction(new APConfig(), vanishingUserManager,
      this.crpManager, this.crpUserManager, new NoCustomParametersManager(), new NoOpParameterManager(),
      this.realValidator, mapperOverVanishingManager, this.exchangeClient);
    action.setSession(new HashMap<String, Object>());
    PendingAuthorization pending = this.seedPending(action, "state-482", GLOBAL_UNIT_ID, null, "nonce-482");
    this.crpUserManager.isMember = true;
    this.exchangeClient.idTokenToReturn = this.validIdToken(pending.getNonce(), CGIAR_EMAIL);

    String result = action.callback("auth-code-482", "state-482", null);

    assertEquals("branch :482 (resolved user vanished) must redirect, not render", Action.LOGIN, result);
    assertTrue("branch :482's redirect target must be the canonical login URL", action.getUrl().endsWith("/login.do"));
  }

  /**
   * Branch :505 -- the ONLY branch that runs after {@code session.stop()} (:495) and a failed
   * {@code Subject.login} (:501), which is exactly why execution.md 37.3 rejects a session-backed flash for
   * this task: {@code ShiroRequestSessionCacheResetter} only runs on the success path, so on this branch the
   * pre-auth session is already gone and nothing re-establishes a fresh one. This test proves the redirect
   * itself needs no session at all -- it is built from {@code getBaseUrl()}, not from anything the stopped
   * session held.
   */
  @Test
  public void realmRejectingTheResolvedIdentityAfterSessionStopStillRedirects() throws Exception {
    // Swapped in BEFORE anything calls SecurityUtils.getSubject() -- Shiro lazily binds a Subject to
    // ThreadContext on first access, tied to whichever SecurityManager is active at that moment, and a
    // later setSecurityManager(...) does not rebind an already-created Subject. Doing this after
    // seedPending(...) (as an earlier version of this test did) silently keeps using setUp()'s no-op
    // realm, and the realm swap this test depends on never takes effect -- the exact failure mode
    // measured while writing this test (see the implementer's report).
    SecurityUtils.setSecurityManager(new DefaultSecurityManager(new RealmRejectingCognitoLogin()));

    this.userManager.register(cgiarUser(9015L));
    TestableCognitoCallbackAction action = this.newAction();
    PendingAuthorization pending = this.seedPending(action, "state-505", GLOBAL_UNIT_ID, null, "nonce-505");
    this.crpUserManager.isMember = true;
    this.exchangeClient.idTokenToReturn = this.validIdToken(pending.getNonce(), CGIAR_EMAIL);

    String result = action.callback("auth-code-505", "state-505", null);

    assertEquals("branch :505 (realm rejected the identity, after session.stop()) must redirect, not render",
      Action.LOGIN, result);
    assertTrue("branch :505's redirect target must be the canonical login URL, even with no session to read from",
      action.getUrl().endsWith("/login.do"));
  }

  /**
   * Not one of the nine branches, but explicitly required by tasks.md T20: a fresh {@code cognitoLogin.do}
   * attempt on the SAME Shiro session must still work normally after a rejected callback -- proving
   * {@code refuse()}'s new redirect writes no session state that could block re-entry (execution.md 37.3's
   * concern, applied in the other direction).
   */
  @Test
  public void aFreshCognitoLoginStartsNormallyAfterARejectedCallback() throws Exception {
    this.userManager.register(cgiarUser(9016L));
    TestableCognitoCallbackAction rejectedAction = this.newAction();
    this.seedPending(rejectedAction, "state-fresh", GLOBAL_UNIT_ID, null, "nonce-fresh");

    String rejectedResult = rejectedAction.callback(null, "state-fresh", "access_denied");
    assertEquals(Action.LOGIN, rejectedResult);
    assertTrue(rejectedAction.getUrl().endsWith("/login.do"));

    // Same thread-bound Subject/session as the rejection above -- cognitoLogin.do's own authorize() must
    // still mint a NEW PendingAuthorization and a NEW authorize URL, not be blocked by anything the refusal
    // above touched.
    TestableCognitoLoginAction loginAction = new TestableCognitoLoginAction(new ConfiguredCognitoApConfig(),
      this.userManager, this.crpManager, new ActiveOverrideCustomParameterManager(), new NoOpParameterManager());
    loginAction.setEmail(CGIAR_EMAIL);
    loginAction.setGlobalUnitId(Long.valueOf(GLOBAL_UNIT_ID));
    loginAction.setAgree(Boolean.TRUE);

    String loginResult = loginAction.authorize(null);

    assertEquals("authorize(...) must still succeed after a rejected callback", Action.SUCCESS, loginResult);
    assertNotNull("a fresh authorize URL must be issued", loginAction.getAuthorizeUrl());
    Object pendingAfter =
      SecurityUtils.getSubject().getSession().getAttribute(APConstants.COGNITO_PENDING_AUTHORIZATION);
    assertTrue("a fresh PendingAuthorization must be issued", pendingAfter instanceof PendingAuthorization);
    assertNotEquals("the fresh authorization must mint a NEW state, not the consumed one", "state-fresh",
      ((PendingAuthorization) pendingAfter).getState());
  }

  /** Throws on every call. Used to prove the Cognito dispatch path performs no LDAP/DB I/O in the realm. */
  private static final class ExplodingAuthenticator implements Authenticator {

    @Override
    public Map<String, Object> authenticate(String email, String password) {
      throw new AssertionError("must not be called on the Cognito path");
    }
  }

  /** Throws on every call. The realm must never look a user up itself on the Cognito path (DD-5). */
  private static final class ExplodingUserManager implements UserManager {

    @Override
    public User getActiveSuperAdminUserByUsernameOccurrence() {
      throw new AssertionError("must not be called on the Cognito path");
    }

    @Override
    public List<String> getCenterPermission(int userId, String crp) {
      throw new AssertionError("must not be called on the Cognito path");
    }

    @Override
    public List<String> getPermission(int userId, String crp) {
      throw new AssertionError("must not be called on the Cognito path");
    }

    @Override
    public User getUser(Long userId) {
      throw new AssertionError("must not be called on the Cognito path");
    }

    @Override
    public User getUserByEmail(String email) {
      throw new AssertionError("must not be called on the Cognito path");
    }

    @Override
    public User getUserByUsername(String username) {
      throw new AssertionError("must not be called on the Cognito path");
    }

    @Override
    public User login(String email, String password) {
      throw new AssertionError("must not be called on the Cognito path");
    }

    @Override
    public boolean saveLastLogin(User user) {
      throw new AssertionError("must not be called on the Cognito path");
    }

    @Override
    public User saveUser(User user) {
      throw new AssertionError("must not be called on the Cognito path");
    }

    @Override
    public List<User> searchUser(String searchValue) {
      throw new AssertionError("must not be called on the Cognito path");
    }
  }

  /** Resolves a fixed, pre-registered {@link GlobalUnit} by id; anything else is a test-authoring error. */
  private static final class FixedGlobalUnitManager implements GlobalUnitManager {

    private final Map<Long, GlobalUnit> byId = new HashMap<Long, GlobalUnit>();

    void register(GlobalUnit globalUnit) {
      this.byId.put(globalUnit.getId(), globalUnit);
    }

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
      return this.byId.get(Long.valueOf(globalUnitID));
    }

    @Override
    public GlobalUnit saveGlobalUnit(GlobalUnit globalUnit) {
      throw new UnsupportedOperationException("not needed by this suite");
    }
  }

  /**
   * Stands in for the pre-auth {@code SessionMap} Struts installs before the action runs -- the one
   * production has already stopped (Shiro-invalidated) by the time a regressed {@code callback(...)} would
   * still be writing through it. Every accessor throws {@code IllegalStateException}, matching {@code
   * ShiroHttpSession}'s behavior on an invalidated session exactly.
   * <p>
   * <b>Deliberately harsher than a plain {@code HashMap}.</b> A double gentler than production is what let
   * the audit's Issue 1 through all ten of the first submission's tests.
   */
  private static final class InvalidatedSessionMap implements Map<String, Object> {

    private static IllegalStateException invalidated() {
      return new IllegalStateException("session has been invalidated");
    }

    @Override
    public void clear() {
      throw invalidated();
    }

    @Override
    public boolean containsKey(Object key) {
      throw invalidated();
    }

    @Override
    public boolean containsValue(Object value) {
      throw invalidated();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
      throw invalidated();
    }

    @Override
    public Object get(Object key) {
      throw invalidated();
    }

    @Override
    public boolean isEmpty() {
      throw invalidated();
    }

    @Override
    public Set<String> keySet() {
      throw invalidated();
    }

    @Override
    public Object put(String key, Object value) {
      throw invalidated();
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> map) {
      throw invalidated();
    }

    @Override
    public Object remove(Object key) {
      throw invalidated();
    }

    @Override
    public int size() {
      throw invalidated();
    }

    @Override
    public Collection<Object> values() {
      throw invalidated();
    }
  }

  /** Toggles the {@code crp_users} membership outcome per test (T09 test 6). */
  private static final class MembershipControllableCrpUserManager implements CrpUserManager {

    private boolean isMember = true;

    @Override
    public void deleteCrpUser(long crpUserId) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public boolean existActiveCrpUser(long userId, long crpId) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public boolean existCrpUser(long crpUserID) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public boolean existCrpUser(long userId, long crpId) {
      return this.isMember;
    }

    @Override
    public List<CrpUser> findAll() {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public CrpUser getCrpUserById(long crpUserID) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public CrpUser getCrpUserByUserIdAndCrpId(long userId, long crpId) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public CrpUser saveCrpUser(CrpUser crpUser) {
      throw new UnsupportedOperationException("not needed by this suite");
    }
  }

  /**
   * Unused by this suite: this action never calls {@code LoginAction#login()} (it authenticates via
   * {@code Subject.login(CognitoAuthenticationToken)} instead), so {@code CognitoAuthSpecificity} is never
   * resolved through this manager here. Present only to satisfy the constructor CHG-COGNITO-AUTH-001-T11b
   * added to {@code LoginAction}.
   */
  private static final class NoOpParameterManager implements ParameterManager {

    @Override
    public void deleteParameter(long parameterId) {
    }

    @Override
    public boolean existParameter(long parameterID) {
      return false;
    }

    @Override
    public List<Parameter> findAll() {
      return new ArrayList<Parameter>();
    }

    @Override
    public Parameter getParameterById(long parameterID) {
      return null;
    }

    @Override
    public Parameter getParameterByKey(String key, long globalUnitId) {
      return null;
    }

    @Override
    public Parameter saveParameter(Parameter parameter) {
      return parameter;
    }
  }

  /** No custom parameters, so the session-population loop in {@code finishLogin} runs zero iterations. */
  private static final class NoCustomParametersManager implements CustomParameterManager {

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
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public CustomParameter saveCustomParameter(CustomParameter customParameter) {
      throw new UnsupportedOperationException("not needed by this suite");
    }
  }

  /**
   * Registers users by email and by id, and records every {@code saveLastLogin} call. {@code saveUser}
   * explodes: {@code AbstractMarloDAO.update(T)} returns before {@code merge()} for an entity the Hibernate
   * session already contains -- which every {@code User} handed to this double is, since it always comes
   * from a lookup -- so a production regression back to {@code saveUser()} is a no-op write that this
   * double turns into a loud test failure instead of a silent one (matching {@code
   * CognitoIdentityMappingTest}'s identical double).
   */
  private static final class RecordingUserManager implements UserManager {

    private final Map<String, User> byEmail = new HashMap<String, User>();
    private final Map<Long, User> byId = new HashMap<Long, User>();
    private User lastSavedUser;
    private int saveLastLoginCallCount;

    void register(User user) {
      this.byEmail.put(user.getEmail(), user);
      this.byId.put(user.getId(), user);
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
      return this.byId.get(userId);
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
      this.lastSavedUser = user;
      this.saveLastLoginCallCount++;
      return true;
    }

    @Override
    public User saveUser(User user) {
      throw new AssertionError("users.agree_terms must be written through saveLastLogin, not saveUser");
    }

    @Override
    public List<User> searchUser(String searchValue) {
      throw new UnsupportedOperationException("not needed by this suite");
    }
  }

  /** Records every exchange call and returns a configurable, fixed outcome. */
  private static final class RecordingTokenExchangeClient implements TokenExchangeClient {

    private String idTokenToReturn;
    private boolean shouldFail;

    @Override
    public ExchangeResult exchange(String authorizationCode, String redirectUri, String codeVerifier) {
      if (this.shouldFail) {
        return ExchangeResult.rejected();
      }
      return ExchangeResult.accepted(this.idTokenToReturn);
    }
  }

  /** Overrides only what needs a live Struts/servlet container, matching the sibling test actions. */
  private static final class TestableCognitoCallbackAction extends CognitoCallbackAction {

    private static final long serialVersionUID = 1L;

    TestableCognitoCallbackAction(APConfig config, UserManager userManager, GlobalUnitManager crpManager,
      CrpUserManager crpUserManager, CustomParameterManager customParameterManager,
      ParameterManager parameterManager, CognitoTokenValidator tokenValidator, CognitoIdentityMapper identityMapper,
      TokenExchangeClient tokenExchangeClient) {
      super(config, userManager, crpManager, crpUserManager, customParameterManager, parameterManager,
        tokenValidator, identityMapper, tokenExchangeClient);
    }

    @Override
    public String getBaseUrl() {
      return "https://marlo.example.org";
    }

    @Override
    public String getText(String aTextName) {
      return aTextName;
    }

    @Override
    public boolean isVisibleTopGUList() {
      return false;
    }

    @Override
    protected String callback(String returnedCode, String returnedState, String idpError) {
      return super.callback(returnedCode, returnedState, idpError);
    }
  }

  /**
   * T20 test support: an unconditionally-active {@code cognito_auth_active} override, so
   * {@link CognitoLoginAction#authorize(String)} resolves eligible without needing a catalog {@link Parameter}
   * row too. Every other method is unused by that test and throws.
   */
  private static final class ActiveOverrideCustomParameterManager implements CustomParameterManager {

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
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public CustomParameter getCustomParameterById(long customParameterID) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public CustomParameter getCustomParameterByParameterKeyAndGlobalUnitId(String paramaterKey, long globalUnitId) {
      CustomParameter override = new CustomParameter();
      override.setValue("true");
      override.setActive(true);
      return override;
    }

    @Override
    public CustomParameter saveCustomParameter(CustomParameter customParameter) {
      throw new UnsupportedOperationException("not needed by this suite");
    }
  }

  /**
   * T20 test support ({@link #aFreshCognitoLoginStartsNormallyAfterARejectedCallback}): a fully-configured
   * Cognito environment for {@code CognitoLoginAction#authorize(String)}, matching {@code
   * CognitoLoginActionTest}'s own {@code ConfiguredApConfig} double.
   */
  private static class ConfiguredCognitoApConfig extends APConfig {

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
   * T20 test support ({@link #realmRejectingTheResolvedIdentityAfterSessionStopStillRedirects}): accepts a
   * {@link CognitoAuthenticationToken} at {@code supports(...)} -- matching the real {@code APCustomRealm}'s
   * T06 dispatch -- but rejects it inside {@code doGetAuthenticationInfo}, the only way to reach branch :505
   * ({@code Subject.login} throwing {@link AuthenticationException}) without a live Cognito pool, since the
   * production realm never rejects a well-formed {@link CognitoAuthenticationToken} by design (DD-5).
   */
  private static final class RealmRejectingCognitoLogin extends APCustomRealm {

    RealmRejectingCognitoLogin() {
      super(new ExplodingAuthenticator(), new ExplodingAuthenticator(), new ExplodingUserManager(), new APConfig());
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
      if (token instanceof CognitoAuthenticationToken) {
        throw new AuthenticationException("simulated realm rejection for branch :505 (test-only)");
      }
      return super.doGetAuthenticationInfo(token);
    }
  }

  /** Overrides only {@code getText}, matching {@code CognitoLoginActionTest}'s and {@code CognitoLogHygieneTest}'s. */
  private static final class TestableCognitoLoginAction extends CognitoLoginAction {

    private static final long serialVersionUID = 1L;

    TestableCognitoLoginAction(APConfig config, UserManager userManager, GlobalUnitManager crpManager,
      CustomParameterManager customParameterManager, ParameterManager parameterManager) {
      super(config, userManager, crpManager, customParameterManager, parameterManager);
    }

    @Override
    public String getText(String aTextName) {
      return aTextName;
    }

    @Override
    protected String authorize(String returnUrl) {
      return super.authorize(returnUrl);
    }
  }

  /**
   * T20 test support ({@link #resolvedUserVanishedBetweenTheIdentityGateAndThisReadRefusesAndRedirects}):
   * {@code getUserByEmail} resolves a row (so gate 1 in {@link CognitoIdentityMapperImpl} accepts), but
   * {@code getUser(Long)} always returns {@code null} -- simulating the row being deleted in the window
   * between the identity gate and {@code CognitoCallbackAction}'s own re-read (branch :482).
   */
  private static final class UserVanishesAfterMappingUserManager implements UserManager {

    private final Map<String, User> byEmail = new HashMap<String, User>();

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
      return null;
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
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public List<User> searchUser(String searchValue) {
      throw new UnsupportedOperationException("not needed by this suite");
    }
  }
}
