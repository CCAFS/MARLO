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

package org.cgiar.ccafs.marlo.action.json.global;

import org.cgiar.ccafs.marlo.data.manager.EmailLogManager;
import org.cgiar.ccafs.marlo.data.model.EmailLog;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.OtpMailSender;
import org.cgiar.ccafs.marlo.utils.SendMailS;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.dispatcher.Parameter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * CHG-OTP-LOGIN-001-T18 (OPS-005, AC-18): System Admin -> Emails never resends a sign-in code email, by id or in bulk,
 * because a resent one would carry no code or an expired one. The email log manager and the mailer are hand-written
 * fakes that record what they received; every other failed row is still selected and resent as before.
 */
public class SendFailEmailActionTest {

  /** Records the calls of the log manager and answers from a fixed set of rows. */
  private static final class Rows {

    final EmailLog otp = row(1L, OtpMailSender.SOURCE_ACTION, false);
    final EmailLog other = row(2L, "/projects/partners", false);
    final EmailLog legacy = row(3L, null, null);
    final EmailLog otherFailed = row(4L, "background", false);
    final List<Long> saved = new ArrayList<Long>();
    final List<Long> resent = new ArrayList<Long>();
    int searches;

    EmailLogManager manager() {
      final List<EmailLog> all = Arrays.asList(this.otp, this.other, this.legacy, this.otherFailed);
      return (EmailLogManager) Proxy.newProxyInstance(EmailLogManager.class.getClassLoader(),
        new Class<?>[] {EmailLogManager.class}, (proxy, invoked, args) -> {
          switch (invoked.getName()) {
            case "getEmailLogById":
              for (EmailLog candidate : all) {
                if (candidate.getId().equals(args[0])) {
                  return candidate;
                }
              }
              return null;
            case "search":
              this.searches++;
              // The DAO filters on the outcome; every row here is one that was not sent.
              return new ArrayList<EmailLog>(all);
            case "saveEmailLog":
              this.saved.add(((EmailLog) args[0]).getId());
              return args[0];
            default:
              throw new UnsupportedOperationException(invoked.getName());
          }
        });
    }

    SendMailS mailer() {
      return new SendMailS(null, null, null, null) {

        private static final long serialVersionUID = 1L;

        @Override
        public boolean sendRetry(String toEmail, String ccEmail, String bbcEmail, String subject,
          String messageContent, byte[] attachment, String attachmentMimeType, String fileName, boolean isHtml,
          Long globalUnitId) {
          Rows.this.resent.add(Long.valueOf(toEmail.substring(0, toEmail.indexOf('@'))));
          return true;
        }
      };
    }

    private static EmailLog row(long id, String source, Boolean succes) {
      EmailLog row = new EmailLog();
      row.setId(id);
      row.setTo(id + "@partner.org");
      row.setSubject("subject " + id);
      row.setMessage("body " + id);
      row.setSourceAction(source);
      row.setSucces(succes);
      return row;
    }
  }

  private static SendFailEmailAction action(Rows rows, final Map<String, Parameter> parameters) {
    SendFailEmailAction action = new SendFailEmailAction(new APConfig(), rows.manager(), rows.mailer()) {

      private static final long serialVersionUID = 1L;

      @Override
      public boolean canAccessSuperAdmin() {
        return true;
      }

      @Override
      public Map<String, Parameter> getParameters() {
        return parameters;
      }
    };
    return action;
  }

  private static Map<String, Parameter> parameters(String... namesAndValues) {
    Map<String, Parameter> parameters = new HashMap<>();
    for (int i = 0; i < namesAndValues.length; i += 2) {
      parameters.put(namesAndValues[i], new Parameter.Request(namesAndValues[i], namesAndValues[i + 1]));
    }
    return parameters;
  }

  private static List<Long> ids(List<EmailLog> emails) {
    List<Long> ids = new ArrayList<Long>();
    for (EmailLog email : emails) {
      ids.add(email.getId());
    }
    return ids;
  }

  private static SendFailEmailAction run(Rows rows, String... namesAndValues) throws Exception {
    SendFailEmailAction action = action(rows, parameters(namesAndValues));
    action.prepare();
    assertEquals("success", action.execute());
    return action;
  }

  @Test
  public void aCodeEmailAskedForByIdIsNotListed() throws Exception {
    Rows rows = new Rows();

    SendFailEmailAction action = run(rows, "type", "0", "id", "1");

    assertTrue("the sign-in code row is excluded", action.getEmails().isEmpty());
  }

  @Test
  public void aCodeEmailAskedForByIdIsNeverResentNorSaved() throws Exception {
    Rows rows = new Rows();

    SendFailEmailAction action = run(rows, "type", "1", "id", "1");

    assertTrue(action.getResults().isEmpty());
    assertEquals("nothing is sent", Collections.<Long>emptyList(), rows.resent);
    assertEquals("nothing is written back", Collections.<Long>emptyList(), rows.saved);
  }

  @Test
  public void anotherFailedRowAskedForByIdIsStillSelected() throws Exception {
    Rows rows = new Rows();

    SendFailEmailAction action = run(rows, "type", "0", "id", "2");

    assertEquals(Arrays.asList(2L), ids(action.getEmails()));
  }

  @Test
  public void theBulkListDropsTheCodeEmailsAndKeepsEveryOtherFailedRow() throws Exception {
    Rows rows = new Rows();

    SendFailEmailAction action = run(rows, "type", "0");

    assertEquals(1, rows.searches);
    assertEquals("a row without a source is a legacy row and stays", Arrays.asList(2L, 3L, 4L),
      ids(action.getEmails()));
  }

  @Test
  public void theBulkResendSendsOnlyTheOtherRowsAndNeverTheCodeEmail() throws Exception {
    Rows rows = new Rows();

    SendFailEmailAction action = run(rows, "type", "1");

    assertEquals(Arrays.asList(2L, 3L, 4L), rows.resent);
    assertEquals(Arrays.asList(2L, 3L, 4L), rows.saved);
    assertEquals(3, action.getResults().size());
    for (Map<String, String> result : action.getResults()) {
      assertTrue(result.get("id"), !"1".equals(result.get("id")));
    }
  }
}
