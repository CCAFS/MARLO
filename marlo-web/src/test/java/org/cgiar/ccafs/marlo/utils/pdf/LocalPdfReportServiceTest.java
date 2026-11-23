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

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Base64;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.LongSupplier;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests the local PDF report service (CHG-LPRE-T04): the three production templates rendered through the real
 * renderer, the payload contract, the execution model and the HTTP response. No test touches the network: every
 * resource request goes to a connection factory double, which serves captured copies of the images the templates
 * load (src/test/resources/pdf/images), or blocks, or only records.
 */
public class LocalPdfReportServiceTest {

  private static final String HOST = "marlo-pdf-resources-dev.s3.us-east-1.amazonaws.com";
  private static final String TEMPLATE_MARKER = "SECRET-TEMPLATE-MARKER";
  private static final String PAYLOAD_MARKER = "SECRET-PAYLOAD-MARKER";
  private static final String URI_MARKER = "SECRET-URI-MARKER";
  private static final String DATA_MARKER = "SECRETDATAMARKER";
  private static final String PNG_1X1 = "data:image/png;base64,"
    + "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR4nGP4z8DwHwAFAAH/iZk9HQAAAABJRU5ErkJggg==";
  private static final ObjectMapper JSON = new ObjectMapper();
  private static final File OUT_DIR = new File("target/local-pdf-report");

  /** A connection that serves a canned response and never touches the network. */
  private static final class FakeConnection extends HttpURLConnection {

    private final byte[] body;
    private final int status;

    FakeConnection(URI uri, int status, byte[] body) throws IOException {
      super(uri.toURL());
      this.status = status;
      this.body = body;
    }

    @Override
    public void connect() {
    }

    @Override
    public void disconnect() {
    }

    @Override
    public int getContentLength() {
      return body.length;
    }

    @Override
    public InputStream getInputStream() {
      return new ByteArrayInputStream(body);
    }

    @Override
    public int getResponseCode() {
      return status;
    }

    @Override
    public boolean usingProxy() {
      return false;
    }
  }

  /**
   * The connection factory double: records every URI the loader opens, and serves the captured image of the URI's
   * path, a 404 for an unknown one, or blocks until released.
   */
  private static final class Network implements PdfResourceLoader.ConnectionFactory {

    private final CountDownLatch release = new CountDownLatch(1);
    private final boolean blocking;
    private final List<URI> opened = new CopyOnWriteArrayList<URI>();
    private final List<URI> missing = new CopyOnWriteArrayList<URI>();

    Network(boolean blocking) {
      this.blocking = blocking;
    }

    @Override
    public HttpURLConnection open(URI uri) throws IOException {
      opened.add(uri);
      if (blocking) {
        try {
          release.await();
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          throw new InterruptedIOException("released by interrupt");
        }
      }
      try (InputStream in = LocalPdfReportServiceTest.class
        .getResourceAsStream("/pdf/images/" + uri.getHost() + uri.getPath())) {
        if (in == null) {
          missing.add(uri);
          return new FakeConnection(uri, 404, new byte[0]);
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int read = in.read(buffer);
        while (read >= 0) {
          out.write(buffer, 0, read);
          read = in.read(buffer);
        }
        return new FakeConnection(uri, 200, out.toByteArray());
      }
    }
  }

  /** What the servlet response double saw. */
  private static final class Recorded {

    private final ByteArrayOutputStream body = new ByteArrayOutputStream();
    private boolean committed;
    private String contentType;
    private String disposition;
    private Integer errorStatus;
    private String errorMessage;
    private IOException failOnWrite;
    private int contentLength = -1;
  }

  private static final class ClientAbortException extends IOException {

    private static final long serialVersionUID = 1L;

    ClientAbortException() {
      super("peer closed");
    }
  }

  private ExecutorService callers;
  private ListAppender<ILoggingEvent> appender;
  private ch.qos.logback.classic.Logger serviceLogger;
  private Level previousLevel;
  private final List<LocalPdfReportService> services = new ArrayList<LocalPdfReportService>();
  private final List<Network> networks = new ArrayList<Network>();

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
    for (Network network : networks) {
      network.release.countDown();
    }
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

  private LocalPdfReportService newService(Network network) {
    networks.add(network);
    LocalPdfReportService service = new LocalPdfReportService(new PdfResourceLoader(network, new LongSupplier() {

      @Override
      public long getAsLong() {
        return System.nanoTime();
      }
    }));
    services.add(service);
    return service;
  }

  private static String resource(String path) throws IOException {
    try (InputStream in = LocalPdfReportServiceTest.class.getResourceAsStream(path)) {
      assertNotNull("missing test resource " + path, in);
      return new String(readAll(in), StandardCharsets.UTF_8);
    }
  }

  private static byte[] readAll(InputStream in) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    byte[] buffer = new byte[8192];
    int read = in.read(buffer);
    while (read >= 0) {
      out.write(buffer, 0, read);
      read = in.read(buffer);
    }
    return out.toByteArray();
  }

  /** The payload of a production report with its {@code __TEMPLATE__} placeholder replaced by the template. */
  private static String fixturePayload(String name) throws IOException {
    return payloadWithTemplate(resource("/pdf/payloads/" + name + "-payload.json"),
      resource("/pdf/templates/" + name + ".html"));
  }

  private static String payloadWithTemplate(String payloadJson, String template) throws IOException {
    ObjectNode root = (ObjectNode) JSON.readTree(payloadJson);
    ((ObjectNode) root.get("data")).put("templateData", template);
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

  private static PDDocument open(byte[] pdf) throws IOException {
    assertTrue("not a PDF", pdf.length > 4 && pdf[0] == '%' && pdf[1] == 'P' && pdf[2] == 'D' && pdf[3] == 'F');
    return Loader.loadPDF(pdf);
  }

  private static void collectFonts(PDResources resources, Set<String> names, Set<Object> seen) throws IOException {
    if (resources == null || !seen.add(resources.getCOSObject())) {
      return;
    }
    for (COSName fontName : resources.getFontNames()) {
      PDFont font = resources.getFont(fontName);
      String base = font.getName();
      names.add(base.contains("+") ? base.substring(base.indexOf('+') + 1) : base);
    }
    for (COSName xName : resources.getXObjectNames()) {
      PDXObject xObject = resources.getXObject(xName);
      if (xObject instanceof PDFormXObject) {
        collectFonts(((PDFormXObject) xObject).getResources(), names, seen);
      }
    }
  }

  private static Set<String> fontsOf(PDDocument document) throws IOException {
    Set<String> names = new TreeSet<String>();
    Set<Object> seen = Collections.newSetFromMap(new IdentityHashMap<Object, Boolean>());
    for (PDPage page : document.getPages()) {
      collectFonts(page.getResources(), names, seen);
    }
    return names;
  }

  private static int countImages(PDResources resources, Set<Object> seen) throws IOException {
    if (resources == null || !seen.add(resources.getCOSObject())) {
      return 0;
    }
    int count = 0;
    for (COSName xName : resources.getXObjectNames()) {
      PDXObject xObject = resources.getXObject(xName);
      if (xObject instanceof PDImageXObject) {
        count++;
      } else if (xObject instanceof PDFormXObject) {
        count += countImages(((PDFormXObject) xObject).getResources(), seen);
      }
    }
    return count;
  }

  private static int imagesOf(PDDocument document) throws IOException {
    int count = 0;
    Set<Object> seen = Collections.newSetFromMap(new IdentityHashMap<Object, Boolean>());
    for (PDPage page : document.getPages()) {
      count += countImages(page.getResources(), seen);
    }
    return count;
  }

  /** Text of one page that sits in the bottom 30 mm band, whitespace removed, and the text above it. */
  private static final class BandText extends PDFTextStripper {

    private static final float BAND_POINTS = 30f / 25.4f * 72f;
    private final StringBuilder band = new StringBuilder();
    private final StringBuilder above = new StringBuilder();

    BandText() throws IOException {
      super();
    }

    @Override
    protected void writeString(String text, List<TextPosition> positions) {
      float pageHeight = getCurrentPage().getMediaBox().getHeight();
      for (TextPosition position : positions) {
        String unicode = java.text.Normalizer.normalize(position.getUnicode(), java.text.Normalizer.Form.NFKC)
          .replaceAll("\\s", "");
        if (position.getYDirAdj() > pageHeight - BAND_POINTS) {
          band.append(unicode);
        } else {
          above.append(unicode);
        }
      }
    }
  }

  private static BandText bandOf(PDDocument document, int pageNumber) throws IOException {
    BandText stripper = new BandText();
    stripper.setStartPage(pageNumber);
    stripper.setEndPage(pageNumber);
    stripper.getText(document);
    return stripper;
  }

  private static String squash(String text) {
    return text.replaceAll("\\s", "");
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

  private static HttpServletResponse response(final Recorded recorded) {
    final ServletOutputStream stream = new ServletOutputStream() {

      public boolean isReady() {
        return true;
      }

      public void setWriteListener(WriteListener listener) {
      }

      @Override
      public void write(int b) throws IOException {
        if (recorded.failOnWrite != null) {
          throw recorded.failOnWrite;
        }
        recorded.body.write(b);
      }

      @Override
      public void write(byte[] b, int off, int len) throws IOException {
        if (recorded.failOnWrite != null) {
          throw recorded.failOnWrite;
        }
        recorded.body.write(b, off, len);
      }
    };
    return (HttpServletResponse) Proxy.newProxyInstance(LocalPdfReportServiceTest.class.getClassLoader(),
      new Class<?>[] {HttpServletResponse.class}, new InvocationHandler() {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
          String name = method.getName();
          if ("setContentType".equals(name)) {
            recorded.contentType = (String) args[0];
          } else if ("setHeader".equals(name) && "Content-Disposition".equals(args[0])) {
            recorded.disposition = (String) args[1];
          } else if ("setContentLength".equals(name)) {
            recorded.contentLength = (Integer) args[0];
          } else if ("sendError".equals(name)) {
            recorded.errorStatus = (Integer) args[0];
            recorded.errorMessage = args.length > 1 ? (String) args[1] : null;
            recorded.committed = true;
          } else if ("isCommitted".equals(name)) {
            return recorded.committed;
          } else if ("getOutputStream".equals(name)) {
            return stream;
          }
          if (method.getReturnType() == boolean.class) {
            return false;
          }
          return null;
        }
      });
  }

  /** Collects what the renderer's java.util.logging loggers deliver, at the levels the service configured. */
  private static final class RendererLog extends java.util.logging.Handler {

    private final List<java.util.logging.LogRecord> records = new CopyOnWriteArrayList<java.util.logging.LogRecord>();
    private final List<java.util.logging.Logger> attached = new ArrayList<java.util.logging.Logger>();

    /** Attach after the service is built: its constructor resets the handlers of these loggers. */
    static RendererLog attach() {
      RendererLog log = new RendererLog();
      for (String name : com.openhtmltopdf.util.XRLog.listRegisteredLoggers()) {
        java.util.logging.Logger logger = java.util.logging.Logger.getLogger(name);
        logger.addHandler(log);
        log.attached.add(logger);
      }
      return log;
    }

    void detach() {
      for (java.util.logging.Logger logger : attached) {
        logger.removeHandler(this);
      }
    }

    @Override
    public void publish(java.util.logging.LogRecord record) {
      records.add(record);
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }

    /** Number of records at the level or above, from any of the attached loggers. */
    int atOrAbove(java.util.logging.Level level) {
      int count = 0;
      for (java.util.logging.LogRecord record : records) {
        if (record.getLevel().intValue() >= level.intValue()) {
          count++;
        }
      }
      return count;
    }

    /** One line per record: logger, level, formatted message. */
    String describe() {
      StringBuilder text = new StringBuilder();
      java.util.logging.SimpleFormatter formatter = new java.util.logging.SimpleFormatter();
      for (java.util.logging.LogRecord record : records) {
        text.append(record.getLoggerName()).append(" | ").append(record.getLevel()).append(" | ")
          .append(formatter.formatMessage(record));
        if (record.getThrown() != null) {
          text.append(" | thrown=").append(record.getThrown());
        }
        text.append('\n');
      }
      return text.toString();
    }
  }

  /** A valid PNG data URI of random noise, which does not compress: about {@code side * side * 4} characters. */
  private static String noisePng(int side) throws IOException {
    BufferedImage image = new BufferedImage(side, side, BufferedImage.TYPE_INT_RGB);
    Random random = new Random(42L);
    for (int y = 0; y < side; y++) {
      for (int x = 0; x < side; x++) {
        image.setRGB(x, y, random.nextInt(0x1000000));
      }
    }
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    assertTrue(ImageIO.write(image, "png", out));
    return "data:image/png;base64," + Base64.getEncoder().encodeToString(out.toByteArray());
  }

  // ---- production templates ------------------------------------------------------------------------------------

  @Test
  public void productionTemplatesRenderWithTheProductionPageCountsAndOnlyTheBundledFonts() throws Exception {
    Network network = new Network(false);
    LocalPdfReportService service = newService(network);
    String[] names = {"oicr", "innovation", "project"};
    int[] pages = {5, 6, 7};
    // Taken from the fixtures and checked against the extracted text: the report title and two section headings
    // each. Headings styled text-transform: uppercase in the project template extract as upper case.
    String[][] expectedText = {
      {"Climate-smart advisory services", "SECTION 1: At a glance", "SECTION 2: The story and impact"},
      {"Sample", "SECTION: At a glance", "SECTION: Bundle composition"},
      {"AICCRA Cluster Summary", "KEY BADGES", "CLUSTER PARTNERS"}};
    Set<String> dejaVu = new TreeSet<String>();
    dejaVu.add("DejaVuSans");
    dejaVu.add("DejaVuSans-Bold");
    OUT_DIR.mkdirs();
    byte[][] rendered = new byte[names.length][];
    for (int i = 0; i < names.length; i++) {
      rendered[i] = service.render(fixturePayload(names[i]), LocalPdfReportService.DOWNLOAD_DEADLINE_MS);
      Files.write(new File(OUT_DIR, names[i] + ".pdf").toPath(), rendered[i]);
    }
    assertTrue("images the fixtures need but src/test/resources/pdf/images lacks: " + network.missing,
      network.missing.isEmpty());
    for (int i = 0; i < names.length; i++) {
      try (PDDocument document = open(rendered[i])) {
        assertEquals(names[i] + " page count", pages[i], document.getNumberOfPages());
        assertEquals(names[i] + " fonts (a fallback font would make the page count meaningless)", dejaVu,
          fontsOf(document));
        String text = new PDFTextStripper().getText(document);
        for (String expected : expectedText[i]) {
          assertTrue(names[i] + " text lacks '" + expected + "'", text.contains(expected));
        }
      }
    }
    List<String> infos = messages(Level.INFO);
    assertEquals(3, infos.size());
    for (String line : infos) {
      assertTrue(line, line.startsWith("PDF render file="));
      assertTrue(line, line.contains("outcome=ok"));
    }
  }

  @Test
  public void firstPageFooterVariantIsOnPageOneAndTheDefaultOneOnTheOthers() throws Exception {
    LocalPdfReportService service = newService(new Network(false));
    String firstOnly = "atagiventimeduringaspecificphase";
    String defaultOnly = "registeredintotheMARLOAICCRASystem";
    try (PDDocument document = open(service.render(fixturePayload("innovation"), 50000L))) {
      assertEquals(6, document.getNumberOfPages());
      BandText first = bandOf(document, 1);
      assertTrue("page 1 footer band: " + first.band, first.band.toString().contains(firstOnly));
      assertFalse(first.band.toString().contains(defaultOnly));
      assertFalse("the footer sentence must not sit above the 30 mm band",
        first.above.toString().contains(firstOnly));
      for (int page = 2; page <= document.getNumberOfPages(); page++) {
        BandText other = bandOf(document, page);
        assertTrue("page " + page + " footer band: " + other.band, other.band.toString().contains(defaultOnly));
        assertFalse(other.band.toString().contains(firstOnly));
        assertFalse(other.above.toString().contains(defaultOnly));
      }
    }
  }

  // ---- payload contract ----------------------------------------------------------------------------------------

  @Test
  public void invalidPayloadsAreRejectedWithoutQuotingThem() throws Exception {
    LocalPdfReportService service = newService(new Network(false));
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
    assertFalse(everythingLogged().contains(PAYLOAD_MARKER));
  }

  @Test
  public void aPayloadWithOnlyTheRequiredFieldsRenders() throws Exception {
    LocalPdfReportService service = newService(new Network(false));
    String minimal = "{\"data\":{\"templateData\":\"<html><body><p>Hello {{name}}</p></body></html>\","
      + "\"data\":{\"name\":\"World\"}}}";
    try (PDDocument document = open(service.render(minimal, 20000L))) {
      assertEquals(1, document.getNumberOfPages());
      assertTrue(squash(new PDFTextStripper().getText(document)).contains("HelloWorld"));
    }
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
    LocalPdfReportService service = newService(new Network(false));
    String template = "<html><body><p>{{name}}</p><p>{{nested.inner.text}}</p>"
      + "{{#each rows}}<p>{{label}}</p>{{/each}}{{#each items}}<p>{{this}}</p>{{/each}}</body></html>";
    try (PDDocument document = open(service.render(payload(template, NULLS_EVERYWHERE), 20000L))) {
      assertEquals(1, document.getNumberOfPages());
      String text = squash(new PDFTextStripper().getText(document));
      assertTrue(text, text.contains("Kept") && text.contains("deep") && text.contains("row-one"));
    }
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

  /** A loader whose session cannot be opened: an unexpected RuntimeException on the render path, before the pool. */
  private LocalPdfReportService serviceFailingWith(final RuntimeException failure) {
    LocalPdfReportService service = new LocalPdfReportService(new PdfResourceLoader() {

      @Override
      PdfResourceLoader.Session newSession() {
        throw failure;
      }
    });
    services.add(service);
    return service;
  }

  @Test
  public void anUnexpectedRuntimeExceptionIsAFailedRenderThatAnswersTheGeneric500AndDoesNotPropagate()
    throws Exception {
    LocalPdfReportService service =
      serviceFailingWith(new IllegalStateException("boom " + PAYLOAD_MARKER, new NullPointerException("inner")));
    Recorded recorded = new Recorded();
    service.renderToResponse(payload("<html><body><p>ok</p></body></html>", "{}"), "boom.pdf", response(recorded));
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
    LocalPdfReportService service = newService(new Network(false));
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
  public void everyRenderWritesOneInfoLineWithFileOutcomeBytesPagesElapsedAndBlockedCount() throws Exception {
    LocalPdfReportService service = newService(new Network(false));
    String template = "<html><body><img src=\"file:///etc/passwd\"/><p>x</p></body></html>";
    byte[] pdf = service.render(payload(template, "{}"), 20000L);
    List<String> infos = messages(Level.INFO);
    assertEquals(infos.toString(), 1, infos.size());
    assertTrue(infos.get(0), infos.get(0).matches(
      "PDF render file=inline-test\\.pdf outcome=ok bytes=" + pdf.length
        + " pages=1 elapsedMs=\\d+ blockedUris=[1-9]\\d* failedFetches=0"));
  }

  // ---- compile cache -------------------------------------------------------------------------------------------

  @Test
  public void compiledTemplatesAreCachedByContentAndBoundedToEight() throws Exception {
    LocalPdfReportService service = newService(new Network(false));
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

  private static String blockingTemplate() {
    return "<html><body><img src=\"https://" + HOST + "/slow.png\"/><p>x</p></body></html>";
  }

  @Test
  public void aRenderWhoseFetchBlocksForeverFailsAtTheSubmissionDeadline() throws Exception {
    Network network = new Network(true);
    LocalPdfReportService service = newService(network);
    long start = System.nanoTime();
    try {
      service.render(payload(blockingTemplate(), "{}"), LocalPdfReportService.SUBMIT_DEADLINE_MS);
      fail("a blocked render returned");
    } catch (PdfRenderException e) {
      long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
      assertTrue("elapsed " + elapsed + " ms", Math.abs(elapsed - 30000L) <= 2000L);
      assertTrue(e.getMessage(), e.getMessage().contains("timed out"));
    }
    assertEquals(1, network.opened.size());
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
    Network network = new Network(true);
    final LocalPdfReportService service = newService(network);
    final String blocked = payload(blockingTemplate(), "{}");
    for (int i = 0; i < LocalPdfReportService.POOL_SIZE; i++) {
      callers.submit(new Callable<byte[]>() {

        @Override
        public byte[] call() throws Exception {
          return service.render(blocked, 120000L);
        }
      });
    }
    awaitOpened(network, LocalPdfReportService.POOL_SIZE);
    Future<byte[]> waiting = callers.submit(new Callable<byte[]>() {

      @Override
      public byte[] call() throws Exception {
        return service.render(blocked, 1500L);
      }
    });
    awaitQueued(service, 1);
    try {
      waiting.get(10, TimeUnit.SECONDS);
      fail("a queued render returned");
    } catch (java.util.concurrent.ExecutionException e) {
      assertTrue(String.valueOf(e.getCause()), e.getCause() instanceof PdfRenderException);
      assertTrue(e.getCause().getMessage(), e.getCause().getMessage().contains("timed out"));
    }
    assertEquals("the cancelled task must leave the queue", 0, service.queuedTaskCount());
  }

  @Test
  public void aRunningRenderThatFinishesAfterItsCallerGaveUpIsWarnedWithItsTotalTime() throws Exception {
    Network network = new Network(true);
    LocalPdfReportService service = newService(network);
    try {
      service.render(payload(blockingTemplate(), "{}"), 1000L);
      fail("a blocked render returned");
    } catch (PdfRenderException e) {
      assertTrue(e.getMessage(), e.getMessage().contains("timed out"));
    }
    // Layout cannot be interrupted: the interrupt releases the double, the render completes on its own thread.
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
    Network network = new Network(true);
    final LocalPdfReportService service = newService(network);
    final String blocked = payload(blockingTemplate(), "{}");
    int capacity = LocalPdfReportService.POOL_SIZE + LocalPdfReportService.QUEUE_CAPACITY;
    for (int i = 0; i < capacity; i++) {
      callers.submit(new Callable<byte[]>() {

        @Override
        public byte[] call() throws Exception {
          return service.render(blocked, 120000L);
        }
      });
      if (i < LocalPdfReportService.POOL_SIZE) {
        awaitOpened(network, i + 1);
      }
    }
    awaitQueued(service, LocalPdfReportService.QUEUE_CAPACITY);
    long start = System.nanoTime();
    try {
      service.render(blocked, 120000L);
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
  }

  @Test
  public void afterShutdownARenderIsRejectedWithAMessageThatIsNotQueueFull() throws Exception {
    LocalPdfReportService service = newService(new Network(false));
    service.shutdown();
    try {
      service.render(payload("<html><body><p>x</p></body></html>", "{}"), 5000L);
      fail("a render was accepted after shutdown");
    } catch (PdfRenderException e) {
      assertTrue(e.getMessage(), e.getMessage().contains("shut down"));
      assertFalse(e.getMessage(), e.getMessage().contains("queue is full"));
    }
    List<String> errors = messages(Level.ERROR);
    assertEquals(errors.toString(), 1, errors.size());
  }

  private static void awaitOpened(Network network, int count) throws InterruptedException {
    for (int i = 0; i < 200 && network.opened.size() < count; i++) {
      Thread.sleep(50L);
    }
    assertTrue("opened " + network.opened.size(), network.opened.size() >= count);
  }

  private static void awaitQueued(LocalPdfReportService service, int count) throws InterruptedException {
    for (int i = 0; i < 200 && service.queuedTaskCount() < count; i++) {
      Thread.sleep(50L);
    }
    assertEquals(count, service.queuedTaskCount());
  }

  // ---- security ------------------------------------------------------------------------------------------------

  @Test
  public void resourcesOutsideTheAllowlistNeverReachTheNetworkThroughTheRealRenderer() throws Exception {
    Network network = new Network(false);
    LocalPdfReportService service = newService(network);
    RendererLog rendererLog = RendererLog.attach();
    String template = "<html><head><link rel=\"stylesheet\" href=\"http://169.254.169.254/" + URI_MARKER + ".css\"/>"
      + "<style>@import url(\"http://169.254.169.254/x-" + URI_MARKER + ".css\"); "
      + "p { background: url(\"file:///etc/passwd-" + URI_MARKER + "\"); }"
      + "</style></head><body><img src=\"file:///etc/passwd\"/>"
      + "<img src=\"https://evil.example/" + URI_MARKER + ".png\"/><img src=\"http://" + HOST + "/a.png\"/>"
      + "<img src=\"https://169.254.169.254/latest/meta-data\"/><img src=\"jar:file:/x.jar!/a.png\"/>"
      + "<img src=\"ftp://" + HOST + "/a.png\"/><img src=\"//evil.example/a.png\"/><img src=\"relative.png\"/>"
      + "<p>text</p></body></html>";
    try (PDDocument document = open(service.render(payload(template, "{}"), 30000L))) {
      assertEquals(1, document.getNumberOfPages());
    } finally {
      rendererLog.detach();
    }
    assertEquals("connections opened for denied resources: " + network.opened, 0, network.opened.size());
    String info = messages(Level.INFO).get(0);
    assertFalse(info, info.contains("blockedUris=0"));
    String delivered = rendererLog.describe();
    for (String quoted : new String[] {URI_MARKER, "evil.example", "169.254.169.254", "/etc/passwd"}) {
      assertFalse("the renderer's own log quotes '" + quoted + "':\n" + delivered, delivered.contains(quoted));
    }
    assertEquals("a WARNING or above from an OpenHTMLtoPDF logger:\n" + delivered, 0,
      rendererLog.atOrAbove(java.util.logging.Level.WARNING));
  }

  @Test
  public void resourcesThatFailToLoadNeverPutTheirUriInTheRenderersOwnLog() throws Exception {
    Network network = new Network(false);
    LocalPdfReportService service = newService(network);
    RendererLog rendererLog = RendererLog.attach();
    String template = "<html><head><link rel=\"stylesheet\" href=\"https://" + HOST + "/" + URI_MARKER + ".css\"/>"
      + "</head><body><img src=\"https://" + HOST + "/" + URI_MARKER + ".png\"/>"
      + "<img src=\"https://" + HOST + ":99999/" + URI_MARKER + ".png\"/>"
      + "<img src=\"data:image/png;base64," + DATA_MARKER + "\"/>"
      + "<div style=\"background-image: url(data:image/png;base64," + DATA_MARKER + ")\">x</div>"
      + "<img src=\"{{{oversized}}}\"/><a href=\"#" + URI_MARKER + "\">anchor</a>"
      + "<a href=\"http://a b/" + URI_MARKER + "\">spaced</a><a href=\"javascript:" + URI_MARKER + "\">script</a>"
      + "<p>text</p></body></html>";
    String oversized = noisePng(760).replace("data:image/png;base64,", "data:image/png;base64," + DATA_MARKER);
    String data = JSON.createObjectNode().put("oversized", oversized).toString();
    try (PDDocument document = open(service.render(payload(template, data), 30000L))) {
      assertEquals(1, document.getNumberOfPages());
    } finally {
      rendererLog.detach();
    }
    String delivered = rendererLog.describe();
    for (String quoted : new String[] {URI_MARKER, DATA_MARKER, "base64,"}) {
      assertFalse("the renderer's own log quotes '" + quoted + "':\n" + delivered, delivered.contains(quoted));
    }
    assertEquals("a WARNING or above from an OpenHTMLtoPDF logger:\n" + delivered, 0,
      rendererLog.atOrAbove(java.util.logging.Level.WARNING));
    assertTrue(messages(Level.ERROR).toString(), messages(Level.ERROR).isEmpty());
  }

  @Test
  public void anOversizedDataUriIsDroppedWhereverItAppearsAndASmallOneStillLoads() throws Exception {
    String big = noisePng(760);
    assertTrue("test image must exceed the cap: " + big.length(), big.length() > PdfResourceLoader.MAX_DATA_URI_CHARS);
    String small = noisePng(700);
    assertTrue("test image must sit just under the cap: " + small.length(),
      small.length() < PdfResourceLoader.MAX_DATA_URI_CHARS && small.length() > PdfResourceLoader.MAX_DATA_URI_CHARS
        - 400000);
    String[] vectors = {"<img src=\"{{{uri}}}\" width=\"40\" height=\"40\"/><p>x</p>",
      "<div style=\"height: 40px; width: 40px; background-image: url({{{uri}}})\">x</div>",
      "<table><tr><td background=\"{{{uri}}}\">x</td></tr></table>"};
    for (String vector : vectors) {
      String template = "<html><body>" + vector + "</body></html>";
      LocalPdfReportService service = newService(new Network(false));
      try (PDDocument document = open(service.render(payload(template, JSON.createObjectNode().put("uri", small)
        .toString()), 30000L))) {
        assertTrue("a data: image under the cap must still load: " + vector, imagesOf(document) >= 1);
      }
      try (PDDocument document = open(service.render(payload(template, JSON.createObjectNode().put("uri", big)
        .toString()), 30000L))) {
        assertEquals("an image over MAX_DATA_URI_CHARS reached the renderer: " + vector, 0, imagesOf(document));
      }
    }
  }

  @Test
  public void anAllowlistedResourceThatAnswers404RendersWithoutAFailure() throws Exception {
    Network network = new Network(false);
    LocalPdfReportService service = newService(network);
    String template = "<html><head><link rel=\"stylesheet\" href=\"https://" + HOST + "/missing.css\"/></head>"
      + "<body><img src=\"https://" + HOST + "/missing.png\" width=\"20\"/><p>still here</p></body></html>";
    try (PDDocument document = open(service.render(payload(template, "{}"), 30000L))) {
      assertEquals(1, document.getNumberOfPages());
      assertEquals(0, imagesOf(document));
      assertTrue(squash(new PDFTextStripper().getText(document)).contains("stillhere"));
    }
    assertEquals(network.opened.toString(), 2, network.opened.size());
    assertEquals(network.missing.toString(), 2, network.missing.size());
    assertTrue(messages(Level.ERROR).toString(), messages(Level.ERROR).isEmpty());
    assertTrue(messages(Level.INFO).get(0), messages(Level.INFO).get(0).contains("outcome=ok"));
  }

  @Test
  public void anAllowlistedFetchThatFailsIsCountedInTheRenderLineAndIsNotABlockedUri() throws Exception {
    Network network = new Network(false);
    LocalPdfReportService service = newService(network);
    String template = "<html><body><img src=\"https://" + HOST + "/missing.png\" width=\"20\"/><p>x</p></body></html>";
    service.render(payload(template, "{}"), 30000L);
    assertEquals(network.missing.toString(), 1, network.missing.size());
    String info = messages(Level.INFO).get(0);
    assertTrue(info, info.contains("blockedUris=0"));
    assertTrue(info, info.endsWith("failedFetches=1"));
  }

  @Test
  public void anAllowlistedImageIsFetchedThroughTheLoaderAndADataImageStillLoads() throws Exception {
    Network network = new Network(false);
    LocalPdfReportService service = newService(network);
    String allowed = "<html><body><img src=\"https://" + HOST + "/year.png\" width=\"48\"/></body></html>";
    try (PDDocument document = open(service.render(payload(allowed, "{}"), 30000L))) {
      assertEquals("an allowlisted https image must go through the loader (the positive control)",
        1, network.opened.size());
      assertEquals(1, imagesOf(document));
    }
    Network quiet = new Network(false);
    LocalPdfReportService other = newService(quiet);
    String data = "<html><body><img src=\"" + PNG_1X1 + "\" width=\"40\" height=\"40\"/></body></html>";
    try (PDDocument document = open(other.render(payload(data, "{}"), 30000L))) {
      assertEquals("a data: image must still load", 1, imagesOf(document));
    }
    assertEquals(0, quiet.opened.size());
  }

  @Test
  public void cssParseWarningsOfTheOicrRenderStayOutOfTheLogWhileARealRenderErrorStillLogs() throws Exception {
    // The service initialises the renderer's logging (which resets the handlers of its loggers) in its constructor.
    LocalPdfReportService service = newService(new Network(false));
    final List<java.util.logging.LogRecord> records = new CopyOnWriteArrayList<java.util.logging.LogRecord>();
    java.util.logging.Handler handler = new java.util.logging.Handler() {

      @Override
      public void publish(java.util.logging.LogRecord record) {
        records.add(record);
      }

      @Override
      public void flush() {
      }

      @Override
      public void close() {
      }
    };
    java.util.logging.Logger cssParse = java.util.logging.Logger.getLogger(com.openhtmltopdf.util.XRLog.CSS_PARSE);
    cssParse.addHandler(handler);
    try {
      service.render(fixturePayload("oicr"), LocalPdfReportService.DOWNLOAD_DEADLINE_MS);
      int cssWarnings = 0;
      for (java.util.logging.LogRecord record : records) {
        if (record.getLevel().intValue() >= java.util.logging.Level.WARNING.intValue()) {
          cssWarnings++;
        }
      }
      assertEquals("css-parse warnings reached the log: " + records.size(), 0, cssWarnings);
      assertEquals(java.util.logging.Level.SEVERE, cssParse.getLevel());
      for (String name : com.openhtmltopdf.util.XRLog.listRegisteredLoggers()) {
        assertEquals("every OpenHTMLtoPDF category must be SEVERE: " + name, java.util.logging.Level.SEVERE,
          java.util.logging.Logger.getLogger(name).getLevel());
      }
      cssParse.severe("probe");
      assertEquals("a SEVERE css-parse record must still be delivered", 1, records.size());

      Recorded recorded = new Recorded();
      service.renderToResponse(payload("<html><body>{{#each rows}}</body></html>", "{}"), "bad.pdf",
        response(recorded));
      assertEquals(1, messages(Level.ERROR).size());
    } finally {
      cssParse.removeHandler(handler);
    }
  }

  // ---- response ------------------------------------------------------------------------------------------------

  @Test
  public void renderToResponseWritesAnInlinePdfWithTheMicroserviceHeaders() throws Exception {
    LocalPdfReportService service = newService(new Network(false));
    Recorded recorded = new Recorded();
    service.renderToResponse(payload("<html><body><p>ok</p></body></html>", "{}"), "my-report.pdf",
      response(recorded));
    assertEquals("application/pdf", recorded.contentType);
    assertEquals("inline; filename=\"my-report.pdf\"", recorded.disposition);
    assertEquals(recorded.body.size(), recorded.contentLength);
    assertEquals(null, recorded.errorStatus);
    try (PDDocument document = open(recorded.body.toByteArray())) {
      assertEquals(1, document.getNumberOfPages());
    }
  }

  @Test
  public void aTemplateThatFailsToCompileAnswers500WithTheGenericMessageOnly() throws Exception {
    LocalPdfReportService service = newService(new Network(false));
    RendererLog rendererLog = RendererLog.attach();
    Recorded recorded = new Recorded();
    String broken = "<html><body><p>" + TEMPLATE_MARKER + "</p>{{#each rows}}<p>" + TEMPLATE_MARKER
      + "</body></html>";
    service.renderToResponse(payload(broken, "{\"secret\":\"" + PAYLOAD_MARKER + "\"}"), "broken.pdf",
      response(recorded));
    rendererLog.detach();
    assertFalse(rendererLog.describe(), rendererLog.describe().contains(TEMPLATE_MARKER)
      || rendererLog.describe().contains(PAYLOAD_MARKER));
    assertEquals(Integer.valueOf(500), recorded.errorStatus);
    assertNull("no reason text", recorded.errorMessage);
    assertEquals(0, recorded.body.size());
    assertEquals(null, recorded.contentType);
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
    LocalPdfReportService service = newService(new Network(false));
    Recorded recorded = new Recorded();
    service.renderToResponse("{\"data\":{\"data\":{}}}", "x.pdf", response(recorded));
    assertEquals(Integer.valueOf(500), recorded.errorStatus);
    assertNull("no reason text", recorded.errorMessage);
  }

  @Test
  public void aClientAbortWhileWritingIsLoggedAtDebugAndAnyOtherWriteFailurePropagates() throws Exception {
    LocalPdfReportService service = newService(new Network(false));
    String small = payload("<html><body><p>ok</p></body></html>", "{}");
    Recorded abort = new Recorded();
    abort.failOnWrite = new ClientAbortException();
    service.renderToResponse(small, "a.pdf", response(abort));
    assertEquals(null, abort.errorStatus);
    boolean debug = false;
    for (String line : messages(Level.DEBUG)) {
      debug |= line.startsWith("Client aborted the PDF download");
    }
    assertTrue(debug);

    Recorded broken = new Recorded();
    broken.failOnWrite = new IOException("disk full");
    try {
      service.renderToResponse(small, "b.pdf", response(broken));
      fail("a write failure that is not a client abort was swallowed");
    } catch (IOException e) {
      assertEquals("disk full", e.getMessage());
    }
  }

  @Test
  public void theFileNameCannotBreakOutOfTheContentDispositionHeader() throws Exception {
    LocalPdfReportService service = newService(new Network(false));
    Recorded recorded = new Recorded();
    service.renderToResponse(payload("<html><body><p>ok</p></body></html>", "{}"), "a\"\r\nSet-Cookie: x=1.pdf",
      response(recorded));
    assertEquals("inline; filename=\"a___Set-Cookie: x=1.pdf\"", recorded.disposition);
  }
}
