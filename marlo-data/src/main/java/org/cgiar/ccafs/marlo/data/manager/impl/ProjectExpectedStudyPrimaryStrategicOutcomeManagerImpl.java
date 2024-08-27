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


import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyPrimaryStrategicOutcomeDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyPrimaryStrategicOutcomeManager;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPrimaryStrategicOutcome;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudyPrimaryStrategicOutcomeManagerImpl implements ProjectExpectedStudyPrimaryStrategicOutcomeManager {


  private ProjectExpectedStudyPrimaryStrategicOutcomeDAO projectExpectedStudyPrimaryStrategicOutcomeDAO;
  // Managers


  @Inject
  public ProjectExpectedStudyPrimaryStrategicOutcomeManagerImpl(ProjectExpectedStudyPrimaryStrategicOutcomeDAO projectExpectedStudyPrimaryStrategicOutcomeDAO) {
    this.projectExpectedStudyPrimaryStrategicOutcomeDAO = projectExpectedStudyPrimaryStrategicOutcomeDAO;


  }

  @Override
  public void deleteProjectExpectedStudyPrimaryStrategicOutcome(long projectExpectedStudyPrimaryStrategicOutcomeId) {

    projectExpectedStudyPrimaryStrategicOutcomeDAO.deleteProjectExpectedStudyPrimaryStrategicOutcome(projectExpectedStudyPrimaryStrategicOutcomeId);
  }

  @Override
  public boolean existProjectExpectedStudyPrimaryStrategicOutcome(long projectExpectedStudyPrimaryStrategicOutcomeID) {

    return projectExpectedStudyPrimaryStrategicOutcomeDAO.existProjectExpectedStudyPrimaryStrategicOutcome(projectExpectedStudyPrimaryStrategicOutcomeID);
  }

  @Override
  public List<ProjectExpectedStudyPrimaryStrategicOutcome> findAll() {

    return projectExpectedStudyPrimaryStrategicOutcomeDAO.findAll();

  }

  @Override
  public ProjectExpectedStudyPrimaryStrategicOutcome getProjectExpectedStudyPrimaryStrategicOutcomeById(long projectExpectedStudyPrimaryStrategicOutcomeID) {

    return projectExpectedStudyPrimaryStrategicOutcomeDAO.find(projectExpectedStudyPrimaryStrategicOutcomeID);
  }

  @Override
  public ProjectExpectedStudyPrimaryStrategicOutcome saveProjectExpectedStudyPrimaryStrategicOutcome(ProjectExpectedStudyPrimaryStrategicOutcome projectExpectedStudyPrimaryStrategicOutcome) {

    return projectExpectedStudyPrimaryStrategicOutcomeDAO.save(projectExpectedStudyPrimaryStrategicOutcome);
  }


}
