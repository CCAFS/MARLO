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


import org.cgiar.ccafs.marlo.data.dao.ProjectLp6ContributionDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectLp6ContributionManager;
import org.cgiar.ccafs.marlo.data.model.ProjectLp6Contribution;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class ProjectLp6ContributionManagerImpl implements ProjectLp6ContributionManager {


  private ProjectLp6ContributionDAO projectLp6ContributionDAO;
  // Managers


  @Inject
  public ProjectLp6ContributionManagerImpl(ProjectLp6ContributionDAO projectLp6ContributionDAO) {
    this.projectLp6ContributionDAO = projectLp6ContributionDAO;
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
    return projectLp6ContributionDAO.save(projectLp6Contribution);
  }

}
