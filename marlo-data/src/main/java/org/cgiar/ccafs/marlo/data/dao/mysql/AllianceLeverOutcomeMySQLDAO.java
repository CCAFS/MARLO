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

import org.cgiar.ccafs.marlo.data.dao.AllianceLeverOutcomeDAO;
import org.cgiar.ccafs.marlo.data.model.AllianceLeverOutcome;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class AllianceLeverOutcomeMySQLDAO extends AbstractMarloDAO<AllianceLeverOutcome, Long>
  implements AllianceLeverOutcomeDAO {


  @Inject
  public AllianceLeverOutcomeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteAllianceLeverOutcome(long allianceLeverOutcomeId) {
    AllianceLeverOutcome allianceLeverOutcome = this.find(allianceLeverOutcomeId);
    allianceLeverOutcome.setActive(false);
    this.update(allianceLeverOutcome);
  }

  @Override
  public boolean existAllianceLeverOutcome(long allianceLeverOutcomeID) {
    AllianceLeverOutcome allianceLeverOutcome = this.find(allianceLeverOutcomeID);
    if (allianceLeverOutcome == null) {
      return false;
    }
    return true;

  }

  @Override
  public AllianceLeverOutcome find(long id) {
    return super.find(AllianceLeverOutcome.class, id);

  }

  @Override
  public List<AllianceLeverOutcome> findAll() {
    String query = "from " + AllianceLeverOutcome.class.getName() + " where is_active=1";
    List<AllianceLeverOutcome> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<AllianceLeverOutcome> findAllianceLeverOutcomeByExpectedPhaseAndLever(long phase, long expectedId,
    long lever) {
    StringBuilder query = new StringBuilder();
    query.append(" select distinct alo.id from alliance_lever_outcomes as alo ");
    query.append(" join project_expected_study_alliance_levers_outcomes as pesalo ");
    query.append(" on alo.id = pesalo.lever_outcome_id ");
    query.append(" where alo.is_active =pesalo.is_active ");
    query.append(" and pesalo.is_active =1 ");
    query.append(" and pesalo.id_phase=" + phase);
    query.append(" and pesalo.expected_id=" + expectedId);
    query.append(" and pesalo.alliance_lever_id=" + lever);

    List<Map<String, Object>> rList = super.findCustomQuery(query.toString());
    List<AllianceLeverOutcome> allianceLeverOutcomeList = new ArrayList<>();

    if (rList != null) {
      for (Map<String, Object> map : rList) {
        AllianceLeverOutcome allianceLeverOutcome = this.find(Long.parseLong(map.get("id").toString()));
        allianceLeverOutcomeList.add(allianceLeverOutcome);
      }
    }

    return allianceLeverOutcomeList;
  }


  @Override
  public AllianceLeverOutcome save(AllianceLeverOutcome allianceLeverOutcome) {
    if (allianceLeverOutcome.getId() == null) {
      super.saveEntity(allianceLeverOutcome);
    } else {
      allianceLeverOutcome = super.update(allianceLeverOutcome);
    }


    return allianceLeverOutcome;
  }


}