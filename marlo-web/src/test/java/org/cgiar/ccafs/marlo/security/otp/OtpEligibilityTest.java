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


package org.cgiar.ccafs.marlo.security.otp;

import org.cgiar.ccafs.marlo.data.manager.CrpUserManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.OtpChallengeManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitType;
import org.cgiar.ccafs.marlo.data.model.User;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * CHG-OTP-LOGIN-001-T06, T21: {@link OtpEligibility} (FN-001, FN-003, FN-004, FN-005, FN-015, SEC-009, DD-13)
 * against hand-written fakes.
 * <p>
 * Every fake answers from data keyed by the arguments it receives, never with a constant, and records those
 * arguments. A fake that returned {@code true} whatever it was asked would pass every "eligible" case and prove
 * nothing, so each case also asserts what the collaborators were called with. Each refusal test changes exactly one
 * fact of an otherwise eligible world, so a refusal can only come from that fact.
 * <p>
 * The keys and ids asserted are literals from the spec ({@code system_otp_login_active},
 * {@code system_otp_excluded_email_domains}), not the production constants, and the submitted email is mixed-case
 * with padding while the lookup is asserted against its trimmed lower-case form (P-12).
 */
public class OtpEligibilityTest {

  /** base64 of the 32 bytes 0x00..0x1f. */
  private static final String SECRET_32_BYTES = "AAECAwQFBgcICQoLDA0ODxAREhMUFRYXGBkaGxwdHh8=";

  private static final String SUBMITTED = "  User@Partner.ORG ";
  private static final String LOOKED_UP = "user@partner.org";
  private static final String SWITCH_KEY = "system_otp_login_active";
  private static final String EXCLUSION_KEY = "system_otp_excluded_email_domains";
  private static final String BOTH_KEYS = "[" + SWITCH_KEY + ", " + EXCLUSION_KEY + "]";

  private static final long GU = 7L;
  private static final long OTHER_GU = 8L;
  private static final long USER_ID = 42L;
  private static final long GU_TYPE = 3L;

  private World world;

  @Before
  public void setUp() {
    this.world = new World();
    this.world.addUnit(GU, "true", "");
    this.world.addUser(LOOKED_UP, USER_ID, true, false);
    this.world.members.add(USER_ID + ":" + GU);
  }

  // ---- the full predicate ------------------------------------------------------------------------------

  @Test
  public void eligibleUserIsEligibleAndEveryCollaboratorReceivesTheRightArguments() {
    assertTrue(this.world.subject().isEligible(SUBMITTED, GU));

    assertEquals(Arrays.asList(LOOKED_UP), this.world.userLookups);
    assertEquals(Arrays.asList(GU), this.world.unitLookups);
    assertEquals("both keys in one read, for the one unit", Arrays.asList(BOTH_KEYS + "|[" + GU + "]"),
      this.world.specificityReads);
    assertEquals(Arrays.asList(USER_ID + ":" + GU), this.world.membershipChecks);
  }

  @Test
  public void unknownUserIsRefused() {
    this.world.users.clear();

    assertFalse(this.world.subject().isEligible(SUBMITTED, GU));
    assertEquals(Arrays.asList(LOOKED_UP), this.world.userLookups);
    assertEquals(0, this.world.membershipChecks.size());
  }

  @Test
  public void inactiveUserIsRefusedEvenWhenAMember() {
    this.world.addUser(LOOKED_UP, USER_ID, false, false);

    assertFalse(this.world.subject().isEligible(SUBMITTED, GU));
    assertEquals(Arrays.asList(LOOKED_UP), this.world.userLookups);
  }

  @Test
  public void nonMemberIsRefused() {
    this.world.members.clear();

    assertFalse(this.world.subject().isEligible(SUBMITTED, GU));
    assertEquals(Arrays.asList(USER_ID + ":" + GU), this.world.membershipChecks);
  }

  @Test
  public void softDeletedMembershipIsRefusedAndOnlyTheActiveGateIsAsked() {
    this.world.members.clear();
    this.world.softDeleted.add(USER_ID + ":" + GU);

    assertFalse(this.world.subject().isEligible(SUBMITTED, GU));
    assertEquals("only existActiveCrpUser is consulted", Arrays.asList(USER_ID + ":" + GU),
      this.world.membershipChecks);
  }

  @Test
  public void softDeletedHereAndActiveElsewhereIsRefusedForThisUnit() {
    this.world.members.clear();
    this.world.softDeleted.add(USER_ID + ":" + GU);
    this.world.members.add(USER_ID + ":" + OTHER_GU);

    assertFalse(this.world.subject().isEligible(SUBMITTED, GU));
  }

  @Test
  public void memberOfAnotherUnitOnlyIsRefusedForThisUnit() {
    this.world.members.clear();
    this.world.members.add(USER_ID + ":" + OTHER_GU);

    assertFalse(this.world.subject().isEligible(SUBMITTED, GU));
    assertEquals(Arrays.asList(USER_ID + ":" + GU), this.world.membershipChecks);
  }

  // ---- SEC-009 (amended 2026-10-15, T21): being a CGIAR account plays no part ---------------------------------

  @Test
  public void cgiarAccountWithAnEmptyExclusionGetsACode() {
    this.world.addUser("staff@cgiar.org", 43L, true, true);
    this.world.members.add(43L + ":" + GU);

    assertTrue(this.world.subject().isEligible("staff@cgiar.org", GU));
    assertEquals(Arrays.asList("staff@cgiar.org"), this.world.userLookups);
  }

  @Test
  public void cgiarAccountWithItsDomainExcludedIsRefusedBeforeAnyAccountLookup() {
    this.world.addUnit(GU, "true", "cgiar.org");
    this.world.addUser("staff@cgiar.org", 43L, true, true);
    this.world.members.add(43L + ":" + GU);

    assertFalse(this.world.subject().isEligible("staff@cgiar.org", GU));
    assertEquals(0, this.world.userLookups.size());
  }

  @Test
  public void aCgiarOrgAddressWhoseFlagIsZeroIsTreatedExactlyLikeOneWhoseFlagIsOne() {
    // In the local snapshot 1672 active @cgiar.org accounts carry is_cgiar_user = 0 (Decision Log 2026-10-15): the
    // flag must not decide anything, whichever way it is set.
    for (boolean flag : new boolean[] {false, true}) {
      World flagged = new World();
      flagged.addUnit(GU, "true", "");
      flagged.addUser("staff@cgiar.org", 43L, true, flag);
      flagged.members.add(43L + ":" + GU);
      assertTrue("is_cgiar_user=" + flag, flagged.subject().isEligible("staff@cgiar.org", GU));
    }
  }

  @Test
  public void noDomainIsFixedInCodeCgiarOrgIsRefusedOnlyWhereTheUnitListsIt() {
    this.world.addUser("staff@cgiar.org", 43L, true, true);
    this.world.members.add(43L + ":" + GU);

    this.world.addUnit(GU, "true", "example.org");
    assertTrue(this.world.subject().isEligible("staff@cgiar.org", GU));
    this.world.addUnit(GU, "true", "example.org, CGIAR.org");
    assertFalse(this.world.subject().isEligible("staff@cgiar.org", GU));
  }

  // ---- FN-001: the switch ------------------------------------------------------------------------------

  @Test
  public void switchOffRefusesBeforeAnyAccountLookup() {
    for (String raw : new String[] {"false", "", "   ", null, "no"}) {
      World off = new World();
      off.addUnit(GU, raw, "");
      off.addUser(LOOKED_UP, USER_ID, true, false);
      off.members.add(USER_ID + ":" + GU);

      assertFalse("switch <" + raw + ">", off.subject().isEligible(SUBMITTED, GU));
      assertEquals("switch <" + raw + ">", 0, off.userLookups.size());
      assertEquals("switch <" + raw + ">", 0, off.unitLookups.size());
    }
  }

  @Test
  public void aUnitWithNoSettingsAtAllIsOff() {
    this.world.settings.remove(GU);

    assertFalse(this.world.subject().isEligible(SUBMITTED, GU));
    assertEquals(0, this.world.userLookups.size());
  }

  @Test
  public void theSwitchOfAnotherUnitDoesNotCount() {
    this.world.addUnit(OTHER_GU, "true", "");
    this.world.addUnit(GU, "false", "");

    assertFalse(this.world.subject().isEligible(SUBMITTED, GU));
  }

  // ---- FN-003: the exclusion ---------------------------------------------------------------------------

  @Test
  public void anExcludedDomainIsRefusedBeforeAnyAccountLookup() {
    this.world.addUnit(GU, "true", "other.org, PARTNER.org");

    assertFalse(this.world.subject().isEligible(SUBMITTED, GU));
    assertEquals(0, this.world.userLookups.size());
  }

  @Test
  public void aDomainThatIsNotExcludedIsEligibleWhateverTheListHolds() {
    this.world.addUnit(GU, "true", "other.org,third.org");

    assertTrue(this.world.subject().isEligible(SUBMITTED, GU));
  }

  @Test
  public void subDomainOfAnExcludedDomainIsNotExcludedUnlessListed() {
    this.world.addUnit(GU, "true", "partner.org");
    this.world.addUser("user@sub.partner.org", USER_ID, true, false);

    assertTrue(this.world.subject().isEligible("user@sub.partner.org", GU));

    this.world.addUnit(GU, "true", "partner.org,sub.partner.org");
    assertFalse(this.world.subject().isEligible("user@sub.partner.org", GU));
  }

  @Test
  public void lookalikeDomainsOfAnExcludedDomainAreNotExcluded() {
    this.world.addUnit(GU, "true", "partner.org");
    for (String lookalike : new String[] {"attackerpartner.org", "partner.org.evil.com", "partner.orgx"}) {
      String email = "user@" + lookalike;
      this.world.addUser(email, USER_ID, true, false);
      assertTrue(email, this.world.subject().isEligible(email, GU));
    }
  }

  @Test
  public void unconfiguredFeatureIsRefusedWithoutReadingAnything() {
    this.world.secret = "";

    assertFalse(this.world.subject().isEligible(SUBMITTED, GU));
    assertEquals(0, this.world.specificityReads.size());
    assertEquals(0, this.world.userLookups.size());
  }

  @Test
  public void unknownGlobalUnitIsRefusedEvenWhenItsSwitchIsStoredOn() {
    this.world.settings.put(999L, settingsOf("true", ""));

    assertFalse(this.world.subject().isEligible(SUBMITTED, 999L));
    assertEquals(Arrays.asList(999L), this.world.unitLookups);
    assertEquals(0, this.world.userLookups.size());
  }

  @Test
  public void malformedAndNonAsciiEmailsAreRefusedBeforeAnyLookup() {
    // U+212A (Kelvin sign) lower-cases to ASCII k: it must never reach the user lookup (FN-003).
    for (String email : new String[] {null, "", "no-at-sign", "a@b@partner.org", "@partner.org", "user@",
      "us\u212Aer@partner.org"}) {
      assertFalse("email <" + email + ">", this.world.subject().isEligible(email, GU));
    }
    assertEquals(0, this.world.userLookups.size());
    assertEquals(0, this.world.membershipChecks.size());
    assertEquals("a malformed email never reaches the store either", 0, this.world.specificityReads.size());
  }

  // ---- FN-002 / DD-12: live reads ----------------------------------------------------------------------

  @Test
  public void everyCallReadsTheSwitchAndTheExclusionAgainSoAChangeAppliesOnTheNextRequest() {
    OtpEligibility subject = this.world.subject();

    assertTrue("switch on, exclusion empty", subject.isEligible(SUBMITTED, GU));
    this.world.addUnit(GU, "false", "");
    assertFalse("switch turned off", subject.isEligible(SUBMITTED, GU));
    this.world.addUnit(GU, "true", "");
    assertTrue("switch turned back on", subject.isEligible(SUBMITTED, GU));
    this.world.addUnit(GU, "true", "partner.org");
    assertFalse("domain added to the exclusion", subject.isEligible(SUBMITTED, GU));
    this.world.addUnit(GU, "true", "");
    assertTrue("domain removed from the exclusion", subject.isEligible(SUBMITTED, GU));

    assertEquals("one read per call, none remembered", 5, this.world.specificityReads.size());
  }

  // ---- the request step 4 variant ----------------------------------------------------------------------

  @Test
  public void isOfferedReadsTheTwoSpecificitiesOnlyAndNothingAboutTheAccount() {
    this.world.users.clear();
    this.world.members.clear();

    assertTrue(this.world.subject().isOffered(SUBMITTED, GU));
    assertEquals(Arrays.asList(BOTH_KEYS + "|[" + GU + "]"), this.world.specificityReads);
    assertEquals(0, this.world.userLookups.size());
    assertEquals(0, this.world.unitLookups.size());
    assertEquals(0, this.world.membershipChecks.size());
  }

  @Test
  public void isOfferedIsFalseForASwitchOffAnExcludedDomainAnUnknownUnitAndAMalformedEmail() {
    this.world.addUnit(OTHER_GU, "false", "");
    assertFalse(this.world.subject().isOffered(SUBMITTED, OTHER_GU));

    this.world.addUnit(GU, "true", "partner.org");
    assertFalse(this.world.subject().isOffered(SUBMITTED, GU));

    assertFalse(this.world.subject().isOffered(SUBMITTED, 999L));

    int readsBefore = this.world.specificityReads.size();
    assertFalse(this.world.subject().isOffered("us\u212Aer@partner.org", GU));
    assertEquals("a malformed email is refused before the read", readsBefore, this.world.specificityReads.size());
  }

  // ---- the crpByEmail.do variant -----------------------------------------------------------------------

  @Test
  public void variantIgnoresActiveStateAndMembership() {
    this.world.addUser(LOOKED_UP, USER_ID, false, false);
    this.world.members.clear();

    Map<Long, Boolean> result = this.world.subject().otpEnabledFor(SUBMITTED, this.world.units(GU));

    assertEquals(Collections.singletonMap(GU, Boolean.TRUE), result);
    assertEquals(0, this.world.userLookups.size());
    assertEquals(0, this.world.membershipChecks.size());
  }

  @Test
  public void variantReadsEveryListedUnitAndBothKeysInOneUncachedQuery() {
    this.world.addUnit(OTHER_GU, "false", "");

    Map<Long, Boolean> result = this.world.subject().otpEnabledFor(SUBMITTED, this.world.units(GU, OTHER_GU));

    assertEquals(Boolean.TRUE, result.get(GU));
    assertEquals(Boolean.FALSE, result.get(OTHER_GU));
    assertEquals(2, result.size());
    assertEquals("one query, both keys, every unit", Arrays.asList(BOTH_KEYS + "|[" + GU + ", " + OTHER_GU + "]"),
      this.world.specificityReads);
  }

  @Test
  public void variantOffersTheCodeToACgiarDomainOnlyWhereItIsNotExcluded() {
    this.world.addUnit(GU, "true", "");
    this.world.addUnit(OTHER_GU, "true", "cgiar.org");

    Map<Long, Boolean> result =
      this.world.subject().otpEnabledFor("staff@cgiar.org", this.world.units(GU, OTHER_GU));

    assertEquals(Boolean.TRUE, result.get(GU));
    assertEquals(Boolean.FALSE, result.get(OTHER_GU));
    assertEquals(1, this.world.specificityReads.size());
  }

  @Test
  public void variantIsFalseForADomainThatIsExcludedOrASwitchThatIsOff() {
    this.world.addUnit(GU, "true", "elsewhere.net");
    this.world.addUnit(OTHER_GU, "", "");

    Map<Long, Boolean> result =
      this.world.subject().otpEnabledFor("user@elsewhere.net", this.world.units(GU, OTHER_GU));

    assertEquals(Boolean.FALSE, result.get(GU));
    assertEquals(Boolean.FALSE, result.get(OTHER_GU));
  }

  @Test
  public void variantIsFalseEverywhereAndReadsNothingWhenUnconfigured() {
    this.world.secret = null;

    Map<Long, Boolean> result = this.world.subject().otpEnabledFor(SUBMITTED, this.world.units(GU));

    assertEquals(Collections.singletonMap(GU, Boolean.FALSE), result);
    assertEquals(0, this.world.specificityReads.size());
  }

  @Test
  public void variantIsFalseForAnEmailWithoutADomainAndReadsNothing() {
    Map<Long, Boolean> result = this.world.subject().otpEnabledFor("us\u212Aer@partner.org", this.world.units(GU));

    assertEquals(Collections.singletonMap(GU, Boolean.FALSE), result);
    assertEquals(0, this.world.specificityReads.size());
  }

  // ---- static lock: no is_cgiar_user and no Cognito specificity on the code path (SEC-009, DD-13) -------------

  @Test
  public void theCodePathNeverReadsIsCgiarUserOrTheCognitoSpecificity() throws IOException {
    // The request and verify actions, the predicate and the pure policy. CrpByUserEmailAction also answers the
    // Cognito flag and the user's own isCgiarUser for the login wizard, so only its otpEnabled method is locked.
    String[] files = {"security/otp/OtpEligibility.java", "security/otp/OtpDomainPolicy.java",
      "action/home/OtpRequestCodeAction.java", "action/home/OtpVerifyCodeAction.java"};
    for (String file : files) {
      assertNoCgiarOrCognitoRule(file, withoutComments(source(file)));
    }
    String action = source("action/json/global/CrpByUserEmailAction.java");
    int start = action.indexOf("private Map<Long, Boolean> otpEnabledByUnit(");
    int end = action.indexOf("public List<Map<String, Object>> getCrps()");
    assertTrue("otpEnabledByUnit found", start > 0 && end > start);
    assertNoCgiarOrCognitoRule("CrpByUserEmailAction.otpEnabledByUnit", withoutComments(action.substring(start, end)));
  }

  @Test
  public void thePredicateHoldsNoManagerThatCouldReadTheCognitoSpecificity() throws IOException {
    String code = withoutComments(source("security/otp/OtpEligibility.java"));

    assertFalse(code.contains("CustomParameterManager"));
    assertFalse(code.contains("ParameterManager"));
  }

  private static String source(String relativePath) throws IOException {
    return new String(Files.readAllBytes(Paths.get("src/main/java/org/cgiar/ccafs/marlo/" + relativePath)),
      StandardCharsets.UTF_8);
  }

  private static void assertNoCgiarOrCognitoRule(String label, String code) {
    for (String forbidden : new String[] {"isCgiarUser", "CgiarUser", "is_cgiar_user", "CognitoAuthSpecificity",
      "cognito_auth_active", "COGNITO_AUTH_ACTIVE", "\"cgiar.org\""}) {
      assertFalse(label + " must not mention " + forbidden, code.contains(forbidden));
    }
  }

  /** Drops block comments and line comments, so prose that explains the removed rule is not read as code. */
  private static String withoutComments(String source) {
    return source.replaceAll("(?s)/\\*.*?\\*/", "").replaceAll("(?m)//.*$", "");
  }

  // ---- the world and its fakes -------------------------------------------------------------------------

  private static Map<String, String> settingsOf(String switchValue, String exclusion) {
    Map<String, String> values = new HashMap<>();
    values.put(SWITCH_KEY, switchValue);
    values.put(EXCLUSION_KEY, exclusion);
    return values;
  }

  /** The state the fakes answer from, and the calls they recorded. */
  private static final class World {

    String secret = SECRET_32_BYTES;
    final Map<String, User> users = new HashMap<>();
    final Map<Long, GlobalUnit> unitsById = new HashMap<>();
    /** Raw effective values by Global Unit id and then by key, as the uncached store would answer them. */
    final Map<Long, Map<String, String>> settings = new HashMap<>();
    final Set<String> members = new HashSet<>();
    /** crp_users rows with is_active=0 (soft delete): seen only by the activity-blind existCrpUser. */
    final Set<String> softDeleted = new HashSet<>();

    final List<String> userLookups = new ArrayList<>();
    final List<Long> unitLookups = new ArrayList<>();
    final List<String> specificityReads = new ArrayList<>();
    final List<String> membershipChecks = new ArrayList<>();

    void addUser(String email, long id, boolean active, boolean cgiar) {
      User user = new User();
      user.setId(Long.valueOf(id));
      user.setEmail(email);
      user.setActive(active);
      user.setCgiarUser(cgiar);
      this.users.put(email, user);
    }

    void addUnit(long id, String switchValue, String exclusion) {
      GlobalUnit unit = new GlobalUnit();
      unit.setId(Long.valueOf(id));
      GlobalUnitType type = new GlobalUnitType();
      type.setId(Long.valueOf(GU_TYPE));
      unit.setGlobalUnitType(type);
      this.unitsById.put(Long.valueOf(id), unit);
      this.settings.put(Long.valueOf(id), settingsOf(switchValue, exclusion));
    }

    List<GlobalUnit> units(long... ids) {
      List<GlobalUnit> result = new ArrayList<>();
      for (long id : ids) {
        result.add(this.unitsById.get(Long.valueOf(id)));
      }
      return result;
    }

    OtpEligibility subject() {
      UserManager userManager = fake(UserManager.class, (method, args) -> {
        if ("getUserByEmail".equals(method)) {
          this.userLookups.add((String) args[0]);
          return this.users.get(args[0]);
        }
        return unsupported(method);
      });
      GlobalUnitManager globalUnitManager = fake(GlobalUnitManager.class, (method, args) -> {
        if ("getGlobalUnitById".equals(method)) {
          this.unitLookups.add((Long) args[0]);
          return this.unitsById.get(args[0]);
        }
        return unsupported(method);
      });
      CrpUserManager crpUserManager = fake(CrpUserManager.class, (method, args) -> {
        if ("existActiveCrpUser".equals(method) && args.length == 2) {
          String pair = args[0] + ":" + args[1];
          this.membershipChecks.add(pair);
          return this.members.contains(pair);
        }
        if ("existCrpUser".equals(method) && args.length == 2) {
          // The old, activity-blind gate answers true for the soft-deleted row, so a regression to it is visible.
          String pair = args[0] + ":" + args[1];
          this.membershipChecks.add("any-state " + pair);
          return this.members.contains(pair) || this.softDeleted.contains(pair);
        }
        return unsupported(method);
      });
      OtpChallengeManager challengeManager = fake(OtpChallengeManager.class, (method, args) -> {
        if ("specificityValuesFor".equals(method)) {
          Collection<?> keys = (Collection<?>) args[0];
          Collection<?> ids = (Collection<?>) args[1];
          this.specificityReads.add(keys + "|" + ids);
          Map<Long, Map<String, String>> answer = new HashMap<>();
          for (Object id : ids) {
            Map<String, String> stored = this.settings.get(id);
            Map<String, String> values = new HashMap<>();
            for (Object key : keys) {
              String raw = stored == null ? null : stored.get(key);
              values.put((String) key, raw == null ? "" : raw);
            }
            answer.put((Long) id, values);
          }
          return answer;
        }
        return unsupported(method);
      });
      return new OtpEligibility(new OtpKeys(this.secret), userManager, globalUnitManager, crpUserManager,
        challengeManager);
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
}
