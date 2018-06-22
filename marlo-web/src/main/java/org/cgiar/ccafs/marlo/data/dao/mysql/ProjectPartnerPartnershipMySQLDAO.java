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

import org.cgiar.ccafs.marlo.data.dao.ProjectPartnerPartnershipDAO;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPartnership;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectPartnerPartnershipMySQLDAO extends AbstractMarloDAO<ProjectPartnerPartnership, Long>
  implements ProjectPartnerPartnershipDAO {


  @Inject
  public ProjectPartnerPartnershipMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectPartnerPartnership(long projectPartnerPartnershipId) {
    ProjectPartnerPartnership projectPartnerPartnership = this.find(projectPartnerPartnershipId);
    projectPartnerPartnership.setActive(false);
    this.update(projectPartnerPartnership);
  }

  @Override
  public boolean existProjectPartnerPartnership(long projectPartnerPartnershipID) {
    ProjectPartnerPartnership projectPartnerPartnership = this.find(projectPartnerPartnershipID);
    if (projectPartnerPartnership == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectPartnerPartnership find(long id) {
    return super.find(ProjectPartnerPartnership.class, id);

  }

  @Override
  public List<ProjectPartnerPartnership> findAll() {
    String query = "from " + ProjectPartnerPartnership.class.getName() + " where is_active=1";
    List<ProjectPartnerPartnership> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<ProjectPartnerPartnership> getProjectPartnerPartnershipByPhase(Phase phase) {
    StringBuilder query = new StringBuilder();
    query.append("SELECT DISTINCT  ");
    query.append("ppp.id as id ");
    query.append("FROM ");
    query.append("project_partner_partnerships AS ppp ");
    query.append("INNER JOIN project_partners AS pp ON pp.id = ppp.project_partner ");
    query.append("INNER JOIN projects AS p ON p.id = pp.project_id ");
    query.append("INNER JOIN projects_info AS pi ON p.id = pi.project_id ");
    query.append("WHERE ppp.is_active = 1 AND ");
    query.append("pp.is_active = 1 AND ");
    query.append("pp.has_partnerships = 1 AND ");
    query.append("p.is_active = 1 AND ");
    query.append("pi.`id_phase` =" + phase.getId() + " AND ");
    query.append("pp.`id_phase` =" + phase.getId());

    List<Map<String, Object>> rList = super.findCustomQuery(query.toString());
    List<ProjectPartnerPartnership> projectPartnerPartnerships = new ArrayList<>();

    if (rList != null) {
      for (Map<String, Object> map : rList) {
        ProjectPartnerPartnership projectPartnerPartnership = this.find(Long.parseLong(map.get("id").toString()));
        projectPartnerPartnerships.add(projectPartnerPartnership);
      }
    }

    return projectPartnerPartnerships.stream().sorted((p1, p2) -> p1.getProjectPartner().getInstitution()
      .getComposedName().compareTo(p2.getProjectPartner().getInstitution().getComposedName()))
      .collect(Collectors.toList());
  }

  @Override
  public ProjectPartnerPartnership save(ProjectPartnerPartnership projectPartnerPartnership) {
    if (projectPartnerPartnership.getId() == null) {
      super.saveEntity(projectPartnerPartnership);
    } else {
      projectPartnerPartnership = super.update(projectPartnerPartnership);
    }


    return projectPartnerPartnership;
  }

}