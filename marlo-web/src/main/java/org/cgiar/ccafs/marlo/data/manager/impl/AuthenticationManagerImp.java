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

import org.cgiar.ccafs.marlo.data.dao.CredentialsDAO;
import org.cgiar.ccafs.marlo.data.manager.AuthenticationManager;

import com.google.inject.Inject;


/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class AuthenticationManagerImp implements AuthenticationManager {


  private CredentialsDAO credentialsDao;

  @Inject
  public AuthenticationManagerImp(CredentialsDAO credentialsDao) {
    super();
    this.credentialsDao = credentialsDao;
  }

  @Override
  public boolean veirifyCredentials(String email, String password) {
    return credentialsDao.verifiyCredentials(email, password);
  }

}
