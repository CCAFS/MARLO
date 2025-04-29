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


import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationContributingOrganizationRoleDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationContributingOrganizationRoleManager;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationContributingOrganizationRole;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectInnovationContributingOrganizationRoleManagerImpl implements ProjectInnovationContributingOrganizationRoleManager {


  private ProjectInnovationContributingOrganizationRoleDAO projectInnovationContributingOrganizationRoleDAO;
  // Managers


  @Inject
  public ProjectInnovationContributingOrganizationRoleManagerImpl(ProjectInnovationContributingOrganizationRoleDAO projectInnovationContributingOrganizationRoleDAO) {
    this.projectInnovationContributingOrganizationRoleDAO = projectInnovationContributingOrganizationRoleDAO;


  }

  @Override
  public void deleteProjectInnovationContributingOrganizationRole(long projectInnovationContributingOrganizationRoleId) {

    projectInnovationContributingOrganizationRoleDAO.deleteProjectInnovationContributingOrganizationRole(projectInnovationContributingOrganizationRoleId);
  }

  @Override
  public boolean existProjectInnovationContributingOrganizationRole(long projectInnovationContributingOrganizationRoleID) {

    return projectInnovationContributingOrganizationRoleDAO.existProjectInnovationContributingOrganizationRole(projectInnovationContributingOrganizationRoleID);
  }

  @Override
  public List<ProjectInnovationContributingOrganizationRole> findAll() {

    return projectInnovationContributingOrganizationRoleDAO.findAll();

  }

  @Override
  public ProjectInnovationContributingOrganizationRole getProjectInnovationContributingOrganizationRoleById(long projectInnovationContributingOrganizationRoleID) {

    return projectInnovationContributingOrganizationRoleDAO.find(projectInnovationContributingOrganizationRoleID);
  }

  @Override
  public ProjectInnovationContributingOrganizationRole saveProjectInnovationContributingOrganizationRole(ProjectInnovationContributingOrganizationRole projectInnovationContributingOrganizationRole) {

    return projectInnovationContributingOrganizationRoleDAO.save(projectInnovationContributingOrganizationRole);
  }


}
