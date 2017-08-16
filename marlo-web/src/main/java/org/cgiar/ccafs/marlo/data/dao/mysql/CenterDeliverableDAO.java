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

import org.cgiar.ccafs.marlo.data.dao.ICenterDeliverableDAO;
import org.cgiar.ccafs.marlo.data.model.CenterDeliverable;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class CenterDeliverableDAO extends AbstractMarloDAO<CenterDeliverable, Long> implements ICenterDeliverableDAO {


  @Inject
  public CenterDeliverableDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public boolean deleteDeliverable(long deliverableId) {
    CenterDeliverable deliverable = this.find(deliverableId);
    deliverable.setActive(false);
    return this.save(deliverable) > 0;
  }

  @Override
  public boolean existDeliverable(long deliverableID) {
    CenterDeliverable deliverable = this.find(deliverableID);
    if (deliverable == null) {
      return false;
    }
    return true;

  }

  @Override
  public CenterDeliverable find(long id) {
    return super.find(CenterDeliverable.class, id);

  }

  @Override
  public List<CenterDeliverable> findAll() {
    String query = "from " + CenterDeliverable.class.getName();
    List<CenterDeliverable> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CenterDeliverable> getDeliverablesByUserId(long userId) {
    String query = "from " + CenterDeliverable.class.getName() + " where user_id=" + userId;
    return super.findAll(query);
  }

  @Override
  public long save(CenterDeliverable deliverable) {
    if (deliverable.getId() == null) {
      super.saveEntity(deliverable);
    } else {
      super.update(deliverable);
    }
    return deliverable.getId();
  }

  @Override
  public long save(CenterDeliverable deliverable, String actionName, List<String> relationsName) {
    if (deliverable.getId() == null) {
      super.saveEntity(deliverable, actionName, relationsName);
    } else {
      super.update(deliverable, actionName, relationsName);
    }
    return deliverable.getId();
  }


}