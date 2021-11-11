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

import org.cgiar.ccafs.marlo.data.dao.DeliverableParticipantDAO;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableParticipant;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

@Named
public class DeliverableParticipantMySQLDAO extends AbstractMarloDAO<DeliverableParticipant, Long>
  implements DeliverableParticipantDAO {


  @Inject
  public DeliverableParticipantMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteDeliverableParticipant(long deliverableParticipantId) {
    DeliverableParticipant deliverableParticipant = this.find(deliverableParticipantId);
    deliverableParticipant.setActive(false);
    this.update(deliverableParticipant);
  }

  @Override
  public boolean existDeliverableParticipant(long deliverableParticipantID) {
    DeliverableParticipant deliverableParticipant = this.find(deliverableParticipantID);
    if (deliverableParticipant == null) {
      return false;
    }
    return true;

  }

  @Override
  public DeliverableParticipant find(long id) {
    return super.find(DeliverableParticipant.class, id);

  }

  @Override
  public List<DeliverableParticipant> findAll() {
    String query = "from " + DeliverableParticipant.class.getName() + " where is_active=1";
    List<DeliverableParticipant> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<DeliverableParticipant> findDeliverableParticipantByDeliverableAndPhase(Long deliverableID,
    Long phaseID) {
    String query = "from " + DeliverableParticipant.class.getName() + " where deliverable_id =" + deliverableID
      + " and phase_id = " + phaseID + " and is_active=1";
    List<DeliverableParticipant> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public DeliverableParticipant findDeliverableParticipantByPhaseAndDeliverable(Phase phase, Deliverable deliverable) {
    String query = "select distinct dp from DeliverableParticipant dp "
      + " where phase.id = :phaseId and deliverable.id= :deliverableId and dp.active = 1";
    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("phaseId", phase.getId());
    createQuery.setParameter("deliverableId", deliverable.getId());

    Object findSingleResult = super.findSingleResult(DeliverableParticipant.class, createQuery);

    DeliverableParticipant deliverableParticipantResult = (DeliverableParticipant) findSingleResult;

    return deliverableParticipantResult;
  }

  @Override
  public List<DeliverableParticipant> getDeliverableParticipantByPhase(Phase phase) {
    StringBuilder query = new StringBuilder();
    query.append("SELECT DISTINCT  ");
    query.append("dp.id as id ");
    query.append("FROM ");
    query.append("deliverable_participants AS dp ");
    query.append("INNER JOIN deliverables AS d ON d.id = dp.deliverable_id ");
    query.append("INNER JOIN deliverables_info AS di ON d.id = di.deliverable_id ");
    query.append("WHERE dp.is_active = 1 AND ");
    query.append("dp.has_participants = 1 AND ");
    query.append("dp.`phase_id` =" + phase.getId() + " AND ");
    query.append("d.is_active = 1 AND ");
    query.append("di.is_active = 1 AND ");
    query.append("di.`id_phase` =" + phase.getId());

    List<Map<String, Object>> rList = super.findCustomQuery(query.toString());
    List<DeliverableParticipant> deliverableParticipants = new ArrayList<>();

    if (rList != null) {
      for (Map<String, Object> map : rList) {
        DeliverableParticipant deliverableParticipant = this.find(Long.parseLong(map.get("id").toString()));
        deliverableParticipants.add(deliverableParticipant);
      }
    }

    return deliverableParticipants.stream()
      .sorted((p1, p2) -> p1.getEventActivityName().compareTo(p2.getEventActivityName())).collect(Collectors.toList());
  }

  @Override
  public List<DeliverableParticipant> getDeliverableParticipantByPhaseAndProject(Phase phase, long projectID) {
    StringBuilder query = new StringBuilder();
    query.append("SELECT DISTINCT  ");
    query.append("dp.id as id ");
    query.append("FROM ");
    query.append("deliverable_participants AS dp ");
    query.append("INNER JOIN deliverables AS d ON d.id = dp.deliverable_id ");
    query.append("INNER JOIN deliverables_info AS di ON d.id = di.deliverable_id ");
    query.append("WHERE dp.is_active = 1 AND ");
    query.append("dp.has_participants = 1 AND ");
    query.append("dp.`phase_id` =" + phase.getId() + " AND ");
    query.append("d.is_active = 1 AND ");
    query.append("d.project_id = " + projectID + " AND ");
    query.append("di.is_active = 1 AND ");
    query.append("di.`id_phase` =" + phase.getId());

    List<Map<String, Object>> rList = super.findCustomQuery(query.toString());
    List<DeliverableParticipant> deliverableParticipants = new ArrayList<>();

    if (rList != null) {
      for (Map<String, Object> map : rList) {
        DeliverableParticipant deliverableParticipant = this.find(Long.parseLong(map.get("id").toString()));
        deliverableParticipants.add(deliverableParticipant);
      }
    }

    return deliverableParticipants.stream()
      .sorted((p1, p2) -> p1.getEventActivityName().compareTo(p2.getEventActivityName())).collect(Collectors.toList());
  }

  @Override
  public DeliverableParticipant save(DeliverableParticipant deliverableParticipant) {
    if (deliverableParticipant.getId() == null) {
      super.saveEntity(deliverableParticipant);
    } else {
      deliverableParticipant = super.update(deliverableParticipant);
    }


    return deliverableParticipant;
  }


}