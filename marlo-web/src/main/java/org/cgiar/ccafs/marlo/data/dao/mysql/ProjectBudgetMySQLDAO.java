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

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.dao.ProjectBudgetDAO;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudget;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.inject.Inject;

public class ProjectBudgetMySQLDAO implements ProjectBudgetDAO {

  private StandardDAO dao;

  @Inject
  public ProjectBudgetMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public String amountByBudgetType(long institutionId, int year, long budgetType, long projectId, Integer coFinancing,
    long idPhase) {
    String query = null;

    switch (coFinancing) {
      case 1:
        query = "select SUM(amount) as amount from project_budgets where institution_id= " + institutionId
          + " and year= " + year + " and budget_type= " + budgetType + " and project_id= " + projectId
          + " and id_phase=" + idPhase + " and is_active=1";
        break;
      case 2:
        query =
          "select SUM(amount) as amount from project_budgets pb INNER JOIN funding_sources fs ON fs.id = pb.funding_source_id "
            + "INNER JOIN funding_sources_info fsi ON fsi.funding_source_id = fs.id AND fsi.id_phase=" + idPhase
            + " where pb.institution_id= " + institutionId + " and pb.year= " + year + " and pb.budget_type= "
            + budgetType + " and pb.project_id= " + projectId + " and pb.id_phase=" + idPhase
            + " and pb.is_active=1 AND fs.w1w2";
        break;
      case 3:
        query =
          "select SUM(amount) as amount from project_budgets pb INNER JOIN funding_sources fs ON fs.id = pb.funding_source_id "
            + "INNER JOIN funding_sources_info fsi ON fsi.funding_source_id = fs.id AND fsi.id_phase=" + idPhase
            + " where pb.institution_id= " + institutionId + " and pb.year= " + year + " and pb.budget_type= "
            + budgetType + " and pb.project_id= " + projectId + " and pb.id_phase=" + idPhase
            + " and  pb.is_active=1 AND !fs.w1w2";
        break;

      default:
        break;
    }

    List<Map<String, Object>> list = dao.findCustomQuery(query);
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
  public String amountByFundingSource(long fundingSourceID, int year, long idPhase) {
    String query =
      "select SUM(amount) as amount from project_budgets pb INNER JOIN funding_sources fs ON fs.id = pb.funding_source_id "
        + "INNER JOIN funding_sources_info fsi ON fsi.funding_source_id = fs.id AND fsi.id_phase=" + idPhase
        + " where pb.funding_source_id= " + fundingSourceID + " and pb.year= " + year
        + " and pb.is_active=1 and pb.id_phase=" + idPhase;
    List<Map<String, Object>> list = dao.findCustomQuery(query);
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

  public void cloneBudget(ProjectBudget projectBudgetAdd, ProjectBudget budget, Phase phase) {
    projectBudgetAdd.setActive(true);
    projectBudgetAdd.setActiveSince(new Date());
    projectBudgetAdd.setModificationJustification(budget.getModificationJustification());
    projectBudgetAdd.setModifiedBy(budget.getCreatedBy());
    projectBudgetAdd.setCreatedBy(budget.getCreatedBy());
    projectBudgetAdd.setPhase(phase);
    projectBudgetAdd.setProject(dao.find(Project.class, budget.getProject().getId()));
    projectBudgetAdd.setAmount(budget.getAmount());
    projectBudgetAdd.setBudgetType(budget.getBudgetType());
    projectBudgetAdd.setFundingSource(budget.getFundingSource());
    projectBudgetAdd.setGenderPercentage(budget.getGenderPercentage());
    projectBudgetAdd.setGenderValue(budget.getGenderValue());
    projectBudgetAdd.setInstitution(budget.getInstitution());
    projectBudgetAdd.setYear(budget.getYear());


  }

  public void deletBudgetPhase(Phase next, long projecID, ProjectBudget projectBudget) {
    Phase phase = dao.find(Phase.class, next.getId());
    if (phase.getEditable() != null && phase.getEditable()) {
      List<ProjectBudget> budgets = phase.getProjectBudgets().stream()
        .filter(c -> c.isActive() && c.getProject().getId().longValue() == projecID
          && c.getFundingSource().getId().equals(projectBudget.getFundingSource().getId())
          && c.getYear() == projectBudget.getYear() && c.getPhase() != null
          && c.getInstitution().getId().equals(projectBudget.getInstitution().getId()))
        .collect(Collectors.toList());
      for (ProjectBudget projectBudgetDB : budgets) {
        projectBudgetDB.setActive(false);
        dao.update(projectBudgetDB);
      }
    }
    if (phase.getNext() != null) {
      this.deletBudgetPhase(phase.getNext(), projecID, projectBudget);

    }
  }

  @Override
  public boolean deleteProjectBudget(long projectBudgetId) {
    ProjectBudget projectBudget = this.find(projectBudgetId);
    projectBudget.setActive(false);
    boolean result = dao.update(projectBudget);
    Phase currentPhase = dao.find(Phase.class, projectBudget.getPhase().getId());
    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (projectBudget.getPhase().getNext() != null) {
        this.deletBudgetPhase(projectBudget.getPhase().getNext(), projectBudget.getProject().getId(), projectBudget);
      }
    }

    return result;

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
  public List<ProjectBudget> getByParameters(long institutionID, int year, long budgetTypeId, long projectId,
    Integer coFinancing, long idPhase) {
    String query = null;

    switch (coFinancing) {
      case 1:
        query = "select id from project_budgets pb where pb.institution_id= " + institutionID + " and pb.year= " + year
          + " and pb.budget_type= " + budgetTypeId + " and pb.project_id= " + projectId + " and pb.is_active=1 "
          + "and pb.id_phase=" + idPhase;
        break;
      case 2:
        query = "select pb.id from project_budgets pb INNER JOIN funding_sources fs ON fs.id = pb.funding_source_id "
          + "INNER JOIN funding_sources_info fsi ON fsi.funding_source_id = fs.id AND fsi.id_phase=" + idPhase
          + " where pb.institution_id= " + institutionID + " and pb.year= " + year + " and pb.budget_type= "
          + budgetTypeId + " and pb.project_id= " + projectId + " and pb.is_active=1 and fsi.w1w2 and pb.id_phase="
          + idPhase;
        break;
      case 3:
        query = "select pb.id from project_budgets pb INNER JOIN funding_sources fs ON fs.id = pb.funding_source_id "
          + "INNER JOIN funding_sources_info fsi ON fsi.funding_source_id = fs.id AND fsi.id_phase=" + idPhase
          + " where pb.institution_id= " + institutionID + " and pb.year= " + year + " and pb.budget_type= "
          + budgetTypeId + " and pb.project_id= " + projectId + " and pb.is_active=1 and !fsi.w1w2 and pb.id_phase="
          + idPhase;
        break;

      default:
        break;
    }


    List<Map<String, Object>> pbList = dao.findCustomQuery(query.toString());

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
  public double getTotalBudget(long projetId, long phaseID, int type, int year) {
    String query = "select sum(pb.amount)'amount' from project_budgets pb where pb.project_id=" + projetId
      + " and pb.id_phase=" + phaseID + " and pb.`year`=" + year + " and pb.budget_type=" + type;
    List<Map<String, Object>> list = dao.findCustomQuery(query);
    try {
      if (list.size() > 0) {
        Map<String, Object> result = list.get(0);
        return Double.parseDouble(result.get("amount").toString());
      }
    } catch (Exception e) {
      return 0;
    }
    return 0;
  }

  @Override
  public long save(ProjectBudget projectBudget) {
    if (projectBudget.getId() == null) {
      dao.save(projectBudget);
    } else {
      dao.update(projectBudget);
    }


    Phase currentPhase = dao.find(Phase.class, projectBudget.getPhase().getId());
    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (projectBudget.getPhase().getNext() != null) {
        this.saveBudgetPhase(projectBudget.getPhase().getNext(), projectBudget.getProject().getId(), projectBudget);
      }
    }
    return projectBudget.getId();
  }

  public void saveBudgetPhase(Phase next, long projecID, ProjectBudget projectBudget) {
    Phase phase = dao.find(Phase.class, next.getId());
    if (phase.getEditable() != null && phase.getEditable()) {
      List<ProjectBudget> budgets = phase.getProjectBudgets().stream()
        .filter(c -> c.isActive() && c.getProject().getId().longValue() == projecID
          && c.getFundingSource().getId().equals(projectBudget.getFundingSource().getId())
          && c.getYear() == projectBudget.getYear() && c.getPhase() != null
          && c.getInstitution().getId().equals(projectBudget.getInstitution().getId()))
        .collect(Collectors.toList());
      if (budgets.isEmpty()) {
        ProjectBudget budgetAdd = new ProjectBudget();
        this.cloneBudget(budgetAdd, projectBudget, phase);
        dao.save(budgetAdd);
      } else {
        ProjectBudget budgetAdd = budgets.get(0);
        this.cloneBudget(budgetAdd, projectBudget, phase);
        dao.update(budgetAdd);
      }

    }
    if (phase.getNext() != null) {
      this.saveBudgetPhase(phase.getNext(), projecID, projectBudget);
    }


  }

}