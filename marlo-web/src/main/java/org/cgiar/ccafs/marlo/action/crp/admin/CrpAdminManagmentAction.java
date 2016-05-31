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
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.manager.UserRoleManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.Role;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.data.model.UserRole;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CrpAdminManagmentAction extends BaseAction {

  private static final long serialVersionUID = 3355662668874414548L;

  private List<User> programManagmentTeam;
  private RoleManager roleManager;
  private UserRoleManager userRoleManager;
  private UserManager userManager;

  private Crp loggedCrp;
  private Role role_pmue;

  @Inject
  public CrpAdminManagmentAction(APConfig config, RoleManager roleManager, UserRoleManager userRoleManager,
    UserManager userManager) {

    super(config);
    this.roleManager = roleManager;
    this.userRoleManager = userRoleManager;
    this.userManager = userManager;

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
    long pmu_permission = Long.parseLong((String) this.getSession().get(APConstants.CRP_PMU_ROLE));
    role_pmue = roleManager.getRoleById(pmu_permission);
    programManagmentTeam = new ArrayList<User>();
    for (UserRole userRole : role_pmue.getUserRoles()) {
      programManagmentTeam.add(userRole.getUser());
    }
    String params[] = {loggedCrp.getAcronym()};
    this.setBasePermission(this.getText(Permission.CRP_ADMIN_BASE_PERMISSION, params));
    if (this.isHttpPost()) {
      programManagmentTeam.clear();
    }
  }


  @Override
  public String save() {
    if (this.hasPermission("*")) {
      /*
       * Load all bd infomartion
       */
      for (int i = 0; i < programManagmentTeam.size(); i++) {
        programManagmentTeam.set(i, userManager.getUser(programManagmentTeam.get(i).getId()));

      }

      /*
       * Removing users roles
       */
      for (UserRole userRole : role_pmue.getUserRoles()) {
        if (!programManagmentTeam.contains(userRole.getUser())) {
          userRoleManager.deleteUserRole(userRole.getId());
        }
      }
      /*
       * Add new Users roles
       */
      for (User user : programManagmentTeam) {

        UserRole userRole = new UserRole(role_pmue, user);

        if (!user.getUserRoles().contains(userRole)) {
          userRoleManager.saveUserRole(userRole);
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

      return INPUT;
    } else

    {
      return NOT_AUTHORIZED;
    }

  }

  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
  }


  public void setProgramManagmentTeam(List<User> programManagmentTeam) {
    this.programManagmentTeam = programManagmentTeam;
  }

}
