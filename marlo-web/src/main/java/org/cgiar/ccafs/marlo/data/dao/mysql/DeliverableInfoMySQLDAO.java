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

import com.google.inject.Inject;

public class DeliverableInfoMySQLDAO implements DeliverableInfoDAO {

  private StandardDAO dao;

  @Inject
  public DeliverableInfoMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteDeliverableInfo(long deliverableInfoId) {
    DeliverableInfo deliverableInfo = this.find(deliverableInfoId);
    return dao.delete(deliverableInfo);
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
    return dao.find(DeliverableInfo.class, id);

  }

  @Override
  public List<DeliverableInfo> findAll() {
    String query = "from " + DeliverableInfo.class.getName() + " where is_active=1";
    List<DeliverableInfo> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(DeliverableInfo deliverableInfo) {
    if (deliverableInfo.getId() == null) {
      dao.save(deliverableInfo);
    } else {
      dao.update(deliverableInfo);
    }


    return deliverableInfo.getId();
  }


}