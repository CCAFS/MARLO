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
import org.cgiar.ccafs.marlo.data.model.DeliverableHomeDTO;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.utils.ListResultTransformer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.FlushMode;
import org.hibernate.Query;
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

  /**
   * Get the answered comment by phase
   * 
   * @author IBD
   * @param phase phase of the project
   * @return deliverable list with the comment count. olny coment with answer
   */
  @Override
  public List<String> getAnsweredCommentByPhase(long phase) {
    StringBuilder query = new StringBuilder();
    query.append("SELECT parent_id as parent_id,count(*) as count ");
    query.append("FROM feedback_qa_comments fqc");
    query.append(" WHERE id_phase=" + phase);
    query.append(" and status_id = 1 ");
    query.append(" and field_id in (select id from feedback_qa_commentable_fields fqcf ");
    query.append(" where section_name = 'deliverable') ");
    query.append(" and reply_id is not null ");
    query.append(" group by parent_id ");

    List<Map<String, Object>> rList = super.findCustomQuery(query.toString());
    List<String> comments = new ArrayList<>();

    if (rList != null) {
      for (Map<String, Object> map : rList) {
        String tmp = map.get("parent_id").toString() + "|" + map.get("count").toString();
        comments.add(tmp);
      }
    }

    return comments;
  }

  /**
   * Get the commentstatus by phase
   * 
   * @author IBD
   * @param phase phase of the project
   * @return deliverable list with the comment count
   */
  @Override
  public List<String> getCommentStatusByPhase(long phase) {
    StringBuilder query = new StringBuilder();
    query.append("SELECT parent_id as parent_id,count(*) as count ");
    query.append("FROM feedback_qa_comments fqc");
    query.append(" WHERE id_phase=" + phase);
    query.append(" and status_id <> 6 ");
    query.append(" and field_id in (select id from feedback_qa_commentable_fields fqcf ");
    query.append(" where section_name = 'deliverable') ");
    query.append(" group by parent_id ");

    List<Map<String, Object>> rList = super.findCustomQuery(query.toString());
    List<String> comments = new ArrayList<>();

    if (rList != null) {
      for (Map<String, Object> map : rList) {
        String tmp = map.get("parent_id").toString() + "|" + map.get("count").toString();
        comments.add(tmp);
      }
    }

    return comments;
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
  public List<Deliverable> getDeliverablesByProjectAndPhase(long phaseId, long projectId) {
    String query = "select d from Deliverable d, DeliverableInfo di "
      + "join d.project pr with pr.id = :projectId and pr.active = true join di.phase ph with ph.id = :phaseId "
      + "where di.deliverable = d and d.active = true and coalesce(nullif(di.newExpectedYear, -1),di.year) = ph.year";

    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);

    createQuery.setParameter("phaseId", phaseId);
    createQuery.setParameter("projectId", projectId);

    List<Deliverable> deliverables = super.findAll(createQuery);

    return deliverables;
  }


  @Override
  public List<DeliverableHomeDTO> getDeliverablesByProjectAndPhaseHome(long phaseId, long projectId) {
    String query = "select d.id as deliverableId, coalesce(di.newExpectedYear, -1) as newExpectedYear, "
      + "di.year as expectedYear, pr.id as projectId, coalesce(di.deliverableType.name, 'None') as deliverableType, "
      + "di.title as deliverableTitle, pi.acronym as projectAcronym from Deliverable d, DeliverableInfo di, Phase ph, Project pr, ProjectInfo pi "
      + "where di.active = true and di.deliverable = d and d.active = true and d.project = pr and pr.id = :projectId and pr.active = true and pr=pi.project and pi.phase=ph and "
      + "di.phase = ph and ph.id = :phaseId and coalesce(nullif(di.newExpectedYear, -1),di.year) = ph.year";

    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);

    createQuery.setParameter("phaseId", phaseId);
    createQuery.setParameter("projectId", projectId);

    createQuery.setResultTransformer(
      (ListResultTransformer) (tuple, aliases) -> new DeliverableHomeDTO(((Number) tuple[0]).longValue(),
        ((Number) tuple[1]).longValue(), ((Number) tuple[2]).longValue(), ((Number) tuple[3]).longValue(),
        (String) tuple[4], (String) tuple[5], (String) tuple[6]));
    createQuery.setFlushMode(FlushMode.COMMIT);

    List<DeliverableHomeDTO> deliverables = createQuery.list();

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
    query.append("dupp.user_id =" + userId + " AND ");
    query.append("dup.is_active = 1 AND ");
    query.append("dupp.is_active = 1 AND ");
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

  /**
   * Get listing to validate duplicate information (dissemination_URL,DIO, handle)
   * 
   * @author IBD
   * @param phase phase of the project
   * @return deliverable list with the data to validate duplicates (dissemination_URL,DIO, handle)
   */
  @Override
  public List<String> getDuplicatesDeliverablesByPhase(long phase) {
    StringBuilder query = new StringBuilder();
    query.append("SELECT DISTINCT  ");
    query.append("d.id as id,");
    query.append("dv.dissemination_URL as dissemination_URL ");
    query.append("FROM ");
    query.append("deliverables AS d ");
    query.append("INNER JOIN deliverable_dissemination AS dv ON d.id = dv.deliverable_id ");
    query.append("INNER JOIN deliverables_info AS di ON d.id = di.deliverable_id ");
    query.append(" WHERE di.is_active =1 ");
    query.append(" and d.id_phase=" + phase);
    query.append(" and dv.dissemination_URL is not null");
    query.append(" and length(dissemination_URL)>0");
    query.append(" UNION ");
    query.append("SELECT DISTINCT  ");
    query.append("d.id as id,");
    query.append("dme.element_value as dissemination_URL ");
    query.append("FROM ");
    query.append("deliverables AS d ");
    query.append("INNER JOIN deliverable_metadata_elements AS dme ON d.id = dme.deliverable_id ");
    query.append("INNER JOIN deliverables_info AS di ON d.id = di.deliverable_id ");
    query.append(" where dme.element_id in(35,36) ");
    query.append(" and di.is_active =1 ");
    query.append(" and d.id_phase=" + phase);
    query.append(" and element_value is not null");
    query.append(" and length(element_value)>0");


    List<Map<String, Object>> rList = super.findCustomQuery(query.toString());
    List<String> deliverables = new ArrayList<>();

    if (rList != null) {
      for (Map<String, Object> map : rList) {
        String tmp = map.get("id").toString() + "|" + map.get("dissemination_URL").toString();
        deliverables.add(tmp);
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

  /**
   * get deliverables without activities
   * 
   * @author IBD
   * @param phase phase of the project
   * @param projectId project id
   * @return quantity deliverables without activities
   */
  @Override
  public int getQuantityDeliverablesWithActivities(long phase, long projectId) {
    StringBuilder query = new StringBuilder();
    query.append("SELECT count(*) as count FROM deliverables d ");
    query.append("join deliverables_info di on d.id = di.deliverable_id ");
    query.append("join phases p on p.id = d.id_phase");
    query.append(" WHERE d.id_phase=" + phase);
    query.append(" and d.id_phase = di.id_phase ");
    query.append(" and d.is_active = 1 and d.is_active =di.is_active ");
    query.append(" and d.project_id=" + projectId);
    query.append(
      " and (d.id not in (select da.deliverable_id from deliverable_activities da where da.deliverable_id = d.id and da.is_active =1 ) ");
    query.append(
      "  or d.id not in (select da.deliverable_id from deliverable_activities da where deliverable_id = d.id and da.is_active =1 and da.id_phase =p.id) ");
    query.append(
      "  or d.id not in (select da.deliverable_id from deliverable_activities da join activities a  on da.activity_id = a.id where deliverable_id = d.id and da.is_active =1 and a.is_active =1 and da.id_phase =p.id)) ");


    List<Map<String, Object>> rList = super.findCustomQuery(query.toString());
    int deliverable = 0;

    if (rList != null) {
      for (Map<String, Object> map : rList) {
        deliverable = Integer.parseInt(map.get("count").toString());
      }
    }

    return deliverable;
  }

  @Override
  public Boolean isDeliverableExcluded(Long deliverableId, Long phaseId) {
    StringBuilder query = new StringBuilder();
    query.append("select is_deliverable_excluded(" + deliverableId.longValue() + "," + phaseId.longValue()
      + ") as isDeliverableExcluded");
    List<Map<String, Object>> rList = super.findCustomQuery(query.toString());
    if (rList != null) {
      for (Map<String, Object> map : rList) {
        if (Long.parseLong(map.get("isDeliverableExcluded").toString()) == 0) {
          return true;
        }
      }
    }
    return false;
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