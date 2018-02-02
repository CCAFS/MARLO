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

import org.cgiar.ccafs.marlo.data.model.PowbToc;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface PowbTocManager {


  /**
   * This method removes a specific powbToc value from the database.
   * 
   * @param powbTocId is the powbToc identifier.
   * @return true if the powbToc was successfully deleted, false otherwise.
   */
  public void deletePowbToc(long powbTocId);


  /**
   * This method validate if the powbToc identify with the given id exists in the system.
   * 
   * @param powbTocID is a powbToc identifier.
   * @return true if the powbToc exists, false otherwise.
   */
  public boolean existPowbToc(long powbTocID);


  /**
   * This method gets a list of powbToc that are active
   * 
   * @return a list from PowbToc null if no exist records
   */
  public List<PowbToc> findAll();


  /**
   * This method gets a powbToc object by a given powbToc identifier.
   * 
   * @param powbTocID is the powbToc identifier.
   * @return a PowbToc object.
   */
  public PowbToc getPowbTocById(long powbTocID);

  /**
   * This method saves the information of the given powbToc
   * 
   * @param powbToc - is the powbToc object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the powbToc was
   *         updated
   *         or -1 is some error occurred.
   */
  public PowbToc savePowbToc(PowbToc powbToc);


}
