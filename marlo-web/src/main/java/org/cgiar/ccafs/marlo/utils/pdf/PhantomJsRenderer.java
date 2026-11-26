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
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Turns the HTML of a report into PDF bytes with PhantomJS 2.1.1, the engine the reports microservice uses, run as a
 * local process (CHG-LPRE-FN-004, design section 18). The page script is html-pdf's own (see {@code render-page.js}),
 * so pages break exactly as they do in production.
 * <p>
 * Per render: the HTML is sanitized (SEC-003), a private temporary directory is created, PhantomJS is started with
 * {@code --local-url-access=false} and an explicit minimal environment (an isolated fontconfig that sees only the
 * bundled DejaVu fonts, NF-002; {@code HOME} and {@code TMPDIR} inside the render directory), the same
 * {@code {html, options}} JSON html-pdf sends is written to its stdin, and its stdout and stderr are drained on their
 * own threads, each kept to {@value #STREAM_CAP} bytes. At the deadline the process and its descendants are killed
 * (SEC-004); the render directory is always removed.
 * <p>
 * Nothing in a failure message comes from the HTML, the options or PhantomJS's output: only the exit code and byte
 * counts. The instance holds no per-render state, so concurrent renders may share it.
 */
final class PhantomJsRenderer {

  /** A render that got a page, with the counts the page script reported. */
  static final class Output {

    final int blocked;
    final int failed;
    final int pages;
    final byte[] pdf;

    Output(byte[] pdf, int pages, int blocked, int failed) {
      this.pdf = pdf;
      this.pages = pages;
      this.blocked = blocked;
      this.failed = failed;
    }
  }

  /**
   * Where the Linux build finds libfontconfig, libfreetype and their dependencies (CHG-PPE-DD-2). Decided once, at
   * start-up, by running {@code --version}: with the host's libraries first, and with the libraries shipped inside
   * MARLO only when the dynamic loader reports a missing one. The decision is kept for every render.
   */
  private enum LibraryMode {

    /** Not decided yet, or no shipped libraries apply (another platform, or a configured path). */
    UNDECIDED,

    /** The host's own libraries work: nothing is extracted and no library path is given. */
    HOST,

    /** The shipped libraries are extracted next to the executable and named in the child's {@code LD_LIBRARY_PATH}. */
    BUNDLED,

    /** Neither works: every render fails at once. */
    UNAVAILABLE
  }

  /** The deadline passed and PhantomJS was killed. */
  static final class DeadlineExpiredException extends PdfRenderException {

    private static final long serialVersionUID = 1L;

    DeadlineExpiredException(String message) {
      super(message);
    }
  }

  /** PhantomJS exited with an error; the exit code and the first stderr line are for the log, not for users. */
  static final class ProcessFailedException extends PdfRenderException {

    private static final long serialVersionUID = 1L;

    private final int exitCode;
    private final String stderrLine;

    ProcessFailedException(String message, int exitCode, String stderrLine) {
      super(message);
      this.exitCode = exitCode;
      this.stderrLine = stderrLine;
    }

    int exitCode() {
      return exitCode;
    }

    /**
     * First line of stderr, control characters removed, at most 200 characters; empty for a render, whose stderr
     * is the page script's and can quote page content.
     */
    String stderrLine() {
      return stderrLine;
    }
  }

  /** What a finished process left behind. */
  private static final class Finished {

    private final int exitCode;

    /** The process failed and said, on stderr, that the dynamic loader could not load a shared library. */
    private final boolean loaderFailure;
    private final int stderrBytes;
    private final String stderrLine;
    private final byte[] stdout;
    private final boolean stdoutTruncated;

    Finished(int exitCode, byte[] stdout, boolean stdoutTruncated, int stderrBytes, String stderrLine,
      boolean loaderFailure) {
      this.loaderFailure = loaderFailure;
      this.stderrLine = stderrLine;
      this.exitCode = exitCode;
      this.stdout = stdout;
      this.stdoutTruncated = stdoutTruncated;
      this.stderrBytes = stderrBytes;
    }
  }

  /** Drains a stream on its own thread and keeps the first {@link #STREAM_CAP} bytes. */
  private static final class Capture implements Runnable {

    private final ByteArrayOutputStream kept = new ByteArrayOutputStream();
    private final InputStream source;
    private long seen;
    private final Thread thread;

    Capture(InputStream source, String name) {
      this.source = source;
      this.thread = new Thread(this, name);
      this.thread.setDaemon(true);
    }

    void start() {
      thread.start();
    }

    /** Waits briefly for the stream to end; a descendant that keeps it open must not hold the render. */
    void await() {
      try {
        thread.join(DRAIN_JOIN_MS);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }

    @Override
    public void run() {
      byte[] buffer = new byte[8192];
      try {
        int read = source.read(buffer);
        while (read >= 0) {
          synchronized (this) {
            int room = (int) Math.max(0L, Math.min(read, STREAM_CAP - seen));
            kept.write(buffer, 0, room);
            seen += read;
          }
          read = source.read(buffer);
        }
      } catch (IOException e) {
        // The process was killed or closed the stream: what was read is all there is.
        LOG.debug("PhantomJS stream closed, cause={}", e.getClass().getSimpleName());
      }
    }

    synchronized byte[] bytes() {
      return kept.toByteArray();
    }

    synchronized long length() {
      return seen;
    }
  }

  /**
   * Hosts a page may load resources from over https (CHG-LPRE-SEC-001, P-7); the page script enforces it. None: every
   * image of the templates ships inside MARLO and is inlined as a {@code data:} URI ({@link PdfImageBundle}, T17 and
   * T18), so the page script refuses every http(s) URL.
   */
  static final List<String> ALLOWED_HOSTS = Collections.emptyList();

  static final int STREAM_CAP = 64 * 1024;

  static final int MAX_STDERR_LINE = 200;

  static final long VERSION_TIMEOUT_MS = 5000L;

  static final String SUPPORT_PREFIX = "marlo-pdf-support-";

  static final String OWNER_FILE = "owner.pid";

  static final long MARKERLESS_MAX_AGE_HOURS = 24L;

  /** Support directories whose lock this JVM holds (real paths); the sweep never opens their lock files. */
  private static final Set<Path> HELD_HERE = ConcurrentHashMap.newKeySet();

  static final String NOT_EXECUTABLE_MESSAGE = "The PhantomJS shipped with MARLO cannot be executed from the temp "
    + "directory (mounted noexec?): set pdf.phantomjs.path or -Djava.io.tmpdir";

  /** What the dynamic loader prints when a shared library cannot be found or loaded. */
  static final String LOADER_FAILURE_TEXT = "error while loading shared libraries";

  /** The failure of every render, and of the start-up preparation, once neither library set lets PhantomJS start. */
  static final String LIBRARIES_UNAVAILABLE_MESSAGE =
    "PhantomJS does not start with the host libraries nor the shipped ones";

  private static final int DEFAULT_PAGE_TIMEOUT_MS = 30000;

  private static final long DRAIN_JOIN_MS = 2000L;

  private static final long REAP_WAIT_MS = 2000L;

  /** The fonts of production's {@code fonts-dejavu-core} 2.37: Debian's rules map serif and monospace to these. */
  static final List<String> FONT_FILES = Collections.unmodifiableList(Arrays.asList("DejaVuSans.ttf",
    "DejaVuSans-Bold.ttf", "DejaVuSerif.ttf", "DejaVuSerif-Bold.ttf", "DejaVuSansMono.ttf", "DejaVuSansMono-Bold.ttf"));

  private static final String FONT_DIR = "/pdf/fonts/";

  /**
   * A leading document type declaration, after optional whitespace. The page-font block goes right after it, or at the
   * very start when there is none (CHG-PPE-DD-3): never at a position found by searching the markup.
   */
  private static final Pattern LEADING_DOCTYPE = Pattern.compile("\\A\\s*<!DOCTYPE[^>]*>", Pattern.CASE_INSENSITIVE);

  /** Bytes of randomness in the family name of the page-font block (the name has twice as many hex digits). */
  private static final int PAGE_FONT_FAMILY_BYTES = 16;

  /** The page-font block, built on first use and kept for the life of the JVM (about 2 MB of text). */
  private static String pageFontBlock;

  private static final String SCRIPT = "/pdf/render-page.js";

  private static final String FONTCONFIG_DIR = "/pdf/fontconfig/";

  private static final Logger LOG = LoggerFactory.getLogger(PhantomJsRenderer.class);

  private static final ObjectMapper JSON = new ObjectMapper();

  /** Reads one JSON value that must end the text. */
  private static final com.fasterxml.jackson.databind.ObjectReader JSON_SINGLE =
    JSON.reader().with(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_TRAILING_TOKENS);

  private static final AtomicInteger THREAD_COUNTER = new AtomicInteger();

  private static final String[] URL_ATTRIBUTES = {"href", "src", "action", "formaction", "xlink:href", "data",
    "poster"};

  private static final String REMOVED_ELEMENTS =
    "script, noscript, noembed, noframes, template, iframe, frame, frameset, object, embed, applet, base, svg, "
      + "math, meta[http-equiv]";

  private static final Pattern REMOVED_TAG_TEXT = Pattern.compile(
    "<\\s*(?:script|noscript|noembed|noframes|template|iframe|frame|frameset|object|embed|applet|base|svg|math)\\b",
    Pattern.CASE_INSENSITIVE);

  /** Text that looks like an event handler attribute; see {@link #sanitize}. */
  private static final Pattern HANDLER_LIKE = Pattern.compile("[\\s/\"'<]on[a-z]+\\s*=", Pattern.CASE_INSENSITIVE);

  private static final Pattern ALLOWED_DATA = Pattern.compile(
    "^data:(?:image/(?!svg\\+xml)[a-z0-9.+-]+|font/[a-z0-9.+-]+|application/(?:x-)?font-[a-z0-9.+-]+)[;,]",
    Pattern.CASE_INSENSITIVE);

  /**
   * Removes what could run script or load an embedded document from the HTML (CHG-LPRE-SEC-003): {@code <script>},
   * {@code <noscript>} (WebKit reads its content as markup when scripting is on, jsoup does not), frames, objects,
   * embeds, applets, {@code <base>}, {@code <svg>}, {@code <math>}, {@code <noembed>}, {@code <noframes>},
   * {@code <template>} and {@code <meta http-equiv>}; every
   * {@code on*} attribute; {@code srcdoc}; and {@code href}/{@code src}/{@code action}/{@code formaction}/
   * {@code xlink:href}/{@code data}/{@code poster} values that start with {@code javascript:}, or with
   * {@code data:} unless it carries an image other than SVG or a font (whitespace and control characters, which
   * browsers ignore inside a scheme, are dropped first).
   * When there is nothing to remove and nothing looks like a handler ({@code onerror=} and the like, anywhere in the
   * text), the very same string comes back: re-serializing would change bytes, and the production templates contain
   * none of these, so their output stays identical.
   * <p>
   * jsoup and WebKit parse some inputs differently, above all foreign content ({@code <svg><style><img onerror>}),
   * which is why {@code <svg>} and {@code <math>} go too. PhantomJS 2.1.1 cannot run the page with JavaScript off
   * (see render-page.js), so this sanitizer is the only barrier against script in the markup; the request allowlist
   * is the second one, for what a script would fetch.
   */
  static String sanitize(String html) {
    if (html == null || html.isEmpty()) {
      return html;
    }
    Document document = Jsoup.parse(html);
    // The raw scan covers elements jsoup may fold away while WebKit would keep them.
    // Likewise, text that looks like a handler is never passed on as it came: whichever reading of it jsoup and WebKit
    // disagree on (noembed is raw text for jsoup only, say), what is sent is jsoup's own normalized markup.
    boolean changed = REMOVED_TAG_TEXT.matcher(html).find() || HANDLER_LIKE.matcher(html).find();
    List<Element> removed = new ArrayList<Element>(document.select(REMOVED_ELEMENTS));
    for (Element element : removed) {
      element.remove();
      changed = true;
    }
    for (Element element : document.getAllElements()) {
      List<String> remove = new ArrayList<String>();
      for (Attribute attribute : element.attributes()) {
        if (isDangerousAttribute(attribute)) {
          remove.add(attribute.getKey());
        }
      }
      for (String key : remove) {
        element.removeAttr(key);
        changed = true;
      }
    }
    if (!changed) {
      return html;
    }
    document.outputSettings().prettyPrint(false);
    return document.outerHtml();
  }

  /**
   * True when a {@code data:} URL carries an image other than SVG, or a font: the only data URLs a report needs.
   * Mirrors {@code isAllowedUrl} in render-page.js, without its size limit.
   */
  static boolean isAllowedDataUrl(String url) {
    return url != null && ALLOWED_DATA.matcher(url).find();
  }

  private static boolean isDangerousAttribute(Attribute attribute) {
    String name = attribute.getKey().toLowerCase(Locale.ROOT);
    if (name.startsWith("on") || "srcdoc".equals(name)) {
      return true;
    }
    for (String urlName : URL_ATTRIBUTES) {
      if (urlName.equals(name)) {
        String value = squeeze(attribute.getValue(), 64);
        return value.startsWith("javascript:") || value.startsWith("data:") && !isAllowedDataUrl(value);
      }
    }
    return false;
  }

  /** The value lowercased with whitespace and control characters removed, cut to {@code max} characters. */
  private static String squeeze(String value, int max) {
    if (value == null) {
      return "";
    }
    StringBuilder squeezed = new StringBuilder(Math.min(value.length(), max));
    for (int i = 0; i < value.length() && squeezed.length() < max; i++) {
      char c = value.charAt(i);
      if (c > ' ' && c != 0x7f) {
        squeezed.append(Character.toLowerCase(c));
      }
    }
    return squeezed.toString();
  }

  /**
   * Counts the pages of a PDF written by PhantomJS (Qt) by its page objects, {@code /Type /Page} and not
   * {@code /Type /Pages}. It only feeds the render's log line.
   */
  static int countPages(byte[] pdf) {
    byte[] marker = "/Type /Page".getBytes(StandardCharsets.ISO_8859_1);
    int count = 0;
    for (int i = 0; i + marker.length < pdf.length; i++) {
      if (pdf[i] != '/') {
        continue;
      }
      boolean same = true;
      for (int j = 1; j < marker.length && same; j++) {
        same = pdf[i + j] == marker[j];
      }
      if (same) {
        byte next = pdf[i + marker.length];
        if (next != 's' && !Character.isLetterOrDigit(next)) {
          count++;
        }
        i += marker.length - 1;
      }
    }
    return count;
  }

  private static byte[] readResource(String path) throws IOException {
    try (InputStream in = PhantomJsRenderer.class.getResourceAsStream(path)) {
      if (in == null) {
        throw new IOException("Missing PDF resource " + path);
      }
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      byte[] buffer = new byte[8192];
      int read = in.read(buffer);
      while (read >= 0) {
        out.write(buffer, 0, read);
        read = in.read(buffer);
      }
      return out.toByteArray();
    }
  }

  private static String xml(String text) {
    return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
  }

  private static void deleteTree(Path root) {
    if (root == null) {
      return;
    }
    try {
      Files.walkFileTree(root, new SimpleFileVisitor<Path>() {

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
          Files.deleteIfExists(file);
          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException failure) throws IOException {
          Files.deleteIfExists(dir);
          return FileVisitResult.CONTINUE;
        }
      });
    } catch (IOException e) {
      LOG.warn("PDF render directory could not be removed, cause={}", e.getClass().getSimpleName());
    }
  }

  /** Kills the process and every descendant it has, then waits briefly for the operating system to reap it. */
  private static void kill(Process process) {
    List<ProcessHandle> descendants = new ArrayList<ProcessHandle>();
    try {
      process.descendants().forEach(descendants::add);
    } catch (RuntimeException e) {
      LOG.debug("PhantomJS descendants could not be listed, cause={}", e.getClass().getSimpleName());
    }
    for (ProcessHandle descendant : descendants) {
      descendant.destroyForcibly();
    }
    process.destroyForcibly();
    try {
      process.waitFor(REAP_WAIT_MS, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  private final String binary;

  private final EmbeddedPhantomJs embedded;

  /** Reads one variable of this JVM's own environment (System.getenv in production); only ever read, never changed. */
  private final Function<String, String> hostEnvironment;

  private final PdfImageBundle images = PdfImageBundle.shared();

  /** Set when the shipped build could not be executed from the temp directory: later calls fail without extracting. */
  private boolean embeddedBlocked;

  private FileChannel ownerChannel;

  private Path ownerKey;

  private EmbeddedPhantomJs.Fingerprint fingerprint;

  /** Where the shipped build gets its shared libraries; decided once, when the first support directory is made. */
  private volatile LibraryMode libraryMode = LibraryMode.UNDECIDED;

  /** The state of the extracted libraries when they were verified; used only in {@link LibraryMode#BUNDLED}. */
  private Map<String, EmbeddedPhantomJs.Fingerprint> libraryFingerprints;

  private Path support;

  /**
   * @param binaryPath the PhantomJS executable ({@code pdf.phantomjs.path}); blank when none is configured, in
   *        which case the build shipped inside MARLO for this platform is used.
   */
  PhantomJsRenderer(String binaryPath) {
    this(binaryPath, EmbeddedPhantomJs.system());
  }

  /** Test seam: as above, with the shipped build (and the platform it is judged against) supplied. */
  PhantomJsRenderer(String binaryPath, EmbeddedPhantomJs embedded) {
    this(binaryPath, embedded, new Function<String, String>() {

      @Override
      public String apply(String name) {
        return System.getenv(name);
      }
    });
  }

  /** Test seam: as above, with the JVM's own environment supplied (only SystemRoot is read, on Windows). */
  PhantomJsRenderer(String binaryPath, EmbeddedPhantomJs embedded, Function<String, String> hostEnvironment) {
    this.binary = binaryPath == null ? "" : binaryPath.trim();
    this.embedded = embedded;
    this.hostEnvironment = hostEnvironment;
  }

  /** True when no path is configured and the shipped build applies to this platform (CHG-LPRE-T16). */
  boolean usesEmbeddedBinary() {
    return binary.isEmpty() && embedded.isSupportedPlatform();
  }

  /** The platform the shipped build is judged against, for log lines. */
  String platform() {
    return embedded.platform();
  }

  /**
   * Where the shipped build gets its shared libraries, for log lines: {@code host}, {@code bundled}, {@code
   * unavailable} (neither works) or {@code undecided} on a Linux build; {@code n/a} when no shipped libraries
   * apply, that is on another platform or with a configured path.
   */
  String libraryMode() {
    if (!usesShippedLibraries()) {
      return "n/a";
    }
    return libraryMode.name().toLowerCase(Locale.ROOT);
  }

  /** True when the shipped build is in use and has fallback libraries (Linux). */
  private boolean usesShippedLibraries() {
    return usesEmbeddedBinary() && embedded.shipsLibraries();
  }

  /** The directory of the extracted libraries for {@code LD_LIBRARY_PATH}; null unless they are in use. */
  private Path libraryDirectoryIn(Path dir) {
    return libraryMode == LibraryMode.BUNDLED ? dir.resolve(EmbeddedPhantomJs.LIBRARIES_DIR) : null;
  }

  /** Short name of the shipped build in use, for log lines; empty when a configured path or no build applies. */
  String buildName() {
    return usesEmbeddedBinary() ? embedded.build().map(PhantomJsBuild::id).orElse("") : "";
  }

  /**
   * The path of the executable in use, for log lines: the configured one, else the extracted shipped build once it
   * exists, else empty (no path configured and none to extract on this platform, or not extracted yet).
   */
  synchronized String binaryPath() {
    if (!binary.isEmpty()) {
      return binary;
    }
    return support != null && usesEmbeddedBinary() ? support.resolve(embedded.fileName()).toString() : "";
  }

  /**
   * Makes the executable ready: extracts and verifies the shipped build when it is the one in use. A configured
   * path needs no preparation.
   *
   * @throws PdfRenderException when the shipped build cannot be extracted or is not the expected one.
   */
  void prepare() throws PdfRenderException {
    if (usesEmbeddedBinary()) {
      supportDirectory();
    }
  }

  /** True when the executable in use is an executable file; a render without one fails at once. */
  boolean isBinaryUsable() {
    return isUsable(binaryPath());
  }

  private static boolean isUsable(String path) {
    if (path.isEmpty()) {
      return false;
    }
    Path file = Path.of(path);
    return Files.isRegularFile(file) && Files.isExecutable(file);
  }

  /** The executable for a render whose support directory is {@code dir}: the configured one, else the shipped. */
  private String executableIn(Path dir) {
    return binary.isEmpty() ? dir.resolve(embedded.fileName()).toString() : binary;
  }

  /**
   * Runs {@code <binary> --version} once (design section 18.1).
   *
   * @return the version line PhantomJS printed.
   * @throws PdfRenderException when there is no usable binary, it does not answer within
   *         {@value #VERSION_TIMEOUT_MS} ms, or it exits with an error.
   */
  String queryVersion() throws PdfRenderException {
    String executable;
    Path libraries = null;
    if (usesEmbeddedBinary()) {
      Path dir = supportDirectory();
      executable = executableIn(dir);
      libraries = libraryDirectoryIn(dir);
    } else {
      executable = binary;
    }
    if (!isUsable(executable)) {
      throw new PdfRenderException("PhantomJS binary is not available");
    }
    try {
      Finished finished = runVersion(executable, libraries);
      if (finished.exitCode != 0) {
        throw new ProcessFailedException("PhantomJS --version exited with code " + finished.exitCode,
          finished.exitCode, finished.stderrLine);
      }
      String text = new String(finished.stdout, StandardCharsets.UTF_8).trim();
      int end = text.indexOf('\n');
      return end < 0 ? text : text.substring(0, end).trim();
    } catch (IOException e) {
      throw new PdfRenderException("PhantomJS --version could not run (" + e.getClass().getSimpleName() + ")");
    }
  }

  /**
   * Runs {@code <executable> --version} in a private directory with the minimal environment, plus
   * {@code LD_LIBRARY_PATH=<libraries>} when {@code libraries} is not null; the variable exists in this child's
   * environment only.
   */
  private Finished runVersion(String executable, Path libraries) throws PdfRenderException, IOException {
    Path home = null;
    try {
      home = Files.createTempDirectory("marlo-pdf-version-");
      Map<String, String> environment = baseEnvironment(home);
      if (libraries != null) {
        environment.put("LD_LIBRARY_PATH", libraries.toString());
      }
      return run(Arrays.asList(executable, "--version"), home, environment, new byte[0],
        System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(VERSION_TIMEOUT_MS));
    } finally {
      deleteTree(home);
    }
  }

  /**
   * Renders one report.
   *
   * @param html the HTML Handlebars produced.
   * @param options the {@code options} object of the microservice payload (paper, header, footer, ...); it is passed
   *        on as html-pdf does, with {@code filename} and {@code allowedHosts} set here.
   * @param deadlineNanos {@link System#nanoTime()} value at which the process is killed.
   * @return the PDF and the counts of blocked and failed resource loads.
   * @throws PdfRenderException when the binary is missing, the process fails, or the deadline passes
   *         ({@link DeadlineExpiredException}). The message never holds HTML or PhantomJS output.
   */
  Output render(String html, Map<String, Object> options, long deadlineNanos) throws PdfRenderException {
    if (binary.isEmpty() ? !usesEmbeddedBinary() : !isBinaryUsable()) {
      throw new PdfRenderException("PhantomJS binary is not available");
    }
    Path supportDir = supportDirectory();
    // Both the command and FONTCONFIG_FILE come from this one directory, even if another thread rebuilds it later.
    String executable = executableIn(supportDir);
    if (!isUsable(executable)) {
      throw new PdfRenderException("PhantomJS binary is not available");
    }
    // Inlining follows the sanitizer on purpose: it is a deliberate transform of trusted, bundled bytes, done by string
    // replacement so the sanitizer's same-instance passthrough (and with it byte identity) is not disturbed.
    PdfImageBundle.Inlined inlined = images.inline(sanitize(html));
    String clean = inlined.html();
    try {
      if (clean != null && usesPageFonts()) {
        // Same reasoning as the images, and the font data URIs already pass the page script's allowlist: nothing
        // about the allowlist changes for them (CHG-PPE-SEC-003).
        clean = withPageFonts(clean);
      }
    } catch (IOException e) {
      throw new PdfRenderException("PhantomJS render could not complete (" + e.getClass().getSimpleName() + ")");
    }
    Path renderDir = null;
    try {
      renderDir = Files.createTempDirectory("marlo-pdf-render-");
      Path pdfFile = renderDir.resolve("report.pdf");
      Map<String, Object> sent = new LinkedHashMap<String, Object>();
      if (options != null) {
        sent.putAll(options);
      }
      sent.put("filename", pdfFile.toString());
      sent.put("timeout", pageTimeout(sent.get("timeout"), TimeUnit.NANOSECONDS.toMillis(deadlineNanos
        - System.nanoTime())));
      sent.put("allowedHosts", ALLOWED_HOSTS);
      sent.put("allowedDataUrls", new ArrayList<String>(inlined.svgDataUrls()));
      Map<String, Object> config = new LinkedHashMap<String, Object>();
      config.put("html", clean);
      config.put("options", sent);
      byte[] stdin = appendNewline(JSON.writeValueAsBytes(config));

      Map<String, String> environment = baseEnvironment(renderDir);
      if (usesFontconfig()) {
        environment.put("FONTCONFIG_FILE", supportDir.resolve("fonts.conf").toString());
      }
      // Only in the environment of this child, and only when the shipped libraries are the ones in use (FN-003).
      Path libraries = libraryDirectoryIn(supportDir);
      if (libraries != null) {
        environment.put("LD_LIBRARY_PATH", libraries.toString());
      }
      List<String> command = Arrays.asList(executable, "--local-url-access=false",
        supportDir.resolve("render-page.js").toString());
      Finished finished = run(command, renderDir, environment, stdin, deadlineNanos);
      if (finished.exitCode != 0) {
        throw new ProcessFailedException(
          "PhantomJS exited with code " + finished.exitCode + " (" + finished.stderrBytes + " bytes on stderr)",
          finished.exitCode, "");
      }
      int[] counts = parseCounts(finished);
      byte[] pdf = readPdf(pdfFile);
      return new Output(pdf, countPages(pdf), counts[0], counts[1]);
    } catch (IOException e) {
      throw new PdfRenderException("PhantomJS render could not complete (" + e.getClass().getSimpleName() + ")");
    } finally {
      deleteTree(renderDir);
    }
  }

  /**
   * Removes support directories that earlier JVMs left in the temp directory (a killed process never runs
   * {@link #close}); the service calls it once at start-up. See {@link #sweepStaleSupportDirectories}.
   */
  static int sweepStaleSupportDirectories() {
    return sweepStaleSupportDirectories(Path.of(System.getProperty("java.io.tmpdir")),
      java.lang.management.ManagementFactory.getRuntimeMXBean().getStartTime(), System.getProperty("user.name", ""));
  }

  /**
   * Deletes the {@code marlo-pdf-support-*} directories of {@code tmp} that are real directories (never links) owned
   * by {@code userName} and that no live instance holds.
   * <p>
   * Every renderer keeps an exclusive lock on the {@value #OWNER_FILE} file of its directory for as long as the
   * directory exists, and the operating system drops it when the process dies, however it dies. So a directory is
   * removed only when its lock can be taken here: that is true across restarts that reuse the same process id (a
   * container's JVM is always PID 1), and a directory of another process, container or process-id namespace sharing
   * the temp directory is never taken, which a process-id check could not promise. A directory this JVM holds is
   * skipped without opening its file (closing a second channel on a file can release the first one's lock on some
   * systems). The directory must also be older than {@code beforeMillis} (this JVM's start). A directory with no lock
   * file (an older build) is removed only when it is more than {@value #MARKERLESS_MAX_AGE_HOURS} hours old, because
   * nothing says whether its instance is alive.
   *
   * @return how many were removed.
   */
  static int sweepStaleSupportDirectories(Path tmp, long beforeMillis, String userName) {
    return sweepStaleSupportDirectories(tmp, beforeMillis, userName, hostEnvironmentStyle());
  }

  /** The environment style of the platform this JVM runs on; POSIX when no build ships for it. */
  private static PhantomJsBuild.EnvironmentStyle hostEnvironmentStyle() {
    return PhantomJsBuild.forPlatform(System.getProperty("os.name", ""), System.getProperty("os.arch", ""))
      .map(PhantomJsBuild::environmentStyle).orElse(PhantomJsBuild.EnvironmentStyle.POSIX);
  }

  /** Test seam: as above, with the platform's environment style supplied (it decides how owners are compared). */
  static int sweepStaleSupportDirectories(Path tmp, long beforeMillis, String userName,
    PhantomJsBuild.EnvironmentStyle style) {
    int removed = 0;
    long markerlessBefore = Math.min(beforeMillis,
      System.currentTimeMillis() - TimeUnit.HOURS.toMillis(MARKERLESS_MAX_AGE_HOURS));
    try (DirectoryStream<Path> candidates = Files.newDirectoryStream(tmp, SUPPORT_PREFIX + "*")) {
      for (Path candidate : candidates) {
        if (sweepIfStale(candidate, beforeMillis, markerlessBefore, userName, style)) {
          removed++;
        }
      }
    } catch (IOException | RuntimeException e) {
      LOG.debug("Stale PDF support directories could not be listed, cause={}", e.getClass().getSimpleName());
    }
    if (removed > 0) {
      LOG.info("Removed {} stale PDF support director{} left by earlier runs", removed, removed == 1 ? "y" : "ies");
    }
    return removed;
  }

  /**
   * Whether the owner of a directory is the user this JVM runs as. On Windows a leading {@code DOMAIN\} or
   * {@code MACHINE\} is dropped from both names first (the owner is reported with it, {@code user.name} normally
   * without); on every other platform the two are compared exactly as they are.
   */
  static boolean isSameOwner(String owner, String userName, PhantomJsBuild.EnvironmentStyle style) {
    if (style == PhantomJsBuild.EnvironmentStyle.WINDOWS) {
      return ownerName(owner).equals(ownerName(userName));
    }
    return userName.equals(owner);
  }

  /**
   * The owner's name without the {@code DOMAIN\} or {@code MACHINE\} prefix Windows puts on it, so that it compares
   * with {@code user.name}; a name without a prefix comes back as it is.
   */
  static String ownerName(String owner) {
    if (owner == null) {
      return "";
    }
    return owner.substring(owner.lastIndexOf('\\') + 1);
  }

  private static boolean sweepIfStale(Path dir, long beforeMillis, long markerlessBefore, String userName,
    PhantomJsBuild.EnvironmentStyle style) {
    try {
      if (!Files.isDirectory(dir, LinkOption.NOFOLLOW_LINKS)
        || !isSameOwner(Files.getOwner(dir, LinkOption.NOFOLLOW_LINKS).getName(), userName, style)) {
        return false;
      }
      long modified = Files.getLastModifiedTime(dir, LinkOption.NOFOLLOW_LINKS).toMillis();
      Path marker = dir.resolve(OWNER_FILE);
      if (!Files.isRegularFile(marker, LinkOption.NOFOLLOW_LINKS)) {
        if (modified >= markerlessBefore) {
          return false;
        }
      } else {
        if (modified >= beforeMillis || HELD_HERE.contains(dir.toRealPath())) {
          return false;
        }
        // Taken, then released before the delete (Windows cannot remove an open file); only a dead instance's passes.
        try (FileChannel channel = FileChannel.open(marker, StandardOpenOption.WRITE, LinkOption.NOFOLLOW_LINKS)) {
          FileLock lock = channel.tryLock();
          if (lock == null) {
            return false;
          }
          lock.release();
        } catch (OverlappingFileLockException e) {
          return false;
        }
      }
      deleteTree(dir);
      return !Files.exists(dir, LinkOption.NOFOLLOW_LINKS);
    } catch (IOException | RuntimeException e) {
      // Unreadable or locked in a way that cannot be told: not provably stale, so it stays.
      return false;
    }
  }

  /** Removes the private fonts and script directory. The service calls it on shutdown. */
  synchronized void close() {
    discardSupport();
  }

  /** Releases this instance's lock on its support directory, then removes the directory. */
  private void discardSupport() {
    releaseOwner();
    deleteTree(support);
    support = null;
  }

  private void releaseOwner() {
    if (ownerKey != null) {
      HELD_HERE.remove(ownerKey);
      ownerKey = null;
    }
    if (ownerChannel != null) {
      try {
        ownerChannel.close();
      } catch (IOException e) {
        LOG.debug("PDF support lock could not be closed, cause={}", e.getClass().getSimpleName());
      }
      ownerChannel = null;
    }
  }

  /** Creates the lock file of a support directory and locks it; a seam so that tests can make the lock fail. */
  interface OwnerLocker {

    /** @return the open channel holding an exclusive lock on {@code file}, which is created. */
    FileChannel lock(Path file) throws IOException;
  }

  private static final OwnerLocker FILE_LOCKER = new OwnerLocker() {

    @Override
    public FileChannel lock(Path file) throws IOException {
      FileChannel channel = FileChannel.open(file, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
      try {
        channel.write(ByteBuffer.wrap(Long.toString(ProcessHandle.current().pid()).getBytes(StandardCharsets.UTF_8)));
        channel.lock();
        return channel;
      } catch (IOException | RuntimeException e) {
        channel.close();
        throw e;
      }
    }
  };

  private OwnerLocker ownerLocker = FILE_LOCKER;

  /** Test seam: replaces the way the lock file is created and locked. */
  void setOwnerLocker(OwnerLocker locker) {
    this.ownerLocker = locker;
  }

  /**
   * Creates the lock file of a new support directory, writes the process id into it for whoever looks (the lock, not
   * the id, is what the sweep trusts) and takes an exclusive lock that lasts until {@link #releaseOwner}. The lock is
   * housekeeping and never gates a render: where the temp file system refuses locks (NFS without lockd, some FUSE
   * mounts) one WARN is logged and the directory is used unlocked. The directory is then not registered as held
   * here, which is harmless: the sweep's own {@code tryLock} on that file system fails too, and a failed lock leaves
   * the directory in place.
   */
  private void holdOwnerLock(Path dir) {
    try {
      ownerChannel = ownerLocker.lock(dir.resolve(OWNER_FILE));
      ownerKey = dir.toRealPath();
      HELD_HERE.add(ownerKey);
    } catch (IOException | RuntimeException e) {
      releaseOwner();
      LOG.warn("PDF support directory {} could not be locked ({}); stale-directory cleanup cannot protect this "
        + "directory", dir, e.getClass().getSimpleName());
    }
  }

  /**
   * html-pdf sends {@code parseInt(options.timeout, 10) || 30000} to its page script, which adds 2000 to it: a text
   * value such as {@code "300000"} would be concatenated there ({@code "3000002000"}) and end the page at once.
   * It is also cut to the time left before the render's deadline, so that an orphaned page script ends sooner.
   */
  static int pageTimeout(Object value, long remainingMillis) {
    int parsed = 0;
    if (value instanceof Number) {
      parsed = ((Number) value).intValue();
    } else if (value instanceof String) {
      try {
        parsed = Integer.parseInt(((String) value).trim());
      } catch (NumberFormatException e) {
        parsed = 0;
      }
    }
    int wanted = parsed > 0 ? parsed : DEFAULT_PAGE_TIMEOUT_MS;
    return (int) Math.max(1L, Math.min(wanted, remainingMillis));
  }

  /** First line of the bytes, without control characters, cut to {@value #MAX_STDERR_LINE} characters. */
  static String firstLine(byte[] text) {
    String all = new String(text, StandardCharsets.UTF_8);
    int end = all.indexOf('\n');
    String line = end < 0 ? all : all.substring(0, end);
    StringBuilder clean = new StringBuilder(Math.min(line.length(), MAX_STDERR_LINE));
    for (int i = 0; i < line.length() && clean.length() < MAX_STDERR_LINE; i++) {
      char c = line.charAt(i);
      if (c >= ' ' && c != 0x7f) {
        clean.append(c);
      }
    }
    return clean.toString().trim();
  }

  /** True when stderr holds the dynamic loader's message about a shared library; the text itself is not kept. */
  static boolean isLoaderFailure(byte[] stderr) {
    return new String(stderr, StandardCharsets.ISO_8859_1).contains(LOADER_FAILURE_TEXT);
  }

  private static byte[] appendNewline(byte[] json) {
    byte[] line = Arrays.copyOf(json, json.length + 1);
    line[json.length] = '\n';
    return line;
  }

  private static byte[] readPdf(Path pdfFile) throws PdfRenderException, IOException {
    if (!Files.isRegularFile(pdfFile) || Files.size(pdfFile) == 0L) {
      throw new PdfRenderException("PhantomJS produced no output file");
    }
    byte[] pdf = Files.readAllBytes(pdfFile);
    if (pdf.length < 5 || pdf[0] != '%' || pdf[1] != 'P' || pdf[2] != 'D' || pdf[3] != 'F') {
      throw new PdfRenderException("PhantomJS output is not a PDF");
    }
    return pdf;
  }

  /** The {@code blocked} and {@code failed} counts of the JSON the page script wrote to stdout. */
  private static int[] parseCounts(Finished finished) throws PdfRenderException {
    if (finished.stdoutTruncated) {
      throw new PdfRenderException("PhantomJS wrote more than " + STREAM_CAP + " bytes to stdout");
    }
    // PhantomJS also prints the page's own script errors to stdout (a malformed inline handler, say), ahead of the
    // script's JSON; html-pdf skips such chunks too, so only what starts at the last {"filename": is parsed.
    String text = new String(finished.stdout, StandardCharsets.UTF_8);
    int start = text.lastIndexOf("{\"filename\":");
    JsonNode root;
    try {
      root = JSON_SINGLE.readTree(start < 0 ? text : text.substring(start));
    } catch (IOException | RuntimeException e) {
      throw new PdfRenderException("PhantomJS stdout is not valid JSON");
    }
    if (root == null || !root.isObject()) {
      throw new PdfRenderException("PhantomJS stdout is not a JSON object");
    }
    return new int[] {Math.max(0, root.path("blocked").asInt(0)), Math.max(0, root.path("failed").asInt(0))};
  }

  /** The Windows fallback when this JVM's environment has no SystemRoot (it always has one on a real Windows). */
  static final String DEFAULT_SYSTEM_ROOT = "C:\\Windows";

  /**
   * The environment style follows the platform MARLO runs on, whether or not a path is configured; a platform with
   * no shipped build keeps the POSIX environment of CHG-LPRE.
   */
  private boolean isWindowsStyle() {
    return embedded.build().map(b -> b.environmentStyle() == PhantomJsBuild.EnvironmentStyle.WINDOWS).orElse(false);
  }

  /**
   * The block that puts MARLO's DejaVu Sans, regular and bold, in the page (CHG-PPE-FN-005, DD-3): two
   * {@code @font-face} rules over {@code data:font/truetype} URIs of the bundled files, and one universal
   * {@code font-family} rule with {@code !important}, so that DejaVu wins over every font the template declares
   * without {@code !important}, {@code Montserrat} included. Built once per JVM, under a family name with a random
   * suffix chosen at that moment, so that no markup can name it ahead of time (CHG-PPE-SEC-003).
   */
  static synchronized String pageFontBlock() throws IOException {
    if (pageFontBlock == null) {
      String regular = fontDataUri("DejaVuSans.ttf");
      String bold = fontDataUri("DejaVuSans-Bold.ttf");
      byte[] random = new byte[PAGE_FONT_FAMILY_BYTES];
      new SecureRandom().nextBytes(random);
      StringBuilder family = new StringBuilder("pf");
      for (byte b : random) {
        family.append(String.format("%02x", b));
      }
      pageFontBlock = "<style type=\"text/css\">"
        + "@font-face{font-family:'" + family + "';font-style:normal;font-weight:normal;"
        + "src:url(" + regular + ") format('truetype');}"
        + "@font-face{font-family:'" + family + "';font-style:normal;font-weight:bold;"
        + "src:url(" + bold + ") format('truetype');}"
        + "*{font-family:'" + family + "' !important;}"
        + "</style>";
    }
    return pageFontBlock;
  }

  private static String fontDataUri(String fileName) throws IOException {
    return "data:font/truetype;base64,"
      + Base64.getEncoder().encodeToString(readResource(FONT_DIR + fileName));
  }

  /**
   * Places {@link #pageFontBlock} in already sanitized markup: right after a leading {@code <!DOCTYPE ...>} (leading
   * whitespace allowed), or at the very start of the document when there is none. The position never depends on the
   * template's or the payload's content past that prefix: a {@code </head>} held in a style or an attribute value
   * cannot move the block into the middle of it. A trusted insertion after the sanitizer.
   */
  static String withPageFonts(String sanitized) throws IOException {
    String block = pageFontBlock();
    Matcher doctype = LEADING_DOCTYPE.matcher(sanitized);
    int at = doctype.find() ? doctype.end() : 0;
    return sanitized.substring(0, at) + block + sanitized.substring(at);
  }

  /** True on macOS and Windows: the fonts are declared in the page itself. No build for the platform: no. */
  private boolean usesPageFonts() {
    return embedded.build().map(b -> b.fontMode() == PhantomJsBuild.FontMode.PAGE).orElse(false);
  }

  /** True on Linux and on a platform with no shipped build: the isolated fontconfig applies (macOS and Windows: no). */
  private boolean usesFontconfig() {
    return embedded.build().map(b -> b.fontMode() == PhantomJsBuild.FontMode.LINUX_FONTCONFIG).orElse(true);
  }

  /** The environment of every child process, in the style of the platform this JVM runs on. */
  Map<String, String> baseEnvironment(Path home) {
    Map<String, String> environment = new LinkedHashMap<String, String>();
    if (isWindowsStyle()) {
      String systemRoot = hostEnvironment.apply("SystemRoot");
      if (systemRoot == null || systemRoot.trim().isEmpty()) {
        systemRoot = DEFAULT_SYSTEM_ROOT;
      }
      // The Windows loader needs SystemRoot; PATH is the system directory only; NUL is OpenSSL's empty configuration.
      environment.put("SystemRoot", systemRoot);
      environment.put("PATH", systemRoot + "\\System32");
      environment.put("OPENSSL_CONF", "NUL");
      environment.put("USERPROFILE", home.toString());
      environment.put("TEMP", home.toString());
      environment.put("TMP", home.toString());
      environment.put("LANG", "C.UTF-8");
      return environment;
    }
    environment.put("PATH", "/usr/bin:/bin");
    // PhantomJS's bundled OpenSSL cannot parse the openssl.cnf of current Linux distributions and exits at start-up.
    environment.put("OPENSSL_CONF", "/dev/null");
    environment.put("HOME", home.toString());
    environment.put("TMPDIR", home.toString());
    environment.put("LANG", "C.UTF-8");
    return environment;
  }

  /**
   * The directory with the DejaVu fonts, the generated fonts.conf, the page script and, when the shipped build is in
   * use, the extracted executable; made once.
   */
  private synchronized Path supportDirectory() throws PdfRenderException {
    if (embeddedBlocked) {
      throw new PdfRenderException(NOT_EXECUTABLE_MESSAGE);
    }
    if (libraryMode == LibraryMode.UNAVAILABLE) {
      throw new PdfRenderException(LIBRARIES_UNAVAILABLE_MESSAGE);
    }
    if (support != null && isComplete(support) && isExecutableIntact(support) && areLibrariesIntact(support)) {
      return support;
    }
    discardSupport();
    Path dir = null;
    try {
      dir = Files.createTempDirectory(SUPPORT_PREFIX);
      // Locked for as long as the directory exists, so that another instance's start-up sweep leaves it alone.
      holdOwnerLock(dir);
      Path fonts = Files.createDirectory(dir.resolve("fonts"));
      for (String font : FONT_FILES) {
        Files.write(fonts.resolve(font), readResource(FONT_DIR + font));
      }
      Path cache = Files.createDirectory(dir.resolve("cache"));
      Files.write(dir.resolve("render-page.js"), readResource(SCRIPT));
      Path rules = Files.createDirectory(dir.resolve("conf.d"));
      extractRules(rules);
      Files.write(dir.resolve("fonts.conf"), fontsConf(fonts, rules, cache).getBytes(StandardCharsets.UTF_8));
      if (usesEmbeddedBinary()) {
        try {
          fingerprint = embedded.fingerprint(embedded.extractTo(dir));
        } catch (EmbeddedPhantomJs.NotExecutableException e) {
          embeddedBlocked = true;
          LOG.warn("The PhantomJS shipped with MARLO cannot be executed from the temp directory {} (temp dir mounted "
            + "noexec?): set pdf.phantomjs.path or -Djava.io.tmpdir; local PDF renders will fail",
            dir.getParent());
          throw new PdfRenderException(NOT_EXECUTABLE_MESSAGE);
        } catch (IOException e) {
          throw new PdfRenderException("The PhantomJS shipped with MARLO could not be extracted or is not the expected "
            + "build (" + e.getClass().getSimpleName() + ")");
        }
        if (embedded.shipsLibraries()) {
          prepareLibraries(dir);
        }
      }
      support = dir;
      return dir;
    } catch (PdfRenderException e) {
      releaseOwner();
      deleteTree(dir);
      throw e;
    } catch (IOException e) {
      releaseOwner();
      deleteTree(dir);
      throw new PdfRenderException("PhantomJS support files could not be prepared (" + e.getClass().getSimpleName()
        + ")");
    }
  }

  /**
   * Makes the libraries of the decided mode ready in {@code dir}. The first time (nothing decided yet) it decides:
   * <ol>
   * <li>{@code --version} with the host's libraries. It works, or fails for a reason other than a missing shared
   * library (or cannot be judged): {@link LibraryMode#HOST}, as CHG-LPRE, and the start-up version check reports
   * whatever else is wrong.</li>
   * <li>The loader says a library is missing: the shipped ones are extracted and verified, and {@code --version} runs
   * again with {@code LD_LIBRARY_PATH} set for that process only. It works: {@link LibraryMode#BUNDLED}, with one
   * INFO. It does not (glibc older than 2.36, say), or the libraries cannot be extracted or are not the expected
   * ones: {@link LibraryMode#UNAVAILABLE}, with one WARN, and this and every later call throw.</li>
   * </ol>
   * When the directory is made again in {@link LibraryMode#BUNDLED}, the libraries are extracted and verified again.
   */
  private void prepareLibraries(Path dir) throws PdfRenderException {
    if (libraryMode == LibraryMode.HOST) {
      return;
    }
    if (libraryMode == LibraryMode.BUNDLED) {
      extractLibraries(dir);
      return;
    }
    String executable = executableIn(dir);
    Finished host;
    try {
      host = runVersion(executable, null);
    } catch (IOException | PdfRenderException e) {
      LOG.debug("PhantomJS --version with the host libraries could not be judged, cause={}",
        e.getClass().getSimpleName());
      libraryMode = LibraryMode.HOST;
      return;
    }
    if (host.exitCode == 0 || !host.loaderFailure) {
      libraryMode = LibraryMode.HOST;
      return;
    }
    try {
      extractLibraries(dir);
    } catch (PdfRenderException e) {
      libraryMode = LibraryMode.UNAVAILABLE;
      LOG.warn("{}, platform={}: local PDF renders will fail", e.getMessage(), embedded.platform());
      throw new PdfRenderException(LIBRARIES_UNAVAILABLE_MESSAGE);
    }
    Finished bundled = null;
    try {
      bundled = runVersion(executable, dir.resolve(EmbeddedPhantomJs.LIBRARIES_DIR));
    } catch (IOException | PdfRenderException e) {
      LOG.debug("PhantomJS --version with the shipped libraries could not run, cause={}",
        e.getClass().getSimpleName());
    }
    if (bundled != null && bundled.exitCode == 0) {
      libraryMode = LibraryMode.BUNDLED;
      LOG.info("PhantomJS does not start with the host libraries; using the libraries shipped with MARLO");
      return;
    }
    libraryMode = LibraryMode.UNAVAILABLE;
    LOG.warn("{} (glibc older than 2.36?), platform={}: local PDF renders will fail", LIBRARIES_UNAVAILABLE_MESSAGE,
      embedded.platform());
    throw new PdfRenderException(LIBRARIES_UNAVAILABLE_MESSAGE);
  }

  /** Extracts the shipped libraries into {@code <dir>/libs}, each checked against its recorded SHA-256 first. */
  private void extractLibraries(Path dir) throws PdfRenderException {
    try {
      Path libraries = Files.createDirectory(dir.resolve(EmbeddedPhantomJs.LIBRARIES_DIR));
      libraryFingerprints = embedded.extractLibrariesTo(libraries);
    } catch (IOException e) {
      throw new PdfRenderException("The libraries shipped with MARLO could not be extracted or are not the expected "
        + "ones (" + e.getClass().getSimpleName() + ")");
    }
  }

  /**
   * True when the shipped libraries are not in use, or the directory still holds exactly them with their recorded
   * bytes: the same cheap re-check as the executable's, and the same re-hash of what no longer matches it.
   */
  private boolean areLibrariesIntact(Path dir) {
    return libraryMode != LibraryMode.BUNDLED
      || embedded.verifyLibraries(dir.resolve(EmbeddedPhantomJs.LIBRARIES_DIR), libraryFingerprints);
  }

  /**
   * True when the shipped executable is not in use, or is still there and executable. The common case costs three
   * attribute reads (size, modification time, file key against those recorded at extraction); only a file that no
   * longer matches is hashed again, and one that still has the expected bytes is accepted with a new record.
   */
  private boolean isExecutableIntact(Path dir) {
    if (!usesEmbeddedBinary()) {
      return true;
    }
    Path file = dir.resolve(embedded.fileName());
    if (embedded.isUnchanged(file, fingerprint)) {
      return true;
    }
    if (!embedded.isIntact(file)) {
      return false;
    }
    try {
      fingerprint = embedded.fingerprint(file);
      return true;
    } catch (IOException e) {
      return false;
    }
  }

  /** True while the key files of the support directory are all there (a temp-directory cleaner may take them). */
  private static boolean isComplete(Path dir) {
    for (String font : FONT_FILES) {
      if (!Files.isRegularFile(dir.resolve("fonts").resolve(font))) {
        return false;
      }
    }
    return Files.isRegularFile(dir.resolve("fonts.conf")) && Files.isRegularFile(dir.resolve("render-page.js"))
      && Files.isDirectory(dir.resolve("conf.d")) && Files.isDirectory(dir.resolve("cache"));
  }

  /**
   * The fontconfig of the production image (Debian bookworm's rules, see pdf/fontconfig/README.txt) with its font
   * directory, cache and {@code conf.d} replaced by the private ones. Without those rules Qt lays text out with
   * different metrics and the pages break elsewhere.
   */
  private static String fontsConf(Path fonts, Path confDir, Path cache) throws IOException {
    String template = new String(readResource(FONTCONFIG_DIR + "fonts.conf.template"), StandardCharsets.UTF_8);
    return template.replace("@FONT_DIR@", xml(fonts.toString())).replace("@CONF_DIR@", xml(confDir.toString()))
      .replace("@CACHE_DIR@", xml(cache.toString()));
  }

  /** Copies the rule files listed in conf.list into {@code confDir}. */
  private static void extractRules(Path confDir) throws IOException {
    String list = new String(readResource(FONTCONFIG_DIR + "conf.list"), StandardCharsets.UTF_8);
    for (String name : list.split("\\R")) {
      if (!name.trim().isEmpty()) {
        Files.write(confDir.resolve(name.trim()), readResource(FONTCONFIG_DIR + "conf.d/" + name.trim()));
      }
    }
  }

  /**
   * Starts the process and waits for it until the deadline. Stdin is written, and stdout and stderr are drained, on
   * their own daemon threads so that a process that reads nothing, or writes without end, cannot hold this thread.
   * Past the deadline, or when this thread is interrupted, the process and its descendants are killed.
   */
  private Finished run(List<String> command, Path directory, Map<String, String> environment, final byte[] stdin,
    long deadlineNanos) throws PdfRenderException {
    if (deadlineNanos - System.nanoTime() <= 0L) {
      throw new DeadlineExpiredException("The render deadline passed before PhantomJS was started");
    }
    ProcessBuilder builder = new ProcessBuilder(command);
    builder.directory(directory.toFile());
    builder.environment().clear();
    builder.environment().putAll(environment);
    final Process process;
    try {
      process = builder.start();
    } catch (IOException e) {
      throw new PdfRenderException("PhantomJS could not be started (" + e.getClass().getSimpleName() + ")");
    }
    int id = THREAD_COUNTER.incrementAndGet();
    Capture out = new Capture(process.getInputStream(), "pdf-phantom-out-" + id);
    Capture err = new Capture(process.getErrorStream(), "pdf-phantom-err-" + id);
    Thread writer = new Thread(new Runnable() {

      @Override
      public void run() {
        try (OutputStream to = process.getOutputStream()) {
          to.write(stdin);
        } catch (IOException e) {
          // The process left without reading everything: its exit code says what happened.
          LOG.debug("PhantomJS stdin closed, cause={}", e.getClass().getSimpleName());
        }
      }
    }, "pdf-phantom-in-" + id);
    writer.setDaemon(true);
    out.start();
    err.start();
    writer.start();
    boolean finishedInTime = false;
    try {
      long remaining = deadlineNanos - System.nanoTime();
      finishedInTime = remaining > 0L && process.waitFor(remaining, TimeUnit.NANOSECONDS);
      if (!finishedInTime) {
        kill(process);
        throw new DeadlineExpiredException("PhantomJS was killed at the render deadline");
      }
      out.await();
      err.await();
      int exitCode = process.exitValue();
      return new Finished(exitCode, out.bytes(), out.length() > STREAM_CAP, (int) err.length(),
        firstLine(err.bytes()), exitCode != 0 && isLoaderFailure(err.bytes()));
    } catch (InterruptedException e) {
      kill(process);
      Thread.currentThread().interrupt();
      throw new PdfRenderException("PDF render interrupted while PhantomJS was running");
    } finally {
      if (process.isAlive()) {
        kill(process);
      }
    }
  }
}
