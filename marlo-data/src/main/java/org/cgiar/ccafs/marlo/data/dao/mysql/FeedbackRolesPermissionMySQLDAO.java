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

    String sql = "SELECT COUNT(frp.id) AS count " + "FROM feedback_roles_permissions frp "
      + "JOIN feedback_permissions fp ON frp.feedback_permission_id = fp.id " + "JOIN roles r ON frp.role_id = r.id "
      + "WHERE frp.role_id IN (:roleIds) " + "AND fp.name = :permissionName " + "AND r.global_unit_id = :globalUnitID "
      + "AND (frp.cluster_type_id IS NULL OR frp.cluster_type_id = :clusterTypeId)";

    Long safeClusterTypeId = (clusterTypeId != null) ? clusterTypeId : -1L;

    Number count = (Number) this.getSessionFactory().getCurrentSession().createSQLQuery(sql)
      .addScalar("count", org.hibernate.type.LongType.INSTANCE).setParameterList("roleIds", roleIds)
      .setParameter("permissionName", permissionName).setParameter("globalUnitID", globalUnitID)
      .setParameter("clusterTypeId", safeClusterTypeId).uniqueResult();

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
    return null;

  }

  @Override
  public List<String> findRoleAcronymsByPermissionName(String permissionName, long globalUnitID) {
    if (permissionName == null || permissionName.isEmpty()) {
      return Collections.emptyList();
    }

    String sql = "SELECT DISTINCT r.acronym " + "FROM feedback_roles_permissions frp "
      + "JOIN roles r ON frp.role_id = r.id " + "JOIN feedback_permissions fp ON frp.feedback_permission_id = fp.id "
      + "WHERE fp.name = :permissionName AND r.global_unit_id = :globalUnitID";

    @SuppressWarnings("unchecked")
    List<String> acronyms = this.getSessionFactory().getCurrentSession().createNativeQuery(sql)
      .setParameter("permissionName", permissionName).getResultList();

    return acronyms;
  }


  @Override
  public List<Long> findRoleIdsByPermissionName(String permissionName, long globalUnitID) {
    if (permissionName == null || permissionName.isEmpty()) {
      return Collections.emptyList();
    }

    String sql = "SELECT r.id " + "FROM feedback_roles_permissions frp "
      + "JOIN feedback_permissions fp ON frp.feedback_permission_id = fp.id " + "JOIN roles r ON frp.role_id = r.id "
      + "WHERE fp.name = :permissionName AND r.global_unit_id = :globalUnitID";

    return this.getSessionFactory().getCurrentSession().createSQLQuery(sql)
      .addScalar("id", org.hibernate.type.LongType.INSTANCE).setParameter("permissionName", permissionName)
      .setParameter("globalUnitID", globalUnitID).list();
  }

  @Override
  public List<FeedbackRolesPermission> getFeedbackRolesPermissionByGlobalUnitID(long globalUnitID) {
    String query =
      "from " + FeedbackRolesPermission.class.getName() + " where is_active=1 and global_unit_id=" + globalUnitID;
    List<FeedbackRolesPermission> list = super.findAll(query);
    if (!list.isEmpty()) {
      return list;
    }
    return null;
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