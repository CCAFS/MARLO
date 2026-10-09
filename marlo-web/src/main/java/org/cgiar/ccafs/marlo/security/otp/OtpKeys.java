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

import org.cgiar.ccafs.marlo.utils.APConfig;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The three MAC keys of the Center email one-time-code sign-in (CHG-OTP-LOGIN-001, SEC-008, DD-10).
 * <p>
 * The secret is read once, from {@link APConfig#getOtpHmacSecret()}, and must be <b>standard base64 that
 * decodes to at least 32 bytes</b> (256 bits). Anything else -- empty, not base64, base64url, too short --
 * leaves the instance <i>unconfigured</i>: {@link #isConfigured()} is {@code false}, no key is derived, and
 * one WARN line explains the reason <b>without</b> the value, its length or its decoded length. There is no
 * generated or default fallback key. Application startup is never affected.
 * <p>
 * When configured, {@code k_email}, {@code k_code} and {@code k_bucket} are
 * {@code HMAC-SHA256(decoded secret, "<tag>\0")} with the distinct tags {@code otp-email}, {@code otp-code}
 * and {@code otp-bucket}, so a MAC made for one purpose can never be replayed as another.
 */
public final class OtpKeys {

  private static final Logger LOG = LoggerFactory.getLogger(OtpKeys.class);

  /** 256 bits. */
  static final int MIN_SECRET_BYTES = 32;

  private static final String MAC_ALGORITHM = "HmacSHA256";
  private static final String TAG_EMAIL = "otp-email";
  private static final String TAG_CODE = "otp-code";
  private static final String TAG_BUCKET = "otp-bucket";

  private final byte[] emailKey;
  private final byte[] codeKey;
  private final byte[] bucketKey;

  /**
   * Reads the secret once from the configuration.
   *
   * @param config the application configuration; its OTP secret getter never returns {@code null}
   */
  public OtpKeys(APConfig config) {
    this(config.getOtpHmacSecret());
  }

  /**
   * @param secret the raw configured value, possibly {@code null} or empty
   */
  OtpKeys(String secret) {
    String problem = null;
    byte[] decoded = null;
    if (secret == null || secret.isEmpty()) {
      problem = "is not set";
    } else {
      try {
        decoded = Base64.getDecoder().decode(secret);
      } catch (IllegalArgumentException e) {
        problem = "is not standard base64";
      }
      if (decoded != null && decoded.length < MIN_SECRET_BYTES) {
        problem = "decodes to fewer than " + MIN_SECRET_BYTES + " bytes";
      }
    }
    if (problem != null) {
      LOG.warn("auth.otp.keys outcome=unconfigured: the one-time-code secret {}; code sign-in is unavailable.",
        problem);
      this.emailKey = null;
      this.codeKey = null;
      this.bucketKey = null;
    } else {
      this.emailKey = derive(decoded, TAG_EMAIL);
      this.codeKey = derive(decoded, TAG_CODE);
      this.bucketKey = derive(decoded, TAG_BUCKET);
    }
    if (decoded != null) {
      Arrays.fill(decoded, (byte) 0);
    }
  }

  private static byte[] derive(byte[] secret, String tag) {
    try {
      Mac mac = Mac.getInstance(MAC_ALGORITHM);
      mac.init(new SecretKeySpec(secret, MAC_ALGORITHM));
      return mac.doFinal((tag + "\0").getBytes(StandardCharsets.US_ASCII));
    } catch (GeneralSecurityException e) {
      // HmacSHA256 is a mandatory JCA algorithm and the key is non-empty: unreachable on a conforming JRE.
      throw new IllegalStateException("HmacSHA256 is unavailable", e);
    }
  }

  private byte[] require(byte[] key) {
    if (key == null) {
      throw new IllegalStateException("the one-time-code secret is not configured");
    }
    return key.clone();
  }

  /**
   * @return a copy of {@code k_bucket}, the key that blinds the rate-limit buckets
   * @throws IllegalStateException when {@link #isConfigured()} is {@code false}
   */
  public byte[] bucketKey() {
    return this.require(this.bucketKey);
  }

  /**
   * @return a copy of {@code k_code}, the key that MACs the code
   * @throws IllegalStateException when {@link #isConfigured()} is {@code false}
   */
  public byte[] codeKey() {
    return this.require(this.codeKey);
  }

  /**
   * @return a copy of {@code k_email}, the key that MACs the normalised email
   * @throws IllegalStateException when {@link #isConfigured()} is {@code false}
   */
  public byte[] emailKey() {
    return this.require(this.emailKey);
  }

  /**
   * @return {@code true} only when the secret was standard base64 of at least 32 bytes
   */
  public boolean isConfigured() {
    return this.emailKey != null;
  }
}
