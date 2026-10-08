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

package org.cgiar.ccafs.marlo.data.manager.impl;

import org.cgiar.ccafs.marlo.data.dao.UserDAO;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.utils.MD5Convert;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

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
@Named
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
  public List<String> getCenterPermission(int userId, String centerId) {
    List<String> permissions = new ArrayList<String>();

    List<Map<String, Object>> view = userDAO.getCenterPermission(userId, centerId);
    if (view != null) {
      for (Map<String, Object> map : view) {
        if (map.get("permission") != null) {
          permissions.add(map.get("permission").toString());
        }

      }
    }

    return permissions;
  }

  @Override
  public List<String> getPermission(int userId, String crpId) {
    List<String> permissions = new ArrayList<String>();

    List<Map<String, Object>> view = userDAO.getPermission(userId, crpId);
    if (view != null) {
      for (Map<String, Object> map : view) {
        if (map.get("permission") != null) {
          permissions.add(map.get("permission").toString());
        }

      }
    }

    return permissions;
  }

  @Override
  public User getUser(Long userId) {
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
    LOG.debug("No user was found for the given email.");
    return null;
  }

  @Override
  public User getUserByUsername(String username) {
    String email = userDAO.getEmailByUsername(username);
    if (email != null) {
      return this.getUserByEmail(email);
    }
    LOG.debug("No user was found for the given username.");
    return null;
  }

  @Override
  public User getActiveSuperAdminUserByUsernameOccurrence() {
    return userDAO.getActiveSuperAdminUserByUsernameOccurrence();
  }

  /**
   * Returns the id of the account an email or username names, so that a log line about a failed login can name the
   * account without logging what was typed: it can be a password entered in the wrong field. Never throws, so that a
   * failure here cannot change the outcome of the login.
   *
   * @param emailOrUsername the identifier the login was attempted with
   * @return the account id, or null when no account matches or the lookup fails
   */
  private Long accountIdForLog(String emailOrUsername) {
    try {
      User account = emailOrUsername.contains("@") ? this.getUserByEmail(emailOrUsername)
        : this.getUserByUsername(emailOrUsername);
      return account != null ? account.getId() : null;
    } catch (RuntimeException e) {
      LOG.debug("Could not resolve the account of a failed login for its log line", e);
      return null;
    }
  }

  @Override
  public User login(String email, String password) {
    User userFound = null;

    if (email != null && password != null) {
      Subject currentUser = SecurityUtils.getSubject();
      if (!currentUser.isAuthenticated()) {
        UsernamePasswordToken token = new UsernamePasswordToken(email, password);
        LOG.info("Trying to log in a user against the database.");
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
          LOG.warn("Login failed: no account matches the given email or username.");
        } catch (IncorrectCredentialsException ice) {
          LOG.warn("Login failed for user {}: the credentials were rejected.", this.accountIdForLog(email));
        } catch (LockedAccountException lae) {
          LOG.warn("Login failed for user {}: the account is locked.", this.accountIdForLog(email));
        }
      } else {
        Long userID = (Long) currentUser.getPrincipals().getPrimaryPrincipal();
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
  public User saveUser(User user) {
    if (!user.isCgiarUser() && user.getPassword() != null) {
      user.setPassword(MD5Convert.stringToMD5(user.getPassword()));
    }

    return userDAO.saveUser(user);

  }

  @Override
  public List<User> searchUser(String searchValue) {
    return userDAO.searchUser(searchValue);
  }

}
