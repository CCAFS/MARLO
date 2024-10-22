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


import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationPartnerTypeDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationPartnerTypeManager;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationPartnerType;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectInnovationPartnerTypeManagerImpl implements ProjectInnovationPartnerTypeManager {


  private ProjectInnovationPartnerTypeDAO projectInnovationPartnerTypeDAO;
  // Managers


  @Inject
  public ProjectInnovationPartnerTypeManagerImpl(ProjectInnovationPartnerTypeDAO projectInnovationPartnerTypeDAO) {
    this.projectInnovationPartnerTypeDAO = projectInnovationPartnerTypeDAO;


  }

  @Override
  public void deleteProjectInnovationPartnerType(long projectInnovationPartnerTypeId) {

    projectInnovationPartnerTypeDAO.deleteProjectInnovationPartnerType(projectInnovationPartnerTypeId);
  }

  @Override
  public boolean existProjectInnovationPartnerType(long projectInnovationPartnerTypeID) {

    return projectInnovationPartnerTypeDAO.existProjectInnovationPartnerType(projectInnovationPartnerTypeID);
  }

  @Override
  public List<ProjectInnovationPartnerType> findAll() {

    return projectInnovationPartnerTypeDAO.findAll();

  }

  @Override
  public ProjectInnovationPartnerType getProjectInnovationPartnerTypeById(long projectInnovationPartnerTypeID) {

    return projectInnovationPartnerTypeDAO.find(projectInnovationPartnerTypeID);
  }

  @Override
  public ProjectInnovationPartnerType saveProjectInnovationPartnerType(ProjectInnovationPartnerType projectInnovationPartnerType) {

    return projectInnovationPartnerTypeDAO.save(projectInnovationPartnerType);
  }


}
