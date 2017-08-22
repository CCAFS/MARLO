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

import org.cgiar.ccafs.marlo.data.dao.ProjectHighligthCountryDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectHighlightCountry;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class ProjectHighligthCountryMySQLDAO extends AbstractMarloDAO<ProjectHighlightCountry, Long>
  implements ProjectHighligthCountryDAO {


  @Inject
  public ProjectHighligthCountryMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectHighligthCountry(long projectHighligthCountryId) {
    ProjectHighlightCountry projectHighlightCountry = this.find(projectHighligthCountryId);

    super.delete(projectHighlightCountry);
  }

  @Override
  public boolean existProjectHighligthCountry(long projectHighligthCountryID) {
    ProjectHighlightCountry projectHighlightCountry = this.find(projectHighligthCountryID);
    if (projectHighlightCountry == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectHighlightCountry find(long id) {
    return super.find(ProjectHighlightCountry.class, id);

  }

  @Override
  public List<ProjectHighlightCountry> findAll() {
    String query = "from " + ProjectHighlightCountry.class.getName() + " where is_active=1";
    List<ProjectHighlightCountry> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectHighlightCountry save(ProjectHighlightCountry projectHighlightCountry) {
    if (projectHighlightCountry.getId() == null) {
      super.saveEntity(projectHighlightCountry);
    } else {
      projectHighlightCountry = super.update(projectHighlightCountry);
    }


    return projectHighlightCountry;
  }


}