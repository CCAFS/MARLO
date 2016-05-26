/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/


package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.CrpSitesLeaderDAO;
import org.cgiar.ccafs.marlo.data.model.CrpSitesLeader;

import java.util.List;

import com.google.inject.Inject;

public class CrpSitesLeaderMySQLDAO implements CrpSitesLeaderDAO {

  private StandardDAO dao;

  @Inject
  public CrpSitesLeaderMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteCrpSitesLeader(long crpSitesLeaderId) {
    CrpSitesLeader crpSitesLeader = this.find(crpSitesLeaderId);
    crpSitesLeader.setActive(false);
    return this.save(crpSitesLeader) > 0;
  }

  @Override
  public boolean existCrpSitesLeader(long crpSitesLeaderID) {
    CrpSitesLeader crpSitesLeader = this.find(crpSitesLeaderID);
    if (crpSitesLeader == null) {
      return false;
    }
    return true;

  }

  @Override
  public CrpSitesLeader find(long id) {
    return dao.find(CrpSitesLeader.class, id);

  }

  @Override
  public List<CrpSitesLeader> findAll() {
    String query = "from " + CrpSitesLeader.class.getName() + " where is_active=1";
    List<CrpSitesLeader> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(CrpSitesLeader crpSitesLeader) {
    dao.saveOrUpdate(crpSitesLeader);
    return crpSitesLeader.getId();
  }


}