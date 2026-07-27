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
import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationToolCategoryDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationToolCategoryManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationToolCategory;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author CCAFS
 */
@Named
public class ProjectInnovationToolCategoryManagerImpl implements ProjectInnovationToolCategoryManager {

  private PhaseDAO phaseDAO;
  private ProjectInnovationToolCategoryDAO projectInnovationToolCategoryDAO;
  // Managers

  @Inject
  public ProjectInnovationToolCategoryManagerImpl(ProjectInnovationToolCategoryDAO projectInnovationToolCategoryDAO,
    PhaseDAO phaseDAO) {
    this.projectInnovationToolCategoryDAO = projectInnovationToolCategoryDAO;
    this.phaseDAO = phaseDAO;
  }

  @Override
  @Transactional
  public void deleteProjectInnovationToolCategory(long projectInnovationToolCategorysId) {

    ProjectInnovationToolCategory projectInnovationToolCategory =
      this.getProjectInnovationToolCategoryById(projectInnovationToolCategorysId);

    // Conditions to Project Innovation Works In AR phase and Upkeep Phase
    if (projectInnovationToolCategory.getPhase().getDescription().equals(APConstants.PLANNING)
      && projectInnovationToolCategory.getPhase().getNext() != null) {
      this.deleteProjectInnovationToolCategoryPhase(projectInnovationToolCategory.getPhase().getNext(),
        projectInnovationToolCategory.getProjectInnovation().getId(), projectInnovationToolCategory);
    }

    if (projectInnovationToolCategory.getPhase().getDescription().equals(APConstants.REPORTING)) {
      if (projectInnovationToolCategory.getPhase().getNext() != null
        && projectInnovationToolCategory.getPhase().getNext().getNext() != null) {
        Phase upkeepPhase = projectInnovationToolCategory.getPhase().getNext().getNext();
        if (upkeepPhase != null) {
          this.deleteProjectInnovationToolCategoryPhase(upkeepPhase,
            projectInnovationToolCategory.getProjectInnovation().getId(), projectInnovationToolCategory);
        }
      }
    }
    projectInnovationToolCategoryDAO.deleteProjectInnovationToolCategory(projectInnovationToolCategorysId);
  }

  @Transactional
  public void deleteProjectInnovationToolCategoryPhase(Phase next, long innovationID,
    ProjectInnovationToolCategory projectInnovationToolCategorys) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectInnovationToolCategory> innovationToolCategorys = projectInnovationToolCategoryDAO
      .getProjectInnovationToolCategoryByInnovationAndPhase(innovationID, phase.getId()).stream()
      .filter(c -> c.getToolCategory().getId().equals(projectInnovationToolCategorys.getToolCategory().getId()))
      .collect(Collectors.toList());

    for (ProjectInnovationToolCategory projectInnovationToolCategorysDB : innovationToolCategorys) {
      projectInnovationToolCategoryDAO.deleteProjectInnovationToolCategory(projectInnovationToolCategorysDB.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectInnovationToolCategoryPhase(phase.getNext(), innovationID, projectInnovationToolCategorys);
    }
  }

  @Override
  public boolean existProjectInnovationToolCategory(long projectInnovationToolCategoryID) {

    return projectInnovationToolCategoryDAO.existProjectInnovationToolCategory(projectInnovationToolCategoryID);
  }

  @Override
  public List<ProjectInnovationToolCategory> findAll() {

    return projectInnovationToolCategoryDAO.findAll();

  }

  @Override
  public ProjectInnovationToolCategory getProjectInnovationToolCategoryById(long projectInnovationToolCategoryID) {

    return projectInnovationToolCategoryDAO.find(projectInnovationToolCategoryID);
  }

  @Override
  public List<ProjectInnovationToolCategory> getProjectInnovationToolCategoryByInnovationAndPhase(long innovationID,
    long phaseID) {
    return projectInnovationToolCategoryDAO.getProjectInnovationToolCategoryByInnovationAndPhase(innovationID, phaseID);
  }

  @Override
  public ProjectInnovationToolCategory
    saveProjectInnovationToolCategory(ProjectInnovationToolCategory projectInnovationToolCategorys) {

    ProjectInnovationToolCategory innovationToolCategory =
      projectInnovationToolCategoryDAO.save(projectInnovationToolCategorys);
    Phase phase = phaseDAO.find(innovationToolCategory.getPhase().getId());

    // Conditions to Project Innovation Works In AR phase and Upkeep Phase
    if (phase.getDescription().equals(APConstants.PLANNING) && phase.getNext() != null) {
      this.saveProjectInnovationToolCategoryPhase(innovationToolCategory.getPhase().getNext(),
        innovationToolCategory.getProjectInnovation().getId(), projectInnovationToolCategorys);
    }

    if (phase.getDescription().equals(APConstants.REPORTING) && phase.getNext() != null
      && phase.getNext().getNext() != null) {
      Phase upkeepPhase = phase.getNext().getNext();
      if (upkeepPhase != null) {
        this.saveProjectInnovationToolCategoryPhase(upkeepPhase, innovationToolCategory.getProjectInnovation().getId(),
          projectInnovationToolCategorys);
      }
    }
    return innovationToolCategory;
  }

  private void saveProjectInnovationToolCategoryPhase(Phase next, Long innovationID,
    ProjectInnovationToolCategory projectInnovationToolCategory) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectInnovationToolCategory> innovationToolCategorys =

      projectInnovationToolCategoryDAO.getProjectInnovationToolCategoryByInnovationAndPhase(innovationID, phase.getId())
        .stream()
        .filter(c -> c.getToolCategory().getId().equals(projectInnovationToolCategory.getToolCategory().getId()))
        .collect(Collectors.toList());

    if (innovationToolCategorys.isEmpty()) {
      ProjectInnovationToolCategory projectInnovationToolCategoryAdd = new ProjectInnovationToolCategory();
      projectInnovationToolCategoryAdd.setToolCategory(projectInnovationToolCategory.getToolCategory());
      projectInnovationToolCategoryAdd.setProjectInnovation(projectInnovationToolCategory.getProjectInnovation());
      projectInnovationToolCategoryAdd.setOtherNarrative(projectInnovationToolCategory.getOtherNarrative());
      projectInnovationToolCategoryAdd.setPhase(phase);
      projectInnovationToolCategoryDAO.save(projectInnovationToolCategoryAdd);

    }
    if (phase.getNext() != null) {
      this.saveProjectInnovationToolCategoryPhase(phase.getNext(), innovationID, projectInnovationToolCategory);
    }
  }
}
