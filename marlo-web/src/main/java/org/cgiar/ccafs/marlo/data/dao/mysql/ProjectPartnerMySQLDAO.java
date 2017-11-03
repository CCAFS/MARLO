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
import org.cgiar.ccafs.marlo.data.model.InstitutionLocation;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerLocation;

import java.util.Date;
import java.util.List;

import com.google.inject.Inject;

public class ProjectPartnerMySQLDAO implements ProjectPartnerDAO {

  private StandardDAO dao;
  private LocElementDAO locElementDAO;
  private InstitutionLocationDAO institutionDAO;


  @Inject
  public ProjectPartnerMySQLDAO(StandardDAO dao, LocElementDAO locElementDAO, InstitutionLocationDAO institutionDAO) {
    this.dao = dao;
    this.locElementDAO = locElementDAO;
    this.institutionDAO = institutionDAO;
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
        dao.save(partnerLocation);
      }
    }
  }


  @Override
  public boolean deleteProjectPartner(long projectPartnerId) {
    ProjectPartner projectPartner = this.find(projectPartnerId);
    projectPartner.setActive(false);
    boolean result = dao.update(projectPartner);

    return result;
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
    return dao.find(ProjectPartner.class, id);

  }

  @Override
  public List<ProjectPartner> findAll() {
    String query = "from " + ProjectPartner.class.getName() + " where is_active=1";
    List<ProjectPartner> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }


  @Override
  public long save(ProjectPartner projectPartner) {
    if (projectPartner.getId() == null) {
      dao.save(projectPartner);
    } else {
      dao.update(projectPartner);
    }

    return projectPartner.getId();
  }


}