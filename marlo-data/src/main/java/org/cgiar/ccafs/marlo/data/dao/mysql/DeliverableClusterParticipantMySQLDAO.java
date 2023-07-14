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

import org.cgiar.ccafs.marlo.data.dao.DeliverableClusterParticipantDAO;
import org.cgiar.ccafs.marlo.data.model.DeliverableClusterParticipant;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

@Named
public class DeliverableClusterParticipantMySQLDAO extends AbstractMarloDAO<DeliverableClusterParticipant, Long>
  implements DeliverableClusterParticipantDAO {


  @Inject
  public DeliverableClusterParticipantMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteDeliverableClusterParticipant(long deliverableClusterParticipantId) {
    DeliverableClusterParticipant deliverableClusterParticipant = this.find(deliverableClusterParticipantId);
    deliverableClusterParticipant.setActive(false);
    this.update(deliverableClusterParticipant);
  }

  @Override
  public boolean existDeliverableClusterParticipant(long deliverableClusterParticipantID) {
    DeliverableClusterParticipant deliverableClusterParticipant = this.find(deliverableClusterParticipantID);
    if (deliverableClusterParticipant == null) {
      return false;
    }
    return true;

  }

  @Override
  public DeliverableClusterParticipant find(long id) {
    return super.find(DeliverableClusterParticipant.class, id);

  }

  @Override
  public List<DeliverableClusterParticipant> findAll() {
    String query = "from " + DeliverableClusterParticipant.class.getName() + " where is_active=1";
    List<DeliverableClusterParticipant> list = super.findAll(query);
    if (!list.isEmpty()) {
      return list;
    }
    return null;

  }

  @Override
  public List<DeliverableClusterParticipant> getDeliverableClusterParticipantByDeliverableAndPhase(long deliverableID,
    long phaseID) {

    String query = "select dp from DeliverableClusterParticipant dp "
      + "where phase.id = :phaseId and deliverable.id= :deliverableId and is_active=1";
    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("phaseId", phaseID);
    createQuery.setParameter("deliverableId", deliverableID);

    Object findSingleResult = super.findAll(createQuery);
    List<DeliverableClusterParticipant> deliverableUserResult = (List<DeliverableClusterParticipant>) findSingleResult;
    return deliverableUserResult;
  }

  @Override
  public List<DeliverableClusterParticipant>
    getDeliverableClusterParticipantByDeliverableProjectPhase(long deliverableID, long projectID, long phaseID) {

    String query = "select dp from DeliverableClusterParticipant dp "
      + "where phase.id = :phaseId and deliverable.id= :deliverableId and project.id= :projectId and is_active=1";
    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("phaseId", phaseID);
    createQuery.setParameter("deliverableId", deliverableID);
    createQuery.setParameter("projectId", projectID);

    Object findSingleResult = super.findAll(createQuery);
    List<DeliverableClusterParticipant> deliverableUserResult = (List<DeliverableClusterParticipant>) findSingleResult;
    return deliverableUserResult;
  }

  @Override
  public List<DeliverableClusterParticipant> getDeliverableClusterParticipantByProjectAndPhase(long projectID,
    long phaseID) {

    String query = "select dp from DeliverableClusterParticipant dp "
      + "where phase.id = :phaseId and project.id= :projectId and is_active=1";
    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("phaseId", phaseID);
    createQuery.setParameter("projectId", projectID);

    Object findSingleResult = super.findAll(createQuery);
    List<DeliverableClusterParticipant> deliverableUserResult = (List<DeliverableClusterParticipant>) findSingleResult;
    return deliverableUserResult;
  }

  @Override
  public DeliverableClusterParticipant save(DeliverableClusterParticipant deliverableClusterParticipant) {
    if (deliverableClusterParticipant.getId() == null) {
      super.saveEntity(deliverableClusterParticipant);
    } else {
      deliverableClusterParticipant = super.update(deliverableClusterParticipant);
    }


    return deliverableClusterParticipant;
  }


}