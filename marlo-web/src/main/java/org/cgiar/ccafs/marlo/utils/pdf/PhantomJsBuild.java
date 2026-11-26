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


package org.cgiar.ccafs.marlo.utils.pdf;

import java.util.Locale;
import java.util.Optional;

/**
 * The PhantomJS 2.1.1 build MARLO ships for one platform (CHG-PPE-FN-001, design DD-1): the one table that says which
 * operating system and architecture have a build, where its gzip-compressed executable and its SHA-256 live on the
 * classpath, what the file is called once extracted, and how the page fonts and the child-process environment are
 * arranged on that platform. Nothing else in the PDF engine decides a platform.
 * <p>
 * {@link #forPlatform} is the only entry point; it answers none for a platform without a build. The macOS build is
 * x86_64 and also serves Apple Silicon through Rosetta; the Windows build is 32-bit and also serves 64-bit Windows.
 */
final class PhantomJsBuild {

  /** Where the page gets its fonts. */
  enum FontMode {

    /** Linux: an isolated fontconfig that sees only the bundled DejaVu fonts. */
    LINUX_FONTCONFIG,

    /** macOS and Windows: the bundled fonts are declared in the page itself. */
    PAGE
  }

  /** How the child-process environment is built. */
  enum EnvironmentStyle {

    /** Linux and macOS. */
    POSIX,

    /** Windows. */
    WINDOWS
  }

  private static final String BASE = "/pdf/phantomjs/";

  /** The Linux x86_64 build of CHG-LPRE, byte for byte (same resource paths). */
  static final PhantomJsBuild LINUX_X86_64 =
    new PhantomJsBuild("linux-x86_64", BASE + "phantomjs-2.1.1-linux-x86_64.gz",
      BASE + "phantomjs-2.1.1-linux-x86_64.sha256", "phantomjs", FontMode.LINUX_FONTCONFIG, EnvironmentStyle.POSIX);

  static final PhantomJsBuild MACOS = new PhantomJsBuild("macosx", BASE + "phantomjs-2.1.1-macosx.gz",
    BASE + "phantomjs-2.1.1-macosx.sha256", "phantomjs", FontMode.PAGE, EnvironmentStyle.POSIX);

  static final PhantomJsBuild WINDOWS = new PhantomJsBuild("windows", BASE + "phantomjs-2.1.1-windows.exe.gz",
    BASE + "phantomjs-2.1.1-windows.exe.sha256", "phantomjs.exe", FontMode.PAGE, EnvironmentStyle.WINDOWS);

  /**
   * The build for an operating system and architecture, as {@code os.name} and {@code os.arch} report them: Linux
   * with amd64 or x86_64; macOS with x86_64 or aarch64; Windows with amd64, x86_64 or x86. Anything else, including
   * null or empty values, has none.
   */
  static Optional<PhantomJsBuild> forPlatform(String osName, String osArch) {
    String name = osName == null ? "" : osName.toLowerCase(Locale.ROOT);
    String arch = osArch == null ? "" : osArch.toLowerCase(Locale.ROOT);
    boolean x64 = "amd64".equals(arch) || "x86_64".equals(arch);
    if (name.startsWith("linux") && x64) {
      return Optional.of(LINUX_X86_64);
    }
    if (name.startsWith("mac") && (x64 || "aarch64".equals(arch))) {
      return Optional.of(MACOS);
    }
    if (name.startsWith("windows") && (x64 || "x86".equals(arch))) {
      return Optional.of(WINDOWS);
    }
    return Optional.empty();
  }

  private final String checksumResource;
  private final EnvironmentStyle environmentStyle;
  private final String fileName;
  private final FontMode fontMode;
  private final String id;
  private final String resource;

  private PhantomJsBuild(String id, String resource, String checksumResource, String fileName, FontMode fontMode,
    EnvironmentStyle environmentStyle) {
    this.id = id;
    this.resource = resource;
    this.checksumResource = checksumResource;
    this.fileName = fileName;
    this.fontMode = fontMode;
    this.environmentStyle = environmentStyle;
  }

  /** Classpath resource of the sha256sum-format checksum of the extracted executable. */
  String checksumResource() {
    return checksumResource;
  }

  EnvironmentStyle environmentStyle() {
    return environmentStyle;
  }

  /** The file name of the executable once extracted. */
  String fileName() {
    return fileName;
  }

  FontMode fontMode() {
    return fontMode;
  }

  /** Short name for log lines. */
  String id() {
    return id;
  }

  /** Classpath resource of the gzip-compressed executable. */
  String resource() {
    return resource;
  }
}
