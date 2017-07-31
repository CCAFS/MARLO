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

import org.cgiar.ccafs.marlo.data.dao.ICenterMilestoneDAO;
import org.cgiar.ccafs.marlo.data.model.CenterMilestone;

import java.util.List;
import java.util.Map;

import com.google.inject.Inject;

public class CenterMilestoneDAO implements ICenterMilestoneDAO {

  private StandardDAO dao;

  @Inject
  public CenterMilestoneDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteCenterMilestone(long centerMilestoneId) {
    CenterMilestone centerMilestone = this.find(centerMilestoneId);
    centerMilestone.setActive(false);
    return this.save(centerMilestone) > 0;
  }

  @Override
  public boolean existCenterMilestone(long centerMilestoneID) {
    CenterMilestone centerMilestone = this.find(centerMilestoneID);
    if (centerMilestone == null) {
      return false;
    }
    return true;

  }

  @Override
  public CenterMilestone find(long id) {
    return dao.find(CenterMilestone.class, id);

  }

  @Override
  public List<CenterMilestone> findAll() {
    String query = "from " + CenterMilestone.class.getName();
    List<CenterMilestone> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CenterMilestone> getCenterMilestonesByUserId(long userId) {
    String query = "from " + CenterMilestone.class.getName() + " where user_id=" + userId;
    return dao.findAll(query);
  }

  @Override
  public List<Map<String, Object>> getCountTargetUnit(long programID) {
    StringBuilder query = new StringBuilder();

    query.append("SELECT  ");
    query.append("center_target_units.`name` AS targetUnit,  ");
    query.append("Count(center_milestones.id) AS count  ");
    query.append("FROM  ");
    query.append("center_milestones  ");
    query.append("INNER JOIN center_target_units ON center_milestones.target_unit_id = center_target_units.id  ");
    query.append("INNER JOIN center_outcomes ON center_milestones.impact_outcome_id = center_outcomes.id  ");
    query.append("INNER JOIN center_topics ON center_outcomes.research_topic_id = center_topics.id  ");
    query.append("WHERE  ");
    query.append("center_topics.research_program_id = " + programID + " AND ");
    query.append("center_milestones.is_active = 1 AND  ");
    query.append("center_target_units.is_active = 1 AND ");
    query.append("center_outcomes.is_active = 1 ");
    query.append("GROUP BY ");
    query.append("center_target_units.`name`  ");

    return dao.findCustomQuery(query.toString());
  }

  @Override
  public long save(CenterMilestone centerMilestone) {
    if (centerMilestone.getId() == null) {
      dao.save(centerMilestone);
    } else {
      dao.update(centerMilestone);
    }
    return centerMilestone.getId();
  }

  @Override
  public long save(CenterMilestone centerMilestone, String actionName, List<String> relationsName) {
    if (centerMilestone.getId() == null) {
      dao.save(centerMilestone, actionName, relationsName);
    } else {
      dao.update(centerMilestone, actionName, relationsName);
    }
    return centerMilestone.getId();
  }


}