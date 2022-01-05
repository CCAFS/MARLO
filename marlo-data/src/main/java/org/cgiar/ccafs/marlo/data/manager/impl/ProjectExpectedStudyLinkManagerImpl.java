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
import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyLinkDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyLinkManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyLink;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudyLinkManagerImpl implements ProjectExpectedStudyLinkManager {


  private ProjectExpectedStudyLinkDAO projectExpectedStudyLinkDAO;
  // Managers
  private PhaseDAO phaseDAO;


  @Inject
  public ProjectExpectedStudyLinkManagerImpl(ProjectExpectedStudyLinkDAO projectExpectedStudyLinkDAO,
    PhaseDAO phaseDAO) {
    this.projectExpectedStudyLinkDAO = projectExpectedStudyLinkDAO;
    this.phaseDAO = phaseDAO;
  }

  @Override
  public void deleteProjectExpectedStudyLink(long projectExpectedStudyLinkId) {


    ProjectExpectedStudyLink projectExpectedStudyLink =
      this.getProjectExpectedStudyLinkById(projectExpectedStudyLinkId);
    Phase currentPhase = projectExpectedStudyLink.getPhase();

    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (currentPhase.getNext() != null) {
        this.deleteProjectExpectedStudyLinkPhase(currentPhase.getNext(),
          projectExpectedStudyLink.getProjectExpectedStudy().getId(), projectExpectedStudyLink);
      }
    }

    if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.deleteProjectExpectedStudyLinkPhase(upkeepPhase,
            projectExpectedStudyLink.getProjectExpectedStudy().getId(), projectExpectedStudyLink);
        }
      }
    }

    projectExpectedStudyLinkDAO.deleteProjectExpectedStudyLink(projectExpectedStudyLinkId);
  }

  public void deleteProjectExpectedStudyLinkPhase(Phase next, long expectedID,
    ProjectExpectedStudyLink projectExpectedStudyLink) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudyLink> projectExpectedStudyLinks = phase.getProjectExpectedStudyLinks().stream()
      .filter(c -> c.getProjectExpectedStudy().getId().longValue() == expectedID
        && c.getLink().equals(projectExpectedStudyLink.getLink()))
      .collect(Collectors.toList());

    for (ProjectExpectedStudyLink projectExpectedStudyLinkDel : projectExpectedStudyLinks) {
      projectExpectedStudyLinkDAO.deleteProjectExpectedStudyLink(projectExpectedStudyLinkDel.getId());
    }


    if (phase.getNext() != null) {
      this.deleteProjectExpectedStudyLinkPhase(phase.getNext(), expectedID, projectExpectedStudyLink);
    }

  }

  @Override
  public boolean existProjectExpectedStudyLink(long projectExpectedStudyLinkID) {

    return projectExpectedStudyLinkDAO.existProjectExpectedStudyLink(projectExpectedStudyLinkID);
  }

  @Override
  public List<ProjectExpectedStudyLink> findAll() {

    return projectExpectedStudyLinkDAO.findAll();

  }

  @Override
  public List<ProjectExpectedStudyLink> getAllStudyLinksByStudy(Long studyId) {
    return this.projectExpectedStudyLinkDAO.getAllStudyLinksByStudy(studyId.longValue());
  }

  @Override
  public ProjectExpectedStudyLink getProjectExpectedStudyLinkById(long projectExpectedStudyLinkID) {

    return projectExpectedStudyLinkDAO.find(projectExpectedStudyLinkID);
  }

  @Override
  public ProjectExpectedStudyLink getProjectExpectedStudyLinkByPhase(Long expectedID, String link, Long phaseID) {

    return projectExpectedStudyLinkDAO.getProjectExpectedStudyLinkByPhase(expectedID, link, phaseID);
  }

  public void saveExpectedStudyLinkPhase(Phase next, long expectedID,
    ProjectExpectedStudyLink projectExpectedStudyLink) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudyLink> projectExpectedStudyLinks = phase.getProjectExpectedStudyLinks().stream()
      .filter(c -> c.getProjectExpectedStudy().getId().longValue() == expectedID
        && c.getLink().equals(projectExpectedStudyLink.getLink()))
      .collect(Collectors.toList());

    if (projectExpectedStudyLinks.isEmpty()) {
      ProjectExpectedStudyLink projectExpectedStudyLinkAdd = new ProjectExpectedStudyLink();
      projectExpectedStudyLinkAdd.setProjectExpectedStudy(projectExpectedStudyLink.getProjectExpectedStudy());
      projectExpectedStudyLinkAdd.setPhase(phase);
      projectExpectedStudyLinkAdd.setLink(projectExpectedStudyLink.getLink());
      projectExpectedStudyLinkDAO.save(projectExpectedStudyLinkAdd);
    } else {
      ProjectExpectedStudyLink projectExpectedStudyLinkAdd = new ProjectExpectedStudyLink();
      projectExpectedStudyLinkAdd.setProjectExpectedStudy(projectExpectedStudyLink.getProjectExpectedStudy());
      projectExpectedStudyLinkAdd.setPhase(phase);
      projectExpectedStudyLinkAdd.setLink(projectExpectedStudyLink.getLink());
      projectExpectedStudyLinkDAO.save(projectExpectedStudyLinkAdd);

      for (ProjectExpectedStudyLink projectExpectedStudyLinkDel : projectExpectedStudyLinks) {
        try {
          projectExpectedStudyLinkDAO.deleteProjectExpectedStudyLink(projectExpectedStudyLinkDel.getId());
        } catch (Exception e) {
          // TODO: handle exception
        }
      }
    }


    if (phase.getNext() != null) {
      this.saveExpectedStudyLinkPhase(phase.getNext(), expectedID, projectExpectedStudyLink);
    }
  }

  @Override
  public ProjectExpectedStudyLink saveProjectExpectedStudyLink(ProjectExpectedStudyLink projectExpectedStudyLink) {


    ProjectExpectedStudyLink projectExpectedStudyLinkResult =
      projectExpectedStudyLinkDAO.save(projectExpectedStudyLink);
    Phase currentPhase = projectExpectedStudyLinkResult.getPhase();

    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (currentPhase.getNext() != null) {
        this.saveExpectedStudyLinkPhase(currentPhase.getNext(),
          projectExpectedStudyLink.getProjectExpectedStudy().getId(), projectExpectedStudyLink);
      }
    }

    if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveExpectedStudyLinkPhase(upkeepPhase, projectExpectedStudyLink.getProjectExpectedStudy().getId(),
            projectExpectedStudyLink);
        }
      }
    }


    return projectExpectedStudyLinkResult;
  }
}
