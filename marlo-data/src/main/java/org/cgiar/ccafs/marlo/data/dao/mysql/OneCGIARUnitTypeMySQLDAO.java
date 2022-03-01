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

/**************
 * @author Diego Perez - CIAT/CCAFS
 **************/

package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.OneCGIARUnitTypeDAO;
import org.cgiar.ccafs.marlo.data.model.OneCGIARUnitType;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

@Named
public class OneCGIARUnitTypeMySQLDAO extends AbstractMarloDAO<OneCGIARUnitType, Long> implements OneCGIARUnitTypeDAO {

  @Inject
  public OneCGIARUnitTypeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteOneCGIARUnitType(long oneCGIARUnitTypeId) {
    OneCGIARUnitType oneCGIARUnitType = this.getUnitTypeById(oneCGIARUnitTypeId);
    this.delete(oneCGIARUnitType);
  }

  @Override
  public boolean existOneCGIARUnitType(long oneCGIARUnitTypeID) {
    OneCGIARUnitType oneCGIARUnitType = this.getUnitTypeById(oneCGIARUnitTypeID);
    if (oneCGIARUnitType == null) {
      return false;
    }
    return true;
  }

  @Override
  public List<OneCGIARUnitType> getAll() {
    String query = "from " + OneCGIARUnitType.class.getName();
    List<OneCGIARUnitType> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public OneCGIARUnitType getUnitTypeByAcronym(String acronym) {
    String query = "select ocut from OneCGIARUnitType ocut where acronym = :acronym";
    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("acronym", acronym);

    List<OneCGIARUnitType> results = super.findAll(createQuery);

    OneCGIARUnitType oneCGIARUnit = (results != null && !results.isEmpty()) ? results.get(0) : null;

    return oneCGIARUnit;
  }

  @Override
  public OneCGIARUnitType getUnitTypeById(long id) {
    return super.find(OneCGIARUnitType.class, id);
  }

  @Override
  public OneCGIARUnitType save(OneCGIARUnitType oneCGIARUnitType) {
    if (oneCGIARUnitType.getId() == null) {
      super.saveEntity(oneCGIARUnitType);
    } else {
      oneCGIARUnitType = super.update(oneCGIARUnitType);
    }
    return oneCGIARUnitType;
  }

}
