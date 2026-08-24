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

package org.cgiar.ccafs.marlo.rest.helldots;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class HelldotsUploadValidatorTest {

  @Test
  public void acceptsJpegAndPng() {
    assertTrue(HelldotsUploadValidator.isAllowedContentType("image/jpeg"));
    assertTrue(HelldotsUploadValidator.isAllowedContentType("image/png"));
  }

  @Test
  public void rejectsEverythingElse() {
    assertFalse(HelldotsUploadValidator.isAllowedContentType("application/pdf"));
    assertFalse(HelldotsUploadValidator.isAllowedContentType("image/svg+xml"));
    assertFalse(HelldotsUploadValidator.isAllowedContentType("text/html"));
    assertFalse(HelldotsUploadValidator.isAllowedContentType(null));
  }

  @Test
  public void sizeBoundaryIsInclusive() {
    assertTrue(HelldotsUploadValidator.isWithinSize(100L, 100L));
    assertFalse(HelldotsUploadValidator.isWithinSize(101L, 100L));
    assertFalse(HelldotsUploadValidator.isWithinSize(0L, 100L));
  }

  @Test
  public void screenshotCapIsFiveMegabytes() {
    assertEquals(5L * 1024L * 1024L, HelldotsUploadValidator.MAX_SCREENSHOT_BYTES);
    assertTrue(HelldotsUploadValidator.isWithinSize(120000L, HelldotsUploadValidator.MAX_SCREENSHOT_BYTES));
    assertFalse(
      HelldotsUploadValidator.isWithinSize(20L * 1024L * 1024L, HelldotsUploadValidator.MAX_SCREENSHOT_BYTES));
  }

  @Test
  public void generatedNameCarriesTheRightExtensionAndIsUnique() {
    String first = HelldotsUploadValidator.generateFileName("image/jpeg");
    String second = HelldotsUploadValidator.generateFileName("image/jpeg");
    assertTrue(first.endsWith(".jpg"));
    assertTrue(HelldotsUploadValidator.generateFileName("image/png").endsWith(".png"));
    assertNotEquals(first, second);
  }

  @Test
  public void generatedNameContainsNoPathSeparator() {
    String name = HelldotsUploadValidator.generateFileName("image/png");
    assertEquals(-1, name.indexOf('/'));
    assertEquals(-1, name.indexOf('\\'));
    assertEquals(-1, name.indexOf(".."));
  }

  @Test
  public void onlyGeneratedNamesAreServable() {
    assertTrue(HelldotsUploadValidator.isGeneratedFileName(HelldotsUploadValidator.generateFileName("image/jpeg")));
    assertTrue(HelldotsUploadValidator.isGeneratedFileName(HelldotsUploadValidator.generateFileName("image/png")));
  }

  @Test
  public void traversalAndArbitraryNamesAreRejected() {
    assertFalse(HelldotsUploadValidator.isGeneratedFileName("../../marlo-dev.properties"));
    assertFalse(HelldotsUploadValidator.isGeneratedFileName("helldots-../x.jpg"));
    assertFalse(HelldotsUploadValidator.isGeneratedFileName("evil.jpg"));
    assertFalse(HelldotsUploadValidator.isGeneratedFileName("helldots-not-a-uuid.jpg"));
    assertFalse(HelldotsUploadValidator.isGeneratedFileName(null));
    assertFalse(HelldotsUploadValidator.isGeneratedFileName(""));
  }
}
