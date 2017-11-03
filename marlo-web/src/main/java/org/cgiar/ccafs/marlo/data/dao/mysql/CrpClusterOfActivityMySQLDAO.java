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
import org.cgiar.ccafs.marlo.data.model.CrpClusterOfActivity;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;

import com.google.inject.Inject;

public class CrpClusterOfActivityMySQLDAO implements CrpClusterOfActivityDAO {

  private StandardDAO dao;

  @Inject
  public CrpClusterOfActivityMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }


  @Override
  public boolean deleteCrpClusterOfActivity(long crpClusterOfActivityId) {
    CrpClusterOfActivity crpClusterOfActivity = this.find(crpClusterOfActivityId);
    crpClusterOfActivity.setActive(false);
    return this.save(crpClusterOfActivity) > 0;
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
    return dao.find(CrpClusterOfActivity.class, id);

  }

  @Override
  public List<CrpClusterOfActivity> findAll() {
    String query = "from " + CrpClusterOfActivity.class.getName() + " where is_active=1";
    List<CrpClusterOfActivity> list = dao.findAll(query);
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
    query.append(" where is_active=1 and crp_program_id = ?");
    query.append(" and id_phase= ? ");
    query.append(" order by identifier asc");
    List<CrpClusterOfActivity> list = dao.findAll(query.toString(), crpProgramID, phaseID);
    return list;

  }

  @Override
  public CrpClusterOfActivity getCrpClusterOfActivityByIdentifierPhase(String crpClusterOfActivityIdentefier,
    Phase phase) {
    String query = "from " + CrpClusterOfActivity.class.getName() + " where is_active=1 and identifier='"
      + crpClusterOfActivityIdentefier + "' and id_phase=" + phase.getId();
    List<CrpClusterOfActivity> list = dao.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public Long save(CrpClusterOfActivity crpClusterOfActivity) {
    if (crpClusterOfActivity.getId() == null) {
      dao.save(crpClusterOfActivity);
    } else {
      dao.update(crpClusterOfActivity);
    }


    return crpClusterOfActivity.getId();
  }


}