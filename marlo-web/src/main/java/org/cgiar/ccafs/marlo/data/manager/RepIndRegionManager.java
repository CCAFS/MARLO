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

import org.cgiar.ccafs.marlo.data.model.RepIndRegion;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface RepIndRegionManager {


  /**
   * This method removes a specific repIndRegion value from the database.
   * 
   * @param repIndRegionId is the repIndRegion identifier.
   * @return true if the repIndRegion was successfully deleted, false otherwise.
   */
  public void deleteRepIndRegion(long repIndRegionId);


  /**
   * This method validate if the repIndRegion identify with the given id exists in the system.
   * 
   * @param repIndRegionID is a repIndRegion identifier.
   * @return true if the repIndRegion exists, false otherwise.
   */
  public boolean existRepIndRegion(long repIndRegionID);


  /**
   * This method gets a list of repIndRegion that are active
   * 
   * @return a list from RepIndRegion null if no exist records
   */
  public List<RepIndRegion> findAll();


  /**
   * This method gets a repIndRegion object by a given repIndRegion identifier.
   * 
   * @param repIndRegionID is the repIndRegion identifier.
   * @return a RepIndRegion object.
   */
  public RepIndRegion getRepIndRegionById(long repIndRegionID);

  /**
   * This method saves the information of the given repIndRegion
   * 
   * @param repIndRegion - is the repIndRegion object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the repIndRegion was
   *         updated
   *         or -1 is some error occurred.
   */
  public RepIndRegion saveRepIndRegion(RepIndRegion repIndRegion);


}
