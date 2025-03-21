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
import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationReferenceDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationReferenceManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationReference;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectInnovationReferenceManagerImpl implements ProjectInnovationReferenceManager {


  private ProjectInnovationReferenceDAO projectInnovationReferenceDAO;
  private PhaseDAO phaseDAO;
  // Managers


  @Inject
  public ProjectInnovationReferenceManagerImpl(ProjectInnovationReferenceDAO projectInnovationReferenceDAO,
    PhaseDAO phaseDAO) {
    this.projectInnovationReferenceDAO = projectInnovationReferenceDAO;
    this.phaseDAO = phaseDAO;

  }

  @Override
  public void deleteProjectInnovationReference(long projectInnovationReferenceId) {
    ProjectInnovationReference projectInnovationReference =
      this.getProjectInnovationReferenceById(projectInnovationReferenceId);
    Phase currentPhase = projectInnovationReference.getPhase();

    if (currentPhase.getDescription().equals(APConstants.PLANNING) && currentPhase.getNext() != null) {
      this.deleteProjectInnovationReferencePhase(currentPhase.getNext(),
        projectInnovationReference.getProjectInnovation().getId(), projectInnovationReference);
    }

    if (currentPhase.getDescription().equals(APConstants.REPORTING) && currentPhase.getNext() != null
      && currentPhase.getNext().getNext() != null) {
      Phase upkeepPhase = currentPhase.getNext().getNext();
      if (upkeepPhase != null) {
        this.deleteProjectInnovationReferencePhase(upkeepPhase,
          projectInnovationReference.getProjectInnovation().getId(), projectInnovationReference);
      }
    }
    projectInnovationReferenceDAO.deleteProjectInnovationReference(projectInnovationReferenceId);
  }

  public void deleteProjectInnovationReferencePhase(Phase next, long innovationID,
    ProjectInnovationReference projectInnovationReference) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectInnovationReference> projectInnovationReferences =
      this.getProjectInnovationReferenceByPhaseAndInnovation(next.getId(), innovationID).stream()
        .filter(
          c -> c != null && c.getId() != null && c.getReference().equals(projectInnovationReference.getReference()))
        .collect(Collectors.toList());

    for (ProjectInnovationReference projectInnovationReferenceDel : projectInnovationReferences) {
      projectInnovationReferenceDAO.deleteProjectInnovationReference(projectInnovationReferenceDel.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectInnovationReferencePhase(phase.getNext(), innovationID, projectInnovationReference);
    }

  }

  @Override
  public boolean existProjectInnovationReference(long projectInnovationReferenceID) {

    return projectInnovationReferenceDAO.existProjectInnovationReference(projectInnovationReferenceID);
  }

  @Override
  public List<ProjectInnovationReference> findAll() {

    return projectInnovationReferenceDAO.findAll();

  }

  @Override
  public ProjectInnovationReference getProjectInnovationReferenceById(long projectInnovationReferenceID) {

    return projectInnovationReferenceDAO.find(projectInnovationReferenceID);
  }

  @Override
  public List<ProjectInnovationReference> getProjectInnovationReferenceByPhaseAndInnovation(long phaseID,
    long innovationID) {
    return projectInnovationReferenceDAO.getProjectInnovationReferenceByPhaseAndInnovation(phaseID, innovationID);
  }

  @Override
  public ProjectInnovationReference
    saveProjectInnovationReference(ProjectInnovationReference projectInnovationReference) {
    ProjectInnovationReference projectInnovationReferenceResult =
      projectInnovationReferenceDAO.save(projectInnovationReference);
    Phase currentPhase = projectInnovationReferenceResult.getPhase();

    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (currentPhase.getNext() != null) {
        this.saveProjectInnovationReferencePhase(currentPhase.getNext(),
          projectInnovationReference.getProjectInnovation().getId(), projectInnovationReference);
      }
    }

    if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveProjectInnovationReferencePhase(upkeepPhase,
            projectInnovationReference.getProjectInnovation().getId(), projectInnovationReference);
        }
      }
    }
    return projectInnovationReferenceResult;
  }

  public void saveProjectInnovationReferencePhase(Phase next, long innovationID,
    ProjectInnovationReference projectInnovationReference) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectInnovationReference> projectInnovationReferences =
      this.getProjectInnovationReferenceByPhaseAndInnovation(next.getId(), innovationID).stream()
        .filter(
          c -> c != null && c.getId() != null && c.getReference().equals(projectInnovationReference.getReference()))
        .collect(Collectors.toList());

    if (projectInnovationReferences.isEmpty()) {
      ProjectInnovationReference projectInnovationReferenceAdd = new ProjectInnovationReference();
      projectInnovationReferenceAdd.setProjectInnovation(projectInnovationReference.getProjectInnovation());
      projectInnovationReferenceAdd.setPhase(phase);
      projectInnovationReferenceAdd.setReference(projectInnovationReference.getReference());
      projectInnovationReferenceAdd.setLink(projectInnovationReference.getLink());
      projectInnovationReferenceAdd.setEvidenceByDeliverable(projectInnovationReference.getEvidenceByDeliverable());
      projectInnovationReferenceAdd.setExternalAuthor(projectInnovationReference.getExternalAuthor());
      projectInnovationReferenceAdd.setDeliverableType(projectInnovationReference.getDeliverableType());
      projectInnovationReferenceDAO.save(projectInnovationReferenceAdd);
    }

    /*
     * else {
     * ProjectInnovationReference projectInnovationReferenceAdd = new ProjectInnovationReference();
     * projectInnovationReferenceAdd.setProjectInnovation(projectInnovationReference.getProjectInnovation());
     * projectInnovationReferenceAdd.setPhase(phase);
     * projectInnovationReferenceAdd.setReference(projectInnovationReference.getReference());
     * projectInnovationReferenceAdd.setLink(projectInnovationReference.getLink());
     * projectInnovationReferenceAdd.setEvidenceByDeliverable(projectInnovationReference.getEvidenceByDeliverable());
     * projectInnovationReferenceAdd.setExternalAuthor(projectInnovationReference.getExternalAuthor());
     * projectInnovationReferenceAdd.setDeliverableType(projectInnovationReference.getDeliverableType());
     * projectInnovationReferenceDAO.save(projectInnovationReferenceAdd);
     * for (ProjectInnovationReference projectInnovationReferenceDel : projectInnovationReferences) {
     * try {
     * projectInnovationReferenceDAO.deleteProjectInnovationReference(projectInnovationReferenceDel.getId());
     * } catch (Exception e) {
     * // TODO: handle exception
     * }
     * }
     * }
     */
    if (phase.getNext() != null) {
      this.saveProjectInnovationReferencePhase(phase.getNext(), innovationID, projectInnovationReference);
    }
  }


}
