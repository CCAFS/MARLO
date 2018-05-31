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

import org.cgiar.ccafs.marlo.data.model.RepIndTypeActivity;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface RepIndTypeActivityManager {


  /**
   * This method removes a specific repIndTypeActivity value from the database.
   * 
   * @param repIndTypeActivityId is the repIndTypeActivity identifier.
   * @return true if the repIndTypeActivity was successfully deleted, false otherwise.
   */
  public void deleteRepIndTypeActivity(long repIndTypeActivityId);


  /**
   * This method validate if the repIndTypeActivity identify with the given id exists in the system.
   * 
   * @param repIndTypeActivityID is a repIndTypeActivity identifier.
   * @return true if the repIndTypeActivity exists, false otherwise.
   */
  public boolean existRepIndTypeActivity(long repIndTypeActivityID);


  /**
   * This method gets a list of repIndTypeActivity that are active
   * 
   * @return a list from RepIndTypeActivity null if no exist records
   */
  public List<RepIndTypeActivity> findAll();


  /**
   * This method gets a repIndTypeActivity object by a given repIndTypeActivity identifier.
   * 
   * @param repIndTypeActivityID is the repIndTypeActivity identifier.
   * @return a RepIndTypeActivity object.
   */
  public RepIndTypeActivity getRepIndTypeActivityById(long repIndTypeActivityID);

  /**
   * This method saves the information of the given repIndTypeActivity
   * 
   * @param repIndTypeActivity - is the repIndTypeActivity object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the repIndTypeActivity was
   *         updated
   *         or -1 is some error occurred.
   */
  public RepIndTypeActivity saveRepIndTypeActivity(RepIndTypeActivity repIndTypeActivity);


}
