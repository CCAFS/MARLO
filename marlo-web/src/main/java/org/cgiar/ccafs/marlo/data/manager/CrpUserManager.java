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

import org.cgiar.ccafs.marlo.data.model.CrpUser;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface CrpUserManager {


  /**
   * This method removes a specific crpUser value from the database.
   * 
   * @param crpUserId is the crpUser identifier.
   * @return true if the crpUser was successfully deleted, false otherwise.
   */
  public void deleteCrpUser(long crpUserId);


  /**
   * This method get the crpUser if the user-crp relation exist and is active.
   * 
   * @param userId is the user id
   * @param crpId is the crp id
   * @return true if the CrpUser exists and is_active; false otherwise
   */
  public boolean existActiveCrpUser(long userId, long crpId);

  /**
   * This method validate if the crpUser identify with the given id exists in the system.
   * 
   * @param crpUserID is a crpUser identifier.
   * @return true if the crpUser exists, false otherwise.
   */
  public boolean existCrpUser(long crpUserID);

  /**
   * This method get the crpUser if the user and crp relation exist.
   * 
   * @param userId is the user id
   * @param crpId is the crp id
   * @return true if the CrpUser exists; false otherwise
   */
  public boolean existCrpUser(long userId, long crpId);

  /**
   * This method gets a list of crpUser that are active
   * 
   * @return a list from CrpUser null if no exist records
   */
  public List<CrpUser> findAll();


  /**
   * This method gets a crpUser object by a given crpUser identifier.
   * 
   * @param crpUserID is the crpUser identifier.
   * @return a CrpUser object.
   */
  public CrpUser getCrpUserById(long crpUserID);

  /**
   * This method saves the information of the given crpUser
   * 
   * @param crpUser - is the crpUser object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the crpUser was
   *         updated
   *         or -1 is some error occurred.
   */
  public CrpUser saveCrpUser(CrpUser crpUser);


}
