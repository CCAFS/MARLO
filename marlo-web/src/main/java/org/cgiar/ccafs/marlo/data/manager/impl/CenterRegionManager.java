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


import org.cgiar.ccafs.marlo.data.dao.ICenterRegionDAO;
import org.cgiar.ccafs.marlo.data.manager.ICenterRegionManager;
import org.cgiar.ccafs.marlo.data.model.CenterRegion;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CenterRegionManager implements ICenterRegionManager {


  private ICenterRegionDAO researchRegionDAO;

  // Managers


  @Inject
  public CenterRegionManager(ICenterRegionDAO researchRegionDAO) {
    this.researchRegionDAO = researchRegionDAO;


  }

  @Override
  public void deleteResearchRegion(long researchRegionId) {

    researchRegionDAO.deleteResearchRegion(researchRegionId);
  }

  @Override
  public boolean existResearchRegion(long researchRegionID) {

    return researchRegionDAO.existResearchRegion(researchRegionID);
  }

  @Override
  public List<CenterRegion> findAll() {

    return researchRegionDAO.findAll();

  }

  @Override
  public CenterRegion getResearchRegionById(long researchRegionID) {

    return researchRegionDAO.find(researchRegionID);
  }

  @Override
  public List<CenterRegion> getResearchRegionsByUserId(Long userId) {
    return researchRegionDAO.getResearchRegionsByUserId(userId);
  }

  @Override
  public CenterRegion saveResearchRegion(CenterRegion researchRegion) {

    return researchRegionDAO.save(researchRegion);
  }


}
