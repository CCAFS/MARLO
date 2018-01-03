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

import org.cgiar.ccafs.marlo.data.model.IpElementType;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface IpElementTypeManager {


  /**
   * This method removes a specific ipElementType value from the database.
   * 
   * @param ipElementTypeId is the ipElementType identifier.
   * @return true if the ipElementType was successfully deleted, false otherwise.
   */
  public void deleteIpElementType(long ipElementTypeId);


  /**
   * This method validate if the ipElementType identify with the given id exists in the system.
   * 
   * @param ipElementTypeID is a ipElementType identifier.
   * @return true if the ipElementType exists, false otherwise.
   */
  public boolean existIpElementType(long ipElementTypeID);


  /**
   * This method gets a list of ipElementType that are active
   * 
   * @return a list from IpElementType null if no exist records
   */
  public List<IpElementType> findAll();


  /**
   * This method gets a ipElementType object by a given ipElementType identifier.
   * 
   * @param ipElementTypeID is the ipElementType identifier.
   * @return a IpElementType object.
   */
  public IpElementType getIpElementTypeById(long ipElementTypeID);

  /**
   * This method saves the information of the given ipElementType
   * 
   * @param ipElementType - is the ipElementType object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the ipElementType was
   *         updated
   *         or -1 is some error occurred.
   */
  public IpElementType saveIpElementType(IpElementType ipElementType);


}
