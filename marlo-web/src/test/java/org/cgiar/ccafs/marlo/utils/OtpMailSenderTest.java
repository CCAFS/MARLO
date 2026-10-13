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
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
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
 * CHG-OTP-LOGIN-001-T09: the code email is built from the real {@code global.properties}, goes to one recipient on a
 * 2-thread executor, writes one {@code auth.otp.delivery} line per hand-off, and never touches the email log. The
 * transport is a stub; no SMTP server is involved. Expected values are literals from the requirements (FN-017,
 * OPS-002, NF-001).
 */
public class OtpMailSenderTest {

  private static final String CODE = "123456";
  private static final String RECIPIENT = "jane.doe@partner.org";
  private static final String SOURCE =
    "src/main/java/org/cgiar/ccafs/marlo/utils/OtpMailSender.java";

  private static Properties global;

  private Logger deliveryLogger;
  private Logger mailerLogger;
  private Level deliveryLevel;
  private Level mailerLevel;
  private ListAppender<ILoggingEvent> appender;
  private OtpMailSender sender;

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

    TestConfig(String timeout) {
      this.timeout = timeout;
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
      return "support@test.org";
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
    this.deliveryLogger = (Logger) LoggerFactory.getLogger(OtpLog.class);
    this.mailerLogger = (Logger) LoggerFactory.getLogger(OtpMailSender.class);
    this.deliveryLevel = this.deliveryLogger.getLevel();
    this.mailerLevel = this.mailerLogger.getLevel();
    this.deliveryLogger.setLevel(Level.TRACE);
    this.mailerLogger.setLevel(Level.TRACE);
    this.appender = new ListAppender<ILoggingEvent>();
    this.appender.start();
    this.deliveryLogger.addAppender(this.appender);
    this.mailerLogger.addAppender(this.appender);
  }

  @After
  public void detachAppender() {
    if (this.sender != null) {
      this.sender.shutdown();
    }
    this.deliveryLogger.detachAppender(this.appender);
    this.mailerLogger.detachAppender(this.appender);
    this.deliveryLogger.setLevel(this.deliveryLevel);
    this.mailerLogger.setLevel(this.mailerLevel);
  }

  private OtpMailSender senderWith(StubTransport transport, String timeout) {
    this.sender = new OtpMailSender(new TestConfig(timeout), transport);
    return this.sender;
  }

  private void send(String recipient) {
    this.sender.sendAsync(texts(global), recipient, CODE, "CCAFS");
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

    this.sender.sendAsync(texts(override("footer.contactEmail", "a<b>&\"c''")), RECIPIENT, CODE, "CCAFS");

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

    this.sender.sendAsync(texts(override("login.otp.email.html", "<p>{0} {1} {9}</p>")), RECIPIENT, CODE, "CCAFS");
    Properties noKey = new Properties();
    noKey.putAll(global);
    noKey.remove("login.otp.email.subject");
    this.sender.sendAsync(texts(noKey), RECIPIENT, CODE, "CCAFS");

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

    this.sender.sendAsync(texts(global), RECIPIENT, null, "CCAFS");
    this.sender.sendAsync(texts(global), RECIPIENT, "  ", "CCAFS");

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
      OtpMailSender local = new OtpMailSender(new TestConfig(c[0]), transport);
      try {
        local.sendAsync(texts(global), RECIPIENT, CODE, "CCAFS");
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
    assertEquals(Arrays.<Class<?>>asList(APConfig.class), Arrays.asList(injectable.get(0).getParameterTypes()));

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
    // support redirect, all of which the code email must ignore (DD-7, P-7). Comments are stripped first.
    String source = new String(Files.readAllBytes(Paths.get(SOURCE)), StandardCharsets.UTF_8);
    String code = source.replaceAll("(?s)/\\*.*?\\*/", "").replaceAll("//[^\n]*", "");
    for (String forbidden : new String[] {"SendMailS", "ThreadSendMail", "EmailLogManager", "EmailLog", "email_log",
      "isNotificationDisabled", "isRestrictedToSupport", "getEmail_pmu", "isProduction", "CustomParameter"}) {
      assertFalse("OtpMailSender must not use " + forbidden, code.contains(forbidden));
    }
  }
}
