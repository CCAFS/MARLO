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

import org.cgiar.ccafs.marlo.data.model.LocElementRegion;

import java.util.List;

public interface LocElementRegionsManager {

  /**
   * This method removes a specific LocElementRegion value from the database.
   * 
   * @param locElementRegionId is the LocElementRegion identifier.
   */

  public void deleteLocElementRegion(long locElementRegionId);

  /**
   * This method validate if the LocElementRegion identify with the given id exists in the system.
   * 
   * @param locElementRegionId is a LocElementRegion identifier.
   * @return true if the LocElementRegion exists, false otherwise.
   */
  public boolean existLocElementRegion(long locElementRegionId);

  /**
   * This method gets a LocElementRegion object by a given LocElementRegion identifier.
   * 
   * @param id is the LocElementRegion identifier.
   * @return a LocElementRegion object.
   */
  public LocElementRegion find(long id);

  /**
   * This method gets a list of LocElementRegion that are active
   * 
   * @return a list from LocElementRegion null if no exist records
   */
  public List<LocElementRegion> findAll();


  /**
   * This method saves the information of the given LocElementRegion
   * 
   * @param locElementRegion- is the LocElementRegion object with the new information to be added/updated.
   * @return a LocElementRegion object new or existing in update case.
   */
  public LocElementRegion save(LocElementRegion locElementRegion);
}
