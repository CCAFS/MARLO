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

import org.cgiar.ccafs.marlo.data.dao.DeliverableTypeDAO;
import org.cgiar.ccafs.marlo.data.model.DeliverableType;

import java.util.List;

import com.google.inject.Inject;

public class DeliverableTypeMySQLDAO implements DeliverableTypeDAO {

  private StandardDAO dao;

  @Inject
  public DeliverableTypeMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteDeliverableType(long deliverableTypeId) {
    DeliverableType deliverableType = this.find(deliverableTypeId);
    return dao.delete(deliverableType);
  }

  @Override
  public boolean existDeliverableType(long deliverableTypeID) {
    DeliverableType deliverableType = this.find(deliverableTypeID);
    if (deliverableType == null) {
      return false;
    }
    return true;

  }

  @Override
  public DeliverableType find(long id) {
    return dao.find(DeliverableType.class, id);

  }

  @Override
  public List<DeliverableType> findAll() {
    String query = "from " + DeliverableType.class.getName() + " where is_active=1";
    List<DeliverableType> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(DeliverableType deliverableType) {
    if (deliverableType.getId() == null) {
      dao.save(deliverableType);
    } else {
      dao.update(deliverableType);
    }


    return deliverableType.getId();
  }


}