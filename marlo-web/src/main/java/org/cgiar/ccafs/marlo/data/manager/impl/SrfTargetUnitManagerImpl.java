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


import org.cgiar.ccafs.marlo.data.dao.SrfTargetUnitDAO;
import org.cgiar.ccafs.marlo.data.manager.SrfTargetUnitManager;
import org.cgiar.ccafs.marlo.data.model.SrfTargetUnit;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class SrfTargetUnitManagerImpl implements SrfTargetUnitManager {


  private SrfTargetUnitDAO srfTargetUnitDAO;
  // Managers


  @Inject
  public SrfTargetUnitManagerImpl(SrfTargetUnitDAO srfTargetUnitDAO) {
    this.srfTargetUnitDAO = srfTargetUnitDAO;


  }

  @Override
  public void deleteSrfTargetUnit(long srfTargetUnitId) {

    srfTargetUnitDAO.deleteSrfTargetUnit(srfTargetUnitId);
  }

  @Override
  public boolean existSrfTargetUnit(long srfTargetUnitID) {

    return srfTargetUnitDAO.existSrfTargetUnit(srfTargetUnitID);
  }

  @Override
  public List<SrfTargetUnit> findAll() {

    return srfTargetUnitDAO.findAll();

  }

  @Override
  public SrfTargetUnit getSrfTargetUnitById(long srfTargetUnitID) {

    return srfTargetUnitDAO.find(srfTargetUnitID);
  }

  @Override
  public SrfTargetUnit saveSrfTargetUnit(SrfTargetUnit srfTargetUnit) {

    return srfTargetUnitDAO.save(srfTargetUnit);
  }


}
