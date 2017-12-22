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

import org.cgiar.ccafs.marlo.data.dao.DeliverableInfoDAO;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class DeliverableInfoMySQLDAO extends AbstractMarloDAO<DeliverableInfo, Long> implements DeliverableInfoDAO {


  @Inject
  public DeliverableInfoMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteDeliverableInfo(long deliverableInfoId) {
    DeliverableInfo deliverableInfo = this.find(deliverableInfoId);
    super.delete(deliverableInfo);
  }

  @Override
  public boolean existDeliverableInfo(long deliverableInfoID) {
    DeliverableInfo deliverableInfo = this.find(deliverableInfoID);
    if (deliverableInfo == null) {
      return false;
    }
    return true;

  }

  @Override
  public DeliverableInfo find(long id) {
    return super.find(DeliverableInfo.class, id);

  }

  @Override
  public List<DeliverableInfo> findAll() {
    String query = "from " + DeliverableInfo.class.getName() + " where is_active=1";
    List<DeliverableInfo> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public DeliverableInfo save(DeliverableInfo deliverableInfo) {
    if (deliverableInfo.getId() == null) {
      super.saveEntity(deliverableInfo);
    } else {
      deliverableInfo = super.update(deliverableInfo);
    }


    return deliverableInfo;
  }


}