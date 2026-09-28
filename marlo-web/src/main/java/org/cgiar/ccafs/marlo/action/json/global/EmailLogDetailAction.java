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
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.model.EmailLog;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

/**
 * One logged email for the detail popup of System Admin -> Emails, message included. The attachment itself is not
 * sent, only its name.
 */
public class EmailLogDetailAction extends BaseAction {

  private static final long serialVersionUID = 6310962245843167307L;

  private final EmailLogManager emailLogManager;

  private final GlobalUnitManager globalUnitManager;

  private Map<String, Object> detail;

  @Inject
  public EmailLogDetailAction(APConfig config, EmailLogManager emailLogManager, GlobalUnitManager globalUnitManager) {
    super(config);
    this.emailLogManager = emailLogManager;
    this.globalUnitManager = globalUnitManager;
  }

  @Override
  public String execute() throws Exception {
    // The json package cannot use superAdminStack, which lives in marlo-default.
    if (!this.canAccessSuperAdmin()) {
      ServletActionContext.getResponse().setStatus(HttpServletResponse.SC_FORBIDDEN);
      return NONE;
    }
    Long id = EmailLogsAction.idValue(EmailLogsAction.value(this.getParameters(), "id"));
    EmailLog emailLog = id == null ? null : emailLogManager.getEmailLogById(id);
    if (emailLog == null) {
      ServletActionContext.getResponse().setStatus(HttpServletResponse.SC_NOT_FOUND);
      return NONE;
    }

    detail = new HashMap<>();
    detail.put("id", emailLog.getId());
    detail.put("subject", emailLog.getSubject());
    detail.put("to", emailLog.getTo());
    detail.put("cc", emailLog.getCc());
    detail.put("bcc", emailLog.getBbc());
    detail.put("date",
      emailLog.getDate() == null ? null : new SimpleDateFormat("yyyy-MM-dd HH:mm").format(emailLog.getDate()));
    detail.put("sent", Boolean.TRUE.equals(emailLog.getSucces()));
    detail.put("tried", emailLog.getTried());
    detail.put("error", emailLog.getError());
    detail.put("fileName", emailLog.getFileName());
    detail.put("messageId", emailLog.getMessageID());
    detail.put("source", emailLog.getSourceAction());
    detail.put("globalUnit", emailLog.getGlobalUnitId() == null ? null
      : EmailLogsAction.acronyms(globalUnitManager).get(emailLog.getGlobalUnitId()));
    detail.put("message", emailLog.getMessage());
    return SUCCESS;
  }

  public Map<String, Object> getDetail() {
    return detail;
  }
}
