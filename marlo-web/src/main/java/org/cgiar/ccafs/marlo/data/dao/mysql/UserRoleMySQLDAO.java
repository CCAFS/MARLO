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

import org.cgiar.ccafs.marlo.data.dao.UserRoleDAO;
import org.cgiar.ccafs.marlo.data.model.UserRole;

import java.util.List;

import com.google.inject.Inject;

public class UserRoleMySQLDAO implements UserRoleDAO {

  private StandardDAO dao;

  @Inject
  public UserRoleMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteUserRole(long userRoleId) {
    UserRole userRole = this.find(userRoleId);
    return this.save(userRole) > 0;
  }

  @Override
  public boolean existUserRole(long userRoleID) {
    UserRole userRole = this.find(userRoleID);
    if (userRole == null) {
      return false;
    }
    return true;

  }

  @Override
  public UserRole find(long id) {
    return dao.find(UserRole.class, id);

  }

  @Override
  public List<UserRole> findAll() {
    String query = "from " + UserRole.class.getName();
    List<UserRole> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(UserRole userRole) {
    dao.saveOrUpdate(userRole);
    return userRole.getId();
  }


}