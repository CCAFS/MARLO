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


import org.cgiar.ccafs.marlo.data.dao.SoilIndicatorDAO;
import org.cgiar.ccafs.marlo.data.manager.SoilIndicatorManager;
import org.cgiar.ccafs.marlo.data.model.SoilIndicator;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class SoilIndicatorManagerImpl implements SoilIndicatorManager {


  private SoilIndicatorDAO soilIndicatorDAO;
  // Managers


  @Inject
  public SoilIndicatorManagerImpl(SoilIndicatorDAO soilIndicatorDAO) {
    this.soilIndicatorDAO = soilIndicatorDAO;


  }

  @Override
  public void deleteSoilIndicator(long soilIndicatorId) {

    soilIndicatorDAO.deleteSoilIndicator(soilIndicatorId);
  }

  @Override
  public boolean existSoilIndicator(long soilIndicatorID) {

    return soilIndicatorDAO.existSoilIndicator(soilIndicatorID);
  }

  @Override
  public List<SoilIndicator> findAll() {

    return soilIndicatorDAO.findAll();

  }

  @Override
  public SoilIndicator getSoilIndicatorById(long soilIndicatorID) {

    return soilIndicatorDAO.find(soilIndicatorID);
  }

  @Override
  public SoilIndicator saveSoilIndicator(SoilIndicator soilIndicator) {

    return soilIndicatorDAO.save(soilIndicator);
  }


}
