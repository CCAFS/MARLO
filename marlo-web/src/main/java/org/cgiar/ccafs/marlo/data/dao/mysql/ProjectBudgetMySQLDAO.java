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
import org.hibernate.SessionFactory;

public class ProjectBudgetMySQLDAO extends AbstractMarloDAO implements ProjectBudgetDAO {


  @Inject
  public ProjectBudgetMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public String amountByBudgetType(long institutionId, int year, long budgetType, long projectId) {
    String query = "select SUM(amount) as amount from project_budgets where institution_id= " + institutionId
      + " and year= " + year + " and budget_type= " + budgetType + " and project_id= " + projectId + " and is_active=1";
    List<Map<String, Object>> list = super.findCustomQuery(query);
    if (list.size() > 0) {
      Map<String, Object> result = list.get(0);
      if (result.containsKey("amount")) {
        if (result.get("amount") != null) {
          return String.valueOf(result.get("amount"));
        }
      }
    }
    return "0";
  }

  @Override
  public String amountByFundingSource(long fundingSourceID, int year) {
    String query = "select SUM(amount) as amount from project_budgets where funding_source_id= " + fundingSourceID
      + " and year= " + year + " and is_active=1";
    List<Map<String, Object>> list = super.findCustomQuery(query);
    try {
      if (list.size() > 0) {
        Map<String, Object> result = list.get(0);
        return result.get("amount").toString();
      }
    } catch (Exception e) {
      return "0";
    }
    return "0";
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
    return super.find(ProjectBudget.class, id);

  }

  @Override
  public List<ProjectBudget> findAll() {
    String query = "from " + ProjectBudget.class.getName() + " where is_active=1";
    List<ProjectBudget> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<ProjectBudget> getByParameters(long institutionID, int year, long budgetTypeId, long projectId) {
    String query = "from " + ProjectBudget.class.getName() + " where institution_id= " + institutionID + " and year= "
      + year + " and budget_type= " + budgetTypeId + " and project_id= " + projectId + " and is_active=1";
    List<ProjectBudget> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public long save(ProjectBudget projectBudget) {
    if (projectBudget.getId() == null) {
      super.save(projectBudget);
    } else {
      super.update(projectBudget);
    }


    return projectBudget.getId();
  }


}