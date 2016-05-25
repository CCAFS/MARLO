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

package org.cgiar.ccafs.marlo.security;


import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.authentication.Authenticator;

import com.google.inject.Inject;
import com.google.inject.name.Named;
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


  // Managers
  private UserManager userManager;


  @Named("DB")
  private Authenticator dbAuthenticator;

  @Named("LDAP")
  private Authenticator ldapAuthenticator;

  @Inject
  public APCustomRealm(UserManager userManager, @Named("DB") Authenticator dbAuthenticator,
    @Named("LDAP") Authenticator ldapAuthenticator) {
    super(new MemoryConstrainedCacheManager());
    this.userManager = userManager;
    this.dbAuthenticator = dbAuthenticator;
    this.ldapAuthenticator = ldapAuthenticator;
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
    boolean authenticated = false;

    // Get user info from db
    if (username.contains("@")) {
      user = userManager.getUserByEmail(username);
    } else {
      // if user is login with his user name, we must attach the cgiar.org.
      user = userManager.getUserByUsername(username);
    }

    if (user != null) {
      if (user.isCgiarUser()) {
        authenticated = ldapAuthenticator.authenticate(user.getUsername(), password);
      } else {
        authenticated = dbAuthenticator.authenticate(user.getEmail(), password);
      }

      if (!authenticated) {
        throw new IncorrectCredentialsException();
      }

      SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user.getId(), user.getPassword(), this.getName());

      return info;
    }

    return null;
  }

  /**
   * This method check the user session privileges
   * 
   * @param principals
   * @return
   */
  @Override
  protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

    SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();

    int userID = ((Long) principals.getPrimaryPrincipal()).intValue();
    /**
     * TODO missing put the crp witch the user login
     */
    authorizationInfo.addStringPermissions(userManager.getPermission(userID, -1));
    System.out.println(authorizationInfo.getStringPermissions());
    return authorizationInfo;
  }

  @Override
  public CredentialsMatcher getCredentialsMatcher() {
    return credentialsMatcher;
  }
}
