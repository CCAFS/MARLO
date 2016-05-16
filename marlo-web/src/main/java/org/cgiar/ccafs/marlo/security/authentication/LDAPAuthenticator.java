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

package org.cgiar.ccafs.marlo.security.authentication;

import org.cgiar.ciat.auth.ADConexion;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class LDAPAuthenticator implements Authenticator {

  public static Logger LOG = LoggerFactory.getLogger(LDAPAuthenticator.class);

  @Inject
  public LDAPAuthenticator() {
  }

  @Override
  public boolean authenticate(String email, String password) {
    boolean logued = false;

    try {
      ADConexion con = new ADConexion(email, password);
      if (con != null) {
        if (con.getLogin() != null) {
          logued = true;
        }
        con.closeContext();
      }
    } catch (Exception e) {
      LOG.error("Exception raised trying to log in the user '{}' against the active directory.", email, e.getMessage());
    }
    return logued;
  }

}
