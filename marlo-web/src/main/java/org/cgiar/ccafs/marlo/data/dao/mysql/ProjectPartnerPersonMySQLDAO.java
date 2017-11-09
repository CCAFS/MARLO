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

import org.cgiar.ccafs.marlo.data.dao.ProjectPartnerPersonDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;

import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import org.hibernate.Query;
import org.hibernate.SessionFactory;

public class ProjectPartnerPersonMySQLDAO extends AbstractMarloDAO<ProjectPartnerPerson, Long>
  implements ProjectPartnerPersonDAO {


  @Inject
  public ProjectPartnerPersonMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectPartnerPerson(long projectPartnerPersonId) {
    ProjectPartnerPerson projectPartnerPerson = this.find(projectPartnerPersonId);
    projectPartnerPerson.setActive(false);
    this.save(projectPartnerPerson);
  }

  @Override
  public boolean existProjectPartnerPerson(long projectPartnerPersonID) {
    ProjectPartnerPerson projectPartnerPerson = this.find(projectPartnerPersonID);
    if (projectPartnerPerson == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectPartnerPerson find(long id) {
    return super.find(ProjectPartnerPerson.class, id);

  }

  @Override
  public List<ProjectPartnerPerson> findAll() {
    String query = "from " + ProjectPartnerPerson.class.getName() + " where is_active=1";
    List<ProjectPartnerPerson> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<ProjectPartnerPerson> findAllForOtherPartnerTypeWithDeliverableIdAndPartnerId(long deliverableId,
    long partnerId) {
    String query = "select projectPartnerPerson from ProjectPartnerPerson as projectPartnerPerson "
      + "inner join projectPartnerPerson.projectPartner as projectPartner "
      + "inner join projectPartnerPerson.deliverablePartnerships as deliverablePartnership "
      + "inner join deliverablePartnership.deliverable as deliverable " + "where deliverablePartnership.active is true "
      + "and deliverablePartnership.partnerType = 'Other' " + "and deliverable.id = :deliverableId "
      + "and projectPartner.id = :partnerId";

    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("deliverableId", deliverableId);
    createQuery.setParameter("partnerId", partnerId);

    List<ProjectPartnerPerson> projectPartnerPersons = createQuery.list();

    return projectPartnerPersons;

  }

  @Override
  public List<ProjectPartnerPerson> findAllForProjectPartner(long projectPartnerId) {
    String query = "select projectPartnerPerson from ProjectPartnerPerson as projectPartnerPerson "
      + "inner join projectPartnerPerson.projectPartner as projectPartner "
      + "where projectPartnerPerson.active is true " + "and projectPartner.id = :projectPartnerId";

    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("projectPartnerId", projectPartnerId);

    List<ProjectPartnerPerson> projectPartnerPersons = createQuery.list();

    return projectPartnerPersons;
  }

  @Override
  public List<Map<String, Object>> findPartner(long institutionId, long phaseId, long projectId, long userId) {
    StringBuilder query = new StringBuilder();
    query.append(
      "select ppp.id from project_partners pp INNER JOIN project_partner_persons ppp on ppp.project_partner_id=pp.id");
    query.append(" where pp.is_active=1 and ppp.is_active=1 and  pp.institution_id=");
    query.append(institutionId);
    query.append(" and pp.id_phase=");
    query.append(phaseId);
    query.append(" and pp.project_id= ");
    query.append(projectId);
    query.append(" and ppp.user_id= ");
    query.append(userId);
    List<Map<String, Object>> result = super.findCustomQuery(query.toString());
    return result;
  }

  @Override
  public ProjectPartnerPerson save(ProjectPartnerPerson projectPartnerPerson) {
    if (projectPartnerPerson.getId() == null) {
      super.saveEntity(projectPartnerPerson);
    } else {
      projectPartnerPerson = super.update(projectPartnerPerson);
    }


    return projectPartnerPerson;
  }


}