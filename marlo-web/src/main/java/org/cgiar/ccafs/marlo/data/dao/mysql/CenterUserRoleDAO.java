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

import org.cgiar.ccafs.marlo.data.dao.ICenterUserRoleDAO;
import org.cgiar.ccafs.marlo.data.model.CenterUserRole;

import java.util.List;

import com.google.inject.Inject;

public class CenterUserRoleDAO implements ICenterUserRoleDAO {

  private StandardDAO dao;

  @Inject
  public CenterUserRoleDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteUserRole(long userRoleId) {
    CenterUserRole userRole = this.find(userRoleId);
    return dao.delete(userRole);
  }

  @Override
  public boolean existUserRole(long userRoleID) {
    CenterUserRole userRole = this.find(userRoleID);
    if (userRole == null) {
      return false;
    }
    return true;

  }

  @Override
  public CenterUserRole find(long id) {
    return dao.find(CenterUserRole.class, id);

  }

  @Override
  public List<CenterUserRole> findAll() {
    String query = "from " + CenterUserRole.class.getName();
    List<CenterUserRole> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CenterUserRole> getUserRolesByUserId(long userId) {
    String query = "from " + CenterUserRole.class.getName() + " where user_id=" + userId;
    return dao.findAll(query);
  }

  @Override
  public long save(CenterUserRole userRole) {
    if (userRole.getId() == null) {
      dao.save(userRole);
    } else {
      dao.update(userRole);
    }
    return userRole.getId();
  }


}