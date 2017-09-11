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


package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.DeliverablePartnershipDAO;
import org.cgiar.ccafs.marlo.data.model.DeliverablePartnership;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.inject.Inject;

public class DeliverablePartnershipMySQLDAO implements DeliverablePartnershipDAO {

  private StandardDAO dao;

  @Inject
  public DeliverablePartnershipMySQLDAO(StandardDAO dao) {
    this.dao = dao;
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
    Phase phase = dao.find(Phase.class, next.getId());

    List<DeliverablePartnership> deliverablePartnerships = phase.getDeliverablePartnerships().stream()
      .filter(c -> c.isActive() && c.getDeliverable().getId().longValue() == deliverableID && deliverablePartnership
        .getProjectPartnerPerson().getUser().getId().equals(c.getProjectPartnerPerson().getUser().getId()))
      .collect(Collectors.toList());
    if (phase.getEditable() != null && phase.getEditable() && deliverablePartnerships.isEmpty()) {
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
        dao.save(deliverablePartnershipAdd);
      }

    }

    if (phase.getNext() != null) {
      this.addDeliverablePartnershipPhase(phase.getNext(), deliverableID, deliverablePartnership);
    }


  }

  @Override
  public boolean deleteDeliverablePartnership(long deliverablePartnershipId) {
    DeliverablePartnership deliverablePartnership = this.find(deliverablePartnershipId);
    deliverablePartnership.setActive(false);
    long result = this.save(deliverablePartnership);

    if (deliverablePartnership.getPhase().getNext() != null) {
      this.deleteDeliverablePartnership(deliverablePartnership.getPhase().getNext(),
        deliverablePartnership.getDeliverable().getId(), deliverablePartnership);
    }
    return result > 0;

  }

  public void deleteDeliverablePartnership(Phase next, long deliverableID,
    DeliverablePartnership deliverablePartnership) {
    Phase phase = dao.find(Phase.class, next.getId());

    if (phase.getEditable() != null && phase.getEditable()) {

      List<DeliverablePartnership> partnerships = phase.getDeliverablePartnerships().stream()
        .filter(c -> c.isActive() && c.getDeliverable().getId().longValue() == deliverableID && deliverablePartnership
          .getProjectPartnerPerson().getUser().getId().equals(c.getProjectPartnerPerson().getUser().getId()))
        .collect(Collectors.toList());

      for (DeliverablePartnership dePartnership : partnerships) {
        this.deleteDeliverablePartnership(dePartnership.getId());
      }
    } else {
      if (phase.getNext() != null) {
        this.deleteDeliverablePartnership(phase.getNext(), deliverableID, deliverablePartnership);
      }
    }


  }

  @Override
  public boolean existDeliverablePartnership(long deliverablePartnershipID) {
    DeliverablePartnership deliverablePartnership = this.find(deliverablePartnershipID);
    if (deliverablePartnership == null) {
      return false;
    }
    return true;

  }

  @Override
  public DeliverablePartnership find(long id) {
    return dao.find(DeliverablePartnership.class, id);

  }

  @Override
  public List<DeliverablePartnership> findAll() {
    String query = "from " + DeliverablePartnership.class.getName() + " where is_active=1";
    List<DeliverablePartnership> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  private ProjectPartnerPerson getPartnerPerson(Phase phase, ProjectPartnerPerson projectPartnerPerson) {
    projectPartnerPerson = dao.find(ProjectPartnerPerson.class, projectPartnerPerson.getId());
    StringBuilder query = new StringBuilder();
    query.append(
      "select ppp.id from project_partners pp INNER JOIN project_partner_persons ppp on ppp.project_partner_id=pp.id");
    query.append(" where pp.is_active=1 and ppp.is_active=1 and  pp.institution_id=");
    query.append(projectPartnerPerson.getProjectPartner().getInstitution());
    query.append(" and pp.id_phase=");
    query.append(phase.getId());
    query.append(" and pp.project_id= ");
    query.append(projectPartnerPerson.getProjectPartner().getProject().getId());
    query.append(" and ppp.user_id= ");
    query.append(projectPartnerPerson.getUser().getId());
    List<Map<String, Object>> result = dao.findCustomQuery(query.toString());
    if (result.size() > 0) {
      Long id = Long.parseLong(result.get(0).get("id").toString());
      return dao.find(ProjectPartnerPerson.class, id);
    }
    return null;

  }

  @Override
  public long save(DeliverablePartnership deliverablePartnership) {
    if (deliverablePartnership.getId() == null) {
      dao.save(deliverablePartnership);
    } else {
      dao.update(deliverablePartnership);
    }

    if (deliverablePartnership.getPhase().getNext() != null) {
      this.addDeliverablePartnershipPhase(deliverablePartnership.getPhase().getNext(),
        deliverablePartnership.getDeliverable().getId(), deliverablePartnership);
    }
    return deliverablePartnership.getId();
  }


}