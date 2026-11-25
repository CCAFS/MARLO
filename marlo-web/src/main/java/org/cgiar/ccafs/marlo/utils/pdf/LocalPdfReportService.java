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
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.HandlebarsError;
import com.github.jknack.handlebars.HandlebarsException;
import com.github.jknack.handlebars.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Renders a MARLO report to PDF inside the application, from the same JSON payload the summary actions already send
 * to the reports microservice (CHG-LPRE-FN-001, FN-002, design sections 7.1, 7.5, 10 and 18): Handlebars fills the
 * template and {@link PhantomJsRenderer} turns the HTML into a PDF with PhantomJS 2.1.1 and html-pdf's page script,
 * the engine of the microservice, as a local process.
 * <p>
 * It is a container singleton shared by concurrent requests and holds no per-request state. Renders run on a
 * dedicated pool of {@value #POOL_SIZE} daemon threads with a queue of {@value #QUEUE_CAPACITY}; a full queue fails
 * the call at once, and a caller that waits longer than its deadline gets a {@link PdfRenderException} (the task is
 * cancelled and removed from the queue, and the PhantomJS process is killed). Both are logged at ERROR, as every
 * failed render is. A render that ends after its caller gave up logs a warning with its total time. The PhantomJS
 * binary is the one shipped with MARLO (one per supported platform) unless {@code pdf.phantomjs.path} names another
 * one (an override, and the only way on a platform with no build); it is checked once at startup, and a render without
 * a usable binary fails at once (CHG-LPRE-NF-007).
 * <p>
 * Nothing in a log line or in an error message comes from the template or from the payload data: a failure is
 * reported by exception class (and the line and column of a template error) with the stack trace of the root cause.
 */
@Named
public class LocalPdfReportService {

  /** Total time a caller waits on the submission path; equal to {@code SUBMIT_TOTAL_DEADLINE_MS} there. */
  public static final long SUBMIT_DEADLINE_MS = 30000L;

  /** Total time a caller waits on a download, below the common 60 s proxy idle timeout. */
  public static final long DOWNLOAD_DEADLINE_MS = 50000L;

  static final int POOL_SIZE = 3;

  static final int QUEUE_CAPACITY = 6;

  static final int TEMPLATE_CACHE_SIZE = 8;

  private static final String DEFAULT_FILE_NAME = "report.pdf";

  /** The version the production microservice runs, and the only one the output is known to match. */
  static final String EXPECTED_VERSION = "2.1.1";

  /**
   * The caller's deadline is the authority: it times out, cancels the task and interrupts the thread, which kills
   * PhantomJS. The renderer's own deadline is later by this margin, so it only acts when that interrupt did not.
   */
  private static final long RENDERER_GRACE_MS = 500L;

  private static final int MAX_LOGGED_FINDINGS = 5;

  private static final int MAX_LOGGED_NAME = 120;

  private static final Logger LOG = LoggerFactory.getLogger(LocalPdfReportService.class);

  private static final ObjectMapper PAYLOAD_PARSER =
    new ObjectMapper().enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);

  /** What the caller learns about one render. */
  private static final class RenderResult {

    private final int blocked;
    private final byte[] bytes;
    private final int failed;
    private final int pages;

    RenderResult(byte[] bytes, int pages, int blocked, int failed) {
      this.bytes = bytes;
      this.pages = pages;
      this.blocked = blocked;
      this.failed = failed;
    }
  }

  /** A template compiled once, with what the subset guard found in its source. */
  private static final class CompiledTemplate {

    private final List<String> unsupported;
    private final Template template;

    CompiledTemplate(Template template, List<String> unsupported) {
      this.template = template;
      this.unsupported = unsupported;
    }
  }

  /** The parts of the microservice payload the local engine reads. */
  private static final class Payload {

    private final Map<String, Object> data;
    private final String fileName;
    private final boolean nullKey;
    private final Map<String, Object> options;
    private final String templateSource;

    Payload(String templateSource, Map<String, Object> data, String fileName, Map<String, Object> options,
      boolean nullKey) {
      this.templateSource = templateSource;
      this.data = data;
      this.fileName = fileName;
      this.options = options;
      this.nullKey = nullKey;
    }
  }

  /** One render on the pool. */
  private final class RenderTask implements Callable<RenderResult> {

    private final AtomicBoolean abandoned = new AtomicBoolean();
    private final long deadlineNanos;
    private final String fileName;
    private final Payload payload;
    private final long submittedNanos = System.nanoTime();

    RenderTask(Payload payload, String fileName, long deadlineNanos) {
      this.payload = payload;
      this.fileName = fileName;
      this.deadlineNanos = deadlineNanos;
    }

    @Override
    public RenderResult call() throws Exception {
      RenderResult result = null;
      try {
        result = renderNow(payload, deadlineNanos);
        return result;
      } finally {
        if (abandoned.get()) {
          LOG.warn("PDF render finished after its caller gave up, file={}, totalMs={}, blockedUris={}", fileName,
            elapsedMillis(submittedNanos), result == null ? 0 : result.blocked);
        }
      }
    }
  }

  private static final class DaemonThreads implements ThreadFactory {

    private final AtomicInteger counter = new AtomicInteger();

    @Override
    public Thread newThread(Runnable runnable) {
      Thread thread = new Thread(runnable, "pdf-render-" + counter.incrementAndGet());
      thread.setDaemon(true);
      return thread;
    }
  }

  private static String sha256(String text) {
    try {
      byte[] digest = MessageDigest.getInstance("SHA-256").digest(text.getBytes(StandardCharsets.UTF_8));
      StringBuilder hex = new StringBuilder(digest.length * 2);
      for (byte b : digest) {
        hex.append(String.format("%02x", b & 0xff));
      }
      return hex.toString();
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA-256 is not available", e);
    }
  }

  private static long elapsedMillis(long startNanos) {
    return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);
  }

  /** A value fit for a log line or a header: control characters and quotes become underscores, length is capped. */
  private static String sanitizeName(String value) {
    if (value == null || value.trim().isEmpty()) {
      return DEFAULT_FILE_NAME;
    }
    StringBuilder clean = new StringBuilder(Math.min(value.length(), MAX_LOGGED_NAME));
    for (int i = 0; i < value.length() && clean.length() < MAX_LOGGED_NAME; i++) {
      char c = value.charAt(i);
      clean.append(c < 0x20 || c == 0x7f || c == '"' || c == '\\' ? '_' : c);
    }
    return clean.toString();
  }

  private static boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }

  private static String stringOrNull(Object value) {
    return value instanceof String ? (String) value : null;
  }

  @SuppressWarnings("unchecked")
  private static Map<String, Object> mapOrNull(Object value) {
    return value instanceof Map ? (Map<String, Object>) value : null;
  }

  /**
   * True when a JSON object at any depth of the value has a key that is literally {@code null}. A JSON {@code null}
   * value or list element is ordinary data (unset report fields serialize that way) and is skipped.
   */
  static boolean hasNullKey(Object root) {
    Deque<Object> pending = new ArrayDeque<Object>();
    if (root != null) {
      pending.push(root);
    }
    while (!pending.isEmpty()) {
      Object current = pending.pop();
      if (current instanceof Map) {
        for (Map.Entry<?, ?> entry : ((Map<?, ?>) current).entrySet()) {
          if ("null".equals(entry.getKey())) {
            return true;
          }
          if (entry.getValue() != null) {
            pending.push(entry.getValue());
          }
        }
      } else if (current instanceof Iterable) {
        for (Object element : (Iterable<?>) current) {
          if (element != null) {
            pending.push(element);
          }
        }
      }
    }
    return false;
  }

  /** Parses the microservice payload (design section 7.1). The messages name the problem, never the content. */
  private static Payload parse(String json) throws PdfRenderException {
    if (isBlank(json)) {
      throw new PdfRenderException("PDF payload is empty");
    }
    Map<String, Object> root;
    try {
      root = PAYLOAD_PARSER.readValue(json, new TypeReference<Map<String, Object>>() {});
    } catch (IOException | RuntimeException e) {
      throw new PdfRenderException("PDF payload is not a valid JSON object (" + e.getClass().getSimpleName() + ")");
    }
    Map<String, Object> envelope = root == null ? null : mapOrNull(root.get("data"));
    if (envelope == null) {
      throw new PdfRenderException("PDF payload has no data object");
    }
    String templateSource = stringOrNull(envelope.get("templateData"));
    if (isBlank(templateSource)) {
      throw new PdfRenderException("PDF payload has no templateData");
    }
    Map<String, Object> data = mapOrNull(envelope.get("data"));
    if (data == null) {
      throw new PdfRenderException("PDF payload data.data is not an object");
    }
    Map<String, Object> options = mapOrNull(envelope.get("options"));
    return new Payload(templateSource, data, sanitizeName(stringOrNull(envelope.get("fileName"))),
      options == null ? new LinkedHashMap<String, Object>() : options, hasNullKey(data));
  }

  private APConfig config;

  private final Handlebars handlebars;

  private volatile PhantomJsRenderer renderer;

  private final EmbeddedPhantomJs embedded;

  private final Map<String, CompiledTemplate> templates =
    new LinkedHashMap<String, CompiledTemplate>(16, 0.75f, true) {

      private static final long serialVersionUID = 1L;

      @Override
      protected boolean removeEldestEntry(Map.Entry<String, CompiledTemplate> eldest) {
        return size() > TEMPLATE_CACHE_SIZE;
      }
    };

  private final ThreadPoolExecutor executor = new ThreadPoolExecutor(POOL_SIZE, POOL_SIZE, 0L,
    TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(QUEUE_CAPACITY), new DaemonThreads());

  /**
   * The constructor the container uses. The optional override path arrives with {@link #setConfig(APConfig)}, and
   * the startup check runs once the bean is complete ({@link #checkBinary()}). An empty path means the PhantomJS
   * shipped with MARLO for the platform; on another platform with no path, a render fails at once.
   */
  public LocalPdfReportService() {
    this(EmbeddedPhantomJs.system());
  }

  /** Test seam: the container's constructor with the shipped PhantomJS (and its platform) supplied. */
  LocalPdfReportService(EmbeddedPhantomJs embedded) {
    this.handlebars = HandlebarsReportHelpers.newHandlebars();
    this.embedded = embedded;
    this.renderer = new PhantomJsRenderer("", embedded);
  }

  /** Test seam: the binary to run, checked at once as the container does after construction. */
  LocalPdfReportService(String phantomJsPath) {
    this(phantomJsPath, HandlebarsReportHelpers.newHandlebars());
  }

  /** Test seam: as above, with the Handlebars instance that compiles the templates. */
  LocalPdfReportService(String phantomJsPath, Handlebars handlebars) {
    this(phantomJsPath, handlebars, EmbeddedPhantomJs.system());
  }

  /** Test seam: as above, with the shipped PhantomJS (and its platform) supplied. */
  LocalPdfReportService(String phantomJsPath, Handlebars handlebars, EmbeddedPhantomJs embedded) {
    this.handlebars = handlebars;
    this.embedded = embedded;
    this.renderer = new PhantomJsRenderer(phantomJsPath, embedded);
    checkBinary();
  }

  @Inject
  public void setConfig(APConfig config) {
    this.config = config;
  }

  /**
   * Startup check, once (CHG-LPRE-NF-007, design section 18.1): INFO with the version when the configured binary
   * answers {@code --version} and is the one the output is known to match, WARN when the path is empty, missing, not
   * executable, silent, or another version. It never throws: the service starts either way, and a render without a
   * usable binary fails at once.
   */
  @PostConstruct
  void initFromConfig() {
    if (config != null) {
      PhantomJsRenderer previous = renderer;
      renderer = new PhantomJsRenderer(config.getPdfPhantomjsPath(), embedded);
      previous.close();
    }
    checkBinary();
  }

  void checkBinary() {
    PhantomJsRenderer.sweepStaleSupportDirectories();
    PhantomJsRenderer current = renderer;
    if (current.usesEmbeddedBinary()) {
      try {
        current.prepare();
      } catch (PdfRenderException e) {
        if (PhantomJsRenderer.NOT_EXECUTABLE_MESSAGE.equals(e.getMessage())
          || PhantomJsRenderer.LIBRARIES_UNAVAILABLE_MESSAGE.equals(e.getMessage())) {
          // The renderer has already logged why: the temp directory and the hint, or the libraries and the glibc one.
          return;
        }
        LOG.warn("The PhantomJS shipped with MARLO could not be prepared: {}: local PDF renders will fail",
          e.getMessage());
        return;
      }
    } else if (current.binaryPath().isEmpty()) {
      LOG.warn("PhantomJS is not configured (pdf.phantomjs.path is empty) and none ships for {}: local PDF renders "
        + "will fail", current.platform());
      return;
    }
    if (!current.isBinaryUsable()) {
      LOG.warn("PhantomJS binary is missing or not executable, path={}: local PDF renders will fail",
        current.binaryPath());
      return;
    }
    try {
      String version = current.queryVersion();
      if (EXPECTED_VERSION.equals(version)) {
        LOG.info("PhantomJS available for local PDF renders, version={}, platform={}, build={}, libraries={}, path={}",
          version, current.platform(), current.buildName().isEmpty() ? "configured" : current.buildName(),
          current.libraryMode(), current.binaryPath());
      } else {
        LOG.warn("PhantomJS version is {} but the output is only known to match {}, path={}", sanitizeName(version),
          EXPECTED_VERSION, current.binaryPath());
      }
    } catch (PhantomJsRenderer.ProcessFailedException e) {
      LOG.warn("PhantomJS failed the version check, path={}, exitCode={}, stderr={}", current.binaryPath(),
        e.exitCode(), e.stderrLine());
    } catch (PdfRenderException e) {
      LOG.warn("PhantomJS did not answer the version check, path={}: {}", current.binaryPath(), e.getMessage());
    }
  }

  /**
   * Renders the report described by the microservice payload.
   *
   * @param payload the JSON the summary actions send to the microservice.
   * @param deadlineMillis the longest the caller waits, from the call: {@link #SUBMIT_DEADLINE_MS} or
   *        {@link #DOWNLOAD_DEADLINE_MS}.
   * @return the PDF bytes.
   * @throws PdfRenderException when the payload is invalid, the pool is full, the deadline passes or the render
   *         fails. The message never holds template or payload text.
   */
  public byte[] render(String payload, long deadlineMillis) throws PdfRenderException {
    return renderLogged(payload, deadlineMillis, null);
  }

  /**
   * Renders the report and writes it to the response as an inline PDF, with the headers
   * {@code MicroserviceReportAction.downloadPDFByURL} sets. A render failure answers HTTP 500 with no reason text
   * when the response is not committed yet; the cause is in the log. A client that
   * aborts is logged at DEBUG; any other write failure propagates.
   *
   * @param payload the JSON the summary actions send to the microservice.
   * @param fileName the name for {@code Content-Disposition}.
   * @param response the response to write.
   * @throws IOException when writing the response fails for a reason other than a client abort.
   */
  public void renderToResponse(String payload, String fileName, HttpServletResponse response) throws IOException {
    String name = sanitizeName(fileName);
    byte[] pdf;
    try {
      pdf = renderLogged(payload, DOWNLOAD_DEADLINE_MS, name);
    } catch (PdfRenderException e) {
      if (!response.isCommitted()) {
        try {
          response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (IOException io) {
          rethrowUnlessClientAbort(io, name);
        }
      }
      return;
    }
    response.setContentType("application/pdf");
    response.setHeader("Content-Disposition", "inline; filename=\"" + name + "\"");
    response.setContentLength(pdf.length);
    try (OutputStream out = response.getOutputStream()) {
      out.write(pdf);
      out.flush();
    } catch (IOException e) {
      rethrowUnlessClientAbort(e, name);
    }
  }

  /** Stops the pool; running renders are interrupted and queued ones are cancelled. The container calls it. */
  @PreDestroy
  public void shutdown() {
    for (Runnable never : executor.shutdownNow()) {
      if (never instanceof Future) {
        ((Future<?>) never).cancel(true);
      }
    }
    renderer.close();
  }

  /** Renders waiting in the queue, for the tests. */
  int queuedTaskCount() {
    return executor.getQueue().size();
  }

  /** Templates held by the compile cache, for the tests. */
  int cachedTemplateCount() {
    synchronized (templates) {
      return templates.size();
    }
  }

  private CompiledTemplate compile(String source) throws IOException {
    String key = sha256(source);
    synchronized (templates) {
      CompiledTemplate known = templates.get(key);
      if (known != null) {
        return known;
      }
    }
    CompiledTemplate fresh = new CompiledTemplate(handlebars.compileInline(source),
      ReportTemplateSubset.findUnsupported(source));
    synchronized (templates) {
      templates.put(key, fresh);
    }
    return fresh;
  }

  /** The render itself, on a pool thread. */
  private RenderResult renderNow(Payload payload, long deadlineNanos) throws IOException, PdfRenderException {
    CompiledTemplate compiled = compile(payload.templateSource);
    warnAboutLimits(payload, compiled);
    String html = HandlebarsReportHelpers.apply(compiled.template, payload.data);
    PhantomJsRenderer.Output output = renderer.render(html, payload.options,
      deadlineNanos + TimeUnit.MILLISECONDS.toNanos(RENDERER_GRACE_MS));
    return new RenderResult(output.pdf, output.pages, output.blocked, output.failed);
  }

  /** One WARN per render for what is outside the supported subset (design section 7.1); nothing when clean. */
  private void warnAboutLimits(Payload payload, CompiledTemplate compiled) {
    if (compiled.unsupported.isEmpty() && !payload.nullKey) {
      return;
    }
    List<String> shown = compiled.unsupported.size() > MAX_LOGGED_FINDINGS
      ? compiled.unsupported.subList(0, MAX_LOGGED_FINDINGS) : compiled.unsupported;
    LOG.warn("PDF render outside the supported template subset, file={}, templateFindings={} {}, "
      + "payloadKeyNamedNull={}", payload.fileName, compiled.unsupported.size(), shown, payload.nullKey);
  }

  private byte[] renderLogged(String json, long deadlineMillis, String fileNameOverride) throws PdfRenderException {
    long start = System.nanoTime();
    String fileName = fileNameOverride == null ? DEFAULT_FILE_NAME : fileNameOverride;
    String outcome = "failed";
    RenderResult result = null;
    try {
      Payload payload = parse(json);
      if (fileNameOverride == null) {
        fileName = payload.fileName;
      }
      RenderTask task = new RenderTask(payload, fileName, start + TimeUnit.MILLISECONDS.toNanos(deadlineMillis));
      Future<RenderResult> future;
      try {
        future = executor.submit(task);
      } catch (RejectedExecutionException e) {
        outcome = "rejected";
        throw new PdfRenderException(executor.isShutdown() ? "PDF render rejected: the render service is shut down"
          : "PDF render rejected: the render queue is full");
      }
      try {
        result = future.get(Math.max(1L, deadlineMillis), TimeUnit.MILLISECONDS);
      } catch (TimeoutException e) {
        task.abandoned.set(true);
        if (!future.cancel(true)) {
          // The render finished between the wait and the cancel: take its outcome rather than discard a done PDF.
          result = finishedResult(future);
        } else {
          executor.remove((Runnable) future);
          outcome = "timeout";
          throw new PdfRenderException("PDF render timed out after " + deadlineMillis + " ms");
        }
      } catch (InterruptedException e) {
        task.abandoned.set(true);
        future.cancel(true);
        executor.remove((Runnable) future);
        Thread.currentThread().interrupt();
        throw new PdfRenderException("PDF render interrupted while waiting");
      } catch (CancellationException e) {
        throw new PdfRenderException("PDF render cancelled");
      } catch (ExecutionException e) {
        if (e.getCause() instanceof PhantomJsRenderer.DeadlineExpiredException) {
          outcome = "timeout";
          throw new PdfRenderException("PDF render timed out after " + deadlineMillis + " ms");
        }
        throw toLogSafe(e.getCause() == null ? e : e.getCause());
      }
      outcome = "ok";
      return result.bytes;
    } catch (PdfRenderException e) {
      if (e instanceof PhantomJsRenderer.ProcessFailedException) {
        // Only the exit code: a render's stderr is the page script's and can quote page content. The startup
        // check, whose stderr is the binary's own, logs its first line.
        PhantomJsRenderer.ProcessFailedException failed = (PhantomJsRenderer.ProcessFailedException) e;
        LOG.error("PDF render failed, file={}, phantomExit={}", fileName, failed.exitCode(), e);
      } else if ("failed".equals(outcome)) {
        LOG.error("PDF render failed, file={}", fileName, e);
      } else {
        // A rejected or timed-out render has no cause to trace, but it is still an error to find in the log.
        LOG.error("PDF render {}, file={}: {}", outcome, fileName, e.getMessage());
      }
      throw e;
    } catch (RuntimeException e) {
      // Anything unexpected on the render path (parse, compile, layout) is a failed render, never an unhandled one.
      PdfRenderException renderException = toLogSafe(e);
      LOG.error("PDF render failed, file={}", fileName, renderException);
      throw renderException;
    } finally {
      LOG.info("PDF render file={} outcome={} bytes={} pages={} elapsedMs={} blockedUris={} failedFetches={}",
        fileName, outcome, result == null ? 0 : result.bytes.length, result == null ? 0 : result.pages,
        elapsedMillis(start), result == null ? 0 : result.blocked, result == null ? 0 : result.failed);
    }
  }

  /** The outcome of a future that is already done, as the result or the failure the caller would have got. */
  private static RenderResult finishedResult(Future<RenderResult> future) throws PdfRenderException {
    try {
      return future.get();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new PdfRenderException("PDF render interrupted while waiting");
    } catch (CancellationException e) {
      throw new PdfRenderException("PDF render cancelled");
    } catch (ExecutionException e) {
      throw toLogSafe(e.getCause() == null ? e : e.getCause());
    }
  }

  /**
   * The failure to throw and log for an unexpected one: exception class names (and the line and column of a template
   * error), the stack trace of the root cause, and no message, since messages of Handlebars, jsoup and the renderer
   * quote template or payload text.
   */
  private static PdfRenderException toLogSafe(Throwable failure) {
    if (failure instanceof PdfRenderException) {
      return (PdfRenderException) failure;
    }
    StringBuilder description = new StringBuilder("PDF render failed: ");
    Throwable root = failure;
    String position = "";
    int depth = 0;
    for (Throwable t = failure; t != null && depth < 10; t = t.getCause(), depth++) {
      description.append(depth == 0 ? "" : " <- ").append(t.getClass().getName());
      root = t;
      if (position.isEmpty() && t instanceof HandlebarsException) {
        HandlebarsError error = ((HandlebarsException) t).getError();
        if (error != null) {
          position = " (template line " + error.line + ", column " + error.column + ")";
        }
      }
    }
    PdfRenderException safe = new PdfRenderException(description.append(position).toString());
    safe.setStackTrace(root.getStackTrace());
    return safe;
  }

  private static boolean isClientAbort(Throwable failure) {
    int depth = 0;
    for (Throwable t = failure; t != null && depth < 10; t = t.getCause(), depth++) {
      if (t.getClass().getName().contains("ClientAbortException")) {
        return true;
      }
    }
    return false;
  }

  private static void rethrowUnlessClientAbort(IOException failure, String fileName) throws IOException {
    if (isClientAbort(failure)) {
      LOG.debug("Client aborted the PDF download, file={}", fileName);
      return;
    }
    throw failure;
  }
}
