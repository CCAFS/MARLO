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
import org.cgiar.ccafs.marlo.data.manager.CustomParameterManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.OtpChallengeManager;
import org.cgiar.ccafs.marlo.data.manager.ParameterManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CustomParameter;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitType;
import org.cgiar.ccafs.marlo.data.model.User;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
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
 * CHG-OTP-LOGIN-001-T06: {@link OtpEligibility} (FN-004, FN-005, FN-015, SEC-009, FN-001) against hand-written fakes.
 * <p>
 * Every fake answers from data keyed by the arguments it receives, never with a constant, and records those
 * arguments. A fake that returned {@code true} whatever it was asked would pass every "eligible" case and prove
 * nothing, so each case also asserts what the collaborators were called with. Each refusal test changes exactly one
 * fact of an otherwise eligible world, so a refusal can only come from that fact.
 * <p>
 * The keys and ids asserted are literals from the spec ({@code crp_otp_allowed_email_domains},
 * {@code cognito_auth_active}), not the production constants, and the submitted email is mixed-case with padding while
 * the lookup is asserted against its trimmed lower-case form (P-12).
 */
public class OtpEligibilityTest {

  /** base64 of the 32 bytes 0x00..0x1f. */
  private static final String SECRET_32_BYTES = "AAECAwQFBgcICQoLDA0ODxAREhMUFRYXGBkaGxwdHh8=";

  private static final String SUBMITTED = "  User@Partner.ORG ";
  private static final String LOOKED_UP = "user@partner.org";
  private static final String ALLOW_LIST_KEY = "crp_otp_allowed_email_domains";
  private static final String COGNITO_KEY = "cognito_auth_active";

  private static final long GU = 7L;
  private static final long OTHER_GU = 8L;
  private static final long USER_ID = 42L;
  private static final long GU_TYPE = 3L;

  private World world;

  @Before
  public void setUp() {
    this.world = new World();
    this.world.addUnit(GU, "partner.org");
    this.world.addUser(LOOKED_UP, USER_ID, true, false);
    this.world.members.add(USER_ID + ":" + GU);
  }

  // ---- the full predicate ------------------------------------------------------------------------------

  @Test
  public void eligibleUserIsEligibleAndEveryCollaboratorReceivesTheRightArguments() {
    assertTrue(this.world.subject().isEligible(SUBMITTED, GU));

    assertEquals(Arrays.asList(LOOKED_UP), this.world.userLookups);
    assertEquals(Arrays.asList(GU), this.world.unitLookups);
    assertEquals(Arrays.asList(ALLOW_LIST_KEY + "|[" + GU + "]"), this.world.allowListReads);
    assertEquals(Arrays.asList(USER_ID + ":" + GU), this.world.membershipChecks);
    assertEquals("a non-CGIAR user never reaches the Cognito flag", 0, this.world.cognitoOverrideReads.size());
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

  @Test
  public void cgiarUserOnACognitoActiveUnitIsRefused() {
    this.world.addUser(LOOKED_UP, USER_ID, true, true);
    this.world.cognitoOverrides.put(GU, "true");

    assertFalse(this.world.subject().isEligible(SUBMITTED, GU));
    assertEquals(Arrays.asList(COGNITO_KEY + "|" + GU), this.world.cognitoOverrideReads);
  }

  @Test
  public void cgiarUserOnAUnitWhereCognitoIsInactiveIsEligible() {
    this.world.addUser(LOOKED_UP, USER_ID, true, true);
    this.world.cognitoOverrides.put(GU, "false");

    assertTrue(this.world.subject().isEligible(SUBMITTED, GU));
    assertEquals(Arrays.asList(COGNITO_KEY + "|" + GU), this.world.cognitoOverrideReads);
  }

  @Test
  public void nonCgiarUserOnACognitoActiveUnitIsEligible() {
    this.world.cognitoOverrides.put(GU, "true");

    assertTrue(this.world.subject().isEligible(SUBMITTED, GU));
  }

  @Test
  public void emailDomainNotInTheAllowListIsRefused() {
    this.world.allowLists.put(GU, "other.org,third.org");

    assertFalse(this.world.subject().isEligible(SUBMITTED, GU));
    assertEquals(0, this.world.userLookups.size());
  }

  @Test
  public void subDomainOfAnAllowedDomainIsRefused() {
    this.world.allowLists.put(GU, "partner.org");
    this.world.addUser("user@sub.partner.org", USER_ID, true, false);

    assertFalse(this.world.subject().isEligible("user@sub.partner.org", GU));
    assertEquals(0, this.world.userLookups.size());
  }

  @Test
  public void cgiarOrgInTheAllowListIsIgnored() {
    this.world.allowLists.put(GU, "cgiar.org,partner.org");
    this.world.addUser("staff@cgiar.org", 43L, true, true);
    this.world.members.add(43L + ":" + GU);

    assertFalse(this.world.subject().isEligible("staff@cgiar.org", GU));
    assertEquals(0, this.world.userLookups.size());
  }

  @Test
  public void blankEmptyOrMissingAllowListDisablesTheUnit() {
    for (String raw : new String[] {"", "   ", null}) {
      this.world.allowLists.put(GU, raw);
      assertFalse("allow-list <" + raw + ">", this.world.subject().isEligible(SUBMITTED, GU));
    }
    this.world.allowLists.remove(GU);
    assertFalse("no entry", this.world.subject().isEligible(SUBMITTED, GU));
    assertEquals(0, this.world.userLookups.size());
  }

  @Test
  public void unconfiguredFeatureIsRefusedWithoutReadingAnything() {
    this.world.secret = "";

    assertFalse(this.world.subject().isEligible(SUBMITTED, GU));
    assertEquals(0, this.world.allowListReads.size());
    assertEquals(0, this.world.userLookups.size());
  }

  @Test
  public void unknownGlobalUnitIsRefusedEvenWhenAnAllowListIsStoredForIt() {
    this.world.allowLists.put(999L, "partner.org");

    assertFalse(this.world.subject().isEligible(SUBMITTED, 999L));
    assertEquals(Arrays.asList(999L), this.world.unitLookups);
    assertEquals(0, this.world.userLookups.size());
  }

  @Test
  public void malformedAndNonAsciiEmailsAreRefusedBeforeAnyLookup() {
    // U+212A (Kelvin sign) lower-cases to ASCII k: it must never reach the user lookup (FN-003).
    for (String email : new String[] {null, "", "no-at-sign", "a@b@partner.org", "@partner.org", "user@",
      "usKer@partner.org"}) {
      assertFalse("email <" + email + ">", this.world.subject().isEligible(email, GU));
    }
    assertEquals(0, this.world.userLookups.size());
    assertEquals(0, this.world.membershipChecks.size());
  }

  // ---- the crpByEmail.do variant -----------------------------------------------------------------------

  @Test
  public void variantIgnoresActiveStateAndMembership() {
    this.world.addUser(LOOKED_UP, USER_ID, false, false);
    this.world.members.clear();

    Map<Long, Boolean> result = this.world.subject().otpEnabledFor(SUBMITTED, false, this.world.units(GU));

    assertEquals(Collections.singletonMap(GU, Boolean.TRUE), result);
    assertEquals(0, this.world.userLookups.size());
    assertEquals(0, this.world.membershipChecks.size());
  }

  @Test
  public void variantReadsEveryListedUnitInOneUncachedQuery() {
    this.world.addUnit(OTHER_GU, "other.org");

    Map<Long, Boolean> result = this.world.subject().otpEnabledFor(SUBMITTED, false, this.world.units(GU, OTHER_GU));

    assertEquals(Boolean.TRUE, result.get(GU));
    assertEquals(Boolean.FALSE, result.get(OTHER_GU));
    assertEquals(2, result.size());
    assertEquals(Arrays.asList(ALLOW_LIST_KEY + "|[" + GU + ", " + OTHER_GU + "]"), this.world.allowListReads);
  }

  @Test
  public void variantRefusesACgiarUserOnlyWhereCognitoIsActive() {
    this.world.addUnit(OTHER_GU, "partner.org");
    this.world.cognitoOverrides.put(GU, "true");
    this.world.cognitoOverrides.put(OTHER_GU, "false");

    Map<Long, Boolean> result = this.world.subject().otpEnabledFor(SUBMITTED, true, this.world.units(GU, OTHER_GU));

    assertEquals(Boolean.FALSE, result.get(GU));
    assertEquals(Boolean.TRUE, result.get(OTHER_GU));
    assertEquals(Arrays.asList(COGNITO_KEY + "|" + GU, COGNITO_KEY + "|" + OTHER_GU), this.world.cognitoOverrideReads);
  }

  @Test
  public void variantIsFalseForADomainThatIsNotAllowedOrBlank() {
    this.world.addUnit(OTHER_GU, "");

    Map<Long, Boolean> result = this.world.subject().otpEnabledFor("user@elsewhere.net", false,
      this.world.units(GU, OTHER_GU));

    assertEquals(Boolean.FALSE, result.get(GU));
    assertEquals(Boolean.FALSE, result.get(OTHER_GU));
  }

  @Test
  public void variantIsFalseEverywhereAndReadsNothingWhenUnconfigured() {
    this.world.secret = null;

    Map<Long, Boolean> result = this.world.subject().otpEnabledFor(SUBMITTED, false, this.world.units(GU));

    assertEquals(Collections.singletonMap(GU, Boolean.FALSE), result);
    assertEquals(0, this.world.allowListReads.size());
  }

  @Test
  public void variantIsFalseForAnEmailWithoutADomainAndReadsNothing() {
    Map<Long, Boolean> result = this.world.subject().otpEnabledFor("usKer@partner.org", false,
      this.world.units(GU));

    assertEquals(Collections.singletonMap(GU, Boolean.FALSE), result);
    assertEquals(0, this.world.allowListReads.size());
  }

  // ---- the world and its fakes -------------------------------------------------------------------------

  /** The state the fakes answer from, and the calls they recorded. */
  private static final class World {

    String secret = SECRET_32_BYTES;
    final Map<String, User> users = new HashMap<>();
    final Map<Long, GlobalUnit> unitsById = new HashMap<>();
    final Map<Long, String> allowLists = new HashMap<>();
    final Set<String> members = new HashSet<>();
    /** crp_users rows with is_active=0 (soft delete): seen only by the activity-blind existCrpUser. */
    final Set<String> softDeleted = new HashSet<>();
    final Map<Long, String> cognitoOverrides = new HashMap<>();

    final List<String> userLookups = new ArrayList<>();
    final List<Long> unitLookups = new ArrayList<>();
    final List<String> allowListReads = new ArrayList<>();
    final List<String> membershipChecks = new ArrayList<>();
    final List<String> cognitoOverrideReads = new ArrayList<>();

    void addUser(String email, long id, boolean active, boolean cgiar) {
      User user = new User();
      user.setId(Long.valueOf(id));
      user.setEmail(email);
      user.setActive(active);
      user.setCgiarUser(cgiar);
      this.users.put(email, user);
    }

    void addUnit(long id, String allowList) {
      GlobalUnit unit = new GlobalUnit();
      unit.setId(Long.valueOf(id));
      GlobalUnitType type = new GlobalUnitType();
      type.setId(Long.valueOf(GU_TYPE));
      unit.setGlobalUnitType(type);
      this.unitsById.put(Long.valueOf(id), unit);
      this.allowLists.put(Long.valueOf(id), allowList);
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
        if ("allowListFor".equals(method)) {
          Collection<?> ids = (Collection<?>) args[1];
          this.allowListReads.add(args[0] + "|" + ids);
          Map<Long, String> answer = new HashMap<>();
          for (Object id : ids) {
            String raw = this.allowLists.get(id);
            answer.put((Long) id, raw == null ? "" : raw);
          }
          return answer;
        }
        return unsupported(method);
      });
      CustomParameterManager customParameterManager = fake(CustomParameterManager.class, (method, args) -> {
        if ("getCustomParameterByParameterKeyAndGlobalUnitId".equals(method)) {
          this.cognitoOverrideReads.add(args[0] + "|" + args[1]);
          String value = this.cognitoOverrides.get(args[1]);
          if (value == null) {
            return null;
          }
          CustomParameter override = new CustomParameter();
          override.setValue(value);
          return override;
        }
        return unsupported(method);
      });
      ParameterManager parameterManager = fake(ParameterManager.class, (method, args) -> {
        if ("getParameterByKey".equals(method)) {
          return null;
        }
        return unsupported(method);
      });
      return new OtpEligibility(new OtpKeys(this.secret), userManager, globalUnitManager, crpUserManager,
        challengeManager, customParameterManager, parameterManager);
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
