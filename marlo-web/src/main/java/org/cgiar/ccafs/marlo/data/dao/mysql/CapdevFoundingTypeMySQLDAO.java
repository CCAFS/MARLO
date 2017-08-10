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

import org.cgiar.ccafs.marlo.data.dao.CapdevFoundingTypeDAO;
import org.cgiar.ccafs.marlo.data.model.CapdevFoundingType;

import java.util.List;

import com.google.inject.Inject;

public class CapdevFoundingTypeMySQLDAO implements CapdevFoundingTypeDAO {

  private final StandardDAO dao;

  @Inject
  public CapdevFoundingTypeMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteCapdevFoundingType(long capdevFoundingTypeId) {
    final CapdevFoundingType capdevFoundingType = this.find(capdevFoundingTypeId);
    return this.save(capdevFoundingType) > 0;
  }

  @Override
  public boolean existCapdevFoundingType(long capdevFoundingTypeID) {
    final CapdevFoundingType capdevFoundingType = this.find(capdevFoundingTypeID);
    if (capdevFoundingType == null) {
      return false;
    }
    return true;

  }

  @Override
  public CapdevFoundingType find(long id) {
    return dao.find(CapdevFoundingType.class, id);

  }

  @Override
  public List<CapdevFoundingType> findAll() {
    final String query = "from " + CapdevFoundingType.class.getName();
    final List<CapdevFoundingType> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(CapdevFoundingType capdevFoundingType) {
    if (capdevFoundingType.getId() == null) {
      dao.save(capdevFoundingType);
    } else {
      dao.update(capdevFoundingType);
    }


    return capdevFoundingType.getId();
  }


}