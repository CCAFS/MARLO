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
    query.append("co.id AS outcomeId,  ");
    query.append("(  ");
    query.append("CASE  ");
    query.append("WHEN co.description IS NULL  ");
    query.append("OR co.description = '' THEN  ");
    query.append("'<Not Defined>'  ");
    query.append("ELSE  ");
    query.append("co.description  ");
    query.append("END  ");
    query.append(") AS outcomeDesc,  ");
    query.append("(  ");
    query.append("CASE  ");
    query.append("WHEN center_impacts.description IS NULL  ");
    query.append("OR center_impacts.description = '' THEN  ");
    query.append("'<Not Defined>'  ");
    query.append("ELSE  ");
    query.append("center_impacts.description  ");
    query.append("END  ");
    query.append(") AS impactStatement,  ");
    query.append("center_topics.research_topic AS topic,  ");
    query.append("t1.`name` AS outcomeTargetUnit,  ");
    query.append("(  ");
    query.append("CASE  ");
    query.append("WHEN co.`value` IS NULL  ");
    query.append("OR co.`value` = '' THEN  ");
    query.append("'<Not Defined>'  ");
    query.append("ELSE  ");
    query.append("co.`value`  ");
    query.append("END  ");
    query.append(") AS outcomeValue,  ");
    query.append("(  ");
    query.append("CASE  ");
    query.append("WHEN co.`year` IS NULL  ");
    query.append("OR co.`year` = '-1' THEN  ");
    query.append("'<Not Defined>'  ");
    query.append("ELSE  ");
    query.append("co.`year`  ");
    query.append("END  ");
    query.append(") AS outcomeYear,  ");
    query.append("center_milestones.id AS milestoneId,  ");
    query.append("(  ");
    query.append("CASE  ");
    query.append("WHEN center_milestones.title IS NULL  ");
    query.append("OR center_milestones.title = '' THEN  ");
    query.append("'<Not Defined>'  ");
    query.append("ELSE  ");
    query.append("center_milestones.title  ");
    query.append("END  ");
    query.append(") AS milestoneDesc,  ");
    query.append("t2.`name` AS milestoneTargetUnit,  ");
    query.append("(  ");
    query.append("CASE  ");
    query.append("WHEN center_milestones.`value` IS NULL  ");
    query.append("OR center_milestones.`value` = '' THEN  ");
    query.append("'<Not Defined>'  ");
    query.append("ELSE  ");
    query.append("center_milestones.`value`  ");
    query.append("END  ");
    query.append(") AS milestoneValue,  ");
    query.append("(  ");
    query.append("CASE  ");
    query.append("WHEN center_milestones.target_year IS NULL  ");
    query.append("OR center_milestones.target_year = '-1' THEN  ");
    query.append("'<Not Defined>'  ");
    query.append("ELSE  ");
    query.append("center_milestones.target_year  ");
    query.append("END  ");
    query.append(") AS milestoneYear  ");
    query.append("FROM  ");
    query.append("center_outcomes co  ");
    query.append("INNER JOIN center_milestones ON center_milestones.impact_outcome_id = co.id  ");
    query.append("INNER JOIN center_impacts ON co.research_impact_id = center_impacts.id  ");
    query.append("INNER JOIN center_topics ON co.research_topic_id = center_topics.id  ");
    query.append("INNER JOIN center_target_units t1 ON co.target_unit_id = t1.id  ");
    query.append("INNER JOIN center_target_units t2 ON center_milestones.target_unit_id = t2.id  ");
    query.append("WHERE  ");
    query.append("center_topics.research_program_id = " + programID + "  ");
    query.append("AND co.is_active = 1  ");
    query.append("AND center_impacts.is_active = 1  ");
    query.append("AND center_topics.is_active = 1  ");
    query.append("AND center_milestones.is_active = 1  ");
    query.append("ORDER BY  ");
    query.append("co.id ASC  ");


    System.out.println(query.toString());

    return dao.findCustomQuery(query.toString());
  }

  @Override
  public List<Map<String, Object>> getMonitoringOutcomes(long programID) {
    StringBuilder query = new StringBuilder();

    query.append("SELECT  ");
    query.append("center_outcomes.id AS outcomeId,  ");
    query.append("(  ");
    query.append("CASE  ");
    query.append("WHEN center_outcomes.description IS NULL  ");
    query.append("OR center_outcomes.description = '' THEN  ");
    query.append("'<Not Defined>'  ");
    query.append("ELSE  ");
    query.append("center_outcomes.description  ");
    query.append("END  ");
    query.append(") AS outcomeDesc,  ");
    query.append("(  ");
    query.append("CASE  ");
    query.append("WHEN center_impacts.description IS NULL  ");
    query.append("OR center_impacts.description = '' THEN  ");
    query.append("'<Not Defined>'  ");
    query.append("ELSE  ");
    query.append("center_impacts.description  ");
    query.append("END  ");
    query.append(") AS impactStatement,  ");
    query.append("center_topics.research_topic AS topic,  ");
    query.append("center_target_units.`name` AS outcomeTargetUnit,  ");
    query.append("center_monitoring_outcomes.`year` AS monitoringYear,  ");
    query.append("(  ");
    query.append("CASE  ");
    query.append("WHEN center_monitoring_outcomes.narrative IS NULL  ");
    query.append("OR center_monitoring_outcomes.narrative = '' THEN  ");
    query.append("'<Not Defined>'  ");
    query.append("ELSE  ");
    query.append("center_monitoring_outcomes.narrative  ");
    query.append("END  ");
    query.append(") AS monitoringProgress  ");
    query.append("FROM  ");
    query.append("center_outcomes  ");
    query.append("INNER JOIN center_impacts ON center_outcomes.research_impact_id = center_impacts.id  ");
    query.append("INNER JOIN center_topics ON center_outcomes.research_topic_id = center_topics.id  ");
    query.append("INNER JOIN center_target_units ON center_outcomes.target_unit_id = center_target_units.id  ");
    query
      .append("INNER JOIN center_monitoring_outcomes ON center_monitoring_outcomes.outcome_id = center_outcomes.id ");
    query.append("WHERE  ");
    query.append("center_topics.research_program_id = " + programID + "  ");
    query.append("AND center_outcomes.is_active = 1  ");
    query.append("AND center_impacts.is_active = 1  ");
    query.append("AND center_topics.is_active = 1  ");
    query.append("AND center_monitoring_outcomes.is_active = 1  ");
    query.append("ORDER BY  ");
    query.append("center_outcomes.id ASC,  ");
    query.append("center_monitoring_outcomes.`year` ASC  ");


    System.out.println(query.toString());

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