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

import org.cgiar.ccafs.marlo.security.otp.OtpLog;
import org.cgiar.ccafs.marlo.security.otp.OtpLog.DeliveryOutcome;

import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.opensymphony.xwork2.TextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sends the one-time-code email of the Center email sign-in (CHG-OTP-LOGIN-001-T09, FN-017, FN-018, SEC-001, OPS-002,
 * NF-001, DD-7).
 * <p>
 * It is deliberately not {@code SendMailS}. That class persists every body, sets no SMTP timeouts, and honours the
 * notification switch and the support redirect, none of which the code email may do. This class builds its own mail
 * session from the same {@link APConfig} SMTP getters, with connect, read and write timeouts, sends to one recipient
 * on a dedicated executor, and never writes to the email log, so the code is never persisted (SEC-001). It does not
 * read the notification switch or the support redirect (FN-018), so the code goes to the submitted address in every
 * environment (OQ-5 default).
 * <p>
 * The send is asynchronous: the request thread renders the message, hands it to a pool of 2 daemon threads behind a
 * queue of 100, and returns. A full queue is an {@code email_failed}, not an error for the caller, and the response
 * was already the neutral one. Every hand-off writes exactly one {@code auth.otp.delivery} line through
 * {@link OtpLog}: {@code delivered} at INFO, {@code email_failed} at WARN. A failure also writes the class name of
 * the cause, and never its message, which can carry the address or the SMTP exchange.
 * <p>
 * It is a container singleton: {@code @Named}, found by the component scan of {@code ApplicationContextConfig} and
 * created once with the {@code APConfig} constructor. Spring's default scope is singleton, so the limit of 2 threads
 * and 100 queued sends holds for the whole application. {@link #shutdown()} is the {@code @PreDestroy} hook, so the
 * workers stop with the context. Do not construct it elsewhere: inject it.
 */
@Named
public final class OtpMailSender {

  /** The send seam: the production one is {@link Transport#send(Message)}; tests substitute a stub. */
  interface MailTransport {

    void deliver(MimeMessage message) throws MessagingException;
  }

  static final String KEY_APP_NAME = "login.otp.email.appName";
  static final String KEY_SUBJECT = "login.otp.email.subject";
  static final String KEY_TEXT = "login.otp.email.text";
  static final String KEY_HTML = "login.otp.email.html";
  /** The existing support contact key, so a program that overrides it also changes this email. */
  static final String KEY_SUPPORT = "footer.contactEmail";

  /** Connect, read and write timeout when {@code otp.smtp.timeout.ms} is unset or malformed. */
  static final int DEFAULT_TIMEOUT_MS = 10000;
  /** The highest timeout honoured: a larger configured value is clamped to it. */
  static final int MAX_TIMEOUT_MS = 30000;
  private static final String CHARSET = "UTF-8";
  /** The code validity stated in the email; the challenge TTL of the design (FN-017). */
  static final int CODE_VALIDITY_MINUTES = 5;
  static final int WORKERS = 2;
  static final int QUEUE_CAPACITY = 100;
  static final String THREAD_PREFIX = "otp-mail-";

  private static final Logger LOG = LoggerFactory.getLogger(OtpMailSender.class);

  /** A placeholder left in a rendered text means an argument was missing: such a text is never sent. */
  private static final Pattern UNRESOLVED = Pattern.compile("\\{\\d");

  private static final String ADDRESS_FORBIDDEN = ",;<>\"()[]\\:";

  private final APConfig config;
  private final MailTransport transport;
  private final ThreadPoolExecutor executor;

  /**
   * The only constructor the container may use; the one below is the test seam.
   *
   * @param config the application settings the SMTP session is built from
   */
  @Inject
  public OtpMailSender(APConfig config) {
    this(config, new MailTransport() {

      @Override
      public void deliver(MimeMessage message) throws MessagingException {
        Transport.send(message);
      }
    });
  }

  OtpMailSender(APConfig config, MailTransport transport) {
    this.config = config;
    this.transport = transport;
    final AtomicInteger counter = new AtomicInteger();
    this.executor = new ThreadPoolExecutor(WORKERS, WORKERS, 0L, TimeUnit.MILLISECONDS,
      new ArrayBlockingQueue<Runnable>(QUEUE_CAPACITY), new ThreadFactory() {

        @Override
        public Thread newThread(Runnable task) {
          Thread worker = new Thread(task, THREAD_PREFIX + counter.incrementAndGet());
          worker.setDaemon(true);
          return worker;
        }
      }, new ThreadPoolExecutor.AbortPolicy());
  }

  /**
   * Renders the code email and hands it to the executor. It never throws: a recipient that is not a single address,
   * an unresolved text, a full queue and a transport failure are all logged as {@code email_failed}, and nothing is
   * sent in the first three cases.
   * <p>
   * Callers must pass {@code pending.email}, the normalised address held in the session's {@code OtpPending}, and
   * never the raw request parameter.
   *
   * @param texts the text provider that resolves the i18n keys, normally the action; read on the calling thread only
   * @param recipient the single address to send to
   * @param code the clear code, which lives only in this call and the message; it is never logged
   * @param guAcronym the Global Unit acronym, for the delivery line
   */
  public void sendAsync(TextProvider texts, String recipient, String code, String guAcronym) {
    long started = System.nanoTime();
    String domain = "";
    try {
      InternetAddress to = singleAddress(recipient);
      domain = domainOf(to);
      final MimeMessage message = this.build(texts, to, code);
      final String gu = guAcronym;
      final String logDomain = domain;
      final long handedOff = started;
      this.executor.execute(new Runnable() {

        @Override
        public void run() {
          OtpMailSender.this.deliver(message, gu, logDomain, handedOff);
        }
      });
    } catch (MessagingException | RuntimeException e) {
      // RejectedExecutionException (queue full) is a RuntimeException and lands here too.
      this.failed(e, guAcronym, domain, started);
    }
  }

  /**
   * Stops accepting sends; the ones already queued still run. The container calls it when the context closes. A send
   * after it is a rejected hand-off, logged as {@code email_failed}.
   */
  @PreDestroy
  public void shutdown() {
    this.executor.shutdown();
  }

  private MimeMessage build(TextProvider texts, InternetAddress to, String code) throws MessagingException {
    if (code == null || code.trim().isEmpty()) {
      throw new IllegalArgumentException("blank code");
    }
    String appName = text(texts, KEY_APP_NAME);
    String support = text(texts, KEY_SUPPORT);
    String minutes = String.valueOf(CODE_VALIDITY_MINUTES);
    String subject = text(texts, KEY_SUBJECT, appName);
    String plain = text(texts, KEY_TEXT, appName, code, minutes, support);
    String html = text(texts, KEY_HTML, escapeHtml(appName), escapeHtml(code), minutes, escapeHtml(support));

    MimeMessage message = new MimeMessage(this.session());
    message.setFrom(new InternetAddress(require(this.config.getEmailNotification(), "sender")));
    message.setRecipient(Message.RecipientType.TO, to);
    message.setSubject(subject, CHARSET);
    message.setSentDate(new Date());
    MimeMultipart body = new MimeMultipart("alternative");
    MimeBodyPart textPart = new MimeBodyPart();
    textPart.setText(plain, CHARSET);
    body.addBodyPart(textPart);
    MimeBodyPart htmlPart = new MimeBodyPart();
    htmlPart.setText(html, CHARSET, "html");
    body.addBodyPart(htmlPart);
    message.setContent(body);
    return message;
  }

  private Session session() {
    final String username = this.config.getEmailUsername();
    final String password = this.config.getEmailPassword();
    Properties properties = new Properties();
    put(properties, "mail.smtp.host", require(this.config.getEmailHost(), "host"));
    put(properties, "mail.smtp.port", String.valueOf(this.config.getEmailPort()));
    put(properties, "mail.smtp.auth", this.config.getEmail_auth());
    put(properties, "mail.smtp.starttls.enable", this.config.getEmail_starttls());
    String timeout = String.valueOf(timeoutMs(this.config.getOtpSmtpTimeoutMs()));
    put(properties, "mail.smtp.connectiontimeout", timeout);
    put(properties, "mail.smtp.timeout", timeout);
    put(properties, "mail.smtp.writetimeout", timeout);
    return Session.getInstance(properties, new Authenticator() {

      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
      }
    });
  }

  private void deliver(MimeMessage message, String gu, String domain, long started) {
    try {
      this.transport.deliver(message);
    } catch (Throwable e) {
      // Throwable: an Error from the transport would otherwise end the worker without the delivery line. The class
      // name is all that is logged.
      this.failed(e, gu, domain, started);
      return;
    }
    OtpLog.delivery(DeliveryOutcome.DELIVERED, gu, domain, elapsedMs(started));
  }

  private void failed(Throwable cause, String gu, String domain, long started) {
    // The class name only: the message of a mail exception can carry the address or the SMTP exchange (SEC-001).
    LOG.warn("auth.otp.mail outcome=send_failed cause={}", cause.getClass().getSimpleName());
    OtpLog.delivery(DeliveryOutcome.EMAIL_FAILED, gu, domain, elapsedMs(started));
  }

  /**
   * Parses the connect, read and write timeout of the mail session.
   *
   * @param configured the raw trimmed setting, possibly {@code null} or empty
   * @return the timeout in milliseconds; {@link #DEFAULT_TIMEOUT_MS} when the value is unset, not a whole number, or
   *         not positive, and at most {@link #MAX_TIMEOUT_MS}
   */
  static int timeoutMs(String configured) {
    if (configured == null) {
      return DEFAULT_TIMEOUT_MS;
    }
    try {
      int parsed = Integer.parseInt(configured.trim());
      return parsed > 0 ? Math.min(parsed, MAX_TIMEOUT_MS) : DEFAULT_TIMEOUT_MS;
    } catch (NumberFormatException e) {
      return DEFAULT_TIMEOUT_MS;
    }
  }

  /**
   * Accepts exactly one bare address. Anything that could add a recipient or a header is refused: a comma or a
   * semicolon list, a display name, an angle bracket, a quote, whitespace or any control character (CR and LF
   * included), and any non-ASCII character.
   */
  static InternetAddress singleAddress(String recipient) throws AddressException {
    if (recipient == null || recipient.isEmpty()) {
      throw new AddressException("no recipient");
    }
    for (int i = 0; i < recipient.length(); i++) {
      char c = recipient.charAt(i);
      if (c <= ' ' || c >= 127 || ADDRESS_FORBIDDEN.indexOf(c) >= 0) {
        throw new AddressException("not a single bare address");
      }
    }
    if (recipient.indexOf('@') != recipient.lastIndexOf('@')) {
      throw new AddressException("not a single bare address");
    }
    InternetAddress address = new InternetAddress(recipient, true);
    address.validate();
    if (!recipient.equals(address.getAddress())) {
      throw new AddressException("not a single bare address");
    }
    return address;
  }

  private static String domainOf(InternetAddress address) {
    String value = address.getAddress();
    return value.substring(value.indexOf('@') + 1);
  }

  /** Resolves a key through the provider; an unresolved key or placeholder fails the render. */
  private static String text(TextProvider texts, String key, String... arguments) {
    String rendered = texts.getText(key, arguments);
    if (rendered == null || key.equals(rendered) || UNRESOLVED.matcher(rendered).find()) {
      throw new IllegalStateException("unresolved text " + key);
    }
    return rendered;
  }

  private static String escapeHtml(String value) {
    StringBuilder escaped = new StringBuilder(value.length() + 16);
    for (int i = 0; i < value.length(); i++) {
      char c = value.charAt(i);
      switch (c) {
        case '&':
          escaped.append("&amp;");
          break;
        case '<':
          escaped.append("&lt;");
          break;
        case '>':
          escaped.append("&gt;");
          break;
        case '"':
          escaped.append("&quot;");
          break;
        case '\'':
          escaped.append("&#39;");
          break;
        default:
          escaped.append(c);
          break;
      }
    }
    return escaped.toString();
  }

  private static String require(String value, String what) {
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalStateException("mail " + what + " is not configured");
    }
    return value.trim();
  }

  private static void put(Properties properties, String key, String value) {
    if (value != null) {
      properties.setProperty(key, value);
    }
  }

  private static long elapsedMs(long startedNanos) {
    return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedNanos);
  }
}
