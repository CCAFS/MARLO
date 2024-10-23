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

import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationPartnershipDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationPartnership;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectInnovationPartnershipMySQLDAO extends AbstractMarloDAO<ProjectInnovationPartnership, Long>
  implements ProjectInnovationPartnershipDAO {


  @Inject
  public ProjectInnovationPartnershipMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectInnovationPartnership(long projectInnovationPartnershipId) {
    ProjectInnovationPartnership projectInnovationPartnership = this.find(projectInnovationPartnershipId);
    projectInnovationPartnership.setActive(false);
    this.update(projectInnovationPartnership);
  }

  @Override
  public boolean existProjectInnovationPartnership(long projectInnovationPartnershipID) {
    ProjectInnovationPartnership projectInnovationPartnership = this.find(projectInnovationPartnershipID);
    if (projectInnovationPartnership == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectInnovationPartnership find(long id) {
    return super.find(ProjectInnovationPartnership.class, id);

  }

  @Override
  public List<ProjectInnovationPartnership> findAll() {
    String query = "from " + ProjectInnovationPartnership.class.getName() + " where is_active=1";
    List<ProjectInnovationPartnership> list = super.findAll(query);
    if (!list.isEmpty()) {
      return list;
    }
    return null;

  }

  @Override
  public List<ProjectInnovationPartnership> findByInnovation(long innovationID) {
    String query = "from " + ProjectInnovationPartnership.class.getName()
      + " where is_active=1 and project_innovation_id=" + innovationID;
    List<ProjectInnovationPartnership> list = super.findAll(query);
    if (!list.isEmpty()) {
      return list;
    }
    return null;
  }

  @Override
  public List<ProjectInnovationPartnership> findByInnovationAndPhase(long innovationID, long phaseId) {
    String query = "from " + ProjectInnovationPartnership.class.getName() + " where is_active=1 and id_phase=" + phaseId
      + " and project_innovation_id=" + innovationID;
    List<ProjectInnovationPartnership> list = super.findAll(query);
    if (!list.isEmpty()) {
      return list;
    }
    return null;
  }

  @Override
  public ProjectInnovationPartnership save(ProjectInnovationPartnership projectInnovationPartnership) {
    if (projectInnovationPartnership.getId() == null) {
      super.saveEntity(projectInnovationPartnership);
    } else {
      projectInnovationPartnership = super.update(projectInnovationPartnership);
    }


    return projectInnovationPartnership;
  }


}