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

import org.cgiar.ccafs.marlo.data.dao.ICapacityDevelopmentTypeDAO;
import org.cgiar.ccafs.marlo.data.model.CapacityDevelopmentType;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class CapacityDevelopmentTypeDAO extends AbstractMarloDAO<CapacityDevelopmentType, Long>
  implements ICapacityDevelopmentTypeDAO {


  @Inject
  public CapacityDevelopmentTypeDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteCapacityDevelopmentType(long capacityDevelopmentTypeId) {
    final CapacityDevelopmentType capacityDevelopmentType = this.find(capacityDevelopmentTypeId);
    // capacityDevelopmentType.setActive(false);
    this.save(capacityDevelopmentType);
  }

  @Override
  public boolean existCapacityDevelopmentType(long capacityDevelopmentTypeID) {
    final CapacityDevelopmentType capacityDevelopmentType = this.find(capacityDevelopmentTypeID);
    if (capacityDevelopmentType == null) {
      return false;
    }
    return true;

  }

  @Override
  public CapacityDevelopmentType find(long id) {
    return super.find(CapacityDevelopmentType.class, id);

  }

  @Override
  public List<CapacityDevelopmentType> findAll() {
    final String query = "from " + CapacityDevelopmentType.class.getName();
    final List<CapacityDevelopmentType> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CapacityDevelopmentType> getCapacityDevelopmentTypesByUserId(long userId) {
    final String query = "from " + CapacityDevelopmentType.class.getName() + " where user_id=" + userId;
    return super.findAll(query);
  }

  @Override
  public CapacityDevelopmentType save(CapacityDevelopmentType capacityDevelopmentType) {
    if (capacityDevelopmentType.getId() == null) {
      super.saveEntity(capacityDevelopmentType);
    } else {
      super.update(capacityDevelopmentType);
    }
    return capacityDevelopmentType;
  }

  @Override
  public CapacityDevelopmentType save(CapacityDevelopmentType capacityDevelopmentType, String actionName,
    List<String> relationsName) {
    if (capacityDevelopmentType.getId() == null) {
      super.saveEntity(capacityDevelopmentType, actionName, relationsName);
    } else {
      super.update(capacityDevelopmentType, actionName, relationsName);
    }
    return capacityDevelopmentType;
  }


}