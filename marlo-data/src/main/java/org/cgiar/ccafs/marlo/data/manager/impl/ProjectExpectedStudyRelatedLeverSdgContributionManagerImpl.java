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


import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyRelatedLeverSdgContributionDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyRelatedLeverSdgContributionManager;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyRelatedLeverSdgContribution;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudyRelatedLeverSdgContributionManagerImpl implements ProjectExpectedStudyRelatedLeverSdgContributionManager {


  private ProjectExpectedStudyRelatedLeverSdgContributionDAO projectExpectedStudyRelatedLeverSdgContributionDAO;
  // Managers


  @Inject
  public ProjectExpectedStudyRelatedLeverSdgContributionManagerImpl(ProjectExpectedStudyRelatedLeverSdgContributionDAO projectExpectedStudyRelatedLeverSdgContributionDAO) {
    this.projectExpectedStudyRelatedLeverSdgContributionDAO = projectExpectedStudyRelatedLeverSdgContributionDAO;


  }

  @Override
  public void deleteProjectExpectedStudyRelatedLeverSdgContribution(long projectExpectedStudyRelatedLeverSdgContributionId) {

    projectExpectedStudyRelatedLeverSdgContributionDAO.deleteProjectExpectedStudyRelatedLeverSdgContribution(projectExpectedStudyRelatedLeverSdgContributionId);
  }

  @Override
  public boolean existProjectExpectedStudyRelatedLeverSdgContribution(long projectExpectedStudyRelatedLeverSdgContributionID) {

    return projectExpectedStudyRelatedLeverSdgContributionDAO.existProjectExpectedStudyRelatedLeverSdgContribution(projectExpectedStudyRelatedLeverSdgContributionID);
  }

  @Override
  public List<ProjectExpectedStudyRelatedLeverSdgContribution> findAll() {

    return projectExpectedStudyRelatedLeverSdgContributionDAO.findAll();

  }

  @Override
  public ProjectExpectedStudyRelatedLeverSdgContribution getProjectExpectedStudyRelatedLeverSdgContributionById(long projectExpectedStudyRelatedLeverSdgContributionID) {

    return projectExpectedStudyRelatedLeverSdgContributionDAO.find(projectExpectedStudyRelatedLeverSdgContributionID);
  }

  @Override
  public ProjectExpectedStudyRelatedLeverSdgContribution saveProjectExpectedStudyRelatedLeverSdgContribution(ProjectExpectedStudyRelatedLeverSdgContribution projectExpectedStudyRelatedLeverSdgContribution) {

    return projectExpectedStudyRelatedLeverSdgContributionDAO.save(projectExpectedStudyRelatedLeverSdgContribution);
  }


}
