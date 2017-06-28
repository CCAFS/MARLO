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

package org.cgiar.ccafs.marlo.data.dao.impl;

import org.cgiar.ccafs.marlo.data.dao.ICredentialsDAO;
import org.cgiar.ccafs.marlo.data.model.User;

import java.util.List;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class CredentialsDAO implements ICredentialsDAO {

  public static Logger LOG = LoggerFactory.getLogger(CredentialsDAO.class);

  private StandardDAO dao;

  @Inject
  public CredentialsDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean verifiyCredentials(String email, String password) {
    String query = "from " + User.class.getName() + " where email= '" + email + "' and password= '" + password
      + "' and is_active = 1";
    List<User> users = dao.findAll(query);
    if (users.size() > 0) {
      return true;
    }
    LOG.error("verifiyCredentials() > There was an error verifiying the credentials", email);
    return false;
  }

}
