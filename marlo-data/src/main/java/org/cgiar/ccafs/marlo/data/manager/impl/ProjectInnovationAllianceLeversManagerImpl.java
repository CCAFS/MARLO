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
import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationAllianceLeversDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationAllianceLeversManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationAllianceLevers;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectInnovationAllianceLeversManagerImpl implements ProjectInnovationAllianceLeversManager {


  private ProjectInnovationAllianceLeversDAO projectInnovationAllianceLeversDAO;
  private PhaseDAO phaseDAO;

  @Inject
  public ProjectInnovationAllianceLeversManagerImpl(
    ProjectInnovationAllianceLeversDAO projectInnovationAllianceLeversDAO, PhaseDAO phaseDAO) {
    this.projectInnovationAllianceLeversDAO = projectInnovationAllianceLeversDAO;
    this.phaseDAO = phaseDAO;
  }

  @Override
  public void deleteProjectInnovationAllianceLevers(long projectInnovationAllianceLeversId) {

    ProjectInnovationAllianceLevers projectInnovationAllianceLever =
      this.getProjectInnovationAllianceLeversById(projectInnovationAllianceLeversId);

    // Conditions to Project Innovation Works In AR phase and Upkeep Phase
    if (projectInnovationAllianceLever.getPhase().getDescription().equals(APConstants.PLANNING)
      && projectInnovationAllianceLever.getPhase().getNext() != null) {
      this.deleteProjectInnovationAllianceLeversPhase(projectInnovationAllianceLever.getPhase().getNext(),
        projectInnovationAllianceLever.getProjectInnovation().getId(), projectInnovationAllianceLever);
    }

    if (projectInnovationAllianceLever.getPhase().getDescription().equals(APConstants.REPORTING)) {
      if (projectInnovationAllianceLever.getPhase().getNext() != null
        && projectInnovationAllianceLever.getPhase().getNext().getNext() != null) {
        Phase upkeepPhase = projectInnovationAllianceLever.getPhase().getNext().getNext();
        if (upkeepPhase != null) {
          this.deleteProjectInnovationAllianceLeversPhase(upkeepPhase,
            projectInnovationAllianceLever.getProjectInnovation().getId(), projectInnovationAllianceLever);
        }
      }
    }
    projectInnovationAllianceLeversDAO.deleteProjectInnovationAllianceLevers(projectInnovationAllianceLeversId);
  }

  public void deleteProjectInnovationAllianceLeversPhase(Phase next, long innovationID,
    ProjectInnovationAllianceLevers projectInnovationAllianceLevers) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectInnovationAllianceLevers> innovationAllianceLevers = projectInnovationAllianceLeversDAO
      .getProjectInnovationAllianceLeversByInnovationAndPhase(innovationID, phase.getId()).stream()
      .filter(c -> c.getAllianceLever().getId().equals(projectInnovationAllianceLevers.getAllianceLever().getId()))
      .collect(Collectors.toList());

    for (ProjectInnovationAllianceLevers projectInnovationAllianceLeversDB : innovationAllianceLevers) {
      projectInnovationAllianceLeversDAO
        .deleteProjectInnovationAllianceLevers(projectInnovationAllianceLeversDB.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectInnovationAllianceLeversPhase(phase.getNext(), innovationID, projectInnovationAllianceLevers);
    }
  }

  @Override
  public boolean existProjectInnovationAllianceLevers(long projectInnovationAllianceLeversID) {

    return projectInnovationAllianceLeversDAO.existProjectInnovationAllianceLevers(projectInnovationAllianceLeversID);
  }

  @Override
  public List<ProjectInnovationAllianceLevers> findAll() {

    return projectInnovationAllianceLeversDAO.findAll();

  }

  @Override
  public ProjectInnovationAllianceLevers
    getProjectInnovationAllianceLeversById(long projectInnovationAllianceLeversID) {

    return projectInnovationAllianceLeversDAO.find(projectInnovationAllianceLeversID);
  }

  @Override
  public List<ProjectInnovationAllianceLevers> getProjectInnovationAllianceLeversByInnovationAndPhase(long innovationId,
    long phaseID) {
    return projectInnovationAllianceLeversDAO.getProjectInnovationAllianceLeversByInnovationAndPhase(innovationId,
      phaseID);
  }

  @Override
  public List<ProjectInnovationAllianceLevers> getProjectInnovationAllianceLeversByPhase(long phaseID) {
    return projectInnovationAllianceLeversDAO.getProjectInnovationAllianceLeversByPhase(phaseID);
  }

  @Override
  public ProjectInnovationAllianceLevers
    saveProjectInnovationAllianceLevers(ProjectInnovationAllianceLevers projectInnovationAllianceLevers) {

    ProjectInnovationAllianceLevers innovationAllianceLever =
      projectInnovationAllianceLeversDAO.save(projectInnovationAllianceLevers);
    Phase phase = phaseDAO.find(innovationAllianceLever.getPhase().getId());

    // Conditions to Project Innovation Works In AR phase and Upkeep Phase
    if (phase.getDescription().equals(APConstants.PLANNING) && phase.getNext() != null) {
      this.saveProjectInnovationAllianceLeversPhase(innovationAllianceLever.getPhase().getNext(),
        innovationAllianceLever.getProjectInnovation().getId(), projectInnovationAllianceLevers);
    }

    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (phase.getNext() != null && phase.getNext().getNext() != null) {
        Phase upkeepPhase = phase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveProjectInnovationAllianceLeversPhase(upkeepPhase,
            innovationAllianceLever.getProjectInnovation().getId(), projectInnovationAllianceLevers);
        }
      }
    }
    return innovationAllianceLever;
  }

  private void saveProjectInnovationAllianceLeversPhase(Phase next, Long innovationid,
    ProjectInnovationAllianceLevers projectInnovationAllianceLevers) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectInnovationAllianceLevers> innovationAllianceLevers = projectInnovationAllianceLeversDAO
      .getProjectInnovationAllianceLeversByInnovationAndPhase(innovationid, phase.getId()).stream()
      .filter(c -> c.getAllianceLever().getId().equals(projectInnovationAllianceLevers.getAllianceLever().getId()))
      .collect(Collectors.toList());

    if (innovationAllianceLevers.isEmpty()) {
      ProjectInnovationAllianceLevers projectInnovationAllianceLeversAdd = new ProjectInnovationAllianceLevers();
      projectInnovationAllianceLeversAdd.setProjectInnovation(projectInnovationAllianceLevers.getProjectInnovation());
      projectInnovationAllianceLeversAdd.setPhase(phase);
      projectInnovationAllianceLeversAdd.setAllianceLever(projectInnovationAllianceLevers.getAllianceLever());
      projectInnovationAllianceLeversAdd.setId(null);
      projectInnovationAllianceLeversDAO.save(projectInnovationAllianceLeversAdd);
    }
    if (phase.getNext() != null) {
      this.saveProjectInnovationAllianceLeversPhase(phase.getNext(), innovationid, projectInnovationAllianceLevers);
    }
  }

}