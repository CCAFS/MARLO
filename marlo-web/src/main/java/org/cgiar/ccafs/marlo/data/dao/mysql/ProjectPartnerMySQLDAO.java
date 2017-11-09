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

import org.cgiar.ccafs.marlo.data.dao.InstitutionLocationDAO;
import org.cgiar.ccafs.marlo.data.dao.LocElementDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectPartnerDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectPartnerLocationDAO;
import org.cgiar.ccafs.marlo.data.model.InstitutionLocation;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerLocation;

import java.util.Date;
import java.util.List;

import com.google.inject.Inject;
import org.hibernate.Query;
import org.hibernate.SessionFactory;

public class ProjectPartnerMySQLDAO extends AbstractMarloDAO<ProjectPartner, Long> implements ProjectPartnerDAO {

  private LocElementDAO locElementDAO;
  private InstitutionLocationDAO institutionDAO;
  private ProjectPartnerLocationDAO projectPartnerLocationDAO;

  @Inject
  public ProjectPartnerMySQLDAO(SessionFactory sessionFactory, LocElementDAO locElementDAO,
    InstitutionLocationDAO institutionDAO, ProjectPartnerLocationDAO projectPartnerLocationDAO) {
    super(sessionFactory);
    this.locElementDAO = locElementDAO;
    this.institutionDAO = institutionDAO;
    this.projectPartnerLocationDAO = projectPartnerLocationDAO;
  }

  /**
   * clone the offices
   * 
   * @param projectPartner Partner original
   * @param projectPartnerAdd Partner new
   */

  private void addOffices(ProjectPartner projectPartner, ProjectPartner projectPartnerAdd) {

    if (projectPartner.getSelectedLocations() != null) {
      for (InstitutionLocation institutioLocation : projectPartner.getSelectedLocations()) {
        LocElement locElement = locElementDAO.findISOCode(institutioLocation.getLocElement().getIsoAlpha2());
        InstitutionLocation institutionLocationDB =
          institutionDAO.findByLocation(locElement.getId(), projectPartner.getInstitution().getId());
        ProjectPartnerLocation partnerLocation = new ProjectPartnerLocation();
        partnerLocation.setInstitutionLocation(institutionLocationDB);
        partnerLocation.setActive(true);
        partnerLocation.setActiveSince(new Date());
        partnerLocation.setCreatedBy(projectPartner.getCreatedBy());
        partnerLocation.setModificationJustification(projectPartner.getModificationJustification());
        partnerLocation.setModifiedBy(projectPartner.getCreatedBy());
        partnerLocation.setProjectPartner(projectPartnerAdd);
        projectPartnerLocationDAO.save(partnerLocation);
      }
    }
  }


  @Override
  public void deleteProjectPartner(long projectPartnerId) {
    ProjectPartner projectPartner = this.find(projectPartnerId);
    projectPartner.setActive(false);
    super.update(projectPartner);
  }


  @Override
  public boolean existProjectPartner(long projectPartnerID) {
    ProjectPartner projectPartner = this.find(projectPartnerID);
    if (projectPartner == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectPartner find(long id) {
    return super.find(ProjectPartner.class, id);

  }

  @Override
  public List<ProjectPartner> findAll() {
    String query = "from " + ProjectPartner.class.getName() + " where is_active=1";
    List<ProjectPartner> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }


  @Override
  public ProjectPartner getProjectPartnerByIdAndEagerFetchLocations(long projectPartnerID) {
    String query = "select distinct pp from ProjectPartner pp left join fetch pp.projectPartnerLocations ppl "
      + "where pp.id = :projectPartnerID";
    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("projectPartnerID", projectPartnerID);
    Object findSingleResult = super.findSingleResult(ProjectPartner.class, createQuery);

    ProjectPartner projectPartner = (ProjectPartner) findSingleResult;
    projectPartner.getProjectPartnerLocations().size();
    return projectPartner;
  }

  @Override
  public List<ProjectPartner> getProjectPartnersForProjectWithActiveProjectPartnerPersons(long projectId) {

    String query = "select distinct pp from ProjectPartner as pp inner join pp.project as project "
      + "left join fetch pp.projectPartnerPersons as ppp where project.id = :projectId " + "and ppp.active = true";

    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("projectId", projectId);
    List<ProjectPartner> projectPartners = createQuery.list();
    return projectPartners;
  }

  @Override
  public ProjectPartner save(ProjectPartner projectPartner) {
    if (projectPartner.getId() == null) {
      super.saveEntity(projectPartner);
    } else {
      projectPartner = super.update(projectPartner);
    }

    return projectPartner;
  }


}