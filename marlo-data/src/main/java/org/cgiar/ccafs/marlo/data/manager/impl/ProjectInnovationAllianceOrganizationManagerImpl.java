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


import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationAllianceOrganizationDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationAllianceOrganizationManager;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationAllianceOrganization;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectInnovationAllianceOrganizationManagerImpl implements ProjectInnovationAllianceOrganizationManager {


  private ProjectInnovationAllianceOrganizationDAO projectInnovationAllianceOrganizationDAO;
  // Managers


  @Inject
  public ProjectInnovationAllianceOrganizationManagerImpl(ProjectInnovationAllianceOrganizationDAO projectInnovationAllianceOrganizationDAO) {
    this.projectInnovationAllianceOrganizationDAO = projectInnovationAllianceOrganizationDAO;


  }

  @Override
  public void deleteProjectInnovationAllianceOrganization(long projectInnovationAllianceOrganizationId) {

    projectInnovationAllianceOrganizationDAO.deleteProjectInnovationAllianceOrganization(projectInnovationAllianceOrganizationId);
  }

  @Override
  public boolean existProjectInnovationAllianceOrganization(long projectInnovationAllianceOrganizationID) {

    return projectInnovationAllianceOrganizationDAO.existProjectInnovationAllianceOrganization(projectInnovationAllianceOrganizationID);
  }

  @Override
  public List<ProjectInnovationAllianceOrganization> findAll() {

    return projectInnovationAllianceOrganizationDAO.findAll();

  }

  @Override
  public ProjectInnovationAllianceOrganization getProjectInnovationAllianceOrganizationById(long projectInnovationAllianceOrganizationID) {

    return projectInnovationAllianceOrganizationDAO.find(projectInnovationAllianceOrganizationID);
  }

  @Override
  public ProjectInnovationAllianceOrganization saveProjectInnovationAllianceOrganization(ProjectInnovationAllianceOrganization projectInnovationAllianceOrganization) {

    return projectInnovationAllianceOrganizationDAO.save(projectInnovationAllianceOrganization);
  }


}
