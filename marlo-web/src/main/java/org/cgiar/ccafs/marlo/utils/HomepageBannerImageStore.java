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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * Filesystem side of the homepage banner image: where it goes, what is allowed in, and how it comes back out.
 * Kept apart from the action so the rules that decide what reaches disk can be tested without an HTTP request.
 * The stored path is fully derived from the Global Unit acronym and the detected format; nothing from the uploaded
 * file name is ever used to build it.
 */
public class HomepageBannerImageStore {

  private static final Logger LOG = LoggerFactory.getLogger(HomepageBannerImageStore.class);

  /** Uploads larger than this are refused before the file is parsed. */
  public static final long MAX_IMAGE_BYTES = 2L * 1024L * 1024L;

  private static final String BANNERS_FOLDER = "homepageBanners";

  private static final String EXTENSION_PNG = "png";
  private static final String EXTENSION_JPG = "jpg";
  private static final String EXTENSION_SVG = "svg";

  /** Every extension the store may ever have written, so a replacement can clean up its predecessor. */
  private static final List<String> KNOWN_EXTENSIONS = Arrays.asList(EXTENSION_PNG, EXTENSION_JPG, EXTENSION_SVG);

  public enum StoreStatus {
    STORED, NO_FILE, TOO_LARGE, INVALID_FORMAT, UPLOADS_NOT_CONFIGURED, UPLOADS_NOT_WRITABLE, WRITE_FAILED
  }

  /**
   * What happened, and — when something was written — the file name to persist on the banner row.
   */
  public static class StoreOutcome {

    private final StoreStatus status;
    private final String fileName;

    private StoreOutcome(StoreStatus status, String fileName) {
      this.status = status;
      this.fileName = fileName;
    }

    public StoreStatus getStatus() {
      return status;
    }

    public String getFileName() {
      return fileName;
    }

    public boolean isStored() {
      return status == StoreStatus.STORED;
    }
  }

  private final String uploadsBaseFolder;

  public HomepageBannerImageStore(String uploadsBaseFolder) {
    this.uploadsBaseFolder = uploadsBaseFolder;
  }

  /**
   * Reduces the acronym to the characters that may appear in a file name. The acronym comes from the session rather
   * than from the request, so this is defence in depth: it makes a traversal sequence inexpressible instead of
   * merely unlikely.
   */
  private static String normalizeAcronym(String acronym) {
    String upper = StringUtils.upperCase(StringUtils.trimToEmpty(acronym), Locale.ROOT);
    String safe = upper.replaceAll("[^A-Z0-9_-]", "");
    if (StringUtils.isBlank(safe)) {
      throw new IllegalArgumentException("A Global Unit acronym is required to store a homepage banner image");
    }
    return safe;
  }

  /**
   * The extension the uploaded file may be stored under, decided by reading the file, or null when the content is
   * not a PNG, a JPEG or an SVG. Public so the validator rejects with the same rule the store enforces.
   */
  public static String extensionFor(File file) {
    if (file == null || !file.exists() || file.length() == 0) {
      return null;
    }
    String raster = rasterExtension(file);
    if (raster != null) {
      return raster;
    }
    return isSvg(file) ? EXTENSION_SVG : null;
  }

  public static boolean isTooLarge(File file) {
    return file != null && file.length() > MAX_IMAGE_BYTES;
  }

  private static String rasterExtension(File file) {
    try (ImageInputStream input = ImageIO.createImageInputStream(file)) {
      if (input == null) {
        return null;
      }
      Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
      if (!readers.hasNext()) {
        return null;
      }
      String format = StringUtils.lowerCase(readers.next().getFormatName(), Locale.ROOT);
      if ("png".equals(format)) {
        return EXTENSION_PNG;
      }
      if ("jpeg".equals(format) || "jpg".equals(format)) {
        return EXTENSION_JPG;
      }
      return null;
    } catch (IOException e) {
      LOG.warn("homepageBannerImage: could not read {} as a raster image", file.getAbsolutePath(), e);
      return null;
    }
  }

  /**
   * An SVG is accepted only when it parses as XML with an svg root element. External entities and doctypes are
   * disabled: the file is never rendered by this server, but parsing it here must not itself fetch anything.
   */
  private static boolean isSvg(File file) {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
      factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
      factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
      factory.setNamespaceAware(true);
      factory.setExpandEntityReferences(false);
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document document = builder.parse(file);
      if (document.getDocumentElement() == null) {
        return false;
      }
      String local = document.getDocumentElement().getLocalName();
      String name = local != null ? local : document.getDocumentElement().getNodeName();
      return "svg".equalsIgnoreCase(name);
    } catch (Exception e) {
      return false;
    }
  }

  public static String contentTypeFor(String fileName) {
    String extension = StringUtils.lowerCase(StringUtils.substringAfterLast(StringUtils.trimToEmpty(fileName), "."),
      Locale.ROOT);
    if (EXTENSION_PNG.equals(extension)) {
      return "image/png";
    }
    if (EXTENSION_JPG.equals(extension)) {
      return "image/jpeg";
    }
    if (EXTENSION_SVG.equals(extension)) {
      return "image/svg+xml";
    }
    return "application/octet-stream";
  }

  private File bannersFolder() {
    return new File(uploadsBaseFolder, BANNERS_FOLDER);
  }

  /**
   * Validates the upload and writes it under a derived name. Bytes are copied verbatim rather than re-encoded, so an
   * SVG keeps its vectors and a PNG keeps its exact pixels.
   *
   * @return the outcome; when the status is STORED, its file name is what the banner row must hold.
   */
  public StoreOutcome store(String acronym, File file) {
    if (file == null || !file.exists()) {
      return new StoreOutcome(StoreStatus.NO_FILE, null);
    }
    if (isTooLarge(file)) {
      LOG.warn("homepageBannerImage: refused a {} byte upload for {}, over the {} byte cap", file.length(), acronym,
        MAX_IMAGE_BYTES);
      return new StoreOutcome(StoreStatus.TOO_LARGE, null);
    }
    String extension = extensionFor(file);
    if (extension == null) {
      LOG.warn("homepageBannerImage: refused an upload for {} whose content is not a PNG, JPEG or SVG", acronym);
      return new StoreOutcome(StoreStatus.INVALID_FORMAT, null);
    }
    if (StringUtils.isBlank(uploadsBaseFolder)) {
      LOG.error("homepageBannerImage: uploads base folder is not configured");
      return new StoreOutcome(StoreStatus.UPLOADS_NOT_CONFIGURED, null);
    }

    String normalizedAcronym = normalizeAcronym(acronym);
    File targetDir = bannersFolder();
    if (!targetDir.exists() && !targetDir.mkdirs()) {
      LOG.error("homepageBannerImage: unable to create {}", targetDir.getAbsolutePath());
      return new StoreOutcome(StoreStatus.UPLOADS_NOT_WRITABLE, null);
    }
    if (!targetDir.isDirectory() || !targetDir.canWrite()) {
      LOG.error("homepageBannerImage: {} is not a writable directory", targetDir.getAbsolutePath());
      return new StoreOutcome(StoreStatus.UPLOADS_NOT_WRITABLE, null);
    }

    String fileName = normalizedAcronym + "." + extension;
    File target = new File(targetDir, fileName);
    try {
      Files.copy(file.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      LOG.error("homepageBannerImage: failed to write {}", target.getAbsolutePath(), e);
      return new StoreOutcome(StoreStatus.WRITE_FAILED, null);
    }
    if (!target.isFile() || target.length() == 0) {
      LOG.error("homepageBannerImage: wrote nothing to {}", target.getAbsolutePath());
      return new StoreOutcome(StoreStatus.WRITE_FAILED, null);
    }

    this.deleteSupersededFormats(normalizedAcronym, extension);
    return new StoreOutcome(StoreStatus.STORED, fileName);
  }

  /**
   * A banner keeps one image, so an upload in a new format has to take the previous one with it; otherwise the old
   * file lingers on disk forever, unreferenced and unserveable.
   */
  private void deleteSupersededFormats(String normalizedAcronym, String keptExtension) {
    for (String extension : KNOWN_EXTENSIONS) {
      if (extension.equals(keptExtension)) {
        continue;
      }
      File superseded = new File(this.bannersFolder(), normalizedAcronym + "." + extension);
      if (superseded.isFile() && !superseded.delete()) {
        LOG.warn("homepageBannerImage: could not delete the superseded {}", superseded.getAbsolutePath());
      }
    }
  }

  /**
   * Removes the stored image. Returns true when nothing is left on disk for this banner, so a caller can clear the
   * row only once the file is actually gone.
   */
  public boolean delete(String acronym, String fileName) {
    File stored = this.resolve(acronym, fileName);
    if (stored == null) {
      return true;
    }
    if (stored.delete()) {
      return true;
    }
    LOG.error("homepageBannerImage: could not delete {}", stored.getAbsolutePath());
    return false;
  }

  /**
   * The file a stored name points at, or null when it is not on disk. The name is rebuilt from the acronym and the
   * stored extension rather than trusted as a path, so a tampered row cannot reach outside the banners folder.
   */
  public File resolve(String acronym, String fileName) {
    if (StringUtils.isBlank(uploadsBaseFolder) || StringUtils.isBlank(fileName)) {
      return null;
    }
    String extension = StringUtils.lowerCase(StringUtils.substringAfterLast(fileName, "."), Locale.ROOT);
    if (!KNOWN_EXTENSIONS.contains(extension)) {
      return null;
    }
    File candidate = new File(this.bannersFolder(), normalizeAcronym(acronym) + "." + extension);
    return candidate.isFile() ? candidate : null;
  }
}
