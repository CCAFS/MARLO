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

package org.cgiar.ccafs.marlo.action.json.global;

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpUserManager;
import org.cgiar.ccafs.marlo.data.manager.CustomParameterManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.OtpChallengeManager;
import org.cgiar.ccafs.marlo.data.manager.ParameterManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CustomParameter;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitType;
import org.cgiar.ccafs.marlo.data.model.Parameter;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.otp.OtpEligibility;
import org.cgiar.ccafs.marlo.security.otp.OtpKeys;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Covers CHG-COGNITO-AUTH-001-T10: {@code crpByEmail.do}'s per-unit {@code cognitoEnabled} flag and the
 * two structural fixes to the existing action (design.md 4).
 * <p>
 * Every collaborator is a hand-rolled double: MARLO has no mocking framework ({@code DEC-005} is
 * {@code PENDING}), matching {@code CognitoLoginActionTest} and {@code LoginActionFinishLoginTest}. Tests
 * drive the real {@link CrpByUserEmailAction#execute()} directly -- it needs no live Struts request. The
 * private {@code userEmail} field, which production only populates from {@code prepare()} reading a live
 * Struts {@code Parameter}, is set through reflection instead, the same workaround
 * {@code LoginActionFinishLoginTest} uses for the same reason.
 * <p>
 * <b>What these tests do not cover</b> (recorded here rather than only in the implementer's report): the
 * Struts interceptor stack that routes a real request to {@link CrpByUserEmailAction#execute()}, and the
 * actual JSON serialization {@code struts-home.xml}'s {@code crpByEmail} action result produces --
 * notably, that result has no {@code excludeNullProperties}, so a {@code null} {@code user} still appears
 * in the wire payload as the JSON key {@code "user":null} rather than being omitted. These tests assert on
 * the action's Java getters, which is the layer available without a servlet container.
 */
public class CrpByUserEmailActionTest {

  private static final String CGIAR_EMAIL = "priya.cgiar@cgiar.org";
  private static final String UNKNOWN_EMAIL = "nobody@cgiar.org";
  private static final String USERNAME = "ann.partner";
  private static final String ALL_KEYS =
    "[system_otp_login_active, system_otp_excluded_email_domains, system_password_login_active]";
  private static final String LAST_RESORT_LINE = "auth.password.last_resort ";

  private RecordingUserManager userManager;
  private StubGlobalUnitManager crpManager;
  private StubCustomParameterManager customParameterManager;
  private StubParameterManager parameterManager;

  /** CHG-OTP-LOGIN-001-T13: the one-time-code world. {@code otpSecret} {@code null} means unconfigured. */
  private static final String SECRET_32_BYTES = "AAECAwQFBgcICQoLDA0ODxAREhMUFRYXGBkaGxwdHh8=";
  private static final String PARTNER_EMAIL = "ann@partner.org";
  private String otpSecret;
  private final Map<Long, String> switches = new HashMap<Long, String>();
  private final Map<Long, String> exclusions = new HashMap<Long, String>();
  private final Map<Long, String> passwordSwitches = new HashMap<Long, String>();
  private final List<String> specificityReads = new ArrayList<String>();
  private final Set<Long> memberUnitIds = new HashSet<Long>();
  private boolean specificityReadThrows;
  private Logger rootLogger;
  private ListAppender<ILoggingEvent> appender;

  /**
   * Global Unit ids and Global Unit TYPE ids are deliberately disjoint number ranges here. If they overlapped,
   * passing the wrong one to the catalog lookup would resolve anyway and the mistake would never surface.
   */
  private static final long EXPECTED_TYPE_ID = 91L;

  private static GlobalUnit globalUnit(long id, String acronym) {
    GlobalUnitType type = new GlobalUnitType();
    type.setId(Long.valueOf(EXPECTED_TYPE_ID));
    GlobalUnit unit = new GlobalUnit();
    unit.setId(Long.valueOf(id));
    unit.setAcronym(acronym);
    unit.setName(acronym + " full name");
    unit.setGlobalUnitType(type);
    return unit;
  }

  private static User cgiarUser() {
    User user = new User();
    user.setId(Long.valueOf(9001L));
    user.setEmail(CGIAR_EMAIL);
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
  public void setUp() {
    this.userManager = new RecordingUserManager();
    this.crpManager = new StubGlobalUnitManager();
    this.customParameterManager = new StubCustomParameterManager();
    this.parameterManager = new StubParameterManager();
    // Catalog default is "false" everywhere unless a test overrides it -- matches CognitoLoginActionTest's
    // baseline and keeps a Global Unit disabled unless something explicitly turns it on.
    this.parameterManager.catalogDefault = "false";
    this.otpSecret = null;
    this.switches.clear();
    this.exclusions.clear();
    this.specificityReads.clear();
    this.memberUnitIds.clear();
    this.specificityReadThrows = false;
    this.passwordSwitches.clear();
    // Every ERROR line of the run is read from the root logger, so the test does not depend on which class logs it.
    this.rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    this.appender = new ListAppender<ILoggingEvent>();
    this.appender.start();
    this.rootLogger.addAppender(this.appender);
  }

  @After
  public void tearDown() {
    this.rootLogger.detachAppender(this.appender);
  }

  private CrpByUserEmailAction newAction() {
    return this.newAction(this.newEligibility());
  }

  private CrpByUserEmailAction newAction(OtpEligibility eligibility) {
    return new CrpByUserEmailAction(new APConfig(), this.crpManager, this.userManager, this.customParameterManager,
      this.parameterManager, eligibility);
  }

  /** The real predicate over fakes: only the managers are doubles, so the real switch and exclusion rules run. */
  private OtpEligibility newEligibility() {
    APConfig config = new APConfig() {

      @Override
      public String getOtpHmacSecret() {
        return CrpByUserEmailActionTest.this.otpSecret == null ? "" : CrpByUserEmailActionTest.this.otpSecret;
      }
    };
    OtpChallengeManager challengeManager = fake(OtpChallengeManager.class, (method, args) -> {
      if ("specificityValuesFor".equals(method)) {
        Collection<?> ids = (Collection<?>) args[1];
        this.specificityReads.add(args[0] + "|" + ids);
        if (this.specificityReadThrows) {
          throw new IllegalStateException("specificity read failed");
        }
        Map<Long, Map<String, String>> answer = new HashMap<Long, Map<String, String>>();
        for (Object id : ids) {
          Map<String, String> values = new HashMap<String, String>();
          values.put("system_otp_login_active", this.switches.get(id));
          values.put("system_otp_excluded_email_domains", this.exclusions.get(id));
          values.put("system_password_login_active", this.passwordSwitches.get(id));
          answer.put((Long) id, values);
        }
        return answer;
      }
      throw new UnsupportedOperationException("not needed by this suite: " + method);
    });
    CrpUserManager crpUserManager = fake(CrpUserManager.class, (method, args) -> {
      if ("existActiveCrpUser".equals(method) && args.length == 2) {
        this.crpUserChecks++;
        return Boolean.valueOf(this.memberUnitIds.contains(args[1]));
      }
      throw new UnsupportedOperationException("not needed by this suite: " + method);
    });
    return new OtpEligibility(new OtpKeys(config), this.userManager, this.crpManager, crpUserManager,
      challengeManager, this.customParameterManager, this.parameterManager);
  }

  private int crpUserChecks;

  @SuppressWarnings("unchecked")
  private static <T> T fake(Class<T> type, Answer answer) {
    InvocationHandler handler =
      (proxy, method, args) -> answer.answer(method.getName(), args == null ? new Object[0] : args);
    return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type}, handler);
  }

  private interface Answer {

    Object answer(String method, Object[] args);
  }

  private static User partnerUser(boolean active, boolean cgiar) {
    User user = new User();
    user.setId(Long.valueOf(9002L));
    user.setEmail(PARTNER_EMAIL);
    user.setCgiarUser(cgiar);
    user.setActive(active);
    return user;
  }

  /** Runs the action for {@link #PARTNER_EMAIL} and returns the per-unit {@code otpEnabled} flags by acronym. */
  private Map<String, Object> otpFlags(User user, GlobalUnit... units) {
    this.crpManager.units = Arrays.asList(units);
    this.userManager.register(user);
    CrpByUserEmailAction action = this.newAction();
    this.setUserEmail(action, PARTNER_EMAIL);
    this.execute(action);
    Map<String, Object> flags = new HashMap<String, Object>();
    for (Map<String, Object> entry : action.getCrps()) {
      assertTrue("every unit entry carries otpEnabled", entry.containsKey("otpEnabled"));
      flags.put((String) entry.get("acronym"), entry.get("otpEnabled"));
    }
    return flags;
  }

  private void configureOtp() {
    this.otpSecret = SECRET_32_BYTES;
    this.switches.put(Long.valueOf(1L), "true");
    this.switches.put(Long.valueOf(2L), "true");
  }

  /** The flag is true only when configured, the unit's switch is on and the domain is not on its exclusion list. */
  @Test
  public void otpEnabledIsTrueOnlyWhenConfiguredSwitchOnAndDomainNotExcluded() {
    this.configureOtp();
    this.switches.put(Long.valueOf(3L), "true");
    this.exclusions.put(Long.valueOf(3L), "other.org, Partner.org");
    this.switches.put(Long.valueOf(4L), "true");
    this.customParameterManager.overridesByUnitId.put(Long.valueOf(4L), activeOverride("true"));
    this.switches.put(Long.valueOf(5L), "false");
    GlobalUnit open = globalUnit(1L, "AAA");
    GlobalUnit excluded = globalUnit(3L, "CCC");
    GlobalUnit cognitoUnit = globalUnit(4L, "DDD");
    GlobalUnit switchedOff = globalUnit(5L, "EEE");

    Map<String, Object> asLocal = this.otpFlags(partnerUser(true, false), open, excluded, cognitoUnit, switchedOff);
    assertEquals(Boolean.TRUE, asLocal.get("AAA"));
    assertEquals("the domain is on the unit's exclusion list", Boolean.FALSE, asLocal.get("CCC"));
    assertEquals("a Cognito-active unit with the switch on", Boolean.TRUE, asLocal.get("DDD"));
    assertEquals("the unit's switch is off", Boolean.FALSE, asLocal.get("EEE"));

    Map<String, Object> asCgiar = this.otpFlags(partnerUser(true, true), open, excluded, cognitoUnit, switchedOff);
    assertEquals("SEC-009 (T21): is_cgiar_user plays no part, on any unit", asLocal, asCgiar);
  }

  /** SEC-009 (T21): no domain is fixed in code, so a cgiar.org address is judged by the unit's list alone. */
  @Test
  public void aCgiarOrgAddressIsOfferedTheCodeUnlessTheUnitExcludesItsDomain() {
    this.configureOtp();
    this.exclusions.put(Long.valueOf(2L), "cgiar.org");
    this.crpManager.units = Arrays.asList(globalUnit(1L, "AAA"), globalUnit(2L, "BBB"));
    this.userManager.register(cgiarUser());
    CrpByUserEmailAction action = this.newAction();
    this.setUserEmail(action, CGIAR_EMAIL);
    this.execute(action);

    assertEquals("empty exclusion", Boolean.TRUE, this.byAcronym(action.getCrps(), "AAA").get("otpEnabled"));
    assertEquals("cgiar.org excluded", Boolean.FALSE, this.byAcronym(action.getCrps(), "BBB").get("otpEnabled"));
  }

  /** JI-11: the flag does not depend on whether the account is active, nor on membership of the listed unit. */
  @Test
  public void otpEnabledIsIdenticalForAnActiveAndAnInactiveUserAndForMemberAndNonMemberUnits() {
    this.configureOtp();
    this.memberUnitIds.add(Long.valueOf(1L));
    GlobalUnit member = globalUnit(1L, "AAA");
    GlobalUnit nonMember = globalUnit(2L, "BBB");

    Map<String, Object> active = this.otpFlags(partnerUser(true, false), member, nonMember);
    Map<String, Object> inactive = this.otpFlags(partnerUser(false, false), member, nonMember);

    assertEquals("an inactive user is told exactly what an active one is", active, inactive);
    assertEquals("a non-member unit is flagged exactly as a member unit", active.get("AAA"), active.get("BBB"));
    assertEquals(Boolean.TRUE, active.get("AAA"));
  }

  /** The disclosure-safe variant reads no account state beyond the user found and asks no membership question. */
  @Test
  public void otpEnabledReadsNoMembershipOrUnitLookup() {
    this.configureOtp();
    this.memberUnitIds.add(Long.valueOf(1L));
    this.otpFlags(partnerUser(true, false), globalUnit(1L, "AAA"), globalUnit(2L, "BBB"));
    assertEquals(0, this.crpUserChecks);
    assertEquals(0, this.crpManager.byIdCalls);
  }

  /** One uncached query for all the listed units, and it carries all three keys (CHG-PWD-SWITCH-001-T03, DD-3). */
  @Test
  public void oneSpecificityQueryCoversBothKeysAndEveryListedUnit() {
    this.configureOtp();
    this.otpFlags(partnerUser(true, false), globalUnit(1L, "AAA"), globalUnit(2L, "BBB"));
    assertEquals(Arrays.asList(
      "[system_otp_login_active, system_otp_excluded_email_domains, system_password_login_active]|[1, 2]"),
      this.specificityReads);
  }

  /**
   * CHG-PWD-SWITCH-001-T04 (DD-6, NF-002, P-17): the early exit is gone. With no key the lookup still makes exactly one
   * query, because {@code passwordEnabled} needs the switch; {@code otpEnabled} is false for every unit.
   */
  @Test
  public void unconfiguredMakesExactlyOneSpecificityQueryAndOtpIsFalseForEveryUnit() {
    this.switches.put(Long.valueOf(1L), "true");
    this.otpSecret = null;
    CrpByUserEmailAction action = this.runFor(partnerUser(true, false), globalUnit(1L, "AAA"), globalUnit(2L, "BBB"));
    assertEquals(Arrays.asList(ALL_KEYS + "|[1, 2]"), this.specificityReads);
    for (String acronym : Arrays.asList("AAA", "BBB")) {
      assertEquals(Boolean.FALSE, this.byAcronym(action.getCrps(), acronym).get("otpEnabled"));
      assertEquals("last resort or the switch is on: the password is offered", Boolean.TRUE,
        this.byAcronym(action.getCrps(), acronym).get("passwordEnabled"));
    }
  }

  /** An email matching no account still discloses nothing, and asks no specificity question. */
  @Test
  public void anUnknownEmailMakesNoSpecificityQuery() {
    this.configureOtp();
    this.crpManager.units = Arrays.asList(globalUnit(1L, "AAA"));
    CrpByUserEmailAction action = this.newAction();
    this.setUserEmail(action, "nobody@partner.org");
    this.execute(action);
    assertNull(action.getUser());
    assertTrue(action.getCrps().isEmpty());
    assertEquals(0, this.specificityReads.size());
  }

  /** Fail closed: a throwing specificity read leaves every flag false and the normal response intact. */
  @Test
  public void aFailingSpecificityReadFailsClosedAndLeavesTheLoginLookupUnchanged() {
    this.configureOtp();
    this.specificityReadThrows = true;
    GlobalUnit unitA = globalUnit(1L, "AAA");
    GlobalUnit unitB = globalUnit(2L, "BBB");
    Map<String, Object> flags = this.otpFlags(partnerUser(true, false), unitA, unitB);
    assertEquals(1, this.specificityReads.size());
    assertEquals(Boolean.FALSE, flags.get("AAA"));
    assertEquals(Boolean.FALSE, flags.get("BBB"));

    CrpByUserEmailAction action = this.runFor(partnerUser(true, false), unitA, unitB);
    assertEquals(2, action.getCrps().size());
    assertNotNull(action.getUser());
    assertEquals(Boolean.FALSE, this.byAcronym(action.getCrps(), "AAA").get("cognitoEnabled"));
  }

  /**
   * DD-6 failure: the query throws, so {@code otpEnabled} is false for every unit and {@code passwordEnabled} is true
   * for an account without Cognito in the unit (today's behaviour) and false for a Cognito account, with one ERROR
   * line that carries the exception. The Cognito answer was resolved before the query, so the failure cannot erase it.
   */
  @Test
  public void aFailingQueryKeepsTheLookupFailOpenForAccountsWithoutCognitoOnly() {
    this.configureOtp();
    this.specificityReadThrows = true;
    this.customParameterManager.overridesByUnitId.put(Long.valueOf(2L), activeOverride("true"));

    CrpByUserEmailAction action = this.runFor(partnerUser(true, true), globalUnit(1L, "AAA"), globalUnit(2L, "BBB"));

    Map<String, Object> plain = this.byAcronym(action.getCrps(), "AAA");
    Map<String, Object> cognito = this.byAcronym(action.getCrps(), "BBB");
    assertEquals(Boolean.FALSE, plain.get("otpEnabled"));
    assertEquals("no Cognito in this unit: today's password", Boolean.TRUE, plain.get("passwordEnabled"));
    assertEquals(Boolean.FALSE, cognito.get("otpEnabled"));
    assertEquals("a Cognito account is never offered the password, not even on failure", Boolean.FALSE,
      cognito.get("passwordEnabled"));
    assertEquals(Boolean.TRUE, cognito.get("cognitoEnabled"));
    List<ILoggingEvent> errors = this.errorLines();
    assertEquals(1, errors.size());
    assertNotNull("the line carries the exception", errors.get(0).getThrowableProxy());
  }

  /**
   * Disqualifier guard: the complete key set of the response is asserted, and every pre-existing key and value is
   * identical with the feature on and off.
   */
  @Test
  public void everyPreExistingKeyAndValueIsUnchangedOnAndOff() {
    GlobalUnit unitA = globalUnit(1L, "AAA");
    GlobalUnit unitB = globalUnit(2L, "BBB");
    this.customParameterManager.overridesByUnitId.put(Long.valueOf(1L), activeOverride("true"));

    this.otpSecret = null;
    CrpByUserEmailAction off = this.runFor(partnerUser(true, false), unitA, unitB);
    this.configureOtp();
    CrpByUserEmailAction on = this.runFor(partnerUser(true, false), unitA, unitB);

    Set<String> unitKeys = new LinkedHashSet<String>(
      Arrays.asList("id", "name", "acronym", "type", "idType", "cognitoEnabled", "otpEnabled", "passwordEnabled"));
    Set<String> userKeys = new LinkedHashSet<String>(Arrays.asList("name", "agree", "isCgiarUser"));
    assertEquals(userKeys, new HashSet<String>(off.getUser().keySet()));
    assertEquals(userKeys, new HashSet<String>(on.getUser().keySet()));
    assertEquals(off.getUser(), on.getUser());
    assertEquals(2, on.getCrps().size());
    for (int i = 0; i < on.getCrps().size(); i++) {
      assertEquals(unitKeys, new HashSet<String>(on.getCrps().get(i).keySet()));
      assertEquals(unitKeys, new HashSet<String>(off.getCrps().get(i).keySet()));
      Map<String, Object> onEntry = new HashMap<String, Object>(on.getCrps().get(i));
      Map<String, Object> offEntry = new HashMap<String, Object>(off.getCrps().get(i));
      assertEquals(Boolean.TRUE, onEntry.remove("otpEnabled"));
      assertEquals(Boolean.FALSE, offEntry.remove("otpEnabled"));
      assertEquals("every switch on: the password is offered", Boolean.TRUE, onEntry.get("passwordEnabled"));
      assertEquals("no key: the password is offered too", Boolean.TRUE, offEntry.get("passwordEnabled"));
      assertEquals(offEntry, onEntry);
    }
    assertEquals(Boolean.TRUE, on.getCrps().get(0).get("cognitoEnabled"));
    assertEquals(Boolean.FALSE, on.getCrps().get(1).get("cognitoEnabled"));
  }

  private CrpByUserEmailAction runFor(User user, GlobalUnit... units) {
    this.crpManager.units = Arrays.asList(units);
    this.userManager.register(user);
    CrpByUserEmailAction action = this.newAction();
    this.setUserEmail(action, PARTNER_EMAIL);
    this.execute(action);
    return action;
  }

  /**
   * T10 test 1 -- the C-1 defect. <b>Not evidence when the two units share one flag state</b> (T10's own
   * "Not evidence when" clause): that variant would pass under the broken single-scalar design and prove
   * nothing about per-unit resolution. Here unit A has an ACTIVE override of {@code "true"} and unit B has
   * no override and a catalog default of {@code "false"} -- two genuinely different resolution paths, not
   * just two different literal values.
   */
  @Test
  public void aUserInTwoUnitsWithDifferentFlagStatesGetsDifferentCognitoEnabledPerEntry() {
    GlobalUnit unitA = globalUnit(1L, "AAA");
    GlobalUnit unitB = globalUnit(2L, "BBB");
    this.crpManager.units = Arrays.asList(unitA, unitB);
    this.userManager.register(cgiarUser());
    this.customParameterManager.overridesByUnitId.put(Long.valueOf(1L), activeOverride("true"));
    // Unit B intentionally has no override: it falls through to the catalog default ("false" from setUp()).

    CrpByUserEmailAction action = this.newAction();
    this.setUserEmail(action, CGIAR_EMAIL);

    this.execute(action);

    List<Map<String, Object>> crps = action.getCrps();
    assertEquals(2, crps.size());
    Map<String, Object> aEntry = this.byAcronym(crps, "AAA");
    Map<String, Object> bEntry = this.byAcronym(crps, "BBB");
    assertEquals(Boolean.TRUE, aEntry.get("cognitoEnabled"));
    assertEquals(Boolean.FALSE, bEntry.get("cognitoEnabled"));
    assertNotNull("a user with Global Units must get a populated user map", action.getUser());
    assertEquals(Boolean.TRUE, action.getUser().get("isCgiarUser"));
  }

  /**
   * <b>The task's own "Fails when" mutation, run and recorded verbatim in the implementer's report</b>:
   * hoisting {@code cognitoEnabled} to a single scalar on {@code user} makes this exact test unable to
   * express two different values for unit A and unit B.
   */
  @Test
  public void mixedFlagsProduceTwoDistinctValuesNotOneSharedScalar() {
    GlobalUnit unitA = globalUnit(1L, "AAA");
    GlobalUnit unitB = globalUnit(2L, "BBB");
    this.crpManager.units = Arrays.asList(unitA, unitB);
    this.userManager.register(cgiarUser());
    this.customParameterManager.overridesByUnitId.put(Long.valueOf(1L), activeOverride("true"));

    CrpByUserEmailAction action = this.newAction();
    this.setUserEmail(action, CGIAR_EMAIL);
    this.execute(action);

    List<Map<String, Object>> crps = action.getCrps();
    Object aFlag = this.byAcronym(crps, "AAA").get("cognitoEnabled");
    Object bFlag = this.byAcronym(crps, "BBB").get("cognitoEnabled");
    assertTrue("the two units must genuinely disagree for this test to prove anything",
      !aFlag.equals(bFlag));
  }

  /**
   * <b>MIG-001's rollback, which nothing in this spec proved until now.</b>
   * <p>
   * Every other test here and in {@code CognitoLoginActionTest} uses a catalog default of {@code "false"} and,
   * where an override exists, an active override of {@code "true"}. Two mutations of
   * {@code CognitoAuthSpecificity.isActiveFor} survive that entire suite green:
   * <ul>
   * <li>{@code if (override != null) { return true; }} — ignoring the override's <i>value</i>. An operator
   * performing MIG-001's rollback (set {@code custom_parameters.value} to {@code 'false'}; "the flag is the
   * rollback") would leave Cognito <b>enabled</b>.</li>
   * <li>reading the catalog before the override — inverting the COALESCE precedence PS-16 exists to make
   * canonical, undetectable while every catalog default is {@code "false"}.</li>
   * </ul>
   * This test inverts both fixtures: the catalog says {@code "true"}, unit A's active override says
   * {@code "false"}. Unit A must come back <b>disabled</b> (override wins, and its value is parsed), unit B —
   * no override — must come back <b>enabled</b> (the catalog default is actually read).
   */
  @Test
  public void anActiveOverrideOfFalseBeatsACatalogDefaultOfTrue() {
    GlobalUnit unitA = globalUnit(1L, "AAA");
    GlobalUnit unitB = globalUnit(2L, "BBB");
    this.crpManager.units = Arrays.asList(unitA, unitB);
    this.userManager.register(cgiarUser());
    this.parameterManager.catalogDefault = "true";
    this.customParameterManager.overridesByUnitId.put(Long.valueOf(1L), activeOverride("false"));

    CrpByUserEmailAction action = this.newAction();
    this.setUserEmail(action, CGIAR_EMAIL);

    this.execute(action);

    List<Map<String, Object>> crps = action.getCrps();
    assertEquals(Boolean.FALSE, this.byAcronym(crps, "AAA").get("cognitoEnabled"));
    assertEquals(Boolean.TRUE, this.byAcronym(crps, "BBB").get("cognitoEnabled"));
  }

  /**
   * T10 test 2 -- the structural bug. A user row exists (found by email) but belongs to zero Global Units:
   * the original code built {@code user} <b>inside</b> the {@code for} loop over {@code crps}, so it never
   * ran and {@code user} stayed {@code null}. The fix must return a well-formed response instead.
   */
  @Test
  public void aUserWithZeroGlobalUnitsReturnsAWellFormedResponseNotUserNull() {
    this.crpManager.units = new ArrayList<GlobalUnit>();
    this.userManager.register(cgiarUser());

    CrpByUserEmailAction action = this.newAction();
    this.setUserEmail(action, CGIAR_EMAIL);
    this.execute(action);

    assertNotNull("user must not be null for an account that exists, even with zero Global Units",
      action.getUser());
    assertEquals(Boolean.TRUE, action.getUser().get("isCgiarUser"));
    assertNotNull("crps must be a well-formed (empty) list, not null", action.getCrps());
    assertTrue(action.getCrps().isEmpty());
    assertEquals("nothing to answer for, so nothing to read", 0, this.specificityReads.size());
  }

  /**
   * T10 test 3 -- the security test (FN-001 "Email not found" / R-D3). An unknown email must not disclose
   * whether it would have been CGIAR or local. This asserts <b>absence</b>, not merely a null field: no
   * {@code isCgiarUser} key can be read at all (there is no {@code user} map to read it from), and
   * {@code crps} carries zero entries -- the same shape a known user with zero Global Units produces, so
   * an attacker comparing the two responses gains no oracle from this endpoint's structure.
   */
  @Test
  public void anUnknownEmailDisclosesNeitherIsCgiarUserNorCrps() {
    this.crpManager.units = Arrays.asList(globalUnit(1L, "AAA"));
    // No user registered under UNKNOWN_EMAIL in either lookup path.

    CrpByUserEmailAction action = this.newAction();
    this.setUserEmail(action, UNKNOWN_EMAIL);
    this.execute(action);

    assertNull("no user object may be produced for an unknown email", action.getUser());
    assertNotNull(action.getCrps());
    assertTrue("no Global Unit membership may be disclosed for an unknown email", action.getCrps().isEmpty());
    // The stubbed GlobalUnitManager below records whether it was even asked -- an unknown email must not
    // cause a membership lookup to run at all, since crps[] cannot be trusted to come back empty by luck.
    assertFalse("crpUsers() must not be called once the account itself cannot be found",
      this.crpManager.crpUsersCalled);
  }

  // ---- CHG-PWD-SWITCH-001-T04 (DD-6): passwordEnabled, one query, the once-per-unit ERROR ---------------------------

  /** The unit settings of one row of the rule. {@code password} {@code null} leaves the switch unset. */
  private GlobalUnit rowUnit(long id, String acronym, String code, String password) {
    this.switches.put(Long.valueOf(id), code);
    if (password != null) {
      this.passwordSwitches.put(Long.valueOf(id), password);
    }
    return globalUnit(id, acronym);
  }

  private void assertFlags(String row, CrpByUserEmailAction action, String acronym, boolean otp, boolean password) {
    Map<String, Object> entry = this.byAcronym(action.getCrps(), acronym);
    assertEquals(row + ": otpEnabled", Boolean.valueOf(otp), entry.get("otpEnabled"));
    assertEquals(row + ": passwordEnabled", Boolean.valueOf(password), entry.get("passwordEnabled"));
  }

  private List<ILoggingEvent> errorLines() {
    List<ILoggingEvent> lines = new ArrayList<ILoggingEvent>();
    for (ILoggingEvent event : this.appender.list) {
      if (event.getLevel() == Level.ERROR) {
        lines.add(event);
      }
    }
    return lines;
  }

  private List<String> lastResortLines() {
    List<String> lines = new ArrayList<String>();
    for (ILoggingEvent event : this.errorLines()) {
      if (event.getFormattedMessage().startsWith(LAST_RESORT_LINE)) {
        lines.add(event.getFormattedMessage());
      }
    }
    return lines;
  }

  /** Design 1 rule, key valid, an email, an account without Cognito: one literal row per unit. */
  @Test
  public void everyRowOfTheRuleForAnAccountWithoutCognito() {
    this.otpSecret = SECRET_32_BYTES;
    this.exclusions.put(Long.valueOf(4L), "partner.org");
    CrpByUserEmailAction action = this.runFor(partnerUser(true, false), this.rowUnit(1L, "A1", "true", "true"),
      this.rowUnit(2L, "A2", "false", "true"), this.rowUnit(3L, "A3", "false", "false"),
      this.rowUnit(4L, "A4", "true", "false"), this.rowUnit(5L, "A5", "false", ""),
      this.rowUnit(6L, "A6", "false", "maybe"), this.rowUnit(7L, "A7", "false", " FALSE "));

    this.assertFlags("both switches on", action, "A1", true, true);
    this.assertFlags("code off, password on", action, "A2", false, true);
    this.assertFlags("password off: the code is the default method", action, "A3", true, false);
    this.assertFlags("password off, domain excluded: the default method still applies", action, "A4", true, false);
    this.assertFlags("an empty password value is on", action, "A5", false, true);
    this.assertFlags("an unreadable password value is on", action, "A6", false, true);
    this.assertFlags("the switch is read trimmed and in any case", action, "A7", true, false);
    assertEquals(Arrays.asList(ALL_KEYS + "|[1, 2, 3, 4, 5, 6, 7]"), this.specificityReads);
  }

  /** SEC-003, FN-005: a Cognito account in the unit never has the password, and keeps its code settings. */
  @Test
  public void aCognitoAccountNeverGetsThePasswordAndKeepsItsCodeSettings() {
    this.otpSecret = SECRET_32_BYTES;
    for (long id = 11L; id <= 13L; id++) {
      this.customParameterManager.overridesByUnitId.put(Long.valueOf(id), activeOverride("true"));
    }
    GlobalUnit[] units = {this.rowUnit(11L, "C1", "false", "true"), this.rowUnit(12L, "C2", "false", "false"),
      this.rowUnit(13L, "C3", "true", "false"), this.rowUnit(14L, "C4", "false", "false"),
      this.rowUnit(15L, "C5", "false", "true")};

    CrpByUserEmailAction cgiar = this.runFor(partnerUser(true, true), units);
    this.assertFlags("Cognito, password on, code off", cgiar, "C1", false, false);
    this.assertFlags("Cognito, password off, code off: the override does not apply", cgiar, "C2", false, false);
    this.assertFlags("Cognito, password off, code on", cgiar, "C3", true, false);
    this.assertFlags("is_cgiar_user without Cognito in the unit is a plain account", cgiar, "C4", true, false);
    this.assertFlags("is_cgiar_user without Cognito, password on", cgiar, "C5", false, true);

    CrpByUserEmailAction plain = this.runFor(partnerUser(true, false), units);
    this.assertFlags("Cognito on in the unit but the account is not a CGIAR one", plain, "C1", false, true);
    this.assertFlags("same, password off", plain, "C2", true, false);
  }

  /** FN-006: with no key the password is offered to every account without Cognito, and never to a Cognito one. */
  @Test
  public void withNoKeyThePasswordIsOfferedToEveryAccountWithoutCognito() {
    this.otpSecret = null;
    this.customParameterManager.overridesByUnitId.put(Long.valueOf(23L), activeOverride("true"));
    GlobalUnit[] units = {this.rowUnit(21L, "K1", "true", "true"), this.rowUnit(22L, "K2", "true", "false"),
      this.rowUnit(23L, "K3", "true", "false")};

    CrpByUserEmailAction cgiar = this.runFor(partnerUser(true, true), units);
    this.assertFlags("switch on", cgiar, "K1", false, true);
    this.assertFlags("switch off: the last resort", cgiar, "K2", false, true);
    this.assertFlags("a Cognito account is not offered the last resort", cgiar, "K3", false, false);
    assertEquals(Arrays.asList(ALL_KEYS + "|[21, 22, 23]"), this.specificityReads);
  }

  /** FN-010: a username cannot receive a code, so a password-off unit offers no method and says "use your email". */
  @Test
  public void aUsernameGetsNoCodeAndTheSameOneQuery() {
    this.otpSecret = SECRET_32_BYTES;
    this.userManager.registerUsername(USERNAME, partnerUser(true, false));
    this.crpManager.units = Arrays.asList(this.rowUnit(31L, "U1", "true", "true"),
      this.rowUnit(32L, "U2", "true", "false"));
    CrpByUserEmailAction action = this.newAction();
    this.setUserEmail(action, USERNAME);
    this.execute(action);

    this.assertFlags("password on: a username signs in with the password", action, "U1", false, true);
    this.assertFlags("password off: only the code is left, and a username has none", action, "U2", false, false);
    assertEquals("one query even for a username", Arrays.asList(ALL_KEYS + "|[31, 32]"), this.specificityReads);
  }

  /**
   * NF-002, P-18: one query per call whatever the units, and it is asked for exactly the units the action loaded, in
   * the order it lists them (acronym order here, which differs from the id order).
   */
  @Test
  public void oneQueryPerCallForExactlyTheLoadedUnitsInListedOrder() {
    this.configureOtp();
    this.runFor(partnerUser(true, false), globalUnit(2L, "AAA"), globalUnit(1L, "BBB"), globalUnit(3L, "CCC"));
    assertEquals(Arrays.asList(ALL_KEYS + "|[2, 1, 3]"), this.specificityReads);
  }

  /**
   * T03 forward pointer: the Cognito answer of each unit is resolved once, by today's per-unit resolution, and
   * {@code methodsFor} adds no read of its own, with the password on and with it off.
   */
  @Test
  public void theCognitoFlagIsReadOncePerUnitAndTheRuleAddsNoRead() {
    this.configureOtp();
    this.customParameterManager.overridesByUnitId.put(Long.valueOf(1L), activeOverride("true"));
    this.runFor(partnerUser(true, true), this.rowUnit(1L, "AAA", "true", "true"),
      this.rowUnit(2L, "BBB", "true", "false"), this.rowUnit(3L, "CCC", "false", "false"));

    assertEquals("one override read per unit, in order", Arrays.asList(1L, 2L, 3L),
      this.customParameterManager.reads);
    assertEquals("the catalog is read only for the units without an override", Arrays.asList(EXPECTED_TYPE_ID,
      EXPECTED_TYPE_ID), this.parameterManager.reads);
  }

  /** OPS-002: the last-resort ERROR is written once per unit, whatever is typed and however often. */
  @Test
  public void theLastResortErrorIsWrittenOncePerUnitAcrossRepeatedCalls() {
    this.otpSecret = null;
    this.customParameterManager.overridesByUnitId.put(Long.valueOf(43L), activeOverride("true"));
    GlobalUnit[] units = {this.rowUnit(41L, "L1", "true", "false"), this.rowUnit(42L, "L2", "true", "true"),
      this.rowUnit(43L, "L3", "true", "false"), this.rowUnit(44L, "L4", "true", "false")};
    OtpEligibility shared = this.newEligibility();
    this.crpManager.units = Arrays.asList(units);
    this.userManager.register(partnerUser(true, true));
    this.userManager.registerUsername(USERNAME, partnerUser(true, true));

    for (String typed : Arrays.asList(PARTNER_EMAIL, PARTNER_EMAIL, USERNAME, PARTNER_EMAIL, USERNAME)) {
      CrpByUserEmailAction action = this.newAction(shared);
      this.setUserEmail(action, typed);
      this.execute(action);
    }

    assertEquals("L1 and L4 only: L2 has the switch on, L3 is a Cognito account", Arrays.asList(
      "auth.password.last_resort gu=L1 reason=code_key_unconfigured",
      "auth.password.last_resort gu=L4 reason=code_key_unconfigured"), this.lastResortLines());
  }

  /** The ERROR belongs to the key problem alone: a configured key, or the switch on, writes no line. */
  @Test
  public void noLastResortErrorWhenTheKeyIsValid() {
    this.otpSecret = SECRET_32_BYTES;
    this.runFor(partnerUser(true, false), this.rowUnit(51L, "M1", "true", "false"));
    assertEquals(0, this.lastResortLines().size());
  }

  /**
   * AC-11, SEC-005 (added by the /akili-test Suite 1 tester): on a password-off unit {@code otpEnabled} and
   * {@code passwordEnabled} are the unit's answer for a kind of account, never the account's own state. An active and
   * an inactive account without Cognito, on a unit they belong to and on one they do not, read the same two flags, so
   * the lookup still tells an anonymous caller nothing about whether the account is active or a member.
   */
  @Test
  public void ac11OnAPasswordOffUnitBothFlagsAreTheSameForEveryAccountStateOfTheSameKind() {
    this.otpSecret = SECRET_32_BYTES;
    this.memberUnitIds.add(Long.valueOf(61L));
    GlobalUnit member = this.rowUnit(61L, "P1", "false", "false");
    GlobalUnit nonMember = this.rowUnit(62L, "P2", "false", "false");

    CrpByUserEmailAction active = this.runFor(partnerUser(true, false), member, nonMember);
    CrpByUserEmailAction inactive = this.runFor(partnerUser(false, false), member, nonMember);

    this.assertFlags("active, member unit", active, "P1", true, false);
    this.assertFlags("active, non-member unit", active, "P2", true, false);
    this.assertFlags("inactive, member unit", inactive, "P1", true, false);
    this.assertFlags("inactive, non-member unit", inactive, "P2", true, false);
    assertEquals("an inactive account gets the very entries an active one gets", active.getCrps(),
      inactive.getCrps());
    assertEquals("the flags ask no membership question", 0, this.crpUserChecks);
  }


  private Map<String, Object> byAcronym(List<Map<String, Object>> crps, String acronym) {
    for (Map<String, Object> entry : crps) {
      if (acronym.equals(entry.get("acronym"))) {
        return entry;
      }
    }
    throw new IllegalStateException("no entry for acronym " + acronym);
  }

  /** Drives {@link CrpByUserEmailAction#execute()}, translating its checked exception into a test failure. */
  private void execute(CrpByUserEmailAction action) {
    try {
      action.execute();
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * {@code userEmail} has no public setter -- production only ever populates it from {@code prepare()}
   * reading a live Struts {@code Parameter}, which these tests deliberately avoid faking (matching
   * {@code LoginActionFinishLoginTest}'s reflection approach for the same reason: no servlet container).
   */
  private void setUserEmail(CrpByUserEmailAction action, String email) {
    try {
      Field field = CrpByUserEmailAction.class.getDeclaredField("userEmail");
      field.setAccessible(true);
      field.set(action, email);
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException(e);
    }
  }

  /** Records whether {@code crpUsers(String)} was invoked, and returns the configured unit list. */
  private static final class StubGlobalUnitManager implements GlobalUnitManager {

    private List<GlobalUnit> units = new ArrayList<GlobalUnit>();
    private boolean crpUsersCalled;
    private int byIdCalls;

    @Override
    public List<GlobalUnit> crpUsers(String email) {
      this.crpUsersCalled = true;
      return new ArrayList<GlobalUnit>(this.units);
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
      this.byIdCalls++;
      for (GlobalUnit unit : this.units) {
        if (unit.getId().longValue() == globalUnitID) {
          return unit;
        }
      }
      return null;
    }

    @Override
    public GlobalUnit saveGlobalUnit(GlobalUnit globalUnit) {
      throw new UnsupportedOperationException("not needed by this suite");
    }
  }

  /** Resolves {@code getUserByEmail}/{@code getUserByUsername} from one registered-user map. */
  private static final class RecordingUserManager implements UserManager {

    private final Map<String, User> byEmail = new HashMap<String, User>();
    private final Map<String, User> byUsername = new HashMap<String, User>();

    void register(User user) {
      this.byEmail.put(user.getEmail(), user);
    }

    /** A user found only by the username typed in step 1, never by email. */
    void registerUsername(String username, User user) {
      this.byUsername.put(username, user);
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
      User byName = this.byUsername.get(username);
      return byName != null ? byName : this.byEmail.get(username);
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

  /** Resolves only the {@code cognito_auth_active} key, keyed by Global Unit id; anything else is a test bug. */
  private static final class StubCustomParameterManager implements CustomParameterManager {

    private final Map<Long, CustomParameter> overridesByUnitId = new HashMap<Long, CustomParameter>();
    private final List<Long> reads = new ArrayList<Long>();

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
      if (!APConstants.COGNITO_AUTH_ACTIVE.equals(paramaterKey)) {
        throw new IllegalStateException("test authoring error: unexpected key " + paramaterKey);
      }
      this.reads.add(Long.valueOf(globalUnitId));
      return this.overridesByUnitId.get(Long.valueOf(globalUnitId));
    }

    @Override
    public CustomParameter saveCustomParameter(CustomParameter customParameter) {
      throw new UnsupportedOperationException("not needed by this suite");
    }
  }

  /** Resolves only the {@code cognito_auth_active} key with one configured catalog default. */
  private static final class StubParameterManager implements ParameterManager {

    private String catalogDefault;
    private final List<Long> reads = new ArrayList<Long>();

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
      // The id argument is HONOURED, not ignored. The `parameters` catalog is keyed by Global Unit TYPE,
      // and this interface names the parameter `globalUnitId` while the DAO beneath it names the same
      // argument `globalUnitTypeId` -- so passing a unit id instead of a type id is an easy, silent mistake.
      // A stub that returns the same row for any id makes that mutation invisible; this one reddens it.
      this.reads.add(Long.valueOf(globalUnitId));
      if (globalUnitId != EXPECTED_TYPE_ID) {
        return null;
      }
      return catalogRow(this.catalogDefault);
    }

    @Override
    public Parameter saveParameter(Parameter parameter) {
      throw new UnsupportedOperationException("not needed by this suite");
    }
  }
}
