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


import org.cgiar.ccafs.marlo.data.dao.ICenterDeliverableDAO;
import org.cgiar.ccafs.marlo.data.manager.ICenterDeliverableManager;
import org.cgiar.ccafs.marlo.data.model.CenterDeliverable;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class CenterDeliverableManager implements ICenterDeliverableManager {


  private ICenterDeliverableDAO deliverableDAO;

  // Managers


  @Inject
  public CenterDeliverableManager(ICenterDeliverableDAO deliverableDAO) {
    this.deliverableDAO = deliverableDAO;


  }

  @Override
  public void deleteDeliverable(long deliverableId) {

    deliverableDAO.deleteDeliverable(deliverableId);
  }

  @Override
  public boolean existDeliverable(long deliverableID) {

    return deliverableDAO.existDeliverable(deliverableID);
  }

  @Override
  public List<CenterDeliverable> findAll() {

    return deliverableDAO.findAll();

  }

  @Override
  public CenterDeliverable getDeliverableById(long deliverableID) {

    return deliverableDAO.find(deliverableID);
  }

  @Override
  public List<CenterDeliverable> getDeliverablesByUserId(Long userId) {
    return deliverableDAO.getDeliverablesByUserId(userId);
  }

  @Override
  public CenterDeliverable saveDeliverable(CenterDeliverable deliverable) {

    return deliverableDAO.save(deliverable);
  }

  @Override
  public CenterDeliverable saveDeliverable(CenterDeliverable deliverable, String actionName, List<String> relationsName) {
    return deliverableDAO.save(deliverable, actionName, relationsName);
  }


}
