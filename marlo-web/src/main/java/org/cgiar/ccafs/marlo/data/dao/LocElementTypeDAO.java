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


package org.cgiar.ccafs.marlo.data.dao;

import org.cgiar.ccafs.marlo.data.dao.mysql.LocElementTypeMySQLDAO;
import org.cgiar.ccafs.marlo.data.model.LocElementType;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(LocElementTypeMySQLDAO.class)
public interface LocElementTypeDAO {

  /**
   * This method removes a specific locElementType value from the database.
   * 
   * @param locElementTypeId is the locElementType identifier.
   * @return true if the locElementType was successfully deleted, false otherwise.
   */
  public void deleteLocElementType(long locElementTypeId);

  /**
   * This method validate if the locElementType identify with the given id exists in the system.
   * 
   * @param locElementTypeID is a locElementType identifier.
   * @return true if the locElementType exists, false otherwise.
   */
  public boolean existLocElementType(long locElementTypeID);

  /**
   * This method gets a locElementType object by a given locElementType identifier.
   * 
   * @param locElementTypeID is the locElementType identifier.
   * @return a LocElementType object.
   */
  public LocElementType find(long id);

  /**
   * This method gets a list of locElementType that are active
   * 
   * @return a list from LocElementType null if no exist records
   */
  public List<LocElementType> findAll();


  /**
   * This method saves the information of the given locElementType
   * 
   * @param locElementType - is the locElementType object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the locElementType was
   *         updated
   *         or -1 is some error occurred.
   */
  public LocElementType save(LocElementType locElementType);
}
