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

import org.cgiar.ccafs.marlo.data.model.RepIndOptions;

import java.util.List;


/**
 * @author CCAFS
 */

public interface RepIndOptionsManager {


  /**
   * This method removes a specific repIndOptions value from the database.
   * 
   * @param repIndOptionsId is the repIndOptions identifier.
   * @return true if the repIndOptions was successfully deleted, false otherwise.
   */
  public void deleteRepIndOptions(long repIndOptionsId);


  /**
   * This method validate if the repIndOptions identify with the given id exists in the system.
   * 
   * @param repIndOptionsID is a repIndOptions identifier.
   * @return true if the repIndOptions exists, false otherwise.
   */
  public boolean existRepIndOptions(long repIndOptionsID);


  /**
   * This method gets a list of repIndOptions that are active
   * 
   * @return a list from RepIndOptions null if no exist records
   */
  public List<RepIndOptions> findAll();


  /**
   * This method gets a repIndOptions object by a given repIndOptions identifier.
   * 
   * @param repIndOptionsID is the repIndOptions identifier.
   * @return a RepIndOptions object.
   */
  public RepIndOptions getRepIndOptionsById(long repIndOptionsID);

  /**
   * This method saves the information of the given repIndOptions
   * 
   * @param repIndOptions - is the repIndOptions object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the repIndOptions was
   *         updated
   *         or -1 is some error occurred.
   */
  public RepIndOptions saveRepIndOptions(RepIndOptions repIndOptions);


}
