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

import org.cgiar.ccafs.marlo.data.dao.LocElementTypeDAO;
import org.cgiar.ccafs.marlo.data.model.LocElementType;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class LocElementTypeMySQLDAO extends AbstractMarloDAO<LocElementType, Long> implements LocElementTypeDAO {


  @Inject
  public LocElementTypeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteLocElementType(long locElementTypeId) {
    LocElementType locElementType = this.find(locElementTypeId);
    locElementType.setActive(false);
    locElementType.setHasCoordinates(false);
    this.save(locElementType);
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
    return super.find(LocElementType.class, id);

  }

  @Override
  public List<LocElementType> findAll() {
    String query = "from " + LocElementType.class.getName() + " where is_active=1";
    List<LocElementType> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public LocElementType save(LocElementType locElementType) {
    if (locElementType.getId() == null) {
      super.saveEntity(locElementType);
    } else {
      locElementType = super.update(locElementType);
    }
    return locElementType;
  }


}