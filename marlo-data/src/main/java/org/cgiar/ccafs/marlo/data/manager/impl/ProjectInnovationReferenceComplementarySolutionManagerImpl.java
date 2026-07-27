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
import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationReferenceComplementarySolutionDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationReferenceComplementarySolutionManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationReferenceComplementarySolution;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author CCAFS
 */
@Named
public class ProjectInnovationReferenceComplementarySolutionManagerImpl
  implements ProjectInnovationReferenceComplementarySolutionManager {

  private ProjectInnovationReferenceComplementarySolutionDAO projectInnovationReferenceComplementarySolutionDAO;
  private PhaseDAO phaseDAO;
  // Managers

  @Inject
  public ProjectInnovationReferenceComplementarySolutionManagerImpl(
    ProjectInnovationReferenceComplementarySolutionDAO projectInnovationReferenceComplementarySolutionDAO,
    PhaseDAO phaseDAO) {
    this.projectInnovationReferenceComplementarySolutionDAO = projectInnovationReferenceComplementarySolutionDAO;
    this.phaseDAO = phaseDAO;
  }

  @Override
  public void
    deleteProjectInnovationReferenceComplementarySolution(long projectInnovationReferenceComplementarySolutionId) {
    ProjectInnovationReferenceComplementarySolution projectInnovationReferenceComplementarySolution =
      this.getProjectInnovationReferenceComplementarySolutionById(projectInnovationReferenceComplementarySolutionId);
    Phase currentPhase = projectInnovationReferenceComplementarySolution.getPhase();

    if (currentPhase.getDescription().equals(APConstants.PLANNING) && currentPhase.getNext() != null) {
      this.deleteProjectInnovationReferenceComplementarySolutionPhase(currentPhase.getNext(),
        projectInnovationReferenceComplementarySolution.getProjectInnovation().getId(),
        projectInnovationReferenceComplementarySolution);
    }

    if (currentPhase.getDescription().equals(APConstants.REPORTING) && currentPhase.getNext() != null
      && currentPhase.getNext().getNext() != null) {
      Phase upkeepPhase = currentPhase.getNext().getNext();
      if (upkeepPhase != null) {
        this.deleteProjectInnovationReferenceComplementarySolutionPhase(upkeepPhase,
          projectInnovationReferenceComplementarySolution.getProjectInnovation().getId(),
          projectInnovationReferenceComplementarySolution);
      }
    }


    projectInnovationReferenceComplementarySolutionDAO
      .deleteProjectInnovationReferenceComplementarySolution(projectInnovationReferenceComplementarySolutionId);
  }

  @Transactional
  public void deleteProjectInnovationReferenceComplementarySolutionPhase(Phase next, long innovationID,
    ProjectInnovationReferenceComplementarySolution projectInnovationReferenceComplementarySolution) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectInnovationReferenceComplementarySolution> projectInnovationReferenceComplementarySolutions =
      this.getProjectInnovationReferenceComplementarySolutionByPhaseAndInnovation(next.getId(), innovationID).stream()
        .filter(c -> c != null && c.getId() != null
          && c.getReference().equals(projectInnovationReferenceComplementarySolution.getReference()))
        .collect(Collectors.toList());

    for (ProjectInnovationReferenceComplementarySolution projectInnovationReferenceComplementarySolutionDel : projectInnovationReferenceComplementarySolutions) {
      projectInnovationReferenceComplementarySolutionDAO.deleteProjectInnovationReferenceComplementarySolution(
        projectInnovationReferenceComplementarySolutionDel.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectInnovationReferenceComplementarySolutionPhase(phase.getNext(), innovationID,
        projectInnovationReferenceComplementarySolution);
    }

  }

  @Override
  public boolean
    existProjectInnovationReferenceComplementarySolution(long projectInnovationReferenceComplementarySolutionID) {

    return projectInnovationReferenceComplementarySolutionDAO
      .existProjectInnovationReferenceComplementarySolution(projectInnovationReferenceComplementarySolutionID);
  }

  @Override
  public List<ProjectInnovationReferenceComplementarySolution> findAll() {

    return projectInnovationReferenceComplementarySolutionDAO.findAll();

  }

  @Override
  public ProjectInnovationReferenceComplementarySolution
    getProjectInnovationReferenceComplementarySolutionById(long projectInnovationReferenceComplementarySolutionID) {

    return projectInnovationReferenceComplementarySolutionDAO.find(projectInnovationReferenceComplementarySolutionID);
  }

  @Override
  public List<ProjectInnovationReferenceComplementarySolution>
    getProjectInnovationReferenceComplementarySolutionByPhaseAndInnovation(long phaseID, long innovationID) {
    return projectInnovationReferenceComplementarySolutionDAO
      .getProjectInnovationReferenceComplementarySolutionByPhaseAndInnovation(phaseID, innovationID);
  }

  @Override
  public ProjectInnovationReferenceComplementarySolution saveProjectInnovationReferenceComplementarySolution(
    ProjectInnovationReferenceComplementarySolution projectInnovationReferenceComplementarySolution) {
    ProjectInnovationReferenceComplementarySolution projectInnovationReferenceComplementarySolutionResult =
      projectInnovationReferenceComplementarySolutionDAO.save(projectInnovationReferenceComplementarySolution);
    Phase currentPhase = projectInnovationReferenceComplementarySolutionResult.getPhase();

    if (currentPhase.getDescription().equals(APConstants.PLANNING) && currentPhase.getNext() != null) {
      this.saveProjectInnovationReferenceComplementarySolutionPhase(currentPhase.getNext(),
        projectInnovationReferenceComplementarySolution.getProjectInnovation().getId(),
        projectInnovationReferenceComplementarySolution);
    }


    if (currentPhase.getDescription().equals(APConstants.REPORTING) && currentPhase.getNext() != null
      && currentPhase.getNext().getNext() != null) {
      Phase upkeepPhase = currentPhase.getNext().getNext();
      if (upkeepPhase != null) {
        this.saveProjectInnovationReferenceComplementarySolutionPhase(upkeepPhase,
          projectInnovationReferenceComplementarySolution.getProjectInnovation().getId(),
          projectInnovationReferenceComplementarySolution);
      }
    }

    return projectInnovationReferenceComplementarySolutionResult;
  }

  public void saveProjectInnovationReferenceComplementarySolutionPhase(Phase next, long innovationID,
    ProjectInnovationReferenceComplementarySolution projectInnovationReferenceComplementarySolution) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectInnovationReferenceComplementarySolution> projectInnovationReferenceComplementarySolutions =
      this.getProjectInnovationReferenceComplementarySolutionByPhaseAndInnovation(next.getId(), innovationID).stream()
        .filter(c -> c != null && c.getId() != null
          && c.getReference().equals(projectInnovationReferenceComplementarySolution.getReference()))
        .collect(Collectors.toList());

    if (projectInnovationReferenceComplementarySolutions.isEmpty()) {
      ProjectInnovationReferenceComplementarySolution projectInnovationReferenceComplementarySolutionAdd =
        new ProjectInnovationReferenceComplementarySolution();
      projectInnovationReferenceComplementarySolutionAdd
        .setProjectInnovation(projectInnovationReferenceComplementarySolution.getProjectInnovation());
      projectInnovationReferenceComplementarySolutionAdd.setPhase(phase);
      projectInnovationReferenceComplementarySolutionAdd
        .setReference(projectInnovationReferenceComplementarySolution.getReference());
      projectInnovationReferenceComplementarySolutionAdd
        .setLink(projectInnovationReferenceComplementarySolution.getLink());
      projectInnovationReferenceComplementarySolutionAdd
        .setEvidenceByDeliverable(projectInnovationReferenceComplementarySolution.getEvidenceByDeliverable());
      projectInnovationReferenceComplementarySolutionDAO.save(projectInnovationReferenceComplementarySolutionAdd);
    }
    /*
     * else {
     * ProjectInnovationReferenceComplementarySolution projectInnovationReferenceComplementarySolutionAdd =
     * new ProjectInnovationReferenceComplementarySolution();
     * projectInnovationReferenceComplementarySolutionAdd
     * .setProjectInnovation(projectInnovationReferenceComplementarySolution.getProjectInnovation());
     * projectInnovationReferenceComplementarySolutionAdd.setPhase(phase);
     * projectInnovationReferenceComplementarySolutionAdd
     * .setReference(projectInnovationReferenceComplementarySolution.getReference());
     * projectInnovationReferenceComplementarySolutionAdd
     * .setLink(projectInnovationReferenceComplementarySolution.getLink());
     * projectInnovationReferenceComplementarySolutionAdd
     * .setEvidenceByDeliverable(projectInnovationReferenceComplementarySolution.getEvidenceByDeliverable());
     * projectInnovationReferenceComplementarySolutionDAO.save(projectInnovationReferenceComplementarySolutionAdd);
     * for (ProjectInnovationReferenceComplementarySolution projectInnovationReferenceComplementarySolutionDel :
     * projectInnovationReferenceComplementarySolutions) {
     * try {
     * projectInnovationReferenceComplementarySolutionDAO.deleteProjectInnovationReferenceComplementarySolution(
     * projectInnovationReferenceComplementarySolutionDel.getId());
     * } catch (Exception e) {
     * // TODO: handle exception
     * }
     * }
     * }
     */
    if (phase.getNext() != null) {
      this.saveProjectInnovationReferenceComplementarySolutionPhase(phase.getNext(), innovationID,
        projectInnovationReferenceComplementarySolution);
    }
  }

}
