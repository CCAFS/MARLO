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

import org.cgiar.ccafs.marlo.data.dao.DeliverableActivityDAO;
import org.cgiar.ccafs.marlo.data.model.DeliverableActivity;

import java.util.List;

import com.google.inject.Inject;

public class DeliverableActivityMySQLDAO implements DeliverableActivityDAO {

  private StandardDAO dao;

  @Inject
  public DeliverableActivityMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteDeliverableActivity(long deliverableActivityId) {
    DeliverableActivity deliverableActivity = this.find(deliverableActivityId);
    deliverableActivity.setActive(false);
    return this.save(deliverableActivity) > 0;
  }

  @Override
  public boolean existDeliverableActivity(long deliverableActivityID) {
    DeliverableActivity deliverableActivity = this.find(deliverableActivityID);
    if (deliverableActivity == null) {
      return false;
    }
    return true;

  }

  @Override
  public DeliverableActivity find(long id) {
    return dao.find(DeliverableActivity.class, id);

  }

  @Override
  public List<DeliverableActivity> findAll() {
    String query = "from " + DeliverableActivity.class.getName() + " where is_active=1";
    List<DeliverableActivity> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public DeliverableActivity findByDeliverableAndActivitie(long deliverableId, long activityId) {
    String query = "from " + DeliverableActivity.class.getName() + " where deliverable_id= " + deliverableId
      + " and activity_id= " + activityId;
    List<DeliverableActivity> list = dao.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public long save(DeliverableActivity deliverableActivity) {
    if (deliverableActivity.getId() == null) {
      dao.save(deliverableActivity);
    } else {
      dao.update(deliverableActivity);
    }


    return deliverableActivity.getId();
  }


}