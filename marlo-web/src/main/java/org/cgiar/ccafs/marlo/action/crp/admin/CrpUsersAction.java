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
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPhaseManager;
import org.cgiar.ccafs.marlo.data.manager.RoleManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.manager.UserRoleManager;
import org.cgiar.ccafs.marlo.data.model.CrpClusterActivityLeader;
import org.cgiar.ccafs.marlo.data.model.CrpProgramLeader;
import org.cgiar.ccafs.marlo.data.model.CrpSitesLeader;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
import org.cgiar.ccafs.marlo.data.model.ProjectPhase;
import org.cgiar.ccafs.marlo.data.model.Role;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.data.model.UserRole;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.inject.Inject;

public class CrpUsersAction extends BaseAction {

  private static final long serialVersionUID = 4072844056573550689L;

  private Phase phase;
  private UserManager userManager;
  private ProjectManager projectManager;
  private List<Project> phasesProjects;

  private PhaseManager phaseManager;
  private RoleManager roleManager;

  private UserRoleManager userRoleManager;

  private ProjectPhaseManager projectPhaseManager;

  private List<UserRole> users;
  private List<Role> rolesCrp;


  @Inject
  public CrpUsersAction(APConfig config, UserManager userManager, ProjectManager projectManager,
    PhaseManager phaseManager, ProjectPhaseManager projectPhaseManager, RoleManager roleManager,
    UserRoleManager userRoleManager) {
    super(config);
    this.projectManager = projectManager;
    this.phaseManager = phaseManager;
    this.projectPhaseManager = projectPhaseManager;
    this.userManager = userManager;
    this.roleManager = roleManager;
    this.userRoleManager = userRoleManager;
  }


  public String getRelations(long userID, long roleID) {

    User user = userManager.getUser(userID);
    Role role = roleManager.getRoleById(roleID);
    List<Object> relations = new ArrayList<>();
    switch (role.getAcronym()) {
      case "FPL":


        for (CrpProgramLeader crpProgramsLeader : user.getCrpProgramLeaders().stream()
          .filter(c -> c.isActive()
            && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue() && !c.isManager())
          .collect(Collectors.toList())) {
          relations.add(crpProgramsLeader.getCrpProgram().getAcronym());
        }
        break;

      case "FPM":


        for (CrpProgramLeader crpProgramsLeader : user.getCrpProgramLeaders().stream()
          .filter(c -> c.isActive()
            && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue() && c.isManager())
          .collect(Collectors.toList())) {
          relations.add(crpProgramsLeader.getCrpProgram().getAcronym());
        }
        break;


      case "RPL":
        for (CrpProgramLeader crpProgramsLeader : user.getCrpProgramLeaders().stream()
          .filter(c -> c.isActive()
            && c.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue() && !c.isManager())
          .collect(Collectors.toList())) {
          relations.add(crpProgramsLeader.getCrpProgram().getAcronym());
        }
        break;

      case "RPM":
        for (CrpProgramLeader crpProgramsLeader : user.getCrpProgramLeaders().stream()
          .filter(c -> c.isActive()
            && c.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue() && c.isManager())
          .collect(Collectors.toList())) {
          relations.add(crpProgramsLeader.getCrpProgram().getAcronym());
        }
        break;

      case "ML":
      case "CP":
        for (LiaisonUser liaisonUser : user.getLiasonsUsers().stream()
          .filter(c -> c.isActive() && c.getLiaisonInstitution().isActive()).collect(Collectors.toList())) {
          relations.add(liaisonUser.getLiaisonInstitution().getAcronym());
        }
        break;
      case "PL":

        for (ProjectPartnerPerson projectPartnerPerson : user.getProjectPartnerPersons().stream()
          .filter(c -> c.isActive() && c.getProjectPartner().isActive() && c.getContactType().equalsIgnoreCase("PL")
            && phasesProjects.contains(c.getProjectPartners().getProject()))
          .collect(Collectors.toList())) {
          relations.add(projectPartnerPerson.getProjectPartner().getProject()
            .getStandardIdentifier(Project.EMAIL_SUBJECT_IDENTIFIER));
        }
        break;
      case "PC":

        for (ProjectPartnerPerson projectPartnerPerson : user.getProjectPartnerPersons().stream()
          .filter(c -> c.isActive() && c.getProjectPartner().isActive() && c.getContactType().equalsIgnoreCase("PC")
            && phasesProjects.contains(c.getProjectPartners().getProject()))
          .collect(Collectors.toList())) {
          relations.add(projectPartnerPerson.getProjectPartner().getProject()
            .getStandardIdentifier(Project.EMAIL_SUBJECT_IDENTIFIER));
        }
        break;
      case "CL":
        for (CrpClusterActivityLeader crpClusterActivityLeader : user.getCrpClusterActivityLeaders().stream()
          .filter(c -> c.isActive()).collect(Collectors.toList())) {
          relations.add(crpClusterActivityLeader.getCrpClusterOfActivity().getIdentifier());
        }
        break;

      case "SL":
        for (CrpSitesLeader crpSitesLeader : user.getCrpSitesLeaders().stream().filter(c -> c.isActive())
          .collect(Collectors.toList())) {
          relations.add(crpSitesLeader.getCrpsSiteIntegration().getLocElement().getName());
        }
        break;

    }
    if (!relations.isEmpty()) {
      return relations.toString();
    }

    return "";
  }


  public List<Role> getRolesCrp() {
    return rolesCrp;
  }


  public List<UserRole> getUsers() {
    return users;
  }


  public List<User> getUsersByRole(long roleID) {
    Set<User> usersRolesSet = new HashSet();
    List<User> usersRoles = new ArrayList<>();

    List<UserRole> userRolesBD = userRoleManager.getUserRolesByRoleId(roleID);

    for (UserRole userRole : userRolesBD) {
      if (this.users.contains(userRole)) {
        usersRolesSet.add(userRole.getUser());
      }
    }

    usersRoles.addAll(usersRolesSet);
    return usersRoles;
  }


  public String hasRelations(String acronym) {
    String ret = null;
    switch (acronym.toUpperCase()) {
      case "ADMIN":
      case "G":
      case "FM":
      case "PMU":
      case "SUPERADMIN":
      case "DM":
        ret = null;
        break;
      case "PL":
      case "PC":
        ret = "Projects";
        break;

      case "ML":
        ret = "Institutions";
        break;

      case "CP":
        ret = "Centers";
        break;
      case "FPM":
      case "FPL":
        ret = "Flagships";
        break;

      case "RPM":
      case "RPL":
        ret = "Regions";
        break;

      case "CL":
        ret = "Cluster of Activities";
        break;
      case "SL":
        ret = "Site Integrations";
        break;
    }
    return ret;

  }

  @Override
  public void prepare() throws Exception {

    phase = phaseManager.findCycle(this.getCurrentCycle(), this.getCurrentCycleYear(), this.getCrpID());
    phasesProjects = new ArrayList<Project>();
    for (ProjectPhase projectPhase : phase.getProjectPhases()) {
      phasesProjects.add(projectManager.getProjectById(projectPhase.getProject().getId()));
    }
    users = new ArrayList<UserRole>();

    this.rolesCrp = roleManager
      .findAll().stream().filter(c -> !c.getUserRoles().isEmpty()
        && c.getCrp().getId().longValue() == this.getCrpID().longValue() && c.getId().longValue() != 17)
      .collect(Collectors.toList());
    rolesCrp.sort((p1, p2) -> p1.getOrder().compareTo(p2.getOrder()));
    for (Role role : rolesCrp) {
      if (role.getAcronym().equals("PL") || role.getAcronym().equals("PC")) {

        for (UserRole userRole : role.getUserRoles()) {
          User user = userRole.getUser();


          for (ProjectPartnerPerson projectPartnerPerson : user.getProjectPartnerPersons().stream()
            .filter(
              c -> c.getContactType().equals(role.getAcronym()) && c.getProjectPartner().isActive() && c.isActive())
            .collect(Collectors.toList())) {
            if (phasesProjects.contains(projectPartnerPerson.getProjectPartner().getProject())) {
              users.add(userRole);
            }
          }
        }
      } else {
        for (UserRole userRole : role.getUserRoles()) {
          users.add(userRole);
        }
      }

    }
    Set<UserRole> userSet = new HashSet<>();

    userSet.addAll(users);
    users.clear();
    users.addAll(userSet);
  }


  public void setRolesCrp(List<Role> rolesCrp) {
    this.rolesCrp = rolesCrp;
  }


  public void setUsers(List<UserRole> users) {
    this.users = users;
  }

}
