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

import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationContributingOrganizationDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationContributingOrganization;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectInnovationContributingOrganizationMySQLDAO
  extends AbstractMarloDAO<ProjectInnovationContributingOrganization, Long>
  implements ProjectInnovationContributingOrganizationDAO {


  @Inject
  public ProjectInnovationContributingOrganizationMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectInnovationContributingOrganization(long projectInnovationContributingOrganizationId) {
    ProjectInnovationContributingOrganization projectInnovationContributingOrganization =
      this.find(projectInnovationContributingOrganizationId);
    this.delete(projectInnovationContributingOrganization);
  }

  @Override
  public boolean existProjectInnovationContributingOrganization(long projectInnovationContributingOrganizationID) {
    ProjectInnovationContributingOrganization projectInnovationContributingOrganization =
      this.find(projectInnovationContributingOrganizationID);
    if (projectInnovationContributingOrganization == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectInnovationContributingOrganization find(long id) {
    return super.find(ProjectInnovationContributingOrganization.class, id);

  }

  @Override
  public List<ProjectInnovationContributingOrganization> findAll() {
    String query = "from " + ProjectInnovationContributingOrganization.class.getName();
    List<ProjectInnovationContributingOrganization> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public ProjectInnovationContributingOrganization
    save(ProjectInnovationContributingOrganization projectInnovationContributingOrganization) {
    if (projectInnovationContributingOrganization.getId() == null) {
      super.saveEntity(projectInnovationContributingOrganization);
    } else {
      projectInnovationContributingOrganization = super.update(projectInnovationContributingOrganization);
    }
    return projectInnovationContributingOrganization;
  }


}