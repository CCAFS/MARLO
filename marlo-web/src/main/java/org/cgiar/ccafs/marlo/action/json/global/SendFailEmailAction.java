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
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.SendMailS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

/**
 * @author Christian Garcia - CIAT/CCAFS
 * @author Julián Rodríguez - CIAT/CCAFS
 */
public class SendFailEmailAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = -6338578372277010087L;


  private EmailLogManager emailLogManager;
  private final SendMailS sendMail;
  private ArrayList<Map<String, String>> results;
  String contentType = "application/pdf";

  @Inject
  public SendFailEmailAction(APConfig config, EmailLogManager emailLogManager, SendMailS sendMail) {
    super(config);
    this.emailLogManager = emailLogManager;
    this.sendMail = sendMail;

  }


  @Override
  public String execute() throws Exception {
    results = new ArrayList<>();
    List<EmailLog> emailLogs = emailLogManager.findAll().stream().filter(c -> c.getSucces().booleanValue() == false)
      .collect(Collectors.toList());
    for (EmailLog emailLog : emailLogs) {
      boolean send = sendMail.sendRetry(emailLog.getTo(), emailLog.getCc(), emailLog.getBbc(), emailLog.getSubject(),
        emailLog.getMessage(), emailLog.getFileContent(), contentType, emailLog.getFileName(), true);
      if (send) {
        emailLog.setFileContent(null);
      }
      emailLog.setSucces(send);
      emailLogManager.saveEmailLog(emailLog);
      HashMap<String, String> map = new HashMap<>();
      map.put("id", emailLog.getId().toString());
      map.put("subject", emailLog.getSubject());
      map.put("result", send + "");
      results.add(map);
    }
    return SUCCESS;
  }


  public ArrayList<Map<String, String>> getResults() {
    return results;
  }


  @Override
  public void prepare() throws Exception {

  }


  public void setResults(ArrayList<Map<String, String>> results) {
    this.results = results;
  }


}
