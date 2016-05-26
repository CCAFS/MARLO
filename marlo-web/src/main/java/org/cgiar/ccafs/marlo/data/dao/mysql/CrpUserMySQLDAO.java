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

import org.cgiar.ccafs.marlo.data.dao.CrpUserDAO;
import org.cgiar.ccafs.marlo.data.model.CrpUser;

import java.util.List;

import com.google.inject.Inject;

public class CrpUserMySQLDAO implements CrpUserDAO {

  private StandardDAO dao;

  @Inject
  public CrpUserMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteCrpUser(long crpUserId) {
    CrpUser crpUser = this.find(crpUserId);
    crpUser.setActive(false);
    return this.save(crpUser) > 0;
  }

  @Override
  public boolean existCrpUser(long crpUserID) {
    CrpUser crpUser = this.find(crpUserID);
    if (crpUser == null) {
      return false;
    }
    return true;

  }

  @Override
  public CrpUser find(long id) {
    return dao.find(CrpUser.class, id);

  }

  @Override
  public List<CrpUser> findAll() {
    String query = "from " + CrpUser.class.getName() + " where is_active=1";
    List<CrpUser> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(CrpUser crpUser) {
    dao.saveOrUpdate(crpUser);
    return crpUser.getId();
  }


}