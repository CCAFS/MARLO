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

import org.cgiar.ccafs.marlo.data.dao.ProjectPartnerPartnershipLocationDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPartnershipLocation;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectPartnerPartnershipLocationMySQLDAO extends AbstractMarloDAO<ProjectPartnerPartnershipLocation, Long>
  implements ProjectPartnerPartnershipLocationDAO {


  @Inject
  public ProjectPartnerPartnershipLocationMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectPartnerPartnershipLocation(long projectPartnerPartnershipLocationId) {
    ProjectPartnerPartnershipLocation projectPartnerPartnershipLocation =
      this.find(projectPartnerPartnershipLocationId);
    projectPartnerPartnershipLocation.setActive(false);
    this.update(projectPartnerPartnershipLocation);
  }

  @Override
  public boolean existProjectPartnerPartnershipLocation(long projectPartnerPartnershipLocationID) {
    ProjectPartnerPartnershipLocation projectPartnerPartnershipLocation =
      this.find(projectPartnerPartnershipLocationID);
    if (projectPartnerPartnershipLocation == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectPartnerPartnershipLocation find(long id) {
    return super.find(ProjectPartnerPartnershipLocation.class, id);

  }

  @Override
  public List<ProjectPartnerPartnershipLocation> findAll() {
    String query = "from " + ProjectPartnerPartnershipLocation.class.getName() + " where is_active=1";
    List<ProjectPartnerPartnershipLocation> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<ProjectPartnerPartnershipLocation>
    findParnershipLocationByPartnership(long projectPartnerPartnershipnId) {
    String query = "from " + ProjectPartnerPartnershipLocation.class.getName() + " where project_partner_partnership="
      + projectPartnerPartnershipnId + " and is_active=1";
    List<ProjectPartnerPartnershipLocation> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public ProjectPartnerPartnershipLocation save(ProjectPartnerPartnershipLocation projectPartnerPartnershipLocation) {
    if (projectPartnerPartnershipLocation.getId() == null) {
      super.saveEntity(projectPartnerPartnershipLocation);
    } else {
      projectPartnerPartnershipLocation = super.update(projectPartnerPartnershipLocation);
    }


    return projectPartnerPartnershipLocation;
  }


}