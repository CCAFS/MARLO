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


import org.cgiar.ccafs.marlo.data.dao.CrpTargetUnitDAO;
import org.cgiar.ccafs.marlo.data.manager.CrpTargetUnitManager;
import org.cgiar.ccafs.marlo.data.model.CrpTargetUnit;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CrpTargetUnitManagerImpl implements CrpTargetUnitManager {


  private CrpTargetUnitDAO crpTargetUnitDAO;
  // Managers


  @Inject
  public CrpTargetUnitManagerImpl(CrpTargetUnitDAO crpTargetUnitDAO) {
    this.crpTargetUnitDAO = crpTargetUnitDAO;


  }

  @Override
  public void deleteCrpTargetUnit(long crpTargetUnitId) {

    crpTargetUnitDAO.deleteCrpTargetUnit(crpTargetUnitId);
  }

  @Override
  public boolean existCrpTargetUnit(long crpTargetUnitID) {

    return crpTargetUnitDAO.existCrpTargetUnit(crpTargetUnitID);
  }

  @Override
  public List<CrpTargetUnit> findAll() {

    return crpTargetUnitDAO.findAll();

  }

  @Override
  public CrpTargetUnit getByTargetUnitIdAndCrpId(long crpId, long targetUnitId) {
    return crpTargetUnitDAO.getByTargetUnitIdAndCrpId(crpId, targetUnitId);
  }

  @Override
  public CrpTargetUnit getCrpTargetUnitById(long crpTargetUnitID) {

    return crpTargetUnitDAO.find(crpTargetUnitID);
  }

  @Override
  public CrpTargetUnit saveCrpTargetUnit(CrpTargetUnit crpTargetUnit) {

    return crpTargetUnitDAO.save(crpTargetUnit);
  }


}
