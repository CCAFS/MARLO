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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests the PhantomJS process wrapper (CHG-LPRE-T13) against a fake executable: what it starts, with which
 * environment and input, and what it does when the process fails, floods its streams or hangs. No test needs the real
 * PhantomJS. The page script's allowlist predicate runs under node when node is installed.
 */
public class PhantomJsRendererTest {

  private static final String SECRET = "SECRET-HTML-MARKER";
  private static final ObjectMapper JSON = new ObjectMapper();

  @Rule
  public TemporaryFolder temp = new TemporaryFolder();

  private PhantomJsRenderer rendererFor(FakePhantomJs fake) {
    // Pinned to Linux, whatever host runs the tests: the environment follows the host platform (CHG-PPE-T03).
    return new PhantomJsRenderer(fake.path(), embeddedFor("Linux", "amd64"));
  }

  private static EmbeddedPhantomJs embeddedFor(String osName, String osArch) {
    return new EmbeddedPhantomJs(osName, osArch, "none", null);
  }

  private static Function<String, String> hostEnvironment(final String systemRoot) {
    return new Function<String, String>() {

      @Override
      public String apply(String name) {
        return "SystemRoot".equals(name) ? systemRoot : null;
      }
    };
  }

  private Map<String, String> environmentOf(String osName, String osArch, String systemRoot) {
    Path home = Path.of("render-home");
    return new PhantomJsRenderer("", embeddedFor(osName, osArch), hostEnvironment(systemRoot)).baseEnvironment(home);
  }

  private static long deadlineIn(long millis) {
    return System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(millis);
  }

  private static Map<String, Object> options() {
    Map<String, Object> options = new LinkedHashMap<String, Object>();
    options.put("format", "A4");
    options.put("orientation", "portrait");
    Map<String, Object> header = new LinkedHashMap<String, Object>();
    header.put("height", "40mm");
    options.put("header", header);
    return options;
  }

  // ---- success and what the process is given -------------------------------------------------------------------

  @Test
  public void aRenderReturnsThePdfAndTheCountsOfBlockedAndFailedLoads() throws Exception {
    FakePhantomJs fake = new FakePhantomJs(temp.newFolder().toPath(), "ok");
    PhantomJsRenderer renderer = rendererFor(fake);
    try {
      PhantomJsRenderer.Output output = renderer.render("<html><body>x</body></html>", options(), deadlineIn(20000L));
      assertTrue(new String(output.pdf, StandardCharsets.ISO_8859_1).startsWith("%PDF-1.4"));
      assertEquals(2, output.blocked);
      assertEquals(1, output.failed);
      assertEquals("the fake PDF holds one /Type /Page and one /Type /Pages", 1, output.pages);
    } finally {
      renderer.close();
    }
  }

  @Test
  public void theProcessGetsTheHtmlPageScriptArgumentsAndOnlyTheMinimalEnvironment() throws Exception {
    FakePhantomJs fake = new FakePhantomJs(temp.newFolder().toPath(), "ok");
    PhantomJsRenderer renderer = rendererFor(fake);
    String html = "<html><body><p>caf\u00e9 \u4e2d\u6587 " + SECRET + "</p></body></html>";
    try {
      renderer.render(html, options(), deadlineIn(20000L));
    } finally {
      renderer.close();
    }
    String pid = fake.started().get(0);

    // The arguments are html-pdf's: local URLs off, then the script.
    String[] args = fake.recorded("args", pid).trim().split(" ");
    assertEquals("--local-url-access=false", args[0]);
    assertTrue(args[1], args[1].endsWith("render-page.js"));
    assertTrue("the page script must exist when PhantomJS starts", fake.hasRecorded("script-present", pid));

    // stdin is the one-line JSON html-pdf sends: {html, options}, UTF-8, ending in a newline.
    String stdin = fake.recorded("stdin", pid);
    assertTrue(stdin.endsWith("}\n"));
    assertEquals(1, stdin.trim().split("\n").length);
    JsonNode sent = JSON.readTree(stdin);
    assertEquals(html, sent.get("html").asText());
    JsonNode sentOptions = sent.get("options");
    assertEquals("A4", sentOptions.get("format").asText());
    assertEquals("40mm", sentOptions.get("header").get("height").asText());
    assertEquals(Arrays.asList(PhantomJsRenderer.ALLOWED_HOSTS.toArray()),
      Arrays.asList(JSON.convertValue(sentOptions.get("allowedHosts"), Object[].class)));
    String home = fake.recorded("home", pid).trim();
    assertEquals("the PDF goes to a file inside the render directory", home + "/report.pdf",
      sentOptions.get("filename").asText());

    // The environment holds what the design lists and nothing inherited (the shell adds PWD, SHLVL and _).
    Map<String, String> env = new LinkedHashMap<String, String>();
    for (String line : fake.recorded("env", pid).split("\n")) {
      int eq = line.indexOf('=');
      if (eq > 0) {
        env.put(line.substring(0, eq), line.substring(eq + 1));
      }
    }
    env.remove("PWD");
    env.remove("SHLVL");
    env.remove("_");
    env.remove("OLDPWD");
    assertEquals(env.toString(), new java.util.TreeSet<String>(Arrays.asList("FONTCONFIG_FILE", "HOME", "LANG",
      "OPENSSL_CONF", "PATH", "TMPDIR")), new java.util.TreeSet<String>(env.keySet()));
    assertEquals("PhantomJS's OpenSSL cannot parse a modern openssl.cnf", "/dev/null", env.get("OPENSSL_CONF"));
    assertTrue(env.get("FONTCONFIG_FILE"), env.get("FONTCONFIG_FILE").endsWith("fonts.conf"));
    assertEquals("C.UTF-8", env.get("LANG"));
    assertEquals(home, env.get("HOME"));
    assertEquals(home, env.get("TMPDIR"));
    String pwd = fake.recorded("pwd", pid).trim();
    assertTrue("the working directory is the render directory (macOS reports it under /private): " + pwd,
      pwd.endsWith(home));
  }

  @Test
  public void fontconfigIsDebiansRuleSetOverThePrivateDejaVuDirectoryAndNothingElse() throws Exception {
    FakePhantomJs fake = new FakePhantomJs(temp.newFolder().toPath(), "ok");
    PhantomJsRenderer renderer = rendererFor(fake);
    String conf;
    Path support;
    Path fonts;
    try {
      renderer.render("<html><body>x</body></html>", options(), deadlineIn(20000L));
      String pid = fake.started().get(0);
      conf = fake.recorded("fonts.conf", pid);
      String env = fake.recorded("env", pid);
      String confPath = env.substring(env.indexOf("FONTCONFIG_FILE=") + "FONTCONFIG_FILE=".length());
      support = Path.of(confPath.substring(0, confPath.indexOf('\n'))).getParent();
      fonts = support.resolve("fonts");
      java.util.Set<String> extracted = new java.util.TreeSet<String>(Arrays.asList(fonts.toFile().list()));
      assertEquals("the six fonts of fonts-dejavu-core: Sans, Serif and Mono, regular and bold",
        new java.util.TreeSet<String>(Arrays.asList("DejaVuSans.ttf", "DejaVuSans-Bold.ttf", "DejaVuSerif.ttf",
          "DejaVuSerif-Bold.ttf", "DejaVuSansMono.ttf", "DejaVuSansMono-Bold.ttf")), extracted);
      assertEquals(extracted, new java.util.TreeSet<String>(PhantomJsRenderer.FONT_FILES));

      // The rule files are the bundled set, extracted whole, and none adds a font or cache directory.
      Path rules = support.resolve("conf.d");
      List<String> listed = new java.util.ArrayList<String>();
      try (InputStream in = PhantomJsRenderer.class.getResourceAsStream("/pdf/fontconfig/conf.list")) {
        for (String line : new String(in.readAllBytes(), StandardCharsets.UTF_8).split("\\R")) {
          if (!line.trim().isEmpty()) {
            listed.add(line.trim());
          }
        }
      }
      assertTrue("the rule set is not bundled: " + listed, listed.size() > 20);
      assertEquals(new java.util.TreeSet<String>(listed), new java.util.TreeSet<String>(
        Arrays.asList(rules.toFile().list())));
      for (String excluded : new String[] {"50-user.conf", "51-local.conf"}) {
        assertFalse(excluded + " adds user font directories", listed.contains(excluded));
      }
      for (String name : listed) {
        String rule = new String(Files.readAllBytes(rules.resolve(name)), StandardCharsets.UTF_8);
        assertFalse(name + " adds a directory", rule.contains("<dir") || rule.contains("<cachedir")
          || rule.contains("<include"));
        assertFalse(name, rule.contains("~") && rule.contains("<include"));
      }
      assertTrue("hinting rule of the production image", Files.exists(rules.resolve("10-hinting-slight.conf")));
      assertTrue(Files.exists(rules.resolve("20-unhint-small-dejavu-sans.conf")));
      assertTrue(Files.exists(rules.resolve("57-dejavu-sans.conf")));
    } finally {
      renderer.close();
    }
    assertEquals("exactly one font directory", 1, conf.split("<dir>", -1).length - 1);
    assertTrue(conf, conf.contains("<dir>" + fonts + "</dir>"));
    assertTrue(conf, conf.contains("<include ignore_missing=\"yes\">" + support.resolve("conf.d") + "</include>"));
    assertTrue(conf, conf.contains("<cachedir>" + support.resolve("cache") + "</cachedir>"));
    assertEquals("exactly one cache directory", 1, conf.split("<cachedir>", -1).length - 1);
    assertFalse("the system fonts must not be visible: " + conf, conf.contains("/usr/share/fonts"));
    assertFalse("no home-relative entries: " + conf, conf.contains("~"));
    assertFalse("a placeholder was left: " + conf, conf.contains("@"));
    assertFalse("close() removes the private directory", Files.exists(support));
  }

  @Test
  public void theRenderDirectoryIsRemovedAfterASuccessAndAfterEveryKindOfFailure() throws Exception {
    String[] modes = {"ok", "exit1", "nofile", "emptyfile", "notpdf", "badjson", "flood-stdout"};
    for (String mode : modes) {
      FakePhantomJs fake = new FakePhantomJs(temp.newFolder().toPath(), mode);
      PhantomJsRenderer renderer = rendererFor(fake);
      try {
        try {
          renderer.render("<html><body>x</body></html>", options(), deadlineIn(20000L));
        } catch (PdfRenderException expected) {
          assertTrue(mode, !"ok".equals(mode));
        }
        Path home = Path.of(fake.recorded("home", fake.started().get(0)).trim());
        assertFalse(mode + ": " + home + " was not removed", Files.exists(home));
      } finally {
        renderer.close();
      }
    }
  }

  @Test
  public void theTimeoutIsANumberAsHtmlPdfSendsItAndNeverBeyondTheTimeLeftBeforeTheDeadline() throws Exception {
    assertEquals(300000, PhantomJsRenderer.pageTimeout("300000", 999999L));
    assertEquals(300000, PhantomJsRenderer.pageTimeout(" 300000 ", 999999L));
    assertEquals(45000, PhantomJsRenderer.pageTimeout(45000, 999999L));
    assertEquals(30000, PhantomJsRenderer.pageTimeout(null, 999999L));
    assertEquals(30000, PhantomJsRenderer.pageTimeout("soon", 999999L));
    assertEquals(30000, PhantomJsRenderer.pageTimeout(0, 999999L));
    assertEquals(30000, PhantomJsRenderer.pageTimeout(-5, 999999L));
    assertEquals("cut to the time left", 7000, PhantomJsRenderer.pageTimeout("300000", 7000L));
    assertEquals(1, PhantomJsRenderer.pageTimeout("300000", -50L));
    FakePhantomJs fake = new FakePhantomJs(temp.newFolder().toPath(), "ok");
    PhantomJsRenderer renderer = rendererFor(fake);
    Map<String, Object> options = options();
    options.put("timeout", "300000");
    try {
      renderer.render("<p>x</p>", options, deadlineIn(20000L));
    } finally {
      renderer.close();
    }
    JsonNode timeout = JSON.readTree(fake.recorded("stdin", fake.started().get(0))).get("options").get("timeout");
    assertTrue("a number, not the text \"300000\": " + timeout, timeout.isInt());
    assertTrue("cut to the 20 s left: " + timeout, timeout.asInt() > 10000 && timeout.asInt() <= 20000);
  }

  @Test
  public void aSupportDirectoryThatLostFilesIsRebuiltBeforeTheNextRender() throws Exception {
    FakePhantomJs fake = new FakePhantomJs(temp.newFolder().toPath(), "ok");
    PhantomJsRenderer renderer = rendererFor(fake);
    try {
      renderer.render("<p>x</p>", options(), deadlineIn(20000L));
      String env = fake.recorded("env", fake.started().get(0));
      String confPath = env.substring(env.indexOf("FONTCONFIG_FILE=") + "FONTCONFIG_FILE=".length());
      Path support = Path.of(confPath.substring(0, confPath.indexOf('\n'))).getParent();
      Files.delete(support.resolve("render-page.js"));
      Files.delete(support.resolve("fonts/DejaVuSans-Bold.ttf"));
      renderer.render("<p>y</p>", options(), deadlineIn(20000L));
      String second = fake.started().get(1);
      String env2 = fake.recorded("env", second);
      String conf2 = env2.substring(env2.indexOf("FONTCONFIG_FILE=") + "FONTCONFIG_FILE=".length());
      support = Path.of(conf2.substring(0, conf2.indexOf('\n'))).getParent();
      assertTrue("the page script must be back when PhantomJS starts", fake.hasRecorded("script-present", second));
      assertTrue(Files.isRegularFile(support.resolve("render-page.js")));
      assertTrue(Files.isRegularFile(support.resolve("fonts/DejaVuSans-Bold.ttf")));
    } finally {
      renderer.close();
    }
  }

  @Test
  public void aSupportDirectoryMissingAnyOneOfTheSixFontsIsRebuilt() throws Exception {
    FakePhantomJs fake = new FakePhantomJs(temp.newFolder().toPath(), "ok");
    PhantomJsRenderer renderer = rendererFor(fake);
    try {
      int render = 0;
      for (String font : PhantomJsRenderer.FONT_FILES) {
        renderer.render("<p>x</p>", options(), deadlineIn(20000L));
        String env = fake.recorded("env", fake.started().get(render++));
        String confPath = env.substring(env.indexOf("FONTCONFIG_FILE=") + "FONTCONFIG_FILE=".length());
        Path support = Path.of(confPath.substring(0, confPath.indexOf('\n'))).getParent();
        Files.delete(support.resolve("fonts").resolve(font));
        renderer.render("<p>y</p>", options(), deadlineIn(20000L));
        String again = fake.recorded("env", fake.started().get(render++));
        String conf2 = again.substring(again.indexOf("FONTCONFIG_FILE=") + "FONTCONFIG_FILE=".length());
        Path rebuilt = Path.of(conf2.substring(0, conf2.indexOf('\n'))).getParent();
        assertTrue(font + " must be back", Files.isRegularFile(rebuilt.resolve("fonts").resolve(font)));
      }
    } finally {
      renderer.close();
    }
  }

  @Test
  public void theBundledFontsAreTheBytesOfDebiansFontsDejavuCore237() throws Exception {
    String[][] expected = {
      {"DejaVuSans.ttf", "abdc775b21b1bc470d50c97e790d276f2054b7504e56e5bd3e64f48d68582322"},
      {"DejaVuSans-Bold.ttf", "0d977336a6d5fba34eab8e3199eb218327161b5143749f802982c2bc34df0c96"},
      {"DejaVuSerif.ttf", "13e61509f5c81d7c3132810f4f903e3523df89c802bf6e0674621e8f659cdfe1"},
      {"DejaVuSerif-Bold.ttf", "e2fd85eba2de65ac270d1cdb1685e252eb827f600850cf62af2d20c41b22e945"},
      {"DejaVuSansMono.ttf", "0f5db4f1749979d961019838b160bec74abdf7f9eca69553fe1aa856bbff49a4"},
      {"DejaVuSansMono-Bold.ttf", "2964f6dac8e6e9d71613928340f17bf868e9ea51692cca333c79e74962f02233"}};
    for (String[] font : expected) {
      byte[] bytes;
      try (InputStream in = PhantomJsRenderer.class.getResourceAsStream("/pdf/fonts/" + font[0])) {
        bytes = in.readAllBytes();
      }
      StringBuilder hex = new StringBuilder();
      for (byte b : java.security.MessageDigest.getInstance("SHA-256").digest(bytes)) {
        hex.append(String.format("%02x", b));
      }
      assertEquals(font[0], font[1], hex.toString());
    }
  }

  @Test
  public void theOptionsOfThePayloadAreNotModified() throws Exception {
    FakePhantomJs fake = new FakePhantomJs(temp.newFolder().toPath(), "ok");
    PhantomJsRenderer renderer = rendererFor(fake);
    Map<String, Object> options = options();
    try {
      renderer.render("<html><body>x</body></html>", options, deadlineIn(20000L));
    } finally {
      renderer.close();
    }
    assertEquals(Arrays.asList("format", "orientation", "header"), Arrays.asList(options.keySet().toArray()));
  }

  // ---- failures ------------------------------------------------------------------------------------------------

  private PdfRenderException failureOf(String mode, String html) throws Exception {
    FakePhantomJs fake = new FakePhantomJs(temp.newFolder().toPath(), mode);
    PhantomJsRenderer renderer = rendererFor(fake);
    try {
      renderer.render(html, options(), deadlineIn(20000L));
      fail(mode + " did not fail");
      return null;
    } catch (PdfRenderException e) {
      return e;
    } finally {
      renderer.close();
    }
  }

  @Test
  public void aNonZeroExitFailsWithTheExitCodeAndNoStderrText() throws Exception {
    PdfRenderException e = failureOf("exit1", "<html><body>" + SECRET + "</body></html>");
    assertTrue(e.getMessage(), e.getMessage().contains("exited with code 1"));
    assertFalse(e.getMessage(), e.getMessage().contains("SECRET"));
  }

  @Test
  public void pageScriptErrorsPrintedAheadOfTheJsonDoNotFailTheRender() throws Exception {
    FakePhantomJs fake = new FakePhantomJs(temp.newFolder().toPath(), "noisy-stdout");
    PhantomJsRenderer renderer = rendererFor(fake);
    try {
      PhantomJsRenderer.Output output = renderer.render("<p>x</p>", options(), deadlineIn(20000L));
      assertTrue(output.pdf.length > 0);
    } finally {
      renderer.close();
    }
  }

  @Test
  public void aMissingEmptyOrForeignOutputFileFails() throws Exception {
    assertTrue(failureOf("nofile", "<p>x</p>").getMessage().contains("no output file"));
    assertTrue(failureOf("emptyfile", "<p>x</p>").getMessage().contains("no output file"));
    assertTrue(failureOf("notpdf", "<p>x</p>").getMessage().contains("not a PDF"));
    assertTrue("text after the JSON must not pass",
      failureOf("trailing-json", "<p>x</p>").getMessage().contains("not valid JSON"));
  }

  @Test
  public void unparsableStdoutFailsWithoutQuotingIt() throws Exception {
    PdfRenderException e = failureOf("badjson", "<p>x</p>");
    assertTrue(e.getMessage(), e.getMessage().contains("not valid JSON"));
    assertFalse(e.getMessage(), e.getMessage().contains("SECRET"));
  }

  @Test
  public void stdoutBeyondTheCapFailsAndStderrBeyondItIsDrainedWithoutBlockingTheProcess() throws Exception {
    PdfRenderException e = failureOf("flood-stdout", "<p>x</p>");
    assertTrue(e.getMessage(), e.getMessage().contains("more than " + PhantomJsRenderer.STREAM_CAP + " bytes"));

    // Three MB on stderr would fill the pipe and block the process if nobody drained it.
    FakePhantomJs fake = new FakePhantomJs(temp.newFolder().toPath(), "flood-stderr");
    PhantomJsRenderer renderer = rendererFor(fake);
    try {
      long start = System.nanoTime();
      PhantomJsRenderer.Output output = renderer.render("<p>x</p>", options(), deadlineIn(20000L));
      assertTrue(output.pdf.length > 0);
      assertTrue("took " + (System.nanoTime() - start) / 1000000L + " ms",
        System.nanoTime() - start < TimeUnit.SECONDS.toNanos(15L));
    } finally {
      renderer.close();
    }
  }

  @Test
  public void aMissingEmptyOrNotExecutableBinaryFailsAtOnceWithoutStartingAnything() throws Exception {
    Path notExecutable = temp.newFile("phantomjs-plain").toPath();
    String[] paths = {null, "", "   ", temp.getRoot().toPath().resolve("does-not-exist").toString(),
      notExecutable.toString(), temp.getRoot().toString()};
    for (String path : paths) {
      PhantomJsRenderer renderer =
        new PhantomJsRenderer(path, new EmbeddedPhantomJs("FreeBSD", "amd64", "none", null));
      assertFalse(String.valueOf(path), renderer.isBinaryUsable());
      long start = System.nanoTime();
      try {
        renderer.render("<p>x</p>", options(), deadlineIn(20000L));
        fail("rendered without a binary: " + path);
      } catch (PdfRenderException e) {
        assertEquals("PhantomJS binary is not available", e.getMessage());
      }
      assertTrue(System.nanoTime() - start < TimeUnit.SECONDS.toNanos(1L));
      try {
        renderer.queryVersion();
        fail("version of a missing binary");
      } catch (PdfRenderException e) {
        assertEquals("PhantomJS binary is not available", e.getMessage());
      }
    }
  }

  // ---- the deadline --------------------------------------------------------------------------------------------

  @Test
  public void aHangingProcessAndItsChildAreKilledAtTheDeadlineAndTheDirectoryIsRemoved() throws Exception {
    FakePhantomJs fake = new FakePhantomJs(temp.newFolder().toPath(), "hang");
    PhantomJsRenderer renderer = rendererFor(fake);
    long start = System.nanoTime();
    try {
      renderer.render("<html><body>x</body></html>", options(), deadlineIn(1500L));
      fail("a hanging render returned");
    } catch (PhantomJsRenderer.DeadlineExpiredException e) {
      long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
      assertTrue("elapsed " + elapsed + " ms", elapsed >= 1400L && elapsed < 6000L);
    } finally {
      renderer.close();
    }
    String pid = fake.started().get(0);
    assertTrue("the process is still alive", FakePhantomJs.awaitDead(pid));
    String child = fake.childOf(pid);
    assertTrue("the descendant (sleep) is still alive", FakePhantomJs.awaitDead(child));
    assertFalse(Files.exists(Path.of(fake.recorded("home", pid).trim())));
  }

  @Test
  public void aDeadlineThatHasAlreadyPassedStartsNothing() throws Exception {
    FakePhantomJs fake = new FakePhantomJs(temp.newFolder().toPath(), "ok");
    PhantomJsRenderer renderer = rendererFor(fake);
    try {
      renderer.render("<p>x</p>", options(), deadlineIn(-1000L));
      fail("rendered after the deadline");
    } catch (PhantomJsRenderer.DeadlineExpiredException expected) {
      assertTrue(fake.started().isEmpty());
    } finally {
      renderer.close();
    }
  }

  @Test
  public void aProcessThatNeverReadsItsInputDoesNotHoldTheCallerPastTheDeadline() throws Exception {
    FakePhantomJs fake = new FakePhantomJs(temp.newFolder().toPath(), "nostdin");
    PhantomJsRenderer renderer = rendererFor(fake);
    StringBuilder big = new StringBuilder("<html><body>");
    for (int i = 0; i < 200000; i++) {
      big.append("<p>0123456789</p>");
    }
    long start = System.nanoTime();
    try {
      renderer.render(big.append("</body></html>").toString(), options(), deadlineIn(1500L));
      fail("a render that never reads its input returned");
    } catch (PhantomJsRenderer.DeadlineExpiredException e) {
      assertTrue("elapsed " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start) + " ms",
        System.nanoTime() - start < TimeUnit.SECONDS.toNanos(8L));
    } finally {
      renderer.close();
    }
    assertTrue(FakePhantomJs.awaitDead(fake.started().get(0)));
  }

  @Test
  public void anInterruptedThreadKillsTheProcessAndKeepsItsInterruptFlag() throws Exception {
    final FakePhantomJs fake = new FakePhantomJs(temp.newFolder().toPath(), "hang");
    final PhantomJsRenderer renderer = rendererFor(fake);
    final boolean[] flagKept = new boolean[1];
    final Exception[] failure = new Exception[1];
    Thread worker = new Thread(new Runnable() {

      @Override
      public void run() {
        try {
          renderer.render("<p>x</p>", options(), deadlineIn(60000L));
        } catch (PdfRenderException e) {
          failure[0] = e;
          flagKept[0] = Thread.currentThread().isInterrupted();
        }
      }
    });
    worker.start();
    fake.awaitStarted(1);
    String pid = fake.started().get(0);
    String child = fake.childOf(pid);
    worker.interrupt();
    worker.join(10000L);
    renderer.close();
    assertFalse("the render did not end", worker.isAlive());
    assertTrue(String.valueOf(failure[0]), failure[0] instanceof PdfRenderException
      && !(failure[0] instanceof PhantomJsRenderer.DeadlineExpiredException));
    assertTrue("the interrupt flag was lost", flagKept[0]);
    assertTrue(FakePhantomJs.awaitDead(pid));
    assertTrue(FakePhantomJs.awaitDead(child));
  }

  // ---- version check -------------------------------------------------------------------------------------------

  @Test
  public void theVersionIsTheFirstLinePhantomJsPrints() throws Exception {
    FakePhantomJs fake = new FakePhantomJs(temp.newFolder().toPath(), "ok");
    assertEquals("2.1.1", rendererFor(fake).queryVersion());
    fake.mode("version-other");
    assertEquals("1.9.8", rendererFor(fake).queryVersion());
    assertTrue("--version must not count as a render", fake.started().isEmpty());
  }

  @Test
  public void theVersionCheckRunsWithTheSameMinimalEnvironmentIncludingOpensslConf() throws Exception {
    FakePhantomJs fake = new FakePhantomJs(temp.newFolder().toPath(), "ok");
    rendererFor(fake).queryVersion();
    Map<String, String> env = new LinkedHashMap<String, String>();
    for (String line : fake.recorded("env", "version").split("\n")) {
      int eq = line.indexOf('=');
      if (eq > 0) {
        env.put(line.substring(0, eq), line.substring(eq + 1));
      }
    }
    env.keySet().removeAll(Arrays.asList("PWD", "SHLVL", "_", "OLDPWD"));
    assertEquals(env.toString(), new java.util.TreeSet<String>(Arrays.asList("HOME", "LANG", "OPENSSL_CONF", "PATH",
      "TMPDIR")), new java.util.TreeSet<String>(env.keySet()));
    assertEquals("/dev/null", env.get("OPENSSL_CONF"));
  }

  @Test
  public void aFailedRenderCarriesItsExitCodeButNoStderrAndTheVersionCheckCarriesTheFirstLineCutAndClean()
    throws Exception {
    PdfRenderException failure = failureOf("exit1-long", "<p>x</p>");
    assertTrue(failure instanceof PhantomJsRenderer.ProcessFailedException);
    PhantomJsRenderer.ProcessFailedException failed = (PhantomJsRenderer.ProcessFailedException) failure;
    assertEquals(1, failed.exitCode());
    assertEquals("a render's stderr is the page script's and can quote page content", "", failed.stderrLine());
    assertFalse("the exception message must not carry stderr", failure.getMessage().contains("eee"));

    FakePhantomJs fake = new FakePhantomJs(temp.newFolder().toPath(), "version-fail");
    try {
      rendererFor(fake).queryVersion();
      fail("a failing --version succeeded");
    } catch (PhantomJsRenderer.ProcessFailedException e) {
      assertEquals(3, e.exitCode());
      assertEquals("Auto configuration failed first line", e.stderrLine());
    }
    assertEquals("e".repeat(PhantomJsRenderer.MAX_STDERR_LINE),
      PhantomJsRenderer.firstLine(("e".repeat(500) + "\u0001\nsecond").getBytes(StandardCharsets.UTF_8)));
    assertEquals("a b", PhantomJsRenderer.firstLine("a\u0001 b\u007f\nsecond".getBytes(StandardCharsets.UTF_8)));
    assertEquals("", PhantomJsRenderer.firstLine(new byte[0]));
  }

  @Test
  public void aVersionCheckThatFailsOrHangsFailsWithinTheFiveSecondBound() throws Exception {
    FakePhantomJs fake = new FakePhantomJs(temp.newFolder().toPath(), "version-fail");
    try {
      rendererFor(fake).queryVersion();
      fail("a failing --version succeeded");
    } catch (PdfRenderException e) {
      assertTrue(e.getMessage(), e.getMessage().contains("code 3"));
    }
    fake.mode("version-hang");
    long start = System.nanoTime();
    try {
      rendererFor(fake).queryVersion();
      fail("a hanging --version returned");
    } catch (PhantomJsRenderer.DeadlineExpiredException e) {
      long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
      assertTrue("elapsed " + elapsed + " ms", elapsed >= 4900L && elapsed < 9000L);
    }
  }

  // ---- sanitizer (CHG-LPRE-SEC-003) ----------------------------------------------------------------------------

  @Test
  public void htmlWithNothingToRemoveComesBackAsTheSameString() throws Exception {
    String html = "<html>\n<head><style>p { color: red }</style></head>\n<body>\n  <div id=\"pageHeader\">h</div>"
      + "<p style=\"x\">one &amp; two <a href=\"https://example.org/a?b=1&amp;c=2\">link</a> "
      + "<img src=\"data:image/png;base64,AAAA\" alt=\"1\"/> <a href=\"#top\">top</a></p>\n"
      + "<table><tr><td>  spaced   </td></tr></table></body></html>";
    assertSame(html, PhantomJsRenderer.sanitize(html));
    String fragment = "<p>just a fragment</p>";
    assertSame(fragment, PhantomJsRenderer.sanitize(fragment));
    assertEquals("", PhantomJsRenderer.sanitize(""));
    assertEquals(null, PhantomJsRenderer.sanitize(null));
  }

  @Test
  public void scriptElementsEventAttributesAndJavascriptUrlsAreRemoved() throws Exception {
    String html = "<html><body onload=\"steal()\"><p onclick=\"a()\" ONMOUSEOVER=\"b()\" class=\"keep\">text</p>"
      + "<script>alert('x')</script><script src=\"https://evil.example/x.js\"></script>"
      + "<a href=\"javascript:alert(1)\">a</a><a href=\"  JaVaScRiPt:alert(1)\">b</a>"
      + "<a href=\"java&#x09;script:alert(1)\">c</a><a href=\"jav\nascript:alert(1)\">d</a>"
      + "<img src=\"javascript:alert(1)\"><form action=\"javascript:x()\"><button formaction=\"javascript:y()\">"
      + "go</button></form><svg><a xlink:href=\"javascript:z()\">s</a><script>q()</script></svg>"
      + "<iframe srcdoc=\"&lt;script&gt;alert(1)&lt;/script&gt;\"></iframe>"
      + "<a href=\"https://ok.example/page\">fine</a><p class=\"onward\">kept</p></body></html>";
    String clean = PhantomJsRenderer.sanitize(html);
    assertNotSame(html, clean);
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse(clean);
    assertEquals("no <script> element may remain", 0, document.getElementsByTag("script").size());
    for (org.jsoup.nodes.Element element : document.getAllElements()) {
      for (org.jsoup.nodes.Attribute attribute : element.attributes()) {
        String name = attribute.getKey().toLowerCase();
        assertFalse("event attribute left: " + name, name.startsWith("on"));
        assertFalse("srcdoc left", name.equals("srcdoc"));
        assertFalse("javascript: left in " + name + "=" + attribute.getValue(),
          attribute.getValue().replaceAll("[\\s\\x00-\\x20]", "").toLowerCase().startsWith("javascript:"));
      }
    }
    assertTrue("untouched markup must survive", clean.contains("class=\"keep\"") && clean.contains("text")
      && clean.contains("https://ok.example/page") && clean.contains("class=\"onward\"") && clean.contains("go"));
  }

  /** The markup left after sanitizing, parsed again: what is live in it. */
  private static org.jsoup.nodes.Document parsed(String html) {
    return org.jsoup.Jsoup.parse(PhantomJsRenderer.sanitize(html));
  }

  @Test
  public void aNoscriptThatWebKitReadsAsMarkupButJsoupAsAnAttributeIsRemoved() throws Exception {
    String html = "<html><body><p>kept</p><noscript><p title=\"</noscript><img src=x onerror=alert(1)>\"></noscript>"
      + "</body></html>";
    String clean = PhantomJsRenderer.sanitize(html);
    assertNotSame(html, clean);
    assertFalse(clean, clean.toLowerCase().contains("noscript"));
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse(clean);
    assertEquals(0, document.getElementsByTag("img").size());
    assertTrue(clean, clean.contains("kept"));
    // A lone noscript that jsoup would fold into the head is still caught by the raw scan.
    assertNotSame("<noscript><p title=\"</noscript><img src=x onerror=a()>\">",
      PhantomJsRenderer.sanitize("<noscript><p title=\"</noscript><img src=x onerror=a()>\">"));
  }

  @Test
  public void embeddedDocumentsBaseAndRefreshAreRemoved() throws Exception {
    String[] hostile = {"<noembed><img src=x onerror=alert(1)></noembed>",
      "<noframes><img src=x onerror=a()></noframes>",
      "<template><img src=x onerror=a()></template>", "<svg><style><img src=x onerror=alert(1)></style></svg>",
      "<math><style><img src=x></style></math>", "<iframe src=\"data:text/html,<script>alert(1)</script>\"></iframe>",
      "<iframe src=\"https://x.example/\"></iframe>", "<frameset><frame src=\"x\"></frameset>",
      "<embed src=\"data:image/svg+xml,<svg onload=alert(1)>\">",
      "<object data=\"data:text/html,<script>alert(1)</script>\"></object>", "<applet code=\"A\"></applet>",
      "<base href=\"https://evil.example/\">",
      "<meta http-equiv=\"refresh\" content=\"0;url=data:text/html,<script>alert(1)</script>\">",
      "<META HTTP-EQUIV=\"Set-Cookie\" CONTENT=\"a=b\">"};
    for (String element : hostile) {
      String html = "<html><head><meta charset=\"UTF-8\"></head><body><p>kept</p>" + element + "</body></html>";
      String clean = PhantomJsRenderer.sanitize(html);
      assertNotSame(element, html, clean);
      org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse(clean);
      assertEquals(element, 0, document.select("iframe, frame, frameset, object, embed, applet, base, "
        + "svg, math, noembed, noframes, template, meta[http-equiv]").size());
      assertTrue(element + " -> " + clean, clean.contains("kept") && clean.contains("charset"));
    }
    String plain = "<html><head><meta charset=\"UTF-8\" /></head><body><p>kept</p></body></html>";
    assertSame("<meta charset> is not an http-equiv and must stay", plain, PhantomJsRenderer.sanitize(plain));
  }

  @Test
  public void textThatLooksLikeAHandlerIsNeverPassedOnAsItCame() throws Exception {
    String[] looks = {"<p>kept</p><x-widget onclick=\"a()\">", "<p>kept</p><img src=x ONERROR=a()>",
      "<p title='x' onmouseover = \"a()\">kept</p>", "<p>kept</p><b/onload=a()>",
      "<p>kept</p><p title=\"\"onfocus=a()>"};
    for (String html : looks) {
      String clean = PhantomJsRenderer.sanitize(html);
      assertNotSame(html, html, clean);
      assertFalse(html + " -> " + clean, clean.toLowerCase().matches("(?s).*\\son[a-z]+\\s*=.*"));
    }
    // Words that merely contain "on" are not handlers. (Text such as ' one=1' does match, and is only re-serialized.)
    String plain = "<html><body><p class=\"onward\" title=\"button=1\">decision=final; on=off; condition=x</p>"
      + "</body></html>";
    assertSame(plain, PhantomJsRenderer.sanitize(plain));
  }

  @Test
  public void dataUrlsAreKeptOnlyForImagesOtherThanSvgAndForFonts() throws Exception {
    String[] removedAttributes = {"<a href=\"data:text/html,<script>alert(1)</script>\">x</a>",
      "<video poster=\"data:text/html,x\"></video>", "<img src=\"data:image/svg+xml;base64,PHN2Zz48L3N2Zz4=\">",
      "<img src=\" DATA:IMAGE/SVG+XML,<svg/>\">", "<img src=\"da\tta:text/html,x\">",
      "<a xlink:href=\"data:application/javascript,alert(1)\">x</a>", "<form action=\"data:text/html,x\"></form>"};
    for (String element : removedAttributes) {
      String html = "<html><body><p>kept</p>" + element + "</body></html>";
      String clean = PhantomJsRenderer.sanitize(html);
      assertNotSame(element, html, clean);
      assertFalse(element + " -> " + clean, clean.toLowerCase().contains("data:text")
        || clean.toLowerCase().contains("svg+xml") || clean.toLowerCase().contains("javascript"));
    }
    String kept = "<html><body><img src=\"data:image/png;base64,iVBORw0KGgo=\">"
      + "<img src=\"data:image/jpeg;base64,/9j/\">"
      + "<style>@font-face { src: url(data:font/woff;base64,d09G) }</style></body></html>";
    assertSame(kept, PhantomJsRenderer.sanitize(kept));
  }

  @Test
  public void theJavaDataRuleMatchesThePageScriptRule() throws Exception {
    for (Object[] row : URL_CASES) {
      String url = (String) row[0];
      if (url.toLowerCase().startsWith("data:") && url.length() < 1000 && !url.equals(TRUSTED_SVG)) {
        assertEquals("isAllowedDataUrl(" + url + ")", row[1], PhantomJsRenderer.isAllowedDataUrl(url));
      }
    }
    assertFalse(PhantomJsRenderer.isAllowedDataUrl(null));
  }

  @Test
  public void theRenderSendsTheSanitizedHtmlToPhantomJs() throws Exception {
    FakePhantomJs fake = new FakePhantomJs(temp.newFolder().toPath(), "ok");
    PhantomJsRenderer renderer = rendererFor(fake);
    try {
      renderer.render("<html><body onload=\"steal()\"><p>kept</p><script>alert(1)</script></body></html>", options(),
        deadlineIn(20000L));
    } finally {
      renderer.close();
    }
    String sent = JSON.readTree(fake.recorded("stdin", fake.started().get(0))).get("html").asText();
    assertTrue(sent, sent.contains("<p>kept</p>"));
    assertFalse(sent, sent.contains("onload") || sent.contains("<script") || sent.contains("alert"));
  }

  // ---- page count ----------------------------------------------------------------------------------------------

  @Test
  public void pagesAreCountedByPageObjectsAndNotByPagesObjects() throws Exception {
    String pdf = "%PDF-1.4\n<< /Type /Catalog >>\n<< /Type /Pages /Count 3 >>\n<< /Type /Page\n/Parent 1 0 R >>\n"
      + "<< /Type /Page\n>>\n<< /Type /Page >>\n<< /Type /Pagesx >>\n<< /Type /Font >>\n%%EOF\n";
    assertEquals(3, PhantomJsRenderer.countPages(pdf.getBytes(StandardCharsets.ISO_8859_1)));
    assertEquals(0, PhantomJsRenderer.countPages("%PDF-1.4\n".getBytes(StandardCharsets.ISO_8859_1)));
    assertEquals(0, PhantomJsRenderer.countPages(new byte[0]));
  }

  // ---- template images shipped inside MARLO (CHG-LPRE-T17) -------------------------------------------------------

  @Test
  public void bucketImagesAreInlinedAfterTheSanitizerAndOnlyTheirSvgDataUrlsAreTrusted() throws Exception {
    FakePhantomJs fake = new FakePhantomJs(temp.newFolder().toPath(), "ok");
    PhantomJsRenderer renderer = rendererFor(fake);
    String readiness = "https://" + PdfImageBundle.PRMS_HOST + "/images/inno-scaling-readiness-3.png";
    String user = "https://" + PdfImageBundle.PRMS_HOST + "/images/a.png";
    String html = "<html><body><img src=\"https://" + PdfImageBundle.HOST + "/flags/ke.svg\">"
      + "<img src=\"https://" + PdfImageBundle.HOST + "/Africa.png\"><img src=\"" + readiness + "\">"
      + "<img src=\"" + user + "\">"
      + "<img src=\"https://" + PdfImageBundle.HOST + "/flags/zz99.svg\" onerror=\"x()\"></body></html>";
    try {
      renderer.render(html, options(), deadlineIn(20000L));
    } finally {
      renderer.close();
    }
    JsonNode sent = JSON.readTree(fake.recorded("stdin", fake.started().get(0)));
    String sentHtml = sent.get("html").asText();
    assertFalse("the bucket is never requested", sentHtml.contains(PdfImageBundle.HOST));
    assertFalse("the second bucket is never requested", sentHtml.contains(PdfImageBundle.PRMS_HOST));
    assertFalse("the sanitizer still ran on the markup", sentHtml.contains("onerror"));
    assertTrue(sentHtml.contains("data:image/png;base64,iVBOR"));
    JsonNode trusted = sent.get("options").get("allowedDataUrls");
    assertEquals("only the SVG that was inlined is trusted, not the PNG or the unavailable one", 1, trusted.size());
    assertTrue(trusted.get(0).asText().startsWith("data:image/svg+xml;base64,"));
    assertTrue(sentHtml.contains(trusted.get(0).asText()));
    assertTrue("the page script allows no host", PhantomJsRenderer.ALLOWED_HOSTS.isEmpty());
    assertEquals(0, sent.get("options").get("allowedHosts").size());
  }

  @Test
  public void aPageWithoutBucketUrlsReachesPhantomJsUnchangedWithNoTrustedDataUrls() throws Exception {
    FakePhantomJs fake = new FakePhantomJs(temp.newFolder().toPath(), "ok");
    PhantomJsRenderer renderer = rendererFor(fake);
    String html = "<html><body><p>plain</p></body></html>";
    try {
      renderer.render(html, options(), deadlineIn(20000L));
    } finally {
      renderer.close();
    }
    JsonNode sent = JSON.readTree(fake.recorded("stdin", fake.started().get(0)));
    assertEquals(html, sent.get("html").asText());
    assertEquals(0, sent.get("options").get("allowedDataUrls").size());
  }

  // ---- the page script's allowlist (CHG-LPRE-SEC-001) ----------------------------------------------------------

  private static final String HOST = "marlo-pdf-resources-dev.s3.us-east-1.amazonaws.com";
  private static final String OTHER_HOST = "prms-file-storage.s3.amazonaws.com";
  /** An SVG data URL the renderer placed in the page (T17): the one SVG the page script lets through. */
  private static final String TRUSTED_SVG =
    "data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciLz4=";

  /** Each row: the URL and whether the page script must let it through, from the SEC-001 text. */
  private static final Object[][] URL_CASES = {
    {"https://" + HOST + "/a.png", true},
    {"https://" + OTHER_HOST + "/dir/b.png?x=1", true},
    {"HTTPS://" + HOST.toUpperCase() + "/a.png", true},
    {"https://" + HOST + ":443/a.png", true},
    {"https://" + HOST, true},
    {"https://" + HOST + "?q=1", true},
    {"data:image/png;base64,iVBORw0KGgo=", true},
    {"DATA:image/png;base64,iVBORw0KGgo=", true},
    {"data:image/jpeg;base64,/9j/4AAQ", true},
    {"data:font/woff;base64,d09GRgABAAAA", true},
    {"data:application/font-woff;base64,d09G", true},
    {"data:application/x-font-ttf;base64,AAEA", true},
    {"data:image/svg+xml;base64,PHN2Zz48L3N2Zz4=", false},
    {TRUSTED_SVG, true},
    {TRUSTED_SVG + "A", false},
    {TRUSTED_SVG.toUpperCase(), false},
    {"data:image/svg+xml;base64,PHN2ZyBvbmxvYWQ9YWxlcnQoMSk+", false},
    {"DATA:IMAGE/SVG+XML,<svg onload=alert(1)>", false},
    {"data:text/html,<script>alert(1)</script>", false},
    {"data:text/html;base64,PHNjcmlwdD4=", false},
    {"data:application/javascript,alert(1)", false},
    {"data:application/xhtml+xml,<html/>", false},
    {"data:,plain", false},
    {"data:image/png", false},
    {"data:font", false},
    {"http://" + HOST + "/a.png", false},
    {"ftp://" + HOST + "/a.png", false},
    {"file:///etc/passwd", false},
    {"jar:file:/x.jar!/a.png", false},
    {"about:blank", false},
    {"//" + HOST + "/a.png", false},
    {"/a.png", false},
    {"relative.png", false},
    {"https://evil.example/a.png", false},
    {"https://" + HOST + ".evil.example/a.png", false},
    {"https://evil." + HOST + "/a.png", false},
    {"https://" + HOST + "@evil.example/a.png", false},
    {"https://user:pw@" + HOST + "/a.png", false},
    {"https://" + HOST + ":8443/a.png", false},
    {"https://" + HOST + ":4433/a.png", false},
    {"https://" + HOST + "\\@evil.example/a.png", false},
    {"https://169.254.169.254/latest/meta-data", false},
    {"https://[::1]/a.png", false},
    {"https://" + HOST + "/" + "a".repeat(2100), false},
    {"data:image/png;base64," + "A".repeat(2 * 1024 * 1024), false},
    {"data:image/png;base64," + "A".repeat(2 * 1024 * 1024 - 30), true},
    {"", false}};

  @Test
  public void theAllowlistPredicateOfThePageScriptMatchesTheSecurityRules() throws Exception {
    JsonNode results = runPredicate("['" + HOST + "', '" + OTHER_HOST + "']");
    if (results == null) {
      return;
    }
    for (int i = 0; i < URL_CASES.length; i++) {
      assertEquals("isAllowedUrl(" + shown(i) + ")", URL_CASES[i][1], results.get(i).asBoolean());
    }
  }

  @Test
  public void withNoAllowedHostEveryHttpUrlIsRefusedAndOnlyTheDataRulesRemain() throws Exception {
    for (String hosts : new String[] {"[]", "undefined", "null"}) {
      JsonNode results = runPredicate(hosts);
      if (results == null) {
        return;
      }
      for (int i = 0; i < URL_CASES.length; i++) {
        String url = (String) URL_CASES[i][0];
        boolean data = url.regionMatches(true, 0, "data:", 0, 5);
        assertEquals("hosts " + hosts + ": isAllowedUrl(" + shown(i) + ")", data && (Boolean) URL_CASES[i][1],
          results.get(i).asBoolean());
      }
    }
  }

  private static String shown(int row) {
    String url = (String) URL_CASES[row][0];
    return url.length() > 80 ? url.substring(0, 80) + "..." : url;
  }

  /** Runs isAllowedUrl under node over URL_CASES with the given hosts expression; null when node is missing. */
  private JsonNode runPredicate(String hostsExpression) throws Exception {
    Process probe;
    try {
      probe = new ProcessBuilder("node", "--version").redirectErrorStream(true).start();
    } catch (IOException e) {
      Assume.assumeNoException("node is not installed; the predicate is covered by the integration test", e);
      return null;
    }
    probe.waitFor(10, TimeUnit.SECONDS);
    String script;
    try (InputStream in = PhantomJsRenderer.class.getResourceAsStream("/pdf/render-page.js")) {
      script = new String(in.readAllBytes(), StandardCharsets.UTF_8);
    }
    int begin = script.indexOf("// BEGIN isAllowedUrl");
    int end = script.indexOf("// END isAllowedUrl");
    assertTrue("the markers around isAllowedUrl are missing", begin > 0 && end > begin);
    Path module = temp.newFile().toPath();
    Files.write(module, (script.substring(begin, end) + "\nvar cases = JSON.parse(require('fs').readFileSync(0, "
      + "'utf8'));\nvar hosts = " + hostsExpression + ";\nvar trusted = {};\ntrusted['" + TRUSTED_SVG
      + "'] = true;\nconsole.log(JSON.stringify(cases.map("
      + "function (url) { return isAllowedUrl(url, hosts, trusted); })));\n").getBytes(StandardCharsets.UTF_8));
    List<String> urls = new java.util.ArrayList<String>();
    for (Object[] row : URL_CASES) {
      urls.add((String) row[0]);
    }
    Process node = new ProcessBuilder("node", module.toString()).redirectErrorStream(true).start();
    node.getOutputStream().write(JSON.writeValueAsBytes(urls));
    node.getOutputStream().close();
    String out = new String(node.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    assertTrue(node.waitFor(20, TimeUnit.SECONDS));
    JsonNode results = JSON.readTree(out);
    assertEquals(URL_CASES.length, results.size());
    return results;
  }

  @Test
  public void theLinuxEnvironmentIsTodaysKeysAndValues() {
    Map<String, String> env = environmentOf("Linux", "amd64", null);
    Map<String, String> expected = new LinkedHashMap<String, String>();
    expected.put("PATH", "/usr/bin:/bin");
    expected.put("OPENSSL_CONF", "/dev/null");
    expected.put("HOME", "render-home");
    expected.put("TMPDIR", "render-home");
    expected.put("LANG", "C.UTF-8");
    assertEquals(expected, env);
  }

  @Test
  public void theMacosEnvironmentIsTheLinuxOneAndRenderingAddsNoFontconfigFile() throws Exception {
    assertEquals(environmentOf("Linux", "amd64", null), environmentOf("Mac OS X", "aarch64", null));
    assertEquals(environmentOf("Linux", "amd64", null), environmentOf("Mac OS X", "x86_64", null));
    FakePhantomJs fake = new FakePhantomJs(temp.newFolder().toPath(), "ok");
    PhantomJsRenderer renderer = new PhantomJsRenderer(fake.path(), embeddedFor("Mac OS X", "aarch64"));
    try {
      renderer.render("<html><body>x</body></html>", options(), deadlineIn(20000L));
      String env = fake.recorded("env", fake.started().get(0));
      assertFalse(env, env.contains("FONTCONFIG_FILE"));
      assertTrue(env, env.contains("OPENSSL_CONF=/dev/null"));
    } finally {
      renderer.close();
    }
  }

  @Test
  public void theWindowsEnvironmentHasTheWindowsKeysAndValuesAndNothingPosix() {
    Map<String, String> env = environmentOf("Windows 11", "amd64", "D:\\WinRoot");
    Map<String, String> expected = new LinkedHashMap<String, String>();
    expected.put("SystemRoot", "D:\\WinRoot");
    expected.put("PATH", "D:\\WinRoot\\System32");
    expected.put("OPENSSL_CONF", "NUL");
    expected.put("USERPROFILE", "render-home");
    expected.put("TEMP", "render-home");
    expected.put("TMP", "render-home");
    expected.put("LANG", "C.UTF-8");
    assertEquals(expected, env);
    assertFalse(env.containsKey("FONTCONFIG_FILE"));
    assertFalse(env.containsKey("HOME"));
    assertFalse(env.containsKey("TMPDIR"));
    for (String value : env.values()) {
      assertFalse(value, value.contains("/dev/null") || value.contains("/usr/bin"));
    }
  }

  @Test
  public void windowsFallsBackToCWindowsWhenTheJvmHasNoSystemRoot() {
    for (String missing : new String[] {null, "", "  "}) {
      Map<String, String> env = environmentOf("Windows 10", "x86", missing);
      assertEquals("C:\\Windows", env.get("SystemRoot"));
      assertEquals("C:\\Windows\\System32", env.get("PATH"));
    }
  }

  @Test
  public void aConfiguredPathDoesNotChangeTheEnvironmentStyleAndAnUnknownPlatformKeepsTheLinuxOne() {
    PhantomJsRenderer configured =
      new PhantomJsRenderer("C:\\tools\\phantomjs.exe", embeddedFor("Windows 11", "amd64"), hostEnvironment("C:\\W"));
    assertEquals("NUL", configured.baseEnvironment(Path.of("h")).get("OPENSSL_CONF"));
    assertEquals(environmentOf("Linux", "amd64", null), environmentOf("FreeBSD", "amd64", null));
  }

  @Test
  public void theOwnerNameLosesItsDomainOrMachinePrefix() {
    assertEquals("kenji", PhantomJsRenderer.ownerName("kenji"));
    assertEquals("kenji", PhantomJsRenderer.ownerName("CGIAR\\kenji"));
    assertEquals("kenji", PhantomJsRenderer.ownerName("MACHINE-01\\kenji"));
    assertEquals("other", PhantomJsRenderer.ownerName("CGIAR\\other"));
    assertEquals("", PhantomJsRenderer.ownerName(null));
    assertFalse("kenji".equals(PhantomJsRenderer.ownerName("CGIAR\\other")));
  }

  @Test
  public void onWindowsTheDomainPrefixIsIgnoredOnBothSidesOfTheOwnerComparison() {
    PhantomJsBuild.EnvironmentStyle windows = PhantomJsBuild.EnvironmentStyle.WINDOWS;
    assertTrue(PhantomJsRenderer.isSameOwner("DOMAIN\\kenji", "kenji", windows));
    assertTrue(PhantomJsRenderer.isSameOwner("kenji", "kenji", windows));
    assertTrue(PhantomJsRenderer.isSameOwner("DOMAIN\\kenji", "DOMAIN\\kenji", windows));
    assertFalse(PhantomJsRenderer.isSameOwner("DOMAIN\\other", "kenji", windows));
  }

  @Test
  public void onPosixTheOwnerIsComparedExactlyAsItIsReported() {
    PhantomJsBuild.EnvironmentStyle posix = PhantomJsBuild.EnvironmentStyle.POSIX;
    assertTrue("winbind: both names carry the domain", PhantomJsRenderer.isSameOwner("DOMAIN\\kenji", "DOMAIN\\kenji",
      posix));
    assertTrue(PhantomJsRenderer.isSameOwner("kenji", "kenji", posix));
    assertFalse(PhantomJsRenderer.isSameOwner("DOMAIN\\kenji", "kenji", posix));
    assertFalse("another domain's kenji is not this user", PhantomJsRenderer.isSameOwner("OTHERDOMAIN\\kenji",
      "kenji", posix));
  }

  @Test
  public void theArgumentsAreTheSameOnLinuxAndMacos() throws Exception {
    String[] platforms = {"Linux", "Mac OS X"};
    String first = null;
    for (String platform : platforms) {
      FakePhantomJs fake = new FakePhantomJs(temp.newFolder().toPath(), "ok");
      PhantomJsRenderer renderer =
        new PhantomJsRenderer(fake.path(), embeddedFor(platform, "x86_64"));
      try {
        renderer.render("<html><body>x</body></html>", options(), deadlineIn(20000L));
        String[] args = fake.recorded("args", fake.started().get(0)).trim().split(" ");
        assertEquals(2, args.length);
        assertEquals("--local-url-access=false", args[0]);
        assertTrue(args[1], args[1].endsWith("render-page.js"));
        String stdin = fake.recorded("stdin", fake.started().get(0));
        assertTrue(stdin.contains("\"allowedHosts\""));
        String shape = args[0] + "|" + JSON.readTree(stdin).path("options").path("allowedHosts");
        if (first == null) {
          first = shape;
        } else {
          assertEquals(first, shape);
        }
      } finally {
        renderer.close();
      }
    }
  }
}
