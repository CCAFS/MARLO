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

import org.cgiar.ccafs.marlo.data.dao.ProjectBudgetsCluserActvityDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectBudgetsCluserActvity;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class ProjectBudgetsCluserActvityMySQLDAO extends AbstractMarloDAO<ProjectBudgetsCluserActvity, Long> implements ProjectBudgetsCluserActvityDAO {


  @Inject
  public ProjectBudgetsCluserActvityMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }


  @Override
  public void deleteProjectBudgetsCluserActvity(long projectBudgetsCluserActvityId) {
    ProjectBudgetsCluserActvity projectBudgetsCluserActvity = this.find(projectBudgetsCluserActvityId);
    projectBudgetsCluserActvity.setActive(false);
     super.update(projectBudgetsCluserActvity);
  }

  @Override
  public boolean existProjectBudgetsCluserActvity(long projectBudgetsCluserActvityID) {
    ProjectBudgetsCluserActvity projectBudgetsCluserActvity = this.find(projectBudgetsCluserActvityID);
    if (projectBudgetsCluserActvity == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectBudgetsCluserActvity find(long id) {
    return super.find(ProjectBudgetsCluserActvity.class, id);

  }

  @Override
  public List<ProjectBudgetsCluserActvity> findAll() {
    String query = "from " + ProjectBudgetsCluserActvity.class.getName() + " where is_active=1";
    List<ProjectBudgetsCluserActvity> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectBudgetsCluserActvity save(ProjectBudgetsCluserActvity projectBudgetsCluserActvity) {
    if (projectBudgetsCluserActvity.getId() == null) {
      super.saveEntity(projectBudgetsCluserActvity);
    } else {
      projectBudgetsCluserActvity = super.update(projectBudgetsCluserActvity);
    }


    return projectBudgetsCluserActvity;
  }


}