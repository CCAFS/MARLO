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
import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyCenterDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyCenterManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCenter;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class ProjectExpectedStudyCenterManagerImpl implements ProjectExpectedStudyCenterManager {

  private ProjectExpectedStudyCenterDAO projectExpectedStudyCenterDAO;
  private PhaseDAO phaseDAO;

  @Inject
  public ProjectExpectedStudyCenterManagerImpl(ProjectExpectedStudyCenterDAO projectExpectedStudyCenterDAO,
    PhaseDAO phaseDAO) {
    this.projectExpectedStudyCenterDAO = projectExpectedStudyCenterDAO;
    this.phaseDAO = phaseDAO;
  }

  @Override
  public void deleteProjectExpectedStudyCenter(long projectExpectedStudyCenterId) {
    ProjectExpectedStudyCenter projectExpectedStudyCenter =
      this.getProjectExpectedStudyCenterById(projectExpectedStudyCenterId);
    Phase currentPhase = projectExpectedStudyCenter.getPhase();

    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (currentPhase.getNext() != null) {
        this.deleteProjectExpectedStudyCenterPhase(currentPhase.getNext(),
          projectExpectedStudyCenter.getProjectExpectedStudy().getId(), projectExpectedStudyCenter);
      }
    }

    if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.deleteProjectExpectedStudyCenterPhase(upkeepPhase,
            projectExpectedStudyCenter.getProjectExpectedStudy().getId(), projectExpectedStudyCenter);
        }
      }
    }
    projectExpectedStudyCenterDAO.deleteProjectExpectedStudyCenter(projectExpectedStudyCenterId);
  }

  public void deleteProjectExpectedStudyCenterPhase(Phase next, long expectedID,
    ProjectExpectedStudyCenter projectExpectedStudyCenter) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudyCenter> projectExpectedStudyCenters = projectExpectedStudyCenterDAO.findAll().stream()
      .filter(c -> c.isActive() && c.getPhase().getId().longValue() == phase.getId().longValue()
        && c.getProjectExpectedStudy().getId().longValue() == expectedID
        && c.getInstitution().getId().equals(projectExpectedStudyCenter.getInstitution().getId()))
      .collect(Collectors.toList());
    for (ProjectExpectedStudyCenter projectExpectedStudyCenterDB : projectExpectedStudyCenters) {
      projectExpectedStudyCenterDAO.deleteProjectExpectedStudyCenter(projectExpectedStudyCenterDB.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectExpectedStudyCenterPhase(phase.getNext(), expectedID, projectExpectedStudyCenter);
    }
  }

  @Override
  public boolean existProjectExpectedStudyCenter(long projectExpectedStudyCenterID) {
    return projectExpectedStudyCenterDAO.existProjectExpectedStudyCenter(projectExpectedStudyCenterID);
  }

  @Override
  public List<ProjectExpectedStudyCenter> findAll() {
    return projectExpectedStudyCenterDAO.findAll();
  }

  @Override
  public List<ProjectExpectedStudyCenter> findAllByInsituttionAndPhase(long institutionId, long phaseId) {
    return projectExpectedStudyCenterDAO.findAllByInsituttionAndPhase(institutionId, phaseId);
  }

  @Override
  public ProjectExpectedStudyCenter getProjectExpectedStudyCenterById(long projectExpectedStudyCenterID) {
    return projectExpectedStudyCenterDAO.find(projectExpectedStudyCenterID);
  }

  public void saveExpectedStudyCenterPhase(Phase next, long expectedID,
    ProjectExpectedStudyCenter projectExpectedStudyCenter) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudyCenter> projectExpectedStudyCenters = projectExpectedStudyCenterDAO.findAll().stream()
      .filter(c -> c.getPhase().getId().longValue() == phase.getId().longValue()
        && c.getProjectExpectedStudy().getId().longValue() == expectedID
        && c.getInstitution().getId().equals(projectExpectedStudyCenter.getInstitution().getId()))
      .collect(Collectors.toList());

    if (projectExpectedStudyCenters.isEmpty()) {
      ProjectExpectedStudyCenter projectExpectedStudyCenterAdd = new ProjectExpectedStudyCenter();
      projectExpectedStudyCenterAdd.setProjectExpectedStudy(projectExpectedStudyCenter.getProjectExpectedStudy());
      projectExpectedStudyCenterAdd.setPhase(phase);
      projectExpectedStudyCenterAdd.setInstitution(projectExpectedStudyCenter.getInstitution());
      projectExpectedStudyCenterDAO.save(projectExpectedStudyCenterAdd);
    }
    if (phase.getNext() != null) {
      this.saveExpectedStudyCenterPhase(phase.getNext(), expectedID, projectExpectedStudyCenter);
    }
  }

  @Override
  public ProjectExpectedStudyCenter
    saveProjectExpectedStudyCenter(ProjectExpectedStudyCenter projectExpectedStudyCenter) {
    ProjectExpectedStudyCenter projectExpectedStudyCenterResult =
      projectExpectedStudyCenterDAO.save(projectExpectedStudyCenter);
    Phase currentPhase = projectExpectedStudyCenterResult.getPhase();

    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (currentPhase.getNext() != null) {
        this.saveExpectedStudyCenterPhase(currentPhase.getNext(),
          projectExpectedStudyCenterResult.getProjectExpectedStudy().getId(), projectExpectedStudyCenter);
      }
    }

    if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveExpectedStudyCenterPhase(upkeepPhase,
            projectExpectedStudyCenterResult.getProjectExpectedStudy().getId(), projectExpectedStudyCenter);
        }
      }
    }

    return projectExpectedStudyCenterResult;
  }
}
