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


import org.cgiar.ccafs.marlo.data.dao.ICenterOutputsNextUserDAO;
import org.cgiar.ccafs.marlo.data.manager.ICenterOutputsNextUserManager;
import org.cgiar.ccafs.marlo.data.model.CenterOutputsNextUser;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CenterOutputsNextUserManager implements ICenterOutputsNextUserManager {


  private ICenterOutputsNextUserDAO researchOutputsNextUserDAO;

  // Managers


  @Inject
  public CenterOutputsNextUserManager(ICenterOutputsNextUserDAO researchOutputsNextUserDAO) {
    this.researchOutputsNextUserDAO = researchOutputsNextUserDAO;


  }

  @Override
  public void deleteResearchOutputsNextUser(long researchOutputsNextUserId) {

    researchOutputsNextUserDAO.deleteResearchOutputsNextUser(researchOutputsNextUserId);
  }

  @Override
  public boolean existResearchOutputsNextUser(long researchOutputsNextUserID) {

    return researchOutputsNextUserDAO.existResearchOutputsNextUser(researchOutputsNextUserID);
  }

  @Override
  public List<CenterOutputsNextUser> findAll() {

    return researchOutputsNextUserDAO.findAll();

  }

  @Override
  public CenterOutputsNextUser getResearchOutputsNextUserById(long researchOutputsNextUserID) {

    return researchOutputsNextUserDAO.find(researchOutputsNextUserID);
  }

  @Override
  public List<CenterOutputsNextUser> getResearchOutputsNextUsersByUserId(Long userId) {
    return researchOutputsNextUserDAO.getResearchOutputsNextUsersByUserId(userId);
  }

  @Override
  public CenterOutputsNextUser saveResearchOutputsNextUser(CenterOutputsNextUser researchOutputsNextUser) {

    return researchOutputsNextUserDAO.save(researchOutputsNextUser);
  }


}
