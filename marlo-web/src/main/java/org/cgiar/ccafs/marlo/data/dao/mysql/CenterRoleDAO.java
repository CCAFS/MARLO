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

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class CenterRoleDAO extends AbstractMarloDAO<CenterRole, Long> implements ICenterRoleDAO {


  @Inject
  public CenterRoleDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteRole(long roleId) {
    CenterRole role = this.find(roleId);
    super.delete(role);
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
    return super.find(CenterRole.class, id);

  }

  @Override
  public List<CenterRole> findAll() {
    String query = "from " + CenterRole.class.getName();
    List<CenterRole> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CenterRole> getRolesByUserId(long userId) {
    String query = "from " + CenterRole.class.getName() + " where user_id=" + userId;
    return super.findAll(query);
  }

  @Override
  public CenterRole save(CenterRole role) {
    if (role.getId() == null) {
      super.saveEntity(role);
    } else {
      role = super.update(role);
    }
    return role;
  }

  @Override
  public CenterRole save(CenterRole role, String actionName, List<String> relationsName) {
    if (role.getId() == null) {
      super.saveEntity(role, actionName, relationsName);
    } else {
      role = super.update(role, actionName, relationsName);
    }
    return role;
  }


}