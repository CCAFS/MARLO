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

import org.cgiar.ccafs.marlo.data.dao.DeliverableDisseminationDAO;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableDissemination;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

@Named
public class DeliverableDisseminationMySQLDAO extends AbstractMarloDAO<DeliverableDissemination, Long>
  implements DeliverableDisseminationDAO {


  @Inject
  public DeliverableDisseminationMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteDeliverableDissemination(long deliverableDisseminationId) {
    DeliverableDissemination deliverableDissemination = this.find(deliverableDisseminationId);
    super.delete(deliverableDissemination);
  }

  @Override
  public boolean existDeliverableDissemination(long deliverableDisseminationID) {
    DeliverableDissemination deliverableDissemination = this.find(deliverableDisseminationID);
    if (deliverableDissemination == null) {
      return false;
    }
    return true;

  }

  @Override
  public DeliverableDissemination find(long id) {
    return super.find(DeliverableDissemination.class, id);

  }

  @Override
  public List<DeliverableDissemination> findAll() {
    String query = "from " + DeliverableDissemination.class.getName() + " where is_active=1";
    List<DeliverableDissemination> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public DeliverableDissemination findDisseminationByPhaseAndDeliverable(Phase phase, Deliverable deliverable) {
    String query = "select distinct dd from DeliverableDissemination dd "
      + " where phase.id = :phaseId and deliverable.id= :deliverableId";
    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("phaseId", phase.getId());
    createQuery.setParameter("deliverableId", deliverable.getId());

    Object findSingleResult = super.findSingleResult(DeliverableDissemination.class, createQuery);
    DeliverableDissemination deliverableDisseminationResult = (DeliverableDissemination) findSingleResult;


    return deliverableDisseminationResult;
  }

  @Override
  public DeliverableDissemination save(DeliverableDissemination deliverableDissemination) {
    if (deliverableDissemination.getId() == null) {
      super.saveEntity(deliverableDissemination);
    } else {
      deliverableDissemination = super.update(deliverableDissemination);
    }


    return deliverableDissemination;
  }


}