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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The images the report templates load from the {@code marlo-pdf-resources-dev} S3 bucket, shipped inside MARLO
 * (CHG-LPRE-T17, Decision Log "Everything from MARLO", design section 18.1). The bytes live under
 * {@code pdf/images/}, at the bucket's own key paths, next to a {@code manifest.tsv} (path, size, SHA-256, source
 * URL). {@link #inline} replaces the URLs of that bucket that a page would load, in the {@code src}, {@code srcset},
 * {@code poster}, {@code data} and {@code background} attributes and in CSS {@code url()} (of {@code style}
 * attributes and {@code <style>} blocks), with a {@code data:} URI of the bundled bytes, so a render never reads the
 * bucket and the bytes PhantomJS decodes are the ones it fetched before (parity). Visible text and {@code href}
 * values are left alone: a bucket URL a user typed into rich text must print as the URL, not as kilobytes of base64.
 * <p>
 * A URL of the bucket that names no bundled object (the templates also build names from data, and a few location
 * codes have no flag in the bucket, where production gets an error response and shows a broken image) is logged by
 * name only, at WARN the first time in the process and at DEBUG afterwards, and replaced by an empty PNG data URI:
 * the image fails to decode as it did, and nothing is requested. The page script no longer allows the bucket host,
 * so a URL this class did not recognise is refused too.
 * <p>
 * The data URIs are encoded on first use and kept; the cache can never hold more than the manifest's objects, about
 * 4.8 MB of text. Bytes that do not match their manifest hash are never used. The instance is thread-safe.
 */
final class PdfImageBundle {

  /** The result of {@link #inline}: the page and the SVG data URLs it now holds (the page script must trust them). */
  static final class Inlined {

    private final String html;
    private final Set<String> svgDataUrls;

    Inlined(String html, Set<String> svgDataUrls) {
      this.html = html;
      this.svgDataUrls = svgDataUrls;
    }

    String html() {
      return html;
    }

    /** SVG data URLs placed in the page; SEC-003 refuses SVG data from any other source. */
    Set<String> svgDataUrls() {
      return svgDataUrls;
    }
  }

  static final String HOST = "marlo-pdf-resources-dev.s3.us-east-1.amazonaws.com";

  static final String RESOURCE_DIR = "/pdf/images/";

  static final String MANIFEST = RESOURCE_DIR + "manifest.tsv";

  /** What replaces a bucket URL that names no bundled object: a PNG that cannot be decoded. */
  static final String UNAVAILABLE = "data:image/png;base64,";

  private static final Logger LOG = LoggerFactory.getLogger(PdfImageBundle.class);

  private static final int MAX_LOGGED_NAME = 100;

  /** Ends a URL early at an HTML-escaped quote, which a re-serialized attribute can put around it. */
  private static final String PATH = "(?:(?!&(?:quot|apos|#0*3[49]|#x0*2[27]);)[^\"'()\\s<>\\\\,])*";

  /** The bucket host (any case, optional :443), then the path, query and fragment up to a delimiter of the markup. */
  private static final String BUCKET = "https://" + Pattern.quote(HOST) + "(?::443)?(?![A-Za-z0-9.:@-])(" + PATH + ")";

  private static final Pattern BUCKET_URL = Pattern.compile(BUCKET, Pattern.CASE_INSENSITIVE);

  /** A bucket URL right after {@code url(} and an optional (possibly escaped) quote: group 1 is the lead-in. */
  private static final Pattern CSS_URL = Pattern.compile(
    "(url\\(\\s*(?:[\"']|&quot;|&apos;|&#0*3[49];|&#x0*2[27];)?)" + BUCKET, Pattern.CASE_INSENSITIVE);

  /** A {@code <style>} block (groups 1 to 3), or an attribute that can load an image (name 4, value 6, 7 or 8). */
  private static final Pattern SCAN = Pattern.compile("(<style\\b[^>]*>)(.*?)(</style\\s*>)"
    + "|(?<![\\w:-])(src|srcset|poster|data|background|style)(\\s*=\\s*)(?:\"([^\"]*)\"|'([^']*)'|([^\\s\"'>]+))",
    Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

  /** Names reported at WARN are remembered up to this many, so hostile input cannot grow the set without bound. */
  private static final int MAX_REPORTED = 1000;

  private static final PdfImageBundle SHARED = new PdfImageBundle();

  private final Map<String, String> sha256ByPath;

  private final Map<String, String> dataUris = new ConcurrentHashMap<String, String>();

  private final Set<String> broken = ConcurrentHashMap.newKeySet();

  private final Set<String> reported = ConcurrentHashMap.newKeySet();

  /** Loads the manifest of the classpath bundle. */
  PdfImageBundle() {
    this(MANIFEST);
  }

  PdfImageBundle(String manifestResource) {
    this.sha256ByPath = Collections.unmodifiableMap(readManifest(manifestResource));
  }

  static PdfImageBundle shared() {
    return SHARED;
  }

  /** The object keys of the manifest, in file order. */
  Set<String> paths() {
    return sha256ByPath.keySet();
  }

  /** The media type of a bundled key, or null when its extension is not one a page can show. */
  static String mimeType(String path) {
    String lower = path.toLowerCase(Locale.ROOT);
    if (lower.endsWith(".png")) {
      return "image/png";
    }
    if (lower.endsWith(".svg")) {
      return "image/svg+xml";
    }
    if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
      return "image/jpeg";
    }
    if (lower.endsWith(".gif")) {
      return "image/gif";
    }
    return null;
  }

  /**
   * Replaces the bucket URLs of {@code html} that a page would load (see the class comment). The very same string
   * comes back, with no SVG data URLs, when there is nothing to replace.
   */
  Inlined inline(String html) {
    if (html == null || html.isEmpty() || !BUCKET_URL.matcher(html).find()) {
      return new Inlined(html, Collections.<String>emptySet());
    }
    Run run = new Run();
    StringBuilder out = new StringBuilder(html.length() + 4096);
    Matcher scan = SCAN.matcher(html);
    int last = 0;
    while (scan.find()) {
      if (scan.group(1) != null) {
        out.append(html, last, scan.start(2)).append(rewrite(scan.group(2), CSS_URL, 2, run));
        last = scan.end(2);
        continue;
      }
      int group = scan.group(6) != null ? 6 : scan.group(7) != null ? 7 : 8;
      String value = scan.group(group);
      boolean css = "style".equalsIgnoreCase(scan.group(4));
      out.append(html, last, scan.start(group)).append(css ? rewrite(value, CSS_URL, 2, run)
        : rewrite(value, BUCKET_URL, 1, run));
      last = scan.end(group);
    }
    if (run.replaced == 0) {
      return new Inlined(html, Collections.<String>emptySet());
    }
    out.append(html, last, html.length());
    return new Inlined(out.toString(), run.svg);
  }

  /** What one {@link #inline} call has seen. */
  private static final class Run {

    private final Set<String> svg = new LinkedHashSet<String>();
    private final Set<String> named = new HashSet<String>();
    private int replaced;
  }

  /** Replaces the bucket URLs of one attribute value or CSS text; {@code pathGroup} holds the path. */
  private String rewrite(String text, Pattern pattern, int pathGroup, Run run) {
    Matcher matcher = pattern.matcher(text);
    if (!matcher.find()) {
      return text;
    }
    StringBuilder out = new StringBuilder(text.length() + 1024);
    int last = 0;
    do {
      out.append(text, last, matcher.start());
      if (pathGroup == 2) {
        out.append(matcher.group(1));
      }
      String key = keyOf(matcher.group(pathGroup));
      String dataUri = key == null ? null : dataUri(key);
      if (dataUri == null) {
        reportUnknown(key, run);
        out.append(UNAVAILABLE);
      } else {
        out.append(dataUri);
        if (dataUri.startsWith("data:image/svg+xml")) {
          run.svg.add(dataUri);
        }
      }
      run.replaced++;
      last = matcher.end();
    } while (matcher.find());
    return out.append(text, last, text.length()).toString();
  }

  /** Once per page, and at WARN once per process (then DEBUG); never again for a name already reported as corrupt. */
  private void reportUnknown(String key, Run run) {
    String name = String.valueOf(key);
    if (!run.named.add(name) || key != null && broken.contains(key)) {
      return;
    }
    if (reported.size() < MAX_REPORTED && reported.add(name)) {
      LOG.warn("Report image is not bundled with MARLO and is left blank: {}", loggable(key));
    } else {
      LOG.debug("Report image is not bundled with MARLO and is left blank: {}", loggable(key));
    }
  }

  /** The object key a URL path names ({@code +} and %XX decoded as S3 does), or null when it names none. */
  static String keyOf(String pathAndQuery) {
    if (pathAndQuery == null || !pathAndQuery.startsWith("/")) {
      return null;
    }
    int end = pathAndQuery.length();
    for (int i = 1; i < pathAndQuery.length(); i++) {
      char c = pathAndQuery.charAt(i);
      if (c == '?' || c == '#') {
        end = i;
        break;
      }
    }
    try {
      return URLDecoder.decode(pathAndQuery.substring(1, end), "UTF-8");
    } catch (UnsupportedEncodingException | IllegalArgumentException e) {
      return null;
    }
  }

  private String dataUri(String key) {
    if (!sha256ByPath.containsKey(key) || broken.contains(key)) {
      return null;
    }
    String cached = dataUris.get(key);
    if (cached != null) {
      return cached;
    }
    String mime = mimeType(key);
    byte[] bytes = mime == null ? null : read(RESOURCE_DIR + key);
    if (bytes == null || !sha256(bytes).equals(sha256ByPath.get(key))) {
      broken.add(key);
      LOG.error("Bundled report image {} is missing or does not match its manifest hash", loggable(key));
      return null;
    }
    String built = "data:" + mime + ";base64," + Base64.getEncoder().encodeToString(bytes);
    dataUris.put(key, built);
    return built;
  }

  /** A name fit for a log line: printable ASCII only, bounded. */
  private static String loggable(String key) {
    if (key == null) {
      return "(undecodable name)";
    }
    StringBuilder clean = new StringBuilder();
    for (int i = 0; i < key.length() && clean.length() < MAX_LOGGED_NAME; i++) {
      char c = key.charAt(i);
      clean.append(c >= ' ' && c < 0x7f ? c : '?');
    }
    return clean.toString();
  }

  private static Map<String, String> readManifest(String resource) {
    Map<String, String> entries = new LinkedHashMap<String, String>();
    byte[] bytes = read(resource);
    if (bytes == null) {
      LOG.error("The report image manifest {} is missing: no template image will be shown", resource);
      return entries;
    }
    String[] lines = new String(bytes, StandardCharsets.UTF_8).split("\\R");
    for (int i = 1; i < lines.length; i++) {
      String[] fields = lines[i].split("\t");
      if (fields.length >= 3 && !fields[0].isEmpty()) {
        entries.put(fields[0], fields[2].toLowerCase(Locale.ROOT));
      }
    }
    return entries;
  }

  private static byte[] read(String resource) {
    try (InputStream in = PdfImageBundle.class.getResourceAsStream(resource)) {
      if (in == null) {
        return null;
      }
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      byte[] buffer = new byte[8192];
      int count = in.read(buffer);
      while (count >= 0) {
        out.write(buffer, 0, count);
        count = in.read(buffer);
      }
      return out.toByteArray();
    } catch (IOException e) {
      return null;
    }
  }

  private static String sha256(byte[] bytes) {
    try {
      byte[] digest = MessageDigest.getInstance("SHA-256").digest(bytes);
      StringBuilder hex = new StringBuilder();
      for (byte b : digest) {
        hex.append(String.format("%02x", b));
      }
      return hex.toString();
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA-256 is not available", e);
    }
  }
}
