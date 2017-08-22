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

import org.cgiar.ccafs.marlo.data.dao.DeliverablePartnershipDAO;
import org.cgiar.ccafs.marlo.data.model.DeliverablePartnership;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class DeliverablePartnershipMySQLDAO extends AbstractMarloDAO<DeliverablePartnership, Long> implements DeliverablePartnershipDAO {


  @Inject
  public DeliverablePartnershipMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteDeliverablePartnership(long deliverablePartnershipId) {
    DeliverablePartnership deliverablePartnership = this.find(deliverablePartnershipId);
    deliverablePartnership.setActive(false);
    this.save(deliverablePartnership);
  }

  @Override
  public boolean existDeliverablePartnership(long deliverablePartnershipID) {
    DeliverablePartnership deliverablePartnership = this.find(deliverablePartnershipID);
    if (deliverablePartnership == null) {
      return false;
    }
    return true;

  }

  @Override
  public DeliverablePartnership find(long id) {
    return super.find(DeliverablePartnership.class, id);

  }

  @Override
  public List<DeliverablePartnership> findAll() {
    String query = "from " + DeliverablePartnership.class.getName() + " where is_active=1";
    List<DeliverablePartnership> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public DeliverablePartnership save(DeliverablePartnership deliverablePartnership) {
    if (deliverablePartnership.getId() == null) {
      super.saveEntity(deliverablePartnership);
    } else {
      deliverablePartnership = super.update(deliverablePartnership);
    }


    return deliverablePartnership;
  }


}