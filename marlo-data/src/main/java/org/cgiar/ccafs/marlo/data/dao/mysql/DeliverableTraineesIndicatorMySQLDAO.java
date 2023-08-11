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

import org.cgiar.ccafs.marlo.data.dao.DeliverableTraineesIndicatorDAO;
import org.cgiar.ccafs.marlo.data.model.DeliverableTraineesIndicator;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class DeliverableTraineesIndicatorMySQLDAO extends AbstractMarloDAO<DeliverableTraineesIndicator, Long>
  implements DeliverableTraineesIndicatorDAO {


  @Inject
  public DeliverableTraineesIndicatorMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteDeliverableTraineesIndicator(long deliverableTraineesIndicatorId) {
    DeliverableTraineesIndicator deliverableTraineesIndicator = this.find(deliverableTraineesIndicatorId);
    this.delete(deliverableTraineesIndicator);
  }

  @Override
  public boolean existDeliverableTraineesIndicator(long deliverableTraineesIndicatorID) {
    DeliverableTraineesIndicator deliverableTraineesIndicator = this.find(deliverableTraineesIndicatorID);
    if (deliverableTraineesIndicator == null) {
      return false;
    }
    return true;

  }

  @Override
  public DeliverableTraineesIndicator find(long id) {
    return super.find(DeliverableTraineesIndicator.class, id);

  }

  @Override
  public List<DeliverableTraineesIndicator> findAll() {
    String query = "from " + DeliverableTraineesIndicator.class.getName();
    List<DeliverableTraineesIndicator> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public DeliverableTraineesIndicator save(DeliverableTraineesIndicator deliverableTraineesIndicator) {
    if (deliverableTraineesIndicator.getId() == null) {
      super.saveEntity(deliverableTraineesIndicator);
    } else {
      deliverableTraineesIndicator = super.update(deliverableTraineesIndicator);
    }


    return deliverableTraineesIndicator;
  }


}