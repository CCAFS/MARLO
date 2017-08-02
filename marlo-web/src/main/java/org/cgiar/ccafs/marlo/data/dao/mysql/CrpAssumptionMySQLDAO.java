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

import org.cgiar.ccafs.marlo.data.dao.CrpAssumptionDAO;
import org.cgiar.ccafs.marlo.data.model.CrpAssumption;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class CrpAssumptionMySQLDAO extends AbstractMarloDAO implements CrpAssumptionDAO {


  @Inject
  public CrpAssumptionMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public boolean deleteCrpAssumption(long crpAssumptionId) {
    CrpAssumption crpAssumption = this.find(crpAssumptionId);
    crpAssumption.setActive(false);
    return this.save(crpAssumption) > 0;
  }

  @Override
  public boolean existCrpAssumption(long crpAssumptionID) {
    CrpAssumption crpAssumption = this.find(crpAssumptionID);
    if (crpAssumption == null) {
      return false;
    }
    return true;

  }

  @Override
  public CrpAssumption find(long id) {
    return super.find(CrpAssumption.class, id);

  }

  @Override
  public List<CrpAssumption> findAll() {
    String query = "from " + CrpAssumption.class.getName() + " where is_active=1";
    List<CrpAssumption> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(CrpAssumption crpAssumption) {
    if (crpAssumption.getId() == null) {
      super.save(crpAssumption);
    } else {
      super.update(crpAssumption);
    }


    return crpAssumption.getId();
  }


}