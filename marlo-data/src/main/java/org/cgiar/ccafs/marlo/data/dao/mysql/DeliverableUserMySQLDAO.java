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

import org.cgiar.ccafs.marlo.data.dao.DeliverableUserDAO;
import org.cgiar.ccafs.marlo.data.model.DeliverableUser;

import java.util.List;

import com.google.inject.Inject;

public class DeliverableUserMySQLDAO implements DeliverableUserDAO {

  private StandardDAO dao;

  @Inject
  public DeliverableUserMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteDeliverableUser(long deliverableUserId) {
    DeliverableUser deliverableUser = this.find(deliverableUserId);
    return dao.delete(deliverableUser);
  }

  @Override
  public boolean existDeliverableUser(long deliverableUserID) {
    DeliverableUser deliverableUser = this.find(deliverableUserID);
    if (deliverableUser == null) {
      return false;
    }
    return true;

  }

  @Override
  public DeliverableUser find(long id) {
    return dao.find(DeliverableUser.class, id);

  }

  @Override
  public List<DeliverableUser> findAll() {
    String query = "from " + DeliverableUser.class.getName() + " where is_active=1";
    List<DeliverableUser> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(DeliverableUser deliverableUser) {
    if (deliverableUser.getId() == null) {
      dao.save(deliverableUser);
    } else {
      dao.update(deliverableUser);
    }


    return deliverableUser.getId();
  }


}