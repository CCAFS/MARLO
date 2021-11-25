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

import org.cgiar.ccafs.marlo.data.dao.DeliverableUserDAO;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableUser;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

@Named
public class DeliverableUserMySQLDAO extends AbstractMarloDAO<DeliverableUser, Long> implements DeliverableUserDAO {


  @Inject
  public DeliverableUserMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteDeliverableUser(long deliverableUserId) {
    DeliverableUser deliverableUser = this.find(deliverableUserId);
    super.delete(deliverableUser);
  }

  @Override
  public boolean existDeliverableUser(long deliverableUserID) {
    DeliverableUser deliverableUser = this.find(deliverableUserID);
    if (deliverableUser == null) {
      return false;
    }
    return true;

  }

  @Override
  public DeliverableUser find(long id) {
    return super.find(DeliverableUser.class, id);

  }

  @Override
  public List<DeliverableUser> findAll() {
    String query = "from " + DeliverableUser.class.getName();
    List<DeliverableUser> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public DeliverableUser findDeliverableUserByPhaseAndDeliverableUser(Phase phase, DeliverableUser deliverableUser) {
    String query =
      "select distinct du from DeliverableUser du " + "where phase.id = :phaseId and deliverable.id= :deliverableId "
        + "and du.firstName = :duFirstName and du.lastName = :duLastName and du.elementId = :duElementId";
    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("phaseId", phase.getId());
    createQuery.setParameter("deliverableId", deliverableUser.getDeliverable().getId());
    createQuery.setParameter("duFirstName", deliverableUser.getFirstName());
    createQuery.setParameter("duLastName", deliverableUser.getLastName());
    createQuery.setParameter("duElementId", deliverableUser.getElementId());

    Object findSingleResult = super.findSingleResult(DeliverableUser.class, createQuery);
    DeliverableUser deliverableUserResult = (DeliverableUser) findSingleResult;
    return deliverableUserResult;
  }

  @Override
  public List<DeliverableUser> findDeliverableUserByPhases(Phase phase, DeliverableUser deliverableUser) {
    String query =
      "select distinct du from DeliverableUser du " + "where phase.id = :phaseId and deliverable.id= :deliverableId "
        + "and du.firstName = :duFirstName and du.lastName = :duLastName and du.elementId = :duElementId";
    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("phaseId", phase.getId());
    createQuery.setParameter("deliverableId", deliverableUser.getDeliverable().getId());
    createQuery.setParameter("duFirstName", deliverableUser.getFirstName());
    createQuery.setParameter("duLastName", deliverableUser.getLastName());
    createQuery.setParameter("duElementId", deliverableUser.getElementId());

    Object findSingleResult = super.findAll(createQuery);
    List<DeliverableUser> findSingleResult2 = (List<DeliverableUser>) findSingleResult;
    List<DeliverableUser> deliverableUserResult = findSingleResult2;
    return deliverableUserResult;

  }

  @Override
  public DeliverableUser save(DeliverableUser deliverableUser) {
    if (deliverableUser.getId() == null) {
      super.saveEntity(deliverableUser);
    } else {
      deliverableUser = super.update(deliverableUser);
    }


    return deliverableUser;
  }

  @Override
  public List<DeliverableUser> findAllByPhaseAndDeliverable(Phase phase, Deliverable deliverable) {
    String query =
      "select distinct du from DeliverableUser du " + "where phase.id = :phaseId and deliverable.id= :deliverableId ";
    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("phaseId", phase.getId());
    createQuery.setParameter("deliverableId", deliverable.getId());
    

    Object findSingleResult = super.findAll(createQuery);
    List<DeliverableUser> findSingleResult2 = (List<DeliverableUser>) findSingleResult;
    return findSingleResult2;
  }


}