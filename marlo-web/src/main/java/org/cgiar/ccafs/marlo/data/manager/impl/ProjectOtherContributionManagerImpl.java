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


import org.cgiar.ccafs.marlo.data.dao.ProjectOtherContributionDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectOtherContributionManager;
import org.cgiar.ccafs.marlo.data.model.ProjectOtherContribution;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class ProjectOtherContributionManagerImpl implements ProjectOtherContributionManager {


  private ProjectOtherContributionDAO projectOtherContributionDAO;
  // Managers


  @Inject
  public ProjectOtherContributionManagerImpl(ProjectOtherContributionDAO projectOtherContributionDAO) {
    this.projectOtherContributionDAO = projectOtherContributionDAO;


  }

  @Override
  public boolean deleteProjectOtherContribution(long projectOtherContributionId) {

    return projectOtherContributionDAO.deleteProjectOtherContribution(projectOtherContributionId);
  }

  @Override
  public boolean existProjectOtherContribution(long projectOtherContributionID) {

    return projectOtherContributionDAO.existProjectOtherContribution(projectOtherContributionID);
  }

  @Override
  public List<ProjectOtherContribution> findAll() {

    return projectOtherContributionDAO.findAll();

  }

  @Override
  public ProjectOtherContribution getProjectOtherContributionById(long projectOtherContributionID) {

    return projectOtherContributionDAO.find(projectOtherContributionID);
  }

  @Override
  public long saveProjectOtherContribution(ProjectOtherContribution projectOtherContribution) {

    return projectOtherContributionDAO.save(projectOtherContribution);
  }


}
