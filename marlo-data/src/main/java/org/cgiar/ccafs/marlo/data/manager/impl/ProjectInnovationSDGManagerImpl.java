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
import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationSDGDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationSDGManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationSDG;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectInnovationSDGManagerImpl implements ProjectInnovationSDGManager {

  // Managers
  private ProjectInnovationSDGDAO projectInnovationSDGDAO;
  private PhaseDAO phaseDAO;


  @Inject
  public ProjectInnovationSDGManagerImpl(ProjectInnovationSDGDAO projectInnovationSDGDAO, PhaseDAO phaseDAO) {
    this.projectInnovationSDGDAO = projectInnovationSDGDAO;
    this.phaseDAO = phaseDAO;
  }

  @Override
  public void deleteProjectInnovationSDG(long projectInnovationSDGId) {
    ProjectInnovationSDG projectInnovationSDG = this.getProjectInnovationSDGById(projectInnovationSDGId);

    // Conditions to Project Innovation Works In AR phase and Upkeep Phase
    if (projectInnovationSDG.getPhase().getDescription().equals(APConstants.PLANNING)
      && projectInnovationSDG.getPhase().getNext() != null) {
      this.deleteProjectInnovationSDGPhase(projectInnovationSDG.getPhase().getNext(),
        projectInnovationSDG.getProjectInnovation().getId(), projectInnovationSDG);
    }

    if (projectInnovationSDG.getPhase().getDescription().equals(APConstants.REPORTING)) {
      if (projectInnovationSDG.getPhase().getNext() != null
        && projectInnovationSDG.getPhase().getNext().getNext() != null) {
        Phase upkeepPhase = projectInnovationSDG.getPhase().getNext().getNext();
        if (upkeepPhase != null) {
          this.deleteProjectInnovationSDGPhase(upkeepPhase, projectInnovationSDG.getProjectInnovation().getId(),
            projectInnovationSDG);
        }
      }
    }
    projectInnovationSDGDAO.deleteProjectInnovationSDG(projectInnovationSDGId);
  }

  public void deleteProjectInnovationSDGPhase(Phase next, long innovationID,
    ProjectInnovationSDG projectInnovationSDG) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectInnovationSDG> projectInnovationSDGs =
      projectInnovationSDGDAO.getProjectInnovationSDGByInnovationAndPhase(innovationID, phase.getId()).stream()
        .filter(c -> c.getSdg().getId().equals(projectInnovationSDG.getSdg().getId())).collect(Collectors.toList());

    for (ProjectInnovationSDG projectInnovationSDGDB : projectInnovationSDGs) {
      projectInnovationSDGDAO.deleteProjectInnovationSDG(projectInnovationSDGDB.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectInnovationSDGPhase(phase.getNext(), innovationID, projectInnovationSDG);
    }
  }

  @Override
  public boolean existProjectInnovationSDG(long projectInnovationSDGID) {

    return projectInnovationSDGDAO.existProjectInnovationSDG(projectInnovationSDGID);
  }

  @Override
  public List<ProjectInnovationSDG> findAll() {

    return projectInnovationSDGDAO.findAll();

  }

  @Override
  public ProjectInnovationSDG getProjectInnovationSDGById(long projectInnovationSDGID) {

    return projectInnovationSDGDAO.find(projectInnovationSDGID);
  }

  @Override
  public List<ProjectInnovationSDG> getProjectInnovationSDGByInnovationAndPhase(long innovationId, long phaseID) {
    return projectInnovationSDGDAO.getProjectInnovationSDGByInnovationAndPhase(innovationId, phaseID);
  }

  @Override
  public List<ProjectInnovationSDG> getProjectInnovationSDGByPhase(long phaseID) {
    return projectInnovationSDGDAO.getProjectInnovationSDGByPhase(phaseID);
  }

  public void saveInnovationSDGPhase(Phase next, long innovationID, ProjectInnovationSDG projectInnovationSDG) {

    Phase phase = phaseDAO.find(next.getId());

    List<ProjectInnovationSDG> projectInnovationSDGs =
      projectInnovationSDGDAO.getProjectInnovationSDGByInnovationAndPhase(innovationID, phase.getId()).stream()
        .filter(c -> c.getSdg().getId().equals(projectInnovationSDG.getSdg().getId())).collect(Collectors.toList());

    if (projectInnovationSDGs.isEmpty()) {
      ProjectInnovationSDG projectInnovationSDGAdd = new ProjectInnovationSDG();
      projectInnovationSDGAdd.setProjectInnovation(projectInnovationSDG.getProjectInnovation());
      projectInnovationSDGAdd.setPhase(phase);
      projectInnovationSDGAdd.setSdg(projectInnovationSDG.getSdg());
      projectInnovationSDGDAO.save(projectInnovationSDGAdd);
    }
    if (phase.getNext() != null) {
      this.saveInnovationSDGPhase(phase.getNext(), innovationID, projectInnovationSDG);
    }
  }


  @Override
  public ProjectInnovationSDG saveProjectInnovationSDG(ProjectInnovationSDG projectInnovationSDG) {
    ProjectInnovationSDG innovationSDG = projectInnovationSDGDAO.save(projectInnovationSDG);
    Phase phase = phaseDAO.find(innovationSDG.getPhase().getId());
    // Conditions to Project Innovation Works In AR phase and Upkeep Phase
    if (phase.getDescription().equals(APConstants.PLANNING) && phase.getNext() != null) {
      this.saveInnovationSDGPhase(innovationSDG.getPhase().getNext(), innovationSDG.getProjectInnovation().getId(),
        projectInnovationSDG);
    }
    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (phase.getNext() != null && phase.getNext().getNext() != null) {
        Phase upkeepPhase = phase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveInnovationSDGPhase(upkeepPhase, innovationSDG.getProjectInnovation().getId(), projectInnovationSDG);
        }
      }
    }
    return innovationSDG;
  }

}
