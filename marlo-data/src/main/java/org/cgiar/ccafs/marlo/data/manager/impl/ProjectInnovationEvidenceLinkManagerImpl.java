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
import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationEvidenceLinkDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationEvidenceLinkManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationEvidenceLink;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectInnovationEvidenceLinkManagerImpl implements ProjectInnovationEvidenceLinkManager {

  private ProjectInnovationEvidenceLinkDAO projectInnovationEvidenceLinkDAO;
  // Managers
  private PhaseDAO phaseDAO;


  @Inject
  public ProjectInnovationEvidenceLinkManagerImpl(ProjectInnovationEvidenceLinkDAO projectInnovationEvidenceLinkDAO,
    PhaseDAO phaseDAO) {
    this.projectInnovationEvidenceLinkDAO = projectInnovationEvidenceLinkDAO;
    this.phaseDAO = phaseDAO;
  }

  @Override
  public void deleteProjectInnovationEvidenceLink(long projectInnovationEvidenceLinkId) {
    ProjectInnovationEvidenceLink projectInnovationEvidenceLink =
      this.getProjectInnovationEvidenceLinkById(projectInnovationEvidenceLinkId);
    Phase currentPhase = projectInnovationEvidenceLink.getPhase();

    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (currentPhase.getNext() != null) {
        this.deleteProjectInnovationEvidenceLinkPhase(currentPhase.getNext(),
          projectInnovationEvidenceLink.getProjectInnovation().getId(), projectInnovationEvidenceLink);
      }
    }

    if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.deleteProjectInnovationEvidenceLinkPhase(upkeepPhase,
            projectInnovationEvidenceLink.getProjectInnovation().getId(), projectInnovationEvidenceLink);
        }
      }
    }

    projectInnovationEvidenceLinkDAO.deleteProjectInnovationEvidenceLink(projectInnovationEvidenceLinkId);
  }

  public void deleteProjectInnovationEvidenceLinkPhase(Phase next, long expectedID,
    ProjectInnovationEvidenceLink projectInnovationEvidenceLink) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectInnovationEvidenceLink> projectInnovationEvidenceLinks = this.getAllInnovationLinksByStudy(expectedID)
      .stream().filter(c -> c != null && c.getId() != null && c.getPhase() != null && c.getPhase().equals(phase)
        && c.getLink().equals(projectInnovationEvidenceLink.getLink()))
      .collect(Collectors.toList());

    for (ProjectInnovationEvidenceLink projectInnovationEvidenceLinkDel : projectInnovationEvidenceLinks) {
      projectInnovationEvidenceLinkDAO.deleteProjectInnovationEvidenceLink(projectInnovationEvidenceLinkDel.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectInnovationEvidenceLinkPhase(phase.getNext(), expectedID, projectInnovationEvidenceLink);
    }

  }

  @Override
  public boolean existProjectInnovationEvidenceLink(long projectInnovationEvidenceLinkID) {
    return projectInnovationEvidenceLinkDAO.existProjectInnovationEvidenceLink(projectInnovationEvidenceLinkID);
  }

  @Override
  public List<ProjectInnovationEvidenceLink> findAll() {
    return projectInnovationEvidenceLinkDAO.findAll();
  }

  @Override
  public List<ProjectInnovationEvidenceLink> getAllInnovationLinksByStudy(Long studyId) {
    return this.projectInnovationEvidenceLinkDAO.getAllInnovationLinksByStudy(studyId.longValue());
  }

  @Override
  public ProjectInnovationEvidenceLink getProjectInnovationEvidenceLinkById(long projectInnovationEvidenceLinkID) {
    return projectInnovationEvidenceLinkDAO.find(projectInnovationEvidenceLinkID);
  }

  @Override
  public List<ProjectInnovationEvidenceLink> getProjectInnovationEvidenceLinkByPhase(Long innovationID, Long phaseID) {

    return projectInnovationEvidenceLinkDAO.getProjectInnovationEvidenceLinkByPhase(innovationID, phaseID);
  }

  @Override
  public ProjectInnovationEvidenceLink getProjectInnovationEvidenceLinkByPhase(Long expectedID, String link,
    Long phaseID) {
    return projectInnovationEvidenceLinkDAO.getProjectInnovationEvidenceLinkByPhase(expectedID, link, phaseID);
  }

  public void saveExpectedStudyLinkPhase(Phase next, long expectedID,
    ProjectInnovationEvidenceLink projectInnovationEvidenceLink) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectInnovationEvidenceLink> projectInnovationEvidenceLinks = this.getAllInnovationLinksByStudy(expectedID)
      .stream().filter(c -> c != null && c.getId() != null && c.getPhase() != null && c.getPhase().equals(phase)
        && c.getLink().equals(projectInnovationEvidenceLink.getLink()))
      .collect(Collectors.toList());

    if (projectInnovationEvidenceLinks.isEmpty()) {
      ProjectInnovationEvidenceLink projectInnovationEvidenceLinkAdd = new ProjectInnovationEvidenceLink();
      projectInnovationEvidenceLinkAdd.setProjectInnovation(projectInnovationEvidenceLink.getProjectInnovation());
      projectInnovationEvidenceLinkAdd.setPhase(phase);
      projectInnovationEvidenceLinkAdd.setLink(projectInnovationEvidenceLink.getLink());
      projectInnovationEvidenceLinkDAO.save(projectInnovationEvidenceLinkAdd);
    } else {
      for (ProjectInnovationEvidenceLink projectInnovationEvidenceLinkDel : projectInnovationEvidenceLinks) {
        try {
          projectInnovationEvidenceLinkDAO
            .deleteProjectInnovationEvidenceLink(projectInnovationEvidenceLinkDel.getId());
        } catch (Exception e) {
          // TODO: handle exception
        }
      }
      ProjectInnovationEvidenceLink projectInnovationEvidenceLinkAdd = new ProjectInnovationEvidenceLink();
      projectInnovationEvidenceLinkAdd.setProjectInnovation(projectInnovationEvidenceLink.getProjectInnovation());
      projectInnovationEvidenceLinkAdd.setPhase(phase);
      projectInnovationEvidenceLinkAdd.setLink(projectInnovationEvidenceLink.getLink());
      projectInnovationEvidenceLinkDAO.save(projectInnovationEvidenceLinkAdd);
    }

    if (phase.getNext() != null) {
      this.saveExpectedStudyLinkPhase(phase.getNext(), expectedID, projectInnovationEvidenceLink);
    }
  }

  @Override
  public ProjectInnovationEvidenceLink
    saveProjectInnovationEvidenceLink(ProjectInnovationEvidenceLink projectInnovationEvidenceLink) {
    ProjectInnovationEvidenceLink projectInnovationEvidenceLinkResult =
      projectInnovationEvidenceLinkDAO.save(projectInnovationEvidenceLink);
    Phase currentPhase = projectInnovationEvidenceLinkResult.getPhase();

    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (currentPhase.getNext() != null) {
        this.saveExpectedStudyLinkPhase(currentPhase.getNext(),
          projectInnovationEvidenceLink.getProjectInnovation().getId(), projectInnovationEvidenceLink);
      }
    }

    if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveExpectedStudyLinkPhase(upkeepPhase, projectInnovationEvidenceLink.getProjectInnovation().getId(),
            projectInnovationEvidenceLink);
        }
      }
    }

    return projectInnovationEvidenceLinkResult;
  }
}
