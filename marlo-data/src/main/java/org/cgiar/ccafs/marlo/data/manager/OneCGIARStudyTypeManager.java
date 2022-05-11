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

/**************
 * @author Diego Perez - CIAT/CCAFS
 **************/

package org.cgiar.ccafs.marlo.data.manager;

import org.cgiar.ccafs.marlo.data.model.OneCGIARStudyType;

import java.util.List;

public interface OneCGIARStudyTypeManager {

  /**
   * This method removes a specific OneCGIARStudyType value from the database.
   * 
   * @param oneCGIARStudyTypeId is the OneCGIARStudyType identifier.
   * @return true if the OneCGIARStudyType was successfully deleted, false otherwise.
   */
  public void deleteOneCGIARStudyType(long oneCGIARStudyTypeId);

  /**
   * This method validate if the OneCGIARStudyType identify with the given id exists in the system.
   * 
   * @param oneCGIARStudyTypeID is a OneCGIARStudyType identifier.
   * @return true if the OneCGIARStudyType exists, false otherwise.
   */
  public boolean existOneCGIARStudyType(long oneCGIARStudyTypeID);


  /**
   * This method gets a list of OneCGIARStudyType that are active
   * 
   * @return a list from OneCGIARStudyType; null if no records exists
   */
  public List<OneCGIARStudyType> getAll();


  /**
   * This method gets a OneCGIARStudyType object by a given identifier.
   * 
   * @param id is the study type identifier.
   * @return a OneCGIARStudyType object.
   */
  public OneCGIARStudyType getStudyTypeById(long id);

  /**
   * This method saves the information of the given OneCGIARStudyType
   * 
   * @param oneCGIARStudyType - is the OneCGIARStudyType object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the OneCGIARStudyType was
   *         updated
   *         or -1 is some error occurred.
   */
  public OneCGIARStudyType save(OneCGIARStudyType oneCGIARStudyType);

}
