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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

/**
 * CHG-PWD-SWITCH-001-T02: the pure sign-in method rule (design 1, DD-1, DD-2; FN-003 to FN-007, FN-010, SEC-003,
 * AC-1, AC-3 to AC-7, AC-9).
 * <p>
 * <b>Where the expected values come from.</b> None is computed by {@link SignInMethodRule}. The 64 expected method
 * sets of {@link #expectedTable()} are written out one row per combination, by hand, from the rule table of design 1
 * and the scenarios of requirements 3.1; the named AC tests carry their own literals taken from those scenarios; the
 * invariants are properties of the answer, not recomputations of it.
 * <p>
 * Row key: {@code K C S X P E}, each 1 or 0:
 * <ul>
 * <li>K: the code key is configured;</li>
 * <li>C: the account is a Cognito account in the Global Unit;</li>
 * <li>S: the code switch of the unit is on;</li>
 * <li>X: the email domain is in the unit's exclusion list;</li>
 * <li>P: the password switch of the unit is on;</li>
 * <li>E: the identifier typed in step 1 is an email (0: a username).</li>
 * </ul>
 */
public class SignInMethodRuleTest {

  private static final int COMBINATIONS = 64;

  /** Raw switch values that mean on. The last two are the "missing value" forms of AC-1, DD-2. */
  private static final List<String> PASSWORD_ON_VALUES =
    Arrays.asList("true", "TRUE", " true ", "yes", "0", "", null);
  /** Raw switch values that mean off: exactly {@code false}, trimmed, any case. */
  private static final List<String> PASSWORD_OFF_VALUES = Arrays.asList("false", "FALSE", " false ", "False");

  // ---- AC-9 and FN-007: every combination, expected sets written out one by one ------------------------------

  @Test
  public void everyCombinationYieldsTheMethodsOfTheRuleTable() {
    Map<String, String> table = expectedTable();
    assertEquals("one expected row per combination of the six inputs", COMBINATIONS, table.size());
    List<String> mismatches = new ArrayList<String>();
    for (int n = 0; n < COMBINATIONS; n++) {
      Row row = new Row(n);
      String expected = table.get(row.key());
      String actual = describe(row.methods());
      if (!actual.equals(expected)) {
        mismatches.add(row.key() + " expected " + expected + " but was " + actual);
      }
    }
    assertEquals("rows whose methods differ from the rule table", Collections.<String>emptyList(), mismatches);
  }

  @Test
  public void everyRawFormOfTheSwitchGivesTheSameRowAsItsOnOrOffMeaning() {
    Map<String, String> table = expectedTable();
    List<String> mismatches = new ArrayList<String>();
    for (int n = 0; n < COMBINATIONS; n++) {
      Row row = new Row(n);
      List<String> rawValues = row.p ? PASSWORD_ON_VALUES : PASSWORD_OFF_VALUES;
      for (String raw : rawValues) {
        String actual = describe(row.methods(raw));
        if (!actual.equals(table.get(row.key()))) {
          mismatches.add(row.key() + " with password switch [" + raw + "] expected " + table.get(row.key())
            + " but was " + actual);
        }
      }
    }
    assertEquals("rows whose methods change with the raw spelling of the switch", Collections.<String>emptyList(),
      mismatches);
  }

  @Test
  public void everyActiveMemberHasAMethodOrIsToldToUseTheEmail() {
    List<String> stranded = new ArrayList<String>();
    for (int n = 0; n < COMBINATIONS; n++) {
      Row row = new Row(n);
      SignInMethodRule.Methods methods = row.methods();
      boolean hasMethod = methods.isCognito() || methods.isCode() || methods.isPassword();
      if (!hasMethod && !methods.isNeedsEmail()) {
        stranded.add(row.key());
      }
      if (methods.isNeedsEmail()) {
        // FN-010: the message is for a username with no other way in, and only then.
        assertFalse(row.key() + ": needsEmail with a method", hasMethod);
        assertFalse(row.key() + ": needsEmail for an email identifier", row.e);
      }
    }
    assertEquals("combinations with neither a method nor the email message", Collections.<String>emptyList(),
      stranded);
  }

  @Test
  public void aCognitoAccountAlwaysHasCognitoAndNeverThePassword() {
    for (int n = 0; n < COMBINATIONS; n++) {
      Row row = new Row(n);
      SignInMethodRule.Methods methods = row.methods();
      assertEquals(row.key() + ": cognito follows the account", row.c, methods.isCognito());
      if (row.c) {
        assertFalse(row.key() + ": a Cognito account got the password (SEC-003)", methods.isPassword());
      }
    }
  }

  @Test
  public void noCombinationOffersMoreThanTwoMethods() {
    for (int n = 0; n < COMBINATIONS; n++) {
      Row row = new Row(n);
      SignInMethodRule.Methods methods = row.methods();
      int count = (methods.isCognito() ? 1 : 0) + (methods.isCode() ? 1 : 0) + (methods.isPassword() ? 1 : 0);
      assertTrue(row.key() + ": " + count + " methods", count <= 2);
    }
  }

  @Test
  public void theCodeIsNeverOfferedToAUsernameOrWithoutTheKey() {
    for (int n = 0; n < COMBINATIONS; n++) {
      Row row = new Row(n);
      SignInMethodRule.Methods methods = row.methods();
      if (!row.e || !row.k) {
        assertFalse(row.key() + ": code without an email or without the key", methods.isCode());
      }
    }
  }

  // ---- named scenarios (requirements 3.1, 7) ----------------------------------------------------------------

  /** AC-3, FN-004, UI-001: password off, code on, domain not excluded: the code only, so no choice menu. */
  @Test
  public void ac3PasswordOffCodeOnDomainNotExcludedGivesTheCodeOnly() {
    SignInMethodRule.Methods methods =
      SignInMethodRule.methodsFor(true, false, "false", "true", "", "ana@partner.org");

    assertEquals("code", describe(methods));
  }

  /** AC-4, FN-004, SEC-004: password off, code off, Cognito off: any member, CGIAR or not, gets the code. */
  @Test
  public void ac4PasswordOffEverythingElseOffGivesTheCodeAsDefaultMethod() {
    assertEquals("an external member", "code",
      describe(SignInMethodRule.methodsFor(true, false, "false", "false", "", "ana@partner.org")));
    assertEquals("a CGIAR member on a unit without Cognito", "code",
      describe(SignInMethodRule.methodsFor(true, false, "false", "false", "", "ana@cgiar.org")));
    assertEquals("a member of a unit that has no code switch row at all", "code",
      describe(SignInMethodRule.methodsFor(true, false, "false", null, null, "ana@partner.org")));
  }

  /** AC-5, FN-004: password off, domain excluded: still the code; with the password back on: the password only. */
  @Test
  public void ac5PasswordOffDomainExcludedGivesTheCodeAndThePasswordOnlyOnceTheSwitchIsBackOn() {
    assertEquals("code", describe(
      SignInMethodRule.methodsFor(true, false, "false", "true", "example.org", "ana@example.org")));
    assertEquals("password", describe(
      SignInMethodRule.methodsFor(true, false, "true", "true", "example.org", "ana@example.org")));
  }

  /** AC-6, FN-005, SEC-003: a Cognito account keeps today's methods whatever the password switch says. */
  @Test
  public void ac6CognitoAccountUnaffectedByThePasswordSwitchAndTheExclusion() {
    assertEquals("excluded domain: Cognito only, no code", "cognito", describe(
      SignInMethodRule.methodsFor(true, true, "false", "true", "cgiar.org", "ana@cgiar.org")));
    assertEquals("same unit, domain not excluded: Cognito and the code, as today", "cognito+code", describe(
      SignInMethodRule.methodsFor(true, true, "false", "true", "example.org", "ana@cgiar.org")));
    assertEquals("code switch off: Cognito only, even with the password off", "cognito", describe(
      SignInMethodRule.methodsFor(true, true, "false", "false", "", "ana@cgiar.org")));
  }

  /** AC-7, FN-006: without a valid code key the password is offered whatever the password switch says. */
  @Test
  public void ac7LastResortPasswordWhenTheCodeKeyIsMissing() {
    assertEquals("password off, code switch on", "password", describe(
      SignInMethodRule.methodsFor(false, false, "false", "true", "", "ana@partner.org")));
    assertEquals("password off, code switch off", "password", describe(
      SignInMethodRule.methodsFor(false, false, "false", "false", "", "ana@partner.org")));
    assertEquals("password off, a username", "password", describe(
      SignInMethodRule.methodsFor(false, false, "false", "true", "", "ana.lopez")));
  }

  /** FN-010, AC-15: a username with the code as the only method: the "use your email" message, no password. */
  @Test
  public void fn010UsernameWithOnlyTheCodeLeftNeedsTheEmailAndOffersNoPassword() {
    SignInMethodRule.Methods methods =
      SignInMethodRule.methodsFor(true, false, "false", "true", "", "ana.lopez");

    assertEquals("needsEmail", describe(methods));
    assertEquals("the same member restarting with the email gets the code", "code",
      describe(SignInMethodRule.methodsFor(true, false, "false", "true", "", "ana@partner.org")));
  }

  /** FN-003: with the switch on, a username keeps today's password-only step 3. */
  @Test
  public void fn003UsernameOnAPasswordOnUnitKeepsThePassword() {
    assertEquals("password", describe(
      SignInMethodRule.methodsFor(true, false, "true", "true", "", "ana.lopez")));
  }

  /** FN-001, AC-1 (resolver half): a unit with no password row reads as on, so the code override does not fire. */
  @Test
  public void ac1MissingPasswordSwitchReadsAsOnAndKeepsTodaysMethods() {
    assertEquals("null value: the code switch off leaves the password only", "password", describe(
      SignInMethodRule.methodsFor(true, false, null, "false", "", "ana@partner.org")));
    assertEquals("empty value: the code switch off leaves the password only", "password", describe(
      SignInMethodRule.methodsFor(true, false, "", "false", "", "ana@partner.org")));
    assertEquals("null value: the choice menu of T21", "code+password", describe(
      SignInMethodRule.methodsFor(true, false, null, "true", "", "ana@partner.org")));
  }

  @Test
  public void anIdentifierWithoutADomainNeverGetsTheCodeEvenWhenNull() {
    assertEquals("null identifier, password on", "password", describe(
      SignInMethodRule.methodsFor(true, false, "true", "true", "", null)));
    assertEquals("null identifier, password off", "needsEmail", describe(
      SignInMethodRule.methodsFor(true, false, "false", "true", "", null)));
    assertEquals("empty identifier, password off", "needsEmail", describe(
      SignInMethodRule.methodsFor(true, false, "false", "true", "", "")));
  }

  @Test
  public void theExclusionIsReadWithTheSharedExactMatchRule() {
    assertEquals("a sub-domain of an excluded domain is not excluded", "cognito+code", describe(
      SignInMethodRule.methodsFor(true, true, "true", "true", "example.org", "ana@sub.example.org")));
    assertEquals("an entry written with a leading at-sign and capitals still excludes", "cognito", describe(
      SignInMethodRule.methodsFor(true, true, "true", "true", " @Example.ORG ", "ANA@example.org")));
  }

  // ---- isPasswordSwitchOn: DD-2, FN-001 ----------------------------------------------------------------------

  @Test
  public void passwordSwitchTextTrueIsOn() {
    assertTrue(SignInMethodRule.isPasswordSwitchOn("true"));
  }

  @Test
  public void passwordSwitchTextFalseIsOffInAnyCaseAndWithPadding() {
    assertFalse(SignInMethodRule.isPasswordSwitchOn("false"));
    assertFalse(SignInMethodRule.isPasswordSwitchOn("FALSE"));
    assertFalse(SignInMethodRule.isPasswordSwitchOn(" false "));
    assertFalse(SignInMethodRule.isPasswordSwitchOn("\tFalse\n"));
  }

  /** AC-1, resolver half: the shared reader turns a missing value into the empty string, and that reads as on. */
  @Test
  public void ac1EmptyPasswordSwitchValueIsOn() {
    assertTrue(SignInMethodRule.isPasswordSwitchOn(""));
    assertTrue(SignInMethodRule.isPasswordSwitchOn("   "));
  }

  /** AC-1, resolver half: a unit with no custom_parameters row may reach the rule as a null value. */
  @Test
  public void ac1NullPasswordSwitchValueIsOn() {
    assertTrue(SignInMethodRule.isPasswordSwitchOn(null));
  }

  @Test
  public void passwordSwitchValueThatIsNotExactlyFalseIsOn() {
    assertTrue(SignInMethodRule.isPasswordSwitchOn("0"));
    assertTrue(SignInMethodRule.isPasswordSwitchOn("no"));
    assertTrue(SignInMethodRule.isPasswordSwitchOn("off"));
    assertTrue(SignInMethodRule.isPasswordSwitchOn("falsey"));
    assertTrue(SignInMethodRule.isPasswordSwitchOn("fals"));
  }

  // ---- the oracle: design 1, one line per combination ---------------------------------------------------------

  private static String describe(SignInMethodRule.Methods methods) {
    List<String> names = new ArrayList<String>();
    if (methods.isCognito()) {
      names.add("cognito");
    }
    if (methods.isCode()) {
      names.add("code");
    }
    if (methods.isPassword()) {
      names.add("password");
    }
    if (methods.isNeedsEmail()) {
      names.add("needsEmail");
    }
    return names.isEmpty() ? "none" : String.join("+", names);
  }

  /**
   * The rule table of design 1, one literal row per combination of K C S X P E (see the class description). Read
   * it by block: no key, no Cognito: the password; no key, Cognito: Cognito; key without Cognito: the code when the
   * code switch is on and the domain not excluded, or when the password is off, and the password when it is on;
   * key with Cognito: Cognito, plus the code when the switch is on and the domain not excluded; a username never
   * gets the code and, when only the code is left, needsEmail.
   */
  private static Map<String, String> expectedTable() {
    Map<String, String> t = new LinkedHashMap<String, String>();

    // Email identifier (E = 1), no code key: the password is the last resort for an account without Cognito.
    row(t, "0 0 0 0 0 1", "password");
    row(t, "0 0 0 0 1 1", "password");
    row(t, "0 0 0 1 0 1", "password");
    row(t, "0 0 0 1 1 1", "password");
    row(t, "0 0 1 0 0 1", "password");
    row(t, "0 0 1 0 1 1", "password");
    row(t, "0 0 1 1 0 1", "password");
    row(t, "0 0 1 1 1 1", "password");
    // Email identifier, no code key, Cognito account: Cognito only.
    row(t, "0 1 0 0 0 1", "cognito");
    row(t, "0 1 0 0 1 1", "cognito");
    row(t, "0 1 0 1 0 1", "cognito");
    row(t, "0 1 0 1 1 1", "cognito");
    row(t, "0 1 1 0 0 1", "cognito");
    row(t, "0 1 1 0 1 1", "cognito");
    row(t, "0 1 1 1 0 1", "cognito");
    row(t, "0 1 1 1 1 1", "cognito");
    // Email identifier, code key, no Cognito: with the password off the code is the default method.
    row(t, "1 0 0 0 0 1", "code");
    row(t, "1 0 0 0 1 1", "password");
    row(t, "1 0 0 1 0 1", "code");
    row(t, "1 0 0 1 1 1", "password");
    row(t, "1 0 1 0 0 1", "code");
    row(t, "1 0 1 0 1 1", "code+password");
    row(t, "1 0 1 1 0 1", "code");
    row(t, "1 0 1 1 1 1", "password");
    // Email identifier, code key, Cognito account: the password switch plays no part.
    row(t, "1 1 0 0 0 1", "cognito");
    row(t, "1 1 0 0 1 1", "cognito");
    row(t, "1 1 0 1 0 1", "cognito");
    row(t, "1 1 0 1 1 1", "cognito");
    row(t, "1 1 1 0 0 1", "cognito+code");
    row(t, "1 1 1 0 1 1", "cognito+code");
    row(t, "1 1 1 1 0 1", "cognito");
    row(t, "1 1 1 1 1 1", "cognito");

    // Username identifier (E = 0), no code key: as for an email, the code never applies.
    row(t, "0 0 0 0 0 0", "password");
    row(t, "0 0 0 0 1 0", "password");
    row(t, "0 0 0 1 0 0", "password");
    row(t, "0 0 0 1 1 0", "password");
    row(t, "0 0 1 0 0 0", "password");
    row(t, "0 0 1 0 1 0", "password");
    row(t, "0 0 1 1 0 0", "password");
    row(t, "0 0 1 1 1 0", "password");
    row(t, "0 1 0 0 0 0", "cognito");
    row(t, "0 1 0 0 1 0", "cognito");
    row(t, "0 1 0 1 0 0", "cognito");
    row(t, "0 1 0 1 1 0", "cognito");
    row(t, "0 1 1 0 0 0", "cognito");
    row(t, "0 1 1 0 1 0", "cognito");
    row(t, "0 1 1 1 0 0", "cognito");
    row(t, "0 1 1 1 1 0", "cognito");
    // Username identifier, code key, no Cognito: the password when it is on; with it off only the code is left,
    // and a username cannot receive it, so the member is told to use the email (FN-010).
    row(t, "1 0 0 0 0 0", "needsEmail");
    row(t, "1 0 0 0 1 0", "password");
    row(t, "1 0 0 1 0 0", "needsEmail");
    row(t, "1 0 0 1 1 0", "password");
    row(t, "1 0 1 0 0 0", "needsEmail");
    row(t, "1 0 1 0 1 0", "password");
    row(t, "1 0 1 1 0 0", "needsEmail");
    row(t, "1 0 1 1 1 0", "password");
    // Username identifier, code key, Cognito account: Cognito only.
    row(t, "1 1 0 0 0 0", "cognito");
    row(t, "1 1 0 0 1 0", "cognito");
    row(t, "1 1 0 1 0 0", "cognito");
    row(t, "1 1 0 1 1 0", "cognito");
    row(t, "1 1 1 0 0 0", "cognito");
    row(t, "1 1 1 0 1 0", "cognito");
    row(t, "1 1 1 1 0 0", "cognito");
    row(t, "1 1 1 1 1 0", "cognito");
    return t;
  }

  private static void row(Map<String, String> table, String key, String expected) {
    if (table.put(key, expected) != null) {
      throw new AssertionError("row listed twice: " + key);
    }
  }

  /** One combination, decoded from its index and turned into the six inputs of the rule. */
  private static final class Row {

    private final boolean k;
    private final boolean c;
    private final boolean s;
    private final boolean x;
    private final boolean p;
    private final boolean e;

    private Row(int index) {
      this.k = (index & 32) != 0;
      this.c = (index & 16) != 0;
      this.s = (index & 8) != 0;
      this.x = (index & 4) != 0;
      this.p = (index & 2) != 0;
      this.e = (index & 1) != 0;
    }

    private String key() {
      return bit(this.k) + " " + bit(this.c) + " " + bit(this.s) + " " + bit(this.x) + " " + bit(this.p) + " "
        + bit(this.e);
    }

    private SignInMethodRule.Methods methods(String passwordSwitchValue) {
      String codeSwitchValue = this.s ? "true" : "false";
      // "excluded.org" lists the email's domain (a hit); "other.org" does not. A username has no domain either way.
      String exclusionValue = this.x ? "excluded.org" : "other.org";
      String identifier = this.e ? "ana@excluded.org" : "ana.lopez";
      return SignInMethodRule.methodsFor(this.k, this.c, passwordSwitchValue, codeSwitchValue, exclusionValue,
        identifier);
    }

    /** The row with the password switch spelled in its plain form, {@code true} or {@code false}. */
    private SignInMethodRule.Methods methods() {
      return this.methods(this.p ? "true" : "false");
    }

    private static int bit(boolean value) {
      return value ? 1 : 0;
    }
  }
}
