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


import org.cgiar.ccafs.marlo.data.dao.GlobalUnitTypeDAO;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitTypeManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitType;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class GlobalUnitTypeManagerImpl implements GlobalUnitTypeManager {


  private GlobalUnitTypeDAO globalUnitTypeDAO;
  // Managers


  @Inject
  public GlobalUnitTypeManagerImpl(GlobalUnitTypeDAO globalUnitTypeDAO) {
    this.globalUnitTypeDAO = globalUnitTypeDAO;


  }

  @Override
  public void deleteGlobalUnitType(long globalUnitTypeId) {

    globalUnitTypeDAO.deleteGlobalUnitType(globalUnitTypeId);
  }

  @Override
  public boolean existGlobalUnitType(long globalUnitTypeID) {

    return globalUnitTypeDAO.existGlobalUnitType(globalUnitTypeID);
  }

  @Override
  public List<GlobalUnitType> findAll() {

    return globalUnitTypeDAO.findAll();

  }

  @Override
  public GlobalUnitType getGlobalUnitTypeById(long globalUnitTypeID) {

    return globalUnitTypeDAO.find(globalUnitTypeID);
  }

  @Override
  public GlobalUnitType saveGlobalUnitType(GlobalUnitType globalUnitType) {

    return globalUnitTypeDAO.save(globalUnitType);
  }


}
