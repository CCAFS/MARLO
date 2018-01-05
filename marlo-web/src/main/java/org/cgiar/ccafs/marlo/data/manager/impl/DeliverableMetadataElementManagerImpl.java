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


import org.cgiar.ccafs.marlo.data.dao.DeliverableMetadataElementDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableMetadataElementManager;
import org.cgiar.ccafs.marlo.data.model.DeliverableMetadataElement;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class DeliverableMetadataElementManagerImpl implements DeliverableMetadataElementManager {


  private DeliverableMetadataElementDAO deliverableMetadataElementDAO;
  // Managers


  @Inject
  public DeliverableMetadataElementManagerImpl(DeliverableMetadataElementDAO deliverableMetadataElementDAO) {
    this.deliverableMetadataElementDAO = deliverableMetadataElementDAO;


  }

  @Override
  public void deleteDeliverableMetadataElement(long deliverableMetadataElementId) {

    deliverableMetadataElementDAO.deleteDeliverableMetadataElement(deliverableMetadataElementId);
  }

  @Override
  public boolean existDeliverableMetadataElement(long deliverableMetadataElementID) {

    return deliverableMetadataElementDAO.existDeliverableMetadataElement(deliverableMetadataElementID);
  }

  @Override
  public List<DeliverableMetadataElement> findAll() {

    return deliverableMetadataElementDAO.findAll();

  }

  @Override
  public DeliverableMetadataElement getDeliverableMetadataElementById(long deliverableMetadataElementID) {

    return deliverableMetadataElementDAO.find(deliverableMetadataElementID);
  }

  @Override
  public DeliverableMetadataElement saveDeliverableMetadataElement(DeliverableMetadataElement deliverableMetadataElement) {

    return deliverableMetadataElementDAO.save(deliverableMetadataElement);
  }


}
