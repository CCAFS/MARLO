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
import org.cgiar.ccafs.marlo.data.dao.DeliverableUserPartnershipPersonDAO;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableUserPartnershipManager;
import org.cgiar.ccafs.marlo.data.model.DeliverableUserPartnership;
import org.cgiar.ccafs.marlo.data.model.DeliverableUserPartnershipPerson;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.ArrayList;
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
  private DeliverableUserPartnershipPersonDAO deliverableUserPartnershipPersonDAO;

  // Managers


  @Inject
  public DeliverableUserPartnershipManagerImpl(DeliverableUserPartnershipDAO deliverableUserPartnershipDAO,
    PhaseDAO phaseDAO, DeliverableUserPartnershipPersonDAO deliverableUserPartnershipPersonDAO) {
    this.deliverableUserPartnershipDAO = deliverableUserPartnershipDAO;
    this.phaseDAO = phaseDAO;
    this.deliverableUserPartnershipPersonDAO = deliverableUserPartnershipPersonDAO;
  }


  public void addPersons(DeliverableUserPartnership deliverableUserPartnership, Long newDeliverableUserPartnershipId) {

    if (deliverableUserPartnership.getPartnershipPersons() != null) {

      for (DeliverableUserPartnershipPerson deliverableUserPartnershipPerson : deliverableUserPartnership
        .getPartnershipPersons()) {
        if (deliverableUserPartnershipPerson.getUser() != null
          && deliverableUserPartnershipPerson.getUser().getId() != null) {
          DeliverableUserPartnershipPerson deliverableUserPartnershipPersonSave =
            new DeliverableUserPartnershipPerson();
          deliverableUserPartnershipPersonSave
            .setDeliverableUserPartnership(this.getDeliverableUserPartnershipById(newDeliverableUserPartnershipId));
          deliverableUserPartnershipPersonSave.setUser(deliverableUserPartnershipPerson.getUser());

          deliverableUserPartnershipPersonDAO.save(deliverableUserPartnershipPersonSave);
        }
      }
    }

  }

  @Override
  public DeliverableUserPartnership copyDeliverableUserPartnership(DeliverableUserPartnership deliverablePartnership,
    Phase phase) {
    DeliverableUserPartnership deliverablePartnershipAdd = new DeliverableUserPartnership();
    deliverablePartnershipAdd.setPhase(phase);
    deliverablePartnershipAdd.setDeliverable(deliverablePartnership.getDeliverable());
    deliverablePartnershipAdd.setDeliverablePartnerType(deliverablePartnership.getDeliverablePartnerType());

    if (deliverablePartnership.getDeliverableUserPartnershipPersons() != null) {

      deliverablePartnershipAdd = deliverableUserPartnershipDAO.save(deliverablePartnershipAdd);

      List<DeliverableUserPartnershipPerson> persons = new ArrayList<>(deliverablePartnership
        .getDeliverableUserPartnershipPersons().stream().filter(dp -> dp.isActive()).collect(Collectors.toList()));
      for (DeliverableUserPartnershipPerson deliverableUserPartnershipPerson : persons) {
        DeliverableUserPartnershipPerson deliverableUserPartnershipPersonNew = new DeliverableUserPartnershipPerson();
        deliverableUserPartnershipPersonNew.setUser(deliverableUserPartnershipPerson.getUser());
        deliverableUserPartnershipPersonNew.setDeliverableUserPartnership(deliverablePartnershipAdd);
        deliverableUserPartnershipPersonDAO.save(deliverableUserPartnershipPersonNew);
      }

      return deliverablePartnershipAdd;


    }
    return null;

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
          && c.getInstitution().getId().equals(deliverableUserPartnership.getInstitution().getId()) && c
            .getDeliverablePartnerType().getId().equals(deliverableUserPartnership.getDeliverablePartnerType().getId()))
        .collect(Collectors.toList());

    for (DeliverableUserPartnership deliverableUserPartnershipDB : deliverableUserPartnerships) {

      if (deliverableUserPartnershipDB.getDeliverableUserPartnershipPersons() != null) {
        List<DeliverableUserPartnershipPerson> deliverableUserPartnershipPersons =
          new ArrayList<>(deliverableUserPartnershipDB.getDeliverableUserPartnershipPersons().stream()
            .filter(dup -> dup.isActive()).collect(Collectors.toList()));
        for (DeliverableUserPartnershipPerson deliverableUserPartnershipPerson : deliverableUserPartnershipPersons) {
          deliverableUserPartnershipPersonDAO
            .deleteDeliverableUserPartnershipPerson(deliverableUserPartnershipPerson.getId());
        }
      }

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
          && c.getInstitution().getId().equals(deliverableUserPartnership.getInstitution().getId()) && c
            .getDeliverablePartnerType().getId().equals(deliverableUserPartnership.getDeliverablePartnerType().getId()))
        .collect(Collectors.toList());


    if (deliverableUserPartnerships.isEmpty()) {
      DeliverableUserPartnership deliverableUserPartnershipAdd = new DeliverableUserPartnership();

      deliverableUserPartnershipAdd.setDeliverable(deliverableUserPartnership.getDeliverable());
      deliverableUserPartnershipAdd.setInstitution(deliverableUserPartnership.getInstitution());
      deliverableUserPartnershipAdd.setDeliverablePartnerType(deliverableUserPartnership.getDeliverablePartnerType());
      deliverableUserPartnershipAdd.setActive(true);
      deliverableUserPartnershipAdd.setActiveSince(new Date());

      deliverableUserPartnershipAdd = deliverableUserPartnershipDAO.save(deliverableUserPartnershipAdd);

      if (deliverableUserPartnershipAdd.getId() != null) {
        this.addPersons(deliverableUserPartnership, deliverableUserPartnershipAdd.getId());
      }
    } else {
      this.updatePersons(deliverableUserPartnerships.get(0), deliverableUserPartnership,
        deliverableUserPartnerships.get(0).getDeliverablePartnerType().getId());
    }


    if (phase.getNext() != null) {
      this.saveDeliverableUserPartnershipPhase(phase.getNext(), deliverableID, deliverableUserPartnership);
    }
  }

  public void updatePersons(DeliverableUserPartnership deliverableUserPartnershipUp,
    DeliverableUserPartnership deliverableUserPartnership, Long deliverablePartnerType) {

    List<DeliverableUserPartnershipPerson> deliverableUserPartnershipPersonsPrev =
      new ArrayList<DeliverableUserPartnershipPerson>(deliverableUserPartnershipUp
        .getDeliverableUserPartnershipPersons().stream().filter(dup -> dup.isActive()).collect(Collectors.toList()));

    for (DeliverableUserPartnershipPerson deliverableUserPartnershipPerson : deliverableUserPartnershipPersonsPrev) {
      if (deliverableUserPartnership.getPartnershipPersons() == null
        || deliverableUserPartnership.getPartnershipPersons().stream()
          .filter(c -> deliverableUserPartnershipPerson.getUser() != null
            && deliverableUserPartnershipPerson.getUser().getId() != null && c.getUser() != null
            && c.getUser().getId() != null
            && c.getUser().getId().equals(deliverableUserPartnershipPerson.getUser().getId()))
          .collect(Collectors.toList()).isEmpty()) {
        deliverableUserPartnershipPersonDAO
          .deleteDeliverableUserPartnershipPerson(deliverableUserPartnershipPerson.getId());
      }


    }

    // This process is for Other Partnership Delete function
    if (deliverableUserPartnership.getPartnershipPersons() != null) {
      for (DeliverableUserPartnershipPerson deliverableUserPartnershipPersonDel : deliverableUserPartnership
        .getPartnershipPersons()) {
        if (deliverableUserPartnershipPersonDel.getId() != null) {
          if (deliverableUserPartnershipPersonDel.getUser() == null
            || deliverableUserPartnershipPersonDel.getUser().getId() == null) {
            deliverableUserPartnershipPersonDAO
              .deleteDeliverableUserPartnershipPerson(deliverableUserPartnershipPersonDel.getId());
          }
        }
      }
    }

    if (deliverableUserPartnership.getPartnershipPersons() != null) {
      if (deliverablePartnerType.equals(APConstants.DELIVERABLE_PARTNERSHIP_TYPE_RESPONSIBLE)) {
        for (DeliverableUserPartnershipPerson deliverableUserPartnershipPerson : deliverableUserPartnership
          .getPartnershipPersons()) {

          List<DeliverableUserPartnershipPerson> deliverableUserPartnershipPersons =
            deliverableUserPartnershipPersonsPrev.stream()
              .filter(dup -> deliverableUserPartnershipPerson.getUser() != null
                && deliverableUserPartnershipPerson.getUser().getId() != null && dup.getUser() != null
                && dup.getUser().getId() != null
                && dup.getUser().getId().equals(deliverableUserPartnershipPerson.getUser().getId()))
              .collect(Collectors.toList());

          if (deliverableUserPartnershipPersons == null || deliverableUserPartnershipPersons.isEmpty()) {

            if (deliverableUserPartnershipPerson.getUser() != null
              && deliverableUserPartnershipPerson.getUser().getId() != null) {
              DeliverableUserPartnershipPerson deliverableUserPartnershipPersonSave =
                new DeliverableUserPartnershipPerson();

              deliverableUserPartnershipPersonSave.setUser(deliverableUserPartnershipPerson.getUser());
              deliverableUserPartnershipPersonSave.setDeliverableUserPartnership(deliverableUserPartnershipUp);

              deliverableUserPartnershipPersonDAO.save(deliverableUserPartnershipPersonSave);
            }

          }

        }
      } else {

        // This process is for Other Partnership Delete function
        for (DeliverableUserPartnershipPerson deliverableUserPartnershipPerson : deliverableUserPartnership
          .getPartnershipPersons()) {

          if (deliverableUserPartnershipPerson.getId() == null) {
            if (deliverableUserPartnershipPerson.getUser() != null
              && deliverableUserPartnershipPerson.getUser().getId() != null) {
              DeliverableUserPartnershipPerson deliverableUserPartnershipPersonSave =
                new DeliverableUserPartnershipPerson();

              deliverableUserPartnershipPersonSave.setUser(deliverableUserPartnershipPerson.getUser());
              deliverableUserPartnershipPersonSave.setDeliverableUserPartnership(deliverableUserPartnershipUp);

              deliverableUserPartnershipPersonDAO.save(deliverableUserPartnershipPersonSave);
            }
          }

        }
      }
    }

  }


}
