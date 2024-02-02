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
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.data.dao.DeliverableShfrmPriorityActionDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableShfrmPriorityActionManager;
import org.cgiar.ccafs.marlo.data.model.DeliverableShfrmPriorityAction;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class DeliverableShfrmPriorityActionManagerImpl implements DeliverableShfrmPriorityActionManager {


  private DeliverableShfrmPriorityActionDAO deliverableShfrmPriorityActionDAO;
  // Managers


  @Inject
  public DeliverableShfrmPriorityActionManagerImpl(DeliverableShfrmPriorityActionDAO deliverableShfrmPriorityActionDAO) {
    this.deliverableShfrmPriorityActionDAO = deliverableShfrmPriorityActionDAO;


  }

  @Override
  public void deleteDeliverableShfrmPriorityAction(long deliverableShfrmPriorityActionId) {

    deliverableShfrmPriorityActionDAO.deleteDeliverableShfrmPriorityAction(deliverableShfrmPriorityActionId);
  }

  @Override
  public boolean existDeliverableShfrmPriorityAction(long deliverableShfrmPriorityActionID) {

    return deliverableShfrmPriorityActionDAO.existDeliverableShfrmPriorityAction(deliverableShfrmPriorityActionID);
  }

  @Override
  public List<DeliverableShfrmPriorityAction> findAll() {

    return deliverableShfrmPriorityActionDAO.findAll();

  }

  @Override
  public DeliverableShfrmPriorityAction getDeliverableShfrmPriorityActionById(long deliverableShfrmPriorityActionID) {

    return deliverableShfrmPriorityActionDAO.find(deliverableShfrmPriorityActionID);
  }

  @Override
  public DeliverableShfrmPriorityAction saveDeliverableShfrmPriorityAction(DeliverableShfrmPriorityAction deliverableShfrmPriorityAction) {

    return deliverableShfrmPriorityActionDAO.save(deliverableShfrmPriorityAction);
  }


}
