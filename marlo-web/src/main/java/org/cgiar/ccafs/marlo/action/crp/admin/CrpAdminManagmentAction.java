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
import org.cgiar.ccafs.marlo.data.manager.RoleManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.Role;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.data.model.UserRole;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CrpAdminManagmentAction extends BaseAction {

  private static final long serialVersionUID = 3355662668874414548L;

  private List<User> programManagmentTeam;
  private RoleManager roleManager;
  private Crp loggedCrp;


  @Inject
  public CrpAdminManagmentAction(APConfig config, RoleManager roleManager) {

    super(config);
    this.roleManager = roleManager;


  }


  public Crp getLoggedCrp() {
    return loggedCrp;
  }


  public List<User> getProgramManagmentTeam() {
    return programManagmentTeam;
  }


  @Override
  public void prepare() throws Exception {

    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    long pmu_permission = (long) this.getSession().get(APConstants.CRP_PMU_ROLE);
    Role role_pmue = roleManager.getRoleById(pmu_permission);
    for (UserRole userRole : role_pmue.getUserRoles()) {
      programManagmentTeam.add(userRole.getUser());
    }
    this.setBasePermission(this.getText(Permission.CRP_ADMIN_BASE_PERMISSION, loggedCrp.getId().toString()));

  }


  @Override
  public String save() {

    return SUCCESS;
  }

  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
  }


  public void setProgramManagmentTeam(List<User> programManagmentTeam) {
    this.programManagmentTeam = programManagmentTeam;
  }

}
