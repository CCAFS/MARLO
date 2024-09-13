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

import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyPartnershipsPersonDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPartnershipsPerson;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectExpectedStudyPartnershipsPersonMySQLDAO extends AbstractMarloDAO<ProjectExpectedStudyPartnershipsPerson, Long> implements ProjectExpectedStudyPartnershipsPersonDAO {


  @Inject
  public ProjectExpectedStudyPartnershipsPersonMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectExpectedStudyPartnershipsPerson(long projectExpectedStudyPartnershipsPersonId) {
    ProjectExpectedStudyPartnershipsPerson projectExpectedStudyPartnershipsPerson = this.find(projectExpectedStudyPartnershipsPersonId);
    projectExpectedStudyPartnershipsPerson.setActive(false);
    this.update(projectExpectedStudyPartnershipsPerson);
  }

  @Override
  public boolean existProjectExpectedStudyPartnershipsPerson(long projectExpectedStudyPartnershipsPersonID) {
    ProjectExpectedStudyPartnershipsPerson projectExpectedStudyPartnershipsPerson = this.find(projectExpectedStudyPartnershipsPersonID);
    if (projectExpectedStudyPartnershipsPerson == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectExpectedStudyPartnershipsPerson find(long id) {
    return super.find(ProjectExpectedStudyPartnershipsPerson.class, id);

  }

  @Override
  public List<ProjectExpectedStudyPartnershipsPerson> findAll() {
    String query = "from " + ProjectExpectedStudyPartnershipsPerson.class.getName() + " where is_active=1";
    List<ProjectExpectedStudyPartnershipsPerson> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectExpectedStudyPartnershipsPerson save(ProjectExpectedStudyPartnershipsPerson projectExpectedStudyPartnershipsPerson) {
    if (projectExpectedStudyPartnershipsPerson.getId() == null) {
      super.saveEntity(projectExpectedStudyPartnershipsPerson);
    } else {
      projectExpectedStudyPartnershipsPerson = super.update(projectExpectedStudyPartnershipsPerson);
    }


    return projectExpectedStudyPartnershipsPerson;
  }


}