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
import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyInstitutionDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyInstitutionManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInstitution;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudyInstitutionManagerImpl implements ProjectExpectedStudyInstitutionManager {


  private ProjectExpectedStudyInstitutionDAO projectExpectedStudyInstitutionDAO;
  // Managers
  private PhaseDAO phaseDAO;


  @Inject
  public ProjectExpectedStudyInstitutionManagerImpl(
    ProjectExpectedStudyInstitutionDAO projectExpectedStudyInstitutionDAO, PhaseDAO phaseDAO) {
    this.projectExpectedStudyInstitutionDAO = projectExpectedStudyInstitutionDAO;
    this.phaseDAO = phaseDAO;
  }

  @Override
  public void deleteProjectExpectedStudyInstitution(long projectExpectedStudyInstitutionId) {

    ProjectExpectedStudyInstitution projectExpectedStudyInstitution =
      this.getProjectExpectedStudyInstitutionById(projectExpectedStudyInstitutionId);
    Phase currentPhase = projectExpectedStudyInstitution.getPhase();

    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (currentPhase.getNext() != null) {
        this.deleteProjectExpectedStudyInstitutionPhase(currentPhase.getNext(),
          projectExpectedStudyInstitution.getProjectExpectedStudy().getId(), projectExpectedStudyInstitution);
      }
    }

    // Uncomment this line to allow reporting replication to upkeep
    // if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
    // if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
    // Phase upkeepPhase = currentPhase.getNext().getNext();
    // if (upkeepPhase != null) {
    // this.deleteProjectExpectedStudyInstitutionPhase(upkeepPhase,
    // projectExpectedStudyInstitution.getProjectExpectedStudy().getId(), projectExpectedStudyInstitution);
    // }
    // }
    // }


    projectExpectedStudyInstitutionDAO.deleteProjectExpectedStudyInstitution(projectExpectedStudyInstitutionId);
  }

  public void deleteProjectExpectedStudyInstitutionPhase(Phase next, long expectedID,
    ProjectExpectedStudyInstitution projectExpectedStudyInstitution) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudyInstitution> projectExpectedStudyInstitutions =
      phase.getProjectExpectedStudyInstitutions().stream()
        .filter(c -> c.isActive() && c.getProjectExpectedStudy().getId().longValue() == expectedID
          && c.getInstitution().getId().equals(projectExpectedStudyInstitution.getInstitution().getId()))
        .collect(Collectors.toList());
    for (ProjectExpectedStudyInstitution projectExpectedStudyInstitutionDB : projectExpectedStudyInstitutions) {
      projectExpectedStudyInstitutionDAO
        .deleteProjectExpectedStudyInstitution(projectExpectedStudyInstitutionDB.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectExpectedStudyInstitutionPhase(phase.getNext(), expectedID, projectExpectedStudyInstitution);
    }
  }

  @Override
  public boolean existProjectExpectedStudyInstitution(long projectExpectedStudyInstitutionID) {

    return projectExpectedStudyInstitutionDAO.existProjectExpectedStudyInstitution(projectExpectedStudyInstitutionID);
  }

  @Override
  public List<ProjectExpectedStudyInstitution> findAll() {

    return projectExpectedStudyInstitutionDAO.findAll();

  }


  @Override
  public ProjectExpectedStudyInstitution
    getProjectExpectedStudyInstitutionById(long projectExpectedStudyInstitutionID) {

    return projectExpectedStudyInstitutionDAO.find(projectExpectedStudyInstitutionID);
  }

  public void saveExpectedStudyInstitutionPhase(Phase next, long expectedID,
    ProjectExpectedStudyInstitution projectExpectedStudyInstitution) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudyInstitution> projectExpectedStudyInstitutions =
      phase.getProjectExpectedStudyInstitutions().stream()
        .filter(c -> c.getProjectExpectedStudy().getId().longValue() == expectedID
          && c.getInstitution().getId().equals(projectExpectedStudyInstitution.getInstitution().getId()))
        .collect(Collectors.toList());

    if (projectExpectedStudyInstitutions.isEmpty()) {
      ProjectExpectedStudyInstitution projectExpectedStudyInstitutionAdd = new ProjectExpectedStudyInstitution();
      projectExpectedStudyInstitutionAdd
        .setProjectExpectedStudy(projectExpectedStudyInstitution.getProjectExpectedStudy());
      projectExpectedStudyInstitutionAdd.setPhase(phase);
      projectExpectedStudyInstitutionAdd.setInstitution(projectExpectedStudyInstitution.getInstitution());
      projectExpectedStudyInstitutionDAO.save(projectExpectedStudyInstitutionAdd);
    }


    if (phase.getNext() != null) {
      this.saveExpectedStudyInstitutionPhase(phase.getNext(), expectedID, projectExpectedStudyInstitution);
    }
  }

  @Override
  public ProjectExpectedStudyInstitution
    saveProjectExpectedStudyInstitution(ProjectExpectedStudyInstitution projectExpectedStudyInstitution) {

    ProjectExpectedStudyInstitution institution =
      projectExpectedStudyInstitutionDAO.save(projectExpectedStudyInstitution);
    Phase currentPhase = institution.getPhase();
    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (currentPhase.getNext() != null) {
        this.saveExpectedStudyInstitutionPhase(currentPhase.getNext(), institution.getProjectExpectedStudy().getId(),
          projectExpectedStudyInstitution);
      }
    }

    // Uncomment this line to allow reporting replication to upkeep
    // if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
    // if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
    // Phase upkeepPhase = currentPhase.getNext().getNext();
    // if (upkeepPhase != null) {
    // this.saveExpectedStudyInstitutionPhase(upkeepPhase, institution.getProjectExpectedStudy().getId(),
    // projectExpectedStudyInstitution);
    // }
    // }
    // }

    return institution;
  }


}
