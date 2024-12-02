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
import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationReferenceUrlDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationReferenceUrlManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationReferenceUrl;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectInnovationReferenceUrlManagerImpl implements ProjectInnovationReferenceUrlManager {


  private ProjectInnovationReferenceUrlDAO projectInnovationReferenceUrlDAO;
  private PhaseDAO phaseDAO;
  // Managers


  @Inject
  public ProjectInnovationReferenceUrlManagerImpl(ProjectInnovationReferenceUrlDAO projectInnovationReferenceUrlDAO,
    PhaseDAO phaseDAO) {
    this.projectInnovationReferenceUrlDAO = projectInnovationReferenceUrlDAO;
    this.phaseDAO = phaseDAO;
  }

  @Override
  public void deleteProjectInnovationReferenceUrl(long projectInnovationReferenceUrlId) {
    ProjectInnovationReferenceUrl projectInnovationReferenceUrl =
      this.getProjectInnovationReferenceUrlById(projectInnovationReferenceUrlId);
    Phase currentPhase = projectInnovationReferenceUrl.getPhase();

    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (currentPhase.getNext() != null) {
        this.deleteProjectInnovationReferenceUrlPhase(currentPhase.getNext(),
          projectInnovationReferenceUrl.getProjectInnovation().getId(), projectInnovationReferenceUrl);
      }
    }

    if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.deleteProjectInnovationReferenceUrlPhase(upkeepPhase,
            projectInnovationReferenceUrl.getProjectInnovation().getId(), projectInnovationReferenceUrl);
        }
      }
    }

    projectInnovationReferenceUrlDAO.deleteProjectInnovationReferenceUrl(projectInnovationReferenceUrlId);
  }

  public void deleteProjectInnovationReferenceUrlPhase(Phase next, long innovationID,
    ProjectInnovationReferenceUrl projectInnovationReferenceUrl) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectInnovationReferenceUrl> projectInnovationReferenceUrls =
      this.getProjectInnovationReferenceUrlByPhaseAndInnovation(innovationID, next.getId()).stream()
        .filter(
          c -> c != null && c.getId() != null && c.getReference().equals(projectInnovationReferenceUrl.getReference()))
        .collect(Collectors.toList());

    for (ProjectInnovationReferenceUrl projectInnovationReferenceUrlDel : projectInnovationReferenceUrls) {
      projectInnovationReferenceUrlDAO.deleteProjectInnovationReferenceUrl(projectInnovationReferenceUrlDel.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectInnovationReferenceUrlPhase(phase.getNext(), innovationID, projectInnovationReferenceUrl);
    }

  }

  @Override
  public boolean existProjectInnovationReferenceUrl(long projectInnovationReferenceUrlID) {

    return projectInnovationReferenceUrlDAO.existProjectInnovationReferenceUrl(projectInnovationReferenceUrlID);
  }

  @Override
  public List<ProjectInnovationReferenceUrl> findAll() {

    return projectInnovationReferenceUrlDAO.findAll();

  }

  @Override
  public ProjectInnovationReferenceUrl getProjectInnovationReferenceUrlById(long projectInnovationReferenceUrlID) {

    return projectInnovationReferenceUrlDAO.find(projectInnovationReferenceUrlID);
  }

  @Override
  public List<ProjectInnovationReferenceUrl> getProjectInnovationReferenceUrlByPhaseAndInnovation(long phaseID,
    long innovationID) {
    return projectInnovationReferenceUrlDAO.getProjectInnovationReferenceUrlByPhaseAndInnovation(phaseID, innovationID);
  }

  @Override
  public ProjectInnovationReferenceUrl
    saveProjectInnovationReferenceUrl(ProjectInnovationReferenceUrl projectInnovationReferenceUrl) {
    ProjectInnovationReferenceUrl projectInnovationReferenceUrlResult =
      projectInnovationReferenceUrlDAO.save(projectInnovationReferenceUrl);
    Phase currentPhase = projectInnovationReferenceUrlResult.getPhase();

    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (currentPhase.getNext() != null) {
        this.saveProjectInnovationReferenceUrlPhase(currentPhase.getNext(),
          projectInnovationReferenceUrl.getProjectInnovation().getId(), projectInnovationReferenceUrl);
      }
    }

    if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveProjectInnovationReferenceUrlPhase(upkeepPhase,
            projectInnovationReferenceUrl.getProjectInnovation().getId(), projectInnovationReferenceUrl);
        }
      }
    }
    return projectInnovationReferenceUrlResult;
  }

  public void saveProjectInnovationReferenceUrlPhase(Phase next, long innovationID,
    ProjectInnovationReferenceUrl projectInnovationReferenceUrl) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectInnovationReferenceUrl> projectInnovationReferenceUrls =
      this.getProjectInnovationReferenceUrlByPhaseAndInnovation(innovationID, next.getId()).stream()
        .filter(
          c -> c != null && c.getId() != null && c.getReference().equals(projectInnovationReferenceUrl.getReference()))
        .collect(Collectors.toList());

    if (projectInnovationReferenceUrls.isEmpty()) {
      ProjectInnovationReferenceUrl projectInnovationReferenceUrlAdd = new ProjectInnovationReferenceUrl();
      projectInnovationReferenceUrlAdd.setProjectInnovation(projectInnovationReferenceUrl.getProjectInnovation());
      projectInnovationReferenceUrlAdd.setPhase(phase);
      projectInnovationReferenceUrlAdd.setReference(projectInnovationReferenceUrl.getReference());
      projectInnovationReferenceUrlAdd.setLink(projectInnovationReferenceUrl.getLink());
      projectInnovationReferenceUrlAdd
        .setEvidenceByDeliverable(projectInnovationReferenceUrl.getEvidenceByDeliverable());
      projectInnovationReferenceUrlAdd
        .setAdditionalArticleType(projectInnovationReferenceUrl.getAdditionalArticleType());
      projectInnovationReferenceUrlAdd.setDatasetType(projectInnovationReferenceUrl.getDatasetType());
      projectInnovationReferenceUrlAdd.setInnovationType(projectInnovationReferenceUrl.getInnovationType());
      projectInnovationReferenceUrlDAO.save(projectInnovationReferenceUrlAdd);
    }
    /*
     * else {
     * ProjectInnovationReferenceUrl projectInnovationReferenceUrlAdd = new ProjectInnovationReferenceUrl();
     * projectInnovationReferenceUrlAdd.setProjectInnovation(projectInnovationReferenceUrl.getProjectInnovation());
     * projectInnovationReferenceUrlAdd.setPhase(phase);
     * projectInnovationReferenceUrlAdd.setReference(projectInnovationReferenceUrl.getReference());
     * projectInnovationReferenceUrlAdd.setLink(projectInnovationReferenceUrl.getLink());
     * projectInnovationReferenceUrlAdd
     * .setEvidenceByDeliverable(projectInnovationReferenceUrl.getEvidenceByDeliverable());
     * projectInnovationReferenceUrlAdd
     * .setAdditionalArticleType(projectInnovationReferenceUrl.getAdditionalArticleType());
     * projectInnovationReferenceUrlAdd.setDatasetType(projectInnovationReferenceUrl.getDatasetType());
     * projectInnovationReferenceUrlAdd.setInnovationType(projectInnovationReferenceUrl.getInnovationType());
     * projectInnovationReferenceUrlDAO.save(projectInnovationReferenceUrlAdd);
     * for (ProjectInnovationReferenceUrl projectInnovationReferenceUrlDel : projectInnovationReferenceUrls) {
     * try {
     * projectInnovationReferenceUrlDAO
     * .deleteProjectInnovationReferenceUrl(projectInnovationReferenceUrlDel.getId());
     * } catch (Exception e) {
     * // TODO: handle exception
     * }
     * }
     * }
     */
    if (phase.getNext() != null) {
      this.saveProjectInnovationReferenceUrlPhase(phase.getNext(), innovationID, projectInnovationReferenceUrl);
    }
  }

}
