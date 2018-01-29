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

import org.cgiar.ccafs.marlo.data.dao.ProjectBudgetsFlagshipDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectBudgetsFlagship;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class ProjectBudgetsFlagshipMySQLDAO extends AbstractMarloDAO<ProjectBudgetsFlagship, Long> implements ProjectBudgetsFlagshipDAO {


  @Inject
  public ProjectBudgetsFlagshipMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectBudgetsFlagship(long projectBudgetsFlagshipId) {
    ProjectBudgetsFlagship projectBudgetsFlagship = this.find(projectBudgetsFlagshipId);
    projectBudgetsFlagship.setActive(false);
    this.save(projectBudgetsFlagship);
  }

  @Override
  public boolean existProjectBudgetsFlagship(long projectBudgetsFlagshipID) {
    ProjectBudgetsFlagship projectBudgetsFlagship = this.find(projectBudgetsFlagshipID);
    if (projectBudgetsFlagship == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectBudgetsFlagship find(long id) {
    return super.find(ProjectBudgetsFlagship.class, id);

  }

  @Override
  public List<ProjectBudgetsFlagship> findAll() {
    String query = "from " + ProjectBudgetsFlagship.class.getName() + " where is_active=1";
    List<ProjectBudgetsFlagship> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectBudgetsFlagship save(ProjectBudgetsFlagship projectBudgetsFlagship) {
    if (projectBudgetsFlagship.getId() == null) {
      super.saveEntity(projectBudgetsFlagship);
    } else {
      projectBudgetsFlagship = super.update(projectBudgetsFlagship);
    }


    return projectBudgetsFlagship;
  }


}