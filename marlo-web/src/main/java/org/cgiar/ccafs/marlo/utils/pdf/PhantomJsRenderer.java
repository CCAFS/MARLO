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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
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
    private final int stderrBytes;
    private final String stderrLine;
    private final byte[] stdout;
    private final boolean stdoutTruncated;

    Finished(int exitCode, byte[] stdout, boolean stdoutTruncated, int stderrBytes, String stderrLine) {
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

  /** Hosts a page may load resources from over https (CHG-LPRE-SEC-001, P-7); the page script enforces it. */
  static final List<String> ALLOWED_HOSTS = Collections.unmodifiableList(Arrays
    .asList("marlo-pdf-resources-dev.s3.us-east-1.amazonaws.com", "prms-file-storage.s3.amazonaws.com"));

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

  private static final int DEFAULT_PAGE_TIMEOUT_MS = 30000;

  private static final long DRAIN_JOIN_MS = 2000L;

  private static final long REAP_WAIT_MS = 2000L;

  private static final String FONT_REGULAR = "/pdf/fonts/DejaVuSans.ttf";

  private static final String FONT_BOLD = "/pdf/fonts/DejaVuSans-Bold.ttf";

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

  /** Set when the shipped build could not be executed from the temp directory: later calls fail without extracting. */
  private boolean embeddedBlocked;

  private FileChannel ownerChannel;

  private Path ownerKey;

  private EmbeddedPhantomJs.Fingerprint fingerprint;

  private Path support;

  /**
   * @param binaryPath the PhantomJS executable ({@code pdf.phantomjs.path}); blank when none is configured, in
   *        which case the build shipped inside MARLO is used on Linux x86_64.
   */
  PhantomJsRenderer(String binaryPath) {
    this(binaryPath, EmbeddedPhantomJs.system());
  }

  /** Test seam: as above, with the shipped build (and the platform it is judged against) supplied. */
  PhantomJsRenderer(String binaryPath, EmbeddedPhantomJs embedded) {
    this.binary = binaryPath == null ? "" : binaryPath.trim();
    this.embedded = embedded;
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
   * The path of the executable in use, for log lines: the configured one, else the extracted shipped build once it
   * exists, else empty (no path configured and none to extract on this platform, or not extracted yet).
   */
  synchronized String binaryPath() {
    if (!binary.isEmpty()) {
      return binary;
    }
    return support != null && usesEmbeddedBinary() ? support.resolve(EmbeddedPhantomJs.FILE_NAME).toString() : "";
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
    return binary.isEmpty() ? dir.resolve(EmbeddedPhantomJs.FILE_NAME).toString() : binary;
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
    if (usesEmbeddedBinary()) {
      executable = executableIn(supportDirectory());
    } else {
      executable = binary;
    }
    if (!isUsable(executable)) {
      throw new PdfRenderException("PhantomJS binary is not available");
    }
    Path home = null;
    try {
      home = Files.createTempDirectory("marlo-pdf-version-");
      Map<String, String> environment = baseEnvironment(home);
      Finished finished = run(Arrays.asList(executable, "--version"), home, environment, new byte[0],
        System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(VERSION_TIMEOUT_MS));
      if (finished.exitCode != 0) {
        throw new ProcessFailedException("PhantomJS --version exited with code " + finished.exitCode,
          finished.exitCode, finished.stderrLine);
      }
      String text = new String(finished.stdout, StandardCharsets.UTF_8).trim();
      int end = text.indexOf('\n');
      return end < 0 ? text : text.substring(0, end).trim();
    } catch (IOException e) {
      throw new PdfRenderException("PhantomJS --version could not run (" + e.getClass().getSimpleName() + ")");
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
    String clean = sanitize(html);
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
      Map<String, Object> config = new LinkedHashMap<String, Object>();
      config.put("html", clean);
      config.put("options", sent);
      byte[] stdin = appendNewline(JSON.writeValueAsBytes(config));

      Map<String, String> environment = baseEnvironment(renderDir);
      environment.put("FONTCONFIG_FILE", supportDir.resolve("fonts.conf").toString());
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
    int removed = 0;
    long markerlessBefore = Math.min(beforeMillis,
      System.currentTimeMillis() - TimeUnit.HOURS.toMillis(MARKERLESS_MAX_AGE_HOURS));
    try (DirectoryStream<Path> candidates = Files.newDirectoryStream(tmp, SUPPORT_PREFIX + "*")) {
      for (Path candidate : candidates) {
        if (sweepIfStale(candidate, beforeMillis, markerlessBefore, userName)) {
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

  private static boolean sweepIfStale(Path dir, long beforeMillis, long markerlessBefore, String userName) {
    try {
      if (!Files.isDirectory(dir, LinkOption.NOFOLLOW_LINKS)
        || !userName.equals(Files.getOwner(dir, LinkOption.NOFOLLOW_LINKS).getName())) {
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

  private Map<String, String> baseEnvironment(Path home) {
    Map<String, String> environment = new LinkedHashMap<String, String>();
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
    if (support != null && isComplete(support) && isExecutableIntact(support)) {
      return support;
    }
    discardSupport();
    Path dir = null;
    try {
      dir = Files.createTempDirectory(SUPPORT_PREFIX);
      // Locked for as long as the directory exists, so that another instance's start-up sweep leaves it alone.
      holdOwnerLock(dir);
      Path fonts = Files.createDirectory(dir.resolve("fonts"));
      Files.write(fonts.resolve("DejaVuSans.ttf"), readResource(FONT_REGULAR));
      Files.write(fonts.resolve("DejaVuSans-Bold.ttf"), readResource(FONT_BOLD));
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
   * True when the shipped executable is not in use, or is still there and executable. The common case costs three
   * attribute reads (size, modification time, file key against those recorded at extraction); only a file that no
   * longer matches is hashed again, and one that still has the expected bytes is accepted with a new record.
   */
  private boolean isExecutableIntact(Path dir) {
    if (!usesEmbeddedBinary()) {
      return true;
    }
    Path file = dir.resolve(EmbeddedPhantomJs.FILE_NAME);
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
    return Files.isRegularFile(dir.resolve("fonts/DejaVuSans.ttf"))
      && Files.isRegularFile(dir.resolve("fonts/DejaVuSans-Bold.ttf")) && Files.isRegularFile(dir.resolve("fonts.conf"))
      && Files.isRegularFile(dir.resolve("render-page.js")) && Files.isDirectory(dir.resolve("conf.d"))
      && Files.isDirectory(dir.resolve("cache"));
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
      return new Finished(process.exitValue(), out.bytes(), out.length() > STREAM_CAP, (int) err.length(),
        firstLine(err.bytes()));
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
