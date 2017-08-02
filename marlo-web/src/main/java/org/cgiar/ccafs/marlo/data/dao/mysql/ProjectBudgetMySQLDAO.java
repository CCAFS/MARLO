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

import java.util.ArrayList;
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
  public String amountByBudgetType(long institutionId, int year, long budgetType, long projectId, Integer coFinancing) {
    String query = null;

    switch (coFinancing) {
      case 1:
        query =
          "select SUM(amount) as amount from project_budgets where institution_id= " + institutionId + " and year= "
            + year + " and budget_type= " + budgetType + " and project_id= " + projectId + " and is_active=1";
        break;
      case 2:
        query =
          "select SUM(amount) as amount from project_budgets pb INNER JOIN funding_sources fs ON fs.id = pb.funding_source_id where pb.institution_id= "
            + institutionId + " and pb.year= " + year + " and pb.budget_type= " + budgetType + " and pb.project_id= "
            + projectId + " and pb.is_active=1 AND fs.w1w2";
        break;
      case 3:
        query =
          "select SUM(amount) as amount from project_budgets pb INNER JOIN funding_sources fs ON fs.id = pb.funding_source_id where pb.institution_id= "
            + institutionId + " and pb.year= " + year + " and pb.budget_type= " + budgetType + " and pb.project_id= "
            + projectId + " and pb.is_active=1 AND !fs.w1w2";
        break;

      default:
        break;
    }

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
  public List<ProjectBudget> getByParameters(long institutionID, int year, long budgetTypeId, long projectId,
    Integer coFinancing) {
    String query = null;

    switch (coFinancing) {
      case 1:
        query = "select id from project_budgets where institution_id= " + institutionID + " and year= " + year
          + " and budget_type= " + budgetTypeId + " and project_id= " + projectId + " and is_active=1";
        break;
      case 2:
        query =
          "select pb.id from project_budgets pb INNER JOIN funding_sources fs ON fs.id = pb.funding_source_id where pb.institution_id= "
            + institutionID + " and pb.year= " + year + " and pb.budget_type= " + budgetTypeId + " and pb.project_id= "
            + projectId + " and pb.is_active=1 and fs.w1w2";
        break;
      case 3:
        query =
          "select pb.id from project_budgets pb INNER JOIN funding_sources fs ON fs.id = pb.funding_source_id where pb.institution_id= "
            + institutionID + " and pb.year= " + year + " and pb.budget_type= " + budgetTypeId + " and pb.project_id= "
            + projectId + " and pb.is_active=1 and !fs.w1w2";
        break;

      default:
        break;
    }


    List<Map<String, Object>> pbList = super.findCustomQuery(query.toString());

    List<ProjectBudget> projectBudgetList = new ArrayList<>();

    if (pbList != null) {
      for (Map<String, Object> map : pbList) {
        ProjectBudget projectBudget = this.find(Long.parseLong(map.get("id").toString()));
        projectBudgetList.add(projectBudget);
      }
      return projectBudgetList;
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