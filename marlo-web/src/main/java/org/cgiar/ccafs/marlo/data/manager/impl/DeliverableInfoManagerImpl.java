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


import org.cgiar.ccafs.marlo.data.dao.DeliverableInfoDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableInfoManager;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class DeliverableInfoManagerImpl implements DeliverableInfoManager {


  private DeliverableInfoDAO deliverableInfoDAO;
  // Managers


  @Inject
  public DeliverableInfoManagerImpl(DeliverableInfoDAO deliverableInfoDAO) {
    this.deliverableInfoDAO = deliverableInfoDAO;


  }

  @Override
  public void deleteDeliverableInfo(long deliverableInfoId) {

    deliverableInfoDAO.deleteDeliverableInfo(deliverableInfoId);
  }

  @Override
  public boolean existDeliverableInfo(long deliverableInfoID) {

    return deliverableInfoDAO.existDeliverableInfo(deliverableInfoID);
  }

  @Override
  public List<DeliverableInfo> findAll() {

    return deliverableInfoDAO.findAll();

  }

  @Override
  public DeliverableInfo getDeliverableInfoById(long deliverableInfoID) {

    return deliverableInfoDAO.find(deliverableInfoID);
  }

  @Override
  public DeliverableInfo saveDeliverableInfo(DeliverableInfo deliverableInfo) {

    return deliverableInfoDAO.save(deliverableInfo);
  }


}
