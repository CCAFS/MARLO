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

import org.cgiar.ccafs.marlo.data.dao.CrpClusterOfActivityDAO;
import org.cgiar.ccafs.marlo.data.dto.ImpactPathwaysClusterDTO;
import org.cgiar.ccafs.marlo.data.model.CrpClusterOfActivity;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.ResultTransformer;

@Named
public class CrpClusterOfActivityMySQLDAO extends AbstractMarloDAO<CrpClusterOfActivity, Long>
  implements CrpClusterOfActivityDAO {


  @Inject
  public CrpClusterOfActivityMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }


  @Override
  public void deleteCrpClusterOfActivity(long crpClusterOfActivityId) {
    CrpClusterOfActivity crpClusterOfActivity = this.find(crpClusterOfActivityId);
    crpClusterOfActivity.setActive(false);
    this.save(crpClusterOfActivity);
  }

  @Override
  public boolean existCrpClusterOfActivity(long crpClusterOfActivityID) {
    CrpClusterOfActivity crpClusterOfActivity = this.find(crpClusterOfActivityID);
    if (crpClusterOfActivity == null) {
      return false;
    }
    return true;

  }

  @Override
  public CrpClusterOfActivity find(long id) {
    return super.find(CrpClusterOfActivity.class, id);

  }

  @Override
  public List<CrpClusterOfActivity> findAll() {
    String query = "from " + CrpClusterOfActivity.class.getName() + " where is_active=1";
    List<CrpClusterOfActivity> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CrpClusterOfActivity> findClusterProgramPhase(long crpProgramID, long phaseID) {
    StringBuilder query = new StringBuilder();
    query.append("from ");
    query.append(CrpClusterOfActivity.class.getName());
    query.append(" where is_active=1 and crp_program_id =" + crpProgramID);
    query.append(" and id_phase=" + phaseID);
    query.append(" order by identifier asc");
    List<CrpClusterOfActivity> list = super.findAll(query.toString());
    return list;

  }

  @Override
  public List<ImpactPathwaysClusterDTO> getAllClusterInfoFromPhase(long phaseId) {
    String query = "select fp.acronym, coa.identifier, coa.description as coa_title, "
      + "GROUP_CONCAT(DISTINCT CONCAT(leader.last_name,', ', leader.first_name, '\\r\\n<', leader.email, '>') SEPARATOR ';\\r\\n'), "
      + "kout.key_output, kout.contribution, "
      + "group_concat(distinct concat('• (', outc.composed_id, ') - ', outc.description) separator '\\r\\n') as outcomes "
      + "from crp_cluster_of_activities coa "
      + "join phases ph on ph.id = coa.id_phase join global_units gu on gu.id = ph.global_unit_id "
      + "join crp_programs fp on fp.id = coa.crp_program_id "
      + "left join crp_cluster_activity_leaders coal on coal.cluster_activity_id = coa.id and coal.is_active "
      + "left join users leader on leader.id = coal.user_id "
      + "left join crp_cluster_key_outputs kout on kout.cluster_activity_id = coa.id and kout.is_active "
      + "left join crp_cluster_key_outputs_outcome kouto on kouto.key_output_id = kout.id and kouto.is_active "
      + "left join crp_program_outcomes outc on outc.id = kouto.outcome_id and outc.is_active "
      + "where coa.is_active and ph.id = ? "
      + "GROUP BY fp.acronym, coa.identifier, coa.description, kout.key_output, kout.contribution " + "order by 1,2";
    Query createQuery = this.getSessionFactory().getCurrentSession().createSQLQuery(query);

    createQuery.setParameter(0, phaseId);
    createQuery.setResultTransformer(new ResultTransformer() {

      @Override
      public List transformList(List collection) {
        // we return the same list as-it-is because we could not care less about this, as we are not using it.
        return collection;
      }

      @Override
      public Object transformTuple(Object[] tuple, String[] aliases) {
        return new ImpactPathwaysClusterDTO((String) tuple[0], (String) tuple[1], (String) tuple[2], (String) tuple[3],
          (String) tuple[4], (tuple[5] == null ? BigDecimal.ZERO : new BigDecimal(((Double) tuple[5]).toString())),
          (String) tuple[6]);
      }
    });
    createQuery.setFlushMode(FlushMode.COMMIT);

    List<ImpactPathwaysClusterDTO> list = createQuery.list();
    return list;
  }

  @Override
  public CrpClusterOfActivity getCrpClusterOfActivityByIdentifierPhase(String crpClusterOfActivityIdentefier,
    Phase phase) {
    String query = "from " + CrpClusterOfActivity.class.getName() + " where is_active=1 and identifier='"
      + crpClusterOfActivityIdentefier + "' and id_phase=" + phase.getId();
    List<CrpClusterOfActivity> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public CrpClusterOfActivity save(CrpClusterOfActivity crpClusterOfActivity) {
    if (crpClusterOfActivity.getId() == null) {
      super.saveEntity(crpClusterOfActivity);
    } else {
      crpClusterOfActivity = super.update(crpClusterOfActivity);
    }


    return crpClusterOfActivity;
  }


}