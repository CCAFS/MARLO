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

import org.cgiar.ccafs.marlo.data.dao.ProjectBudgetExecutionDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectBudgetExecution;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectBudgetExecutionMySQLDAO extends AbstractMarloDAO<ProjectBudgetExecution, Long> implements ProjectBudgetExecutionDAO {


  @Inject
  public ProjectBudgetExecutionMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectBudgetExecution(long projectBudgetExecutionId) {
    ProjectBudgetExecution projectBudgetExecution = this.find(projectBudgetExecutionId);
    projectBudgetExecution.setActive(false);
    this.update(projectBudgetExecution);
  }

  @Override
  public boolean existProjectBudgetExecution(long projectBudgetExecutionID) {
    ProjectBudgetExecution projectBudgetExecution = this.find(projectBudgetExecutionID);
    if (projectBudgetExecution == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectBudgetExecution find(long id) {
    return super.find(ProjectBudgetExecution.class, id);

  }

  @Override
  public List<ProjectBudgetExecution> findAll() {
    String query = "from " + ProjectBudgetExecution.class.getName() + " where is_active=1";
    List<ProjectBudgetExecution> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectBudgetExecution save(ProjectBudgetExecution projectBudgetExecution) {
    if (projectBudgetExecution.getId() == null) {
      super.saveEntity(projectBudgetExecution);
    } else {
      projectBudgetExecution = super.update(projectBudgetExecution);
    }


    return projectBudgetExecution;
  }


}