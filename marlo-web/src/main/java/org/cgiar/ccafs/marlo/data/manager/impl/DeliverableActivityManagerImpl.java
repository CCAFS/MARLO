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


import org.cgiar.ccafs.marlo.data.dao.DeliverableActivityDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableActivityManager;
import org.cgiar.ccafs.marlo.data.model.DeliverableActivity;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class DeliverableActivityManagerImpl implements DeliverableActivityManager {


  private DeliverableActivityDAO deliverableActivityDAO;
  // Managers


  @Inject
  public DeliverableActivityManagerImpl(DeliverableActivityDAO deliverableActivityDAO) {
    this.deliverableActivityDAO = deliverableActivityDAO;


  }

  @Override
  public void deleteDeliverableActivity(long deliverableActivityId) {

    deliverableActivityDAO.deleteDeliverableActivity(deliverableActivityId);
  }

  @Override
  public boolean existDeliverableActivity(long deliverableActivityID) {

    return deliverableActivityDAO.existDeliverableActivity(deliverableActivityID);
  }

  @Override
  public List<DeliverableActivity> findAll() {

    return deliverableActivityDAO.findAll();

  }

  @Override
  public DeliverableActivity findByDeliverableAndActivitie(long deliverableId, long activityId) {
    return deliverableActivityDAO.findByDeliverableAndActivitie(deliverableId, activityId);
  }

  @Override
  public DeliverableActivity getDeliverableActivityById(long deliverableActivityID) {

    return deliverableActivityDAO.find(deliverableActivityID);
  }

  @Override
  public DeliverableActivity saveDeliverableActivity(DeliverableActivity deliverableActivity) {

    return deliverableActivityDAO.save(deliverableActivity);
  }


}
