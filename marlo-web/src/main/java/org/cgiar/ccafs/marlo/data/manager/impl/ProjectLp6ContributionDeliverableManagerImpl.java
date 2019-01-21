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
  public List<ProjectLp6ContributionDeliverable>
    getProjectLp6ContributionDeliverablebyPhase(long projectLp6ContributionID, long phaseID) {
    return projectLp6ContributionDeliverableDAO.getProjectLp6ContributionDeliverablebyPhase(projectLp6ContributionID,
      phaseID);
  }


  @Override
  public ProjectLp6ContributionDeliverable
    saveProjectLp6ContributionDeliverable(ProjectLp6ContributionDeliverable projectLp6ContributionDeliverable) {
    ProjectLp6ContributionDeliverable deliverable =
      projectLp6ContributionDeliverableDAO.save(projectLp6ContributionDeliverable);
    /*
     * if (deliverable.getPhase().getNext() != null) {
     * this.saveProjectLp6ContributionDeliverablePhase(deliverable.getPhase().getNext(),
     * deliverable.getProjectLp6Contribution().getId(), projectLp6ContributionDeliverable);
     * }
     */
    return deliverable;

  }


  public void saveProjectLp6ContributionDeliverablePhase(Phase next, long projectLp6ContributionID,
    ProjectLp6ContributionDeliverable projectLp6ContributionDeliverable) {
    Phase phase = phaseDAO.find(next.getId());
    /*
     * if (phase.getProjectLp6ContributionDeliverables() != null) {
     * System.out.println("largo  phase.getProjectLp6ContributionDeliverables(): "
     * + phase.getProjectLp6ContributionDeliverables().size());
     * }
     * List<ProjectLp6ContributionDeliverable> projectLp6ContributionDeliverables =
     * phase.getProjectLp6ContributionDeliverables().stream()
     * .filter(c -> c.getProjectLp6Contribution().getId().longValue() == projectLp6ContributionID
     * && c.getDeliverable().getId().equals(projectLp6ContributionDeliverable.getDeliverable().getId()))
     * .collect(Collectors.toList());
     */
    List<ProjectLp6ContributionDeliverable> projectLp6ContributionDeliverables2 =
      projectLp6ContributionDeliverableDAO.findAll().stream()
        .filter(
          c -> c.getPhase() == phase && c.getProjectLp6Contribution().getId().longValue() == projectLp6ContributionID
            && c.getDeliverable().getId().equals(projectLp6ContributionDeliverable.getDeliverable().getId()))
        .collect(Collectors.toList());

    /*
     * if (projectLp6ContributionDeliverables2 != null) {
     * System.out.println("lista 2 largo " + projectLp6ContributionDeliverables2.size());
     * System.out.println("projectLp6ContributionID " + projectLp6ContributionID
     * + " projectLp6ContributionDeliverable.getDeliverable().getId(): "
     * + projectLp6ContributionDeliverable.getDeliverable().getId() + " phase " + phase);
     * System.out.println("projectLp6ContributionDeliverables2.getProjectLp6Contribution().getId() "
     * + projectLp6ContributionDeliverables2.get(0).getId()
     * + " projectLp6ContributionDeliverable2.getDeliverable().getId(): "
     * + projectLp6ContributionDeliverables2.get(0).getDeliverable().getId() + " phase "
     * + projectLp6ContributionDeliverables2.get(0).getPhase());
     * }
     */
    if (projectLp6ContributionDeliverables2.isEmpty()) {
      ProjectLp6ContributionDeliverable projectLp6ContributionDeliverableAdd = new ProjectLp6ContributionDeliverable();
      projectLp6ContributionDeliverableAdd
        .setProjectLp6Contribution(projectLp6ContributionDeliverable.getProjectLp6Contribution());
      projectLp6ContributionDeliverableAdd.setPhase(phase);
      projectLp6ContributionDeliverableAdd.setDeliverable(projectLp6ContributionDeliverable.getDeliverable());
      projectLp6ContributionDeliverableAdd.setId(projectLp6ContributionDeliverable.getId());
      projectLp6ContributionDeliverableAdd.setModifiedBy(projectLp6ContributionDeliverable.getModifiedBy());
      projectLp6ContributionDeliverableDAO.save(projectLp6ContributionDeliverableAdd);
    }


    if (phase.getNext() != null) {
      this.saveProjectLp6ContributionDeliverablePhase(phase.getNext(), projectLp6ContributionID,
        projectLp6ContributionDeliverable);
    }
  }


}
