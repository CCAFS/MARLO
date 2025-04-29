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


package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationContributingOrganizationRoleDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationContributingOrganizationRole;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectInnovationContributingOrganizationRoleMySQLDAO
  extends AbstractMarloDAO<ProjectInnovationContributingOrganizationRole, Long>
  implements ProjectInnovationContributingOrganizationRoleDAO {


  @Inject
  public ProjectInnovationContributingOrganizationRoleMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void
    deleteProjectInnovationContributingOrganizationRole(long projectInnovationContributingOrganizationRoleId) {
    ProjectInnovationContributingOrganizationRole projectInnovationContributingOrganizationRole =
      this.find(projectInnovationContributingOrganizationRoleId);
    projectInnovationContributingOrganizationRole.setActive(false);
    this.update(projectInnovationContributingOrganizationRole);
  }

  @Override
  public boolean
    existProjectInnovationContributingOrganizationRole(long projectInnovationContributingOrganizationRoleID) {
    ProjectInnovationContributingOrganizationRole projectInnovationContributingOrganizationRole =
      this.find(projectInnovationContributingOrganizationRoleID);
    if (projectInnovationContributingOrganizationRole == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectInnovationContributingOrganizationRole find(long id) {
    return super.find(ProjectInnovationContributingOrganizationRole.class, id);

  }

  @Override
  public List<ProjectInnovationContributingOrganizationRole> findAll() {
    String query = "from " + ProjectInnovationContributingOrganizationRole.class.getName();
    List<ProjectInnovationContributingOrganizationRole> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectInnovationContributingOrganizationRole
    save(ProjectInnovationContributingOrganizationRole projectInnovationContributingOrganizationRole) {
    if (projectInnovationContributingOrganizationRole.getId() == null) {
      super.saveEntity(projectInnovationContributingOrganizationRole);
    } else {
      projectInnovationContributingOrganizationRole = super.update(projectInnovationContributingOrganizationRole);
    }


    return projectInnovationContributingOrganizationRole;
  }


}