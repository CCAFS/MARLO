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

import org.cgiar.ccafs.marlo.data.dao.DeliverableShfrmPriorityActionDAO;
import org.cgiar.ccafs.marlo.data.model.DeliverableShfrmPriorityAction;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class DeliverableShfrmPriorityActionMySQLDAO extends AbstractMarloDAO<DeliverableShfrmPriorityAction, Long>
  implements DeliverableShfrmPriorityActionDAO {


  @Inject
  public DeliverableShfrmPriorityActionMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteDeliverableShfrmPriorityAction(long deliverableShfrmPriorityActionId) {
    DeliverableShfrmPriorityAction deliverableShfrmPriorityAction = this.find(deliverableShfrmPriorityActionId);
    /*
     * deliverableShfrmPriorityAction.setActive(false);
     * this.update(deliverableShfrmPriorityAction);
     */
    this.delete(deliverableShfrmPriorityAction);

  }

  @Override
  public boolean existDeliverableShfrmPriorityAction(long deliverableShfrmPriorityActionID) {
    DeliverableShfrmPriorityAction deliverableShfrmPriorityAction = this.find(deliverableShfrmPriorityActionID);
    if (deliverableShfrmPriorityAction == null) {
      return false;
    }
    return true;

  }

  @Override
  public DeliverableShfrmPriorityAction find(long id) {
    return super.find(DeliverableShfrmPriorityAction.class, id);

  }

  @Override
  public List<DeliverableShfrmPriorityAction> findAll() {
    String query = "from " + DeliverableShfrmPriorityAction.class.getName() + " where is_active=1";
    List<DeliverableShfrmPriorityAction> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<DeliverableShfrmPriorityAction> findByDeliverableAndPhase(long deliverableId, long phaseId) {
    String query = "from " + DeliverableShfrmPriorityAction.class.getName() + " where is_active=1 and deliverable_id="
      + deliverableId + " and id_phase=" + phaseId;
    List<DeliverableShfrmPriorityAction> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public List<DeliverableShfrmPriorityAction> findByDeliverablePriorityActionAndPhase(long deliverableId,
    long priorityActionId, long phaseId) {
    String query = "from " + DeliverableShfrmPriorityAction.class.getName() + " where is_active=1 and deliverable_id="
      + deliverableId + " and shfrm_priority_action_id=" + priorityActionId + " and id_phase=" + phaseId;
    List<DeliverableShfrmPriorityAction> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public DeliverableShfrmPriorityAction save(DeliverableShfrmPriorityAction deliverableShfrmPriorityAction) {
    if (deliverableShfrmPriorityAction.getId() == null) {
      super.saveEntity(deliverableShfrmPriorityAction);
    } else {
      deliverableShfrmPriorityAction = super.update(deliverableShfrmPriorityAction);
    }


    return deliverableShfrmPriorityAction;
  }


}