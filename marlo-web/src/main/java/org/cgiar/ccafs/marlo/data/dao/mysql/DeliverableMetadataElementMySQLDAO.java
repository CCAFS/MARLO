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

import org.cgiar.ccafs.marlo.data.dao.DeliverableMetadataElementDAO;
import org.cgiar.ccafs.marlo.data.model.DeliverableMetadataElement;

import java.util.List;

import com.google.inject.Inject;

public class DeliverableMetadataElementMySQLDAO implements DeliverableMetadataElementDAO {

  private StandardDAO dao;

  @Inject
  public DeliverableMetadataElementMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteDeliverableMetadataElement(long deliverableMetadataElementId) {
    DeliverableMetadataElement deliverableMetadataElement = this.find(deliverableMetadataElementId);
    return this.save(deliverableMetadataElement) > 0;
  }

  @Override
  public boolean existDeliverableMetadataElement(long deliverableMetadataElementID) {
    DeliverableMetadataElement deliverableMetadataElement = this.find(deliverableMetadataElementID);
    if (deliverableMetadataElement == null) {
      return false;
    }
    return true;

  }

  @Override
  public DeliverableMetadataElement find(long id) {
    return dao.find(DeliverableMetadataElement.class, id);

  }

  @Override
  public List<DeliverableMetadataElement> findAll() {
    String query = "from " + DeliverableMetadataElement.class.getName();
    List<DeliverableMetadataElement> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(DeliverableMetadataElement deliverableMetadataElement) {
    if (deliverableMetadataElement.getId() == null) {
      dao.save(deliverableMetadataElement);
    } else {
      dao.update(deliverableMetadataElement);
    }


    return deliverableMetadataElement.getId();
  }


}