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

import org.cgiar.ccafs.marlo.data.dao.ICenterRoleDAO;
import org.cgiar.ccafs.marlo.data.model.CenterRole;

import java.util.List;

import com.google.inject.Inject;

public class CenterRoleDAO implements ICenterRoleDAO {

  private StandardDAO dao;

  @Inject
  public CenterRoleDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteRole(long roleId) {
    CenterRole role = this.find(roleId);
    return dao.delete(role);
  }

  @Override
  public boolean existRole(long roleID) {
    CenterRole role = this.find(roleID);
    if (role == null) {
      return false;
    }
    return true;

  }

  @Override
  public CenterRole find(long id) {
    return dao.find(CenterRole.class, id);

  }

  @Override
  public List<CenterRole> findAll() {
    String query = "from " + CenterRole.class.getName();
    List<CenterRole> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CenterRole> getRolesByUserId(long userId) {
    String query = "from " + CenterRole.class.getName() + " where user_id=" + userId;
    return dao.findAll(query);
  }

  @Override
  public long save(CenterRole role) {
    if (role.getId() == null) {
      dao.save(role);
    } else {
      dao.update(role);
    }
    return role.getId();
  }

  @Override
  public long save(CenterRole role, String actionName, List<String> relationsName) {
    if (role.getId() == null) {
      dao.save(role, actionName, relationsName);
    } else {
      dao.update(role, actionName, relationsName);
    }
    return role.getId();
  }


}