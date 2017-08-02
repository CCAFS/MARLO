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

import org.cgiar.ccafs.marlo.data.dao.ProjectPartnerLocationDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerLocation;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class ProjectPartnerLocationMySQLDAO extends AbstractMarloDAO implements ProjectPartnerLocationDAO {


  @Inject
  public ProjectPartnerLocationMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public boolean deleteProjectPartnerLocation(long projectPartnerLocationId) {
    ProjectPartnerLocation projectPartnerLocation = this.find(projectPartnerLocationId);
    projectPartnerLocation.setActive(false);
    return this.save(projectPartnerLocation) > 0;
  }

  @Override
  public boolean existProjectPartnerLocation(long projectPartnerLocationID) {
    ProjectPartnerLocation projectPartnerLocation = this.find(projectPartnerLocationID);
    if (projectPartnerLocation == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectPartnerLocation find(long id) {
    return super.find(ProjectPartnerLocation.class, id);

  }

  @Override
  public List<ProjectPartnerLocation> findAll() {
    String query = "from " + ProjectPartnerLocation.class.getName() + " where is_active=1";
    List<ProjectPartnerLocation> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(ProjectPartnerLocation projectPartnerLocation) {
    if (projectPartnerLocation.getId() == null) {
      super.save(projectPartnerLocation);
    } else {
      super.update(projectPartnerLocation);
    }


    return projectPartnerLocation.getId();
  }


}