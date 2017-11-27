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


import org.cgiar.ccafs.marlo.data.dao.ProjectScopeDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectScopeManager;
import org.cgiar.ccafs.marlo.data.model.ProjectScope;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class ProjectScopeManagerImpl implements ProjectScopeManager {


  private ProjectScopeDAO projectScopeDAO;
  // Managers


  @Inject
  public ProjectScopeManagerImpl(ProjectScopeDAO projectScopeDAO) {
    this.projectScopeDAO = projectScopeDAO;


  }

  @Override
  public void deleteProjectScope(long projectScopeId) {

    projectScopeDAO.deleteProjectScope(projectScopeId);
  }

  @Override
  public boolean existProjectScope(long projectScopeID) {

    return projectScopeDAO.existProjectScope(projectScopeID);
  }

  @Override
  public List<ProjectScope> findAll() {

    return projectScopeDAO.findAll();

  }

  @Override
  public ProjectScope getProjectScopeById(long projectScopeID) {

    return projectScopeDAO.find(projectScopeID);
  }

  @Override
  public ProjectScope saveProjectScope(ProjectScope projectScope) {

    return projectScopeDAO.save(projectScope);
  }


}
