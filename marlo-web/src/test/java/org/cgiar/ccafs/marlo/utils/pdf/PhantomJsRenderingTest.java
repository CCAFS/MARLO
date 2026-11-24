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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Renders the three production templates, and some hostile HTML, with the real PhantomJS (CHG-LPRE-T13). It runs only
 * when the environment variable {@code PHANTOMJS_PATH} names a PhantomJS executable, and is skipped otherwise.
 * <p>
 * Page counts, fonts and footer bands are the production ones only for the Linux x86_64 build with the bundled DejaVu
 * fonts (design section 18.4); on another platform the test asserts that the pages render and prints what it saw.
 * Every template image goes through the production {@link PdfImageBundle} (inlined, SVG flags trusted) and the page
 * script allows no host at all (T18), so the test needs no network and does no inlining of its own.
 */
public class PhantomJsRenderingTest {

  private static final Logger LOG = LoggerFactory.getLogger(PhantomJsRenderingTest.class);
  private static final String PNG_1X1 = "data:image/png;base64,"
    + "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR4nGP4z8DwHwAFAAH/iZk9HQAAAABJRU5ErkJggg==";
  private static final ObjectMapper JSON = new ObjectMapper();
  private static final File OUT_DIR = new File("target/local-pdf-report-phantomjs");

  private ListAppender<ILoggingEvent> appender;
  private ch.qos.logback.classic.Logger serviceLogger;
  private Level previousLevel;
  private LocalPdfReportService service;

  private static boolean isLinux() {
    return System.getProperty("os.name", "").toLowerCase().contains("linux");
  }

  @Before
  public void setUp() {
    String path = System.getenv("PHANTOMJS_PATH");
    Assume.assumeTrue("PHANTOMJS_PATH is not set: the real PhantomJS tests are skipped",
      path != null && !path.trim().isEmpty());
    serviceLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(LocalPdfReportService.class);
    previousLevel = serviceLogger.getLevel();
    serviceLogger.setLevel(Level.DEBUG);
    appender = new ListAppender<ILoggingEvent>();
    appender.start();
    serviceLogger.addAppender(appender);
    service = new LocalPdfReportService(path.trim());
  }

  @After
  public void tearDown() {
    if (service != null) {
      service.shutdown();
      serviceLogger.detachAppender(appender);
      serviceLogger.setLevel(previousLevel);
    }
  }

  private static String resource(String path) throws IOException {
    try (InputStream in = PhantomJsRenderingTest.class.getResourceAsStream(path)) {
      assertNotNull("missing test resource " + path, in);
      return new String(in.readAllBytes(), StandardCharsets.UTF_8);
    }
  }

  private static String fixturePayload(String name) throws IOException {
    ObjectNode root = (ObjectNode) JSON.readTree(resource("/pdf/payloads/" + name + "-payload.json"));
    ((ObjectNode) root.get("data")).put("templateData", resource("/pdf/templates/" + name + ".html"));
    return JSON.writeValueAsString(root);
  }

  private static String payload(String template) throws IOException {
    ObjectNode root = JSON.createObjectNode();
    ObjectNode data = root.putObject("data");
    data.put("templateData", template);
    data.putObject("data");
    ObjectNode options = data.putObject("options");
    options.put("format", "A4");
    options.put("orientation", "portrait");
    options.putObject("header").put("height", "40mm");
    options.putObject("footer").put("height", "30mm");
    data.put("fileName", "probe.pdf");
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

  private List<String> infoLines() {
    List<String> found = new ArrayList<String>();
    for (ILoggingEvent event : new ArrayList<ILoggingEvent>(appender.list)) {
      if (event.getLevel() == Level.INFO && event.getFormattedMessage().startsWith("PDF render file=")) {
        found.add(event.getFormattedMessage());
      }
    }
    return found;
  }

  @Test
  public void productionTemplatesRenderWithTheProductionPageCountsOnLinux() throws Exception {
    String[] names = {"oicr", "innovation", "project"};
    int[] pages = {5, 6, 7};
    String[][] expectedText = {
      {"Climate-smart advisory services", "SECTION 1: At a glance", "SECTION 2: The story and impact"},
      {"Sample", "SECTION: At a glance", "SECTION: Bundle composition"},
      {"AICCRA Cluster Summary", "KEY BADGES", "CLUSTER PARTNERS"}};
    Set<String> dejaVu = new TreeSet<String>();
    dejaVu.add("DejaVuSans");
    dejaVu.add("DejaVuSans-Bold");
    OUT_DIR.mkdirs();
    for (int i = 0; i < names.length; i++) {
      byte[] pdf = service.render(fixturePayload(names[i]), LocalPdfReportService.DOWNLOAD_DEADLINE_MS);
      Files.write(new File(OUT_DIR, names[i] + ".pdf").toPath(), pdf);
      try (PDDocument document = open(pdf)) {
        LOG.info("PhantomJsRenderingTest {}: pages={} fonts={} os={}", names[i], document.getNumberOfPages(),
          fontsOf(document), System.getProperty("os.name"));
        if (isLinux()) {
          assertEquals(names[i] + " page count", pages[i], document.getNumberOfPages());
          assertEquals(names[i] + " fonts", dejaVu, fontsOf(document));
        } else {
          assertTrue(names[i] + " page count " + document.getNumberOfPages(),
            document.getNumberOfPages() >= 3 && document.getNumberOfPages() <= 12);
        }
        String text = new PDFTextStripper().getText(document);
        if (isLinux()) {
          for (String expected : expectedText[i]) {
            assertTrue(names[i] + " text lacks '" + expected + "'", text.contains(expected));
          }
        } else {
          assertTrue(names[i] + " text lacks its title: " + text.substring(0, Math.min(300, text.length())),
            squash(text).toLowerCase().contains(squash(expectedText[i][0]).toLowerCase()));
        }
      }
    }
    List<String> infos = infoLines();
    assertEquals(infos.toString(), 3, infos.size());
    for (String line : infos) {
      assertTrue(line,
        line.contains("outcome=ok") && line.contains("blockedUris=0") && line.contains("failedFetches=0"));
    }
  }

  @Test
  public void firstPageFooterVariantIsOnPageOneAndTheDefaultOneOnTheOthersOnLinux() throws Exception {
    Assume.assumeTrue("footer bands are asserted for the Linux build only (design section 18.4)", isLinux());
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

  @Test
  public void aHostileTemplateRendersWithoutRunningItsScriptOrFetchingOutsideTheAllowlist() throws Exception {
    String template = "<html><body><p>kept text</p>"
      + "<script>document.body.innerHTML = 'SCRIPT-RAN';</script>"
      + "<img src=\"x\" onerror=\"document.body.innerHTML = 'HANDLER-RAN'\">"
      + "<a href=\"javascript:document.body.innerHTML = 'LINK-RAN'\">link</a>"
      + "<img src=\"http://169.254.169.254/latest/meta-data/\">"
      + "<img src=\"https://evil.example.com/a.png\">"
      + "<img src=\"file:///etc/passwd\">"
      + "<link rel=\"stylesheet\" href=\"https://evil.example.com/s.css\">"
      + "<img src=\"" + PNG_1X1 + "\""
      + " width=\"40\" height=\"40\"></body></html>";
    try (PDDocument document = open(service.render(payload(template), 30000L))) {
      String text = new PDFTextStripper().getText(document);
      assertTrue(text, text.contains("kept text"));
      for (String effect : new String[] {"SCRIPT-RAN", "HANDLER-RAN", "LINK-RAN"}) {
        assertFalse(effect + " ran: " + text, text.contains(effect));
      }
      assertEquals("the data: image still loads", 1, imagesOf(document));
    }
    List<String> infos = infoLines();
    assertEquals(infos.toString(), 1, infos.size());
    assertFalse("nothing outside the allowlist may be requested without a block count: " + infos.get(0),
      infos.get(0).contains("blockedUris=0"));
  }

  /** What every bypass payload tries: change the page visibly, and reach a host outside the allowlist two ways. */
  private static final String ATTACK = "document.body.innerHTML='PWNED';"
    + "new Image().src='https://evil.example.com/i.png';new WebSocket('ws://evil.example.com/')";

  /** Inputs jsoup and WebKit read differently, and embedded documents; the sanitizer cannot catch all of them. */
  private static String[] bypassPayloads() {
    return new String[] {
      "<noembed><img src=x onerror=" + ATTACK + "></noembed>",
      "<noframes><img src=x onerror=" + ATTACK + "></noframes>",
      "<template><img src=x onerror=" + ATTACK + "></template>",
      "<noscript><p title=\"</noscript><img src=x onerror=" + ATTACK + ">\"></noscript>",
      "<svg><style><img src=x onerror=" + ATTACK + "></style></svg>",
      "<svg><style><img src=x onerror=" + ATTACK + "></style></svg><noscript><p title=\"</noscript>"
        + "<img src=x onerror=" + ATTACK + ">\"></noscript>",
      "<iframe src=\"data:text/html,<script>" + ATTACK + "</script>\"></iframe>",
      "<embed src=\"data:image/svg+xml,<svg xmlns='http://www.w3.org/2000/svg' onload=\\\"" + ATTACK + "\\\">\">",
      "<object data=\"data:text/html,<script>" + ATTACK + "</script>\"></object>",
      "<meta http-equiv=\"refresh\" content=\"0;url=data:text/html,<script>" + ATTACK + "</script>\">",
      "<img src=x onerror=\"" + ATTACK + "\">", "<script>" + ATTACK + "</script>",
      "<a href=\"javascript:" + ATTACK + "\">go</a><body onload=\"" + ATTACK + "\">"};
  }

  @Test
  public void bypassPayloadsDoNotChangeThePageOrReachOutsideTheAllowlist() throws Exception {
    String[] payloads = bypassPayloads();
    for (int i = 0; i < payloads.length; i++) {
      appender.list.clear();
      String template = "<html><body><p>original content</p>" + payloads[i] + "<p>more content</p></body></html>";
      byte[] pdf;
      try {
        pdf = service.render(payload(template), 30000L);
      } catch (PdfRenderException e) {
        throw new AssertionError("payload " + i + " failed the render: " + e.getMessage(), e);
      }
      try (PDDocument document = open(pdf)) {
        String text = new PDFTextStripper().getText(document);
        assertFalse("payload " + i + " changed the page: " + text, text.contains("PWNED"));
        // What follows a payload may go with it (jsoup nests it inside the noscript it removes), what came before
        // must stay.
        assertTrue("payload " + i + " lost the original content: " + text, text.contains("original content"));
      }
      List<String> infos = infoLines();
      assertEquals(infos.toString(), 1, infos.size());
      assertTrue("payload " + i + " requested a URL outside the allowlist: " + infos.get(0),
        infos.get(0).contains("outcome=ok") && infos.get(0).contains("blockedUris=0"));
    }
  }

  @Test
  public void aPayloadWithOnlyTheRequiredFieldsRenders() throws Exception {
    String minimal = "{\"data\":{\"templateData\":\"<html><body><p>Hello {{name}}</p></body></html>\","
      + "\"data\":{\"name\":\"World\"}}}";
    try (PDDocument document = open(service.render(minimal, 30000L))) {
      assertEquals(1, document.getNumberOfPages());
      assertTrue(new PDFTextStripper().getText(document).replaceAll("\\s", "").contains("HelloWorld"));
    }
  }

  @Test
  public void renderToResponseWritesTheRealPdf() throws Exception {
    final ByteArrayOutputStream body = new ByteArrayOutputStream();
    PdfResponses.Recorded recorded = new PdfResponses.Recorded();
    service.renderToResponse(payload("<html><body><p>ok</p></body></html>"), "real.pdf",
      PdfResponses.response(recorded));
    assertEquals("application/pdf", recorded.contentType);
    assertEquals("inline; filename=\"real.pdf\"", recorded.disposition);
    body.write(recorded.body.toByteArray());
    try (PDDocument document = open(body.toByteArray())) {
      assertEquals(1, document.getNumberOfPages());
    }
  }
}
