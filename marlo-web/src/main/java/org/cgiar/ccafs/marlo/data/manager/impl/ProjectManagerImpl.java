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


import org.cgiar.ccafs.marlo.data.dao.ProjectDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class ProjectManagerImpl implements ProjectManager {


  private ProjectDAO projectDAO;
  // Managers


  @Inject
  public ProjectManagerImpl(ProjectDAO projectDAO) {
    this.projectDAO = projectDAO;


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

  public List<Project> getCompletedProjects(long crpId) {
    return projectDAO.getCompletedProjects(crpId);
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
  public List<Project> getUserProjects(long userId, String crp) {

    List<Project> projects = new ArrayList<>();

    List<Map<String, Object>> view = projectDAO.getUserProjects(userId, crp);

    if (view != null) {
      for (Map<String, Object> map : view) {
        projects.add(this.getProjectById((Long.parseLong(map.get("project_id").toString()))));
      }
    }


    return projects;
  }

  @Override
  public List<Project> getUserProjectsReporting(long userId, String crp) {

    List<Project> projects = new ArrayList<>();

    List<Map<String, Object>> view = projectDAO.getUserProjectsReporting(userId, crp);

    if (view != null) {
      for (Map<String, Object> map : view) {
        projects.add(this.getProjectById((Long.parseLong(map.get("project_id").toString()))));
      }
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
