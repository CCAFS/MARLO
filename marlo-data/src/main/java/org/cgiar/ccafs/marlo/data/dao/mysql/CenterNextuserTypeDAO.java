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

import org.cgiar.ccafs.marlo.data.dao.ICenterNextuserTypeDAO;
import org.cgiar.ccafs.marlo.data.model.CenterNextuserType;

import java.util.List;

import com.google.inject.Inject;

public class CenterNextuserTypeDAO implements ICenterNextuserTypeDAO {

  private StandardDAO dao;

  @Inject
  public CenterNextuserTypeDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteNextuserType(long nextuserTypeId) {
    CenterNextuserType nextuserType = this.find(nextuserTypeId);
    nextuserType.setActive(false);
    return this.save(nextuserType) > 0;
  }

  @Override
  public boolean existNextuserType(long nextuserTypeID) {
    CenterNextuserType nextuserType = this.find(nextuserTypeID);
    if (nextuserType == null) {
      return false;
    }
    return true;

  }

  @Override
  public CenterNextuserType find(long id) {
    return dao.find(CenterNextuserType.class, id);

  }

  @Override
  public List<CenterNextuserType> findAll() {
    String query = "from " + CenterNextuserType.class.getName();
    List<CenterNextuserType> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CenterNextuserType> getNextuserTypesByUserId(long userId) {
    String query = "from " + CenterNextuserType.class.getName() + " where user_id=" + userId;
    return dao.findAll(query);
  }

  @Override
  public long save(CenterNextuserType nextuserType) {
    if (nextuserType.getId() == null) {
      dao.save(nextuserType);
    } else {
      dao.update(nextuserType);
    }
    return nextuserType.getId();
  }


}