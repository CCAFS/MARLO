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
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.data.dao.ICenterDeliverableDocumentDAO;
import org.cgiar.ccafs.marlo.data.manager.ICenterDeliverableDocumentManager;
import org.cgiar.ccafs.marlo.data.model.CenterDeliverableDocument;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CenterDeliverableDocumentManager implements ICenterDeliverableDocumentManager {


  private ICenterDeliverableDocumentDAO deliverableDocumentDAO;

  // Managers


  @Inject
  public CenterDeliverableDocumentManager(ICenterDeliverableDocumentDAO deliverableDocumentDAO) {
    this.deliverableDocumentDAO = deliverableDocumentDAO;


  }

  @Override
  public boolean deleteDeliverableDocument(long deliverableDocumentId) {

    return deliverableDocumentDAO.deleteDeliverableDocument(deliverableDocumentId);
  }

  @Override
  public boolean existDeliverableDocument(long deliverableDocumentID) {

    return deliverableDocumentDAO.existDeliverableDocument(deliverableDocumentID);
  }

  @Override
  public List<CenterDeliverableDocument> findAll() {

    return deliverableDocumentDAO.findAll();

  }

  @Override
  public CenterDeliverableDocument getDeliverableDocumentById(long deliverableDocumentID) {

    return deliverableDocumentDAO.find(deliverableDocumentID);
  }

  @Override
  public List<CenterDeliverableDocument> getDeliverableDocumentsByUserId(Long userId) {
    return deliverableDocumentDAO.getDeliverableDocumentsByUserId(userId);
  }

  @Override
  public long saveDeliverableDocument(CenterDeliverableDocument deliverableDocument) {

    return deliverableDocumentDAO.save(deliverableDocument);
  }


}
