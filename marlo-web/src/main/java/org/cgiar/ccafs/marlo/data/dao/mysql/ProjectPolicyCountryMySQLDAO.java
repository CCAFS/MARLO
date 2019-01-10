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

import org.cgiar.ccafs.marlo.data.dao.ProjectPolicyCountryDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyCountry;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectPolicyCountryMySQLDAO extends AbstractMarloDAO<ProjectPolicyCountry, Long>
  implements ProjectPolicyCountryDAO {


  @Inject
  public ProjectPolicyCountryMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectPolicyCountry(long projectPolicyCountryId) {
    this.delete(projectPolicyCountryId);
  }

  @Override
  public boolean existProjectPolicyCountry(long projectPolicyCountryID) {
    ProjectPolicyCountry projectPolicyCountry = this.find(projectPolicyCountryID);
    if (projectPolicyCountry == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectPolicyCountry find(long id) {
    return super.find(ProjectPolicyCountry.class, id);

  }

  @Override
  public List<ProjectPolicyCountry> findAll() {
    String query = "from " + ProjectPolicyCountry.class.getName();
    List<ProjectPolicyCountry> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectPolicyCountry save(ProjectPolicyCountry projectPolicyCountry) {
    if (projectPolicyCountry.getId() == null) {
      super.saveEntity(projectPolicyCountry);
    } else {
      projectPolicyCountry = super.update(projectPolicyCountry);
    }


    return projectPolicyCountry;
  }


}