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

import org.cgiar.ccafs.marlo.data.dao.FeedbackRolesPermissionDAO;
import org.cgiar.ccafs.marlo.data.model.FeedbackRolesPermission;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;

@Named
public class FeedbackRolesPermissionMySQLDAO extends AbstractMarloDAO<FeedbackRolesPermission, Long>
  implements FeedbackRolesPermissionDAO {


  @Inject
  public FeedbackRolesPermissionMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteFeedbackRolesPermission(long feedbackRolesPermissionId) {
    FeedbackRolesPermission feedbackRolesPermission = this.find(feedbackRolesPermissionId);
    this.delete(feedbackRolesPermission);
  }

  @Override
  public boolean existFeedbackRolesPermission(long feedbackRolesPermissionID) {
    FeedbackRolesPermission feedbackRolesPermission = this.find(feedbackRolesPermissionID);
    if (feedbackRolesPermission == null) {
      return false;
    }
    return true;

  }

  @Override
  public boolean existsByRoleIdsAndPermissionName(List<Long> roleIds, String permissionName, long globalUnitID,
    Long clusterTypeId) {

    if (roleIds == null || roleIds.isEmpty() || permissionName == null || permissionName.isEmpty()) {
      return false;
    }

    StringBuilder sql = new StringBuilder();
    sql.append("SELECT COUNT(frp.id) AS count ").append("FROM feedback_roles_permissions frp ")
      .append("JOIN feedback_permissions fp ON frp.feedback_permission_id = fp.id ")
      .append("JOIN roles r ON frp.role_id = r.id ").append("WHERE frp.role_id IN (:roleIds) ")
      .append("AND fp.name = :permissionName ").append("AND frp.global_unit_id = :globalUnitID ")
      .append("AND r.global_unit_id = frp.global_unit_id ");

    if (clusterTypeId == null) {
      sql.append("AND frp.cluster_type_id IS NULL ");
    } else {
      sql.append("AND (frp.cluster_type_id IS NULL OR frp.cluster_type_id = :clusterTypeId) ");
    }

    NativeQuery<?> query = this.getSessionFactory().getCurrentSession().createNativeQuery(sql.toString())
      .setParameterList("roleIds", roleIds).setParameter("permissionName", permissionName)
      .setParameter("globalUnitID", globalUnitID);

    if (clusterTypeId != null) {
      query.setParameter("clusterTypeId", clusterTypeId);
    }

    Number count = (Number) query.uniqueResult();

    return count != null && count.longValue() > 0;
  }


  @Override
  public FeedbackRolesPermission find(long id) {
    return super.find(FeedbackRolesPermission.class, id);

  }

  @Override
  public List<FeedbackRolesPermission> findAll() {
    String query = "from " + FeedbackRolesPermission.class.getName();
    List<FeedbackRolesPermission> list = super.findAll(query);
    if (!list.isEmpty()) {
      return list;
    }
    return Collections.emptyList();

  }

  @Override
  public List<FeedbackRolesPermission> findObjectsByRoleIdsAndPermissionName(List<Long> roleIds, String permissionName,
    Long globalUnitID, Long clusterTypeId) {

    String sql = "SELECT frp.* FROM feedback_roles_permissions frp " + "WHERE frp.role_id IN (:roleIds) "
      + "AND frp.feedback_permission_id = (SELECT id FROM feedback_permissions WHERE name = :permissionName) "
      + "AND frp.global_unit_id = :globalUnitID ";

    if (clusterTypeId == null) {
      sql += "AND frp.cluster_type_id IS NULL ";
    } else {
      sql += "AND (frp.cluster_type_id IS NULL OR frp.cluster_type_id = :clusterTypeId) ";
    }

    NativeQuery<FeedbackRolesPermission> query = this.getSessionFactory().getCurrentSession()
      .createNativeQuery(sql, FeedbackRolesPermission.class).setParameter("roleIds", roleIds)
      .setParameter("permissionName", permissionName).setParameter("globalUnitID", globalUnitID);

    if (clusterTypeId != null) {
      query.setParameter("clusterTypeId", clusterTypeId);
    }

    return query.getResultList();
  }


  @Override
  public List<String> findRoleAcronymsByPermissionName(String permissionName, long globalUnitID) {
    if (permissionName == null || permissionName.isEmpty()) {
      return Collections.emptyList();
    }

    String sql = "SELECT DISTINCT r.acronym " + "FROM feedback_roles_permissions frp "
      + "JOIN roles r ON frp.role_id = r.id " + "JOIN feedback_permissions fp ON frp.feedback_permission_id = fp.id "
      + "WHERE fp.name = :permissionName AND frp.global_unit_id = :globalUnitID "
      + "AND r.global_unit_id = frp.global_unit_id";

    @SuppressWarnings("unchecked")
    List<String> acronyms = this.getSessionFactory().getCurrentSession().createNativeQuery(sql)
      .setParameter("permissionName", permissionName).setParameter("globalUnitID", globalUnitID).getResultList();

    return acronyms;
  }

  @Override
  public List<Long> findRoleIdsByPermissionName(String permissionName, long globalUnitID) {
    if (permissionName == null || permissionName.isEmpty()) {
      return Collections.emptyList();
    }

    String sql = "SELECT r.id " + "FROM feedback_roles_permissions frp "
      + "JOIN feedback_permissions fp ON frp.feedback_permission_id = fp.id " + "JOIN roles r ON frp.role_id = r.id "
      + "WHERE fp.name = :permissionName AND frp.global_unit_id = :globalUnitID "
      + "AND r.global_unit_id = frp.global_unit_id";

    return this.getSessionFactory().getCurrentSession().createSQLQuery(sql)
      .addScalar("id", org.hibernate.type.LongType.INSTANCE).setParameter("permissionName", permissionName)
      .setParameter("globalUnitID", globalUnitID).list();
  }

  @Override
  public List<FeedbackRolesPermission> getFeedbackRolesPermissionByGlobalUnitID(long globalUnitID) {
    String query = "from " + FeedbackRolesPermission.class.getName() + " where global_unit_id=" + globalUnitID;
    List<FeedbackRolesPermission> list = super.findAll(query);
    if (list == null) {
      return Collections.emptyList();
    }
    return list;
  }


  @Override
  public FeedbackRolesPermission save(FeedbackRolesPermission feedbackRolesPermission) {
    if (feedbackRolesPermission.getId() == null) {
      super.saveEntity(feedbackRolesPermission);
    } else {
      feedbackRolesPermission = super.update(feedbackRolesPermission);
    }


    return feedbackRolesPermission;
  }

}