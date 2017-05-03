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

import org.cgiar.ccafs.marlo.data.manager.impl.UserRoleManagerImpl;
import org.cgiar.ccafs.marlo.data.model.UserRole;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(UserRoleManagerImpl.class)
public interface UserRoleManager {


  /**
   * This method removes a specific userRole value from the database.
   * 
   * @param userRoleId is the userRole identifier.
   * @return true if the userRole was successfully deleted, false otherwise.
   */
  public boolean deleteUserRole(long userRoleId);


  /**
   * This method validate if the userRole identify with the given id exists in the system.
   * 
   * @param userRoleID is a userRole identifier.
   * @return true if the userRole exists, false otherwise.
   */
  public boolean existUserRole(long userRoleID);


  /**
   * This method gets a list of userRole that are active
   * 
   * @return a list from UserRole null if no exist records
   */
  public List<UserRole> findAll();


  /**
   * This method gets a userRole object by a given userRole identifier.
   * 
   * @param userRoleID is the userRole identifier.
   * @return a UserRole object.
   */
  public UserRole getUserRoleById(long userRoleID);


  /**
   * This method gets a list of userRoles belongs of the role
   * 
   * @param userId - the role id
   * @return List of UserRoles or null if the user is invalid or not have roles.
   */
  public List<UserRole> getUserRolesByRoleId(Long roleID);

  /**
   * This method gets a list of userRoles belongs of the user
   * 
   * @param userId - the user id
   * @return List of UserRoles or null if the user is invalid or not have roles.
   */
  public List<UserRole> getUserRolesByUserId(Long userId);


  /**
   * This method saves the information of the given userRole
   * 
   * @param userRole - is the userRole object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the userRole was
   *         updated
   *         or -1 is some error occurred.
   */
  public long saveUserRole(UserRole userRole);


}
