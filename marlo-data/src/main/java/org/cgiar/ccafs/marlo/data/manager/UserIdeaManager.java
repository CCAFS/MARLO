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

import org.cgiar.ccafs.marlo.data.model.UserIdea;

import java.util.List;


/**
 * @author CCAFS
 */

public interface UserIdeaManager {


  /**
   * This method removes a specific userIdea value from the database.
   * 
   * @param userIdeaId is the userIdea identifier.
   * @return true if the userIdea was successfully deleted, false otherwise.
   */
  public void deleteUserIdea(long userIdeaId);


  /**
   * This method validate if the userIdea identify with the given id exists in the system.
   * 
   * @param userIdeaID is a userIdea identifier.
   * @return true if the userIdea exists, false otherwise.
   */
  public boolean existUserIdea(long userIdeaID);


  /**
   * This method gets a list of userIdea that are active
   * 
   * @return a list from UserIdea null if no exist records
   */
  public List<UserIdea> findAll();


  /**
   * This method gets a userIdea object by a given userIdea identifier.
   * 
   * @param userIdeaID is the userIdea identifier.
   * @return a UserIdea object.
   */
  public UserIdea getUserIdeaById(long userIdeaID);

  /**
   * This method saves the information of the given userIdea
   * 
   * @param userIdea - is the userIdea object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the userIdea was
   *         updated
   *         or -1 is some error occurred.
   */
  public UserIdea saveUserIdea(UserIdea userIdea);


}
