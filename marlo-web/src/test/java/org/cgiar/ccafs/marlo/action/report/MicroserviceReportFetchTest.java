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

package org.cgiar.ccafs.marlo.action.report;

import org.cgiar.ccafs.marlo.utils.APConfig;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opensymphony.xwork2.ActionContext;
import org.apache.struts2.ServletActionContext;
import org.slf4j.LoggerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Proves the bounded PDF fetch of the submit (BUG-SUBMIT-EMAIL-001 FN-002, FN-003, NF-001): attempts, waits, timeouts
 * and the exact arguments each network seam receives. No real network is used.
 */
public class MicroserviceReportFetchTest {

  private static final String QUEUE_URL = "amqp://queue.example.test:5672";
  private static final String QUEUE_NAME = "pdf-queue";
  private static final String API_KEY = "key-1234";
  private static final String S3_URL = "https://s3.example.test/bucket/";
  private static final String REPORT = "Full_Cluster_Report-TEST-C77-20261112-1000.pdf";
  private static final String JSON = "{\"pattern\":\"pdf.generate\",\"data\":{\"fileName\":\"" + REPORT + "\"}}";
  private static final byte[] PDF = {37, 80, 68, 70, 45, 1, 2, 3};

  /**
   * Records every call and answers from the scripted fields. Each call is charged min(latency, timeoutMs): the real
   * transport guarantees that, as the loopback tests below prove, because timeoutMs is its total budget per request.
   */
  private static final class FakeTransport implements MicroserviceReportAction.FetchTransport {

    final List<String> calls = new ArrayList<>();
    final List<String> headUrls = new ArrayList<>();
    final List<String> getUrls = new ArrayList<>();
    final List<Integer> headTimeouts = new ArrayList<>();
    final List<Integer> getTimeouts = new ArrayList<>();
    final List<Long> waits = new ArrayList<>();
    final List<String> publishedMessages = new ArrayList<>();
    String publishedQueueUrl;
    String publishedQueueName;
    int publishTimeout = -1;
    int readyAtAttempt = Integer.MAX_VALUE;
    byte[] body = PDF;
    boolean getThrows = false;
    boolean publishThrows = false;
    boolean interruptOnWait = false;
    long now = 1000000L;
    long headLatency = 0L;
    long getLatency = 0L;
    long publishLatency = 0L;
    final List<Long> headStarts = new ArrayList<>();
    final List<Long> waitStarts = new ArrayList<>();
    final List<Long> getStarts = new ArrayList<>();
    final List<Long> publishStarts = new ArrayList<>();
    final List<Integer> publishTimeouts = new ArrayList<>();

    @Override
    public long nowMillis() {
      return this.now;
    }

    @Override
    public byte[] get(String url, int timeoutMs) throws Exception {
      this.getStarts.add(this.now);
      this.now += Math.min(this.getLatency, timeoutMs);
      this.calls.add("get");
      this.getUrls.add(url);
      this.getTimeouts.add(timeoutMs);
      if (this.getThrows) {
        throw new java.io.IOException("get failed");
      }
      return this.body;
    }

    @Override
    public int head(String url, int timeoutMs) {
      this.headStarts.add(this.now);
      this.now += Math.min(this.headLatency, timeoutMs);
      this.calls.add("head");
      this.headUrls.add(url);
      this.headTimeouts.add(timeoutMs);
      return this.headUrls.size() >= this.readyAtAttempt ? 200 : 404;
    }

    @Override
    public void publish(String queueUrl, String queueName, String message, int timeoutMs) throws Exception {
      this.publishStarts.add(this.now);
      this.publishTimeouts.add(timeoutMs);
      this.now += this.publishLatency;
      this.calls.add("publish");
      if (this.publishThrows) {
        throw new java.io.IOException("queue unreachable");
      }
      this.publishedQueueUrl = queueUrl;
      this.publishedQueueName = queueName;
      this.publishedMessages.add(message);
      this.publishTimeout = timeoutMs;
    }

    @Override
    public void sleep(long millis) throws InterruptedException {
      this.waitStarts.add(this.now);
      this.now += millis;
      this.calls.add("sleep");
      this.waits.add(millis);
      if (this.interruptOnWait) {
        throw new InterruptedException("interrupted");
      }
    }
  }

  /** The four values the fetch reads, each one distinct. */
  private static final class FetchConfig extends APConfig {

    private final String queueUrl;
    private final String s3Url;

    FetchConfig(String queueUrl, String s3Url) {
      this.queueUrl = queueUrl;
      this.s3Url = s3Url;
    }

    @Override
    public String getMicroserviceApiKey() {
      return API_KEY;
    }

    @Override
    public String getMicroserviceQueueName() {
      return QUEUE_NAME;
    }

    @Override
    public String getMicroserviceQueueURL() {
      return this.queueUrl;
    }

    @Override
    public String getMicroserviceS3Url() {
      return this.s3Url;
    }
  }

  private FakeTransport transport;

  private MicroserviceReportAction action(String queueUrl, String s3Url) {
    MicroserviceReportAction action = new MicroserviceReportAction(new FetchConfig(queueUrl, s3Url), null);
    action.setFetchTransport(this.transport);
    return action;
  }

  private MicroserviceReportAction action() {
    return this.action(QUEUE_URL, S3_URL);
  }

  @Before
  public void setUp() {
    this.transport = new FakeTransport();
  }

  @After
  public void tearDown() {
    Thread.interrupted();
    ActionContext.clear();
  }

  @Test
  public void readyAtThirdAttemptReturnsTheGetBytesAfterTwoWaits() {
    this.transport.readyAtAttempt = 3;

    byte[] result = this.action().fetchClusterReportBytes(JSON, REPORT);

    assertArrayEquals(PDF, result);
    assertEquals(3, this.transport.headUrls.size());
    assertEquals(2, this.transport.waits.size());
    for (Long wait : this.transport.waits) {
      assertEquals(Long.valueOf(MicroserviceReportAction.SUBMIT_POLL_WAIT_MS), wait);
    }
    assertEquals(1, this.transport.getUrls.size());
  }

  @Test
  public void neverReadyReturnsNullAfterExactlyTheConfiguredAttemptsAndNoGet() {
    byte[] result = this.action().fetchClusterReportBytes(JSON, REPORT);

    assertNull(result);
    assertEquals(MicroserviceReportAction.SUBMIT_POLL_ATTEMPTS, this.transport.headUrls.size());
    assertEquals(MicroserviceReportAction.SUBMIT_POLL_ATTEMPTS - 1, this.transport.waits.size());
    assertTrue(this.transport.getUrls.isEmpty());
  }

  @Test
  public void zeroByteBodyReturnsNull() {
    this.transport.readyAtAttempt = 1;
    this.transport.body = new byte[0];

    assertNull(this.action().fetchClusterReportBytes(JSON, REPORT));
    assertEquals(1, this.transport.getUrls.size());
  }

  @Test
  public void nonSuccessGetReturnsNull() {
    this.transport.readyAtAttempt = 1;
    this.transport.body = null;

    assertNull(this.action().fetchClusterReportBytes(JSON, REPORT));
    assertEquals(1, this.transport.getUrls.size());
  }

  @Test
  public void throwingGetReturnsNull() {
    this.transport.readyAtAttempt = 1;
    this.transport.getThrows = true;

    assertNull(this.action().fetchClusterReportBytes(JSON, REPORT));
    assertEquals(1, this.transport.getUrls.size());
  }

  @Test
  public void throwingPublishReturnsNullWithoutAnyHead() {
    this.transport.publishThrows = true;

    assertNull(this.action().fetchClusterReportBytes(JSON, REPORT));
    assertEquals(1, this.transport.calls.size());
    assertEquals("publish", this.transport.calls.get(0));
    assertTrue(this.transport.headUrls.isEmpty());
  }

  @Test
  public void nullOrBlankInputsReturnNullWithoutPublishing() {
    String[][] inputs = {{null, REPORT}, {" ", REPORT}, {JSON, null}, {JSON, ""}};
    for (String[] input : inputs) {
      assertNull(this.action().fetchClusterReportBytes(input[0], input[1]));
    }
    assertNull(this.action(QUEUE_URL, null).fetchClusterReportBytes(JSON, REPORT));
    assertNull(this.action(QUEUE_URL, "  ").fetchClusterReportBytes(JSON, REPORT));
    assertNull(this.action(null, S3_URL).fetchClusterReportBytes(JSON, REPORT));
    assertNull(this.action("", S3_URL).fetchClusterReportBytes(JSON, REPORT));
    assertTrue(this.transport.calls.isEmpty());
  }

  @Test
  public void interruptDuringTheWaitReturnsNullAndRestoresTheFlag() {
    this.transport.interruptOnWait = true;

    byte[] result = this.action().fetchClusterReportBytes(JSON, REPORT);

    boolean restored = Thread.currentThread().isInterrupted();
    Thread.interrupted();
    assertNull(result);
    assertTrue(restored);
    assertEquals(1, this.transport.headUrls.size());
    assertTrue(this.transport.getUrls.isEmpty());
  }

  @Test
  public void theSubmitResponseReceivesNoCallInAnyCase() {
    List<String> responseCalls = new ArrayList<>();
    HttpServletResponse response = (HttpServletResponse) Proxy.newProxyInstance(
      MicroserviceReportFetchTest.class.getClassLoader(), new Class<?>[] {HttpServletResponse.class},
      new InvocationHandler() {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
          if ("toString".equals(method.getName())) {
            return "recording response";
          }
          responseCalls.add(method.getName());
          return null;
        }
      });
    ActionContext.of(new HashMap<String, Object>()).bind();
    ServletActionContext.setResponse(response);
    assertTrue(ServletActionContext.getResponse() == response);

    this.transport.readyAtAttempt = 3;
    assertArrayEquals(PDF, this.action().fetchClusterReportBytes(JSON, REPORT));

    this.transport = new FakeTransport();
    assertNull(this.action().fetchClusterReportBytes(JSON, REPORT));

    assertEquals(new ArrayList<String>(), responseCalls);
  }

  @Test
  public void seamsReceiveTheConfiguredTimeoutsUrlsAndTheApiKey() throws Exception {
    this.transport.readyAtAttempt = 2;

    assertArrayEquals(PDF, this.action().fetchClusterReportBytes(JSON, REPORT));

    assertEquals(MicroserviceReportAction.SUBMIT_CONNECT_TIMEOUT_MS, this.transport.publishTimeout);
    assertEquals(5000, MicroserviceReportAction.SUBMIT_CONNECT_TIMEOUT_MS);
    assertEquals(QUEUE_URL, this.transport.publishedQueueUrl);
    assertEquals(QUEUE_NAME, this.transport.publishedQueueName);
    for (Integer timeout : this.transport.headTimeouts) {
      assertEquals(Integer.valueOf(MicroserviceReportAction.SUBMIT_HTTP_TIMEOUT_MS), timeout);
    }
    assertEquals(Integer.valueOf(MicroserviceReportAction.SUBMIT_HTTP_TIMEOUT_MS), this.transport.getTimeouts.get(0));
    assertEquals(5000, MicroserviceReportAction.SUBMIT_HTTP_TIMEOUT_MS);
    for (String url : this.transport.headUrls) {
      assertEquals(S3_URL + REPORT, url);
    }
    assertEquals(S3_URL + REPORT, this.transport.getUrls.get(0));

    assertEquals(1, this.transport.publishedMessages.size());
    Map<?, ?> message = new ObjectMapper().readValue(this.transport.publishedMessages.get(0), Map.class);
    assertEquals("pdf.generate", message.get("pattern"));
    Map<?, ?> inner = (Map<?, ?>) message.get("data");
    assertEquals(API_KEY, inner.get("apiKey"));
    assertEquals(REPORT, inner.get("fileName"));
  }

  /** Captures the formatted messages the fetch logs through its own logger. */
  private List<String> captureLog(Runnable body) {
    Logger logger = (Logger) LoggerFactory.getLogger(MicroserviceReportAction.class);
    ListAppender<ILoggingEvent> capture = new ListAppender<>();
    capture.start();
    logger.addAppender(capture);
    try {
      body.run();
    } finally {
      logger.detachAppender(capture);
      capture.stop();
    }
    List<String> messages = new ArrayList<>();
    for (ILoggingEvent event : capture.list) {
      messages.add(event.getFormattedMessage());
    }
    return messages;
  }

  @Test
  public void deadlineStopsThePollingAndCutsEveryTimeoutToTheTimeLeft() {
    this.transport.headLatency = 5000L;
    long start = this.transport.now;
    long deadline = start + 30000L;

    byte[] result = this.action().fetchClusterReportBytes(JSON, REPORT);

    assertNull(result);
    assertTrue(this.transport.getStarts.isEmpty());
    for (int i = 0; i < this.transport.headStarts.size(); i++) {
      long left = deadline - this.transport.headStarts.get(i);
      assertTrue("HEAD " + i + " started with no time left", left > 0);
      assertTrue("HEAD " + i + " timeout above the time left", this.transport.headTimeouts.get(i) <= left);
    }
    for (int i = 0; i < this.transport.waitStarts.size(); i++) {
      long left = deadline - this.transport.waitStarts.get(i);
      assertTrue("wait " + i + " started with no time left", left > 0);
      assertTrue("wait " + i + " above the time left", this.transport.waits.get(i) <= left);
    }
    assertEquals(5, this.transport.headStarts.size());
    assertEquals(30000L, MicroserviceReportAction.SUBMIT_TOTAL_DEADLINE_MS);
    assertEquals(Integer.valueOf(2000), this.transport.headTimeouts.get(4));
    assertEquals(4, this.transport.waits.size());
    assertEquals(Long.valueOf(2000L), this.transport.waits.get(3));
    assertEquals(deadline, this.transport.now);
  }

  @Test
  public void getTimeoutIsCutToTheTimeLeftAfterASlowPublish() {
    this.transport.publishLatency = 28500L;
    this.transport.readyAtAttempt = 1;

    assertArrayEquals(PDF, this.action().fetchClusterReportBytes(JSON, REPORT));

    assertEquals(Integer.valueOf(5000), this.transport.publishTimeouts.get(0));
    assertEquals(1, this.transport.headStarts.size());
    assertEquals(Integer.valueOf(1500), this.transport.headTimeouts.get(0));
    assertEquals(Integer.valueOf(1500), this.transport.getTimeouts.get(0));
  }

  @Test
  public void publishThatConsumesTheWholeDeadlineStartsNoHeadAndNoGet() {
    this.transport.publishLatency = 31000L;
    this.transport.readyAtAttempt = 1;

    assertNull(this.action().fetchClusterReportBytes(JSON, REPORT));

    assertTrue(this.transport.headStarts.isEmpty());
    assertTrue(this.transport.getStarts.isEmpty());
    assertTrue(this.transport.waits.isEmpty());
  }

  @Test
  public void zeroByteBodyLogsOutcomeEmptyAndAFailureLogsError() {
    this.transport.readyAtAttempt = 1;
    this.transport.body = new byte[0];
    List<String> empty = this.captureLog(() -> assertNull(this.action().fetchClusterReportBytes(JSON, REPORT)));

    assertEquals(1, empty.size());
    assertTrue(empty.get(0), empty.get(0).contains("outcome=empty"));
    assertTrue(empty.get(0), empty.get(0).contains("bytes=0"));

    this.transport = new FakeTransport();
    this.transport.readyAtAttempt = 1;
    this.transport.getThrows = true;
    List<String> failed = this.captureLog(() -> assertNull(this.action().fetchClusterReportBytes(JSON, REPORT)));
    assertFalse(failed.toString(), failed.stream().anyMatch(m -> m.contains("outcome=empty")));
    assertTrue(failed.toString(), failed.stream().anyMatch(m -> m.contains("outcome=error")));
  }

  /**
   * Starts a loopback server. With dribble=true it answers 200 with a large Content-Length and then sends one byte
   * every 50 ms for at most 3 s; otherwise it accepts and never writes. Returns the server socket.
   */
  private static java.net.ServerSocket startLoopbackServer(boolean dribble) throws java.io.IOException {
    final java.net.ServerSocket server = new java.net.ServerSocket(0, 1, java.net.InetAddress.getByName("127.0.0.1"));
    Thread thread = new Thread(() -> {
      try (java.net.Socket socket = server.accept()) {
        java.io.InputStream in = socket.getInputStream();
        int matched = 0;
        int b;
        // Read the request headers up to the blank line.
        while (matched < 4 && (b = in.read()) != -1) {
          matched = (b == '\r' || b == '\n') ? matched + 1 : 0;
        }
        if (!dribble) {
          Thread.sleep(4000L);
          return;
        }
        java.io.OutputStream out = socket.getOutputStream();
        out.write(("HTTP/1.1 200 OK\r\nContent-Length: 10000\r\nConnection: close\r\n\r\n").getBytes("US-ASCII"));
        out.flush();
        for (int i = 0; i < 60; i++) {
          out.write('x');
          out.flush();
          Thread.sleep(50L);
        }
      } catch (Exception e) {
        // The client gave up or the server was closed: expected.
      }
    });
    thread.setDaemon(true);
    thread.start();
    return server;
  }

  @Test(timeout = 10000)
  public void realGetWithASlowBodyStopsWithinTheTotalBudget() throws Exception {
    java.net.ServerSocket server = startLoopbackServer(true);
    try {
      String url = "http://127.0.0.1:" + server.getLocalPort() + "/report.pdf";
      MicroserviceReportAction.DefaultFetchTransport real = new MicroserviceReportAction.DefaultFetchTransport();
      long start = System.nanoTime();
      boolean failed = false;
      try {
        real.get(url, 500);
      } catch (java.net.SocketTimeoutException e) {
        failed = true;
      } catch (Exception e) {
        failed = true;
      }
      long elapsedMs = (System.nanoTime() - start) / 1000000L;
      assertTrue("elapsed " + elapsedMs + " ms, budget 500 ms", elapsedMs < 2000L);
      assertTrue("the slow GET must fail", failed);
    } finally {
      server.close();
    }
  }

  @Test(timeout = 10000)
  public void realHeadOnASilentServerStopsWithinTheTotalBudget() throws Exception {
    java.net.ServerSocket server = startLoopbackServer(false);
    try {
      String url = "http://127.0.0.1:" + server.getLocalPort() + "/report.pdf";
      MicroserviceReportAction.DefaultFetchTransport real = new MicroserviceReportAction.DefaultFetchTransport();
      long start = System.nanoTime();
      boolean failed = false;
      try {
        real.head(url, 500);
      } catch (Exception e) {
        failed = true;
      }
      long elapsedMs = (System.nanoTime() - start) / 1000000L;
      assertTrue("the silent HEAD must fail", failed);
      assertTrue("elapsed " + elapsedMs + " ms, budget 500 ms", elapsedMs < 2000L);
    } finally {
      server.close();
    }
  }
}
