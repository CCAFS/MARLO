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

import org.cgiar.ccafs.marlo.data.dao.CrpProgramOutcomeIndicatorDAO;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcomeIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

@Named

public class CrpProgramOutcomeIndicatorMySQLDAO extends AbstractMarloDAO<CrpProgramOutcomeIndicator, Long>
  implements CrpProgramOutcomeIndicatorDAO {


  @Inject
  public CrpProgramOutcomeIndicatorMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteCrpProgramOutcomeIndicator(long crpProgramOutcomeIndicatorId) {
    CrpProgramOutcomeIndicator crpProgramOutcomeIndicator = this.find(crpProgramOutcomeIndicatorId);
    crpProgramOutcomeIndicator.setActive(false);
    this.save(crpProgramOutcomeIndicator);
  }

  @Override
  public boolean existCrpProgramOutcomeIndicator(long crpProgramOutcomeIndicatorID) {
    CrpProgramOutcomeIndicator crpProgramOutcomeIndicator = this.find(crpProgramOutcomeIndicatorID);
    if (crpProgramOutcomeIndicator == null) {
      return false;
    }
    return true;

  }

  @Override
  public CrpProgramOutcomeIndicator find(long id) {
    return super.find(CrpProgramOutcomeIndicator.class, id);

  }

  @Override
  public List<CrpProgramOutcomeIndicator> findAll() {
    String query = "from " + CrpProgramOutcomeIndicator.class.getName() + " where is_active=1";
    List<CrpProgramOutcomeIndicator> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public CrpProgramOutcomeIndicator getCrpProgramOutcomeIndicator(String composedId,
    CrpProgramOutcome crpProgramOutcome) {
    String query = "select distinct pp from CrpProgramOutcomeIndicator  pp "
      + "where composeID=:id and crpProgramOutcome.id=:crpProgramOutcomeID and active=true";
    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("id", composedId);
    createQuery.setParameter("crpProgramOutcomeID", crpProgramOutcome.getId());
    Object findSingleResult = super.findSingleResult(CrpProgramOutcomeIndicator.class, createQuery);
    CrpProgramOutcomeIndicator crpProgramOutcomeIndicator = (CrpProgramOutcomeIndicator) findSingleResult;


    // projectPartner.getProjectPartnerLocations().size();

    return crpProgramOutcomeIndicator;
  }

  @Override
  public List<CrpProgramOutcomeIndicator> getCrpProgramOutcomeIndicatorByOutcome(CrpProgramOutcome crpProgramOutcome) {

    String query = "from " + CrpProgramOutcomeIndicator.class.getName()
      + " where is_active=1 and crp_program_outcome_id = " + crpProgramOutcome.getId();
    List<CrpProgramOutcomeIndicator> list = super.findAll(query);
    if (!list.isEmpty()) {
      return list;
    }
    return null;

  }

  @Override
  public List<CrpProgramOutcomeIndicator> getCrpProgramOutcomeIndicatorByOutcomeAndIndicator(String indicator,
    CrpProgramOutcome crpProgramOutcome) {

    String query =
      "from " + CrpProgramOutcomeIndicator.class.getName() + " where is_active=1 and crp_program_outcome_id = "
        + crpProgramOutcome.getId() + " and indicator = '" + indicator + "'";
    List<CrpProgramOutcomeIndicator> list = super.findAll(query);
    if (!list.isEmpty()) {
      return list;
    }
    return null;

  }

  @Override
  public CrpProgramOutcomeIndicator getCrpProgramOutcomeIndicatorByPhase(String composedID, long phaseID) {
    StringBuilder query = new StringBuilder();

    query.append("SELECT DISTINCT  ");
    query.append("cm.id as id ");
    query.append("FROM ");
    query.append("crp_program_outcome_indicator cm ");
    query.append("INNER JOIN crp_program_outcomes co ON co.id=cm.crp_program_outcome_id ");
    query.append("INNER JOIN crp_programs ON crp_programs.id=co.crp_program_id ");
    query.append("INNER JOIN global_units gu ON gu.id=crp_programs.global_unit_id ");
    query.append("INNER JOIN phases ON phases.id=co.id_phase ");
    query.append("WHERE co.is_active = 1 AND ");
    query.append("phases.id =" + phaseID + " AND ");
    query.append("cm.is_active=1 AND ");
    query.append("cm.composed_id ='" + composedID + "'");

    List<Map<String, Object>> rList = super.findCustomQuery(query.toString());
    List<CrpProgramOutcomeIndicator> crpProgramOutcomeIndicators = new ArrayList<>();

    if (rList != null) {
      for (Map<String, Object> map : rList) {
        CrpProgramOutcomeIndicator crpProgramOutcomeIndicator = this.find(Long.parseLong(map.get("id").toString()));
        crpProgramOutcomeIndicators.add(crpProgramOutcomeIndicator);
      }
      if (!crpProgramOutcomeIndicators.isEmpty()) {
        return crpProgramOutcomeIndicators.get(0);
      } else {
        return null;
      }
    } else {
      return null;
    }
  }

  @Override
  public CrpProgramOutcomeIndicator getCrpProgramOutcomeIndicatorPhase(long phaseID,
    CrpProgramOutcome crpProgramOutcome) {
    String query = "from " + CrpProgramOutcomeIndicator.class.getName()
      + " where is_active=1 and crp_program_outcome_id = " + crpProgramOutcome.getId();
    List<CrpProgramOutcomeIndicator> list = super.findAll(query);
    if (phaseID != 0) {
      if (!list.isEmpty()) {
        if (list.stream()
          .filter(o -> o.getCrpProgramOutcome() != null && o.getCrpProgramOutcome().getPhase() != null
            && o.getCrpProgramOutcome().getPhase().getId() != null
            && o.getCrpProgramOutcome().getPhase().getId().equals(phaseID))
          .collect(Collectors.toList()) != null) {
          list = list.stream()
            .filter(o -> o.getCrpProgramOutcome() != null && o.getCrpProgramOutcome().getPhase() != null
              && o.getCrpProgramOutcome().getPhase().getId() != null
              && o.getCrpProgramOutcome().getPhase().getId().equals(phaseID))
            .collect(Collectors.toList());
        }
      }
    }
    if (!list.isEmpty() && list.get(0) != null) {
      return list.get(0);
    }

    return null;

  }

  @Override
  public CrpProgramOutcomeIndicator save(CrpProgramOutcomeIndicator crpProgramOutcomeIndicator) {
    if (crpProgramOutcomeIndicator.getId() == null) {
      super.saveEntity(crpProgramOutcomeIndicator);
    } else {
      crpProgramOutcomeIndicator = super.update(crpProgramOutcomeIndicator);
    }


    return crpProgramOutcomeIndicator;
  }


}