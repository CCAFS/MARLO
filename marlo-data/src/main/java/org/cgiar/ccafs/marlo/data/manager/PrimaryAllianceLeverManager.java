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

import org.cgiar.ccafs.marlo.data.model.PrimaryAllianceLever;

import java.util.List;


/**
 * @author CCAFS
 */

public interface PrimaryAllianceLeverManager {


  /**
   * This method removes a specific primaryAllianceLever value from the database.
   * 
   * @param primaryAllianceLeverId is the primaryAllianceLever identifier.
   * @return true if the primaryAllianceLever was successfully deleted, false otherwise.
   */
  public void deletePrimaryAllianceLever(long primaryAllianceLeverId);


  /**
   * This method validate if the primaryAllianceLever identify with the given id exists in the system.
   * 
   * @param primaryAllianceLeverID is a primaryAllianceLever identifier.
   * @return true if the primaryAllianceLever exists, false otherwise.
   */
  public boolean existPrimaryAllianceLever(long primaryAllianceLeverID);


  /**
   * This method gets a list of primaryAllianceLever that are active
   * 
   * @return a list from PrimaryAllianceLever null if no exist records
   */
  public List<PrimaryAllianceLever> findAll();

  /**
   * This method gets a list of primaryAllianceLever that are active, by phase
   * 
   * @param phaseId phase ID
   * @return a list from PrimaryAllianceLever null if no exist records
   */
  List<PrimaryAllianceLever> findAllByPhase(long phaseId);

  /**
   * This method gets a primaryAllianceLever object by a given primaryAllianceLever identifier.
   * 
   * @param primaryAllianceLeverID is the primaryAllianceLever identifier.
   * @return a PrimaryAllianceLever object.
   */
  public PrimaryAllianceLever getPrimaryAllianceLeverById(long primaryAllianceLeverID);


  /**
   * This method saves the information of the given primaryAllianceLever
   * 
   * @param primaryAllianceLever - is the primaryAllianceLever object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the primaryAllianceLever was
   *         updated
   *         or -1 is some error occurred.
   */
  public PrimaryAllianceLever savePrimaryAllianceLever(PrimaryAllianceLever primaryAllianceLever);


}