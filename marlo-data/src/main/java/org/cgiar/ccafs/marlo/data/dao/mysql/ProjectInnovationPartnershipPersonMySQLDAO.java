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

import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationPartnershipPersonDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationPartnershipPerson;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectInnovationPartnershipPersonMySQLDAO extends AbstractMarloDAO<ProjectInnovationPartnershipPerson, Long> implements ProjectInnovationPartnershipPersonDAO {


  @Inject
  public ProjectInnovationPartnershipPersonMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectInnovationPartnershipPerson(long projectInnovationPartnershipPersonId) {
    ProjectInnovationPartnershipPerson projectInnovationPartnershipPerson = this.find(projectInnovationPartnershipPersonId);
    projectInnovationPartnershipPerson.setActive(false);
    this.update(projectInnovationPartnershipPerson);
  }

  @Override
  public boolean existProjectInnovationPartnershipPerson(long projectInnovationPartnershipPersonID) {
    ProjectInnovationPartnershipPerson projectInnovationPartnershipPerson = this.find(projectInnovationPartnershipPersonID);
    if (projectInnovationPartnershipPerson == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectInnovationPartnershipPerson find(long id) {
    return super.find(ProjectInnovationPartnershipPerson.class, id);

  }

  @Override
  public List<ProjectInnovationPartnershipPerson> findAll() {
    String query = "from " + ProjectInnovationPartnershipPerson.class.getName() + " where is_active=1";
    List<ProjectInnovationPartnershipPerson> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectInnovationPartnershipPerson save(ProjectInnovationPartnershipPerson projectInnovationPartnershipPerson) {
    if (projectInnovationPartnershipPerson.getId() == null) {
      super.saveEntity(projectInnovationPartnershipPerson);
    } else {
      projectInnovationPartnershipPerson = super.update(projectInnovationPartnershipPerson);
    }


    return projectInnovationPartnershipPerson;
  }


}