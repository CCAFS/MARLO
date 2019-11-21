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

import org.cgiar.ccafs.marlo.data.dao.CrpProgramLeaderDAO;
import org.cgiar.ccafs.marlo.data.model.CrpProgramLeader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class CrpProgramLeaderMySQLDAO extends AbstractMarloDAO<CrpProgramLeader, Long> implements CrpProgramLeaderDAO {


  @Inject
  public CrpProgramLeaderMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteCrpProgramLeader(long crpProgramLeaderId) {
    CrpProgramLeader crpProgramLeader = this.find(crpProgramLeaderId);
    crpProgramLeader.setActive(false);
    this.save(crpProgramLeader);
  }

  @Override
  public boolean existCrpProgramLeader(long crpProgramLeaderID) {
    CrpProgramLeader crpProgramLeader = this.find(crpProgramLeaderID);
    if (crpProgramLeader == null) {
      return false;
    }
    return true;

  }

  @Override
  public CrpProgramLeader find(long id) {
    return super.find(CrpProgramLeader.class, id);

  }

  @Override
  public List<CrpProgramLeader> findAll() {
    String query = "from " + CrpProgramLeader.class.getName() + " where is_active=1";
    List<CrpProgramLeader> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public CrpProgramLeader getCrpProgramLeaderByProgram(long crpProgramID, long globalUnitID, long userID) {
    String query = "Select crp_program_leaders.id from crp_program_leaders "
      + "INNER JOIN crp_programs ON crp_programs.id=crp_program_leaders.crp_program_id "
      + "WHERE crp_program_leaders.is_active=1 and crp_program_leaders.crp_program_id=" + crpProgramID + " "
      + "AND crp_programs.global_unit_id=" + globalUnitID + " " + "AND crp_program_leaders.user_id=" + userID;
    List<Map<String, Object>> rList = super.findCustomQuery(query.toString());
    List<CrpProgramLeader> crpProgramLeaders = new ArrayList<CrpProgramLeader>();
    if (rList != null) {
      for (Map<String, Object> map : rList) {
        CrpProgramLeader crpProgramLeader = this.find(Long.parseLong(map.get("id").toString()));
        crpProgramLeaders.add(crpProgramLeader);
      }
    }
    if (crpProgramLeaders.size() > 0) {
      return crpProgramLeaders.get(0);
    } else {
      return null;
    }

  }

  @Override
  public CrpProgramLeader save(CrpProgramLeader crpProgramLeader) {
    if (crpProgramLeader.getId() == null) {
      super.saveEntity(crpProgramLeader);
    } else {
      crpProgramLeader = super.update(crpProgramLeader);
    }
    return crpProgramLeader;
  }


}