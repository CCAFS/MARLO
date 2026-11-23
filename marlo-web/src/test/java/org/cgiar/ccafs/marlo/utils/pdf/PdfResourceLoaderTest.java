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
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongSupplier;

import com.openhtmltopdf.extend.FSStream;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests the resource allowlist and fetch limits (CHG-LPRE-SEC-001, CHG-LPRE-NF-004). Every denial is asserted on what
 * the recording connection factory saw, not only on the returned value.
 */
public class PdfResourceLoaderTest {

  private static final String HOST = "marlo-pdf-resources-dev.s3.us-east-1.amazonaws.com";
  private static final String OK_URL = "https://" + HOST + "/a.png";
  private static final long SECOND = 1000000000L;
  private static final int MB = 1024 * 1024;

  /** A connection that serves a canned response and never touches the network. */
  private static final class FakeConnection extends HttpURLConnection {

    private final byte[] body;
    private final int declaredLength;
    private final int status;

    FakeConnection(URI uri, int status, byte[] body, int declaredLength) throws IOException {
      super(uri.toURL());
      this.status = status;
      this.body = body;
      this.declaredLength = declaredLength;
    }

    @Override
    public void connect() {
    }

    @Override
    public void disconnect() {
    }

    @Override
    public int getContentLength() {
      return declaredLength;
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

  /** Records every URI the loader asks to open. */
  private static final class RecordingFactory implements PdfResourceLoader.ConnectionFactory {

    private final AtomicLong clock;
    private long advanceNanosPerOpen;
    private int declaredLength = -1;
    private byte[] body = new byte[] {1, 2, 3};
    private HttpURLConnection lastConnection;
    private final List<URI> opened = new ArrayList<URI>();
    private int status = 200;

    RecordingFactory(AtomicLong clock) {
      this.clock = clock;
    }

    @Override
    public HttpURLConnection open(URI uri) throws IOException {
      opened.add(uri);
      clock.addAndGet(advanceNanosPerOpen);
      lastConnection = new FakeConnection(uri, status, body, declaredLength);
      return lastConnection;
    }
  }

  private AtomicLong clock;
  private RecordingFactory factory;
  private PdfResourceLoader loader;

  @Before
  public void setUp() {
    clock = new AtomicLong(1000 * SECOND);
    factory = new RecordingFactory(clock);
    final AtomicLong c = clock;
    loader = new PdfResourceLoader(factory, new LongSupplier() {

      @Override
      public long getAsLong() {
        return c.get();
      }
    });
  }

  @Test
  public void disallowedUrisNeverReachTheConnectionFactory() {
    List<String> denied = Arrays.asList("file:///etc/passwd", "jar:file:/x.jar!/a",
      "http://marlo-pdf-resources-dev.s3.us-east-1.amazonaws.com/a.png", "https://169.254.169.254/latest/meta-data",
      "https://evil.example/a.png", "https://x.s3.amazonaws.com/a.png",
      "https://user@marlo-pdf-resources-dev.s3.us-east-1.amazonaws.com/a.png",
      "https://marlo-pdf-resources-dev.s3.us-east-1.amazonaws.com:8443/a.png",
      "ftp://prms-file-storage.s3.amazonaws.com/a", "//prms-file-storage.s3.amazonaws.com/a.png", "a.png",
      "https://[::1]/a.png",
      "https://marlo-pdf-resources-dev.s3.us-east-1.amazonaws.com.evil.example/a.png");
    PdfResourceLoader.Session session = loader.newSession();
    for (String uri : denied) {
      byte[] body = session.fetch(uri);
      assertEquals(uri + " must not be opened, recorded: " + factory.opened, 0, factory.opened.size());
      assertNull(uri, body);
      assertNull(uri, session.resolveUri(uri));
    }
    assertEquals(denied.size() * 2, session.getBlockedCount());
  }

  @Test
  public void anAllowedHostIsFetchedOnceAndOnlyWithTheLowercasedHost() {
    PdfResourceLoader.Session session = loader.newSession();
    byte[] body = session.fetch("https://MARLO-PDF-RESOURCES-DEV.s3.us-east-1.amazonaws.com/a.png");
    assertArrayEquals(new byte[] {1, 2, 3}, body);
    assertEquals(1, factory.opened.size());
    assertEquals(HOST, factory.opened.get(0).getHost());
    assertEquals("https", factory.opened.get(0).getScheme());
    assertEquals(-1, factory.opened.get(0).getPort());
    assertEquals(0, session.getBlockedCount());
  }

  @Test
  public void theSecondAllowedHostIsFetched() {
    assertNotNull(loader.newSession().fetch("https://prms-file-storage.s3.amazonaws.com/b/c.png?x=1"));
    assertEquals(1, factory.opened.size());
    assertEquals("prms-file-storage.s3.amazonaws.com", factory.opened.get(0).getHost());
    assertEquals("x=1", factory.opened.get(0).getRawQuery());
  }

  @Test
  public void connectionsUseTheDocumentedTimeoutsAndDoNotFollowRedirects() {
    loader.newSession().fetch(OK_URL);
    assertEquals(5000, factory.lastConnection.getConnectTimeout());
    assertEquals(5000, factory.lastConnection.getReadTimeout());
    assertFalse(factory.lastConnection.getInstanceFollowRedirects());
  }

  @Test
  public void aRedirectIsNotFollowedAndYieldsNothing() {
    factory.status = 302;
    factory.body = "moved".getBytes();
    PdfResourceLoader.Session session = loader.newSession();
    assertNull(session.fetch(OK_URL));
    assertEquals(1, factory.opened.size());
    assertNull(session.fetch(OK_URL));
    assertEquals("a failure is remembered, no second connection", 1, factory.opened.size());
  }

  @Test
  public void aSuccessfulFetchIsCachedWithinTheTtlAndRefetchedAfterIt() {
    assertNotNull(loader.newSession().fetch(OK_URL));
    assertNotNull(loader.newSession().fetch(OK_URL));
    assertEquals(1, factory.opened.size());
    clock.addAndGet(3600L * SECOND + 1);
    assertNotNull(loader.newSession().fetch(OK_URL));
    assertEquals(2, factory.opened.size());
  }

  @Test
  public void aFailedFetchIsRememberedForSixtySecondsOnly() {
    factory.status = 500;
    assertNull(loader.newSession().fetch(OK_URL));
    assertNull(loader.newSession().fetch(OK_URL));
    assertEquals(1, factory.opened.size());
    clock.addAndGet(60L * SECOND + 1);
    factory.status = 200;
    assertNotNull(loader.newSession().fetch(OK_URL));
    assertEquals(2, factory.opened.size());
  }

  @Test
  public void aNon200ResponseIsNeverCached() {
    factory.status = 404;
    assertNull(loader.newSession().fetch(OK_URL));
    clock.addAndGet(61L * SECOND);
    factory.status = 200;
    assertNotNull(loader.newSession().fetch(OK_URL));
    clock.addAndGet(1L * SECOND);
    assertNotNull(loader.newSession().fetch(OK_URL));
    assertEquals("only the 200 is cached", 2, factory.opened.size());
  }

  @Test
  public void aBodyOverThreeMegabytesYieldsNothing() {
    factory.body = new byte[3 * MB + 1];
    assertNull(loader.newSession().fetch(OK_URL));
    assertEquals(1, factory.opened.size());
    factory.body = new byte[3 * MB];
    assertEquals(3 * MB, loader.newSession().fetch("https://" + HOST + "/exact.png").length);
  }

  @Test
  public void aDeclaredLengthOverThreeMegabytesYieldsNothing() {
    factory.body = new byte[10];
    factory.declaredLength = 3 * MB + 1;
    assertNull(loader.newSession().fetch(OK_URL));
  }

  @Test
  public void theCacheIsBoundedToThirtyTwoMegabytes() {
    factory.body = new byte[3 * MB];
    for (int i = 0; i < 11; i++) {
      assertNotNull(loader.newSession().fetch("https://" + HOST + "/img" + i + ".png"));
    }
    assertEquals(11, factory.opened.size());
    assertNotNull(loader.newSession().fetch("https://" + HOST + "/img10.png"));
    assertEquals("most recent entry is cached", 11, factory.opened.size());
    assertNotNull(loader.newSession().fetch("https://" + HOST + "/img0.png"));
    assertEquals("oldest entry was evicted by the 32 MB bound", 12, factory.opened.size());
  }

  @Test
  public void aDataUriOverTwoMegabytesIsRefusedAndASmallOneIsAllowed() {
    StringBuilder big = new StringBuilder("data:image/png;base64,");
    for (int i = 0; i < 2 * MB; i++) {
      big.append('A');
    }
    PdfResourceLoader.Session session = loader.newSession();
    assertNull(session.resolveUri(big.toString()));
    assertEquals(1, session.getBlockedCount());
    String small = "data:image/png;base64,iVBORw0KGgo=";
    assertEquals(small, session.resolveUri(small));
    assertEquals(0, factory.opened.size());
  }

  @Test
  public void anExhaustedBudgetYieldsNothingWithoutOpeningAConnection() {
    PdfResourceLoader.Session session = loader.newSession(new PdfResourceLoader.FetchBudget(0));
    assertNull(session.fetch(OK_URL));
    assertEquals(0, factory.opened.size());
  }

  @Test
  public void fetchTimeSpentInOneRenderExhaustsItsBudgetButNotAnotherRendersBudget() {
    factory.advanceNanosPerOpen = 21 * SECOND;
    PdfResourceLoader.Session first = loader.newSession();
    assertNotNull(first.fetch("https://" + HOST + "/one.png"));
    assertEquals(1, factory.opened.size());
    assertNull(first.fetch("https://" + HOST + "/two.png"));
    assertEquals("budget spent: no second connection", 1, factory.opened.size());
    PdfResourceLoader.Session other = loader.newSession();
    assertNotNull(other.fetch("https://" + HOST + "/three.png"));
    assertEquals(2, factory.opened.size());
  }

  @Test
  public void theBuilderHooksApplyTheSameDecision() throws IOException {
    PdfResourceLoader.Session session = loader.newSession();
    assertNull(session.uriResolver().resolveURI("https://localhost/", "file:///etc/passwd"));
    assertEquals(OK_URL, session.uriResolver().resolveURI("https://localhost/", OK_URL));
    assertNull(session.refusingStreamFactory().getUrl("file:///etc/passwd"));
    assertNull(session.httpStreamFactory().getUrl("https://evil.example/a.png"));
    assertEquals("denied hooks open nothing: " + factory.opened, 0, factory.opened.size());
    FSStream stream = session.httpStreamFactory().getUrl(OK_URL);
    assertNotNull(stream);
    assertEquals(1, factory.opened.size());
    assertEquals(1, stream.getStream().read());
    assertTrue(session.getBlockedCount() >= 3);
  }

  /** A URL on the allowed host whose total length is exactly {@code length} characters. */
  private static String urlOfLength(int length) {
    StringBuilder url = new StringBuilder("https://" + HOST + "/a.png?p=");
    while (url.length() < length) {
      url.append('x');
    }
    return url.toString();
  }

  /** A distinct 2,000 character URL on the allowed host for each {@code n}. */
  private static String keyFor(int n) {
    StringBuilder url = new StringBuilder("https://" + HOST + "/a.png?n=" + n + "&p=");
    while (url.length() < 2000) {
      url.append('x');
    }
    return url.toString();
  }

  @Test
  public void manyDistinctQueryKeysOnATinyBodyStayWithinTheCacheBound() {
    factory.body = new byte[] {1};
    PdfResourceLoader.Session session = loader.newSession(new PdfResourceLoader.FetchBudget(1000000000L));
    for (int i = 0; i < 12000; i++) {
      assertNotNull(session.fetch(keyFor(i)));
    }
    assertTrue("accounted cache bytes " + loader.accountedCacheBytes(),
      loader.accountedCacheBytes() <= PdfResourceLoader.CACHE_MAX_BYTES);
    int opened = factory.opened.size();
    assertNotNull(session.fetch(keyFor(0)));
    assertEquals("the oldest key was evicted by the byte bound, so it is fetched again", opened + 1,
      factory.opened.size());
  }

  @Test
  public void anEmptyBodyIsNotCached() {
    factory.body = new byte[0];
    assertEquals(0, loader.newSession().fetch(OK_URL).length);
    assertEquals(0, loader.accountedCacheBytes());
    assertEquals(0, loader.newSession().fetch(OK_URL).length);
    assertEquals("an empty body is fetched again", 2, factory.opened.size());
  }

  @Test
  public void aUriOverTwoThousandFortyEightCharactersIsRefusedWithoutAConnection() {
    PdfResourceLoader.Session session = loader.newSession();
    assertNull(session.fetch(urlOfLength(2049)));
    assertEquals("recorded: " + factory.opened.size(), 0, factory.opened.size());
    assertNull(session.resolveUri(urlOfLength(2049)));
    assertEquals(2, session.getBlockedCount());
    assertNotNull(session.fetch(urlOfLength(2048)));
    assertEquals(1, factory.opened.size());
  }

  @Test
  public void expiredEntriesAreDroppedWhenANewOneIsStored() {
    assertNotNull(loader.newSession().fetch(OK_URL));
    long one = loader.accountedCacheBytes();
    clock.addAndGet(3600L * SECOND + 1);
    assertNotNull(loader.newSession().fetch("https://" + HOST + "/b.png"));
    assertEquals("only the new entry remains accounted", one, loader.accountedCacheBytes());
  }

  @Test
  public void aRuntimeExceptionFromTheConnectionFactoryYieldsNothing() {
    PdfResourceLoader throwing = new PdfResourceLoader(new PdfResourceLoader.ConnectionFactory() {

      @Override
      public HttpURLConnection open(URI uri) {
        throw new IllegalStateException("boom");
      }
    }, new LongSupplier() {

      @Override
      public long getAsLong() {
        return clock.get();
      }
    });
    assertNull(throwing.newSession().fetch(OK_URL));
  }
}
