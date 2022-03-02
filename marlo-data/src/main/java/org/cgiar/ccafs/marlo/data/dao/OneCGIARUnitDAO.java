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

import org.cgiar.ccafs.marlo.data.model.OneCGIARUnit;

import java.util.List;

public interface OneCGIARUnitDAO {

  /**
   * This method removes a specific oneCGIARUnit value from the database.
   * 
   * @param oneCGIARUnitId is the oneCGIARUnit identifier.
   * @return true if the oneCGIARUnit was successfully deleted, false otherwise.
   */
  public void deleteOneCGIARUnit(long oneCGIARUnitId);

  /**
   * This method validate if the oneCGIARUnit identify with the given id exists in the system.
   * 
   * @param oneCGIARUnitID is a oneCGIARUnit identifier.
   * @return true if the oneCGIARUnit exists, false otherwise.
   */
  public boolean existOneCGIARUnit(long oneCGIARUnitID);

  /**
   * This method gets a list of oneCGIARUnit that are active
   * 
   * @return a list from OneCGIARUnit; null if no records exists
   */
  public List<OneCGIARUnit> getAll();

  /**
   * This method gets a OneCGIARUnit object by a given financialCode identifier.
   * 
   * @param financialCode is the unit financialCode identifier.
   * @return a OneCGIARUnit object.
   */
  public OneCGIARUnit getUnitByFinancialCode(String financialCode);

  /**
   * This method gets a OneCGIARUnit object by a given identifier.
   * 
   * @param id is the unit identifier.
   * @return a OneCGIARUnit object.
   */
  public OneCGIARUnit getUnitById(long id);

  /**
   * This method gets a list of OneCGIARUnit objects by a given unit parent identifier.
   * 
   * @param parentId is the unit parent identifier.
   * @return a list from OneCGIARUnit; empty list if no records exists.
   */
  public List<OneCGIARUnit> getUnitsByParent(long parentId);

  /**
   * This method gets a list of OneCGIARUnit objects by a given science group identifier.
   * 
   * @param scienceGroupId is the science group identifier.
   * @return a list from OneCGIARUnit; empty list if no records exists.
   */
  public List<OneCGIARUnit> getUnitsByScienceGroup(long scienceGroupId);

  /**
   * This method saves the information of the given oneCGIARUnit
   * 
   * @param oneCGIARUnit - is the oneCGIARUnit object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the oneCGIARUnit was
   *         updated
   *         or -1 is some error occurred.
   */
  public OneCGIARUnit save(OneCGIARUnit oneCGIARUnit);

}
