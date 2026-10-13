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

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;
import java.util.function.IntUnaryOperator;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Codes, nonces and code MACs of the Center email one-time-code sign-in (CHG-OTP-LOGIN-001-T05, FN-009, SEC-001,
 * SEC-003).
 * <p>
 * A code is {@code SecureRandom.nextInt(1_000_000)} zero-padded to six digits, so leading zeros are kept. A nonce is
 * 16 random bytes as 22 base64url characters. The only value ever stored for a code is
 * {@code HMAC-SHA256(k_code, "<nonce>|<code>")} in lower-case hex, so a MAC is bound to its challenge. The check
 * decodes the stored hex and compares the bytes with {@link MessageDigest#isEqual(byte[], byte[])}, which takes
 * constant time; it returns {@code false} on any shape error and never throws.
 * <p>
 * It is a container singleton: {@code @Named}, found by the component scan, so every action shares one seeded
 * {@link SecureRandom}. Do not construct it in an action: inject it.
 */
@Named
public final class OtpCodes {

  private static final int CODE_BOUND = 1_000_000;
  private static final int NONCE_BYTES = 16;
  private static final int MAC_HEX_LENGTH = 64;
  private static final String MAC_ALGORITHM = "HmacSHA256";

  private final OtpKeys keys;
  private final IntUnaryOperator codeSource;
  private final SecureRandom random = new SecureRandom();

  /**
   * The only constructor the container may use; the one below is the test seam.
   *
   * @param keys the derived MAC keys
   */
  @Inject
  public OtpCodes(OtpKeys keys) {
    this.keys = keys;
    this.codeSource = this.random::nextInt;
  }

  /**
   * @param keys the derived MAC keys
   * @param codeSource returns a value in {@code [0, bound)}; a stub in tests
   */
  OtpCodes(OtpKeys keys, IntUnaryOperator codeSource) {
    this.keys = keys;
    this.codeSource = codeSource;
  }

  /**
   * @return a uniformly drawn code of exactly six digits, with leading zeros kept
   */
  public String newCode() {
    return String.format(Locale.ROOT, "%06d", this.codeSource.applyAsInt(CODE_BOUND));
  }

  /**
   * @return 16 random bytes as 22 base64url characters, without padding
   */
  public String newNonce() {
    byte[] bytes = new byte[NONCE_BYTES];
    this.random.nextBytes(bytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  }

  /**
   * @param nonce the challenge nonce
   * @param code the code
   * @return {@code HMAC-SHA256(k_code, nonce|code)} as lower-case hex
   * @throws IllegalStateException when the keys are not configured
   */
  public String mac(String nonce, String code) {
    return HexFormat.of().formatHex(this.macBytes(nonce, code));
  }

  /**
   * Compares a presented code with the stored MAC in constant time.
   *
   * @param storedMacHex the stored {@code code_hmac}, possibly malformed or {@code null}
   * @param nonce the challenge nonce
   * @param code the presented code
   * @return {@code true} only when the stored MAC is 64 hex characters equal to the MAC of the presented code.
   *         {@code false} on a malformed value, a {@code null} argument or unconfigured keys. Never throws
   */
  public boolean matches(String storedMacHex, String nonce, String code) {
    if (storedMacHex == null || nonce == null || code == null || storedMacHex.length() != MAC_HEX_LENGTH) {
      return false;
    }
    try {
      byte[] stored = HexFormat.of().parseHex(storedMacHex);
      return MessageDigest.isEqual(stored, this.macBytes(nonce, code));
    } catch (RuntimeException e) {
      return false;
    }
  }

  private byte[] macBytes(String nonce, String code) {
    byte[] key = this.keys.codeKey();
    try {
      Mac mac = Mac.getInstance(MAC_ALGORITHM);
      mac.init(new SecretKeySpec(key, MAC_ALGORITHM));
      return mac.doFinal((nonce + "|" + code).getBytes(StandardCharsets.UTF_8));
    } catch (GeneralSecurityException e) {
      // HmacSHA256 is a mandatory JCA algorithm and the key is non-empty: unreachable on a conforming JRE.
      throw new IllegalStateException("HmacSHA256 is unavailable", e);
    }
  }
}
