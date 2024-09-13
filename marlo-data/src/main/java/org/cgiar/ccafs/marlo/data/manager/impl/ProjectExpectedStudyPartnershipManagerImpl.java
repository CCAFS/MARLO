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


import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyPartnershipDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyPartnershipManager;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPartnership;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudyPartnershipManagerImpl implements ProjectExpectedStudyPartnershipManager {


  private ProjectExpectedStudyPartnershipDAO projectExpectedStudyPartnershipDAO;
  // Managers


  @Inject
  public ProjectExpectedStudyPartnershipManagerImpl(ProjectExpectedStudyPartnershipDAO projectExpectedStudyPartnershipDAO) {
    this.projectExpectedStudyPartnershipDAO = projectExpectedStudyPartnershipDAO;


  }

  @Override
  public void deleteProjectExpectedStudyPartnership(long projectExpectedStudyPartnershipId) {

    projectExpectedStudyPartnershipDAO.deleteProjectExpectedStudyPartnership(projectExpectedStudyPartnershipId);
  }

  @Override
  public boolean existProjectExpectedStudyPartnership(long projectExpectedStudyPartnershipID) {

    return projectExpectedStudyPartnershipDAO.existProjectExpectedStudyPartnership(projectExpectedStudyPartnershipID);
  }

  @Override
  public List<ProjectExpectedStudyPartnership> findAll() {

    return projectExpectedStudyPartnershipDAO.findAll();

  }

  @Override
  public ProjectExpectedStudyPartnership getProjectExpectedStudyPartnershipById(long projectExpectedStudyPartnershipID) {

    return projectExpectedStudyPartnershipDAO.find(projectExpectedStudyPartnershipID);
  }

  @Override
  public ProjectExpectedStudyPartnership saveProjectExpectedStudyPartnership(ProjectExpectedStudyPartnership projectExpectedStudyPartnership) {

    return projectExpectedStudyPartnershipDAO.save(projectExpectedStudyPartnership);
  }


}
