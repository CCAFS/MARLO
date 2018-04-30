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

import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyCountryDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCountry;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectExpectedStudyCountryMySQLDAO extends AbstractMarloDAO<ProjectExpectedStudyCountry, Long>
  implements ProjectExpectedStudyCountryDAO {


  @Inject
  public ProjectExpectedStudyCountryMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectExpectedStudyCountry(long projectExpectedStudyCountryId) {
    ProjectExpectedStudyCountry projectExpectedStudyCountry = this.find(projectExpectedStudyCountryId);
    this.delete(projectExpectedStudyCountry);
  }

  @Override
  public boolean existProjectExpectedStudyCountry(long projectExpectedStudyCountryID) {
    ProjectExpectedStudyCountry projectExpectedStudyCountry = this.find(projectExpectedStudyCountryID);
    if (projectExpectedStudyCountry == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectExpectedStudyCountry find(long id) {
    return super.find(ProjectExpectedStudyCountry.class, id);

  }

  @Override
  public List<ProjectExpectedStudyCountry> findAll() {
    String query = "from " + ProjectExpectedStudyCountry.class.getName();
    List<ProjectExpectedStudyCountry> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectExpectedStudyCountry save(ProjectExpectedStudyCountry projectExpectedStudyCountry) {
    if (projectExpectedStudyCountry.getId() == null) {
      super.saveEntity(projectExpectedStudyCountry);
    } else {
      projectExpectedStudyCountry = super.update(projectExpectedStudyCountry);
    }


    return projectExpectedStudyCountry;
  }


}