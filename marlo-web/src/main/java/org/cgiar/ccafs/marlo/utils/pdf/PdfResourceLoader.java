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
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongSupplier;

import com.openhtmltopdf.extend.FSStream;
import com.openhtmltopdf.extend.FSStreamFactory;
import com.openhtmltopdf.extend.FSUriResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The single decision point for every resource the PDF renderer would load (CHG-LPRE-SEC-001, design section 11),
 * plus the fetching and caching behind it. Rich text reaches the report templates unescaped, so without this check
 * a crafted {@code <img src>} would make the server request internal addresses (SSRF).
 * <p>
 * Allowed: {@code data:} URIs up to 2 MB, and {@code https} URIs with no user info on the default port whose
 * lowercased host is exactly one of {@link #ALLOWED_HOSTS}. Everything else is refused: the renderer gets nothing and
 * the element renders empty. Redirects are never followed. The instance is a singleton shared by concurrent renders;
 * each render works through its own {@link Session}.
 */
class PdfResourceLoader {

  /** Opens the connection for an already validated URI. Replaceable so tests can see what is requested. */
  interface ConnectionFactory {

    HttpURLConnection open(URI uri) throws IOException;
  }

  /** Fetch time one render may still spend. Not shared between renders. */
  static final class FetchBudget {

    private final AtomicLong remainingNanos;

    FetchBudget(long millis) {
      this.remainingNanos = new AtomicLong(millis * 1000000L);
    }

    void consume(long nanos) {
      remainingNanos.addAndGet(-nanos);
    }

    boolean isExhausted() {
      return remainingNanos.get() <= 0;
    }
  }

  /**
   * The state of one render: its fetch budget and its count of refused resources. Not shared between renders.
   */
  final class Session {

    private final AtomicLong blocked = new AtomicLong();
    private final FetchBudget budget;

    private Session(FetchBudget budget) {
      this.budget = budget;
    }

    /**
     * Fetches an allowlisted resource.
     *
     * @return the body, or {@code null} when the resource is refused, the budget is spent, the fetch failed or the
     *         response is not a 200 within the size limit.
     */
    byte[] fetch(String uri) {
      URI allowed = allowedHttpsUri(uri);
      if (allowed == null) {
        countBlocked(uri);
        return null;
      }
      String key = allowed.toString();
      long now = nanoClock.getAsLong();
      synchronized (cache) {
        CacheEntry cached = cache.get(key);
        if (cached != null) {
          if (now - cached.storedAtNanos <= CACHE_TTL_NANOS) {
            return cached.body;
          }
          removeFromCache(key);
        }
        Long failedAt = failures.get(key);
        if (failedAt != null) {
          if (now - failedAt <= FAILURE_TTL_NANOS) {
            return null;
          }
          failures.remove(key);
        }
      }
      if (budget.isExhausted()) {
        LOG.debug("PDF resource skipped, fetch budget spent, host={}", allowed.getHost());
        return null;
      }
      byte[] body;
      try {
        body = download(allowed);
      } finally {
        budget.consume(nanoClock.getAsLong() - now);
      }
      long finished = nanoClock.getAsLong();
      synchronized (cache) {
        if (body == null) {
          rememberFailure(key, finished);
        } else {
          store(key, body, finished);
        }
      }
      return body;
    }

    /** Number of resources refused during this render, for the render log line. */
    int getBlockedCount() {
      return (int) blocked.get();
    }

    /** Hook for {@code useHttpStreamImplementation}: allowlisted fetches only; refused ones yield {@code null}. */
    FSStreamFactory httpStreamFactory() {
      return new FSStreamFactory() {

        @Override
        public FSStream getUrl(String url) {
          final byte[] body = fetch(url);
          if (body == null) {
            return null;
          }
          return new FSStream() {

            @Override
            public Reader getReader() {
              return new InputStreamReader(new ByteArrayInputStream(body), StandardCharsets.UTF_8);
            }

            @Override
            public InputStream getStream() {
              return new ByteArrayInputStream(body);
            }
          };
        }
      };
    }

    /**
     * Hook for {@code useProtocolsStreamImplementation} on every protocol but https ({@code file}, {@code jar},
     * {@code ftp}, {@code http}): always refuses and counts the attempt.
     */
    FSStreamFactory refusingStreamFactory() {
      return new FSStreamFactory() {

        @Override
        public FSStream getUrl(String url) {
          countBlocked(url);
          return null;
        }
      };
    }

    /**
     * Resolves a resource URI for the renderer.
     *
     * @return the URI to load (lowercased host for https), or {@code null} when the resource is not allowed.
     */
    String resolveUri(String uri) {
      if (uri == null || uri.isEmpty()) {
        return null;
      }
      if (isAllowedDataUri(uri)) {
        return uri;
      }
      URI allowed = allowedHttpsUri(uri);
      if (allowed == null) {
        countBlocked(uri);
        return null;
      }
      return allowed.toString();
    }

    /** Hook for {@code useUriResolver}; the base URI is ignored because no relative resource is allowed. */
    FSUriResolver uriResolver() {
      return new FSUriResolver() {

        @Override
        public String resolveURI(String baseUri, String uri) {
          return resolveUri(uri);
        }
      };
    }

    private void countBlocked(String uri) {
      blocked.incrementAndGet();
      if (LOG.isDebugEnabled()) {
        LOG.debug("PDF resource blocked, host={}", hostOf(uri));
      }
    }
  }

  private static final class CacheEntry {

    private final byte[] body;
    private final long size;
    private final long storedAtNanos;

    CacheEntry(String key, byte[] body, long storedAtNanos) {
      this.body = body;
      this.size = accountedSize(key, body);
      this.storedAtNanos = storedAtNanos;
    }
  }

  /** The only hosts a render may fetch from (P-7). Compared lowercased and exactly. */
  static final Set<String> ALLOWED_HOSTS = Collections.unmodifiableSet(new HashSet<String>(
    Arrays.asList("marlo-pdf-resources-dev.s3.us-east-1.amazonaws.com", "prms-file-storage.s3.amazonaws.com")));

  static final long FETCH_BUDGET_MILLIS = 20000L;

  static final int CONNECT_TIMEOUT_MILLIS = 5000;

  static final int READ_TIMEOUT_MILLIS = 5000;

  static final int MAX_BODY_BYTES = 3 * 1024 * 1024;

  static final int MAX_DATA_URI_CHARS = 2 * 1024 * 1024;

  static final long CACHE_MAX_BYTES = 32L * 1024 * 1024;

  /** Longest URI the allow decision accepts; it also bounds the bytes held by the cache and failure maps. */
  static final int MAX_URI_CHARS = 2048;

  /** Fixed cost charged to the cache bound for every entry, on top of its key and body. */
  static final long CACHE_ENTRY_OVERHEAD_BYTES = 64L;

  private static final long CACHE_TTL_NANOS = 3600L * 1000000000L;

  private static final long FAILURE_TTL_NANOS = 60L * 1000000000L;

  private static final int MAX_FAILURE_ENTRIES = 1024;

  private static final long PURGE_INTERVAL_NANOS = 60L * 1000000000L;

  private static final Logger LOG = LoggerFactory.getLogger(PdfResourceLoader.class);

  /**
   * Returns the URI to connect to, or {@code null} when {@code uri} is not an allowlisted https resource. The value
   * is built from the parsed parts, so the host that was checked is the host that is contacted.
   */
  static URI allowedHttpsUri(String uri) {
    if (uri == null || uri.length() > MAX_URI_CHARS) {
      return null;
    }
    URI parsed;
    try {
      parsed = new URI(uri);
    } catch (URISyntaxException e) {
      return null;
    }
    String scheme = parsed.getScheme();
    String host = parsed.getHost();
    if (scheme == null || !"https".equals(scheme.toLowerCase(Locale.ROOT)) || parsed.isOpaque() || host == null
      || parsed.getUserInfo() != null || (parsed.getPort() != -1 && parsed.getPort() != 443)) {
      return null;
    }
    String lowerHost = host.toLowerCase(Locale.ROOT);
    if (!ALLOWED_HOSTS.contains(lowerHost)) {
      return null;
    }
    String path = parsed.getRawPath();
    StringBuilder normalized = new StringBuilder("https://").append(lowerHost)
      .append(path == null || path.isEmpty() ? "/" : path);
    if (parsed.getRawQuery() != null) {
      normalized.append('?').append(parsed.getRawQuery());
    }
    try {
      return new URI(normalized.toString());
    } catch (URISyntaxException e) {
      return null;
    }
  }

  /** What one cache entry costs against the bound: its key (two bytes per char), its body and a fixed overhead. */
  private static long accountedSize(String key, byte[] body) {
    return key.length() * 2L + body.length + CACHE_ENTRY_OVERHEAD_BYTES;
  }

  /** The host for the DEBUG log line, never the path or query. */
  private static String hostOf(String uri) {
    if (uri == null) {
      return "";
    }
    try {
      String host = new URI(uri).getHost();
      return host == null ? "" : host;
    } catch (URISyntaxException e) {
      return "";
    }
  }

  static boolean isAllowedDataUri(String uri) {
    return uri.length() <= MAX_DATA_URI_CHARS && uri.regionMatches(true, 0, "data:", 0, 5);
  }

  private final Map<String, CacheEntry> cache = new LinkedHashMap<String, CacheEntry>(16, 0.75f, true);

  private long cacheBytes;

  private boolean purged;

  private long purgedAtNanos;

  private final ConnectionFactory connectionFactory;

  private final Map<String, Long> failures = new LinkedHashMap<String, Long>();

  private final LongSupplier nanoClock;

  PdfResourceLoader() {
    this(new ConnectionFactory() {

      @Override
      public HttpURLConnection open(URI uri) throws IOException {
        return (HttpURLConnection) uri.toURL().openConnection();
      }
    }, new LongSupplier() {

      @Override
      public long getAsLong() {
        return System.nanoTime();
      }
    });
  }

  PdfResourceLoader(ConnectionFactory connectionFactory, LongSupplier nanoClock) {
    this.connectionFactory = connectionFactory;
    this.nanoClock = nanoClock;
  }

  /** Bytes the cache currently accounts for, for the bound test. */
  long accountedCacheBytes() {
    synchronized (cache) {
      return cacheBytes;
    }
  }

  /** Starts the state of one render, with the standard 20 s fetch budget. */
  Session newSession() {
    return new Session(new FetchBudget(FETCH_BUDGET_MILLIS));
  }

  Session newSession(FetchBudget budget) {
    return new Session(budget);
  }

  /** One GET, no redirects, bounded body. Returns the body of a 200 response, otherwise {@code null}. */
  private byte[] download(URI uri) {
    HttpURLConnection connection = null;
    try {
      connection = connectionFactory.open(uri);
      connection.setInstanceFollowRedirects(false);
      connection.setConnectTimeout(CONNECT_TIMEOUT_MILLIS);
      connection.setReadTimeout(READ_TIMEOUT_MILLIS);
      connection.setRequestMethod("GET");
      int status = connection.getResponseCode();
      if (status != HttpURLConnection.HTTP_OK) {
        LOG.debug("PDF resource fetch failed, host={}, status={}", uri.getHost(), status);
        return null;
      }
      if (connection.getContentLength() > MAX_BODY_BYTES) {
        LOG.debug("PDF resource over the size limit, host={}", uri.getHost());
        return null;
      }
      try (InputStream in = connection.getInputStream()) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int read = in.read(buffer);
        while (read >= 0) {
          if (out.size() + read > MAX_BODY_BYTES) {
            LOG.debug("PDF resource over the size limit, host={}", uri.getHost());
            return null;
          }
          out.write(buffer, 0, read);
          read = in.read(buffer);
        }
        return out.toByteArray();
      }
    } catch (IOException | RuntimeException e) {
      LOG.debug("PDF resource fetch failed, host={}, cause={}", uri.getHost(), e.getClass().getSimpleName());
      return null;
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
  }

  private void rememberFailure(String key, long now) {
    failures.remove(key);
    failures.put(key, now);
    Iterator<Map.Entry<String, Long>> it = failures.entrySet().iterator();
    while (failures.size() > MAX_FAILURE_ENTRIES && it.hasNext()) {
      it.next();
      it.remove();
    }
  }

  private void removeFromCache(String key) {
    CacheEntry removed = cache.remove(key);
    if (removed != null) {
      cacheBytes -= removed.size;
    }
  }

  /** Drops expired entries, at most once per interval so a flood of inserts does not rescan the whole cache. */
  private void purgeExpired(long now) {
    if (purged && now - purgedAtNanos < PURGE_INTERVAL_NANOS) {
      return;
    }
    purged = true;
    purgedAtNanos = now;
    Iterator<Map.Entry<String, CacheEntry>> it = cache.entrySet().iterator();
    while (it.hasNext()) {
      CacheEntry entry = it.next().getValue();
      if (now - entry.storedAtNanos > CACHE_TTL_NANOS) {
        cacheBytes -= entry.size;
        it.remove();
      }
    }
  }

  private void store(String key, byte[] body, long now) {
    failures.remove(key);
    removeFromCache(key);
    if (body.length == 0) {
      return;
    }
    purgeExpired(now);
    CacheEntry entry = new CacheEntry(key, body, now);
    Iterator<Map.Entry<String, CacheEntry>> it = cache.entrySet().iterator();
    while (cacheBytes + entry.size > CACHE_MAX_BYTES && it.hasNext()) {
      CacheEntry eldest = it.next().getValue();
      cacheBytes -= eldest.size;
      it.remove();
    }
    cache.put(key, entry);
    cacheBytes += entry.size;
  }
}
