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
import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationBundleDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationBundleManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationBundle;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectInnovationBundleManagerImpl implements ProjectInnovationBundleManager {


  private ProjectInnovationBundleDAO projectInnovationBundleDAO;
  private PhaseDAO phaseDAO;
  // Managers


  @Inject
  public ProjectInnovationBundleManagerImpl(ProjectInnovationBundleDAO projectInnovationBundleDAO, PhaseDAO phaseDAO) {
    this.projectInnovationBundleDAO = projectInnovationBundleDAO;
    this.phaseDAO = phaseDAO;
  }

  @Override
  public void deleteProjectInnovationBundle(long projectInnovationBundleId) {
    ProjectInnovationBundle projectInnovationBundle = this.getProjectInnovationBundleById(projectInnovationBundleId);

    // Conditions to Project Innovation Works In AR phase and Upkeep Phase
    if (projectInnovationBundle.getPhase().getDescription().equals(APConstants.PLANNING)
      && projectInnovationBundle.getPhase().getNext() != null) {
      this.deleteProjectInnovationBundlePhase(projectInnovationBundle.getPhase().getNext(),
        projectInnovationBundle.getProjectInnovation().getId(), projectInnovationBundle);
    }

    if (projectInnovationBundle.getPhase().getDescription().equals(APConstants.REPORTING)
      && projectInnovationBundle.getPhase().getNext() != null
      && projectInnovationBundle.getPhase().getNext().getNext() != null) {
      Phase upkeepPhase = projectInnovationBundle.getPhase().getNext().getNext();
      if (upkeepPhase != null) {
        this.deleteProjectInnovationBundlePhase(upkeepPhase, projectInnovationBundle.getProjectInnovation().getId(),
          projectInnovationBundle);
      }
    }
    projectInnovationBundleDAO.deleteProjectInnovationBundle(projectInnovationBundleId);
  }

  public void deleteProjectInnovationBundlePhase(Phase next, long innovationID,
    ProjectInnovationBundle projectInnovationBundle) {

    Phase phase = phaseDAO.find(next.getId());

    List<ProjectInnovationBundle> innovationBundles = Optional
      .ofNullable(
        projectInnovationBundleDAO.getProjectInnovationBundleByInnovationAndPhase(innovationID, phase.getId()))
      .orElse(Collections.emptyList()).stream().filter(c -> {
        ProjectInnovation selA = c.getSelectedInnovation();
        ProjectInnovation selB = projectInnovationBundle.getSelectedInnovation();
        return selA != null && selA.getId() != null && selB != null && selB.getId() != null
          && selA.getId().equals(selB.getId());
      }).collect(Collectors.toList());

    for (ProjectInnovationBundle projectInnovationBundlesDB : innovationBundles) {
      if (projectInnovationBundlesDB != null && projectInnovationBundlesDB.getId() != null) {
        projectInnovationBundleDAO.deleteProjectInnovationBundle(projectInnovationBundlesDB.getId());
      }
    }

    if (phase.getNext() != null) {
      this.deleteProjectInnovationBundlePhase(phase.getNext(), innovationID, projectInnovationBundle);
    }
  }

  @Override
  public boolean existProjectInnovationBundle(long projectInnovationBundleID) {

    return projectInnovationBundleDAO.existProjectInnovationBundle(projectInnovationBundleID);
  }

  @Override
  public List<ProjectInnovationBundle> findAll() {

    return projectInnovationBundleDAO.findAll();

  }

  @Override
  public ProjectInnovationBundle getProjectInnovationBundleById(long projectInnovationBundleID) {

    return projectInnovationBundleDAO.find(projectInnovationBundleID);
  }

  @Override
  public List<ProjectInnovationBundle> getProjectInnovationBundleByInnovationAndPhase(long innovationID, long phaseID) {
    return projectInnovationBundleDAO.getProjectInnovationBundleByInnovationAndPhase(innovationID, phaseID);
  }

  @Override
  public ProjectInnovationBundle saveProjectInnovationBundle(ProjectInnovationBundle projectInnovationBundle) {

    ProjectInnovationBundle innovationBundle = projectInnovationBundleDAO.save(projectInnovationBundle);
    Phase phase = phaseDAO.find(projectInnovationBundle.getPhase().getId());

    if (phase.getDescription().equals(APConstants.PLANNING) && phase.getNext() != null) {
      this.saveProjectInnovationBundlePhase(innovationBundle.getPhase().getNext(),
        innovationBundle.getProjectInnovation().getId(), innovationBundle);
    }

    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (phase.getNext() != null && phase.getNext().getNext() != null) {
        Phase upkeepPhase = phase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveProjectInnovationBundlePhase(upkeepPhase, innovationBundle.getProjectInnovation().getId(),
            innovationBundle);
        }
      }
    }
    return innovationBundle;
  }

  private void saveProjectInnovationBundlePhase(Phase next, Long innovationID,
    ProjectInnovationBundle projectInnovationBundle) {

    Phase phase = phaseDAO.find(next.getId());

    List<ProjectInnovationBundle> innovationBundles = Optional
      .ofNullable(
        projectInnovationBundleDAO.getProjectInnovationBundleByInnovationAndPhase(innovationID, phase.getId()))
      .orElse(Collections.emptyList()).stream().filter(c -> {
        ProjectInnovation selA = c.getSelectedInnovation();
        ProjectInnovation selB = projectInnovationBundle.getSelectedInnovation();
        return selA != null && selA.getId() != null && selB != null && selB.getId() != null
          && selA.getId().equals(selB.getId());
      }).collect(Collectors.toList());


    if (innovationBundles.isEmpty()) {
      ProjectInnovationBundle projectInnovationBundleAdd = new ProjectInnovationBundle();
      projectInnovationBundleAdd.setProjectInnovation(projectInnovationBundle.getProjectInnovation());
      projectInnovationBundleAdd.setPhase(phase);
      projectInnovationBundleAdd.setSelectedInnovation(projectInnovationBundle.getSelectedInnovation());
      projectInnovationBundleDAO.save(projectInnovationBundleAdd);
    }
    if (phase.getNext() != null) {
      this.saveProjectInnovationBundlePhase(phase.getNext(), innovationID, projectInnovationBundle);
    }
  }

}
