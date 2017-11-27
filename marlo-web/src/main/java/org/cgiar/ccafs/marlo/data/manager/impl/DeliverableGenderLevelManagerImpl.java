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


import org.cgiar.ccafs.marlo.data.dao.DeliverableGenderLevelDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableGenderLevelManager;
import org.cgiar.ccafs.marlo.data.model.DeliverableGenderLevel;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class DeliverableGenderLevelManagerImpl implements DeliverableGenderLevelManager {


  private DeliverableGenderLevelDAO deliverableGenderLevelDAO;
  // Managers


  @Inject
  public DeliverableGenderLevelManagerImpl(DeliverableGenderLevelDAO deliverableGenderLevelDAO) {
    this.deliverableGenderLevelDAO = deliverableGenderLevelDAO;


  }

  @Override
  public void deleteDeliverableGenderLevel(long deliverableGenderLevelId) {

    deliverableGenderLevelDAO.deleteDeliverableGenderLevel(deliverableGenderLevelId);
  }

  @Override
  public boolean existDeliverableGenderLevel(long deliverableGenderLevelID) {

    return deliverableGenderLevelDAO.existDeliverableGenderLevel(deliverableGenderLevelID);
  }

  @Override
  public List<DeliverableGenderLevel> findAll() {

    return deliverableGenderLevelDAO.findAll();

  }

  @Override
  public DeliverableGenderLevel getDeliverableGenderLevelById(long deliverableGenderLevelID) {

    return deliverableGenderLevelDAO.find(deliverableGenderLevelID);
  }

  @Override
  public DeliverableGenderLevel saveDeliverableGenderLevel(DeliverableGenderLevel deliverableGenderLevel) {

    return deliverableGenderLevelDAO.save(deliverableGenderLevel);
  }


}
