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


package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.CrpAssumptionDAO;
import org.cgiar.ccafs.marlo.data.model.CrpAssumption;

import com.google.inject.Inject;

public class CrpAssumptionMySQLDAO implements CrpAssumptionDAO {

  private StandardDAO dao;

  @Inject
  public CrpAssumptionMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteCrpAssumption(long crpAssumptionId, long userID, String justification) {
    CrpAssumption crpAssumption = this.find(crpAssumptionId);
    return dao.delete(crpAssumption);
  }

  @Override
  public boolean existCrpAssumption(long crpAssumptionID) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public CrpAssumption find(long id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public long save(CrpAssumption casesStudies) {
    // TODO Auto-generated method stub
    return 0;
  }


}