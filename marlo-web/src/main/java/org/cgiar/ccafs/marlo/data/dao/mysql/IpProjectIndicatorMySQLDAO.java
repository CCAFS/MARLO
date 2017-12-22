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

import org.cgiar.ccafs.marlo.data.dao.IpProjectIndicatorDAO;
import org.cgiar.ccafs.marlo.data.model.IpProjectIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class IpProjectIndicatorMySQLDAO extends AbstractMarloDAO<IpProjectIndicator, Long>
  implements IpProjectIndicatorDAO {


  @Inject
  public IpProjectIndicatorMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteIpProjectIndicator(long ipProjectIndicatorId) {
    IpProjectIndicator ipProjectIndicator = this.find(ipProjectIndicatorId);
    ipProjectIndicator.setActive(false);
    this.save(ipProjectIndicator);
  }

  @Override
  public boolean existIpProjectIndicator(long ipProjectIndicatorID) {
    IpProjectIndicator ipProjectIndicator = this.find(ipProjectIndicatorID);
    if (ipProjectIndicator == null) {
      return false;
    }
    return true;

  }

  @Override
  public IpProjectIndicator find(long id) {
    return super.find(IpProjectIndicator.class, id);

  }

  @Override
  public List<IpProjectIndicator> findAll() {
    String query = "from " + IpProjectIndicator.class.getName() + " where is_active=1";
    List<IpProjectIndicator> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

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

    List<Map<String, Object>> rList = super.findCustomQuery(query.toString());
    List<IpProjectIndicator> ipIndicators = new ArrayList<>();
    if (rList != null) {
      for (Map<String, Object> map : rList) {
        IpProjectIndicator indicatorDB = super.find(IpProjectIndicator.class, Long.parseLong(map.get("id").toString()));

        ipIndicators.add(indicatorDB);
      }
    }

    return ipIndicators;
  }

  @Override
  public IpProjectIndicator save(IpProjectIndicator ipProjectIndicator) {
    if (ipProjectIndicator.getId() == null) {
      super.saveEntity(ipProjectIndicator);
    } else {
      ipProjectIndicator = super.update(ipProjectIndicator);
    }


    return ipProjectIndicator;
  }

}