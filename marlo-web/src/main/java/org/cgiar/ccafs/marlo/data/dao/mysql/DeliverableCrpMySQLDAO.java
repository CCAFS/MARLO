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

import org.cgiar.ccafs.marlo.data.dao.DeliverableCrpDAO;
import org.cgiar.ccafs.marlo.data.model.DeliverableCrp;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class DeliverableCrpMySQLDAO extends AbstractMarloDAO<DeliverableCrp, Long> implements DeliverableCrpDAO {


  @Inject
  public DeliverableCrpMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteDeliverableCrp(long deliverableCrpId) {
    DeliverableCrp deliverableCrp = this.find(deliverableCrpId);

    super.delete(deliverableCrp);
  }

  @Override
  public boolean existDeliverableCrp(long deliverableCrpID) {
    DeliverableCrp deliverableCrp = this.find(deliverableCrpID);
    if (deliverableCrp == null) {
      return false;
    }
    return true;

  }

  @Override
  public DeliverableCrp find(long id) {
    return super.find(DeliverableCrp.class, id);

  }

  @Override
  public List<DeliverableCrp> findAll() {
    String query = "from " + DeliverableCrp.class.getName() + " where is_active=1";
    List<DeliverableCrp> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public DeliverableCrp save(DeliverableCrp deliverableCrp) {
    if (deliverableCrp.getId() == null) {
      super.saveEntity(deliverableCrp);
    } else {
      deliverableCrp = super.update(deliverableCrp);
    }


    return deliverableCrp;
  }


}