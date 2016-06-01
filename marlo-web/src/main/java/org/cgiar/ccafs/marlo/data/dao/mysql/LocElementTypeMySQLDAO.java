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

import org.cgiar.ccafs.marlo.data.dao.LocElementTypeDAO;
import org.cgiar.ccafs.marlo.data.model.LocElementType;

import java.util.List;

import com.google.inject.Inject;

public class LocElementTypeMySQLDAO implements LocElementTypeDAO {

  private StandardDAO dao;

  @Inject
  public LocElementTypeMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteLocElementType(long locElementTypeId) {
    LocElementType locElementType = this.find(locElementTypeId);

    return dao.delete(locElementType);
  }

  @Override
  public boolean existLocElementType(long locElementTypeID) {
    LocElementType locElementType = this.find(locElementTypeID);
    if (locElementType == null) {
      return false;
    }
    return true;

  }

  @Override
  public LocElementType find(long id) {
    return dao.find(LocElementType.class, id);

  }

  @Override
  public List<LocElementType> findAll() {
    String query = "from " + LocElementType.class.getName();
    List<LocElementType> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(LocElementType locElementType) {
    if (locElementType.getId() == null) {
      dao.save(locElementType);
    } else {
      dao.update(locElementType);
    }
    return locElementType.getId();
  }


}