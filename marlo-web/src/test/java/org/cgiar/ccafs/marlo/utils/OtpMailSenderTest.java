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

package org.cgiar.ccafs.marlo.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.cgiar.ccafs.marlo.data.manager.EmailLogManager;
import org.cgiar.ccafs.marlo.data.model.EmailLog;
import org.cgiar.ccafs.marlo.security.otp.OtpLog;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.function.IntSupplier;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.read.ListAppender;
import com.opensymphony.xwork2.TextProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

/**
 * CHG-OTP-LOGIN-001-T09, T18 and T19: the code email is built from the real {@code global.properties}, goes to one
 * recipient on a 2-thread executor, writes one {@code auth.otp.delivery} line per hand-off, and leaves one
 * {@code email_logs} row per queued message holding the HTML body, code included (OPS-005, amended 2026-10-15).
 * Outside production the only recipient is the support mailbox, with a subject prefix and a body header naming the
 * submitted address (FN-018, AC-19). The transport and the log manager are stubs; no SMTP server or database is
 * involved. Expected values are literals from the requirements (FN-017, FN-018, OPS-002, OPS-005, NF-001).
 */
public class OtpMailSenderTest {

  private static final String CODE = "123456";
  private static final String RECIPIENT = "jane.doe@partner.org";
  private static final Long UNIT = Long.valueOf(7L);
  private static final String SUPPORT = "support@test.org";
  private static final String PREFIX = "(TESTING ENVIRONMENT) ";
  private static final String SEPARATOR = "----------------------------------------------------";
  private static final String SUBJECT = "Your MARLO sign-in code";
  private static final String ASSETS_URL = "https://cdn.test";
  private static final String SOURCE =
    "src/main/java/org/cgiar/ccafs/marlo/utils/OtpMailSender.java";

  private static Properties global;

  private Logger deliveryLogger;
  private Logger mailerLogger;
  private Level deliveryLevel;
  private Level mailerLevel;
  private ListAppender<ILoggingEvent> appender;
  private DeliveryCounter counter;
  private OtpMailSender sender;
  private TestConfig config;
  private RecordingLogs logs;

  /**
   * Records the rows saved through the email log manager, the threads that saved them, how many
   * {@code auth.otp.delivery} lines had been written at the moment of each save, and can fail on demand.
   */
  private static final class RecordingLogs {

    final List<EmailLog> rows = Collections.synchronizedList(new ArrayList<EmailLog>());
    final List<Thread> threads = Collections.synchronizedList(new ArrayList<Thread>());
    final List<Integer> deliveryLinesAtSave = Collections.synchronizedList(new ArrayList<Integer>());
    volatile IntSupplier deliveryLines;
    volatile Throwable failWith;

    EmailLogManager manager() {
      return (EmailLogManager) Proxy.newProxyInstance(EmailLogManager.class.getClassLoader(),
        new Class<?>[] {EmailLogManager.class}, (proxy, invoked, args) -> {
          if (!"saveEmailLog".equals(invoked.getName())) {
            throw new UnsupportedOperationException(invoked.getName());
          }
          this.threads.add(Thread.currentThread());
          IntSupplier lines = this.deliveryLines;
          if (lines != null) {
            this.deliveryLinesAtSave.add(lines.getAsInt());
          }
          Throwable failure = this.failWith;
          if (failure instanceof RuntimeException) {
            throw (RuntimeException) failure;
          }
          if (failure instanceof Error) {
            throw (Error) failure;
          }
          this.rows.add((EmailLog) args[0]);
          return args[0];
        });
    }
  }

  /**
   * Counts the {@code auth.otp.delivery} lines as they are written. The count is read from the worker thread while it
   * saves a row, so it cannot iterate the list of the {@code ListAppender}, which the other worker is appending to.
   */
  private static final class DeliveryCounter extends AppenderBase<ILoggingEvent> {

    final AtomicInteger lines = new AtomicInteger();

    @Override
    protected void append(ILoggingEvent event) {
      if (event.getFormattedMessage().startsWith("auth.otp.delivery ")) {
        this.lines.incrementAndGet();
      }
    }
  }

  /** Records the messages and the threads that delivered them, and can fail or block on demand. */
  private static final class StubTransport implements OtpMailSender.MailTransport {

    final List<MimeMessage> delivered = Collections.synchronizedList(new ArrayList<MimeMessage>());
    final List<Thread> threads = Collections.synchronizedList(new ArrayList<Thread>());
    final AtomicInteger running = new AtomicInteger();
    final AtomicInteger maxRunning = new AtomicInteger();
    volatile Throwable failWith;
    volatile CountDownLatch entered;
    volatile CountDownLatch release;

    @Override
    public void deliver(MimeMessage message) throws MessagingException {
      this.threads.add(Thread.currentThread());
      int now = this.running.incrementAndGet();
      this.maxRunning.accumulateAndGet(now, Math::max);
      try {
        if (this.entered != null) {
          this.entered.countDown();
        }
        if (this.release != null) {
          try {
            this.release.await(20, TimeUnit.SECONDS);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
          }
        }
        Throwable failure = this.failWith;
        if (failure instanceof MessagingException) {
          throw (MessagingException) failure;
        }
        if (failure instanceof RuntimeException) {
          throw (RuntimeException) failure;
        }
        if (failure instanceof Error) {
          throw (Error) failure;
        }
        this.delivered.add(message);
      } finally {
        this.running.decrementAndGet();
      }
    }
  }

  private static final class TestConfig extends APConfig {

    private final String timeout;
    /** Production by default, as the T09 and T18 cases assert; the T19 cases flip it. Read per send. */
    volatile boolean production = true;
    volatile String notification = SUPPORT;

    TestConfig(String timeout) {
      this.timeout = timeout;
    }

    @Override
    public boolean isProduction() {
      return this.production;
    }

    @Override
    public String getEmailHost() {
      return "smtp.test.org";
    }

    @Override
    public int getEmailPort() {
      return 2525;
    }

    @Override
    public String getEmail_auth() {
      return "true";
    }

    @Override
    public String getEmail_starttls() {
      return "true";
    }

    @Override
    public String getEmailUsername() {
      return "mailer";
    }

    @Override
    public String getEmailPassword() {
      return "not-a-real-password";
    }

    @Override
    public String getEmailNotification() {
      return this.notification;
    }

    @Override
    public String getOtpSmtpTimeoutMs() {
      return this.timeout;
    }
  }

  @BeforeClass
  public static void loadGlobal() throws IOException {
    global = new Properties();
    try (InputStream stream = OtpMailSenderTest.class.getResourceAsStream("/global.properties")) {
      assertNotNull("global.properties is on the test classpath", stream);
      global.load(stream);
    }
  }

  /**
   * Answers getText(key, String[]) as Struts does: the pattern of the key formatted with the arguments, or the key
   * itself when no file defines it.
   */
  private static TextProvider texts(final Properties properties) {
    return (TextProvider) Proxy.newProxyInstance(TextProvider.class.getClassLoader(),
      new Class<?>[] {TextProvider.class}, (proxy, invoked, args) -> {
        String key = (String) args[0];
        String pattern = properties.getProperty(key);
        return pattern == null ? key : MessageFormat.format(pattern, (Object[]) args[1]);
      });
  }

  private static Properties override(String key, String value) {
    Properties copy = new Properties(global);
    copy.setProperty(key, value);
    return copy;
  }

  @Before
  public void attachAppender() {
    this.logs = new RecordingLogs();
    this.deliveryLogger = (Logger) LoggerFactory.getLogger(OtpLog.class);
    this.mailerLogger = (Logger) LoggerFactory.getLogger(OtpMailSender.class);
    this.deliveryLevel = this.deliveryLogger.getLevel();
    this.mailerLevel = this.mailerLogger.getLevel();
    this.deliveryLogger.setLevel(Level.TRACE);
    this.mailerLogger.setLevel(Level.TRACE);
    this.appender = new ListAppender<ILoggingEvent>();
    this.appender.start();
    this.counter = new DeliveryCounter();
    this.counter.start();
    this.logs.deliveryLines = this.counter.lines::get;
    this.deliveryLogger.addAppender(this.appender);
    this.deliveryLogger.addAppender(this.counter);
    this.mailerLogger.addAppender(this.appender);
  }

  @After
  public void detachAppender() {
    if (this.sender != null) {
      this.sender.shutdown();
    }
    this.deliveryLogger.detachAppender(this.appender);
    this.deliveryLogger.detachAppender(this.counter);
    this.mailerLogger.detachAppender(this.appender);
    this.deliveryLogger.setLevel(this.deliveryLevel);
    this.mailerLogger.setLevel(this.mailerLevel);
  }

  private OtpMailSender senderWith(StubTransport transport, String timeout) {
    this.config = new TestConfig(timeout);
    this.sender = new OtpMailSender(this.config, transport, this.logs.manager());
    return this.sender;
  }

  private void send(String recipient) {
    this.sender.sendAsync(texts(global), ASSETS_URL, recipient, CODE, "CCAFS", UNIT);
  }

  private List<EmailLog> awaitRows(int count) throws InterruptedException {
    long deadline = System.currentTimeMillis() + 15000L;
    while (this.logs.rows.size() < count && System.currentTimeMillis() < deadline) {
      Thread.sleep(20L);
    }
    synchronized (this.logs.rows) {
      return new ArrayList<EmailLog>(this.logs.rows);
    }
  }

  /** Every String property of the row, by its getter: a field added later is covered without editing the test. */
  private static List<String> stringFields(EmailLog row) throws Exception {
    List<String> values = new ArrayList<String>();
    for (Method getter : EmailLog.class.getMethods()) {
      if (getter.getName().startsWith("get") && getter.getParameterCount() == 0
        && getter.getReturnType() == String.class) {
        String value = (String) getter.invoke(row);
        values.add(getter.getName() + "=" + value);
      }
    }
    return values;
  }

  /** The {@code auth.otp.delivery} lines written so far. */
  private List<ILoggingEvent> deliveryLines() {
    List<ILoggingEvent> lines = new ArrayList<ILoggingEvent>();
    synchronized (this.appender.list) {
      for (ILoggingEvent event : this.appender.list) {
        if (event.getFormattedMessage().startsWith("auth.otp.delivery ")) {
          lines.add(event);
        }
      }
    }
    return lines;
  }

  private List<ILoggingEvent> awaitDeliveryLines(int count) throws InterruptedException {
    long deadline = System.currentTimeMillis() + 15000L;
    List<ILoggingEvent> lines = this.deliveryLines();
    while (lines.size() < count && System.currentTimeMillis() < deadline) {
      Thread.sleep(20L);
      lines = this.deliveryLines();
    }
    return lines;
  }

  private void assertNothingSecretWasLogged() {
    synchronized (this.appender.list) {
      for (ILoggingEvent event : this.appender.list) {
        String line = event.getFormattedMessage();
        assertFalse("the code never reaches a log line: " + line, line.contains(CODE));
        assertFalse("the address never reaches a log line: " + line, line.toLowerCase().contains("jane"));
        assertFalse("the exception message never reaches a log line: " + line, line.contains("smtp-secret"));
        assertTrue("no exception is attached to a line", event.getThrowableProxy() == null);
      }
    }
  }

  private static String textOf(MimeMessage message) throws Exception {
    return (String) ((MimeMultipart) message.getContent()).getBodyPart(0).getContent();
  }

  private static String htmlOf(MimeMessage message) throws Exception {
    return (String) ((MimeMultipart) message.getContent()).getBodyPart(1).getContent();
  }

  @Test
  public void successDeliversOnceToTheSubmittedRecipientAndLogsDelivered() throws Exception {
    StubTransport transport = new StubTransport();
    this.senderWith(transport, "");

    this.send(RECIPIENT);

    List<ILoggingEvent> lines = this.awaitDeliveryLines(1);
    assertEquals(1, lines.size());
    String line = lines.get(0).getFormattedMessage();
    assertTrue(line, line.startsWith("auth.otp.delivery outcome=delivered gu=ccafs domain=partner.org ms="));
    assertEquals(Level.INFO, lines.get(0).getLevel());
    assertEquals(1, transport.delivered.size());
    Address[] to = transport.delivered.get(0).getRecipients(Message.RecipientType.TO);
    assertEquals(1, to.length);
    assertEquals(RECIPIENT, to[0].toString());
    assertNull(transport.delivered.get(0).getRecipients(Message.RecipientType.CC));
    assertNull(transport.delivered.get(0).getRecipients(Message.RecipientType.BCC));
    assertEquals("support@test.org", transport.delivered.get(0).getFrom()[0].toString());
    this.assertNothingSecretWasLogged();
  }

  @Test
  public void aTransportExceptionLogsEmailFailedAtWarnWithTheClassNameOnly() throws Exception {
    StubTransport transport = new StubTransport();
    transport.failWith = new MessagingException("smtp-secret jane.doe@partner.org 123456");
    this.senderWith(transport, "");

    this.send(RECIPIENT);

    List<ILoggingEvent> lines = this.awaitDeliveryLines(1);
    assertEquals(1, lines.size());
    String line = lines.get(0).getFormattedMessage();
    assertTrue(line, line.startsWith("auth.otp.delivery outcome=email_failed gu=ccafs domain=partner.org ms="));
    assertEquals(Level.WARN, lines.get(0).getLevel());
    boolean causeLogged = false;
    synchronized (this.appender.list) {
      for (ILoggingEvent event : this.appender.list) {
        causeLogged |= event.getFormattedMessage().contains("cause=MessagingException");
      }
    }
    assertTrue("the failure class is logged", causeLogged);
    this.assertNothingSecretWasLogged();
  }

  @Test
  public void aRuntimeFailureInTheTransportIsContainedAndTheWorkerSurvives() throws Exception {
    StubTransport transport = new StubTransport();
    transport.failWith = new IllegalStateException("smtp-secret");
    this.senderWith(transport, "");

    this.send(RECIPIENT);
    assertEquals(1, this.awaitDeliveryLines(1).size());
    transport.failWith = null;
    this.send(RECIPIENT);

    List<ILoggingEvent> lines = this.awaitDeliveryLines(2);
    assertEquals(2, lines.size());
    assertTrue(lines.get(0).getFormattedMessage().contains("outcome=email_failed"));
    assertTrue(lines.get(1).getFormattedMessage().contains("outcome=delivered"));
    this.assertNothingSecretWasLogged();
  }

  @Test
  public void aFullQueueLogsEmailFailedAndTheCallerIsNotAffected() throws Exception {
    final StubTransport transport = new StubTransport();
    transport.entered = new CountDownLatch(2);
    transport.release = new CountDownLatch(1);
    this.senderWith(transport, "");

    // Two sends occupy both workers: the stub blocks on the executor threads, so the caller is free.
    this.send(RECIPIENT);
    this.send(RECIPIENT);
    assertTrue("both workers are busy", transport.entered.await(10, TimeUnit.SECONDS));
    // The next 100 fill the bounded queue.
    for (int i = 0; i < 100; i++) {
      this.send(RECIPIENT);
    }
    assertEquals("nothing is rejected yet", 0, this.deliveryLines().size());

    // The 103rd cannot be queued: the caller returns normally and the failure is logged on its own thread.
    this.send(RECIPIENT);

    List<ILoggingEvent> rejected = this.deliveryLines();
    assertEquals(1, rejected.size());
    assertTrue(rejected.get(0).getFormattedMessage()
      .startsWith("auth.otp.delivery outcome=email_failed gu=ccafs domain=partner.org ms="));
    assertEquals(Level.WARN, rejected.get(0).getLevel());
    assertEquals("only two sends ever run at once", 2, transport.maxRunning.get());

    transport.release.countDown();
    List<ILoggingEvent> all = this.awaitDeliveryLines(103);
    assertEquals(103, all.size());
    assertEquals("the rejected send is never delivered", 102, transport.delivered.size());
    // Proof that the stub ran on the executor, not on the caller.
    for (Thread worker : new ArrayList<Thread>(transport.threads)) {
      assertNotEquals(Thread.currentThread(), worker);
      assertTrue(worker.getName(), worker.getName().startsWith("otp-mail-"));
      assertTrue("daemon threads", worker.isDaemon());
    }
    this.assertNothingSecretWasLogged();
  }

  @Test
  public void theRenderedEmailStatesEveryRequiredElement() throws Exception {
    StubTransport transport = new StubTransport();
    this.senderWith(transport, "");

    this.send(RECIPIENT);

    assertEquals(1, this.awaitDeliveryLines(1).size());
    MimeMessage message = transport.delivered.get(0);
    assertEquals("Your MARLO sign-in code", message.getSubject());
    for (String body : new String[] {textOf(message), htmlOf(message)}) {
      assertTrue(body, body.contains("MARLO"));
      assertTrue(body, body.contains(CODE));
      assertTrue(body, body.contains("expires in 5 minutes"));
      assertTrue(body, body.contains("used only once"));
      assertTrue(body, body.contains("Do not share this code with anyone."));
      assertTrue(body, body.contains("If you did not request this code, you can ignore this email."));
      assertTrue(body, body.contains("MARLOSupport@cgiar.org"));
      assertFalse("no link at all, so none carries the code", body.toLowerCase().contains("http"));
    }
    assertFalse("the subject does not carry the code", message.getSubject().contains(CODE));
    assertTrue(htmlOf(message).contains("<p"));
    assertFalse("the text part is not HTML", textOf(message).contains("<"));
  }

  @Test
  public void everyDynamicValueIsHtmlEscapedInTheHtmlPartOnly() throws Exception {
    StubTransport transport = new StubTransport();
    this.senderWith(transport, "");

    this.sender.sendAsync(texts(override("footer.contactEmail", "a<b>&\"c''")), ASSETS_URL, RECIPIENT, CODE, "CCAFS",
      UNIT);

    assertEquals(1, this.awaitDeliveryLines(1).size());
    String html = htmlOf(transport.delivered.get(0));
    assertTrue(html, html.contains("a&lt;b&gt;&amp;&quot;c&#39;"));
    assertFalse(html, html.contains("a<b>"));
    assertTrue(textOf(transport.delivered.get(0)).contains("a<b>&\"c'"));
  }

  @Test
  public void aRenderWithAnUnresolvedPlaceholderOrKeyFailsAndSendsNothing() throws Exception {
    StubTransport transport = new StubTransport();
    this.senderWith(transport, "");

    this.sender.sendAsync(texts(override("login.otp.email.html", "<p>{0} {1} {9}</p>")), ASSETS_URL, RECIPIENT, CODE,
      "CCAFS", UNIT);
    Properties noKey = new Properties();
    noKey.putAll(global);
    noKey.remove("login.otp.email.subject");
    this.sender.sendAsync(texts(noKey), ASSETS_URL, RECIPIENT, CODE, "CCAFS", UNIT);

    List<ILoggingEvent> lines = this.awaitDeliveryLines(2);
    assertEquals(2, lines.size());
    for (ILoggingEvent line : lines) {
      assertTrue(line.getFormattedMessage().contains("outcome=email_failed"));
      assertEquals(Level.WARN, line.getLevel());
    }
    assertEquals("nothing was handed to the transport", 0, transport.threads.size());
    this.assertNothingSecretWasLogged();
  }

  @Test
  public void aRecipientThatIsNotASingleAddressIsNeverSent() throws Exception {
    StubTransport transport = new StubTransport();
    this.senderWith(transport, "");
    String[] bad = {null, "", "   ", "no-at-sign", "a@b.org,c@d.org", "a@b.org;c@d.org", "a@b.org c@d.org",
      "a@b.org\r\nBcc: x@y.org", "a@b.org\nBcc: x@y.org", "a@b.org\rBcc:x@y.org", "Name <a@b.org>", " a@b.org",
      "a@b.org ", "@b.org", "a@", "\"a b\"@b.org"};

    for (String recipient : bad) {
      this.send(recipient);
    }

    List<ILoggingEvent> lines = this.awaitDeliveryLines(bad.length);
    assertEquals(bad.length, lines.size());
    for (ILoggingEvent line : lines) {
      assertTrue(line.getFormattedMessage(), line.getFormattedMessage().contains("outcome=email_failed"));
      assertFalse(line.getFormattedMessage(), line.getFormattedMessage().contains("Bcc"));
    }
    assertEquals("nothing was handed to the transport", 0, transport.threads.size());
  }

  @Test
  public void aBlankCodeIsNeverSent() throws Exception {
    StubTransport transport = new StubTransport();
    this.senderWith(transport, "");

    this.sender.sendAsync(texts(global), ASSETS_URL, RECIPIENT, null, "CCAFS", UNIT);
    this.sender.sendAsync(texts(global), ASSETS_URL, RECIPIENT, "  ", "CCAFS", UNIT);

    assertEquals(2, this.awaitDeliveryLines(2).size());
    assertEquals(0, transport.threads.size());
  }

  @Test
  public void theSessionCarriesTheSmtpSettingsAndTheConfiguredOrDefaultTimeouts() throws Exception {
    String[][] cases = {{"", "10000"}, {"2500", "2500"}, {" 3000 ", "3000"}, {"30000", "30000"},
      {"30001", "30000"}, {"60000", "30000"}, {"abc", "10000"}, {"-5", "10000"}, {"0", "10000"},
      {"1.5", "10000"}, {"99999999999999999999", "10000"}, {null, "10000"}};
    for (String[] c : cases) {
      StubTransport transport = new StubTransport();
      OtpMailSender local = new OtpMailSender(new TestConfig(c[0]), transport, this.logs.manager());
      try {
        local.sendAsync(texts(global), ASSETS_URL, RECIPIENT, CODE, "CCAFS", UNIT);
        long deadline = System.currentTimeMillis() + 10000L;
        while (transport.delivered.isEmpty() && System.currentTimeMillis() < deadline) {
          Thread.sleep(20L);
        }
        assertEquals("timeout input " + c[0], 1, transport.delivered.size());
        Properties session = transport.delivered.get(0).getSession().getProperties();
        assertEquals("smtp.test.org", session.getProperty("mail.smtp.host"));
        assertEquals("2525", session.getProperty("mail.smtp.port"));
        assertEquals("true", session.getProperty("mail.smtp.auth"));
        assertEquals("true", session.getProperty("mail.smtp.starttls.enable"));
        assertEquals("timeout input " + c[0], c[1], session.getProperty("mail.smtp.connectiontimeout"));
        assertEquals("timeout input " + c[0], c[1], session.getProperty("mail.smtp.timeout"));
        assertEquals("timeout input " + c[0], c[1], session.getProperty("mail.smtp.writetimeout"));
      } finally {
        local.shutdown();
      }
    }
  }

  @Test
  public void anErrorThrownByTheTransportIsStillOneEmailFailedLineAndTheWorkerSurvives() throws Exception {
    StubTransport transport = new StubTransport();
    transport.failWith = new LinkageError("smtp-secret");
    this.senderWith(transport, "");

    this.send(RECIPIENT);
    List<ILoggingEvent> first = this.awaitDeliveryLines(1);
    assertEquals(1, first.size());
    assertTrue(first.get(0).getFormattedMessage().contains("outcome=email_failed"));
    transport.failWith = null;
    this.send(RECIPIENT);

    assertEquals(2, this.awaitDeliveryLines(2).size());
    assertEquals(1, transport.delivered.size());
    this.assertNothingSecretWasLogged();
  }

  @Test
  public void theContainerSeesOneNamedBeanWithOneInjectConstructorAndTheShutdownDestroyHook()
    throws NoSuchMethodException {
    assertTrue("discovered by component scan", OtpMailSender.class.isAnnotationPresent(Named.class));

    List<Constructor<?>> injectable = new ArrayList<Constructor<?>>();
    for (Constructor<?> constructor : OtpMailSender.class.getDeclaredConstructors()) {
      if (constructor.isAnnotationPresent(Inject.class)) {
        injectable.add(constructor);
      }
    }
    assertEquals("exactly one injectable constructor, so the container never picks the test seam", 1,
      injectable.size());
    assertTrue(Modifier.isPublic(injectable.get(0).getModifiers()));
    assertEquals(Arrays.<Class<?>>asList(APConfig.class, EmailLogManager.class),
      Arrays.asList(injectable.get(0).getParameterTypes()));

    Method shutdown = OtpMailSender.class.getDeclaredMethod("shutdown");
    assertTrue("shutdown is the destroy hook", shutdown.isAnnotationPresent(PreDestroy.class));
  }

  @Test
  public void springScansOneSingletonAndClosingTheContextStopsTheExecutor() throws Exception {
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
    ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(context);
    // The real scanner, over the real package, narrowed to this class: the others need a database.
    scanner.addExcludeFilter(new TypeFilter() {

      @Override
      public boolean match(MetadataReader reader, MetadataReaderFactory factory) {
        return !reader.getClassMetadata().getClassName().equals(OtpMailSender.class.getName());
      }
    });
    scanner.scan("org.cgiar.ccafs.marlo.utils");
    // A ready instance: a bean definition would have its @Value fields injected, which needs the real properties.
    context.getBeanFactory().registerSingleton("apConfig", new TestConfig(""));
    context.getBeanFactory().registerSingleton("emailLogManager", this.logs.manager());
    context.refresh();
    try {
      assertEquals("exactly one bean, found by @Named", 1, context.getBeanNamesForType(OtpMailSender.class).length);
      OtpMailSender bean = context.getBean(OtpMailSender.class);
      assertTrue("a singleton", context.isSingleton("otpMailSender"));
      assertSame(bean, context.getBean(OtpMailSender.class));
      this.sender = bean;
    } finally {
      context.close();
    }

    // The context honoured the destroy hook: the executor no longer accepts work, so the send is a failed hand-off.
    this.send(RECIPIENT);
    List<ILoggingEvent> lines = this.deliveryLines();
    assertEquals(1, lines.size());
    assertTrue(lines.get(0).getFormattedMessage(), lines.get(0).getFormattedMessage().contains("outcome=email_failed"));
  }

  @Test
  public void theMailerNeverReachesTheEmailLogTheNotificationFlagOrTheSupportRedirect() throws IOException {
    // Static lock on the shipped file: SendMailS persists every body and honours the notification flag and the
    // support redirect, all of which the code email must ignore (DD-7, P-7). Comments are stripped first. Since T18 the
    // mailer writes its own row through EmailLogManager, which is allowed; the resend path is not. Since T19 it reads
    // isProduction() itself, per send, which is allowed too; the placeholder is gone.
    String source = new String(Files.readAllBytes(Paths.get(SOURCE)), StandardCharsets.UTF_8);
    String code = source.replaceAll("(?s)/\\*.*?\\*/", "").replaceAll("//[^\n]*", "");
    for (String forbidden : new String[] {"SendMailS", "ThreadSendMail", "email_log", "isNotificationDisabled",
      "isRestrictedToSupport", "getEmail_pmu", "PLACEHOLDER_MESSAGE", "CustomParameter", "CRP_EMAIL_NOTIFICATIONS",
      "CRP_EMAIL_SUPPORT_TEAM", "crp_enable_email_notification", "crp_email_support_team"}) {
      assertFalse("OtpMailSender must not use " + forbidden, code.contains(forbidden));
    }
    assertTrue("the mailer records its row through the log manager", code.contains("emailLogManager.saveEmailLog"));
    assertTrue("the mailer reads the production flag itself", code.contains("config.isProduction()"));
  }

  // ---- OPS-005 (T18): the email_logs row ----------------------------------------------------------------

  @Test
  public void aDeliveredCodeEmailLeavesOneRowWithTheFullBodyAndTheCodeInNoOtherField() throws Exception {
    StubTransport transport = new StubTransport();
    this.senderWith(transport, "");
    Date before = new Date();

    this.send(RECIPIENT);

    List<EmailLog> rows = this.awaitRows(1);
    assertEquals(1, rows.size());
    EmailLog row = rows.get(0);
    for (String field : stringFields(row)) {
      if (!field.startsWith("getMessage=")) {
        assertFalse("the code is in no string field but the body: " + field, field.contains(CODE));
      }
    }
    assertEquals(RECIPIENT, row.getTo());
    assertEquals("Your MARLO sign-in code", row.getSubject());
    assertEquals(UNIT, row.getGlobalUnitId());
    assertEquals("/otpRequestCode", row.getSourceAction());
    assertEquals(Integer.valueOf(1), row.getTried());
    assertEquals(Boolean.TRUE, row.getSucces());
    assertNull(row.getError());
    assertEquals("the body is the HTML part as sent", htmlOf(transport.delivered.get(0)), row.getMessage());
    assertTrue("the body holds the code (SEC-001 exception 2026-10-15)", row.getMessage().contains(CODE));
    assertNull(row.getCc());
    assertNull(row.getBbc());
    assertNull(row.getFileName());
    assertNull(row.getFileContent());
    assertNull(row.getMessageID());
    assertNotNull(row.getDate());
    assertFalse("the date is the send time", row.getDate().before(new Date(before.getTime() - 1000L)));
    assertEquals(1, transport.delivered.size());
    assertEquals("one row, one email", transport.delivered.size(), this.logs.rows.size());
  }

  @Test
  public void theRowIsSavedOnTheWorkerThreadAfterTheDeliveryLine() throws Exception {
    StubTransport transport = new StubTransport();
    this.senderWith(transport, "");

    this.send(RECIPIENT);

    assertEquals(1, this.awaitRows(1).size());
    assertEquals(1, this.deliveryLines().size());
    assertEquals("the delivery line was already written when the row was saved (OPS-005: the row follows the line)",
      Collections.singletonList(1), new ArrayList<Integer>(this.logs.deliveryLinesAtSave));
    for (Thread saver : new ArrayList<Thread>(this.logs.threads)) {
      assertNotEquals("never on the calling thread (SEC-006)", Thread.currentThread(), saver);
      assertTrue(saver.getName(), saver.getName().startsWith("otp-mail-"));
    }
    assertEquals("the row is saved by the thread that delivered", transport.threads.get(0),
      this.logs.threads.get(0));
  }

  @Test
  public void aFailedSendLeavesOneFailedRowWithTheClassNameAndNoExceptionMessage() throws Exception {
    StubTransport transport = new StubTransport();
    transport.failWith = new MessagingException("smtp-secret jane.doe@partner.org 123456");
    this.senderWith(transport, "");

    this.send(RECIPIENT);

    List<EmailLog> rows = this.awaitRows(1);
    assertEquals(1, rows.size());
    EmailLog row = rows.get(0);
    for (String field : stringFields(row)) {
      if (!field.startsWith("getMessage=")) {
        assertFalse("the code is in no string field but the body: " + field, field.contains(CODE));
      }
      assertFalse("the exception message is in no string field: " + field, field.contains("smtp-secret"));
    }
    assertEquals(Boolean.FALSE, row.getSucces());
    assertEquals("MessagingException", row.getError());
    assertEquals(RECIPIENT, row.getTo());
    assertEquals(UNIT, row.getGlobalUnitId());
    assertEquals("/otpRequestCode", row.getSourceAction());
    assertEquals(Integer.valueOf(1), row.getTried());
    assertTrue("a failed row also holds the body", row.getMessage().contains(CODE));
    assertTrue(row.getMessage().contains("<p"));
  }

  @Test
  public void aRuntimeFailureOfTheTransportRecordsItsClassNameOnly() throws Exception {
    StubTransport transport = new StubTransport();
    transport.failWith = new IllegalStateException("smtp-secret");
    this.senderWith(transport, "");

    this.send(RECIPIENT);

    List<EmailLog> rows = this.awaitRows(1);
    assertEquals("IllegalStateException", rows.get(0).getError());
    assertEquals(Boolean.FALSE, rows.get(0).getSucces());
  }

  @Test
  public void aFullQueueWritesNoRowForTheRejectedHandOff() throws Exception {
    StubTransport transport = new StubTransport();
    transport.entered = new CountDownLatch(2);
    transport.release = new CountDownLatch(1);
    this.senderWith(transport, "");
    this.send(RECIPIENT);
    this.send(RECIPIENT);
    assertTrue("both workers are busy", transport.entered.await(10, TimeUnit.SECONDS));
    for (int i = 0; i < 100; i++) {
      this.send(RECIPIENT);
    }

    this.send(RECIPIENT);

    assertEquals("the rejected hand-off is logged at once", 1, this.deliveryLines().size());
    assertEquals("and writes no row, nor does anything queued before it ends", 0, this.logs.rows.size());
    assertEquals("nothing is saved on the request thread", 0, this.logs.threads.size());
    transport.release.countDown();
    assertEquals(102, this.awaitRows(102).size());
    assertEquals(103, this.awaitDeliveryLines(103).size());
    assertEquals("102 queued sends, 102 rows: the rejected one left none", 102, this.logs.rows.size());
  }

  @Test
  public void anUnbuildableMessageOrRecipientWritesNoRow() throws Exception {
    StubTransport transport = new StubTransport();
    this.senderWith(transport, "");

    this.send("a@b.org,c@d.org");
    this.send(null);
    this.sender.sendAsync(texts(override("login.otp.email.html", "<p>{0} {1} {9}</p>")), ASSETS_URL, RECIPIENT, CODE,
      "CCAFS", UNIT);
    this.sender.sendAsync(texts(global), ASSETS_URL, RECIPIENT, " ", "CCAFS", UNIT);

    assertEquals(4, this.awaitDeliveryLines(4).size());
    Thread.sleep(200L);
    assertEquals("no row for a message that was never queued", 0, this.logs.rows.size());
    assertEquals(0, this.logs.threads.size());
  }

  @Test
  public void aSendAfterShutdownWritesNoRow() throws Exception {
    StubTransport transport = new StubTransport();
    this.senderWith(transport, "");
    this.sender.shutdown();

    this.send(RECIPIENT);

    assertEquals(1, this.deliveryLines().size());
    Thread.sleep(200L);
    assertEquals(0, this.logs.rows.size());
  }

  @Test
  public void aSaveThatThrowsKeepsTheDeliveryLineAndTheNextSendStillRecordsItsRow() throws Exception {
    StubTransport transport = new StubTransport();
    this.logs.failWith = new IllegalStateException("db-secret jane.doe@partner.org 123456");
    this.senderWith(transport, "");

    this.send(RECIPIENT);

    List<ILoggingEvent> lines = this.awaitDeliveryLines(1);
    assertEquals(1, lines.size());
    assertTrue(lines.get(0).getFormattedMessage().contains("outcome=delivered"));
    long deadline = System.currentTimeMillis() + 10000L;
    while (this.logs.threads.isEmpty() && System.currentTimeMillis() < deadline) {
      Thread.sleep(20L);
    }
    assertEquals("the save was attempted", 1, this.logs.threads.size());
    boolean warned = false;
    synchronized (this.appender.list) {
      for (ILoggingEvent event : this.appender.list) {
        String line = event.getFormattedMessage();
        if (line.contains("log_failed")) {
          warned = true;
          assertEquals(Level.WARN, event.getLevel());
          assertTrue(line, line.contains("cause=IllegalStateException"));
        }
        assertFalse("the save failure message is never logged: " + line, line.contains("db-secret"));
        assertTrue("no exception is attached to a line", event.getThrowableProxy() == null);
      }
    }
    assertTrue("the failed save is logged at WARN by class name", warned);

    this.logs.failWith = null;
    this.send(RECIPIENT);

    assertEquals(2, this.awaitDeliveryLines(2).size());
    assertEquals(1, this.awaitRows(1).size());
    assertEquals("the worker survived and the email went out", 2, transport.delivered.size());
  }

  @Test
  public void anErrorThrownByTheSaveIsContainedToo() throws Exception {
    StubTransport transport = new StubTransport();
    this.logs.failWith = new LinkageError("db-secret");
    this.senderWith(transport, "");

    this.send(RECIPIENT);
    assertEquals(1, this.awaitDeliveryLines(1).size());
    this.logs.failWith = null;
    this.send(RECIPIENT);

    assertEquals(1, this.awaitRows(1).size());
    assertEquals(2, transport.delivered.size());
  }

  @Test
  public void aNullGlobalUnitIdIsRecordedAsNull() throws Exception {
    StubTransport transport = new StubTransport();
    this.senderWith(transport, "");

    this.sender.sendAsync(texts(global), ASSETS_URL, RECIPIENT, CODE, "CCAFS", null);

    List<EmailLog> rows = this.awaitRows(1);
    assertNull(rows.get(0).getGlobalUnitId());
  }

  // ---- FN-018 / AC-19 (T19): the non-production rule ------------------------------------------------------

  private static String header(String address) {
    return "To: " + address;
  }

  private static void assertOnlyRecipient(MimeMessage message, String expected) throws MessagingException {
    Address[] to = message.getRecipients(Message.RecipientType.TO);
    assertEquals(1, to.length);
    assertEquals(expected, to[0].toString());
    assertNull(message.getRecipients(Message.RecipientType.CC));
    assertNull(message.getRecipients(Message.RecipientType.BCC));
  }

  @Test
  public void outsideProductionTheOnlyRecipientIsTheSupportMailboxWithThePrefixAndTheHeader() throws Exception {
    StubTransport transport = new StubTransport();
    this.senderWith(transport, "");
    this.config.production = false;

    this.send(RECIPIENT);

    assertEquals(1, this.awaitDeliveryLines(1).size());
    assertEquals(1, transport.delivered.size());
    MimeMessage message = transport.delivered.get(0);
    assertOnlyRecipient(message, SUPPORT);
    assertEquals(PREFIX + SUBJECT, message.getSubject());
    String plain = textOf(message);
    String html = htmlOf(message);
    assertTrue(plain, plain.startsWith(header(RECIPIENT) + "\n" + SEPARATOR + "\n\n"));
    assertTrue(html, html.startsWith(header(RECIPIENT) + "<br>" + SEPARATOR + "<br><br>"));
    assertTrue("the code is still in the plain part", plain.contains(CODE));
    assertTrue("the code is still in the HTML part", html.contains(CODE));
    assertTrue(plain.contains("expires in 5 minutes"));
    assertTrue(html.contains("expires in 5 minutes"));
  }

  @Test
  public void outsideProductionTheRowKeepsTheSubmittedAddressTheSupportBccAndTheProductionBody() throws Exception {
    StubTransport transport = new StubTransport();
    this.senderWith(transport, "");
    this.config.production = true;
    this.send(RECIPIENT);
    List<EmailLog> productionRows = this.awaitRows(1);
    assertEquals(1, productionRows.size());
    String productionBody = productionRows.get(0).getMessage();

    this.config.production = false;
    this.send(RECIPIENT);

    List<EmailLog> rows = this.awaitRows(2);
    assertEquals(2, rows.size());
    EmailLog row = rows.get(1);
    assertEquals(RECIPIENT, row.getTo());
    assertEquals(SUPPORT, row.getBbc());
    assertNull(row.getCc());
    assertEquals("the subject has no prefix", SUBJECT, row.getSubject());
    assertEquals("the body is the production HTML body", productionBody, row.getMessage());
    assertTrue(row.getMessage().contains(CODE));
    assertFalse("the body has no test header", row.getMessage().startsWith("To:"));
    assertFalse("the body has no test header", row.getMessage().contains(SEPARATOR));
    String sent = htmlOf(transport.delivered.get(1));
    assertEquals("the row body is the sent HTML without the header", sent.substring(
      (header(RECIPIENT) + "<br>" + SEPARATOR + "<br><br>").length()), row.getMessage());
    assertEquals("/otpRequestCode", row.getSourceAction());
    assertEquals(UNIT, row.getGlobalUnitId());
    assertEquals(Boolean.TRUE, row.getSucces());
    for (String field : stringFields(row)) {
      if (!field.startsWith("getMessage=")) {
        assertFalse("the code is in no string field but the body: " + field, field.contains(CODE));
      }
    }
  }

  @Test
  public void outsideProductionAFailedSendStillRecordsTheSubmittedAddressAndTheSupportBcc() throws Exception {
    StubTransport transport = new StubTransport();
    transport.failWith = new MessagingException("smtp-secret");
    this.senderWith(transport, "");
    this.config.production = false;

    this.send(RECIPIENT);

    List<EmailLog> rows = this.awaitRows(1);
    assertEquals(RECIPIENT, rows.get(0).getTo());
    assertEquals(SUPPORT, rows.get(0).getBbc());
    assertEquals(SUBJECT, rows.get(0).getSubject());
    assertEquals(Boolean.FALSE, rows.get(0).getSucces());
    assertEquals("MessagingException", rows.get(0).getError());
  }

  @Test
  public void outsideProductionTheHtmlHeaderEscapesTheSubmittedAddressAndThePlainHeaderDoesNot() throws Exception {
    StubTransport transport = new StubTransport();
    this.senderWith(transport, "");
    this.config.production = false;

    this.send("a&b@partner.org");

    assertEquals(1, this.awaitDeliveryLines(1).size());
    MimeMessage message = transport.delivered.get(0);
    assertTrue(htmlOf(message), htmlOf(message).startsWith("To: a&amp;b@partner.org<br>"));
    assertTrue(textOf(message), textOf(message).startsWith("To: a&b@partner.org\n"));
    assertOnlyRecipient(message, SUPPORT);
  }

  @Test
  public void outsideProductionABlankOrInvalidSupportMailboxFailsClosedAndNeverUsesTheSubmittedAddress()
    throws Exception {
    StubTransport transport = new StubTransport();
    this.senderWith(transport, "");
    this.config.production = false;
    String[] bad = {null, "", "   ", "a@b.org,c@d.org", "a@b.org;c@d.org", "Name <a@b.org>", "no-at-sign"};

    for (String mailbox : bad) {
      this.config.notification = mailbox;
      this.send(RECIPIENT);
    }

    List<ILoggingEvent> lines = this.awaitDeliveryLines(bad.length);
    assertEquals(bad.length, lines.size());
    for (ILoggingEvent line : lines) {
      assertTrue(line.getFormattedMessage(), line.getFormattedMessage().contains("outcome=email_failed"));
      assertEquals(Level.WARN, line.getLevel());
    }
    assertEquals("nothing was handed to the transport, so the submitted address received nothing", 0,
      transport.threads.size());
    assertEquals(0, transport.delivered.size());
    Thread.sleep(200L);
    assertEquals("no row for a message that was never queued", 0, this.logs.rows.size());
    assertEquals(0, this.logs.threads.size());
    this.assertNothingSecretWasLogged();
  }

  @Test
  public void inProductionTheOnlyRecipientIsTheSubmittedAddressWithNoPrefixHeaderOrBcc() throws Exception {
    StubTransport transport = new StubTransport();
    this.senderWith(transport, "");
    this.config.production = true;

    this.send(RECIPIENT);

    List<EmailLog> rows = this.awaitRows(1);
    assertEquals(1, transport.delivered.size());
    MimeMessage message = transport.delivered.get(0);
    assertOnlyRecipient(message, RECIPIENT);
    assertEquals(SUBJECT, message.getSubject());
    assertFalse(textOf(message), textOf(message).startsWith("To:"));
    assertFalse(htmlOf(message), htmlOf(message).startsWith("To:"));
    assertFalse(textOf(message).contains(SEPARATOR));
    assertFalse(htmlOf(message).contains(SEPARATOR));
    assertFalse(htmlOf(message).contains("TESTING"));
    assertEquals(RECIPIENT, rows.get(0).getTo());
    assertNull("no BCC in production", rows.get(0).getBbc());
    assertEquals(SUBJECT, rows.get(0).getSubject());
    assertEquals(htmlOf(message), rows.get(0).getMessage());
  }

  @Test
  public void inProductionABlankSupportMailboxIsNotConsultedForTheRecipient() throws Exception {
    // The sender address still needs it (T09); the recipient never comes from it.
    StubTransport transport = new StubTransport();
    this.senderWith(transport, "");
    this.config.production = true;
    this.config.notification = "other@test.org";

    this.send(RECIPIENT);

    assertEquals(1, this.awaitDeliveryLines(1).size());
    assertOnlyRecipient(transport.delivered.get(0), RECIPIENT);
  }

  @Test
  public void theProductionFlagIsReadOnEverySendNotCachedAtConstruction() throws Exception {
    StubTransport transport = new StubTransport();
    this.senderWith(transport, "");

    this.config.production = true;
    this.send(RECIPIENT);
    assertEquals(1, this.awaitRows(1).size());
    this.config.production = false;
    this.send(RECIPIENT);
    assertEquals(2, this.awaitRows(2).size());
    this.config.production = true;
    this.send(RECIPIENT);
    assertEquals(3, this.awaitRows(3).size());

    assertEquals(3, transport.delivered.size());
    assertOnlyRecipient(transport.delivered.get(0), RECIPIENT);
    assertEquals(SUBJECT, transport.delivered.get(0).getSubject());
    assertOnlyRecipient(transport.delivered.get(1), SUPPORT);
    assertEquals(PREFIX + SUBJECT, transport.delivered.get(1).getSubject());
    assertOnlyRecipient(transport.delivered.get(2), RECIPIENT);
    assertEquals(SUBJECT, transport.delivered.get(2).getSubject());
    assertNull(this.logs.rows.get(0).getBbc());
    assertEquals(SUPPORT, this.logs.rows.get(1).getBbc());
    assertNull(this.logs.rows.get(2).getBbc());
  }

  // ---- OPS-001 exception (T19): the code line, outside production only -----------------------------------

  /** The {@code auth.otp.code} lines written so far. */
  private List<ILoggingEvent> codeLines() {
    List<ILoggingEvent> lines = new ArrayList<ILoggingEvent>();
    synchronized (this.appender.list) {
      for (ILoggingEvent event : this.appender.list) {
        if (event.getFormattedMessage().startsWith("auth.otp.code ")) {
          lines.add(event);
        }
      }
    }
    return lines;
  }

  @Test
  public void outsideProductionADeliveredEmailWritesExactlyOneCodeLineAfterTheDeliveryLine() throws Exception {
    StubTransport transport = new StubTransport();
    this.senderWith(transport, "");
    this.config.production = false;

    this.send(RECIPIENT);

    assertEquals(1, this.awaitRows(1).size());
    List<ILoggingEvent> lines = this.codeLines();
    assertEquals(1, lines.size());
    assertEquals("auth.otp.code gu=CCAFS to=" + RECIPIENT + " code=" + CODE, lines.get(0).getFormattedMessage());
    assertEquals(Level.INFO, lines.get(0).getLevel());
    assertTrue("no exception is attached", lines.get(0).getThrowableProxy() == null);
    synchronized (this.appender.list) {
      int delivery = -1;
      int code = -1;
      for (int i = 0; i < this.appender.list.size(); i++) {
        String line = this.appender.list.get(i).getFormattedMessage();
        if (line.startsWith("auth.otp.delivery ")) {
          delivery = i;
        } else if (line.startsWith("auth.otp.code ")) {
          code = i;
        } else {
          assertFalse("no other line carries the code or the address: " + line,
            line.contains(CODE) || line.toLowerCase().contains("jane"));
        }
      }
      assertTrue("the code line follows the delivery line", delivery >= 0 && code > delivery);
    }
  }

  @Test
  public void outsideProductionAFailedSendStillWritesOneCodeLine() throws Exception {
    StubTransport transport = new StubTransport();
    transport.failWith = new MessagingException("smtp-secret");
    this.senderWith(transport, "");
    this.config.production = false;

    this.send(RECIPIENT);

    assertEquals(1, this.awaitRows(1).size());
    List<ILoggingEvent> lines = this.codeLines();
    assertEquals(1, lines.size());
    assertEquals("auth.otp.code gu=CCAFS to=" + RECIPIENT + " code=" + CODE, lines.get(0).getFormattedMessage());
    assertEquals(1, this.deliveryLines().size());
    assertTrue(this.deliveryLines().get(0).getFormattedMessage().contains("outcome=email_failed"));
  }

  @Test
  public void outsideProductionEveryHandedOffEmailWritesItsOwnCodeLineAndARejectedOneNone() throws Exception {
    StubTransport transport = new StubTransport();
    this.senderWith(transport, "");
    this.config.production = false;

    this.send(RECIPIENT);
    this.send("other.user@partner.org");
    this.send("a@b.org,c@d.org");

    assertEquals(2, this.awaitRows(2).size());
    assertEquals(3, this.awaitDeliveryLines(3).size());
    assertEquals(2, this.codeLines().size());
  }

  @Test
  public void inProductionNoCodeLineExistsAndNoLogLineCarriesTheCodeOrTheAddress() throws Exception {
    StubTransport transport = new StubTransport();
    this.senderWith(transport, "");
    this.config.production = true;

    this.send(RECIPIENT);
    assertEquals(1, this.awaitRows(1).size());
    transport.failWith = new MessagingException("smtp-secret jane.doe@partner.org 123456");
    this.send(RECIPIENT);
    assertEquals(2, this.awaitRows(2).size());

    assertEquals(0, this.codeLines().size());
    assertEquals(2, this.awaitDeliveryLines(2).size());
    this.assertNothingSecretWasLogged();
  }

  @Test
  public void theCodeLineFollowsTheFlagReadForThatSend() throws Exception {
    StubTransport transport = new StubTransport();
    this.senderWith(transport, "");

    this.config.production = true;
    this.send(RECIPIENT);
    assertEquals(1, this.awaitRows(1).size());
    assertEquals(0, this.codeLines().size());
    this.config.production = false;
    this.send(RECIPIENT);
    assertEquals(2, this.awaitRows(2).size());
    assertEquals(1, this.codeLines().size());
    this.config.production = true;
    this.send(RECIPIENT);
    assertEquals(3, this.awaitRows(3).size());
    assertEquals(1, this.codeLines().size());
  }

  // ---- FN-017, UI-005, AC-20 (T20): the branded format ---------------------------------------------------

  /** The key arguments of the body, as the sender passes them, for a unit whose support contact is the default. */
  private static String bareBody() {
    return texts(global).getText("login.otp.email.html",
      new String[] {"MARLO", CODE, "5", "MARLOSupport@cgiar.org"});
  }

  private static final Pattern LINK_WITH_CODE = Pattern.compile("(href|src)=\"[^\"]*" + CODE);

  @Test
  public void aUnitWithAShellSendsTheHtmlPartInsideItAndTheTextPartWithoutIt() throws Exception {
    StubTransport transport = new StubTransport();
    this.senderWith(transport, "");

    this.sender.sendAsync(UnitTextProvider.forCustomFile("aicrra"), ASSETS_URL, RECIPIENT, CODE, "AICCRA", UNIT);

    assertEquals(1, this.awaitDeliveryLines(1).size());
    MimeMessage message = transport.delivered.get(0);
    String html = htmlOf(message);
    assertTrue(html, html.startsWith("<table role=\"presentation\""));
    assertTrue("the logo comes from the assets URL", html.contains("src=\"" + ASSETS_URL
      + "/global/images/email/marlo-logo.png\""));
    assertTrue("the band comes from the assets URL", html.contains("src=\"" + ASSETS_URL
      + "/global/images/email/email-band.png\""));
    assertTrue("the body with the code sits inside the shell", html.contains("sign-in code</p>")
      && html.contains(">" + CODE + "</p>"));
    assertEquals("[AICCRA] Your MARLO sign-in code", message.getSubject());
    String plain = textOf(message);
    assertFalse("the text part is not wrapped", plain.contains("<"));
    assertTrue(plain, plain.contains(CODE));
    assertTrue(plain, plain.contains("expires in 5 minutes"));
    assertFalse("no placeholder is left unfilled", html.contains("{") || html.contains("}"));
  }

  @Test
  public void everyShellUnitStatesEveryRequiredElementAcrossBodyAndShell() throws Exception {
    StubTransport transport = new StubTransport();
    this.senderWith(transport, "");
    String[] files = {"aicrra", "aiccra3", "test"};

    for (String file : files) {
      this.sender.sendAsync(UnitTextProvider.forCustomFile(file), ASSETS_URL, RECIPIENT, CODE, "UNIT", UNIT);
    }

    assertEquals(files.length, this.awaitDeliveryLines(files.length).size());
    assertEquals(files.length, transport.delivered.size());
    for (MimeMessage message : transport.delivered) {
      String html = htmlOf(message);
      for (String required : new String[] {"MARLO", CODE, "expires in 5 minutes", "used only once",
        "Do not share this code with anyone.", "If you did not request this code, you can ignore this email.",
        "MARLOSupport@cgiar.org", "automated notification"}) {
        assertTrue("a shell unit is missing <" + required + ">: " + html, html.contains(required));
      }
      assertTrue("branded: " + html, html.startsWith("<table role=\"presentation\""));
      assertFalse("no link carries the code", LINK_WITH_CODE.matcher(html).find());
    }
  }

  @Test
  public void aUnitWithoutAShellSendsTheBareBodyAsBefore() throws Exception {
    StubTransport transport = new StubTransport();
    this.senderWith(transport, "");
    TextProvider[] providers = {texts(global), UnitTextProvider.forCustomFile(null),
      UnitTextProvider.forCustomFile("noSuchFile")};

    for (TextProvider provider : providers) {
      this.sender.sendAsync(provider, ASSETS_URL, RECIPIENT, CODE, "CCAFS", UNIT);
    }

    assertEquals(providers.length, this.awaitDeliveryLines(providers.length).size());
    for (MimeMessage message : transport.delivered) {
      String html = htmlOf(message);
      assertEquals("the HTML part is the bare body", bareBody(), html);
      assertFalse(html.contains("<table"));
    }
  }

  @Test
  public void theShellWrapsExactlyTheBodyWithTheAssetsUrl() throws Exception {
    StubTransport transport = new StubTransport();
    this.senderWith(transport, "");

    this.sender.sendAsync(texts(override("email.layout", "[[{0}|{1}]]")), ASSETS_URL, RECIPIENT, CODE, "CCAFS", UNIT);

    assertEquals(1, this.awaitDeliveryLines(1).size());
    assertEquals("[[" + bareBody() + "|" + ASSETS_URL + "]]", htmlOf(transport.delivered.get(0)));
    assertEquals("the text part is never wrapped", textOf(transport.delivered.get(0)).contains("[["), false);
    List<EmailLog> rows = this.awaitRows(1);
    assertEquals("the row stores the wrapped HTML as sent", "[[" + bareBody() + "|" + ASSETS_URL + "]]",
      rows.get(0).getMessage());
  }

  @Test
  public void outsideProductionTheHeaderPrecedesTheWrappedHtmlAndTheRowStoresItWithoutTheHeader() throws Exception {
    StubTransport transport = new StubTransport();
    this.senderWith(transport, "");
    this.config.production = true;
    this.sender.sendAsync(UnitTextProvider.forCustomFile("aicrra"), ASSETS_URL, RECIPIENT, CODE, "AICCRA", UNIT);
    List<EmailLog> first = this.awaitRows(1);
    String productionBody = first.get(0).getMessage();

    this.config.production = false;
    this.sender.sendAsync(UnitTextProvider.forCustomFile("aicrra"), ASSETS_URL, RECIPIENT, CODE, "AICCRA", UNIT);

    List<EmailLog> rows = this.awaitRows(2);
    String lead = header(RECIPIENT) + "<br>" + SEPARATOR + "<br><br>";
    String sent = htmlOf(transport.delivered.get(1));
    assertTrue(sent, sent.startsWith(lead + "<table role=\"presentation\""));
    assertEquals("the sent HTML is the header plus the wrapped HTML", lead + productionBody, sent);
    assertTrue(productionBody.startsWith("<table role=\"presentation\""));
    assertEquals("the row holds the wrapped HTML, without the header", productionBody, rows.get(1).getMessage());
    assertFalse(rows.get(1).getMessage().contains(SEPARATOR));
    assertEquals(PREFIX + "[AICCRA] Your MARLO sign-in code", transport.delivered.get(1).getSubject());
    assertEquals("[AICCRA] Your MARLO sign-in code", rows.get(1).getSubject());
  }
}
