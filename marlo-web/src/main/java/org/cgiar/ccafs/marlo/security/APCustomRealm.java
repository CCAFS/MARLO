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

package org.cgiar.ccafs.marlo.security;


import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.manager.UserRoleManager;
import org.cgiar.ccafs.marlo.data.model.ADLoginMessages;
import org.cgiar.ccafs.marlo.data.model.Center;
import org.cgiar.ccafs.marlo.data.model.CenterUserRole;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.data.model.UserRole;
import org.cgiar.ccafs.marlo.security.authentication.Authenticator;
import org.cgiar.ccafs.marlo.utils.APConfig;

import org.cgiar.ciat.auth.LDAPService;
import org.cgiar.ciat.auth.LDAPUser;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.AllowAllCredentialsMatcher;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Is a class component that can access application security data such as users, roles, and permissions.
 * 
 * @author Hernán David Carvajal
 * @author Héctor Fabio Tobón
 * @author Chirstian David Garcia
 * @author Hermes Jimenez
 */

public class APCustomRealm extends AuthorizingRealm {

  // Logger
  public static Logger LOG = LoggerFactory.getLogger(APCustomRealm.class);

  // Variables
  final AllowAllCredentialsMatcher credentialsMatcher = new AllowAllCredentialsMatcher();
  private APConfig config;


  // Managers
  private UserManager userManager;
  private UserRoleManager userRoleManager;


  @Named("DB")
  private Authenticator dbAuthenticator;

  @Named("LDAP")
  private Authenticator ldapAuthenticator;

  @Inject
  public APCustomRealm(UserManager userManager, UserRoleManager userRoleManager,
    @Named("DB") Authenticator dbAuthenticator, @Named("LDAP") Authenticator ldapAuthenticator, APConfig config) {
    super(new MemoryConstrainedCacheManager());
    this.userManager = userManager;
    this.userRoleManager = userRoleManager;
    this.dbAuthenticator = dbAuthenticator;
    this.ldapAuthenticator = ldapAuthenticator;
    this.config = config;
    this.setName("APCustomRealm");
  }

  @Override
  public void clearCachedAuthorizationInfo(PrincipalCollection principals) {
    super.clearCachedAuthorizationInfo(principals);
  }

  /**
   * This method check the correct user authentication
   * 
   * @param token
   * @return
   * @throws AuthenticationException
   */
  @Override
  protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
    // identify account to log to
    UsernamePasswordToken userPassToken = (UsernamePasswordToken) token;
    final String username = userPassToken.getUsername();
    final String password = new String(userPassToken.getPassword());
    User user;
    Map<String, Object> authenticated = new HashMap<>();

    Session session = SecurityUtils.getSubject().getSession();

    // Get user info from db
    if (username.contains("@")) {
      user = userManager.getUserByEmail(username);
    } else {
      // if user is login with his user name, we must attach the cgiar.org.
      user = userManager.getUserByUsername(username);
    }

    if (user != null) {
      if (user.isActive()) {
        if (user.isCgiarUser()) {
          if (user.getUsername() == null) {
            if (this.getCgiarNickname(user)) {
              authenticated = ldapAuthenticator.authenticate(user.getUsername(), password);
            } else {
              authenticated.put(APConstants.LOGIN_STATUS, false);
              authenticated.put(APConstants.LOGIN_MESSAGE, ADLoginMessages.ERROR_NO_SUCH_USER.getValue());
            }
          } else {
            authenticated = ldapAuthenticator.authenticate(user.getUsername(), password);
          }
        } else {
          authenticated = dbAuthenticator.authenticate(user.getEmail(), password);
        }
      } else {
        authenticated.put(APConstants.LOGIN_STATUS, false);
        authenticated.put(APConstants.LOGIN_MESSAGE, APConstants.USER_DISABLED);
      }


      session.setAttribute(APConstants.LOGIN_MESSAGE, authenticated.get(APConstants.LOGIN_MESSAGE));

      if (!(boolean) authenticated.get(APConstants.LOGIN_STATUS)) {
        throw new IncorrectCredentialsException();
      }

      SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user.getId(), user.getPassword(), this.getName());

      return info;
    }

    return null;
  }

  /**
   * This method check and get the user session privileges
   * 
   * @param principals
   * @return SimpleAuthorizationInfo object whit the permissions list of the current user
   */
  @Override
  protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

    SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
    Session session = SecurityUtils.getSubject().getSession();

    User user = userManager.getUser((Long) principals.getPrimaryPrincipal());

    Crp crp = (Crp) session.getAttribute(APConstants.SESSION_CRP);
    Center center = (Center) session.getAttribute(APConstants.SESSION_CENTER);

    /*
     * Check
     */
    if (crp != null) {
      for (UserRole userRole : user.getUserRoles()) {
        authorizationInfo.addRole(userRole.getRole().getAcronym());
      }
      authorizationInfo.addStringPermissions(userManager.getPermission(user.getId().intValue(), crp.getAcronym()));
      return authorizationInfo;
    }

    if (center != null) {
      for (CenterUserRole userRole : user.getCenterUserRoles()) {
        authorizationInfo.addRole(userRole.getRole().getAcronym());
      }
      authorizationInfo
        .addStringPermissions(userManager.getCenterPermission(user.getId().intValue(), center.getAcronym()));
      return authorizationInfo;
    }

    return null;


  }

  boolean getCgiarNickname(User user) {
    // ldap createe instace
    LDAPService service = new LDAPService();
    // validate internal or external connection
    if (config.isProduction()) {
      service.setInternalConnection(false);
    } else {
      service.setInternalConnection(true);
    }
    // get info form LDAP User
    LDAPUser ldapUser = service.searchUserByEmail(user.getEmail());
    if (ldapUser != null) {
      // get the username from LDAP
      user.setUsername(ldapUser.getLogin().toLowerCase());
      // Save user
      userManager.saveUser(user, user);
      return true;
    } else {
      return false;
    }


  }

  @Override
  public CredentialsMatcher getCredentialsMatcher() {
    return credentialsMatcher;
  }
}
