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


import org.cgiar.ccafs.marlo.data.dao.ProjectCrpContributionDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectCrpContributionManager;
import org.cgiar.ccafs.marlo.data.model.ProjectCrpContribution;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class ProjectCrpContributionManagerImpl implements ProjectCrpContributionManager {


  private ProjectCrpContributionDAO projectCrpContributionDAO;
  // Managers


  @Inject
  public ProjectCrpContributionManagerImpl(ProjectCrpContributionDAO projectCrpContributionDAO) {
    this.projectCrpContributionDAO = projectCrpContributionDAO;


  }

  @Override
  public boolean deleteProjectCrpContribution(long projectCrpContributionId) {

    return projectCrpContributionDAO.deleteProjectCrpContribution(projectCrpContributionId);
  }

  @Override
  public boolean existProjectCrpContribution(long projectCrpContributionID) {

    return projectCrpContributionDAO.existProjectCrpContribution(projectCrpContributionID);
  }

  @Override
  public List<ProjectCrpContribution> findAll() {

    return projectCrpContributionDAO.findAll();

  }

  @Override
  public ProjectCrpContribution getProjectCrpContributionById(long projectCrpContributionID) {

    return projectCrpContributionDAO.find(projectCrpContributionID);
  }

  @Override
  public long saveProjectCrpContribution(ProjectCrpContribution projectCrpContribution) {

    return projectCrpContributionDAO.save(projectCrpContribution);
  }


}
