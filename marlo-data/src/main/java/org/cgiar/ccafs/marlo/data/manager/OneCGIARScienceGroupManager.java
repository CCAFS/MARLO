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

package org.cgiar.ccafs.marlo.data.manager;

import org.cgiar.ccafs.marlo.data.model.OneCGIARScienceGroup;

import java.util.List;

public interface OneCGIARScienceGroupManager {

  /**
   * This method removes a specific oneCGIARScienceGroup value from the database.
   * 
   * @param oneCGIARScienceGroupId is the oneCGIARScienceGroup identifier.
   * @return true if the oneCGIARScienceGroup was successfully deleted, false otherwise.
   */
  public void deleteOneCGIARScienceGroup(Long oneCGIARScienceGroupId);

  /**
   * This method validate if the oneCGIARScienceGroup identify with the given id exists in the system.
   * 
   * @param oneCGIARScienceGroupID is a oneCGIARScienceGroup identifier.
   * @return true if the oneCGIARScienceGroup exists, false otherwise.
   */
  public boolean existOneCGIARScienceGroup(Long oneCGIARScienceGroupID);

  /**
   * This method gets a list of OneCGIARScienceGroup that are active
   * 
   * @return a list from OneCGIARScienceGroup; null if no records exists
   */
  public List<OneCGIARScienceGroup> getAll();

  /**
   * This method gets a OneCGIARScienceGroup object by a given finance code.
   * 
   * @param financeCode is the science group finance code.
   * @return a OneCGIARScienceGroup object.
   */
  public OneCGIARScienceGroup getScienceGroupByFinanceCode(String financeCode);

  /**
   * This method gets a OneCGIARScienceGroup object by a given identifier.
   * 
   * @param id is the science group identifier.
   * @return a OneCGIARScienceGroup object.
   */
  public OneCGIARScienceGroup getScienceGroupById(Long id);

  /**
   * This method gets a list of OneCGIARScienceGroup objects by a given scienceGroup parent identifier.
   * 
   * @param parentId is the scienceGroup parent identifier.
   * @return a list from OneCGIARScienceGroup; empty list if no records exists.
   */
  public List<OneCGIARScienceGroup> getScienceGroupsByParent(Long parentId);

  /**
   * This method saves the information of the given oneCGIARScienceGroup
   * 
   * @param oneCGIARScienceGroup - is the oneCGIARScienceGroup object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the oneCGIARScienceGroup was
   *         updated
   *         or -1 is some error occurred.
   */
  public OneCGIARScienceGroup save(OneCGIARScienceGroup oneCGIARScienceGroup);
}
