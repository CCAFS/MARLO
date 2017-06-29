package org.cgiar.ccafs.marlo.data.dao;

import org.cgiar.ccafs.marlo.data.dao.mysql.UserMySQLDAO;
import org.cgiar.ccafs.marlo.data.model.User;

import java.util.List;
import java.util.Map;

import com.google.inject.ImplementedBy;

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


/**
 * @author Hermes Jiménez - CIAT/CCAFS
 * @author Christian Garcia - CIAT/CCAFS
 */

@ImplementedBy(UserMySQLDAO.class)
public interface UserDAO {

  /**
   * Get the user's email that relates with the given user name.
   * 
   * @param username is the user nickname.
   * @return a String with the user's email, or null if nothing was found.
   */
  public String getEmailByUsername(String username);

  /**
   * This method gets the data of a Permissions identified with a given id.
   * 
   * @param userId: the user id to search permissions
   * @param crp: the crp acronym that user is loggin
   * @return a List with the select of the view user_permissions
   */

  public List<Map<String, Object>> getPermission(int userId, String crp);

  /**
   * This method gets the data of a User identified with a given id.
   * 
   * @param userId is the id of the User.
   * @return a Map with the user data.
   */
  public User getUser(Long id);

  /**
   * Get a Users object with the given email.
   * 
   * @param email is the user email
   * @return a Users object or null if the User not exist.
   */
  public User getUser(String email);

  /**
   * Save in the database the date and time that the user made its last login.
   * 
   * @param userData - User information
   * @return - True if the information was succesfully saved, false otherwise.
   */
  public boolean saveLastLogin(User user);

  /**
   * Save the user data into the database.
   * 
   * @param userData - Information to be saved.
   * @return the id of the user that was created, 0 if the user was updated or -1 if some error appeared.
   */
  public Long saveUser(User user);

  /**
   * This method looks for the active users that contains the
   * searchValue in its name, last name or email
   * 
   * @param searchValue
   * @return a list of maps with the information of the users found.
   */
  public List<User> searchUser(String searchValue);
}
