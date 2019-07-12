/*****************************************************************
 * This file is part of Managing Agricultural Research for Learning &
 * Outcomes Platform (MARLO).
 * MARLO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * MARLO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty ofs
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with MARLO. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectLp6ContributionDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectLp6ContributionManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectLp6Contribution;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class ProjectLp6ContributionManagerImpl implements ProjectLp6ContributionManager {


  private ProjectLp6ContributionDAO projectLp6ContributionDAO;
  private ProjectDAO projectDAO;
  // Managers
  private PhaseDAO phaseDAO;


  @Inject
  public ProjectLp6ContributionManagerImpl(ProjectLp6ContributionDAO projectLp6ContributionDAO, PhaseDAO phaseDAO,
    ProjectDAO projectDAO) {
    this.projectLp6ContributionDAO = projectLp6ContributionDAO;
    this.phaseDAO = phaseDAO;
    this.projectDAO = projectDAO;
  }

  @Override
  public void deleteProjectLp6Contribution(long projectLp6ContributionId) {
    projectLp6ContributionDAO.deleteProjectLp6Contribution(projectLp6ContributionId);
  }

  @Override
  public boolean existProjectLp6Contribution(long projectLp6ContributionId) {
    return projectLp6ContributionDAO.existProjectLp6Contribution(projectLp6ContributionId);
  }

  @Override
  public List<ProjectLp6Contribution> findAll() {
    return projectLp6ContributionDAO.findAll();
  }

  @Override
  public ProjectLp6Contribution getProjectLp6ContributionById(long projectLp6ContributionId) {
    return projectLp6ContributionDAO.find(projectLp6ContributionId);
  }

  @Override
  public ProjectLp6Contribution saveProjectLp6Contribution(ProjectLp6Contribution projectLp6Contribution) {

    ProjectLp6Contribution projectLp6ContributionSave = projectLp6ContributionDAO.save(projectLp6Contribution);

    if (projectLp6ContributionSave.getPhase().getNext() != null) {
      this.saveProjectLp6ContributionPhase(projectLp6ContributionSave.getPhase().getNext(),
        projectLp6ContributionSave.getProject().getId(), projectLp6Contribution);
    }
    return projectLp6ContributionSave;

  }

  public void saveProjectLp6ContributionPhase(Phase next, long projectID,
    ProjectLp6Contribution projectLp6Contribution) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectLp6Contribution> projectLp6Contributions = phase.getProjectLp6Contributions().stream()
      .filter(c -> c.isActive() && c.getProject().getId().equals(projectID)).collect(Collectors.toList());

    if (projectLp6Contributions.isEmpty()) {
      ProjectLp6Contribution projectLp6ContributionAdd = new ProjectLp6Contribution();
      projectLp6ContributionAdd.setProject(projectDAO.find(projectID));
      projectLp6ContributionAdd.updateProjectLp6Contribution(projectLp6Contribution, phase);
      projectLp6ContributionDAO.save(projectLp6ContributionAdd);
    } else {
      for (ProjectLp6Contribution projectLp6ContributionPhase : projectLp6Contributions) {
        projectLp6ContributionPhase.updateProjectLp6Contribution(projectLp6Contribution, phase);
        projectLp6ContributionDAO.save(projectLp6ContributionPhase);
      }
    }

    if (phase.getNext() != null) {
      this.saveProjectLp6ContributionPhase(phase.getNext(), projectID, projectLp6Contribution);
    }
  }

}
