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

import org.cgiar.ccafs.marlo.data.dao.ICenterOutcomeDAO;
import org.cgiar.ccafs.marlo.data.model.CenterOutcome;

import java.util.List;
import java.util.Map;

import com.google.inject.Inject;

public class CenterOutcomeDAO implements ICenterOutcomeDAO {

  private StandardDAO dao;

  @Inject
  public CenterOutcomeDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteResearchOutcome(long researchOutcomeId) {
    CenterOutcome researchOutcome = this.find(researchOutcomeId);
    researchOutcome.setActive(false);
    return this.save(researchOutcome) > 0;
  }

  @Override
  public boolean existResearchOutcome(long researchOutcomeID) {
    CenterOutcome researchOutcome = this.find(researchOutcomeID);
    if (researchOutcome == null) {
      return false;
    }
    return true;

  }

  @Override
  public CenterOutcome find(long id) {
    return dao.find(CenterOutcome.class, id);

  }

  @Override
  public List<CenterOutcome> findAll() {
    String query = "from " + CenterOutcome.class.getName();
    List<CenterOutcome> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<Map<String, Object>> getCountTargetUnit(long programID) {
    StringBuilder query = new StringBuilder();

    query.append("SELECT  ");
    query.append("center_target_units.`name` AS targetUnit,  ");
    query.append("Count(center_outcomes.id) AS count  ");
    query.append("FROM  ");
    query.append("center_outcomes  ");
    query.append("INNER JOIN center_target_units ON center_outcomes.target_unit_id = center_target_units.id  ");
    query.append("INNER JOIN center_topics ON center_outcomes.research_topic_id = center_topics.id  ");
    query.append("WHERE  ");
    query.append("center_outcomes.is_active = 1 AND  ");
    query.append("center_target_units.is_active = 1 AND  ");
    query.append("center_topics.research_program_id = " + programID);
    query.append(" GROUP BY ");
    query.append("center_target_units.`name`  ");

    return dao.findCustomQuery(query.toString());
  }

  @Override
  public List<Map<String, Object>> getImpactPathwayOutcomes(long programID) {
    StringBuilder query = new StringBuilder();

    query.append("SELECT  ");
    query.append("center_outcomes.id AS outcomeId,  ");
    query.append("center_outcomes.description AS outcomeDesc,  ");
    query.append("center_impacts.description AS impactStatement,  ");
    query.append("center_topics.research_topic AS topic,  ");
    query.append("t1.`name` AS outcomeTargetUnit,  ");
    query.append("center_outcomes.`value` AS outcomeValue,  ");
    query.append("center_outcomes.`year` AS outcomeYear,  ");
    query.append("center_milestones.id AS milestoneId,  ");
    query.append("center_milestones.title AS milestoneDesc,  ");
    query.append("t2.`name` AS milestoneTargetUnit,  ");
    query.append("center_milestones.`value` AS milestoneValue,  ");
    query.append("center_milestones.target_year AS milestoneYear  ");
    query.append("FROM  ");
    query.append("center_outcomes  ");
    query.append("INNER JOIN center_milestones ON center_milestones.impact_outcome_id = center_outcomes.id  ");
    query.append("INNER JOIN center_impacts ON center_outcomes.research_impact_id = center_impacts.id  ");
    query.append("INNER JOIN center_topics ON center_outcomes.research_topic_id = center_topics.id  ");
    query.append("INNER JOIN center_target_units t1 ON center_outcomes.target_unit_id = t1.id  ");
    query.append("INNER JOIN center_target_units t2 ON center_milestones.target_unit_id = t2.id  ");
    query.append("WHERE  ");
    query.append("center_topics.research_program_id = " + programID + " AND  ");
    query.append("center_outcomes.is_active = 1 AND  ");
    query.append("center_impacts.is_active = 1 AND  ");
    query.append("center_topics.is_active = 1 AND  ");
    query.append("t1.is_active = 1 AND  ");
    query.append("t2.is_active = 1 AND  ");
    query.append("center_milestones.is_active = 1  ");
    query.append("ORDER BY  ");
    query.append("outcomeId ASC  ");

    return dao.findCustomQuery(query.toString());
  }

  @Override
  public List<CenterOutcome> getResearchOutcomesByUserId(long userId) {
    String query = "from " + CenterOutcome.class.getName() + " where user_id=" + userId;
    return dao.findAll(query);
  }

  @Override
  public long save(CenterOutcome researchOutcome) {
    if (researchOutcome.getId() == null) {
      dao.save(researchOutcome);
    } else {
      dao.update(researchOutcome);
    }
    return researchOutcome.getId();
  }

  @Override
  public long save(CenterOutcome outcome, String actionName, List<String> relationsName) {
    if (outcome.getId() == null) {
      dao.save(outcome, actionName, relationsName);
    } else {
      dao.update(outcome, actionName, relationsName);
    }
    return outcome.getId();
  }


}