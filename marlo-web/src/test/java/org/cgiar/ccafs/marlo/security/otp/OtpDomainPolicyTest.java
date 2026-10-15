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
 * CHG-OTP-LOGIN-001-T05, T21, FN-001, FN-003 and SEC-009: the pure email, switch and exclusion rules. Every
 * expectation is a literal taken from the FN-003 scenario or the task's test vectors; none is built with the
 * normaliser under test.
 */
public class OtpDomainPolicyTest {

  private static final List<String> EXCLUDED = Collections.singletonList("excluded.org");

  // ---- FN-003 scenario: lookalikes are rejected ---------------------------------------------------------------

  @Test
  public void suffixLookalikeIsNotExcluded() {
    assertFalse(OtpDomainPolicy.isExcluded("x@attackerexcluded.org", EXCLUDED));
  }

  @Test
  public void excludedNameAsLeftLabelOfAnotherDomainIsNotExcluded() {
    assertFalse(OtpDomainPolicy.isExcluded("x@excluded.org.evil.com", EXCLUDED));
  }

  @Test
  public void unlistedSubdomainIsNotExcluded() {
    assertFalse(OtpDomainPolicy.isExcluded("x@sub.excluded.org", EXCLUDED));
  }

  @Test
  public void listedSubdomainIsExcludedWhenListedItself() {
    assertTrue(OtpDomainPolicy.isExcluded("x@sub.excluded.org", Arrays.asList("excluded.org", "sub.excluded.org")));
  }

  @Test
  public void emailWithNoDomainIsNotExcluded() {
    assertFalse(OtpDomainPolicy.isExcluded("a@", EXCLUDED));
  }

  @Test
  public void emailWithNoLocalPartIsNotExcluded() {
    assertFalse(OtpDomainPolicy.isExcluded("@excluded.org", EXCLUDED));
  }

  @Test
  public void emailWithTwoAtSignsIsNotExcluded() {
    assertFalse(OtpDomainPolicy.isExcluded("a@b@excluded.org", EXCLUDED));
    assertFalse(OtpDomainPolicy.isExcluded("a@excluded.org@evil.com", EXCLUDED));
  }

  @Test
  public void nullAndEmptyEmailAreNotExcluded() {
    assertFalse(OtpDomainPolicy.isExcluded(null, EXCLUDED));
    assertFalse(OtpDomainPolicy.isExcluded("", EXCLUDED));
  }

  @Test
  public void nullExclusionListExcludesNothing() {
    assertFalse(OtpDomainPolicy.isExcluded("x@excluded.org", null));
  }

  @Test
  public void paddedUpperCaseEmailIsExcludedAndNormalised() {
    assertTrue(OtpDomainPolicy.isExcluded("  X@EXCLUDED.ORG ", EXCLUDED));
    assertEquals("x@excluded.org", OtpDomainPolicy.normaliseEmail("  X@EXCLUDED.ORG "));
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

  // ---- exclusion-list parsing --------------------------------------------------------------------------------------

  @Test
  public void exclusionListKeepsTheNormalisedEntriesIncludingCgiarOrg() {
    // Entries: leading @ and mixed case (kept), a duplicate, a full address (dropped), and cgiar.org, which is an
    // entry like any other (SEC-009, DD-13): no domain is fixed in code.
    List<String> parsed = OtpDomainPolicy.parseExclusionList(" @Excluded.ORG , excluded.org, x@y.org, cgiar.org");

    assertEquals(Arrays.asList("excluded.org", "cgiar.org"), parsed);
  }

  @Test
  public void exclusionListKeepsFirstSeenOrderAndDropsDuplicates() {
    assertEquals(Arrays.asList("b.org", "a.org"), OtpDomainPolicy.parseExclusionList("B.org,a.org,@B.ORG"));
  }

  @Test
  public void exclusionListDropsEmptyEntriesAndEntriesThatStillContainAnAt() {
    assertEquals(Collections.singletonList("ok.org"), OtpDomainPolicy.parseExclusionList(",, ,@,@@bad.org,ok.org"));
  }

  @Test
  public void everyCgiarOrgSpellingParsesToTheOneEntryAndExcludesItsAddresses() {
    List<String> parsed = OtpDomainPolicy.parseExclusionList("cgiar.org, @CGIAR.org ,CgIaR.OrG");

    assertEquals(Collections.singletonList("cgiar.org"), parsed);
    assertTrue(OtpDomainPolicy.isExcluded("staff@cgiar.org", parsed));
    assertFalse("the empty list excludes nobody, cgiar.org included",
      OtpDomainPolicy.isExcluded("staff@cgiar.org", OtpDomainPolicy.parseExclusionList("")));
  }

  @Test
  public void exclusionListDoesNotDropTheCgiarOrgSubdomainsOrLookalikes() {
    assertEquals(Arrays.asList("ccafs.cgiar.org", "notcgiar.org"),
      OtpDomainPolicy.parseExclusionList("ccafs.cgiar.org, notcgiar.org"));
  }

  @Test
  public void nullOrBlankExclusionListParsesToEmpty() {
    assertTrue(OtpDomainPolicy.parseExclusionList(null).isEmpty());
    assertTrue(OtpDomainPolicy.parseExclusionList("").isEmpty());
    assertTrue(OtpDomainPolicy.parseExclusionList("   ").isEmpty());
  }

  // ---- the switch (FN-001) and the rule that joins it to the exclusion (FN-003, DD-13) --------------------------

  @Test
  public void onlyTheTextTrueSwitchesCodeSignInOn() {
    for (String on : new String[] {"true", "TRUE", " True "}) {
      assertTrue("on: <" + on + ">", OtpDomainPolicy.isSwitchOn(on));
    }
    for (String off : new String[] {"false", "", "   ", null, "yes", "1", "truee", "t"}) {
      assertFalse("off: <" + off + ">", OtpDomainPolicy.isSwitchOn(off));
    }
  }

  @Test
  public void switchOnWithAnEmptyExclusionOffersTheCodeToEveryDomainCgiarOrgIncluded() {
    for (String exclusion : new String[] {"", "   ", null}) {
      assertTrue("exclusion <" + exclusion + ">", OtpDomainPolicy.isOffered("true", exclusion, "x@partner.org"));
      assertTrue("exclusion <" + exclusion + ">", OtpDomainPolicy.isOffered("true", exclusion, "staff@cgiar.org"));
    }
  }

  @Test
  public void anExcludedDomainIsNotOfferedAndItsSubdomainAndLookalikesAre() {
    String exclusion = "cgiar.org, example.org";

    assertFalse(OtpDomainPolicy.isOffered("true", exclusion, "staff@cgiar.org"));
    assertFalse(OtpDomainPolicy.isOffered("true", exclusion, "  STAFF@CGIAR.ORG "));
    assertFalse(OtpDomainPolicy.isOffered("true", exclusion, "x@example.org"));
    assertTrue(OtpDomainPolicy.isOffered("true", exclusion, "x@sub.cgiar.org"));
    assertTrue(OtpDomainPolicy.isOffered("true", exclusion, "x@notcgiar.org"));
    assertTrue(OtpDomainPolicy.isOffered("true", exclusion, "x@cgiar.org.evil.com"));
    assertTrue(OtpDomainPolicy.isOffered("true", exclusion, "x@partner.org"));
  }

  @Test
  public void aSwitchThatIsOffOffersNothingWhateverTheExclusion() {
    for (String off : new String[] {"false", "", null, "no"}) {
      assertFalse("switch <" + off + ">", OtpDomainPolicy.isOffered(off, "", "x@partner.org"));
      assertFalse("switch <" + off + ">", OtpDomainPolicy.isOffered(off, "other.org", "x@partner.org"));
    }
  }

  @Test
  public void anEmailWithoutADomainIsNeverOffered() {
    for (String email : new String[] {null, "", "no-at-sign", "a@b@partner.org", "@partner.org", "user@",
      "x@\u212Aey.org"}) {
      assertFalse("email <" + email + ">", OtpDomainPolicy.isOffered("true", "", email));
    }
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

    assertFalse(OtpDomainPolicy.isExcluded(kelvin, Collections.singletonList("key.org")));
    assertNull(OtpDomainPolicy.domainOf(kelvin));
    assertEquals("", OtpDomainPolicy.normaliseEmail(kelvin));
    assertEquals("***", OtpDomainPolicy.mask(kelvin));
  }

  @Test
  public void kelvinSignInTheLocalPartIsRejected() {
    assertFalse(OtpDomainPolicy.isExcluded("\u212A@excluded.org", EXCLUDED));
    assertNull(OtpDomainPolicy.domainOf("\u212A@excluded.org"));
  }

  @Test
  public void anyNonAsciiOrNonPrintableCharacterInAnEmailIsRejected() {
    String[] rejected = {"x@ex\u00fccluded.org", "x@\uFF45xcluded.org", "x@excluded.org\u00a0", "x@excluded\u200b.org",
      "a b@excluded.org", "a\u0001@excluded.org", "x@excluded.org\u007f", "\u0130x@excluded.org"};
    for (String email : rejected) {
      assertFalse("excluded: " + email, OtpDomainPolicy.isExcluded(email, EXCLUDED));
      assertNull("domain: " + email, OtpDomainPolicy.domainOf(email));
    }
  }

  @Test
  public void everyPrintableAsciiCharacterIsStillAccepted() {
    assertEquals("excluded.org", OtpDomainPolicy.domainOf("!#$%&'*+/=?^_`{|}~-.x@excluded.org"));
  }

  @Test
  public void exclusionListEntriesWithAKelvinSignOrAnyNonAsciiCharacterAreDropped() {
    assertEquals(Collections.singletonList("ok.org"), OtpDomainPolicy.parseExclusionList("\u212Aey.org, ok.org"));
    assertEquals(Collections.singletonList("ok.org"),
      OtpDomainPolicy.parseExclusionList("m\u00fcnchen.org,@\u00e9.org, ok.org ,excluded.org\u00a0"));
  }

  @Test
  public void exclusionListEntryWithAnInternalSpaceIsDropped() {
    assertEquals(Collections.singletonList("ok.org"), OtpDomainPolicy.parseExclusionList("a b.org, ok.org"));
  }
}
