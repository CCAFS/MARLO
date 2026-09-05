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
import org.cgiar.ccafs.marlo.action.json.global.ValidateUserAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpUserManager;
import org.cgiar.ccafs.marlo.data.manager.CustomParameterManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.ParameterManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CrpUser;
import org.cgiar.ccafs.marlo.data.model.CustomParameter;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitType;
import org.cgiar.ccafs.marlo.data.model.Parameter;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.APCustomRealm;
import org.cgiar.ccafs.marlo.security.CognitoAssertion;
import org.cgiar.ccafs.marlo.security.CognitoIdentityMapper;
import org.cgiar.ccafs.marlo.security.CognitoTokenValidator;
import org.cgiar.ccafs.marlo.security.authentication.Authenticator;
import org.cgiar.ccafs.marlo.security.impl.CognitoIdentityMapperImpl;
import org.cgiar.ccafs.marlo.security.impl.CognitoTokenValidatorImpl;
import org.cgiar.ccafs.marlo.security.impl.CognitoTokenValidatorImpl.JwksSource;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.read.ListAppender;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.util.ThreadContext;
import org.apache.struts2.ServletActionContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Covers CHG-COGNITO-AUTH-001-T14: log hygiene and observability (OPS-001, defect class D-6) across every
 * Cognito authentication surface named by the task -- {@link CognitoLoginAction}, {@link
 * CognitoCallbackAction}, {@link CognitoTokenValidatorImpl}, {@link CognitoIdentityMapperImpl}, {@link
 * ValidateUserAction} -- plus the T11b guard line in {@link LoginAction}.
 * <p>
 * <b>Captured at TRACE, not INFO.</b> This task's own <i>Not evidence when</i> clause: an appender that
 * only captures {@code INFO} and above would pass over a {@code LOG.debug("token=" + idToken)} leak, and
 * production log levels are configuration that changes independently of this test. Every logger this suite
 * attaches to is explicitly set to {@code Level.TRACE} before the appender is attached, and restored to its
 * original level afterward.
 * <p>
 * <b>Guard the guard.</b> A capturing appender that happens to receive zero events would let the
 * no-secret-leak assertion pass by asserting nothing. {@link #assertLoggerFired(ListAppender, Class)} names
 * the specific logger that produced no events rather than only checking the capture as a whole, and
 * {@link #assertLoggerFiredGuardActuallyCatchesSilence()} proves that guard itself fails loudly when a
 * logger stays silent -- the same shape of self-check {@code StrutsConfigurationWellFormedTest} and {@code
 * CognitoI18nKeysTest} already carry per the launcher's brief.
 * <p>
 * Every collaborator is a hand-rolled double: MARLO has no mocking framework ({@code DEC-005} is {@code
 * PENDING}), matching every other suite in this Cognito family.
 */
public class CognitoLogHygieneTest {

  private static final String ISSUER = "https://cognito-idp.us-east-1.amazonaws.com/us-east-1_TESTPOOL";
  private static final String AUDIENCE = "test-client-id";
  private static final String CGIAR_EMAIL = "priya.cgiar@cgiar.org";
  private static final long GLOBAL_UNIT_ID = 55L;

  /**
   * The one secret this test still chooses itself: the authorization code Cognito would return on the
   * callback query string. MARLO never generates this value (the IdP does), unlike {@code state}/{@code
   * nonce}/the PKCE verifier, which are minted by the real {@code authorize()} seam below and read back
   * from the session rather than hand-picked.
   */
  private static final String AUTH_CODE_SECRET = "authcode-super-secret-1234567890";

  private final List<LoggerHandle> attachedHandles = new ArrayList<LoggerHandle>();

  private static RSAKey generateRsaKey(String kid) throws JOSEException {
    return new RSAKeyGenerator(2048).keyID(kid).generate();
  }

  private static GlobalUnit globalUnit(long id, String acronym, int typeId) {
    GlobalUnitType type = new GlobalUnitType();
    type.setId(Long.valueOf(typeId));
    GlobalUnit unit = new GlobalUnit();
    unit.setId(Long.valueOf(id));
    unit.setAcronym(acronym);
    unit.setGlobalUnitType(type);
    return unit;
  }

  private static User cgiarUser(long id, String email) {
    User user = new User();
    user.setId(Long.valueOf(id));
    user.setEmail(email);
    user.setCgiarUser(true);
    user.setActive(true);
    return user;
  }

  private static Parameter catalogRow(String defaultValue) {
    Parameter parameter = new Parameter();
    parameter.setKey(APConstants.COGNITO_AUTH_ACTIVE);
    parameter.setDefaultValue(defaultValue);
    return parameter;
  }

  private static CustomParameter activeOverride(String value) {
    CustomParameter override = new CustomParameter();
    override.setValue(value);
    override.setActive(true);
    return override;
  }

  @Before
  public void bindSecurityManager() {
    SecurityUtils.setSecurityManager(new DefaultSecurityManager());
    ActionContext.of(new HashMap<String, Object>()).bind();
  }

  /**
   * Same shape as {@code CognitoCallbackActionTest}: the Cognito dispatch performs no I/O. Only the test
   * that drives a real {@code Subject.login(CognitoAuthenticationToken)} needs this in place of the plain
   * {@link DefaultSecurityManager} {@link #bindSecurityManager()} installs.
   */
  private static APCustomRealm realmWithNoIo() {
    return new APCustomRealm(new ExplodingAuthenticator(), new ExplodingAuthenticator(), new ExplodingUserManager(),
      new APConfig());
  }

  @After
  public void tearDown() {
    for (LoggerHandle handle : this.attachedHandles) {
      handle.logger.detachAppender(handle.appender);
      handle.logger.setLevel(handle.originalLevel);
    }
    this.attachedHandles.clear();
    SecurityUtils.setSecurityManager(null);
    ThreadContext.remove();
    ActionContext.clear();
  }

  /**
   * Attaches a fresh, started {@link ListAppender} to every {@code loggerClass}, first setting each one to
   * {@link Level#TRACE} -- the clause that decides this task. Original levels are restored in
   * {@link #tearDown()}.
   */
  private ListAppender<ILoggingEvent> attachAtTrace(Class<?>... loggerClasses) {
    ListAppender<ILoggingEvent> appender = new ListAppender<ILoggingEvent>();
    appender.start();
    for (Class<?> loggerClass : loggerClasses) {
      Logger logbackLogger = (Logger) LoggerFactory.getLogger(loggerClass);
      this.attachedHandles.add(new LoggerHandle(logbackLogger, logbackLogger.getLevel(), appender));
      logbackLogger.setLevel(Level.TRACE);
      logbackLogger.addAppender(appender);
    }
    return appender;
  }

  /**
   * Fails, naming the logger, when {@code loggerClass} produced zero captured events. The guard against a
   * pass-by-asserting-nothing appender: see the class javadoc.
   */
  private static void assertLoggerFired(ListAppender<ILoggingEvent> appender, Class<?> loggerClass) {
    for (ILoggingEvent event : appender.list) {
      if (event.getLoggerName().equals(loggerClass.getName())) {
        return;
      }
    }
    fail("expected " + loggerClass.getName() + " to have produced at least one captured TRACE+ event, but it "
      + "produced zero -- an appender that captures nothing cannot prove the absence of a leak");
  }

  /** Fails, quoting the offending line, the moment any captured message contains {@code needle}. */
  /**
   * Checks both the rendered message AND any attached throwable's class name and message, walking the full
   * {@code getCause()} chain. Audit finding (1): a check on {@code getFormattedMessage()} alone is
   * structurally blind to a secret carried by a logged exception -- e.g. {@code LOG.warn("...", e)} where
   * {@code e}'s message itself contains the leaked value. logback still renders that to the file even
   * though {@code getFormattedMessage()} never includes it.
   */
  private static void assertNoMessageContains(ListAppender<ILoggingEvent> appender, String needle, String label) {
    for (ILoggingEvent event : appender.list) {
      String formatted = event.getFormattedMessage();
      if (formatted != null && formatted.contains(needle)) {
        fail("log leak (" + label + "): logger " + event.getLoggerName() + " emitted [" + formatted + "]");
      }
      IThrowableProxy throwableProxy = event.getThrowableProxy();
      while (throwableProxy != null) {
        String className = throwableProxy.getClassName();
        String throwableMessage = throwableProxy.getMessage();
        if (className != null && className.contains(needle)) {
          fail("log leak (" + label + "): logger " + event.getLoggerName()
            + " emitted it in a throwable's class name [" + className + "]");
        }
        if (throwableMessage != null && throwableMessage.contains(needle)) {
          fail("log leak (" + label + "): logger " + event.getLoggerName()
            + " emitted it in a throwable's message [" + throwableMessage + "]");
        }
        throwableProxy = throwableProxy.getCause();
      }
    }
  }

  private String validIdToken(RSAKey signingKey, String nonce, String email) throws JOSEException {
    Instant now = Instant.now();
    JWTClaimsSet claims = new JWTClaimsSet.Builder().issuer(ISSUER).audience(AUDIENCE).subject("sub-priya")
      .claim("email", email).claim("cognito:username", "priyac").claim("token_use", "id").claim("nonce", nonce)
      .issueTime(Date.from(now.minusSeconds(5))).expirationTime(Date.from(now.plusSeconds(3600))).build();
    SignedJWT jwt = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(signingKey.getKeyID()).build(),
      claims);
    jwt.sign(new RSASSASigner(signingKey));
    return jwt.serialize();
  }

  /**
   * Test 1 (the task's own <i>Fails when</i> clause target). A complete, successful Cognito round trip --
   * attempt started, state minted, state consumed, token exchanged, token validated, identity mapped,
   * session rotated, membership gate passed, {@code finishLogin} reached -- must emit no token, code,
   * {@code state}, {@code nonce} or PKCE verifier into a log captured at {@code TRACE}, across every
   * collaborator this task names.
   * <p>
   * <b>Routed through the real {@code authorize()} seam</b>, not a hand-seeded {@link PendingAuthorization},
   * so the {@code state}/{@code nonce}/verifier this test treats as secrets are the exact
   * {@link java.security.SecureRandom}-backed values production would mint -- and so the new "attempt
   * started" line (design.md 11's first row, added after the coordinator caught its absence) is exercised
   * by the same sweep as every other line, not by a separate, easier-to-satisfy test.
   */
  @Test
  public void fullSuccessfulRoundTripLeaksNoSecretAtTraceLevel() throws Exception {
    // Subject.login(CognitoAuthenticationToken) further down needs a realm that dispatches on it (T06,
    // DD-5) -- the plain manager bindSecurityManager() installs has no realm at all. authorize() itself
    // never touches the realm, so installing this up front for the whole test is safe.
    SecurityUtils.setSecurityManager(new DefaultSecurityManager(realmWithNoIo()));

    RecordingUserManager userManager = new RecordingUserManager();
    userManager.register(cgiarUser(9001L, CGIAR_EMAIL));
    FixedGlobalUnitManager crpManager = new FixedGlobalUnitManager();
    crpManager.register(globalUnit(GLOBAL_UNIT_ID, "TESTCRP", 1));
    StubCustomParameterManager customParameterManager = new StubCustomParameterManager();
    customParameterManager.overridesByUnitId.put(Long.valueOf(GLOBAL_UNIT_ID), activeOverride("true"));
    StubParameterManager parameterManager = new StubParameterManager();
    parameterManager.catalogRow = catalogRow("false");
    MembershipCrpUserManager crpUserManager = new MembershipCrpUserManager(true);

    ListAppender<ILoggingEvent> appender = this.attachAtTrace(CognitoLoginAction.class, CognitoCallbackAction.class,
      CognitoTokenValidatorImpl.class, CognitoIdentityMapperImpl.class, LoginAction.class,
      ValidateUserAction.class);

    // Step A: mint a real PendingAuthorization through the real authorize() seam.
    TestableCognitoLoginAction loginAction = new TestableCognitoLoginAction(new ConfiguredApConfig(), userManager,
      crpManager, customParameterManager, parameterManager);
    loginAction.setEmail(CGIAR_EMAIL);
    loginAction.setGlobalUnitId(Long.valueOf(GLOBAL_UNIT_ID));
    loginAction.setAgree(Boolean.TRUE);
    assertEquals(Action.SUCCESS, loginAction.authorize(null));

    PendingAuthorization pending = (PendingAuthorization) SecurityUtils.getSubject().getSession()
      .getAttribute(APConstants.COGNITO_PENDING_AUTHORIZATION);

    // Step B: complete the round trip at the callback, exactly as Cognito would redirect back -- using the
    // nonce, state and verifier authorize() actually minted, not test-chosen stand-ins.
    RSAKey signingKey = generateRsaKey("kid-t14");
    JWKSet jwks = new JWKSet(Collections.singletonList(signingKey.toPublicJWK()));
    JwksSource jwksSource = () -> jwks;
    CognitoTokenValidatorImpl validator = new CognitoTokenValidatorImpl(ISSUER, AUDIENCE, jwksSource);
    CognitoIdentityMapperImpl identityMapper = new CognitoIdentityMapperImpl(userManager);
    RecordingTokenExchangeClient exchangeClient = new RecordingTokenExchangeClient();
    exchangeClient.idTokenToReturn = this.validIdToken(signingKey, pending.getNonce(), CGIAR_EMAIL);

    TestableCognitoCallbackAction callbackAction =
      new TestableCognitoCallbackAction(new ConfiguredApConfig(), userManager, crpManager, crpUserManager,
        new NoCustomParametersManager(), new NoOpParameterManager(), validator, identityMapper, exchangeClient);
    callbackAction.setSession(new HashMap<String, Object>());

    String result = callbackAction.callback(AUTH_CODE_SECRET, pending.getState(), null);

    assertEquals(Action.SUCCESS, result);
    // Guard the guard: a successful round trip is guaranteed to log through these three loggers (the new
    // attempt-started line, the callback success line, and finishLogin's success line). If any produced
    // nothing, the leak assertions below would be passing over an appender that never actually observed
    // that part of the flow.
    assertLoggerFired(appender, CognitoLoginAction.class);
    assertLoggerFired(appender, CognitoCallbackAction.class);
    assertLoggerFired(appender, LoginAction.class);
    assertTrue("the appender must have captured at least one event overall", !appender.list.isEmpty());

    assertNoMessageContains(appender, exchangeClient.idTokenToReturn, "raw ID token");
    assertNoMessageContains(appender, AUTH_CODE_SECRET, "authorization code");
    assertNoMessageContains(appender, pending.getState(), "state");
    assertNoMessageContains(appender, pending.getNonce(), "nonce");
    assertNoMessageContains(appender, pending.getVerifier(), "PKCE verifier");

    // design.md 11's first row, covered by the same TRACE sweep as every other line: it must exist, and it
    // must name the Global Unit (email as submitted, Global Unit acronym, resolved mode -- never a secret).
    assertMessageContaining(appender, "Cognito login attempt started for " + CGIAR_EMAIL);
    assertMessageContaining(appender, "Global Unit TESTCRP, mode COGNITO");
  }

  /**
   * Audit finding (2): no rejection path was ever swept for secrets -- every {@code
   * assertNoMessageContains} call in this class sat inside the successful round trip above. This drives a
   * real REJECTION at the callback (a tampered {@code state}, refused before the token exchange even runs)
   * using the SAME real-minted {@code state}/{@code nonce}/verifier {@code authorize()} produced, and the
   * same {@link #AUTH_CODE_SECRET}, then sweeps every rejection-path log line for all four.
   */
  @Test
  public void callbackRejectionPathIsAlsoSweptForSecrets() throws Exception {
    SecurityUtils.setSecurityManager(new DefaultSecurityManager(realmWithNoIo()));

    RecordingUserManager userManager = new RecordingUserManager();
    userManager.register(cgiarUser(9001L, CGIAR_EMAIL));
    FixedGlobalUnitManager crpManager = new FixedGlobalUnitManager();
    crpManager.register(globalUnit(GLOBAL_UNIT_ID, "TESTCRP", 1));
    StubCustomParameterManager customParameterManager = new StubCustomParameterManager();
    customParameterManager.overridesByUnitId.put(Long.valueOf(GLOBAL_UNIT_ID), activeOverride("true"));
    StubParameterManager parameterManager = new StubParameterManager();
    parameterManager.catalogRow = catalogRow("false");

    TestableCognitoLoginAction loginAction = new TestableCognitoLoginAction(new ConfiguredApConfig(), userManager,
      crpManager, customParameterManager, parameterManager);
    loginAction.setEmail(CGIAR_EMAIL);
    loginAction.setGlobalUnitId(Long.valueOf(GLOBAL_UNIT_ID));
    loginAction.setAgree(Boolean.TRUE);
    assertEquals(Action.SUCCESS, loginAction.authorize(null));
    PendingAuthorization pending = (PendingAuthorization) SecurityUtils.getSubject().getSession()
      .getAttribute(APConstants.COGNITO_PENDING_AUTHORIZATION);

    RSAKey signingKey = generateRsaKey("kid-t14-rejection");
    JWKSet jwks = new JWKSet(Collections.singletonList(signingKey.toPublicJWK()));
    CognitoTokenValidatorImpl validator = new CognitoTokenValidatorImpl(ISSUER, AUDIENCE, () -> jwks);
    CognitoIdentityMapperImpl identityMapper = new CognitoIdentityMapperImpl(userManager);
    // Never reached: the state mismatch below refuses before step 2 (token exchange) runs.
    RecordingTokenExchangeClient exchangeClient = new RecordingTokenExchangeClient();

    TestableCognitoCallbackAction callbackAction = new TestableCognitoCallbackAction(new ConfiguredApConfig(),
      userManager, crpManager, new MembershipCrpUserManager(true), new NoCustomParametersManager(),
      new NoOpParameterManager(), validator, identityMapper, exchangeClient);
    callbackAction.setSession(new HashMap<String, Object>());

    ListAppender<ILoggingEvent> appender = this.attachAtTrace(CognitoLoginAction.class, CognitoCallbackAction.class,
      CognitoTokenValidatorImpl.class, CognitoIdentityMapperImpl.class, LoginAction.class);

    String result = callbackAction.callback(AUTH_CODE_SECRET, "a-completely-wrong-state-value", null);

    // CHG-COGNITO-AUTH-001-T20 (V-5): a refused callback now redirects to the canonical login URL instead
    // of rendering login.ftl in place -- updated from the old Action.INPUT assertion, which was exactly the
    // contract that left the authorization code and state parked in the address bar (execution.md 37.1).
    assertEquals(Action.LOGIN, result);
    assertTrue("the redirect target must be the canonical login URL, not a rendered view",
      callbackAction.getUrl().endsWith("/login.do"));
    assertLoggerFired(appender, CognitoCallbackAction.class);
    assertMessageContaining(appender, "state mismatch");

    assertNoMessageContains(appender, AUTH_CODE_SECRET, "authorization code, on a rejection path");
    assertNoMessageContains(appender, pending.getState(), "state, on a rejection path");
    assertNoMessageContains(appender, pending.getNonce(), "nonce, on a rejection path");
    assertNoMessageContains(appender, pending.getVerifier(), "PKCE verifier, on a rejection path");
    // V-5's own point: the redirect target itself must never carry the code or state either.
    assertFalse("the redirect target must not carry the authorization code",
      callbackAction.getUrl().contains(AUTH_CODE_SECRET));
    assertFalse("the redirect target must not carry the state value",
      callbackAction.getUrl().contains(pending.getState()));
  }

  /**
   * Meta-test for the "guard the guard" clause itself: {@link #assertLoggerFired(ListAppender, Class)} must
   * fail, by name, when the logger it names never fired -- otherwise test 1 above could pass over an
   * appender wired to the wrong logger, or never started, without anyone noticing.
   */
  @Test
  public void assertLoggerFiredGuardActuallyCatchesSilence() {
    ListAppender<ILoggingEvent> appender = this.attachAtTrace(CognitoLoginAction.class);
    // Deliberately produce no events at all through CognitoLoginAction's logger.

    try {
      assertLoggerFired(appender, CognitoLoginAction.class);
      fail("the guard must fail when the named logger produced zero events");
    } catch (AssertionError expected) {
      assertTrue("the failure must name the silent logger", expected.getMessage().contains(
        CognitoLoginAction.class.getName()));
    }
  }

  /**
   * Meta-test for audit finding (1): {@link #assertNoMessageContains(ListAppender, String, String)} must
   * fail when the needle is carried only by a logged throwable's message, not the rendered message text --
   * otherwise a maintainer "improving" a warn call from {@code LOG.warn("...", e.getClass().getSimpleName())}
   * to {@code LOG.warn("...", e)} to get a stack trace would leak silently past every test in this suite.
   * Logs through the REAL logback pipeline (a genuine {@code ILoggingEvent} with a populated {@code
   * IThrowableProxy}), not a hand-built event, so this is the same real-machinery standard as test 1.
   */
  @Test
  public void assertNoMessageContainsGuardCatchesThrowableCarriedSecrets() {
    ListAppender<ILoggingEvent> appender = this.attachAtTrace(CognitoLoginAction.class);
    Logger rawLogger = (Logger) LoggerFactory.getLogger(CognitoLoginAction.class);
    String secret = "leak-via-throwable-message-1234567890";
    // The rendered message itself carries no secret -- only the exception's message does, exactly the
    // shape the auditor's example produces.
    rawLogger.warn("something failed, see cause", new IllegalStateException(secret));

    try {
      assertNoMessageContains(appender, secret, "throwable-carried secret");
      fail("the guard must fail when the needle only appears inside a logged throwable's message");
    } catch (AssertionError expected) {
      assertTrue("the failure must call out the throwable, not just the rendered message",
        expected.getMessage().contains("throwable"));
    }
  }

  /**
   * {@code CognitoLoginAction} rejection paths, sweeping the two branches this task adds a log line to
   * (previously silent) alongside one pre-existing branch, so each names which check failed.
   */
  @Test
  public void cognitoLoginActionRejectionsEachNameWhichCheckFailed() {
    RecordingUserManager userManager = new RecordingUserManager();
    userManager.register(cgiarUser(9001L, CGIAR_EMAIL));
    NoOpGlobalUnitManager crpManager = new NoOpGlobalUnitManager();
    crpManager.unit = globalUnit(GLOBAL_UNIT_ID, "TESTCRP", 1);
    StubCustomParameterManager customParameterManager = new StubCustomParameterManager();
    StubParameterManager parameterManager = new StubParameterManager();
    parameterManager.catalogRow = catalogRow("false");

    ListAppender<ILoggingEvent> appender = this.attachAtTrace(CognitoLoginAction.class);

    // Branch 1 (new in this task): no Global Unit selected at all.
    TestableCognitoLoginAction noUnitAction =
      new TestableCognitoLoginAction(new ConfiguredApConfig(), userManager, crpManager, customParameterManager,
        parameterManager);
    noUnitAction.setEmail(CGIAR_EMAIL);
    noUnitAction.setAgree(Boolean.TRUE);
    assertEquals(Action.INPUT, noUnitAction.authorize(null));

    // Branch 2 (new in this task): a Global Unit id that resolves to nothing.
    TestableCognitoLoginAction unresolvedAction =
      new TestableCognitoLoginAction(new ConfiguredApConfig(), userManager, crpManager, customParameterManager,
        parameterManager);
    unresolvedAction.setGlobalUnitId(Long.valueOf(999999L));
    unresolvedAction.setEmail(CGIAR_EMAIL);
    unresolvedAction.setAgree(Boolean.TRUE);
    assertEquals(Action.INPUT, unresolvedAction.authorize(null));

    // Branch 3 (pre-existing): the flag is off for the resolved Global Unit.
    TestableCognitoLoginAction flagOffAction =
      new TestableCognitoLoginAction(new ConfiguredApConfig(), userManager, crpManager, customParameterManager,
        parameterManager);
    flagOffAction.setGlobalUnitId(Long.valueOf(GLOBAL_UNIT_ID));
    flagOffAction.setEmail(CGIAR_EMAIL);
    flagOffAction.setAgree(Boolean.TRUE);
    assertEquals(Action.INPUT, flagOffAction.authorize(null));

    assertLoggerFired(appender, CognitoLoginAction.class);
    assertMessageContaining(appender, "no Global Unit was selected");
    assertMessageContaining(appender, "could not be resolved");
    assertMessageContaining(appender, "is off for Global Unit");
  }

  /**
   * Audit finding (4), CRLF advisory, {@code CognitoLoginAction}'s door: {@code this.email} is
   * attacker-controlled on this unauthenticated endpoint and reaches the attempt-started line before any
   * check has run.
   */
  @Test
  public void aNewlineInTheSubmittedEmailDoesNotForgeASecondLogLineInCognitoLoginAction() {
    String forgedSuccessLine = "2026-09-02 10:00:00 info cognitologinaction - cognito sign-in succeeded for "
      + "global unit ccafs";
    String maliciousEmail = CGIAR_EMAIL + "\n" + forgedSuccessLine;

    RecordingUserManager userManager = new RecordingUserManager();
    userManager.register(cgiarUser(9001L, maliciousEmail));
    NoOpGlobalUnitManager crpManager = new NoOpGlobalUnitManager();
    crpManager.unit = globalUnit(GLOBAL_UNIT_ID, "TESTCRP", 1);
    StubCustomParameterManager customParameterManager = new StubCustomParameterManager();
    StubParameterManager parameterManager = new StubParameterManager();
    parameterManager.catalogRow = catalogRow("false");

    TestableCognitoLoginAction action = new TestableCognitoLoginAction(new ConfiguredApConfig(), userManager,
      crpManager, customParameterManager, parameterManager);
    action.setEmail(maliciousEmail);
    action.setGlobalUnitId(Long.valueOf(GLOBAL_UNIT_ID));
    action.setAgree(Boolean.TRUE);

    ListAppender<ILoggingEvent> appender = this.attachAtTrace(CognitoLoginAction.class);

    action.authorize(null);

    assertLoggerFired(appender, CognitoLoginAction.class);
    boolean foundAttemptStarted = false;
    for (ILoggingEvent event : appender.list) {
      String formatted = event.getFormattedMessage();
      if (formatted != null && formatted.startsWith("Cognito login attempt started for")) {
        foundAttemptStarted = true;
        assertFalse("the sanitized attempt-started line must never contain a raw newline: [" + formatted + "]",
          formatted.contains("\n") || formatted.contains("\r"));
      }
    }
    assertTrue("expected the attempt-started line to have fired", foundAttemptStarted);
  }

  /**
   * {@code CognitoTokenValidatorImpl} already names which check failed for every rejection (pre-existing,
   * unmodified by this task) -- pinned here as a regression guard against this task's own sweep silently
   * removing it.
   */
  @Test
  public void tokenValidationRejectionLogsWhichCheckFailed() throws Exception {
    RSAKey signingKey = generateRsaKey("kid-t14-nonce");
    JWKSet jwks = new JWKSet(Collections.singletonList(signingKey.toPublicJWK()));
    JwksSource jwksSource = () -> jwks;
    CognitoTokenValidatorImpl validator = new CognitoTokenValidatorImpl(ISSUER, AUDIENCE, jwksSource);
    String idToken = this.validIdToken(signingKey, "actual-nonce", CGIAR_EMAIL);

    ListAppender<ILoggingEvent> appender = this.attachAtTrace(CognitoTokenValidatorImpl.class);

    CognitoTokenValidator.Result result = validator.validate(idToken, "a-completely-different-nonce");

    assertFalse(result.isAccepted());
    assertLoggerFired(appender, CognitoTokenValidatorImpl.class);
    assertMessageContaining(appender, "NONCE_MISMATCH");
    // Audit finding (2): no rejection path was ever swept for secrets before -- test 1 only exercises the
    // success path, and a leak added to a rejection branch (the auditor's own example: a raw idToken
    // appended to this exact NONCE_MISMATCH log line) would have stayed green forever. idToken is in scope
    // here and was simply never checked.
    assertNoMessageContains(appender, idToken, "raw ID token, on the rejection path");
  }

  /**
   * {@code CognitoIdentityMapperImpl} had no logger at all before this task; gate 1 (no {@code users} row)
   * must now name the gate it rejected on.
   */
  @Test
  public void identityMapperRejectionLogsWhichGateFailed() {
    RecordingUserManager userManager = new RecordingUserManager();
    // No user registered: gate 1 (ACCOUNT_NOT_FOUND) fires.
    CognitoIdentityMapperImpl identityMapper = new CognitoIdentityMapperImpl(userManager);
    CognitoAssertion assertion = new CognitoAssertion("sub-unknown", "unknown@cgiar.org", null, Instant.now());

    ListAppender<ILoggingEvent> appender = this.attachAtTrace(CognitoIdentityMapperImpl.class);

    CognitoIdentityMapper.Result result = identityMapper.map(assertion);

    assertFalse(result.isAccepted());
    assertLoggerFired(appender, CognitoIdentityMapperImpl.class);
    assertMessageContaining(appender, "ACCOUNT_NOT_FOUND");
    // Same audit finding as the token-validation sibling above: this rejection path was never swept
    // either. CognitoIdentityMapperImpl takes no raw token, so the comparable secret here is the identity
    // claim -- CognitoAssertion's own javadoc says its toString() omits it "since it lands in logs";
    // this pins that the mapper's own log line honors the same rule.
    assertNoMessageContains(appender, assertion.getIdentityClaim(), "identity claim, on the rejection path");
  }

  /**
   * {@code LoginAction#finishLogin}'s gate-4 (crp_users membership) branch had no log line at all before
   * this task -- design.md 11's own correction names this exact gap.
   */
  @Test
  public void loginActionMembershipFailureNowLogsGateFour() throws Exception {
    TestableLoginAction action = new TestableLoginAction(new APConfig(), new RecordingUserManager(),
      new NoOpGlobalUnitManager(), new MembershipCrpUserManager(false), new NoCustomParametersManager(),
      new NoOpParameterManager());
    action.setSession(new HashMap<String, Object>());
    setFormUser(action, cgiarUser(9001L, CGIAR_EMAIL));

    ListAppender<ILoggingEvent> appender = this.attachAtTrace(LoginAction.class);

    String result = action.finishLogin(cgiarUser(9001L, CGIAR_EMAIL), globalUnit(GLOBAL_UNIT_ID, "TESTCRP", 1), null);

    assertEquals(Action.INPUT, result);
    assertLoggerFired(appender, LoginAction.class);
    assertMessageContaining(appender, "gate 4: crp_users membership");
  }

  /** The success line (design.md 11) must now name the Global Unit, not only the email. */
  @Test
  public void successLineContainsTheGlobalUnit() throws Exception {
    TestableLoginAction action = new TestableLoginAction(new APConfig(), new RecordingUserManager(),
      new NoOpGlobalUnitManager(), new MembershipCrpUserManager(true), new NoCustomParametersManager(),
      new NoOpParameterManager());
    action.setSession(new HashMap<String, Object>());
    setFormUser(action, cgiarUser(9001L, CGIAR_EMAIL));

    ListAppender<ILoggingEvent> appender = this.attachAtTrace(LoginAction.class);

    String result = action.finishLogin(cgiarUser(9001L, CGIAR_EMAIL), globalUnit(GLOBAL_UNIT_ID, "TESTCRP", 1), null);

    assertEquals(Action.SUCCESS, result);
    assertLoggerFired(appender, LoginAction.class);
    assertMessageContaining(appender, "logged in successfully for Global Unit TESTCRP");
  }

  /**
   * The local-path counterpart of design.md 11's "attempt started" row (the Cognito path is covered inside
   * {@link #fullSuccessfulRoundTripLeaksNoSecretAtTraceLevel()}). Drives the real {@code login()} entry
   * point -- not {@code finishLogin} directly -- because the new line sits before the T11b guard and before
   * {@code userManager.login()}, both of which {@code login()} alone reaches.
   */
  @Test
  public void localLoginAttemptStartedLineContainsTheGlobalUnit() throws Exception {
    RecordingUserManager userManager = new RecordingUserManager();
    userManager.register(cgiarUser(9001L, CGIAR_EMAIL));
    NoOpGlobalUnitManager crpManager = new NoOpGlobalUnitManager();
    crpManager.unit = globalUnit(GLOBAL_UNIT_ID, "TESTCRP", 1);
    crpManager.byAcronym.put("TESTCRP", crpManager.unit);
    // The flag resolves to off (no override, catalog default "false"): the T11b guard must not block this
    // attempt, so the flow reaches userManager.login() and finishLogin -- exactly like
    // LoginActionCgiarGuardTest's "flag off" scenario.
    StubCustomParameterManager customParameterManager = new StubCustomParameterManager();
    StubParameterManager parameterManager = new StubParameterManager();
    parameterManager.catalogRow = catalogRow("false");

    TestableLoginAction action = new TestableLoginAction(new APConfig(), userManager, crpManager,
      new MembershipCrpUserManager(true), customParameterManager, parameterManager);
    action.setSession(new HashMap<String, Object>());
    setPlatformsList(action);
    User formUser = new User();
    formUser.setEmail(CGIAR_EMAIL);
    formUser.setPassword("whatever-password");
    action.setUser(formUser);
    action.setCrp("TESTCRP");
    // login(User, GlobalUnit) reads the Referer header via ServletActionContext; bindPostRequest() supplies
    // a request whose getHeader(...) returns null, matching LoginActionFinishLoginTest's null-Referer case.
    bindPostRequest();

    ListAppender<ILoggingEvent> appender = this.attachAtTrace(LoginAction.class);

    String result = action.login();

    assertEquals(Action.SUCCESS, result);
    assertLoggerFired(appender, LoginAction.class);
    assertMessageContaining(appender, "Local login attempt started for " + CGIAR_EMAIL);
    assertMessageContaining(appender, "Global Unit TESTCRP, mode LOCAL");
  }

  /**
   * T11b's own carried-forward obligation (execution.md 22.5): the guard's LOG line must name the gate
   * (SEC-005), while the field error the caller sees stays the byte-identical generic wrong-password
   * message.
   */
  @Test
  public void loginActionCgiarRelayGuardLogsTheGateWithoutChangingTheUserFacingMessage() throws Exception {
    ExplodingLoginUserManager userManager = new ExplodingLoginUserManager();
    userManager.register(cgiarUser(9001L, CGIAR_EMAIL));
    NoOpGlobalUnitManager crpManager = new NoOpGlobalUnitManager();
    crpManager.unit = globalUnit(1L, "AAA", 91);
    crpManager.byAcronym.put("AAA", crpManager.unit);
    StubCustomParameterManager customParameterManager = new StubCustomParameterManager();
    customParameterManager.overridesByUnitId.put(Long.valueOf(1L), activeOverride("true"));
    StubParameterManager parameterManager = new StubParameterManager();
    parameterManager.catalogRow = catalogRow("false");

    TestableLoginAction action = new TestableLoginAction(new APConfig(), userManager, crpManager,
      new UnreachedCrpUserManager(), customParameterManager, parameterManager);
    action.setSession(new HashMap<String, Object>());
    User formUser = new User();
    formUser.setEmail(CGIAR_EMAIL);
    formUser.setPassword("whatever-password");
    action.setUser(formUser);
    action.setCrp("AAA");
    setPlatformsList(action);

    ListAppender<ILoggingEvent> appender = this.attachAtTrace(LoginAction.class);

    String result = action.login();

    assertEquals(Action.INPUT, result);
    assertEquals("ADLoginMessages.ERROR_LOGON_FAILURE.getValue() must be the only user-facing text",
      1, action.getFieldErrors().get("loginMessage").size());
    assertFalse("the user-facing message must not name the gate",
      action.getFieldErrors().get("loginMessage").get(0).toLowerCase().contains("sec-005"));
    assertLoggerFired(appender, LoginAction.class);
    assertMessageContaining(appender, "SEC-005 CGIAR relay guard");
  }

  /**
   * Audit finding (4), CRLF advisory, {@code LoginAction}'s door -- both of this task's new lines that
   * interpolate the attacker-controlled, pre-authentication {@code email} (the attempt-started line and the
   * T11b guard line) in the same request, since a migrated-CGIAR-account submission reaches both in
   * sequence. See {@code aNewlineInTheSubmittedEmailDoesNotForgeASecondLogLineInValidateUserAction} for why
   * this checks only the new lines, not the pre-existing failure line elsewhere in this file.
   */
  @Test
  public void aNewlineInTheSubmittedEmailDoesNotForgeASecondLogLineInLoginAction() throws Exception {
    // Kept all-lowercase so LoginAction's userEmail.trim().toLowerCase() normalization does not stop this
    // registered user from being found -- that normalization is orthogonal to the CRLF property under test.
    String forgedSuccessLine = "2026-09-02 10:00:00 info loginaction - user victim@cgiar.org logged in "
      + "successfully for global unit ccafs.";
    String maliciousEmail = CGIAR_EMAIL + "\n" + forgedSuccessLine;

    ExplodingLoginUserManager userManager = new ExplodingLoginUserManager();
    userManager.register(cgiarUser(9001L, maliciousEmail));
    NoOpGlobalUnitManager crpManager = new NoOpGlobalUnitManager();
    crpManager.unit = globalUnit(1L, "AAA", 91);
    crpManager.byAcronym.put("AAA", crpManager.unit);
    StubCustomParameterManager customParameterManager = new StubCustomParameterManager();
    customParameterManager.overridesByUnitId.put(Long.valueOf(1L), activeOverride("true"));
    StubParameterManager parameterManager = new StubParameterManager();
    parameterManager.catalogRow = catalogRow("false");

    TestableLoginAction action = new TestableLoginAction(new APConfig(), userManager, crpManager,
      new UnreachedCrpUserManager(), customParameterManager, parameterManager);
    action.setSession(new HashMap<String, Object>());
    setPlatformsList(action);
    User formUser = new User();
    formUser.setEmail(maliciousEmail);
    formUser.setPassword("whatever-password");
    action.setUser(formUser);
    action.setCrp("AAA");
    bindPostRequest();

    ListAppender<ILoggingEvent> appender = this.attachAtTrace(LoginAction.class);

    String result = action.login();

    assertEquals(Action.INPUT, result);
    assertLoggerFired(appender, LoginAction.class);
    boolean foundAttemptStarted = false;
    boolean foundGuardLine = false;
    for (ILoggingEvent event : appender.list) {
      String formatted = event.getFormattedMessage();
      if (formatted == null) {
        continue;
      }
      if (formatted.startsWith("Local login attempt started for")) {
        foundAttemptStarted = true;
        assertFalse("the sanitized attempt-started line must never contain a raw newline: [" + formatted + "]",
          formatted.contains("\n") || formatted.contains("\r"));
      }
      if (formatted.contains("SEC-005 CGIAR relay guard")) {
        foundGuardLine = true;
        assertFalse("the sanitized guard line must never contain a raw newline: [" + formatted + "]",
          formatted.contains("\n") || formatted.contains("\r"));
      }
    }
    assertTrue("expected the attempt-started line to have fired", foundAttemptStarted);
    assertTrue("expected the SEC-005 guard line to have fired", foundGuardLine);
  }

  /** Same obligation, {@code ValidateUserAction}'s door: previously had no logger at all. */
  @Test
  public void validateUserActionRelayGuardLogsTheGateWithoutChangingTheUserFacingMessage() throws Exception {
    ExplodingLoginUserManager userManager = new ExplodingLoginUserManager();
    userManager.register(cgiarUser(9001L, CGIAR_EMAIL));
    NoOpGlobalUnitManager crpManager = new NoOpGlobalUnitManager();
    crpManager.unit = globalUnit(1L, "AAA", 91);
    StubCustomParameterManager customParameterManager = new StubCustomParameterManager();
    customParameterManager.overridesByUnitId.put(Long.valueOf(1L), activeOverride("true"));
    StubParameterManager parameterManager = new StubParameterManager();
    parameterManager.catalogRow = catalogRow("false");

    ValidateUserAction action =
      new ValidateUserAction(new APConfig(), userManager, crpManager, customParameterManager, parameterManager);
    action.setSession(new HashMap<String, Object>());
    action.setUserEmail(CGIAR_EMAIL);
    action.setUserPassword("whatever-password");
    action.setAgree(Boolean.TRUE);
    action.setGlobalUnitId(Long.valueOf(1L));
    bindPostRequest();

    ListAppender<ILoggingEvent> appender = this.attachAtTrace(ValidateUserAction.class);

    action.execute();

    assertEquals(Boolean.FALSE, action.getUserFound().get("loginSuccess"));
    assertLoggerFired(appender, ValidateUserAction.class);
    assertMessageContaining(appender, "SEC-005 CGIAR relay guard");
    // Audit finding (3): design.md 11 row 4 asks for "which gate + user id + Global Unit". this.globalUnitId
    // and this.crpManager were already in hand and unused -- pinning that the Global Unit now appears.
    assertMessageContaining(appender, "Global Unit AAA");
  }

  /**
   * Audit finding (4), CRLF advisory. {@code email} is attacker-controlled on this unauthenticated
   * endpoint; a value containing {@code \n} must never let one {@code LOG} call render as more than one
   * line in the file this task's own OPS-001 exists to make trustworthy -- otherwise
   * {@code a%0A2026-09-02 10:00:00 INFO ... - User victim@cgiar.org logged in successfully...} forges a
   * fabricated record into the authentication log. Checked at the {@link LogSanitizer}-protected new line
   * this task added ({@code ValidateUserAction}'s SEC-005 guard line), not at the pre-existing failure line
   * two lines below it in the same file, which this task's scope does not extend to (reported, not fixed).
   */
  @Test
  public void aNewlineInTheSubmittedEmailDoesNotForgeASecondLogLineInValidateUserAction() throws Exception {
    ExplodingLoginUserManager userManager = new ExplodingLoginUserManager();
    String forgedSuccessLine = "2026-09-02 10:00:00 INFO ValidateUserAction - User victim@cgiar.org logged in "
      + "successfully for Global Unit CCAFS.";
    String maliciousEmail = CGIAR_EMAIL + "\n" + forgedSuccessLine;
    userManager.register(cgiarUser(9001L, maliciousEmail));
    NoOpGlobalUnitManager crpManager = new NoOpGlobalUnitManager();
    crpManager.unit = globalUnit(1L, "AAA", 91);
    StubCustomParameterManager customParameterManager = new StubCustomParameterManager();
    customParameterManager.overridesByUnitId.put(Long.valueOf(1L), activeOverride("true"));
    StubParameterManager parameterManager = new StubParameterManager();
    parameterManager.catalogRow = catalogRow("false");

    ValidateUserAction action =
      new ValidateUserAction(new APConfig(), userManager, crpManager, customParameterManager, parameterManager);
    action.setSession(new HashMap<String, Object>());
    action.setUserEmail(maliciousEmail);
    action.setUserPassword("whatever-password");
    action.setAgree(Boolean.TRUE);
    action.setGlobalUnitId(Long.valueOf(1L));
    bindPostRequest();

    ListAppender<ILoggingEvent> appender = this.attachAtTrace(ValidateUserAction.class);

    action.execute();

    assertLoggerFired(appender, ValidateUserAction.class);
    boolean foundGuardLine = false;
    for (ILoggingEvent event : appender.list) {
      String formatted = event.getFormattedMessage();
      if (formatted != null && formatted.contains("SEC-005 CGIAR relay guard")) {
        foundGuardLine = true;
        assertFalse("the sanitized guard line must never contain a raw newline: [" + formatted + "]",
          formatted.contains("\n") || formatted.contains("\r"));
      }
    }
    assertTrue("expected the SEC-005 guard line to have fired", foundGuardLine);
  }

  private static void assertMessageContaining(ListAppender<ILoggingEvent> appender, String needle) {
    for (ILoggingEvent event : appender.list) {
      String formatted = event.getFormattedMessage();
      if (formatted != null && formatted.contains(needle)) {
        return;
      }
    }
    StringBuilder captured = new StringBuilder();
    for (ILoggingEvent event : appender.list) {
      captured.append('[').append(event.getLoggerName()).append("] ").append(event.getFormattedMessage())
        .append('\n');
    }
    fail("expected a captured message containing [" + needle + "] but found none. Captured:\n" + captured);
  }

  private static void bindPostRequest() {
    HttpServletRequest request = (HttpServletRequest) Proxy.newProxyInstance(
      CognitoLogHygieneTest.class.getClassLoader(), new Class<?>[] {HttpServletRequest.class},
      new InvocationHandler() {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
          if ("getMethod".equals(method.getName())) {
            return "POST";
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
      });
    ActionContext.of(new HashMap<String, Object>()).bind();
    ServletActionContext.setRequest(request);
  }

  private static void setFormUser(LoginAction action, User formUser) throws Exception {
    Field field = LoginAction.class.getDeclaredField("user");
    field.setAccessible(true);
    field.set(action, formUser);
  }

  private static void setPlatformsList(LoginAction action) throws Exception {
    Field field = LoginAction.class.getDeclaredField("platformsList");
    field.setAccessible(true);
    field.set(action, new ArrayList<GlobalUnit>());
  }

  /** Throws on every call. Used to prove the Cognito dispatch path performs no LDAP/DB I/O in the realm. */
  private static final class ExplodingAuthenticator implements Authenticator {

    @Override
    public Map<String, Object> authenticate(String email, String password) {
      throw new AssertionError("must not be called on the Cognito path");
    }
  }

  /** A fully-configured Cognito environment -- the opposite of design.md 9.3's phase-0 default. */
  private static final class ConfiguredApConfig extends APConfig {

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

  /** Resolves exactly the users registered; throws on {@code login} to prove it is never reached. */
  private static final class ExplodingLoginUserManager implements UserManager {

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
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public User getUserByEmail(String email) {
      return this.byEmail.get(email);
    }

    @Override
    public User getUserByUsername(String username) {
      return this.byEmail.get(username);
    }

    @Override
    public User login(String email, String password) {
      throw new AssertionError("userManager.login() must not be called (SEC-005)");
    }

    @Override
    public boolean saveLastLogin(User user) {
      throw new AssertionError("saveLastLogin must not be called on a refused attempt");
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

  /** Resolves a fixed, pre-registered {@link GlobalUnit} by id. */
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

  /** Pairs an attached logger with its appender and its pre-attach level, so both can be restored. */
  private static final class LoggerHandle {

    private final Logger logger;
    private final Level originalLevel;
    private final ListAppender<ILoggingEvent> appender;

    LoggerHandle(Logger logger, Level originalLevel, ListAppender<ILoggingEvent> appender) {
      this.logger = logger;
      this.originalLevel = originalLevel;
      this.appender = appender;
    }
  }

  /** Toggles the {@code crp_users} membership outcome (gate 4). */
  private static final class MembershipCrpUserManager implements CrpUserManager {

    private final boolean isMember;

    MembershipCrpUserManager(boolean isMember) {
      this.isMember = isMember;
    }

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

  /** Resolves a fixed unit by id, and by acronym via {@code byAcronym} when populated. */
  private static final class NoOpGlobalUnitManager implements GlobalUnitManager {

    private GlobalUnit unit;
    private final Map<String, GlobalUnit> byAcronym = new HashMap<String, GlobalUnit>();

    @Override
    public List<GlobalUnit> crpUsers(String email) {
      return this.unit == null ? new ArrayList<GlobalUnit>() : Arrays.asList(this.unit);
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
      return this.byAcronym.get(acronym);
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

  /** No custom parameters: the session-population loop in {@code finishLogin} runs zero iterations. */
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

  /** Unused by every test in this suite; present only to satisfy the constructors that need one. */
  private static final class NoOpParameterManager implements ParameterManager {

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
      return null;
    }

    @Override
    public Parameter saveParameter(Parameter parameter) {
      throw new UnsupportedOperationException("not needed by this suite");
    }
  }

  /** Records every registered user; {@code login} returns whatever was registered for that email. */
  private static final class RecordingUserManager implements UserManager {

    private final Map<String, User> byEmail = new HashMap<String, User>();
    private final Map<Long, User> byId = new HashMap<Long, User>();

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
      return this.byEmail.get(username);
    }

    @Override
    public User login(String email, String password) {
      return this.byEmail.get(email);
    }

    @Override
    public boolean saveLastLogin(User user) {
      return true;
    }

    @Override
    public User saveUser(User user) {
      return user;
    }

    @Override
    public List<User> searchUser(String searchValue) {
      throw new UnsupportedOperationException("not needed by this suite");
    }
  }

  /** Records every exchange call and returns a configurable, fixed outcome. */
  private static final class RecordingTokenExchangeClient implements TokenExchangeClient {

    private String idTokenToReturn;

    @Override
    public ExchangeResult exchange(String authorizationCode, String redirectUri, String codeVerifier) {
      return ExchangeResult.accepted(this.idTokenToReturn);
    }
  }

  /** Resolves only the {@code cognito_auth_active} key, keyed by Global Unit id. */
  private static final class StubCustomParameterManager implements CustomParameterManager {

    private final Map<Long, CustomParameter> overridesByUnitId = new HashMap<Long, CustomParameter>();

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
      // finishLogin's session-population loop calls this on the success path; empty means it runs zero
      // iterations, matching NoCustomParametersManager, without losing this class's flag-override map.
      return new ArrayList<CustomParameter>();
    }

    @Override
    public CustomParameter getCustomParameterById(long customParameterID) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public CustomParameter getCustomParameterByParameterKeyAndGlobalUnitId(String paramaterKey, long globalUnitId) {
      return this.overridesByUnitId.get(Long.valueOf(globalUnitId));
    }

    @Override
    public CustomParameter saveCustomParameter(CustomParameter customParameter) {
      throw new UnsupportedOperationException("not needed by this suite");
    }
  }

  /** Resolves only the {@code cognito_auth_active} key, with one configured catalog default. */
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
      return this.catalogRow;
    }

    @Override
    public Parameter saveParameter(Parameter parameter) {
      throw new UnsupportedOperationException("not needed by this suite");
    }
  }

  /** Overrides only what needs a live Struts/servlet container, matching every sibling test in this family. */
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

  /** Overrides only {@code getText}, which otherwise needs a live Struts container. */
  private static final class TestableCognitoLoginAction extends CognitoLoginAction {

    private static final long serialVersionUID = 1L;

    TestableCognitoLoginAction(APConfig config, UserManager userManager, GlobalUnitManager crpManager,
      CustomParameterManager customParameterManager, ParameterManager parameterManager) {
      super(config, userManager, crpManager, customParameterManager, parameterManager);
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
    protected String authorize(String returnUrl) {
      return super.authorize(returnUrl);
    }
  }

  /** Bypasses only the infrastructure this task does not touch, matching {@code LoginActionFinishLoginTest}. */
  private static final class TestableLoginAction extends LoginAction {

    private static final long serialVersionUID = 1L;

    TestableLoginAction(APConfig config, UserManager userManager, GlobalUnitManager crpManager,
      CrpUserManager crpUserManager, CustomParameterManager customParameterManager,
      ParameterManager parameterManager) {
      super(config, userManager, crpManager, crpUserManager, customParameterManager, parameterManager);
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
  }

  /** Never reached: {@code finishLogin} (T01's shared tail) is out of the T11b guard's path. */
  private static final class UnreachedCrpUserManager implements CrpUserManager {

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
      throw new UnsupportedOperationException("not needed by this suite");
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
}
