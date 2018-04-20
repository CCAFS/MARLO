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

import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationCountryDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCountry;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectInnovationCountryMySQLDAO extends AbstractMarloDAO<ProjectInnovationCountry, Long>
  implements ProjectInnovationCountryDAO {


  @Inject
  public ProjectInnovationCountryMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectInnovationCountry(long projectInnovationCountryId) {
    ProjectInnovationCountry projectInnovationCountry = this.find(projectInnovationCountryId);
    this.delete(projectInnovationCountry);
  }

  @Override
  public boolean existProjectInnovationCountry(long projectInnovationCountryID) {
    ProjectInnovationCountry projectInnovationCountry = this.find(projectInnovationCountryID);
    if (projectInnovationCountry == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectInnovationCountry find(long id) {
    return super.find(ProjectInnovationCountry.class, id);

  }

  @Override
  public List<ProjectInnovationCountry> findAll() {
    String query = "from " + ProjectInnovationCountry.class.getName();
    List<ProjectInnovationCountry> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectInnovationCountry save(ProjectInnovationCountry projectInnovationCountry) {
    if (projectInnovationCountry.getId() == null) {
      super.saveEntity(projectInnovationCountry);
    } else {
      projectInnovationCountry = super.update(projectInnovationCountry);
    }


    return projectInnovationCountry;
  }


}