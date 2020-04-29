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

import org.cgiar.ccafs.marlo.data.dao.DeliverableClarisaUserDAO;
import org.cgiar.ccafs.marlo.data.model.DeliverableClarisaUser;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class DeliverableClarisaUserMySQLDAO extends AbstractMarloDAO<DeliverableClarisaUser, Long>
  implements DeliverableClarisaUserDAO {


  @Inject
  public DeliverableClarisaUserMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteDeliverableClarisaUser(long deliverableClarisaUserId) {
    DeliverableClarisaUser deliverableClarisaUser = this.find(deliverableClarisaUserId);
    this.update(deliverableClarisaUser);
  }

  @Override
  public boolean existDeliverableClarisaUser(long deliverableClarisaUserID) {
    DeliverableClarisaUser deliverableClarisaUser = this.find(deliverableClarisaUserID);
    if (deliverableClarisaUser == null) {
      return false;
    }
    return true;

  }

  @Override
  public DeliverableClarisaUser find(long id) {
    return super.find(DeliverableClarisaUser.class, id);

  }

  @Override
  public List<DeliverableClarisaUser> findAll() {
    String query = "from " + DeliverableClarisaUser.class.getName();
    List<DeliverableClarisaUser> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public DeliverableClarisaUser save(DeliverableClarisaUser deliverableClarisaUser) {
    if (deliverableClarisaUser.getId() == null) {
      super.saveEntity(deliverableClarisaUser);
    } else {
      deliverableClarisaUser = super.update(deliverableClarisaUser);
    }


    return deliverableClarisaUser;
  }


}