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

import org.cgiar.ccafs.marlo.data.dao.DeliverableGenderLevelDAO;
import org.cgiar.ccafs.marlo.data.model.DeliverableGenderLevel;

import java.util.List;

import com.google.inject.Inject;

public class DeliverableGenderLevelMySQLDAO implements DeliverableGenderLevelDAO {

  private StandardDAO dao;

  @Inject
  public DeliverableGenderLevelMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteDeliverableGenderLevel(long deliverableGenderLevelId) {
    DeliverableGenderLevel deliverableGenderLevel = this.find(deliverableGenderLevelId);
    deliverableGenderLevel.setActive(false);
    return this.save(deliverableGenderLevel) > 0;
  }

  @Override
  public boolean existDeliverableGenderLevel(long deliverableGenderLevelID) {
    DeliverableGenderLevel deliverableGenderLevel = this.find(deliverableGenderLevelID);
    if (deliverableGenderLevel == null) {
      return false;
    }
    return true;

  }

  @Override
  public DeliverableGenderLevel find(long id) {
    return dao.find(DeliverableGenderLevel.class, id);

  }

  @Override
  public List<DeliverableGenderLevel> findAll() {
    String query = "from " + DeliverableGenderLevel.class.getName() + " where is_active=1";
    List<DeliverableGenderLevel> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(DeliverableGenderLevel deliverableGenderLevel) {
    if (deliverableGenderLevel.getId() == null) {
      dao.save(deliverableGenderLevel);
    } else {
      dao.update(deliverableGenderLevel);
    }


    return deliverableGenderLevel.getId();
  }


}