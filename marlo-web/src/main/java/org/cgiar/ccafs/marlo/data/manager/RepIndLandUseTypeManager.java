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

import org.cgiar.ccafs.marlo.data.model.RepIndLandUseType;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface RepIndLandUseTypeManager {


  /**
   * This method removes a specific repIndLandUseType value from the database.
   * 
   * @param repIndLandUseTypeId is the repIndLandUseType identifier.
   * @return true if the repIndLandUseType was successfully deleted, false otherwise.
   */
  public void deleteRepIndLandUseType(long repIndLandUseTypeId);


  /**
   * This method validate if the repIndLandUseType identify with the given id exists in the system.
   * 
   * @param repIndLandUseTypeID is a repIndLandUseType identifier.
   * @return true if the repIndLandUseType exists, false otherwise.
   */
  public boolean existRepIndLandUseType(long repIndLandUseTypeID);


  /**
   * This method gets a list of repIndLandUseType that are active
   * 
   * @return a list from RepIndLandUseType null if no exist records
   */
  public List<RepIndLandUseType> findAll();


  /**
   * This method gets a repIndLandUseType object by a given repIndLandUseType identifier.
   * 
   * @param repIndLandUseTypeID is the repIndLandUseType identifier.
   * @return a RepIndLandUseType object.
   */
  public RepIndLandUseType getRepIndLandUseTypeById(long repIndLandUseTypeID);

  /**
   * This method saves the information of the given repIndLandUseType
   * 
   * @param repIndLandUseType - is the repIndLandUseType object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the repIndLandUseType was
   *         updated
   *         or -1 is some error occurred.
   */
  public RepIndLandUseType saveRepIndLandUseType(RepIndLandUseType repIndLandUseType);


}
