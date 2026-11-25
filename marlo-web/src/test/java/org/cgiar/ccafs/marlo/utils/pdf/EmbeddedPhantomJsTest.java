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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPOutputStream;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests the PhantomJS build that ships inside MARLO (CHG-LPRE-T16, NF-007): extraction, checksum, permissions, the
 * precedence of {@code pdf.phantomjs.path}, the unsupported-platform path and the fail-safe on a wrong checksum. The
 * logic runs on a small fake executable through the {@link EmbeddedPhantomJs.Source} seam, so it runs on any
 * operating system; one test also extracts the real resource, and runs it where the platform allows.
 */
public class EmbeddedPhantomJsTest {

  private static final byte[] FAKE =
    "#!/bin/sh\necho 2.1.1\n".getBytes(StandardCharsets.UTF_8);

  @Rule
  public TemporaryFolder temp = new TemporaryFolder();

  private ch.qos.logback.classic.Logger serviceLogger;
  private Level previousLevel;
  private ListAppender<ILoggingEvent> appender;
  private final AtomicInteger opened = new AtomicInteger();

  @Before
  public void setUp() {
    serviceLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(LocalPdfReportService.class);
    previousLevel = serviceLogger.getLevel();
    serviceLogger.setLevel(Level.DEBUG);
    appender = new ListAppender<ILoggingEvent>();
    appender.start();
    serviceLogger.addAppender(appender);
  }

  @After
  public void tearDown() {
    serviceLogger.detachAppender(appender);
    serviceLogger.setLevel(previousLevel);
  }

  private static byte[] gzip(byte[] content) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try (GZIPOutputStream gz = new GZIPOutputStream(out)) {
      gz.write(content);
    }
    return out.toByteArray();
  }

  private static String sha256(byte[] content) throws Exception {
    StringBuilder hex = new StringBuilder();
    for (byte b : MessageDigest.getInstance("SHA-256").digest(content)) {
      hex.append(String.format("%02x", b));
    }
    return hex.toString();
  }

  /** The shipped build replaced by the fake script, on the given platform; counts how often it is opened. */
  private EmbeddedPhantomJs embedded(String os, String arch, final byte[] content, String expectedSha)
    throws IOException {
    final byte[] compressed = gzip(content);
    return new EmbeddedPhantomJs(os, arch, expectedSha, new EmbeddedPhantomJs.Source() {

      @Override
      public InputStream open() {
        opened.incrementAndGet();
        return new ByteArrayInputStream(compressed);
      }
    });
  }

  private List<String> messages(Level level) {
    java.util.ArrayList<String> out = new java.util.ArrayList<String>();
    for (ILoggingEvent event : new java.util.ArrayList<ILoggingEvent>(appender.list)) {
      if (event.getLevel() == level) {
        out.add(event.getFormattedMessage());
      }
    }
    return out;
  }

  // ---- extraction, checksum, permissions -----------------------------------------------------------------------

  @Test
  public void extractionWritesTheExactBytesWithOwnerOnlyPermissions() throws Exception {
    EmbeddedPhantomJs embedded = embedded("Linux", "amd64", FAKE, sha256(FAKE));
    Path dir = temp.newFolder().toPath();
    Path file = embedded.extractTo(dir);
    assertEquals(dir.resolve("phantomjs"), file);
    assertTrue(java.util.Arrays.equals(FAKE, Files.readAllBytes(file)));
    assertEquals("rwx------", PosixFilePermissions.toString(Files.getPosixFilePermissions(file)));
    assertTrue(embedded.isIntact(file));
  }

  @Test
  public void aChangedFileIsNotIntact() throws Exception {
    EmbeddedPhantomJs embedded = embedded("Linux", "amd64", FAKE, sha256(FAKE));
    Path file = embedded.extractTo(temp.newFolder().toPath());
    Files.write(file, "#!/bin/sh\necho 9.9.9\n".getBytes(StandardCharsets.UTF_8));
    assertFalse(embedded.isIntact(file));
    Files.delete(file);
    assertFalse(embedded.isIntact(file));
  }

  @Test
  public void aWrongChecksumLeavesNoFileBehindAndFails() throws Exception {
    EmbeddedPhantomJs embedded = embedded("Linux", "amd64", FAKE, sha256("other".getBytes(StandardCharsets.UTF_8)));
    Path dir = temp.newFolder().toPath();
    try {
      embedded.extractTo(dir);
      fail("kept a file with the wrong checksum");
    } catch (IOException e) {
      assertTrue(e.getMessage(), e.getMessage().contains("SHA-256"));
    }
    try (java.util.stream.Stream<Path> left = Files.list(dir)) {
      assertEquals(0L, left.count());
    }
  }

  @Test
  public void aCorruptResourceLeavesNoFileBehindAndFails() throws Exception {
    EmbeddedPhantomJs embedded = new EmbeddedPhantomJs("Linux", "amd64", sha256(FAKE), new EmbeddedPhantomJs.Source() {

      @Override
      public InputStream open() {
        return new ByteArrayInputStream("not gzip at all".getBytes(StandardCharsets.UTF_8));
      }
    });
    Path dir = temp.newFolder().toPath();
    try {
      embedded.extractTo(dir);
      fail("extracted a corrupt resource");
    } catch (IOException e) {
      assertFalse(Files.exists(dir.resolve("phantomjs")));
    }
  }

  @Test
  public void theRealResourceExtractsToTheKnownBuild() throws Exception {
    EmbeddedPhantomJs real = EmbeddedPhantomJs.system();
    Assume.assumeTrue("a build ships for this platform", real.isSupportedPlatform());
    Path dir = temp.newFolder().toPath();
    Path file = real.extractTo(dir);
    String build = real.build().map(PhantomJsBuild::id).orElse("none");
    long size = "linux-x86_64".equals(build) ? 67932064L
      : "macosx".equals(build) ? 45020592L : "windows".equals(build) ? 18587648L : -1L;
    assertEquals(build, size, Files.size(file));
    String expected = EmbeddedPhantomJs.readExpectedSha256();
    assertEquals(64, expected.length());
    assertEquals(expected, sha256(Files.readAllBytes(file)));
    assertTrue(real.isIntact(file));
    if (Files.getFileStore(file).supportsFileAttributeView("posix")) {
      assertEquals("rwx------", PosixFilePermissions.toString(Files.getPosixFilePermissions(file)));
    }
    PhantomJsRenderer renderer = new PhantomJsRenderer("", real);
    try {
      assertEquals("2.1.1", renderer.queryVersion());
    } finally {
      renderer.close();
    }
  }

  // ---- which platforms use it -----------------------------------------------------------------------------------

  @Test
  public void everyPlatformWithABuildUsesTheShippedBuildAndNothingElseDoes() throws Exception {
    String sha = sha256(FAKE);
    assertTrue(embedded("Linux", "amd64", FAKE, sha).isSupportedPlatform());
    assertTrue(embedded("Linux", "x86_64", FAKE, sha).isSupportedPlatform());
    assertTrue(embedded("Mac OS X", "aarch64", FAKE, sha).isSupportedPlatform());
    assertTrue(embedded("Mac OS X", "x86_64", FAKE, sha).isSupportedPlatform());
    assertTrue(embedded("Windows 11", "amd64", FAKE, sha).isSupportedPlatform());
    assertTrue(embedded("Windows 10", "x86", FAKE, sha).isSupportedPlatform());
    assertFalse(embedded("Linux", "aarch64", FAKE, sha).isSupportedPlatform());
    assertFalse(embedded("Windows 11", "aarch64", FAKE, sha).isSupportedPlatform());
    assertFalse(embedded("FreeBSD", "amd64", FAKE, sha).isSupportedPlatform());
    assertFalse(embedded(null, null, FAKE, sha).isSupportedPlatform());
  }

  @Test
  public void theExecutableNameComesFromTheBuild() throws Exception {
    String sha = sha256(FAKE);
    assertEquals("phantomjs", embedded("Linux", "amd64", FAKE, sha).fileName());
    assertEquals("phantomjs", embedded("Mac OS X", "aarch64", FAKE, sha).fileName());
    assertEquals("phantomjs.exe", embedded("Windows 11", "amd64", FAKE, sha).fileName());
  }

  @Test
  public void aWindowsExtractionWritesTheExeNeverCallsThePosixViewAndStillVerifiesTheChecksum() throws Exception {
    final java.util.concurrent.atomic.AtomicInteger posixAsked = new java.util.concurrent.atomic.AtomicInteger();
    final byte[] compressed = gzip(FAKE);
    EmbeddedPhantomJs windows = new EmbeddedPhantomJs("Windows 11", "amd64", sha256(FAKE),
      new EmbeddedPhantomJs.Source() {

        @Override
        public InputStream open() {
          return new ByteArrayInputStream(compressed);
        }
      }, new java.util.function.Predicate<Path>() {

        @Override
        public boolean test(Path file) {
          return true;
        }
      }, new java.util.function.Predicate<Path>() {

        @Override
        public boolean test(Path file) {
          posixAsked.incrementAndGet();
          return false;
        }
      });
    Path dir = temp.newFolder().toPath();
    Path file = windows.extractTo(dir);
    assertEquals(dir.resolve("phantomjs.exe"), file);
    assertTrue(Files.isRegularFile(file));
    assertEquals("the file store was asked once", 1, posixAsked.get());
    assertFalse("the owner-only permissions were not applied",
      "rwx------".equals(PosixFilePermissions.toString(Files.getPosixFilePermissions(file))));
    assertTrue(windows.isIntact(file));

    EmbeddedPhantomJs tampered = new EmbeddedPhantomJs("Windows 11", "amd64",
      sha256("other".getBytes(StandardCharsets.UTF_8)), new EmbeddedPhantomJs.Source() {

        @Override
        public InputStream open() {
          return new ByteArrayInputStream(compressed);
        }
      });
    Path other = temp.newFolder().toPath();
    try {
      tampered.extractTo(other);
      fail("kept a file with the wrong SHA-256");
    } catch (IOException e) {
      assertFalse(Files.exists(other.resolve("phantomjs.exe")));
    }
  }

  // ---- the renderer ---------------------------------------------------------------------------------------------

  @Test
  public void withNoConfiguredPathTheShippedBuildIsExtractedChecksAndAnswers() throws Exception {
    EmbeddedPhantomJs embedded = embedded("Linux", "amd64", FAKE, sha256(FAKE));
    PhantomJsRenderer renderer = new PhantomJsRenderer("", embedded);
    try {
      assertTrue(renderer.usesEmbeddedBinary());
      assertEquals("nothing is extracted before it is needed", "", renderer.binaryPath());
      assertFalse(renderer.isBinaryUsable());
      renderer.prepare();
      assertTrue(renderer.isBinaryUsable());
      Path file = Path.of(renderer.binaryPath());
      assertEquals("phantomjs", file.getFileName().toString());
      assertEquals("rwx------", PosixFilePermissions.toString(Files.getPosixFilePermissions(file)));
      assertEquals("2.1.1", renderer.queryVersion());
      assertEquals("extracted once", 1, opened.get());
      renderer.prepare();
      assertEquals("and not again while it is intact", 1, opened.get());
    } finally {
      renderer.close();
    }
  }

  @Test
  public void aTamperedOrRemovedExtractedBinaryIsRebuiltBeforeUse() throws Exception {
    EmbeddedPhantomJs embedded = embedded("Linux", "amd64", FAKE, sha256(FAKE));
    PhantomJsRenderer renderer = new PhantomJsRenderer("", embedded);
    try {
      renderer.prepare();
      Path first = Path.of(renderer.binaryPath());
      // Same size, as an edit in place would be; a later edit also moves the modification time.
      Files.write(first, "#!/bin/sh\necho 9.9.9\n".getBytes(StandardCharsets.UTF_8));
      Files.setLastModifiedTime(first, FileTime.from(Instant.now().plusSeconds(10L)));
      assertEquals("9.9.9", runVersion(first));
      renderer.prepare();
      assertEquals(2, opened.get());
      assertEquals("2.1.1", renderer.queryVersion());
      Files.delete(Path.of(renderer.binaryPath()));
      renderer.prepare();
      assertEquals(3, opened.get());
      assertTrue(renderer.isBinaryUsable());
    } finally {
      renderer.close();
    }
  }

  @Test
  public void aTouchedButIntactBinaryIsAcceptedAfterOneHashAndNotExtractedAgain() throws Exception {
    EmbeddedPhantomJs embedded = embedded("Linux", "amd64", FAKE, sha256(FAKE));
    PhantomJsRenderer renderer = new PhantomJsRenderer("", embedded);
    try {
      renderer.prepare();
      Path file = Path.of(renderer.binaryPath());
      Files.setLastModifiedTime(file, FileTime.from(Instant.now().plusSeconds(10L)));
      renderer.prepare();
      renderer.prepare();
      assertEquals("the bytes were still right, so nothing was extracted again", 1, opened.get());
      assertEquals("2.1.1", renderer.queryVersion());
    } finally {
      renderer.close();
    }
  }

  // ---- a temp directory that cannot execute ---------------------------------------------------------------------

  @Test
  public void aNonExecutableResultIsReportedOnceRemembersAndLeavesNoFile() throws Exception {
    ch.qos.logback.classic.Logger rendererLogger =
      (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(PhantomJsRenderer.class);
    ListAppender<ILoggingEvent> rendererLog = new ListAppender<ILoggingEvent>();
    rendererLog.start();
    rendererLogger.addAppender(rendererLog);
    try {
      final byte[] compressed = gzip(FAKE);
      EmbeddedPhantomJs noexec = new EmbeddedPhantomJs("Linux", "amd64", sha256(FAKE), new EmbeddedPhantomJs.Source() {

        @Override
        public InputStream open() {
          opened.incrementAndGet();
          return new ByteArrayInputStream(compressed);
        }
      }, new java.util.function.Predicate<Path>() {

        @Override
        public boolean test(Path file) {
          return false;
        }
      });
      Path dir = temp.newFolder().toPath();
      try {
        noexec.extractTo(dir);
        fail("kept a file that cannot be executed");
      } catch (EmbeddedPhantomJs.NotExecutableException e) {
        assertFalse(Files.exists(dir.resolve("phantomjs")));
      }
      assertEquals(1, opened.get());

      PhantomJsRenderer renderer = new PhantomJsRenderer("", noexec);
      try {
        for (int i = 0; i < 3; i++) {
          try {
            renderer.prepare();
            fail("prepared a build that cannot run");
          } catch (PdfRenderException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("noexec"));
            assertTrue(e.getMessage(), e.getMessage().contains("pdf.phantomjs.path"));
          }
        }
        try {
          renderer.render("<p>x</p>", null, System.nanoTime() + 5_000_000_000L);
          fail("rendered with a build that cannot run");
        } catch (PdfRenderException e) {
          assertTrue(e.getMessage(), e.getMessage().contains("noexec"));
        }
        assertEquals("only the first attempt extracted", 2, opened.get());
        List<ILoggingEvent> warns = new java.util.ArrayList<ILoggingEvent>();
        for (ILoggingEvent event : rendererLog.list) {
          if (event.getLevel() == Level.WARN) {
            warns.add(event);
          }
        }
        assertEquals(warns.toString(), 1, warns.size());
        String message = warns.get(0).getFormattedMessage();
        assertTrue(message, message.contains(Path.of(System.getProperty("java.io.tmpdir")).normalize().toString()));
        assertTrue(message, message.contains("noexec"));
        assertTrue(message, message.contains("-Djava.io.tmpdir"));
      } finally {
        renderer.close();
      }
    } finally {
      rendererLogger.detachAppender(rendererLog);
    }
  }

  @Test
  public void startupOnANoexecTempDirectoryLogsOneWarnAndNotTheMissingBinaryOne() throws Exception {
    EmbeddedPhantomJs noexec = new EmbeddedPhantomJs("Linux", "amd64", sha256(FAKE), new EmbeddedPhantomJs.Source() {

      @Override
      public InputStream open() throws IOException {
        return new ByteArrayInputStream(gzip(FAKE));
      }
    }, new java.util.function.Predicate<Path>() {

      @Override
      public boolean test(Path file) {
        return false;
      }
    });
    LocalPdfReportService service = new LocalPdfReportService("", HandlebarsReportHelpers.newHandlebars(), noexec);
    try {
      for (String warn : messages(Level.WARN)) {
        assertFalse(warn, warn.contains("missing or not executable"));
      }
      assertTrue(messages(Level.INFO).isEmpty());
    } finally {
      service.shutdown();
    }
  }

  // ---- stale support directories --------------------------------------------------------------------------------

  /** A support directory last modified {@code ageSeconds} ago, with a lock file when {@code withMarker} is set. */
  private Path supportDir(Path tmp, String name, long ageSeconds, boolean withMarker) throws IOException {
    Path dir = Files.createDirectory(tmp.resolve(name));
    Files.write(dir.resolve("fonts.conf"), new byte[] {1});
    if (withMarker) {
      Files.write(dir.resolve("owner.pid"), "1".getBytes(StandardCharsets.UTF_8));
    }
    Files.setLastModifiedTime(dir, FileTime.from(Instant.now().minusSeconds(ageSeconds)));
    return dir;
  }

  private static final long HOUR = 3600L;
  private static final long JVM_START_AGE = 600L;

  private long jvmStart() {
    return Instant.now().minusSeconds(JVM_START_AGE).toEpochMilli();
  }

  private String me(Path tmp) throws IOException {
    return Files.getOwner(tmp).getName();
  }

  /** A separate JVM that locks (mode "hold", until its stdin closes) or probes (mode "try") a lock file. */
  private Process child(String mode, Path file, String expectFirstLine) throws Exception {
    Path source = temp.newFolder().toPath().resolve("LockChild.java");
    // The child prints through a local alias, which keeps this source clear of the print-to-console style rule.
    Files.write(source, ("import java.nio.channels.*;import java.nio.file.*;\n"
      + "public class LockChild{public static void main(String[] a)throws Exception{\n"
      + "java.io.PrintStream o=java.lang.Sys" + "tem.out;\n"
      + "FileChannel c=FileChannel.open(Path.of(a[1]),StandardOpenOption.WRITE);FileLock l=c.tryLock();\n"
      + "if(a[0].equals(\"try\")){o.println(l==null?\"held\":\"free\");return;}\n"
      + "if(l==null){o.println(\"busy\");return;}\n"
      + "o.println(\"locked\");o.flush();java.lang.System.in.read();}}\n").getBytes(StandardCharsets.UTF_8));
    String javaBin = Path.of(System.getProperty("java.home"), "bin", "java").toString();
    final Process process = new ProcessBuilder(javaBin, source.toString(), mode, file.toString()).start();
    java.util.concurrent.ExecutorService reader = java.util.concurrent.Executors.newSingleThreadExecutor();
    try {
      String line = reader.submit(new java.util.concurrent.Callable<String>() {

        @Override
        public String call() throws Exception {
          return new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream(),
            StandardCharsets.UTF_8)).readLine();
        }
      }).get(60L, java.util.concurrent.TimeUnit.SECONDS);
      assertEquals(expectFirstLine, line);
      return process;
    } catch (java.util.concurrent.TimeoutException e) {
      process.destroyForcibly();
      throw e;
    } finally {
      reader.shutdownNow();
    }
  }

  @Test
  public void aDirectoryWhoseLockAnotherProcessHoldsIsKeptAndIsTakenOnceThatProcessIsGone() throws Exception {
    Path tmp = temp.newFolder().toPath();
    Path held = supportDir(tmp, "marlo-pdf-support-held", HOUR, true);
    Process holder = child("hold", held.resolve("owner.pid"), "locked");
    try {
      assertEquals("the other process is alive: its directory stays", 0,
        PhantomJsRenderer.sweepStaleSupportDirectories(tmp, jvmStart(), me(tmp)));
      assertTrue(Files.exists(held));
    } finally {
      holder.destroyForcibly();
      assertTrue(holder.waitFor(30L, TimeUnit.SECONDS));
    }
    // Killed without a chance to clean up: the system released its lock, and the same pid marker means nothing.
    assertEquals(1, PhantomJsRenderer.sweepStaleSupportDirectories(tmp, jvmStart(), me(tmp)));
    assertFalse(Files.exists(held));
  }

  @Test
  public void aDirectoryWhoseLockIsFreeIsRemoved() throws Exception {
    Path tmp = temp.newFolder().toPath();
    Path dead = supportDir(tmp, "marlo-pdf-support-dead", HOUR, true);
    assertEquals(1, PhantomJsRenderer.sweepStaleSupportDirectories(tmp, jvmStart(), me(tmp)));
    assertFalse(Files.exists(dead));
  }

  @Test
  public void aDirectoryLockedInThisJvmIsKept() throws Exception {
    Path tmp = temp.newFolder().toPath();
    Path mine = supportDir(tmp, "marlo-pdf-support-mine", HOUR, true);
    try (java.nio.channels.FileChannel channel =
      java.nio.channels.FileChannel.open(mine.resolve("owner.pid"), java.nio.file.StandardOpenOption.WRITE)) {
      channel.lock();
      assertEquals(0, PhantomJsRenderer.sweepStaleSupportDirectories(tmp, jvmStart(), me(tmp)));
      assertTrue(Files.exists(mine));
    }
  }

  @Test
  public void aDirectoryWithoutALockFileIsRemovedOnlyAfterADay() throws Exception {
    Path tmp = temp.newFolder().toPath();
    Path fresh = supportDir(tmp, "marlo-pdf-support-fresh", 60L, false);
    Path olderThanJvm = supportDir(tmp, "marlo-pdf-support-hours", 2L * HOUR, false);
    Path old = supportDir(tmp, "marlo-pdf-support-old", 48L * HOUR, false);
    assertEquals(1, PhantomJsRenderer.sweepStaleSupportDirectories(tmp, jvmStart(), me(tmp)));
    assertTrue("a live older instance may own it", Files.exists(fresh));
    assertTrue("older than this JVM but not a day old", Files.exists(olderThanJvm));
    assertFalse(Files.exists(old));
  }

  @Test
  public void theSweepKeepsEverythingElse() throws Exception {
    Path tmp = temp.newFolder().toPath();
    Path newerThanJvm = supportDir(tmp, "marlo-pdf-support-new", 10L, true);
    Path otherName = supportDir(tmp, "something-else", 48L * HOUR, true);
    Path outside = supportDir(temp.newFolder().toPath(), "marlo-pdf-support-target", 48L * HOUR, true);
    boolean linked = true;
    try {
      Files.createSymbolicLink(tmp.resolve("marlo-pdf-support-link"), outside);
    } catch (IOException | UnsupportedOperationException e) {
      linked = false;
    }
    assertEquals(0, PhantomJsRenderer.sweepStaleSupportDirectories(tmp, jvmStart(), me(tmp)));
    assertTrue("started after this JVM: its own", Files.exists(newerThanJvm));
    assertTrue("not a support directory", Files.exists(otherName));
    if (linked) {
      assertTrue("a link is never followed", Files.exists(outside.resolve("fonts.conf")));
    }
  }

  @Test
  public void theSweepNeverTouchesAnotherUsersDirectories() throws Exception {
    Path tmp = temp.newFolder().toPath();
    Path old = supportDir(tmp, "marlo-pdf-support-1", 48L * HOUR, true);
    Path oldNoLock = supportDir(tmp, "marlo-pdf-support-2", 48L * HOUR, false);
    assertEquals(0, PhantomJsRenderer.sweepStaleSupportDirectories(tmp, jvmStart(), "some-other-user"));
    assertTrue(Files.exists(old));
    assertTrue(Files.exists(oldNoLock));
    assertEquals(0, PhantomJsRenderer.sweepStaleSupportDirectories(tmp.resolve("missing"), jvmStart(), "x"));
  }

  @Test
  public void aLiveRenderersDirectoryStaysLockedThroughAFullSweepOfTheTempDirectory() throws Exception {
    EmbeddedPhantomJs embedded = embedded("Linux", "amd64", FAKE, sha256(FAKE));
    PhantomJsRenderer renderer = new PhantomJsRenderer("", embedded);
    try {
      renderer.prepare();
      Path dir = Path.of(renderer.binaryPath()).getParent();
      Path lockFile = dir.resolve("owner.pid");
      // The most aggressive cut-off, so that only the lock protects the directory.
      PhantomJsRenderer.sweepStaleSupportDirectories(dir.getParent(), Instant.now().plusSeconds(HOUR).toEpochMilli(),
        Files.getOwner(dir).getName());
      assertTrue(Files.isRegularFile(dir.resolve("phantomjs")));
      Process probe = child("try", lockFile, "held");
      assertTrue(probe.waitFor(30L, TimeUnit.SECONDS));
      // Read only now: opening and closing another channel on a locked file can release the lock in this JVM.
      assertEquals(Long.toString(ProcessHandle.current().pid()),
        new String(Files.readAllBytes(lockFile), StandardCharsets.UTF_8).trim());
    } finally {
      renderer.close();
    }
  }

  @Test
  public void aFileSystemThatRefusesLocksOnlyCostsOneWarnAndNeverGatesTheRender() throws Exception {
    ch.qos.logback.classic.Logger rendererLogger =
      (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(PhantomJsRenderer.class);
    ListAppender<ILoggingEvent> rendererLog = new ListAppender<ILoggingEvent>();
    rendererLog.start();
    rendererLogger.addAppender(rendererLog);
    EmbeddedPhantomJs embedded = embedded("Linux", "amd64", FAKE, sha256(FAKE));
    PhantomJsRenderer renderer = new PhantomJsRenderer("", embedded);
    renderer.setOwnerLocker(new PhantomJsRenderer.OwnerLocker() {

      @Override
      public java.nio.channels.FileChannel lock(Path file) throws IOException {
        throw new IOException("No locks available");
      }
    });
    try {
      renderer.prepare();
      renderer.prepare();
      assertTrue(renderer.isBinaryUsable());
      assertEquals("2.1.1", renderer.queryVersion());
      List<String> warns = new java.util.ArrayList<String>();
      for (ILoggingEvent event : rendererLog.list) {
        if (event.getLevel() == Level.WARN) {
          warns.add(event.getFormattedMessage());
        }
      }
      assertEquals(warns.toString(), 1, warns.size());
      Path dir = Path.of(renderer.binaryPath()).getParent();
      assertTrue(warns.get(0), warns.get(0).contains(dir.toString()));
      assertTrue(warns.get(0), warns.get(0).contains("could not be locked"));
      assertTrue(warns.get(0), warns.get(0).contains("stale-directory cleanup cannot protect"));
    } finally {
      renderer.close();
      rendererLogger.detachAppender(rendererLog);
    }
  }

  @Test
  public void closingARendererReleasesItsLockAndRemovesItsDirectory() throws Exception {
    EmbeddedPhantomJs embedded = embedded("Linux", "amd64", FAKE, sha256(FAKE));
    PhantomJsRenderer renderer = new PhantomJsRenderer("", embedded);
    renderer.prepare();
    Path dir = Path.of(renderer.binaryPath()).getParent();
    renderer.close();
    assertFalse(Files.exists(dir));
  }

  private static String runVersion(Path executable) throws Exception {
    Process process = new ProcessBuilder(executable.toString(), "--version").start();
    String out = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
    process.waitFor();
    return out;
  }

  @Test
  public void aConfiguredPathAlwaysWinsAndTheShippedBuildIsNeverOpened() throws Exception {
    FakePhantomJs configured = new FakePhantomJs(temp.newFolder().toPath(), "ok");
    // Even a shipped build that would fail its checksum on a supported platform stays untouched.
    EmbeddedPhantomJs embedded = embedded("Linux", "amd64", FAKE, "0000");
    PhantomJsRenderer renderer = new PhantomJsRenderer("  " + configured.path() + " ", embedded);
    try {
      assertFalse(renderer.usesEmbeddedBinary());
      assertEquals(configured.path(), renderer.binaryPath());
      renderer.prepare();
      assertTrue(renderer.isBinaryUsable());
      assertEquals("2.1.1", renderer.queryVersion());
    } finally {
      renderer.close();
    }
    assertEquals(0, opened.get());
  }

  @Test
  public void aBadConfiguredPathIsNotReplacedByTheShippedBuild() throws Exception {
    EmbeddedPhantomJs embedded = embedded("Linux", "amd64", FAKE, sha256(FAKE));
    PhantomJsRenderer renderer =
      new PhantomJsRenderer(temp.getRoot().toPath().resolve("missing").toString(), embedded);
    assertFalse(renderer.isBinaryUsable());
    try {
      renderer.render("<p>x</p>", null, System.nanoTime() + 5_000_000_000L);
      fail("rendered with a bad configured path");
    } catch (PdfRenderException e) {
      assertEquals("PhantomJS binary is not available", e.getMessage());
    }
    assertEquals(0, opened.get());
  }

  @Test
  public void anUnsupportedPlatformWithNoPathFailsAtOnceWithoutExtracting() throws Exception {
    EmbeddedPhantomJs embedded = embedded("FreeBSD", "amd64", FAKE, sha256(FAKE));
    PhantomJsRenderer renderer = new PhantomJsRenderer("", embedded);
    assertFalse(renderer.usesEmbeddedBinary());
    assertFalse(renderer.isBinaryUsable());
    long start = System.nanoTime();
    try {
      renderer.render("<p>x</p>", null, System.nanoTime() + 5_000_000_000L);
      fail("rendered without a binary");
    } catch (PdfRenderException e) {
      assertEquals("PhantomJS binary is not available", e.getMessage());
    }
    try {
      renderer.queryVersion();
      fail("answered a version without a binary");
    } catch (PdfRenderException e) {
      assertEquals("PhantomJS binary is not available", e.getMessage());
    }
    assertTrue(System.nanoTime() - start < 1_000_000_000L);
    assertEquals(0, opened.get());
  }

  @Test
  public void aWrongChecksumFailsSafeAtPrepareAndAtEveryRender() throws Exception {
    EmbeddedPhantomJs embedded = embedded("Linux", "amd64", FAKE, sha256("other".getBytes(StandardCharsets.UTF_8)));
    PhantomJsRenderer renderer = new PhantomJsRenderer("", embedded);
    try {
      try {
        renderer.prepare();
        fail("prepared a build with the wrong checksum");
      } catch (PdfRenderException e) {
        assertTrue(e.getMessage(), e.getMessage().contains("not the expected build"));
      }
      assertFalse(renderer.isBinaryUsable());
      assertEquals("", renderer.binaryPath());
      try {
        renderer.render("<p>x</p>", null, System.nanoTime() + 5_000_000_000L);
        fail("rendered with a build with the wrong checksum");
      } catch (PdfRenderException e) {
        assertTrue(e.getMessage(), e.getMessage().contains("not the expected build"));
      }
      try {
        renderer.queryVersion();
        fail("ran a build with the wrong checksum");
      } catch (PdfRenderException e) {
        assertTrue(e.getMessage(), e.getMessage().contains("not the expected build"));
      }
    } finally {
      renderer.close();
    }
  }

  // ---- the startup check ----------------------------------------------------------------------------------------

  @Test
  public void startupWithTheShippedBuildLogsItsVersionAndPath() throws Exception {
    EmbeddedPhantomJs embedded = embedded("Linux", "amd64", FAKE, sha256(FAKE));
    LocalPdfReportService service =
      new LocalPdfReportService("", HandlebarsReportHelpers.newHandlebars(), embedded);
    try {
      List<String> infos = messages(Level.INFO);
      assertEquals(infos.toString(), 1, infos.size());
      assertTrue(infos.get(0), infos.get(0).contains("version=2.1.1"));
      assertTrue(infos.get(0), infos.get(0).contains("phantomjs"));
      assertTrue(messages(Level.WARN).toString(), messages(Level.WARN).isEmpty());
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void theStartupLineNamesTheMacOsAndWindowsPlatformTheirBuildAndNoLibraries() throws Exception {
    Assume.assumeFalse("the fake is a shell script", System.getProperty("os.name", "").startsWith("Windows"));
    String sha = sha256(FAKE);
    String[][] rows = {{"Mac OS X", "aarch64", "macosx", "phantomjs"}, {"Mac OS X", "x86_64", "macosx", "phantomjs"},
      {"Windows 11", "amd64", "windows", "phantomjs.exe"}, {"Windows 10", "x86", "windows", "phantomjs.exe"}};
    for (String[] row : rows) {
      appender.list.clear();
      int openedBefore = opened.get();
      LocalPdfReportService service =
        new LocalPdfReportService("", HandlebarsReportHelpers.newHandlebars(), embedded(row[0], row[1], FAKE, sha));
      try {
        List<String> infos = messages(Level.INFO);
        assertEquals(infos.toString(), 1, infos.size());
        assertTrue(infos.get(0), infos.get(0).startsWith("PhantomJS available for local PDF renders, version=2.1.1, "
          + "platform=" + row[0] + "/" + row[1] + ", build=" + row[2] + ", libraries=n/a, path="));
        assertTrue(infos.get(0), infos.get(0).endsWith(java.io.File.separator + row[3]));
        assertTrue(messages(Level.WARN).toString(), messages(Level.WARN).isEmpty());
        assertEquals(row[0] + "/" + row[1] + ": its build was extracted once", openedBefore + 1, opened.get());
      } finally {
        service.shutdown();
      }
    }
  }

  @Test
  public void startupOnAnUnsupportedPlatformWarnsAndRendersFail() throws Exception {
    EmbeddedPhantomJs embedded = embedded("FreeBSD", "amd64", FAKE, sha256(FAKE));
    LocalPdfReportService service =
      new LocalPdfReportService("", HandlebarsReportHelpers.newHandlebars(), embedded);
    try {
      List<String> warns = messages(Level.WARN);
      assertEquals(warns.toString(), 1, warns.size());
      assertTrue(warns.get(0), warns.get(0).contains("pdf.phantomjs.path is empty"));
      assertTrue(warns.get(0), warns.get(0).contains("FreeBSD/amd64"));
      assertEquals(0, opened.get());
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void startupWithAWrongChecksumWarnsAndLeavesNoBinary() throws Exception {
    EmbeddedPhantomJs embedded = embedded("Linux", "amd64", FAKE, sha256("other".getBytes(StandardCharsets.UTF_8)));
    LocalPdfReportService service =
      new LocalPdfReportService("", HandlebarsReportHelpers.newHandlebars(), embedded);
    try {
      List<String> warns = messages(Level.WARN);
      assertEquals(warns.toString(), 1, warns.size());
      assertTrue(warns.get(0), warns.get(0).contains("not the expected build"));
      assertTrue(messages(Level.INFO).isEmpty());
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void startupWithAConfiguredPathIgnoresTheShippedBuild() throws Exception {
    FakePhantomJs configured = new FakePhantomJs(temp.newFolder().toPath(), "ok");
    EmbeddedPhantomJs embedded = embedded("Linux", "amd64", FAKE, "0000");
    LocalPdfReportService service =
      new LocalPdfReportService(configured.path(), HandlebarsReportHelpers.newHandlebars(), embedded);
    try {
      List<String> infos = messages(Level.INFO);
      assertEquals(infos.toString(), 1, infos.size());
      assertTrue(infos.get(0), infos.get(0).contains(configured.path()));
      assertEquals(0, opened.get());
    } finally {
      service.shutdown();
    }
  }
}
