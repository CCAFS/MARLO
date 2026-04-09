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

import org.cgiar.ccafs.marlo.data.dao.RoleDAO;
import org.cgiar.ccafs.marlo.data.model.Role;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class RoleMySQLDAO extends AbstractMarloDAO<Role, Long> implements RoleDAO {


  @Inject
  public RoleMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteRole(long roleId) {
    Role role = this.find(roleId);

    super.delete(role);
  }

  @Override
  public boolean existRole(long roleID) {
    Role role = this.find(roleID);
    if (role == null) {
      return false;
    }
    return true;

  }

  @Override
  public Role find(long id) {
    return super.find(Role.class, id);

  }

  @Override
  public List<Role> findAll() {
    String query = "from " + Role.class.getName();
    List<Role> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<Role> findByGloablUnitAndAcronym(long globalUnitId, String acronym) {
    String query =
      "from " + Role.class.getName() + " where global_unit_id =" + globalUnitId + " and acronym ='" + acronym + "'";
    List<Role> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public int cloneRolePermissionsByAcronym(long templateGlobalUnitId, long targetGlobalUnitId) {
    String sql = "INSERT INTO role_permissions (role_id, permission_id) "
      + "SELECT target_role.id, source_rp.permission_id "
      + "FROM roles source_role "
      + "INNER JOIN role_permissions source_rp ON source_rp.role_id = source_role.id "
      + "INNER JOIN roles target_role ON target_role.acronym = source_role.acronym "
      + "AND target_role.global_unit_id = :targetGlobalUnitId "
      + "WHERE source_role.global_unit_id = :templateGlobalUnitId "
      + "AND NOT EXISTS ( "
      + "  SELECT 1 FROM role_permissions existing_rp "
      + "  WHERE existing_rp.role_id = target_role.id "
      + "  AND existing_rp.permission_id = source_rp.permission_id "
      + ")";

    return this.getSessionFactory().getCurrentSession().createSQLQuery(sql)
      .setParameter("templateGlobalUnitId", templateGlobalUnitId)
      .setParameter("targetGlobalUnitId", targetGlobalUnitId).executeUpdate();
  }

  @Override
  public Role save(Role role) {
    if (role.getId() == null) {
      super.saveEntity(role);
    } else {
      role = super.update(role);
    }
    return role;
  }


}