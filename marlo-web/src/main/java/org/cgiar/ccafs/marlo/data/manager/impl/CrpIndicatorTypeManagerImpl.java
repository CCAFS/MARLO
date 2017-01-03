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


import org.cgiar.ccafs.marlo.data.dao.CrpIndicatorTypeDAO;
import org.cgiar.ccafs.marlo.data.manager.CrpIndicatorTypeManager;
import org.cgiar.ccafs.marlo.data.model.CrpIndicatorType;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CrpIndicatorTypeManagerImpl implements CrpIndicatorTypeManager {


  private CrpIndicatorTypeDAO crpIndicatorTypeDAO;
  // Managers


  @Inject
  public CrpIndicatorTypeManagerImpl(CrpIndicatorTypeDAO crpIndicatorTypeDAO) {
    this.crpIndicatorTypeDAO = crpIndicatorTypeDAO;


  }

  @Override
  public boolean deleteCrpIndicatorType(long crpIndicatorTypeId) {

    return crpIndicatorTypeDAO.deleteCrpIndicatorType(crpIndicatorTypeId);
  }

  @Override
  public boolean existCrpIndicatorType(long crpIndicatorTypeID) {

    return crpIndicatorTypeDAO.existCrpIndicatorType(crpIndicatorTypeID);
  }

  @Override
  public List<CrpIndicatorType> findAll() {

    return crpIndicatorTypeDAO.findAll();

  }

  @Override
  public CrpIndicatorType getCrpIndicatorTypeById(long crpIndicatorTypeID) {

    return crpIndicatorTypeDAO.find(crpIndicatorTypeID);
  }

  @Override
  public long saveCrpIndicatorType(CrpIndicatorType crpIndicatorType) {

    return crpIndicatorTypeDAO.save(crpIndicatorType);
  }


}
