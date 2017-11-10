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

package org.cgiar.ccafs.marlo.security.authentication;

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.utils.APConfig;

import org.cgiar.ciat.auth.ADConexion;
import org.cgiar.ciat.auth.LDAPService;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * validate if the input user belongs in CGIAR active directory
 * 
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@Named("LDAP")
public class LDAPAuthenticator implements Authenticator {

  public static Logger LOG = LoggerFactory.getLogger(LDAPAuthenticator.class);


  private final APConfig config;
  private Map<String, Object> looged;

  @Inject
  public LDAPAuthenticator(APConfig config) {

    this.config = config;
  }

  @Override
  public Map<String, Object> authenticate(String email, String password) {
    looged = new HashMap<>();
    looged.put(APConstants.LOGIN_STATUS, false);


    try {
      ADConexion con = null;
      LDAPService service = new LDAPService();
      if (config.isProduction()) {
        service.setInternalConnection(false);
      } else {
        service.setInternalConnection(true);
      }

      con = service.authenticateUser(email, password);

      if (con != null) {
        if (con.getLogin() != null) {
          looged.replace(APConstants.LOGIN_STATUS, true);
          looged.put(APConstants.LOGIN_MESSAGE, con.getAuthenticationMessage());
        } else {
          looged.put(APConstants.LOGIN_MESSAGE, con.getAuthenticationMessage());
          LOG.error("Authentication error  {}", con.getAuthenticationMessage());
        }
        con.closeContext();
      } else {
        looged.put(APConstants.LOGIN_MESSAGE, APConstants.ERROR_LOGON_FAILURE);

      }
    } catch (Exception e) {
      if (!looged.containsKey(APConstants.LOGIN_MESSAGE)) {
        looged.put(APConstants.LOGIN_MESSAGE, APConstants.ERROR_LDAP_CONNECTION);
      }
      LOG.error("Exception raised trying to log in the user '{}' against the active directory. ", email,
        e.getMessage());
    }
    return looged;
  }

}
