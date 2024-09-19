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

import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyPartnerTypeDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPartnerType;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectExpectedStudyPartnerTypeMySQLDAO extends AbstractMarloDAO<ProjectExpectedStudyPartnerType, Long> implements ProjectExpectedStudyPartnerTypeDAO {


  @Inject
  public ProjectExpectedStudyPartnerTypeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectExpectedStudyPartnerType(long projectExpectedStudyPartnerTypeId) {
    ProjectExpectedStudyPartnerType projectExpectedStudyPartnerType = this.find(projectExpectedStudyPartnerTypeId);
    projectExpectedStudyPartnerType.setActive(false);
    this.update(projectExpectedStudyPartnerType);
  }

  @Override
  public boolean existProjectExpectedStudyPartnerType(long projectExpectedStudyPartnerTypeID) {
    ProjectExpectedStudyPartnerType projectExpectedStudyPartnerType = this.find(projectExpectedStudyPartnerTypeID);
    if (projectExpectedStudyPartnerType == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectExpectedStudyPartnerType find(long id) {
    return super.find(ProjectExpectedStudyPartnerType.class, id);

  }

  @Override
  public List<ProjectExpectedStudyPartnerType> findAll() {
    String query = "from " + ProjectExpectedStudyPartnerType.class.getName() + " where is_active=1";
    List<ProjectExpectedStudyPartnerType> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectExpectedStudyPartnerType save(ProjectExpectedStudyPartnerType projectExpectedStudyPartnerType) {
    if (projectExpectedStudyPartnerType.getId() == null) {
      super.saveEntity(projectExpectedStudyPartnerType);
    } else {
      projectExpectedStudyPartnerType = super.update(projectExpectedStudyPartnerType);
    }


    return projectExpectedStudyPartnerType;
  }


}