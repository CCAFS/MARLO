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

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.EmailLogManager;
import org.cgiar.ccafs.marlo.data.model.EmailLog;
import org.cgiar.ccafs.marlo.data.model.EmailLogSearch;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.SendMailS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Christian Garcia - CIAT/CCAFS
 * @author Julián Rodríguez - CIAT/CCAFS
 */
public class SendFailEmailAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = -6338578372277010087L;

  private static final Logger LOG = LoggerFactory.getLogger(SendFailEmailAction.class);


  private EmailLogManager emailLogManager;
  private final SendMailS sendMail;
  private ArrayList<Map<String, String>> results;
  private ArrayList<EmailLog> emails;

  private int type;
  String contentType = "application/pdf";

  @Inject
  public SendFailEmailAction(APConfig config, EmailLogManager emailLogManager, SendMailS sendMail) {
    super(config);
    this.emailLogManager = emailLogManager;
    this.sendMail = sendMail;

  }


  @Override
  public String execute() throws Exception {
    // The json package cannot use superAdminStack, which lives in marlo-default, and without this check any
    // signed-in user could list the failed emails of every global unit or resend all of them.
    if (!this.canAccessSuperAdmin()) {
      ServletActionContext.getResponse().setStatus(HttpServletResponse.SC_FORBIDDEN);
      return NONE;
    }
    results = new ArrayList<>();
    List<EmailLog> emailLogs = this.selectedEmailLogs();

    switch (type) {
      case 0:
        emails = new ArrayList<>();
        emails.addAll(emailLogs);
        break;
      case 1:
        for (EmailLog emailLog : emailLogs) {
          boolean send = false;
          // One email that cannot be resent must not stop the rest of the batch.
          try {
            send = sendMail.sendRetry(emailLog.getTo(), emailLog.getCc(), emailLog.getBbc(), emailLog.getSubject(),
              emailLog.getMessage(), emailLog.getFileContent(), contentType, emailLog.getFileName(), true,
              emailLog.getGlobalUnitId());
            if (send) {
              emailLog.setFileContent(null);
            }
            emailLog.setSucces(send);
            emailLogManager.saveEmailLog(emailLog);
          } catch (Exception e) {
            LOG.error("Could not resend the logged email {}", emailLog.getId(), e);
          }
          HashMap<String, String> map = new HashMap<>();
          map.put("id", String.valueOf(emailLog.getId()));
          map.put("subject", emailLog.getSubject());
          map.put("to", emailLog.getTo());

          map.put("result", send + "");
          results.add(map);
        }
        break;

      default:
        break;
    }

    return SUCCESS;
  }


  /**
   * The emails not sent that the request asks for: the one given by "id", or every one matching the filters of
   * System Admin -> Emails, which are all of them when no filter is set. A row whose outcome was never recorded
   * counts as not sent; the previous filter threw a NullPointerException on it.
   */
  private List<EmailLog> selectedEmailLogs() {
    Map<String, Parameter> parameters = this.getParameters();
    Long id = EmailLogsAction.idValue(EmailLogsAction.value(parameters, "id"));
    if (id != null) {
      EmailLog emailLog = emailLogManager.getEmailLogById(id);
      List<EmailLog> single = new ArrayList<>();
      if (emailLog != null && !Boolean.TRUE.equals(emailLog.getSucces())) {
        single.add(emailLog);
      }
      return single;
    }
    EmailLogSearch search = EmailLogsAction.read(parameters);
    search.setSent(false);
    return emailLogManager.search(search);
  }

  public ArrayList<EmailLog> getEmails() {
    return emails;
  }


  public ArrayList<Map<String, String>> getResults() {
    return results;
  }


  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();
    // A missing or unreadable type does nothing, instead of throwing before execute() checks the permission.
    type = EmailLogsAction.intValue(parameters, "type", -1);


  }


  public void setEmails(ArrayList<EmailLog> emails) {
    this.emails = emails;
  }


  public void setResults(ArrayList<Map<String, String>> results) {
    this.results = results;
  }


}
