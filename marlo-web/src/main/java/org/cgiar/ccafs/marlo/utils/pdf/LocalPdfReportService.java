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
import java.io.OutputStream;
import java.io.Reader;
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
import java.util.logging.Level;

import javax.annotation.PreDestroy;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.HandlebarsError;
import com.github.jknack.handlebars.HandlebarsException;
import com.github.jknack.handlebars.Template;
import com.openhtmltopdf.extend.FSStream;
import com.openhtmltopdf.extend.FSStreamFactory;
import com.openhtmltopdf.extend.FSSupplier;
import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder.FontStyle;
import com.openhtmltopdf.pdfboxout.PdfBoxRenderer;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.openhtmltopdf.util.XRLog;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Renders a MARLO report to PDF inside the application, from the same JSON payload the summary actions already send
 * to the reports microservice (CHG-LPRE-FN-001, FN-002, design sections 7.1, 7.4, 7.5 and 10): Handlebars fills the
 * template, {@link HtmlPdfCompatibility} reshapes the HTML the way html-pdf did, and OpenHTMLtoPDF renders it with the
 * bundled DejaVu fonts.
 * <p>
 * It is a container singleton shared by concurrent requests and holds no per-request state. Renders run on a
 * dedicated pool of {@value #POOL_SIZE} daemon threads with a queue of {@value #QUEUE_CAPACITY}; a full queue fails
 * the call at once, and a caller that waits longer than its deadline gets a {@link PdfRenderException} (the task is
 * cancelled and removed from the queue). Both are logged at ERROR, as every failed render is. Layout cannot be
 * interrupted, so a render that finishes after its caller gave up logs a warning with its total time. Every
 * resource the renderer would load goes through {@link PdfResourceLoader}.
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

  /** The only text the user sees when a render fails. */
  public static final String GENERIC_ERROR_MESSAGE = "The PDF report could not be generated.";

  static final int POOL_SIZE = 3;

  static final int QUEUE_CAPACITY = 6;

  static final int TEMPLATE_CACHE_SIZE = 8;

  private static final String BASE_URI = "https://localhost/";

  private static final String DEFAULT_FILE_NAME = "report.pdf";

  private static final String FONT_REGULAR = "/pdf/fonts/DejaVuSans.ttf";

  private static final String FONT_BOLD = "/pdf/fonts/DejaVuSans-Bold.ttf";

  private static final String[] FONT_FAMILIES = {"sans-serif", "Montserrat", "DejaVu Sans"};

  private static final int DOTS_PER_PIXEL = 27;

  private static final int MAX_LOGGED_FINDINGS = 5;

  private static final int MAX_LOGGED_NAME = 120;

  private static final Logger LOG = LoggerFactory.getLogger(LocalPdfReportService.class);

  private static final ObjectMapper PAYLOAD_PARSER =
    new ObjectMapper().enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);

  /** Strong references: java.util.logging keeps loggers weakly, and a collected logger forgets its level. */
  private static final Map<String, java.util.logging.Logger> RENDERER_LOGGERS =
    new LinkedHashMap<String, java.util.logging.Logger>();

  private static final FSStream NO_RESOURCE = new FSStream() {

    @Override
    public Reader getReader() {
      return null;
    }

    @Override
    public InputStream getStream() {
      return null;
    }
  };

  /** What the caller learns about one render. */
  private static final class RenderResult {

    private final byte[] bytes;
    private final int pages;

    RenderResult(byte[] bytes, int pages) {
      this.bytes = bytes;
      this.pages = pages;
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
    private final String footerHeight;
    private final String headerHeight;
    private final boolean nonA4Format;
    private final boolean nullKey;
    private final String orientation;
    private final String templateSource;

    Payload(String templateSource, Map<String, Object> data, String fileName, String headerHeight,
      String footerHeight, String orientation, boolean nonA4Format, boolean nullKey) {
      this.templateSource = templateSource;
      this.data = data;
      this.fileName = fileName;
      this.headerHeight = headerHeight;
      this.footerHeight = footerHeight;
      this.orientation = orientation;
      this.nonA4Format = nonA4Format;
      this.nullKey = nullKey;
    }
  }

  /** One render on the pool. */
  private final class RenderTask implements Callable<RenderResult> {

    private final AtomicBoolean abandoned = new AtomicBoolean();
    private final String fileName;
    private final Payload payload;
    private final PdfResourceLoader.Session session;
    private final long submittedNanos = System.nanoTime();

    RenderTask(Payload payload, String fileName, PdfResourceLoader.Session session) {
      this.payload = payload;
      this.fileName = fileName;
      this.session = session;
    }

    @Override
    public RenderResult call() throws Exception {
      try {
        return renderNow(payload, session);
      } finally {
        if (abandoned.get()) {
          LOG.warn("PDF render finished after its caller gave up, file={}, totalMs={}, blockedUris={}", fileName,
            elapsedMillis(submittedNanos), session.getBlockedCount());
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

  /**
   * Quiets OpenHTMLtoPDF's own logging (design section 7.4): every category it registers is set to SEVERE, denying
   * by default. Its WARNING messages quote what the template holds: the full URI of a resource that failed to load
   * (for a {@code data:} image, the image itself, up to 2 MB per use) and the href of a link whose target is missing
   * or that is not a valid URI, all of it user-supplied text; the css-parse category adds per-render noise nobody
   * can act on. A failed resource already renders as empty and is counted in the render line. The one INFO line per
   * render is ours, not theirs.
   */
  private static synchronized void quietRendererLogging() {
    for (String name : XRLog.listRegisteredLoggers()) {
      XRLog.setLevel(name, Level.SEVERE);
      if (!RENDERER_LOGGERS.containsKey(name)) {
        RENDERER_LOGGERS.put(name, java.util.logging.Logger.getLogger(name));
      }
    }
  }

  private static byte[] readResource(String path) {
    try (InputStream in = LocalPdfReportService.class.getResourceAsStream(path)) {
      if (in == null) {
        throw new IllegalStateException("Missing PDF font resource " + path);
      }
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      byte[] buffer = new byte[8192];
      int read = in.read(buffer);
      while (read >= 0) {
        out.write(buffer, 0, read);
        read = in.read(buffer);
      }
      return out.toByteArray();
    } catch (IOException e) {
      throw new IllegalStateException("Cannot read PDF font resource " + path, e);
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

  private static String nestedString(Map<String, Object> options, String key, String child) {
    Map<String, Object> nested = options == null ? null : mapOrNull(options.get(key));
    return nested == null ? null : stringOrNull(nested.get(child));
  }

  /** True when a JSON object at any depth of the value has a key that is literally {@code null}. */
  static boolean hasNullKey(Object root) {
    Deque<Object> pending = new ArrayDeque<Object>();
    pending.push(root);
    while (!pending.isEmpty()) {
      Object current = pending.pop();
      if (current instanceof Map) {
        for (Map.Entry<?, ?> entry : ((Map<?, ?>) current).entrySet()) {
          if ("null".equals(entry.getKey())) {
            return true;
          }
          pending.push(entry.getValue());
        }
      } else if (current instanceof Iterable) {
        for (Object element : (Iterable<?>) current) {
          pending.push(element);
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
    String format = options == null ? null : stringOrNull(options.get("format"));
    boolean nonA4 = format != null && !format.trim().isEmpty() && !"A4".equalsIgnoreCase(format.trim());
    return new Payload(templateSource, data, sanitizeName(stringOrNull(envelope.get("fileName"))),
      nestedString(options, "header", "height"), nestedString(options, "footer", "height"),
      options == null ? null : stringOrNull(options.get("orientation")), nonA4, hasNullKey(data));
  }

  private final byte[] boldFont;

  private final Handlebars handlebars;

  private final PdfResourceLoader loader;

  private final byte[] regularFont;

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

  /** The constructor the container uses. */
  public LocalPdfReportService() {
    this(new PdfResourceLoader());
  }

  /** Test seam: the loader decides which resources a render may fetch, and how. */
  LocalPdfReportService(PdfResourceLoader loader) {
    this.loader = loader;
    this.handlebars = HandlebarsReportHelpers.newHandlebars();
    this.regularFont = readResource(FONT_REGULAR);
    this.boldFont = readResource(FONT_BOLD);
    quietRendererLogging();
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
   * {@code MicroserviceReportAction.downloadPDFByURL} sets. A render failure answers HTTP 500 with
   * {@link #GENERIC_ERROR_MESSAGE} when the response is not committed yet; the cause is in the log. A client that
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
          response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, GENERIC_ERROR_MESSAGE);
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
  private RenderResult renderNow(Payload payload, PdfResourceLoader.Session session) throws IOException {
    CompiledTemplate compiled = compile(payload.templateSource);
    warnAboutLimits(payload, compiled);
    String html = HandlebarsReportHelpers.apply(compiled.template, payload.data);
    org.w3c.dom.Document document = HtmlPdfCompatibility.toW3cDocument(html, payload.headerHeight,
      payload.footerHeight, payload.orientation);
    return renderPdf(document, session);
  }

  private RenderResult renderPdf(org.w3c.dom.Document document, PdfResourceLoader.Session session)
    throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream(64 * 1024);
    PdfRendererBuilder builder = new PdfRendererBuilder();
    builder.useFastMode();
    builder.withW3cDocument(document, BASE_URI);
    for (String family : FONT_FAMILIES) {
      builder.useFont(fontSupplier(regularFont), family, 400, FontStyle.NORMAL, true);
      builder.useFont(fontSupplier(boldFont), family, 700, FontStyle.NORMAL, true);
    }
    builder.useUriResolver(session.uriResolver());
    builder.useHttpStreamImplementation(neverNull(session.httpStreamFactory()));
    builder.useProtocolsStreamImplementation(neverNull(session.refusingStreamFactory()), "file", "jar", "ftp");
    builder.toStream(out);
    try (PdfBoxRenderer renderer = builder.buildPdfRenderer()) {
      renderer.getSharedContext().setDotsPerPixel(DOTS_PER_PIXEL);
      renderer.layout();
      renderer.createPDFWithoutClosing();
      PDDocument pdf = renderer.getPdfDocument();
      int pages = pdf.getNumberOfPages();
      pdf.save(out);
      return new RenderResult(out.toByteArray(), pages);
    }
  }

  /**
   * The renderer dereferences what a stream factory returns without a null check (NaiveUserAgent.openStream), so a
   * refused or failed resource, which the loader reports as {@code null}, would fail the whole render. An FSStream
   * without a stream is what the renderer reads as "resource not found": the element renders empty.
   */
  private static FSStreamFactory neverNull(final FSStreamFactory inner) {
    return new FSStreamFactory() {

      @Override
      public FSStream getUrl(String url) {
        FSStream stream = inner.getUrl(url);
        return stream == null ? NO_RESOURCE : stream;
      }
    };
  }

  private static FSSupplier<InputStream> fontSupplier(final byte[] font) {
    return new FSSupplier<InputStream>() {

      @Override
      public InputStream supply() {
        return new ByteArrayInputStream(font);
      }
    };
  }

  /** One WARN per render for what is outside the supported subset (design section 7.1); nothing when clean. */
  private void warnAboutLimits(Payload payload, CompiledTemplate compiled) {
    if (payload.nonA4Format) {
      LOG.warn("PDF page format other than A4 requested; rendering A4");
    }
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
    PdfResourceLoader.Session session = null;
    try {
      session = loader.newSession();
      Payload payload = parse(json);
      if (fileNameOverride == null) {
        fileName = payload.fileName;
      }
      RenderTask task = new RenderTask(payload, fileName, session);
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
        throw toLogSafe(e.getCause() == null ? e : e.getCause());
      }
      outcome = "ok";
      return result.bytes;
    } catch (PdfRenderException e) {
      if ("failed".equals(outcome)) {
        LOG.error("PDF render failed, file={}", fileName, e);
      } else {
        // A rejected or timed-out render has no cause to trace, but it is still an error to find in the log.
        LOG.error("PDF render {}, file={}: {}", outcome, fileName, e.getMessage());
      }
      throw e;
    } finally {
      LOG.info("PDF render file={} outcome={} bytes={} pages={} elapsedMs={} blockedUris={} failedFetches={}",
        fileName, outcome, result == null ? 0 : result.bytes.length, result == null ? 0 : result.pages,
        elapsedMillis(start), session == null ? 0 : session.getBlockedCount(),
        session == null ? 0 : session.getFailedFetchCount());
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
