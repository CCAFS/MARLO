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

import org.cgiar.ccafs.marlo.data.dao.CenterAllTypesDAO;
import org.cgiar.ccafs.marlo.data.model.CenterAllTypes;

import java.util.List;

import com.google.inject.Inject;

public class CenterAllTypesMySQLDAO implements CenterAllTypesDAO {

  private StandardDAO dao;

  @Inject
  public CenterAllTypesMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteCenterAllTypes(long centerAllTypesId) {
    CenterAllTypes centerAllTypes = this.find(centerAllTypesId);
    centerAllTypes.setActive(false);
    return this.save(centerAllTypes) > 0;
  }

  @Override
  public boolean existCenterAllTypes(long centerAllTypesID) {
    CenterAllTypes centerAllTypes = this.find(centerAllTypesID);
    if (centerAllTypes == null) {
      return false;
    }
    return true;

  }

  @Override
  public CenterAllTypes find(long id) {
    return dao.find(CenterAllTypes.class, id);

  }

  @Override
  public List<CenterAllTypes> findAll() {
    String query = "from " + CenterAllTypes.class.getName() + " where is_active=1";
    List<CenterAllTypes> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(CenterAllTypes centerAllTypes) {
    if (centerAllTypes.getId() == null) {
      dao.save(centerAllTypes);
    } else {
      dao.update(centerAllTypes);
    }


    return centerAllTypes.getId();
  }


}