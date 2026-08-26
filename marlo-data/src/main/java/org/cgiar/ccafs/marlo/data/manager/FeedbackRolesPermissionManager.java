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
package org.cgiar.ccafs.marlo.data.manager;

import org.cgiar.ccafs.marlo.data.model.FeedbackRolesPermission;

import java.util.List;


/**
 * @author CCAFS
 */

public interface FeedbackRolesPermissionManager {


  /**
   * This method removes a specific feedbackRolesPermission value from the database.
   * 
   * @param feedbackRolesPermissionId is the feedbackRolesPermission identifier.
   * @return true if the feedbackRolesPermission was successfully deleted, false otherwise.
   */
  public void deleteFeedbackRolesPermission(long feedbackRolesPermissionId);


  /**
   * This method validate if the feedbackRolesPermission identify with the given id exists in the system.
   * 
   * @param feedbackRolesPermissionID is a feedbackRolesPermission identifier.
   * @return true if the feedbackRolesPermission exists, false otherwise.
   */
  public boolean existFeedbackRolesPermission(long feedbackRolesPermissionID);


  /**
   * This method verifies whether any of the given roles is granted the given feedback permission within the given
   * global unit. Both the grant and the role must belong to that global unit.
   *
   * @param roleIds is the list of roles IDs.
   * @param permissionName is the permission name, as stored in feedback_permissions.name.
   * @param globalUnitID is the global unit the grant and the role must belong to.
   * @param clusterTypeID is the cluster type to match; when null, only grants with no cluster type match.
   * @return true if at least one matching grant exists, false otherwise
   */
  public boolean existsByRoleIdsAndPermissionName(List<Long> roleIds, String permissionName, long globalUnitID,
    Long clusterTypeID);

  /**
   * This method gets a list of feedbackRolesPermission that are active
   * 
   * @return a list from FeedbackRolesPermission null if no exist records
   */
  public List<FeedbackRolesPermission> findAll();

  /**
   * This method gets a list of feedbackRolesPermission filtered by role IDs and permission name.
   *
   * @param roleIds is the list of roles IDs.
   * @param permissionName is the permission name, as stored in feedback_permissions.name.
   * @param globalUnitID is the global unit the grant must belong to.
   * @param clusterTypeID is the cluster type to match; when null, only grants with no cluster type match.
   * @return a list from FeedbackRolesPermission null if no exist records
   */
  List<FeedbackRolesPermission> findObjectsByRoleIdsAndPermissionName(List<Long> roleIds, String permissionName,
    Long globalUnitID, Long clusterTypeID);

  /**
   * This method gets the acronyms of the roles granted the given feedback permission within the given global unit.
   *
   * @param permissionName is the permission name, as stored in feedback_permissions.name.
   * @param globalUnitID is the global unit the grant and the role must belong to.
   * @return a list of role acronyms, empty if no records exist
   */
  public List<String> findRoleAcronymsByPermissionName(String permissionName, long globalUnitID);


  /**
   * This method gets the IDs of the roles granted the given feedback permission within the given global unit.
   *
   * @param permissionName is the permission name, as stored in feedback_permissions.name.
   * @param globalUnitID is the global unit the grant and the role must belong to.
   * @return a list of role IDs, empty if no records exist
   */
  public List<Long> findRoleIdsByPermissionName(String permissionName, long globalUnitID);

  /**
   * This method gets a list of feedbackRolesPermission that are active filtered by global unit.
   * 
   * @return a list from FeedbackRolesPermission null if no exist records
   */
  public List<FeedbackRolesPermission> getFeedbackRolesPermissionByGlobalUnitID(long globalUnitID);

  /**
   * This method gets a feedbackRolesPermission object by a given feedbackRolesPermission identifier.
   * 
   * @param feedbackRolesPermissionID is the feedbackRolesPermission identifier.
   * @return a FeedbackRolesPermission object.
   */
  public FeedbackRolesPermission getFeedbackRolesPermissionById(long feedbackRolesPermissionID);

  /**
   * This method saves the information of the given feedbackRolesPermission
   * 
   * @param feedbackRolesPermission - is the feedbackRolesPermission object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the feedbackRolesPermission
   *         was
   *         updated
   *         or -1 is some error occurred.
   */
  public FeedbackRolesPermission saveFeedbackRolesPermission(FeedbackRolesPermission feedbackRolesPermission);

}
