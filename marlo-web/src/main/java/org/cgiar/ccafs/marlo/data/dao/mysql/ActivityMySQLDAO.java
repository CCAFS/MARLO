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

import org.cgiar.ccafs.marlo.data.dao.ActivityDAO;
import org.cgiar.ccafs.marlo.data.model.Activity;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class ActivityMySQLDAO extends AbstractMarloDAO<Activity, Long> implements ActivityDAO {


  @Inject
  public ActivityMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }


  @Override
  public void deleteActivity(long activityId) {
    Activity activity = this.find(activityId);
    activity.setActive(false);
    this.save(activity);
  }

  @Override
  public boolean existActivity(long activityID) {
    Activity activity = this.find(activityID);
    if (activity == null) {
      return false;
    }
    return true;

  }

  @Override
  public Activity find(long id) {
    return super.find(Activity.class, id);

  }

  @Override
  public List<Activity> findAll() {
    String query = "from " + Activity.class.getName() + " where is_active=1";
    List<Activity> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public Activity save(Activity activity) {
    if (activity.getId() == null) {
      activity = super.saveEntity(activity);
    } else {
      activity = super.update(activity);
    }

    return activity;
  }


}