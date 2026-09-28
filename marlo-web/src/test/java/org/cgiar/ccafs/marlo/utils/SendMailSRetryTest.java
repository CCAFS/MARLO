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

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CustomParameterManager;
import org.cgiar.ccafs.marlo.data.model.CustomParameter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Proxy;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * The retry of System Admin -> Emails must apply the email specificities of the global unit recorded on the row, as
 * the first send did. The rows keep the recipients they were logged with, which in a database copied from production
 * are real people, so a retry from an environment that sends only to the support team must not reach them.
 * The mail server is a local socket that records every RCPT TO it is given.
 */
public class SendMailSRetryTest {

  /** The smallest SMTP dialogue JavaMail needs, keeping the address of every RCPT TO. */
  private static final class RecordingSmtpServer implements Runnable {

    private final ServerSocket server;
    private final List<String> recipients = new CopyOnWriteArrayList<>();

    RecordingSmtpServer() throws IOException {
      server = new ServerSocket(0);
      Thread thread = new Thread(this, "recording-smtp");
      thread.setDaemon(true);
      thread.start();
    }

    void close() throws IOException {
      server.close();
    }

    int port() {
      return server.getLocalPort();
    }

    private void reply(OutputStream out, String line) throws IOException {
      out.write((line + "\r\n").getBytes(StandardCharsets.US_ASCII));
      out.flush();
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
            if (command.startsWith("RCPT TO:")) {
              recipients.add(line.substring(line.indexOf('<') + 1, line.indexOf('>')));
              this.reply(out, "250 ok");
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
  }

  /** Answers only what sendRetry reads. */
  private static final class MailConfig extends APConfig {

    private final int port;
    private final boolean production;

    MailConfig(int port, boolean production) {
      this.port = port;
      this.production = production;
    }

    @Override
    public String getEmail_auth() {
      return "false";
    }

    @Override
    public String getEmail_starttls() {
      return "false";
    }

    @Override
    public String getEmailHost() {
      return "127.0.0.1";
    }

    @Override
    public String getEmailNotification() {
      return SUPPORT;
    }

    @Override
    public String getEmailPassword() {
      return "";
    }

    @Override
    public int getEmailPort() {
      return port;
    }

    @Override
    public String getEmailUsername() {
      return "";
    }

    @Override
    public boolean isProduction() {
      return production;
    }
  }

  private static final String SUPPORT = "support@marlo.test";

  private static final Long GLOBAL_UNIT = 45L;

  /** A CustomParameterManager answering the two email specificities with the given values. */
  private static CustomParameterManager specificities(Map<String, String> values) {
    return (CustomParameterManager) Proxy.newProxyInstance(CustomParameterManager.class.getClassLoader(),
      new Class<?>[] {CustomParameterManager.class}, (proxy, method, args) -> {
        if (!"getCustomParameterByParameterKeyAndGlobalUnitId".equals(method.getName())
          || !values.containsKey(args[0])) {
          return null;
        }
        CustomParameter parameter = new CustomParameter();
        parameter.setValue(values.get(args[0]));
        return parameter;
      });
  }

  private Properties savedSystemProperties;

  private RecordingSmtpServer smtp;

  private boolean retry(boolean production, Map<String, String> values, Long globalUnitId, String bcc) {
    SendMailS sendMail = new SendMailS(new MailConfig(smtp.port(), production), null, null, specificities(values));
    return sendMail.sendRetry("someone@marlo.test", "colleague@marlo.test", bcc, "[AICCRA] Welcome", "<p>Hi</p>",
      null, null, null, true, globalUnitId);
  }

  @Before
  public void setUp() throws IOException {
    // sendRetry writes its mail.* settings into the system properties; they are restored after each test.
    savedSystemProperties = (Properties) System.getProperties().clone();
    smtp = new RecordingSmtpServer();
  }

  @After
  public void tearDown() throws IOException {
    smtp.close();
    System.setProperties(savedSystemProperties);
  }

  @Test
  public void testDisabledNotificationsSendNothing() {
    boolean sent = this.retry(true, Map.of(APConstants.CRP_EMAIL_NOTIFICATIONS, "false",
      APConstants.CRP_EMAIL_SUPPORT_TEAM, "false"), GLOBAL_UNIT, null);

    assertFalse(sent);
    assertTrue(smtp.recipients.isEmpty());
  }

  @Test
  public void testRowWithoutGlobalUnitGoesOnlyToSupport() {
    boolean sent = this.retry(true, Map.of(), null, "watcher@marlo.test");

    assertTrue(sent);
    assertEquals(List.of(SUPPORT), smtp.recipients);
  }

  @Test
  public void testSupportTeamSpecificityReplacesTheRecipients() {
    boolean sent = this.retry(false, Map.of(APConstants.CRP_EMAIL_SUPPORT_TEAM, "true"), GLOBAL_UNIT,
      "watcher@marlo.test");

    assertTrue(sent);
    assertEquals("neither the stored TO, CC nor BCC is reached", List.of(SUPPORT), smtp.recipients);
  }

  @Test
  public void testTestEnvironmentRetryWithoutBccIsSent() {
    // The TO used to be the BCC unconditionally, so a row without BCC failed on every retry.
    boolean sent = this.retry(false, Map.of(APConstants.CRP_EMAIL_SUPPORT_TEAM, "false"), GLOBAL_UNIT, null);

    assertTrue(sent);
    assertEquals(List.of("someone@marlo.test"), smtp.recipients);
  }

  @Test
  public void testUnrestrictedProductionRetryReachesTheStoredRecipients() {
    boolean sent = this.retry(true, Map.of(APConstants.CRP_EMAIL_SUPPORT_TEAM, "false"), GLOBAL_UNIT,
      "watcher@marlo.test");

    assertTrue(sent);
    assertTrue(
      smtp.recipients.containsAll(List.of("someone@marlo.test", "colleague@marlo.test", "watcher@marlo.test")));
  }
}
