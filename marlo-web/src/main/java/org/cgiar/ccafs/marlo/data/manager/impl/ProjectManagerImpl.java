/*****************************************************************
 * This file is part of Managing Agricultural Research for Learning & 
 * Outcomes Platform (MARLO). * MARLO is free software: you can redistribute it and/or modify
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
import org.cgiar.ccafs.marlo.data.model.Project;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class ProjectManagerImpl implements ProjectManager {


  private ProjectDAO projectDAO;
  // Managers


  @Inject
  public ProjectManagerImpl(ProjectDAO projectDAO) {
    this.projectDAO = projectDAO;


  }

  @Override
  public boolean deleteProject(long projectId) {

    return projectDAO.deleteProject(projectId);
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
  public Project getProjectById(long projectID) {

    return projectDAO.find(projectID);
  }

  @Override
  public long saveProject(Project project) {

    return projectDAO.save(project);
  }


}
