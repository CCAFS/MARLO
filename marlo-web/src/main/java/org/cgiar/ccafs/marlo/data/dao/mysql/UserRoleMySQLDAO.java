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

import org.cgiar.ccafs.marlo.data.dao.UserRoleDAO;
import org.cgiar.ccafs.marlo.data.model.UserRole;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class UserRoleMySQLDAO extends AbstractMarloDAO<UserRole, Long> implements UserRoleDAO {


  @Inject
  public UserRoleMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteUserRole(long userRoleId) {
    UserRole userRole = this.find(userRoleId);
    super.delete(userRole);
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
    return super.find(UserRole.class, id);

  }

  @Override
  public List<UserRole> findAll() {
    String query = "from " + UserRole.class.getName();
    List<UserRole> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<UserRole> getUserRolesByRoleId(Long roleID) {
    String query = "from " + UserRole.class.getName() + " where role_id=" + roleID;
    return super.findAll(query);
  }

  @Override
  public List<UserRole> getUserRolesByUserId(long userId) {
    String query = "from " + UserRole.class.getName() + " where user_id=" + userId;
    return super.findAll(query);
  }

  @Override
  public UserRole save(UserRole userRole) {
    if (userRole.getId() == null) {
      super.saveEntity(userRole);
    } else {
      userRole = super.update(userRole);
    }
    return userRole;
  }


}