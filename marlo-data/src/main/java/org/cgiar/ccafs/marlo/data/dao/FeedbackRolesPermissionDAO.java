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


package org.cgiar.ccafs.marlo.data.dao;

import org.cgiar.ccafs.marlo.data.model.FeedbackRolesPermission;

import java.util.List;


public interface FeedbackRolesPermissionDAO {

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
   * This method gets a feedbackRolesPermission object by a given feedbackRolesPermission identifier.
   * 
   * @param feedbackRolesPermissionID is the feedbackRolesPermission identifier.
   * @return a FeedbackRolesPermission object.
   */
  public FeedbackRolesPermission find(long id);

  /**
   * This method gets a list of feedbackRolesPermission that are active
   * 
   * @return a list from FeedbackRolesPermission null if no exist records
   */
  public List<FeedbackRolesPermission> findAll();


  /**
   * This method saves the information of the given feedbackRolesPermission
   * 
   * @param feedbackRolesPermission - is the feedbackRolesPermission object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the feedbackRolesPermission was
   *         updated
   *         or -1 is some error occurred.
   */
  public FeedbackRolesPermission save(FeedbackRolesPermission feedbackRolesPermission);
}
