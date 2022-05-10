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

import org.cgiar.ccafs.marlo.data.dao.DeliverableCrpOutcomeDAO;
import org.cgiar.ccafs.marlo.data.model.DeliverableCrpOutcome;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class DeliverableCrpOutcomeMySQLDAO extends AbstractMarloDAO<DeliverableCrpOutcome, Long>
  implements DeliverableCrpOutcomeDAO {


  @Inject
  public DeliverableCrpOutcomeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteDeliverableCrpOutcome(long deliverableCrpOutcomeId) {
    DeliverableCrpOutcome deliverableCrpOutcome = this.find(deliverableCrpOutcomeId);
    this.delete(deliverableCrpOutcome);
  }

  @Override
  public boolean existDeliverableCrpOutcome(long deliverableCrpOutcomeID) {
    DeliverableCrpOutcome deliverableCrpOutcome = this.find(deliverableCrpOutcomeID);
    if (deliverableCrpOutcome == null) {
      return false;
    }
    return true;

  }

  @Override
  public DeliverableCrpOutcome find(long id) {
    return super.find(DeliverableCrpOutcome.class, id);

  }

  @Override
  public List<DeliverableCrpOutcome> findAll() {
    String query = "from " + DeliverableCrpOutcome.class.getName();
    List<DeliverableCrpOutcome> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public DeliverableCrpOutcome save(DeliverableCrpOutcome deliverableCrpOutcome) {
    if (deliverableCrpOutcome.getId() == null) {
      super.saveEntity(deliverableCrpOutcome);
    } else {
      deliverableCrpOutcome = super.update(deliverableCrpOutcome);
    }


    return deliverableCrpOutcome;
  }


}