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

import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationAllianceOrganizationDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationAllianceOrganization;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectInnovationAllianceOrganizationMySQLDAO extends
  AbstractMarloDAO<ProjectInnovationAllianceOrganization, Long> implements ProjectInnovationAllianceOrganizationDAO {


  @Inject
  public ProjectInnovationAllianceOrganizationMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectInnovationAllianceOrganization(long projectInnovationAllianceOrganizationId) {
    ProjectInnovationAllianceOrganization projectInnovationAllianceOrganization =
      this.find(projectInnovationAllianceOrganizationId);
    projectInnovationAllianceOrganization.setActive(false);
    this.update(projectInnovationAllianceOrganization);
  }

  @Override
  public boolean existProjectInnovationAllianceOrganization(long projectInnovationAllianceOrganizationID) {
    ProjectInnovationAllianceOrganization projectInnovationAllianceOrganization =
      this.find(projectInnovationAllianceOrganizationID);
    if (projectInnovationAllianceOrganization == null) {
      return false;
    }
    return true;
  }

  @Override
  public ProjectInnovationAllianceOrganization find(long id) {
    return super.find(ProjectInnovationAllianceOrganization.class, id);

  }

  @Override
  public List<ProjectInnovationAllianceOrganization> findAll() {
    String query = "from " + ProjectInnovationAllianceOrganization.class.getName() + " where is_active=1";
    List<ProjectInnovationAllianceOrganization> list = super.findAll(query);
    if (!list.isEmpty()) {
      return list;
    }
    return null;
  }

  @Override
  public List<ProjectInnovationAllianceOrganization>
    getProjectInnovationAllianceOrganizationsByInnovationAndPhase(long innovationID, long phaseID) {
    String query = "from " + ProjectInnovationAllianceOrganization.class.getName()
      + " where is_active=1 and project_innovation_id=" + innovationID + " id_phase=" + phaseID;
    List<ProjectInnovationAllianceOrganization> list = super.findAll(query);
    if (!list.isEmpty()) {
      return list;
    }
    return null;
  }

  @Override
  public ProjectInnovationAllianceOrganization
    save(ProjectInnovationAllianceOrganization projectInnovationAllianceOrganization) {
    if (projectInnovationAllianceOrganization.getId() == null) {
      super.saveEntity(projectInnovationAllianceOrganization);
    } else {
      projectInnovationAllianceOrganization = super.update(projectInnovationAllianceOrganization);
    }

    return projectInnovationAllianceOrganization;

  }


}