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
package org.cgiar.ccafs.marlo.data.service;

import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.service.impl.LocElementService;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(LocElementService.class)
public interface ILocElementService {


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
   * This method gets a list of locElement that are active
   * 
   * @return a list from LocElement null if no exist records
   */
  public List<LocElement> findAll();

  /**
   * This method gets a locElement object by a given locElement identifier.
   * 
   * @param locElementID is the locElement identifier.
   * @return a LocElement object.
   */
  public LocElement getLocElementById(long locElementID);

  /**
   * This method gets a locElement object by a given iso code identfier.
   * 
   * @param ISOCode is the iso code identifier.
   * @return a LocElement object.
   */
  public LocElement getLocElementByISOCode(String ISOCode);

  /**
   * This method gets a list of locElements belongs of the user
   * 
   * @param userId - the user id
   * @return List of LocElements or null if the user is invalid or not have roles.
   */
  public List<LocElement> getLocElementsByUserId(Long userId);

  /**
   * This method saves the information of the given locElement
   * 
   * @param locElement - is the locElement object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the locElement was
   *         updated
   *         or -1 is some error occurred.
   */
  public long saveLocElement(LocElement locElement);


}
