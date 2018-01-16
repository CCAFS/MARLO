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


package org.cgiar.ccafs.marlo.action.crp.admin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramLeaderManager;
import org.cgiar.ccafs.marlo.data.manager.RoleManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.manager.UserRoleManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramLeader;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Role;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.data.model.UserRole;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;

/**
 * @author Christian Garcia
 */
public class CrpLeadersAdminMagmentAction extends BaseAction {


  private static final long serialVersionUID = 4581809312156690381L;


  private Crp loggedCrp;
  private CrpManager crpManager;
  private CrpProgramLeaderManager crpProgramLeaderManager;
  private UserManager userManager;
  private UserRoleManager userRoleManager;
  private RoleManager roleManager;
  private List<CrpProgram> programs;
  private Role fplRole;
  private Role rplRole;

  @Inject
  public CrpLeadersAdminMagmentAction(APConfig config, CrpManager crpManager,
    CrpProgramLeaderManager crpProgramLeaderManager, UserManager userManager, RoleManager roleManager,
    UserRoleManager userRoleManager) {
    super(config);
    this.crpManager = crpManager;
    this.crpProgramLeaderManager = crpProgramLeaderManager;
    this.userRoleManager = userRoleManager;
    this.userManager = userManager;
    this.roleManager = roleManager;
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

    fplRole = roleManager.getRoleById(Long.parseLong((String) this.getSession().get(APConstants.CRP_FPL_ROLE)));

    if (this.getSession().containsKey(APConstants.CRP_RPL_ROLE)) {
      rplRole = roleManager.getRoleById(Long.parseLong((String) this.getSession().get(APConstants.CRP_RPL_ROLE)));
    }

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

        for (CrpProgramLeader leaderPreview : crpProgram.getCrpProgramLeaders().stream().filter(c -> c.isActive())
          .collect(Collectors.toList())) {
          if (!crpProgram.getLeaders().contains(leaderPreview)) {
            crpProgramLeaderManager.deleteCrpProgramLeader(leaderPreview.getId());
            User user = userManager.getUser(leaderPreview.getUser().getId());

            List<CrpProgramLeader> existsUserLeader = user.getCrpProgramLeaders().stream()
              .filter(u -> u.isActive() && u.getCrpProgram().getProgramType() == crpProgram.getProgramType())
              .collect(Collectors.toList());


            if (existsUserLeader == null || existsUserLeader.isEmpty()) {

              if (crpProgram.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()) {
                List<UserRole> fplUserRoles =
                  user.getUserRoles().stream().filter(ur -> ur.getRole().equals(fplRole)).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(fplUserRoles)) {
                  for (UserRole userRole : fplUserRoles) {
                    userRoleManager.deleteUserRole(userRole.getId());
                  }
                }
              } else if (crpProgram.getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue()) {
                List<UserRole> rplUserRoles =
                  user.getUserRoles().stream().filter(ur -> ur.getRole().equals(rplRole)).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(rplUserRoles)) {
                  for (UserRole userRole : rplUserRoles) {
                    userRoleManager.deleteUserRole(userRole.getId());
                  }
                }
              }
            }

          }
        }


        for (CrpProgramLeader crpProgramLeader : crpProgram.getLeaders()) {
          if (crpProgramLeader.getId() == null) {
            crpProgramLeader.setActive(true);
            crpProgramLeader.setCrpProgram(crpProgram);
            crpProgramLeader.setCreatedBy(this.getCurrentUser());
            crpProgramLeader.setModifiedBy(this.getCurrentUser());
            crpProgramLeader.setModificationJustification("");
            crpProgramLeader.setActiveSince(new Date());
            crpProgramLeaderManager.saveCrpProgramLeader(crpProgramLeader);

            User user = userManager.getUser(crpProgramLeader.getUser().getId());
            UserRole userRole = new UserRole();
            userRole.setUser(user);

            if (crpProgram.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()) {
              userRole.setRole(fplRole);
            } else if (crpProgram.getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue()) {
              userRole.setRole(rplRole);
            }

            if (!user.getUserRoles().contains(userRole)) {
              userRoleManager.saveUserRole(userRole);
            }
          }
        }

      }

      Collection<String> messages = this.getActionMessages();
      if (!messages.isEmpty()) {
        String validationMessage = messages.iterator().next();
        this.setActionMessages(null);
        this.addActionWarning(this.getText("saving.saved") + validationMessage);
      } else {
        this.addActionMessage("message:" + this.getText("saving.saved"));
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
