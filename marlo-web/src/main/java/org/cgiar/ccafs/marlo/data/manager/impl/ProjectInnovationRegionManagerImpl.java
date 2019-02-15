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
import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationRegionDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationRegionManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationRegion;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectInnovationRegionManagerImpl implements ProjectInnovationRegionManager {


  private ProjectInnovationRegionDAO projectInnovationRegionDAO;
  // Managers
  private PhaseDAO phaseDAO;


  @Inject
  public ProjectInnovationRegionManagerImpl(ProjectInnovationRegionDAO projectInnovationRegionDAO, PhaseDAO phaseDAO) {
    this.projectInnovationRegionDAO = projectInnovationRegionDAO;
    this.phaseDAO = phaseDAO;


  }

  @Override
  public void deleteProjectInnovationRegion(long projectInnovationRegionId) {

    ProjectInnovationRegion projectInnovationRegion = this.getProjectInnovationRegionById(projectInnovationRegionId);

    if (projectInnovationRegion.getPhase().getNext() != null) {
      this.deleteProjectInnovationRegionPhase(projectInnovationRegion.getPhase().getNext(),
        projectInnovationRegion.getProjectInnovation().getId(), projectInnovationRegion);
    }

    projectInnovationRegionDAO.deleteProjectInnovationRegion(projectInnovationRegionId);
  }

  public void deleteProjectInnovationRegionPhase(Phase next, long innovationID,
    ProjectInnovationRegion projectInnovationRegion) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectInnovationRegion> projectInnovationRegions = phase.getProjectInnovationRegions().stream()
      .filter(c -> c.isActive() && c.getProjectInnovation().getId().longValue() == innovationID
        && c.getLocElement().getId().equals(projectInnovationRegion.getLocElement().getId()))
      .collect(Collectors.toList());
    for (ProjectInnovationRegion projectInnovationRegionDB : projectInnovationRegions) {
      projectInnovationRegionDAO.deleteProjectInnovationRegion(projectInnovationRegionDB.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectInnovationRegionPhase(phase.getNext(), innovationID, projectInnovationRegion);
    }
  }

  @Override
  public boolean existProjectInnovationRegion(long projectInnovationRegionID) {

    return projectInnovationRegionDAO.existProjectInnovationRegion(projectInnovationRegionID);
  }

  @Override
  public List<ProjectInnovationRegion> findAll() {

    return projectInnovationRegionDAO.findAll();

  }

  @Override
  public List<ProjectInnovationRegion> getInnovationRegionbyPhase(long innovationID, long phaseID) {
    return projectInnovationRegionDAO.getInnovationRegionbyPhase(innovationID, phaseID);
  }

  @Override
  public ProjectInnovationRegion getProjectInnovationRegionById(long projectInnovationRegionID) {

    return projectInnovationRegionDAO.find(projectInnovationRegionID);
  }

  public void saveInnovationRegionPhase(Phase next, long innovationid,
    ProjectInnovationRegion projectInnovationRegion) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectInnovationRegion> projectInnovationRegions = phase.getProjectInnovationRegions().stream()
      .filter(c -> c.getProjectInnovation().getId().longValue() == innovationid
        && c.getLocElement().getId().equals(projectInnovationRegion.getLocElement().getId()))
      .collect(Collectors.toList());

    if (projectInnovationRegions.isEmpty()) {
      ProjectInnovationRegion projectInnovationRegionAdd = new ProjectInnovationRegion();
      projectInnovationRegionAdd.setProjectInnovation(projectInnovationRegion.getProjectInnovation());
      projectInnovationRegionAdd.setPhase(phase);
      projectInnovationRegionAdd.setLocElement(projectInnovationRegion.getLocElement());
      projectInnovationRegionDAO.save(projectInnovationRegionAdd);
    }


    if (phase.getNext() != null) {
      this.saveInnovationRegionPhase(phase.getNext(), innovationid, projectInnovationRegion);
    }
  }

  @Override
  public ProjectInnovationRegion saveProjectInnovationRegion(ProjectInnovationRegion projectInnovationRegion) {

    ProjectInnovationRegion region = projectInnovationRegionDAO.save(projectInnovationRegion);

    Phase phase = phaseDAO.find(region.getPhase().getId());
    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (region.getPhase().getNext() != null) {
        this.saveInnovationRegionPhase(region.getPhase().getNext(), region.getProjectInnovation().getId(),
          projectInnovationRegion);
      }
    }
    return region;
  }


}
