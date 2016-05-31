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
import org.cgiar.ccafs.marlo.data.model.UserRole;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.Collection;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CrpAdminManagmentAction extends BaseAction {

  private static final long serialVersionUID = 3355662668874414548L;

  private RoleManager roleManager;
  private UserRoleManager userRoleManager;
  private UserManager userManager;

  private Crp loggedCrp;
  private Role rolePmu;
  private long pmuRol;

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


  @Override
  public void prepare() throws Exception {

    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    pmuRol = Long.parseLong((String) this.getSession().get(APConstants.CRP_PMU_ROLE));
    rolePmu = roleManager.getRoleById(pmuRol);


    String params[] = {loggedCrp.getAcronym()};
    this.setBasePermission(this.getText(Permission.CRP_ADMIN_BASE_PERMISSION, params));
    if (this.isHttpPost()) {
      rolePmu.getUserRoles().clear();
    }
  }


  @Override
  public String save() {
    if (this.hasPermission("*")) {

      Role rolePreview = roleManager.getRoleById(pmuRol);
      /*
       * Removing users roles
       */
      for (UserRole userRole : rolePreview.getUserRoles()) {
        if (!rolePmu.getUserRoles().contains(userRole.getUser())) {
          userRoleManager.deleteUserRole(userRole.getId());
        }
      }
      /*
       * Add new Users roles
       */
      for (UserRole userRole : rolePmu.getUserRoles()) {

        if (userRole.getId() == null) {
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


}
