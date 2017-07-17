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

import org.cgiar.ccafs.marlo.data.dao.ICapacityDevelopmentDAO;
import org.cgiar.ccafs.marlo.data.model.CapacityDevelopment;

import java.util.List;

import com.google.inject.Inject;

public class CapacityDevelopmentDAO implements ICapacityDevelopmentDAO {

  private StandardDAO dao;

  @Inject
  public CapacityDevelopmentDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteCapacityDevelopment(long capacityDevelopmentId) {
    CapacityDevelopment capacityDevelopment = this.find(capacityDevelopmentId);
    capacityDevelopment.setActive(false);
    return this.save(capacityDevelopment) > 0;
  }

  @Override
  public boolean existCapacityDevelopment(long capacityDevelopmentID) {
    CapacityDevelopment capacityDevelopment = this.find(capacityDevelopmentID);
    if (capacityDevelopment == null) {
      return false;
    }
    return true;

  }

  @Override
  public CapacityDevelopment find(long id) {
    return dao.find(CapacityDevelopment.class, id);

  }

  @Override
  public List<CapacityDevelopment> findAll() {
    String query = "from " + CapacityDevelopment.class.getName();
    List<CapacityDevelopment> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CapacityDevelopment> getCapacityDevelopmentsByUserId(long userId) {
    String query = "from " + CapacityDevelopment.class.getName() + " where user_id=" + userId;
    return dao.findAll(query);
  }

  @Override
  public long save(CapacityDevelopment capacityDevelopment) {
    if (capacityDevelopment.getId() == null) {
      dao.save(capacityDevelopment);
    } else {
      dao.update(capacityDevelopment);
    }
    return capacityDevelopment.getId();
  }

  @Override
  public long save(CapacityDevelopment capacityDevelopment, String actionName, List<String> relationsName) {
    if (capacityDevelopment.getId() == null) {
      dao.save(capacityDevelopment, actionName, relationsName);
    } else {
      dao.update(capacityDevelopment, actionName, relationsName);
    }
    return capacityDevelopment.getId();
  }


}