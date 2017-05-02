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
import org.cgiar.ccafs.marlo.data.model.Phase;
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


  private UserManager userManager;
  private ProjectManager projectManager;


  private PhaseManager phaseManager;
  private RoleManager roleManager;

  private ProjectPhaseManager projectPhaseManager;

  private List<User> users;


  @Inject
  public CrpUsersAction(APConfig config, UserManager userManager, ProjectManager projectManager,
    PhaseManager phaseManager, ProjectPhaseManager projectPhaseManager, RoleManager roleManager) {
    super(config);
    this.projectManager = projectManager;
    this.phaseManager = phaseManager;
    this.projectPhaseManager = projectPhaseManager;
    this.userManager = userManager;
    this.roleManager = roleManager;
  }


  public List<User> getUsers() {
    return users;
  }


  @Override
  public void prepare() throws Exception {

    Phase phase = phaseManager.findCycle(this.getCurrentCycle(), this.getCurrentCycleYear(), this.getCrpID());
    List<Project> phasesProjects = new ArrayList<Project>();
    for (ProjectPhase projectPhase : phase.getProjectPhases()) {
      phasesProjects.add(projectManager.getProjectById(projectPhase.getProject().getId()));
    }
    users = new ArrayList<User>();
    List<Role> roles = roleManager.findAll().stream()
      .filter(c -> c.getCrp().getId().longValue() == this.getCrpID().longValue()).collect(Collectors.toList());

    for (Role role : roles) {
      if (role.getAcronym().equals("PL") || role.getAcronym().equals("PC")) {

        for (UserRole userRole : role.getUserRoles()) {
          User user = userRole.getUser();
          for (ProjectPartnerPerson projectPartnerPerson : user.getProjectPartnerPersons().stream()
            .filter(c -> c.getContactType().equals(role.getAcronym()) && c.isActive()).collect(Collectors.toList())) {
            if (phasesProjects.contains(projectPartnerPerson.getProjectPartner().getProject())) {
              users.add(userManager.getUser(user.getId()));
            }
          }
        }
      } else {
        for (UserRole userRole : role.getUserRoles()) {
          users.add(userManager.getUser(userRole.getUser().getId()));
        }
      }

    }
    Set<User> userSet = new HashSet<>();

    userSet.addAll(users);
    users.clear();
    users.addAll(userSet);
  }


  public void setUsers(List<User> users) {
    this.users = users;
  }

}
