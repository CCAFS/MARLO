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


import org.cgiar.ccafs.marlo.data.dao.ICenterTargetUnitDAO;
import org.cgiar.ccafs.marlo.data.model.CenterTargetUnit;
import org.cgiar.ccafs.marlo.data.service.ICenterTargetUnitService;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CenterTargetUnitService implements ICenterTargetUnitService {


  private ICenterTargetUnitDAO targetUnitDAO;

  // Managers


  @Inject
  public CenterTargetUnitService(ICenterTargetUnitDAO targetUnitDAO) {
    this.targetUnitDAO = targetUnitDAO;


  }

  @Override
  public boolean deleteTargetUnit(long targetUnitId) {

    return targetUnitDAO.deleteTargetUnit(targetUnitId);
  }

  @Override
  public boolean existTargetUnit(long targetUnitID) {

    return targetUnitDAO.existTargetUnit(targetUnitID);
  }

  @Override
  public List<CenterTargetUnit> findAll() {

    return targetUnitDAO.findAll();

  }

  @Override
  public CenterTargetUnit getTargetUnitById(long targetUnitID) {

    return targetUnitDAO.find(targetUnitID);
  }

  @Override
  public List<CenterTargetUnit> getTargetUnitsByUserId(Long userId) {
    return targetUnitDAO.getTargetUnitsByUserId(userId);
  }

  @Override
  public long saveTargetUnit(CenterTargetUnit targetUnit) {

    return targetUnitDAO.save(targetUnit);
  }


}
