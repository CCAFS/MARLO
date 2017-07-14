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

import org.cgiar.ccafs.marlo.data.manager.impl.UserManagerImp;
import org.cgiar.ccafs.marlo.data.model.User;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 * @author Christian Garcia - CIAT/CCAFS
 */
@ImplementedBy(UserManagerImp.class)
public interface UserManager {

  /**
   * This method gets the data of a Permissions identified with a given id.
   * 
   * @param userId: the user id to search permissions
   * @param center: the center acronym that user is loggin
   * @return a List with string permissions
   */


  public List<String> getCenterPermission(int userId, String crp);

  /**
   * This method gets the data of a Permissions identified with a given id.
   * 
   * @param userId: the user id to search permissions
   * @param crp: the crp acronym that user is loggin
   * @return a List with string permissions
   */


  public List<String> getPermission(int userId, String crp);

  /**
   * This method find an user identify with a given id.
   * 
   * @param userId is the id of the user.
   * @return a User object.
   */
  public User getUser(Long userId);

  /**
   * Get the user identified by the specified email parameter.
   * 
   * @param email of the user.
   * @return User object representing the user identified by the email provided or Null if the user doesn't exist in the
   *         database.
   */
  public User getUserByEmail(String email);

  /**
   * Get the user identified by the specified username parameter.
   * 
   * @param username of the user - cgiar account username.
   * @return User object representing the user identifier by the username provided or Null if the user doesn't exist in
   *         the database.
   */
  public User getUserByUsername(String username);

  /**
   * Authenticate a user.
   * 
   * @param email of the user.
   * @param password of the user.
   * @return a User object representing the user identified by the email provided or Null if login failed.
   */
  public User login(String email, String password);


  /**
   * Save in the database the date and time that the user made its last login.
   * 
   * @param user - User information
   * @return - True if the information was successfully saved, false otherwise.
   */
  public boolean saveLastLogin(User user);

  /**
   * Create or update a user in the system by saving it into the the database.
   * 
   * @param user - The user information
   * @param modifiedBy - is the user that is creating/updating the given user.
   * @return the id of the user id that was created, 0 if the user was updated and -1 if some error occurred.
   */
  public Long saveUser(User user, User modifiedBy);

  /**
   * This method looks for the active users that contains the
   * searchValue in its name, last name or email
   * 
   * @param searchValue
   * @return the list of users that match the search parameter or an empty list.
   */
  public List<User> searchUser(String searchValue);
}
