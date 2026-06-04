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

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class RoleMySQLDAO extends AbstractMarloDAO<Role, Long> implements RoleDAO {

  private static final String TEMPLATE_GLOBAL_UNIT_ID_PARAM = "templateGlobalUnitId";
  private static final String TARGET_GLOBAL_UNIT_ID_PARAM = "targetGlobalUnitId";


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
    return role != null;

  }

  @Override
  public boolean existsPermissionsByNames(List<String> permissionNames) {
    if (permissionNames == null || permissionNames.isEmpty()) {
      return true;
    }

    List<String> sanitizedPermissionNames = permissionNames.stream().filter(Objects::nonNull)
      .map(String::trim).filter(permissionName -> !permissionName.isEmpty()).distinct().collect(Collectors.toList());

    if (sanitizedPermissionNames.isEmpty()) {
      return true;
    }

    String sql = "SELECT COUNT(DISTINCT permission) FROM permissions WHERE permission IN (:permissionNames)";
    Number count = (Number) this.getSessionFactory().getCurrentSession().createSQLQuery(sql)
      .setParameterList("permissionNames", sanitizedPermissionNames).uniqueResult();

    return count != null && count.intValue() == sanitizedPermissionNames.size();
  }

  @Override
  public Role find(long id) {
    return super.find(Role.class, id);

  }

  @Override
  public List<Role> findAll() {
    String query = "from " + Role.class.getName();
    List<Role> list = super.findAll(query);
    if (!list.isEmpty()) {
      return list;
    }
    return Collections.emptyList();

  }

  @Override
  public List<Role> findByGloablUnitAndAcronym(long globalUnitId, String acronym) {
    String query =
      "from " + Role.class.getName() + " where global_unit_id =" + globalUnitId + " and acronym ='" + acronym + "'";
    List<Role> list = super.findAll(query);
    if (!list.isEmpty()) {
      return list;
    }
    return Collections.emptyList();

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
      .setParameter(TEMPLATE_GLOBAL_UNIT_ID_PARAM, templateGlobalUnitId)
      .setParameter(TARGET_GLOBAL_UNIT_ID_PARAM, targetGlobalUnitId).executeUpdate();
  }

  @Override
  public int ensureSuperAdminRoleAndPermissions(long targetGlobalUnitId, long templateGlobalUnitId) {
    String createSuperAdminRoleSql = "INSERT INTO roles (global_unit_id, description, acronym, `order`) "
      + "SELECT :targetGlobalUnitId, 'Super Admin', 'SuperAdmin', 1 "
      + "WHERE NOT EXISTS ( "
      + "  SELECT 1 FROM roles existing_role "
      + "  WHERE existing_role.global_unit_id = :targetGlobalUnitId "
      + "  AND LOWER(existing_role.acronym) = 'superadmin' "
      + ")";

    this.getSessionFactory().getCurrentSession().createSQLQuery(createSuperAdminRoleSql)
      .setParameter(TARGET_GLOBAL_UNIT_ID_PARAM, targetGlobalUnitId).executeUpdate();

    String cloneFromTemplateSql = "INSERT INTO role_permissions (role_id, permission_id) "
      + "SELECT target_role.id, source_rp.permission_id "
      + "FROM roles target_role "
      + "INNER JOIN roles source_role ON source_role.global_unit_id = :templateGlobalUnitId "
      + "AND LOWER(source_role.acronym) = 'superadmin' "
      + "INNER JOIN role_permissions source_rp ON source_rp.role_id = source_role.id "
      + "WHERE target_role.global_unit_id = :targetGlobalUnitId "
      + "AND LOWER(target_role.acronym) = 'superadmin' "
      + "AND NOT EXISTS ( "
      + "  SELECT 1 FROM role_permissions existing_rp "
      + "  WHERE existing_rp.role_id = target_role.id "
      + "  AND existing_rp.permission_id = source_rp.permission_id "
      + ")";

    int insertedFromTemplate = this.getSessionFactory().getCurrentSession().createSQLQuery(cloneFromTemplateSql)
      .setParameter(TEMPLATE_GLOBAL_UNIT_ID_PARAM, templateGlobalUnitId)
      .setParameter(TARGET_GLOBAL_UNIT_ID_PARAM, targetGlobalUnitId).executeUpdate();

    if (insertedFromTemplate > 0) {
      return insertedFromTemplate;
    }

    String fallbackSuperAdminPermissionsSql = "INSERT INTO role_permissions (role_id, permission_id) "
      + "SELECT target_role.id, permission_table.id "
      + "FROM roles target_role "
      + "INNER JOIN permissions permission_table ON permission_table.permission IN ('*', 'superadmin:canEdit') "
      + "WHERE target_role.global_unit_id = :targetGlobalUnitId "
      + "AND LOWER(target_role.acronym) = 'superadmin' "
      + "AND NOT EXISTS ( "
      + "  SELECT 1 FROM role_permissions existing_rp "
      + "  WHERE existing_rp.role_id = target_role.id "
      + "  AND existing_rp.permission_id = permission_table.id "
      + ")";

    return this.getSessionFactory().getCurrentSession().createSQLQuery(fallbackSuperAdminPermissionsSql)
      .setParameter(TARGET_GLOBAL_UNIT_ID_PARAM, targetGlobalUnitId).executeUpdate();
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