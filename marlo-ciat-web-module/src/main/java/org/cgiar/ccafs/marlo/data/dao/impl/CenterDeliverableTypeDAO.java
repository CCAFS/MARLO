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


package org.cgiar.ccafs.marlo.data.dao.impl;

import org.cgiar.ccafs.marlo.data.dao.ICenterDeliverableTypeDAO;
import org.cgiar.ccafs.marlo.data.model.CenterDeliverableType;

import java.util.List;

import com.google.inject.Inject;

public class CenterDeliverableTypeDAO implements ICenterDeliverableTypeDAO {

  private StandardDAO dao;

  @Inject
  public CenterDeliverableTypeDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteDeliverableType(long deliverableTypeId) {
    CenterDeliverableType deliverableType = this.find(deliverableTypeId);
    deliverableType.setActive(false);
    return this.save(deliverableType) > 0;
  }

  @Override
  public boolean existDeliverableType(long deliverableTypeID) {
    CenterDeliverableType deliverableType = this.find(deliverableTypeID);
    if (deliverableType == null) {
      return false;
    }
    return true;

  }

  @Override
  public CenterDeliverableType find(long id) {
    return dao.find(CenterDeliverableType.class, id);

  }

  @Override
  public List<CenterDeliverableType> findAll() {
    String query = "from " + CenterDeliverableType.class.getName();
    List<CenterDeliverableType> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CenterDeliverableType> getDeliverableTypesByUserId(long userId) {
    String query = "from " + CenterDeliverableType.class.getName() + " where user_id=" + userId;
    return dao.findAll(query);
  }

  @Override
  public List<CenterDeliverableType> getSubDeliverableType(Long deliverableId) {
    String query = "from " + CenterDeliverableType.class.getName() + " where parent_id= " + deliverableId;
    List<CenterDeliverableType> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public long save(CenterDeliverableType deliverableType) {
    if (deliverableType.getId() == null) {
      dao.save(deliverableType);
    } else {
      dao.update(deliverableType);
    }
    return deliverableType.getId();
  }


}