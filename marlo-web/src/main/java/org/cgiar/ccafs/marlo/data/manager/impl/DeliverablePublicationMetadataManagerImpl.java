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


import org.cgiar.ccafs.marlo.data.dao.DeliverablePublicationMetadataDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverablePublicationMetadataManager;
import org.cgiar.ccafs.marlo.data.model.DeliverablePublicationMetadata;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class DeliverablePublicationMetadataManagerImpl implements DeliverablePublicationMetadataManager {


  private DeliverablePublicationMetadataDAO deliverablePublicationMetadataDAO;
  // Managers


  @Inject
  public DeliverablePublicationMetadataManagerImpl(DeliverablePublicationMetadataDAO deliverablePublicationMetadataDAO) {
    this.deliverablePublicationMetadataDAO = deliverablePublicationMetadataDAO;


  }

  @Override
  public void deleteDeliverablePublicationMetadata(long deliverablePublicationMetadataId) {

    deliverablePublicationMetadataDAO.deleteDeliverablePublicationMetadata(deliverablePublicationMetadataId);
  }

  @Override
  public boolean existDeliverablePublicationMetadata(long deliverablePublicationMetadataID) {

    return deliverablePublicationMetadataDAO.existDeliverablePublicationMetadata(deliverablePublicationMetadataID);
  }

  @Override
  public List<DeliverablePublicationMetadata> findAll() {

    return deliverablePublicationMetadataDAO.findAll();

  }

  @Override
  public DeliverablePublicationMetadata getDeliverablePublicationMetadataById(long deliverablePublicationMetadataID) {

    return deliverablePublicationMetadataDAO.find(deliverablePublicationMetadataID);
  }

  @Override
  public DeliverablePublicationMetadata saveDeliverablePublicationMetadata(DeliverablePublicationMetadata deliverablePublicationMetadata) {

    return deliverablePublicationMetadataDAO.save(deliverablePublicationMetadata);
  }


}
