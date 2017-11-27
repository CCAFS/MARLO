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

import org.cgiar.ccafs.marlo.data.manager.impl.CrpLocElementTypeManagerImpl;
import org.cgiar.ccafs.marlo.data.model.CrpLocElementType;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(CrpLocElementTypeManagerImpl.class)
public interface CrpLocElementTypeManager {


  /**
   * This method removes a specific crpLocElementType value from the database.
   * 
   * @param crpLocElementTypeId is the crpLocElementType identifier.
   * @return true if the crpLocElementType was successfully deleted, false otherwise.
   */
  public void deleteCrpLocElementType(long crpLocElementTypeId);


  /**
   * This method validate if the crpLocElementType identify with the given id exists in the system.
   * 
   * @param crpLocElementTypeID is a crpLocElementType identifier.
   * @return true if the crpLocElementType exists, false otherwise.
   */
  public boolean existCrpLocElementType(long crpLocElementTypeID);


  /**
   * This method gets a list of crpLocElementType that are active
   * 
   * @return a list from CrpLocElementType null if no exist records
   */
  public List<CrpLocElementType> findAll();


  public CrpLocElementType getByLocElementTypeAndCrpId(long crpId, long locElementTypeID);


  /**
   * This method gets a crpLocElementType object by a given crpLocElementType identifier.
   * 
   * @param crpLocElementTypeID is the crpLocElementType identifier.
   * @return a CrpLocElementType object.
   */
  public CrpLocElementType getCrpLocElementTypeById(long crpLocElementTypeID);

  /**
   * This method saves the information of the given crpLocElementType
   * 
   * @param crpLocElementType - is the crpLocElementType object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the crpLocElementType was
   *         updated
   *         or -1 is some error occurred.
   */
  public CrpLocElementType saveCrpLocElementType(CrpLocElementType crpLocElementType);


}
