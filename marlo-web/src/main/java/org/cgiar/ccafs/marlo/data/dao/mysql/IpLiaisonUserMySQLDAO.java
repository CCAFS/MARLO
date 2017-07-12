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

import org.cgiar.ccafs.marlo.data.dao.IpLiaisonUserDAO;
import org.cgiar.ccafs.marlo.data.model.IpLiaisonUser;

import java.util.List;

import com.google.inject.Inject;

public class IpLiaisonUserMySQLDAO implements IpLiaisonUserDAO {

  private StandardDAO dao;

  @Inject
  public IpLiaisonUserMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteIpLiaisonUser(long ipLiaisonUserId) {
    IpLiaisonUser ipLiaisonUser = this.find(ipLiaisonUserId);
    return dao.delete(ipLiaisonUser);
  }

  @Override
  public boolean existIpLiaisonUser(long ipLiaisonUserID) {
    IpLiaisonUser ipLiaisonUser = this.find(ipLiaisonUserID);
    if (ipLiaisonUser == null) {
      return false;
    }
    return true;

  }

  @Override
  public IpLiaisonUser find(long id) {
    return dao.find(IpLiaisonUser.class, id);

  }

  @Override
  public List<IpLiaisonUser> findAll() {
    String query = "from " + IpLiaisonUser.class.getName() + " where is_active=1";
    List<IpLiaisonUser> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(IpLiaisonUser ipLiaisonUser) {
    if (ipLiaisonUser.getId() == null) {
      dao.save(ipLiaisonUser);
    } else {
      dao.update(ipLiaisonUser);
    }


    return ipLiaisonUser.getId();
  }


}