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

import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Upload guards for HellDots screenshots. The generated name never derives from client input, so a
 * crafted filename cannot reach a filesystem path.
 */
public final class HelldotsUploadValidator {

  /**
   * Cap for a single screenshot. Deliberately not `file.maxSizeAllowed.bytes`: no Java in this repository
   * reads that property, and MARLO's general document cap is orders of magnitude larger than any capture the
   * widget produces (an automatic one is around 33 KB).
   */
  public static final long MAX_SCREENSHOT_BYTES = 5L * 1024L * 1024L;

  private static final String JPEG = "image/jpeg";
  private static final String PNG = "image/png";

  /** Exactly what generateFileName produces: the prefix, a canonical UUID, and one of the two extensions. */
  private static final Pattern GENERATED_NAME =
    Pattern.compile("^helldots-[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\.(jpg|png)$");

  public static String generateFileName(String contentType) {
    String extension = PNG.equals(normalise(contentType)) ? ".png" : ".jpg";
    return "helldots-" + UUID.randomUUID().toString() + extension;
  }

  public static boolean isAllowedContentType(String contentType) {
    String normalised = normalise(contentType);
    return JPEG.equals(normalised) || PNG.equals(normalised);
  }

  /**
   * Strips any `; charset=...`-style parameter and normalises case, so a real `Content-Type` header
   * (e.g. from `MultipartFile.getContentType()`) is compared on its media type alone, per RFC 9110
   * (media types are case-insensitive).
   */
  private static String normalise(String contentType) {
    if (contentType == null) {
      return null;
    }
    int separator = contentType.indexOf(';');
    String mediaType = separator >= 0 ? contentType.substring(0, separator) : contentType;
    return mediaType.trim().toLowerCase(Locale.ROOT);
  }

  /**
   * Guards the serving endpoint: only a name this class generated can be read back, so no traversal
   * sequence and no arbitrary path reaches the filesystem.
   */
  public static boolean isGeneratedFileName(String fileName) {
    if (fileName == null || fileName.isEmpty()) {
      return false;
    }
    return GENERATED_NAME.matcher(fileName).matches();
  }

  public static boolean isWithinSize(long bytes, long maxBytes) {
    return bytes > 0L && bytes <= maxBytes;
  }

  private HelldotsUploadValidator() {
  }
}
