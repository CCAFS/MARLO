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

package org.cgiar.ccafs.marlo.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * CHG-COGNITO-AUTH-001-T14 audit finding (CRLF advisory): direct, exhaustive coverage of {@link
 * LogSanitizer#sanitizeForLog(String)} in isolation, separate from the integration-level proof in {@code
 * CognitoLogHygieneTest} that the four new T14 call sites actually invoke it.
 */
public class LogSanitizerTest {

  @Test
  public void nullReturnsEmptyString() {
    assertEquals("", LogSanitizer.sanitizeForLog(null));
  }

  @Test
  public void ordinaryValuePassesThroughUnchanged() {
    assertEquals("priya.cgiar@cgiar.org", LogSanitizer.sanitizeForLog("priya.cgiar@cgiar.org"));
  }

  @Test
  public void bareLineFeedIsStripped() {
    String sanitized = LogSanitizer.sanitizeForLog("a\nb");
    assertFalse(sanitized.contains("\n"));
    assertEquals("ab", sanitized);
  }

  @Test
  public void bareCarriageReturnIsStripped() {
    String sanitized = LogSanitizer.sanitizeForLog("a\rb");
    assertFalse(sanitized.contains("\r"));
    assertEquals("ab", sanitized);
  }

  @Test
  public void crlfPairIsFullyStripped() {
    String sanitized = LogSanitizer.sanitizeForLog("a\r\nb");
    assertFalse(sanitized.contains("\r") || sanitized.contains("\n"));
    assertEquals("ab", sanitized);
  }

  /**
   * The auditor's own concrete example: a single field must never let one log call render as more than
   * one line in the file.
   */
  @Test
  public void theAuditorsForgedLogLineExampleIsNeutralized() {
    String forged = "a\n2026-09-02 10:00:00 INFO LoginAction - User victim@cgiar.org logged in successfully "
      + "for Global Unit CCAFS.";
    String sanitized = LogSanitizer.sanitizeForLog(forged);
    assertFalse("a forged newline must never survive sanitization: [" + sanitized + "]",
      sanitized.contains("\n") || sanitized.contains("\r"));
  }

  @Test
  public void aValueUnderTheLimitIsNotTruncated() {
    String value = "a".repeat(200);
    assertEquals(value, LogSanitizer.sanitizeForLog(value));
  }

  @Test
  public void aValueOverTheLimitIsTruncatedWithASuffix() {
    String value = "a".repeat(500);
    String sanitized = LogSanitizer.sanitizeForLog(value);
    assertTrue("a value over the cap must be shortened: length was " + sanitized.length(),
      sanitized.length() < value.length());
    assertTrue("truncation must be visible, not silent", sanitized.endsWith("...(truncated)"));
  }
}
