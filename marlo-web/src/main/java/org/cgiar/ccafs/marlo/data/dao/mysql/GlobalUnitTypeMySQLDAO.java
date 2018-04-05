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

import org.cgiar.ccafs.marlo.data.dao.GlobalUnitTypeDAO;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitType;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class GlobalUnitTypeMySQLDAO extends AbstractMarloDAO<GlobalUnitType, Long> implements GlobalUnitTypeDAO {

  @Inject
  public GlobalUnitTypeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteGlobalUnitType(long globalUnitTypeId) {
    GlobalUnitType globalUnitType = this.find(globalUnitTypeId);
    globalUnitType.setActive(false);
    this.save(globalUnitType);
  }

  @Override
  public boolean existGlobalUnitType(long globalUnitTypeID) {
    GlobalUnitType globalUnitType = this.find(globalUnitTypeID);
    if (globalUnitType == null) {
      return false;
    }
    return true;

  }

  @Override
  public GlobalUnitType find(long id) {
    return super.find(GlobalUnitType.class, id);

  }

  @Override
  public List<GlobalUnitType> findAll() {
    String query = "from " + GlobalUnitType.class.getName() + " where is_active=1";
    List<GlobalUnitType> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public GlobalUnitType save(GlobalUnitType globalUnitType) {
    if (globalUnitType.getId() == null) {
      super.saveEntity(globalUnitType);
    } else {
      super.update(globalUnitType);
    }


    return globalUnitType;
  }


}