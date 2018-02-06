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

import org.cgiar.ccafs.marlo.data.model.PowbExpectedCrpProgress;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface PowbExpectedCrpProgressManager {


  /**
   * This method removes a specific powbExpectedCrpProgress value from the database.
   * 
   * @param powbExpectedCrpProgressId is the powbExpectedCrpProgress identifier.
   * @return true if the powbExpectedCrpProgress was successfully deleted, false otherwise.
   */
  public void deletePowbExpectedCrpProgress(long powbExpectedCrpProgressId);


  /**
   * This method validate if the powbExpectedCrpProgress identify with the given id exists in the system.
   * 
   * @param powbExpectedCrpProgressID is a powbExpectedCrpProgress identifier.
   * @return true if the powbExpectedCrpProgress exists, false otherwise.
   */
  public boolean existPowbExpectedCrpProgress(long powbExpectedCrpProgressID);


  /**
   * This method gets a list of powbExpectedCrpProgress that are active
   * 
   * @return a list from PowbExpectedCrpProgress null if no exist records
   */
  public List<PowbExpectedCrpProgress> findAll();

  public List<PowbExpectedCrpProgress> findByProgram(long crpProgramID);


  /**
   * This method gets a powbExpectedCrpProgress object by a given powbExpectedCrpProgress identifier.
   * 
   * @param powbExpectedCrpProgressID is the powbExpectedCrpProgress identifier.
   * @return a PowbExpectedCrpProgress object.
   */
  public PowbExpectedCrpProgress getPowbExpectedCrpProgressById(long powbExpectedCrpProgressID);

  /**
   * This method saves the information of the given powbExpectedCrpProgress
   * 
   * @param powbExpectedCrpProgress - is the powbExpectedCrpProgress object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the powbExpectedCrpProgress
   *         was
   *         updated
   *         or -1 is some error occurred.
   */
  public PowbExpectedCrpProgress savePowbExpectedCrpProgress(PowbExpectedCrpProgress powbExpectedCrpProgress);


}
