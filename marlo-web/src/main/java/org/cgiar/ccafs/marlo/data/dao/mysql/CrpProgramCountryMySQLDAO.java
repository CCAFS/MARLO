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

import org.cgiar.ccafs.marlo.data.dao.CrpProgramCountryDAO;
import org.cgiar.ccafs.marlo.data.model.CrpProgramCountry;

import java.util.List;

import com.google.inject.Inject;

public class CrpProgramCountryMySQLDAO implements CrpProgramCountryDAO {

  private StandardDAO dao;

  @Inject
  public CrpProgramCountryMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteCrpProgramCountry(long crpProgramCountryId) {
    CrpProgramCountry crpProgramCountry = this.find(crpProgramCountryId);
    crpProgramCountry.setActive(false);
    return this.save(crpProgramCountry) > 0;
  }

  @Override
  public boolean existCrpProgramCountry(long crpProgramCountryID) {
    CrpProgramCountry crpProgramCountry = this.find(crpProgramCountryID);
    if (crpProgramCountry == null) {
      return false;
    }
    return true;

  }

  @Override
  public CrpProgramCountry find(long id) {
    return dao.find(CrpProgramCountry.class, id);

  }

  @Override
  public List<CrpProgramCountry> findAll() {
    String query = "from " + CrpProgramCountry.class.getName() + " where is_active=1";
    List<CrpProgramCountry> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(CrpProgramCountry crpProgramCountry) {
    if (crpProgramCountry.getId() == null) {
      dao.save(crpProgramCountry);
    } else {
      dao.update(crpProgramCountry);
    }


    return crpProgramCountry.getId();
  }


}