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
import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyFundingSourceDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyFundingSourceManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyFundingSource;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudyFundingSourceManagerImpl implements ProjectExpectedStudyFundingSourceManager {


  private ProjectExpectedStudyFundingSourceDAO projectExpectedStudyFundingSourceDAO;
  // Managers
  private PhaseDAO phaseDAO;


  @Inject
  public ProjectExpectedStudyFundingSourceManagerImpl(
    ProjectExpectedStudyFundingSourceDAO projectExpectedStudyFundingSourceDAO, PhaseDAO phaseDAO) {
    this.projectExpectedStudyFundingSourceDAO = projectExpectedStudyFundingSourceDAO;
    this.phaseDAO = phaseDAO;


  }

  @Override
  public void deleteProjectExpectedStudyFundingSource(long projectExpectedStudyFundingSourceId) {

    ProjectExpectedStudyFundingSource projectExpectedStudyFundingSource =
      this.getProjectExpectedStudyFundingSourceById(projectExpectedStudyFundingSourceId);
    Phase currentPhase = projectExpectedStudyFundingSource.getPhase();

    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (currentPhase.getNext() != null) {
        this.deleteProjectExpectedStudyFundingSourcePhase(currentPhase.getNext(),
          projectExpectedStudyFundingSource.getProjectExpectedStudy().getId(), projectExpectedStudyFundingSource);
      }
    }

    if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.deleteProjectExpectedStudyFundingSourcePhase(upkeepPhase,
            projectExpectedStudyFundingSource.getProjectExpectedStudy().getId(), projectExpectedStudyFundingSource);
        }
      }
    }

    projectExpectedStudyFundingSourceDAO.deleteProjectExpectedStudyFundingSource(projectExpectedStudyFundingSourceId);
  }

  public void deleteProjectExpectedStudyFundingSourcePhase(Phase next, long expectedID,
    ProjectExpectedStudyFundingSource projectExpectedStudyFundingSource) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudyFundingSource> projectExpectedStudyFundingSources =
      phase.getProjectExpectedStudyFundingSources().stream()
        .filter(c -> c.isActive() && c.getProjectExpectedStudy().getId().longValue() == expectedID
          && c.getFundingSource().getId().equals(projectExpectedStudyFundingSource.getFundingSource().getId()))
        .collect(Collectors.toList());
    for (ProjectExpectedStudyFundingSource projectExpectedStudyFundingSourceDB : projectExpectedStudyFundingSources) {
      projectExpectedStudyFundingSourceDAO
        .deleteProjectExpectedStudyFundingSource(projectExpectedStudyFundingSourceDB.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectExpectedStudyFundingSourcePhase(phase.getNext(), expectedID, projectExpectedStudyFundingSource);
    }
  }

  @Override
  public boolean existProjectExpectedStudyFundingSource(long projectExpectedStudyFundingSourceID) {

    return projectExpectedStudyFundingSourceDAO
      .existProjectExpectedStudyFundingSource(projectExpectedStudyFundingSourceID);
  }

  @Override
  public List<ProjectExpectedStudyFundingSource> findAll() {

    return projectExpectedStudyFundingSourceDAO.findAll();

  }

  @Override
  public List<ProjectExpectedStudyFundingSource> getAllStudyFundingSourcesByStudy(Long studyId) {
    return this.projectExpectedStudyFundingSourceDAO.getAllStudyFundingSourcesByStudy(studyId.longValue());
  }

  @Override
  public ProjectExpectedStudyFundingSource
    getProjectExpectedStudyFundingSourceById(long projectExpectedStudyFundingSourceID) {

    return projectExpectedStudyFundingSourceDAO.find(projectExpectedStudyFundingSourceID);
  }

  @Override
  public ProjectExpectedStudyFundingSource getProjectExpectedStudyFundingSourceByPhase(Long expectedID,
    Long fundingSourceID, Long phaseID) {

    return projectExpectedStudyFundingSourceDAO.getProjectExpectedStudyFundingSourceByPhase(expectedID, fundingSourceID,
      phaseID);
  }

  public void saveExpectedStudyFundingSourcePhase(Phase next, long expectedID,
    ProjectExpectedStudyFundingSource projectExpectedStudyFundingSource) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudyFundingSource> projectExpectedStudyFundingSources =
      phase.getProjectExpectedStudyFundingSources().stream()
        .filter(c -> c.getProjectExpectedStudy().getId().longValue() == expectedID
          && c.getFundingSource().getId().equals(projectExpectedStudyFundingSource.getFundingSource().getId()))
        .collect(Collectors.toList());

    if (projectExpectedStudyFundingSources.isEmpty()) {
      ProjectExpectedStudyFundingSource projectExpectedStudyFundingSourceAdd = new ProjectExpectedStudyFundingSource();
      projectExpectedStudyFundingSourceAdd
        .setProjectExpectedStudy(projectExpectedStudyFundingSource.getProjectExpectedStudy());
      projectExpectedStudyFundingSourceAdd.setPhase(phase);
      projectExpectedStudyFundingSourceAdd.setFundingSource(projectExpectedStudyFundingSource.getFundingSource());
      projectExpectedStudyFundingSourceDAO.save(projectExpectedStudyFundingSourceAdd);
    }


    if (phase.getNext() != null) {
      this.saveExpectedStudyFundingSourcePhase(phase.getNext(), expectedID, projectExpectedStudyFundingSource);
    }
  }

  @Override
  public ProjectExpectedStudyFundingSource
    saveProjectExpectedStudyFundingSource(ProjectExpectedStudyFundingSource projectExpectedStudyFundingSource) {

    ProjectExpectedStudyFundingSource projectExpectedStudyFundingSourceResult =
      projectExpectedStudyFundingSourceDAO.save(projectExpectedStudyFundingSource);
    Phase currentPhase = projectExpectedStudyFundingSourceResult.getPhase();

    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (currentPhase.getNext() != null) {
        this.saveExpectedStudyFundingSourcePhase(currentPhase.getNext(),
          projectExpectedStudyFundingSourceResult.getProjectExpectedStudy().getId(), projectExpectedStudyFundingSource);
      }
    }

    if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveExpectedStudyFundingSourcePhase(upkeepPhase,
            projectExpectedStudyFundingSourceResult.getProjectExpectedStudy().getId(),
            projectExpectedStudyFundingSource);
        }
      }
    }

    return projectExpectedStudyFundingSourceResult;
  }
}
