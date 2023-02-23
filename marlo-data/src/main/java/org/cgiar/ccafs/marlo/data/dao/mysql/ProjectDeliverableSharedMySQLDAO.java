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

import org.cgiar.ccafs.marlo.data.dao.ProjectDeliverableSharedDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectDeliverableShared;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

@Named
public class ProjectDeliverableSharedMySQLDAO extends AbstractMarloDAO<ProjectDeliverableShared, Long>
  implements ProjectDeliverableSharedDAO {


  @Inject
  public ProjectDeliverableSharedMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectDeliverableShared(long projectDeliverableSharedId) {
    ProjectDeliverableShared projectDeliverableShared = this.find(projectDeliverableSharedId);
    projectDeliverableShared.setActive(false);
    this.update(projectDeliverableShared);
  }

  @Override
  public boolean existProjectDeliverableShared(long projectDeliverableSharedID) {
    ProjectDeliverableShared projectDeliverableShared = this.find(projectDeliverableSharedID);
    if (projectDeliverableShared == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectDeliverableShared find(long id) {
    return super.find(ProjectDeliverableShared.class, id);

  }

  @Override
  public List<ProjectDeliverableShared> findAll() {
    String query = "from " + ProjectDeliverableShared.class.getName() + " where is_active=1";
    List<ProjectDeliverableShared> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<ProjectDeliverableShared> getByDeliverable(long deliverableId, long phaseId) {
    String query = "select distinct esp from ProjectDeliverableShared esp "
      + "where deliverable.id = :deliverableId and phase.id = :phaseId and is_active=1";
    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("deliverableId", deliverableId);
    createQuery.setParameter("phaseId", phaseId);
    List<ProjectDeliverableShared> result = super.findAll(createQuery);

    if (result != null && !result.isEmpty()) {
      return result;
    }

    return null;
  }

  @Override
  public List<ProjectDeliverableShared> getByPhase(long phaseId) {
    String query =
      "select distinct esp from ProjectDeliverableShared esp " + "where phase.id = :phaseId and is_active=1";
    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("phaseId", phaseId);
    List<ProjectDeliverableShared> result = super.findAll(createQuery);

    if (result != null && !result.isEmpty()) {
      return result;
    }

    return null;
  }

  @Override
  public List<ProjectDeliverableShared> getByProjectAndPhase(long projectId, long phaseId) {
    String query = "select distinct esp from ProjectDeliverableShared esp "
      + "where project.id = :projectId and phase.id = :phaseId and is_active=1";
    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("projectId", projectId);
    createQuery.setParameter("phaseId", phaseId);
    List<ProjectDeliverableShared> result = super.findAll(createQuery);

    if (result != null && !result.isEmpty()) {
      return result;
    }

    return null;
  }

  @Override
  public ProjectDeliverableShared save(ProjectDeliverableShared projectDeliverableShared) {
    if (projectDeliverableShared.getId() == null) {
      super.saveEntity(projectDeliverableShared);
    } else {
      projectDeliverableShared = super.update(projectDeliverableShared);
    }


    return projectDeliverableShared;
  }


}