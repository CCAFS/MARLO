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
import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationImpactAreaDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationImpactAreaManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationImpactArea;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectInnovationImpactAreaManagerImpl implements ProjectInnovationImpactAreaManager {

  private ProjectInnovationImpactAreaDAO projectInnovationImpactAreaDAO;
  private PhaseDAO phaseDAO;
  // Managers

  @Inject
  public ProjectInnovationImpactAreaManagerImpl(ProjectInnovationImpactAreaDAO projectInnovationImpactAreaDAO,
    PhaseDAO phaseDAO) {
    this.projectInnovationImpactAreaDAO = projectInnovationImpactAreaDAO;
    this.phaseDAO = phaseDAO;
  }

  @Override
  public void deleteProjectInnovationImpactArea(long projectInnovationImpactAreaId) {

    ProjectInnovationImpactArea projectInnovationImpactArea =
      this.getProjectInnovationImpactAreaById(projectInnovationImpactAreaId);

    // Conditions to Project Innovation Works In AR phase and Upkeep Phase
    if (projectInnovationImpactArea.getPhase().getDescription().equals(APConstants.PLANNING)
      && projectInnovationImpactArea.getPhase().getNext() != null) {
      this.deleteProjectInnovationImpactAreaPhase(projectInnovationImpactArea.getPhase().getNext(),
        projectInnovationImpactArea.getProjectInnovation().getId(), projectInnovationImpactArea);
    }

    if (projectInnovationImpactArea.getPhase().getDescription().equals(APConstants.REPORTING)
      && projectInnovationImpactArea.getPhase().getNext() != null
      && projectInnovationImpactArea.getPhase().getNext().getNext() != null) {
      Phase upkeepPhase = projectInnovationImpactArea.getPhase().getNext().getNext();
      if (upkeepPhase != null) {
        this.deleteProjectInnovationImpactAreaPhase(upkeepPhase,
          projectInnovationImpactArea.getProjectInnovation().getId(), projectInnovationImpactArea);
      }
    }

    projectInnovationImpactAreaDAO.deleteProjectInnovationImpactArea(projectInnovationImpactAreaId);
  }

  public void deleteProjectInnovationImpactAreaPhase(Phase next, long innovationID,
    ProjectInnovationImpactArea projectInnovationImpactArea) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectInnovationImpactArea> innovationImpactAreas =
      projectInnovationImpactAreaDAO.getProjectInnovationImpactAreaByInnovationAndPhase(innovationID, phase.getId())
        .stream().filter(c -> c.getImpactArea().getId().equals(projectInnovationImpactArea.getImpactArea().getId()))
        .collect(Collectors.toList());

    for (ProjectInnovationImpactArea projectInnovationImpactAreasDB : innovationImpactAreas) {
      projectInnovationImpactAreaDAO.deleteProjectInnovationImpactArea(projectInnovationImpactAreasDB.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectInnovationImpactAreaPhase(phase.getNext(), innovationID, projectInnovationImpactArea);
    }
  }

  @Override
  public boolean existProjectInnovationImpactArea(long projectInnovationImpactAreaID) {

    return projectInnovationImpactAreaDAO.existProjectInnovationImpactArea(projectInnovationImpactAreaID);
  }

  @Override
  public List<ProjectInnovationImpactArea> findAll() {

    return projectInnovationImpactAreaDAO.findAll();

  }

  @Override
  public ProjectInnovationImpactArea getProjectInnovationImpactAreaById(long projectInnovationImpactAreaID) {

    return projectInnovationImpactAreaDAO.find(projectInnovationImpactAreaID);
  }

  @Override
  public List<ProjectInnovationImpactArea> getProjectInnovationImpactAreaByInnovationAndPhase(long innovationID,
    long phaseID) {
    return projectInnovationImpactAreaDAO.getProjectInnovationImpactAreaByInnovationAndPhase(innovationID, phaseID);

  }

  @Override
  public ProjectInnovationImpactArea
    saveProjectInnovationImpactArea(ProjectInnovationImpactArea projectInnovationImpactArea) {

    ProjectInnovationImpactArea innovationImpactArea = projectInnovationImpactAreaDAO.save(projectInnovationImpactArea);
    Phase phase = phaseDAO.find(innovationImpactArea.getPhase().getId());

    // Conditions to Project Innovation Works In AR phase and Upkeep Phase
    if (phase.getDescription().equals(APConstants.PLANNING) && phase.getNext() != null) {
      this.saveProjectInnovationImpactAreasPhase(innovationImpactArea.getPhase().getNext(),
        innovationImpactArea.getProjectInnovation().getId(), projectInnovationImpactArea);
    }

    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (phase.getNext() != null && phase.getNext().getNext() != null) {
        Phase upkeepPhase = phase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveProjectInnovationImpactAreasPhase(upkeepPhase, innovationImpactArea.getProjectInnovation().getId(),
            projectInnovationImpactArea);
        }
      }
    }
    return innovationImpactArea;
  }

  private void saveProjectInnovationImpactAreasPhase(Phase next, Long innovationid,
    ProjectInnovationImpactArea projectInnovationImpactArea) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectInnovationImpactArea> innovationImpactAreas =

      projectInnovationImpactAreaDAO.getProjectInnovationImpactAreaByInnovationAndPhase(innovationid, phase.getId())
        .stream().filter(c -> c.getImpactArea().getId().equals(projectInnovationImpactArea.getImpactArea().getId()))
        .collect(Collectors.toList());

    if (innovationImpactAreas.isEmpty()) {
      ProjectInnovationImpactArea projectInnovationImpactAreaAdd = new ProjectInnovationImpactArea();
      projectInnovationImpactAreaAdd.setProjectInnovation(projectInnovationImpactArea.getProjectInnovation());
      projectInnovationImpactAreaAdd.setPhase(phase);
      projectInnovationImpactAreaAdd.setImpactArea(projectInnovationImpactArea.getImpactArea());
      projectInnovationImpactAreaDAO.save(projectInnovationImpactAreaAdd);
    }
    if (phase.getNext() != null) {
      this.saveProjectInnovationImpactAreasPhase(phase.getNext(), innovationid, projectInnovationImpactArea);
    }
  }

}
