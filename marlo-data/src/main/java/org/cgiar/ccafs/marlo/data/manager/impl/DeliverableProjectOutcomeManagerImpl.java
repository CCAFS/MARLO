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
import org.cgiar.ccafs.marlo.data.dao.DeliverableProjectOutcomeDAO;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableProjectOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectOutcomeManager;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class DeliverableProjectOutcomeManagerImpl implements DeliverableProjectOutcomeManager {


  private DeliverableProjectOutcomeDAO deliverableProjectOutcomeDAO;
  private DeliverableManager deliverableManager;
  // Managers
  private PhaseDAO phaseDAO;

  @Inject
  public DeliverableProjectOutcomeManagerImpl(DeliverableProjectOutcomeDAO deliverableProjectOutcomeDAO,
    PhaseDAO phaseDAO, ProjectOutcomeManager projectOutcomeManager, DeliverableManager deliverableManager) {
    this.deliverableProjectOutcomeDAO = deliverableProjectOutcomeDAO;
    this.phaseDAO = phaseDAO;
    this.deliverableManager = deliverableManager;
  }

  @Override
  public void deleteDeliverableProjectOutcome(long deliverableProjectOutcomeId, long phaseId) {
    DeliverableProjectOutcome deliverableProjectOutcome =
      deliverableProjectOutcomeDAO.find(deliverableProjectOutcomeId);
    deliverableProjectOutcomeDAO.deleteDeliverableProjectOutcome(deliverableProjectOutcomeId);
    Phase phase = phaseDAO.find(phaseId);
    if (phase.getDescription().equals(APConstants.PLANNING) && phase.getNext() != null) {
      this.deleteDeliverableProjectOutcomePhase(deliverableProjectOutcome.getPhase().getNext(),
        deliverableProjectOutcome.getDeliverable().getId(), deliverableProjectOutcome);
    }
    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (phase.getNext() != null && phase.getNext().getNext() != null) {
        Phase upkeepPhase = phase.getNext().getNext();
        if (upkeepPhase != null) {
          this.deleteDeliverableProjectOutcomePhase(upkeepPhase, deliverableProjectOutcome.getDeliverable().getId(),
            deliverableProjectOutcome);
        }
      }
    }
  }

  public void deleteDeliverableProjectOutcomePhase(Phase next, long deliverableId,
    DeliverableProjectOutcome deliverableProjectOutcome) {
    Phase phase = phaseDAO.find(next.getId());

    List<DeliverableProjectOutcome> deliverableOutcomes = deliverableProjectOutcomeDAO.findAll().stream()
      .filter(c -> c.getDeliverable().getId().equals(deliverableId) && c.getPhase().getId().equals(phase.getId())
        && c.getProjectOutcome().getId().equals(deliverableProjectOutcome.getProjectOutcome().getId()))
      .collect(Collectors.toList());

    // Get project outcomes phases
    Deliverable deliverable = deliverableManager.getDeliverableById(deliverableId);
    Project project = deliverable.getProject();

    List<ProjectOutcome> projectOutcomes = phase.getProjectOutcomes().stream()
      .filter(c -> c.isActive() && c.getProject().getId().longValue() == project.getId()
        && c.getCrpProgramOutcome().getComposeID()
          .equals(deliverableProjectOutcome.getProjectOutcome().getCrpProgramOutcome().getComposeID()))
      .collect(Collectors.toList());

    if (projectOutcomes != null && !projectOutcomes.isEmpty() && projectOutcomes.get(0) != null) {
      if (deliverableOutcomes.isEmpty()) {
        DeliverableProjectOutcome expectedProjectOutcomeAdd = new DeliverableProjectOutcome();
        if (deliverableProjectOutcomeDAO.findAll().stream()
          .filter(po -> po.getPhase().getId().equals(phase.getId())
            && po.getProjectOutcome().getId().equals(projectOutcomes.get(0).getId())
            && po.getDeliverable().getId().equals(deliverable.getId()))
          .collect(Collectors.toList()) != null) {
          List<DeliverableProjectOutcome> listTemp = deliverableProjectOutcomeDAO.findAll().stream()
            .filter(po -> po.getPhase().getId().equals(phase.getId())
              && po.getProjectOutcome().getId().equals(projectOutcomes.get(0).getId())
              && po.getDeliverable().getId().equals(deliverable.getId()))
            .collect(Collectors.toList());
          if (listTemp != null && !listTemp.isEmpty() && listTemp.get(0) != null) {
            expectedProjectOutcomeAdd = listTemp.get(0);
            deliverableProjectOutcomeDAO.deleteDeliverableProjectOutcome(expectedProjectOutcomeAdd.getId());
          }
        }
      }
    }

    if (phase.getNext() != null) {
      this.deleteDeliverableProjectOutcomePhase(phase.getNext(), deliverableId, deliverableProjectOutcome);
    }
  }

  @Override
  public boolean existDeliverableProjectOutcome(long deliverableProjectOutcomeID) {

    return deliverableProjectOutcomeDAO.existDeliverableProjectOutcome(deliverableProjectOutcomeID);
  }

  @Override
  public List<DeliverableProjectOutcome> findAll() {

    return deliverableProjectOutcomeDAO.findAll();

  }

  @Override
  public DeliverableProjectOutcome getDeliverableProjectOutcomeById(long deliverableProjectOutcomeID) {

    return deliverableProjectOutcomeDAO.find(deliverableProjectOutcomeID);
  }

  @Override
  public DeliverableProjectOutcome saveDeliverableProjectOutcome(DeliverableProjectOutcome deliverableProjectOutcome) {
    DeliverableProjectOutcome deliverableOutcome = deliverableProjectOutcomeDAO.save(deliverableProjectOutcome);
    Phase phase = phaseDAO.find(deliverableOutcome.getPhase().getId());
    if (phase.getDescription().equals(APConstants.PLANNING) && phase.getNext() != null) {
      this.saveDeliverableProjectOutcomePhase(deliverableOutcome.getPhase().getNext(),
        deliverableOutcome.getDeliverable().getId(), deliverableProjectOutcome);
    }
    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (phase.getNext() != null && phase.getNext().getNext() != null) {
        Phase upkeepPhase = phase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveDeliverableProjectOutcomePhase(upkeepPhase, deliverableOutcome.getDeliverable().getId(),
            deliverableProjectOutcome);
        }
      }
    }
    return deliverableOutcome;
  }

  public void saveDeliverableProjectOutcomePhase(Phase next, long deliverableId,
    DeliverableProjectOutcome deliverableProjectOutcome) {
    Phase phase = phaseDAO.find(next.getId());

    List<DeliverableProjectOutcome> deliverableOutcomes = deliverableProjectOutcomeDAO.findAll().stream()
      .filter(c -> c.getDeliverable().getId().equals(deliverableId) && c.getPhase().getId().equals(phase.getId())
        && c.getProjectOutcome().getId().equals(deliverableProjectOutcome.getProjectOutcome().getId()))
      .collect(Collectors.toList());

    // Get project outcomes phases
    Deliverable deliverable = deliverableManager.getDeliverableById(deliverableId);
    Project project = deliverable.getProject();

    List<ProjectOutcome> projectOutcomes = phase.getProjectOutcomes().stream()
      .filter(c -> c.isActive() && c.getProject().getId().longValue() == project.getId()
        && c.getCrpProgramOutcome().getComposeID()
          .equals(deliverableProjectOutcome.getProjectOutcome().getCrpProgramOutcome().getComposeID()))
      .collect(Collectors.toList());

    if (projectOutcomes != null && !projectOutcomes.isEmpty() && projectOutcomes.get(0) != null) {
      if (deliverableOutcomes.isEmpty()) {
        DeliverableProjectOutcome deliverableProjectOutcomeAdd = new DeliverableProjectOutcome();
        deliverableProjectOutcomeAdd.setDeliverable(deliverableProjectOutcome.getDeliverable());
        deliverableProjectOutcomeAdd.setPhase(phase);
        deliverableProjectOutcomeAdd.setProjectOutcome(projectOutcomes.get(0));
        deliverableProjectOutcomeDAO.save(deliverableProjectOutcomeAdd);
      }
    }

    if (phase.getNext() != null) {
      this.saveDeliverableProjectOutcomePhase(phase.getNext(), deliverableId, deliverableProjectOutcome);
    }
  }
}
