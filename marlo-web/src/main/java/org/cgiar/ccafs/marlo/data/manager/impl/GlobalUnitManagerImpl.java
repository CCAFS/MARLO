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


import org.cgiar.ccafs.marlo.data.dao.GlobalUnitDAO;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class GlobalUnitManagerImpl implements GlobalUnitManager {


  private GlobalUnitDAO globalUnitDAO;
  // Managers


  @Inject
  public GlobalUnitManagerImpl(GlobalUnitDAO globalUnitDAO) {
    this.globalUnitDAO = globalUnitDAO;


  }

  @Override
  public void deleteGlobalUnit(long globalUnitId) {

    globalUnitDAO.deleteGlobalUnit(globalUnitId);
  }

  @Override
  public boolean existGlobalUnit(long globalUnitID) {

    return globalUnitDAO.existGlobalUnit(globalUnitID);
  }

  @Override
  public List<GlobalUnit> findAll() {

    return globalUnitDAO.findAll();

  }

  @Override
  public GlobalUnit getGlobalUnitById(long globalUnitID) {

    return globalUnitDAO.find(globalUnitID);
  }

  @Override
  public GlobalUnit saveGlobalUnit(GlobalUnit globalUnit) {

    return globalUnitDAO.save(globalUnit);
  }


}
