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

import com.google.inject.Inject;

public class ActivityMySQLDAO implements ActivityDAO {

  private StandardDAO dao;

  @Inject
  public ActivityMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }


  @Override
  public boolean deleteActivity(long activityId) {
    Activity activity = this.find(activityId);
    activity.setActive(false);
    boolean result = dao.update(activity);
    return result;
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
    return dao.find(Activity.class, id);

  }

  @Override
  public List<Activity> findAll() {
    String query = "from " + Activity.class.getName() + " where is_active=1";
    List<Activity> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(Activity activity) {
    if (activity.getId() == null) {
      dao.save(activity);
    } else {
      dao.update(activity);
    }

    return activity.getId();
  }


}