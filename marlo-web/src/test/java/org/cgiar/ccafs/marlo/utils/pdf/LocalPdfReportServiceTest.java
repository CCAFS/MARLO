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

import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests the local PDF report service (CHG-LPRE-T04, reworked in T13 for the PhantomJS renderer): the payload contract,
 * the HTML handed to PhantomJS, the execution model, the logging, the startup check and the HTTP response. PhantomJS
 * is replaced by a fake executable (src/test/resources/pdf/fake-phantomjs.sh), so no test needs the real engine or
 * the network; PhantomJsRenderingTest renders with the real one when PHANTOMJS_PATH is set.
 */
public class LocalPdfReportServiceTest {

  private static final String TEMPLATE_MARKER = "SECRET-TEMPLATE-MARKER";
  private static final String PAYLOAD_MARKER = "SECRET-PAYLOAD-MARKER";
  private static final ObjectMapper JSON = new ObjectMapper();

  @Rule
  public TemporaryFolder temp = new TemporaryFolder();

  private ExecutorService callers;
  private ListAppender<ILoggingEvent> appender;
  private ch.qos.logback.classic.Logger serviceLogger;
  private Level previousLevel;
  private final List<LocalPdfReportService> services = new ArrayList<LocalPdfReportService>();

  @Before
  public void setUp() {
    callers = Executors.newCachedThreadPool();
    serviceLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(LocalPdfReportService.class);
    previousLevel = serviceLogger.getLevel();
    serviceLogger.setLevel(Level.DEBUG);
    appender = new ListAppender<ILoggingEvent>();
    appender.start();
    serviceLogger.addAppender(appender);
  }

  @After
  public void tearDown() {
    for (LocalPdfReportService service : services) {
      service.shutdown();
    }
    callers.shutdownNow();
    try {
      // Late log lines of this test's threads must not land in the next test's appender.
      callers.awaitTermination(15, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    serviceLogger.detachAppender(appender);
    serviceLogger.setLevel(previousLevel);
  }

  private FakePhantomJs fake(String mode) throws IOException {
    return new FakePhantomJs(temp.newFolder().toPath(), mode);
  }

  /** A service on the fake binary, with the startup check's log line already dropped. */
  private LocalPdfReportService newService(FakePhantomJs fake) {
    LocalPdfReportService service = new LocalPdfReportService(fake.path());
    services.add(service);
    appender.list.clear();
    return service;
  }

  private LocalPdfReportService newService(String mode) throws IOException {
    return newService(fake(mode));
  }

  private static String resource(String path) throws IOException {
    try (InputStream in = LocalPdfReportServiceTest.class.getResourceAsStream(path)) {
      assertNotNull("missing test resource " + path, in);
      return new String(in.readAllBytes(), StandardCharsets.UTF_8);
    }
  }

  /** The payload of a production report with its {@code __TEMPLATE__} placeholder replaced by the template. */
  private static String fixturePayload(String name) throws IOException {
    ObjectNode root = (ObjectNode) JSON.readTree(resource("/pdf/payloads/" + name + "-payload.json"));
    ((ObjectNode) root.get("data")).put("templateData", resource("/pdf/templates/" + name + ".html"));
    return JSON.writeValueAsString(root);
  }

  /** A small payload around an inline template, for the tests that are not about the production templates. */
  private static String payload(String template, String dataJson) throws IOException {
    ObjectNode root = JSON.createObjectNode();
    ObjectNode data = root.putObject("data");
    data.put("templateData", template);
    data.set("data", JSON.readTree(dataJson));
    ObjectNode options = data.putObject("options");
    options.put("format", "A4");
    options.put("orientation", "portrait");
    options.putObject("header").put("height", "40mm");
    options.putObject("footer").put("height", "30mm");
    data.put("fileName", "inline-test.pdf");
    return JSON.writeValueAsString(root);
  }

  /** What the fake received on stdin for the render with this pid. */
  private static JsonNode sentTo(FakePhantomJs fake, String pid) throws IOException {
    return JSON.readTree(fake.recorded("stdin", pid));
  }

  private static String lastHtml(FakePhantomJs fake) throws IOException {
    List<String> started = fake.started();
    return sentTo(fake, started.get(started.size() - 1)).get("html").asText();
  }

  private List<String> messages(Level level) {
    List<String> found = new ArrayList<String>();
    for (ILoggingEvent event : new ArrayList<ILoggingEvent>(appender.list)) {
      if (event.getLevel() == level) {
        found.add(event.getFormattedMessage());
      }
    }
    return found;
  }

  private String everythingLogged() {
    StringBuilder all = new StringBuilder();
    for (ILoggingEvent event : new ArrayList<ILoggingEvent>(appender.list)) {
      all.append(event.getFormattedMessage()).append('\n');
      if (event.getThrowableProxy() != null) {
        all.append(ch.qos.logback.classic.spi.ThrowableProxyUtil.asString(event.getThrowableProxy())).append('\n');
      }
    }
    return all.toString();
  }

  // ---- production templates ------------------------------------------------------------------------------------

  @Test
  public void thePhantomJsInputForTheProductionTemplatesIsTheHtmlHandlebarsJsProduces() throws Exception {
    FakePhantomJs fake = fake("ok");
    LocalPdfReportService service = newService(fake);
    String[] names = {"oicr", "innovation", "project"};
    for (int i = 0; i < names.length; i++) {
      byte[] pdf = service.render(fixturePayload(names[i]), LocalPdfReportService.DOWNLOAD_DEADLINE_MS);
      assertTrue(new String(pdf, StandardCharsets.ISO_8859_1).startsWith("%PDF"));
      assertEquals(names[i] + ": the HTML PhantomJS gets must be byte-identical to Handlebars.js's (and the "
        + "sanitizer must leave it alone)", resource("/pdf/expected-html/" + names[i] + ".html"), lastHtml(fake));
      JsonNode options = sentTo(fake, fake.started().get(i)).get("options");
      assertEquals("the payload's own options reach html-pdf's script", "A4", options.get("format").asText());
    }
    List<String> infos = messages(Level.INFO);
    assertEquals(infos.toString(), 3, infos.size());
    for (String line : infos) {
      assertTrue(line, line.startsWith("PDF render file=") && line.contains("outcome=ok"));
    }
  }

  // ---- payload contract ----------------------------------------------------------------------------------------

  @Test
  public void invalidPayloadsAreRejectedWithoutQuotingThemAndWithoutStartingPhantomJs() throws Exception {
    FakePhantomJs fake = fake("ok");
    LocalPdfReportService service = newService(fake);
    String[] bad = {null, "", "   ", "not json " + PAYLOAD_MARKER, "[1,2]", "{\"pattern\":\"x\"}",
      "{\"data\":{\"data\":{}}}", "{\"data\":{\"templateData\":\"   \",\"data\":{}}}",
      "{\"data\":{\"templateData\":\"<p>x</p>\"}}",
      "{\"data\":{\"templateData\":\"<p>x</p>\",\"data\":[\"" + PAYLOAD_MARKER + "\"]}}",
      "{\"data\":{\"templateData\":\"<p>x</p>\",\"data\":\"" + PAYLOAD_MARKER + "\"}}",
      "{\"data\":{\"templateData\":7,\"data\":{}}}"};
    for (String payload : bad) {
      try {
        service.render(payload, 5000L);
        fail("accepted: " + payload);
      } catch (PdfRenderException e) {
        assertFalse(String.valueOf(e.getMessage()), String.valueOf(e.getMessage()).contains(PAYLOAD_MARKER));
      }
    }
    assertTrue("PhantomJS must not start for an invalid payload", fake.started().isEmpty());
    assertFalse(everythingLogged().contains(PAYLOAD_MARKER));
  }

  @Test
  public void aPayloadWithOnlyTheRequiredFieldsRendersAndSendsEmptyOptions() throws Exception {
    FakePhantomJs fake = fake("ok");
    LocalPdfReportService service = newService(fake);
    String minimal = "{\"data\":{\"templateData\":\"<html><body><p>Hello {{name}}</p></body></html>\","
      + "\"data\":{\"name\":\"World\"}}}";
    assertTrue(service.render(minimal, 20000L).length > 0);
    assertTrue(lastHtml(fake), lastHtml(fake).contains("<p>Hello World</p>"));
    JsonNode options = sentTo(fake, fake.started().get(0)).get("options");
    assertEquals(options.toString(), 3, options.size());
    assertTrue(options.has("filename") && options.has("allowedHosts") && options.get("timeout").asInt() > 1
      && options.get("timeout").asInt() <= 20500);
  }

  /** Null at every depth of data.data: the shape of a real report, where unset fields serialize as JSON null. */
  private static final String NULLS_EVERYWHERE = "{\"topLevel\":null,\"name\":\"Kept\","
    + "\"nested\":{\"missing\":null,\"inner\":{\"deepMissing\":null,\"text\":\"deep\"}},"
    + "\"items\":[null,\"first\",null],"
    + "\"rows\":[{\"label\":\"row-one\",\"gap\":null},{\"label\":null,\"gap\":null}],"
    + "\"grid\":[[null,\"cell\"],null]}";

  @Test
  public void aPayloadWithNullValuesAtEveryDepthRendersAPdf() throws Exception {
    assertTrue("the fixture must hold nulls at every depth",
      NULLS_EVERYWHERE.contains("\"topLevel\":null") && NULLS_EVERYWHERE.contains("\"missing\":null")
        && NULLS_EVERYWHERE.contains("[null,") && NULLS_EVERYWHERE.contains("\"gap\":null"));
    FakePhantomJs fake = fake("ok");
    LocalPdfReportService service = newService(fake);
    String template = "<html><body><p>{{name}}</p><p>{{nested.inner.text}}</p>"
      + "{{#each rows}}<p>{{label}}</p>{{/each}}{{#each items}}<p>{{this}}</p>{{/each}}</body></html>";
    assertTrue(service.render(payload(template, NULLS_EVERYWHERE), 20000L).length > 0);
    String html = lastHtml(fake);
    assertTrue(html, html.contains("<p>Kept</p>") && html.contains("<p>deep</p>") && html.contains("<p>row-one</p>"));
    List<String> infos = messages(Level.INFO);
    assertEquals(infos.toString(), 1, infos.size());
    assertTrue(infos.get(0), infos.get(0).contains("outcome=ok"));
    assertTrue(messages(Level.ERROR).isEmpty());
  }

  @Test
  public void theNullKeyWalkToleratesNullsAndStillFlagsAKeyNamedNullAtDepth() throws Exception {
    assertFalse(LocalPdfReportService.hasNullKey(JSON.readValue(NULLS_EVERYWHERE, Object.class)));
    assertTrue(LocalPdfReportService.hasNullKey(JSON.readValue(
      "{\"a\":null,\"b\":[null,{\"c\":[null,[{\"x\":null,\"null\":1}]]}]}", Object.class)));
    assertTrue(LocalPdfReportService.hasNullKey(JSON.readValue("{\"null\":null}", Object.class)));
    assertFalse(LocalPdfReportService.hasNullKey(null));
  }

  // ---- unexpected exceptions -----------------------------------------------------------------------------------

  /** A Handlebars whose compile fails with an unexpected RuntimeException, on the render path inside the pool. */
  private LocalPdfReportService serviceFailingWith(final RuntimeException failure) throws IOException {
    LocalPdfReportService service = new LocalPdfReportService(fake("ok").path(), new Handlebars() {

      @Override
      public Template compileInline(String input) {
        throw failure;
      }
    });
    services.add(service);
    appender.list.clear();
    return service;
  }

  @Test
  public void anUnexpectedRuntimeExceptionIsAFailedRenderThatAnswersTheGeneric500AndDoesNotPropagate()
    throws Exception {
    LocalPdfReportService service =
      serviceFailingWith(new IllegalStateException("boom " + PAYLOAD_MARKER, new NullPointerException("inner")));
    PdfResponses.Recorded recorded = new PdfResponses.Recorded();
    service.renderToResponse(payload("<html><body><p>ok</p></body></html>", "{}"), "boom.pdf",
      PdfResponses.response(recorded));
    assertEquals(Integer.valueOf(500), recorded.errorStatus);
    assertNull("no reason text", recorded.errorMessage);
    assertEquals(0, recorded.body.size());
    List<String> errors = messages(Level.ERROR);
    assertEquals(errors.toString(), 1, errors.size());
    assertTrue(errors.get(0), errors.get(0).contains("boom.pdf"));
    String logged = everythingLogged();
    assertTrue(logged, logged.contains("java.lang.IllegalStateException"));
    assertFalse("the exception message must not be logged", logged.contains(PAYLOAD_MARKER));
    List<String> infos = messages(Level.INFO);
    assertEquals(infos.toString(), 1, infos.size());
    assertTrue(infos.get(0), infos.get(0).contains("outcome=failed") && infos.get(0).contains("bytes=0"));
  }

  @Test
  public void anUnexpectedRuntimeExceptionMakesRenderThrowAPdfRenderException() throws Exception {
    LocalPdfReportService service = serviceFailingWith(new IllegalStateException("boom " + PAYLOAD_MARKER));
    try {
      service.render(payload("<html><body><p>ok</p></body></html>", "{}"), 20000L);
      fail("an unexpected exception did not fail the render");
    } catch (PdfRenderException e) {
      assertTrue(e.getMessage(), e.getMessage().contains("java.lang.IllegalStateException"));
      assertFalse(e.getMessage(), e.getMessage().contains(PAYLOAD_MARKER));
    }
    assertEquals(1, messages(Level.ERROR).size());
    assertFalse(everythingLogged().contains(PAYLOAD_MARKER));
  }

  // ---- logging -------------------------------------------------------------------------------------------------

  @Test
  public void oneWarnNamesTheUnsupportedConstructsAndANullKeyAndNothingElseIsQuoted() throws Exception {
    LocalPdfReportService service = newService("ok");
    String template = "<html><body>{{#each rows}}<p>{{../label}}</p>{{/each}}<p>" + TEMPLATE_MARKER
      + "</p></body></html>";
    String data = "{\"label\":\"L\",\"rows\":[1],\"nested\":{\"deep\":[{\"null\":\"" + PAYLOAD_MARKER + "\"}]}}";
    service.render(payload(template, data), 20000L);
    List<String> warnings = messages(Level.WARN);
    assertEquals(warnings.toString(), 1, warnings.size());
    assertTrue(warnings.get(0), warnings.get(0).contains("{{../label}}"));
    assertTrue(warnings.get(0), warnings.get(0).contains("payloadKeyNamedNull=true"));
    assertFalse(everythingLogged().contains(PAYLOAD_MARKER));
    assertFalse(everythingLogged().contains(TEMPLATE_MARKER));

    appender.list.clear();
    service.render(payload("<html><body>{{#each rows}}<p>{{label}}</p>{{/each}}</body></html>",
      "{\"rows\":[{\"label\":\"x\"}]}"), 20000L);
    assertTrue(messages(Level.WARN).isEmpty());
  }

  @Test
  public void everyRenderWritesOneInfoLineWithFileOutcomeBytesPagesElapsedAndTheCountsTheScriptReported()
    throws Exception {
    LocalPdfReportService service = newService("ok");
    byte[] pdf = service.render(payload("<html><body><p>x</p></body></html>", "{}"), 20000L);
    List<String> infos = messages(Level.INFO);
    assertEquals(infos.toString(), 1, infos.size());
    assertTrue(infos.get(0), infos.get(0).matches("PDF render file=inline-test\\.pdf outcome=ok bytes=" + pdf.length
      + " pages=1 elapsedMs=\\d+ blockedUris=2 failedFetches=1"));
  }

  @Test
  public void aFailedProcessLogsOneErrorWithItsStackAndExitCodeButNeitherTheHtmlNorTheRenderStderr()
    throws Exception {
    LocalPdfReportService service = newService("exit1");
    try {
      service.render(payload("<html><body><p>" + TEMPLATE_MARKER + "</p></body></html>", "{}"), 20000L);
      fail("a failing process rendered");
    } catch (PdfRenderException e) {
      assertTrue(e.getMessage(), e.getMessage().contains("exited with code 1"));
      assertFalse("stderr must not reach the exception", e.getMessage().contains("SECRET-STDERR-MARKER"));
    }
    List<String> errors = messages(Level.ERROR);
    assertEquals(errors.toString(), 1, errors.size());
    assertTrue(errors.get(0), errors.get(0).contains("inline-test.pdf") && errors.get(0).contains("phantomExit=1"));
    String logged = everythingLogged();
    assertTrue("the ERROR carries a stack trace", logged.contains("\tat ") || logged.contains("at org."));
    assertFalse("a render's stderr is the page script's and can quote page content",
      logged.contains(TEMPLATE_MARKER) || logged.contains("SECRET-STDERR-MARKER"));

    List<String> infos = messages(Level.INFO);
    assertTrue(infos.toString(), infos.size() == 1 && infos.get(0).contains("outcome=failed"));
    PdfResponses.Recorded recorded = new PdfResponses.Recorded();
    service.renderToResponse(anyPayload(), "x.pdf", PdfResponses.response(recorded));
    assertNull("no reason text in the response", recorded.errorMessage);
    assertEquals(0, recorded.body.size());
  }

  // ---- compile cache -------------------------------------------------------------------------------------------

  @Test
  public void compiledTemplatesAreCachedByContentAndBoundedToEight() throws Exception {
    LocalPdfReportService service = newService("ok");
    String same = payload("<html><body><p>same {{x}}</p></body></html>", "{\"x\":1}");
    service.render(same, 20000L);
    service.render(same, 20000L);
    assertEquals(1, service.cachedTemplateCount());
    for (int i = 0; i < 12; i++) {
      service.render(payload("<html><body><p>variant " + i + " {{x}}</p></body></html>", "{\"x\":1}"), 20000L);
    }
    assertEquals(LocalPdfReportService.TEMPLATE_CACHE_SIZE, service.cachedTemplateCount());
  }

  // ---- execution model -----------------------------------------------------------------------------------------

  private static String anyPayload() throws IOException {
    return payload("<html><body><p>x</p></body></html>", "{}");
  }

  @Test
  public void aRenderWhoseProcessHangsFailsAtTheCallersDeadlineAndTheProcessIsKilled() throws Exception {
    FakePhantomJs fake = fake("hang");
    LocalPdfReportService service = newService(fake);
    long start = System.nanoTime();
    try {
      service.render(anyPayload(), 1500L);
      fail("a hanging render returned");
    } catch (PdfRenderException e) {
      long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
      assertTrue("elapsed " + elapsed + " ms", Math.abs(elapsed - 1500L) <= 2000L);
      assertTrue(e.getMessage(), e.getMessage().contains("timed out"));
    }
    String pid = fake.started().get(0);
    assertTrue("PhantomJS was left running", FakePhantomJs.awaitDead(pid));
    assertTrue("its child was left running", FakePhantomJs.awaitDead(fake.childOf(pid)));
    assertEquals(0, service.queuedTaskCount());
    List<String> infos = messages(Level.INFO);
    assertEquals(infos.toString(), 1, infos.size());
    assertTrue(infos.get(0), infos.get(0).contains("outcome=timeout"));
    List<String> errors = messages(Level.ERROR);
    assertEquals("a timed-out render must log exactly one ERROR (AC-NF-003): " + errors, 1, errors.size());
    assertTrue(errors.get(0), errors.get(0).contains("timeout") && errors.get(0).contains("inline-test.pdf"));
  }

  @Test
  public void aQueuedRenderWhoseCallerGivesUpIsRemovedFromTheQueue() throws Exception {
    FakePhantomJs fake = fake("hang");
    final LocalPdfReportService service = newService(fake);
    final String payload = anyPayload();
    for (int i = 0; i < LocalPdfReportService.POOL_SIZE; i++) {
      callers.submit(new Callable<byte[]>() {

        @Override
        public byte[] call() throws Exception {
          return service.render(payload, 120000L);
        }
      });
    }
    fake.awaitStarted(LocalPdfReportService.POOL_SIZE);
    Future<byte[]> waiting = callers.submit(new Callable<byte[]>() {

      @Override
      public byte[] call() throws Exception {
        return service.render(payload, 1500L);
      }
    });
    awaitQueued(service, 1);
    try {
      waiting.get(10, TimeUnit.SECONDS);
      fail("a queued render returned");
    } catch (ExecutionException e) {
      assertTrue(String.valueOf(e.getCause()), e.getCause() instanceof PdfRenderException);
      assertTrue(e.getCause().getMessage(), e.getCause().getMessage().contains("timed out"));
    }
    assertEquals("the cancelled task must leave the queue", 0, service.queuedTaskCount());
    assertEquals("the queued render must never start PhantomJS", LocalPdfReportService.POOL_SIZE,
      fake.started().size());
  }

  @Test
  public void aRunningRenderThatEndsAfterItsCallerGaveUpIsWarnedWithItsTotalTime() throws Exception {
    LocalPdfReportService service = newService("hang");
    try {
      service.render(anyPayload(), 1000L);
      fail("a hanging render returned");
    } catch (PdfRenderException e) {
      assertTrue(e.getMessage(), e.getMessage().contains("timed out"));
    }
    // The caller's give-up interrupts the pool thread, which kills PhantomJS and then logs the late end.
    boolean late = false;
    for (int i = 0; i < 100 && !late; i++) {
      for (String warning : messages(Level.WARN)) {
        late |= warning.startsWith("PDF render finished after its caller gave up") && warning.contains("totalMs=");
      }
      if (!late) {
        Thread.sleep(100L);
      }
    }
    assertTrue(everythingLogged(), late);
  }

  @Test
  public void withThePoolAndTheQueueFullTheNextCallFailsAtOnce() throws Exception {
    FakePhantomJs fake = fake("hang");
    final LocalPdfReportService service = newService(fake);
    final String payload = anyPayload();
    int capacity = LocalPdfReportService.POOL_SIZE + LocalPdfReportService.QUEUE_CAPACITY;
    for (int i = 0; i < capacity; i++) {
      callers.submit(new Callable<byte[]>() {

        @Override
        public byte[] call() throws Exception {
          return service.render(payload, 120000L);
        }
      });
      if (i < LocalPdfReportService.POOL_SIZE) {
        fake.awaitStarted(i + 1);
      }
    }
    awaitQueued(service, LocalPdfReportService.QUEUE_CAPACITY);
    long start = System.nanoTime();
    try {
      service.render(payload, 120000L);
      fail("the tenth render was accepted");
    } catch (PdfRenderException e) {
      assertTrue(e.getMessage(), e.getMessage().contains("queue is full"));
    }
    assertTrue(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start) < 2000L);
    assertEquals(LocalPdfReportService.QUEUE_CAPACITY, service.queuedTaskCount());
    boolean rejected = false;
    for (String info : messages(Level.INFO)) {
      rejected |= info.contains("outcome=rejected");
    }
    assertTrue(rejected);
    List<String> errors = messages(Level.ERROR);
    assertEquals("a rejected render must log exactly one ERROR (AC-NF-003): " + errors, 1, errors.size());
    assertTrue(errors.get(0), errors.get(0).contains("rejected") && errors.get(0).contains("queue is full"));

    // Shutting the service down interrupts the running renders, which kill their processes.
    List<String> pids = fake.started();
    service.shutdown();
    for (String pid : pids) {
      assertTrue("PhantomJS " + pid + " survived the shutdown", FakePhantomJs.awaitDead(pid));
    }
  }

  @Test
  public void afterShutdownARenderIsRejectedWithAMessageThatIsNotQueueFull() throws Exception {
    LocalPdfReportService service = newService("ok");
    service.shutdown();
    try {
      service.render(anyPayload(), 5000L);
      fail("a render was accepted after shutdown");
    } catch (PdfRenderException e) {
      assertTrue(e.getMessage(), e.getMessage().contains("shut down"));
      assertFalse(e.getMessage(), e.getMessage().contains("queue is full"));
    }
    List<String> errors = messages(Level.ERROR);
    assertEquals(errors.toString(), 1, errors.size());
  }

  private static void awaitQueued(LocalPdfReportService service, int count) throws InterruptedException {
    for (int i = 0; i < 200 && service.queuedTaskCount() < count; i++) {
      Thread.sleep(50L);
    }
    assertEquals(count, service.queuedTaskCount());
  }

  // ---- the binary: startup check and fast failure (CHG-LPRE-NF-007) --------------------------------------------

  @Test
  public void theStartupCheckLogsTheVersionAtInfoWhenItIsTheExpectedOne() throws Exception {
    FakePhantomJs fake = fake("ok");
    services.add(new LocalPdfReportService(fake.path()));
    List<String> infos = messages(Level.INFO);
    assertEquals(infos.toString(), 1, infos.size());
    assertTrue(infos.get(0), infos.get(0).contains("version=2.1.1") && infos.get(0).contains(fake.path()));
    assertTrue(messages(Level.WARN).isEmpty());
  }

  @Test
  public void theStartupCheckWarnsWhenTheBinaryIsEmptyMissingNotExecutableSilentOrAnotherVersion() throws Exception {
    String[] unusable = {"", "   ", temp.getRoot().toPath().resolve("missing").toString(),
      temp.newFile("plain-file").getPath()};
    for (String path : unusable) {
      appender.list.clear();
      services.add(new LocalPdfReportService(path));
      assertEquals(path, 1, messages(Level.WARN).size());
      assertTrue(path, messages(Level.INFO).isEmpty());
    }
    String[] modes = {"version-other", "version-fail"};
    for (String mode : modes) {
      appender.list.clear();
      services.add(new LocalPdfReportService(fake(mode).path()));
      List<String> warnings = messages(Level.WARN);
      assertEquals(mode + ": " + warnings, 1, warnings.size());
      assertTrue(mode, messages(Level.INFO).isEmpty());
      if ("version-other".equals(mode)) {
        assertTrue(warnings.get(0), warnings.get(0).contains("1.9.8") && warnings.get(0).contains("2.1.1"));
      } else {
        assertTrue(warnings.get(0), warnings.get(0).contains("exitCode=3")
          && warnings.get(0).contains("stderr=Auto configuration failed first line")
          && !warnings.get(0).contains("SECOND-LINE-MARKER") && !warnings.get(0).contains("\u0001"));
      }
    }
  }

  @Test
  public void withoutAUsableBinaryARenderFailsAtOnceAndTheDownloadAnswersTheGeneric500() throws Exception {
    LocalPdfReportService service = new LocalPdfReportService(temp.getRoot().toPath().resolve("missing").toString());
    services.add(service);
    appender.list.clear();
    PdfResponses.Recorded recorded = new PdfResponses.Recorded();
    long start = System.nanoTime();
    service.renderToResponse(anyPayload(), "no-binary.pdf", PdfResponses.response(recorded));
    assertTrue(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start) < 2000L);
    assertEquals(Integer.valueOf(500), recorded.errorStatus);
    assertNull("no reason text", recorded.errorMessage);
    assertEquals(0, recorded.body.size());
    List<String> errors = messages(Level.ERROR);
    assertEquals(errors.toString(), 1, errors.size());
    assertTrue(errors.get(0), errors.get(0).contains("no-binary.pdf"));
    try {
      service.render(anyPayload(), 20000L);
      fail("rendered without a binary");
    } catch (PdfRenderException e) {
      assertEquals("PhantomJS binary is not available", e.getMessage());
    }
  }

  @Test
  public void theContainerPathReadsTheBinaryFromTheConfiguration() throws Exception {
    FakePhantomJs fake = fake("ok");
    APConfig config = new APConfig();
    Field path = APConfig.class.getDeclaredField("PDF_PHANTOMJS_PATH");
    path.setAccessible(true);
    path.set(config, "  " + fake.path() + "  ");
    assertEquals(fake.path(), config.getPdfPhantomjsPath());

    LocalPdfReportService service =
      new LocalPdfReportService(new EmbeddedPhantomJs("Mac OS X", "aarch64", "none", null));
    services.add(service);
    appender.list.clear();
    try {
      service.render(anyPayload(), 5000L);
      fail("rendered before the configuration arrived");
    } catch (PdfRenderException e) {
      assertEquals("PhantomJS binary is not available", e.getMessage());
    }
    service.setConfig(config);
    service.initFromConfig();
    List<String> infos = messages(Level.INFO);
    assertTrue(infos.toString(), infos.get(infos.size() - 1).contains("version=2.1.1"));
    assertTrue(service.render(anyPayload(), 20000L).length > 0);

    assertEquals("an unset property reads as empty", "", new APConfig().getPdfPhantomjsPath());
  }

  // ---- response ------------------------------------------------------------------------------------------------

  @Test
  public void renderToResponseWritesAnInlinePdfWithTheMicroserviceHeaders() throws Exception {
    LocalPdfReportService service = newService("ok");
    PdfResponses.Recorded recorded = new PdfResponses.Recorded();
    service.renderToResponse(anyPayload(), "my-report.pdf", PdfResponses.response(recorded));
    assertEquals("application/pdf", recorded.contentType);
    assertEquals("inline; filename=\"my-report.pdf\"", recorded.disposition);
    assertEquals(recorded.body.size(), recorded.contentLength);
    assertEquals(null, recorded.errorStatus);
    assertTrue(new String(recorded.body.toByteArray(), StandardCharsets.ISO_8859_1).startsWith("%PDF"));
  }

  @Test
  public void aTemplateThatFailsToCompileAnswers500WithTheGenericMessageOnly() throws Exception {
    FakePhantomJs fake = fake("ok");
    LocalPdfReportService service = newService(fake);
    PdfResponses.Recorded recorded = new PdfResponses.Recorded();
    String broken = "<html><body><p>" + TEMPLATE_MARKER + "</p>{{#each rows}}<p>" + TEMPLATE_MARKER
      + "</body></html>";
    service.renderToResponse(payload(broken, "{\"secret\":\"" + PAYLOAD_MARKER + "\"}"), "broken.pdf",
      PdfResponses.response(recorded));
    assertEquals(Integer.valueOf(500), recorded.errorStatus);
    assertNull("no reason text", recorded.errorMessage);
    assertEquals(0, recorded.body.size());
    assertEquals(null, recorded.contentType);
    assertTrue("PhantomJS must not start for a template that does not compile", fake.started().isEmpty());
    String logged = everythingLogged();
    assertFalse(logged, logged.contains(TEMPLATE_MARKER));
    assertFalse(logged, logged.contains(PAYLOAD_MARKER));
    List<String> errors = messages(Level.ERROR);
    assertEquals(errors.toString(), 1, errors.size());
    assertTrue(errors.get(0), errors.get(0).contains("broken.pdf"));
    assertTrue("the ERROR carries a stack trace", logged.contains("\tat ") || logged.contains("at org."));
  }

  @Test
  public void anInvalidPayloadAnswersTheGeneric500() throws Exception {
    LocalPdfReportService service = newService("ok");
    PdfResponses.Recorded recorded = new PdfResponses.Recorded();
    service.renderToResponse("{\"data\":{\"data\":{}}}", "x.pdf", PdfResponses.response(recorded));
    assertEquals(Integer.valueOf(500), recorded.errorStatus);
    assertNull("no reason text", recorded.errorMessage);
  }

  @Test
  public void aClientAbortWhileWritingIsLoggedAtDebugAndAnyOtherWriteFailurePropagates() throws Exception {
    LocalPdfReportService service = newService("ok");
    String small = anyPayload();
    PdfResponses.Recorded abort = new PdfResponses.Recorded();
    abort.failOnWrite = new PdfResponses.ClientAbortException();
    service.renderToResponse(small, "a.pdf", PdfResponses.response(abort));
    assertEquals(null, abort.errorStatus);
    boolean debug = false;
    for (String line : messages(Level.DEBUG)) {
      debug |= line.startsWith("Client aborted the PDF download");
    }
    assertTrue(debug);

    PdfResponses.Recorded broken = new PdfResponses.Recorded();
    broken.failOnWrite = new IOException("disk full");
    try {
      service.renderToResponse(small, "b.pdf", PdfResponses.response(broken));
      fail("a write failure that is not a client abort was swallowed");
    } catch (IOException e) {
      assertEquals("disk full", e.getMessage());
    }
  }

  @Test
  public void theFileNameCannotBreakOutOfTheContentDispositionHeader() throws Exception {
    LocalPdfReportService service = newService("ok");
    PdfResponses.Recorded recorded = new PdfResponses.Recorded();
    service.renderToResponse(anyPayload(), "a\"\r\nSet-Cookie: x=1.pdf", PdfResponses.response(recorded));
    assertEquals("inline; filename=\"a___Set-Cookie: x=1.pdf\"", recorded.disposition);
  }
}
