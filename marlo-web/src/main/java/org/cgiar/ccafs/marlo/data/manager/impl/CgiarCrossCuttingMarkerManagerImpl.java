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


import org.cgiar.ccafs.marlo.data.dao.CgiarCrossCuttingMarkerDAO;
import org.cgiar.ccafs.marlo.data.manager.CgiarCrossCuttingMarkerManager;
import org.cgiar.ccafs.marlo.data.model.CgiarCrossCuttingMarker;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class CgiarCrossCuttingMarkerManagerImpl implements CgiarCrossCuttingMarkerManager {


  private CgiarCrossCuttingMarkerDAO cgiarCrossCuttingMarkerDAO;
  // Managers


  @Inject
  public CgiarCrossCuttingMarkerManagerImpl(CgiarCrossCuttingMarkerDAO cgiarCrossCuttingMarkerDAO) {
    this.cgiarCrossCuttingMarkerDAO = cgiarCrossCuttingMarkerDAO;


  }

  @Override
  public void deleteCgiarCrossCuttingMarker(long cgiarCrossCuttingMarkerId) {

    cgiarCrossCuttingMarkerDAO.deleteCgiarCrossCuttingMarker(cgiarCrossCuttingMarkerId);
  }

  @Override
  public boolean existCgiarCrossCuttingMarker(long cgiarCrossCuttingMarkerID) {

    return cgiarCrossCuttingMarkerDAO.existCgiarCrossCuttingMarker(cgiarCrossCuttingMarkerID);
  }

  @Override
  public List<CgiarCrossCuttingMarker> findAll() {

    return cgiarCrossCuttingMarkerDAO.findAll();

  }

  @Override
  public CgiarCrossCuttingMarker getCgiarCrossCuttingMarkerById(long cgiarCrossCuttingMarkerID) {

    return cgiarCrossCuttingMarkerDAO.find(cgiarCrossCuttingMarkerID);
  }

  @Override
  public CgiarCrossCuttingMarker saveCgiarCrossCuttingMarker(CgiarCrossCuttingMarker cgiarCrossCuttingMarker) {

    return cgiarCrossCuttingMarkerDAO.save(cgiarCrossCuttingMarker);
  }


}
