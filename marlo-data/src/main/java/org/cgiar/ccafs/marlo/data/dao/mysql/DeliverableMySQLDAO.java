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

import org.cgiar.ccafs.marlo.data.dao.DeliverableDAO;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class DeliverableMySQLDAO extends AbstractMarloDAO<Deliverable, Long> implements DeliverableDAO {


  @Inject
  public DeliverableMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteDeliverable(long deliverableId) {
    Deliverable deliverable = this.find(deliverableId);
    deliverable.setActive(false);
    this.save(deliverable);
  }

  @Override
  public boolean existDeliverable(long deliverableID) {
    Deliverable deliverable = this.find(deliverableID);
    if (deliverable == null) {
      return false;
    }
    return true;

  }

  @Override
  public Deliverable find(long id) {
    return super.find(Deliverable.class, id);

  }

  @Override
  public List<Deliverable> findAll() {
    String query = "from " + Deliverable.class.getName() + " where is_active=1";
    List<Deliverable> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<Deliverable> getDeliverablesByParameters(Phase phase, boolean filterPhaseYear, boolean filterParticipants,
    Boolean filterPublications) {
    StringBuilder query = new StringBuilder();
    query.append("SELECT DISTINCT  ");
    query.append("d.id as id ");
    query.append("FROM ");
    query.append("deliverables AS d ");
    query
      .append("INNER JOIN deliverables_info AS di ON d.id = di.deliverable_id AND di.is_active = 1 AND di.`id_phase` = "
        + phase.getId() + " ");
    if (filterPhaseYear) {
      query.append("AND di.status IS NOT NULL ");
      query.append("AND di.`status` != " + Integer.parseInt(ProjectStatusEnum.Cancelled.getStatusId()) + " ");

      query.append("AND (");
      // Extended with equal new expected year
      query.append("(di.`status` = " + Integer.parseInt(ProjectStatusEnum.Extended.getStatusId())
        + " AND di.new_expected_year = " + phase.getYear() + " ) ");
      // Completed with equal new expected year -equal year (if extended doesn't exists)-
      query.append("OR ");
      query.append("(di.`status` = " + Integer.parseInt(ProjectStatusEnum.Complete.getStatusId()) + " AND "
        + "((di.new_expected_year IS NOT NULL AND di.new_expected_year !=-1 AND di.new_expected_year = "
        + phase.getYear() + " ) " + "OR ((di.new_expected_year IS NULL OR di.new_expected_year =-1) AND di.year = "
        + phase.getYear() + " ))) ");
      // Ongoing
      query.append("OR ");
      query.append("(di.`status` = " + Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId()) + " AND di.year = "
        + phase.getYear() + ")");
      query.append(") ");
    }

    if (filterParticipants) {
      query.append(
        "INNER JOIN deliverable_participants AS dp ON d.id = dp.deliverable_id AND dp.has_participants = 1 AND dp.is_active = 1 AND dp.phase_id = "
          + phase.getId() + " ");
    }
    query.append("WHERE d.is_active = 1 ORDER BY d.id");
    if (filterPublications != null) {
      if (filterPublications) {

      } else {

      }
    }

    List<Map<String, Object>> rList = super.findCustomQuery(query.toString());
    List<Deliverable> deliverables = new ArrayList<>();

    if (rList != null) {
      for (Map<String, Object> map : rList) {
        Deliverable deliverable = this.find(Long.parseLong(map.get("id").toString()));
        deliverables.add(deliverable);
      }
    }

    return deliverables;
  }

  @Override
  public List<Deliverable> getDeliverablesByPhase(long phase) {
    StringBuilder query = new StringBuilder();
    query.append("SELECT DISTINCT  ");
    query.append("d.id as id ");
    query.append("FROM ");
    query.append("deliverables AS d ");
    query.append("INNER JOIN deliverables_info AS di ON d.id = di.deliverable_id ");
    query.append(
      "WHERE d.is_active = 1 AND d.project_id IS NOT NULL AND (d.is_publication IS NULL OR d.is_publication = 0) AND ");
    query.append("di.is_active = 1 AND ");
    query.append("di.`id_phase` =" + phase);

    List<Map<String, Object>> rList = super.findCustomQuery(query.toString());
    List<Deliverable> deliverables = new ArrayList<>();

    if (rList != null) {
      for (Map<String, Object> map : rList) {
        Deliverable deliverable = this.find(Long.parseLong(map.get("id").toString()));
        deliverables.add(deliverable);
      }
    }

    return deliverables;
  }

  @Override
  public List<Deliverable> getDeliverablesByProjectAndPhase(long phase, long project) {
    StringBuilder query = new StringBuilder();
    query.append("SELECT DISTINCT  ");
    query.append("d.id as id ");
    query.append("FROM ");
    query.append("deliverables AS d ");
    query.append("INNER JOIN deliverables_info AS di ON d.id = di.deliverable_id ");
    query.append("WHERE d.is_active = 1 AND d.project_id IS NOT NULL AND d.project_id = " + project
      + " AND (d.is_publication IS NULL OR d.is_publication = 0) AND ");
    query.append("di.is_active = 1 AND ");
    query.append("di.`id_phase` =" + phase);
    List<Map<String, Object>> rList = super.findCustomQuery(query.toString());
    List<Deliverable> deliverables = new ArrayList<>();

    if (rList != null) {
      for (Map<String, Object> map : rList) {
        Deliverable deliverable = this.find(Long.parseLong(map.get("id").toString()));
        deliverables.add(deliverable);
      }
    }

    return deliverables;
  }


  @Override
  public List<Deliverable> getDeliverablesLeadByInstitution(long institutionId, long phaseId) {
    StringBuilder query = new StringBuilder();
    query.append("SELECT DISTINCT ");
    query.append("dup.deliverable_id as id ");
    query.append("FROM ");
    query.append("deliverable_user_partnerships AS dup ");
    query.append("INNER JOIN deliverable_user_partnership_persons AS dupp ON dupp.user_partnership_id = dup.id ");
    query.append("WHERE ");
    query.append("dup.is_active = 1 AND ");
    query.append("dupp.is_active = 1 AND ");
    query.append("dup.institution_id =" + institutionId + " AND ");
    query.append("dup.id_phase = " + phaseId);

    List<Map<String, Object>> rList = super.findCustomQuery(query.toString());
    List<Deliverable> deliverables = new ArrayList<>();

    if (rList != null) {
      for (Map<String, Object> map : rList) {
        Deliverable deliverable = this.find(Long.parseLong(map.get("id").toString()));
        deliverables.add(deliverable);
      }
    }

    return deliverables;
  }

  @Override
  public List<Deliverable> getDeliverablesLeadByUser(long userId, long phaseId) {
    StringBuilder query = new StringBuilder();
    query.append("SELECT DISTINCT ");
    query.append("dup.deliverable_id as id ");
    query.append("FROM ");
    query.append("deliverable_user_partnerships AS dup ");
    query.append("INNER JOIN deliverable_user_partnership_persons AS dupp ON dupp.user_partnership_id = dup.id ");
    query.append("WHERE ");
    query.append("dup.is_active = 1 AND ");
    query.append("dupp.is_active = 1 AND ");
    query.append("dupp.user_id =" + userId + " AND ");
    query.append("dup.id_phase =" + phaseId);

    List<Map<String, Object>> rList = super.findCustomQuery(query.toString());
    List<Deliverable> deliverables = new ArrayList<>();

    if (rList != null) {
      for (Map<String, Object> map : rList) {
        Deliverable deliverable = this.find(Long.parseLong(map.get("id").toString()));
        deliverables.add(deliverable);
      }
    }

    return deliverables;
  }

  @Override
  public List<Deliverable> getPublicationsByPhase(long phase) {
    StringBuilder query = new StringBuilder();
    query.append("SELECT DISTINCT  ");
    query.append("d.id as id ");
    query.append("FROM ");
    query.append("deliverables AS d ");
    query.append("INNER JOIN deliverables_info AS di ON d.id = di.deliverable_id ");
    query.append("WHERE d.is_active = 1 AND d.project_id IS NULL AND (d.is_publication = 1) AND ");
    query.append("di.is_active = 1 AND ");
    query.append("di.`id_phase` =" + phase);

    List<Map<String, Object>> rList = super.findCustomQuery(query.toString());
    List<Deliverable> deliverables = new ArrayList<>();

    if (rList != null) {
      for (Map<String, Object> map : rList) {
        Deliverable deliverable = this.find(Long.parseLong(map.get("id").toString()));
        deliverables.add(deliverable);
      }
    }

    return deliverables;
  }

  @Override
  public Deliverable save(Deliverable deliverable) {
    if (deliverable.getId() == null) {
      super.saveEntity(deliverable);
    } else {
      deliverable = super.update(deliverable);
    }


    return deliverable;
  }

  @Override
  public Deliverable save(Deliverable deliverable, String section, List<String> relationsName, Phase phase) {
    if (deliverable.getId() == null) {
      deliverable = super.saveEntity(deliverable, section, relationsName, phase);
    } else {
      deliverable = super.update(deliverable, section, relationsName, phase);
    }


    return deliverable;
  }


}