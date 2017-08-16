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
import org.hibernate.SessionFactory;

public class DeliverableActivityMySQLDAO extends AbstractMarloDAO<DeliverableActivity, Long> implements DeliverableActivityDAO {


  @Inject
  public DeliverableActivityMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
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
    return super.find(DeliverableActivity.class, id);

  }

  @Override
  public List<DeliverableActivity> findAll() {
    String query = "from " + DeliverableActivity.class.getName() + " where is_active=1";
    List<DeliverableActivity> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public DeliverableActivity findByDeliverableAndActivitie(long deliverableId, long activityId) {
    String query = "from " + DeliverableActivity.class.getName() + " where deliverable_id= " + deliverableId
      + " and activity_id= " + activityId;
    List<DeliverableActivity> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public long save(DeliverableActivity deliverableActivity) {
    if (deliverableActivity.getId() == null) {
      super.saveEntity(deliverableActivity);
    } else {
      super.update(deliverableActivity);
    }


    return deliverableActivity.getId();
  }


}