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


import org.cgiar.ccafs.marlo.data.dao.AdUserDAO;
import org.cgiar.ccafs.marlo.data.manager.AdUserManager;
import org.cgiar.ccafs.marlo.data.model.AdUser;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class AdUserManagerImpl implements AdUserManager {


  private AdUserDAO adUserDAO;
  // Managers


  @Inject
  public AdUserManagerImpl(AdUserDAO adUserDAO) {
    this.adUserDAO = adUserDAO;


  }

  @Override
  public void deleteAdUser(long adUserId) {

    adUserDAO.deleteAdUser(adUserId);
  }

  @Override
  public boolean existAdUser(long adUserID) {

    return adUserDAO.existAdUser(adUserID);
  }

  @Override
  public List<AdUser> findAll() {

    return adUserDAO.findAll();

  }

  @Override
  public AdUser findByUserLogin(String login) {
    return adUserDAO.findByLogin(login);
  }

  @Override
  public AdUser getAdUserById(long adUserID) {

    return adUserDAO.find(adUserID);
  }

  @Override
  public AdUser saveAdUser(AdUser adUser) {

    return adUserDAO.save(adUser);
  }


}
