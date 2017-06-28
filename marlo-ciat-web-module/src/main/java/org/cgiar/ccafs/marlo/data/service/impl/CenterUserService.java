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
package org.cgiar.ccafs.marlo.data.service.impl;


import org.cgiar.ccafs.marlo.data.dao.ICenterUserDAO;
import org.cgiar.ccafs.marlo.data.model.CenterUser;
import org.cgiar.ccafs.marlo.data.service.ICenterUserService;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CenterUserService implements ICenterUserService {


  private ICenterUserDAO crpUserDAO;

  // Managers


  @Inject
  public CenterUserService(ICenterUserDAO crpUserDAO) {
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
  public List<CenterUser> findAll() {

    return crpUserDAO.findAll();

  }

  @Override
  public CenterUser getCrpUserById(long crpUserID) {

    return crpUserDAO.find(crpUserID);
  }

  @Override
  public long saveCrpUser(CenterUser crpUser) {

    return crpUserDAO.save(crpUser);
  }


}
