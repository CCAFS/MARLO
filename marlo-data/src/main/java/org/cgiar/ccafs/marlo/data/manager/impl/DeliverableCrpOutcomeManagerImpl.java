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
import org.cgiar.ccafs.marlo.data.dao.DeliverableCrpOutcomeDAO;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableCrpOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableCrpOutcome;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class DeliverableCrpOutcomeManagerImpl implements DeliverableCrpOutcomeManager {


  private DeliverableCrpOutcomeDAO deliverableCrpOutcomeDAO;
  private DeliverableManager deliverableManager;
  private PhaseDAO phaseDAO;

  // Managers


  @Inject
  public DeliverableCrpOutcomeManagerImpl(DeliverableCrpOutcomeDAO deliverableCrpOutcomeDAO,
    DeliverableManager deliverableManager, PhaseDAO phaseDAO) {
    this.deliverableCrpOutcomeDAO = deliverableCrpOutcomeDAO;
    this.deliverableManager = deliverableManager;
    this.phaseDAO = phaseDAO;
  }

  @Override
  public void deleteDeliverableCrpOutcome(long deliverableCrpOutcomeId, long phaseId) {

    DeliverableCrpOutcome deliverableCrpOutcome = deliverableCrpOutcomeDAO.find(deliverableCrpOutcomeId);
    deliverableCrpOutcomeDAO.deleteDeliverableCrpOutcome(deliverableCrpOutcomeId);
    Phase phase = phaseDAO.find(phaseId);
    if (phase.getDescription().equals(APConstants.PLANNING) && phase.getNext() != null) {
      this.deleteDeliverableCrpOutcomePhase(deliverableCrpOutcome.getPhase().getNext(),
        deliverableCrpOutcome.getDeliverable().getId(), deliverableCrpOutcome);
    }
    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (phase.getNext() != null && phase.getNext().getNext() != null) {
        Phase upkeepPhase = phase.getNext().getNext();
        if (upkeepPhase != null) {
          this.deleteDeliverableCrpOutcomePhase(upkeepPhase, deliverableCrpOutcome.getDeliverable().getId(),
            deliverableCrpOutcome);
        }
      }
    }
  }

  public void deleteDeliverableCrpOutcomePhase(Phase next, long deliverableId,
    DeliverableCrpOutcome deliverableCrpOutcome) {
    Phase phase = phaseDAO.find(next.getId());

    List<DeliverableCrpOutcome> deliverableOutcomes = deliverableCrpOutcomeDAO.findAll().stream()
      .filter(c -> c.getDeliverable().getId().equals(deliverableId) && c.getPhase().getId().equals(phase.getId())
        && c.getCrpProgramOutcome().getId().equals(deliverableCrpOutcome.getCrpProgramOutcome().getId()))
      .collect(Collectors.toList());

    // Get CRP outcomes phases
    Deliverable deliverable = deliverableManager.getDeliverableById(deliverableId);
    Project project = deliverable.getProject();

    List<CrpProgramOutcome> crpOutcomes = phase.getOutcomes().stream()
      .filter(c -> c.isActive() && c.getComposeID().equals(deliverableCrpOutcome.getCrpProgramOutcome().getComposeID()))
      .collect(Collectors.toList());

    if (crpOutcomes != null && !crpOutcomes.isEmpty() && crpOutcomes.get(0) != null) {
      if (deliverableOutcomes.isEmpty()) {
        DeliverableCrpOutcome expectedCrpOutcomeAdd = new DeliverableCrpOutcome();
        if (deliverableCrpOutcomeDAO.findAll().stream()
          .filter(po -> po.getPhase().getId().equals(phase.getId())
            && po.getCrpProgramOutcome().getId().equals(crpOutcomes.get(0).getId())
            && po.getDeliverable().getId().equals(deliverable.getId()))
          .collect(Collectors.toList()) != null) {
          List<DeliverableCrpOutcome> listTemp = deliverableCrpOutcomeDAO.findAll().stream()
            .filter(po -> po.getPhase().getId().equals(phase.getId())
              && po.getCrpProgramOutcome().getId().equals(crpOutcomes.get(0).getId())
              && po.getDeliverable().getId().equals(deliverable.getId()))
            .collect(Collectors.toList());
          if (listTemp != null && !listTemp.isEmpty() && listTemp.get(0) != null) {
            expectedCrpOutcomeAdd = listTemp.get(0);
            deliverableCrpOutcomeDAO.deleteDeliverableCrpOutcome(expectedCrpOutcomeAdd.getId());
          }
        }
      }
    }

    if (phase.getNext() != null) {
      this.deleteDeliverableCrpOutcomePhase(phase.getNext(), deliverableId, deliverableCrpOutcome);
    }
  }


  @Override
  public boolean existDeliverableCrpOutcome(long deliverableCrpOutcomeID) {

    return deliverableCrpOutcomeDAO.existDeliverableCrpOutcome(deliverableCrpOutcomeID);
  }

  @Override
  public List<DeliverableCrpOutcome> findAll() {

    return deliverableCrpOutcomeDAO.findAll();

  }

  @Override
  public DeliverableCrpOutcome getDeliverableCrpOutcomeById(long deliverableCrpOutcomeID) {

    return deliverableCrpOutcomeDAO.find(deliverableCrpOutcomeID);
  }

  @Override
  public DeliverableCrpOutcome saveDeliverableCrpOutcome(DeliverableCrpOutcome deliverableCrpOutcome) {
    DeliverableCrpOutcome deliverableOutcome = deliverableCrpOutcomeDAO.save(deliverableCrpOutcome);
    Phase phase = phaseDAO.find(deliverableOutcome.getPhase().getId());
    if (phase.getDescription().equals(APConstants.PLANNING) && phase.getNext() != null) {
      this.saveDeliverableCrpOutcomePhase(deliverableOutcome.getPhase().getNext(),
        deliverableOutcome.getDeliverable().getId(), deliverableCrpOutcome);
    }
    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (phase.getNext() != null && phase.getNext().getNext() != null) {
        Phase upkeepPhase = phase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveDeliverableCrpOutcomePhase(upkeepPhase, deliverableOutcome.getDeliverable().getId(),
            deliverableCrpOutcome);
        }
      }
    }
    return deliverableOutcome;
  }

  public void saveDeliverableCrpOutcomePhase(Phase next, long deliverableId,
    DeliverableCrpOutcome deliverableCrpOutcome) {
    Phase phase = phaseDAO.find(next.getId());

    List<DeliverableCrpOutcome> deliverableOutcomes = deliverableCrpOutcomeDAO.findAll().stream()
      .filter(c -> c.getDeliverable().getId().equals(deliverableId) && c.getPhase().getId().equals(phase.getId())
        && c.getCrpProgramOutcome() != null && c.getCrpProgramOutcome().getComposedName() != null
        && deliverableCrpOutcome.getCrpProgramOutcome().getComposedName() != null && c.getCrpProgramOutcome()
          .getComposedName().equals(deliverableCrpOutcome.getCrpProgramOutcome().getComposedName()))
      .collect(Collectors.toList());

    // Get project outcomes phases
    List<CrpProgramOutcome> crpOutcomes = phase.getOutcomes().stream()
      .filter(c -> c.isActive() && c.getComposeID().equals(deliverableCrpOutcome.getCrpProgramOutcome().getComposeID()))
      .collect(Collectors.toList());

    if (crpOutcomes != null && !crpOutcomes.isEmpty() && crpOutcomes.get(0) != null) {
      if (deliverableOutcomes.isEmpty()) {
        DeliverableCrpOutcome deliverableCrpOutcomeAdd = new DeliverableCrpOutcome();
        deliverableCrpOutcomeAdd.setDeliverable(deliverableCrpOutcome.getDeliverable());
        deliverableCrpOutcomeAdd.setPhase(phase);
        deliverableCrpOutcomeAdd.setCrpProgramOutcome(crpOutcomes.get(0));
        deliverableCrpOutcomeDAO.save(deliverableCrpOutcomeAdd);
      }
    }

    if (phase.getNext() != null) {
      this.saveDeliverableCrpOutcomePhase(phase.getNext(), deliverableId, deliverableCrpOutcome);
    }
  }


}
