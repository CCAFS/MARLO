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


import org.cgiar.ccafs.marlo.data.dao.CenterFundingSyncTypeDAO;
import org.cgiar.ccafs.marlo.data.manager.CenterFundingSyncTypeManager;
import org.cgiar.ccafs.marlo.data.model.CenterFundingSyncType;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CenterFundingSyncTypeManagerImpl implements CenterFundingSyncTypeManager {


  private CenterFundingSyncTypeDAO centerFundingSyncTypeDAO;
  // Managers


  @Inject
  public CenterFundingSyncTypeManagerImpl(CenterFundingSyncTypeDAO centerFundingSyncTypeDAO) {
    this.centerFundingSyncTypeDAO = centerFundingSyncTypeDAO;


  }

  @Override
  public boolean deleteCenterFundingSyncType(long centerFundingSyncTypeId) {

    return centerFundingSyncTypeDAO.deleteCenterFundingSyncType(centerFundingSyncTypeId);
  }

  @Override
  public boolean existCenterFundingSyncType(long centerFundingSyncTypeID) {

    return centerFundingSyncTypeDAO.existCenterFundingSyncType(centerFundingSyncTypeID);
  }

  @Override
  public List<CenterFundingSyncType> findAll() {

    return centerFundingSyncTypeDAO.findAll();

  }

  @Override
  public CenterFundingSyncType getCenterFundingSyncTypeById(long centerFundingSyncTypeID) {

    return centerFundingSyncTypeDAO.find(centerFundingSyncTypeID);
  }

  @Override
  public long saveCenterFundingSyncType(CenterFundingSyncType centerFundingSyncType) {

    return centerFundingSyncTypeDAO.save(centerFundingSyncType);
  }


}
