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

import org.cgiar.ccafs.marlo.data.dao.CrpMilestoneDAO;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

@Named
public class CrpMilestoneMySQLDAO extends AbstractMarloDAO<CrpMilestone, Long> implements CrpMilestoneDAO {


  @Inject
  public CrpMilestoneMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteCrpMilestone(long crpMilestoneId) {
    CrpMilestone crpMilestone = this.find(crpMilestoneId);
    crpMilestone.setActive(false);
    this.save(crpMilestone);
  }

  @Override
  public boolean existCrpMilestone(long crpMilestoneID) {
    CrpMilestone crpMilestone = this.find(crpMilestoneID);
    if (crpMilestone == null) {
      return false;
    }
    return true;

  }

  @Override
  public CrpMilestone find(long id) {
    return super.find(CrpMilestone.class, id);

  }

  @Override
  public List<CrpMilestone> findAll() {
    String query = "from " + CrpMilestone.class.getName() + " where is_active=1";
    List<CrpMilestone> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public CrpMilestone getCrpMilestone(String composedId, CrpProgramOutcome crpProgramOutcome) {
    String query = "select distinct pp from CrpMilestone  pp "
      + "where composeID=:composeID and crpProgramOutcome.id=:crpProgramOutcomeID and active=true";
    Query<CrpMilestone> createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("composeID", composedId);
    createQuery.setParameter("crpProgramOutcomeID", crpProgramOutcome.getId());
    Object findSingleResult = super.findSingleResult(CrpMilestone.class, createQuery);
    CrpMilestone crpMilestone = (CrpMilestone) findSingleResult;


    // projectPartner.getProjectPartnerLocations().size();

    return crpMilestone;

  }

  @Override
  public CrpMilestone getCrpMilestoneByPhase(String composedID, long phaseID) {
    StringBuilder query = new StringBuilder();

    query.append("SELECT DISTINCT  ");
    query.append("cm.id as id ");
    query.append("FROM ");
    query.append("crp_milestones cm ");
    query.append("INNER JOIN crp_program_outcomes co ON co.id=cm.crp_program_outcome_id ");
    query.append("INNER JOIN crp_programs ON crp_programs.id=co.crp_program_id ");
    query.append("INNER JOIN global_units gu ON gu.id=crp_programs.global_unit_id ");
    query.append("INNER JOIN phases ON phases.id=co.id_phase ");
    query.append("WHERE co.is_active = 1 AND ");
    query.append("phases.id =" + phaseID + " AND ");
    query.append("cm.is_active=1 AND ");
    query.append("cm.composed_id ='" + composedID + "'");

    List<Map<String, Object>> rList = super.findCustomQuery(query.toString());
    List<CrpMilestone> crpMilestones = new ArrayList<>();

    if (rList != null) {
      for (Map<String, Object> map : rList) {
        CrpMilestone crpMilestone = this.find(Long.parseLong(map.get("id").toString()));
        crpMilestones.add(crpMilestone);
      }
      if (crpMilestones.size() > 0) {
        return crpMilestones.get(0);
      } else {
        return null;
      }
    } else {
      return null;
    }
  }

  @Override
  public CrpMilestone save(CrpMilestone crpMilestone) {
    if (crpMilestone.getId() == null) {
      super.saveEntity(crpMilestone);
    } else {
      crpMilestone = super.update(crpMilestone);
    }
    return crpMilestone;
  }

}