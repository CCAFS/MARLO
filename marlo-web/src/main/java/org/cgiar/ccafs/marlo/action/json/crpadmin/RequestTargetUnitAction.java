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

package org.cgiar.ccafs.marlo.action.json.crpadmin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.SendMailS;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class RequestTargetUnitAction extends BaseAction {


  private static final long serialVersionUID = 197365264899810106L;

  private String targetUnitName;
  // GlobalUnit Manager
  private GlobalUnitManager crpManager;
  private GlobalUnit loggedCrp;
  private Map<String, Object> status;
  private final SendMailS sendMail;

  @Inject
  public RequestTargetUnitAction(APConfig config, GlobalUnitManager crpManager, SendMailS sendMail) {
    super(config);
    this.crpManager = crpManager;
    this.sendMail = sendMail;
  }

  @Override
  public String execute() throws Exception {
    status = new HashMap<>();
    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());

    String crp = loggedCrp.getAcronym() != null && !loggedCrp.getAcronym().isEmpty() ? loggedCrp.getAcronym()
      : loggedCrp.getName();
    String subject = this.getText("targetunit.request.email.subject", new String[] {crp});
    String text = this.getText("targetUnit.request.email.text",
      new String[] {this.getCurrentUser().getComposedCompleteName(), this.getCurrentUser().getEmail(), targetUnitName});

    try {
      sendMail.send(config.getEmailNotification(), null, config.getEmailNotification(), subject, text, null, null, null,
        true);

      status.put("ok", true);
    } catch (Exception e) {
      status.put("ok", false);
    }


    return SUCCESS;

  }

  public Map<String, Object> getStatus() {
    return status;
  }

  public String getTargetUnitName() {
    return targetUnitName;
  }

  @Override
  public void prepare() throws Exception {
    // Map<String, Object> parameters = this.getParameters();
    // targetUnitName = StringUtils.trim(((String[]) parameters.get(APConstants.TARGET_UNIT_NAME))[0]);
    Map<String, Parameter> parameters = this.getParameters();
    targetUnitName = StringUtils.trim(parameters.get(APConstants.TARGET_UNIT_NAME).getMultipleValues()[0]);
  }


  public void setStatus(Map<String, Object> status) {
    this.status = status;
  }

  public void setTargetUnitName(String targetUnitName) {
    this.targetUnitName = targetUnitName;
  }

}
