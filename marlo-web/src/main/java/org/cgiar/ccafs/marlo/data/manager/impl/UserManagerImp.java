/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/

package org.cgiar.ccafs.marlo.data.manager.impl;

import org.cgiar.ccafs.marlo.data.dao.UserDAO;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.utils.MD5Convert;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class UserManagerImp implements UserManager {

  // Logger
  private static final Logger LOG = LoggerFactory.getLogger(UserManagerImp.class);

  // DAO
  private UserDAO userDAO;

  @Inject
  public UserManagerImp(UserDAO userDAO) {
    this.userDAO = userDAO;
  }

  @Override
  public List<String> getPermission(int userId, int crpId) {
    List<String> permissions = new ArrayList<String>();

    List<Map<String, Object>> view = userDAO.getPermission(userId, crpId);
    for (Map<String, Object> map : view) {
      permissions.add(map.get("permission").toString());
    }
    return permissions;
  }

  @Override
  public User getUser(int userId) {
    User user = userDAO.getUser(userId);
    if (user != null) {
      return user;
    }
    LOG.warn("Information related to the user with id {} wasn't found.", userId);
    return null;
  }

  @Override
  public User getUserByEmail(String email) {
    User user = userDAO.getUser(email);
    if (user != null) {
      return user;
    }
    LOG.warn("Information related to the user {} wasn't found.", email);
    return null;
  }

  @Override
  public User getUserByUsername(String username) {
    String email = userDAO.getEmailByUsername(username);
    return this.getUserByEmail(email);
  }

  @Override
  public User login(String email, String password) {
    User userFound = null;

    if (email != null && password != null) {
      Subject currentUser = SecurityUtils.getSubject();
      if (!currentUser.isAuthenticated()) {
        UsernamePasswordToken token = new UsernamePasswordToken(email, password);
        LOG.info("Trying to log in the user {} against the database.", email);
        try {
          currentUser.login(token);

          // If user is logging-in with their email account.
          if (email.contains("@")) {
            userFound = this.getUserByEmail(email);
          } else {
            // if user is loggin with his username, we must attach the cgiar.org.
            userFound = this.getUserByUsername(email);
          }
        } catch (UnknownAccountException uae) {
          LOG.warn("There is no user with email of " + token.getPrincipal());
        } catch (IncorrectCredentialsException ice) {
          LOG.warn("Password for account " + token.getPrincipal() + " was incorrect!");
        } catch (LockedAccountException lae) {
          LOG.warn("The account for username " + token.getPrincipal() + " is locked.  "
            + "Please contact your administrator to unlock it.");
        }
      } else {
        int userID = ((Long) currentUser.getPrincipals().getPrimaryPrincipal()).intValue();
        userFound = this.getUser(userID);
        LOG.info("Already logged in");
      }

    }
    return userFound;
  }

  @Override
  public boolean saveLastLogin(User user) {
    user.setLastLogin(new Date());
    return userDAO.saveLastLogin(user);
  }

  @Override
  public int saveUser(User user, User modifiedBy) {
    user.setPassword(MD5Convert.stringToMD5(user.getPassword()));
    user.setCreatedBy(modifiedBy.getId());
    return userDAO.saveUser(user);
  }

}
