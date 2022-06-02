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
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.data.dao.GlobalUnitDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectDAO;
import org.cgiar.ccafs.marlo.data.dao.RoleDAO;
import org.cgiar.ccafs.marlo.data.dao.UserRoleDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.Role;
import org.cgiar.ccafs.marlo.data.model.UserRole;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian Garcia
 */
@Named
public class ProjectManagerImpl implements ProjectManager {


  private ProjectDAO projectDAO;
  private UserRoleDAO userRoleDAO;
  private RoleDAO roleDAO;
  private GlobalUnitDAO globalUnitDAO;
  // Managers


  @Inject
  public ProjectManagerImpl(ProjectDAO projectDAO, UserRoleDAO userRoleDAO, RoleDAO roleDAO,
    GlobalUnitDAO globalUnitDAO) {
    this.projectDAO = projectDAO;
    this.userRoleDAO = userRoleDAO;
    this.roleDAO = roleDAO;
    this.globalUnitDAO = globalUnitDAO;
  }

  @Override
  public void deleteProject(Project project) {

    projectDAO.deleteProject(project);
  }

  @Override
  public boolean existProject(long projectID) {

    return projectDAO.existProject(projectID);
  }

  @Override
  public List<Project> findAll() {

    return projectDAO.findAll();

  }

  @Override
  public List<Project> getActiveProjectsByPhase(Phase phase, int year, String[] projectStatuses) {
    return projectDAO.getActiveProjectsByPhase(phase, year, projectStatuses);
  }

  @Override
  public List<Project> getCompletedProjects(long crpId, long idPhase) {
    return projectDAO.getCompletedProjects(crpId, idPhase);
  }

  @Override
  public List<Project> getNoPhaseProjects(long crpId, Phase phase) {
    return projectDAO.getNoPhaseProjects(crpId, phase);
  }

  @Override
  public List<CrpProgram> getPrograms(long projectID, int type, long idPhase) {
    return projectDAO.getPrograms(projectID, type, idPhase);
  }

  @Override
  public Project getProjectById(long projectID) {
    return projectDAO.find(projectID);
  }

  @Override
  public List<Project> getProjectWebPageList(Long globalunit_id) {

    return projectDAO.getProjectWebPageList(globalunit_id);
  }


  @Override
  public List<Project> getUserProjects(long userId, String crp) {
    List<Project> projects = new ArrayList<>();

    List<Map<String, Object>> view = projectDAO.getUserProjects(userId, crp);

    if (view != null) {
      for (Map<String, Object> map : view) {
        projects.add(this.getProjectById((Long.parseLong(map.get("project_id").toString()))));
      }
    }

    try {
      List<UserRole> userRoles = userRoleDAO.getUserRolesByUserId(userId);
      List<Role> roles = new ArrayList<>();
      List<Project> projectsTemp = new ArrayList<>();
      if (userRoles != null) {
        for (UserRole userRole : userRoles) {
          if (userRole != null && userRole.getRole() != null) {
            if (roles == null || (roles != null && !roles.contains(userRole.getRole()))) {
              roles.add(userRole.getRole());
            }
          }
        }
        if (roles != null) {
          GlobalUnit globalUnit = globalUnitDAO.findGlobalUnitByAcronym(crp);
          Role roleFPM = roleDAO.findAll().stream()
            .filter(r -> r != null && r.getCrp() != null && globalUnit != null
              && r.getCrp().getId().equals(globalUnit.getId()) && r.getAcronym().equals("FPM"))
            .collect(Collectors.toList()).get(0);
          Role roleFPL = roleDAO.findAll().stream()
            .filter(r -> r != null && r.getCrp() != null && globalUnit != null
              && r.getCrp().getId().equals(globalUnit.getId()) && r.getAcronym().equals("FPL"))
            .collect(Collectors.toList()).get(0);

          if (roles.contains(roleFPM) || roles.contains(roleFPL)) {
            projectsTemp =
              projectDAO.findAll().stream().filter(p -> p != null && p.isActive()).collect(Collectors.toList());

            if (projectsTemp != null) {
              for (Project project : projectsTemp) {
                if (projects == null || (projects != null && !projects.contains(project))) {
                  projects.add(project);
                }
              }
            }
          }

        }
      }
    } catch (Exception e) {

    }

    return projects;
  }

  @Override
  public Project saveProject(Project project) {

    return projectDAO.save(project);
  }

  @Override
  public Project saveProject(Project project, String sectionName, List<String> relationsName) {

    return projectDAO.save(project, sectionName, relationsName);
  }

  @Override
  public Project saveProject(Project project, String sectionName, List<String> relationsName, Phase phase) {

    return projectDAO.save(project, sectionName, relationsName, phase);
  }

}
