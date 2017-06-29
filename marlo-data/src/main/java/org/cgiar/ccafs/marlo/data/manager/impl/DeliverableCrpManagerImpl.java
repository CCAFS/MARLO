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


import org.cgiar.ccafs.marlo.data.dao.DeliverableCrpDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableCrpManager;
import org.cgiar.ccafs.marlo.data.model.DeliverableCrp;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class DeliverableCrpManagerImpl implements DeliverableCrpManager {


  private DeliverableCrpDAO deliverableCrpDAO;
  // Managers


  @Inject
  public DeliverableCrpManagerImpl(DeliverableCrpDAO deliverableCrpDAO) {
    this.deliverableCrpDAO = deliverableCrpDAO;


  }

  @Override
  public boolean deleteDeliverableCrp(long deliverableCrpId) {

    return deliverableCrpDAO.deleteDeliverableCrp(deliverableCrpId);
  }

  @Override
  public boolean existDeliverableCrp(long deliverableCrpID) {

    return deliverableCrpDAO.existDeliverableCrp(deliverableCrpID);
  }

  @Override
  public List<DeliverableCrp> findAll() {

    return deliverableCrpDAO.findAll();

  }

  @Override
  public DeliverableCrp getDeliverableCrpById(long deliverableCrpID) {

    return deliverableCrpDAO.find(deliverableCrpID);
  }

  @Override
  public long saveDeliverableCrp(DeliverableCrp deliverableCrp) {

    return deliverableCrpDAO.save(deliverableCrp);
  }


}
