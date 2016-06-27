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


package org.cgiar.ccafs.marlo.action.impactpathway;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpClusterActivityLeaderManager;
import org.cgiar.ccafs.marlo.data.manager.CrpClusterOfActivityManager;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.RoleManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.manager.UserRoleManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CrpClusterActivityLeader;
import org.cgiar.ccafs.marlo.data.model.CrpClusterOfActivity;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Role;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.data.model.UserRole;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.validation.impactpathway.ClusterActivitiesValidator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Christian Garcia
 */
public class ClusterActivitiesAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = -2049759808815382048L;
  private RoleManager roleManager;
  private UserRoleManager userRoleManager;
  private CrpManager crpManager;
  private CrpProgramManager crpProgramManager;
  private CrpClusterOfActivityManager crpClusterOfActivityManager;
  private CrpClusterActivityLeaderManager crpClusterActivityLeaderManager;
  private UserManager userManager;
  private Crp loggedCrp;
  private Role roleCl;
  private long clRol;
  private List<CrpProgram> programs;
  private CrpProgram selectedProgram;
  private long crpProgramID;
  private List<CrpClusterOfActivity> clusterofActivities;
  private ClusterActivitiesValidator validator;

  @Inject
  public ClusterActivitiesAction(APConfig config, RoleManager roleManager, UserRoleManager userRoleManager,
    CrpManager crpManager, UserManager userManager, CrpProgramManager crpProgramManager,
    CrpClusterOfActivityManager crpClusterOfActivityManager, ClusterActivitiesValidator validator,
    CrpClusterActivityLeaderManager crpClusterActivityLeaderManager) {
    super(config);
    this.roleManager = roleManager;
    this.userRoleManager = userRoleManager;
    this.crpManager = crpManager;
    this.userManager = userManager;
    this.crpProgramManager = crpProgramManager;
    this.crpClusterOfActivityManager = crpClusterOfActivityManager;
    this.crpClusterActivityLeaderManager = crpClusterActivityLeaderManager;
    this.validator = validator;
  }

  public long getClRol() {
    return clRol;
  }

  public List<CrpClusterOfActivity> getClusterofActivities() {
    return clusterofActivities;
  }

  public long getCrpProgramID() {
    return crpProgramID;
  }


  public Crp getLoggedCrp() {
    return loggedCrp;
  }


  public List<CrpProgram> getPrograms() {
    return programs;
  }

  public Role getRoleCl() {
    return roleCl;
  }


  public CrpProgram getSelectedProgram() {
    return selectedProgram;
  }


  @Override
  public void prepare() throws Exception {

    // Get the Users list that have the pmu role in this crp.
    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());
    clRol = Long.parseLong((String) this.getSession().get(APConstants.CRP_CL_ROLE));
    roleCl = roleManager.getRoleById(clRol);

    List<CrpProgram> allPrograms = loggedCrp.getCrpPrograms().stream()
      .filter(c -> c.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue() && c.isActive())
      .collect(Collectors.toList());
    crpProgramID = -1;
    clusterofActivities = new ArrayList<>();
    if (allPrograms != null) {

      this.programs = allPrograms;

      try {
        crpProgramID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.CRP_PROGRAM_ID)));
      } catch (Exception e) {
        if (!this.programs.isEmpty()) {
          crpProgramID = this.programs.get(0).getId();
        }
      }
    } else {
      programs = new ArrayList<>();
    }

    if (crpProgramID != -1) {
      selectedProgram = crpProgramManager.getCrpProgramById(crpProgramID);
      clusterofActivities.addAll(
        selectedProgram.getCrpClusterOfActivities().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
      for (CrpClusterOfActivity crpClusterOfActivity : clusterofActivities) {

        crpClusterOfActivity.setLeaders(crpClusterOfActivity.getCrpClusterActivityLeaders().stream()
          .filter(c -> c.isActive()).collect(Collectors.toList()));
      }
    }

    String params[] = {loggedCrp.getAcronym(), selectedProgram.getId().toString()};
    this.setBasePermission(this.getText(Permission.IMPACT_PATHWAY_BASE_PERMISSION, params));
    if (this.isHttpPost()) {
      clusterofActivities.clear();
    }
  }


  @Override
  public String save() {

    if (this.hasPermission("*")) {

      /*
       * Removing outcomes
       */
      selectedProgram = crpProgramManager.getCrpProgramById(crpProgramID);
      for (CrpClusterOfActivity crpClusterOfActivity : selectedProgram.getCrpClusterOfActivities().stream()
        .filter(c -> c.isActive()).collect(Collectors.toList())) {
        if (!clusterofActivities.contains(crpClusterOfActivity)) {
          // if (crpClusterOfActivity.getCrpMilestones().isEmpty() &&
          // crpProgramOutcome.getCrpOutcomeSubIdos().isEmpty()) {
          crpClusterOfActivityManager.deleteCrpClusterOfActivity(crpClusterOfActivity.getId());
          // }

        }

      }

      /*
       * Save outcomes
       */
      for (CrpClusterOfActivity crpClusterOfActivity : clusterofActivities) {

        if (crpClusterOfActivity.getId() == null) {
          crpClusterOfActivity.setActive(true);

          crpClusterOfActivity.setCreatedBy(this.getCurrentUser());
          crpClusterOfActivity.setModifiedBy(this.getCurrentUser());
          crpClusterOfActivity.setModificationJustification("");
          crpClusterOfActivity.setActiveSince(new Date());

        } else {
          CrpClusterOfActivity db =
            crpClusterOfActivityManager.getCrpClusterOfActivityById(crpClusterOfActivity.getId());
          crpClusterOfActivity.setActive(true);
          crpClusterOfActivity.setCreatedBy(db.getCreatedBy());
          crpClusterOfActivity.setModifiedBy(this.getCurrentUser());
          crpClusterOfActivity.setModificationJustification("");
          crpClusterOfActivity.setActiveSince(db.getActiveSince());
        }
        crpClusterOfActivity.setCrpProgram(selectedProgram);
        crpClusterOfActivityManager.saveCrpClusterOfActivity(crpClusterOfActivity);

        /*
         * Check leaders
         */
        CrpClusterOfActivity crpClusterPrev =
          crpClusterOfActivityManager.getCrpClusterOfActivityById(crpClusterOfActivity.getId());
        for (CrpClusterActivityLeader leaderPreview : crpClusterPrev.getCrpClusterActivityLeaders().stream()
          .filter(c -> c.isActive()).collect(Collectors.toList())) {

          if (crpClusterOfActivity.getLeaders() == null) {
            crpClusterOfActivity.setLeaders(new ArrayList<>());
          }
          if (!crpClusterOfActivity.getLeaders().contains(leaderPreview)) {
            crpClusterActivityLeaderManager.deleteCrpClusterActivityLeader(leaderPreview.getId());
            User user = userManager.getUser(leaderPreview.getUser().getId());

            List<CrpClusterActivityLeader> existsUserLeader =
              user.getCrpClusterActivityLeaders().stream().filter(u -> u.isActive()).collect(Collectors.toList());


            if (existsUserLeader == null || existsUserLeader.isEmpty()) {


              List<UserRole> clUserRoles =
                user.getUserRoles().stream().filter(ur -> ur.getRole().equals(roleCl)).collect(Collectors.toList());
              if (clUserRoles != null || !clUserRoles.isEmpty()) {
                for (UserRole userRole : clUserRoles) {
                  userRoleManager.deleteUserRole(userRole.getId());
                }
              }

            }

          }
        }
        /*
         * Save leaders
         */

        if (crpClusterOfActivity.getLeaders() != null) {
          for (CrpClusterActivityLeader crpClusterActivityLeader : crpClusterOfActivity.getLeaders()) {
            if (crpClusterActivityLeader.getId() == null) {
              crpClusterActivityLeader.setActive(true);
              crpClusterActivityLeader.setCrpClusterOfActivity(crpClusterOfActivity);
              crpClusterActivityLeader.setCreatedBy(this.getCurrentUser());
              crpClusterActivityLeader.setModifiedBy(this.getCurrentUser());
              crpClusterActivityLeader.setModificationJustification("");
              crpClusterActivityLeader.setActiveSince(new Date());
              CrpClusterOfActivity crpClusterPreview =
                crpClusterOfActivityManager.getCrpClusterOfActivityById(crpClusterOfActivity.getId());
              if (crpClusterPreview.getCrpClusterActivityLeaders().stream()
                .filter(c -> c.isActive() && c.getUser().equals(crpClusterActivityLeader.getUser()))
                .collect(Collectors.toList()).isEmpty()) {
                crpClusterActivityLeaderManager.saveCrpClusterActivityLeader(crpClusterActivityLeader);
              }

              User user = userManager.getUser(crpClusterActivityLeader.getUser().getId());
              UserRole userRole = new UserRole();
              userRole.setUser(user);
              userRole.setRole(this.roleCl);
              if (!user.getUserRoles().contains(userRole)) {
                userRoleManager.saveUserRole(userRole);
              }
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
        this.addActionMessage(this.getText("saving.saved"));
      }
      messages = this.getActionMessages();
      return SUCCESS;
    } else {
      return NOT_AUTHORIZED;
    }


  }


  public void setClRol(long clRol) {
    this.clRol = clRol;
  }

  public void setClusterofActivities(List<CrpClusterOfActivity> clusterofActivities) {
    this.clusterofActivities = clusterofActivities;
  }


  public void setCrpProgramID(long crpProgramID) {
    this.crpProgramID = crpProgramID;
  }


  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
  }


  public void setPrograms(List<CrpProgram> programs) {
    this.programs = programs;
  }


  public void setRoleCl(Role roleCl) {
    this.roleCl = roleCl;
  }


  public void setSelectedProgram(CrpProgram selectedProgram) {
    this.selectedProgram = selectedProgram;
  }


  @Override
  public void validate() {
    if (save) {
      validator.validate(this, clusterofActivities);
    }
  }


}
