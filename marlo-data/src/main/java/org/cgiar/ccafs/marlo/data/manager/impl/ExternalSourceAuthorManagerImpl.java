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

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.dao.ExternalSourceAuthorDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableMetadataExternalSourcesManager;
import org.cgiar.ccafs.marlo.data.manager.ExternalSourceAuthorManager;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableMetadataExternalSources;
import org.cgiar.ccafs.marlo.data.model.ExternalSourceAuthor;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/
@Named
public class ExternalSourceAuthorManagerImpl implements ExternalSourceAuthorManager {

  // Logger
  private static final Logger LOG = LoggerFactory.getLogger(ExternalSourceAuthorManagerImpl.class);

  // DAO
  private ExternalSourceAuthorDAO externalSourceAuthorDAO;

  // Managers
  private DeliverableMetadataExternalSourcesManager deliverableMetadataExternalSourcesManager;
  private DeliverableManager deliverableManager;

  @Inject
  public ExternalSourceAuthorManagerImpl(ExternalSourceAuthorDAO externalSourceAuthorDAO,
    DeliverableMetadataExternalSourcesManager deliverableMetadataExternalSourcesManager,
    DeliverableManager deliverableManager) {
    this.externalSourceAuthorDAO = externalSourceAuthorDAO;
    this.deliverableMetadataExternalSourcesManager = deliverableMetadataExternalSourcesManager;
    this.deliverableManager = deliverableManager;
  }

  @Override
  public void deleteAllAuthorsFromPhase(Deliverable deliverable, Phase initialPhase) {
    Phase current = initialPhase;
    int initialPhaseYear = initialPhase.getYear();
    Deliverable thisDeliverable = deliverable;

    if (thisDeliverable != null) {
      thisDeliverable = deliverableManager.getDeliverableById(thisDeliverable.getId());
    }

    while (current != null) {
      DeliverableMetadataExternalSources deliverableMetadataExternalSources =
        this.deliverableMetadataExternalSourcesManager.findByPhaseAndDeliverable(current, deliverable);
      if (deliverableMetadataExternalSources != null) {
        List<ExternalSourceAuthor> phaseAuthors =
          this.findExternalSourceAuthorFromExternalSource(deliverableMetadataExternalSources.getId());

        for (ExternalSourceAuthor externalSourceAuthor : phaseAuthors) {
          if (externalSourceAuthor != null) {
            this.deleteExternalSourceAuthor(externalSourceAuthor.getId());
          }
        }
      }

      current = ((current.getDescription().equals(APConstants.REPORTING) && current.getYear() == initialPhaseYear)
        ? current.getNext().getNext() : current.getNext());
    }
  }

  @Override
  public void deleteExternalSourceAuthor(long externalSourceAuthorId) {
    externalSourceAuthorDAO.deleteExternalSourceAuthor(externalSourceAuthorId);
  }

  @Override
  public boolean existExternalSourceAuthor(long externalSourceAuthorID) {
    return externalSourceAuthorDAO.existExternalSourceAuthor(externalSourceAuthorID);
  }

  @Override
  public List<ExternalSourceAuthor> findAll() {
    return externalSourceAuthorDAO.findAll();
  }

  @Override
  public List<ExternalSourceAuthor>
    findExternalSourceAuthorFromExternalSource(long deliverableMetadataExternalSourceId) {
    return this.externalSourceAuthorDAO.findExternalSourceAuthorFromExternalSource(deliverableMetadataExternalSourceId);
  }

  @Override
  public ExternalSourceAuthor getExternalSourceAuthorById(long externalSourceAuthorID) {
    return externalSourceAuthorDAO.find(externalSourceAuthorID);
  }

  @Override
  public void replicate(ExternalSourceAuthor originalExternalSourceAuthor, Phase initialPhase) {
    Phase current = initialPhase;
    Deliverable deliverable = originalExternalSourceAuthor.getDeliverableMetadataExternalSources().getDeliverable();
    if (deliverable != null) {
      deliverable = this.deliverableManager.getDeliverableById(deliverable.getId());
    }

    while (current != null) {
      DeliverableMetadataExternalSources externalSource =
        this.deliverableMetadataExternalSourcesManager.findByPhaseAndDeliverable(current, deliverable);

      ExternalSourceAuthor externalSourceAuthor = new ExternalSourceAuthor();
      externalSourceAuthor.copyFields(originalExternalSourceAuthor);
      externalSourceAuthor.setDeliverableMetadataExternalSources(externalSource);
      // LOG.debug(current.toString());
      current = current.getNext();
    }
  }

  @Override
  public ExternalSourceAuthor saveExternalSourceAuthor(ExternalSourceAuthor externalSourceAuthor) {
    return this.externalSourceAuthorDAO.save(externalSourceAuthor);
  }
}
