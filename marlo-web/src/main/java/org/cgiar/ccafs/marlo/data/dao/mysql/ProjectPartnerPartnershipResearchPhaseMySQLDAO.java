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

import org.cgiar.ccafs.marlo.data.dao.ProjectPartnerPartnershipResearchPhaseDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPartnershipResearchPhase;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectPartnerPartnershipResearchPhaseMySQLDAO extends
  AbstractMarloDAO<ProjectPartnerPartnershipResearchPhase, Long> implements ProjectPartnerPartnershipResearchPhaseDAO {


  @Inject
  public ProjectPartnerPartnershipResearchPhaseMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectPartnerPartnershipResearchPhase(long projectPartnerPartnershipResearchPhaseId) {
    ProjectPartnerPartnershipResearchPhase projectPartnerPartnershipResearchPhase =
      this.find(projectPartnerPartnershipResearchPhaseId);
    projectPartnerPartnershipResearchPhase.setActive(false);
    this.update(projectPartnerPartnershipResearchPhase);
  }

  @Override
  public boolean existProjectPartnerPartnershipResearchPhase(long projectPartnerPartnershipResearchPhaseID) {
    ProjectPartnerPartnershipResearchPhase projectPartnerPartnershipResearchPhase =
      this.find(projectPartnerPartnershipResearchPhaseID);
    if (projectPartnerPartnershipResearchPhase == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectPartnerPartnershipResearchPhase find(long id) {
    return super.find(ProjectPartnerPartnershipResearchPhase.class, id);

  }

  @Override
  public List<ProjectPartnerPartnershipResearchPhase> findAll() {
    String query = "from " + ProjectPartnerPartnershipResearchPhase.class.getName() + " where is_active=1";
    List<ProjectPartnerPartnershipResearchPhase> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<ProjectPartnerPartnershipResearchPhase>
    findParnershipResearchPhaseByPartnership(long projectPartnerPartnershipnId) {
    String query = "from " + ProjectPartnerPartnershipResearchPhase.class.getName()
      + " where project_partner_partnership_id=" + projectPartnerPartnershipnId + " and is_active=1";
    List<ProjectPartnerPartnershipResearchPhase> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public ProjectPartnerPartnershipResearchPhase
    save(ProjectPartnerPartnershipResearchPhase projectPartnerPartnershipResearchPhase) {
    if (projectPartnerPartnershipResearchPhase.getId() == null) {
      super.saveEntity(projectPartnerPartnershipResearchPhase);
    } else {
      projectPartnerPartnershipResearchPhase = super.update(projectPartnerPartnershipResearchPhase);
    }


    return projectPartnerPartnershipResearchPhase;
  }


}