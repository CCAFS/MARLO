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

import org.cgiar.ccafs.marlo.data.dao.DeliverablePartnerTypeDAO;
import org.cgiar.ccafs.marlo.data.model.DeliverablePartnerType;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class DeliverablePartnerTypeMySQLDAO extends AbstractMarloDAO<DeliverablePartnerType, Long>
  implements DeliverablePartnerTypeDAO {


  @Inject
  public DeliverablePartnerTypeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteDeliverablePartnerType(long deliverablePartnerTypeId) {
    DeliverablePartnerType deliverablePartnerType = this.find(deliverablePartnerTypeId);
    this.delete(deliverablePartnerType);
  }

  @Override
  public boolean existDeliverablePartnerType(long deliverablePartnerTypeID) {
    DeliverablePartnerType deliverablePartnerType = this.find(deliverablePartnerTypeID);
    if (deliverablePartnerType == null) {
      return false;
    }
    return true;

  }

  @Override
  public DeliverablePartnerType find(long id) {
    return super.find(DeliverablePartnerType.class, id);

  }

  @Override
  public List<DeliverablePartnerType> findAll() {
    String query = "from " + DeliverablePartnerType.class.getName() + " where is_active=1";
    List<DeliverablePartnerType> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public DeliverablePartnerType save(DeliverablePartnerType deliverablePartnerType) {
    if (deliverablePartnerType.getId() == null) {
      super.saveEntity(deliverablePartnerType);
    } else {
      deliverablePartnerType = super.update(deliverablePartnerType);
    }


    return deliverablePartnerType;
  }


}