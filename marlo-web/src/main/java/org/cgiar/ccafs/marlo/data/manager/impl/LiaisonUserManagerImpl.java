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


import org.cgiar.ccafs.marlo.data.dao.LiaisonUserDAO;
import org.cgiar.ccafs.marlo.data.manager.LiaisonUserManager;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class LiaisonUserManagerImpl implements LiaisonUserManager {


  private LiaisonUserDAO liaisonUserDAO;
  // Managers


  @Inject
  public LiaisonUserManagerImpl(LiaisonUserDAO liaisonUserDAO) {
    this.liaisonUserDAO = liaisonUserDAO;


  }

  @Override
  public boolean deleteLiaisonUser(long liaisonUserId) {

    return liaisonUserDAO.deleteLiaisonUser(liaisonUserId);
  }

  @Override
  public boolean existLiaisonUser(long liaisonUserID) {

    return liaisonUserDAO.existLiaisonUser(liaisonUserID);
  }

  @Override
  public List<LiaisonUser> findAll() {

    return liaisonUserDAO.findAll();

  }

  @Override
  public LiaisonUser getLiaisonUserById(long liaisonUserID) {

    return liaisonUserDAO.find(liaisonUserID);
  }

  @Override
  public long saveLiaisonUser(LiaisonUser liaisonUser) {

    return liaisonUserDAO.save(liaisonUser);
  }


}
