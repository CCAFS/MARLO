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


import org.cgiar.ccafs.marlo.data.dao.ProgressTargetCaseGeographicRegionDAO;
import org.cgiar.ccafs.marlo.data.manager.ProgressTargetCaseGeographicRegionManager;
import org.cgiar.ccafs.marlo.data.model.ProgressTargetCaseGeographicRegion;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProgressTargetCaseGeographicRegionManagerImpl implements ProgressTargetCaseGeographicRegionManager {


  private ProgressTargetCaseGeographicRegionDAO progressTargetCaseGeographicRegionDAO;
  // Managers


  @Inject
  public ProgressTargetCaseGeographicRegionManagerImpl(
    ProgressTargetCaseGeographicRegionDAO progressTargetCaseGeographicRegionDAO) {
    this.progressTargetCaseGeographicRegionDAO = progressTargetCaseGeographicRegionDAO;


  }

  @Override
  public void deleteProgressTargetCaseGeographicRegion(long progressTargetCaseGeographicRegionId) {

    progressTargetCaseGeographicRegionDAO
      .deleteProgressTargetCaseGeographicRegion(progressTargetCaseGeographicRegionId);
  }

  @Override
  public boolean existProgressTargetCaseGeographicRegion(long progressTargetCaseGeographicRegionID) {

    return progressTargetCaseGeographicRegionDAO
      .existProgressTargetCaseGeographicRegion(progressTargetCaseGeographicRegionID);
  }

  @Override
  public List<ProgressTargetCaseGeographicRegion> findAll() {

    return progressTargetCaseGeographicRegionDAO.findAll();

  }

  @Override
  public List<ProgressTargetCaseGeographicRegion> findGeographicRegionByTargetCase(long targetCaseID) {
    return progressTargetCaseGeographicRegionDAO.findGeographicRegionByTargetCase(targetCaseID);
  }

  @Override
  public ProgressTargetCaseGeographicRegion
    getProgressTargetCaseGeographicRegionById(long progressTargetCaseGeographicRegionID) {

    return progressTargetCaseGeographicRegionDAO.find(progressTargetCaseGeographicRegionID);
  }

  @Override
  public ProgressTargetCaseGeographicRegion
    saveProgressTargetCaseGeographicRegion(ProgressTargetCaseGeographicRegion progressTargetCaseGeographicRegion) {

    return progressTargetCaseGeographicRegionDAO.save(progressTargetCaseGeographicRegion);
  }


}
