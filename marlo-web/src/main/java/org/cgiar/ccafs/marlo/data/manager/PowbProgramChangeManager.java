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

import org.cgiar.ccafs.marlo.data.model.PowbProgramChange;

import java.util.List;


/**
 * @author CCAFS
 */

public interface PowbProgramChangeManager {


  /**
   * This method removes a specific powbProgramChange value from the database.
   * 
   * @param powbProgramChangeId is the powbProgramChange identifier.
   * @return true if the powbProgramChange was successfully deleted, false otherwise.
   */
  public void deletePowbProgramChange(long powbProgramChangeId);


  /**
   * This method validate if the powbProgramChange identify with the given id exists in the system.
   * 
   * @param powbProgramChangeID is a powbProgramChange identifier.
   * @return true if the powbProgramChange exists, false otherwise.
   */
  public boolean existPowbProgramChange(long powbProgramChangeID);


  /**
   * This method gets a list of powbProgramChange that are active
   * 
   * @return a list from PowbProgramChange null if no exist records
   */
  public List<PowbProgramChange> findAll();


  /**
   * This method gets a powbProgramChange object by a given powbProgramChange identifier.
   * 
   * @param powbProgramChangeID is the powbProgramChange identifier.
   * @return a PowbProgramChange object.
   */
  public PowbProgramChange getPowbProgramChangeById(long powbProgramChangeID);

  /**
   * This method saves the information of the given powbProgramChange
   * 
   * @param powbProgramChange - is the powbProgramChange object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the powbProgramChange was
   *         updated
   *         or -1 is some error occurred.
   */
  public PowbProgramChange savePowbProgramChange(PowbProgramChange powbProgramChange);


}
