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


import org.cgiar.ccafs.marlo.data.dao.ProjectBudgetsFlagshipDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectBudgetsFlagshipManager;
import org.cgiar.ccafs.marlo.data.model.ProjectBudgetsFlagship;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class ProjectBudgetsFlagshipManagerImpl implements ProjectBudgetsFlagshipManager {


  private ProjectBudgetsFlagshipDAO projectBudgetsFlagshipDAO;
  // Managers


  @Inject
  public ProjectBudgetsFlagshipManagerImpl(ProjectBudgetsFlagshipDAO projectBudgetsFlagshipDAO) {
    this.projectBudgetsFlagshipDAO = projectBudgetsFlagshipDAO;


  }

  @Override
  public void deleteProjectBudgetsFlagship(long projectBudgetsFlagshipId) {

    projectBudgetsFlagshipDAO.deleteProjectBudgetsFlagship(projectBudgetsFlagshipId);
  }

  @Override
  public boolean existProjectBudgetsFlagship(long projectBudgetsFlagshipID) {

    return projectBudgetsFlagshipDAO.existProjectBudgetsFlagship(projectBudgetsFlagshipID);
  }

  @Override
  public List<ProjectBudgetsFlagship> findAll() {

    return projectBudgetsFlagshipDAO.findAll();

  }

  @Override
  public ProjectBudgetsFlagship getProjectBudgetsFlagshipById(long projectBudgetsFlagshipID) {

    return projectBudgetsFlagshipDAO.find(projectBudgetsFlagshipID);
  }

  @Override
  public ProjectBudgetsFlagship saveProjectBudgetsFlagship(ProjectBudgetsFlagship projectBudgetsFlagship) {

    return projectBudgetsFlagshipDAO.save(projectBudgetsFlagship);
  }


}
