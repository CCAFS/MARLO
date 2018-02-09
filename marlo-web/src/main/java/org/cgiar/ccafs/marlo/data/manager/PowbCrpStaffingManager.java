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

import org.cgiar.ccafs.marlo.data.model.PowbCrpStaffing;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface PowbCrpStaffingManager {


  /**
   * This method removes a specific powbCrpStaffing value from the database.
   * 
   * @param powbCrpStaffingId is the powbCrpStaffing identifier.
   * @return true if the powbCrpStaffing was successfully deleted, false otherwise.
   */
  public void deletePowbCrpStaffing(long powbCrpStaffingId);


  /**
   * This method validate if the powbCrpStaffing identify with the given id exists in the system.
   * 
   * @param powbCrpStaffingID is a powbCrpStaffing identifier.
   * @return true if the powbCrpStaffing exists, false otherwise.
   */
  public boolean existPowbCrpStaffing(long powbCrpStaffingID);


  /**
   * This method gets a list of powbCrpStaffing that are active
   * 
   * @return a list from PowbCrpStaffing null if no exist records
   */
  public List<PowbCrpStaffing> findAll();


  /**
   * This method gets a powbCrpStaffing object by a given powbCrpStaffing identifier.
   * 
   * @param powbCrpStaffingID is the powbCrpStaffing identifier.
   * @return a PowbCrpStaffing object.
   */
  public PowbCrpStaffing getPowbCrpStaffingById(long powbCrpStaffingID);

  /**
   * This method saves the information of the given powbCrpStaffing
   * 
   * @param powbCrpStaffing - is the powbCrpStaffing object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the powbCrpStaffing was
   *         updated
   *         or -1 is some error occurred.
   */
  public PowbCrpStaffing savePowbCrpStaffing(PowbCrpStaffing powbCrpStaffing);


}
