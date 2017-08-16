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
import org.hibernate.SessionFactory;

public class DeliverableTypeMySQLDAO extends AbstractMarloDAO<DeliverableType, Long> implements DeliverableTypeDAO {


  @Inject
  public DeliverableTypeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public boolean deleteDeliverableType(long deliverableTypeId) {
    DeliverableType deliverableType = this.find(deliverableTypeId);
    return super.delete(deliverableType);
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
    return super.find(DeliverableType.class, id);

  }

  @Override
  public List<DeliverableType> findAll() {
    String query = "from " + DeliverableType.class.getName();
    List<DeliverableType> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<DeliverableType> getSubDeliverableType(Long deliverableId) {
    String query = "from " + DeliverableType.class.getName() + " where parent_id= " + deliverableId;
    List<DeliverableType> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public long save(DeliverableType deliverableType) {
    if (deliverableType.getId() == null) {
      super.saveEntity(deliverableType);
    } else {
      super.update(deliverableType);
    }


    return deliverableType.getId();
  }


}