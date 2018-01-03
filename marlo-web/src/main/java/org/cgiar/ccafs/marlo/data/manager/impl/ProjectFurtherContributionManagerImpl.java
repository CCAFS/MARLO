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


import org.cgiar.ccafs.marlo.data.dao.ProjectFurtherContributionDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectFurtherContributionManager;
import org.cgiar.ccafs.marlo.data.model.ProjectFurtherContribution;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class ProjectFurtherContributionManagerImpl implements ProjectFurtherContributionManager {


  private ProjectFurtherContributionDAO projectFurtherContributionDAO;
  // Managers


  @Inject
  public ProjectFurtherContributionManagerImpl(ProjectFurtherContributionDAO projectFurtherContributionDAO) {
    this.projectFurtherContributionDAO = projectFurtherContributionDAO;


  }

  @Override
  public void deleteProjectFurtherContribution(long projectFurtherContributionId) {

    projectFurtherContributionDAO.deleteProjectFurtherContribution(projectFurtherContributionId);
  }

  @Override
  public boolean existProjectFurtherContribution(long projectFurtherContributionID) {

    return projectFurtherContributionDAO.existProjectFurtherContribution(projectFurtherContributionID);
  }

  @Override
  public List<ProjectFurtherContribution> findAll() {

    return projectFurtherContributionDAO.findAll();

  }

  @Override
  public ProjectFurtherContribution getProjectFurtherContributionById(long projectFurtherContributionID) {

    return projectFurtherContributionDAO.find(projectFurtherContributionID);
  }

  @Override
  public ProjectFurtherContribution saveProjectFurtherContribution(ProjectFurtherContribution projectFurtherContribution) {

    return projectFurtherContributionDAO.save(projectFurtherContribution);
  }


}
