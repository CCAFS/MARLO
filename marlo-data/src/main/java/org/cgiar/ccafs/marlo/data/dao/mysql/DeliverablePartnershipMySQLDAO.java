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

public class DeliverablePartnershipMySQLDAO implements DeliverablePartnershipDAO {

  private StandardDAO dao;

  @Inject
  public DeliverablePartnershipMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteDeliverablePartnership(long deliverablePartnershipId) {
    DeliverablePartnership deliverablePartnership = this.find(deliverablePartnershipId);
    deliverablePartnership.setActive(false);
    return this.save(deliverablePartnership) > 0;
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
    return dao.find(DeliverablePartnership.class, id);

  }

  @Override
  public List<DeliverablePartnership> findAll() {
    String query = "from " + DeliverablePartnership.class.getName() + " where is_active=1";
    List<DeliverablePartnership> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(DeliverablePartnership deliverablePartnership) {
    if (deliverablePartnership.getId() == null) {
      dao.save(deliverablePartnership);
    } else {
      dao.update(deliverablePartnership);
    }


    return deliverablePartnership.getId();
  }


}