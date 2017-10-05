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


import org.cgiar.ccafs.marlo.data.dao.CenterAllTypesDAO;
import org.cgiar.ccafs.marlo.data.manager.CenterAllTypesManager;
import org.cgiar.ccafs.marlo.data.model.CenterAllTypes;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CenterAllTypesManagerImpl implements CenterAllTypesManager {


  private CenterAllTypesDAO centerAllTypesDAO;
  // Managers


  @Inject
  public CenterAllTypesManagerImpl(CenterAllTypesDAO centerAllTypesDAO) {
    this.centerAllTypesDAO = centerAllTypesDAO;


  }

  @Override
  public boolean deleteCenterAllTypes(long centerAllTypesId) {

    return centerAllTypesDAO.deleteCenterAllTypes(centerAllTypesId);
  }

  @Override
  public boolean existCenterAllTypes(long centerAllTypesID) {

    return centerAllTypesDAO.existCenterAllTypes(centerAllTypesID);
  }

  @Override
  public List<CenterAllTypes> findAll() {

    return centerAllTypesDAO.findAll();

  }

  @Override
  public CenterAllTypes getCenterAllTypesById(long centerAllTypesID) {

    return centerAllTypesDAO.find(centerAllTypesID);
  }

  @Override
  public long saveCenterAllTypes(CenterAllTypes centerAllTypes) {

    return centerAllTypesDAO.save(centerAllTypes);
  }


}
