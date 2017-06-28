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

import org.cgiar.ccafs.marlo.data.dao.ICenterDeliverableDAO;
import org.cgiar.ccafs.marlo.data.model.CenterDeliverable;

import java.util.List;

import com.google.inject.Inject;

public class CenterDeliverableDAO implements ICenterDeliverableDAO {

  private StandardDAO dao;

  @Inject
  public CenterDeliverableDAO(StandardDAO dao) {
    this.dao = dao;
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
    return dao.find(CenterDeliverable.class, id);

  }

  @Override
  public List<CenterDeliverable> findAll() {
    String query = "from " + CenterDeliverable.class.getName();
    List<CenterDeliverable> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CenterDeliverable> getDeliverablesByUserId(long userId) {
    String query = "from " + CenterDeliverable.class.getName() + " where user_id=" + userId;
    return dao.findAll(query);
  }

  @Override
  public long save(CenterDeliverable deliverable) {
    if (deliverable.getId() == null) {
      dao.save(deliverable);
    } else {
      dao.update(deliverable);
    }
    return deliverable.getId();
  }

  @Override
  public long save(CenterDeliverable deliverable, String actionName, List<String> relationsName) {
    if (deliverable.getId() == null) {
      dao.save(deliverable, actionName, relationsName);
    } else {
      dao.update(deliverable, actionName, relationsName);
    }
    return deliverable.getId();
  }


}