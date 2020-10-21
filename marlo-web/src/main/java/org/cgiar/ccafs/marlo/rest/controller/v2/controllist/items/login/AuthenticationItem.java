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

/**************
 * @author Diego Perez - CIAT/CCAFS
 **************/

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.login;

import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.UserAutenticationDTO;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.MD5Convert;

import org.cgiar.ciat.auth.ADConexion;
import org.cgiar.ciat.auth.LDAPService;

import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Named
public class AuthenticationItem<T> {

  private static final Logger LOG = LoggerFactory.getLogger(AuthenticationItem.class);

  private UserManager userManager;
  private final APConfig config;

  @Inject
  public AuthenticationItem(UserManager userManager, APConfig config) {
    super();
    this.userManager = userManager;
    this.config = config;
  }


  public ResponseEntity<UserAutenticationDTO> userAuthentication(String email, String password) {
    UserAutenticationDTO userAutenticationDTO = null;
    User userlogged = userManager.getUserByEmail(email);
    if (userlogged != null) {
      String userEmail = userlogged.getEmail().trim().toLowerCase();
      String md5Pass = MD5Convert.stringToMD5(password);

      userAutenticationDTO = new UserAutenticationDTO();
      userAutenticationDTO.setEmail(userEmail);
      userAutenticationDTO.setFirst_name(userlogged.getFirstName());
      userAutenticationDTO.setLast_name(userlogged.getLastName());
      userAutenticationDTO.setId(userlogged.getId());
      if (userlogged.getPassword().equals(md5Pass)) {
        userAutenticationDTO.setAuthenticated(true);
      } else {
        userAutenticationDTO.setAuthenticated(false);
        // try LDPA authentication
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
              userAutenticationDTO.setAuthenticated(true);
              // looged.replace(APConstants.LOGIN_STATUS, true);
              // looged.put(APConstants.LOGIN_MESSAGE, con.getAuthenticationMessage());
            } else {
              // looged.put(APConstants.LOGIN_MESSAGE, con.getAuthenticationMessage());
              LOG.error("Authentication error  {}", con.getAuthenticationMessage());
            }
            con.closeContext();
          } else {
            // looged.put(APConstants.LOGIN_MESSAGE, APConstants.ERROR_LOGON_FAILURE);

          }
        } catch (Exception e) {
          // if (!looged.containsKey(APConstants.LOGIN_MESSAGE)) {
          // looged.put(APConstants.LOGIN_MESSAGE, APConstants.ERROR_LDAP_CONNECTION);
          // }
          LOG.error("Exception raised trying to log in the user '{}' against the active directory. ", email,
            e.getMessage());
        }
      }
    }
    return Optional.ofNullable(userAutenticationDTO).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }
}
