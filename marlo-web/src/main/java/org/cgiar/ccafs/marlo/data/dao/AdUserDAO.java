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

import org.cgiar.ccafs.marlo.data.model.AdUser;

import java.util.List;

public interface AdUserDAO {

  /**
   * This method removes a specific adUser value from the database.
   * 
   * @param adUserId is the adUser identifier.
   * @return true if the adUser was successfully deleted, false otherwise.
   */
  public void deleteAdUser(long adUserId);

  /**
   * This method validate if the adUser identify with the given id exists in the system.
   * 
   * @param adUserID is a adUser identifier.
   * @return true if the adUser exists, false otherwise.
   */
  public boolean existAdUser(long adUserID);

  /**
   * This method gets a adUser object by a given adUser identifier.
   * 
   * @param adUserID is the adUser identifier.
   * @return a AdUser object.
   */
  public AdUser find(long id);

  /**
   * This method gets a list of adUser that are active
   * 
   * @return a list from AdUser null if no exist records
   */
  public List<AdUser> findAll();

  /**
   * This method gets a adUser object by a given adUser identification.
   * 
   * @param adUserID is the adUser identification.
   * @return a AdUser object.
   */
  public AdUser findByEmail(String login);


  /**
   * This method saves the information of the given adUser
   * 
   * @param adUser - is the adUser object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the adUser was
   *         updated
   *         or -1 is some error occurred.
   */
  public AdUser save(AdUser adUser);


  /**
   * This method gets a list of adUser that are active and match with the parameter
   * 
   * @return a list from AdUser null if no exist records
   */
  public List<AdUser> searchUsers(String parameter);
}
