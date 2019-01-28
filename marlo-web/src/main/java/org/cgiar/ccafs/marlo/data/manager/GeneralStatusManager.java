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

import org.cgiar.ccafs.marlo.data.model.GeneralStatus;

import java.util.List;


/**
 * @author CCAFS
 */

public interface GeneralStatusManager {


  /**
   * This method removes a specific generalStatus value from the database.
   * 
   * @param generalStatusId is the generalStatus identifier.
   * @return true if the generalStatus was successfully deleted, false otherwise.
   */
  public void deleteGeneralStatus(long generalStatusId);


  /**
   * This method validate if the generalStatus identify with the given id exists in the system.
   * 
   * @param generalStatusID is a generalStatus identifier.
   * @return true if the generalStatus exists, false otherwise.
   */
  public boolean existGeneralStatus(long generalStatusID);


  /**
   * This method gets a list of generalStatus that are active
   * 
   * @return a list from GeneralStatus null if no exist records
   */
  public List<GeneralStatus> findAll();


  /**
   * This method gets a generalStatus object by a given generalStatus identifier.
   * 
   * @param generalStatusID is the generalStatus identifier.
   * @return a GeneralStatus object.
   */
  public GeneralStatus getGeneralStatusById(long generalStatusID);

  /**
   * This method saves the information of the given generalStatus
   * 
   * @param generalStatus - is the generalStatus object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the generalStatus was
   *         updated
   *         or -1 is some error occurred.
   */
  public GeneralStatus saveGeneralStatus(GeneralStatus generalStatus);


}
