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


import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyProjectOutcomeDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyProjectOutcomeManager;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyProjectOutcome;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudyProjectOutcomeManagerImpl implements ProjectExpectedStudyProjectOutcomeManager {


  private ProjectExpectedStudyProjectOutcomeDAO projectExpectedStudyProjectOutcomeDAO;
  // Managers


  @Inject
  public ProjectExpectedStudyProjectOutcomeManagerImpl(ProjectExpectedStudyProjectOutcomeDAO projectExpectedStudyProjectOutcomeDAO) {
    this.projectExpectedStudyProjectOutcomeDAO = projectExpectedStudyProjectOutcomeDAO;


  }

  @Override
  public void deleteProjectExpectedStudyProjectOutcome(long projectExpectedStudyProjectOutcomeId) {

    projectExpectedStudyProjectOutcomeDAO.deleteProjectExpectedStudyProjectOutcome(projectExpectedStudyProjectOutcomeId);
  }

  @Override
  public boolean existProjectExpectedStudyProjectOutcome(long projectExpectedStudyProjectOutcomeID) {

    return projectExpectedStudyProjectOutcomeDAO.existProjectExpectedStudyProjectOutcome(projectExpectedStudyProjectOutcomeID);
  }

  @Override
  public List<ProjectExpectedStudyProjectOutcome> findAll() {

    return projectExpectedStudyProjectOutcomeDAO.findAll();

  }

  @Override
  public ProjectExpectedStudyProjectOutcome getProjectExpectedStudyProjectOutcomeById(long projectExpectedStudyProjectOutcomeID) {

    return projectExpectedStudyProjectOutcomeDAO.find(projectExpectedStudyProjectOutcomeID);
  }

  @Override
  public ProjectExpectedStudyProjectOutcome saveProjectExpectedStudyProjectOutcome(ProjectExpectedStudyProjectOutcome projectExpectedStudyProjectOutcome) {

    return projectExpectedStudyProjectOutcomeDAO.save(projectExpectedStudyProjectOutcome);
  }


}
