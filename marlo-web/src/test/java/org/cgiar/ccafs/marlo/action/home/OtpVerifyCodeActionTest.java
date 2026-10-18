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
import org.cgiar.ccafs.marlo.data.manager.OtpRateLimitManager;
import org.cgiar.ccafs.marlo.data.manager.ParameterManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CustomParameter;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitType;
import org.cgiar.ccafs.marlo.data.model.OtpChallenge;
import org.cgiar.ccafs.marlo.data.model.Parameter;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.APCustomRealm;
import org.cgiar.ccafs.marlo.security.OtpAuthenticationToken;
import org.cgiar.ccafs.marlo.security.authentication.Authenticator;
import org.cgiar.ccafs.marlo.security.otp.ClientIpResolver;
import org.cgiar.ccafs.marlo.security.otp.OtpCodes;
import org.cgiar.ccafs.marlo.security.otp.OtpEligibility;
import org.cgiar.ccafs.marlo.security.otp.OtpKeys;
import org.cgiar.ccafs.marlo.security.otp.OtpPending;
import org.cgiar.ccafs.marlo.security.otp.OtpRateLimiter;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.util.ThreadContext;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.struts2.ServletActionContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * CHG-OTP-LOGIN-001-T11: {@link OtpVerifyCodeAction} (FN-011 to FN-016, SEC-004 to SEC-007, SEC-010, OPS-001; design
 * 5.2) against hand-written fakes; there is no mocking framework (P-17).
 * <p>
 * <b>What is real.</b> The action runs the real {@code LoginAction.finishLogin} tail, the real Shiro
 * {@code Subject.login} through the real {@code APCustomRealm}, and the real {@link OtpRateLimiter}, {@link OtpCodes},
 * {@link OtpKeys} and {@link OtpEligibility}. Only the managers, the servlet request and the mailer are fakes. The
 * challenge store is a stateful fake that follows the documented contract of {@link OtpChallengeManager}, so a test can
 * observe what the action left in the "database", for example that a challenge stays consumed.
 * <p>
 * <b>Where the expected values come from.</b> The status words are those of design 4. The code MACs and the bucket
 * names are literals computed once with an independent implementation (Python {@code hmac}) from the secret below
 * and the formulas of design 3 and 5.3, never with the code under test. The round-trip tests take the code from the
 * mailer double of the real request action, so no fixture supplies the code that an assertion later checks. A fake
 * that discards its arguments proves nothing, so the fakes record what they receive and the tests assert it.
 */
public class OtpVerifyCodeActionTest {

  /** base64 of the 32 bytes 0x00..0x1f. */
  private static final String SECRET = "AAECAwQFBgcICQoLDA0ODxAREhMUFRYXGBkaGxwdHh8=";

  private static final long GU = 7L;
  private static final long GU_TYPE = 1L;
  private static final String GU_ACRONYM = "TESTCRP";
  private static final String COGNITO_KEY = "cognito_auth_active";
  private static final String BASE_URL = "https://marlo.example.org";
  private static final String CLIENT_IP = "203.0.113.7";
  private static final Instant NOW = Instant.parse("2026-10-13T12:00:00Z");
  private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);
  private static final Duration TTL = Duration.ofMinutes(5);
  private static final String ZERO_MAC = "0000000000000000000000000000000000000000000000000000000000000000";

  // HMAC-SHA256(k_code, "<nonce>|<code>"), k_code = HMAC(secret, "otp-code\0"). Python hmac, not the action.
  private static final String NONCE_A = "AAAAAAAAAAAAAAAAAAAAAA";
  private static final String CODE_A = "123456";
  private static final String MAC_A = "57ab94cd3209a8e1d3e35a67b785a3aa4f776043713e2fc8e4f0d88d5ad3a607";
  private static final String NONCE_B = "BBBBBBBBBBBBBBBBBBBBBB";
  private static final String CODE_B = "654321";
  private static final String MAC_B = "0925ff3f2a1bc8a8fe4d19308b2b038dc5c9771190b48eb9b9b30ab46a5b2725";
  private static final String NONCE_C = "CCCCCCCCCCCCCCCCCCCCCC";
  private static final String CODE_C = "000042";
  private static final String MAC_C = "9bd9fe2c100e4c7ab528a42ca89548f8e9c2d3900feac046fe466830dd0786bb";
  /** The MAC of the same code {@link #CODE_A}, but under another nonce: it must not open a challenge of nonce A. */
  private static final String NONCE_D = "DDDDDDDDDDDDDDDDDDDDDD";
  private static final String MAC_D = "8b218c2655506037b6d370e9ed3af400b7ceecee518c33fc33cd4a27cfcd5d91";

  // HMAC-SHA256(k_bucket, "<route>|<dimension>|<value>"), k_bucket = HMAC(secret, "otp-bucket\0").
  private static final String BUCKET_EMAIL_ANA = "b10b0338b07ff0b0b2203f32868b6ccecd287240ae39f6bf8fc9b9d58d6379db";
  private static final String BUCKET_IP_CLIENT = "c00955a96e9f5672528e4b6d96bec3b6822c93735699fd7bd810111da31e7696";
  private static final String BUCKET_EMAIL_ALB = "17192fcd4428ffc013826bc6dc3377bec395ee094ba8ebabea6e05be369fd9d8";
  private static final String BUCKET_EMAIL_AMY = "959c146e2fcab1724ddea52e71ed54db1d9bba693b510571e1f6b5da02366481";
  private static final String BUCKET_EMAIL_ANN = "3f0e46a5621229c2c60f833a65f5261c3c4125f38afc4b1bb36e3170aff41b62";

  private static final Pattern VERIFY_OK_LINE =
    Pattern.compile("auth\\.otp\\.verify outcome=ok gu=testcrp domain=partner\\.org ms=[0-9]+");

  /** The account states of FN-005, each made from the eligible world by changing one fact. */
  private enum State {
    ELIGIBLE("ana@partner.org", 41L, BUCKET_EMAIL_ANA),
    UNKNOWN("alb@partner.org", 42L, BUCKET_EMAIL_ALB),
    INACTIVE("amy@partner.org", 43L, BUCKET_EMAIL_AMY),
    NON_MEMBER("ann@partner.org", 44L, BUCKET_EMAIL_ANN),
    /** A crp_users row that exists with is_active=0: MARLO removes a member by soft delete. */
    SOFT_DELETED("ann@partner.org", 44L, BUCKET_EMAIL_ANN);

    final String email;
    final long userId;
    final String verifyEmailBucket;

    State(String email, long userId, String verifyEmailBucket) {
      this.email = email;
      this.userId = userId;
      this.verifyEmailBucket = verifyEmailBucket;
    }

    /** The submitted form of the request: padded and upper-case, so that the request also normalises. */
    String submitted() {
      return "  " + this.email.toUpperCase(Locale.ROOT) + " ";
    }
  }

  private Logger capturedLogger;
  private Level previousLevel;
  private ListAppender<ILoggingEvent> appender;

  @Before
  public void setUp() {
    this.capturedLogger = (Logger) LoggerFactory.getLogger("org.cgiar.ccafs.marlo");
    this.previousLevel = this.capturedLogger.getLevel();
    this.capturedLogger.setLevel(Level.TRACE);
    this.appender = new ListAppender<ILoggingEvent>();
    this.appender.start();
    this.capturedLogger.addAppender(this.appender);
  }

  @After
  public void tearDown() {
    this.capturedLogger.detachAppender(this.appender);
    this.capturedLogger.setLevel(this.previousLevel);
    SecurityUtils.setSecurityManager(null);
    ThreadContext.remove();
    ActionContext.clear();
  }

  // ---- FN-011: a correct code signs in through the same tail as a password login ---------------------------

  @Test
  public void aCorrectCodeSignsInAndRedirectsToTheCrpDashboardOfATypeOneUnit() {
    World world = this.with(State.ELIGIBLE);
    world.open(State.ELIGIBLE, NONCE_A, MAC_A);

    OtpVerifyCodeAction action = world.call(CODE_A);

    assertNoFault();
    assertEquals(Action.SUCCESS, world.lastResult);
    assertEquals(ok(BASE_URL + "/TESTCRP/crpDashboard.do"), action.getResponse());
    assertNotNull("the challenge is consumed", world.store.row(NONCE_A).consumedAt);
    assertTrue(SecurityUtils.getSubject().isAuthenticated());
    assertEquals("the principal is the users.id, as for every other login", Long.valueOf(41L),
      SecurityUtils.getSubject().getPrincipal());
    assertEquals(1, world.verifyLines().size());
    assertTrue(world.verifyLines().get(0), VERIFY_OK_LINE.matcher(world.verifyLines().get(0)).matches());
  }

  @Test
  public void aCgiarAccountSignsInWithItsCodeLikeAnyOtherAccountWhenItsDomainIsNotExcluded() {
    // SEC-009 (T21): on a password-on unit is_cgiar_user plays no part, so the account the Cognito rule used to
    // refuse now signs in.
    World world = this.with(State.ELIGIBLE);
    world.users.get("ana@partner.org").setCgiarUser(true);
    world.open(State.ELIGIBLE, NONCE_A, MAC_A);

    OtpVerifyCodeAction action = world.call(CODE_A);

    assertNoFault();
    assertEquals(ok(BASE_URL + "/TESTCRP/crpDashboard.do"), action.getResponse());
    assertTrue(SecurityUtils.getSubject().isAuthenticated());
    assertEquals(1, world.count("realm:"));
    assertEquals("password on: the Cognito flag is never resolved by the re-check (DD-4)", 0, world.count("cognito"));
    assertEquals("password on: is_cgiar_user is never read", 0, world.cgiarReads());
  }

  @Test
  public void theTokenIsBuiltOnlyAfterTheConsumeAndTheEligibilityRecheck() {
    World world = this.with(State.ELIGIBLE);
    world.open(State.ELIGIBLE, NONCE_A, MAC_A);

    world.call(CODE_A);

    int consume = world.events.indexOf("consume");
    int eligibilityUser = world.events.indexOf("user:ana@partner.org");
    int eligibilityMember = world.events.indexOf("member");
    int realm = world.events.indexOf("realm:41");
    assertTrue(world.events.toString(), consume >= 0 && eligibilityUser >= 0 && eligibilityMember >= 0 && realm >= 0);
    assertTrue("consume before the eligibility re-check: " + world.events, consume < eligibilityMember);
    assertTrue("the re-check before the token is built: " + world.events, eligibilityMember < realm);
    assertEquals("one token, one login", 1, world.count("realm:"));
  }

  @Test
  public void theSessionCarriesTheLoadedEntityTheUnitAndOnlyTheActiveCustomParameters() {
    World world = this.with(State.ELIGIBLE);
    world.customParameters.add(customParameter("otpTestActive", "active-value", true));
    world.customParameters.add(customParameter("otpTestInactive", "inactive-value", false));
    world.open(State.ELIGIBLE, NONCE_A, MAC_A);

    OtpVerifyCodeAction action = world.call(CODE_A);

    assertNoFault();
    Map<String, Object> session = action.getSession();
    assertSame("finishLogin got the loaded entity, which is what the session holds", world.users.get("ana@partner.org"),
      session.get(APConstants.SESSION_USER));
    assertSame(world.units.get(Long.valueOf(GU)), session.get(APConstants.SESSION_CRP));
    assertEquals("active-value", session.get("otpTestActive"));
    assertFalse("an inactive custom parameter never reaches the session", session.containsKey("otpTestInactive"));
    assertEquals("the action's own answer, which is true here so a constant false could not satisfy it",
      Boolean.TRUE, session.get(APConstants.CRP_VISIBLE_TOP_GULIST));
    assertTrue(String.valueOf(session.get("color")), String.valueOf(session.get("color")).matches("^#[0-9a-f]{6}$"));
  }

  @Test
  public void theInheritedUserFieldHoldsADetachedUserCarryingOnlyTheEmail() {
    World world = this.with(State.ELIGIBLE);
    world.open(State.ELIGIBLE, NONCE_A, MAC_A);

    OtpVerifyCodeAction action = world.call(CODE_A);

    assertNotNull("finishLogin dereferences it on its refusal paths (P-27)", action.getUser());
    assertNotSame("never the Hibernate-managed entity", world.users.get("ana@partner.org"), action.getUser());
    assertEquals("ana@partner.org", action.getUser().getEmail());
    assertNull("carries nothing else: not even the id", action.getUser().getId());
  }

  @Test
  public void aTypeTwoUnitLandsOnTheCenterDashboard() {
    World world = this.with(State.ELIGIBLE);
    world.units.get(Long.valueOf(GU)).getGlobalUnitType().setId(Long.valueOf(2L));
    world.open(State.ELIGIBLE, NONCE_A, MAC_A);

    OtpVerifyCodeAction action = world.call(CODE_A);

    assertNoFault();
    assertEquals(ok(BASE_URL + "/TESTCRP/centerDashboard.do"), action.getResponse());
  }

  @Test
  public void aSameOriginDoReturnUrlIsWhereTheUserLands() {
    World world = this.with(State.ELIGIBLE);
    world.open(State.ELIGIBLE, NONCE_A, MAC_A, BASE_URL + "/TESTCRP/projects.do", true);

    OtpVerifyCodeAction action = world.call(CODE_A);

    assertNoFault();
    assertEquals(ok(BASE_URL + "/TESTCRP/projects.do"), action.getResponse());
  }

  @Test
  public void aLeadingZeroCodeSignsIn() {
    World world = this.with(State.ELIGIBLE);
    world.open(State.ELIGIBLE, NONCE_C, MAC_C);

    assertEquals(ok(BASE_URL + "/TESTCRP/crpDashboard.do"), world.call(CODE_C).getResponse());
  }

  @Test
  public void agreeTermsIsRecordedThroughSaveLastLoginOnlyWhenThePendingChallengeCarriesIt() {
    for (boolean agree : new boolean[] {true, false}) {
      World world = this.with(State.ELIGIBLE);
      world.users.get("ana@partner.org").setAgreeTerms(Boolean.FALSE);
      world.open(State.ELIGIBLE, NONCE_A, MAC_A, null, agree);

      world.call(CODE_A);

      assertNoFault();
      assertEquals("saveLastLogin twice, the same double call as Cognito (design 5.2 step 9)", 2,
        world.userWrites.size());
      assertEquals("the write of this action is the first one: agree_terms is set before it", Boolean.valueOf(agree),
        world.userWrites.get(0).agreeTermsAtCall);
      assertEquals(Boolean.valueOf(agree), world.users.get("ana@partner.org").getAgreeTerms());
    }
  }

  @Test
  public void theOnlyWritesAreSaveLastLoginOfTheSignedInUser() {
    World world = this.with(State.ELIGIBLE);
    world.open(State.ELIGIBLE, NONCE_A, MAC_A);

    world.call(CODE_A);

    assertNoFault();
    for (UserWrite write : world.userWrites) {
      assertEquals("SEC-007: no user, membership or role is created or changed", Long.valueOf(41L), write.userId);
    }
    assertEquals("saveUser would explode, and any other manager write is unsupported", 2, world.userWrites.size());
  }

  // ---- SEC-005: the session is rotated -------------------------------------------------------------------

  @Test
  public void theSessionIdIsRotatedAndTheNewSessionHoldsNoPendingChallenge() {
    World world = this.with(State.ELIGIBLE);
    world.open(State.ELIGIBLE, NONCE_A, MAC_A);
    Serializable before = SecurityUtils.getSubject().getSession().getId();

    world.call(CODE_A);

    assertNoFault();
    Serializable after = SecurityUtils.getSubject().getSession().getId();
    assertNotEquals("the pre-authentication session id is never the authenticated one", before, after);
    assertNull(SecurityUtils.getSubject().getSession().getAttribute(APConstants.OTP_PENDING_CHALLENGE));
  }

  @Test
  public void theStoppedSessionViewIsNeverWrittenThrough() {
    World world = this.with(State.ELIGIBLE);
    world.open(State.ELIGIBLE, NONCE_A, MAC_A);
    // What Struts installs before execute(): a view of the pre-auth session, stopped by the time finishLogin runs.
    world.sessionView = invalidatedSessionMap();

    OtpVerifyCodeAction action = world.call(CODE_A);

    assertNoFault();
    assertEquals(ok(BASE_URL + "/TESTCRP/crpDashboard.do"), action.getResponse());
  }

  @Test
  public void aPostActionAccessThroughTheShiroWrappedRequestDoesNotHitTheStoppedSession() {
    World world = this.with(State.ELIGIBLE);
    ShiroHttpServletRequest shiroRequest = new ShiroHttpServletRequest(newFakeHttpServletRequest(), null, false);
    ActionContext.of(new HashMap<String, Object>()).bind();
    ServletActionContext.setRequest(shiroRequest);
    assertNotNull("forces the request to cache a wrapper of the pre-auth session", shiroRequest.getSession(true));
    world.open(State.ELIGIBLE, NONCE_A, MAC_A);

    OtpVerifyCodeAction action = world.call(CODE_A);

    assertNoFault();
    assertEquals(ok(BASE_URL + "/TESTCRP/crpDashboard.do"), action.getResponse());
    HttpSession after = shiroRequest.getSession();
    assertNotNull(after);
    try {
      after.setAttribute("cspProbe", "value");
    } catch (IllegalStateException e) {
      fail("the request still caches the stopped session: " + e);
    }
    assertEquals("value", after.getAttribute("cspProbe"));
  }

  // ---- the round trip: the code comes out of the real request action -------------------------------------

  @Test
  public void aCodeIssuedByTheRealRequestActionSignsTheUserIn() {
    World world = this.with(State.ELIGIBLE);
    world.requestCode(State.ELIGIBLE.submitted());
    assertEquals("the request mailed one code", 1, world.mails.size());
    String mailed = world.mails.get(0).code;

    OtpVerifyCodeAction action = world.call(mailed);

    assertNoFault();
    assertEquals(ok(BASE_URL + "/TESTCRP/crpDashboard.do"), action.getResponse());
    assertNotNull(world.store.onlyRow().consumedAt);
  }

  // ---- T22 (V-1): the method check, design 5.2 step 0 ---------------------------------------------------------

  @Test
  public void aRequestThatIsNotAPostIsRefusedAndChangesNothingAndALaterPostWithTheSameCodeSignsIn() {
    // A null servlet request is the last case: it can never be shown to be a POST.
    for (String method : new String[] {"GET", "HEAD", "PUT", "DELETE", "", null}) {
      World world = this.with(State.ELIGIBLE);
      Row row = world.open(State.ELIGIBLE, NONCE_A, MAC_A);
      Serializable sessionBefore = SecurityUtils.getSubject().getSession().getId();
      Object pendingBefore = SecurityUtils.getSubject().getSession().getAttribute(APConstants.OTP_PENDING_CHALLENGE);
      world.events.clear();
      world.requestMethod = method;
      world.noServletRequest = method == null;
      String label = String.valueOf(method);

      OtpVerifyCodeAction refused = world.call(CODE_A);

      assertEquals(label, only("unavailable"), refused.getResponse());
      assertEquals(label, Action.SUCCESS, world.lastResult);
      assertEquals(label + ": no limiter hit, no store, no user read, no realm", Collections.emptyList(),
        world.events);
      assertEquals(label + ": no attempt counted", 0, row.attempts);
      assertNull(label + ": the challenge is not consumed", row.consumedAt);
      assertFalse(label, SecurityUtils.getSubject().isAuthenticated());
      assertEquals(label + ": the session id is unchanged", sessionBefore,
        SecurityUtils.getSubject().getSession().getId());
      assertSame(label + ": the session still holds the pending challenge", pendingBefore,
        SecurityUtils.getSubject().getSession().getAttribute(APConstants.OTP_PENDING_CHALLENGE));
      assertEquals(label, Collections.singletonList("bad_method"), world.verifyOutcomes());

      world.requestMethod = "POST";
      world.noServletRequest = false;
      OtpVerifyCodeAction accepted = world.call(CODE_A);

      assertNoFault();
      assertEquals(label + ": the same code, sent as a POST, signs in", ok(BASE_URL + "/TESTCRP/crpDashboard.do"),
        accepted.getResponse());
      assertNotNull(label, row.consumedAt);
      assertTrue(label, SecurityUtils.getSubject().isAuthenticated());
    }
  }

  @Test
  public void aPostIsAcceptedWhateverTheCaseOfItsMethodName() {
    for (String method : new String[] {"post", "Post"}) {
      World world = this.with(State.ELIGIBLE);
      world.open(State.ELIGIBLE, NONCE_A, MAC_A);
      world.requestMethod = method;

      OtpVerifyCodeAction action = world.call(CODE_A);

      assertNoFault();
      assertEquals(method, ok(BASE_URL + "/TESTCRP/crpDashboard.do"), action.getResponse());
    }
  }

  @Test
  public void theBadMethodLineCarriesNeitherTheCodeNorTheNonceNorTheEmailNorTheSessionId() {
    World world = this.with(State.ELIGIBLE);
    world.open(State.ELIGIBLE, NONCE_A, MAC_A);
    world.requestMethod = "GET";
    Serializable sessionId = SecurityUtils.getSubject().getSession().getId();

    world.call(CODE_A);

    assertEquals(Collections.singletonList("bad_method"), world.verifyOutcomes());
    assertEquals(1, world.verifyLines().size());
    world.assertNoLeak(CODE_A, "000001", NONCE_A, "ana@partner.org", String.valueOf(sessionId), MAC_A);
  }

  // ---- FN-012: attempts -----------------------------------------------------------------------------------

  @Test
  public void wrongCodesCountAndTheThirdExhaustsTheChallengeForever() {
    World world = this.with(State.ELIGIBLE);
    Row row = world.open(State.ELIGIBLE, NONCE_A, MAC_A);

    assertEquals(only("mismatch"), world.call("000001").getResponse());
    assertEquals(1, row.attempts);
    assertEquals(only("mismatch"), world.call("000002").getResponse());
    assertEquals(2, row.attempts);
    assertEquals("the 3rd wrong code exhausts and says so", only("exhausted"), world.call("000003").getResponse());
    assertEquals(3, row.attempts);
    assertEquals("an exhausted challenge never authenticates, even with the correct code", only("exhausted"),
      world.call(CODE_A).getResponse());
    assertNull("and the correct code did not consume it", row.consumedAt);
    assertFalse(SecurityUtils.getSubject().isAuthenticated());
    assertEquals("the exhausted submission did not even reach the store write", 3, world.count("registerMismatch"));
    assertEquals(0, world.count("consume"));
    assertEquals(0, world.count("realm:"));
    assertEquals(Arrays.asList("mismatch", "mismatch", "exhausted", "exhausted"), world.verifyOutcomes());
  }

  @Test
  public void anExhaustedChallengeWithTheCorrectCodeIsExhaustedAndNeverAuthenticates() {
    World world = this.with(State.ELIGIBLE);
    Row row = world.open(State.ELIGIBLE, NONCE_A, MAC_A);
    row.attempts = 3;

    assertEquals(only("exhausted"), world.call(CODE_A).getResponse());

    assertNull(row.consumedAt);
    assertEquals(0, world.count("consume"));
    assertEquals(0, world.count("realm:"));
    assertEquals(Collections.singletonList("exhausted"), world.verifyOutcomes());
  }

  @Test
  public void aMacBoundToAnotherNonceDoesNotOpenTheChallenge() {
    World world = this.with(State.ELIGIBLE);
    world.open(State.ELIGIBLE, NONCE_A, MAC_D);

    assertEquals("the same code under another nonce is a wrong code (SEC-003)", only("mismatch"),
      world.call(CODE_A).getResponse());
    assertEquals(1, world.store.row(NONCE_A).attempts);
  }

  // ---- FN-013, RJ-3: at most once; races answer as the pre-checks do ---------------------------------------

  @Test
  public void aSecondSubmissionOfTheSameCodeIsRefusedAndSignsNobodyInTwice() {
    World world = this.with(State.ELIGIBLE);
    world.open(State.ELIGIBLE, NONCE_A, MAC_A);
    assertEquals(ok(BASE_URL + "/TESTCRP/crpDashboard.do"), world.call(CODE_A).getResponse());
    int loginsAfterFirst = world.count("realm:");
    int writesAfterFirst = world.userWrites.size();
    // A second tab of the same browser read the pending challenge before the first request removed it.
    world.seedPending(State.ELIGIBLE.email, NONCE_A, null, true);

    assertEquals(only("invalid"), world.call(CODE_A).getResponse());

    assertEquals(loginsAfterFirst, world.count("realm:"));
    assertEquals(writesAfterFirst, world.userWrites.size());
    assertEquals(0, world.count("registerMismatch"));
  }

  @Test
  public void aConcurrentConsumeThatWonTheRaceAnswersInvalidAndSignsNobodyIn() {
    World world = this.with(State.ELIGIBLE);
    Row row = world.open(State.ELIGIBLE, NONCE_A, MAC_A);
    world.store.beforeConsume = () -> row.consumedAt = NOW;

    assertEquals(only("invalid"), world.call(CODE_A).getResponse());

    assertEquals(0, world.count("realm:"));
    assertEquals(0, world.userWrites.size());
    assertFalse(SecurityUtils.getSubject().isAuthenticated());
    assertEquals(Collections.singletonList("invalid"), world.verifyOutcomes());
  }

  @Test
  public void aWrongCodeOnARowConsumedMeanwhileAnswersInvalidNotMismatch() {
    World world = this.with(State.ELIGIBLE);
    Row row = world.open(State.ELIGIBLE, NONCE_A, MAC_A);
    world.store.beforeRegisterMismatch = () -> row.consumedAt = NOW;

    assertEquals("a concurrent consume or supersede answers as the pre-check would (RJ-3)", only("invalid"),
      world.call("000001").getResponse());
    assertEquals(0, row.attempts);
  }

  @Test
  public void aWrongCodeOnARowExhaustedMeanwhileAnswersExhausted() {
    World world = this.with(State.ELIGIBLE);
    Row row = world.open(State.ELIGIBLE, NONCE_A, MAC_A);
    world.store.beforeRegisterMismatch = () -> row.attempts = 3;

    assertEquals(only("exhausted"), world.call("000001").getResponse());
  }

  // ---- FN-014: expired, superseded, consumed ---------------------------------------------------------------

  @Test
  public void anExpiredChallengeIsInvalidEvenWithTheCorrectCode() {
    World world = this.with(State.ELIGIBLE);
    Row row = world.open(State.ELIGIBLE, NONCE_A, MAC_A);
    row.expiresAt = NOW.minusSeconds(1);

    assertEquals(only("invalid"), world.call(CODE_A).getResponse());

    assertNull(row.consumedAt);
    assertEquals("refused before the compare and the consume", 0, world.count("consume"));
    assertEquals(0, world.count("realm:"));
    assertEquals(Collections.singletonList("invalid"), world.verifyOutcomes());
  }

  @Test
  public void aFiveMinuteOldChallengeIsInvalidOnceTheActionClockPassesItsExpiry() {
    World world = this.with(State.ELIGIBLE);
    world.open(State.ELIGIBLE, NONCE_A, MAC_A);
    world.actionClock = Clock.fixed(NOW.plus(TTL).plusSeconds(1), ZoneOffset.UTC);
    world.storeClock = world.actionClock;

    assertEquals("FN-010: 5 min 1 s old", only("invalid"), world.call(CODE_A).getResponse());
    assertEquals(0, world.count("realm:"));
  }

  @Test
  public void aSupersededConsumedOrPurgedChallengeIsInvalidEvenWithTheCorrectCode() {
    World world = this.with(State.ELIGIBLE);
    Row row = world.open(State.ELIGIBLE, NONCE_A, MAC_A);
    row.consumedAt = NOW.minusSeconds(10);

    assertEquals("superseded or consumed: the row still exists", only("invalid"), world.call(CODE_A).getResponse());

    world.store.rows.remove(NONCE_A);
    assertEquals("purged, or never written", only("invalid"), world.call(CODE_A).getResponse());
    assertEquals(0, world.count("consume"));
    assertEquals(0, world.count("realm:"));
  }

  @Test
  public void expiryIsNeverAcceptedOnJavasWordAloneTheStoreHasTheLastSay() {
    World world = this.with(State.ELIGIBLE);
    Row row = world.open(State.ELIGIBLE, NONCE_A, MAC_A);
    // The action's clock calls the challenge open; the store's own clock calls it expired (a shifted read).
    world.storeClock = Clock.fixed(NOW.plus(TTL).plusSeconds(1), ZoneOffset.UTC);

    assertEquals(only("invalid"), world.call(CODE_A).getResponse());

    assertEquals("the conditional update was asked, and refused", 1, world.count("consume"));
    assertNull(row.consumedAt);
    assertEquals(0, world.count("realm:"));
    assertFalse(SecurityUtils.getSubject().isAuthenticated());
  }

  @Test
  public void aChallengeAtTheExactInstantOfItsExpiryPassesTheJavaPrecheckAndTheStoreRefusesIt() {
    World world = this.with(State.ELIGIBLE);
    Row row = world.open(State.ELIGIBLE, NONCE_A, MAC_A);
    row.expiresAt = NOW;

    assertEquals(only("invalid"), world.call(CODE_A).getResponse());

    assertEquals("expires_at > now is strict in the store", 1, world.count("consume"));
    assertEquals(0, world.count("realm:"));
  }

  // ---- FN-015: eligibility is re-checked after the consume -------------------------------------------------

  @Test
  public void anAccountThatLostEligibilitySinceTheRequestIsInvalidAndTheChallengeStaysConsumed() {
    for (String lost : new String[] {"removed from the unit", "soft-deleted from the unit (is_active=0)", "deactivated",
      "deleted", "switch turned off", "domain excluded"}) {
      World world = this.with(State.ELIGIBLE);
      Row row = world.open(State.ELIGIBLE, NONCE_A, MAC_A);
      User user = world.users.get("ana@partner.org");
      switch (lost) {
        case "removed from the unit":
          world.members.clear();
          break;
        case "soft-deleted from the unit (is_active=0)":
          world.softDeleted.addAll(world.members);
          world.members.clear();
          break;
        case "deactivated":
          user.setActive(false);
          break;
        case "deleted":
          world.users.clear();
          break;
        case "switch turned off":
          world.switches.put(Long.valueOf(GU), "false");
          break;
        case "domain excluded":
          world.exclusions.put(Long.valueOf(GU), "Partner.org");
          break;
        default:
          throw new IllegalArgumentException(lost);
      }
      Serializable before = SecurityUtils.getSubject().getSession().getId();

      OtpVerifyCodeAction action = world.call(CODE_A);

      assertNoFault();
      assertEquals(lost, only("invalid"), action.getResponse());
      assertNotNull(lost + ": the challenge stays consumed", row.consumedAt);
      assertEquals(lost + ": consumed exactly once", 1, world.count("consume"));
      assertEquals(lost + ": no token is built", 0, world.count("realm:"));
      assertFalse(lost, SecurityUtils.getSubject().isAuthenticated());
      assertEquals(lost + ": no write at all", 0, world.userWrites.size());
      assertEquals(lost + ": the session is not rotated", before, SecurityUtils.getSubject().getSession().getId());
      assertNull(lost + ": the pending challenge is removed",
        SecurityUtils.getSubject().getSession().getAttribute(APConstants.OTP_PENDING_CHALLENGE));
      assertTrue(lost + ": " + world.verifyLines(), world.verifyLines().get(0).contains("outcome=not_eligible "));
    }
  }

  // ---- CHG-PWD-SWITCH-001-T03: the re-check follows the sign-in method rule -------------------------------------

  @Test
  public void ac4TheRecheckAcceptsAMemberUnderTheOverrideWhileTheCodeSwitchIsOffOrTheDomainIsExcluded() {
    for (String ownCode : new String[] {"code switch off", "domain excluded"}) {
      World world = this.with(State.ELIGIBLE);
      world.passwordSwitches.put(Long.valueOf(GU), "false");
      if ("code switch off".equals(ownCode)) {
        world.switches.put(Long.valueOf(GU), "false");
      } else {
        world.exclusions.put(Long.valueOf(GU), "Partner.org");
      }
      world.open(State.ELIGIBLE, NONCE_A, MAC_A);

      OtpVerifyCodeAction action = world.call(CODE_A);

      assertNoFault();
      assertEquals(ownCode, ok(BASE_URL + "/TESTCRP/crpDashboard.do"), action.getResponse());
      assertTrue(ownCode, SecurityUtils.getSubject().isAuthenticated());
      assertEquals(ownCode + ": one token", 1, world.count("realm:"));
    }
  }

  @Test
  public void theRecheckReadsTheCognitoFlagForTheUnitAfterTheConsumeAndBeforeTheAccountLookup() {
    World world = this.with(State.ELIGIBLE);
    world.passwordSwitches.put(Long.valueOf(GU), "false");
    world.switches.put(Long.valueOf(GU), "false");
    world.open(State.ELIGIBLE, NONCE_A, MAC_A);

    world.call(CODE_A);

    int consume = world.events.indexOf("consume");
    int cognito = world.events.indexOf("cognito-custom:" + GU);
    int lookup = world.events.indexOf("user:ana@partner.org");
    assertTrue(world.events.toString(), consume >= 0 && cognito > consume && lookup > cognito);
    assertEquals("one read for the unit's own row, as the resolver does: " + world.events, 1,
      world.count("cognito-custom"));
  }

  @Test
  public void aCodeIssuedUnderTheOverrideIsRefusedAtTheRecheckOnceTheAccountBecomesACognitoAccount() {
    for (boolean cognitoTurnedOnLater : new boolean[] {false, true}) {
      World world = this.with(State.ELIGIBLE);
      world.users.get("ana@partner.org").setCgiarUser(true);
      world.passwordSwitches.put(Long.valueOf(GU), "false");
      world.switches.put(Long.valueOf(GU), "false");
      // The unit has no Cognito when the code is requested: the member is mailed the code.
      world.requestCode(State.ELIGIBLE.submitted());
      assertEquals("issued legitimately: the account was not a Cognito account", 1, world.mails.size());
      String mailed = world.mails.get(0).code;
      Row row = world.store.onlyRow();
      if (cognitoTurnedOnLater) {
        world.cognitoOverrides.put(Long.valueOf(GU), "true");
      }
      int cognitoReadsBefore = world.count("cognito-custom");

      OtpVerifyCodeAction action = world.call(mailed);

      assertNoFault();
      assertNotNull("the challenge is consumed either way", row.consumedAt);
      assertEquals("one Cognito read by the re-check", cognitoReadsBefore + 1, world.count("cognito-custom"));
      if (cognitoTurnedOnLater) {
        assertEquals("FN-005, SEC-004: a Cognito account has no code of its own here", only("invalid"),
          action.getResponse());
        assertEquals("no token is built", 0, world.count("realm:"));
        assertFalse(SecurityUtils.getSubject().isAuthenticated());
        assertEquals(0, world.userWrites.size());
        assertTrue(world.verifyLines().toString(), world.verifyLines().get(0).contains("outcome=not_eligible "));
      } else {
        assertEquals("control: the same code signs in while the setting has not changed",
          ok(BASE_URL + "/TESTCRP/crpDashboard.do"), action.getResponse());
        assertTrue(SecurityUtils.getSubject().isAuthenticated());
        assertEquals(1, world.count("realm:"));
      }
    }
  }

  @Test
  public void aCognitoAccountStillSignsInWithItsCodeWhereTheUnitOffersItByItsOwnSettings() {
    // FN-005: the password being off changes nothing for a Cognito account, which keeps the code the unit offers.
    World world = this.with(State.ELIGIBLE);
    world.users.get("ana@partner.org").setCgiarUser(true);
    world.passwordSwitches.put(Long.valueOf(GU), "false");
    world.cognitoOverrides.put(Long.valueOf(GU), "true");
    world.open(State.ELIGIBLE, NONCE_A, MAC_A);

    OtpVerifyCodeAction action = world.call(CODE_A);

    assertNoFault();
    assertEquals(ok(BASE_URL + "/TESTCRP/crpDashboard.do"), action.getResponse());
    assertEquals("the flag was read, and the code switch of the unit decided", 1, world.count("cognito-custom"));
  }

  @Test
  public void onAPasswordOnUnitTheRecheckNeverResolvesTheCognitoFlagForAnyAccountState() {
    // Replaces the T21 locks (design DD-4, P-16) with counting doubles, for the re-check of every account state.
    for (State state : State.values()) {
      World world = this.with(state);
      world.cognitoOverrides.put(Long.valueOf(GU), "true");
      if (world.users.containsKey(state.email)) {
        world.users.get(state.email).setCgiarUser(true);
      }
      world.open(state, NONCE_A, MAC_A);

      world.call(CODE_A);

      assertEquals(state + ": zero Cognito reads: " + world.events, 0, world.count("cognito"));
      assertEquals(state + ": is_cgiar_user never read", 0, world.cgiarReads());
    }
  }

  @Test
  public void aDecoyGuessedWithItsRandomCodeIsInvalidLikeAnExpiredOneAndNeverSignsIn() {
    World world = this.with(State.UNKNOWN);
    // The decoy's code is random and never mailed. Here a fake supplies the "guessed" code, to exercise the path.
    Row decoy = world.open(State.UNKNOWN, NONCE_B, MAC_B);

    Map<String, Object> decoyAnswer = world.call(CODE_B).getResponse();
    assertEquals(only("invalid"), decoyAnswer);

    assertNotNull("the guess consumed the decoy, as it does a real row", decoy.consumedAt);
    assertEquals("no token for an account that does not exist", 0, world.count("realm:"));
    assertFalse(SecurityUtils.getSubject().isAuthenticated());
    assertEquals(0, world.userWrites.size());

    World expired = this.with(State.ELIGIBLE);
    expired.open(State.ELIGIBLE, NONCE_B, MAC_B).expiresAt = NOW.minusSeconds(1);
    assertEquals("the same answer an expired code gets", expired.call(CODE_B).getResponse(), decoyAnswer);
  }

  @Test
  public void everyExceptionOfTheEligibilityRecheckAnswersTheSameAsAnIneligibleAccount() {
    for (Throwable failure : new Throwable[] {new IllegalStateException("boom"), new AssertionError("boom"),
      new NoClassDefFoundError("boom")}) {
      World world = this.with(State.ELIGIBLE);
      Row row = world.open(State.ELIGIBLE, NONCE_A, MAC_A);
      world.userLookupFailure = failure;

      OtpVerifyCodeAction action = world.call(CODE_A);

      assertEquals(failure.getClass().getSimpleName(), only("invalid"), action.getResponse());
      assertNotNull(row.consumedAt);
      assertEquals(0, world.count("realm:"));
      assertFalse(SecurityUtils.getSubject().isAuthenticated());
      assertEquals(1, world.verifyLines().size());
      assertTrue(world.verifyLines().get(0), world.verifyLines().get(0).contains("outcome=internal_error "));
      for (String fault : world.lines("auth.otp.fault")) {
        assertFalse("only the class name: " + fault, fault.contains("boom"));
      }
    }
  }

  // ---- FN-016, FN-005 (verify half), SEC-006 --------------------------------------------------------------

  @Test
  public void noPendingChallengeAnswersExactlyLikeAWrongCodeAndTouchesNoStore() {
    World withNone = this.with(State.ELIGIBLE);
    Map<String, Object> noPending = withNone.call(CODE_A).getResponse();
    assertEquals(Collections.singletonList("mismatch"), withNone.verifyOutcomes());
    assertEquals(0, withNone.store.calls.size());
    assertEquals(0, withNone.count("user:"));

    World withPending = this.with(State.ELIGIBLE);
    withPending.open(State.ELIGIBLE, NONCE_A, MAC_A);
    Map<String, Object> wrongCode = withPending.call("000001").getResponse();

    assertEquals(only("mismatch"), noPending);
    assertEquals("FN-016: the same answer as a wrong code", wrongCode, noPending);
  }

  @Test
  public void aPendingValueOfAnotherTypeCountsAsNoPendingChallenge() {
    World world = this.with(State.ELIGIBLE);
    SecurityUtils.getSubject().getSession().setAttribute(APConstants.OTP_PENDING_CHALLENGE, "not a pending");

    assertEquals(only("mismatch"), world.call(CODE_A).getResponse());
    assertEquals(0, world.store.calls.size());
  }

  @Test
  public void afterAnyRequestTheVerifySequenceIsTheSameForEveryAccountState() {
    for (State state : State.values()) {
      World world = this.with(state);
      world.requestCode(state.submitted());
      assertEquals(state + ": one row is written in every state (DD-5)", 1, world.store.rows.size());
      assertEquals(state + ": only an eligible account is mailed", state == State.ELIGIBLE ? 1 : 0, world.mails.size());
      Row row = world.store.onlyRow();

      List<Object> statuses = new ArrayList<Object>();
      for (int i = 0; i < 4; i++) {
        statuses.add(world.call(world.aWrongCodeFor(row)).getResponse().get("status"));
      }

      assertEquals(state.toString(), Arrays.<Object>asList("mismatch", "mismatch", "exhausted", "exhausted"),
        statuses);
      assertEquals(state + ": the same attempt count", 3, row.attempts);
      assertEquals(state + ": nobody signed in", 0, world.count("realm:"));
      if (state == State.ELIGIBLE) {
        assertEquals("the mailed code is refused once the challenge is exhausted", only("exhausted"),
          world.call(world.mails.get(0).code).getResponse());
        assertNull(row.consumedAt);
      }
    }
  }

  @Test
  public void aChallengeSupersededByARequestFromAnotherSessionIsInvalidInEveryState() {
    for (State state : new State[] {State.ELIGIBLE, State.UNKNOWN}) {
      World world = this.with(state);
      world.requestCode(state.submitted());
      Row first = world.store.onlyRow();
      String firstCode = state == State.ELIGIBLE ? world.mails.get(0).code : world.aWrongCodeFor(first);
      // Another session requests a code for the same email: the first row is superseded (consumed_at is set).
      world.store.issue(first.emailHmac, GU, "OTHERNONCEOTHERNONCE0", ZERO_MAC, NOW.plus(TTL));

      assertEquals(state + ": superseded", only("invalid"), world.call(firstCode).getResponse());
      assertEquals(0, world.count("realm:"));
    }
  }

  // ---- step 1: the limiter ---------------------------------------------------------------------------------

  @Test
  public void theLimiterCountsTheEmailAndTheIpBucketBeforeAnyStoreOrUserRead() {
    World world = this.with(State.ELIGIBLE);
    world.open(State.ELIGIBLE, NONCE_A, MAC_A);

    world.call("000001");

    assertEquals(Arrays.asList("hit:" + State.ELIGIBLE.verifyEmailBucket, "hit:" + BUCKET_IP_CLIENT),
      world.events.subList(0, 2));
    assertEquals("find:" + NONCE_A, world.events.get(2));
    assertEquals("a verify never moves the request cooldown", 0, world.count("cooldown:"));
  }

  @Test
  public void withNoPendingOnlyTheIpBucketIsCountedAndAMalformedCodeIsCountedToo() {
    World world = this.with(State.ELIGIBLE);

    world.call("12");

    assertEquals(Collections.singletonList("hit:" + BUCKET_IP_CLIENT), world.events);
  }

  @Test
  public void theLimitsAreTenPerEmailAndFiftyPerIpAndHoldAtTheBoundary() {
    World atLimit = this.with(State.ELIGIBLE);
    atLimit.open(State.ELIGIBLE, NONCE_A, MAC_A);
    atLimit.hitAnswers.put(State.ELIGIBLE.verifyEmailBucket, Integer.valueOf(10));
    atLimit.hitAnswers.put(BUCKET_IP_CLIENT, Integer.valueOf(50));
    assertEquals("the 10th and the 50th are allowed", only("mismatch"), atLimit.call("000001").getResponse());

    World overEmail = this.with(State.ELIGIBLE);
    Row row = overEmail.open(State.ELIGIBLE, NONCE_A, MAC_A);
    overEmail.hitAnswers.put(State.ELIGIBLE.verifyEmailBucket, Integer.valueOf(11));
    assertEquals(only("rateLimited"), overEmail.call(CODE_A).getResponse());
    assertEquals("nothing after the limiter ran", 0, overEmail.store.calls.size());
    assertEquals(0, row.attempts);
    assertNotNull("the pending challenge is kept", SecurityUtils.getSubject().getSession()
      .getAttribute(APConstants.OTP_PENDING_CHALLENGE));
    assertEquals(Collections.singletonList("rate_limited"), overEmail.verifyOutcomes());

    World overIp = this.with(State.ELIGIBLE);
    overIp.open(State.ELIGIBLE, NONCE_A, MAC_A);
    overIp.hitAnswers.put(BUCKET_IP_CLIENT, Integer.valueOf(51));
    assertEquals(only("rateLimited"), overIp.call(CODE_A).getResponse());
    assertEquals(0, overIp.store.calls.size());
  }

  @Test
  public void aLimiterStoreFailureAnswersUnavailableAndTouchesNothingElse() {
    World world = this.with(State.ELIGIBLE);
    world.open(State.ELIGIBLE, NONCE_A, MAC_A);
    world.rateStoreFailure = new IllegalStateException("lock wait timeout for " + NONCE_A);

    assertEquals(only("unavailable"), world.call(CODE_A).getResponse());

    assertEquals(0, world.store.calls.size());
    assertEquals(0, world.count("realm:"));
    assertEquals(Collections.singletonList("internal_error"), world.verifyOutcomes());
    world.assertNoLeak(NONCE_A);
  }

  @Test
  public void anUnconfiguredSecretAnswersUnavailableThroughTheLimiter() {
    World world = this.with(State.ELIGIBLE);
    world.open(State.ELIGIBLE, NONCE_A, MAC_A);
    world.secret = "";

    assertEquals(only("unavailable"), world.call(CODE_A).getResponse());

    assertEquals(0, world.store.calls.size());
    assertEquals(0, world.count("hit:"));
  }

  // ---- step 2: shape ----------------------------------------------------------------------------------------

  @Test
  public void aMalformedCodeIsAMismatchThatSpendsNoAttemptAndReadsNoRow() {
    for (String bad : new String[] {null, "", "12345", "1234567", "12345a", " 123456", "123456 ", "12 456",
      "١٢٣٤٥٦"}) {
      World world = this.with(State.ELIGIBLE);
      Row row = world.open(State.ELIGIBLE, NONCE_A, MAC_A);

      assertEquals("shape " + bad, only("mismatch"), world.call(bad).getResponse());

      assertEquals("shape " + bad, 0, row.attempts);
      assertEquals("shape " + bad + ": the row is not even read", 0, world.store.calls.size());
      assertEquals("shape " + bad + ": but it was counted", 2, world.count("hit:"));
    }
  }

  // ---- step 9, 10: what finishLogin and the realm can refuse ---------------------------------------------

  @Test
  public void aMembershipRefusalInsideTheRealFinishLoginAnswersFailedWithTheGenericRedirectAndNoNpe() {
    World world = this.with(State.ELIGIBLE);
    Row row = world.open(State.ELIGIBLE, NONCE_A, MAC_A);
    // The re-check (step 8, existActiveCrpUser) still sees the membership; gate 4 inside finishLogin
    // (existCrpUser) no longer does.
    world.memberAnswers.add(Boolean.FALSE);

    OtpVerifyCodeAction action = world.call(CODE_A);

    assertNoFault();
    assertEquals(failed(), action.getResponse());
    assertEquals("the real tail ran its refusal branch: its field error is the evidence", Collections
      .singletonList("login.error.invalidUserCrp"), action.getFieldErrors().get("loginMessage"));
    assertFalse("the real tail logged the user out", SecurityUtils.getSubject().isAuthenticated());
    assertFalse("and cleared the session view", action.getSession().containsKey(APConstants.SESSION_USER));
    assertNotNull(row.consumedAt);
    assertEquals(Collections.singletonList("failed"), world.verifyOutcomes());
  }

  @Test
  public void aGlobalUnitThatVanishedAfterTheConsumeAnswersFailedThroughTheRealFinishLoginToo() {
    World world = this.with(State.ELIGIBLE);
    world.open(State.ELIGIBLE, NONCE_A, MAC_A);
    world.unitVanishesAfterTheFirstRead = true;

    OtpVerifyCodeAction action = world.call(CODE_A);

    assertNoFault();
    assertEquals(failed(), action.getResponse());
    assertEquals(Collections.singletonList("login.error.selectCrp"), action.getFieldErrors().get("loginMessage"));
    assertFalse(SecurityUtils.getSubject().isAuthenticated());
  }

  @Test
  public void aUserThatVanishedAfterTheConsumeAnswersFailedAndNothingIsRotatedOrWritten() {
    World world = this.with(State.ELIGIBLE);
    Row row = world.open(State.ELIGIBLE, NONCE_A, MAC_A);
    world.userVanishesAfterTheFirstLookup = true;
    Serializable before = SecurityUtils.getSubject().getSession().getId();

    OtpVerifyCodeAction action = world.call(CODE_A);

    assertNoFault();
    assertEquals(failed(), action.getResponse());
    assertNotNull(row.consumedAt);
    assertEquals(0, world.count("realm:"));
    assertEquals(0, world.userWrites.size());
    assertEquals("the guard sits before the session is stopped", before, SecurityUtils.getSubject().getSession()
      .getId());
    assertEquals(Collections.singletonList("failed"), world.verifyOutcomes());
  }

  @Test
  public void aRealmThatRejectsTheTokenAnswersFailedNotAServerError() {
    World world = this.with(State.ELIGIBLE);
    world.open(State.ELIGIBLE, NONCE_A, MAC_A);
    world.realm.rejection = new AuthenticationException("simulated realm rejection (test only)");

    OtpVerifyCodeAction action = world.call(CODE_A);

    assertEquals(failed(), action.getResponse());
    assertEquals(Action.SUCCESS, world.lastResult);
    assertEquals(Collections.singletonList("failed"), world.verifyOutcomes());
    assertFalse(SecurityUtils.getSubject().isAuthenticated());
    for (String fault : world.lines("auth.otp.fault")) {
      assertFalse("only the class name: " + fault, fault.contains("simulated"));
    }
  }

  // ---- store failures --------------------------------------------------------------------------------------

  @Test
  public void aStoreFailureAtAnyStepAnswersUnavailableAndSignsNobodyIn() {
    for (String step : new String[] {"find", "registerMismatch", "consume"}) {
      World world = this.with(State.ELIGIBLE);
      Row row = world.open(State.ELIGIBLE, NONCE_A, MAC_A);
      RuntimeException failure = new IllegalStateException("store down " + NONCE_A + " " + CODE_A);
      String code = CODE_A;
      switch (step) {
        case "find":
          world.store.findFailure = failure;
          break;
        case "registerMismatch":
          world.store.registerFailure = failure;
          code = "000001";
          break;
        default:
          world.store.consumeFailure = failure;
          break;
      }

      assertEquals(step, only("unavailable"), world.call(code).getResponse());

      assertEquals(step, 0, world.count("realm:"));
      assertNull(step + ": nothing consumed", row.consumedAt);
      assertEquals(step, Collections.singletonList("internal_error"), world.verifyOutcomes());
      world.assertNoLeak(NONCE_A, CODE_A);
      assertEquals(step + ": one fault line, the class only", 1, world.lines("auth.otp.fault").size());
    }
  }

  // ---- OPS-001: one line per call, and nothing secret in it ------------------------------------------------

  @Test
  public void everyCallWritesExactlyOneVerifyLineAndNoLineCarriesASecret() {
    World world = this.with(State.ELIGIBLE);
    world.open(State.ELIGIBLE, NONCE_A, MAC_A);
    Serializable sessionId = SecurityUtils.getSubject().getSession().getId();

    world.call("000001");
    world.call(CODE_A);

    assertEquals(Arrays.asList("mismatch", "ok"), world.verifyOutcomes());
    world.assertNoLeak(CODE_A, "000001", NONCE_A, "ana@partner.org", String.valueOf(sessionId),
      String.valueOf(SecurityUtils.getSubject().getSession().getId()), MAC_A);
  }

  // ---- the shipped artifact ---------------------------------------------------------------------------------

  @Test
  public void theContainerBuildsTheActionThroughOneInjectConstructorTakingTheBeans() {
    List<Constructor<?>> injectable = new ArrayList<Constructor<?>>();
    for (Constructor<?> constructor : OtpVerifyCodeAction.class.getDeclaredConstructors()) {
      if (constructor.isAnnotationPresent(Inject.class)) {
        injectable.add(constructor);
      }
    }
    assertEquals("exactly one injectable constructor, so the container never picks the test seam", 1,
      injectable.size());
    assertTrue(Modifier.isPublic(injectable.get(0).getModifiers()));
    assertEquals(Arrays.<Class<?>>asList(APConfig.class, UserManager.class, GlobalUnitManager.class,
      CrpUserManager.class, CustomParameterManager.class, ParameterManager.class, OtpRateLimiter.class,
      OtpCodes.class, OtpEligibility.class, OtpChallengeManager.class),
      Arrays.asList(injectable.get(0).getParameterTypes()));
  }

  @Test
  public void thisActionIsTheOnlySiteInSrcMainThatBuildsTheOtpToken() throws Exception {
    Set<String> sites = new HashSet<String>();
    for (String root : new String[] {"src/main/java", "../marlo-data/src/main/java"}) {
      try (Stream<Path> files = Files.walk(Paths.get(root))) {
        for (Path file : files.filter(path -> path.toString().endsWith(".java")).collect(Collectors.toList())) {
          String source = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
          String code = source.replaceAll("(?s)/\\*.*?\\*/", "").replaceAll("//[^\n]*", "");
          if (code.contains("new OtpAuthenticationToken(")) {
            sites.add(file.getFileName().toString());
          }
        }
      }
    }
    assertEquals("pre-auth code builds the token only after the consume and the re-check", Collections
      .singleton("OtpVerifyCodeAction.java"), sites);
  }

  @Test
  public void theActionNeverConstructsTheSharedBeansAndNeverOverridesValidate() throws Exception {
    String source = new String(Files.readAllBytes(
      Paths.get("src/main/java/org/cgiar/ccafs/marlo/action/home/OtpVerifyCodeAction.java")), StandardCharsets.UTF_8);
    String code = source.replaceAll("(?s)/\\*.*?\\*/", "").replaceAll("//[^\n]*", "");

    for (String bean : new String[] {"OtpKeys", "OtpRateLimiter", "OtpCodes", "OtpEligibility"}) {
      assertFalse(bean + " is a container singleton: inject it, never new it", code.contains("new " + bean + "("));
    }
    for (Method method : OtpVerifyCodeAction.class.getDeclaredMethods()) {
      assertNotEquals("no transactional manager call may run in validate(): everything runs in execute()",
        "validate", method.getName());
    }
  }

  // ---- helpers ------------------------------------------------------------------------------------------------

  private World with(State state) {
    return new World().withFixtures(state);
  }

  private void assertNoFault() {
    List<String> faults = new ArrayList<String>();
    for (ILoggingEvent event : this.appender.list) {
      if (event.getFormattedMessage().startsWith("auth.otp.fault")) {
        faults.add(event.getFormattedMessage());
      }
    }
    assertTrue("unexpected fault: " + faults, faults.isEmpty());
  }

  private static Map<String, Object> only(String status) {
    return Collections.<String, Object>singletonMap("status", status);
  }

  private static Map<String, Object> ok(String redirect) {
    Map<String, Object> expected = new LinkedHashMap<String, Object>();
    expected.put("status", "ok");
    expected.put("redirect", redirect);
    return expected;
  }

  /** The generic refusal: Cognito's category, because only that literal makes login.ftl render a message. */
  private static Map<String, Object> failed() {
    Map<String, Object> expected = new LinkedHashMap<String, Object>();
    expected.put("status", "failed");
    expected.put("redirect", BASE_URL + "/login.do?authError=cognitoFailed");
    return expected;
  }

  private static CustomParameter customParameter(String key, String value, boolean active) {
    Parameter parameter = new Parameter();
    parameter.setKey(key);
    CustomParameter custom = new CustomParameter();
    custom.setParameter(parameter);
    custom.setValue(value);
    custom.setActive(active);
    return custom;
  }

  /** Stands in for the SessionMap Struts installed before the action ran: every access throws, like a stopped one. */
  @SuppressWarnings("unchecked")
  private static Map<String, Object> invalidatedSessionMap() {
    InvocationHandler handler = (proxy, method, args) -> {
      throw new IllegalStateException("session has been invalidated");
    };
    return (Map<String, Object>) Proxy.newProxyInstance(Map.class.getClassLoader(), new Class<?>[] {Map.class},
      handler);
  }

  /** A real servlet request over an attribute map, as the Cognito callback suite builds it. */
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
    return (HttpServletRequest) Proxy.newProxyInstance(OtpVerifyCodeActionTest.class.getClassLoader(),
      new Class<?>[] {HttpServletRequest.class}, handler);
  }

  private interface Answer {

    Object answer(String method, Object[] args);
  }

  private static Object unsupported(String method) {
    throw new UnsupportedOperationException("not needed by this suite: " + method);
  }

  @SuppressWarnings("unchecked")
  private static <T> T fake(Class<T> type, Answer answer) {
    InvocationHandler handler =
      (proxy, method, args) -> answer.answer(method.getName(), args == null ? new Object[0] : args);
    return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type}, handler);
  }

  private static final class TestConfig extends APConfig {

    private final String secret;
    private final String proxies;

    TestConfig(String secret, String proxies) {
      this.secret = secret;
      this.proxies = proxies;
    }

    @Override
    public String getBaseUrl() {
      return BASE_URL;
    }

    @Override
    public String getOtpHmacSecret() {
      return this.secret;
    }

    @Override
    public String getOtpTrustedProxies() {
      return this.proxies;
    }
  }

  /** A hand-written servlet request double: only the members the client address resolution may read. */
  private static final class FakeRequest implements InvocationHandler {

    private final String remoteAddr;
    private final String method;

    FakeRequest(String remoteAddr) {
      this(remoteAddr, "POST");
    }

    FakeRequest(String remoteAddr, String method) {
      this.remoteAddr = remoteAddr;
      this.method = method;
    }

    HttpServletRequest proxy() {
      return (HttpServletRequest) Proxy.newProxyInstance(HttpServletRequest.class.getClassLoader(),
        new Class<?>[] {HttpServletRequest.class}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
      if ("getMethod".equals(method.getName())) {
        return this.method;
      }
      if ("getRemoteAddr".equals(method.getName())) {
        return this.remoteAddr;
      }
      if ("getHeaders".equals(method.getName()) && "X-Forwarded-For".equals(args[0])) {
        Enumeration<String> lines = Collections.enumeration(Collections.<String>emptyList());
        return lines;
      }
      if ("getHeader".equals(method.getName()) && "Referer".equals(args[0])) {
        return null;
      }
      throw new UnsupportedOperationException(method.getName());
    }
  }

  /** What a user-manager write received: the id and the agree_terms value at the moment of the call. */
  private static final class UserWrite {

    final Long userId;
    final Boolean agreeTermsAtCall;

    UserWrite(Long userId, Boolean agreeTermsAtCall) {
      this.userId = userId;
      this.agreeTermsAtCall = agreeTermsAtCall;
    }
  }

  /** One row of the fake challenge table. */
  private static final class Row {

    final String nonce;
    final String emailHmac;
    final long globalUnitId;
    final String codeHmac;
    Instant expiresAt;
    int attempts;
    Instant consumedAt;

    Row(String nonce, String emailHmac, long globalUnitId, String codeHmac, Instant expiresAt) {
      this.nonce = nonce;
      this.emailHmac = emailHmac;
      this.globalUnitId = globalUnitId;
      this.codeHmac = codeHmac;
      this.expiresAt = expiresAt;
    }

    /** A detached copy, as {@code findByNonce} returns. */
    OtpChallenge copy() {
      OtpChallenge copy = new OtpChallenge();
      copy.setNonce(this.nonce);
      copy.setEmailHmac(this.emailHmac);
      copy.setGlobalUnitId(Long.valueOf(this.globalUnitId));
      copy.setCodeHmac(this.codeHmac);
      copy.setExpiresAt(this.expiresAt);
      copy.setAttempts(this.attempts);
      copy.setConsumedAt(this.consumedAt);
      return copy;
    }
  }

  /** What the mailer of the real request action was handed. */
  private static final class Mail {

    final String recipient;
    final String code;

    Mail(String recipient, String code) {
      this.recipient = recipient;
      this.code = code;
    }
  }

  /** Records every call it receives, in order, and fails the way the stateful store is told to. */
  private static final class RecordingRealm extends APCustomRealm {

    private final List<String> events;
    private AuthenticationException rejection;

    RecordingRealm(List<String> events) {
      super(fake(Authenticator.class, (method, args) -> {
        throw new AssertionError("the realm must do no I/O on the OTP path: " + method);
      }), fake(Authenticator.class, (method, args) -> {
        throw new AssertionError("the realm must do no I/O on the OTP path: " + method);
      }), fake(UserManager.class, (method, args) -> {
        throw new AssertionError("the realm must do no I/O on the OTP path: " + method);
      }), new APConfig());
      this.events = events;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
      this.events.add("realm:" + (token instanceof OtpAuthenticationToken
        ? String.valueOf(((OtpAuthenticationToken) token).getUserId()) : token.getClass().getSimpleName()));
      if (this.rejection != null) {
        throw this.rejection;
      }
      return super.doGetAuthenticationInfo(token);
    }
  }

  /** A user whose reads of {@code is_cgiar_user} are counted. */
  private static final class CountingUser extends User {

    private static final long serialVersionUID = 1L;

    int cgiarReads;

    @Override
    public boolean isCgiarUser() {
      this.cgiarReads++;
      return super.isCgiarUser();
    }
  }

  /** Overrides only what needs a live Struts container; the tail, the session and the realm stay real. */
  private static final class TestableOtpVerifyCodeAction extends OtpVerifyCodeAction {

    private static final long serialVersionUID = 1L;

    TestableOtpVerifyCodeAction(APConfig config, UserManager userManager, GlobalUnitManager globalUnitManager,
      CrpUserManager crpUserManager, CustomParameterManager customParameterManager,
      ParameterManager parameterManager, OtpRateLimiter limiter, OtpCodes codes, OtpEligibility eligibility,
      OtpChallengeManager challengeManager, ClientIpResolver ipResolver, Clock clock) {
      super(config, userManager, globalUnitManager, crpUserManager, customParameterManager, parameterManager,
        limiter, codes, eligibility, challengeManager, ipResolver, clock);
    }

    @Override
    public String getText(String aTextName) {
      return aTextName;
    }

    @Override
    public boolean isVisibleTopGUList() {
      return true;
    }
  }

  /** The state the fakes answer from, the calls they recorded in order, and the failures they can be told to raise. */
  private final class World {

    String secret = SECRET;
    Clock actionClock = CLOCK;
    Clock storeClock = CLOCK;
    Map<String, Object> sessionView = new HashMap<String, Object>();

    final Map<String, User> users = new HashMap<String, User>();
    final Map<Long, GlobalUnit> units = new HashMap<Long, GlobalUnit>();
    /** The raw effective value of system_otp_login_active and of system_otp_excluded_email_domains, by unit. */
    final Map<Long, String> switches = new HashMap<Long, String>();
    final Map<Long, String> exclusions = new HashMap<Long, String>();
    /** The raw effective value of system_password_login_active, by unit; an absent unit reads as "" like the store. */
    final Map<Long, String> passwordSwitches = new HashMap<Long, String>();
    /** The unit's own custom_parameters row for cognito_auth_active, by unit id; a unit with no entry has none. */
    final Map<Long, String> cognitoOverrides = new HashMap<Long, String>();
    final Set<String> members = new HashSet<String>();
    /** Rows with is_active=0: seen by the activity-blind existCrpUser, never by existActiveCrpUser. */
    final Set<String> softDeleted = new HashSet<String>();
    final List<CustomParameter> customParameters = new ArrayList<CustomParameter>();

    final List<String> events = new ArrayList<String>();
    final List<UserWrite> userWrites = new ArrayList<UserWrite>();
    final List<Mail> mails = new ArrayList<Mail>();
    final Map<String, Integer> hitAnswers = new HashMap<String, Integer>();
    final List<Boolean> memberAnswers = new ArrayList<Boolean>();
    final Store store = new Store();
    final RecordingRealm realm = new RecordingRealm(this.events);
    /** Where this world's log lines start in the shared appender: a test may build several worlds. */
    final int logStart = OtpVerifyCodeActionTest.this.appender.list.size();

    RuntimeException rateStoreFailure;
    Throwable userLookupFailure;
    boolean userVanishesAfterTheFirstLookup;
    boolean unitVanishesAfterTheFirstRead;
    private int userLookups;
    private int unitReads;

    String lastResult;
    /** The HTTP method of the request double. The default is the only one the action serves (T22). */
    String requestMethod = "POST";
    boolean noServletRequest;

    World() {
      ThreadContext.remove();
      SecurityUtils.setSecurityManager(new DefaultSecurityManager(this.realm));
    }

    World withFixtures(State state) {
      GlobalUnit unit = new GlobalUnit();
      unit.setId(Long.valueOf(GU));
      unit.setAcronym(GU_ACRONYM);
      GlobalUnitType type = new GlobalUnitType();
      type.setId(Long.valueOf(GU_TYPE));
      unit.setGlobalUnitType(type);
      this.units.put(Long.valueOf(GU), unit);
      this.switches.put(Long.valueOf(GU), "true");
      this.exclusions.put(Long.valueOf(GU), "");
      this.passwordSwitches.put(Long.valueOf(GU), "true");
      switch (state) {
        case ELIGIBLE:
          this.addUser(state, true, false);
          this.members.add(state.userId + ":" + GU);
          break;
        case UNKNOWN:
          break;
        case INACTIVE:
          this.addUser(state, false, false);
          this.members.add(state.userId + ":" + GU);
          break;
        case NON_MEMBER:
          this.addUser(state, true, false);
          break;
        case SOFT_DELETED:
          this.addUser(state, true, false);
          this.softDeleted.add(state.userId + ":" + GU);
          break;
        default:
          throw new IllegalArgumentException(state.toString());
      }
      return this;
    }

    private void addUser(State state, boolean active, boolean cgiar) {
      User user = new CountingUser();
      user.setId(Long.valueOf(state.userId));
      user.setEmail(state.email);
      user.setActive(active);
      user.setCgiarUser(cgiar);
      this.users.put(state.email, user);
    }

    /** A challenge row and the matching pending value in the session, for the given account state. */
    Row open(State state, String nonce, String codeHmac) {
      return this.open(state, nonce, codeHmac, null, true);
    }

    Row open(State state, String nonce, String codeHmac, String returnUrl, boolean agree) {
      Row row = new Row(nonce, ZERO_MAC, GU, codeHmac, NOW.plus(TTL));
      this.store.rows.put(nonce, row);
      this.seedPending(state.email, nonce, returnUrl, agree);
      return row;
    }

    void seedPending(String email, String nonce, String returnUrl, boolean agree) {
      SecurityUtils.getSubject().getSession().setAttribute(APConstants.OTP_PENDING_CHALLENGE,
        new OtpPending(nonce, email, GU, returnUrl, agree));
    }

    int count(String prefix) {
      int count = 0;
      for (String event : this.events) {
        if (event.startsWith(prefix)) {
          count++;
        }
      }
      return count;
    }

    /** How many times any user of this world had {@code isCgiarUser()} read. */
    int cgiarReads() {
      int reads = 0;
      for (User user : this.users.values()) {
        reads += ((CountingUser) user).cgiarReads;
      }
      return reads;
    }

    /** A code that is not the row's code: the first of 000000.. whose MAC differs, so a draw can never collide. */
    String aWrongCodeFor(Row row) {
      OtpCodes codes = new OtpCodes(new OtpKeys(new TestConfig(SECRET, "")));
      for (int candidate = 0; candidate < 10; candidate++) {
        String code = String.format(Locale.ROOT, "%06d", Integer.valueOf(candidate));
        if (!codes.matches(row.codeHmac, row.nonce, code)) {
          return code;
        }
      }
      throw new AssertionError("ten different codes cannot all match one MAC");
    }

    List<String> lines(String prefix) {
      List<String> lines = new ArrayList<String>();
      List<ILoggingEvent> all = OtpVerifyCodeActionTest.this.appender.list;
      for (ILoggingEvent event : all.subList(this.logStart, all.size())) {
        if (event.getFormattedMessage().startsWith(prefix)) {
          lines.add(event.getFormattedMessage());
        }
      }
      return lines;
    }

    List<String> verifyLines() {
      return this.lines("auth.otp.verify ");
    }

    /** The outcome words of the verify lines, in order. */
    List<String> verifyOutcomes() {
      List<String> outcomes = new ArrayList<String>();
      for (String line : this.verifyLines()) {
        outcomes.add(line.split(" ")[1].substring("outcome=".length()));
      }
      return outcomes;
    }

    void assertNoLeak(String... secrets) {
      for (ILoggingEvent event : OtpVerifyCodeActionTest.this.appender.list) {
        for (String secretValue : secrets) {
          assertFalse("a log line carries " + secretValue + ": " + event.getFormattedMessage(),
            event.getFormattedMessage().contains(secretValue));
          assertNull("no stack trace may carry the message of a store exception", event.getThrowableProxy());
        }
      }
    }

    TestableOtpVerifyCodeAction call(String code) {
      TestableOtpVerifyCodeAction action = this.newAction();
      action.setCode(code);
      try {
        this.lastResult = action.execute();
      } catch (RuntimeException e) {
        throw new AssertionError("execute() must not throw: " + e);
      }
      return action;
    }

    /** The real request action, over the same store, so a code of the round trip is the one it mailed. */
    OtpRequestCodeAction requestCode(String email) {
      APConfig config = new TestConfig(this.secret, "");
      OtpKeys keys = new OtpKeys(config);
      OtpChallengeManager challengeManager = this.store;
      GlobalUnitManager globalUnitManager = this.globalUnitManager();
      OtpEligibility eligibility = new OtpEligibility(keys, this.userManager(), globalUnitManager,
        this.crpUserManager(), challengeManager, this.customParameterManager(), this.parameterManager());
      OtpRequestCodeAction action = new OtpRequestCodeAction(config, keys,
        new OtpRateLimiter(keys, this.rateLimitManager()), new OtpCodes(keys), eligibility, challengeManager,
        globalUnitManager, this.customParameterManager(),
        (texts, assetsUrl, recipient, code, guAcronym, unitId) -> this.mails.add(new Mail(recipient, code)),
        new ClientIpResolver(""), CLOCK);
      action.setServletRequest(new FakeRequest(CLIENT_IP).proxy());
      action.setEmail(email);
      action.setGlobalUnitId(String.valueOf(GU));
      action.setAgree("true");
      action.execute();
      assertEquals("the request answers sent in every state", "sent", action.getResponse().get("status"));
      return action;
    }

    TestableOtpVerifyCodeAction newAction() {
      APConfig config = new TestConfig(this.secret, "");
      OtpKeys keys = new OtpKeys(config);
      GlobalUnitManager globalUnitManager = this.globalUnitManager();
      UserManager userManager = this.userManager();
      CrpUserManager crpUserManager = this.crpUserManager();
      CustomParameterManager customParameterManager = this.customParameterManager();
      ParameterManager parameterManager = this.parameterManager();
      OtpEligibility eligibility = new OtpEligibility(keys, userManager, globalUnitManager, crpUserManager,
        this.store, customParameterManager, parameterManager);
      TestableOtpVerifyCodeAction action = new TestableOtpVerifyCodeAction(config, userManager, globalUnitManager,
        crpUserManager, customParameterManager, parameterManager, new OtpRateLimiter(keys, this.rateLimitManager()),
        new OtpCodes(keys), eligibility, this.store, new ClientIpResolver(""), this.actionClock);
      action.setSession(this.sessionView);
      if (!this.noServletRequest) {
        action.setServletRequest(new FakeRequest(CLIENT_IP, this.requestMethod).proxy());
      }
      return action;
    }

    OtpRateLimitManager rateLimitManager() {
      return fake(OtpRateLimitManager.class, (method, args) -> {
        if ("hit".equals(method)) {
          this.events.add("hit:" + args[0]);
          if (this.rateStoreFailure != null) {
            throw this.rateStoreFailure;
          }
          Integer answer = this.hitAnswers.get(args[0]);
          return answer == null ? Integer.valueOf(1) : answer;
        }
        if ("acceptCooldown".equals(method)) {
          this.events.add("cooldown:" + args[0]);
          return Boolean.TRUE;
        }
        if ("purgeWindowsBefore".equals(method) || "purgeCooldownsBefore".equals(method)) {
          return Integer.valueOf(0);
        }
        return unsupported(method);
      });
    }

    UserManager userManager() {
      return fake(UserManager.class, (method, args) -> {
        if ("getUserByEmail".equals(method)) {
          this.events.add("user:" + args[0]);
          if (this.userLookupFailure instanceof Error) {
            throw (Error) this.userLookupFailure;
          }
          if (this.userLookupFailure instanceof RuntimeException) {
            throw (RuntimeException) this.userLookupFailure;
          }
          this.userLookups++;
          if (this.userVanishesAfterTheFirstLookup && this.userLookups > 1) {
            return null;
          }
          return this.users.get(args[0]);
        }
        if ("getUser".equals(method)) {
          this.events.add("getUser:" + args[0]);
          for (User user : this.users.values()) {
            if (user.getId().equals(args[0])) {
              return user;
            }
          }
          return null;
        }
        if ("saveLastLogin".equals(method)) {
          User user = (User) args[0];
          this.events.add("saveLastLogin:" + user.getId());
          this.userWrites.add(new UserWrite(user.getId(), user.getAgreeTerms()));
          return Boolean.TRUE;
        }
        if ("saveUser".equals(method)) {
          throw new AssertionError("users.agree_terms must be written through saveLastLogin, not saveUser");
        }
        return unsupported(method);
      });
    }

    GlobalUnitManager globalUnitManager() {
      return fake(GlobalUnitManager.class, (method, args) -> {
        if ("getGlobalUnitById".equals(method)) {
          this.events.add("unit");
          this.unitReads++;
          if (this.unitVanishesAfterTheFirstRead && this.unitReads > 1) {
            return null;
          }
          return this.units.get(args[0]);
        }
        return unsupported(method);
      });
    }

    CrpUserManager crpUserManager() {
      return fake(CrpUserManager.class, (method, args) -> {
        if ("existActiveCrpUser".equals(method) && args.length == 2) {
          this.events.add("member");
          return Boolean.valueOf(this.members.contains(args[0] + ":" + args[1]));
        }
        if ("existCrpUser".equals(method) && args.length == 2) {
          // The old, activity-blind gate: it sees the soft-deleted row. Answering it makes a regression visible.
          this.events.add("member-any-state");
          if (!this.memberAnswers.isEmpty()) {
            return this.memberAnswers.remove(0);
          }
          String pair = args[0] + ":" + args[1];
          return Boolean.valueOf(this.members.contains(pair) || this.softDeleted.contains(pair));
        }
        return unsupported(method);
      });
    }

    CustomParameterManager customParameterManager() {
      return fake(CustomParameterManager.class, (method, args) -> {
        if ("getCustomParameterByParameterKeyAndGlobalUnitId".equals(method)
          && APConstants.CRP_CUSTOM_FILE.equals(args[0])) {
          // The real request action of the round trip reads the unit's custom i18n file (T20).
          return null;
        }
        if ("getCustomParameterByParameterKeyAndGlobalUnitId".equals(method) && COGNITO_KEY.equals(args[0])) {
          // Answered and counted: the predicate reads the Cognito flag only while the unit's password switch is off
          // (CHG-PWD-SWITCH-001 DD-4), and a read on a password-on unit is a failure the tests look for.
          this.events.add("cognito-custom:" + args[1]);
          String value = this.cognitoOverrides.get(args[1]);
          if (value == null) {
            return null;
          }
          CustomParameter row = new CustomParameter();
          row.setValue(value);
          row.setActive(true);
          return row;
        }
        if ("getAllCustomParametersByGlobalUnitId".equals(method)) {
          return this.customParameters;
        }
        return unsupported(method);
      });
    }

    ParameterManager parameterManager() {
      return fake(ParameterManager.class, (method, args) -> {
        if ("getParameterByKey".equals(method)) {
          if (COGNITO_KEY.equals(args[0])) {
            // The catalog is keyed by the unit TYPE and holds no row for it here.
            this.events.add("cognito-catalog:" + args[1]);
          }
          return null;
        }
        return unsupported(method);
      });
    }

    /**
     * The challenge table as a stateful fake that follows the documented contract of {@link OtpChallengeManager}:
     * the conditional consume, the conditional increment with its re-read, the supersede on issue. The hooks run
     * just before a write, so a test can let a concurrent request win the race.
     */
    final class Store implements OtpChallengeManager {

      final Map<String, Row> rows = new LinkedHashMap<String, Row>();
      final List<String> calls = new ArrayList<String>();
      Runnable beforeConsume;
      Runnable beforeRegisterMismatch;
      RuntimeException findFailure;
      RuntimeException consumeFailure;
      RuntimeException registerFailure;

      Row row(String nonce) {
        return this.rows.get(nonce);
      }

      Row onlyRow() {
        assertEquals("exactly one row", 1, this.rows.size());
        return this.rows.values().iterator().next();
      }

      private void record(String event) {
        this.calls.add(event);
        World.this.events.add(event);
      }

      @Override
      public Map<Long, Map<String, String>> specificityValuesFor(Collection<String> keys,
        Collection<Long> globalUnitIds) {
        World.this.events.add("specificity");
        Map<Long, Map<String, String>> answer = new HashMap<Long, Map<String, String>>();
        for (Long id : globalUnitIds) {
          String switchValue = World.this.switches.get(id);
          String exclusion = World.this.exclusions.get(id);
          Map<String, String> values = new HashMap<String, String>();
          values.put("system_otp_login_active", switchValue == null ? "" : switchValue);
          values.put("system_otp_excluded_email_domains", exclusion == null ? "" : exclusion);
          String passwordValue = World.this.passwordSwitches.get(id);
          values.put("system_password_login_active", passwordValue == null ? "" : passwordValue);
          answer.put(id, values);
        }
        return answer;
      }

      @Override
      public boolean consume(String nonce) {
        this.record("consume");
        if (this.beforeConsume != null) {
          this.beforeConsume.run();
        }
        if (this.consumeFailure != null) {
          throw this.consumeFailure;
        }
        Instant now = World.this.storeClock.instant();
        Row row = this.rows.get(nonce);
        if (row != null && row.consumedAt == null && row.attempts < OtpChallenge.MAX_ATTEMPTS
          && row.expiresAt.isAfter(now)) {
          row.consumedAt = now;
          return true;
        }
        return false;
      }

      @Override
      public OtpChallenge findByNonce(String nonce) {
        this.record("find:" + nonce);
        if (this.findFailure != null) {
          throw this.findFailure;
        }
        Row row = this.rows.get(nonce);
        return row == null ? null : row.copy();
      }

      @Override
      public void issue(String emailHmac, long globalUnitId, String nonce, String codeHmac, Instant expiresAt) {
        World.this.events.add("issue");
        Instant now = World.this.storeClock.instant();
        for (Row open : this.rows.values()) {
          if (open.emailHmac.equals(emailHmac) && open.consumedAt == null) {
            open.consumedAt = now;
          }
        }
        this.rows.put(nonce, new Row(nonce, emailHmac, globalUnitId, codeHmac, expiresAt));
      }

      @Override
      public int purgeExpired(Instant olderThan) {
        World.this.events.add("purge");
        return 0;
      }

      @Override
      public MismatchOutcome registerMismatch(String nonce) {
        this.record("registerMismatch");
        if (this.beforeRegisterMismatch != null) {
          this.beforeRegisterMismatch.run();
        }
        if (this.registerFailure != null) {
          throw this.registerFailure;
        }
        Row row = this.rows.get(nonce);
        if (row != null && row.consumedAt == null && row.attempts < OtpChallenge.MAX_ATTEMPTS) {
          row.attempts++;
          return row.attempts >= OtpChallenge.MAX_ATTEMPTS ? MismatchOutcome.EXHAUSTED : MismatchOutcome.MISMATCH;
        }
        if (row == null || row.consumedAt != null) {
          return MismatchOutcome.INVALID;
        }
        return row.attempts >= OtpChallenge.MAX_ATTEMPTS ? MismatchOutcome.EXHAUSTED : MismatchOutcome.INVALID;
      }
    }
  }
}
