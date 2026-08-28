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

import java.util.ArrayList;
import java.util.Collections;
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
  public List<Activity> getActiveActivitiesByProject(long projectId, long phaseId) {
    String query = "from " + Activity.class.getName() + " where project_id=" + projectId + " and id_phase=" + phaseId
      + " and is_active=1";
    List<Activity> list = super.findAll(query);
    if (!list.isEmpty()) {
      return list;
    }
    return Collections.emptyList();
  }

  @Override
  public List<Map<String, Object>> getActivityTitleRelations(long globalUnitId, long currentPhaseId) {
    StringBuilder query = new StringBuilder();
    query.append("select a.title_id as titleId");
    query.append(", a.project_id as clusterId");
    query.append(", a.composed_id as composedId");
    query.append(", a.id as activityId");
    query.append(", a.is_active as activityActive");
    query.append(", a.description as activityDescription");
    query.append(", a.id_phase as phaseId");
    query.append(", ph.name as phaseName");
    query.append(", ph.year as phaseYear");
    // Cluster title of the current phase, falling back to the latest phase where the cluster was described. Scalar
    // subqueries are used on purpose: joining projects_info would multiply the activity rows.
    query.append(", coalesce(");
    query.append("(select pic.title from projects_info pic where pic.project_id = a.project_id");
    query.append(" and pic.id_phase = " + currentPhaseId + " and pic.is_active = 1 limit 1)");
    query.append(", (select pil.title from projects_info pil inner join phases phl on phl.id = pil.id_phase");
    query.append(" where pil.project_id = a.project_id and pil.is_active = 1");
    query.append(" order by phl.year desc, phl.id desc limit 1)");
    query.append(") as clusterTitle");
    query.append(" from activities a");
    query.append(" inner join activities_titles att on att.id = a.title_id");
    query.append(" inner join phases ph on ph.id = a.id_phase");
    query.append(" where att.global_unit_id = " + globalUnitId);
    // A year holds up to three phases (POWB, AR, UpKeep) and their ids were reassigned over time, so start_date
    // is what puts them in chronological order; id is only the last tie breaker.
    query.append(" order by a.project_id asc, a.composed_id asc, ph.year asc, ph.start_date asc, ph.id asc");

    List<Map<String, Object>> list = super.findCustomQuery(query.toString());
    if (list == null) {
      return new ArrayList<>();
    }
    return list;
  }

  @Override
  public List<Activity> getActivitiesByComposedID(String composedID, long phaseId) {
    String query = "from " + Activity.class.getName() + " where composed_id=" + composedID + " and id_phase=" + phaseId
      + " and is_active=1";
    List<Activity> list = super.findAll(query);
    if (!list.isEmpty()) {
      return list;
    }
    return Collections.emptyList();
  }


  @Override
  public List<Activity> getActivitiesByComposedIDPhaseIDProjectID(String composedID, long phaseId, long projectId) {
    String query = "from " + Activity.class.getName() + " where composed_id=" + composedID + " and id_phase=" + phaseId
      + " and project_id=" + projectId + " and is_active=1";
    List<Activity> list = super.findAll(query);
    if (!list.isEmpty()) {
      return list;
    }
    return Collections.emptyList();
  }

  @Override
  public int getActivitiesByDeliverableAndPhaseQuantity(long deliverableId, long phaseId) {

    StringBuilder query = new StringBuilder();
    query.append("SELECT count(*) as count from deliverable_activities da  ");
    query.append(" where deliverable_id= " + deliverableId);
    query.append(" and id_phase = " + phaseId);
    query.append(" and is_active =1 ");

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