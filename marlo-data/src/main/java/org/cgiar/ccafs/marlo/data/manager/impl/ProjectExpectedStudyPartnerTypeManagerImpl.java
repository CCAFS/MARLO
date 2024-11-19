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


import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyPartnerTypeDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyPartnerTypeManager;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPartnerType;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudyPartnerTypeManagerImpl implements ProjectExpectedStudyPartnerTypeManager {


  private ProjectExpectedStudyPartnerTypeDAO projectExpectedStudyPartnerTypeDAO;
  // Managers


  @Inject
  public ProjectExpectedStudyPartnerTypeManagerImpl(ProjectExpectedStudyPartnerTypeDAO projectExpectedStudyPartnerTypeDAO) {
    this.projectExpectedStudyPartnerTypeDAO = projectExpectedStudyPartnerTypeDAO;


  }

  @Override
  public void deleteProjectExpectedStudyPartnerType(long projectExpectedStudyPartnerTypeId) {

    projectExpectedStudyPartnerTypeDAO.deleteProjectExpectedStudyPartnerType(projectExpectedStudyPartnerTypeId);
  }

  @Override
  public boolean existProjectExpectedStudyPartnerType(long projectExpectedStudyPartnerTypeID) {

    return projectExpectedStudyPartnerTypeDAO.existProjectExpectedStudyPartnerType(projectExpectedStudyPartnerTypeID);
  }

  @Override
  public List<ProjectExpectedStudyPartnerType> findAll() {

    return projectExpectedStudyPartnerTypeDAO.findAll();

  }

  @Override
  public ProjectExpectedStudyPartnerType getProjectExpectedStudyPartnerTypeById(long projectExpectedStudyPartnerTypeID) {

    return projectExpectedStudyPartnerTypeDAO.find(projectExpectedStudyPartnerTypeID);
  }

  @Override
  public ProjectExpectedStudyPartnerType saveProjectExpectedStudyPartnerType(ProjectExpectedStudyPartnerType projectExpectedStudyPartnerType) {

    return projectExpectedStudyPartnerTypeDAO.save(projectExpectedStudyPartnerType);
  }


}
