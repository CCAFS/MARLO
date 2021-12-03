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
import org.cgiar.ccafs.marlo.data.dao.DeliverableUserDAO;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableUserManager;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableUser;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian Garcia
 */
@Named
public class DeliverableUserManagerImpl implements DeliverableUserManager {


  private DeliverableUserDAO deliverableUserDAO;
  private PhaseDAO phaseDAO;
  // Managers


  @Inject
  public DeliverableUserManagerImpl(DeliverableUserDAO deliverableUserDAO, PhaseDAO phaseDAO) {
    this.deliverableUserDAO = deliverableUserDAO;
    this.phaseDAO = phaseDAO;
  }

  private DeliverableUser cloneDeliverableUser(DeliverableUser deliverableUser, DeliverableUser newDeliverableUser,
    Phase phase) {
    newDeliverableUser.setDeliverable(deliverableUser.getDeliverable());
    newDeliverableUser.setPhase(phase);
    newDeliverableUser.setFirstName(deliverableUser.getFirstName());
    newDeliverableUser.setLastName(deliverableUser.getLastName());
    newDeliverableUser.setElementId(deliverableUser.getElementId());

    return newDeliverableUser;
  }

  @Override
  public void deleteDeliverableUser(long deliverableUserId) {

    DeliverableUser deliverableUser = this.getDeliverableUserById(deliverableUserId);
    Phase currentPhase = phaseDAO.find(deliverableUser.getPhase().getId());
    boolean isPublication = deliverableUser.getDeliverable().getIsPublication() != null
      && deliverableUser.getDeliverable().getIsPublication();

    if (currentPhase.getDescription().equals(APConstants.REPORTING) && !isPublication) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.deleteDeliverableUserPhase(deliverableUser, upkeepPhase.getId());
        }
      }
    } else {
      // UpKeep
      if (currentPhase.getDescription().equals(APConstants.PLANNING) && currentPhase.getUpkeep() && !isPublication) {
        if (currentPhase.getNext() != null) {
          this.deleteDeliverableUserPhase(deliverableUser, currentPhase.getNext().getId());
        }
      }
    }

    deliverableUserDAO.deleteDeliverableUser(deliverableUserId);
  }

  private void deleteDeliverableUserPhase(DeliverableUser deliverableUser, Long next) {
    Phase phase = phaseDAO.find(next);

    List<DeliverableUser> deliverableUserPhases =
      deliverableUserDAO.findDeliverableUserByPhases(phase, deliverableUser);

    for (DeliverableUser deliverableUserdel : deliverableUserPhases) {
      deliverableUserDAO.deleteDeliverableUser(deliverableUserdel.getId());
    }

    if (phase.getNext() != null) {
      this.deleteDeliverableUserPhase(deliverableUser, phase.getNext().getId());
    }
  }

  @Override
  public boolean existDeliverableUser(long deliverableUserID) {

    return deliverableUserDAO.existDeliverableUser(deliverableUserID);
  }

  @Override
  public List<DeliverableUser> findAll() {

    return deliverableUserDAO.findAll();

  }

  @Override
  public List<DeliverableUser> findAllByPhaseAndDeliverable(Phase phase, Deliverable deliverable) {

    return deliverableUserDAO.findAllByPhaseAndDeliverable(phase, deliverable);
  }

  @Override
  public DeliverableUser getDeliverableUserById(long deliverableUserID) {

    return deliverableUserDAO.find(deliverableUserID);
  }

  @Override
  public DeliverableUser saveDeliverableUser(DeliverableUser deliverableUser) {
    DeliverableUser deliverableUserResult = deliverableUserDAO.save(deliverableUser);
    Phase currentPhase = phaseDAO.find(deliverableUserResult.getPhase().getId());
    boolean isPublication = deliverableUserResult.getDeliverable().getIsPublication() != null
      && deliverableUserResult.getDeliverable().getIsPublication();

    if (currentPhase.getDescription().equals(APConstants.REPORTING) && !isPublication) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveDeliverableUserPhase(deliverableUserResult, upkeepPhase.getId(),
            deliverableUserResult.getDeliverable().getId());
        }
      }
    } else {
      // UpKeep
      if (currentPhase.getDescription().equals(APConstants.PLANNING) && currentPhase.getUpkeep() && !isPublication) {
        if (currentPhase.getNext() != null) {
          this.saveDeliverableUserPhase(deliverableUserResult, currentPhase.getNext().getId(),
            deliverableUserResult.getId());
        }
      }
    }

    return deliverableUserResult;
  }

  private void saveDeliverableUserPhase(DeliverableUser deliverableUserResult, Long phaseID, Long deliverableID) {
    Phase phase = phaseDAO.find(phaseID);


    List<DeliverableUser> deliverableUserPhases =
      deliverableUserDAO.findDeliverableUserByPhases(phase, deliverableUserResult);


    if (deliverableUserPhases.isEmpty()) {
      DeliverableUser newDeliverableUser = new DeliverableUser();
      newDeliverableUser = this.cloneDeliverableUser(deliverableUserResult, newDeliverableUser, phase);
      deliverableUserDAO.save(newDeliverableUser);
    } else {
      for (DeliverableUser deliverableUserDel : deliverableUserPhases) {
        try {
          deliverableUserDAO.deleteDeliverableUser(deliverableUserDel.getId());
        } catch (Exception e) {
          // TODO: handle exception
        }
      }
      DeliverableUser newDeliverableUser = new DeliverableUser();
      newDeliverableUser = this.cloneDeliverableUser(deliverableUserResult, newDeliverableUser, phase);
      deliverableUserDAO.save(newDeliverableUser);

    }

    if (phase.getNext() != null) {
      this.saveDeliverableUserPhase(deliverableUserResult, phase.getNext().getId(),
        deliverableUserResult.getDeliverable().getId());
    }
  }
}
