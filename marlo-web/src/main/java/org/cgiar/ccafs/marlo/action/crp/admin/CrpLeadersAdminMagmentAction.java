/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/


package org.cgiar.ccafs.marlo.action.crp.admin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramLeader;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CrpLeadersAdminMagmentAction extends BaseAction {


  private static final long serialVersionUID = 4581809312156690381L;


  private Crp loggedCrp;
  private CrpManager crpManager;

  private List<CrpProgram> programs;


  @Inject
  public CrpLeadersAdminMagmentAction(APConfig config, CrpManager crpManager) {
    super(config);
    this.crpManager = crpManager;
  }

  public Crp getLoggedCrp() {
    return loggedCrp;
  }

  public List<CrpProgram> getPrograms() {
    return programs;
  }

  @Override
  public void prepare() throws Exception {
    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());
    programs = loggedCrp.getCrpPrograms().stream().filter(c -> c.isActive()).collect(Collectors.toList());
    for (CrpProgram crpProgram : programs) {

      crpProgram
        .setLeaders(crpProgram.getCrpProgramLeaders().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
    }
    String params[] = {loggedCrp.getAcronym()};
    this.setBasePermission(this.getText(Permission.CRP_ADMIN_BASE_PERMISSION, params));
    if (this.isHttpPost()) {
      for (CrpProgram crpProgram : programs) {
        crpProgram.getLeaders().clear();
      }
    }

  }


  @Override
  public String save() {

    if (this.hasPermission("*")) {


      for (CrpProgram crpProgram : programs) {
        for (CrpProgramLeader crpProgramLeader : crpProgram.getLeaders()) {
          if (crpProgramLeader.getId() == null) {
            /**
             * TODO:SAVE
             */
          }
        }

      }

      Collection<String> messages = this.getActionMessages();
      if (!messages.isEmpty()) {
        String validationMessage = messages.iterator().next();
        this.setActionMessages(null);
        this.addActionWarning(this.getText("saving.saved") + validationMessage);
      } else {
        this.addActionMessage(this.getText("saving.saved"));
      }
      messages = this.getActionMessages();
      return SUCCESS;
    } else {
      return NOT_AUTHORIZED;
    }
  }

  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

  public void setPrograms(List<CrpProgram> programs) {
    this.programs = programs;
  }
}
