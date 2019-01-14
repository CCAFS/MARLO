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
import org.cgiar.ccafs.marlo.data.dao.ProjectLp6ContributionDeliverableDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectLp6ContributionDeliverableManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectLp6ContributionDeliverable;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectLp6ContributionDeliverableManagerImpl implements ProjectLp6ContributionDeliverableManager {


  private ProjectLp6ContributionDeliverableDAO projectLp6ContributionDeliverableDAO;
  // Managers
  private PhaseDAO phaseDAO;


  @Inject
  public ProjectLp6ContributionDeliverableManagerImpl(
    ProjectLp6ContributionDeliverableDAO projectLp6ContributionDeliverableDAO, PhaseDAO phaseDAO) {
    this.projectLp6ContributionDeliverableDAO = projectLp6ContributionDeliverableDAO;
    this.phaseDAO = phaseDAO;
  }


  @Override
  public void deleteProjectLp6ContributionDeliverable(long projectLp6ContributionDeliverableID) {
    ProjectLp6ContributionDeliverable projectLp6ContributionDeliverable =
      this.getProjectLp6ContributionDeliverableById(projectLp6ContributionDeliverableID);

    if (projectLp6ContributionDeliverable.getPhase().getNext() != null) {
      this.deleteProjectLp6ContributionDeliverablePhase(projectLp6ContributionDeliverable.getPhase().getNext(),
        projectLp6ContributionDeliverable.getProjectLp6Contribution().getId(), projectLp6ContributionDeliverable);
    }

    projectLp6ContributionDeliverableDAO.deleteProjectLp6ContributionDeliverable(projectLp6ContributionDeliverableID);
  }

  public void deleteProjectLp6ContributionDeliverablePhase(Phase next, long projectLp6ContributionDeliverableID,
    ProjectLp6ContributionDeliverable projectLp6ContributionDeliverable) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectLp6ContributionDeliverable> projectLp6ContributionDeliverables =
      phase.getProjectLp6ContributionDeliverables().stream()
        .filter(
          c -> c.isActive() && c.getProjectLp6Contribution().getId().longValue() == projectLp6ContributionDeliverableID
            && c.getDeliverable().getId().equals(projectLp6ContributionDeliverable.getDeliverable().getId()))
        .collect(Collectors.toList());

    for (ProjectLp6ContributionDeliverable projectLp6ContributionDeliverableDB : projectLp6ContributionDeliverables) {
      projectLp6ContributionDeliverableDAO
        .deleteProjectLp6ContributionDeliverable(projectLp6ContributionDeliverableDB.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectLp6ContributionDeliverablePhase(phase.getNext(), projectLp6ContributionDeliverableID,
        projectLp6ContributionDeliverable);
    }
  }


  @Override
  public boolean existProjectLp6ContributionDeliverable(long projectLp6ContributionDeliverableID) {
    return projectLp6ContributionDeliverableDAO
      .existProjectLp6ContributionDeliverable(projectLp6ContributionDeliverableID);

  }


  @Override
  public List<ProjectLp6ContributionDeliverable> findAll() {
    return projectLp6ContributionDeliverableDAO.findAll();

  }


  @Override
  public ProjectLp6ContributionDeliverable
    getProjectLp6ContributionDeliverableById(long projectLp6ContributionDeliverableID) {
    return projectLp6ContributionDeliverableDAO.find(projectLp6ContributionDeliverableID);

  }

  @Override
  public ProjectLp6ContributionDeliverable
    saveProjectLp6ContributionDeliverable(ProjectLp6ContributionDeliverable projectLp6ContributionDeliverable) {

    ProjectLp6ContributionDeliverable deliverable =
      projectLp6ContributionDeliverableDAO.save(projectLp6ContributionDeliverable);

    Phase phase = phaseDAO.find(deliverable.getPhase().getId());
    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (deliverable.getPhase().getNext() != null) {
        this.saveProjectLp6ContributionDeliverablePhase(deliverable.getPhase().getNext(),
          deliverable.getProjectLp6Contribution().getId(), projectLp6ContributionDeliverable);
      }
    }
    return deliverable;

  }


  public void saveProjectLp6ContributionDeliverablePhase(Phase next, long projectLp6ContributionDeliverableID,
    ProjectLp6ContributionDeliverable projectLp6ContributionDeliverable) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectLp6ContributionDeliverable> projectLp6ContributionDeliverables =
      phase.getProjectLp6ContributionDeliverables().stream()
        .filter(c -> c.getProjectLp6Contribution().getId().longValue() == projectLp6ContributionDeliverableID
          && c.getDeliverable().getId().equals(projectLp6ContributionDeliverable.getDeliverable().getId()))
        .collect(Collectors.toList());

    if (projectLp6ContributionDeliverables.isEmpty()) {
      ProjectLp6ContributionDeliverable projectLp6ContributionDeliverableAdd = new ProjectLp6ContributionDeliverable();
      projectLp6ContributionDeliverableAdd
        .setProjectLp6Contribution(projectLp6ContributionDeliverable.getProjectLp6Contribution());
      projectLp6ContributionDeliverableAdd.setPhase(phase);
      projectLp6ContributionDeliverableAdd.setDeliverable(projectLp6ContributionDeliverable.getDeliverable());
      projectLp6ContributionDeliverableDAO.save(projectLp6ContributionDeliverableAdd);
    }


    if (phase.getNext() != null) {
      this.saveProjectLp6ContributionDeliverablePhase(phase.getNext(), projectLp6ContributionDeliverableID,
        projectLp6ContributionDeliverable);
    }
  }


}
