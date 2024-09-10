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

import org.cgiar.ccafs.marlo.data.model.AllianceLever;

import java.util.List;


/**
 * @author CCAFS
 */

public interface AllianceLeverManager {


  /**
   * This method removes a specific allianceLever value from the database.
   * 
   * @param allianceLeverId is the allianceLever identifier.
   * @return true if the allianceLever was successfully deleted, false otherwise.
   */
  public void deleteAllianceLever(long allianceLeverId);


  /**
   * This method validate if the allianceLever identify with the given id exists in the system.
   * 
   * @param allianceLeverID is a allianceLever identifier.
   * @return true if the allianceLever exists, false otherwise.
   */
  public boolean existAllianceLever(long allianceLeverID);


  /**
   * This method gets a list of allianceLever that are active
   * 
   * @return a list from AllianceLever null if no exist records
   */
  public List<AllianceLever> findAll();


  /**
   * This method gets a allianceLever object by a given allianceLever identifier.
   * 
   * @param allianceLeverID is the allianceLever identifier.
   * @return a AllianceLever object.
   */
  public AllianceLever getAllianceLeverById(long allianceLeverID);

  /**
   * This method saves the information of the given allianceLever
   * 
   * @param allianceLever - is the allianceLever object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the allianceLever was
   *         updated
   *         or -1 is some error occurred.
   */
  public AllianceLever saveAllianceLever(AllianceLever allianceLever);


}
