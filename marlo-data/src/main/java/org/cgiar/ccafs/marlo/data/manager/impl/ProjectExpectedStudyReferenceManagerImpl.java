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
import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyReferenceDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyReferenceManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyReference;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudyReferenceManagerImpl implements ProjectExpectedStudyReferenceManager {

  private ProjectExpectedStudyReferenceDAO projectExpectedStudyReferenceDAO;
  // Managers
  private PhaseDAO phaseDAO;


  @Inject
  public ProjectExpectedStudyReferenceManagerImpl(ProjectExpectedStudyReferenceDAO projectExpectedStudyReferenceDAO,
    PhaseDAO phaseDAO) {
    this.projectExpectedStudyReferenceDAO = projectExpectedStudyReferenceDAO;
    this.phaseDAO = phaseDAO;
  }

  @Override
  public void deleteProjectExpectedStudyReference(long projectExpectedStudyReferenceId) {
    ProjectExpectedStudyReference projectExpectedStudyReference =
      this.getProjectExpectedStudyReferenceById(projectExpectedStudyReferenceId);
    Phase currentPhase = projectExpectedStudyReference.getPhase();

    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (currentPhase.getNext() != null) {
        this.deleteProjectExpectedStudyReferencePhase(currentPhase.getNext(),
          projectExpectedStudyReference.getProjectExpectedStudy().getId(), projectExpectedStudyReference);
      }
    }

    if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.deleteProjectExpectedStudyReferencePhase(upkeepPhase,
            projectExpectedStudyReference.getProjectExpectedStudy().getId(), projectExpectedStudyReference);
        }
      }
    }

    projectExpectedStudyReferenceDAO.deleteProjectExpectedStudyReference(projectExpectedStudyReferenceId);
  }

  public void deleteProjectExpectedStudyReferencePhase(Phase next, long expectedID,
    ProjectExpectedStudyReference projectExpectedStudyReference) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudyReference> projectExpectedStudyReferences = this.getAllStudyReferencesByStudy(expectedID)
      .stream().filter(c -> c != null && c.getId() != null && c.getPhase() != null && c.getPhase().equals(phase)
        && c.getReference().equals(projectExpectedStudyReference.getReference()))
      .collect(Collectors.toList());

    for (ProjectExpectedStudyReference projectExpectedStudyReferenceDel : projectExpectedStudyReferences) {
      projectExpectedStudyReferenceDAO.deleteProjectExpectedStudyReference(projectExpectedStudyReferenceDel.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectExpectedStudyReferencePhase(phase.getNext(), expectedID, projectExpectedStudyReference);
    }

  }

  @Override
  public boolean existProjectExpectedStudyReference(long projectExpectedStudyReferenceID) {

    return projectExpectedStudyReferenceDAO.existProjectExpectedStudyReference(projectExpectedStudyReferenceID);
  }

  @Override
  public List<ProjectExpectedStudyReference> findAll() {

    return projectExpectedStudyReferenceDAO.findAll();

  }

  @Override
  public List<ProjectExpectedStudyReference> getAllStudyReferencesByStudy(Long studyId) {
    return this.projectExpectedStudyReferenceDAO.getAllStudyReferencesByStudy(studyId.longValue());
  }

  @Override
  public ProjectExpectedStudyReference getProjectExpectedStudyReferenceById(long projectExpectedStudyReferenceID) {

    return projectExpectedStudyReferenceDAO.find(projectExpectedStudyReferenceID);
  }

  @Override
  public ProjectExpectedStudyReference getProjectExpectedStudyReferenceByPhase(Long expectedID, String link,
    Long phaseID) {

    return projectExpectedStudyReferenceDAO.getProjectExpectedStudyReferenceByPhase(expectedID, link, phaseID);
  }

  public void saveExpectedStudyLinkPhase(Phase next, long expectedID,
    ProjectExpectedStudyReference projectExpectedStudyReference) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudyReference> projectExpectedStudyReferences = this.getAllStudyReferencesByStudy(expectedID)
      .stream().filter(c -> c != null && c.getId() != null && c.getPhase() != null && c.getPhase().equals(phase)
        && c.getReference().equals(projectExpectedStudyReference.getReference()))
      .collect(Collectors.toList());

    if (projectExpectedStudyReferences.isEmpty()) {
      ProjectExpectedStudyReference projectExpectedStudyReferenceAdd = new ProjectExpectedStudyReference();
      projectExpectedStudyReferenceAdd.setProjectExpectedStudy(projectExpectedStudyReference.getProjectExpectedStudy());
      projectExpectedStudyReferenceAdd.setPhase(phase);
      projectExpectedStudyReferenceAdd.setReference(projectExpectedStudyReference.getReference());
      projectExpectedStudyReferenceAdd.setLink(projectExpectedStudyReference.getLink());
      projectExpectedStudyReferenceDAO.save(projectExpectedStudyReferenceAdd);
    } else {
      ProjectExpectedStudyReference projectExpectedStudyReferenceAdd = new ProjectExpectedStudyReference();
      projectExpectedStudyReferenceAdd.setProjectExpectedStudy(projectExpectedStudyReference.getProjectExpectedStudy());
      projectExpectedStudyReferenceAdd.setPhase(phase);
      projectExpectedStudyReferenceAdd.setReference(projectExpectedStudyReference.getReference());
      projectExpectedStudyReferenceAdd.setLink(projectExpectedStudyReference.getLink());
      projectExpectedStudyReferenceDAO.save(projectExpectedStudyReferenceAdd);

      for (ProjectExpectedStudyReference projectExpectedStudyReferenceDel : projectExpectedStudyReferences) {
        try {
          projectExpectedStudyReferenceDAO
            .deleteProjectExpectedStudyReference(projectExpectedStudyReferenceDel.getId());
        } catch (Exception e) {
          // TODO: handle exception
        }
      }
    }

    if (phase.getNext() != null) {
      this.saveExpectedStudyLinkPhase(phase.getNext(), expectedID, projectExpectedStudyReference);
    }
  }

  @Override
  public ProjectExpectedStudyReference
    saveProjectExpectedStudyReference(ProjectExpectedStudyReference projectExpectedStudyReference) {
    ProjectExpectedStudyReference projectExpectedStudyReferenceResult =
      projectExpectedStudyReferenceDAO.save(projectExpectedStudyReference);
    Phase currentPhase = projectExpectedStudyReferenceResult.getPhase();

    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (currentPhase.getNext() != null) {
        this.saveExpectedStudyLinkPhase(currentPhase.getNext(),
          projectExpectedStudyReference.getProjectExpectedStudy().getId(), projectExpectedStudyReference);
      }
    }

    if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveExpectedStudyLinkPhase(upkeepPhase, projectExpectedStudyReference.getProjectExpectedStudy().getId(),
            projectExpectedStudyReference);
        }
      }
    }


    return projectExpectedStudyReferenceResult;
  }
}
