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

import org.cgiar.ccafs.marlo.data.dao.DeliverableAffiliationDAO;
import org.cgiar.ccafs.marlo.data.model.DeliverableAffiliation;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class DeliverableAffiliationMySQLDAO extends AbstractMarloDAO<DeliverableAffiliation, Long>
  implements DeliverableAffiliationDAO {


  @Inject
  public DeliverableAffiliationMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteDeliverableAffiliation(long deliverableAffiliationId) {
    DeliverableAffiliation deliverableAffiliation = this.find(deliverableAffiliationId);
    deliverableAffiliation.setActive(false);
    this.update(deliverableAffiliation);
  }

  @Override
  public boolean existDeliverableAffiliation(long deliverableAffiliationID) {
    DeliverableAffiliation deliverableAffiliation = this.find(deliverableAffiliationID);
    if (deliverableAffiliation == null) {
      return false;
    }
    return true;

  }

  @Override
  public DeliverableAffiliation find(long id) {
    return super.find(DeliverableAffiliation.class, id);

  }

  @Override
  public List<DeliverableAffiliation> findAll() {
    String query = "from " + DeliverableAffiliation.class.getName();
    List<DeliverableAffiliation> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public DeliverableAffiliation save(DeliverableAffiliation deliverableAffiliation) {
    if (deliverableAffiliation.getId() == null) {
      super.saveEntity(deliverableAffiliation);
    } else {
      deliverableAffiliation = super.update(deliverableAffiliation);
    }


    return deliverableAffiliation;
  }


}