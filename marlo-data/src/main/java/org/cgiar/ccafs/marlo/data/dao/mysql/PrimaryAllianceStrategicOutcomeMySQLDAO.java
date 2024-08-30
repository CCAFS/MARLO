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

import org.cgiar.ccafs.marlo.data.dao.PrimaryAllianceStrategicOutcomeDAO;
import org.cgiar.ccafs.marlo.data.model.PrimaryAllianceStrategicOutcome;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class PrimaryAllianceStrategicOutcomeMySQLDAO extends AbstractMarloDAO<PrimaryAllianceStrategicOutcome, Long>
  implements PrimaryAllianceStrategicOutcomeDAO {


  @Inject
  public PrimaryAllianceStrategicOutcomeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deletePrimaryAllianceStrategicOutcome(long primaryAllianceStrategicOutcomeId) {
    PrimaryAllianceStrategicOutcome primaryAllianceStrategicOutcome = this.find(primaryAllianceStrategicOutcomeId);
    primaryAllianceStrategicOutcome.setActive(false);
    this.update(primaryAllianceStrategicOutcome);
  }

  @Override
  public boolean existPrimaryAllianceStrategicOutcome(long primaryAllianceStrategicOutcomeID) {
    PrimaryAllianceStrategicOutcome primaryAllianceStrategicOutcome = this.find(primaryAllianceStrategicOutcomeID);
    if (primaryAllianceStrategicOutcome == null) {
      return false;
    }
    return true;

  }

  @Override
  public PrimaryAllianceStrategicOutcome find(long id) {
    return super.find(PrimaryAllianceStrategicOutcome.class, id);

  }

  @Override
  public List<PrimaryAllianceStrategicOutcome> findAll() {
    String query = "from " + PrimaryAllianceStrategicOutcome.class.getName() + " where is_active=1";
    List<PrimaryAllianceStrategicOutcome> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<PrimaryAllianceStrategicOutcome>
    getPrimaryAllianceStrategicOutcomeByStudyAndPhase(long projectExpectedStudyId, long phaseId) {
    StringBuilder query = new StringBuilder();
    query.append(" select pespso.strategic_outcome_id as id  ");
    query.append(" from project_expected_study_primary_strategic_outcomes as pespso ");
    query.append(" join primary_alliance_strategic_outcomes as paso on pespso.strategic_outcome_id = paso.id ");
    query.append(" where expected_study_id = " + projectExpectedStudyId);
    query.append(" and pespso.id_phase =paso.id_phase ");
    query.append(" and paso.id_phase = " + phaseId);


    List<Map<String, Object>> rList = super.findCustomQuery(query.toString());
    List<PrimaryAllianceStrategicOutcome> primaryAllianceStrategicOutcome = new ArrayList<>();

    if (rList != null) {
      for (Map<String, Object> map : rList) {
        PrimaryAllianceStrategicOutcome primaryAllianceStrategicOutcomeTmp =
          this.find(Long.parseLong(map.get("id").toString()));
        primaryAllianceStrategicOutcome.add(primaryAllianceStrategicOutcomeTmp);
      }
    }

    return primaryAllianceStrategicOutcome;
  }


  @Override
  public PrimaryAllianceStrategicOutcome save(PrimaryAllianceStrategicOutcome primaryAllianceStrategicOutcome) {
    if (primaryAllianceStrategicOutcome.getId() == null) {
      super.saveEntity(primaryAllianceStrategicOutcome);
    } else {
      primaryAllianceStrategicOutcome = super.update(primaryAllianceStrategicOutcome);
    }


    return primaryAllianceStrategicOutcome;
  }


}