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


import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyRelatedLeverSdgContributionDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyRelatedLeverSdgContributionManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyRelatedLeverSdgContribution;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudyRelatedLeverSdgContributionManagerImpl
  implements ProjectExpectedStudyRelatedLeverSdgContributionManager {


  private ProjectExpectedStudyRelatedLeverSdgContributionDAO projectExpectedStudyRelatedLeverSDGContributionDAO;
  private PhaseDAO phaseDAO;
  // Managers


  @Inject
  public ProjectExpectedStudyRelatedLeverSdgContributionManagerImpl(
    ProjectExpectedStudyRelatedLeverSdgContributionDAO projectExpectedStudyRelatedLeverSDGContributionDAO,
    PhaseDAO phaseDAO) {
    this.projectExpectedStudyRelatedLeverSDGContributionDAO = projectExpectedStudyRelatedLeverSDGContributionDAO;
    this.phaseDAO = phaseDAO;

  }

  @Override
  public void
    deleteProjectExpectedStudyRelatedLeverSdgContribution(long projectExpectedStudyRelatedLeverSDGContributionId) {

    ProjectExpectedStudyRelatedLeverSdgContribution projectExpectedStudyRelatedLeverSdgContribution =
      this.getProjectExpectedStudyRelatedLeverSdgContributionById(projectExpectedStudyRelatedLeverSDGContributionId);
    Phase currentPhase = projectExpectedStudyRelatedLeverSdgContribution.getPhase();

    if (currentPhase.getDescription().equals(APConstants.PLANNING) && currentPhase.getNext() != null) {
      this.deleteProjectExpectedStudyRelatedLeverSdgContributionPhase(currentPhase.getNext(),
        projectExpectedStudyRelatedLeverSdgContribution);
    }

    if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.deleteProjectExpectedStudyRelatedLeverSdgContributionPhase(upkeepPhase.getNext(),
            projectExpectedStudyRelatedLeverSdgContribution);
        }
      }
    }

    projectExpectedStudyRelatedLeverSDGContributionDAO
      .deleteProjectExpectedStudyRelatedLeverSdgContribution(projectExpectedStudyRelatedLeverSDGContributionId);
  }

  public void deleteProjectExpectedStudyRelatedLeverSdgContributionPhase(Phase next,
    ProjectExpectedStudyRelatedLeverSdgContribution projectExpectedStudyRelatedLeverSdgContribution) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudyRelatedLeverSdgContribution> projectExpectedStudyRelatedLeverSdgContributionList =
      phase.getProjectExpectedStudyRelatedLeverSdgContribution().stream()
        .filter(c -> c.isActive()
          && c.getProjectExpectedStudy().getId() == projectExpectedStudyRelatedLeverSdgContribution
            .getProjectExpectedStudy().getId()
          && c.getRelatedLeverRelatedLeversSDGContribution().getId() == projectExpectedStudyRelatedLeverSdgContribution
            .getRelatedLeverRelatedLeversSDGContribution().getId())
        .collect(Collectors.toList());
    for (ProjectExpectedStudyRelatedLeverSdgContribution projectExpectedStudyRelatedLeverSdgContributionTmp : projectExpectedStudyRelatedLeverSdgContributionList) {
      projectExpectedStudyRelatedLeverSDGContributionDAO.deleteProjectExpectedStudyRelatedLeverSdgContribution(
        projectExpectedStudyRelatedLeverSdgContributionTmp.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectExpectedStudyRelatedLeverSdgContributionPhase(phase.getNext(),
        projectExpectedStudyRelatedLeverSdgContribution);
    }
  }

  @Override
  public boolean
    existProjectExpectedStudyRelatedLeverSdgContribution(long projectExpectedStudyRelatedLeverSDGContributionID) {

    return projectExpectedStudyRelatedLeverSDGContributionDAO
      .existProjectExpectedStudyRelatedLeverSdgContribution(projectExpectedStudyRelatedLeverSDGContributionID);
  }

  @Override
  public List<ProjectExpectedStudyRelatedLeverSdgContribution> findAll() {

    return projectExpectedStudyRelatedLeverSDGContributionDAO.findAll();

  }

  @Override
  public ProjectExpectedStudyRelatedLeverSdgContribution
    getProjectExpectedStudyRelatedLeverSdgContributionById(long projectExpectedStudyRelatedLeverSDGContributionID) {

    return projectExpectedStudyRelatedLeverSDGContributionDAO.find(projectExpectedStudyRelatedLeverSDGContributionID);
  }

  /**
   * Reply the information to the next Phases
   * 
   * @param next - The next Phase
   * @param projectExpectedStudyRelatedLeverSDGContribution - The project expected study related level sdg into the
   *        database.
   */
  public void saveInfoPhase(Phase next,
    ProjectExpectedStudyRelatedLeverSdgContribution projectExpectedStudyRelatedLeverSDGContribution) {

    Phase phase = phaseDAO.find(next.getId());
    List<ProjectExpectedStudyRelatedLeverSdgContribution> projectExpectedStudyRelatedLeverSdgContributionList =
      phase.getProjectExpectedStudyRelatedLeverSdgContribution().stream()
        .filter(c -> c.getProjectExpectedStudy().getId().longValue() == projectExpectedStudyRelatedLeverSDGContribution
          .getProjectExpectedStudy().getId()
          && c.getRelatedLeverRelatedLeversSDGContribution().getId() == projectExpectedStudyRelatedLeverSDGContribution
            .getRelatedLeverRelatedLeversSDGContribution().getId())
        .collect(Collectors.toList());
    if (projectExpectedStudyRelatedLeverSdgContributionList.isEmpty()) {

      ProjectExpectedStudyRelatedLeverSdgContribution projectExpectedStudyRelatedLeverSdgContributionAdd =
        new ProjectExpectedStudyRelatedLeverSdgContribution();

      projectExpectedStudyRelatedLeverSdgContributionAdd
        .setProjectExpectedStudy(projectExpectedStudyRelatedLeverSDGContribution.getProjectExpectedStudy());
      projectExpectedStudyRelatedLeverSdgContributionAdd.setPhase(phase);
      projectExpectedStudyRelatedLeverSdgContributionAdd.setRelatedLeverRelatedLeversSDGContribution(
        projectExpectedStudyRelatedLeverSDGContribution.getRelatedLeverRelatedLeversSDGContribution());

      projectExpectedStudyRelatedLeverSDGContributionDAO.save(projectExpectedStudyRelatedLeverSdgContributionAdd);
    }

    if (phase.getNext() != null) {
      this.saveInfoPhase(phase.getNext(), projectExpectedStudyRelatedLeverSDGContribution);
    }
  }


  @Override
  public ProjectExpectedStudyRelatedLeverSdgContribution saveProjectExpectedStudyRelatedLeverSdgContribution(
    ProjectExpectedStudyRelatedLeverSdgContribution projectExpectedStudyRelatedLeverSDGContribution) {


    ProjectExpectedStudyRelatedLeverSdgContribution sourceInfo =
      projectExpectedStudyRelatedLeverSDGContributionDAO.save(projectExpectedStudyRelatedLeverSDGContribution);
    Phase phase = phaseDAO.find(sourceInfo.getPhase().getId());


    if (phase.getDescription().equals(APConstants.PLANNING)) {
      if (phase.getNext() != null) {
        this.saveInfoPhase(phase.getNext(), projectExpectedStudyRelatedLeverSDGContribution);
      }
    }

    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (phase.getNext() != null && phase.getNext().getNext() != null) {
        Phase upkeepPhase = phase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveInfoPhase(upkeepPhase, projectExpectedStudyRelatedLeverSDGContribution);
        }
      }
    }


    return sourceInfo;


  }


}
