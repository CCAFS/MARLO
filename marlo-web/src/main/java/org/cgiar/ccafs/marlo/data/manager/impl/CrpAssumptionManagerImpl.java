/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.data.manager.CrpAssumptionManager;
import org.cgiar.ccafs.marlo.data.model.CrpAssumption;
import org.cgiar.ccafs.marlo.data.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Christian Garcia
 */
public class CrpAssumptionManagerImpl implements CrpAssumptionManager {

  // LOG
  private static Logger LOG = LoggerFactory.getLogger(CrpAssumptionManagerImpl.class);

  @Override
  public boolean deleteCrpAssumption(long crpAssumptionId, User user, String justification) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean existCrpAssumption(long crpAssumptionID) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public CrpAssumption getCrpAssumptionById(long crpAssumptionID) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public long saveCrpAssumption(CrpAssumption crpAssumption, User user, String justification) {
    // TODO Auto-generated method stub
    return 0;
  }

  // DAO's


}
