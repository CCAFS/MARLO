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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.IntUnaryOperator;

import org.junit.Test;

/**
 * CHG-OTP-LOGIN-001-T05, FN-009, SEC-001 and SEC-003. The MAC expectations are known-answer literals produced outside
 * this code base ({@code openssl dgst -sha256 -mac HMAC -macopt hexkey:...} and {@code python3 hmac}, which agreed):
 * the key is {@code k_code} of the secret {@code 00 01 ... 1f} (see {@code OtpKeysTest}), the message is
 * {@code "<nonce>|<code>"}. The test never computes an expectation with the code under test. Constant time cannot be
 * tested; the {@code MessageDigest.isEqual} choice is a review item.
 */
public class OtpCodesTest {

  /** base64 of the 32 bytes 0x00..0x1f. */
  private static final String SECRET = "AAECAwQFBgcICQoLDA0ODxAREhMUFRYXGBkaGxwdHh8=";

  private static final String NONCE = "AAAAAAAAAAAAAAAAAAAAAA";
  private static final String OTHER_NONCE = "BBBBBBBBBBBBBBBBBBBBBB";

  /** HMAC-SHA256(k_code, "AAAAAAAAAAAAAAAAAAAAAA|004219"), hex. */
  private static final String MAC_NONCE_A_CODE_004219 =
    "8d4212554f48fecc8173a324eb9565ac2ccc0c75111bd3a861b952520d918ff7";
  /** HMAC-SHA256(k_code, "BBBBBBBBBBBBBBBBBBBBBB|004219"), hex. */
  private static final String MAC_NONCE_B_CODE_004219 =
    "c3f38889e4bf46a51c26994b10de490a7a55514ffabdcd23937c07df6f341d06";

  private final OtpCodes codes = new OtpCodes(new OtpKeys(SECRET));

  // ---- FN-009: format ------------------------------------------------------------------------------------------

  @Test
  public void generatorValue4219YieldsSixDigitsWithLeadingZeros() {
    List<Integer> bounds = new ArrayList<Integer>();
    IntUnaryOperator stub = bound -> {
      bounds.add(bound);
      return 4219;
    };

    String code = new OtpCodes(new OtpKeys(SECRET), stub).newCode();

    assertEquals("004219", code);
    assertEquals("the generator must be asked for a value below 10^6, nothing else",
      Collections.singletonList(1_000_000), bounds);
  }

  @Test
  public void generatorValueZeroYieldsSixZeros() {
    assertEquals("000000", new OtpCodes(new OtpKeys(SECRET), bound -> 0).newCode());
  }

  @Test
  public void generatorValueMaximumYieldsSixNines() {
    assertEquals("999999", new OtpCodes(new OtpKeys(SECRET), bound -> 999_999).newCode());
  }

  @Test
  public void everyOneHundredThousandGeneratedCodesIsSixDigits() {
    for (int i = 0; i < 100_000; i++) {
      String code = this.codes.newCode();
      assertTrue("code " + i + " has the wrong shape", code.matches("[0-9]{6}"));
    }
  }

  @Test
  public void nonceIsTwentyTwoBase64UrlCharactersAndDoesNotRepeat() {
    Set<String> seen = new HashSet<String>();
    for (int i = 0; i < 1_000; i++) {
      String nonce = this.codes.newNonce();
      assertTrue("nonce " + nonce, nonce.matches("[A-Za-z0-9_-]{22}"));
      seen.add(nonce);
    }
    assertEquals(1_000, seen.size());
  }

  // ---- SEC-001: the stored value is a keyed MAC ----------------------------------------------------------------

  @Test
  public void macMatchesTheIndependentKnownAnswer() {
    assertEquals(MAC_NONCE_A_CODE_004219, this.codes.mac(NONCE, "004219"));
  }

  @Test
  public void macDependsOnTheNonce() {
    assertEquals(MAC_NONCE_B_CODE_004219, this.codes.mac(OTHER_NONCE, "004219"));
    assertNotEquals(this.codes.mac(NONCE, "004219"), this.codes.mac(OTHER_NONCE, "004219"));
  }

  @Test
  public void macIsSixtyFourLowerCaseHexCharacters() {
    assertTrue(this.codes.mac(NONCE, "123456").matches("[0-9a-f]{64}"));
  }

  // ---- SEC-003: compare, and never throw -----------------------------------------------------------------------

  @Test
  public void correctCodeMatchesItsStoredMac() {
    assertTrue(this.codes.matches(MAC_NONCE_A_CODE_004219, NONCE, "004219"));
  }

  @Test
  public void storedMacInUpperCaseHexStillMatches() {
    assertTrue(this.codes.matches(MAC_NONCE_A_CODE_004219.toUpperCase(Locale.ROOT), NONCE, "004219"));
  }

  @Test
  public void wrongCodeDoesNotMatch() {
    assertFalse(this.codes.matches(MAC_NONCE_A_CODE_004219, NONCE, "004218"));
  }

  @Test
  public void codeFromAnotherNonceDoesNotMatch() {
    assertFalse(this.codes.matches(MAC_NONCE_A_CODE_004219, OTHER_NONCE, "004219"));
  }

  @Test
  public void malformedStoredMacReturnsFalseWithoutThrowing() {
    String[] malformed = {null, "", "zz", "8d42", MAC_NONCE_A_CODE_004219.substring(1),
      MAC_NONCE_A_CODE_004219 + "00", "g" + MAC_NONCE_A_CODE_004219.substring(1),
      MAC_NONCE_A_CODE_004219.substring(0, 63) + " ",
      "not hex, but exactly sixty-four characters long. Padding padding"};
    for (String stored : malformed) {
      assertFalse("stored=" + stored, this.codes.matches(stored, NONCE, "004219"));
    }
  }

  @Test
  public void nullNonceOrCodeReturnsFalseWithoutThrowing() {
    assertFalse(this.codes.matches(MAC_NONCE_A_CODE_004219, null, "004219"));
    assertFalse(this.codes.matches(MAC_NONCE_A_CODE_004219, NONCE, null));
  }

  @Test
  public void unconfiguredKeysReturnFalseWithoutThrowing() {
    OtpCodes unconfigured = new OtpCodes(new OtpKeys(""));

    assertFalse(unconfigured.matches(MAC_NONCE_A_CODE_004219, NONCE, "004219"));
  }
}
