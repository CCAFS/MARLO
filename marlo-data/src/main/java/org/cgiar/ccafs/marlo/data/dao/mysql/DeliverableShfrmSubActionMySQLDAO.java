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

import org.cgiar.ccafs.marlo.data.dao.DeliverableShfrmSubActionDAO;
import org.cgiar.ccafs.marlo.data.model.DeliverableShfrmSubAction;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class DeliverableShfrmSubActionMySQLDAO extends AbstractMarloDAO<DeliverableShfrmSubAction, Long>
  implements DeliverableShfrmSubActionDAO {


  @Inject
  public DeliverableShfrmSubActionMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteDeliverableShfrmSubAction(long deliverableShfrmSubActionId) {
    DeliverableShfrmSubAction deliverableShfrmSubAction = this.find(deliverableShfrmSubActionId);
    /*
     * deliverableShfrmSubAction.setActive(false);
     * this.update(deliverableShfrmSubAction);
     */
    this.delete(deliverableShfrmSubAction);

  }

  @Override
  public boolean existDeliverableShfrmSubAction(long deliverableShfrmSubActionID) {
    DeliverableShfrmSubAction deliverableShfrmSubAction = this.find(deliverableShfrmSubActionID);
    if (deliverableShfrmSubAction == null) {
      return false;
    }
    return true;

  }

  @Override
  public DeliverableShfrmSubAction find(long id) {
    return super.find(DeliverableShfrmSubAction.class, id);

  }

  @Override
  public List<DeliverableShfrmSubAction> findAll() {
    String query = "from " + DeliverableShfrmSubAction.class.getName() + " where is_active=1";
    List<DeliverableShfrmSubAction> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }


  @Override
  public List<DeliverableShfrmSubAction> findByPriorityActionAndPhase(long priorityActionId, long phaseId) {
    String query = "from " + DeliverableShfrmSubAction.class.getName()
      + " where is_active=1 and deliverable_shfrm_priority_action_id=" + priorityActionId + " and id_phase=" + phaseId;
    List<DeliverableShfrmSubAction> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<DeliverableShfrmSubAction> findByPriorityActionPhaseAndSubAction(long priorityActionId, long phaseId,
    long shfrmSubActionId) {
    String query = "from " + DeliverableShfrmSubAction.class.getName()
      + " where is_active=1 and deliverable_shfrm_priority_action_id=" + priorityActionId + " and id_phase=" + phaseId
      + " and shfrm_sub_action_id=" + shfrmSubActionId;
    List<DeliverableShfrmSubAction> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public DeliverableShfrmSubAction save(DeliverableShfrmSubAction deliverableShfrmSubAction) {
    if (deliverableShfrmSubAction.getId() == null) {
      super.saveEntity(deliverableShfrmSubAction);
    } else {
      deliverableShfrmSubAction = super.update(deliverableShfrmSubAction);
    }


    return deliverableShfrmSubAction;
  }


}