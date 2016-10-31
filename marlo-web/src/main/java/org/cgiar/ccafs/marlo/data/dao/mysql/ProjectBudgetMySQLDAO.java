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

import org.cgiar.ccafs.marlo.data.dao.ProjectBudgetDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectBudget;

import java.util.List;
import java.util.Map;

import com.google.inject.Inject;

public class ProjectBudgetMySQLDAO implements ProjectBudgetDAO {

  private StandardDAO dao;

  @Inject
  public ProjectBudgetMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public String amountByBudgetType(long institutionId, int year, long budgetType) {
    String query = "select SUM(amount) as amount from project_budgets where institution_id= " + institutionId
      + " and year= " + year + "and budget_type= " + budgetType + " and is_active=1";
    List<Map<String, Object>> list = dao.findCustomQuery(query);
    if (list.size() > 0) {
      Map<String, Object> result = list.get(0);
      return result.get("amount").toString();
    }
    return null;
  }

  @Override
  public String amountByFundingSource(long fundingSourceID, int year) {
    String query = "select SUM(amount) as amount from project_budgets where funding_source_id= " + fundingSourceID
      + " and year= " + year + " and is_active=1";
    List<Map<String, Object>> list = dao.findCustomQuery(query);
    if (list.size() > 0) {
      Map<String, Object> result = list.get(0);
      return result.get("amount").toString();
    }
    return null;
  }

  @Override
  public boolean deleteProjectBudget(long projectBudgetId) {
    ProjectBudget projectBudget = this.find(projectBudgetId);
    projectBudget.setActive(false);
    return this.save(projectBudget) > 0;
  }

  @Override
  public boolean existProjectBudget(long projectBudgetID) {
    ProjectBudget projectBudget = this.find(projectBudgetID);
    if (projectBudget == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectBudget find(long id) {
    return dao.find(ProjectBudget.class, id);

  }

  @Override
  public List<ProjectBudget> findAll() {
    String query = "from " + ProjectBudget.class.getName() + " where is_active=1";
    List<ProjectBudget> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(ProjectBudget projectBudget) {
    if (projectBudget.getId() == null) {
      dao.save(projectBudget);
    } else {
      dao.update(projectBudget);
    }


    return projectBudget.getId();
  }


}