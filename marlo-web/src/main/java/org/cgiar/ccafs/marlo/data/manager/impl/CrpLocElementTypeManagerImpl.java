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


import org.cgiar.ccafs.marlo.data.dao.CrpLocElementTypeDAO;
import org.cgiar.ccafs.marlo.data.manager.CrpLocElementTypeManager;
import org.cgiar.ccafs.marlo.data.model.CrpLocElementType;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class CrpLocElementTypeManagerImpl implements CrpLocElementTypeManager {


  private CrpLocElementTypeDAO crpLocElementTypeDAO;
  // Managers


  @Inject
  public CrpLocElementTypeManagerImpl(CrpLocElementTypeDAO crpLocElementTypeDAO) {
    this.crpLocElementTypeDAO = crpLocElementTypeDAO;


  }

  @Override
  public void deleteCrpLocElementType(long crpLocElementTypeId) {

    crpLocElementTypeDAO.deleteCrpLocElementType(crpLocElementTypeId);
  }

  @Override
  public boolean existCrpLocElementType(long crpLocElementTypeID) {

    return crpLocElementTypeDAO.existCrpLocElementType(crpLocElementTypeID);
  }

  @Override
  public List<CrpLocElementType> findAll() {

    return crpLocElementTypeDAO.findAll();

  }

  @Override
  public CrpLocElementType getByLocElementTypeAndCrpId(long crpId, long locElementTypeID) {
    return crpLocElementTypeDAO.getByLocElementTypeAndCrpId(crpId, locElementTypeID);
  }

  @Override
  public CrpLocElementType getCrpLocElementTypeById(long crpLocElementTypeID) {

    return crpLocElementTypeDAO.find(crpLocElementTypeID);
  }

  @Override
  public CrpLocElementType saveCrpLocElementType(CrpLocElementType crpLocElementType) {

    return crpLocElementTypeDAO.save(crpLocElementType);
  }


}
