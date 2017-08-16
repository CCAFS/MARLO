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

import org.cgiar.ccafs.marlo.data.dao.DeliverableLeaderDAO;
import org.cgiar.ccafs.marlo.data.model.DeliverableLeader;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class DeliverableLeaderMySQLDAO extends AbstractMarloDAO<DeliverableLeader, Long> implements DeliverableLeaderDAO {


  @Inject
  public DeliverableLeaderMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public boolean deleteDeliverableLeader(long deliverableLeaderId) {
    DeliverableLeader deliverableLeader = this.find(deliverableLeaderId);

    return super.delete(deliverableLeader);
  }

  @Override
  public boolean existDeliverableLeader(long deliverableLeaderID) {
    DeliverableLeader deliverableLeader = this.find(deliverableLeaderID);
    if (deliverableLeader == null) {
      return false;
    }
    return true;

  }

  @Override
  public DeliverableLeader find(long id) {
    return super.find(DeliverableLeader.class, id);

  }

  @Override
  public List<DeliverableLeader> findAll() {
    String query = "from " + DeliverableLeader.class.getName() + " where is_active=1";
    List<DeliverableLeader> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(DeliverableLeader deliverableLeader) {
    if (deliverableLeader.getId() == null) {
      super.saveEntity(deliverableLeader);
    } else {
      super.update(deliverableLeader);
    }


    return deliverableLeader.getId();
  }


}