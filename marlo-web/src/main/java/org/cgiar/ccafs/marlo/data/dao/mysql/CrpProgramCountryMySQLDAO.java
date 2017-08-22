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


package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.CrpProgramCountryDAO;
import org.cgiar.ccafs.marlo.data.model.CrpProgramCountry;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class CrpProgramCountryMySQLDAO extends AbstractMarloDAO<CrpProgramCountry, Long> implements CrpProgramCountryDAO {


  @Inject
  public CrpProgramCountryMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteCrpProgramCountry(long crpProgramCountryId) {
    CrpProgramCountry crpProgramCountry = this.find(crpProgramCountryId);
    crpProgramCountry.setActive(false);
    this.save(crpProgramCountry);
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
    return super.find(CrpProgramCountry.class, id);

  }

  @Override
  public List<CrpProgramCountry> findAll() {
    String query = "from " + CrpProgramCountry.class.getName() + " where is_active=1";
    List<CrpProgramCountry> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public CrpProgramCountry save(CrpProgramCountry crpProgramCountry) {
    if (crpProgramCountry.getId() == null) {
      super.saveEntity(crpProgramCountry);
    } else {
      crpProgramCountry = super.update(crpProgramCountry);
    }


    return crpProgramCountry;
  }


}