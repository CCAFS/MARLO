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

import org.cgiar.ccafs.marlo.data.model.CrpIndicatorType;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface CrpIndicatorTypeManager {


  /**
   * This method removes a specific crpIndicatorType value from the database.
   * 
   * @param crpIndicatorTypeId is the crpIndicatorType identifier.
   * @return true if the crpIndicatorType was successfully deleted, false otherwise.
   */
  public void deleteCrpIndicatorType(long crpIndicatorTypeId);


  /**
   * This method validate if the crpIndicatorType identify with the given id exists in the system.
   * 
   * @param crpIndicatorTypeID is a crpIndicatorType identifier.
   * @return true if the crpIndicatorType exists, false otherwise.
   */
  public boolean existCrpIndicatorType(long crpIndicatorTypeID);


  /**
   * This method gets a list of crpIndicatorType that are active
   * 
   * @return a list from CrpIndicatorType null if no exist records
   */
  public List<CrpIndicatorType> findAll();


  /**
   * This method gets a crpIndicatorType object by a given crpIndicatorType identifier.
   * 
   * @param crpIndicatorTypeID is the crpIndicatorType identifier.
   * @return a CrpIndicatorType object.
   */
  public CrpIndicatorType getCrpIndicatorTypeById(long crpIndicatorTypeID);

  /**
   * This method saves the information of the given crpIndicatorType
   * 
   * @param crpIndicatorType - is the crpIndicatorType object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the crpIndicatorType was
   *         updated
   *         or -1 is some error occurred.
   */
  public CrpIndicatorType saveCrpIndicatorType(CrpIndicatorType crpIndicatorType);


}
