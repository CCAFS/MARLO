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

import org.cgiar.ccafs.marlo.data.model.CrpAssumption;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface CrpAssumptionManager {


  /**
   * This method removes a specific crpAssumption value from the database.
   * 
   * @param crpAssumptionId is the crpAssumption identifier.
   * @return true if the crpAssumption was successfully deleted, false otherwise.
   */
  public void deleteCrpAssumption(long crpAssumptionId);


  /**
   * This method validate if the crpAssumption identify with the given id exists in the system.
   * 
   * @param crpAssumptionID is a crpAssumption identifier.
   * @return true if the crpAssumption exists, false otherwise.
   */
  public boolean existCrpAssumption(long crpAssumptionID);


  /**
   * This method gets a list of crpAssumption that are active
   * 
   * @return a list from CrpAssumption null if no exist records
   */
  public List<CrpAssumption> findAll();


  /**
   * This method gets a crpAssumption object by a given crpAssumption identifier.
   * 
   * @param crpAssumptionID is the crpAssumption identifier.
   * @return a CrpAssumption object.
   */
  public CrpAssumption getCrpAssumptionById(long crpAssumptionID);

  /**
   * This method saves the information of the given crpAssumption
   * 
   * @param crpAssumption - is the crpAssumption object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the crpAssumption was
   *         updated
   *         or -1 is some error occurred.
   */
  public CrpAssumption saveCrpAssumption(CrpAssumption crpAssumption);


}
