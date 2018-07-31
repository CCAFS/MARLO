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
import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudySubIdoDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudySubIdoManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySubIdo;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudySubIdoManagerImpl implements ProjectExpectedStudySubIdoManager {


  private ProjectExpectedStudySubIdoDAO projectExpectedStudySubIdoDAO;
  // Managers
  private PhaseDAO phaseDAO;


  @Inject
  public ProjectExpectedStudySubIdoManagerImpl(ProjectExpectedStudySubIdoDAO projectExpectedStudySubIdoDAO,
    PhaseDAO phaseDAO) {
    this.projectExpectedStudySubIdoDAO = projectExpectedStudySubIdoDAO;
    this.phaseDAO = phaseDAO;


  }

  @Override
  public void deleteProjectExpectedStudySubIdo(long projectExpectedStudySubIdoId) {

    ProjectExpectedStudySubIdo projectExpectedStudySubIdo =
      this.getProjectExpectedStudySubIdoById(projectExpectedStudySubIdoId);
    Phase currentPhase = projectExpectedStudySubIdo.getPhase();

    if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.deleteProjectExpectedStudySubIdoPhase(upkeepPhase,
            projectExpectedStudySubIdo.getProjectExpectedStudy().getId(), projectExpectedStudySubIdo);
        }
      }
    } else {
      if (currentPhase.getNext() != null) {
        this.deleteProjectExpectedStudySubIdoPhase(currentPhase.getNext(),
          projectExpectedStudySubIdo.getProjectExpectedStudy().getId(), projectExpectedStudySubIdo);
      }
    }

    projectExpectedStudySubIdoDAO.deleteProjectExpectedStudySubIdo(projectExpectedStudySubIdoId);
  }

  public void deleteProjectExpectedStudySubIdoPhase(Phase next, long expectedID,
    ProjectExpectedStudySubIdo projectExpectedStudySubIdo) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudySubIdo> projectExpectedStudySubIdos = phase.getProjectExpectedStudySubIdos().stream()
      .filter(c -> c.isActive() && c.getProjectExpectedStudy().getId().longValue() == expectedID
        && c.getSrfSubIdo().getId().equals(projectExpectedStudySubIdo.getSrfSubIdo().getId()))
      .collect(Collectors.toList());
    for (ProjectExpectedStudySubIdo projectExpectedStudySubIdoDB : projectExpectedStudySubIdos) {
      projectExpectedStudySubIdoDAO.deleteProjectExpectedStudySubIdo(projectExpectedStudySubIdoDB.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectExpectedStudySubIdoPhase(phase.getNext(), expectedID, projectExpectedStudySubIdo);
    }
  }

  @Override
  public boolean existProjectExpectedStudySubIdo(long projectExpectedStudySubIdoID) {

    return projectExpectedStudySubIdoDAO.existProjectExpectedStudySubIdo(projectExpectedStudySubIdoID);
  }

  @Override
  public List<ProjectExpectedStudySubIdo> findAll() {

    return projectExpectedStudySubIdoDAO.findAll();

  }

  @Override
  public ProjectExpectedStudySubIdo getProjectExpectedStudySubIdoById(long projectExpectedStudySubIdoID) {

    return projectExpectedStudySubIdoDAO.find(projectExpectedStudySubIdoID);
  }

  public void saveExpectedStudySubIdoPhase(Phase next, long expectedID,
    ProjectExpectedStudySubIdo projectExpectedStudySubIdo) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudySubIdo> projectExpectedStudySubIdos = phase.getProjectExpectedStudySubIdos().stream()
      .filter(c -> c.getProjectExpectedStudy().getId().longValue() == expectedID
        && c.getSrfSubIdo().getId().equals(projectExpectedStudySubIdo.getSrfSubIdo().getId()))
      .collect(Collectors.toList());

    if (projectExpectedStudySubIdos.isEmpty()) {
      ProjectExpectedStudySubIdo projectExpectedStudySubIdoAdd = new ProjectExpectedStudySubIdo();
      projectExpectedStudySubIdoAdd.setProjectExpectedStudy(projectExpectedStudySubIdo.getProjectExpectedStudy());
      projectExpectedStudySubIdoAdd.setPhase(phase);
      projectExpectedStudySubIdoAdd.setSrfSubIdo(projectExpectedStudySubIdo.getSrfSubIdo());
      projectExpectedStudySubIdoDAO.save(projectExpectedStudySubIdoAdd);
    }


    if (phase.getNext() != null) {
      this.saveExpectedStudySubIdoPhase(phase.getNext(), expectedID, projectExpectedStudySubIdo);
    }
  }

  @Override
  public ProjectExpectedStudySubIdo
    saveProjectExpectedStudySubIdo(ProjectExpectedStudySubIdo projectExpectedStudySubIdo) {

    ProjectExpectedStudySubIdo subIdo = projectExpectedStudySubIdoDAO.save(projectExpectedStudySubIdo);
    Phase currentPhase = subIdo.getPhase();

    if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveExpectedStudySubIdoPhase(upkeepPhase, subIdo.getProjectExpectedStudy().getId(),
            projectExpectedStudySubIdo);
        }
      }
    } else {
      if (currentPhase.getNext() != null) {
        this.saveExpectedStudySubIdoPhase(currentPhase.getNext(), subIdo.getProjectExpectedStudy().getId(),
          projectExpectedStudySubIdo);
      }
    }

    return subIdo;
  }


}
