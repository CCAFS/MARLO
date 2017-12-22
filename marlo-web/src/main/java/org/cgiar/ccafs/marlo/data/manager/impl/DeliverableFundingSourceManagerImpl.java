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
import org.cgiar.ccafs.marlo.data.dao.DeliverableFundingSourceDAO;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableFundingSourceManager;
import org.cgiar.ccafs.marlo.data.model.DeliverableFundingSource;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class DeliverableFundingSourceManagerImpl implements DeliverableFundingSourceManager {


  private DeliverableFundingSourceDAO deliverableFundingSourceDAO;
  // Managers

  private PhaseDAO phaseDao;

  @Inject
  public DeliverableFundingSourceManagerImpl(DeliverableFundingSourceDAO deliverableFundingSourceDAO,
    PhaseDAO phaseDao) {
    this.deliverableFundingSourceDAO = deliverableFundingSourceDAO;
    this.phaseDao = phaseDao;


  }

  /**
   * clone or update the deliverable funding sources for next phases
   * 
   * @param next the next phase to clone
   * @param deliverableID the deliverable id we are working
   * @param deliverableFundingSource the deliverable funding source to clone
   */
  private void addDeliverableFundingSourcePhase(Phase next, long deliverableID,
    DeliverableFundingSource deliverableFundingSource) {
    Phase phase = phaseDao.find(next.getId());

    List<DeliverableFundingSource> deliverableFundingSources = phase.getDeliverableFundingSources().stream()
      .filter(c -> c.isActive() && c.getDeliverable().getId().longValue() == deliverableID
        && deliverableFundingSource.getFundingSource().getId().equals(c.getFundingSource().getId()))
      .collect(Collectors.toList());
    if (phase.getEditable() != null && phase.getEditable() && deliverableFundingSources.isEmpty()) {
      DeliverableFundingSource deliverableFundingSourceAdd = new DeliverableFundingSource();
      deliverableFundingSourceAdd.setActive(true);
      deliverableFundingSourceAdd.setActiveSince(deliverableFundingSource.getActiveSince());
      deliverableFundingSourceAdd.setCreatedBy(deliverableFundingSource.getCreatedBy());
      deliverableFundingSourceAdd.setModificationJustification(deliverableFundingSource.getModificationJustification());
      deliverableFundingSourceAdd.setModifiedBy(deliverableFundingSource.getModifiedBy());
      deliverableFundingSourceAdd.setPhase(phase);
      deliverableFundingSourceAdd.setDeliverable(deliverableFundingSource.getDeliverable());
      deliverableFundingSourceAdd.setFundingSource(deliverableFundingSource.getFundingSource());
      deliverableFundingSourceDAO.save(deliverableFundingSourceAdd);
    }

    if (phase.getNext() != null) {
      this.addDeliverableFundingSourcePhase(phase.getNext(), deliverableID, deliverableFundingSource);
    }


  }

  @Override
  public DeliverableFundingSource copyDeliverableFundingSource(DeliverableFundingSource deliverableFundingSource,
    Phase phase) {
    DeliverableFundingSource deliverableFundingSourceAdd = new DeliverableFundingSource();
    deliverableFundingSourceAdd.setActive(true);
    deliverableFundingSourceAdd.setActiveSince(deliverableFundingSource.getActiveSince());
    deliverableFundingSourceAdd.setCreatedBy(deliverableFundingSource.getCreatedBy());
    deliverableFundingSourceAdd.setModificationJustification(deliverableFundingSource.getModificationJustification());
    deliverableFundingSourceAdd.setModifiedBy(deliverableFundingSource.getModifiedBy());
    deliverableFundingSourceAdd.setPhase(phase);
    deliverableFundingSourceAdd.setDeliverable(deliverableFundingSource.getDeliverable());
    deliverableFundingSourceAdd.setFundingSource(deliverableFundingSource.getFundingSource());
    deliverableFundingSourceDAO.save(deliverableFundingSourceAdd);
    return deliverableFundingSourceAdd;
  }

  @Override
  public void deleteDeliverableFundingSource(long deliverableFundingSourceId) {


    DeliverableFundingSource deliverableFundingSource =
      this.getDeliverableFundingSourceById(deliverableFundingSourceId);
    deliverableFundingSource.setActive(false);
    this.saveDeliverableFundingSource(deliverableFundingSource);
    Phase currentPhase = phaseDao.find(deliverableFundingSource.getPhase().getId());
    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (deliverableFundingSource.getPhase().getNext() != null) {
        this.deleteDeliverableFundingSource(deliverableFundingSource.getPhase().getNext(),
          deliverableFundingSource.getDeliverable().getId(), deliverableFundingSource);
      }
    }

  }

  public void deleteDeliverableFundingSource(Phase next, long deliverableID,
    DeliverableFundingSource deliverableFundingSource) {
    Phase phase = phaseDao.find(next.getId());

    if (phase.getEditable() != null && phase.getEditable()) {

      List<DeliverableFundingSource> fundingSources = phase.getDeliverableFundingSources().stream()
        .filter(c -> c.isActive() && c.getDeliverable().getId().longValue() == deliverableID
          && deliverableFundingSource.getFundingSource().getId().equals(c.getFundingSource().getId()))
        .collect(Collectors.toList());

      for (DeliverableFundingSource deFundingSource : fundingSources) {
        deFundingSource.setActive(false);
        deliverableFundingSourceDAO.save(deFundingSource);

      }
    }
    if (phase.getNext() != null) {
      this.deleteDeliverableFundingSource(phase.getNext(), deliverableID, deliverableFundingSource);
    }


  }

  @Override
  public boolean existDeliverableFundingSource(long deliverableFundingSourceID) {

    return deliverableFundingSourceDAO.existDeliverableFundingSource(deliverableFundingSourceID);
  }

  @Override
  public List<DeliverableFundingSource> findAll() {

    return deliverableFundingSourceDAO.findAll();

  }

  @Override
  public DeliverableFundingSource getDeliverableFundingSourceById(long deliverableFundingSourceID) {

    return deliverableFundingSourceDAO.find(deliverableFundingSourceID);
  }

  @Override
  public DeliverableFundingSource saveDeliverableFundingSource(DeliverableFundingSource deliverableFundingSource) {

    DeliverableFundingSource deliverableFundingSourceDB = deliverableFundingSourceDAO.save(deliverableFundingSource);
    Phase currentPhase = phaseDao.find(deliverableFundingSource.getPhase().getId());
    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (deliverableFundingSource.getPhase().getNext() != null) {
        this.addDeliverableFundingSourcePhase(deliverableFundingSource.getPhase().getNext(),
          deliverableFundingSource.getDeliverable().getId(), deliverableFundingSource);
      }
    }
    return deliverableFundingSourceDB;
  }

}
