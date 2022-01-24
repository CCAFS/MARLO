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

import org.cgiar.ccafs.marlo.data.model.OneCGIARAccountType;

import java.util.List;

public interface OneCGIARAccountTypeDAO {

  /**
   * This method removes a specific oneCGIARAccountType value from the database.
   * 
   * @param oneCGIARAccountTypeId is the oneCGIARAccountType identifier.
   * @return true if the oneCGIARAccountType was successfully deleted, false otherwise.
   */
  public void deleteOneCGIARAccountType(long oneCGIARAccountTypeId);

  /**
   * This method validate if the oneCGIARAccountType identify with the given id exists in the system.
   * 
   * @param oneCGIARAccountTypeID is a oneCGIARAccountType identifier.
   * @return true if the oneCGIARAccountType exists, false otherwise.
   */
  public boolean existOneCGIARAccountType(long oneCGIARAccountTypeID);

  /**
   * This method gets a OneCGIARAccountType object by a given acronym.
   * 
   * @param acronym is the account type acronym.
   * @return a OneCGIARAccountType object.
   */
  public OneCGIARAccountType getAccountTypeByAcronym(String acronym);

  /**
   * This method gets a OneCGIARAccountType object by a given identifier.
   * 
   * @param id is the account type identifier.
   * @return a OneCGIARAccountType object.
   */
  public OneCGIARAccountType getAccountTypeById(long id);

  /**
   * This method gets a list of OneCGIARAccountType that are active
   * 
   * @return a list from OneCGIARAccountType; null if no records exists
   */
  public List<OneCGIARAccountType> getAll();

  /**
   * This method saves the information of the given oneCGIARAccountType
   * 
   * @param oneCGIARAccountType - is the oneCGIARAccountType object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the oneCGIARAccountType was
   *         updated
   *         or -1 is some error occurred.
   */
  public OneCGIARAccountType save(OneCGIARAccountType oneCGIARAccountType);

}
