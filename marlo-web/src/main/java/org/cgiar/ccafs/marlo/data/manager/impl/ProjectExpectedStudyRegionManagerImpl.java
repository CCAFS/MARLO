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


import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyRegionDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyRegionManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyRegion;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudyRegionManagerImpl implements ProjectExpectedStudyRegionManager {


  private ProjectExpectedStudyRegionDAO projectExpectedStudyRegionDAO;
  // Managers
  private PhaseDAO phaseDAO;


  @Inject
  public ProjectExpectedStudyRegionManagerImpl(ProjectExpectedStudyRegionDAO projectExpectedStudyRegionDAO,
    PhaseDAO phaseDAO) {
    this.projectExpectedStudyRegionDAO = projectExpectedStudyRegionDAO;
    this.phaseDAO = phaseDAO;


  }

  @Override
  public void deleteProjectExpectedStudyRegion(long projectExpectedStudyRegionId) {


    ProjectExpectedStudyRegion projectExpectedStudyRegion =
      this.getProjectExpectedStudyRegionById(projectExpectedStudyRegionId);
    Phase currentPhase = projectExpectedStudyRegion.getPhase();


    if (currentPhase.getNext() != null) {
      this.deleteProjectExpectedStudyRegionPhase(currentPhase.getNext(),
        projectExpectedStudyRegion.getProjectExpectedStudy().getId(), projectExpectedStudyRegion);
    }


    projectExpectedStudyRegionDAO.deleteProjectExpectedStudyRegion(projectExpectedStudyRegionId);
  }

  public void deleteProjectExpectedStudyRegionPhase(Phase next, long expectedID,
    ProjectExpectedStudyRegion projectExpectedStudyRegion) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudyRegion> projectExpectedStudyRegions = phase.getProjectExpectedStudyRegions().stream()
      .filter(c -> c.isActive() && c.getProjectExpectedStudy().getId().longValue() == expectedID
        && c.getLocElement().getId().equals(projectExpectedStudyRegion.getLocElement().getId()))
      .collect(Collectors.toList());
    for (ProjectExpectedStudyRegion projectExpectedStudyRegionDB : projectExpectedStudyRegions) {
      projectExpectedStudyRegionDAO.deleteProjectExpectedStudyRegion(projectExpectedStudyRegionDB.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectExpectedStudyRegionPhase(phase.getNext(), expectedID, projectExpectedStudyRegion);
    }
  }

  @Override
  public boolean existProjectExpectedStudyRegion(long projectExpectedStudyRegionID) {

    return projectExpectedStudyRegionDAO.existProjectExpectedStudyRegion(projectExpectedStudyRegionID);
  }

  @Override
  public List<ProjectExpectedStudyRegion> findAll() {

    return projectExpectedStudyRegionDAO.findAll();

  }

  @Override
  public ProjectExpectedStudyRegion getProjectExpectedStudyRegionById(long projectExpectedStudyRegionID) {

    return projectExpectedStudyRegionDAO.find(projectExpectedStudyRegionID);
  }


  @Override
  public List<ProjectExpectedStudyRegion> getProjectExpectedStudyRegionbyPhase(long expectedID, long phaseID) {
    return projectExpectedStudyRegionDAO.getProjectExpectedStudyRegionbyPhase(expectedID, phaseID);
  }

  public void saveExpectedStudyRegionPhase(Phase next, long expectedID,
    ProjectExpectedStudyRegion projectExpectedStudyRegion) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudyRegion> projectExpectedStudyRegions = phase.getProjectExpectedStudyRegions().stream()
      .filter(c -> c.getProjectExpectedStudy().getId().longValue() == expectedID
        && c.getLocElement().getId().equals(projectExpectedStudyRegion.getLocElement().getId()))
      .collect(Collectors.toList());

    if (projectExpectedStudyRegions.isEmpty()) {
      ProjectExpectedStudyRegion projectExpectedStudyRegionAdd = new ProjectExpectedStudyRegion();
      projectExpectedStudyRegionAdd.setProjectExpectedStudy(projectExpectedStudyRegion.getProjectExpectedStudy());
      projectExpectedStudyRegionAdd.setPhase(phase);
      projectExpectedStudyRegionAdd.setLocElement(projectExpectedStudyRegion.getLocElement());
      projectExpectedStudyRegionDAO.save(projectExpectedStudyRegionAdd);
    }


    if (phase.getNext() != null) {
      this.saveExpectedStudyRegionPhase(phase.getNext(), expectedID, projectExpectedStudyRegion);
    }
  }

  @Override
  public ProjectExpectedStudyRegion
    saveProjectExpectedStudyRegion(ProjectExpectedStudyRegion projectExpectedStudyRegion) {

    ProjectExpectedStudyRegion region = projectExpectedStudyRegionDAO.save(projectExpectedStudyRegion);
    Phase currentPhase = region.getPhase();


    if (currentPhase.getNext() != null) {
      this.saveExpectedStudyRegionPhase(currentPhase.getNext(), region.getProjectExpectedStudy().getId(),
        projectExpectedStudyRegion);
    }


    return region;
  }


}
