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


import org.cgiar.ccafs.marlo.data.dao.CrpAssumptionDAO;
import org.cgiar.ccafs.marlo.data.manager.CrpAssumptionManager;
import org.cgiar.ccafs.marlo.data.model.CrpAssumption;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CrpAssumptionManagerImpl implements CrpAssumptionManager {


  private CrpAssumptionDAO crpAssumptionDAO;
  // Managers


  @Inject
  public CrpAssumptionManagerImpl(CrpAssumptionDAO crpAssumptionDAO) {
    this.crpAssumptionDAO = crpAssumptionDAO;


  }

  @Override
  public void deleteCrpAssumption(long crpAssumptionId) {

    crpAssumptionDAO.deleteCrpAssumption(crpAssumptionId);
  }

  @Override
  public boolean existCrpAssumption(long crpAssumptionID) {

    return crpAssumptionDAO.existCrpAssumption(crpAssumptionID);
  }

  @Override
  public List<CrpAssumption> findAll() {

    return crpAssumptionDAO.findAll();

  }

  @Override
  public CrpAssumption getCrpAssumptionById(long crpAssumptionID) {

    return crpAssumptionDAO.find(crpAssumptionID);
  }

  @Override
  public CrpAssumption saveCrpAssumption(CrpAssumption crpAssumption) {

    return crpAssumptionDAO.save(crpAssumption);
  }


}
