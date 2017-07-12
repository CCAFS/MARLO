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


import org.cgiar.ccafs.marlo.data.dao.ProjectBudgetsCluserActvityDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectBudgetsCluserActvityManager;
import org.cgiar.ccafs.marlo.data.model.ProjectBudgetsCluserActvity;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class ProjectBudgetsCluserActvityManagerImpl implements ProjectBudgetsCluserActvityManager {


  private ProjectBudgetsCluserActvityDAO projectBudgetsCluserActvityDAO;
  // Managers


  @Inject
  public ProjectBudgetsCluserActvityManagerImpl(ProjectBudgetsCluserActvityDAO projectBudgetsCluserActvityDAO) {
    this.projectBudgetsCluserActvityDAO = projectBudgetsCluserActvityDAO;


  }

  @Override
  public boolean deleteProjectBudgetsCluserActvity(long projectBudgetsCluserActvityId) {

    return projectBudgetsCluserActvityDAO.deleteProjectBudgetsCluserActvity(projectBudgetsCluserActvityId);
  }

  @Override
  public boolean existProjectBudgetsCluserActvity(long projectBudgetsCluserActvityID) {

    return projectBudgetsCluserActvityDAO.existProjectBudgetsCluserActvity(projectBudgetsCluserActvityID);
  }

  @Override
  public List<ProjectBudgetsCluserActvity> findAll() {

    return projectBudgetsCluserActvityDAO.findAll();

  }

  @Override
  public ProjectBudgetsCluserActvity getProjectBudgetsCluserActvityById(long projectBudgetsCluserActvityID) {

    return projectBudgetsCluserActvityDAO.find(projectBudgetsCluserActvityID);
  }

  @Override
  public long saveProjectBudgetsCluserActvity(ProjectBudgetsCluserActvity projectBudgetsCluserActvity) {

    return projectBudgetsCluserActvityDAO.save(projectBudgetsCluserActvity);
  }


}
