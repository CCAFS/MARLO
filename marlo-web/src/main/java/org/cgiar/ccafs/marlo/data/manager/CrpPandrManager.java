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

import org.cgiar.ccafs.marlo.data.model.CrpPandr;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface CrpPandrManager {


  /**
   * This method removes a specific crpPandr value from the database.
   * 
   * @param crpPandrId is the crpPandr identifier.
   * @return true if the crpPandr was successfully deleted, false otherwise.
   */
  public void deleteCrpPandr(long crpPandrId);


  /**
   * This method validate if the crpPandr identify with the given id exists in the system.
   * 
   * @param crpPandrID is a crpPandr identifier.
   * @return true if the crpPandr exists, false otherwise.
   */
  public boolean existCrpPandr(long crpPandrID);


  /**
   * This method gets a list of crpPandr that are active
   * 
   * @return a list from CrpPandr null if no exist records
   */
  public List<CrpPandr> findAll();


  /**
   * This method gets a crpPandr object by a given crpPandr identifier.
   * 
   * @param crpPandrID is the crpPandr identifier.
   * @return a CrpPandr object.
   */
  public CrpPandr getCrpPandrById(long crpPandrID);

  /**
   * This method saves the information of the given crpPandr
   * 
   * @param crpPandr - is the crpPandr object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the crpPandr was
   *         updated
   *         or -1 is some error occurred.
   */
  public CrpPandr saveCrpPandr(CrpPandr crpPandr);


}
