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


import org.cgiar.ccafs.marlo.data.dao.DeliverableDisseminationDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableDisseminationManager;
import org.cgiar.ccafs.marlo.data.model.DeliverableDissemination;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class DeliverableDisseminationManagerImpl implements DeliverableDisseminationManager {


  private DeliverableDisseminationDAO deliverableDisseminationDAO;
  // Managers


  @Inject
  public DeliverableDisseminationManagerImpl(DeliverableDisseminationDAO deliverableDisseminationDAO) {
    this.deliverableDisseminationDAO = deliverableDisseminationDAO;


  }

  @Override
  public void deleteDeliverableDissemination(long deliverableDisseminationId) {

    deliverableDisseminationDAO.deleteDeliverableDissemination(deliverableDisseminationId);
  }

  @Override
  public boolean existDeliverableDissemination(long deliverableDisseminationID) {

    return deliverableDisseminationDAO.existDeliverableDissemination(deliverableDisseminationID);
  }

  @Override
  public List<DeliverableDissemination> findAll() {

    return deliverableDisseminationDAO.findAll();

  }

  @Override
  public DeliverableDissemination getDeliverableDisseminationById(long deliverableDisseminationID) {

    return deliverableDisseminationDAO.find(deliverableDisseminationID);
  }

  @Override
  public DeliverableDissemination saveDeliverableDissemination(DeliverableDissemination deliverableDissemination) {

    return deliverableDisseminationDAO.save(deliverableDissemination);
  }


}
