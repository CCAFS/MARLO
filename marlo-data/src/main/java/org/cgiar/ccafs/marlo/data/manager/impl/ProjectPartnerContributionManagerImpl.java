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


import org.cgiar.ccafs.marlo.data.dao.ProjectPartnerContributionDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerContributionManager;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerContribution;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class ProjectPartnerContributionManagerImpl implements ProjectPartnerContributionManager {


  private ProjectPartnerContributionDAO projectPartnerContributionDAO;
  // Managers


  @Inject
  public ProjectPartnerContributionManagerImpl(ProjectPartnerContributionDAO projectPartnerContributionDAO) {
    this.projectPartnerContributionDAO = projectPartnerContributionDAO;


  }

  @Override
  public boolean deleteProjectPartnerContribution(long projectPartnerContributionId) {

    return projectPartnerContributionDAO.deleteProjectPartnerContribution(projectPartnerContributionId);
  }

  @Override
  public boolean existProjectPartnerContribution(long projectPartnerContributionID) {

    return projectPartnerContributionDAO.existProjectPartnerContribution(projectPartnerContributionID);
  }

  @Override
  public List<ProjectPartnerContribution> findAll() {

    return projectPartnerContributionDAO.findAll();

  }

  @Override
  public ProjectPartnerContribution getProjectPartnerContributionById(long projectPartnerContributionID) {

    return projectPartnerContributionDAO.find(projectPartnerContributionID);
  }

  @Override
  public long saveProjectPartnerContribution(ProjectPartnerContribution projectPartnerContribution) {

    return projectPartnerContributionDAO.save(projectPartnerContribution);
  }


}
