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

import org.cgiar.ccafs.marlo.data.model.RepIndGenderYouthFocusLevel;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface RepIndGenderYouthFocusLevelManager {


  /**
   * This method removes a specific repIndGenderYouthFocusLevel value from the database.
   * 
   * @param repIndGenderYouthFocusLevelId is the repIndGenderYouthFocusLevel identifier.
   * @return true if the repIndGenderYouthFocusLevel was successfully deleted, false otherwise.
   */
  public void deleteRepIndGenderYouthFocusLevel(long repIndGenderYouthFocusLevelId);


  /**
   * This method validate if the repIndGenderYouthFocusLevel identify with the given id exists in the system.
   * 
   * @param repIndGenderYouthFocusLevelID is a repIndGenderYouthFocusLevel identifier.
   * @return true if the repIndGenderYouthFocusLevel exists, false otherwise.
   */
  public boolean existRepIndGenderYouthFocusLevel(long repIndGenderYouthFocusLevelID);


  /**
   * This method gets a list of repIndGenderYouthFocusLevel that are active
   * 
   * @return a list from RepIndGenderYouthFocusLevel null if no exist records
   */
  public List<RepIndGenderYouthFocusLevel> findAll();


  /**
   * This method gets a repIndGenderYouthFocusLevel object by a given repIndGenderYouthFocusLevel identifier.
   * 
   * @param repIndGenderYouthFocusLevelID is the repIndGenderYouthFocusLevel identifier.
   * @return a RepIndGenderYouthFocusLevel object.
   */
  public RepIndGenderYouthFocusLevel getRepIndGenderYouthFocusLevelById(long repIndGenderYouthFocusLevelID);

  /**
   * This method saves the information of the given repIndGenderYouthFocusLevel
   * 
   * @param repIndGenderYouthFocusLevel - is the repIndGenderYouthFocusLevel object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the repIndGenderYouthFocusLevel was
   *         updated
   *         or -1 is some error occurred.
   */
  public RepIndGenderYouthFocusLevel saveRepIndGenderYouthFocusLevel(RepIndGenderYouthFocusLevel repIndGenderYouthFocusLevel);


}
