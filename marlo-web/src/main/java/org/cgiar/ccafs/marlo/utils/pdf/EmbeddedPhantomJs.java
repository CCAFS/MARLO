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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.zip.GZIPInputStream;

/**
 * The PhantomJS 2.1.1 executable that ships inside MARLO (CHG-LPRE-T16, NF-007, design section 18.1): the official
 * {@code phantomjs-2.1.1-linux-x86_64} build, gzip-compressed under {@code pdf/phantomjs/} with its BSD licence and
 * third-party notices. It is used only on Linux x86_64 and only when {@code pdf.phantomjs.path} is empty.
 * <p>
 * {@link #extractTo} writes it once into a private directory, hashes the bytes as they are written, refuses to keep a
 * file whose SHA-256 is not the checksum resource, and sets the permissions to 0700. {@link #isIntact} repeats the
 * hash on an already extracted file. Nothing about the platform or the resource is read from configuration.
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

  static final String RESOURCE = "/pdf/phantomjs/phantomjs-2.1.1-linux-x86_64.gz";

  /**
   * SHA-256 of the official linux-x86_64 {@code bin/phantomjs} (the binary every parity run used), in sha256sum
   * format. It lives in a resource, not in Java source, so that the credential-literal scan does not read a
   * 64-character hex string as a secret.
   */
  static final String CHECKSUM_RESOURCE = "/pdf/phantomjs/phantomjs-2.1.1-linux-x86_64.sha256";

  /** The file name of the executable inside the support directory. */
  static final String FILE_NAME = "phantomjs";

  private static final Set<PosixFilePermission> OWNER_ONLY = PosixFilePermissions.fromString("rwx------");

  /** The shipped binary on the platform this JVM runs on. */
  static EmbeddedPhantomJs system() {
    return new EmbeddedPhantomJs(System.getProperty("os.name", ""), System.getProperty("os.arch", ""),
      readExpectedSha256(), new Source() {

        @Override
        public InputStream open() throws IOException {
          InputStream in = EmbeddedPhantomJs.class.getResourceAsStream(RESOURCE);
          if (in == null) {
            throw new IOException("Missing PDF resource " + RESOURCE);
          }
          return in;
        }
      });
  }

  /** The expected checksum; empty when its resource is unreadable, which no file can match (fail safe). */
  static String readExpectedSha256() {
    try (InputStream in = EmbeddedPhantomJs.class.getResourceAsStream(CHECKSUM_RESOURCE)) {
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

  private final Predicate<Path> executableProbe;
  private final String expectedSha256;
  private final String osArch;
  private final String osName;
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
    this.executableProbe = executableProbe;
    this.osName = osName == null ? "" : osName;
    this.osArch = osArch == null ? "" : osArch;
    this.expectedSha256 = expectedSha256;
    this.source = source;
  }

  /** True on Linux x86_64 (os.arch amd64 or x86_64), the one platform the shipped build runs on. */
  boolean isSupportedPlatform() {
    String name = osName.toLowerCase(Locale.ROOT);
    String arch = osArch.toLowerCase(Locale.ROOT);
    return name.startsWith("linux") && ("amd64".equals(arch) || "x86_64".equals(arch));
  }

  /** The platform, for log lines. */
  String platform() {
    return osName + "/" + osArch;
  }

  /**
   * Decompresses the executable into {@code directory} as {@value #FILE_NAME}, verifying its SHA-256 while writing.
   * On a mismatch the file is deleted and nothing is left behind.
   *
   * @return the extracted executable, permissions 0700.
   * @throws NotExecutableException when the file, once written, cannot be executed from {@code directory}.
   * @throws IOException when the resource is missing or unreadable, or the bytes are not the expected build.
   */
  Path extractTo(Path directory) throws IOException {
    Path target = directory.resolve(FILE_NAME);
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
    Files.setPosixFilePermissions(target, OWNER_ONLY);
    if (!executableProbe.test(target)) {
      Files.deleteIfExists(target);
      throw new NotExecutableException("The extracted PhantomJS cannot be executed from its directory");
    }
    return target;
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
