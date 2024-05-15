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


import org.cgiar.ccafs.marlo.data.dao.ProjectMilestoneDisaggregatedTargetDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectMilestoneDisaggregatedTargetManager;
import org.cgiar.ccafs.marlo.data.model.ProjectMilestoneDisaggregatedTarget;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectMilestoneDisaggregatedTargetManagerImpl implements ProjectMilestoneDisaggregatedTargetManager {


  private ProjectMilestoneDisaggregatedTargetDAO projectMilestoneDisaggregatedTargetDAO;
  // Managers


  @Inject
  public ProjectMilestoneDisaggregatedTargetManagerImpl(ProjectMilestoneDisaggregatedTargetDAO projectMilestoneDisaggregatedTargetDAO) {
    this.projectMilestoneDisaggregatedTargetDAO = projectMilestoneDisaggregatedTargetDAO;


  }

  @Override
  public void deleteProjectMilestoneDisaggregatedTarget(long projectMilestoneDisaggregatedTargetId) {

    projectMilestoneDisaggregatedTargetDAO.deleteProjectMilestoneDisaggregatedTarget(projectMilestoneDisaggregatedTargetId);
  }

  @Override
  public boolean existProjectMilestoneDisaggregatedTarget(long projectMilestoneDisaggregatedTargetID) {

    return projectMilestoneDisaggregatedTargetDAO.existProjectMilestoneDisaggregatedTarget(projectMilestoneDisaggregatedTargetID);
  }

  @Override
  public List<ProjectMilestoneDisaggregatedTarget> findAll() {

    return projectMilestoneDisaggregatedTargetDAO.findAll();

  }

  @Override
  public ProjectMilestoneDisaggregatedTarget getProjectMilestoneDisaggregatedTargetById(long projectMilestoneDisaggregatedTargetID) {

    return projectMilestoneDisaggregatedTargetDAO.find(projectMilestoneDisaggregatedTargetID);
  }

  @Override
  public ProjectMilestoneDisaggregatedTarget saveProjectMilestoneDisaggregatedTarget(ProjectMilestoneDisaggregatedTarget projectMilestoneDisaggregatedTarget) {

    return projectMilestoneDisaggregatedTargetDAO.save(projectMilestoneDisaggregatedTarget);
  }


}
