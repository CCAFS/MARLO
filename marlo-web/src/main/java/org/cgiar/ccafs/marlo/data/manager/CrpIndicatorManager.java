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

import org.cgiar.ccafs.marlo.data.model.CrpIndicator;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface CrpIndicatorManager {


  /**
   * This method removes a specific crpIndicator value from the database.
   * 
   * @param crpIndicatorId is the crpIndicator identifier.
   * @return true if the crpIndicator was successfully deleted, false otherwise.
   */
  public void deleteCrpIndicator(long crpIndicatorId);


  /**
   * This method validate if the crpIndicator identify with the given id exists in the system.
   * 
   * @param crpIndicatorID is a crpIndicator identifier.
   * @return true if the crpIndicator exists, false otherwise.
   */
  public boolean existCrpIndicator(long crpIndicatorID);


  /**
   * This method gets a list of crpIndicator that are active
   * 
   * @return a list from CrpIndicator null if no exist records
   */
  public List<CrpIndicator> findAll();


  /**
   * This method gets a crpIndicator object by a given crpIndicator identifier.
   * 
   * @param crpIndicatorID is the crpIndicator identifier.
   * @return a CrpIndicator object.
   */
  public CrpIndicator getCrpIndicatorById(long crpIndicatorID);

  /**
   * This method saves the information of the given crpIndicator
   * 
   * @param crpIndicator - is the crpIndicator object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the crpIndicator was
   *         updated
   *         or -1 is some error occurred.
   */
  public CrpIndicator saveCrpIndicator(CrpIndicator crpIndicator);


}
