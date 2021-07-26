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


import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyLeverOutcomeDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyLeverOutcomeManager;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyLeverOutcome;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudyLeverOutcomeManagerImpl implements ProjectExpectedStudyLeverOutcomeManager {


  private ProjectExpectedStudyLeverOutcomeDAO projectExpectedStudyLeverOutcomeDAO;
  // Managers


  @Inject
  public ProjectExpectedStudyLeverOutcomeManagerImpl(
    ProjectExpectedStudyLeverOutcomeDAO projectExpectedStudyLeverOutcomeDAO) {
    this.projectExpectedStudyLeverOutcomeDAO = projectExpectedStudyLeverOutcomeDAO;


  }

  @Override
  public void deleteProjectExpectedStudyLeverOutcome(long projectExpectedStudyLeverOutcomeId) {

    projectExpectedStudyLeverOutcomeDAO.deleteProjectExpectedStudyLeverOutcome(projectExpectedStudyLeverOutcomeId);
  }

  @Override
  public boolean existProjectExpectedStudyLeverOutcome(long projectExpectedStudyLeverOutcomeID) {

    return projectExpectedStudyLeverOutcomeDAO
      .existProjectExpectedStudyLeverOutcome(projectExpectedStudyLeverOutcomeID);
  }

  @Override
  public List<ProjectExpectedStudyLeverOutcome> findAll() {

    return projectExpectedStudyLeverOutcomeDAO.findAll();

  }

  @Override
  public List<ProjectExpectedStudyLeverOutcome> getAllStudyLeverOutcomesByStudy(Long studyId) {
    return this.projectExpectedStudyLeverOutcomeDAO.getAllStudyLeverOutcomesByStudy(studyId.longValue());
  }

  @Override
  public ProjectExpectedStudyLeverOutcome
    getProjectExpectedStudyLeverOutcomeById(long projectExpectedStudyLeverOutcomeID) {

    return projectExpectedStudyLeverOutcomeDAO.find(projectExpectedStudyLeverOutcomeID);
  }

  @Override
  public ProjectExpectedStudyLeverOutcome
    saveProjectExpectedStudyLeverOutcome(ProjectExpectedStudyLeverOutcome projectExpectedStudyLeverOutcome) {

    return projectExpectedStudyLeverOutcomeDAO.save(projectExpectedStudyLeverOutcome);
  }
}
