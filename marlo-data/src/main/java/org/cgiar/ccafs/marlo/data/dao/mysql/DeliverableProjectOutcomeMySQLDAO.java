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

import org.cgiar.ccafs.marlo.data.dao.DeliverableProjectOutcomeDAO;
import org.cgiar.ccafs.marlo.data.model.DeliverableProjectOutcome;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class DeliverableProjectOutcomeMySQLDAO extends AbstractMarloDAO<DeliverableProjectOutcome, Long>
  implements DeliverableProjectOutcomeDAO {


  @Inject
  public DeliverableProjectOutcomeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteDeliverableProjectOutcome(long deliverableProjectOutcomeId) {
    DeliverableProjectOutcome deliverableProjectOutcome = this.find(deliverableProjectOutcomeId);
    this.delete(deliverableProjectOutcome);
  }

  @Override
  public boolean existDeliverableProjectOutcome(long deliverableProjectOutcomeID) {
    DeliverableProjectOutcome deliverableProjectOutcome = this.find(deliverableProjectOutcomeID);
    if (deliverableProjectOutcome == null) {
      return false;
    }
    return true;

  }

  @Override
  public DeliverableProjectOutcome find(long id) {
    return super.find(DeliverableProjectOutcome.class, id);

  }

  @Override
  public List<DeliverableProjectOutcome> findAll() {
    String query = "from " + DeliverableProjectOutcome.class.getName();
    List<DeliverableProjectOutcome> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public DeliverableProjectOutcome save(DeliverableProjectOutcome deliverableProjectOutcome) {
    if (deliverableProjectOutcome.getId() == null) {
      super.saveEntity(deliverableProjectOutcome);
    } else {
      deliverableProjectOutcome = super.update(deliverableProjectOutcome);
    }


    return deliverableProjectOutcome;
  }


}