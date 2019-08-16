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

import org.cgiar.ccafs.marlo.data.dao.DeliverableUserPartnershipDAO;
import org.cgiar.ccafs.marlo.data.model.DeliverableUserPartnership;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class DeliverableUserPartnershipMySQLDAO extends AbstractMarloDAO<DeliverableUserPartnership, Long> implements DeliverableUserPartnershipDAO {


  @Inject
  public DeliverableUserPartnershipMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteDeliverableUserPartnership(long deliverableUserPartnershipId) {
    DeliverableUserPartnership deliverableUserPartnership = this.find(deliverableUserPartnershipId);
    deliverableUserPartnership.setActive(false);
    this.update(deliverableUserPartnership);
  }

  @Override
  public boolean existDeliverableUserPartnership(long deliverableUserPartnershipID) {
    DeliverableUserPartnership deliverableUserPartnership = this.find(deliverableUserPartnershipID);
    if (deliverableUserPartnership == null) {
      return false;
    }
    return true;

  }

  @Override
  public DeliverableUserPartnership find(long id) {
    return super.find(DeliverableUserPartnership.class, id);

  }

  @Override
  public List<DeliverableUserPartnership> findAll() {
    String query = "from " + DeliverableUserPartnership.class.getName() + " where is_active=1";
    List<DeliverableUserPartnership> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public DeliverableUserPartnership save(DeliverableUserPartnership deliverableUserPartnership) {
    if (deliverableUserPartnership.getId() == null) {
      super.saveEntity(deliverableUserPartnership);
    } else {
      deliverableUserPartnership = super.update(deliverableUserPartnership);
    }


    return deliverableUserPartnership;
  }


}