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
import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationComplementarySolutionDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationComplementarySolutionFunctionDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationComplementarySolutionManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationComplementarySolution;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author CCAFS
 */
@Named
public class ProjectInnovationComplementarySolutionManagerImpl
  implements ProjectInnovationComplementarySolutionManager {

  private PhaseDAO phaseDAO;
  private ProjectInnovationComplementarySolutionDAO projectInnovationComplementarySolutionDAO;
  private ProjectInnovationComplementarySolutionFunctionDAO projectInnovationComplementarySolutionFunctionDAO;
  // Managers


  @Inject
  public ProjectInnovationComplementarySolutionManagerImpl(
    ProjectInnovationComplementarySolutionDAO projectInnovationComplementarySolutionDAO, PhaseDAO phaseDAO,
    ProjectInnovationComplementarySolutionFunctionDAO projectInnovationComplementarySolutionFunctionDAO) {
    this.projectInnovationComplementarySolutionDAO = projectInnovationComplementarySolutionDAO;
    this.projectInnovationComplementarySolutionFunctionDAO = projectInnovationComplementarySolutionFunctionDAO;
    this.phaseDAO = phaseDAO;
  }

  @Override
  @Transactional
  public void deleteProjectInnovationComplementarySolution(long projectInnovationComplementarySolutionId) {
    ProjectInnovationComplementarySolution projectInnovationComplementarySolution =
      this.getProjectInnovationComplementarySolutionById(projectInnovationComplementarySolutionId);

    // Conditions to Project Innovation Works In AR phase and Upkeep Phase
    if (projectInnovationComplementarySolution.getPhase().getDescription().equals(APConstants.PLANNING)
      && projectInnovationComplementarySolution.getPhase().getNext() != null) {
      this.deleteProjectInnovationComplementarySolutionPhase(
        projectInnovationComplementarySolution.getPhase().getNext(),
        projectInnovationComplementarySolution.getProjectInnovation().getId(), projectInnovationComplementarySolution);
    }

    if (projectInnovationComplementarySolution.getPhase().getDescription().equals(APConstants.REPORTING)
      && projectInnovationComplementarySolution.getPhase().getNext() != null
      && projectInnovationComplementarySolution.getPhase().getNext().getNext() != null) {
      Phase upkeepPhase = projectInnovationComplementarySolution.getPhase().getNext().getNext();
      if (upkeepPhase != null) {
        this.deleteProjectInnovationComplementarySolutionPhase(upkeepPhase,
          projectInnovationComplementarySolution.getProjectInnovation().getId(),
          projectInnovationComplementarySolution);
      }
    }
    projectInnovationComplementarySolutionDAO
      .deleteProjectInnovationComplementarySolution(projectInnovationComplementarySolutionId);
  }

  @Transactional
  public void deleteProjectInnovationComplementarySolutionPhase(Phase next, long innovationID,
    ProjectInnovationComplementarySolution projectInnovationComplementarySolution) {

    Phase phase = phaseDAO.find(next.getId());

    List<ProjectInnovationComplementarySolution> innovationComplementarySolution = Optional
      .ofNullable(
        projectInnovationComplementarySolutionDAO.getProjectInnovationComplementarySolutionByInnovation(innovationID))
      .orElse(Collections.emptyList()).stream().filter(c -> {
        ProjectInnovationComplementarySolution selA = c;
        ProjectInnovationComplementarySolution selB = projectInnovationComplementarySolution;
        return selA != null && selA.getId() != null && selB != null && selB.getId() != null
          && selA.getTitle().equals(selB.getTitle());
      }).collect(Collectors.toList());

    for (ProjectInnovationComplementarySolution projectInnovationComplementarySolutionDB : innovationComplementarySolution) {
      if (projectInnovationComplementarySolutionDB != null
        && projectInnovationComplementarySolutionDB.getId() != null) {
        projectInnovationComplementarySolutionDAO
          .deleteProjectInnovationComplementarySolution(projectInnovationComplementarySolutionDB.getId());
      }
    }

    if (phase.getNext() != null) {
      this.deleteProjectInnovationComplementarySolutionPhase(phase.getNext(), innovationID,
        projectInnovationComplementarySolution);
    }
  }

  @Override
  public boolean existProjectInnovationComplementarySolution(long projectInnovationComplementarySolutionID) {

    return projectInnovationComplementarySolutionDAO
      .existProjectInnovationComplementarySolution(projectInnovationComplementarySolutionID);
  }

  @Override
  public List<ProjectInnovationComplementarySolution> findAll() {

    return projectInnovationComplementarySolutionDAO.findAll();

  }

  @Override
  public ProjectInnovationComplementarySolution
    getProjectInnovationComplementarySolutionById(long projectInnovationComplementarySolutionID) {

    return projectInnovationComplementarySolutionDAO.find(projectInnovationComplementarySolutionID);
  }

  @Override
  public List<ProjectInnovationComplementarySolution>
    getProjectInnovationComplementarySolutionByInnovation(long innovationID) {
    return projectInnovationComplementarySolutionDAO
      .getProjectInnovationComplementarySolutionByInnovation(innovationID);
  }

  @Override
  public List<ProjectInnovationComplementarySolution>
    getProjectInnovationComplementarySolutionByInnovationAndPhase(long innovationID, long phaseID) {
    return projectInnovationComplementarySolutionDAO
      .getProjectInnovationComplementarySolutionByInnovationAndPhase(innovationID, phaseID);
  }

  @Override
  public ProjectInnovationComplementarySolution saveProjectInnovationComplementarySolution(
    ProjectInnovationComplementarySolution projectInnovationComplementarySolution) {

    ProjectInnovationComplementarySolution innovationComplementarySolution =
      projectInnovationComplementarySolutionDAO.save(projectInnovationComplementarySolution);
    Phase phase = phaseDAO.find(projectInnovationComplementarySolution.getPhase().getId());

    if (phase.getDescription().equals(APConstants.PLANNING) && phase.getNext() != null) {
      this.saveProjectInnovationComplementarySolutionPhase(innovationComplementarySolution.getPhase().getNext(),
        innovationComplementarySolution.getProjectInnovation().getId(), innovationComplementarySolution);
    }

    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (phase.getNext() != null && phase.getNext().getNext() != null) {
        Phase upkeepPhase = phase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveProjectInnovationComplementarySolutionPhase(upkeepPhase,
            innovationComplementarySolution.getProjectInnovation().getId(), innovationComplementarySolution);
        }
      }
    }
    return innovationComplementarySolution;
  }

  private void saveProjectInnovationComplementarySolutionPhase(Phase next, Long innovationID,
    ProjectInnovationComplementarySolution projectInnovationComplementarySolution) {

    Phase phase = phaseDAO.find(next.getId());

    List<ProjectInnovationComplementarySolution> innovationComplementarySolutions = Optional
      .ofNullable(
        projectInnovationComplementarySolutionDAO.getProjectInnovationComplementarySolutionByInnovation(innovationID))
      .orElse(Collections.emptyList()).stream().filter(c -> {
        ProjectInnovationComplementarySolution selA = c;
        ProjectInnovationComplementarySolution selB = projectInnovationComplementarySolution;
        return selA != null && selA.getId() != null && selB != null && selB.getId() != null
          && selA.getTitle().equals(selB.getTitle()) && selA.getPhase().getId().equals(phase.getId());
      }).collect(Collectors.toList());


    if (innovationComplementarySolutions.isEmpty()) {
      ProjectInnovationComplementarySolution projectInnovationComplementarySolutionAdd =
        new ProjectInnovationComplementarySolution();
      projectInnovationComplementarySolutionAdd
        .setProjectInnovation(projectInnovationComplementarySolution.getProjectInnovation());
      projectInnovationComplementarySolutionAdd.setPhase(phase);
      projectInnovationComplementarySolutionAdd
        .setProjectInnovationType(projectInnovationComplementarySolution.getProjectInnovationType());
      projectInnovationComplementarySolutionAdd.setShortTitle(projectInnovationComplementarySolution.getShortTitle());
      projectInnovationComplementarySolutionAdd.setTitle(projectInnovationComplementarySolution.getTitle());
      projectInnovationComplementarySolutionAdd
        .setShortDescription(projectInnovationComplementarySolution.getShortDescription());
      projectInnovationComplementarySolutionAdd =
        projectInnovationComplementarySolutionDAO.save(projectInnovationComplementarySolutionAdd);
    }
    if (phase.getNext() != null) {
      this.saveProjectInnovationComplementarySolutionPhase(phase.getNext(), innovationID,
        projectInnovationComplementarySolution);
    }
  }


}
