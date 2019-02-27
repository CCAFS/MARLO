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
import org.cgiar.ccafs.marlo.data.dao.DeliverableGeographicScopeDAO;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.model.DeliverableGeographicScope;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class DeliverableGeographicScopeManagerImpl implements DeliverableGeographicScopeManager {


  private DeliverableGeographicScopeDAO deliverableGeographicScopeDAO;
  // Managers
  private PhaseDAO phaseDAO;

  @Inject
  public DeliverableGeographicScopeManagerImpl(DeliverableGeographicScopeDAO deliverableGeographicScopeDAO,
    PhaseDAO phaseDAO) {
    this.deliverableGeographicScopeDAO = deliverableGeographicScopeDAO;
    this.phaseDAO = phaseDAO;
  }

  @Override
  public void deleteDeliverableGeographicScope(long deliverableGeographicScopeId) {

    DeliverableGeographicScope deliverableGeographicScope =
      this.getDeliverableGeographicScopeById(deliverableGeographicScopeId);

    if (deliverableGeographicScope.getPhase().getNext() != null) {
      this.deleteDeliverableGeographicScopePhase(deliverableGeographicScope.getPhase().getNext(),
        deliverableGeographicScope.getDeliverable().getId(), deliverableGeographicScope);
    }
    deliverableGeographicScopeDAO.deleteDeliverableGeographicScope(deliverableGeographicScopeId);
  }

  private void deleteDeliverableGeographicScopePhase(Phase next, Long deliverableID,
    DeliverableGeographicScope deliverableGeographicScope) {
    Phase phase = phaseDAO.find(next.getId());

    List<DeliverableGeographicScope> deliverableGeographicScopes = phase.getDeliverableGeographicScopes().stream()
      .filter(c -> c.isActive() && c.getDeliverable().getId().longValue() == deliverableID
        && c.getRepIndGeographicScope().getId().equals(deliverableGeographicScope.getRepIndGeographicScope().getId()))
      .collect(Collectors.toList());
    for (DeliverableGeographicScope deliverableGeographicScopeDB : deliverableGeographicScopes) {
      deliverableGeographicScopeDAO.deleteDeliverableGeographicScope(deliverableGeographicScopeDB.getId());
    }

    if (phase.getNext() != null) {
      this.deleteDeliverableGeographicScopePhase(phase.getNext(), deliverableID, deliverableGeographicScope);
    }
  }

  @Override
  public boolean existDeliverableGeographicScope(long deliverableGeographicScopeID) {

    return deliverableGeographicScopeDAO.existDeliverableGeographicScope(deliverableGeographicScopeID);
  }

  @Override
  public List<DeliverableGeographicScope> findAll() {

    return deliverableGeographicScopeDAO.findAll();

  }

  @Override
  public DeliverableGeographicScope getDeliverableGeographicScopeById(long deliverableGeographicScopeID) {

    return deliverableGeographicScopeDAO.find(deliverableGeographicScopeID);
  }

  private void saveDeliverableGegrapihcScopePhase(Phase next, Long deliverableID,
    DeliverableGeographicScope deliverableGeographicScope) {
    Phase phase = phaseDAO.find(next.getId());

    List<DeliverableGeographicScope> deliverableGeographicScopes = phase.getDeliverableGeographicScopes().stream()
      .filter(c -> c.getDeliverable().getId().longValue() == deliverableID
        && c.getRepIndGeographicScope().getId().equals(deliverableGeographicScope.getRepIndGeographicScope().getId()))
      .collect(Collectors.toList());

    if (deliverableGeographicScopes.isEmpty()) {
      DeliverableGeographicScope deliverableGeographicScopeAdd = new DeliverableGeographicScope();
      deliverableGeographicScopeAdd.setDeliverable(deliverableGeographicScope.getDeliverable());
      deliverableGeographicScopeAdd.setPhase(phase);
      deliverableGeographicScopeAdd.setRepIndGeographicScope(deliverableGeographicScope.getRepIndGeographicScope());
      deliverableGeographicScopeDAO.save(deliverableGeographicScopeAdd);
    }

    if (phase.getNext() != null) {
      this.saveDeliverableGegrapihcScopePhase(phase.getNext(), deliverableID, deliverableGeographicScope);
    }

  }

  @Override
  public DeliverableGeographicScope
    saveDeliverableGeographicScope(DeliverableGeographicScope deliverableGeographicScope) {

    DeliverableGeographicScope scope = deliverableGeographicScopeDAO.save(deliverableGeographicScope);

    if (scope.getPhase().getDescription().equals(APConstants.PLANNING) && scope.getPhase().getNext() != null) {
      if (scope.getPhase().getNext() != null) {
        this.saveDeliverableGegrapihcScopePhase(scope.getPhase().getNext(), scope.getDeliverable().getId(),
          deliverableGeographicScope);
      }
    }

    if (scope.getPhase().getDescription().equals(APConstants.REPORTING)) {
      if (scope.getPhase().getNext() != null && scope.getPhase().getNext().getNext() != null) {
        Phase upkeepPhase = scope.getPhase().getNext().getNext();
        if (upkeepPhase != null) {
          this.saveDeliverableGegrapihcScopePhase(upkeepPhase, scope.getDeliverable().getId(),
            deliverableGeographicScope);
        }
      }
    }

    return scope;
  }


}
