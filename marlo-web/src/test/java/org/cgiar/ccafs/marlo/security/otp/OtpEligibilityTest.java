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
import org.cgiar.ccafs.marlo.data.model.Parameter;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.otp.OtpEligibility.Basis;
import org.cgiar.ccafs.marlo.security.otp.SignInMethodRule.Methods;

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

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.subject.Subject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * CHG-OTP-LOGIN-001-T06, T21 and CHG-PWD-SWITCH-001-T03: {@link OtpEligibility} (FN-001, FN-003, FN-004, FN-005,
 * FN-015, SEC-009, DD-13; password switch FN-004 to FN-006, SEC-003, SEC-004, OPS-002, DD-3, DD-4, DD-6) against
 * hand-written fakes.
 * <p>
 * Every fake answers from data keyed by the arguments it receives, never with a constant, and records those
 * arguments. A fake that returned {@code true} whatever it was asked would pass every "eligible" case and prove
 * nothing, so each case also asserts what the collaborators were called with. Each refusal test changes exactly one
 * fact of an otherwise eligible world, so a refusal can only come from that fact.
 * <p>
 * The keys and ids asserted are literals from the spec ({@code system_otp_login_active},
 * {@code system_otp_excluded_email_domains}, {@code system_password_login_active}, {@code cognito_auth_active}), not
 * the production constants, and the submitted email is mixed-case with padding while the lookup is asserted against
 * its trimmed lower-case form (P-12).
 * <p>
 * The Cognito resolver and {@code users.is_cgiar_user} are observed by counting doubles: the two managers the
 * resolver reads record every call with its arguments, and the users are a subclass that counts reads of
 * {@code isCgiarUser()}. They replace the two T21 static locks on the source text (design DD-4, P-16): what is proven
 * is the behaviour, namely that a password-on unit makes zero Cognito reads and zero {@code is_cgiar_user} reads, and
 * that a password-off unit reads the flag for every account before the lookup. The Global Unit ids (7, 8) and the type
 * id (3) are disjoint, so a unit id passed where the type id belongs is visible.
 * <p>
 * The password gate (CHG-PWD-SWITCH-001-T05, DD-5, SEC-001, OPS-001) is covered by the tests whose names start with
 * {@code theGate}. The membership double answers by the exact stored email, as the real equality does, and records what
 * it was asked, so a gate that asked with what was typed instead of the stored email finds no membership and a test
 * fails.
 */
public class OtpEligibilityTest {

  /** base64 of the 32 bytes 0x00..0x1f. */
  private static final String SECRET_32_BYTES = "AAECAwQFBgcICQoLDA0ODxAREhMUFRYXGBkaGxwdHh8=";

  private static final String SUBMITTED = "  User@Partner.ORG ";
  private static final String LOOKED_UP = "user@partner.org";
  private static final String SWITCH_KEY = "system_otp_login_active";
  private static final String EXCLUSION_KEY = "system_otp_excluded_email_domains";
  private static final String PASSWORD_KEY = "system_password_login_active";
  private static final String ALL_KEYS = "[" + SWITCH_KEY + ", " + EXCLUSION_KEY + ", " + PASSWORD_KEY + "]";
  private static final String COGNITO_KEY = "cognito_auth_active";

  private static final long GU = 7L;
  private static final long OTHER_GU = 8L;
  private static final long USER_ID = 42L;
  private static final long GU_TYPE = 3L;

  private static final String LAST_RESORT_LINE = "auth.password.last_resort ";
  private static final String REFUSED_LINE = "auth.password.refused ";
  private static final String GATE_ERROR_LINE = "auth.password.gate ";

  private World world;
  private Logger capturedLogger;
  private ListAppender<ILoggingEvent> appender;

  @Before
  public void setUp() {
    this.world = new World();
    this.world.addUnit(GU, "true", "");
    this.world.addUser(LOOKED_UP, USER_ID, true, false);
    this.world.members.add(USER_ID + ":" + GU);
    this.capturedLogger = (Logger) LoggerFactory.getLogger(OtpEligibility.class);
    this.appender = new ListAppender<ILoggingEvent>();
    this.appender.start();
    this.capturedLogger.addAppender(this.appender);
  }

  @After
  public void tearDown() {
    this.capturedLogger.detachAppender(this.appender);
  }

  // ---- the full predicate ------------------------------------------------------------------------------

  @Test
  public void eligibleUserIsEligibleAndEveryCollaboratorReceivesTheRightArguments() {
    assertTrue(this.world.subject().isEligible(SUBMITTED, GU));

    assertEquals(Arrays.asList(LOOKED_UP), this.world.userLookups);
    assertEquals(Arrays.asList(GU), this.world.unitLookups);
    assertEquals("all three keys in one read, for the one unit", Arrays.asList(ALL_KEYS + "|[" + GU + "]"),
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

  // ---- SEC-009 (amended 2026-10-15, T21): on a password-on unit being a CGIAR account plays no part ----------

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
    this.world.settings.put(999L, settingsOf("true", "", "true"));

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
  public void isOfferedReadsTheSpecificitiesOnlyAndNothingAboutTheAccount() {
    this.world.users.clear();
    this.world.members.clear();

    assertTrue(this.world.subject().isOffered(SUBMITTED, GU));
    assertEquals(Arrays.asList(ALL_KEYS + "|[" + GU + "]"), this.world.specificityReads);
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

  // ---- CHG-PWD-SWITCH-001-T03: step 4 admits the default-method override (rule 3d) ---------------------------

  @Test
  public void ac4WithThePasswordOffTheCodeIsOfferedWhileTheCodeSwitchIsOff() {
    this.world.addUnit(GU, "false", "", "false");

    assertTrue(this.world.subject().isOffered(SUBMITTED, GU));
  }

  @Test
  public void ac5WithThePasswordOffTheCodeIsOfferedWhileTheDomainIsExcluded() {
    this.world.addUnit(GU, "true", "other.org, PARTNER.org", "false");

    assertTrue(this.world.subject().isOffered(SUBMITTED, GU));
  }

  @Test
  public void theOverrideIsAccountBlindAndReadsNeitherTheAccountNorTheCognitoFlag() {
    this.world.addUnit(GU, "false", "", "false");
    this.world.users.clear();
    this.world.members.clear();

    assertTrue(this.world.subject().isOffered(SUBMITTED, GU));

    assertEquals(Arrays.asList(ALL_KEYS + "|[" + GU + "]"), this.world.specificityReads);
    assertEquals(0, this.world.userLookups.size());
    assertEquals(0, this.world.unitLookups.size());
    assertEquals(0, this.world.membershipChecks.size());
    assertEquals("step 4 cannot tell a Cognito account: not even the flag is read", 0,
      this.world.cognitoReads.size());
  }

  @Test
  public void theOverrideNeedsAValidCodeKeyAndAWellFormedEmail() {
    this.world.addUnit(GU, "false", "", "false");

    this.world.secret = "";
    assertFalse("FN-006: with no key the code is never offered, the password stays",
      this.world.subject().isOffered(SUBMITTED, GU));

    this.world.secret = SECRET_32_BYTES;
    int readsBefore = this.world.specificityReads.size();
    assertFalse(this.world.subject().isOffered("us\u212Aer@partner.org", GU));
    assertFalse(this.world.subject().isOffered(null, GU));
    assertFalse(this.world.subject().isOffered("no-at-sign", GU));
    assertEquals("a malformed email is refused before the read", readsBefore, this.world.specificityReads.size());
  }

  @Test
  public void onlyAPasswordSwitchThatIsExactlyFalseAdmitsTheOverride() {
    // DD-2: any other value, a missing one included, is on, so the code switch decides as it does today (FN-003).
    for (String raw : new String[] {"true", "TRUE", " true ", "", "   ", null, "yes", "1", "no", "off", "garbage"}) {
      World on = new World();
      on.addUnit(GU, "false", "", raw);
      assertFalse("password <" + raw + "> reads as on, the code switch is off", on.subject().isOffered(SUBMITTED, GU));
    }
    for (String raw : new String[] {"false", "FALSE", " False ", "false\t"}) {
      World off = new World();
      off.addUnit(GU, "false", "", raw);
      assertTrue("password <" + raw + "> reads as off", off.subject().isOffered(SUBMITTED, GU));
    }
  }

  @Test
  public void anUnknownUnitIsNotRescuedByTheOverride() {
    this.world.addUnit(GU, "false", "", "false");

    assertFalse("999 has no settings: its password reads as on and its code as off",
      this.world.subject().isOffered(SUBMITTED, 999L));
  }

  @Test
  public void ac8ThePasswordSwitchIsReadAgainOnEveryCallSoAChangeAppliesOnTheNextRequest() {
    OtpEligibility subject = this.world.subject();

    this.world.addUnit(GU, "false", "", "true");
    assertFalse("password on, code off: today's rule", subject.isOffered(SUBMITTED, GU));
    this.world.addUnit(GU, "false", "", "false");
    assertTrue("password turned off", subject.isOffered(SUBMITTED, GU));
    this.world.addUnit(GU, "false", "", "true");
    assertFalse("password turned back on", subject.isOffered(SUBMITTED, GU));

    assertEquals("one read per call, none remembered", 3, this.world.specificityReads.size());
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
  public void variantReadsEveryListedUnitAndAllThreeKeysInOneUncachedQuery() {
    this.world.addUnit(OTHER_GU, "false", "");

    Map<Long, Boolean> result = this.world.subject().otpEnabledFor(SUBMITTED, this.world.units(GU, OTHER_GU));

    assertEquals(Boolean.TRUE, result.get(GU));
    assertEquals(Boolean.FALSE, result.get(OTHER_GU));
    assertEquals(2, result.size());
    assertEquals("one query, all three keys, every unit", Arrays.asList(ALL_KEYS + "|[" + GU + ", " + OTHER_GU + "]"),
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

  // ---- CHG-PWD-SWITCH-001-T03: the override at the full predicate (FN-004, FN-005, SEC-004) -------------------
  // These replace the two T21 static locks on the source text (design DD-4, P-16). The password-on side, which the
  // locks protected, is the counting test further down.

  @Test
  public void ac4aWithThePasswordOffAMemberIsEligibleWhileTheCodeSwitchIsOff() {
    this.world.addUnit(GU, "false", "", "false");

    assertTrue(this.world.subject().isEligible(SUBMITTED, GU));
    assertEquals(Arrays.asList(LOOKED_UP), this.world.userLookups);
    assertEquals(Arrays.asList(USER_ID + ":" + GU), this.world.membershipChecks);
  }

  @Test
  public void ac5aWithThePasswordOffAMemberIsEligibleWhileTheDomainIsExcluded() {
    this.world.addUnit(GU, "true", "other.org, PARTNER.org", "false");

    assertTrue(this.world.subject().isEligible(SUBMITTED, GU));
  }

  @Test
  public void theOverrideWidensOnlyTheSwitchAndTheExclusionNeverTheAccountChecks() {
    // Unknown, inactive, non-member and soft-deleted accounts stay refused with the password off (FN-004 says "a
    // member"): only the code switch and the exclusion are bypassed.
    for (String fact : new String[] {"unknown", "inactive", "non-member", "soft-deleted"}) {
      World w = new World();
      w.addUnit(GU, "false", "", "false");
      w.addUser(LOOKED_UP, USER_ID, true, false);
      w.members.add(USER_ID + ":" + GU);
      if ("unknown".equals(fact)) {
        w.users.clear();
      } else if ("inactive".equals(fact)) {
        w.addUser(LOOKED_UP, USER_ID, false, false);
      } else if ("non-member".equals(fact)) {
        w.members.clear();
      } else {
        w.members.clear();
        w.softDeleted.add(USER_ID + ":" + GU);
      }

      assertFalse(fact, w.subject().isEligible(SUBMITTED, GU));
    }
  }

  @Test
  public void theOverrideNeverSendsACodeWithoutAKeyOrToAMalformedEmail() {
    this.world.addUnit(GU, "false", "", "false");
    this.world.secret = "";
    assertFalse("FN-006: no key", this.world.subject().isEligible(SUBMITTED, GU));
    assertEquals(0, this.world.specificityReads.size());

    this.world.secret = SECRET_32_BYTES;
    assertFalse(this.world.subject().isEligible("usKer@partner.org", GU));
    assertFalse(this.world.subject().isEligible(null, GU));
    assertEquals("a malformed email is refused before any read", 0, this.world.specificityReads.size());
  }

  // ---- eligibilityBasis: why the code is available (design 11, via=password_off) ----------------------------

  @Test
  public void theBasisSaysWhetherTheUnitOffersTheCodeOnItsOwnOrOnlyThePasswordOffOverrideDoes() {
    // code switch, exclusion, password switch, is_cgiar_user, the unit's own cognito_auth_active row, the basis.
    // Written by hand from design 1: the override is the cause exactly when the code would not be offered with the
    // password on.
    Object[][] rows = {
      {"true", "", "true", false, null, Basis.UNIT_SETTINGS},
      {"false", "", "true", false, null, Basis.NONE},
      {"true", "partner.org", "true", false, null, Basis.NONE},
      {"true", "", "false", false, null, Basis.UNIT_SETTINGS},
      {"false", "", "false", false, null, Basis.PASSWORD_SWITCH_OFF},
      {"true", "partner.org", "false", false, null, Basis.PASSWORD_SWITCH_OFF},
      {"true", "", "", false, null, Basis.UNIT_SETTINGS},
      {"false", "", "false", true, null, Basis.PASSWORD_SWITCH_OFF},
      {"false", "", "false", true, "false", Basis.PASSWORD_SWITCH_OFF},
      {"true", "", "false", true, "true", Basis.UNIT_SETTINGS},
      {"false", "", "false", true, "true", Basis.NONE},
      {"true", "partner.org", "false", true, "true", Basis.NONE}};
    for (Object[] row : rows) {
      World w = new World();
      w.addUnit(GU, (String) row[0], (String) row[1], (String) row[2]);
      if (row[4] != null) {
        w.cognitoOverrides.put(Long.valueOf(GU), (String) row[4]);
      }
      w.addUser(LOOKED_UP, USER_ID, true, (Boolean) row[3]);
      w.members.add(USER_ID + ":" + GU);
      String label = "code " + row[0] + ", exclusion <" + row[1] + ">, password <" + row[2] + ">, cgiar " + row[3]
        + ", cognito row " + row[4];

      assertEquals(label, row[5], w.subject().eligibilityBasis(SUBMITTED, GU));
      assertEquals(label + ": isEligible is the basis being anything but NONE", row[5] != Basis.NONE,
        w.subject().isEligible(SUBMITTED, GU));
    }
  }

  @Test
  public void theBasisIsNoneForEveryRefusalWhateverTheOverrideSays() {
    for (String fact : new String[] {"unknown", "inactive", "non-member", "no key", "malformed email"}) {
      World w = new World();
      w.addUnit(GU, "false", "", "false");
      w.addUser(LOOKED_UP, USER_ID, true, false);
      w.members.add(USER_ID + ":" + GU);
      String email = SUBMITTED;
      if ("unknown".equals(fact)) {
        w.users.clear();
      } else if ("inactive".equals(fact)) {
        w.addUser(LOOKED_UP, USER_ID, false, false);
      } else if ("non-member".equals(fact)) {
        w.members.clear();
      } else if ("no key".equals(fact)) {
        w.secret = "";
      } else {
        email = "us\u212Aer@partner.org";
      }

      assertEquals(fact, Basis.NONE, w.subject().eligibilityBasis(email, GU));
    }
  }

  @Test
  public void theBasisMakesExactlyTheCallsIsEligibleMakesSoLoggingItAddsNoQueryAndNoTimingDifference() {
    for (boolean accountExists : new boolean[] {true, false}) {
      World forBasis = this.passwordOffWorld(accountExists, false);
      World forPredicate = this.passwordOffWorld(accountExists, false);

      forBasis.subject().eligibilityBasis(LOOKED_UP, GU);
      forPredicate.subject().isEligible(LOOKED_UP, GU);

      assertEquals("account exists: " + accountExists, forPredicate.calls, forBasis.calls);
      assertEquals(forPredicate.cognitoReads, forBasis.cognitoReads);
    }
  }

  @Test
  public void ac6ACognitoAccountIsRefusedUnderTheOverrideAndOnlyAfterTheMembershipCheck() {
    // Scenario "Cognito account unaffected": Cognito on, password off, code switch on, cgiar.org excluded.
    World w = new World();
    w.addUnit(GU, "true", "cgiar.org", "false");
    w.cognitoOverrides.put(Long.valueOf(GU), "true");
    w.addUser("staff@cgiar.org", 43L, true, true);
    w.members.add(43L + ":" + GU);

    assertFalse(w.subject().isEligible("staff@cgiar.org", GU));

    assertEquals("the account was looked up: the refusal comes after it", Arrays.asList("staff@cgiar.org"),
      w.userLookups);
    assertEquals("and after the membership check", Arrays.asList("43:" + GU), w.membershipChecks);
    assertEquals(Arrays.asList("custom:" + COGNITO_KEY + ":" + GU), w.cognitoReads);
  }

  @Test
  public void aCognitoAccountKeepsTheCodeOnlyWhereTheUnitOffersItByItsOwnSettings() {
    // FN-005: Cognito, plus the code only when the code switch is on and the domain is not excluded.
    String[][] rows = {{"true", "", "true"}, {"true", "cgiar.org", "false"}, {"false", "", "false"},
      {"false", "cgiar.org", "false"}};
    for (String[] row : rows) {
      World w = new World();
      w.addUnit(GU, row[0], row[1], "false");
      w.cognitoOverrides.put(Long.valueOf(GU), "true");
      w.addUser("staff@cgiar.org", 43L, true, true);
      w.members.add(43L + ":" + GU);

      assertEquals("code switch " + row[0] + ", exclusion <" + row[1] + ">", Boolean.parseBoolean(row[2]),
        w.subject().isEligible("staff@cgiar.org", GU));
    }
  }

  @Test
  public void anAccountIsACognitoAccountOnlyWhenItIsCgiarAndTheUnitHasCognito() {
    // Password off, code switch off: only the Cognito account loses the code. Four literal rows, not a formula.
    assertTrue("not CGIAR, no Cognito on the unit", this.eligibleUnderTheOverride(false, null));
    assertTrue("CGIAR, no Cognito on the unit: the AD password is replaced by the code", this.eligibleUnderTheOverride(
      true, null));
    assertTrue("CGIAR, Cognito switched off by the unit's own row", this.eligibleUnderTheOverride(true, "false"));
    assertTrue("not CGIAR, Cognito on the unit", this.eligibleUnderTheOverride(false, "true"));
    assertFalse("CGIAR, Cognito on the unit", this.eligibleUnderTheOverride(true, "true"));
  }

  private boolean eligibleUnderTheOverride(boolean cgiar, String cognitoOverride) {
    World w = new World();
    w.addUnit(GU, "false", "", "false");
    if (cognitoOverride != null) {
      w.cognitoOverrides.put(Long.valueOf(GU), cognitoOverride);
    }
    w.addUser(LOOKED_UP, USER_ID, true, cgiar);
    w.members.add(USER_ID + ":" + GU);
    return w.subject().isEligible(SUBMITTED, GU);
  }

  @Test
  public void theCognitoFlagIsResolvedForTheUnitThenItsTypeCatalogAndNothingElse() {
    World noRow = this.passwordOffWorld(true, true);
    noRow.cognitoOverrides.clear();
    noRow.cognitoCatalogDefault = "true";

    assertFalse("a catalog default of true makes the unit a Cognito one", noRow.subject().isEligible(LOOKED_UP, GU));
    assertEquals("the unit's own row first, then the catalog of its TYPE (3), never the unit id",
      Arrays.asList("custom:" + COGNITO_KEY + ":" + GU, "catalog:" + COGNITO_KEY + ":" + GU_TYPE),
      noRow.cognitoReads);

    World off = this.passwordOffWorld(true, true);
    off.cognitoOverrides.clear();
    off.cognitoCatalogDefault = "false";
    assertTrue("a catalog default of false leaves the account a plain one", off.subject().isEligible(LOOKED_UP, GU));
  }

  @Test
  public void sec004TheCognitoFlagIsReadForEveryAccountBeforeTheLookupAndACognitoAccountPaysAMembersQueries() {
    World member = this.passwordOffWorld(true, false);
    World cognito = this.passwordOffWorld(true, true);
    World unknown = this.passwordOffWorld(false, false);

    assertTrue("a member gets the code", member.subject().isEligible(LOOKED_UP, GU));
    assertFalse("a Cognito account does not", cognito.subject().isEligible(LOOKED_UP, GU));
    assertFalse("an unknown account does not", unknown.subject().isEligible(LOOKED_UP, GU));

    List<String> oneRead = Arrays.asList("custom:" + COGNITO_KEY + ":" + GU);
    assertEquals(oneRead, member.cognitoReads);
    assertEquals("the Cognito account pays the same Cognito reads", oneRead, cognito.cognitoReads);
    assertEquals("so does an unknown account: the flag is read before the lookup", oneRead, unknown.cognitoReads);

    assertEquals("the order a member is served in", Arrays.asList("spec", "unit", "custom", "user", "member"),
      member.calls);
    assertEquals("a Cognito account makes exactly a member's calls, refusal included", member.calls, cognito.calls);
    assertEquals("an unknown account stops where its lookup finds nobody, as in T21",
      Arrays.asList("spec", "unit", "custom", "user"), unknown.calls);
  }

  /** A password-off unit with the code switch off and Cognito on, and an account of the given kind in it. */
  private World passwordOffWorld(boolean accountExists, boolean cgiar) {
    World w = new World();
    w.addUnit(GU, "false", "", "false");
    w.cognitoOverrides.put(Long.valueOf(GU), "true");
    if (accountExists) {
      w.addUser(LOOKED_UP, USER_ID, true, cgiar);
      w.members.add(USER_ID + ":" + GU);
    }
    return w;
  }

  // ---- DD-4: on a password-on unit the predicate is T21's, with zero Cognito reads ---------------------------

  @Test
  public void onAPasswordOnUnitTheCognitoFlagIsNeverResolvedAndIsCgiarUserIsNeverRead() {
    // Replaces the two T21 static locks: the behaviour they protected, proven with counting doubles. Cognito is on
    // and the account is a CGIAR one, so a read would find something; "yes" and "garbage" are on too (DD-2).
    for (String raw : new String[] {"true", "TRUE", " true ", "", "   ", null, "yes", "garbage"}) {
      World w = new World();
      w.addUnit(GU, "true", "", raw);
      w.cognitoOverrides.put(Long.valueOf(GU), "true");
      w.addUser(LOOKED_UP, USER_ID, true, true);
      w.members.add(USER_ID + ":" + GU);
      OtpEligibility subject = w.subject();

      assertTrue("password <" + raw + ">: T21's rule, the CGIAR account gets its code",
        subject.isEligible(SUBMITTED, GU));
      assertTrue(subject.isOffered(SUBMITTED, GU));
      subject.methodsFor(SUBMITTED, w.units(GU), Collections.<Long, Boolean>emptyMap());
      subject.otpEnabledFor(SUBMITTED, w.units(GU));

      assertEquals("password <" + raw + ">: zero Cognito reads", Collections.emptyList(), w.cognitoReads);
      assertEquals("password <" + raw + ">: is_cgiar_user never read", 0, w.cgiarReads());
    }
  }

  @Test
  public void theCountingDoublesDoSeeTheReadsOfAPasswordOffUnit() {
    World w = this.passwordOffWorld(true, true);

    w.subject().isEligible(LOOKED_UP, GU);

    assertEquals(Arrays.asList("custom:" + COGNITO_KEY + ":" + GU), w.cognitoReads);
    assertTrue("a CGIAR account's flag is read on a Cognito unit", w.cgiarReads() > 0);
  }

  // ---- methodsFor: the pre-auth lookup's methods per unit (DD-6, FN-006, FN-007, FN-010) -----------------------

  @Test
  public void methodsForAnswersEveryCellOfTheRuleFromOneQueryWithAllThreeKeys() {
    // unit: code switch, exclusion, password switch (raw, as the store answers). Expected values are written by
    // hand from design 1, never derived from the settings.
    this.world.addUnit(11L, "true", "", "true");
    this.world.addUnit(12L, "false", "", "true");
    this.world.addUnit(13L, "false", "", "false");
    this.world.addUnit(14L, "true", "partner.org", "false");
    this.world.addUnit(15L, "false", "", "false");
    this.world.addUnit(16L, "true", "", "false");
    this.world.addUnit(17L, "true", "", "true");
    this.world.addUnit(18L, "false", "", "");
    Map<Long, Boolean> cognito = new HashMap<>();
    cognito.put(15L, Boolean.TRUE);
    cognito.put(16L, Boolean.TRUE);
    cognito.put(17L, Boolean.TRUE);

    Map<Long, Methods> result =
      this.world.subject().methodsFor(SUBMITTED, this.world.units(11, 12, 13, 14, 15, 16, 17, 18), cognito);

    assertEquals("one query, all three keys, every unit",
      Arrays.asList(ALL_KEYS + "|[11, 12, 13, 14, 15, 16, 17, 18]"), this.world.specificityReads);
    assertEquals(Arrays.asList(11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L), new ArrayList<>(result.keySet()));
    assertMethods("11 password on, code on", result.get(11L), false, true, true, false);
    assertMethods("12 password on, code off", result.get(12L), false, false, true, false);
    assertMethods("13 AC-4 password off, code switch off", result.get(13L), false, true, false, false);
    assertMethods("14 AC-5 password off, domain excluded", result.get(14L), false, true, false, false);
    assertMethods("15 AC-6 Cognito, password off, code off", result.get(15L), true, false, false, false);
    assertMethods("16 Cognito, password off, code on", result.get(16L), true, true, false, false);
    assertMethods("17 SEC-003 Cognito, password on", result.get(17L), true, true, false, false);
    assertMethods("18 AC-1 a missing password value is on", result.get(18L), false, false, true, false);
  }

  private static void assertMethods(String label, Methods methods, boolean cognito, boolean code, boolean password,
    boolean needsEmail) {
    assertEquals(label + ": cognito", cognito, methods.isCognito());
    assertEquals(label + ": code (otpEnabled)", code, methods.isCode());
    assertEquals(label + ": password (passwordEnabled)", password, methods.isPassword());
    assertEquals(label + ": needsEmail", needsEmail, methods.isNeedsEmail());
  }

  @Test
  public void ac7MethodsForRunsItsOneQueryAlsoWithoutACodeKeyAndTheLastResortIsThePassword() {
    this.world.secret = "";
    this.world.addUnit(OTHER_GU, "true", "", "false");
    Map<Long, Boolean> cognito = new HashMap<>();
    cognito.put(OTHER_GU, Boolean.FALSE);

    Map<Long, Methods> result =
      this.world.subject().methodsFor(SUBMITTED, this.world.units(GU, OTHER_GU), cognito);

    assertEquals("the query runs without a key: passwordEnabled needs the switch",
      Arrays.asList(ALL_KEYS + "|[" + GU + ", " + OTHER_GU + "]"), this.world.specificityReads);
    assertMethods("password on, no key", result.get(GU), false, false, true, false);
    assertMethods("password off, no key: the last resort", result.get(OTHER_GU), false, false, true, false);
  }

  @Test
  public void aCognitoAccountHasNoPasswordWithoutAKeyEither() {
    this.world.secret = "";
    this.world.addUnit(OTHER_GU, "true", "", "false");

    Map<Long, Methods> result = this.world.subject().methodsFor(SUBMITTED, this.world.units(OTHER_GU),
      Collections.singletonMap(OTHER_GU, Boolean.TRUE));

    assertMethods("SEC-003: Cognito, no key", result.get(OTHER_GU), true, false, false, false);
  }

  @Test
  public void ac15MethodsForAUsernameRunsOneQueryAndAPasswordOffUnitNeedsTheEmail() {
    this.world.addUnit(OTHER_GU, "true", "", "false");

    Map<Long, Methods> result = this.world.subject().methodsFor("ana.username", this.world.units(GU, OTHER_GU), null);

    assertEquals("one query even for a username", Arrays.asList(ALL_KEYS + "|[" + GU + ", " + OTHER_GU + "]"),
      this.world.specificityReads);
    assertMethods("password on: a username signs in with the password", result.get(GU), false, false, true, false);
    assertMethods("FN-010: password off, a username", result.get(OTHER_GU), false, false, false, true);
  }

  @Test
  public void methodsForReadsNoAccountNoMembershipNoUnitAndNoCognitoFlag() {
    this.world.subject().methodsFor(SUBMITTED, this.world.units(GU), Collections.singletonMap(GU, Boolean.TRUE));

    assertEquals(0, this.world.userLookups.size());
    assertEquals(0, this.world.unitLookups.size());
    assertEquals(0, this.world.membershipChecks.size());
    assertEquals("the caller passes the Cognito answer, the predicate reads none", 0, this.world.cognitoReads.size());
    assertEquals(0, this.world.cgiarReads());
  }

  @Test
  public void methodsForWithNoUsableUnitMakesNoQuery() {
    OtpEligibility subject = this.world.subject();

    assertTrue(subject.methodsFor(SUBMITTED, null, null).isEmpty());
    assertTrue(subject.methodsFor(SUBMITTED, Collections.<GlobalUnit>emptyList(), null).isEmpty());
    assertTrue(subject.methodsFor(SUBMITTED, Arrays.asList(null, new GlobalUnit()), null).isEmpty());
    assertEquals("nothing to answer for, so nothing to read", 0, this.world.specificityReads.size());
  }

  @Test
  public void methodsForKeepsTheOrderGivenAndAnswersEachUnitOnce() {
    this.world.addUnit(OTHER_GU, "true", "", "true");

    Map<Long, Methods> result =
      this.world.subject().methodsFor(SUBMITTED, this.world.units(OTHER_GU, GU, OTHER_GU), null);

    assertEquals(Arrays.asList(OTHER_GU, GU), new ArrayList<>(result.keySet()));
    assertEquals(Arrays.asList(ALL_KEYS + "|[" + OTHER_GU + ", " + GU + "]"), this.world.specificityReads);
  }

  @Test
  public void aMissingCognitoMapOrEntryReadsAsNotACognitoAccount() {
    OtpEligibility subject = this.world.subject();

    assertMethods("null map", subject.methodsFor(SUBMITTED, this.world.units(GU), null).get(GU), false, true, true,
      false);
    assertMethods("empty map", subject.methodsFor(SUBMITTED, this.world.units(GU),
      Collections.<Long, Boolean>emptyMap()).get(GU), false, true, true, false);
    assertMethods("an entry for another unit", subject.methodsFor(SUBMITTED, this.world.units(GU),
      Collections.singletonMap(OTHER_GU, Boolean.TRUE)).get(GU), false, true, true, false);
  }

  @Test
  public void aFailedSpecificityReadSurfacesToTheCallerWhichOwnsTheFailOpenAnswer() {
    this.world.specificityFailure = new IllegalStateException("store down");

    try {
      this.world.subject().methodsFor(SUBMITTED, this.world.units(GU), null);
      fail("the manager's exception must reach the caller");
    } catch (IllegalStateException expected) {
      assertEquals("store down", expected.getMessage());
    }
    assertEquals(0, this.lastResortLines().size());
  }

  // ---- OPS-002: the last-resort ERROR, once per unit ----------------------------------------------------------

  @Test
  public void ops002TheLastResortIsLoggedOnceAtErrorPerUnitAndNamesTheUnitOnly() {
    this.world.secret = "";
    this.world.addUnit(GU, "true", "", "false");
    this.world.addUnit(OTHER_GU, "true", "", "false");
    this.world.addUnit(9L, "true", "", "true");
    OtpEligibility subject = this.world.subject();

    subject.methodsFor(SUBMITTED, this.world.units(GU, 9L), null);
    assertEquals("one line, for the password-off unit only", 1, this.lastResortLines().size());
    assertEquals(Level.ERROR, this.lastResortLines().get(0).getLevel());
    assertEquals("auth.password.last_resort gu=GU7 reason=code_key_unconfigured",
      this.lastResortLines().get(0).getFormattedMessage());

    // Repeats from "unauthenticated requests": other identifiers, a username, the same unit again.
    subject.methodsFor(SUBMITTED, this.world.units(GU), null);
    subject.methodsFor("someone.else@partner.org", this.world.units(GU), null);
    subject.methodsFor("a-username", this.world.units(GU), null);
    subject.methodsFor(null, this.world.units(GU, 9L), null);
    assertEquals("never repeated for the same unit", 1, this.lastResortLines().size());

    subject.methodsFor(SUBMITTED, this.world.units(OTHER_GU), null);
    assertEquals("another unit gets its own line", 2, this.lastResortLines().size());
    assertEquals("auth.password.last_resort gu=GU8 reason=code_key_unconfigured",
      this.lastResortLines().get(1).getFormattedMessage());
    for (ILoggingEvent line : this.lastResortLines()) {
      assertFalse("no email in the line", line.getFormattedMessage().toLowerCase().contains("partner"));
    }
  }

  @Test
  public void ops002NothingIsLoggedWhenThePasswordIsNotOfferedOnlyBecauseOfTheMissingKey() {
    // (a) the key is valid: the code is the method. (b) no key but the password switch is on: the password is
    // offered anyway. (c) no key, password off, but a Cognito account: it is not offered the password.
    World valid = new World();
    valid.addUnit(GU, "true", "", "false");
    valid.subject().methodsFor(SUBMITTED, valid.units(GU), null);

    World switchOn = new World();
    switchOn.secret = "";
    switchOn.addUnit(GU, "true", "", "true");
    switchOn.subject().methodsFor(SUBMITTED, switchOn.units(GU), null);

    World cognito = new World();
    cognito.secret = "";
    cognito.addUnit(GU, "true", "", "false");
    cognito.subject().methodsFor(SUBMITTED, cognito.units(GU), Collections.singletonMap(GU, Boolean.TRUE));

    assertEquals(0, this.lastResortLines().size());
  }

  @Test
  public void ops002TheMemoryBelongsToTheInstanceSoItStartsEmptyWithTheApplication() {
    this.world.secret = "";
    this.world.addUnit(GU, "true", "", "false");

    this.world.subject().methodsFor(SUBMITTED, this.world.units(GU), null);
    this.world.subject().methodsFor(SUBMITTED, this.world.units(GU), null);

    assertEquals("a new instance is a new start", 2, this.lastResortLines().size());
  }

  // ---- the password gate (CHG-PWD-SWITCH-001-T05, DD-5) ---------------------------------------------------------

  private void gateWorld(String passwordValue) {
    this.world.addUnit(GU, "true", "", passwordValue);
    this.world.addMemberships(LOOKED_UP, GU);
  }

  @Test
  public void theGateRefusesAMemberOfAPasswordOffUnitAndLogsOneLineWithoutTheAccount() {
    this.gateWorld("false");

    assertTrue(this.world.subject().passwordGate(LOOKED_UP, Long.valueOf(GU)).isRefused());

    assertEquals("the account first, then its memberships, then the settings", Arrays.asList("user", "members", "spec"),
      this.world.calls);
    assertEquals(Arrays.asList(LOOKED_UP), this.world.userLookups);
    assertEquals("memberships by the stored email", Arrays.asList(LOOKED_UP), this.world.membershipReads);
    assertEquals("the three keys in one read, for the one unit of the attempt",
      Arrays.asList(ALL_KEYS + "|[" + GU + "]"), this.world.specificityReads);
    List<ILoggingEvent> lines = this.refusedLines();
    assertEquals(1, lines.size());
    assertEquals(Level.INFO, lines.get(0).getLevel());
    assertEquals("auth.password.refused gate=password_switch gu=GU7", lines.get(0).getFormattedMessage());
    assertFalse("no email in the line", lines.get(0).getFormattedMessage().contains("partner"));
  }

  @Test
  public void theGateAllowsAMemberOfAPasswordOnUnitAndSaysNothing() {
    this.gateWorld("true");

    assertFalse(this.world.subject().passwordGate(LOOKED_UP, Long.valueOf(GU)).isRefused());

    assertEquals(0, this.refusedLines().size());
    assertEquals(0, this.gateErrorLines().size());
  }

  /** The password switch is read like the rule reads it: on unless the trimmed value is {@code false}. */
  @Test
  public void theGateReadsThePasswordSwitchLikeTheRuleDoes() {
    for (String on : new String[] {"", "true", "TRUE", " true ", "yes", "0", "garbage"}) {
      World world = new World();
      world.addUnit(GU, "true", "", on);
      world.addUser(LOOKED_UP, USER_ID, true, false);
      world.addMemberships(LOOKED_UP, GU);
      assertFalse("'" + on + "' is on",
        world.subject().passwordGate(LOOKED_UP, Long.valueOf(GU)).isRefused());
    }
    for (String off : new String[] {"false", "FALSE", " False ", "false\n"}) {
      World world = new World();
      world.addUnit(GU, "true", "", off);
      world.addUser(LOOKED_UP, USER_ID, true, false);
      world.addMemberships(LOOKED_UP, GU);
      assertTrue("'" + off + "' is off",
        world.subject().passwordGate(LOOKED_UP, Long.valueOf(GU)).isRefused());
    }
  }

  /**
   * <b>The foreign-unit bypass (Judgment Day C-2).</b> The account is a member of unit 7, where the password is off.
   * The attempt names unit 8, where it is on and the account is no member, or a unit that does not exist. Only a
   * membership narrows the decision; anything else falls through to the sweep, which still refuses. The named id
   * never reaches the settings read.
   */
  @Test
  public void theGateIgnoresAnAttemptUnitTheAccountDoesNotBelongTo() {
    this.world.addUnit(GU, "true", "", "false");
    this.world.addUnit(OTHER_GU, "true", "", "true");
    this.world.addMemberships(LOOKED_UP, GU);

    assertTrue(this.world.subject().passwordGate(LOOKED_UP, Long.valueOf(OTHER_GU)).isRefused());
    assertTrue(this.world.subject().passwordGate(LOOKED_UP, Long.valueOf(99999L)).isRefused());

    assertEquals("only the account's own unit is ever read",
      Arrays.asList(ALL_KEYS + "|[" + GU + "]", ALL_KEYS + "|[" + GU + "]"), this.world.specificityReads);
  }

  @Test
  public void theGateJudgesAMemberByTheUnitOfTheAttemptAlone() {
    this.world.addUnit(GU, "true", "", "false");
    this.world.addUnit(OTHER_GU, "true", "", "true");
    this.world.addMemberships(LOOKED_UP, GU, OTHER_GU);

    assertFalse("the member's password-on unit allows it", this.world.subject()
      .passwordGate(LOOKED_UP, Long.valueOf(OTHER_GU)).isRefused());
    assertEquals(Arrays.asList(ALL_KEYS + "|[" + OTHER_GU + "]"), this.world.specificityReads);
    assertTrue("the member's password-off unit refuses it", this.world.subject()
      .passwordGate(LOOKED_UP, Long.valueOf(GU)).isRefused());
  }

  @Test
  public void withNoUnitNamedTheGateSweepsEveryMembershipAndLogsTheFirstOffUnitOnce() {
    this.world.addUnit(GU, "true", "", "true");
    this.world.addUnit(OTHER_GU, "true", "", "false");
    this.world.addUnit(9L, "true", "", "false");
    this.world.addMemberships(LOOKED_UP, GU, OTHER_GU, 9L);

    assertTrue(this.world.subject().passwordGate(LOOKED_UP, null).isRefused());

    assertEquals("every membership in one read", Arrays.asList(ALL_KEYS + "|[" + GU + ", " + OTHER_GU + ", 9]"),
      this.world.specificityReads);
    assertEquals("one line for one refusal", 1, this.refusedLines().size());
    assertEquals("auth.password.refused gate=password_switch gu=GU8", this.refusedLines().get(0).getFormattedMessage());

    World allOn = new World();
    allOn.addUnit(GU, "true", "", "true");
    allOn.addUnit(OTHER_GU, "true", "", "true");
    allOn.addUser(LOOKED_UP, USER_ID, true, false);
    allOn.addMemberships(LOOKED_UP, GU, OTHER_GU);
    assertFalse(allOn.subject().passwordGate(LOOKED_UP, null).isRefused());
  }

  @Test
  public void theGateNeverRefusesAnUnknownAccountOrABlankIdentifier() {
    this.world.addUnit(GU, "true", "", "false");
    this.world.addMemberships("someone.else@partner.org", GU);

    assertFalse("no account: the credential check fails as today",
      this.world.subject().passwordGate("nobody@partner.org", Long.valueOf(GU)).isRefused());
    assertEquals(Arrays.asList("user"), this.world.calls);

    this.world.calls.clear();
    for (String blank : new String[] {null, "", "   "}) {
      assertFalse("'" + blank + "'", this.world.subject().passwordGate(blank, Long.valueOf(GU)).isRefused());
    }
    assertEquals("a blank identifier is never looked up", 0, this.world.calls.size());
    assertEquals(0, this.refusedLines().size());
  }

  @Test
  public void theGateNeverRefusesAnAccountWithNoMembershipAndReadsNoSettings() {
    this.world.addUnit(GU, "true", "", "false");

    assertFalse(this.world.subject().passwordGate(LOOKED_UP, Long.valueOf(GU)).isRefused());

    assertEquals(Arrays.asList("user", "members"), this.world.calls);
    assertEquals(0, this.world.specificityReads.size());
  }

  /** Last resort (DD-5): without a valid key the password stays available; the gate makes no call, writes no line. */
  @Test
  public void withoutTheCodeKeyTheGateNeverRefusesMakesNoCallAndLogsNothing() {
    this.world.secret = "";
    this.gateWorld("false");

    assertFalse(this.world.subject().passwordGate(LOOKED_UP, Long.valueOf(GU)).isRefused());

    assertEquals("no call at all", 0, this.world.calls.size());
    assertEquals(0, this.refusedLines().size());
    assertEquals("the ERROR of this case belongs to crpByEmail.do", 0, this.gateErrorLines().size());
    assertEquals(0, this.lastResortLines().size());
  }

  /**
   * Fail-open (requirements Decision Log, exception to SEC-001): a failing read of the account, the memberships or the
   * settings means today's behavior, the password allowed, and one ERROR line. A database fault must not become a
   * lock-out. The line carries neither the identifier nor a unit.
   */
  @Test
  public void aFailingReadFailsOpenWithOneErrorLineForEachOfTheThreeReads() {
    for (String failing : new String[] {"user", "members", "spec"}) {
      this.appender.list.clear();
      World world = new World();
      world.addUnit(GU, "true", "", "false");
      world.addUser(LOOKED_UP, USER_ID, true, false);
      world.addMemberships(LOOKED_UP, GU);
      RuntimeException failure = new IllegalStateException(failing + " read failed");
      if ("user".equals(failing)) {
        world.userFailure = failure;
      } else if ("members".equals(failing)) {
        world.membershipFailure = failure;
      } else {
        world.specificityFailure = failure;
      }

      assertFalse(failing, world.subject().passwordGate(LOOKED_UP, Long.valueOf(GU)).isRefused());

      List<ILoggingEvent> errors = this.gateErrorLines();
      assertEquals(failing, 1, errors.size());
      assertEquals(Level.ERROR, errors.get(0).getLevel());
      assertEquals(failing, "auth.password.gate outcome=settings_unavailable fail_open=true",
        errors.get(0).getFormattedMessage());
      assertFalse("no identifier in the line", errors.get(0).getFormattedMessage().contains("partner"));
      assertEquals(failing, 0, this.refusedLines().size());
    }
  }

  /** The memberships are read by the account's stored email, whatever the identifier was typed as. */
  @Test
  public void theGateAsksForMembershipsByTheStoredEmailNotByWhatWasTyped() {
    this.world.normaliseUserLookups = true;
    this.gateWorld("false");

    assertTrue(this.world.subject().passwordGate(SUBMITTED, Long.valueOf(GU)).isRefused());

    assertEquals("the identifier goes to the account lookup as typed, which normalises it", Arrays.asList(SUBMITTED),
      this.world.userLookups);
    assertEquals("the memberships by the stored email", Arrays.asList(LOOKED_UP), this.world.membershipReads);
  }

  @Test
  public void theGateResolvesAnIdentifierWithoutAnAtSignAsAUsername() {
    this.world.usernames.put("jane.local", LOOKED_UP);
    this.gateWorld("false");

    assertTrue(this.world.subject().passwordGate("jane.local", Long.valueOf(GU)).isRefused());

    assertEquals(Arrays.asList("username:jane.local"), this.world.userLookups);
    assertEquals(Arrays.asList(LOOKED_UP), this.world.membershipReads);
  }

  /** The gate asks about the password switch and nothing else: no Cognito flag read, no crp_users row read. */
  @Test
  public void theGateReadsNoCognitoStateAndNoCrpUsersRow() {
    this.gateWorld("false");

    assertTrue(this.world.subject().passwordGate(LOOKED_UP, Long.valueOf(GU)).isRefused());

    assertEquals(0, this.world.cognitoReads.size());
    assertEquals(0, this.world.membershipChecks.size());
  }

  /**
   * SEC-002: a refusal leaves in the Shiro session what {@code APCustomRealm} leaves when the credential check fails,
   * which differs by kind of account: nothing for an active local account (the realm sets the attribute to {@code
   * null}, which removes it, so a message left by an earlier attempt is cleared too), the failed-logon code for an
   * active CGIAR account and the disabled code for an inactive one, local or CGIAR. The expected values are the raw
   * codes of the spec and of the realm, written as literals.
   */
  @Test
  public void aRefusalLeavesWhatTheRealmLeavesForEachKindOfAccount() {
    assertNull("active local: no message, and an earlier one is cleared",
      this.messageLeftFor(true, false, "USER_DISABLED"));
    assertEquals("active CGIAR", "ERROR_LOGON_FAILURE", this.messageLeftFor(true, true, null));
    assertEquals("inactive local", "USER_DISABLED", this.messageLeftFor(false, false, null));
    assertEquals("inactive CGIAR: disabled wins, as in the realm", "USER_DISABLED",
      this.messageLeftFor(false, true, null));
  }

  /**
   * SEC-002, cookies: a refusal runs Shiro's failed-login handling for remember-me, which {@code Subject.login} runs on
   * every failure. The remember-me manager receives the subject, a token built as {@code UserManagerImp.login} builds
   * it (the typed account and password) and the exception the realm throws for a failed password. The recorder copies
   * what it sees at the moment of the call, because the token is cleared afterwards.
   */
  @Test
  public void aRefusalRunsTheRememberMeFailedLoginHandlingWithTheTokenALoginWouldHaveBuilt() {
    List<String> seen = new ArrayList<>();
    DefaultSecurityManager securityManager = new DefaultSecurityManager();
    securityManager.setRememberMeManager(this.recordingRememberMe(seen, null));
    Subject subject = new Subject.Builder(securityManager).buildSubject();

    this.refusalOnAPasswordOffUnit().leaveRememberMeFailure(subject, securityManager, "jane@partner.org", "typed-pw");

    assertEquals(Arrays.asList("jane@partner.org|typed-pw|IncorrectCredentialsException|" + subject.hashCode()), seen);
  }

  /** Nothing to run, nothing to fail: no remember-me manager, a security manager of another kind, or a throwing one. */
  @Test
  public void theRememberMeFailureHandlingNeverChangesTheAnswerOfARefusal() {
    OtpEligibility.PasswordGateDecision refusal = this.refusalOnAPasswordOffUnit();

    DefaultSecurityManager withoutManager = new DefaultSecurityManager();
    withoutManager.setRememberMeManager(null);
    refusal.leaveRememberMeFailure(new Subject.Builder(withoutManager).buildSubject(), withoutManager,
      "jane@partner.org", "typed-pw");

    org.apache.shiro.mgt.SecurityManager notDefault = fake(org.apache.shiro.mgt.SecurityManager.class,
      (method, args) -> {
        throw new AssertionError("a security manager of another kind must not be asked, but " + method + " was");
      });
    refusal.leaveRememberMeFailure(new Subject.Builder(withoutManager).buildSubject(), notDefault, "jane@partner.org",
      "typed-pw");

    this.appender.list.clear();
    DefaultSecurityManager throwing = new DefaultSecurityManager();
    throwing.setRememberMeManager(
      this.recordingRememberMe(new ArrayList<String>(), new IllegalStateException("boom")));
    refusal.leaveRememberMeFailure(new Subject.Builder(throwing).buildSubject(), throwing, "jane@partner.org",
      "typed-pw");

    List<ILoggingEvent> warnings = this.linesStartingWith("auth.password.gate outcome=remember_me_unavailable");
    assertEquals("contained, and logged once", 1, warnings.size());
    assertEquals(Level.WARN, warnings.get(0).getLevel());
    assertFalse("the line does not name the account", warnings.get(0).getFormattedMessage().contains("jane"));
  }

  /** A remember-me manager that records what it is asked, and can throw. */
  private RememberMeManager recordingRememberMe(List<String> seen, RuntimeException failure) {
    return fake(RememberMeManager.class, (method, args) -> {
      if ("getRememberedPrincipals".equals(method)) {
        return null; // Shiro asks it when it builds a subject; nothing is remembered
      }
      if (!"onFailedLogin".equals(method)) {
        throw new AssertionError("only the failed-login handling may be asked, but " + method + " was called");
      }
      AuthenticationToken token = (AuthenticationToken) args[1];
      seen.add(token.getPrincipal() + "|" + new String(((UsernamePasswordToken) token).getPassword()) + "|"
        + ((AuthenticationException) args[2]).getClass().getSimpleName() + "|" + args[0].hashCode());
      if (failure != null) {
        throw failure;
      }
      return null;
    });
  }

  private OtpEligibility.PasswordGateDecision refusalOnAPasswordOffUnit() {
    World world = new World();
    world.addUnit(GU, "true", "", "false");
    world.addUser(LOOKED_UP, USER_ID, true, false);
    world.addMemberships(LOOKED_UP, GU);
    OtpEligibility.PasswordGateDecision decision = world.subject().passwordGate(LOOKED_UP, Long.valueOf(GU));
    assertTrue(decision.isRefused());
    return decision;
  }

  /** The gate allowing the password leaves nothing to leave; the endpoint never asks it to. */
  @Test
  public void anAllowedDecisionIsNotARefusal() {
    this.gateWorld("true");

    assertFalse(this.world.subject().passwordGate(LOOKED_UP, Long.valueOf(GU)).isRefused());
  }

  /** Runs the gate on a password-off unit for one kind of account and returns what the refusal left in the session. */
  private Object messageLeftFor(boolean active, boolean cgiar, String earlierMessage) {
    World world = new World();
    world.addUnit(GU, "true", "", "false");
    world.addUser(LOOKED_UP, USER_ID, active, cgiar);
    world.addMemberships(LOOKED_UP, GU);
    OtpEligibility.PasswordGateDecision decision = world.subject().passwordGate(LOOKED_UP, Long.valueOf(GU));
    assertTrue(decision.isRefused());
    Session session = new SimpleSession();
    if (earlierMessage != null) {
      session.setAttribute("loginMessage", earlierMessage);
    }
    decision.leaveRealmMessage(session);
    return session.getAttribute("loginMessage");
  }

  /** The refusal line names the unit as stored, sanitised; a missing acronym or line breaks cannot break the line. */
  @Test
  public void theGateLogsTheUnitAcronymSanitisedAndSurvivesAMissingOne() {
    this.gateWorld("false");
    this.world.unitsById.get(Long.valueOf(GU)).setAcronym("A\nB\rC");

    assertTrue(this.world.subject().passwordGate(LOOKED_UP, Long.valueOf(GU)).isRefused());
    assertEquals("auth.password.refused gate=password_switch gu=ABC", this.refusedLines().get(0).getFormattedMessage());

    this.appender.list.clear();
    this.world.unitsById.get(Long.valueOf(GU)).setAcronym(null);
    assertTrue(this.world.subject().passwordGate(LOOKED_UP, Long.valueOf(GU)).isRefused());
    assertEquals("auth.password.refused gate=password_switch gu=", this.refusedLines().get(0).getFormattedMessage());
    assertEquals(0, this.gateErrorLines().size());
  }

  private List<ILoggingEvent> refusedLines() {
    return this.linesStartingWith(REFUSED_LINE);
  }

  private List<ILoggingEvent> gateErrorLines() {
    return this.linesStartingWith(GATE_ERROR_LINE);
  }

  private List<ILoggingEvent> linesStartingWith(String prefix) {
    List<ILoggingEvent> lines = new ArrayList<>();
    for (ILoggingEvent event : this.appender.list) {
      if (event.getFormattedMessage().startsWith(prefix)) {
        lines.add(event);
      }
    }
    return lines;
  }

  private List<ILoggingEvent> lastResortLines() {
    List<ILoggingEvent> lines = new ArrayList<>();
    for (ILoggingEvent event : this.appender.list) {
      if (event.getFormattedMessage().startsWith(LAST_RESORT_LINE)) {
        lines.add(event);
      }
    }
    return lines;
  }

  // ---- the world and its fakes -------------------------------------------------------------------------

  private static Map<String, String> settingsOf(String switchValue, String exclusion, String passwordValue) {
    Map<String, String> values = new HashMap<>();
    values.put(SWITCH_KEY, switchValue);
    values.put(EXCLUSION_KEY, exclusion);
    values.put(PASSWORD_KEY, passwordValue);
    return values;
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
    /** The unit's own custom_parameters row for cognito_auth_active, by unit id; a unit with no entry has none. */
    final Map<Long, String> cognitoOverrides = new HashMap<>();
    /** The catalog default of cognito_auth_active for the type {@link #GU_TYPE}; {@code null} is no catalog row. */
    String cognitoCatalogDefault;
    RuntimeException specificityFailure;
    RuntimeException userFailure;
    RuntimeException membershipFailure;
    /** Whether the user lookup trims and lower-cases what it is asked for, as the real DAO does. */
    boolean normaliseUserLookups;
    /** username to stored email, for the lookup by username. */
    final Map<String, String> usernames = new HashMap<>();
    /** The units each stored email is an active member of, as {@code crpUsers} answers by exact equality. */
    final Map<String, List<GlobalUnit>> membershipsByStoredEmail = new HashMap<>();

    final List<String> userLookups = new ArrayList<>();
    /** Every {@code crpUsers} argument: the email the memberships were asked for. */
    final List<String> membershipReads = new ArrayList<>();
    final List<Long> unitLookups = new ArrayList<>();
    final List<String> specificityReads = new ArrayList<>();
    final List<String> membershipChecks = new ArrayList<>();
    /** Every Cognito flag read with its arguments: {@code custom:<key>:<unit id>} and {@code catalog:<key>:<type>}. */
    final List<String> cognitoReads = new ArrayList<>();
    /** Every collaborator call in order, by name only, so the sequences of two accounts can be compared. */
    final List<String> calls = new ArrayList<>();

    void addUser(String email, long id, boolean active, boolean cgiar) {
      User user = new CountingUser();
      user.setId(Long.valueOf(id));
      user.setEmail(email);
      user.setActive(active);
      user.setCgiarUser(cgiar);
      this.users.put(email, user);
    }

    void addUnit(long id, String switchValue, String exclusion) {
      this.addUnit(id, switchValue, exclusion, "true");
    }

    void addUnit(long id, String switchValue, String exclusion, String passwordValue) {
      GlobalUnit unit = new GlobalUnit();
      unit.setId(Long.valueOf(id));
      unit.setAcronym("GU" + id);
      GlobalUnitType type = new GlobalUnitType();
      type.setId(Long.valueOf(GU_TYPE));
      unit.setGlobalUnitType(type);
      this.unitsById.put(Long.valueOf(id), unit);
      this.settings.put(Long.valueOf(id), settingsOf(switchValue, exclusion, passwordValue));
    }

    /** The units already added to this world that the stored email is an active member of. */
    void addMemberships(String storedEmail, long... unitIds) {
      this.membershipsByStoredEmail.put(storedEmail, this.units(unitIds));
    }

    List<GlobalUnit> units(long... ids) {
      List<GlobalUnit> result = new ArrayList<>();
      for (long id : ids) {
        result.add(this.unitsById.get(Long.valueOf(id)));
      }
      return result;
    }

    /** How many times any user of this world had {@code isCgiarUser()} read. */
    int cgiarReads() {
      int reads = 0;
      for (User user : this.users.values()) {
        reads += ((CountingUser) user).cgiarReads;
      }
      return reads;
    }

    OtpEligibility subject() {
      UserManager userManager = fake(UserManager.class, (method, args) -> {
        if ("getUserByEmail".equals(method)) {
          this.calls.add("user");
          this.userLookups.add((String) args[0]);
          if (this.userFailure != null) {
            throw this.userFailure;
          }
          String key = (String) args[0];
          return this.users.get(this.normaliseUserLookups ? key.trim().toLowerCase() : key);
        }
        if ("getUserByUsername".equals(method)) {
          this.calls.add("user");
          this.userLookups.add("username:" + args[0]);
          if (this.userFailure != null) {
            throw this.userFailure;
          }
          return this.users.get(this.usernames.get(args[0]));
        }
        return unsupported(method);
      });
      GlobalUnitManager globalUnitManager = fake(GlobalUnitManager.class, (method, args) -> {
        if ("crpUsers".equals(method)) {
          this.calls.add("members");
          this.membershipReads.add((String) args[0]);
          if (this.membershipFailure != null) {
            throw this.membershipFailure;
          }
          List<GlobalUnit> stored = this.membershipsByStoredEmail.get(args[0]);
          return stored == null ? new ArrayList<GlobalUnit>() : new ArrayList<GlobalUnit>(stored);
        }
        if ("getGlobalUnitById".equals(method)) {
          this.calls.add("unit");
          this.unitLookups.add((Long) args[0]);
          return this.unitsById.get(args[0]);
        }
        return unsupported(method);
      });
      CrpUserManager crpUserManager = fake(CrpUserManager.class, (method, args) -> {
        if ("existActiveCrpUser".equals(method) && args.length == 2) {
          this.calls.add("member");
          String pair = args[0] + ":" + args[1];
          this.membershipChecks.add(pair);
          return this.members.contains(pair);
        }
        if ("existCrpUser".equals(method) && args.length == 2) {
          // The old, activity-blind gate answers true for the soft-deleted row, so a regression to it is visible.
          this.calls.add("member-any-state");
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
          this.calls.add("spec");
          this.specificityReads.add(keys + "|" + ids);
          if (this.specificityFailure != null) {
            throw this.specificityFailure;
          }
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
      CustomParameterManager customParameterManager = fake(CustomParameterManager.class, (method, args) -> {
        if ("getCustomParameterByParameterKeyAndGlobalUnitId".equals(method)) {
          this.calls.add("custom");
          this.cognitoReads.add("custom:" + args[0] + ":" + args[1]);
          String value = COGNITO_KEY.equals(args[0]) ? this.cognitoOverrides.get(args[1]) : null;
          if (value == null) {
            return null;
          }
          CustomParameter row = new CustomParameter();
          row.setValue(value);
          row.setActive(true);
          return row;
        }
        return unsupported(method);
      });
      ParameterManager parameterManager = fake(ParameterManager.class, (method, args) -> {
        if ("getParameterByKey".equals(method)) {
          this.calls.add("catalog");
          this.cognitoReads.add("catalog:" + args[0] + ":" + args[1]);
          // Honoured, not ignored: the catalog is keyed by the unit TYPE, so a unit id passed here finds nothing.
          if (!COGNITO_KEY.equals(args[0]) || !Long.valueOf(GU_TYPE).equals(args[1])
            || this.cognitoCatalogDefault == null) {
            return null;
          }
          Parameter row = new Parameter();
          row.setDefaultValue(this.cognitoCatalogDefault);
          return row;
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
