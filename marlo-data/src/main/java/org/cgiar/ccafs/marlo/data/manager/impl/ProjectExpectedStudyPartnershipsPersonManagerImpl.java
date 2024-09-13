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


import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyPartnershipsPersonDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyPartnershipsPersonManager;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPartnershipsPerson;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudyPartnershipsPersonManagerImpl implements ProjectExpectedStudyPartnershipsPersonManager {


  private ProjectExpectedStudyPartnershipsPersonDAO projectExpectedStudyPartnershipsPersonDAO;
  // Managers


  @Inject
  public ProjectExpectedStudyPartnershipsPersonManagerImpl(ProjectExpectedStudyPartnershipsPersonDAO projectExpectedStudyPartnershipsPersonDAO) {
    this.projectExpectedStudyPartnershipsPersonDAO = projectExpectedStudyPartnershipsPersonDAO;


  }

  @Override
  public void deleteProjectExpectedStudyPartnershipsPerson(long projectExpectedStudyPartnershipsPersonId) {

    projectExpectedStudyPartnershipsPersonDAO.deleteProjectExpectedStudyPartnershipsPerson(projectExpectedStudyPartnershipsPersonId);
  }

  @Override
  public boolean existProjectExpectedStudyPartnershipsPerson(long projectExpectedStudyPartnershipsPersonID) {

    return projectExpectedStudyPartnershipsPersonDAO.existProjectExpectedStudyPartnershipsPerson(projectExpectedStudyPartnershipsPersonID);
  }

  @Override
  public List<ProjectExpectedStudyPartnershipsPerson> findAll() {

    return projectExpectedStudyPartnershipsPersonDAO.findAll();

  }

  @Override
  public ProjectExpectedStudyPartnershipsPerson getProjectExpectedStudyPartnershipsPersonById(long projectExpectedStudyPartnershipsPersonID) {

    return projectExpectedStudyPartnershipsPersonDAO.find(projectExpectedStudyPartnershipsPersonID);
  }

  @Override
  public ProjectExpectedStudyPartnershipsPerson saveProjectExpectedStudyPartnershipsPerson(ProjectExpectedStudyPartnershipsPerson projectExpectedStudyPartnershipsPerson) {

    return projectExpectedStudyPartnershipsPersonDAO.save(projectExpectedStudyPartnershipsPerson);
  }


}
