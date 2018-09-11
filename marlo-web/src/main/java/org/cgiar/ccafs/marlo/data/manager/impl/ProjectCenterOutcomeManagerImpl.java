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


import org.cgiar.ccafs.marlo.data.dao.ProjectCenterOutcomeDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectCenterOutcomeManager;
import org.cgiar.ccafs.marlo.data.model.ProjectCenterOutcome;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectCenterOutcomeManagerImpl implements ProjectCenterOutcomeManager {


  private ProjectCenterOutcomeDAO projectCenterOutcomeDAO;
  // Managers


  @Inject
  public ProjectCenterOutcomeManagerImpl(ProjectCenterOutcomeDAO projectCenterOutcomeDAO) {
    this.projectCenterOutcomeDAO = projectCenterOutcomeDAO;


  }

  @Override
  public void deleteProjectCenterOutcome(long projectCenterOutcomeId) {

    projectCenterOutcomeDAO.deleteProjectCenterOutcome(projectCenterOutcomeId);
  }

  @Override
  public boolean existProjectCenterOutcome(long projectCenterOutcomeID) {

    return projectCenterOutcomeDAO.existProjectCenterOutcome(projectCenterOutcomeID);
  }

  @Override
  public List<ProjectCenterOutcome> findAll() {

    return projectCenterOutcomeDAO.findAll();

  }

  @Override
  public ProjectCenterOutcome getProjectCenterOutcomeById(long projectCenterOutcomeID) {

    return projectCenterOutcomeDAO.find(projectCenterOutcomeID);
  }

  @Override
  public ProjectCenterOutcome saveProjectCenterOutcome(ProjectCenterOutcome projectCenterOutcome) {

    return projectCenterOutcomeDAO.save(projectCenterOutcome);
  }


}
