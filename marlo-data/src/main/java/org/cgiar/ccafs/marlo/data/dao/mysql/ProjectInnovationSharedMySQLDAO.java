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

import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationSharedDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationShared;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

@Named
public class ProjectInnovationSharedMySQLDAO extends AbstractMarloDAO<ProjectInnovationShared, Long>
  implements ProjectInnovationSharedDAO {


  @Inject
  public ProjectInnovationSharedMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectInnovationShared(long projectInnovationSharedId) {
    ProjectInnovationShared projectInnovationShared = this.find(projectInnovationSharedId);
    projectInnovationShared.setActive(false);
    this.update(projectInnovationShared);
  }

  @Override
  public boolean existProjectInnovationShared(long projectInnovationSharedID) {
    ProjectInnovationShared projectInnovationShared = this.find(projectInnovationSharedID);
    if (projectInnovationShared == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectInnovationShared find(long id) {
    return super.find(ProjectInnovationShared.class, id);

  }

  @Override
  public List<ProjectInnovationShared> findAll() {
    String query = "from " + ProjectInnovationShared.class.getName() + " where is_active=1";
    List<ProjectInnovationShared> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<ProjectInnovationShared> getByProjectAndPhase(long projectId, long phaseId) {
    String query = "select distinct esp from ProjectInnovationShared esp "
      + "where project.id = :projectId and phase.id = :phaseId and is_active=1";
    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("projectId", projectId);
    createQuery.setParameter("phaseId", phaseId);
    List<ProjectInnovationShared> result = super.findAll(createQuery);

    if (result != null && !result.isEmpty()) {
      return result;
    }
    return null;
  }

  @Override
  public ProjectInnovationShared save(ProjectInnovationShared projectInnovationShared) {
    if (projectInnovationShared.getId() == null) {
      super.saveEntity(projectInnovationShared);
    } else {
      projectInnovationShared = super.update(projectInnovationShared);
    }


    return projectInnovationShared;
  }


}