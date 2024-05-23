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

/**************
 * @author Diego Perez - CIAT/CCAFS
 **************/

package org.cgiar.ccafs.marlo.data.manager.impl;

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationCenterDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationCenterManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCenter;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class ProjectInnovationCenterManagerImpl implements ProjectInnovationCenterManager {

  private ProjectInnovationCenterDAO projectInnovationCenterDAO;
  // Managers
  private PhaseDAO phaseDAO;

  @Inject
  public ProjectInnovationCenterManagerImpl(ProjectInnovationCenterDAO projectInnovationCenterDAO, PhaseDAO phaseDAO) {
    this.projectInnovationCenterDAO = projectInnovationCenterDAO;
    this.phaseDAO = phaseDAO;
  }

  @Override
  public void deleteProjectInnovationCenter(long projectInnovationCenterId) {
    ProjectInnovationCenter projectInnovationCenter = this.getProjectInnovationCenterById(projectInnovationCenterId);

    // Conditions to Project Innovation Works In AR phase and Upkeep Phase
    if (projectInnovationCenter.getPhase().getDescription().equals(APConstants.PLANNING)
      && projectInnovationCenter.getPhase().getNext() != null) {
      this.deleteProjectInnovationCenterPhase(projectInnovationCenter.getPhase().getNext(),
        projectInnovationCenter.getProjectInnovation().getId(), projectInnovationCenter);
    }

    if (projectInnovationCenter.getPhase().getDescription().equals(APConstants.REPORTING)) {
      if (projectInnovationCenter.getPhase().getNext() != null
        && projectInnovationCenter.getPhase().getNext().getNext() != null) {
        Phase upkeepPhase = projectInnovationCenter.getPhase().getNext().getNext();
        if (upkeepPhase != null) {
          this.deleteProjectInnovationCenterPhase(upkeepPhase, projectInnovationCenter.getProjectInnovation().getId(),
            projectInnovationCenter);
        }
      }
    }

    projectInnovationCenterDAO.deleteProjectInnovationCenter(projectInnovationCenterId);

  }

  public void deleteProjectInnovationCenterPhase(Phase next, long innovationID,
    ProjectInnovationCenter projectInnovationCenter) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectInnovationCenter> projectInnovationCenters = projectInnovationCenterDAO.findAll().stream()
      .filter(c -> c.isActive() && c.getPhase().getId().longValue() == phase.getId().longValue()
        && c.getProjectInnovation().getId().longValue() == innovationID
        && c.getInstitution().getId().equals(projectInnovationCenter.getInstitution().getId()))
      .collect(Collectors.toList());

    for (ProjectInnovationCenter projectInnovationCenterDB : projectInnovationCenters) {
      projectInnovationCenterDAO.deleteProjectInnovationCenter(projectInnovationCenterDB.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectInnovationCenterPhase(phase.getNext(), innovationID, projectInnovationCenter);
    }
  }

  @Override
  public boolean existProjectInnovationCenter(long projectInnovationCenterID) {
    return projectInnovationCenterDAO.existProjectInnovationCenter(projectInnovationCenterID);
  }

  @Override
  public List<ProjectInnovationCenter> findAll() {
    return projectInnovationCenterDAO.findAll();
  }

  @Override
  public List<ProjectInnovationCenter> findAllByInsitutionAndPhase(long institutionId, long phaseId) {
    return projectInnovationCenterDAO.findAllByInsitutionAndPhase(institutionId, phaseId);
  }

  @Override
  public ProjectInnovationCenter getProjectInnovationCenterById(long projectInnovationCenterID) {
    return projectInnovationCenterDAO.find(projectInnovationCenterID);
  }

  @Override
  public ProjectInnovationCenter getProjectInnovationCenterById(long innovationid, long globalUnitID, long phaseID) {
    return projectInnovationCenterDAO.getProjectInnovationCenterById(innovationid, globalUnitID, phaseID);
  }

  public void saveInnovationCenterPhase(Phase next, long innovationid,
    ProjectInnovationCenter projectInnovationCenter) {

    Phase phase = phaseDAO.find(next.getId());

    List<ProjectInnovationCenter> projectInnovatioCenters =
      projectInnovationCenterDAO.findAll().stream()
        .filter(c -> c.getProjectInnovation().getId().longValue() == innovationid
          && c.getPhase().getId().equals(phase.getId())
          && c.getInstitution().getId().equals(projectInnovationCenter.getInstitution().getId()))
        .collect(Collectors.toList());

    if (projectInnovatioCenters.isEmpty()) {
      ProjectInnovationCenter projectInnovationCenterAdd = new ProjectInnovationCenter();
      projectInnovationCenterAdd.setProjectInnovation(projectInnovationCenter.getProjectInnovation());
      projectInnovationCenterAdd.setPhase(phase);
      projectInnovationCenterAdd.setInstitution(projectInnovationCenter.getInstitution());
      projectInnovationCenterDAO.save(projectInnovationCenterAdd);
    }
    if (phase.getNext() != null) {
      this.saveInnovationCenterPhase(phase.getNext(), innovationid, projectInnovationCenter);
    }
  }

  @Override
  public ProjectInnovationCenter saveProjectInnovationCenter(ProjectInnovationCenter projectInnovationCenter) {
    ProjectInnovationCenter center = projectInnovationCenterDAO.save(projectInnovationCenter);
    Phase phase = phaseDAO.find(center.getPhase().getId());
    // Conditions to Project Innovation Works In AR phase and Upkeep Phase
    if (phase.getDescription().equals(APConstants.PLANNING) && phase.getNext() != null) {
      this.saveInnovationCenterPhase(center.getPhase().getNext(), center.getProjectInnovation().getId(),
        projectInnovationCenter);
    }
    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (phase.getNext() != null && phase.getNext().getNext() != null) {
        Phase upkeepPhase = phase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveInnovationCenterPhase(upkeepPhase, center.getProjectInnovation().getId(), projectInnovationCenter);
        }
      }
    }
    return center;
  }

}
