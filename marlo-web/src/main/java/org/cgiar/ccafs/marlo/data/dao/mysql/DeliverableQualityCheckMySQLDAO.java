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

import org.cgiar.ccafs.marlo.data.dao.DeliverableQualityCheckDAO;
import org.cgiar.ccafs.marlo.data.model.DeliverableQualityCheck;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class DeliverableQualityCheckMySQLDAO extends AbstractMarloDAO<DeliverableQualityCheck, Long> implements DeliverableQualityCheckDAO {


  @Inject
  public DeliverableQualityCheckMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteDeliverableQualityCheck(long deliverableQualityCheckId) {
    DeliverableQualityCheck deliverableQualityCheck = this.find(deliverableQualityCheckId);
    deliverableQualityCheck.setActive(false);
    this.save(deliverableQualityCheck);
  }

  @Override
  public boolean existDeliverableQualityCheck(long deliverableQualityCheckID) {
    DeliverableQualityCheck deliverableQualityCheck = this.find(deliverableQualityCheckID);
    if (deliverableQualityCheck == null) {
      return false;
    }
    return true;

  }

  @Override
  public DeliverableQualityCheck find(long id) {
    return super.find(DeliverableQualityCheck.class, id);

  }

  @Override
  public List<DeliverableQualityCheck> findAll() {
    String query = "from " + DeliverableQualityCheck.class.getName() + " where is_active=1";
    List<DeliverableQualityCheck> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public DeliverableQualityCheck findByDeliverable(long id) {
    String query =
      "from " + DeliverableQualityCheck.class.getName() + " where deliverable_id=" + id + " and is_active=1";
    List<DeliverableQualityCheck> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public DeliverableQualityCheck save(DeliverableQualityCheck deliverableQualityCheck) {
    if (deliverableQualityCheck.getId() == null) {
      super.saveEntity(deliverableQualityCheck);
    } else {
      deliverableQualityCheck = super.update(deliverableQualityCheck);
    }


    return deliverableQualityCheck;
  }


}