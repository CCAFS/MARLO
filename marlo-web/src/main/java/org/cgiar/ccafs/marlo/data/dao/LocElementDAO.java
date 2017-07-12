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

import org.cgiar.ccafs.marlo.data.dao.mysql.LocElementMySQLDAO;
import org.cgiar.ccafs.marlo.data.model.LocElement;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(LocElementMySQLDAO.class)
public interface LocElementDAO {

  /**
   * This method removes a specific locElement value from the database.
   * 
   * @param locElementId is the locElement identifier.
   * @return true if the locElement was successfully deleted, false otherwise.
   */
  public boolean deleteLocElement(long locElementId);

  /**
   * This method validate if the locElement identify with the given id exists in the system.
   * 
   * @param locElementID is a locElement identifier.
   * @return true if the locElement exists, false otherwise.
   */
  public boolean existLocElement(long locElementID);

  /**
   * This method gets a locElement object by a given locElement identifier.
   * 
   * @param locElementID is the locElement identifier.
   * @return a LocElement object.
   */
  public LocElement find(long id);


  /**
   * This method gets a list of locElement that are active
   * 
   * @return a list from LocElement null if no exist records
   */
  public List<LocElement> findAll();

  /**
   * This method gets a locElement object by a given locElement IsoCode.
   * 
   * @param ISOCode of the LocElement.
   * @return a LocElement object.
   */
  public LocElement findISOCode(String ISOcode);

  /**
   * This method gets a locElement object by a parent locElement.
   * 
   * @param parentId is the locElement parent id.
   * @return a LocElement object.
   */
  public List<LocElement> findLocElementByParent(Long parentId);


  /**
   * This method saves the information of the given locElement
   * 
   * @param locElement - is the locElement object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the locElement was
   *         updated
   *         or -1 is some error occurred.
   */
  public long save(LocElement locElement);
}
