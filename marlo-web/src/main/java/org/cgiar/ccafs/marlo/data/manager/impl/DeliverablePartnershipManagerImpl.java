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
import org.cgiar.ccafs.marlo.data.dao.DeliverablePartnershipDAO;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectPartnerDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectPartnerPersonDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverablePartnershipManager;
import org.cgiar.ccafs.marlo.data.model.DeliverablePartnership;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian Garcia
 */
@Named
public class DeliverablePartnershipManagerImpl implements DeliverablePartnershipManager {


  private DeliverablePartnershipDAO deliverablePartnershipDAO;
  private PhaseDAO phaseDAO;
  private ProjectPartnerPersonDAO partnerPersonDao;
  private ProjectPartnerDAO projectPartnerDao;

  // Managers


  @Inject
  public DeliverablePartnershipManagerImpl(DeliverablePartnershipDAO deliverablePartnershipDAO, PhaseDAO phaseDAO,
    ProjectPartnerPersonDAO partnerPersonDao, ProjectPartnerDAO projectPartnerDao) {
    this.deliverablePartnershipDAO = deliverablePartnershipDAO;
    this.phaseDAO = phaseDAO;
    this.partnerPersonDao = partnerPersonDao;
    this.projectPartnerDao = projectPartnerDao;

  }

  /**
   * clone or update the deliverable Partnership for next phases
   * 
   * @param next the next phase to clone
   * @param deliverableID the deliverable id we are working
   * @param deliverablePartnership the deliverable Partnership to clone
   */
  private void addDeliverablePartnershipPhase(Phase next, long deliverableID,
    DeliverablePartnership deliverablePartnership) {
    Phase phase = phaseDAO.find(next.getId());

    ProjectPartner projectPartner = this.getProjectPartner(phase, deliverablePartnership.getProjectPartner());
    Long projectPartnerId = null;
    if (projectPartner != null) {
      projectPartnerId = projectPartner.getId();
    }
    ProjectPartnerPerson projectPartnerPerson =
      this.getPartnerPerson(phase, deliverablePartnership.getProjectPartnerPerson());
    Long projectPartnerPersonId = null;
    if (projectPartnerPerson != null) {
      projectPartnerPersonId = projectPartnerPerson.getId();
    }
    Long partnerDivisionId = null;
    if (deliverablePartnership.getPartnerDivision() != null
      && deliverablePartnership.getPartnerDivision().getId() != -1) {
      partnerDivisionId = deliverablePartnership.getPartnerDivision().getId();
    }

    String partnerType = null;
    if (deliverablePartnership.getPartnerType() != null) {
      partnerType = deliverablePartnership.getPartnerType();
    }

    List<DeliverablePartnership> deliverablePartnerships =
      deliverablePartnershipDAO.findByDeliverablePhasePartnerAndPartnerperson(deliverableID, phase.getId(),
        projectPartnerId, projectPartnerPersonId, partnerDivisionId, partnerType);

    if (deliverablePartnerships.isEmpty()) {
      DeliverablePartnership deliverablePartnershipAdd = new DeliverablePartnership();
      deliverablePartnershipAdd.setActive(true);
      deliverablePartnershipAdd.setActiveSince(deliverablePartnership.getActiveSince());
      deliverablePartnershipAdd.setCreatedBy(deliverablePartnership.getCreatedBy());
      deliverablePartnershipAdd.setModificationJustification(deliverablePartnership.getModificationJustification());
      deliverablePartnershipAdd.setModifiedBy(deliverablePartnership.getModifiedBy());
      deliverablePartnershipAdd.setPhase(phase);
      deliverablePartnershipAdd.setDeliverable(deliverablePartnership.getDeliverable());
      deliverablePartnershipAdd.setPartnerType(deliverablePartnership.getPartnerType());
      deliverablePartnershipAdd.setPartnerDivision(deliverablePartnership.getPartnerDivision());
      if (deliverablePartnership.getProjectPartner() != null) {
        deliverablePartnershipAdd.setProjectPartner(projectPartner);
      }
      if (deliverablePartnership.getProjectPartnerPerson() != null) {
        deliverablePartnershipAdd.setProjectPartnerPerson(projectPartnerPerson);
      }
      deliverablePartnershipDAO.save(deliverablePartnershipAdd);
    }

    if (phase.getNext() != null) {
      this.addDeliverablePartnershipPhase(phase.getNext(), deliverableID, deliverablePartnership);
    }


  }

  @Override
  public DeliverablePartnership copyDeliverablePartnership(DeliverablePartnership deliverablePartnership, Phase phase) {
    DeliverablePartnership deliverablePartnershipAdd = new DeliverablePartnership();
    deliverablePartnershipAdd.setActive(true);
    deliverablePartnershipAdd.setActiveSince(deliverablePartnership.getActiveSince());
    deliverablePartnershipAdd.setCreatedBy(deliverablePartnership.getCreatedBy());
    deliverablePartnershipAdd.setModificationJustification(deliverablePartnership.getModificationJustification());
    deliverablePartnershipAdd.setModifiedBy(deliverablePartnership.getModifiedBy());
    deliverablePartnershipAdd.setPhase(phase);
    deliverablePartnershipAdd.setDeliverable(deliverablePartnership.getDeliverable());
    deliverablePartnershipAdd.setPartnerType(deliverablePartnership.getPartnerType());
    deliverablePartnershipAdd.setPartnerDivision(deliverablePartnership.getPartnerDivision());
    deliverablePartnershipAdd
      .setProjectPartnerPerson(this.getPartnerPerson(phase, deliverablePartnership.getProjectPartnerPerson()));
    if (deliverablePartnershipAdd.getProjectPartnerPerson() != null) {
      deliverablePartnershipDAO.save(deliverablePartnershipAdd);
      return deliverablePartnershipAdd;
    }
    return null;
  }

  @Override
  public void deleteDeliverablePartnership(long deliverablePartnershipId) {


    DeliverablePartnership deliverablePartnership = this.getDeliverablePartnershipById(deliverablePartnershipId);
    Phase currentPhase = phaseDAO.find(deliverablePartnership.getPhase().getId());
    deliverablePartnership.setActive(false);
    deliverablePartnershipDAO.save(deliverablePartnership);
    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (deliverablePartnership.getPhase().getNext() != null) {
        this.deleteDeliverablePartnership(deliverablePartnership.getPhase().getNext(),
          deliverablePartnership.getDeliverable().getId(), deliverablePartnership);
      }
    }
  }

  public void deleteDeliverablePartnership(Phase next, long deliverableID,
    DeliverablePartnership deliverablePartnership) {
    Phase phase = phaseDAO.find(next.getId());
    ProjectPartner projectPartner = this.getProjectPartner(phase, deliverablePartnership.getProjectPartner());
    Long projectPartnerId = null;
    if (projectPartner != null) {
      projectPartnerId = projectPartner.getId();
    }
    ProjectPartnerPerson projectPartnerPerson =
      this.getPartnerPerson(phase, deliverablePartnership.getProjectPartnerPerson());
    Long projectPartnerPersonId = null;
    if (projectPartnerPerson != null) {
      projectPartnerPersonId = projectPartnerPerson.getId();
    }
    Long partnerDivisionId = null;
    if (deliverablePartnership.getPartnerDivision() != null
      && deliverablePartnership.getPartnerDivision().getId() != -1) {
      partnerDivisionId = deliverablePartnership.getPartnerDivision().getId();
    }

    String partnerType = null;
    if (deliverablePartnership.getPartnerType() != null) {
      partnerType = deliverablePartnership.getPartnerType();
    }

    List<DeliverablePartnership> deliverablePartnerships =
      deliverablePartnershipDAO.findByDeliverablePhasePartnerAndPartnerperson(deliverableID, phase.getId(),
        projectPartnerId, projectPartnerPersonId, partnerDivisionId, partnerType);

    for (DeliverablePartnership dePartnership : deliverablePartnerships) {
      dePartnership.setActive(false);
      deliverablePartnershipDAO.save(dePartnership);
    }

    if (phase.getNext() != null) {
      this.deleteDeliverablePartnership(phase.getNext(), deliverableID, deliverablePartnership);
    }
  }

  @Override
  public boolean existDeliverablePartnership(long deliverablePartnershipID) {
    return deliverablePartnershipDAO.existDeliverablePartnership(deliverablePartnershipID);
  }

  @Override
  public List<DeliverablePartnership> findAll() {
    return deliverablePartnershipDAO.findAll();
  }

  @Override
  public List<DeliverablePartnership> findByDeliverablePhasePartnerAndPartnerperson(long deliverableID, Long phase,
    Long projectPartnerId, Long projectPartnerPersonId, Long partnerDivisionId, String partnerType) {
    return deliverablePartnershipDAO.findByDeliverablePhasePartnerAndPartnerperson(deliverableID, phase,
      projectPartnerId, projectPartnerPersonId, partnerDivisionId, partnerType);
  }

  @Override
  public List<DeliverablePartnership> findForDeliverableIdAndPartnerTypeOther(long deliverableId) {
    return deliverablePartnershipDAO.findForDeliverableIdAndPartnerTypeOther(deliverableId);
  }

  @Override
  public List<DeliverablePartnership> findForDeliverableIdAndProjectPersonIdPartnerTypeOther(long deliverableId,
    long projectPersonId) {
    return deliverablePartnershipDAO.findForDeliverableIdAndProjectPersonIdPartnerTypeOther(deliverableId,
      projectPersonId);
  }

  @Override
  public DeliverablePartnership getDeliverablePartnershipById(long deliverablePartnershipID) {
    return deliverablePartnershipDAO.find(deliverablePartnershipID);
  }

  private ProjectPartnerPerson getPartnerPerson(Phase phase, ProjectPartnerPerson projectPartnerPerson) {
    if (projectPartnerPerson == null || projectPartnerPerson.getId() == null) {
      return null;
    }
    projectPartnerPerson = partnerPersonDao.find(projectPartnerPerson.getId());

    List<Map<String, Object>> result =
      partnerPersonDao.findPartner(projectPartnerPerson.getProjectPartner().getInstitution().getId(), phase.getId(),
        projectPartnerPerson.getProjectPartner().getProject().getId(), projectPartnerPerson.getUser().getId());
    if (result.size() > 0) {
      Long id = Long.parseLong(result.get(0).get("id").toString());
      return partnerPersonDao.find(id);
    }
    return null;

  }

  private ProjectPartner getProjectPartner(Phase phase, ProjectPartner projectPartner) {
    if (projectPartner.getId() != null) {
      projectPartner = projectPartnerDao.find(projectPartner.getId());
      return projectPartnerDao.getPartnerPhase(phase, projectPartner.getProject(), projectPartner.getInstitution());
    } else {
      return null;
    }
  }

  @Override
  public DeliverablePartnership saveDeliverablePartnership(DeliverablePartnership deliverablePartnership) {

    DeliverablePartnership dePartnership = deliverablePartnershipDAO.save(deliverablePartnership);
    Phase currentPhase = phaseDAO.find(deliverablePartnership.getPhase().getId());
    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (deliverablePartnership.getPhase().getNext() != null) {
        this.addDeliverablePartnershipPhase(deliverablePartnership.getPhase().getNext(),
          deliverablePartnership.getDeliverable().getId(), deliverablePartnership);
      }
    }
    return dePartnership;
  }

  @Override
  public DeliverablePartnership updateDeliverablePartnership(DeliverablePartnership partnershipResponsibleDBUpdated,
    DeliverablePartnership partnershipResponsibleDB) {
    DeliverablePartnership dePartnership = deliverablePartnershipDAO.save(partnershipResponsibleDBUpdated);
    Phase currentPhase = phaseDAO.find(partnershipResponsibleDBUpdated.getPhase().getId());
    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (partnershipResponsibleDBUpdated.getPhase().getNext() != null) {
        this.updateDeliverablePartnershipPhase(partnershipResponsibleDBUpdated.getPhase().getNext(),
          partnershipResponsibleDBUpdated.getDeliverable().getId(), partnershipResponsibleDBUpdated,
          partnershipResponsibleDB);
      }
    }
    return dePartnership;
  }

  private void updateDeliverablePartnershipPhase(Phase next, Long deliverableID,
    DeliverablePartnership partnershipResponsibleDBUpdated, DeliverablePartnership partnershipResponsibleDB) {
    Phase phase = phaseDAO.find(next.getId());

    ProjectPartner projectPartner = this.getProjectPartner(phase, partnershipResponsibleDBUpdated.getProjectPartner());
    Long projectPartnerId = null;
    if (projectPartner != null) {
      projectPartnerId = projectPartner.getId();
    }
    ProjectPartnerPerson projectPartnerPerson =
      this.getPartnerPerson(phase, partnershipResponsibleDBUpdated.getProjectPartnerPerson());
    Long projectPartnerPersonId = null;
    if (projectPartnerPerson != null) {
      projectPartnerPersonId = projectPartnerPerson.getId();
    }
    Long partnerDivisionId = null;
    if (partnershipResponsibleDBUpdated.getPartnerDivision() != null
      && partnershipResponsibleDBUpdated.getPartnerDivision().getId() != -1) {
      partnerDivisionId = partnershipResponsibleDBUpdated.getPartnerDivision().getId();
    }

    String partnerType = null;
    if (partnershipResponsibleDBUpdated.getPartnerType() != null) {
      partnerType = partnershipResponsibleDBUpdated.getPartnerType();
    }

    List<DeliverablePartnership> deliverablePartnershipsUpdated =
      deliverablePartnershipDAO.findByDeliverablePhasePartnerAndPartnerperson(deliverableID, phase.getId(),
        projectPartnerId, projectPartnerPersonId, partnerDivisionId, partnerType);

    ProjectPartner projectPartnerDB = this.getProjectPartner(phase, partnershipResponsibleDB.getProjectPartner());
    Long projectPartnerDBId = null;
    if (projectPartnerDB != null) {
      projectPartnerDBId = projectPartnerDB.getId();
    }
    ProjectPartnerPerson projectPartnerPersonDB =
      this.getPartnerPerson(phase, partnershipResponsibleDB.getProjectPartnerPerson());
    Long projectPartnerPersonDBId = null;
    if (projectPartnerPersonDB != null) {
      projectPartnerPersonDBId = projectPartnerPersonDB.getId();
    }
    Long partnerDivisionDBId = null;
    if (partnershipResponsibleDB.getPartnerDivision() != null
      && partnershipResponsibleDB.getPartnerDivision().getId() != -1) {
      partnerDivisionDBId = partnershipResponsibleDB.getPartnerDivision().getId();
    }
    String partnerTypeDB = null;
    if (partnershipResponsibleDB.getPartnerType() != null) {
      partnerTypeDB = partnershipResponsibleDB.getPartnerType();
    }
    List<DeliverablePartnership> deliverablePartnershipsDB =
      deliverablePartnershipDAO.findByDeliverablePhasePartnerAndPartnerperson(deliverableID, phase.getId(),
        projectPartnerDBId, projectPartnerPersonDBId, partnerDivisionDBId, partnerTypeDB);

    if (deliverablePartnershipsUpdated.isEmpty() && !deliverablePartnershipsDB.isEmpty()) {
      for (DeliverablePartnership deliverablePartnershipDB : deliverablePartnershipsDB) {
        deliverablePartnershipDB.setActive(true);
        deliverablePartnershipDB.setActiveSince(partnershipResponsibleDBUpdated.getActiveSince());
        deliverablePartnershipDB.setCreatedBy(partnershipResponsibleDBUpdated.getCreatedBy());
        deliverablePartnershipDB
          .setModificationJustification(partnershipResponsibleDBUpdated.getModificationJustification());
        deliverablePartnershipDB.setModifiedBy(partnershipResponsibleDBUpdated.getModifiedBy());
        deliverablePartnershipDB.setPhase(phase);
        deliverablePartnershipDB.setDeliverable(partnershipResponsibleDBUpdated.getDeliverable());
        deliverablePartnershipDB.setPartnerType(partnershipResponsibleDBUpdated.getPartnerType());
        deliverablePartnershipDB.setPartnerDivision(partnershipResponsibleDBUpdated.getPartnerDivision());
        if (partnershipResponsibleDBUpdated.getProjectPartner() != null) {
          deliverablePartnershipDB.setProjectPartner(projectPartner);
        }
        if (partnershipResponsibleDBUpdated.getProjectPartnerPerson() != null) {
          deliverablePartnershipDB.setProjectPartnerPerson(projectPartnerPerson);
        }
        deliverablePartnershipDAO.save(partnershipResponsibleDBUpdated);
      }
    }

    if (phase.getNext() != null) {
      this.updateDeliverablePartnershipPhase(phase.getNext(), deliverableID, partnershipResponsibleDBUpdated,
        partnershipResponsibleDB);
    }
  }


}
