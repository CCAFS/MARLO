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

import org.cgiar.ccafs.marlo.data.model.Region;

import java.util.List;

public interface RegionsManager {

  /**
   * This method removes a specific Region value from the database.
   * 
   * @param regionId is the Region identifier.
   */

  public void deleteRegion(long regionId);

  /**
   * This method validate if the Region identify with the given id exists in the system.
   * 
   * @param regionId is a Region identifier.
   * @return true if the Region exists, false otherwise.
   */
  public boolean existRegion(long regionId);

  /**
   * This method gets a Region object by a given Region identifier.
   * 
   * @param id is the Region identifier.
   * @return a Region object.
   */
  public Region find(long id);

  /**
   * This method gets a list of Region that are active
   * 
   * @return a list from Region null if no exist records
   */
  public List<Region> findAll();

  /**
   * This method gets a Region object by a given acronym.
   * 
   * @param acronym is the account acronym.
   * @return a Region object.
   */
  public Region getRegionByAcronym(String acronym);

  /**
   * This method saves the information of the given Region
   * 
   * @param region- is the Region object with the new information to be added/updated.
   * @return a Region object new or existing in update case.
   */
  public Region save(Region region);
}
