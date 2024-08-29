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


  private ProjectExpectedStudyRelatedLeverSdgContributionDAO projectExpectedStudyRelatedLeverSDGContributionDAO;
  // Managers


  @Inject
  public ProjectExpectedStudyRelatedLeverSdgContributionManagerImpl(ProjectExpectedStudyRelatedLeverSdgContributionDAO projectExpectedStudyRelatedLeverSDGContributionDAO) {
    this.projectExpectedStudyRelatedLeverSDGContributionDAO = projectExpectedStudyRelatedLeverSDGContributionDAO;


  }

  @Override
  public void deleteProjectExpectedStudyRelatedLeverSdgContribution(long projectExpectedStudyRelatedLeverSDGContributionId) {

    projectExpectedStudyRelatedLeverSDGContributionDAO.deleteProjectExpectedStudyRelatedLeverSdgContribution(projectExpectedStudyRelatedLeverSDGContributionId);
  }

  @Override
  public boolean existProjectExpectedStudyRelatedLeverSdgContribution(long projectExpectedStudyRelatedLeverSDGContributionID) {

    return projectExpectedStudyRelatedLeverSDGContributionDAO.existProjectExpectedStudyRelatedLeverSdgContribution(projectExpectedStudyRelatedLeverSDGContributionID);
  }

  @Override
  public List<ProjectExpectedStudyRelatedLeverSdgContribution> findAll() {

    return projectExpectedStudyRelatedLeverSDGContributionDAO.findAll();

  }

  @Override
  public ProjectExpectedStudyRelatedLeverSdgContribution getProjectExpectedStudyRelatedLeverSdgContributionById(long projectExpectedStudyRelatedLeverSDGContributionID) {

    return projectExpectedStudyRelatedLeverSDGContributionDAO.find(projectExpectedStudyRelatedLeverSDGContributionID);
  }

  @Override
  public ProjectExpectedStudyRelatedLeverSdgContribution saveProjectExpectedStudyRelatedLeverSdgContribution(ProjectExpectedStudyRelatedLeverSdgContribution projectExpectedStudyRelatedLeverSDGContribution) {

    return projectExpectedStudyRelatedLeverSDGContributionDAO.save(projectExpectedStudyRelatedLeverSDGContribution);
  }


}
