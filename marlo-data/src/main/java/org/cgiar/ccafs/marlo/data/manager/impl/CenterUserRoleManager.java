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


import org.cgiar.ccafs.marlo.data.dao.ICenterUserRoleDAO;
import org.cgiar.ccafs.marlo.data.model.CenterUserRole;
import org.cgiar.ccafs.marlo.data.service.ICenterUserRoleManager;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CenterUserRoleManager implements ICenterUserRoleManager {


  private ICenterUserRoleDAO userRoleDAO;

  // Managers


  @Inject
  public CenterUserRoleManager(ICenterUserRoleDAO userRoleDAO) {
    this.userRoleDAO = userRoleDAO;


  }

  @Override
  public boolean deleteUserRole(long userRoleId) {

    return userRoleDAO.deleteUserRole(userRoleId);
  }

  @Override
  public boolean existUserRole(long userRoleID) {

    return userRoleDAO.existUserRole(userRoleID);
  }

  @Override
  public List<CenterUserRole> findAll() {

    return userRoleDAO.findAll();

  }

  @Override
  public CenterUserRole getUserRoleById(long userRoleID) {

    return userRoleDAO.find(userRoleID);
  }

  @Override
  public List<CenterUserRole> getUserRolesByUserId(Long userId) {
    return userRoleDAO.getUserRolesByUserId(userId);
  }

  @Override
  public long saveUserRole(CenterUserRole userRole) {

    return userRoleDAO.save(userRole);
  }


}
