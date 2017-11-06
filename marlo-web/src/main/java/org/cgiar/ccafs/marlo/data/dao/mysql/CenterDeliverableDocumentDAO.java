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

import org.cgiar.ccafs.marlo.data.dao.ICenterDeliverableDocumentDAO;
import org.cgiar.ccafs.marlo.data.model.CenterDeliverableDocument;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class CenterDeliverableDocumentDAO extends AbstractMarloDAO<CenterDeliverableDocument, Long>
  implements ICenterDeliverableDocumentDAO {


  @Inject
  public CenterDeliverableDocumentDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteDeliverableDocument(long deliverableDocumentId) {
    CenterDeliverableDocument deliverableDocument = this.find(deliverableDocumentId);
    deliverableDocument.setActive(false);
    this.save(deliverableDocument);
  }

  @Override
  public boolean existDeliverableDocument(long deliverableDocumentID) {
    CenterDeliverableDocument deliverableDocument = this.find(deliverableDocumentID);
    if (deliverableDocument == null) {
      return false;
    }
    return true;

  }

  @Override
  public CenterDeliverableDocument find(long id) {
    return super.find(CenterDeliverableDocument.class, id);

  }

  @Override
  public List<CenterDeliverableDocument> findAll() {
    String query = "from " + CenterDeliverableDocument.class.getName();
    List<CenterDeliverableDocument> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CenterDeliverableDocument> getDeliverableDocumentsByUserId(long userId) {
    String query = "from " + CenterDeliverableDocument.class.getName() + " where user_id=" + userId;
    return super.findAll(query);
  }

  @Override
  public CenterDeliverableDocument save(CenterDeliverableDocument deliverableDocument) {
    if (deliverableDocument.getId() == null) {
      super.saveEntity(deliverableDocument);
    } else {
      deliverableDocument = super.update(deliverableDocument);
    }
    return deliverableDocument;
  }


}