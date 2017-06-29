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

import org.cgiar.ccafs.marlo.data.dao.IpIndicatorDAO;
import org.cgiar.ccafs.marlo.data.model.IpIndicator;
import org.cgiar.ccafs.marlo.data.model.IpProjectIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;

public class IpIndicatorMySQLDAO implements IpIndicatorDAO {

  private StandardDAO dao;

  @Inject
  public IpIndicatorMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteIpIndicator(long ipIndicatorId) {
    IpIndicator ipIndicator = this.find(ipIndicatorId);
    ipIndicator.setActive(false);
    return this.save(ipIndicator) > 0;
  }

  @Override
  public boolean existIpIndicator(long ipIndicatorID) {
    IpIndicator ipIndicator = this.find(ipIndicatorID);
    if (ipIndicator == null) {
      return false;
    }
    return true;

  }

  @Override
  public IpIndicator find(long id) {
    return dao.find(IpIndicator.class, id);

  }

  @Override
  public List<IpIndicator> findAll() {
    String query = "from " + IpIndicator.class.getName() + " where is_active=1";
    List<IpIndicator> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<IpIndicator> findOtherContributions(long projectID) {

    StringBuilder sb = new StringBuilder();

    sb.append("    SELECT     distinct  i.*");
    sb.append("                          FROM       ip_indicators i ");
    sb.append("                          LEFT JOIN  ip_indicators p ");
    sb.append("                          ON         i.parent_id = p.id ");
    sb.append("                         INNER JOIN ip_project_indicators ipi ");
    sb.append("                         ON         i.id = ipi.parent_id ");
    sb.append("                         INNER JOIN ip_elements ie ");
    sb.append("                         ON         ipi.outcome_id = ie.id ");
    sb.append("      inner  JOIN ip_programs prog on prog.id=ie.ip_program_id and prog.type_id=4");
    sb.append("    where i.id not in(");
    sb.append("    SELECT     distinct  i.id ");
    sb.append("                         FROM       ip_indicators i ");
    sb.append("                         LEFT JOIN  ip_indicators p ");
    sb.append("                         ON         i.parent_id = p.id ");
    sb.append("                         INNER JOIN ip_project_indicators ipi ");
    sb.append("                         ON         i.id = ipi.parent_id ");
    sb.append("                         INNER JOIN ip_elements ie ");
    sb.append("                         ON         ipi.outcome_id = ie.id ");
    sb.append("                         WHERE      ipi.project_id =" + projectID + ") ");


    String query = sb.toString();;
    List<Map<String, Object>> rList = dao.findCustomQuery(query);
    List<IpIndicator> ipIndicators = new ArrayList<>();
    if (rList != null) {
      for (Map<String, Object> map : rList) {
        IpIndicator indicator = this.find(Long.parseLong(map.get("id").toString()));
        ipIndicators.add(indicator);

      }
    }

    return ipIndicators;
  }

  @Override
  public List<IpIndicator> getIndicatorsByElementID(long elementID) {
    StringBuilder query = new StringBuilder();
    query.append("SELECT i.id, i.description, i.target, p.id as 'parent_id', p.description as 'parent_description' ");
    query.append("FROM ip_indicators i ");
    query.append("LEFT JOIN ip_indicators p ON i.parent_id = p.id ");
    query.append("WHERE i.ip_element_id = ");
    query.append(elementID);
    List<Map<String, Object>> rList = dao.findCustomQuery(query.toString());
    List<IpIndicator> ipIndicators = new ArrayList<>();
    if (rList != null) {
      for (Map<String, Object> map : rList) {
        IpIndicator indicator = this.find(Long.parseLong(map.get("id").toString()));

        ipIndicators.add(indicator);
      }
    }

    return ipIndicators;
  }

  @Override
  public List<IpIndicator> getIndicatorsFlagShips() {

    StringBuilder query = new StringBuilder();


    query.append("SELECT DISTINCT i.id, ");
    query.append("                i.* ,prog.acronym");
    query.append(" FROM   ip_indicators i ");
    query.append("       LEFT JOIN ip_indicators p ");
    query.append("              ON i.parent_id = p.id ");
    query.append("       INNER JOIN ip_project_indicators ipi ");
    query.append("               ON i.id = ipi.parent_id ");
    query.append("       INNER JOIN ip_elements ie ");
    query.append("               ON ipi.outcome_id = ie.id ");
    query.append("        inner  JOIN ip_programs prog on prog.id=ie.ip_program_id and prog.type_id=4");

    List<Map<String, Object>> rList = dao.findCustomQuery(query.toString());
    List<IpIndicator> ipIndicators = new ArrayList<>();
    if (rList != null) {
      for (Map<String, Object> map : rList) {
        IpIndicator indicator = this.find(Long.parseLong(map.get("id").toString()));
        indicator.setDescription(map.get("acronym").toString() + "-" + indicator.getDescription());
        ipIndicators.add(indicator);
      }
    }

    return ipIndicators;
  }

  @Override
  public List<IpProjectIndicator> getProjectIndicators(int year, long indicator, long program, long midOutcome) {
    StringBuilder query = new StringBuilder();
    query.append("SELECT ai.id,ai.project_id, ai.description, ai.gender, ai.target, ai.year, aip.id as 'parent_id', ");
    query.append("aip.description as 'parent_description', aip.target as 'parent_target', ");
    query.append(
      "ie.id as 'outcome_id', ie.description as 'outcome_description',ai.archived,ai.narrative_gender,ai.narrative_targets ");
    query.append("FROM ip_project_indicators as ai ");
    query.append("INNER JOIN ip_indicators aip ON ai.parent_id = aip.id ");
    query.append("INNER JOIN ip_elements ie ON ai.outcome_id = ie.id ");
    query.append("WHERE ai.is_active = TRUE and aip.id=" + indicator + " and ai.year=" + year + " and ie.ip_program_id="
      + program + " and ie.id=" + midOutcome);

    List<Map<String, Object>> rList = dao.findCustomQuery(query.toString());
    List<IpProjectIndicator> ipIndicators = new ArrayList<>();
    if (rList != null) {
      for (Map<String, Object> map : rList) {
        IpProjectIndicator indicatorDB = dao.find(IpProjectIndicator.class, Long.parseLong(map.get("id").toString()));

        ipIndicators.add(indicatorDB);
      }
    }

    return ipIndicators;
  }

  @Override
  public long save(IpIndicator ipIndicator) {
    if (ipIndicator.getId() == null) {
      dao.save(ipIndicator);
    } else {
      dao.update(ipIndicator);
    }


    return ipIndicator.getId();
  }


}