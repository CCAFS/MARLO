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
import org.cgiar.ccafs.marlo.data.manager.AuthenticationManager;
import org.cgiar.ccafs.marlo.utils.MD5Convert;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * validate if the input user is register in the Database
 * 
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@Named("DB")
public class DBAuthenticator implements Authenticator {

  private final AuthenticationManager authenticationManager;

  @Inject
  public DBAuthenticator(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }


  @Override
  public Map<String, Object> authenticate(String email, String password) {
    Map<String, Object> authentic = new HashMap<>();
    String md5Pass = MD5Convert.stringToMD5(password);
    authentic.put(APConstants.LOGIN_STATUS, authenticationManager.veirifyCredentials(email, md5Pass));
    return authentic;
  }

}
