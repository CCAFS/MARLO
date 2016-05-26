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

import org.cgiar.ccafs.marlo.data.dao.CrpOutcomeSubIdoDAO;
import org.cgiar.ccafs.marlo.data.model.CrpOutcomeSubIdo;

import java.util.List;

import com.google.inject.Inject;

public class CrpOutcomeSubIdoMySQLDAO implements CrpOutcomeSubIdoDAO {

  private StandardDAO dao;

  @Inject
  public CrpOutcomeSubIdoMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteCrpOutcomeSubIdo(long crpOutcomeSubIdoId) {
    CrpOutcomeSubIdo crpOutcomeSubIdo = this.find(crpOutcomeSubIdoId);
    crpOutcomeSubIdo.setActive(false);
    return this.save(crpOutcomeSubIdo) > 0;
  }

  @Override
  public boolean existCrpOutcomeSubIdo(long crpOutcomeSubIdoID) {
    CrpOutcomeSubIdo crpOutcomeSubIdo = this.find(crpOutcomeSubIdoID);
    if (crpOutcomeSubIdo == null) {
      return false;
    }
    return true;

  }

  @Override
  public CrpOutcomeSubIdo find(long id) {
    return dao.find(CrpOutcomeSubIdo.class, id);

  }

  @Override
  public List<CrpOutcomeSubIdo> findAll() {
    String query = "from " + CrpOutcomeSubIdo.class.getName() + " where is_active=1";
    List<CrpOutcomeSubIdo> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(CrpOutcomeSubIdo crpOutcomeSubIdo) {
    dao.saveOrUpdate(crpOutcomeSubIdo);
    return crpOutcomeSubIdo.getId();
  }


}