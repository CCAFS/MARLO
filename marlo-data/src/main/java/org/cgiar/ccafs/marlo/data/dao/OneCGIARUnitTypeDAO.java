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

/**************
 * @author Diego Perez - CIAT/CCAFS
 **************/

package org.cgiar.ccafs.marlo.data.dao;

import org.cgiar.ccafs.marlo.data.model.OneCGIARUnitType;

import java.util.List;

public interface OneCGIARUnitTypeDAO {

  /**
   * This method removes a specific oneCGIARUnitType value from the database.
   * 
   * @param oneCGIARUnitTypeId is the oneCGIARUnitType identifier.
   * @return true if the oneCGIARUnitType was successfully deleted, false otherwise.
   */
  public void deleteOneCGIARUnitType(long oneCGIARUnitTypeId);

  /**
   * This method validate if the oneCGIARUnitType identify with the given id exists in the system.
   * 
   * @param oneCGIARUnitTypeID is a oneCGIARUnitType identifier.
   * @return true if the oneCGIARUnitType exists, false otherwise.
   */
  public boolean existOneCGIARUnitType(long oneCGIARUnitTypeID);

  /**
   * This method gets a list of OneCGIARUnitType that are active
   * 
   * @return a list from OneCGIARUnitType; null if no records exists
   */
  public List<OneCGIARUnitType> getAll();

  /**
   * This method gets a OneCGIARUnitType object by a given acronym.
   * 
   * @param acronym is the unit type acronym.
   * @return a OneCGIARUnitType object.
   */
  public OneCGIARUnitType getUnitTypeByAcronym(String acronym);

  /**
   * This method gets a OneCGIARUnitType object by a given identifier.
   * 
   * @param id is the unit type identifier.
   * @return a OneCGIARUnitType object.
   */
  public OneCGIARUnitType getUnitTypeById(long id);

  /**
   * This method saves the information of the given oneCGIARUnitType
   * 
   * @param oneCGIARUnitType - is the oneCGIARUnitType object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the oneCGIARUnitType was
   *         updated
   *         or -1 is some error occurred.
   */
  public OneCGIARUnitType save(OneCGIARUnitType oneCGIARUnitType);

}
