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


import org.cgiar.ccafs.marlo.data.dao.DeliverableShfrmSubActionDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableShfrmSubActionManager;
import org.cgiar.ccafs.marlo.data.model.DeliverableShfrmSubAction;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class DeliverableShfrmSubActionManagerImpl implements DeliverableShfrmSubActionManager {


  private DeliverableShfrmSubActionDAO deliverableShfrmSubActionDAO;
  // Managers


  @Inject
  public DeliverableShfrmSubActionManagerImpl(DeliverableShfrmSubActionDAO deliverableShfrmSubActionDAO) {
    this.deliverableShfrmSubActionDAO = deliverableShfrmSubActionDAO;


  }

  @Override
  public void deleteDeliverableShfrmSubAction(long deliverableShfrmSubActionId) {

    deliverableShfrmSubActionDAO.deleteDeliverableShfrmSubAction(deliverableShfrmSubActionId);
  }

  @Override
  public boolean existDeliverableShfrmSubAction(long deliverableShfrmSubActionID) {

    return deliverableShfrmSubActionDAO.existDeliverableShfrmSubAction(deliverableShfrmSubActionID);
  }

  @Override
  public List<DeliverableShfrmSubAction> findAll() {

    return deliverableShfrmSubActionDAO.findAll();

  }

  @Override
  public DeliverableShfrmSubAction getDeliverableShfrmSubActionById(long deliverableShfrmSubActionID) {

    return deliverableShfrmSubActionDAO.find(deliverableShfrmSubActionID);
  }

  @Override
  public DeliverableShfrmSubAction saveDeliverableShfrmSubAction(DeliverableShfrmSubAction deliverableShfrmSubAction) {

    return deliverableShfrmSubActionDAO.save(deliverableShfrmSubAction);
  }


}
