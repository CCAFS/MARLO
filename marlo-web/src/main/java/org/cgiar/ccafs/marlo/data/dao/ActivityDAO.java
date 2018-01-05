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

import org.cgiar.ccafs.marlo.data.model.Activity;

import java.util.List;


public interface ActivityDAO {

  /**
   * This method removes a specific activity value from the database.
   * 
   * @param activityId is the activity identifier.
   * @return true if the activity was successfully deleted, false otherwise.
   */
  public void deleteActivity(long activityId);

  /**
   * This method validate if the activity identify with the given id exists in the system.
   * 
   * @param activityID is a activity identifier.
   * @return true if the activity exists, false otherwise.
   */
  public boolean existActivity(long activityID);

  /**
   * This method gets a activity object by a given activity identifier.
   * 
   * @param activityID is the activity identifier.
   * @return a Activity object.
   */
  public Activity find(long id);

  /**
   * This method gets a list of activity that are active
   * 
   * @return a list from Activity null if no exist records
   */
  public List<Activity> findAll();


  /**
   * This method saves the information of the given activity
   * 
   * @param activity - is the activity object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the activity was
   *         updated
   *         or -1 is some error occurred.
   */
  public Activity save(Activity activity);
}
