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
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.otp.ClientIpResolver;
import org.cgiar.ccafs.marlo.security.otp.OtpCodes;
import org.cgiar.ccafs.marlo.security.otp.OtpEligibility;
import org.cgiar.ccafs.marlo.security.otp.OtpKeys;
import org.cgiar.ccafs.marlo.security.otp.OtpPending;
import org.cgiar.ccafs.marlo.security.otp.OtpRateLimiter;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.OtpMailSender;
import org.cgiar.ccafs.marlo.utils.UnitTextProvider;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.TextProvider;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.util.ThreadContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * CHG-OTP-LOGIN-001-T10: {@link OtpRequestCodeAction} (FN-001, FN-004 to FN-008, SEC-006, SEC-008, SEC-010, OPS-001,
 * NF-001) against hand-written fakes; there is no mocking framework (P-17).
 * <p>
 * The managers, the mailer and the servlet request are fakes. The limiter, the code helper, the keys and the
 * eligibility predicate are the real classes over those fakes, so the neutrality tests run the real account rules:
 * unknown, inactive, non-member and soft-deleted accounts are made by changing one fact of an eligible world. The
 * expected values are literals from the requirements and the design: the status words of design 4, and the MACs and
 * bucket names, which were computed once with an independent implementation (Python {@code hmac}) from the secret
 * below and the formulas of design 3 and 5.3, never with the code under test.
 * <p>
 * Every {@code response} asserted here is the map the action produced. A fake that discards its arguments proves
 * nothing, so every fake records what it received and the tests assert the arguments.
 */
public class OtpRequestCodeActionTest {

  /** base64 of the 32 bytes 0x00..0x1f. */
  private static final String SECRET = "AAECAwQFBgcICQoLDA0ODxAREhMUFRYXGBkaGxwdHh8=";

  private static final long GU = 7L;
  private static final long GU_TYPE = 3L;
  private static final String GU_ACRONYM = "TESTCRP";
  private static final String COGNITO_KEY = "cognito_auth_active";
  private static final String BASE_URL = "https://marlo.example.org";
  private static final String CLIENT_IP = "203.0.113.7";
  private static final Instant NOW = Instant.parse("2026-10-13T12:00:00Z");
  private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);

  // HMAC-SHA256(k_email, normalised email), k_email = HMAC(secret, "otp-email\0"). Python hmac, not the action.
  private static final String EMAIL_HMAC_ANA = "b95216761a28c0d71900890f26c9a0cbd970dccc5343061e493d52bfafab79f1";
  private static final String EMAIL_HMAC_ALB = "98ff87c51fb159ebce5ea350eceaa3a7fb17b8d22de015c96da5cdc1a6528328";
  private static final String EMAIL_HMAC_AMY = "4e4b8e8b16eb50284f251b615430dea6586121f2b533a06db58aa1c0b41008c4";
  private static final String EMAIL_HMAC_ANN = "86eaca754fc8e94b2adda3a20f8352a441cbcb82cd7d20e118c1a70b790e2495";

  // HMAC-SHA256(k_bucket, "<route>|<dimension>|<value>"), k_bucket = HMAC(secret, "otp-bucket\0").
  private static final String BUCKET_EMAIL_ANA = "8e064215410d546342699cb24ea505835a264e01a111da2d1c380fd383d13823";
  private static final String BUCKET_IP_CLIENT = "31a2de6fe71d81299f2cdcf33bc85f32bd0dd15262fcff3a82eec4d09009b06e";
  private static final String BUCKET_COOLDOWN_ANA = "805d892daa0333f8e59471f70c5e019bcd72296a492756f3ae847446c8756f9c";
  private static final String BUCKET_IP_UNKNOWN = "2c2d08d76ec6ee5b498c760f8394ac9d22ea9ed23707fecec95a72cf89ea165c";
  private static final String BUCKET_EMAIL_EMPTY = "456704cc60442479d9be951c1622cd0c692bd000e39bfd1d89b6be82b601b16a";

  private static final Pattern NONCE = Pattern.compile("[A-Za-z0-9_-]{22}");
  private static final Pattern HEX_64 = Pattern.compile("[0-9a-f]{64}");
  private static final Pattern SIX_DIGITS = Pattern.compile("[0-9]{6}");

  /** Design 11: the line of a real send, and the same line of a code issued by the password-off override. */
  private static final Pattern SENT_LINE =
    Pattern.compile("auth\\.otp\\.request outcome=sent gu=testcrp domain=partner\\.org ms=[0-9]+");
  /** Design 11 (T22): the line of a refused method, written before anything about the call is known. */
  private static final Pattern BAD_METHOD_LINE =
    Pattern.compile("auth\\.otp\\.request outcome=bad_method gu= domain= ms=[0-9]+");
  private static final Pattern SENT_VIA_LINE = Pattern.compile(SENT_LINE.pattern() + " via=password_off");

  /** The account states of FN-005, each made from the eligible world by changing one fact. */
  private enum State {
    ELIGIBLE("ana@partner.org", EMAIL_HMAC_ANA, 41L),
    UNKNOWN("alb@partner.org", EMAIL_HMAC_ALB, 42L),
    INACTIVE("amy@partner.org", EMAIL_HMAC_AMY, 43L),
    NON_MEMBER("ann@partner.org", EMAIL_HMAC_ANN, 44L),
    /** A crp_users row that exists with is_active=0: MARLO removes a member by soft delete. */
    SOFT_DELETED("ann@partner.org", EMAIL_HMAC_ANN, 44L);

    final String email;
    final String emailHmac;
    final long userId;

    State(String email, String emailHmac, long userId) {
      this.email = email;
      this.emailHmac = emailHmac;
      this.userId = userId;
    }

    /** The submitted form: padded and upper-case, so that every test also exercises the normalisation. */
    String submitted() {
      return "  " + this.email.toUpperCase(Locale.ROOT) + " ";
    }
  }

  private Logger capturedLogger;
  private Level previousLevel;
  private ListAppender<ILoggingEvent> appender;

  @Before
  public void setUp() {
    SecurityUtils.setSecurityManager(new DefaultSecurityManager());
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
  }

  // ---- the response of an eligible request ----------------------------------------------------------------

  @Test
  public void anEligibleRequestIsSentWithTheMaskedSubmittedEmailAndNothingElse() {
    World world = World.with(State.ELIGIBLE);

    OtpRequestCodeAction action = world.call(State.ELIGIBLE.submitted(), "7", "true");

    Map<String, Object> expected = new LinkedHashMap<String, Object>();
    expected.put("status", "sent");
    expected.put("destination", "a***@partner.org");
    assertEquals(expected, action.getResponse());
    assertEquals(Action.SUCCESS, world.lastResult);
  }

  @Test
  public void anEligibleRequestIssuesOneRowAndMailsTheCodeToTheNormalisedPendingEmail() {
    World world = World.with(State.ELIGIBLE);

    OtpRequestCodeAction action = world.call(State.ELIGIBLE.submitted(), "7", "true");

    assertEquals("one challenge row", 1, world.issues.size());
    Issue issue = world.issues.get(0);
    assertEquals(State.ELIGIBLE.emailHmac, issue.emailHmac);
    assertEquals(GU, issue.globalUnitId);
    assertTrue(issue.nonce, NONCE.matcher(issue.nonce).matches());
    assertTrue(issue.codeHmac, HEX_64.matcher(issue.codeHmac).matches());
    assertEquals("five minutes from the action's own clock (FN-010)", NOW.plus(Duration.ofMinutes(5)),
      issue.expiresAt);

    assertEquals("exactly one email (FN-004)", 1, world.mails.size());
    Mail mail = world.mails.get(0);
    assertEquals("sent to the normalised pending email, not the raw parameter", "ana@partner.org", mail.recipient);
    assertEquals(GU_ACRONYM, mail.guAcronym);
    assertEquals("the Global Unit id reaches the mailer for its email_logs row (OPS-005)", Long.valueOf(GU),
      mail.globalUnitId);
    assertNotSame("the email is not rendered with the request's own provider (T20)", action, mail.texts);
    assertTrue("a provider built for the unit", mail.texts instanceof UnitTextProvider);
    assertTrue(mail.code, SIX_DIGITS.matcher(mail.code).matches());
    assertTrue("the mailed code is the code whose MAC was stored",
      world.codes().matches(issue.codeHmac, issue.nonce, mail.code));
    assertEquals(mail.recipient, pending().getEmail());
  }

  // ---- FN-005: the neutral response ---------------------------------------------------------------------

  @Test
  public void everyIneligibleStateAnswersExactlyAsTheEligibleOneApartFromTheMaskedEmail() {
    Map<String, Object> eligible = World.with(State.ELIGIBLE).call(State.ELIGIBLE.submitted(), "7", "true")
      .getResponse();
    assertEquals("sent", eligible.get("status"));

    for (State state : State.values()) {
      World world = World.with(state);
      Map<String, Object> actual = world.call(state.submitted(), "7", "true").getResponse();

      assertEquals(state + ": the same keys", eligible.keySet(), actual.keySet());
      assertEquals(state + ": the same status", "sent", actual.get("status"));
      // The four emails share their first letter and domain, so the whole map must be equal.
      assertEquals(state + ": the same page state", eligible, actual);
    }
  }

  @Test
  public void theNeutralResponseEchoesTheMaskOfTheSubmittedEmailOfAnUnknownAccount() {
    World world = World.with(State.UNKNOWN);

    Map<String, Object> actual = world.call("  Bob@Partner.ORG ", "7", "true").getResponse();

    Map<String, Object> expected = new LinkedHashMap<String, Object>();
    expected.put("status", "sent");
    expected.put("destination", "b***@partner.org");
    assertEquals(expected, actual);
  }

  @Test
  public void aRowIsIssuedInEveryAccountStateAndOnlyTheEligibleOneIsMailed() {
    for (State state : State.values()) {
      World world = World.with(state);

      world.call(state.submitted(), "7", "true");

      assertEquals(state + ": a challenge row is written in every state (DD-5, JD-1, JD-2)", 1, world.issues.size());
      Issue issue = world.issues.get(0);
      assertEquals(state + ": the row is bound to the email", state.emailHmac, issue.emailHmac);
      assertEquals(state + ": the row is bound to the unit", GU, issue.globalUnitId);
      assertTrue(state + ": a nonce", NONCE.matcher(issue.nonce).matches());
      assertTrue(state + ": a code MAC", HEX_64.matcher(issue.codeHmac).matches());
      assertEquals(state + ": the same expiry", NOW.plus(Duration.ofMinutes(5)), issue.expiresAt);
      assertEquals(state + ": the mailer is called only when eligible", state == State.ELIGIBLE ? 1 : 0,
        world.mails.size());
    }
  }

  @Test
  public void everyStateLeavesTheSameKindOfPendingChallengeInTheSession() {
    for (State state : State.values()) {
      World world = World.with(state);

      world.call(state.submitted(), "7", "true");

      OtpPending pending = pending();
      assertNotNull(state + ": a pending challenge", pending);
      assertEquals(state.email, pending.getEmail());
      assertEquals(GU, pending.getGlobalUnitId());
      assertEquals(world.issues.get(0).nonce, pending.getNonce());
      assertTrue(pending.isAgree());
      ThreadContext.remove();
      SecurityUtils.setSecurityManager(new DefaultSecurityManager());
    }
  }

  @Test
  public void aResendIsAnsweredLikeAnEligibleOneForAnIneligibleAccountToo() {
    List<List<String>> statusSequences = new ArrayList<List<String>>();
    List<List<Boolean>> newRows = new ArrayList<List<Boolean>>();
    for (State state : new State[] {State.ELIGIBLE, State.UNKNOWN}) {
      World world = World.with(state);
      List<String> statuses = new ArrayList<String>();
      List<Boolean> rowWritten = new ArrayList<Boolean>();

      statuses.add((String) world.call(state.submitted(), "7", "true").getResponse().get("status"));
      rowWritten.add(world.issues.size() == 1);
      String firstNonce = pending().getNonce();

      world.cooldownAccepted = false;
      statuses.add((String) world.call(state.submitted(), "7", "true").getResponse().get("status"));
      rowWritten.add(world.issues.size() == 2);
      assertEquals(state + ": a refused resend changes nothing", firstNonce, pending().getNonce());

      world.cooldownAccepted = true;
      statuses.add((String) world.call(state.submitted(), "7", "true").getResponse().get("status"));
      rowWritten.add(world.issues.size() == 2);
      assertNotEquals(state + ": the accepted resend replaces the pending challenge", firstNonce,
        pending().getNonce());
      assertEquals(state + ": the resend supersedes through a second issue", 2, world.issues.size());
      statusSequences.add(statuses);
      newRows.add(rowWritten);
      ThreadContext.remove();
      SecurityUtils.setSecurityManager(new DefaultSecurityManager());
    }
    assertEquals(Arrays.asList("sent", "rateLimited", "sent"), statusSequences.get(0));
    assertEquals("an ineligible account's resends behave as the eligible account's", statusSequences.get(0),
      statusSequences.get(1));
    assertEquals(newRows.get(0), newRows.get(1));
  }

  @Test
  public void noResponseValueNamesTheCauseOfANeutralAnswer() {
    for (State state : State.values()) {
      World world = World.with(state);

      Map<String, Object> response = world.call(state.submitted(), "7", "true").getResponse();

      for (Object value : response.values()) {
        String text = String.valueOf(value).toLowerCase();
        assertFalse(state + ": " + text, text.contains("neutral") || text.contains("ineligible")
          || text.contains("inactive") || text.contains("member") || text.contains("unknown"));
      }
      assertEquals(2, response.size());
    }
  }

  @Test
  public void anAccountThatCannotBeEvaluatedAnswersTheNeutralResponseNotAFailure() {
    World reference = World.with(State.UNKNOWN);
    Map<String, Object> neutral = reference.call(State.ELIGIBLE.submitted(), "7", "true").getResponse();
    this.appender.list.clear();
    ThreadContext.remove();
    SecurityUtils.setSecurityManager(new DefaultSecurityManager());

    World world = World.with(State.ELIGIBLE);
    world.userLookupFailure = new NullPointerException("depends on the account, like a null global unit type");
    Map<String, Object> actual = world.call(State.ELIGIBLE.submitted(), "7", "true").getResponse();

    assertEquals("one response, whatever the predicate did (SEC-006)", neutral, actual);
    assertEquals("a decoy row is written, so later requests behave alike", 1, world.issues.size());
    assertEquals("nothing is mailed after a failed predicate", 0, world.mails.size());
    assertNotNull(pending());
    assertEquals(1, this.requestLines().size());
    assertTrue(this.requestLines().get(0), this.requestLines().get(0).contains("outcome=internal_error"));
  }

  // ---- order: the limiter first, no entity load before it ---------------------------------------------------

  @Test
  public void theLimiterCountsEveryBucketBeforeAnyUserLookupOrEntityRead() {
    World world = World.with(State.ELIGIBLE);

    world.call(State.ELIGIBLE.submitted(), "7", "true");

    assertEquals(Arrays.asList("hit:" + BUCKET_EMAIL_ANA, "hit:" + BUCKET_IP_CLIENT,
      "cooldown:" + BUCKET_COOLDOWN_ANA), world.events.subList(0, 3));
    int lastLimiterEvent = world.events.indexOf("cooldown:" + BUCKET_COOLDOWN_ANA);
    for (String read : new String[] {"specificity", "user:ana@partner.org", "unit", "member", "issue"}) {
      assertTrue(read + " must come after the limiter: " + world.events,
        world.events.indexOf(read) > lastLimiterEvent);
    }
    int specificity = world.events.indexOf("specificity");
    int lookup = world.events.indexOf("user:ana@partner.org");
    int issue = world.events.indexOf("issue");
    assertTrue(world.events.toString(), specificity < lookup);
    assertTrue(world.events.toString(), lookup < issue);
    assertTrue("mail only after the row is committed", issue < world.events.indexOf("mail"));
    assertTrue("the purge follows the issue", issue < world.events.indexOf("purge"));
  }

  @Test
  public void aRejectedAddressIsStillCountedAndNeverReachesAnyLookup() {
    World world = World.with(State.ELIGIBLE);

    // U+212A (Kelvin sign) lower-cases to ASCII k: the policy rejects it, so the email bucket value is empty.
    Map<String, Object> response = world.call("anaK@partner.org", "7", "true").getResponse();

    assertEquals(Collections.singletonMap("status", "domainNotEnabled"), response);
    assertEquals(Arrays.asList("hit:" + BUCKET_EMAIL_EMPTY, "hit:" + BUCKET_IP_CLIENT), world.events.subList(0, 2));
    assertEquals(0, world.issues.size());
    assertEquals(0, world.count("user:"));
    assertEquals(0, world.count("specificity"));
  }

  @Test
  public void theLimiterIsGivenTheResolvedClientAddressBehindATrustedProxy() {
    World world = World.with(State.ELIGIBLE);
    world.proxies = "10.0.0.0/8";
    world.remoteAddr = "10.0.0.5";
    world.forwardedLines = Arrays.asList(CLIENT_IP + ", 10.0.0.5");

    world.call(State.ELIGIBLE.submitted(), "7", "true");

    assertEquals("hit:" + BUCKET_IP_CLIENT, world.events.get(1));
  }

  @Test
  public void aRequestWithoutARemoteAddressIsCountedInTheExplicitUnknownBucketNeverNull() {
    World world = World.with(State.ELIGIBLE);
    world.remoteAddr = null;

    world.call(State.ELIGIBLE.submitted(), "7", "true");

    assertEquals("hit:" + BUCKET_IP_UNKNOWN, world.events.get(1));
  }

  // ---- T22 (V-1): the method check, design 5.1 step 0 ---------------------------------------------------------

  @Test
  public void aRequestThatIsNotAPostIsRefusedBeforeAnyStoreSessionLimiterOrMailerIsTouched() {
    // A null servlet request is the fourth case: it can never be shown to be a POST.
    for (String method : new String[] {"GET", "HEAD", "PUT", "DELETE", "OPTIONS", "", null}) {
      World world = World.with(State.ELIGIBLE);
      world.requestMethod = method;
      this.appender.list.clear();

      OtpRequestCodeAction action = world.call(State.ELIGIBLE.submitted(), "7", "true");

      String label = String.valueOf(method);
      assertEquals(label, Collections.singletonMap("status", "unavailable"), action.getResponse());
      assertEquals(label, Action.SUCCESS, world.lastResult);
      assertEquals(label + ": no limiter hit, no manager read, no mail, no purge", Collections.emptyList(),
        world.events);
      assertEquals(label, 0, world.issues.size());
      assertEquals(label, 0, world.mails.size());
      assertEquals(label, 0, world.purges.size());
      assertNull(label + ": no pending challenge written", pending());
      assertEquals(label + ": the session was not even written", Collections.emptySet(),
        new HashSet<Object>(SecurityUtils.getSubject().getSession().getAttributeKeys()));
      assertEquals(label + ": exactly one line", 1, this.requestLines().size());
      assertTrue(label + ": " + this.requestLines(),
        BAD_METHOD_LINE.matcher(this.requestLines().get(0)).matches());
      ThreadContext.remove();
      SecurityUtils.setSecurityManager(new DefaultSecurityManager());
    }
  }

  @Test
  public void aRequestWithNoServletRequestAtAllIsRefusedAsBadMethod() {
    World world = World.with(State.ELIGIBLE);
    world.noServletRequest = true;

    Map<String, Object> response = world.call(State.ELIGIBLE.submitted(), "7", "true").getResponse();

    assertEquals(Collections.singletonMap("status", "unavailable"), response);
    assertEquals(Collections.emptyList(), world.events);
    assertEquals(1, this.requestLines().size());
    assertTrue(this.requestLines().get(0), this.requestLines().get(0).contains("outcome=bad_method"));
  }

  @Test
  public void aPostIsAcceptedWhateverTheCaseOfItsMethodName() {
    for (String method : new String[] {"POST", "post", "Post"}) {
      World world = World.with(State.ELIGIBLE);
      world.requestMethod = method;

      Map<String, Object> response = world.call(State.ELIGIBLE.submitted(), "7", "true").getResponse();

      assertEquals(method, "sent", response.get("status"));
      assertEquals(method, 1, world.issues.size());
      ThreadContext.remove();
      SecurityUtils.setSecurityManager(new DefaultSecurityManager());
    }
  }

  @Test
  public void theBadMethodLineCarriesNeitherTheEmailNorTheCodeNorAnyIdentity() {
    World world = World.with(State.ELIGIBLE);
    world.requestMethod = "GET";

    world.call(State.ELIGIBLE.submitted(), "7", "true");

    assertEquals(1, this.requestLines().size());
    assertTrue(this.requestLines().get(0), BAD_METHOD_LINE.matcher(this.requestLines().get(0)).matches());
    for (ILoggingEvent event : this.appender.list) {
      String line = event.getFormattedMessage();
      assertFalse(line, line.toLowerCase(Locale.ROOT).contains("ana@partner.org"));
      assertFalse(line, line.contains("partner.org"));
      assertFalse(line, line.contains(EMAIL_HMAC_ANA));
      assertNull(line, event.getThrowableProxy());
    }
  }

  @Test
  public void anErrorThrownByTheEligibilityPredicateAnswersTheNeutralSentAndDoesNotPropagate() {
    World reference = World.with(State.UNKNOWN);
    Map<String, Object> neutral = reference.call(State.ELIGIBLE.submitted(), "7", "true").getResponse();
    this.appender.list.clear();
    ThreadContext.remove();
    SecurityUtils.setSecurityManager(new DefaultSecurityManager());

    World world = World.with(State.ELIGIBLE);
    world.userLookupError = new NoClassDefFoundError("depends on the account");
    Map<String, Object> actual = world.call(State.ELIGIBLE.submitted(), "7", "true").getResponse();

    assertEquals("the same answer as any ineligible account (SEC-006)", neutral, actual);
    assertEquals("sent", actual.get("status"));
    assertEquals("a decoy row is written, so later requests behave alike", 1, world.issues.size());
    assertEquals("nothing is mailed after a failed predicate", 0, world.mails.size());
    assertEquals(1, this.requestLines().size());
    assertTrue(this.requestLines().get(0), this.requestLines().get(0).contains("outcome=internal_error"));
  }

  @Test
  public void anErrorThatEscapesTheWholeCallStillAnswersUnavailableAndIsLoggedInternalError() {
    World world = World.with(State.ELIGIBLE);
    world.issueError = new AssertionError("boom");

    OtpRequestCodeAction action = world.call(State.ELIGIBLE.submitted(), "7", "true");

    assertEquals(Collections.singletonMap("status", "unavailable"), action.getResponse());
    assertEquals(1, this.requestLines().size());
    assertTrue(this.requestLines().get(0), this.requestLines().get(0).contains("outcome=internal_error"));
  }

  // ---- rejections ---------------------------------------------------------------------------------------

  @Test
  public void anExcludedDomainAnswersDomainNotEnabledWithNoRowNoMailAndNoSession() {
    World world = World.with(State.ELIGIBLE);
    world.exclusions.put(GU, "other.org, PARTNER.org");

    Map<String, Object> response = world.call(State.ELIGIBLE.submitted(), "7", "true").getResponse();

    assertEquals(Collections.singletonMap("status", "domainNotEnabled"), response);
    assertEquals(0, world.issues.size());
    assertEquals(0, world.mails.size());
    assertNull(pending());
    assertEquals("no account state is read for a domain that is excluded", 0, world.count("user:"));
    assertEquals("no purge either: nothing was written", 0, world.count("purge"));
  }

  @Test
  public void lookalikeDomainsAndASubdomainAreNotExcludedUnlessListedAndTheNormalisedFormIs() {
    for (String email : new String[] {"x@attackerpartner.org", "x@partner.org.evil.com", "x@sub.partner.org"}) {
      World world = World.with(State.ELIGIBLE);
      world.exclusions.put(GU, "partner.org");

      Map<String, Object> response = world.call(email, "7", "true").getResponse();

      assertEquals(email + ": not excluded, so the request goes on and the unknown account gets the neutral answer",
        "sent", response.get("status"));
      assertEquals(email, 1, world.issues.size());
      assertEquals(email + ": an unknown account is mailed nothing", 0, world.mails.size());
    }
    World excluded = World.with(State.ELIGIBLE);
    excluded.exclusions.put(GU, "partner.org");
    assertEquals("the normalised form is excluded", Collections.singletonMap("status", "domainNotEnabled"),
      excluded.call("  X@PARTNER.ORG ", "7", "true").getResponse());
    assertEquals(0, excluded.issues.size());
  }

  @Test
  public void aSwitchThatIsOffOrNoSuchUnitAnswersDomainNotEnabled() {
    for (String raw : new String[] {"false", "", "   ", null, "no"}) {
      World world = World.with(State.ELIGIBLE);
      world.switches.put(GU, raw);

      Map<String, Object> response = world.call(State.ELIGIBLE.submitted(), "7", "true").getResponse();

      assertEquals("switch <" + raw + ">", Collections.singletonMap("status", "domainNotEnabled"), response);
      assertEquals(0, world.issues.size());
      assertEquals(0, world.mails.size());
      assertNull(pending());
      assertEquals("no account state is read while the switch is off", 0, world.count("user:"));
    }
    World world = World.with(State.ELIGIBLE);
    Map<String, Object> response = world.call(State.ELIGIBLE.submitted(), "99", "true").getResponse();
    assertEquals("an unknown unit", Collections.singletonMap("status", "domainNotEnabled"), response);
    assertEquals(0, world.issues.size());
    assertEquals("the unit id reached the uncached read, with all three keys",
      "specificity:[system_otp_login_active, system_otp_excluded_email_domains, system_password_login_active]|[99]",
      world.specificityReads.get(0));
  }

  // ---- SEC-009 (amended 2026-10-15, T21): on a password-on unit being a CGIAR account plays no part ----------

  @Test
  public void aCgiarAccountIsMailedLikeAnyOtherWhenItsDomainIsNotExcluded() {
    World world = World.with(State.ELIGIBLE);
    world.users.get(State.ELIGIBLE.email).setCgiarUser(true);

    Map<String, Object> response = world.call(State.ELIGIBLE.submitted(), "7", "true").getResponse();

    assertEquals("sent", response.get("status"));
    assertEquals("one challenge row", 1, world.issues.size());
    assertEquals("one email: the CGIAR flag decides nothing", 1, world.mails.size());
    assertEquals("ana@partner.org", world.mails.get(0).recipient);
    assertEquals("password on: the Cognito flag is never resolved (DD-4)", 0, world.count("cognito"));
    assertEquals("password on: is_cgiar_user is never read", 0, world.cgiarReads());
  }

  @Test
  public void aCgiarOrgAddressIsMailedWithAnEmptyExclusionAndRefusedOnlyWhenTheUnitListsTheDomain() {
    World open = World.with(State.ELIGIBLE);
    open.addUser("staff@cgiar.org", 51L, true, true);
    open.members.add("51:" + GU);
    assertEquals("sent", open.call("staff@cgiar.org", "7", "true").getResponse().get("status"));
    assertEquals("no domain is fixed in code: cgiar.org is mailed", 1, open.mails.size());

    World excluded = World.with(State.ELIGIBLE);
    excluded.addUser("staff@cgiar.org", 51L, true, true);
    excluded.members.add("51:" + GU);
    excluded.exclusions.put(GU, "cgiar.org");
    assertEquals(Collections.singletonMap("status", "domainNotEnabled"),
      excluded.call("staff@cgiar.org", "7", "true").getResponse());
    assertEquals(0, excluded.issues.size());
    assertEquals(0, excluded.mails.size());
    assertEquals("the account is not even looked up", 0, excluded.count("user:"));
  }

  @Test
  public void aChangeOfTheSwitchOrTheExclusionAppliesToTheNextRequestOfTheSameWorld() {
    World world = World.with(State.ELIGIBLE);
    assertEquals("sent", world.call(State.ELIGIBLE.submitted(), "7", "true").getResponse().get("status"));

    world.switches.put(GU, "false");
    assertEquals("domainNotEnabled",
      world.call(State.ELIGIBLE.submitted(), "7", "true").getResponse().get("status"));

    world.switches.put(GU, "true");
    world.exclusions.put(GU, "partner.org");
    assertEquals("domainNotEnabled",
      world.call(State.ELIGIBLE.submitted(), "7", "true").getResponse().get("status"));

    world.exclusions.put(GU, "");
    assertEquals("sent", world.call(State.ELIGIBLE.submitted(), "7", "true").getResponse().get("status"));
  }

  // ---- CHG-PWD-SWITCH-001-T03: the default method (rule 3d) --------------------------------------------------

  @Test
  public void ac4WithThePasswordOffACodeIsIssuedAndMailedWhileTheCodeSwitchIsOff() {
    World world = World.with(State.ELIGIBLE);
    world.passwordSwitches.put(Long.valueOf(GU), "false");
    world.switches.put(Long.valueOf(GU), "false");

    Map<String, Object> response = world.call(State.ELIGIBLE.submitted(), "7", "true").getResponse();

    Map<String, Object> expected = new LinkedHashMap<String, Object>();
    expected.put("status", "sent");
    expected.put("destination", "a***@partner.org");
    assertEquals(expected, response);
    assertEquals("a challenge row is issued", 1, world.issues.size());
    assertEquals("the member is mailed the code", 1, world.mails.size());
    assertEquals("ana@partner.org", world.mails.get(0).recipient);
    assertTrue("the mailed code is the code whose MAC was stored",
      world.codes().matches(world.issues.get(0).codeHmac, world.issues.get(0).nonce, world.mails.get(0).code));
  }

  @Test
  public void ac5WithThePasswordOffACodeIsIssuedAndMailedWhileTheDomainIsExcluded() {
    World world = World.with(State.ELIGIBLE);
    world.passwordSwitches.put(Long.valueOf(GU), "false");
    world.exclusions.put(Long.valueOf(GU), "other.org, PARTNER.org");

    Map<String, Object> response = world.call(State.ELIGIBLE.submitted(), "7", "true").getResponse();

    assertEquals("sent", response.get("status"));
    assertEquals("a challenge row is issued", 1, world.issues.size());
    assertEquals("the member is mailed the code", 1, world.mails.size());
    assertEquals("ana@partner.org", world.mails.get(0).recipient);
  }

  @Test
  public void ac8TurningThePasswordBackOnAppliesToTheNextRequestOfTheSameWorld() {
    World world = World.with(State.ELIGIBLE);
    world.switches.put(Long.valueOf(GU), "false");
    world.passwordSwitches.put(Long.valueOf(GU), "false");
    assertEquals("sent", world.call(State.ELIGIBLE.submitted(), "7", "true").getResponse().get("status"));
    assertEquals("the override mailed a code", 1, world.mails.size());

    world.passwordSwitches.put(Long.valueOf(GU), "true");
    assertEquals("today's rule again: the code switch is off", Collections.singletonMap("status", "domainNotEnabled"),
      world.call(State.ELIGIBLE.submitted(), "7", "true").getResponse());
    assertEquals("no second row, no second mail", 1, world.mails.size());

    world.passwordSwitches.put(Long.valueOf(GU), "false");
    assertEquals("sent", world.call(State.ELIGIBLE.submitted(), "7", "true").getResponse().get("status"));
    assertEquals(2, world.mails.size());
  }

  @Test
  public void theSentLineOfACodeIssuedByThePasswordOffOverrideEndsWithViaPasswordOff() {
    // Design 11: both ways the override issues a code that the unit would not offer on its own.
    for (String ownSettings : new String[] {"code switch off", "domain excluded"}) {
      World world = World.with(State.ELIGIBLE);
      world.passwordSwitches.put(Long.valueOf(GU), "false");
      if ("code switch off".equals(ownSettings)) {
        world.switches.put(Long.valueOf(GU), "false");
      } else {
        world.exclusions.put(Long.valueOf(GU), "partner.org");
      }
      this.appender.list.clear();

      Map<String, Object> response = world.call(State.ELIGIBLE.submitted(), "7", "true").getResponse();

      assertEquals(ownSettings + ": a real code was mailed", 1, world.mails.size());
      assertEquals(ownSettings + ": one request line", 1, this.requestLines().size());
      assertTrue(ownSettings + ": " + this.requestLines(), SENT_VIA_LINE.matcher(this.requestLines().get(0)).matches());
      assertEquals(ownSettings + ": the caller is told nothing of it", new LinkedHashSet<String>(
        Arrays.asList("status", "destination")), response.keySet());
    }
  }

  @Test
  public void theSentLineOfACodeTheUnitOffersOnItsOwnHasNoVia() {
    World normal = World.with(State.ELIGIBLE);
    World passwordOffButOffered = World.with(State.ELIGIBLE);
    passwordOffButOffered.passwordSwitches.put(Long.valueOf(GU), "false");
    World cognitoOffered = World.with(State.ELIGIBLE);
    cognitoOffered.users.get(State.ELIGIBLE.email).setCgiarUser(true);
    cognitoOffered.passwordSwitches.put(Long.valueOf(GU), "false");
    cognitoOffered.cognitoOverrides.put(Long.valueOf(GU), "true");
    String[] labels = {"password on", "password off, code switch on, domain not excluded",
      "password off, a Cognito account the unit offers the code to"};
    World[] worlds = {normal, passwordOffButOffered, cognitoOffered};

    for (int i = 0; i < worlds.length; i++) {
      this.appender.list.clear();

      worlds[i].call(State.ELIGIBLE.submitted(), "7", "true");

      assertEquals(labels[i] + ": a real code was mailed", 1, worlds[i].mails.size());
      assertEquals(1, this.requestLines().size());
      assertTrue(labels[i] + ": " + this.requestLines(), SENT_LINE.matcher(this.requestLines().get(0)).matches());
    }
  }

  @Test
  public void aRequestThatIssuedNoRealCodeNeverCarriesViaUnderTheOverride() {
    // The neutral sent, the decoy row and the failed predicate are logged as today, with nothing about the override.
    for (String kind : new String[] {"unknown", "inactive", "non-member", "soft-deleted", "Cognito account",
      "failed predicate"}) {
      State state = "unknown".equals(kind) ? State.UNKNOWN : "inactive".equals(kind) ? State.INACTIVE
        : "non-member".equals(kind) ? State.NON_MEMBER : "soft-deleted".equals(kind) ? State.SOFT_DELETED
        : State.ELIGIBLE;
      World world = World.with(state);
      world.passwordSwitches.put(Long.valueOf(GU), "false");
      world.switches.put(Long.valueOf(GU), "false");
      if ("Cognito account".equals(kind)) {
        world.users.get(state.email).setCgiarUser(true);
        world.cognitoOverrides.put(Long.valueOf(GU), "true");
      }
      if ("failed predicate".equals(kind)) {
        world.userLookupFailure = new IllegalStateException("store down");
      }
      this.appender.list.clear();

      world.call(state.submitted(), "7", "true");

      assertEquals(kind + ": nothing was mailed", 0, world.mails.size());
      assertEquals(kind + ": a decoy row was issued", 1, world.issues.size());
      assertEquals(kind + ": one request line", 1, this.requestLines().size());
      String line = this.requestLines().get(0);
      assertTrue(kind + ": " + line, line.startsWith("auth.otp.request outcome="
        + ("failed predicate".equals(kind) ? "internal_error" : "neutral") + " gu=testcrp domain=partner.org ms="));
      assertFalse(kind + ": " + line, line.contains("via="));
      ThreadContext.remove();
      SecurityUtils.setSecurityManager(new DefaultSecurityManager());
    }
  }

  @Test
  public void ac6ACognitoAccountIsRefusedUnderTheOverrideWithTheNeutralSentAndADecoyRow() {
    // Scenario "Cognito account unaffected": Cognito on, password off, and the unit offers the account no code of its
    // own, either because the code switch is off or because its domain is excluded. Both refusals are checked.
    World reference = World.with(State.ELIGIBLE);
    reference.passwordSwitches.put(Long.valueOf(GU), "false");
    reference.switches.put(Long.valueOf(GU), "false");
    reference.cognitoOverrides.put(Long.valueOf(GU), "true");
    Map<String, Object> eligibleAnswer = reference.call(State.ELIGIBLE.submitted(), "7", "true").getResponse();
    assertEquals("a plain member of the same unit is mailed", 1, reference.mails.size());
    ThreadContext.remove();
    SecurityUtils.setSecurityManager(new DefaultSecurityManager());

    for (String ownCode : new String[] {"code switch off", "domain excluded"}) {
      World world = World.with(State.ELIGIBLE);
      world.users.get(State.ELIGIBLE.email).setCgiarUser(true);
      world.passwordSwitches.put(Long.valueOf(GU), "false");
      world.cognitoOverrides.put(Long.valueOf(GU), "true");
      if ("code switch off".equals(ownCode)) {
        world.switches.put(Long.valueOf(GU), "false");
      } else {
        world.exclusions.put(Long.valueOf(GU), "partner.org");
      }

      OtpRequestCodeAction action = world.call(State.ELIGIBLE.submitted(), "7", "true");

      assertEquals(ownCode + ": the same answer a member gets", eligibleAnswer, action.getResponse());
      assertEquals(ownCode + ": a decoy row is issued, so later requests behave alike", 1, world.issues.size());
      assertEquals(ownCode + ": the row is bound to the email and the unit", State.ELIGIBLE.emailHmac,
        world.issues.get(0).emailHmac);
      assertEquals(ownCode + ": nothing is mailed", 0, world.mails.size());
      assertNotNull(ownCode + ": the pending challenge is in the session", pending());
      assertEquals(ownCode, State.ELIGIBLE.email, pending().getEmail());
      assertTrue(ownCode, this.requestLines().get(this.requestLines().size() - 1).contains("outcome=neutral"));
      assertEquals(ownCode + ": the Cognito flag was read once, as for a member", 1, world.count("cognito"));
      assertEquals(ownCode + ": the same calls a member makes up to the decoy row, the Cognito read included",
        kinds(untilTheIssue(reference.events)), kinds(untilTheIssue(world.events)));
      ThreadContext.remove();
      SecurityUtils.setSecurityManager(new DefaultSecurityManager());
    }
  }

  @Test
  public void sec004OnAPasswordOffUnitEveryAccountPaysTheCognitoReadBeforeTheLookup() {
    World member = World.with(State.ELIGIBLE);
    member.passwordSwitches.put(Long.valueOf(GU), "false");
    member.cognitoOverrides.put(Long.valueOf(GU), "true");
    member.call(State.ELIGIBLE.submitted(), "7", "true");
    String[] expectedOrder = {"specificity", "specificity", "unit", "cognito-custom", "user", "member", "issue"};

    for (State state : State.values()) {
      World world = World.with(state);
      world.passwordSwitches.put(Long.valueOf(GU), "false");
      world.cognitoOverrides.put(Long.valueOf(GU), "true");

      world.call(state.submitted(), "7", "true");

      assertEquals(state + ": one Cognito read for the unit, known account or not: " + world.events, 1,
        world.count("cognito"));
      assertTrue(state + ": the flag is read for unit 7, never for the type: " + world.events,
        world.events.contains("cognito-custom:" + GU));
      assertTrue(state + ": before the lookup: " + world.events,
        world.events.indexOf("cognito-custom:" + GU) < world.events.indexOf("user:" + state.email));
      assertEquals(state + ": a row is issued in every state", 1, world.issues.size());
    }
    List<String> order = new ArrayList<String>();
    for (String kind : kinds(untilTheIssue(member.events))) {
      if (Arrays.asList(expectedOrder).contains(kind)) {
        order.add(kind);
      }
    }
    assertEquals("the order a member is served in", Arrays.asList(expectedOrder), order);
  }

  @Test
  public void everyAccountStateGetsTheNeutralAnswerWithThePasswordOffAndTheCodeSwitchOff() {
    World reference = World.with(State.ELIGIBLE);
    reference.passwordSwitches.put(Long.valueOf(GU), "false");
    reference.switches.put(Long.valueOf(GU), "false");
    Map<String, Object> eligible = reference.call(State.ELIGIBLE.submitted(), "7", "true").getResponse();
    assertEquals("sent", eligible.get("status"));

    for (State state : State.values()) {
      World world = World.with(state);
      world.passwordSwitches.put(Long.valueOf(GU), "false");
      world.switches.put(Long.valueOf(GU), "false");

      Map<String, Object> actual = world.call(state.submitted(), "7", "true").getResponse();

      assertEquals(state + ": the same page state", eligible, actual);
      assertEquals(state + ": a row in every state", 1, world.issues.size());
      assertEquals(state + ": only an eligible account is mailed", state == State.ELIGIBLE ? 1 : 0,
        world.mails.size());
    }
  }

  @Test
  public void onAPasswordOnUnitTheCognitoFlagIsNeverResolvedAndIsCgiarUserIsNeverRead() {
    // Replaces the T21 locks (design DD-4, P-16): the behaviour, with counting doubles, for every account state.
    for (State state : State.values()) {
      World world = World.with(state);
      world.cognitoOverrides.put(Long.valueOf(GU), "true");
      if (world.users.containsKey(state.email)) {
        world.users.get(state.email).setCgiarUser(true);
      }

      world.call(state.submitted(), "7", "true");

      assertEquals(state + ": zero Cognito reads: " + world.events, 0, world.count("cognito"));
      assertEquals(state + ": is_cgiar_user never read", 0, world.cgiarReads());
      assertEquals(state + ": a row in every state", 1, world.issues.size());
    }
  }

  @Test
  public void theCountingDoublesDoSeeACognitoReadAndACgiarReadOnAPasswordOffUnit() {
    World world = World.with(State.ELIGIBLE);
    world.users.get(State.ELIGIBLE.email).setCgiarUser(true);
    world.passwordSwitches.put(Long.valueOf(GU), "false");
    world.cognitoOverrides.put(Long.valueOf(GU), "true");

    world.call(State.ELIGIBLE.submitted(), "7", "true");

    assertEquals(1, world.count("cognito"));
    assertTrue("a CGIAR account of a Cognito unit has its flag read", world.cgiarReads() > 0);
  }

  @Test
  public void ac7WithoutAKeyTheOverrideSendsNothingAndReadsNothing() {
    World world = World.with(State.ELIGIBLE);
    world.passwordSwitches.put(Long.valueOf(GU), "false");
    world.switches.put(Long.valueOf(GU), "false");
    world.secret = "";

    Map<String, Object> response = world.call(State.ELIGIBLE.submitted(), "7", "true").getResponse();

    assertEquals("FN-006: the code is unavailable, the password is the way in",
      Collections.singletonMap("status", "domainNotEnabled"), response);
    assertEquals("nothing is read before the key check", Collections.emptyList(), world.events);
    assertEquals(0, world.issues.size());
    assertEquals(0, world.mails.size());
  }

  @Test
  public void anUnknownUnitIsNotRescuedByTheOverrideOfAnotherUnit() {
    World world = World.with(State.ELIGIBLE);
    world.passwordSwitches.put(Long.valueOf(GU), "false");
    world.switches.put(Long.valueOf(GU), "false");

    Map<String, Object> response = world.call(State.ELIGIBLE.submitted(), "99", "true").getResponse();

    assertEquals(Collections.singletonMap("status", "domainNotEnabled"), response);
    assertEquals(0, world.issues.size());
    assertEquals("the override of unit 7 is read for unit 99 only", 0, world.count("cognito"));
  }

  /**
   * The events up to the challenge row: what the eligibility decision and its neighbours read. Past it the eligible
   * account is mailed and the Global Unit acronym of the log line is read at a different moment, which T21 already
   * did and which has nothing to do with the Cognito read.
   */
  private static List<String> untilTheIssue(List<String> events) {
    return events.subList(0, events.indexOf("issue") + 1);
  }

  /** The kind of every event: what comes before the first colon, so two accounts' sequences can be compared. */
  private static List<String> kinds(List<String> events) {
    List<String> kinds = new ArrayList<String>();
    for (String event : events) {
      kinds.add(event.contains(":") ? event.substring(0, event.indexOf(':')) : event);
    }
    return kinds;
  }

  @Test
  public void aBadEmailShapeOrANonNumericUnitIsDomainNotEnabledAfterTheLimiterRan() {
    String longLocal = String.join("", Collections.nCopies(250, "a"));
    Object[][] cases = {{"not-an-email", "7"}, {"a@b@partner.org", "7"}, {"@partner.org", "7"}, {"ana@", "7"},
      {longLocal + "@partner.org", "7"}, {null, "7"}, {"", "7"}, {"ana@partner.org", "abc"}, {"ana@partner.org", ""},
      {"ana@partner.org", null}, {"ana@partner.org", "7.5"}, {"ana@partner.org", "-7"},
      {"ana@partner.org", "٧"}, {"a na@partner.org", "7"}};
    for (Object[] row : cases) {
      World world = World.with(State.ELIGIBLE);

      Map<String, Object> response = world.call((String) row[0], (String) row[1], "true").getResponse();

      String label = row[0] == null ? "null" : row[0].toString().length() > 30 ? "long" : row[0] + "/" + row[1];
      assertEquals(label, Collections.singletonMap("status", "domainNotEnabled"), response);
      assertTrue(label + ": the limiter ran first: " + world.events, world.events.get(0).startsWith("hit:"));
      assertEquals(label, 0, world.issues.size());
      assertEquals(label, 0, world.count("user:"));
      assertEquals(label, 0, world.count("specificity"));
    }
  }

  @Test
  public void anUnconfiguredSecretAnswersDomainNotEnabledBeforeTheLimiterAndReadsNothing() {
    for (String secret : new String[] {"", null, "c2hvcnQ=", "!!not-base64!!"}) {
      World world = World.with(State.ELIGIBLE);
      world.secret = secret;

      Map<String, Object> response = world.call(State.ELIGIBLE.submitted(), "7", "true").getResponse();

      assertEquals("secret <" + secret + ">", Collections.singletonMap("status", "domainNotEnabled"), response);
      assertEquals("not 'unavailable': the unconfigured limiter must not answer first", Collections.emptyList(),
        world.events);
      assertEquals(0, world.issues.size());
      assertEquals(0, world.mails.size());
      assertNull(pending());
    }
  }

  @Test
  public void termsNotAgreedAnswerTermsRequiredAfterTheLimiterAndTheDomainCheck() {
    for (String agree : new String[] {"false", null, "", "no", "0"}) {
      World world = World.with(State.ELIGIBLE);

      Map<String, Object> response = world.call(State.ELIGIBLE.submitted(), "7", agree).getResponse();

      assertEquals("agree <" + agree + ">", Collections.singletonMap("status", "termsRequired"), response);
      assertEquals(0, world.issues.size());
      assertEquals(0, world.mails.size());
      assertNull(pending());
      assertEquals("no account state is read before the terms are agreed", 0, world.count("user:"));
      assertTrue(world.events.toString(), world.events.get(0).startsWith("hit:"));
    }
    World disabled = World.with(State.ELIGIBLE);
    disabled.exclusions.put(GU, "partner.org");
    assertEquals("the domain is decided before the terms",
      Collections.singletonMap("status", "domainNotEnabled"),
      disabled.call(State.ELIGIBLE.submitted(), "7", "false").getResponse());
  }

  @Test
  public void anyLimitOrTheCooldownAnswersRateLimitedAndChangesNothing() {
    for (int scenario = 0; scenario < 3; scenario++) {
      World world = World.with(State.ELIGIBLE);
      int expectedEvents;
      if (scenario == 0) {
        world.hitAnswers.add(Integer.valueOf(6));
        expectedEvents = 1;
      } else if (scenario == 1) {
        world.hitAnswers.add(Integer.valueOf(1));
        world.hitAnswers.add(Integer.valueOf(21));
        expectedEvents = 2;
      } else {
        world.cooldownAccepted = false;
        expectedEvents = 3;
      }

      Map<String, Object> response = world.call(State.ELIGIBLE.submitted(), "7", "true").getResponse();

      assertEquals("scenario " + scenario, Collections.singletonMap("status", "rateLimited"), response);
      assertEquals("the first refusal ends the check: " + world.events, expectedEvents, world.events.size());
      assertEquals(0, world.issues.size());
      assertEquals(0, world.mails.size());
      assertNull(pending());
      assertEquals("nothing is read after a refusal", 0, world.count("user:") + world.count("specificity"));
    }
  }

  @Test
  public void aRefusedRequestLeavesAnExistingPendingChallengeUntouched() {
    World world = World.with(State.ELIGIBLE);
    world.call(State.ELIGIBLE.submitted(), "7", "true");
    OtpPending before = pending();
    world.cooldownAccepted = false;

    world.call(State.ELIGIBLE.submitted(), "7", "true");

    assertSame(before, pending());
    assertEquals(1, world.issues.size());
    assertEquals(1, world.mails.size());
  }

  @Test
  public void aStoreFailureOfTheLimiterAnswersUnavailableWithNoSessionWrite() {
    World world = World.with(State.ELIGIBLE);
    world.rateStoreFailure = new IllegalStateException("lock wait timeout exceeded");

    Map<String, Object> response = world.call(State.ELIGIBLE.submitted(), "7", "true").getResponse();

    assertEquals(Collections.singletonMap("status", "unavailable"), response);
    assertEquals(0, world.issues.size());
    assertEquals(0, world.mails.size());
    assertNull(pending());
  }

  @Test
  public void aFailedSpecificityReadAnswersUnavailableWithNoSessionWrite() {
    World world = World.with(State.ELIGIBLE);
    world.specificityFailure = new IllegalStateException("connection refused");

    Map<String, Object> response = world.call(State.ELIGIBLE.submitted(), "7", "true").getResponse();

    assertEquals(Collections.singletonMap("status", "unavailable"), response);
    assertEquals(0, world.issues.size());
    assertNull(pending());
  }

  @Test
  public void aFailedIssueAnswersUnavailableMailsNothingAndWritesNoSession() {
    for (State state : new State[] {State.ELIGIBLE, State.UNKNOWN}) {
      World world = World.with(state);
      world.call(state.submitted(), "7", "true");
      OtpPending before = pending();
      world.mails.clear();
      world.events.clear();
      world.issueFailure = new IllegalStateException("Duplicate entry 'NONCE-SECRET-VALUE' / deadlock");

      Map<String, Object> response = world.call(state.submitted(), "7", "true").getResponse();

      assertEquals(state.toString(), Collections.singletonMap("status", "unavailable"), response);
      assertSame(state + ": no session write", before, pending());
      assertEquals(state + ": the code of a row that was not written is never mailed", 0, world.mails.size());
      assertEquals(state + ": no purge after a failed issue", 0, world.count("purge"));
      for (ILoggingEvent event : this.appender.list) {
        assertFalse("a store exception's message can carry the nonce: " + event.getFormattedMessage(),
          event.getFormattedMessage().contains("NONCE-SECRET-VALUE"));
        assertNull("no stack trace, whose message would carry it", event.getThrowableProxy());
      }
      ThreadContext.remove();
      SecurityUtils.setSecurityManager(new DefaultSecurityManager());
    }
  }

  @Test
  public void aMissingSessionAfterTheRowWasIssuedAnswersUnavailableNotAServerError() {
    World world = World.with(State.ELIGIBLE);
    SecurityUtils.setSecurityManager(null);
    ThreadContext.remove();

    OtpRequestCodeAction action = world.call(State.ELIGIBLE.submitted(), "7", "true");

    assertEquals(Collections.singletonMap("status", "unavailable"), action.getResponse());
    assertEquals(Action.SUCCESS, world.lastResult);
    assertEquals(1, this.requestLines().size());
    assertTrue(this.requestLines().get(0), this.requestLines().get(0).contains("outcome=internal_error"));
  }

  @Test
  public void anInterruptedRequestStillCarriesAnUnavailableResponse() {
    World world = World.with(State.ELIGIBLE);

    OtpRequestCodeAction untouched = world.newAction();

    assertEquals("the global input result of the package serialises this (RJ-9)",
      Collections.singletonMap("status", "unavailable"), untouched.getResponse());
  }

  // ---- the session -----------------------------------------------------------------------------------------

  @Test
  public void thePendingChallengeReplacesThePreviousOneAndHoldsEmailUnitNonceAndReturnUrl() {
    World world = World.with(State.ELIGIBLE);
    world.referer = BASE_URL + "/projects/list.do";
    SecurityUtils.getSubject().getSession().setAttribute(APConstants.OTP_PENDING_CHALLENGE,
      new OtpPending("previous-nonce", "old@partner.org", 99L, null, false));

    world.call(State.ELIGIBLE.submitted(), "7", "true");

    OtpPending pending = pending();
    assertEquals("ana@partner.org", pending.getEmail());
    assertEquals(GU, pending.getGlobalUnitId());
    assertEquals(world.issues.get(0).nonce, pending.getNonce());
    assertNotEquals("previous-nonce", pending.getNonce());
    assertEquals(BASE_URL + "/projects/list.do", pending.getReturnUrl());
    assertTrue(pending.isAgree());
  }

  @Test
  public void anOffSiteOrAuthenticationEndpointRefererIsNotKeptAsTheReturnUrl() {
    for (String referer : new String[] {"https://evil.example.com/x.do", BASE_URL + "/cognitoLogin.do", null}) {
      World world = World.with(State.ELIGIBLE);
      world.referer = referer;

      world.call(State.ELIGIBLE.submitted(), "7", "true");

      assertNull("referer " + referer, pending().getReturnUrl());
      ThreadContext.remove();
      SecurityUtils.setSecurityManager(new DefaultSecurityManager());
    }
  }

  @Test
  public void eachRequestGetsAFreshNonceAndAnAcceptedResendReplacesTheSessionHandle() {
    World world = World.with(State.ELIGIBLE);

    world.call(State.ELIGIBLE.submitted(), "7", "true");
    String first = pending().getNonce();
    world.call(State.ELIGIBLE.submitted(), "7", "true");

    assertNotEquals(first, pending().getNonce());
    assertNotEquals(world.issues.get(0).codeHmac, world.issues.get(1).codeHmac);
    assertEquals(world.issues.get(1).nonce, pending().getNonce());
  }

  // ---- purge, mailer failures -----------------------------------------------------------------------------

  @Test
  public void aPurgeFailureStillYieldsSentAndTheSessionIsWritten() {
    World world = World.with(State.ELIGIBLE);
    world.purgeFailure = new IllegalStateException("deadlock found when trying to get lock");

    Map<String, Object> response = world.call(State.ELIGIBLE.submitted(), "7", "true").getResponse();

    assertEquals("sent", response.get("status"));
    assertEquals(Arrays.asList(NOW.minus(Duration.ofHours(1))), world.purges);
    assertEquals(1, world.mails.size());
    assertNotNull(pending());
    assertEquals(1, this.requestLines().size());
    assertTrue(this.requestLines().get(0), this.requestLines().get(0).contains("outcome=sent"));
  }

  @Test
  public void thePurgeCutoffIsOneHourBeforeNowAndRunsAfterTheIssue() {
    World world = World.with(State.UNKNOWN);

    world.call(State.UNKNOWN.submitted(), "7", "true");

    assertEquals(Arrays.asList(Instant.parse("2026-10-13T11:00:00Z")), world.purges);
    assertTrue(world.events.indexOf("issue") < world.events.indexOf("purge"));
  }

  @Test
  public void aMailerThatFailsOrThrowsAnErrorStillYieldsTheNeutralSentResponse() {
    for (Throwable failure : new Throwable[] {new IllegalStateException("smtp"), new NoClassDefFoundError("x"),
      new AssertionError("boom")}) {
      World world = World.with(State.ELIGIBLE);
      world.mailFailure = failure;

      Map<String, Object> response = world.call(State.ELIGIBLE.submitted(), "7", "true").getResponse();

      String label = failure.getClass().getSimpleName();
      assertEquals(label, "sent", response.get("status"));
      assertEquals(label, "a***@partner.org", response.get("destination"));
      assertNotNull(label, pending());
      assertEquals(label + ": the purge still ran", 1, world.purges.size());
      assertEquals(label, 1, this.requestLines().size());
      this.appender.list.clear();
      ThreadContext.remove();
      SecurityUtils.setSecurityManager(new DefaultSecurityManager());
    }
  }

  // ---- OPS-001: logging -------------------------------------------------------------------------------------

  @Test
  public void exactlyOneRequestLineIsWrittenPerCallWithTheClosedOutcome() {
    String[][] expectedOutcomes = new String[7][];
    World sent = World.with(State.ELIGIBLE);
    World neutral = World.with(State.NON_MEMBER);
    World denied = World.with(State.ELIGIBLE);
    denied.exclusions.put(GU, "partner.org");
    World terms = World.with(State.ELIGIBLE);
    World limited = World.with(State.ELIGIBLE);
    limited.cooldownAccepted = false;
    World unconfigured = World.with(State.ELIGIBLE);
    unconfigured.secret = "";
    World failed = World.with(State.ELIGIBLE);
    failed.issueFailure = new IllegalStateException("store down");
    expectedOutcomes[0] = new String[] {"sent", "gu=testcrp domain=partner.org"};
    expectedOutcomes[1] = new String[] {"neutral", "gu=testcrp domain=partner.org"};
    expectedOutcomes[2] = new String[] {"denied_domain", "gu=testcrp domain=partner.org"};
    expectedOutcomes[3] = new String[] {"terms_required", "gu=testcrp domain=partner.org"};
    expectedOutcomes[4] = new String[] {"rate_limited", "domain=partner.org"};
    expectedOutcomes[5] = new String[] {"unconfigured", "domain=partner.org"};
    expectedOutcomes[6] = new String[] {"internal_error", "gu=testcrp domain=partner.org"};
    World[] worlds = {sent, neutral, denied, terms, limited, unconfigured, failed};
    String[] agree = {"true", "true", "true", "false", "true", "true", "true"};
    for (int i = 0; i < worlds.length; i++) {
      this.appender.list.clear();
      State state = worlds[i] == neutral ? State.NON_MEMBER : State.ELIGIBLE;

      worlds[i].call(state.submitted(), "7", agree[i]);

      List<String> lines = this.requestLines();
      assertEquals(expectedOutcomes[i][0] + ": exactly one request line per call", 1, lines.size());
      assertTrue(lines.get(0), lines.get(0).startsWith("auth.otp.request outcome=" + expectedOutcomes[i][0] + " "));
      assertTrue(lines.get(0), lines.get(0).contains(expectedOutcomes[i][1]));
      ThreadContext.remove();
      SecurityUtils.setSecurityManager(new DefaultSecurityManager());
    }
  }

  @Test
  public void noLogLineCarriesTheCodeTheNonceTheMacsOrTheFullEmail() {
    World world = World.with(State.ELIGIBLE);

    world.call(State.ELIGIBLE.submitted(), "7", "true");

    assertFalse(this.appender.list.isEmpty());
    String nonce = world.issues.get(0).nonce;
    String code = world.mails.get(0).code;
    for (ILoggingEvent event : this.appender.list) {
      String line = event.getFormattedMessage();
      assertFalse(line, line.contains(code));
      assertFalse(line, line.contains(nonce));
      assertFalse(line, line.contains(world.issues.get(0).codeHmac));
      assertFalse(line, line.contains(EMAIL_HMAC_ANA));
      assertFalse(line, line.toLowerCase().contains("ana@partner.org"));
    }
  }

  // ---- the shipped artifacts ------------------------------------------------------------------------------

  @Test
  public void theChallengeLifetimeIsTheFiveMinutesTheEmailStates() throws Exception {
    Field emailMinutes = OtpMailSender.class.getDeclaredField("CODE_VALIDITY_MINUTES");
    emailMinutes.setAccessible(true);

    assertEquals("FN-010", 5, emailMinutes.getInt(null));
    assertEquals("the email copy and the real expiry cannot drift", Duration.ofMinutes(emailMinutes.getInt(null)),
      OtpRequestCodeAction.CODE_VALIDITY);
  }

  @Test
  public void theContainerBuildsTheActionThroughOneInjectConstructorThatTakesTheMailerAsABean() {
    List<Constructor<?>> injectable = new ArrayList<Constructor<?>>();
    for (Constructor<?> constructor : OtpRequestCodeAction.class.getDeclaredConstructors()) {
      if (constructor.isAnnotationPresent(Inject.class)) {
        injectable.add(constructor);
      }
    }
    assertEquals("exactly one injectable constructor, so the container never picks the test seam", 1,
      injectable.size());
    assertTrue(Modifier.isPublic(injectable.get(0).getModifiers()));
    assertEquals(Arrays.<Class<?>>asList(APConfig.class, OtpKeys.class, OtpRateLimiter.class, OtpCodes.class,
      OtpEligibility.class, OtpChallengeManager.class, GlobalUnitManager.class, CustomParameterManager.class,
      OtpMailSender.class),
      Arrays.asList(injectable.get(0).getParameterTypes()));
  }

  @Test
  public void theActionNeverConstructsTheMailerAndNeverOverridesValidate() throws Exception {
    String source = new String(Files.readAllBytes(
      Paths.get("src/main/java/org/cgiar/ccafs/marlo/action/home/OtpRequestCodeAction.java")),
      StandardCharsets.UTF_8);
    String code = source.replaceAll("(?s)/\\*.*?\\*/", "").replaceAll("//[^\n]*", "");

    assertFalse("the mailer is a container singleton: inject it, never new it", code.contains("new OtpMailSender"));
    for (Method method : OtpRequestCodeAction.class.getDeclaredMethods()) {
      assertNotEquals("no transactional manager call may run in validate(): everything runs in execute()",
        "validate", method.getName());
    }
  }

  // ---- the four shared helpers are container singletons ---------------------------------------------------

  @Test
  public void theFourSharedHelpersAreNamedBeansWithExactlyOnePublicInjectConstructorEach() {
    Object[][] expected = {
      {OtpKeys.class, new Class<?>[] {APConfig.class}},
      {OtpRateLimiter.class, new Class<?>[] {OtpKeys.class, OtpRateLimitManager.class}},
      {OtpCodes.class, new Class<?>[] {OtpKeys.class}},
      {OtpEligibility.class, new Class<?>[] {OtpKeys.class, UserManager.class, GlobalUnitManager.class,
        CrpUserManager.class, OtpChallengeManager.class, CustomParameterManager.class, ParameterManager.class}}};
    for (Object[] row : expected) {
      Class<?> type = (Class<?>) row[0];
      assertTrue(type.getSimpleName() + " is found by the component scan", type.isAnnotationPresent(Named.class));
      List<Constructor<?>> injectable = new ArrayList<Constructor<?>>();
      for (Constructor<?> constructor : type.getDeclaredConstructors()) {
        if (constructor.isAnnotationPresent(Inject.class)) {
          injectable.add(constructor);
        }
      }
      assertEquals(type.getSimpleName() + ": one injectable constructor, never the test seam", 1, injectable.size());
      assertTrue(Modifier.isPublic(injectable.get(0).getModifiers()));
      assertEquals(Arrays.asList((Class<?>[]) row[1]), Arrays.asList(injectable.get(0).getParameterTypes()));
    }
  }

  @Test
  public void springScansOneSharedSingletonOfEachHelperAndTheyShareTheSameKeys() throws Exception {
    final Set<String> wanted = new HashSet<String>(Arrays.asList(OtpKeys.class.getName(),
      OtpRateLimiter.class.getName(), OtpCodes.class.getName(), OtpEligibility.class.getName()));
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
    ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(context);
    // The real scanner, over the real package, narrowed to these four classes.
    scanner.addExcludeFilter(new TypeFilter() {

      @Override
      public boolean match(MetadataReader reader, MetadataReaderFactory factory) {
        return !wanted.contains(reader.getClassMetadata().getClassName());
      }
    });
    scanner.scan("org.cgiar.ccafs.marlo.security.otp");
    World world = World.with(State.ELIGIBLE);
    context.getBeanFactory().registerSingleton("apConfig", new TestConfig(SECRET, ""));
    context.getBeanFactory().registerSingleton("otpRateLimitManager", world.rateLimitManager());
    context.getBeanFactory().registerSingleton("userManager", world.userManager());
    context.getBeanFactory().registerSingleton("globalUnitManager", world.globalUnitManager());
    context.getBeanFactory().registerSingleton("crpUserManager", world.crpUserManager());
    context.getBeanFactory().registerSingleton("otpChallengeManager", world.challengeManager());
    // The predicate gets the two Cognito managers back (CHG-PWD-SWITCH-001 DD-4): the scan must find both.
    context.getBeanFactory().registerSingleton("customParameterManager", world.customParameterManager());
    context.getBeanFactory().registerSingleton("parameterManager", world.parameterManager());
    context.refresh();
    try {
      for (Class<?> type : new Class<?>[] {OtpKeys.class, OtpRateLimiter.class, OtpCodes.class,
        OtpEligibility.class}) {
        assertEquals(type.getSimpleName() + ": exactly one bean, found by @Named", 1,
          context.getBeanNamesForType(type).length);
        assertSame("a singleton, so every action shares it", context.getBean(type), context.getBean(type));
      }
      OtpKeys keys = context.getBean(OtpKeys.class);
      assertTrue("the secret reached the keys through the APConfig constructor", keys.isConfigured());
      for (Class<?> consumer : new Class<?>[] {OtpRateLimiter.class, OtpCodes.class, OtpEligibility.class}) {
        Field field = consumer.getDeclaredField("keys");
        field.setAccessible(true);
        assertSame(consumer.getSimpleName() + " holds the one OtpKeys bean", keys,
          field.get(context.getBean(consumer)));
      }
    } finally {
      context.close();
    }
  }

  // ---- T20: the text provider of the unit's custom file ----------------------------------------------------

  /** The shell argument: what the provider answers for email.layout, with a recognisable body and assets URL. */
  private static String layoutOf(TextProvider texts) {
    return texts.getText("email.layout", new String[] {"BODY", "ASSETS"});
  }

  @Test
  public void theEmailIsRenderedWithTheCustomFileOfTheSelectedUnitAndTheInstanceAssetsUrl() {
    World world = World.with(State.ELIGIBLE);
    world.customFiles.put(Long.valueOf(GU), "aicrra");

    world.call(State.ELIGIBLE.submitted(), "7", "true");

    assertEquals(1, world.mails.size());
    Mail mail = world.mails.get(0);
    String layout = layoutOf(mail.texts);
    assertTrue("the AICCRA shell: " + layout, layout.contains("marlo-logo.png") && layout.contains("BODY"));
    assertTrue("the subject is the unit's own", mail.texts.getText("login.otp.email.subject", new String[] {"MARLO"})
      .startsWith("[AICCRA]"));
    assertEquals("a key the unit does not define falls back to global", "MARLO",
      mail.texts.getText("login.otp.email.appName"));
    assertEquals("the images come from the instance's CDN or base URL", BASE_URL, mail.assetsUrl);
  }

  @Test
  public void aUnitWithoutACustomFileGetsTheGlobalTextsAndTheBareShell() {
    World world = World.with(State.ELIGIBLE);

    world.call(State.ELIGIBLE.submitted(), "7", "true");

    assertEquals("the base shell is {0}: the body as it is", "BODY", layoutOf(world.mails.get(0).texts));
  }

  @Test
  public void theCustomFileIsReadForEveryStateSoTheReadAddsWorkToNoOnePath() {
    for (State state : State.values()) {
      World world = World.with(state);
      world.customFiles.put(Long.valueOf(GU), "aicrra");

      world.call(state.submitted(), "7", "true");

      assertEquals(state + ": one read, eligible or not: " + world.events, 1, world.count("custom-file"));
      int read = world.events.indexOf("custom-file");
      assertTrue(state + ": after the eligibility check: " + world.events, read > world.events.indexOf("specificity"));
      assertTrue(state + ": before the challenge row: " + world.events, read < world.events.indexOf("issue"));
      assertEquals(state + ": mailed only when eligible", state == State.ELIGIBLE ? 1 : 0, world.mails.size());
    }
  }

  @Test
  public void theCustomFileIsNotReadWhenTheRequestIsRefusedBeforeTheSendDecision() {
    World world = World.with(State.ELIGIBLE);

    world.call(State.ELIGIBLE.submitted(), "7", "false");

    assertEquals("terms not agreed: refused before the send decision", 0, world.count("custom-file"));
  }

  @Test
  public void aFailedCustomFileReadSendsTheEmailWithTheRequestsOwnProviderAndChangesNoAnswer() {
    Map<String, Object> reference = World.with(State.ELIGIBLE).call(State.ELIGIBLE.submitted(), "7", "true")
      .getResponse();
    for (State state : new State[] {State.ELIGIBLE, State.UNKNOWN}) {
      World world = World.with(state);
      world.customFileFailure = new IllegalStateException("connection refused");

      OtpRequestCodeAction action = world.call(state.submitted(), "7", "true");

      assertEquals(state + ": the same answer", reference, action.getResponse());
      assertEquals(state + ": one read", 1, world.count("custom-file"));
      assertEquals(state + ": a challenge row for every state", 1, world.issues.size());
      assertEquals(state + ": mailed only when eligible", state == State.ELIGIBLE ? 1 : 0, world.mails.size());
      if (state == State.ELIGIBLE) {
        assertSame("the fallback is the request's own provider", action, world.mails.get(0).texts);
      }
      assertTrue(state + ": the request line is sent", this.requestLines().get(this.requestLines().size() - 1)
        .contains("outcome=" + (state == State.ELIGIBLE ? "sent" : "neutral")));
    }
  }

  @Test
  public void theSessionOfAPreSignInRequestHoldsNothingButThePendingChallenge() {
    World world = World.with(State.ELIGIBLE);
    world.customFiles.put(Long.valueOf(GU), "aicrra");

    world.call(State.ELIGIBLE.submitted(), "7", "true");

    assertEquals("the custom file is never written to the session",
      Collections.singleton(APConstants.OTP_PENDING_CHALLENGE),
      new HashSet<Object>(SecurityUtils.getSubject().getSession().getAttributeKeys()));
  }

  // ---- helpers ------------------------------------------------------------------------------------------

  private static OtpPending pending() {
    return (OtpPending) SecurityUtils.getSubject().getSession().getAttribute(APConstants.OTP_PENDING_CHALLENGE);
  }

  private List<String> requestLines() {
    List<String> lines = new ArrayList<String>();
    for (ILoggingEvent event : this.appender.list) {
      if (event.getFormattedMessage().startsWith("auth.otp.request ")) {
        lines.add(event.getFormattedMessage());
      }
    }
    return lines;
  }

  /** What the challenge manager was asked to issue. */
  private static final class Issue {

    final String emailHmac;
    final long globalUnitId;
    final String nonce;
    final String codeHmac;
    final Instant expiresAt;

    Issue(String emailHmac, long globalUnitId, String nonce, String codeHmac, Instant expiresAt) {
      this.emailHmac = emailHmac;
      this.globalUnitId = globalUnitId;
      this.nonce = nonce;
      this.codeHmac = codeHmac;
      this.expiresAt = expiresAt;
    }
  }

  /** What the mailer was handed. */
  private static final class Mail {

    final TextProvider texts;
    final String assetsUrl;
    final String recipient;
    final String code;
    final String guAcronym;
    final Long globalUnitId;

    Mail(TextProvider texts, String assetsUrl, String recipient, String code, String guAcronym, Long globalUnitId) {
      this.texts = texts;
      this.assetsUrl = assetsUrl;
      this.recipient = recipient;
      this.code = code;
      this.guAcronym = guAcronym;
      this.globalUnitId = globalUnitId;
    }
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

  /** A hand-written servlet request double: only the four members the action may read. */
  private static final class FakeRequest implements InvocationHandler {

    private final String remoteAddr;
    private final List<String> forwardedLines;
    private final String referer;
    private final String method;

    FakeRequest(String remoteAddr, List<String> forwardedLines, String referer, String method) {
      this.remoteAddr = remoteAddr;
      this.forwardedLines = forwardedLines;
      this.referer = referer;
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
        Enumeration<String> lines = Collections.enumeration(this.forwardedLines);
        return lines;
      }
      if ("getHeader".equals(method.getName()) && "Referer".equals(args[0])) {
        return this.referer;
      }
      throw new UnsupportedOperationException(method.getName());
    }
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

  /** The state the fakes answer from, the calls they recorded in order, and the failures they can be told to raise. */
  private static final class World {

    String secret = SECRET;
    String proxies = "";
    String remoteAddr = CLIENT_IP;
    List<String> forwardedLines = Collections.emptyList();
    String referer;
    boolean noServletRequest;
    /** The HTTP method of the request double. The default is the only one the action serves (T22). */
    String requestMethod = "POST";

    final Map<String, User> users = new HashMap<String, User>();
    final Map<Long, GlobalUnit> units = new HashMap<Long, GlobalUnit>();
    /** The raw effective value of system_otp_login_active and of system_otp_excluded_email_domains, by unit. */
    final Map<Long, String> switches = new HashMap<Long, String>();
    final Map<Long, String> exclusions = new HashMap<Long, String>();
    /** The raw effective value of system_password_login_active, by unit; an absent unit reads as "" like the store. */
    final Map<Long, String> passwordSwitches = new HashMap<Long, String>();
    final Set<String> members = new HashSet<String>();
    /** Rows with is_active=0: seen by the activity-blind existCrpUser, never by existActiveCrpUser. */
    final Set<String> softDeleted = new HashSet<String>();
    /** The crp_custom_file value of each Global Unit; a unit with no entry has no such parameter. */
    final Map<Long, String> customFiles = new HashMap<Long, String>();
    /** The unit's own custom_parameters row for cognito_auth_active, by unit id; a unit with no entry has none. */
    final Map<Long, String> cognitoOverrides = new HashMap<Long, String>();

    final List<String> events = new ArrayList<String>();
    final List<Issue> issues = new ArrayList<Issue>();
    final List<Mail> mails = new ArrayList<Mail>();
    final List<Instant> purges = new ArrayList<Instant>();
    final List<String> specificityReads = new ArrayList<String>();

    final List<Integer> hitAnswers = new ArrayList<Integer>();
    boolean cooldownAccepted = true;
    RuntimeException rateStoreFailure;
    RuntimeException specificityFailure;
    RuntimeException issueFailure;
    Error issueError;
    Error userLookupError;
    RuntimeException purgeFailure;
    RuntimeException userLookupFailure;
    RuntimeException customFileFailure;
    Throwable mailFailure;

    String lastResult;

    static World with(State state) {
      World world = new World();
      GlobalUnit unit = new GlobalUnit();
      unit.setId(Long.valueOf(GU));
      unit.setAcronym(GU_ACRONYM);
      GlobalUnitType type = new GlobalUnitType();
      type.setId(Long.valueOf(GU_TYPE));
      unit.setGlobalUnitType(type);
      world.units.put(Long.valueOf(GU), unit);
      world.switches.put(Long.valueOf(GU), "true");
      world.exclusions.put(Long.valueOf(GU), "");
      world.passwordSwitches.put(Long.valueOf(GU), "true");
      switch (state) {
        case ELIGIBLE:
          world.addUser(state, true, false);
          world.members.add(state.userId + ":" + GU);
          break;
        case UNKNOWN:
          break;
        case INACTIVE:
          world.addUser(state, false, false);
          world.members.add(state.userId + ":" + GU);
          break;
        case NON_MEMBER:
          world.addUser(state, true, false);
          break;
        case SOFT_DELETED:
          world.addUser(state, true, false);
          world.softDeleted.add(state.userId + ":" + GU);
          break;
        default:
          throw new IllegalArgumentException(state.toString());
      }
      return world;
    }

    private void addUser(State state, boolean active, boolean cgiar) {
      this.addUser(state.email, state.userId, active, cgiar);
    }

    void addUser(String email, long id, boolean active, boolean cgiar) {
      User user = new CountingUser();
      user.setId(Long.valueOf(id));
      user.setEmail(email);
      user.setActive(active);
      user.setCgiarUser(cgiar);
      this.users.put(email, user);
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

    OtpCodes codes() {
      return new OtpCodes(new OtpKeys(new TestConfig(SECRET, "")));
    }

    OtpRequestCodeAction call(String email, String globalUnitId, String agree) {
      OtpRequestCodeAction action = this.newAction();
      action.setEmail(email);
      action.setGlobalUnitId(globalUnitId);
      action.setAgree(agree);
      try {
        this.lastResult = action.execute();
      } catch (Exception e) {
        throw new AssertionError("execute() must not throw: " + e);
      }
      return action;
    }

    OtpRequestCodeAction newAction() {
      APConfig config = new TestConfig(this.secret, this.proxies);
      OtpKeys keys = new OtpKeys(config);
      OtpChallengeManager challengeManager = this.challengeManager();
      GlobalUnitManager globalUnitManager = this.globalUnitManager();
      OtpEligibility eligibility = new OtpEligibility(keys, this.userManager(), globalUnitManager,
        this.crpUserManager(), challengeManager, this.customParameterManager(), this.parameterManager());
      OtpRequestCodeAction action = new OtpRequestCodeAction(config, keys,
        new OtpRateLimiter(keys, this.rateLimitManager()), new OtpCodes(keys), eligibility, challengeManager,
        globalUnitManager, this.customParameterManager(), this.mailer(), new ClientIpResolver(this.proxies), CLOCK);
      if (!this.noServletRequest) {
        action.setServletRequest(
          new FakeRequest(this.remoteAddr, this.forwardedLines, this.referer, this.requestMethod).proxy());
      }
      return action;
    }

    OtpRequestCodeAction.CodeSender mailer() {
      return (texts, assetsUrl, recipient, code, guAcronym, globalUnitId) -> {
        this.events.add("mail");
        this.mails.add(new Mail(texts, assetsUrl, recipient, code, guAcronym, globalUnitId));
        if (this.mailFailure instanceof Error) {
          throw (Error) this.mailFailure;
        }
        if (this.mailFailure instanceof RuntimeException) {
          throw (RuntimeException) this.mailFailure;
        }
      };
    }

    OtpRateLimitManager rateLimitManager() {
      return fake(OtpRateLimitManager.class, (method, args) -> {
        if ("hit".equals(method)) {
          this.events.add("hit:" + args[0]);
          if (this.rateStoreFailure != null) {
            throw this.rateStoreFailure;
          }
          return this.hitAnswers.isEmpty() ? Integer.valueOf(1) : this.hitAnswers.remove(0);
        }
        if ("acceptCooldown".equals(method)) {
          this.events.add("cooldown:" + args[0]);
          return Boolean.valueOf(this.cooldownAccepted);
        }
        if ("purgeWindowsBefore".equals(method) || "purgeCooldownsBefore".equals(method)) {
          return Integer.valueOf(0);
        }
        return unsupported(method);
      });
    }

    OtpChallengeManager challengeManager() {
      return fake(OtpChallengeManager.class, (method, args) -> {
        if ("specificityValuesFor".equals(method)) {
          Collection<?> ids = (Collection<?>) args[1];
          this.events.add("specificity");
          this.specificityReads.add("specificity:" + args[0] + "|" + ids);
          if (this.specificityFailure != null) {
            throw this.specificityFailure;
          }
          Map<Long, Map<String, String>> answer = new HashMap<Long, Map<String, String>>();
          for (Object id : ids) {
            String switchValue = this.switches.get(id);
            String exclusion = this.exclusions.get(id);
            Map<String, String> values = new HashMap<String, String>();
            values.put("system_otp_login_active", switchValue == null ? "" : switchValue);
            values.put("system_otp_excluded_email_domains", exclusion == null ? "" : exclusion);
            String passwordValue = this.passwordSwitches.get(id);
            values.put("system_password_login_active", passwordValue == null ? "" : passwordValue);
            answer.put((Long) id, values);
          }
          return answer;
        }
        if ("issue".equals(method)) {
          this.events.add("issue");
          if (this.issueError != null) {
            throw this.issueError;
          }
          if (this.issueFailure != null) {
            throw this.issueFailure;
          }
          this.issues.add(new Issue((String) args[0], ((Long) args[1]).longValue(), (String) args[2],
            (String) args[3], (Instant) args[4]));
          return null;
        }
        if ("purgeExpired".equals(method)) {
          this.events.add("purge");
          this.purges.add((Instant) args[0]);
          if (this.purgeFailure != null) {
            throw this.purgeFailure;
          }
          return Integer.valueOf(0);
        }
        return unsupported(method);
      });
    }

    UserManager userManager() {
      return fake(UserManager.class, (method, args) -> {
        if ("getUserByEmail".equals(method)) {
          this.events.add("user:" + args[0]);
          if (this.userLookupError != null) {
            throw this.userLookupError;
          }
          if (this.userLookupFailure != null) {
            throw this.userLookupFailure;
          }
          return this.users.get(args[0]);
        }
        return unsupported(method);
      });
    }

    GlobalUnitManager globalUnitManager() {
      return fake(GlobalUnitManager.class, (method, args) -> {
        if ("getGlobalUnitById".equals(method)) {
          this.events.add("unit");
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
          this.events.add("custom-file");
          if (this.customFileFailure != null) {
            throw this.customFileFailure;
          }
          String file = this.customFiles.get(args[1]);
          if (file == null) {
            return null;
          }
          CustomParameter parameter = new CustomParameter();
          parameter.setValue(file);
          return parameter;
        }
        if ("getCustomParameterByParameterKeyAndGlobalUnitId".equals(method)
          && COGNITO_KEY.equals(args[0])) {
          // Answered and counted: the predicate reads the Cognito flag only while the unit's password switch is off
          // (CHG-PWD-SWITCH-001 DD-4), and a read on a password-on unit is a failure the tests look for.
          this.events.add("cognito-custom:" + args[1]);
          String value = this.cognitoOverrides.get(args[1]);
          if (value == null) {
            return null;
          }
          CustomParameter parameter = new CustomParameter();
          parameter.setValue(value);
          parameter.setActive(true);
          return parameter;
        }
        // Any other key is unsupported: a read of it would fail the test.
        return unsupported(method);
      });
    }

    ParameterManager parameterManager() {
      return fake(ParameterManager.class, (method, args) -> {
        if ("getParameterByKey".equals(method) && COGNITO_KEY.equals(args[0])) {
          // The catalog is keyed by the unit TYPE and holds no row for it here.
          this.events.add("cognito-catalog:" + args[1]);
          return null;
        }
        return unsupported(method);
      });
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
}
