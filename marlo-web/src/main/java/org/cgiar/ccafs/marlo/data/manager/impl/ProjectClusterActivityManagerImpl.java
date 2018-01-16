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
import org.cgiar.ccafs.marlo.data.dao.ProjectClusterActivityDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectClusterActivityManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectClusterActivity;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian Garcia
 */
@Named
public class ProjectClusterActivityManagerImpl implements ProjectClusterActivityManager {


  private ProjectClusterActivityDAO projectClusterActivityDAO;
  private PhaseDAO phaseDAO;

  // Managers


  @Inject
  public ProjectClusterActivityManagerImpl(ProjectClusterActivityDAO projectClusterActivityDAO, PhaseDAO phaseDAO) {
    this.projectClusterActivityDAO = projectClusterActivityDAO;
    this.phaseDAO = phaseDAO;


  }

  public void addProjectClusterPhase(Phase next, long projecID, ProjectClusterActivity projectCluster) {
    Phase phase = phaseDAO.find(next.getId());
    List<ProjectClusterActivity> clusters = phase.getProjectClusters().stream()
      .filter(c -> c.isActive() && c.getProject().getId().longValue() == projecID && projectCluster
        .getCrpClusterOfActivity().getId().longValue() == c.getCrpClusterOfActivity().getId().longValue())
      .collect(Collectors.toList());
    if ( clusters.isEmpty()) {

      ProjectClusterActivity projectClusterAdd = new ProjectClusterActivity();
      projectClusterAdd.setActive(true);
      projectClusterAdd.setActiveSince(projectCluster.getActiveSince());
      projectClusterAdd.setCreatedBy(projectCluster.getCreatedBy());
      projectClusterAdd.setCrpClusterOfActivity(projectCluster.getCrpClusterOfActivity());
      projectClusterAdd.setModificationJustification(projectCluster.getModificationJustification());
      projectClusterAdd.setModifiedBy(projectCluster.getModifiedBy());
      projectClusterAdd.setPhase(phase);
      projectClusterAdd.setProject(projectCluster.getProject());
      this.projectClusterActivityDAO.save(projectClusterAdd);
    }

    if (phase.getNext() != null) {
      this.addProjectClusterPhase(phase.getNext(), projecID, projectCluster);
    }


  }

  @Override
  public void deleteProjectClusterActivity(long projectClusterActivityId) {

    ProjectClusterActivity projectClusterActivity = this.getProjectClusterActivityById(projectClusterActivityId);
    projectClusterActivity.setActive(false);
    this.saveProjectClusterActivity(projectClusterActivity);

    if (projectClusterActivity.getPhase().getNext() != null) {
      this.deletProjectClusterPhase(projectClusterActivity.getPhase().getNext(),
        projectClusterActivity.getProject().getId(), projectClusterActivity);
    }
  }

  public void deletProjectClusterPhase(Phase next, long projecID, ProjectClusterActivity projectClusterActivity) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectClusterActivity> clusters = phase.getProjectClusters().stream()
      .filter(c -> c.isActive() && c.getProject().getId().longValue() == projecID && projectClusterActivity
        .getCrpClusterOfActivity().getId().longValue() == c.getCrpClusterOfActivity().getId().longValue())
      .collect(Collectors.toList());
    for (ProjectClusterActivity clusterActivity : clusters) {
      this.deleteProjectClusterActivity(clusterActivity.getId());
    }

    if (phase.getNext() != null) {
      this.deletProjectClusterPhase(phase.getNext(), projecID, projectClusterActivity);
    }


  }

  @Override
  public boolean existProjectClusterActivity(long projectClusterActivityID) {

    return projectClusterActivityDAO.existProjectClusterActivity(projectClusterActivityID);
  }

  @Override
  public List<ProjectClusterActivity> findAll() {

    return projectClusterActivityDAO.findAll();

  }

  @Override
  public ProjectClusterActivity getProjectClusterActivityById(long projectClusterActivityID) {

    return projectClusterActivityDAO.find(projectClusterActivityID);
  }

  @Override
  public ProjectClusterActivity saveProjectClusterActivity(ProjectClusterActivity projectClusterActivity) {

    ProjectClusterActivity projectClusterActivityDB = projectClusterActivityDAO.save(projectClusterActivity);
    if (projectClusterActivity.getPhase().getNext() != null) {
      this.addProjectClusterPhase(projectClusterActivity.getPhase().getNext(),
        projectClusterActivity.getProject().getId(), projectClusterActivity);
    }
    return projectClusterActivityDB;
  }

}
