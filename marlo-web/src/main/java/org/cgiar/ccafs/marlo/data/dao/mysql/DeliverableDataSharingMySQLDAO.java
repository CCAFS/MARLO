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

import org.cgiar.ccafs.marlo.data.dao.DeliverableDataSharingDAO;
import org.cgiar.ccafs.marlo.data.model.DeliverableDataSharing;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class DeliverableDataSharingMySQLDAO extends AbstractMarloDAO<DeliverableDataSharing, Long> implements DeliverableDataSharingDAO {


  @Inject
  public DeliverableDataSharingMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteDeliverableDataSharing(long deliverableDataSharingId) {
    DeliverableDataSharing deliverableDataSharing = this.find(deliverableDataSharingId);
    super.delete(deliverableDataSharing);
  }

  @Override
  public boolean existDeliverableDataSharing(long deliverableDataSharingID) {
    DeliverableDataSharing deliverableDataSharing = this.find(deliverableDataSharingID);
    if (deliverableDataSharing == null) {
      return false;
    }
    return true;

  }

  @Override
  public DeliverableDataSharing find(long id) {
    return super.find(DeliverableDataSharing.class, id);

  }

  @Override
  public List<DeliverableDataSharing> findAll() {
    String query = "from " + DeliverableDataSharing.class.getName() + " where is_active=1";
    List<DeliverableDataSharing> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public DeliverableDataSharing save(DeliverableDataSharing deliverableDataSharing) {
    if (deliverableDataSharing.getId() == null) {
      super.saveEntity(deliverableDataSharing);
    } else {
      deliverableDataSharing = super.update(deliverableDataSharing);
    }


    return deliverableDataSharing;
  }


}