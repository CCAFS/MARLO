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

import com.google.inject.Inject;

public class CapacityDevelopmentTypeDAO implements ICapacityDevelopmentTypeDAO {

  private final StandardDAO dao;

  @Inject
  public CapacityDevelopmentTypeDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteCapacityDevelopmentType(long capacityDevelopmentTypeId) {
    final CapacityDevelopmentType capacityDevelopmentType = this.find(capacityDevelopmentTypeId);
    // capacityDevelopmentType.setActive(false);
    return this.save(capacityDevelopmentType) > 0;
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
    return dao.find(CapacityDevelopmentType.class, id);

  }

  @Override
  public List<CapacityDevelopmentType> findAll() {
    final String query = "from " + CapacityDevelopmentType.class.getName();
    final List<CapacityDevelopmentType> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CapacityDevelopmentType> getCapacityDevelopmentTypesByUserId(long userId) {
    final String query = "from " + CapacityDevelopmentType.class.getName() + " where user_id=" + userId;
    return dao.findAll(query);
  }

  @Override
  public long save(CapacityDevelopmentType capacityDevelopmentType) {
    if (capacityDevelopmentType.getId() == null) {
      dao.save(capacityDevelopmentType);
    } else {
      dao.update(capacityDevelopmentType);
    }
    return capacityDevelopmentType.getId();
  }

  @Override
  public long save(CapacityDevelopmentType capacityDevelopmentType, String actionName, List<String> relationsName) {
    if (capacityDevelopmentType.getId() == null) {
      dao.save(capacityDevelopmentType, actionName, relationsName);
    } else {
      dao.update(capacityDevelopmentType, actionName, relationsName);
    }
    return capacityDevelopmentType.getId();
  }


}