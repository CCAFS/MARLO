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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

/**
 * The PhantomJS 2.1.1 executable that ships inside MARLO (CHG-LPRE-T16, NF-007, design section 18.1): the official
 * build for the platform, gzip-compressed under {@code pdf/phantomjs/} with its BSD licence and third-party notices.
 * {@link PhantomJsBuild} chooses it by operating system and architecture; it is used only when
 * {@code pdf.phantomjs.path} is empty and the platform has a build.
 * <p>
 * {@link #extractTo} writes it once into a private directory, hashes the bytes as they are written, refuses to keep a
 * file whose SHA-256 is not the checksum resource, and sets the permissions to 0700 where the file store has POSIX
 * permissions (Windows has none to set). {@link #isIntact} repeats the
 * hash on an already extracted file. Nothing about the platform or the resource is read from configuration.
 * <p>
 * On Linux the same class holds the fallback libraries (CHG-PPE-T04, DD-2): seven Debian 12 shared libraries, each
 * gzip-compressed under {@code pdf/phantomjs/linux-libs/} with its SHA-256 in {@code SHA256SUMS}.
 * {@link #extractLibrariesTo} checks every one against its checksum before any is written, and
 * {@link #verifyLibraries} checks a directory of them again before use. They are never loaded into the JVM: the
 * renderer names their directory in {@code LD_LIBRARY_PATH} for the PhantomJS process only.
 */
final class EmbeddedPhantomJs {

  /** The extracted file cannot be executed from where it was written (a noexec mount, typically). */
  static final class NotExecutableException extends IOException {

    private static final long serialVersionUID = 1L;

    NotExecutableException(String message) {
      super(message);
    }
  }

  /** What an extracted file looked like when it was verified; a cheap stand-in for hashing it again. */
  static final class Fingerprint {

    private final Object fileKey;
    private final FileTime modified;
    private final long size;

    Fingerprint(BasicFileAttributes attributes) {
      this.size = attributes.size();
      this.modified = attributes.lastModifiedTime();
      this.fileKey = attributes.fileKey();
    }

    boolean matches(BasicFileAttributes attributes) {
      return attributes.isRegularFile() && size == attributes.size()
        && modified.equals(attributes.lastModifiedTime()) && Objects.equals(fileKey, attributes.fileKey());
    }
  }

  /** Opens the compressed executable; a seam so that tests can supply a small one. */
  interface Source {

    InputStream open() throws IOException;
  }

  /** The shipped libraries; a seam so that tests can supply altered ones. */
  interface Libraries {

    /** The file name of each library and the SHA-256 of its uncompressed bytes (lower-case hex), in a fixed order. */
    Map<String, String> expected() throws IOException;

    /** Opens the gzip-compressed bytes of one library listed by {@link #expected}. */
    InputStream open(String name) throws IOException;
  }

  /** The directory, inside the support directory, that holds the extracted libraries. */
  static final String LIBRARIES_DIR = "libs";

  private static final String LIBRARIES_BASE = "/pdf/phantomjs/linux-libs/";

  /** Far more than any shipped library (the largest is 0.8 MB); a stream that goes on longer is not one of them. */
  private static final int MAX_LIBRARY_BYTES = 32 * 1024 * 1024;

  private static final Set<PosixFilePermission> OWNER_READ_WRITE = PosixFilePermissions.fromString("rw-------");

  /** The libraries inside MARLO: the names and checksums of {@code SHA256SUMS}, the bytes of {@code <name>.gz}. */
  static Libraries shippedLibraries() {
    return new Libraries() {

      @Override
      public Map<String, String> expected() throws IOException {
        Map<String, String> sums = new LinkedHashMap<String, String>();
        try (InputStream in = EmbeddedPhantomJs.class.getResourceAsStream(LIBRARIES_BASE + "SHA256SUMS")) {
          if (in == null) {
            throw new IOException("Missing PDF resource " + LIBRARIES_BASE + "SHA256SUMS");
          }
          for (String line : new String(in.readAllBytes(), StandardCharsets.UTF_8).split("\\R")) {
            String text = line.trim();
            int gap = text.indexOf(' ');
            if (text.isEmpty() || gap < 0) {
              continue;
            }
            String name = text.substring(gap).trim();
            if (!name.matches("[A-Za-z0-9][A-Za-z0-9._+-]*")) {
              throw new IOException("Unusable library name in " + LIBRARIES_BASE + "SHA256SUMS");
            }
            sums.put(name, text.substring(0, gap).toLowerCase(Locale.ROOT));
          }
        }
        if (sums.isEmpty()) {
          throw new IOException("No libraries are recorded in " + LIBRARIES_BASE + "SHA256SUMS");
        }
        return sums;
      }

      @Override
      public InputStream open(String name) throws IOException {
        InputStream in = EmbeddedPhantomJs.class.getResourceAsStream(LIBRARIES_BASE + name + ".gz");
        if (in == null) {
          throw new IOException("Missing PDF resource " + LIBRARIES_BASE + name + ".gz");
        }
        return in;
      }
    };
  }

  private static final Set<PosixFilePermission> OWNER_ONLY = PosixFilePermissions.fromString("rwx------");

  /** The shipped binary on the platform this JVM runs on. */
  static EmbeddedPhantomJs system() {
    final Optional<PhantomJsBuild> build =
      PhantomJsBuild.forPlatform(System.getProperty("os.name", ""), System.getProperty("os.arch", ""));
    return new EmbeddedPhantomJs(System.getProperty("os.name", ""), System.getProperty("os.arch", ""),
      build.isPresent() ? readExpectedSha256(build.get()) : "", new Source() {

        @Override
        public InputStream open() throws IOException {
          String resource = build.isPresent() ? build.get().resource() : "(none for this platform)";
          InputStream in = build.isPresent() ? EmbeddedPhantomJs.class.getResourceAsStream(resource) : null;
          if (in == null) {
            throw new IOException("Missing PDF resource " + resource);
          }
          return in;
        }
      });
  }

  /** The expected checksum of this JVM's build; empty when none ships or its resource is unreadable. */
  static String readExpectedSha256() {
    Optional<PhantomJsBuild> build =
      PhantomJsBuild.forPlatform(System.getProperty("os.name", ""), System.getProperty("os.arch", ""));
    return build.isPresent() ? readExpectedSha256(build.get()) : "";
  }

  /** The expected checksum; empty when its resource is unreadable, which no file can match (fail safe). */
  static String readExpectedSha256(PhantomJsBuild build) {
    try (InputStream in = EmbeddedPhantomJs.class.getResourceAsStream(build.checksumResource())) {
      if (in == null) {
        return "";
      }
      String line = new String(in.readAllBytes(), StandardCharsets.UTF_8).trim();
      int end = line.indexOf(' ');
      return (end < 0 ? line : line.substring(0, end)).toLowerCase(Locale.ROOT);
    } catch (IOException e) {
      return "";
    }
  }

  /** The build for this platform; null when none ships. */
  private final PhantomJsBuild build;
  private final Predicate<Path> executableProbe;
  private final String expectedSha256;
  private final String osArch;
  private final Libraries libraries;
  private final String osName;
  private final Predicate<Path> posixStore;
  private final Source source;

  EmbeddedPhantomJs(String osName, String osArch, String expectedSha256, Source source) {
    this(osName, osArch, expectedSha256, source, new Predicate<Path>() {

      @Override
      public boolean test(Path file) {
        return Files.isExecutable(file);
      }
    });
  }

  /** Test seam: {@code executableProbe} answers whether a file can be executed (Files.isExecutable in production). */
  EmbeddedPhantomJs(String osName, String osArch, String expectedSha256, Source source,
    Predicate<Path> executableProbe) {
    this(osName, osArch, expectedSha256, source, executableProbe, new Predicate<Path>() {

      @Override
      public boolean test(Path file) {
        try {
          return Files.getFileStore(file).supportsFileAttributeView("posix");
        } catch (IOException e) {
          return false;
        }
      }
    });
  }

  /**
   * Test seam: {@code posixStore} answers whether the file store of an extracted file has the POSIX permission view
   * (its file store supports "posix" in production); the owner-only permissions are set only when it does.
   */
  EmbeddedPhantomJs(String osName, String osArch, String expectedSha256, Source source,
    Predicate<Path> executableProbe, Predicate<Path> posixStore) {
    this(osName, osArch, expectedSha256, source, executableProbe, posixStore, shippedLibraries());
  }

  /** Test seam: as above, with the shipped libraries supplied. */
  EmbeddedPhantomJs(String osName, String osArch, String expectedSha256, Source source,
    Predicate<Path> executableProbe, Predicate<Path> posixStore, Libraries libraries) {
    this.libraries = libraries;
    this.posixStore = posixStore;
    this.executableProbe = executableProbe;
    this.osName = osName == null ? "" : osName;
    this.osArch = osArch == null ? "" : osArch;
    this.expectedSha256 = expectedSha256;
    this.source = source;
    this.build = PhantomJsBuild.forPlatform(this.osName, this.osArch).orElse(null);
  }

  /** The build shipped for this platform, or none. */
  Optional<PhantomJsBuild> build() {
    return Optional.ofNullable(build);
  }

  /** True when the build for this platform has fallback libraries: the Linux one. */
  boolean shipsLibraries() {
    return build != null && build.fontMode() == PhantomJsBuild.FontMode.LINUX_FONTCONFIG;
  }

  /** The file name of the executable once extracted; {@code phantomjs} when no build ships for the platform. */
  String fileName() {
    return build == null ? "phantomjs" : build.fileName();
  }

  /** True when a build ships for this platform (see {@link PhantomJsBuild#forPlatform}). */
  boolean isSupportedPlatform() {
    return build != null;
  }

  /** The platform, for log lines. */
  String platform() {
    return osName + "/" + osArch;
  }

  /**
   * Decompresses the executable into {@code directory} under the build's file name, verifying its SHA-256 while
   * writing.
   * On a mismatch the file is deleted and nothing is left behind.
   *
   * @return the extracted executable, permissions 0700 where the file store has POSIX permissions.
   * @throws NotExecutableException when the file, once written, cannot be executed from {@code directory}.
   * @throws IOException when the resource is missing or unreadable, or the bytes are not the expected build.
   */
  Path extractTo(Path directory) throws IOException {
    Path target = directory.resolve(fileName());
    MessageDigest digest = newDigest();
    boolean kept = false;
    try (InputStream in = new GZIPInputStream(source.open(), 64 * 1024);
      OutputStream out = Files.newOutputStream(target, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
      byte[] buffer = new byte[64 * 1024];
      int read = in.read(buffer);
      while (read >= 0) {
        digest.update(buffer, 0, read);
        out.write(buffer, 0, read);
        read = in.read(buffer);
      }
    } finally {
      kept = Files.exists(target) && hex(digest.digest()).equals(expectedSha256);
      if (!kept) {
        Files.deleteIfExists(target);
      }
    }
    if (!kept) {
      throw new IOException("Embedded PhantomJS does not match the expected SHA-256");
    }
    if (posixStore.test(target)) {
      Files.setPosixFilePermissions(target, OWNER_ONLY);
    }
    if (!executableProbe.test(target)) {
      Files.deleteIfExists(target);
      throw new NotExecutableException("The extracted PhantomJS cannot be executed from its directory");
    }
    return target;
  }

  /**
   * Writes the shipped libraries into {@code directory}, each under its own file name with permissions 0600 where the
   * file store has POSIX permissions. Every library is decompressed and checked against its recorded SHA-256 in
   * memory first: one that does not match, or a resource that is missing, fails the whole call before any file is
   * written, so nothing unverified ever reaches the disk (CHG-PPE-SEC-001).
   *
   * @return the state of each written file, to hand to {@link #verifyLibraries} later.
   * @throws IOException when a resource is missing or unreadable, is not the recorded library, or cannot be written;
   *         files already written by this call are removed.
   */
  Map<String, Fingerprint> extractLibrariesTo(Path directory) throws IOException {
    Map<String, String> expected = libraries.expected();
    Map<String, byte[]> verified = new LinkedHashMap<String, byte[]>();
    for (Map.Entry<String, String> library : expected.entrySet()) {
      verified.put(library.getKey(), readVerified(library.getKey(), library.getValue()));
    }
    Files.createDirectories(directory);
    Map<String, Fingerprint> written = new LinkedHashMap<String, Fingerprint>();
    try {
      for (Map.Entry<String, byte[]> library : verified.entrySet()) {
        Path target = directory.resolve(library.getKey());
        Files.write(target, library.getValue(), StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
        written.put(library.getKey(), new Fingerprint(
          Files.readAttributes(target, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS)));
        if (posixStore.test(target)) {
          Files.setPosixFilePermissions(target, OWNER_READ_WRITE);
        }
      }
    } catch (IOException | RuntimeException e) {
      for (String name : verified.keySet()) {
        Files.deleteIfExists(directory.resolve(name));
      }
      throw e;
    }
    return written;
  }

  /**
   * True when {@code directory} holds exactly the shipped libraries and each has the recorded SHA-256. A regular
   * file whose size, modification time and file key are as {@code recorded} is accepted without reading it; any other
   * is hashed, and one that still has the right bytes is accepted and recorded again. A missing file, a link, a file
   * with other bytes, or any file that is not one of the libraries (it could shadow a host library) makes it false.
   */
  boolean verifyLibraries(Path directory, Map<String, Fingerprint> recorded) {
    try {
      Map<String, String> expected = libraries.expected();
      try (Stream<Path> children = Files.list(directory)) {
        Set<String> present = new HashSet<String>();
        children.forEach(child -> present.add(child.getFileName().toString()));
        if (!present.equals(expected.keySet())) {
          return false;
        }
      }
      for (Map.Entry<String, String> library : expected.entrySet()) {
        Path file = directory.resolve(library.getKey());
        BasicFileAttributes attributes = Files.readAttributes(file, BasicFileAttributes.class,
          LinkOption.NOFOLLOW_LINKS);
        Fingerprint known = recorded == null ? null : recorded.get(library.getKey());
        if (!attributes.isRegularFile()) {
          return false;
        }
        if (known != null && known.matches(attributes)) {
          continue;
        }
        if (!library.getValue().equals(sha256Of(file))) {
          return false;
        }
        if (recorded != null) {
          recorded.put(library.getKey(), new Fingerprint(attributes));
        }
      }
      return true;
    } catch (IOException | RuntimeException e) {
      return false;
    }
  }

  private byte[] readVerified(String name, String expectedHash) throws IOException {
    MessageDigest digest = newDigest();
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    try (InputStream in = new GZIPInputStream(libraries.open(name), 64 * 1024)) {
      byte[] buffer = new byte[64 * 1024];
      int read = in.read(buffer);
      while (read >= 0) {
        if (bytes.size() + read > MAX_LIBRARY_BYTES) {
          throw new IOException("Shipped library " + name + " is larger than expected");
        }
        digest.update(buffer, 0, read);
        bytes.write(buffer, 0, read);
        read = in.read(buffer);
      }
    }
    if (!hex(digest.digest()).equals(expectedHash)) {
      throw new IOException("Shipped library " + name + " does not match its recorded SHA-256");
    }
    return bytes.toByteArray();
  }

  private static String sha256Of(Path file) throws IOException {
    MessageDigest digest = newDigest();
    try (InputStream in = Files.newInputStream(file, LinkOption.NOFOLLOW_LINKS)) {
      byte[] buffer = new byte[64 * 1024];
      int read = in.read(buffer);
      while (read >= 0) {
        digest.update(buffer, 0, read);
        read = in.read(buffer);
      }
    }
    return hex(digest.digest());
  }

  /** The state of {@code file} to compare with {@link #isUnchanged} later. */
  Fingerprint fingerprint(Path file) throws IOException {
    return new Fingerprint(Files.readAttributes(file, BasicFileAttributes.class));
  }

  /** The cheap re-check: a regular, executable file whose size, modification time and file key are as recorded. */
  boolean isUnchanged(Path file, Fingerprint recorded) {
    if (recorded == null || !Files.isRegularFile(file) || !executableProbe.test(file)) {
      return false;
    }
    try {
      return recorded.matches(Files.readAttributes(file, BasicFileAttributes.class));
    } catch (IOException e) {
      return false;
    }
  }

  /** True when {@code file} is a regular file owned-executable whose SHA-256 is the expected one. */
  boolean isIntact(Path file) {
    if (!Files.isRegularFile(file) || !executableProbe.test(file)) {
      return false;
    }
    MessageDigest digest = newDigest();
    try (InputStream in = Files.newInputStream(file)) {
      byte[] buffer = new byte[64 * 1024];
      int read = in.read(buffer);
      while (read >= 0) {
        digest.update(buffer, 0, read);
        read = in.read(buffer);
      }
    } catch (IOException e) {
      return false;
    }
    return hex(digest.digest()).equals(expectedSha256);
  }

  private static MessageDigest newDigest() {
    try {
      return MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA-256 is not available", e);
    }
  }

  private static String hex(byte[] bytes) {
    StringBuilder text = new StringBuilder(bytes.length * 2);
    for (byte b : bytes) {
      text.append(Character.forDigit((b >> 4) & 0xf, 16)).append(Character.forDigit(b & 0xf, 16));
    }
    return text.toString();
  }
}
