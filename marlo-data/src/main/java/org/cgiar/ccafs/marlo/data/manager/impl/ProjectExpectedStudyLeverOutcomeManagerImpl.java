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
import org.cgiar.ccafs.marlo.data.model.AllianceLeverOutcome;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
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
  public ProjectExpectedStudyLeverOutcome getStudyLeverOutcomeByStudyLeverOutcomeAndPhase(ProjectExpectedStudy study,
    AllianceLeverOutcome leverOutcome, Phase phase) {
    if (study != null && leverOutcome != null && phase != null) {
      return this.projectExpectedStudyLeverOutcomeDAO.getStudyLeverOutcomeByStudyLeverOutcomeAndPhase(study.getId(),
        leverOutcome.getId(), phase.getId());
    }

    return null;
  }

  @Override
  public void replicate(ProjectExpectedStudyLeverOutcome originalProjectExpectedStudyLeverOutcome, Phase initialPhase) {
    Phase current = initialPhase;

    while (current != null && originalProjectExpectedStudyLeverOutcome != null
      && originalProjectExpectedStudyLeverOutcome.getProjectExpectedStudy() != null
      && originalProjectExpectedStudyLeverOutcome.getLeverOutcome() != null
      && originalProjectExpectedStudyLeverOutcome.getPhase() != null) {
      ProjectExpectedStudyLeverOutcome studyLeverOutcome = this.getStudyLeverOutcomeByStudyLeverOutcomeAndPhase(
        originalProjectExpectedStudyLeverOutcome.getProjectExpectedStudy(),
        originalProjectExpectedStudyLeverOutcome.getLeverOutcome(), current);
      if (studyLeverOutcome == null) {
        studyLeverOutcome = new ProjectExpectedStudyLeverOutcome();
      }

      studyLeverOutcome.copyFields(originalProjectExpectedStudyLeverOutcome);
      studyLeverOutcome.setPhase(current);

      studyLeverOutcome = this.projectExpectedStudyLeverOutcomeDAO.save(studyLeverOutcome);

      // LOG.debug(current.toString());
      current = current.getNext();
    }
  }

  @Override
  public ProjectExpectedStudyLeverOutcome
    saveProjectExpectedStudyLeverOutcome(ProjectExpectedStudyLeverOutcome projectExpectedStudyLeverOutcome) {

    return projectExpectedStudyLeverOutcomeDAO.save(projectExpectedStudyLeverOutcome);
  }
}
