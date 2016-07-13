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

import java.util.List;

import com.google.inject.Inject;

public class CrpProgramLeaderMySQLDAO implements CrpProgramLeaderDAO {

  private StandardDAO dao;

  @Inject
  public CrpProgramLeaderMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteCrpProgramLeader(long crpProgramLeaderId) {
    CrpProgramLeader crpProgramLeader = this.find(crpProgramLeaderId);
    crpProgramLeader.setActive(false);
    return this.save(crpProgramLeader) > 0;
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
    return dao.find(CrpProgramLeader.class, id);

  }

  @Override
  public List<CrpProgramLeader> findAll() {
    String query = "from " + CrpProgramLeader.class.getName() + " where is_active=1";
    List<CrpProgramLeader> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(CrpProgramLeader crpProgramLeader) {
    if (crpProgramLeader.getId() == null) {
      dao.save(crpProgramLeader);
    } else {
      dao.update(crpProgramLeader);
    }
    return crpProgramLeader.getId();
  }


}