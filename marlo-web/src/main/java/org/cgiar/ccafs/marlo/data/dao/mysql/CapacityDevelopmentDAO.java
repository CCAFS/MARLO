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

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class CapacityDevelopmentDAO extends AbstractMarloDAO<CapacityDevelopment, Long>
  implements ICapacityDevelopmentDAO {


  @Inject
  public CapacityDevelopmentDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteCapacityDevelopment(long capacityDevelopmentId) {
    CapacityDevelopment capacityDevelopment = this.find(capacityDevelopmentId);
    capacityDevelopment.setActive(false);
    this.save(capacityDevelopment);
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
    return super.find(CapacityDevelopment.class, id);

  }

  @Override
  public List<CapacityDevelopment> findAll() {
    String query = "from " + CapacityDevelopment.class.getName();
    List<CapacityDevelopment> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CapacityDevelopment> getCapacityDevelopmentsByUserId(long userId) {
    String query = "from " + CapacityDevelopment.class.getName() + " where user_id=" + userId;
    return super.findAll(query);
  }

  @Override
  public CapacityDevelopment save(CapacityDevelopment capacityDevelopment) {
    System.out.println(capacityDevelopment.getCategory());
    if (capacityDevelopment.getId() == null) {
      super.saveEntity(capacityDevelopment);
    } else {
      super.update(capacityDevelopment);
    }
    return capacityDevelopment;
  }

  @Override
  public CapacityDevelopment save(CapacityDevelopment capacityDevelopment, String actionName,
    List<String> relationsName) {
    if (capacityDevelopment.getId() == null) {
      super.saveEntity(capacityDevelopment, actionName, relationsName);
    } else {
      super.update(capacityDevelopment, actionName, relationsName);
    }
    return capacityDevelopment;
  }


}