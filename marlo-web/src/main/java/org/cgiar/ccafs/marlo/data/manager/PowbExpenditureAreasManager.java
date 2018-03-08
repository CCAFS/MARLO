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

import org.cgiar.ccafs.marlo.data.model.PowbExpenditureAreas;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface PowbExpenditureAreasManager {


  /**
   * This method removes a specific powbExpenditureAreas value from the database.
   * 
   * @param powbExpenditureAreasId is the powbExpenditureAreas identifier.
   * @return true if the powbExpenditureAreas was successfully deleted, false otherwise.
   */
  public void deletePowbExpenditureAreas(long powbExpenditureAreasId);


  /**
   * This method validate if the powbExpenditureAreas identify with the given id exists in the system.
   * 
   * @param powbExpenditureAreasID is a powbExpenditureAreas identifier.
   * @return true if the powbExpenditureAreas exists, false otherwise.
   */
  public boolean existPowbExpenditureAreas(long powbExpenditureAreasID);


  /**
   * This method gets a list of powbExpenditureAreas that are active
   * 
   * @return a list from PowbExpenditureAreas null if no exist records
   */
  public List<PowbExpenditureAreas> findAll();


  /**
   * This method gets a powbExpenditureAreas object by a given powbExpenditureAreas identifier.
   * 
   * @param powbExpenditureAreasID is the powbExpenditureAreas identifier.
   * @return a PowbExpenditureAreas object.
   */
  public PowbExpenditureAreas getPowbExpenditureAreasById(long powbExpenditureAreasID);

  /**
   * This method saves the information of the given powbExpenditureAreas
   * 
   * @param powbExpenditureAreas - is the powbExpenditureAreas object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the powbExpenditureAreas was
   *         updated
   *         or -1 is some error occurred.
   */
  public PowbExpenditureAreas savePowbExpenditureAreas(PowbExpenditureAreas powbExpenditureAreas);


}
