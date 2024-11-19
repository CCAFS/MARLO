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
import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyImpactAreaDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyImpactAreaManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyImpactArea;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudyImpactAreaManagerImpl implements ProjectExpectedStudyImpactAreaManager {


  private ProjectExpectedStudyImpactAreaDAO projectExpectedStudyImpactAreaDAO;
  private PhaseDAO phaseDAO;
  // Managers


  @Inject
  public ProjectExpectedStudyImpactAreaManagerImpl(ProjectExpectedStudyImpactAreaDAO projectExpectedStudyImpactAreaDAO,
    PhaseDAO phaseDAO) {
    this.projectExpectedStudyImpactAreaDAO = projectExpectedStudyImpactAreaDAO;
    this.phaseDAO = phaseDAO;


  }

  @Override
  public void deleteProjectExpectedStudyImpactArea(long projectExpectedStudyImpactAreaId) {

    ProjectExpectedStudyImpactArea projectExpectedStudyImpactArea =
      this.getProjectExpectedStudyImpactAreaById(projectExpectedStudyImpactAreaId);
    Phase currentPhase = projectExpectedStudyImpactArea.getPhase();

    if (currentPhase.getDescription().equals(APConstants.PLANNING) && currentPhase.getNext() != null) {
      this.deleteProjectExpectedStudyImpactAreaPhase(currentPhase.getNext(), projectExpectedStudyImpactArea);
    }

    if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.deleteProjectExpectedStudyImpactAreaPhase(upkeepPhase.getNext(), projectExpectedStudyImpactArea);
        }
      }
    }


    projectExpectedStudyImpactAreaDAO.deleteProjectExpectedStudyImpactArea(projectExpectedStudyImpactAreaId);
  }


  public void deleteProjectExpectedStudyImpactAreaPhase(Phase next,
    ProjectExpectedStudyImpactArea projectExpectedStudyImpactArea) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudyImpactArea> projectExpectedStudyImpactAreaList =
      phase.getProjectExpectedStudyImpactAreas().stream()
        .filter(c -> c.isActive()
          && c.getProjectExpectedStudy().getId() == projectExpectedStudyImpactArea.getProjectExpectedStudy().getId()
          && c.getImpactArea().getId() == projectExpectedStudyImpactArea.getImpactArea().getId())
        .collect(Collectors.toList());
    for (ProjectExpectedStudyImpactArea projectExpectedStudyImpactAreaTmp : projectExpectedStudyImpactAreaList) {
      projectExpectedStudyImpactAreaDAO.deleteProjectExpectedStudyImpactArea(projectExpectedStudyImpactAreaTmp.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectExpectedStudyImpactAreaPhase(phase.getNext(), projectExpectedStudyImpactArea);
    }
  }

  @Override
  public boolean existProjectExpectedStudyImpactArea(long projectExpectedStudyImpactAreaID) {

    return projectExpectedStudyImpactAreaDAO.existProjectExpectedStudyImpactArea(projectExpectedStudyImpactAreaID);
  }

  @Override
  public List<ProjectExpectedStudyImpactArea> findAll() {

    return projectExpectedStudyImpactAreaDAO.findAll();

  }

  @Override
  public ProjectExpectedStudyImpactArea findAllByStudyAndAreaAndPhase(long expectedId, long impactAreaId,
    long phaseId) {

    return projectExpectedStudyImpactAreaDAO.findAllByStudyAndAreaAndPhase(expectedId, impactAreaId, phaseId);

  }

  @Override
  public ProjectExpectedStudyImpactArea getProjectExpectedStudyImpactAreaById(long projectExpectedStudyImpactAreaID) {

    return projectExpectedStudyImpactAreaDAO.find(projectExpectedStudyImpactAreaID);
  }

  /**
   * Reply the information to the next Phases
   * 
   * @param next - The next Phase
   * @param projectExpectedStudyImpactArea - The project expected study ImpactArea into the
   *        database.
   */
  public void saveInfoPhase(Phase next, ProjectExpectedStudyImpactArea projectExpectedStudyImpactArea) {

    Phase phase = phaseDAO.find(next.getId());
    List<ProjectExpectedStudyImpactArea> projectExpectedStudyImpactAreaList =
      phase.getProjectExpectedStudyImpactAreas().stream()
        .filter(c -> c.isActive()
          && c.getProjectExpectedStudy().getId() == projectExpectedStudyImpactArea.getProjectExpectedStudy().getId()
          && c.getImpactArea().getId() == projectExpectedStudyImpactArea.getImpactArea().getId())
        .collect(Collectors.toList());
    if (projectExpectedStudyImpactAreaList.isEmpty()) {

      ProjectExpectedStudyImpactArea projectExpectedStudyImpactAreaAdd = new ProjectExpectedStudyImpactArea();

      projectExpectedStudyImpactAreaAdd
        .setProjectExpectedStudy(projectExpectedStudyImpactArea.getProjectExpectedStudy());
      projectExpectedStudyImpactAreaAdd.setPhase(phase);
      projectExpectedStudyImpactAreaAdd.setImpactArea(projectExpectedStudyImpactArea.getImpactArea());


      projectExpectedStudyImpactAreaDAO.save(projectExpectedStudyImpactAreaAdd);
    }

    if (phase.getNext() != null) {
      this.saveInfoPhase(phase.getNext(), projectExpectedStudyImpactArea);
    }
  }

  @Override
  public ProjectExpectedStudyImpactArea
    saveProjectExpectedStudyImpactArea(ProjectExpectedStudyImpactArea projectExpectedStudyImpactArea) {
    ProjectExpectedStudyImpactArea sourceInfo = projectExpectedStudyImpactAreaDAO.save(projectExpectedStudyImpactArea);
    Phase phase = phaseDAO.find(sourceInfo.getPhase().getId());

    if (phase.getDescription().equals(APConstants.PLANNING)) {
      if (phase.getNext() != null) {
        this.saveInfoPhase(phase.getNext(), projectExpectedStudyImpactArea);
      }
    }

    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (phase.getNext() != null && phase.getNext().getNext() != null) {
        Phase upkeepPhase = phase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveInfoPhase(upkeepPhase, projectExpectedStudyImpactArea);
        }
      }
    }


    return sourceInfo;
  }


}
