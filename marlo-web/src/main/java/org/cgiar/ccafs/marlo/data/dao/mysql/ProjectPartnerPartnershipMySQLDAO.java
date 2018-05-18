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
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPartnership;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectPartnerPartnershipMySQLDAO extends AbstractMarloDAO<ProjectPartnerPartnership, Long> implements ProjectPartnerPartnershipDAO {


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
  public ProjectPartnerPartnership save(ProjectPartnerPartnership projectPartnerPartnership) {
    if (projectPartnerPartnership.getId() == null) {
      super.saveEntity(projectPartnerPartnership);
    } else {
      projectPartnerPartnership = super.update(projectPartnerPartnership);
    }


    return projectPartnerPartnership;
  }


}