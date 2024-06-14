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
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

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
  public List<Activity> getActivitiesByProject(long projectId, long phaseId) {
    String query = "from " + Activity.class.getName() + " where project_id=" + projectId + " and id_phase=" + phaseId
      + " and is_active=1 and activityStatus=2";
    List<Activity> list = super.findAll(query);
    if (!list.isEmpty()) {
      return list;
    }
    return null;

  }

  @Override
  public int getActivitiesByProjectAndUserQuantity(long projectId, long phaseId, long projectPersonId) {

    StringBuilder query = new StringBuilder();
    query.append("SELECT count(*) as count from activities as a  ");
    query.append(" join project_partner_persons as ppp ");
    query.append(" on a.leader_id = PPP.ID ");
    query.append(" where project_id =" + projectId);
    query.append(" and id_phase =" + phaseId);
    query.append(" and ppp.id= " + projectPersonId);

    List<Map<String, Object>> rList = super.findCustomQuery(query.toString());
    int activity = 0;

    if (rList != null) {
      for (Map<String, Object> map : rList) {
        activity = Integer.parseInt(map.get("count").toString());
      }
    }

    return activity;

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