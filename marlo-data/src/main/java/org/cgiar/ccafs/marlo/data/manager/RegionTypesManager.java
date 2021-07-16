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

import org.cgiar.ccafs.marlo.data.model.RegionType;

import java.util.List;

public interface RegionTypesManager {

  /**
   * This method removes a specific RegionType value from the database.
   * 
   * @param regionTypeId is the RegionType identifier.
   */

  public void deleteRegionType(long regionTypeId);

  /**
   * This method validate if the RegionType identify with the given id exists in the system.
   * 
   * @param regionTypeId is a RegionType identifier.
   * @return true if the RegionType exists, false otherwise.
   */
  public boolean existRegionType(long regionTypeId);

  /**
   * This method gets a RegionType object by a given RegionType identifier.
   * 
   * @param id is the RegionType identifier.
   * @return a RegionType object.
   */
  public RegionType find(long id);

  /**
   * This method gets a list of RegionType that are active
   * 
   * @return a list from RegionType null if no exist records
   */
  public List<RegionType> findAll();


  /**
   * This method saves the information of the given RegionType
   * 
   * @param regionType - is the RegionType object with the new information to be added/updated.
   * @return a RegionType object new or existing in update case.
   */
  public RegionType save(RegionType regionType);
}
