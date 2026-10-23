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

import java.util.Arrays;
import java.util.List;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Covers CHG-OTP-LOGIN-001-T02: {@link OtpKeys} (SEC-008, DD-10).
 * <p>
 * Every expected value is an independent literal: the base64 secrets and the three HMAC-SHA256 known-answer
 * vectors were produced outside this code base with {@code python3} and {@code openssl dgst -mac HMAC} (the
 * two agreed) from the key bytes {@code 00 01 02 ... 1f}. The test never derives an expectation with the
 * algorithm under test.
 */
public class OtpKeysTest {

  /** base64 of the 32 bytes 0x00..0x1f. */
  private static final String SECRET_32_BYTES = "AAECAwQFBgcICQoLDA0ODxAREhMUFRYXGBkaGxwdHh8=";
  /** base64 of the 31 bytes 0x00..0x1e. */
  private static final String SECRET_31_BYTES = "AAECAwQFBgcICQoLDA0ODxAREhMUFRYXGBkaGxwdHg==";
  /** base64 of the 48 bytes 0x00..0x2f (what {@code openssl rand -base64 48} produces in shape). */
  private static final String SECRET_48_BYTES =
    "AAECAwQFBgcICQoLDA0ODxAREhMUFRYXGBkaGxwdHh8gISIjJCUmJygpKissLS4v";
  /** The URL-safe alphabet encoding of 32 bytes of 0xfb: valid base64url, not standard base64. */
  private static final String SECRET_32_BYTES_URL_SAFE = "-_v7-_v7-_v7-_v7-_v7-_v7-_v7-_v7-_v7-_v7-_s=";
  /** Chosen so it cannot be confused with any other string in a log line. */
  private static final String NOT_BASE64_MARKER = "not*base64*MARKER-7f3a9c";

  /** HMAC-SHA256(00..1f, "otp-email\0"), hex. */
  private static final String EMAIL_KEY_HEX = "2235ef4ae4fec4111a9882c729ba0b2596f11f0121110bb452e871af9f3d43ed";
  /** HMAC-SHA256(00..1f, "otp-code\0"), hex. */
  private static final String CODE_KEY_HEX = "a8fc47925af082856826904e4592e6a55330d672c1c6a661cdb40dcf94472ce2";
  /** HMAC-SHA256(00..1f, "otp-bucket\0"), hex. */
  private static final String BUCKET_KEY_HEX = "3f7f9585a45d7c3831692d1951610594fea37fd9cb37bc3ffa17b92ff13c02bb";

  private Logger keysLogger;
  private Level originalLevel;
  private ListAppender<ILoggingEvent> appender;

  @Before
  public void attachAppender() {
    this.keysLogger = (Logger) LoggerFactory.getLogger(OtpKeys.class);
    this.originalLevel = this.keysLogger.getLevel();
    this.keysLogger.setLevel(Level.TRACE);
    this.appender = new ListAppender<ILoggingEvent>();
    this.appender.start();
    this.keysLogger.addAppender(this.appender);
  }

  @After
  public void detachAppender() {
    this.keysLogger.detachAppender(this.appender);
    this.keysLogger.setLevel(this.originalLevel);
  }

  private static byte[] fromHex(String hex) {
    byte[] bytes = new byte[hex.length() / 2];
    for (int i = 0; i < bytes.length; i++) {
      bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
    }
    return bytes;
  }

  @Test
  public void emptySecretIsUnconfigured() {
    assertFalse(new OtpKeys("").isConfigured());
  }

  @Test
  public void nullSecretIsUnconfigured() {
    assertFalse(new OtpKeys((String) null).isConfigured());
  }

  @Test
  public void nonBase64SecretIsUnconfigured() {
    assertFalse(new OtpKeys(NOT_BASE64_MARKER).isConfigured());
  }

  @Test
  public void urlSafeAlphabetIsNotStandardBase64() {
    assertFalse(new OtpKeys(SECRET_32_BYTES_URL_SAFE).isConfigured());
  }

  @Test
  public void thirtyOneDecodedBytesIsUnconfigured() {
    assertFalse(new OtpKeys(SECRET_31_BYTES).isConfigured());
  }

  @Test
  public void thirtyTwoDecodedBytesIsConfigured() {
    assertTrue(new OtpKeys(SECRET_32_BYTES).isConfigured());
  }

  @Test
  public void fortyEightDecodedBytesIsConfigured() {
    assertTrue(new OtpKeys(SECRET_48_BYTES).isConfigured());
  }

  @Test
  public void derivedKeysMatchIndependentKnownAnswers() {
    OtpKeys keys = new OtpKeys(SECRET_32_BYTES);
    assertArrayEquals(fromHex(EMAIL_KEY_HEX), keys.emailKey());
    assertArrayEquals(fromHex(CODE_KEY_HEX), keys.codeKey());
    assertArrayEquals(fromHex(BUCKET_KEY_HEX), keys.bucketKey());
  }

  @Test
  public void derivedKeysArePairwiseDifferent() {
    OtpKeys keys = new OtpKeys(SECRET_48_BYTES);
    byte[] email = keys.emailKey();
    byte[] code = keys.codeKey();
    byte[] bucket = keys.bucketKey();
    assertEquals(32, email.length);
    assertEquals(32, code.length);
    assertEquals(32, bucket.length);
    assertFalse(Arrays.equals(email, code));
    assertFalse(Arrays.equals(email, bucket));
    assertFalse(Arrays.equals(code, bucket));
  }

  @Test
  public void mutatingAReturnedKeyDoesNotChangeTheNextOne() {
    OtpKeys keys = new OtpKeys(SECRET_32_BYTES);
    byte[] first = keys.codeKey();
    Arrays.fill(first, (byte) 0);
    assertArrayEquals(fromHex(CODE_KEY_HEX), keys.codeKey());
  }

  @Test
  public void unconfiguredKeysRefuseToDeriveAnything() {
    OtpKeys keys = new OtpKeys(SECRET_31_BYTES);
    try {
      keys.emailKey();
      fail("an unconfigured OtpKeys must not hand out a key");
    } catch (IllegalStateException expected) {
      assertNotNull(expected);
    }
  }

  @Test
  public void everyUnconfiguredCaseLogsOneWarnWithoutTheSecret() {
    String[] unconfigured = {"", NOT_BASE64_MARKER, SECRET_31_BYTES, SECRET_32_BYTES_URL_SAFE};
    for (String secret : unconfigured) {
      this.appender.list.clear();
      new OtpKeys(secret);
      List<ILoggingEvent> events = this.appender.list;
      assertEquals("exactly one line for secret of length " + secret.length(), 1, events.size());
      assertEquals(Level.WARN, events.get(0).getLevel());
      assertNoLeak(events, secret);
    }
  }

  @Test
  public void aConfiguredSecretNeverAppearsInTheLog() {
    this.appender.list.clear();
    OtpKeys keys = new OtpKeys(SECRET_32_BYTES);
    keys.emailKey();
    keys.codeKey();
    keys.bucketKey();
    assertNoLeak(this.appender.list, SECRET_32_BYTES);
    assertNoLeak(this.appender.list, EMAIL_KEY_HEX);
    assertNoLeak(this.appender.list, CODE_KEY_HEX);
    assertNoLeak(this.appender.list, BUCKET_KEY_HEX);
  }

  /**
   * Fails when any captured event carries {@code needle} in its message, its arguments or its throwable. A
   * non-empty needle is required so the check cannot pass by searching for nothing.
   */
  private static void assertNoLeak(List<ILoggingEvent> events, String needle) {
    if (needle.isEmpty()) {
      return;
    }
    for (ILoggingEvent event : events) {
      String all = event.getFormattedMessage() + " " + event.getMessage() + " "
        + Arrays.toString(event.getArgumentArray()) + " " + event.getThrowableProxy();
      assertFalse("log line leaks the secret: " + all, all.contains(needle));
    }
  }
}
