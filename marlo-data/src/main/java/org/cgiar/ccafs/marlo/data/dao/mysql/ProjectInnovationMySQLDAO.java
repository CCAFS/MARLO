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

import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationDAO;
import org.cgiar.ccafs.marlo.data.model.InnovationHomeDTO;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.utils.ListResultTransformer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.AliasToEntityMapResultTransformer;

@Named
public class ProjectInnovationMySQLDAO extends AbstractMarloDAO<ProjectInnovation, Long>
  implements ProjectInnovationDAO {


  @Inject
  public ProjectInnovationMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectInnovation(long projectInnovationId) {
    ProjectInnovation projectInnovation = this.find(projectInnovationId);
    projectInnovation.setActive(false);
    this.update(projectInnovation);
  }

  @Override
  public boolean existProjectInnovation(long projectInnovationID) {
    ProjectInnovation projectInnovation = this.find(projectInnovationID);
    if (projectInnovation == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectInnovation find(long id) {
    return super.find(ProjectInnovation.class, id);

  }

  @Override
  public List<ProjectInnovation> findAll() {
    String query = "from " + ProjectInnovation.class.getName() + " where is_active=1";
    List<ProjectInnovation> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<ProjectInnovation> getInnovationsByPhase(Phase phase) {
    String query = "SELECT DISTINCT pi.id AS id FROM ProjectInnovation pi, ProjectInnovationInfo pii "
      + "where pii.projectInnovation = pi and pi.active = true AND pii.phase.id = :phaseId";

    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("phaseId", phase.getId());
    createQuery.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
    createQuery.setFlushMode(FlushMode.COMMIT);

    List<Map<String, Object>> rList = createQuery.list();
    List<ProjectInnovation> projectInnovations = new ArrayList<>();

    if (rList != null) {
      for (Map<String, Object> map : rList) {
        ProjectInnovation projectInnovation = this.find(Long.parseLong(map.get("id").toString()));
        projectInnovations.add(projectInnovation);
      }
    }

    return projectInnovations;
  }

  @Override
  public List<InnovationHomeDTO> getInnovationsByProjectAndPhaseHome(long phaseId, long projectId) {
    String query = "select pi.id as innovationId, pii.year as expectedYear, "
      + "pr.id as projectId, coalesce(pii.repIndInnovationType.name, 'None') as innovationType, pii.title as innovationTitle, pi.acronym as projectAcronym "
      + "from ProjectInnovation pi, ProjectInnovationInfo pii, Phase ph, Project pr "
      + "where pii.projectInnovation = pi and pi.active = true and "
      + "pi.project = pr and pr.id = :projectId and pr.active = true and "
      + "pii.phase = ph and ph.id = :phaseId and pii.year = ph.year";

    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);

    createQuery.setParameter("phaseId", phaseId);
    createQuery.setParameter("projectId", projectId);

    createQuery.setResultTransformer(
      (ListResultTransformer) (tuple, aliases) -> new InnovationHomeDTO(((Number) tuple[0]).longValue(),
        ((Number) tuple[1]).longValue(), ((Number) tuple[2]).longValue(), (String) tuple[3], (String) tuple[4],
        (String) tuple[5]));
    createQuery.setFlushMode(FlushMode.COMMIT);

    List<InnovationHomeDTO> innovations = createQuery.list();

    return innovations;
  }

  @Override
  public Boolean isInnovationExcluded(Long innovationId, Long phaseId) {
    StringBuilder query = new StringBuilder();
    query.append("select is_innovation_excluded(" + innovationId.longValue() + "," + phaseId.longValue()
      + ") as isInnovationExcluded");
    List<Map<String, Object>> rList = super.findCustomQuery(query.toString());
    if (rList != null) {
      for (Map<String, Object> map : rList) {
        if (Long.parseLong(map.get("isInnovationExcluded").toString()) == 0) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public ProjectInnovation save(ProjectInnovation projectInnovation) {
    if (projectInnovation.getId() == null) {
      super.saveEntity(projectInnovation);
    } else {
      projectInnovation = super.update(projectInnovation);
    }


    return projectInnovation;
  }

  @Override
  public ProjectInnovation save(ProjectInnovation projectInnovation, String section, List<String> relationsName,
    Phase phase) {
    if (projectInnovation.getId() == null) {
      super.saveEntity(projectInnovation, section, relationsName, phase);
    } else {
      projectInnovation = super.update(projectInnovation, section, relationsName, phase);
    }
    return projectInnovation;
  }
}