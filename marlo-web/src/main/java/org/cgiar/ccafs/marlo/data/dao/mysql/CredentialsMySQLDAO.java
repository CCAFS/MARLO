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

package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.CredentialsDAO;
import org.cgiar.ccafs.marlo.data.model.User;

import java.util.List;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class CredentialsMySQLDAO implements CredentialsDAO {

  public static Logger LOG = LoggerFactory.getLogger(CredentialsMySQLDAO.class);

  private StandardDAO dao;

  @Inject
  public CredentialsMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean verifiyCredentials(String email, String password) {
    String query =
      "from " + User.class + " where email= '" + email + "' and password= '" + password + "' and is_active = TRUE";
    List<User> users = dao.findAll(query);
    if (users.size() > 0) {
      return true;
    }
    LOG.error("verifiyCredentials() > There was an error verifiying the credentials", email);
    return false;
  }

}
