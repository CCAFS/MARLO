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

import org.cgiar.ccafs.marlo.data.model.CenterOutputsOutcome;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface CenterOutputsOutcomeManager {


  /**
   * This method removes a specific centerOutputsOutcome value from the database.
   * 
   * @param centerOutputsOutcomeId is the centerOutputsOutcome identifier.
   * @return true if the centerOutputsOutcome was successfully deleted, false otherwise.
   */
  public void deleteCenterOutputsOutcome(long centerOutputsOutcomeId);


  /**
   * This method validate if the centerOutputsOutcome identify with the given id exists in the system.
   * 
   * @param centerOutputsOutcomeID is a centerOutputsOutcome identifier.
   * @return true if the centerOutputsOutcome exists, false otherwise.
   */
  public boolean existCenterOutputsOutcome(long centerOutputsOutcomeID);


  /**
   * This method gets a list of centerOutputsOutcome that are active
   * 
   * @return a list from CenterOutputsOutcome null if no exist records
   */
  public List<CenterOutputsOutcome> findAll();


  /**
   * This method gets a centerOutputsOutcome object by a given centerOutputsOutcome identifier.
   * 
   * @param centerOutputsOutcomeID is the centerOutputsOutcome identifier.
   * @return a CenterOutputsOutcome object.
   */
  public CenterOutputsOutcome getCenterOutputsOutcomeById(long centerOutputsOutcomeID);

  /**
   * This method saves the information of the given centerOutputsOutcome
   * 
   * @param centerOutputsOutcome - is the centerOutputsOutcome object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the centerOutputsOutcome was
   *         updated
   *         or -1 is some error occurred.
   */
  public CenterOutputsOutcome saveCenterOutputsOutcome(CenterOutputsOutcome centerOutputsOutcome);


}
