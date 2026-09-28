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

import org.cgiar.ccafs.marlo.data.model.EmailLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * ThreadSendMail must log each email in exactly one row, with the outcome of the last server it tried. The main
 * failure used to be a row of its own, so an email the backup delivered stayed marked as failed and the retry of
 * System Admin -> Emails sent it again.
 * The servers are local sockets: a closed port refuses the connection at once, and FakeSmtpServer accepts the message.
 */
public class ThreadSendMailTest {

  /** The smallest SMTP dialogue JavaMail needs to hand over a message. */
  private static final class FakeSmtpServer implements Runnable {

    private final ServerSocket server;

    FakeSmtpServer() throws IOException {
      server = new ServerSocket(0);
      Thread thread = new Thread(this, "fake-smtp");
      thread.setDaemon(true);
      thread.start();
    }

    void close() throws IOException {
      server.close();
    }

    int port() {
      return server.getLocalPort();
    }

    @Override
    public void run() {
      while (!server.isClosed()) {
        try (Socket socket = server.accept()) {
          BufferedReader in =
            new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.US_ASCII));
          OutputStream out = socket.getOutputStream();
          this.reply(out, "220 fake");
          String line;
          while ((line = in.readLine()) != null) {
            String command = line.toUpperCase();
            if (command.startsWith("EHLO") || command.startsWith("HELO")) {
              this.reply(out, "250 fake");
            } else if (command.startsWith("DATA")) {
              this.reply(out, "354 go ahead");
              while ((line = in.readLine()) != null && !".".equals(line)) {
                // The message body is not inspected.
              }
              this.reply(out, "250 queued");
            } else if (command.startsWith("QUIT")) {
              this.reply(out, "221 bye");
              break;
            } else {
              this.reply(out, "250 ok");
            }
          }
        } catch (IOException e) {
          // The socket was closed by the test.
        }
      }
    }

    private void reply(OutputStream out, String line) throws IOException {
      out.write((line + "\r\n").getBytes(StandardCharsets.US_ASCII));
      out.flush();
    }
  }

  /** Answers only what the backup send reads. */
  private static final class BackupConfig extends APConfig {

    private final int backupPort;

    BackupConfig(int backupPort) {
      this.backupPort = backupPort;
    }

    @Override
    public String getEmail_authBackup() {
      return "false";
    }

    @Override
    public String getEmail_notificaction_backup() {
      return "backup@marlo.test";
    }

    @Override
    public String getEmail_password_backup() {
      return "";
    }

    @Override
    public String getEmail_starttlsbackup() {
      return "false";
    }

    @Override
    public String getEmail_user_backup() {
      return "";
    }

    @Override
    public String getEmailHostbackup() {
      return "127.0.0.1";
    }

    @Override
    public int getEmailPortbackup() {
      return backupPort;
    }

    @Override
    public boolean isProduction() {
      return false;
    }
  }

  /** Records every row it would have written, and never waits between attempts. */
  private static final class RecordingThreadSendMail extends ThreadSendMail {

    private final List<EmailLog> rows = new ArrayList<>();
    private int pauses;

    RecordingThreadSendMail(Message message, EmailLog emailLog, APConfig config) {
      super(message, emailLog.getSubject(), null, emailLog, config);
    }

    @Override
    void pause(String label) {
      pauses++;
    }

    @Override
    void persistEmailLog(EmailLog log) {
      rows.add(log);
    }
  }

  private static final Long GLOBAL_UNIT = 45L;

  private static final Date SENT_ON = new Date(1_790_000_000_000L);

  private static int closedPort() throws IOException {
    try (ServerSocket socket = new ServerSocket(0)) {
      return socket.getLocalPort();
    }
  }

  private Properties savedSystemProperties;

  private FakeSmtpServer smtp;

  private EmailLog emailLog() {
    EmailLog log = new EmailLog();
    log.setTo("someone@marlo.test");
    log.setSubject("[AICCRA] Welcome to MARLO");
    log.setMessage("<p>Welcome</p>");
    log.setDate(SENT_ON);
    log.setGlobalUnitId(GLOBAL_UNIT);
    log.setFileName("report.pdf");
    log.setFileContent(new byte[] {1, 2, 3});
    log.setMessageID("<main@marlo.test>");
    return log;
  }

  private MimeMessage message(int port) throws Exception {
    Properties properties = new Properties();
    properties.put("mail.smtp.host", "127.0.0.1");
    properties.put("mail.smtp.port", String.valueOf(port));
    properties.put("mail.smtp.connectiontimeout", "2000");
    properties.put("mail.smtp.timeout", "2000");
    MimeMessage message = new MimeMessage(Session.getInstance(properties));
    message.setFrom(new InternetAddress("main@marlo.test"));
    message.setRecipients(Message.RecipientType.TO, "someone@marlo.test");
    message.setSubject("[AICCRA] Welcome to MARLO");
    MimeMultipart content = new MimeMultipart();
    MimeBodyPart body = new MimeBodyPart();
    body.setText("Welcome");
    content.addBodyPart(body);
    message.setContent(content);
    return message;
  }

  @Before
  public void setUp() throws IOException {
    // The backup send writes its mail.* settings into the system properties; they are restored after each test.
    savedSystemProperties = (Properties) System.getProperties().clone();
    smtp = new FakeSmtpServer();
  }

  @After
  public void tearDown() throws IOException {
    smtp.close();
    System.setProperties(savedSystemProperties);
  }

  @Test
  public void testBackupDeliveryIsOneSentRow() throws Exception {
    RecordingThreadSendMail thread =
      new RecordingThreadSendMail(this.message(closedPort()), this.emailLog(), new BackupConfig(smtp.port()));

    thread.run();

    assertEquals("one row per email", 1, thread.rows.size());
    EmailLog row = thread.rows.get(0);
    assertEquals(Boolean.TRUE, row.getSucces());
    assertEquals("the ten failed attempts on the main server", Integer.valueOf(10), row.getTried());
    assertTrue(row.getError(),
      row.getError().startsWith("Delivered by the backup server after the main server failed: "));
    assertNull("a delivered email drops its attachment", row.getFileContent());
    assertNotNull(row.getMessageID());
    assertEquals(SENT_ON, row.getDate());
    assertEquals(GLOBAL_UNIT, row.getGlobalUnitId());
    assertEquals("a pause after each failed attempt but the last", 9, thread.pauses);
  }

  @Test
  public void testBothServersFailingIsOneFailedRow() throws Exception {
    RecordingThreadSendMail thread =
      new RecordingThreadSendMail(this.message(closedPort()), this.emailLog(), new BackupConfig(closedPort()));

    thread.run();

    assertEquals("one row per email", 1, thread.rows.size());
    EmailLog row = thread.rows.get(0);
    assertEquals(Boolean.FALSE, row.getSucces());
    assertEquals(Integer.valueOf(20), row.getTried());
    assertTrue(row.getError(), row.getError().startsWith("Main server: "));
    assertTrue(row.getError(), row.getError().contains(" Backup server: "));
    assertNotNull("the retry needs the attachment", row.getFileContent());
    assertEquals("<main@marlo.test>", row.getMessageID());
    assertEquals(SENT_ON, row.getDate());
    assertEquals(GLOBAL_UNIT, row.getGlobalUnitId());
    assertEquals(18, thread.pauses);
  }

  @Test
  public void testFailureWithoutMessageIsNeverReadAsSent() {
    // sendWithRetries answers null for a sent message, so the description of a failure can never be null.
    assertEquals("javax.mail.MessagingException", ThreadSendMail.describe(new MessagingException()));
    assertEquals("java.io.IOException",
      ThreadSendMail.describe(new MessagingException("outer", new java.io.IOException())));
    assertEquals("refused",
      ThreadSendMail.describe(new MessagingException("outer", new java.io.IOException("refused"))));
  }

  @Test
  public void testMainDeliveryIsOneSentRow() throws Exception {
    RecordingThreadSendMail thread =
      new RecordingThreadSendMail(this.message(smtp.port()), this.emailLog(), new BackupConfig(closedPort()));

    thread.run();

    assertEquals("one row per email", 1, thread.rows.size());
    EmailLog row = thread.rows.get(0);
    assertEquals(Boolean.TRUE, row.getSucces());
    assertEquals(Integer.valueOf(0), row.getTried());
    assertNull(row.getError());
    assertNull(row.getFileContent());
    assertEquals(SENT_ON, row.getDate());
    assertEquals(GLOBAL_UNIT, row.getGlobalUnitId());
    assertEquals(0, thread.pauses);
  }
}
