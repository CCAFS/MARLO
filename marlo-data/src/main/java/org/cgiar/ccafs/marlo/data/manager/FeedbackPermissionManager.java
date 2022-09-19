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

import org.cgiar.ccafs.marlo.data.model.FeedbackPermission;

import java.util.List;


/**
 * @author CCAFS
 */

public interface FeedbackPermissionManager {


  /**
   * This method removes a specific feedbackPermission value from the database.
   * 
   * @param feedbackPermissionId is the feedbackPermission identifier.
   * @return true if the feedbackPermission was successfully deleted, false otherwise.
   */
  public void deleteFeedbackPermission(long feedbackPermissionId);


  /**
   * This method validate if the feedbackPermission identify with the given id exists in the system.
   * 
   * @param feedbackPermissionID is a feedbackPermission identifier.
   * @return true if the feedbackPermission exists, false otherwise.
   */
  public boolean existFeedbackPermission(long feedbackPermissionID);


  /**
   * This method gets a list of feedbackPermission that are active
   * 
   * @return a list from FeedbackPermission null if no exist records
   */
  public List<FeedbackPermission> findAll();


  /**
   * This method gets a feedbackPermission object by a given feedbackPermission identifier.
   * 
   * @param feedbackPermissionID is the feedbackPermission identifier.
   * @return a FeedbackPermission object.
   */
  public FeedbackPermission getFeedbackPermissionById(long feedbackPermissionID);

  /**
   * This method saves the information of the given feedbackPermission
   * 
   * @param feedbackPermission - is the feedbackPermission object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the feedbackPermission was
   *         updated
   *         or -1 is some error occurred.
   */
  public FeedbackPermission saveFeedbackPermission(FeedbackPermission feedbackPermission);


}
