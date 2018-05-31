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

import org.cgiar.ccafs.marlo.data.dao.CrpClusterKeyOutputDAO;
import org.cgiar.ccafs.marlo.data.model.CrpClusterKeyOutput;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class CrpClusterKeyOutputMySQLDAO extends AbstractMarloDAO<CrpClusterKeyOutput, Long>
  implements CrpClusterKeyOutputDAO {


  @Inject
  public CrpClusterKeyOutputMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteCrpClusterKeyOutput(long crpClusterKeyOutputId) {
    CrpClusterKeyOutput crpClusterKeyOutput = this.find(crpClusterKeyOutputId);
    crpClusterKeyOutput.setActive(false);
    this.save(crpClusterKeyOutput);
  }

  @Override
  public boolean existCrpClusterKeyOutput(long crpClusterKeyOutputID) {
    CrpClusterKeyOutput crpClusterKeyOutput = this.find(crpClusterKeyOutputID);
    if (crpClusterKeyOutput == null) {
      return false;
    }
    return true;

  }

  @Override
  public CrpClusterKeyOutput find(long id) {
    return super.find(CrpClusterKeyOutput.class, id);

  }

  @Override
  public List<CrpClusterKeyOutput> findAll() {
    String query = "from " + CrpClusterKeyOutput.class.getName() + " where is_active=1";
    List<CrpClusterKeyOutput> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CrpClusterKeyOutput> findCrpClusterKeyOutputByGlobalUnitAndPhase(long globalUnitID, long phaseId) {
    StringBuilder query = new StringBuilder();
    query.append("SELECT DISTINCT  ");
    query.append("crp_cluster_key_outputs.id as id ");
    query.append("FROM ");
    query.append("crp_cluster_key_outputs ");
    query.append(
      "INNER JOIN crp_cluster_of_activities ON crp_cluster_of_activities.id = crp_cluster_key_outputs.cluster_activity_id ");
    query.append("INNER JOIN crp_programs ON crp_programs.id = crp_cluster_of_activities.crp_program_id ");
    query.append("WHERE ");
    query.append("crp_cluster_of_activities.id_phase =" + phaseId + " AND crp_programs.global_unit_id=" + globalUnitID
      + " AND crp_programs.is_active = 1" + "  AND  crp_cluster_of_activities.is_active = 1 and ");
    query.append("crp_cluster_key_outputs.is_active = 1   ");

    List<Map<String, Object>> rList = super.findCustomQuery(query.toString());

    List<CrpClusterKeyOutput> crpClusterKeyOutputs = new ArrayList<>();

    if (rList != null) {
      for (Map<String, Object> map : rList) {
        CrpClusterKeyOutput crpClusterKeyOutput = this.find(Long.parseLong(map.get("id").toString()));
        crpClusterKeyOutputs.add(crpClusterKeyOutput);
      }
    }

    return crpClusterKeyOutputs;
  }

  @Override
  public CrpClusterKeyOutput save(CrpClusterKeyOutput crpClusterKeyOutput) {
    if (crpClusterKeyOutput.getId() == null) {
      super.saveEntity(crpClusterKeyOutput);
    } else {
      crpClusterKeyOutput = super.update(crpClusterKeyOutput);
    }


    return crpClusterKeyOutput;
  }


}