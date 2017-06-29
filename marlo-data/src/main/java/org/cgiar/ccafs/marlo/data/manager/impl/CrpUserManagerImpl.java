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


import org.cgiar.ccafs.marlo.data.dao.CrpUserDAO;
import org.cgiar.ccafs.marlo.data.manager.CrpUserManager;
import org.cgiar.ccafs.marlo.data.model.CrpUser;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CrpUserManagerImpl implements CrpUserManager {


  private CrpUserDAO crpUserDAO;
  // Managers


  @Inject
  public CrpUserManagerImpl(CrpUserDAO crpUserDAO) {
    this.crpUserDAO = crpUserDAO;


  }

  @Override
  public boolean deleteCrpUser(long crpUserId) {

    return crpUserDAO.deleteCrpUser(crpUserId);
  }

  @Override
  public boolean existCrpUser(long crpUserID) {

    return crpUserDAO.existCrpUser(crpUserID);
  }

  @Override
  public boolean existCrpUser(long userId, long crpId) {
    return crpUserDAO.existCrpUser(userId, crpId);
  }

  @Override
  public List<CrpUser> findAll() {

    return crpUserDAO.findAll();

  }

  @Override
  public CrpUser getCrpUserById(long crpUserID) {

    return crpUserDAO.find(crpUserID);
  }

  @Override
  public long saveCrpUser(CrpUser crpUser) {

    return crpUserDAO.save(crpUser);
  }


}
