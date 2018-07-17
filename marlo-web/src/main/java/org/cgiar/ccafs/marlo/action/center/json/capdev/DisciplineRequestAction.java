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

package org.cgiar.ccafs.marlo.action.center.json.capdev;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class DisciplineRequestAction extends BaseAction {


  private static final long serialVersionUID = -217744901715732782L;


  private static final Logger LOG = LoggerFactory.getLogger(DisciplineRequestAction.class);


  private Map<String, Object> sucess;

  private Long capdevID;

  private final SendMailS sendMail;
  private GlobalUnitManager crpManager;
  private GlobalUnit loggedCrp;
  private String disciplineText;
  private boolean messageSent;

  @Inject
  public DisciplineRequestAction(APConfig config, SendMailS sendMail, GlobalUnitManager crpManager) {
    super(config);
    this.sendMail = sendMail;
    this.crpManager = crpManager;

  }

  @Override
  public String execute() throws Exception {
    try {

      sucess = new HashMap<String, Object>();
      String subject;
      StringBuilder message = new StringBuilder();

      subject =
        "[MARLO-" + this.getCrpSession().toUpperCase() + "] CapDev - Discipline Verification (" + disciplineText + ")";

      // Message content
      message.append(this.getCurrentUser().getFirstName() + " " + this.getCurrentUser().getLastName() + " ");
      message.append("(" + this.getCurrentUser().getEmail() + ") ");
      message.append("is requesting to add the following discipline: ");

      message.append("</br></br>");
      message.append("CapDev Id : ");
      message.append(capdevID);

      message.append("</br></br>");
      message.append("Discipline : ");
      message.append(disciplineText);
      message.append(".</br>");
      message.append("</br>");


      try {
        sendMail.send(config.getEmailNotification(), null, config.getEmailNotification(), subject, message.toString(),
          null, null, null, true);
      } catch (Exception e) {
        messageSent = false;
      }
      messageSent = true;
    } catch (Exception e) {
      messageSent = false;
    }

    if (messageSent) {
      sucess.put("result", "1");
    } else {
      sucess.put("result", "0");
    }

    LOG.info("The user {} send a message requesting add discipline to the capdev {}", this.getCurrentUser().getEmail(),
      capdevID);
    return SUCCESS;

  }

  public Map<String, Object> getSucess() {
    return sucess;
  }


  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();
    capdevID = Long.parseLong((StringUtils.trim(parameters.get(APConstants.CAPDEV_ID).getMultipleValues()[0])));

    disciplineText = StringUtils.trim(parameters.get(APConstants.CAPDEV_ID).getMultipleValues()[0]);

    // Get loggerCrp
    try {
      loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
      loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());
    } catch (Exception e) {
      LOG.error("Failed to get " + APConstants.SESSION_CRP + " parameter. Exception: " + e.getMessage());
    }
  }


  public void setSucess(Map<String, Object> sucess) {
    this.sucess = sucess;
  }

}
