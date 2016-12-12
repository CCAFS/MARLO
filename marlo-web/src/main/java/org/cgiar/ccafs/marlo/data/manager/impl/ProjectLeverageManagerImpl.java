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


import org.cgiar.ccafs.marlo.data.dao.ProjectLeverageDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectLeverageManager;
import org.cgiar.ccafs.marlo.data.model.ProjectLeverage;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class ProjectLeverageManagerImpl implements ProjectLeverageManager {


  private ProjectLeverageDAO projectLeverageDAO;
  // Managers


  @Inject
  public ProjectLeverageManagerImpl(ProjectLeverageDAO projectLeverageDAO) {
    this.projectLeverageDAO = projectLeverageDAO;


  }

  @Override
  public boolean deleteProjectLeverage(long projectLeverageId) {

    return projectLeverageDAO.deleteProjectLeverage(projectLeverageId);
  }

  @Override
  public boolean existProjectLeverage(long projectLeverageID) {

    return projectLeverageDAO.existProjectLeverage(projectLeverageID);
  }

  @Override
  public List<ProjectLeverage> findAll() {

    return projectLeverageDAO.findAll();

  }

  @Override
  public ProjectLeverage getProjectLeverageById(long projectLeverageID) {

    return projectLeverageDAO.find(projectLeverageID);
  }

  @Override
  public long saveProjectLeverage(ProjectLeverage projectLeverage) {

    return projectLeverageDAO.save(projectLeverage);
  }


}
