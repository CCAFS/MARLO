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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

/**
 * CHG-OTP-LOGIN-001-T05, FN-003 and SEC-009: the pure email and allow-list rules. Every expectation is a literal
 * taken from the FN-003 scenario or the task's test vectors; none is built with the normaliser under test.
 */
public class OtpDomainPolicyTest {

  private static final List<String> TRUSTED = Collections.singletonList("trusted.org");

  // ---- FN-003 scenario: lookalikes are rejected ---------------------------------------------------------------

  @Test
  public void suffixLookalikeIsNotAllowed() {
    assertFalse(OtpDomainPolicy.isAllowed("x@attackertrusted.org", TRUSTED));
  }

  @Test
  public void allowListedNameAsLeftLabelOfAnotherDomainIsNotAllowed() {
    assertFalse(OtpDomainPolicy.isAllowed("x@trusted.org.evil.com", TRUSTED));
  }

  @Test
  public void unlistedSubdomainIsNotAllowed() {
    assertFalse(OtpDomainPolicy.isAllowed("x@sub.trusted.org", TRUSTED));
  }

  @Test
  public void listedSubdomainIsAllowedWhenListedItself() {
    assertTrue(OtpDomainPolicy.isAllowed("x@sub.trusted.org", Arrays.asList("trusted.org", "sub.trusted.org")));
  }

  @Test
  public void emailWithNoDomainIsNotAllowed() {
    assertFalse(OtpDomainPolicy.isAllowed("a@", TRUSTED));
  }

  @Test
  public void emailWithNoLocalPartIsNotAllowed() {
    assertFalse(OtpDomainPolicy.isAllowed("@trusted.org", TRUSTED));
  }

  @Test
  public void emailWithTwoAtSignsIsNotAllowed() {
    assertFalse(OtpDomainPolicy.isAllowed("a@b@trusted.org", TRUSTED));
    assertFalse(OtpDomainPolicy.isAllowed("a@trusted.org@evil.com", TRUSTED));
  }

  @Test
  public void nullAndEmptyEmailAreNotAllowed() {
    assertFalse(OtpDomainPolicy.isAllowed(null, TRUSTED));
    assertFalse(OtpDomainPolicy.isAllowed("", TRUSTED));
  }

  @Test
  public void nullAllowListAllowsNothing() {
    assertFalse(OtpDomainPolicy.isAllowed("x@trusted.org", null));
  }

  @Test
  public void paddedUpperCaseEmailIsAllowedAndNormalised() {
    assertTrue(OtpDomainPolicy.isAllowed("  X@TRUSTED.ORG ", TRUSTED));
    assertEquals("x@trusted.org", OtpDomainPolicy.normaliseEmail("  X@TRUSTED.ORG "));
  }

  @Test
  public void normaliseEmailOfNullIsEmpty() {
    assertEquals("", OtpDomainPolicy.normaliseEmail(null));
  }

  // ---- domainOf: exactly one '@' -------------------------------------------------------------------------------

  @Test
  public void domainOfIsThePartAfterTheSingleAt() {
    assertEquals("partner.org", OtpDomainPolicy.domainOf("ana@partner.org"));
  }

  @Test
  public void domainOfIsNullWithoutExactlyOneAtOrWithAnEmptySide() {
    assertNull(OtpDomainPolicy.domainOf("anapartner.org"));
    assertNull(OtpDomainPolicy.domainOf("a@b@partner.org"));
    assertNull(OtpDomainPolicy.domainOf("a@"));
    assertNull(OtpDomainPolicy.domainOf("@partner.org"));
    assertNull(OtpDomainPolicy.domainOf(""));
    assertNull(OtpDomainPolicy.domainOf(null));
  }

  // ---- allow-list parsing --------------------------------------------------------------------------------------

  @Test
  public void allowListKeepsOnlyTheNormalisedTrustedOrgEntry() {
    // Entries: leading @ and mixed case (kept), a duplicate, a full address (dropped), and cgiar.org (SEC-009).
    List<String> parsed = OtpDomainPolicy.parseAllowList(" @Trusted.ORG , trusted.org, x@y.org, cgiar.org");

    assertEquals(Collections.singletonList("trusted.org"), parsed);
  }

  @Test
  public void allowListKeepsFirstSeenOrderAndDropsDuplicates() {
    assertEquals(Arrays.asList("b.org", "a.org"), OtpDomainPolicy.parseAllowList("B.org,a.org,@B.ORG"));
  }

  @Test
  public void allowListDropsEmptyEntriesAndEntriesThatStillContainAnAt() {
    assertEquals(Collections.singletonList("ok.org"), OtpDomainPolicy.parseAllowList(",, ,@,@@bad.org,ok.org"));
  }

  @Test
  public void allowListDropsEveryCgiarOrgSpelling() {
    assertTrue(OtpDomainPolicy.parseAllowList("cgiar.org, @CGIAR.org ,CgIaR.OrG").isEmpty());
  }

  @Test
  public void allowListDoesNotDropTheCgiarOrgSubdomainsOrLookalikes() {
    assertEquals(Arrays.asList("ccafs.cgiar.org", "notcgiar.org"),
      OtpDomainPolicy.parseAllowList("ccafs.cgiar.org, notcgiar.org"));
  }

  @Test
  public void nullOrBlankAllowListParsesToEmpty() {
    assertTrue(OtpDomainPolicy.parseAllowList(null).isEmpty());
    assertTrue(OtpDomainPolicy.parseAllowList("").isEmpty());
    assertTrue(OtpDomainPolicy.parseAllowList("   ").isEmpty());
  }

  // ---- mask ----------------------------------------------------------------------------------------------------

  @Test
  public void maskKeepsTheFirstCharacterAndTheDomain() {
    assertEquals("a***@partner.org", OtpDomainPolicy.mask("ana@partner.org"));
  }

  @Test
  public void maskNormalisesBeforeMasking() {
    assertEquals("a***@partner.org", OtpDomainPolicy.mask("  ANA@Partner.ORG "));
  }

  @Test
  public void maskOfOneCharacterLocalPartStillHidesNothingMoreThanTheStars() {
    assertEquals("a***@partner.org", OtpDomainPolicy.mask("a@partner.org"));
  }

  @Test
  public void maskOfAMalformedEmailRevealsNothing() {
    assertEquals("***", OtpDomainPolicy.mask("not-an-email"));
    assertEquals("***", OtpDomainPolicy.mask("a@b@c.org"));
    assertEquals("***", OtpDomainPolicy.mask("@partner.org"));
    assertEquals("***", OtpDomainPolicy.mask(null));
  }

  // ---- FN-003 (amended 2026-10-13): printable ASCII only, so case folding cannot map a lookalike -------------------

  @Test
  public void kelvinSignEmailIsRejectedAndDoesNotFoldOntoAnotherDomain() {
    // U+212A KELVIN SIGN lower-cases to ASCII 'k', so "x@<K>ey.org" would otherwise become "x@key.org".
    String kelvin = "x@\u212Aey.org";

    assertFalse(OtpDomainPolicy.isAllowed(kelvin, Collections.singletonList("key.org")));
    assertNull(OtpDomainPolicy.domainOf(kelvin));
    assertEquals("", OtpDomainPolicy.normaliseEmail(kelvin));
    assertEquals("***", OtpDomainPolicy.mask(kelvin));
  }

  @Test
  public void kelvinSignInTheLocalPartIsRejected() {
    assertFalse(OtpDomainPolicy.isAllowed("\u212A@trusted.org", TRUSTED));
    assertNull(OtpDomainPolicy.domainOf("\u212A@trusted.org"));
  }

  @Test
  public void anyNonAsciiOrNonPrintableCharacterInAnEmailIsRejected() {
    String[] rejected = {"x@tr\u00fcsted.org", "x@\uFF54rusted.org", "x@trusted.org\u00a0", "x@trusted\u200b.org",
      "a b@trusted.org", "a\u0001@trusted.org", "x@trusted.org\u007f", "\u0130x@trusted.org"};
    for (String email : rejected) {
      assertFalse("allowed: " + email, OtpDomainPolicy.isAllowed(email, TRUSTED));
      assertNull("domain: " + email, OtpDomainPolicy.domainOf(email));
    }
  }

  @Test
  public void everyPrintableAsciiCharacterIsStillAccepted() {
    assertEquals("trusted.org", OtpDomainPolicy.domainOf("!#$%&'*+/=?^_`{|}~-.x@trusted.org"));
  }

  @Test
  public void allowListEntriesWithAKelvinSignOrAnyNonAsciiCharacterAreDropped() {
    assertEquals(Collections.singletonList("ok.org"), OtpDomainPolicy.parseAllowList("\u212Aey.org, ok.org"));
    assertEquals(Collections.singletonList("ok.org"),
      OtpDomainPolicy.parseAllowList("m\u00fcnchen.org,@\u00e9.org, ok.org ,trusted.org\u00a0"));
  }

  @Test
  public void allowListEntryWithAnInternalSpaceIsDropped() {
    assertEquals(Collections.singletonList("ok.org"), OtpDomainPolicy.parseAllowList("a b.org, ok.org"));
  }
}
