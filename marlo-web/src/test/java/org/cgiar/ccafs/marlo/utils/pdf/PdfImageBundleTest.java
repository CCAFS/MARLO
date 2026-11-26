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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * The template images shipped inside MARLO (CHG-LPRE-T17): the bundle covers every object of the bucket the reports
 * can reference, the bytes are the ones recorded in the manifest, and inlining rewrites URLs without disturbing
 * anything else.
 */
public class PdfImageBundleTest {

  private static final String BASE = "https://" + PdfImageBundle.HOST + "/";
  private static final String PRMS_BASE = "https://" + PdfImageBundle.PRMS_HOST + "/";
  private static final String BUNDLE_DIR = "src/main/resources/pdf/images";
  private static final String[] TEMPLATES = {"oicr", "innovation", "project"};

  /** ISO 3166-1 alpha-2 codes, plus xk (Kosovo): the flags a country location can ask for. */
  private static final List<String> ISO_CODES = Arrays.asList((""
    + "ad ae af ag ai al am ao aq ar as at au aw ax az ba bb bd be bf bg bh bi bj bl bm bn bo bq br bs bt "
    + "bv bw by bz ca cc cd cf cg ch ci ck cl cm cn co cr cu cv cw cx cy cz de dj dk dm do dz ec ee eg eh "
    + "er es et fi fj fk fm fo fr ga gb gd ge gf gg gh gi gl gm gn gp gq gr gs gt gu gw gy hk hm hn hr ht "
    + "hu id ie il im in io iq ir is it je jm jo jp ke kg kh ki km kn kp kr kw ky kz la lb lc li lk lr ls "
    + "lt lu lv ly ma mc md me mf mg mh mk ml mm mn mo mp mq mr ms mt mu mv mw mx my mz na nc ne nf ng ni "
    + "nl no np nr nu nz om pa pe pf pg ph pk pl pm pn pr ps pt pw py qa re ro rs ru rw sa sb sc sd se sg "
    + "sh si sj sk sl sm sn so sr ss st sv sx sy sz tc td tf tg th tj tk tl tm tn to tr tt tv tw tz ua ug "
    + "um us uy uz va vc ve vg vi vn vu wf ws ye yt za zm zw xk "
    ).trim().split("\\s+"));

  private ListAppender<ILoggingEvent> appender;
  private ch.qos.logback.classic.Logger log;

  @Before
  public void captureLog() {
    log = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(PdfImageBundle.class);
    appender = new ListAppender<ILoggingEvent>();
    appender.start();
    log.addAppender(appender);
  }

  @After
  public void releaseLog() {
    log.detachAppender(appender);
  }

  private static String template(String name) throws IOException {
    try (InputStream in = PdfImageBundleTest.class.getResourceAsStream("/pdf/templates/" + name + ".html")) {
      assertNotNull(name, in);
      return new String(in.readAllBytes(), StandardCharsets.UTF_8);
    }
  }

  private static byte[] bundled(String key) throws IOException {
    return Files.readAllBytes(Paths.get(BUNDLE_DIR, key));
  }

  private static String sha256(byte[] bytes) throws Exception {
    StringBuilder hex = new StringBuilder();
    for (byte b : MessageDigest.getInstance("SHA-256").digest(bytes)) {
      hex.append(String.format("%02x", b));
    }
    return hex.toString();
  }

  private static byte[] decodeDataUri(String dataUri) {
    return Base64.getDecoder().decode(dataUri.substring(dataUri.indexOf(";base64,") + ";base64,".length()));
  }

  private List<String> warnings() {
    List<String> found = new ArrayList<String>();
    for (ILoggingEvent event : appender.list) {
      if (event.getLevel() == Level.WARN) {
        found.add(event.getFormattedMessage());
      }
    }
    return found;
  }

  // ---- the bundle itself ---------------------------------------------------------------------------------------

  @Test
  public void everyManifestEntryHasItsFileWithTheRecordedSizeAndHashAndNothingIsUnlisted() throws Exception {
    List<String> lines = Files.readAllLines(Paths.get(BUNDLE_DIR, "manifest.tsv"), StandardCharsets.UTF_8);
    assertEquals("path\tsize\tsha256\tsource_url", lines.get(0));
    Set<String> listed = new HashSet<String>();
    for (String line : lines.subList(1, lines.size())) {
      String[] fields = line.split("\t");
      assertEquals(line, 4, fields.length);
      byte[] bytes = bundled(fields[0]);
      assertEquals(fields[0], Integer.parseInt(fields[1]), bytes.length);
      assertEquals(fields[0], fields[2], sha256(bytes));
      assertTrue(fields[3], fields[0].startsWith(PdfImageBundle.PRMS_PREFIX) ? fields[3].equals(PRMS_BASE
        + fields[0].substring(PdfImageBundle.PRMS_PREFIX.length())) : fields[3].startsWith(BASE));
      assertNotNull("a bundled object needs a media type: " + fields[0], PdfImageBundle.mimeType(fields[0]));
      assertTrue("duplicate manifest path " + fields[0], listed.add(fields[0]));
    }
    Path root = Paths.get(BUNDLE_DIR);
    try (Stream<Path> files = Files.walk(root)) {
      Set<String> onDisk = files.filter(Files::isRegularFile).map(f -> root.relativize(f).toString())
        .filter(name -> !name.equals("manifest.tsv") && !name.equals("README.txt")).collect(Collectors.toSet());
      assertEquals("files and manifest must list the same objects", listed, onDisk);
    }
    assertEquals(listed, PdfImageBundle.shared().paths());
  }

  @Test
  public void everyBucketUrlTheTemplatesHoldIsBundledAndTheGeneratedOnesCoverTheirWholeRange() throws Exception {
    Set<String> bundledKeys = PdfImageBundle.shared().paths();
    Pattern url = Pattern.compile("(" + Pattern.quote(BASE) + "|" + Pattern.quote(PRMS_BASE)
      + ")([^\"'()\\s<>\\\\]+)");
    Set<String> generated = new HashSet<String>();
    int statics = 0;
    for (String name : TEMPLATES) {
      Matcher matcher = url.matcher(template(name));
      while (matcher.find()) {
        String path = matcher.group(2);
        String host = matcher.group(1).equals(BASE) ? PdfImageBundle.HOST : PdfImageBundle.PRMS_HOST;
        if (path.contains("{{")) {
          generated.add(host.equals(PdfImageBundle.HOST) ? path.substring(0, path.indexOf("{{"))
            : "prms:" + path.substring(0, path.indexOf("{{")));
        } else {
          statics++;
          String key = PdfImageBundle.keyOf(host, "/" + path);
          assertTrue(name + " references a bucket object that is not bundled: " + key, bundledKeys.contains(key));
        }
      }
    }
    assertTrue(statics > 20);
    // The names built from data: flags by ISO code, SDG icons by goal number, impact-area icons by area id.
    assertEquals(new HashSet<String>(Arrays.asList("flags/", "sdg/E-WEB-Goal-", "impact-areas/ICONS+CGIAR-",
      "prms:images/inno-scaling-readiness-")), generated);
    for (String code : ISO_CODES) {
      assertTrue("flag " + code, bundledKeys.contains("flags/" + code + ".svg"));
    }
    for (int goal = 1; goal <= 17; goal++) {
      assertTrue("SDG " + goal, bundledKeys.contains("sdg/E-WEB-Goal-" + goal + ".png"));
    }
    for (int area = 1; area <= 5; area++) {
      assertTrue("impact area " + area, bundledKeys.contains("impact-areas/ICONS CGIAR-" + area + ".png"));
    }
  }

  @Test
  public void theScalingReadinessImagesCoverLevelsZeroToNineAndNotProvided() {
    Set<String> keys = PdfImageBundle.shared().paths();
    for (int level = 0; level <= 9; level++) {
      assertTrue("level " + level, keys.contains(PdfImageBundle.PRMS_PREFIX + "images/inno-scaling-readiness-"
        + level + ".png"));
    }
    assertTrue(keys.contains(PdfImageBundle.PRMS_PREFIX + "images/inno-scaling-readiness-not-provided.png"));
  }

  /**
   * Only {@code innovation.html} builds the image URL {@code inno-scaling-readiness-{{readinessScale}}.png}, from
   * {@code ProjectInnovationSummaryAction}: the level digit 0..9 (a null value takes the not-provided branch).
   * {@code ReportingSummaryAction}'s value (a name such as "0 - Idea", or an id) is shown as text in project.html and
   * builds no URL. The extra values here (10, 11, names, junk) only prove that anything else renders blank.
   */
  @Test
  public void everyReachableReadinessScaleValueIsBundledOrHandledAsUnknown() {
    PdfImageBundle bundle = new PdfImageBundle();
    String[] values = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "-1", "0 - Idea",
      "9 - Proven Innovation", "", "x"};
    for (String value : values) {
      String url = PRMS_BASE + "images/inno-scaling-readiness-" + value + ".png";
      String out = bundle.inline("<img src=\"" + url + "\">").html();
      assertFalse(value, out.contains(PdfImageBundle.PRMS_HOST));
      boolean bundled = value.matches("[0-9]");
      boolean blank = Pattern.compile(Pattern.quote(PdfImageBundle.UNAVAILABLE) + "(?![A-Za-z0-9+/])").matcher(out)
        .find();
      assertEquals(value, bundled, !blank);
    }
  }

  @Test
  public void secondBucketUrlsAreInlinedInEveryPlaceAndKeepTheirKeysApartFromTheFirstBucket() throws Exception {
    PdfImageBundle bundle = new PdfImageBundle();
    String png = PRMS_BASE + "images/inno-scaling-readiness-not-provided.png";
    String page = "<img src=\"" + png + "\"><img SRC='HTTPS://" + PdfImageBundle.PRMS_HOST.toUpperCase()
      + ":443/images/inno-scaling-readiness-4.png'>"
      + "<div style=\"background: url('" + PRMS_BASE + "images/inno-scaling-readiness-0.png')\"></div>"
      + "<style>.a { background: url(" + PRMS_BASE + "images/inno-scaling-readiness-9.png) }</style>"
      + "<p>see " + png + "</p><a href=\"" + png + "\">x</a>";
    String out = bundle.inline(page).html();
    assertEquals(4, out.split("data:image/png;base64,", -1).length - 1);
    assertTrue(out, out.contains("<p>see " + png + "</p><a href=\"" + png + "\">x</a>"));
    String first = out.substring(out.indexOf("base64,") + 7, out.indexOf("\"", out.indexOf("base64,")));
    assertTrue(Arrays.equals(bundled(PdfImageBundle.PRMS_PREFIX + "images/inno-scaling-readiness-not-provided.png"),
      Base64.getDecoder().decode(first)));
    assertTrue(warnings().toString(), warnings().isEmpty());
    // The second bucket's key space is not reachable through the first bucket, nor by an encoded slash or "..".
    Pattern blankPng = Pattern.compile(Pattern.quote(PdfImageBundle.UNAVAILABLE) + "(?![A-Za-z0-9+/])");
    String[] refused = {BASE + PdfImageBundle.PRMS_PREFIX + "images/inno-scaling-readiness-3.png",
      BASE + "prms-file-storage%2Fimages/inno-scaling-readiness-3.png",
      PRMS_BASE + "prms-file-storage/../flags/ke.svg", PRMS_BASE + "../flags/ke.svg"};
    for (String url : refused) {
      String result = bundle.inline("<img src=\"" + url + "\">").html();
      assertEquals(url, "<img src=\"" + PdfImageBundle.UNAVAILABLE + "\">", result);
      assertTrue(url, blankPng.matcher(result).find());
    }
    int warnedBefore = warnings().size();
    // And an unknown object of the second bucket is blank, warned once by name, never requested.
    String unknown = "<img src=\"" + PRMS_BASE + "images/user-upload.png\"><img src=\"" + PRMS_BASE
      + "images/user-upload.png\">";
    String blank = bundle.inline(unknown).html();
    assertFalse(blank, blank.contains(PdfImageBundle.PRMS_HOST));
    assertEquals(2, blank.split(Pattern.quote(PdfImageBundle.UNAVAILABLE), -1).length - 1);
    assertEquals(warnings().toString(), 1, warnings().size() - warnedBefore);
  }

  @Test
  public void theSummaryActionBuildsBucketUrlsOnlyUnderTheSdgFolder() throws Exception {
    File source = new File(
      "src/main/java/org/cgiar/ccafs/marlo/action/summaries/ReportingSummaryAction.java");
    assertTrue(source.getPath(), source.isFile());
    String text = new String(Files.readAllBytes(source.toPath()), StandardCharsets.UTF_8);
    Matcher matcher = Pattern.compile(Pattern.quote(BASE) + "([A-Za-z0-9_./-]*)").matcher(text);
    int found = 0;
    while (matcher.find()) {
      found++;
      assertEquals("a new generated bucket URL needs its object set bundled (and this test extended)", "sdg/",
        matcher.group(1));
    }
    assertTrue(found > 0);
  }

  @Test
  public void bundledSvgsCarryNoScriptOrExternalReference() throws Exception {
    Path root = Paths.get(BUNDLE_DIR);
    int svgs = 0;
    try (Stream<Path> files = Files.walk(root)) {
      for (Path file : files.filter(f -> f.toString().endsWith(".svg")).collect(Collectors.toList())) {
        svgs++;
        String text = new String(Files.readAllBytes(file), StandardCharsets.UTF_8).toLowerCase();
        assertFalse(file + " holds script", text.contains("<script") || text.contains("<foreignobject"));
        assertFalse(file + " holds an event handler", Pattern.compile("\\son[a-z]+\\s*=").matcher(text).find());
        assertFalse(file + " refers outside itself", Pattern.compile("href\\s*=\\s*[\"'](?!#)").matcher(text).find()
          || text.contains("<image") || text.contains("@import") || text.contains("url(http"));
      }
    }
    assertTrue(svgs > 200);
  }

  // ---- inlining ------------------------------------------------------------------------------------------------

  @Test
  public void anAttributeAndACssUrlAreReplacedWithTheBundledBytesWhateverTheQuotingOrSpelling() throws Exception {
    PdfImageBundle bundle = PdfImageBundle.shared();
    String html = "<img src=\"" + BASE + "Africa.png\"><div style=\"background: url(" + BASE + "cover-PDF.png)\">"
      + "<div style='background:url(\"" + BASE + "year.png?v=2#x\")'></div>"
      + "<img src=\"" + BASE + "innovations/Inno+type.png\"><img src=\"" + BASE + "innovations/Inno%20nature.png\">"
      + "<img src=\"" + BASE + "dac_markers/DAC+Markers+-+Gender.png\">"
      + "<img src=\"HTTPS://" + PdfImageBundle.HOST.toUpperCase() + ":443/code.png\">";
    PdfImageBundle.Inlined inlined = bundle.inline(html);
    assertFalse(inlined.html(), inlined.html().toLowerCase().contains("amazonaws"));
    assertTrue("nothing but bundled objects was inlined", warnings().isEmpty());
    Matcher matcher = Pattern.compile("data:image/png;base64,([A-Za-z0-9+/=]+)").matcher(inlined.html());
    List<byte[]> got = new ArrayList<byte[]>();
    while (matcher.find()) {
      got.add(Base64.getDecoder().decode(matcher.group(1)));
    }
    String[] keys = {"Africa.png", "cover-PDF.png", "year.png", "innovations/Inno type.png",
      "innovations/Inno nature.png", "dac_markers/DAC Markers - Gender.png", "code.png"};
    assertEquals(keys.length, got.size());
    for (int i = 0; i < keys.length; i++) {
      assertTrue("byte identity of " + keys[i], Arrays.equals(bundled(keys[i]), got.get(i)));
    }
    assertTrue(inlined.svgDataUrls().isEmpty());
    assertTrue(inlined.html().contains("url(data:image/png;base64,"));
    assertTrue(inlined.html().contains("style='background:url(\"data:image/png;base64,"));
  }

  @Test
  public void aFlagIsInlinedAsAnSvgDataUrlThatIsReportedAsTrustedAndTheSanitizerWouldNotAcceptIt() throws Exception {
    PdfImageBundle.Inlined inlined = PdfImageBundle.shared().inline("<img src=\"" + BASE + "flags/ke.svg\">"
      + "<img src=\"" + BASE + "flags/ke.svg\"><img src=\"" + BASE + "flags/sn.svg\">");
    assertEquals(2, inlined.svgDataUrls().size());
    for (String dataUri : inlined.svgDataUrls()) {
      assertTrue(dataUri.startsWith("data:image/svg+xml;base64,"));
      assertTrue(inlined.html().contains(dataUri));
      assertFalse("SEC-003 narrows data: to non-SVG images; only the trusted list admits these",
        PhantomJsRenderer.isAllowedDataUrl(dataUri));
    }
    String ke = inlined.svgDataUrls().iterator().next();
    assertTrue(Arrays.equals(bundled("flags/ke.svg"), decodeDataUri(ke)));
  }

  @Test
  public void everyBundledObjectInlinesToADataUrlOfItsOwnBytesAndRastersPassTheNarrowedRule() throws Exception {
    PdfImageBundle bundle = PdfImageBundle.shared();
    for (String key : bundle.paths()) {
      String url = key.startsWith(PdfImageBundle.PRMS_PREFIX)
        ? PRMS_BASE + key.substring(PdfImageBundle.PRMS_PREFIX.length()) : BASE + key.replace(" ", "+");
      PdfImageBundle.Inlined inlined = bundle.inline("<img src=\"" + url + "\">");
      String dataUri = inlined.html().substring("<img src=\"".length(), inlined.html().length() - 2);
      assertTrue(key, Arrays.equals(bundled(key), decodeDataUri(dataUri)));
      assertTrue(key, dataUri.length() < 2 * 1024 * 1024);
      if (key.endsWith(".svg")) {
        assertTrue(key, inlined.svgDataUrls().contains(dataUri));
      } else {
        assertTrue(key, PhantomJsRenderer.isAllowedDataUrl(dataUri));
        assertTrue(key, inlined.svgDataUrls().isEmpty());
      }
    }
    assertTrue(warnings().isEmpty());
  }

  // ---- where URLs are replaced ---------------------------------------------------------------------------------

  @Test
  public void aBucketUrlShownAsTextOrUsedAsALinkIsLeftAlone() {
    PdfImageBundle bundle = PdfImageBundle.shared();
    String page = "<p>See " + BASE + "Africa.png for the map</p><a href=\"" + BASE + "Africa.png\">map</a>"
      + "<a title=\"" + BASE + "year.png\" href='" + BASE + "code.png'>x</a><p>src=" + "</p>";
    assertSame(page, bundle.inline(page).html());
  }

  @Test
  public void onlyTheImageUrlsOfAPageAreReplacedWhenTextAndLinksCarryTheSameUrl() {
    String page = "<p>" + BASE + "Africa.png</p><a href=\"" + BASE + "Africa.png\">" + BASE + "Africa.png</a>"
      + "<img src=\"" + BASE + "Africa.png\">";
    String inlined = PdfImageBundle.shared().inline(page).html();
    assertTrue(inlined.startsWith("<p>" + BASE + "Africa.png</p><a href=\"" + BASE + "Africa.png\">" + BASE
      + "Africa.png</a><img src=\"data:image/png;base64,"));
    assertEquals(3, inlined.split(Pattern.quote(BASE), -1).length - 1);
  }

  @Test
  public void aStyleAttributeWithEscapedQuotesIsInlinedWithoutTakingTheQuotesIntoTheName() throws Exception {
    PdfImageBundle bundle = new PdfImageBundle();
    String page = "<div style=\"background:url(&quot;" + BASE + "year.png&quot;) no-repeat\"></div>"
      + "<div style=\"background:url(&#39;" + BASE + "code.png?v=1&#39;)\"></div>";
    String inlined = bundle.inline(page).html();
    assertTrue(warnings().toString(), warnings().isEmpty());
    Matcher matcher = Pattern.compile("url\\(&quot;data:image/png;base64,([A-Za-z0-9+/=]+)&quot;\\) no-repeat")
      .matcher(inlined);
    assertTrue(inlined, matcher.find());
    assertTrue(Arrays.equals(bundled("year.png"), Base64.getDecoder().decode(matcher.group(1))));
    assertTrue(inlined, inlined.contains("url(&#39;data:image/png;base64,"));
    assertTrue(inlined, inlined.contains("&#39;)\"></div>"));
  }

  @Test
  public void styleBlocksSrcsetPosterBackgroundAndUnquotedAttributesAreInlined() {
    PdfImageBundle bundle = new PdfImageBundle();
    String page = "<style>.a { background: url(" + BASE + "cover-PDF.png) }\n.b { background: url( '" + BASE
      + "year.png') }</style>"
      + "<img srcset=\"" + BASE + "code.png 1x, " + BASE + "status.png 2x\">"
      + "<video poster=" + BASE + "Africa.png></video><table background='" + BASE + "tag_as.png'></table>"
      + "<img SRC=\"" + BASE + "lead_person.png\">";
    String inlined = bundle.inline(page).html();
    assertFalse(inlined, inlined.contains("amazonaws"));
    assertEquals(7, inlined.split("data:image/png;base64,", -1).length - 1);
    assertTrue(warnings().toString(), warnings().isEmpty());
  }

  @Test
  public void aPageWithNoBucketUrlComesBackAsTheSameInstance() {
    PdfImageBundle bundle = PdfImageBundle.shared();
    String html = "<html><img src=\"https://uploads.example.org/images/a.png\"></html>";
    assertSame(html, bundle.inline(html).html());
    assertSame("", bundle.inline("").html());
    assertEquals(null, bundle.inline(null).html());
  }

  @Test
  public void lookAlikeHostsAndOtherHostsAreNotTouched() {
    String lookAlike = "<img src=\"" + PRMS_BASE.replace(".com/", ".com.evil.example/")
      + "images/inno-scaling-readiness-3.png\">"
      + "<img src=\"https://" + PdfImageBundle.PRMS_HOST + "@evil.example/images/inno-scaling-readiness-3.png\">"
      + "<img src=\"https://" + PdfImageBundle.PRMS_HOST + ":8443/images/inno-scaling-readiness-3.png\">"
      + "<img src=\"https://evil." + PdfImageBundle.PRMS_HOST + "/images/inno-scaling-readiness-3.png\">"
      + "<img src=\"http://" + PdfImageBundle.PRMS_HOST + "/images/inno-scaling-readiness-3.png\">"
      + "<img src=\"https://uploads.example.org/images/inno-scaling-readiness-3.png\">"
      + "<img src=\"" + BASE.replace(".com/", ".com.evil.example/") + "Africa.png\">"
      + "<img src=\"https://" + PdfImageBundle.HOST + "@evil.example/Africa.png\">"
      + "<img src=\"https://" + PdfImageBundle.HOST + ":8443/Africa.png\">"
      + "<img src=\"https://evil." + PdfImageBundle.HOST + "/Africa.png\">";
    String page = lookAlike;
    assertSame(page, PdfImageBundle.shared().inline(page).html());
  }

  @Test
  public void anUnknownBucketObjectIsWarnedByNameOnceAndBecomesAnUndecodableImageWithNoRequest() {
    PdfImageBundle bundle = new PdfImageBundle();
    String html = "<img src=\"" + BASE + "flags/zz99.svg\"><img src=\"" + BASE + "flags/zz99.svg\">"
      + "<img src=\"" + BASE + "../etc/passwd\"><img src=\"" + BASE + "x%zz.png\"><img src=\"" + BASE + "\">"
      + "<img src=\"" + BASE + "Africa.png.bak\"><img src=\"" + BASE + "AFRICA.PNG\">";
    PdfImageBundle.Inlined inlined = bundle.inline(html);
    assertFalse(inlined.html().contains(PdfImageBundle.HOST));
    assertEquals(7, inlined.html().split(Pattern.quote("src=\"" + PdfImageBundle.UNAVAILABLE + "\""), -1).length - 1);
    assertTrue(inlined.svgDataUrls().isEmpty());
    List<String> warned = warnings();
    assertEquals(warned.toString(), 6, warned.size());
    assertTrue(warned.get(0), warned.get(0).endsWith("flags/zz99.svg"));
    assertFalse("the page text is never logged", warned.toString().contains("<img"));
    bundle.inline(html);
    assertEquals("the same names are not warned about again in this process", 6, warnings().size());
  }

  @Test
  public void aNameCorruptedByItsHashIsReportedAsAnErrorOnlyNotAsNotBundled() {
    PdfImageBundle bundle = new PdfImageBundle("/pdf/test-image-manifest-bad-hash.tsv");
    bundle.inline("<img src=\"" + BASE + "code.png\">");
    bundle.inline("<img src=\"" + BASE + "code.png\">");
    assertTrue(warnings().toString(), warnings().isEmpty());
  }

  @Test
  public void aLoggedNameIsBoundedAndPrintableOnly() {
    String hostile = "a".repeat(500) + "%0a%0d%e2%82%ac";
    new PdfImageBundle().inline("<img src=\"" + BASE + hostile + ".png\">");
    String line = warnings().get(0);
    assertTrue(line, line.length() < 200);
    assertTrue(line, line.chars().allMatch(c -> c >= ' ' && c < 0x7f));
  }

  @Test
  public void bytesThatDoNotMatchTheirManifestHashAreNeverUsed() {
    PdfImageBundle bundle = new PdfImageBundle("/pdf/test-image-manifest-bad-hash.tsv");
    PdfImageBundle.Inlined inlined = bundle.inline("<img src=\"" + BASE + "code.png\">");
    assertEquals("<img src=\"" + PdfImageBundle.UNAVAILABLE + "\">", inlined.html());
    boolean errored = false;
    for (ILoggingEvent event : appender.list) {
      errored |= event.getLevel() == Level.ERROR && event.getFormattedMessage().contains("code.png");
    }
    assertTrue(errored);
  }

  @Test
  public void aMissingManifestLeavesEveryImageUnavailableWithoutFailing() {
    PdfImageBundle bundle = new PdfImageBundle("/pdf/no-such-manifest.tsv");
    assertTrue(bundle.paths().isEmpty());
    assertEquals("<img src=\"" + PdfImageBundle.UNAVAILABLE + "\">",
      bundle.inline("<img src=\"" + BASE + "code.png\">").html());
  }

  // ---- parity guard --------------------------------------------------------------------------------------------

  @Test
  public void theHtmlOfTheProductionFixturesInlinesCompletelyAndTheRestOfThePageIsUntouched() throws Exception {
    PdfImageBundle bundle = PdfImageBundle.shared();
    for (String name : TEMPLATES) {
      String expected;
      try (InputStream in = getClass().getResourceAsStream("/pdf/expected-html/" + name + ".html")) {
        expected = new String(in.readAllBytes(), StandardCharsets.UTF_8);
      }
      assertSame(name + ": the sanitizer leaves the fixture alone", expected, PhantomJsRenderer.sanitize(expected));
      PdfImageBundle.Inlined inlined = bundle.inline(expected);
      assertFalse(name, inlined.html().contains(PdfImageBundle.HOST));
      assertFalse(name, inlined.html().contains(PdfImageBundle.PRMS_HOST));
      assertFalse(name + ": a fixture image is not bundled",
        Pattern.compile(Pattern.quote(PdfImageBundle.UNAVAILABLE) + "(?![A-Za-z0-9+/])").matcher(inlined.html())
          .find());
      String restored = inlined.html();
      Matcher bucket = Pattern.compile("(" + Pattern.quote(BASE) + "|" + Pattern.quote(PRMS_BASE)
        + ")[^\"'()\\s<>\\\\]+").matcher(expected);
      while (bucket.find()) {
        String base = bucket.group(1);
        String key = PdfImageBundle.keyOf(base.equals(BASE) ? PdfImageBundle.HOST : PdfImageBundle.PRMS_HOST,
          bucket.group().substring(base.length() - 1));
        String mime = PdfImageBundle.mimeType(key);
        String dataUri = "data:" + mime + ";base64," + Base64.getEncoder().encodeToString(bundled(key));
        restored = restored.replaceFirst(Pattern.quote(dataUri), Matcher.quoteReplacement(bucket.group()));
      }
      assertEquals(name + ": undoing the inlining gives back Handlebars.js's output", expected, restored);
    }
  }
}
