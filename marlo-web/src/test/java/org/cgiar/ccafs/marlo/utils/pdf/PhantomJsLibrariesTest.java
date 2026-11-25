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
import java.nio.file.attribute.PosixFilePermissions;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests the Linux library fallback (CHG-PPE-T04, FN-002, FN-003, FN-007, SEC-001, OPS-002, design DD-2): the renderer
 * tries the shipped build with the host's libraries first and extracts the shipped ones only when the dynamic loader
 * says one is missing. A fake PhantomJS ({@code fake-phantomjs.sh}, modes {@code libs-required} and
 * {@code loader-fail}) stands in for a host without the font libraries, so the logic runs on any operating system; the
 * libraries themselves are the real resources, and the expected checksums below are literals taken from the Debian 12
 * packages (see {@code linux-libs/PROVENANCE.txt}), not read back from the resource that is being tested.
 */
public class PhantomJsLibrariesTest {

  /** SHA-256 of each shipped library after decompression: the Debian 12 files recorded in PROVENANCE.txt. */
  private static final Map<String, String> SHIPPED = new LinkedHashMap<String, String>();

  static {
    SHIPPED.put("libbrotlicommon.so.1", "7a22eb878ad6033f1ee0908faeb5d665aa0809a1da2eb6f49bf37081cfff888a");
    SHIPPED.put("libbrotlidec.so.1", "0ed56000cc214627a4f00af7b7a20c86ffdc9de4d81d56ce12cba637ab9b3ff0");
    SHIPPED.put("libexpat.so.1", "b342d94f6a71df596f19b1b0d4d3af9b883f0333027de6c3fc0dbc9ff8c45057");
    SHIPPED.put("libfontconfig.so.1", "eba9fe8895df08cb98d6a294825132a52b65b53f4f1bce26bb3f6ee6ac6b16ec");
    SHIPPED.put("libfreetype.so.6", "5c691e2297772f84647dc07392a1a5d682ff4a190b057df34cd26bd2c4a27179");
    SHIPPED.put("libpng16.so.16", "8a6b5ae14e223d7c01bf09988ea9631c38fd5c898df6d330c343654b76414897");
    SHIPPED.put("libz.so.1", "7e2a72b4c4b38c61e6962de6e3f4a5e9ae692e732c68deead10a7ce2135a7f68");
  }

  private static final String HOST_MESSAGE = "PhantomJS does not start with the host libraries; using the libraries "
    + "shipped with MARLO";

  @Rule
  public TemporaryFolder temp = new TemporaryFolder();

  private ch.qos.logback.classic.Logger rendererLogger;
  private Level previousLevel;
  private ListAppender<ILoggingEvent> log;

  @Before
  public void setUp() {
    rendererLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(PhantomJsRenderer.class);
    previousLevel = rendererLogger.getLevel();
    rendererLogger.setLevel(Level.DEBUG);
    log = new ListAppender<ILoggingEvent>();
    log.start();
    rendererLogger.addAppender(log);
  }

  @After
  public void tearDown() {
    rendererLogger.detachAppender(log);
    rendererLogger.setLevel(previousLevel);
  }

  private List<String> messages(Level level) {
    List<String> out = new ArrayList<String>();
    for (ILoggingEvent event : new ArrayList<ILoggingEvent>(log.list)) {
      if (event.getLevel() == level) {
        out.add(event.getFormattedMessage());
      }
    }
    return out;
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

  private static String sha256(Path file) throws Exception {
    return sha256(Files.readAllBytes(file));
  }

  /** The shipped build replaced by the fake script, on the given platform, with the given libraries. */
  private static EmbeddedPhantomJs embeddedFor(FakePhantomJs fake, String os, String arch,
    EmbeddedPhantomJs.Libraries libraries) throws Exception {
    final byte[] script = Files.readAllBytes(Path.of(fake.path()));
    final byte[] compressed = gzip(script);
    return new EmbeddedPhantomJs(os, arch, sha256(script), new EmbeddedPhantomJs.Source() {

      @Override
      public InputStream open() {
        return new ByteArrayInputStream(compressed);
      }
    }, new java.util.function.Predicate<Path>() {

      @Override
      public boolean test(Path file) {
        return Files.isExecutable(file);
      }
    }, new java.util.function.Predicate<Path>() {

      @Override
      public boolean test(Path file) {
        return true;
      }
    }, libraries);
  }

  private PhantomJsRenderer linuxRenderer(FakePhantomJs fake) throws Exception {
    return new PhantomJsRenderer("", embeddedFor(fake, "Linux", "amd64", EmbeddedPhantomJs.shippedLibraries()));
  }

  private FakePhantomJs fake(String mode) throws IOException {
    return new FakePhantomJs(temp.newFolder().toPath(), mode);
  }

  private static Path libsOf(PhantomJsRenderer renderer) {
    return Path.of(renderer.binaryPath()).getParent().resolve("libs");
  }

  private static List<String> versionLibraryPaths(FakePhantomJs fake) throws IOException {
    return Files.readAllLines(Path.of(fake.path()).getParent().resolve("record").resolve("libpath.version"),
      StandardCharsets.UTF_8);
  }

  private static long deadlineIn(long millis) {
    return System.nanoTime() + java.util.concurrent.TimeUnit.MILLISECONDS.toNanos(millis);
  }

  // ---- FN-002: a host without the libraries ---------------------------------------------------------------------

  @Test
  public void withoutHostLibrariesTheShippedOnesAreExtractedVerifiedAndGivenToPhantomJsOnly() throws Exception {
    String javaLibraryPath = System.getProperty("java.library.path");
    String hostLdPath = System.getenv("LD_LIBRARY_PATH");
    FakePhantomJs fake = fake("libs-required");
    PhantomJsRenderer renderer = linuxRenderer(fake);
    try {
      renderer.prepare();
      assertEquals("bundled", renderer.libraryMode());
      Path libs = libsOf(renderer);
      for (Map.Entry<String, String> library : SHIPPED.entrySet()) {
        assertEquals(library.getKey(), library.getValue(), sha256(libs.resolve(library.getKey())));
      }
      assertEquals("nothing but the libraries is in the directory", new TreeSet<String>(SHIPPED.keySet()),
        listing(libs));
      assertEquals("2.1.1", renderer.queryVersion());

      renderer.render("<p>x</p>", null, deadlineIn(20000L));
      String pid = fake.started().get(0);
      String env = fake.recorded("env", pid);
      assertTrue(env, Arrays.asList(env.split("\n")).contains("LD_LIBRARY_PATH=" + libs));
      assertEquals("the probe ran first without the variable, the retry with it, and so did the later query",
        Arrays.asList("unset", libs.toString(), libs.toString()), versionLibraryPaths(fake));
      assertEquals("INFO", levelOf(HOST_MESSAGE));
      assertEquals(messages(Level.WARN).toString(), 0, messages(Level.WARN).size());
    } finally {
      renderer.close();
    }
    assertEquals("FN-003: the JVM's own library path is untouched", javaLibraryPath,
      System.getProperty("java.library.path"));
    assertEquals("FN-003: so is its environment", hostLdPath, System.getenv("LD_LIBRARY_PATH"));
  }

  private String levelOf(String message) {
    for (ILoggingEvent event : new ArrayList<ILoggingEvent>(log.list)) {
      if (message.equals(event.getFormattedMessage())) {
        return event.getLevel().toString();
      }
    }
    return "never logged";
  }

  private static TreeSet<String> listing(Path dir) throws IOException {
    TreeSet<String> names = new TreeSet<String>();
    try (java.util.stream.Stream<Path> children = Files.list(dir)) {
      children.forEach(child -> names.add(child.getFileName().toString()));
    }
    return names;
  }

  @Test
  public void withHostLibrariesNothingIsExtractedAndNoLibraryPathIsGiven() throws Exception {
    FakePhantomJs fake = fake("ok");
    PhantomJsRenderer renderer = linuxRenderer(fake);
    try {
      renderer.prepare();
      assertEquals("host", renderer.libraryMode());
      assertFalse("no libs directory in host mode", Files.exists(libsOf(renderer)));
      renderer.render("<p>x</p>", null, deadlineIn(20000L));
      String pid = fake.started().get(0);
      assertFalse(fake.recorded("env", pid), fake.recorded("env", pid).contains("LD_LIBRARY_PATH"));
      assertEquals(Arrays.asList("unset"), versionLibraryPaths(fake));
      assertEquals("host libraries: nothing to say beyond CHG-LPRE", 0, messages(Level.INFO).size()
        + messages(Level.WARN).size());
    } finally {
      renderer.close();
    }
  }

  @Test
  public void aFailureThatIsNotAMissingLibraryDoesNotSwitchToTheShippedOnes() throws Exception {
    FakePhantomJs fake = fake("version-fail");
    PhantomJsRenderer renderer = linuxRenderer(fake);
    try {
      renderer.prepare();
      assertEquals("host", renderer.libraryMode());
      assertFalse(Files.exists(libsOf(renderer)));
      try {
        renderer.queryVersion();
        fail("the fake exits 3");
      } catch (PhantomJsRenderer.ProcessFailedException e) {
        assertEquals(3, e.exitCode());
      }
      assertEquals(Arrays.asList("unset", "unset"), versionLibraryPaths(fake));
    } finally {
      renderer.close();
    }
  }

  @Test
  public void platformsWithoutLibrariesAndConfiguredPathsNeverProbe() throws Exception {
    FakePhantomJs mac = fake("libs-required");
    PhantomJsRenderer macRenderer =
      new PhantomJsRenderer("", embeddedFor(mac, "Mac OS X", "x86_64", EmbeddedPhantomJs.shippedLibraries()));
    FakePhantomJs configured = fake("loader-fail");
    PhantomJsRenderer override = new PhantomJsRenderer(configured.path(),
      embeddedFor(configured, "Linux", "amd64", EmbeddedPhantomJs.shippedLibraries()));
    try {
      override.prepare();
      assertEquals("n/a", override.libraryMode());
      assertFalse("a configured path is run as it is, never probed",
        Files.exists(Path.of(configured.path()).getParent().resolve("record").resolve("libpath.version")));
      try {
        macRenderer.prepare();
        macRenderer.queryVersion();
        fail("the fake needs libraries and macOS never gives any");
      } catch (PhantomJsRenderer.ProcessFailedException e) {
        assertEquals(127, e.exitCode());
      }
      assertEquals("n/a", macRenderer.libraryMode());
      assertEquals(Arrays.asList("unset"), versionLibraryPaths(mac));
    } finally {
      override.close();
      macRenderer.close();
    }
  }

  // ---- FN-007: neither the host's libraries nor the shipped ones ------------------------------------------------

  @Test
  public void whenNeitherWorksOneWarnWithTheGlibcHintAndEveryRenderFailsWithoutRunningPhantomJs() throws Exception {
    FakePhantomJs fake = fake("loader-fail");
    PhantomJsRenderer renderer = linuxRenderer(fake);
    try {
      for (int i = 0; i < 3; i++) {
        try {
          renderer.prepare();
          fail("prepared a build that cannot start");
        } catch (PdfRenderException e) {
          assertEquals(PhantomJsRenderer.LIBRARIES_UNAVAILABLE_MESSAGE, e.getMessage());
        }
      }
      try {
        renderer.render("<p>SECRET-HTML-MARKER</p>", null, deadlineIn(5000L));
        fail("rendered with a build that cannot start");
      } catch (PdfRenderException e) {
        assertEquals(PhantomJsRenderer.LIBRARIES_UNAVAILABLE_MESSAGE, e.getMessage());
      }
      assertEquals("unavailable", renderer.libraryMode());
      assertTrue("no render was started", fake.started().isEmpty());
      List<String> probes = versionLibraryPaths(fake);
      assertEquals("probed once with each kind of library, never again: " + probes, 2, probes.size());
      assertEquals("unset", probes.get(0));
      assertTrue(probes.get(1), probes.get(1).endsWith("libs"));
      List<String> warns = messages(Level.WARN);
      assertEquals(warns.toString(), 1, warns.size());
      assertTrue(warns.get(0), warns.get(0).contains("host libraries nor the shipped ones"));
      assertTrue(warns.get(0), warns.get(0).contains("glibc older than 2.36"));
      assertTrue("the only startup line names the platform (FN-007): " + warns.get(0),
        warns.get(0).contains("platform=Linux/amd64"));
      assertEquals("no INFO about using the shipped libraries: they did not work", "never logged",
        levelOf(HOST_MESSAGE));
    } finally {
      renderer.close();
    }
  }

  // ---- FN-002 / DD-2: a probe that could not be judged ---------------------------------------------------------

  /** The shipped libraries, recording every call as {@code expected} or {@code open <name>}. */
  private static EmbeddedPhantomJs.Libraries recording(final List<String> calls) {
    final EmbeddedPhantomJs.Libraries shipped = EmbeddedPhantomJs.shippedLibraries();
    return new EmbeddedPhantomJs.Libraries() {

      @Override
      public Map<String, String> expected() throws IOException {
        calls.add("expected");
        return shipped.expected();
      }

      @Override
      public InputStream open(String name) throws IOException {
        calls.add("open " + name);
        return shipped.open(name);
      }
    };
  }

  private boolean debugContains(String text) {
    for (String line : messages(Level.DEBUG)) {
      if (line.contains(text)) {
        return true;
      }
    }
    return false;
  }

  @Test(timeout = 30000L)
  public void aHostProbeThatTimesOutKeepsTheHostLibrariesForEveryRenderAndNeverOpensTheShippedOnes()
    throws Exception {
    FakePhantomJs fake = fake("version-hang");
    List<String> calls = new ArrayList<String>();
    PhantomJsRenderer renderer = new PhantomJsRenderer("", embeddedFor(fake, "Linux", "amd64", recording(calls)));
    try {
      long start = System.nanoTime();
      renderer.prepare();
      long elapsedMs = (System.nanoTime() - start) / 1000000L;
      assertTrue("bounded by the version timeout, not by the fake's 60 s sleep: " + elapsedMs + " ms",
        elapsedMs < PhantomJsRenderer.VERSION_TIMEOUT_MS + 5000L);
      assertTrue(messages(Level.DEBUG).toString(),
        debugContains("host libraries could not be judged, cause=DeadlineExpiredException"));
      assertEquals("host", renderer.libraryMode());
      assertFalse("no libs directory in host mode", Files.exists(libsOf(renderer)));
      assertEquals("the shipped libraries were never opened", new ArrayList<String>(), calls);
      assertEquals(Arrays.asList("unset"), versionLibraryPaths(fake));

      fake.mode("ok");
      renderer.render("<p>x</p>", null, deadlineIn(20000L));
      String env = fake.recorded("env", fake.started().get(0));
      assertFalse(env, env.contains("LD_LIBRARY_PATH"));
      assertEquals("the decision is kept: no second probe", Arrays.asList("unset"), versionLibraryPaths(fake));
      assertEquals(messages(Level.WARN).toString(), 0, messages(Level.WARN).size());
    } finally {
      renderer.close();
    }
  }

  @Test(timeout = 30000L)
  public void aHostProbeThatCannotStartKeepsTheHostLibrariesAndNeverOpensTheShippedOnes() throws Exception {
    // libs-required: had the probe started, the loader message would have switched to the shipped libraries.
    FakePhantomJs fake = fake("libs-required");
    List<String> calls = new ArrayList<String>();
    final byte[] script = Files.readAllBytes(Path.of(fake.path()));
    final byte[] compressed = gzip(script);
    // The file store is reported as non-POSIX, so the extracted copy keeps its 0644 bits and the start is refused.
    EmbeddedPhantomJs embedded = new EmbeddedPhantomJs("Linux", "amd64", sha256(script),
      () -> new ByteArrayInputStream(compressed), file -> true, file -> false, recording(calls));
    PhantomJsRenderer renderer = new PhantomJsRenderer("", embedded);
    try {
      renderer.prepare();
      assertTrue(messages(Level.DEBUG).toString(),
        debugContains("host libraries could not be judged, cause=PdfRenderException"));
      assertEquals("host", renderer.libraryMode());
      assertFalse("no libs directory in host mode", Files.exists(libsOf(renderer)));
      assertEquals("the shipped libraries were never opened", new ArrayList<String>(), calls);
      assertFalse("no --version ever ran", Files.exists(
        Path.of(fake.path()).getParent().resolve("record").resolve("libpath.version")));
      try {
        renderer.render("<p>x</p>", null, deadlineIn(5000L));
        fail("rendered with an executable that cannot start");
      } catch (PdfRenderException e) {
        assertEquals("PhantomJS binary is not available", e.getMessage());
      }
      assertTrue(fake.started().isEmpty());
      assertEquals(messages(Level.WARN).toString(), 0, messages(Level.WARN).size());
    } finally {
      renderer.close();
    }
  }

  private void assertUnavailableAfterTheRetry(PhantomJsRenderer renderer, FakePhantomJs fake, List<String> calls)
    throws Exception {
    for (int i = 0; i < 2; i++) {
      try {
        renderer.prepare();
        fail("prepared a build whose retry with the shipped libraries could not be judged");
      } catch (PdfRenderException e) {
        assertEquals(PhantomJsRenderer.LIBRARIES_UNAVAILABLE_MESSAGE, e.getMessage());
      }
    }
    try {
      renderer.render("<p>SECRET-HTML-MARKER</p>", null, deadlineIn(5000L));
      fail("rendered with a build whose retry could not be judged");
    } catch (PdfRenderException e) {
      assertEquals(PhantomJsRenderer.LIBRARIES_UNAVAILABLE_MESSAGE, e.getMessage());
    }
    assertEquals("unavailable", renderer.libraryMode());
    assertTrue("no render was started", fake.started().isEmpty());
    assertEquals("expected", calls.get(0));
    TreeSet<String> opened = new TreeSet<String>();
    for (String call : calls.subList(1, calls.size())) {
      opened.add(call.replaceFirst("^open ", ""));
    }
    assertEquals("each recorded library was opened once to be verified, and nothing else", SHIPPED.size() + 1,
      calls.size());
    assertEquals(new TreeSet<String>(SHIPPED.keySet()), opened);
    assertTrue(messages(Level.DEBUG).toString(),
      debugContains("PhantomJS --version with the shipped libraries could not run, cause="));
    List<String> warns = messages(Level.WARN);
    assertEquals(warns.toString(), 1, warns.size());
    assertTrue(warns.get(0), warns.get(0).contains("glibc older than 2.36"));
    assertTrue(warns.get(0), warns.get(0).contains("platform=Linux/amd64"));
    assertEquals("never logged", levelOf(HOST_MESSAGE));
  }

  @Test(timeout = 30000L)
  public void aRetryThatCannotStartMakesTheBuildUnavailableWithOneWarnAndNoRender() throws Exception {
    FakePhantomJs fake = fake("loader-fail-then-unstartable");
    List<String> calls = new ArrayList<String>();
    PhantomJsRenderer renderer = new PhantomJsRenderer("", embeddedFor(fake, "Linux", "amd64", recording(calls)));
    try {
      assertUnavailableAfterTheRetry(renderer, fake, calls);
      assertEquals("the retry was refused before the script ran", Arrays.asList("unset"),
        versionLibraryPaths(fake));
    } finally {
      renderer.close();
    }
  }

  @Test(timeout = 30000L)
  public void aRetryThatHangsIsKilledAtTheVersionTimeoutAndTheBuildIsUnavailable() throws Exception {
    FakePhantomJs fake = fake("libs-hang");
    List<String> calls = new ArrayList<String>();
    PhantomJsRenderer renderer = new PhantomJsRenderer("", embeddedFor(fake, "Linux", "amd64", recording(calls)));
    try {
      long start = System.nanoTime();
      assertUnavailableAfterTheRetry(renderer, fake, calls);
      long elapsedMs = (System.nanoTime() - start) / 1000000L;
      assertTrue("bounded by the version timeout, not by the fake's 60 s sleep: " + elapsedMs + " ms",
        elapsedMs < PhantomJsRenderer.VERSION_TIMEOUT_MS + 5000L);
      assertTrue(debugContains("shipped libraries could not run, cause=DeadlineExpiredException"));
      List<String> probes = versionLibraryPaths(fake);
      assertEquals(probes.toString(), 2, probes.size());
      assertEquals("unset", probes.get(0));
      assertTrue(probes.get(1), probes.get(1).endsWith("libs"));
    } finally {
      renderer.close();
    }
  }

  // ---- NF-003: only the current platform's libraries ------------------------------------------------------------

  @Test
  public void onMacOsAndWindowsTheShippedLibrariesAreNeverOpenedNorExtracted() throws Exception {
    String[][] platforms = {{"Mac OS X", "aarch64"}, {"Mac OS X", "x86_64"}, {"Windows 11", "amd64"}};
    for (String[] platform : platforms) {
      // libs-required: a probe on this platform would have extracted the libraries.
      FakePhantomJs fake = fake("libs-required");
      List<String> calls = new ArrayList<String>();
      PhantomJsRenderer renderer =
        new PhantomJsRenderer("", embeddedFor(fake, platform[0], platform[1], recording(calls)));
      try {
        renderer.prepare();
        String label = platform[0] + "/" + platform[1];
        assertTrue(label, renderer.usesEmbeddedBinary());
        assertEquals(label, "n/a", renderer.libraryMode());
        assertEquals(label + ": the shipped libraries were never opened", new ArrayList<String>(), calls);
        assertFalse(label, Files.exists(libsOf(renderer)));
      } finally {
        renderer.close();
      }
    }
  }

  // ---- SEC-001: nothing unverified is written or run ------------------------------------------------------------

  /** The shipped libraries with one of them replaced by other bytes, as a damaged or swapped WAR entry would be. */
  private static EmbeddedPhantomJs.Libraries withDamaged(final String name) {
    final EmbeddedPhantomJs.Libraries shipped = EmbeddedPhantomJs.shippedLibraries();
    return new EmbeddedPhantomJs.Libraries() {

      @Override
      public Map<String, String> expected() throws IOException {
        return shipped.expected();
      }

      @Override
      public InputStream open(String library) throws IOException {
        if (name.equals(library)) {
          return new ByteArrayInputStream(gzip("not the library".getBytes(StandardCharsets.UTF_8)));
        }
        return shipped.open(library);
      }
    };
  }

  @Test
  public void aLibraryResourceThatDoesNotMatchItsChecksumIsNeverWrittenNorAnyOther() throws Exception {
    FakePhantomJs fake = fake("ok");
    EmbeddedPhantomJs embedded = embeddedFor(fake, "Linux", "amd64", withDamaged("libz.so.1"));
    Path dir = temp.newFolder().toPath();
    try {
      embedded.extractLibrariesTo(dir);
      fail("extracted a library that is not the recorded one");
    } catch (IOException e) {
      assertTrue(e.getMessage(), e.getMessage().contains("libz.so.1"));
    }
    assertEquals("nothing is left behind, not even the libraries that were fine", new TreeSet<String>(),
      listing(dir));
  }

  @Test
  public void aDamagedLibraryResourceRefusesTheRenderWithOneWarnAndNoRun() throws Exception {
    FakePhantomJs fake = fake("loader-fail");
    PhantomJsRenderer renderer =
      new PhantomJsRenderer("", embeddedFor(fake, "Linux", "amd64", withDamaged("libexpat.so.1")));
    try {
      try {
        renderer.prepare();
        fail("prepared with libraries that are not the recorded ones");
      } catch (PdfRenderException e) {
        assertEquals(PhantomJsRenderer.LIBRARIES_UNAVAILABLE_MESSAGE, e.getMessage());
      }
      try {
        renderer.render("<p>x</p>", null, deadlineIn(5000L));
        fail("rendered with libraries that are not the recorded ones");
      } catch (PdfRenderException e) {
        assertEquals(PhantomJsRenderer.LIBRARIES_UNAVAILABLE_MESSAGE, e.getMessage());
      }
      assertTrue(fake.started().isEmpty());
      assertEquals("the retry never ran: only the host probe did", Arrays.asList("unset"),
        versionLibraryPaths(fake));
      List<String> warns = messages(Level.WARN);
      assertEquals(warns.toString(), 1, warns.size());
      assertTrue(warns.get(0), warns.get(0).contains("libraries shipped with MARLO could not be extracted"));
      assertTrue("the only startup line names the platform (FN-007): " + warns.get(0),
        warns.get(0).contains("platform=Linux/amd64"));
    } finally {
      renderer.close();
    }
  }

  @Test
  public void aLibraryAlteredAfterExtractionIsExtractedAgainBeforeTheNextRender() throws Exception {
    FakePhantomJs fake = fake("libs-required");
    PhantomJsRenderer renderer = linuxRenderer(fake);
    try {
      renderer.prepare();
      Path before = Path.of(renderer.binaryPath()).getParent();
      Files.write(before.resolve("libs").resolve("libz.so.1"), "tampered".getBytes(StandardCharsets.UTF_8));

      renderer.render("<p>x</p>", null, deadlineIn(20000L));

      Path after = Path.of(renderer.binaryPath()).getParent();
      assertNotEquals("the altered directory was replaced", before, after);
      assertFalse(Files.exists(before));
      assertEquals(SHIPPED.get("libz.so.1"), sha256(after.resolve("libs").resolve("libz.so.1")));
      String env = fake.recorded("env", fake.started().get(0));
      assertTrue(env, Arrays.asList(env.split("\n")).contains("LD_LIBRARY_PATH=" + after.resolve("libs")));
      assertEquals("bundled", renderer.libraryMode());
      assertEquals("the mode is kept: the rebuild did not probe again", 2, versionLibraryPaths(fake).size());
    } finally {
      renderer.close();
    }
  }

  @Test
  public void aMissingLibraryIsExtractedAgainBeforeTheNextRender() throws Exception {
    FakePhantomJs fake = fake("libs-required");
    PhantomJsRenderer renderer = linuxRenderer(fake);
    try {
      renderer.prepare();
      Path before = Path.of(renderer.binaryPath()).getParent();
      Files.delete(before.resolve("libs").resolve("libexpat.so.1"));

      renderer.render("<p>x</p>", null, deadlineIn(20000L));

      Path after = Path.of(renderer.binaryPath()).getParent();
      assertNotEquals(before, after);
      assertEquals(SHIPPED.get("libexpat.so.1"), sha256(after.resolve("libs").resolve("libexpat.so.1")));
    } finally {
      renderer.close();
    }
  }

  // ---- the extraction itself ------------------------------------------------------------------------------------

  @Test
  public void theShippedLibrariesAreExactlyTheSevenRecordedOnesAndNeverLibcOrLibm() throws Exception {
    FakePhantomJs fake = fake("ok");
    EmbeddedPhantomJs embedded = embeddedFor(fake, "Linux", "amd64", EmbeddedPhantomJs.shippedLibraries());
    assertTrue(embedded.shipsLibraries());
    Path dir = temp.newFolder().toPath();
    Map<String, EmbeddedPhantomJs.Fingerprint> recorded = embedded.extractLibrariesTo(dir);
    assertEquals(new TreeSet<String>(SHIPPED.keySet()), new TreeSet<String>(recorded.keySet()));
    assertEquals(new TreeSet<String>(SHIPPED.keySet()), listing(dir));
    for (Map.Entry<String, String> library : SHIPPED.entrySet()) {
      assertEquals(library.getValue(), sha256(dir.resolve(library.getKey())));
      assertEquals("rw-------", PosixFilePermissions.toString(Files.getPosixFilePermissions(
        dir.resolve(library.getKey()))));
    }
    assertFalse(listing(dir).contains("libc.so.6"));
    assertFalse(listing(dir).contains("libm.so.6"));
    assertTrue(embedded.verifyLibraries(dir, recorded));
  }

  @Test
  public void verifyingLibrariesCatchesAnAlteredOrMissingOneAndAcceptsATouchedCopyWithTheRightBytes()
    throws Exception {
    FakePhantomJs fake = fake("ok");
    EmbeddedPhantomJs embedded = embeddedFor(fake, "Linux", "amd64", EmbeddedPhantomJs.shippedLibraries());
    Path dir = temp.newFolder().toPath();
    Map<String, EmbeddedPhantomJs.Fingerprint> recorded = embedded.extractLibrariesTo(dir);

    Path png = dir.resolve("libpng16.so.16");
    byte[] original = Files.readAllBytes(png);
    Files.setLastModifiedTime(png, java.nio.file.attribute.FileTime.from(java.time.Instant.now().plusSeconds(30L)));
    assertTrue("same bytes, new time: accepted after hashing", embedded.verifyLibraries(dir, recorded));

    byte[] flipped = original.clone();
    flipped[flipped.length / 2] ^= 1;
    Files.write(png, flipped);
    assertFalse("same size, one bit changed", embedded.verifyLibraries(dir, recorded));
    Files.write(png, original);
    assertTrue(embedded.verifyLibraries(dir, recorded));

    Files.delete(dir.resolve("libz.so.1"));
    assertFalse(embedded.verifyLibraries(dir, recorded));
    Files.write(dir.resolve("libz.so.1"), Files.readAllBytes(png));
    assertFalse("another file in the place of a library", embedded.verifyLibraries(dir, recorded));
  }

  @Test
  public void aFileThatIsNotOneOfTheLibrariesMakesTheDirectoryUnverified() throws Exception {
    FakePhantomJs fake = fake("ok");
    EmbeddedPhantomJs embedded = embeddedFor(fake, "Linux", "amd64", EmbeddedPhantomJs.shippedLibraries());
    Path dir = temp.newFolder().toPath();
    Map<String, EmbeddedPhantomJs.Fingerprint> recorded = embedded.extractLibrariesTo(dir);
    assertTrue(embedded.verifyLibraries(dir, recorded));
    Files.write(dir.resolve("libm.so.6"), "shadows the host's libm".getBytes(StandardCharsets.UTF_8));
    assertFalse("a planted library would shadow a host one", embedded.verifyLibraries(dir, recorded));
    assertFalse(embedded.verifyLibraries(dir.resolve("absent"), recorded));
  }

  // ---- OPS-002: the startup line --------------------------------------------------------------------------------

  private ListAppender<ILoggingEvent> serviceLog() {
    ch.qos.logback.classic.Logger serviceLogger =
      (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(LocalPdfReportService.class);
    serviceLogger.setLevel(Level.DEBUG);
    ListAppender<ILoggingEvent> appender = new ListAppender<ILoggingEvent>();
    appender.start();
    serviceLogger.addAppender(appender);
    return appender;
  }

  private static List<String> startupLines(ListAppender<ILoggingEvent> appender) {
    List<String> lines = new ArrayList<String>();
    for (ILoggingEvent event : new ArrayList<ILoggingEvent>(appender.list)) {
      if (event.getFormattedMessage().startsWith("PhantomJS available")) {
        lines.add(event.getFormattedMessage());
      }
    }
    return lines;
  }

  @Test
  public void theStartupLineNamesThePlatformTheBuildAndTheLibraryMode() throws Exception {
    Assume.assumeFalse("the fake is a shell script", System.getProperty("os.name", "").startsWith("Windows"));
    for (String[] row : new String[][] {{"libs-required", "bundled"}, {"ok", "host"}}) {
      ListAppender<ILoggingEvent> appender = serviceLog();
      FakePhantomJs fake = fake(row[0]);
      LocalPdfReportService service = new LocalPdfReportService("", HandlebarsReportHelpers.newHandlebars(),
        embeddedFor(fake, "Linux", "amd64", EmbeddedPhantomJs.shippedLibraries()));
      try {
        List<String> lines = startupLines(appender);
        assertEquals(lines.toString(), 1, lines.size());
        assertTrue(lines.get(0), lines.get(0).startsWith("PhantomJS available for local PDF renders, version=2.1.1, "
          + "platform=Linux/amd64, build=linux-x86_64, libraries=" + row[1] + ", path="));
      } finally {
        service.shutdown();
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(LocalPdfReportService.class))
          .detachAppender(appender);
      }
    }
  }

  @Test
  public void startupOnAHostThatCannotRunItAddsNoSecondLineToTheRenderersOneWarn() throws Exception {
    ListAppender<ILoggingEvent> appender = serviceLog();
    FakePhantomJs fake = fake("loader-fail");
    LocalPdfReportService service = new LocalPdfReportService("", HandlebarsReportHelpers.newHandlebars(),
      embeddedFor(fake, "Linux", "amd64", EmbeddedPhantomJs.shippedLibraries()));
    try {
      assertTrue(startupLines(appender).isEmpty());
      List<String> warns = new ArrayList<String>();
      for (ILoggingEvent event : new ArrayList<ILoggingEvent>(appender.list)) {
        if (event.getLevel() == Level.WARN) {
          warns.add(event.getFormattedMessage());
        }
      }
      assertEquals("the renderer's WARN says why; the service adds nothing", new ArrayList<String>(), warns);
      assertEquals(messages(Level.WARN).toString(), 1, messages(Level.WARN).size());
      assertTrue(fake.started().isEmpty());
    } finally {
      service.shutdown();
      ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(LocalPdfReportService.class))
        .detachAppender(appender);
    }
  }
}
