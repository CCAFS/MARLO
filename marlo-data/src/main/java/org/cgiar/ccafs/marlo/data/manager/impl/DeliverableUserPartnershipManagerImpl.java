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
import org.cgiar.ccafs.marlo.data.dao.DeliverableUserPartnershipDAO;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableUserPartnershipManager;
import org.cgiar.ccafs.marlo.data.model.DeliverableUserPartnership;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class DeliverableUserPartnershipManagerImpl implements DeliverableUserPartnershipManager {

  private PhaseDAO phaseDAO;
  private DeliverableUserPartnershipDAO deliverableUserPartnershipDAO;

  // Managers


  @Inject
  public DeliverableUserPartnershipManagerImpl(DeliverableUserPartnershipDAO deliverableUserPartnershipDAO,
    PhaseDAO phaseDAO) {
    this.deliverableUserPartnershipDAO = deliverableUserPartnershipDAO;
    this.phaseDAO = phaseDAO;
  }

  @Override
  public void deleteDeliverableUserPartnership(long deliverableUserPartnershipId) {
    DeliverableUserPartnership deliverableUserPartnership =
      this.getDeliverableUserPartnershipById(deliverableUserPartnershipId);
    if (!deliverableUserPartnership.getPhase().getDescription().equals(APConstants.REPORTING)) {
      if (deliverableUserPartnership.getPhase().getNext() != null) {
        this.deleteDeliverableUserPartnershipPhase(deliverableUserPartnership.getPhase().getNext(),
          deliverableUserPartnership.getDeliverable().getId(), deliverableUserPartnership);
      }
    }
    deliverableUserPartnershipDAO.deleteDeliverableUserPartnership(deliverableUserPartnershipId);
  }


  public void deleteDeliverableUserPartnershipPhase(Phase next, long deliverableID,
    DeliverableUserPartnership deliverableUserPartnership) {
    Phase phase = phaseDAO.find(next.getId());

    List<DeliverableUserPartnership> deliverableUserPartnerships =
      phase.getDeliverableUserPartnerships().stream()
        .filter(c -> c.isActive() && c.getDeliverable().getId().longValue() == deliverableID
          && c.getInstitution().getId().equals(deliverableUserPartnership.getInstitution().getId())
          && c.getUser().getId().equals(deliverableUserPartnership.getUser().getId()) && c.getDeliverablePartnerType()
            .getId().equals(deliverableUserPartnership.getDeliverablePartnerType().getId()))
        .collect(Collectors.toList());

    for (DeliverableUserPartnership deliverableUserPartnershipDB : deliverableUserPartnerships) {
      deliverableUserPartnershipDAO.deleteDeliverableUserPartnership(deliverableUserPartnershipDB.getId());
    }

    if (phase.getNext() != null) {
      this.deleteDeliverableUserPartnershipPhase(phase.getNext(), deliverableID, deliverableUserPartnership);
    }
  }

  @Override
  public boolean existDeliverableUserPartnership(long deliverableUserPartnershipID) {

    return deliverableUserPartnershipDAO.existDeliverableUserPartnership(deliverableUserPartnershipID);
  }

  @Override
  public List<DeliverableUserPartnership> findAll() {

    return deliverableUserPartnershipDAO.findAll();

  }

  @Override
  public DeliverableUserPartnership getDeliverableUserPartnershipById(long deliverableUserPartnershipID) {
    return deliverableUserPartnershipDAO.find(deliverableUserPartnershipID);
  }

  @Override
  public DeliverableUserPartnership
    saveDeliverableUserPartnership(DeliverableUserPartnership deliverableUserPartnership) {


    DeliverableUserPartnership userPartnership = deliverableUserPartnershipDAO.save(deliverableUserPartnership);

    Phase phase = phaseDAO.find(userPartnership.getPhase().getId());
    if (!phase.getDescription().equals(APConstants.REPORTING)) {
      if (userPartnership.getPhase().getNext() != null) {
        this.saveDeliverableUserPartnershipPhase(deliverableUserPartnership.getPhase().getNext(),
          deliverableUserPartnership.getDeliverable().getId(), deliverableUserPartnership);
      }
    }
    return userPartnership;

  }

  public void saveDeliverableUserPartnershipPhase(Phase next, long deliverableID,
    DeliverableUserPartnership deliverableUserPartnership) {

    Phase phase = phaseDAO.find(next.getId());

    List<DeliverableUserPartnership> deliverableUserPartnerships =
      phase.getDeliverableUserPartnerships().stream()
        .filter(c -> c.isActive() && c.getDeliverable().getId().longValue() == deliverableID
          && c.getInstitution().getId().equals(deliverableUserPartnership.getInstitution().getId())
          && c.getUser().getId().equals(deliverableUserPartnership.getUser().getId()) && c.getDeliverablePartnerType()
            .getId().equals(deliverableUserPartnership.getDeliverablePartnerType().getId()))
        .collect(Collectors.toList());


    if (deliverableUserPartnerships.isEmpty()) {
      DeliverableUserPartnership deliverableUserPartnershipAdd = new DeliverableUserPartnership();

      deliverableUserPartnershipAdd.setDeliverable(deliverableUserPartnership.getDeliverable());
      deliverableUserPartnershipAdd.setInstitution(deliverableUserPartnership.getInstitution());
      deliverableUserPartnershipAdd.setUser(deliverableUserPartnership.getUser());
      deliverableUserPartnershipAdd.setDeliverablePartnerType(deliverableUserPartnership.getDeliverablePartnerType());
      deliverableUserPartnershipAdd.setPartnerDivision(deliverableUserPartnership.getPartnerDivision());
      deliverableUserPartnershipAdd.setActive(true);
      deliverableUserPartnershipAdd.setActiveSince(new Date());

      deliverableUserPartnershipDAO.save(deliverableUserPartnershipAdd);
    }


    if (phase.getNext() != null) {
      this.saveDeliverableUserPartnershipPhase(phase.getNext(), deliverableID, deliverableUserPartnership);
    }
  }


}
