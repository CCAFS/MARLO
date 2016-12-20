/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/

package org.cgiar.ccafs.marlo.validation.projects;


import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.BudgetTypeManager;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudget;
import org.cgiar.ccafs.marlo.data.model.ProjectBudgetsCluserActvity;
import org.cgiar.ccafs.marlo.data.model.ProjectClusterActivity;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.validation.BaseValidator;
import org.cgiar.ccafs.marlo.validation.model.ProjectValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;


/**
 * @author Christian Garcia. - CIAT/CCAFS
 */

public class ProjectBudgetsCoAValidator extends BaseValidator {

  private boolean hasErros;

  private BudgetTypeManager budgetTypeManager;
  private ProjectManager projectManager;


  @Inject
  private CrpManager crpManager;

  @Inject
  public ProjectBudgetsCoAValidator(ProjectValidator projectValidator, BudgetTypeManager budgetTypeManager,
    ProjectManager projectManager) {
    super();

    this.projectManager = projectManager;
    this.budgetTypeManager = budgetTypeManager;
  }

  public double calculateGender(Long type, int year, long projectID) {
    double gender = 0;
    Project projectBD = projectManager.getProjectById(projectID);
    List<ProjectBudget> budgets = projectBD.getProjectBudgets()
      .stream().filter(c -> c.isActive() && c.getYear() == year
        && c.getBudgetType().getId().longValue() == type.longValue() && (c.getAmount() != null && c.getAmount() > 0))
      .collect(Collectors.toList());

    for (ProjectBudget projectBudget : budgets) {
      if (projectBudget != null && projectBudget.getGenderValue() != null) {
        gender = projectBudget.getGenderValue().doubleValue() + gender;
      }

    }
    return gender;
  }

  private Path getAutoSaveFilePath(Project project, long crpID) {
    Crp crp = crpManager.getCrpById(crpID);
    String composedClassName = project.getClass().getSimpleName();
    String actionFile = ProjectSectionStatusEnum.BUDGETBYCOA.getStatus().replace("/", "_");
    String autoSaveFile =
      project.getId() + "_" + composedClassName + "_" + crp.getAcronym() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public boolean hasBudgets(Long type, int year, long projectID) {
    Project projectBD = projectManager.getProjectById(projectID);
    List<ProjectBudget> budgets = projectBD.getProjectBudgets()
      .stream().filter(c -> c.isActive() && c.getYear() == year
        && c.getBudgetType().getId().longValue() == type.longValue() && (c.getAmount() != null && c.getAmount() > 0))
      .collect(Collectors.toList());

    return budgets.size() > 0;
  }

  public boolean isHasErros() {
    return hasErros;
  }

  public void replaceAll(StringBuilder builder, String from, String to) {
    int index = builder.indexOf(from);
    while (index != -1) {
      builder.replace(index, index + from.length(), to);
      index += to.length(); // Move to the end of the replacement
      index = builder.indexOf(from, index);
    }
  }


  public void setHasErros(boolean hasErros) {
    this.hasErros = hasErros;
  }


  public void validate(BaseAction action, Project project, boolean saving) {
    action.setInvalidFields(new HashMap<>());
    hasErros = false;
    if (project != null) {
      if (!saving) {
        Path path = this.getAutoSaveFilePath(project, action.getCrpID());

        if (path.toFile().exists()) {
          this.addMissingField("draft");
        }
      }
      Project projectDB = projectManager.getProjectById(project.getId());
      List<ProjectClusterActivity> activities =
        projectDB.getProjectClusterActivities().stream().filter(c -> c.isActive()).collect(Collectors.toList());
      if (!activities.isEmpty()) {
        if (project.getBudgetsCluserActvities() == null) {
          project.setBudgetsCluserActvities(new ArrayList<ProjectBudgetsCluserActvity>());
        }
        if (!project.getBudgetsCluserActvities().isEmpty()) {
          if (this.hasBudgets(new Long(1), action.getCurrentCycleYear(), project.getId())) {
            this.validateBudgets(action,
              project.getBudgetsCluserActvities().stream()
                .filter(c -> c.isActive() && c.getBudgetType().getId().longValue() == 1).collect(Collectors.toList()),
              new Long(1), this.calculateGender(new Long(1), action.getCurrentCycleYear(), project.getId()));
          }
          if (this.hasBudgets(new Long(2), action.getCurrentCycleYear(), project.getId())) {
            this.validateBudgets(action,
              project.getBudgetsCluserActvities().stream()
                .filter(c -> c.isActive() && c.getBudgetType().getId().longValue() == 2).collect(Collectors.toList()),
              new Long(2), this.calculateGender(new Long(2), action.getCurrentCycleYear(), project.getId()));
          }
          if (this.hasBudgets(new Long(3), action.getCurrentCycleYear(), project.getId())) {
            this.validateBudgets(action,
              project.getBudgetsCluserActvities().stream()
                .filter(c -> c.isActive() && c.getBudgetType().getId().longValue() == 3).collect(Collectors.toList()),
              new Long(3), this.calculateGender(new Long(3), action.getCurrentCycleYear(), project.getId()));
          }
          if (this.hasBudgets(new Long(4), action.getCurrentCycleYear(), project.getId())) {
            this.validateBudgets(action,
              project.getBudgetsCluserActvities().stream()
                .filter(c -> c.isActive() && c.getBudgetType().getId().longValue() == 4).collect(Collectors.toList()),
              new Long(4), this.calculateGender(new Long(4), action.getCurrentCycleYear(), project.getId()));
          }
        } else {
          this.addMessage(action.getText("project.budgets"));
        }
      }

      if (!action.getFieldErrors().isEmpty()) {
        hasErros = true;
        action.addActionError(action.getText("saving.fields.required"));
      } else if (validationMessage.length() > 0) {
        action
          .addActionMessage(" " + action.getText("saving.missingFields", new String[] {validationMessage.toString()}));
      }
      if (action.isReportingActive()) {
        this.saveMissingFields(project, APConstants.REPORTING, action.getReportingYear(),
          ProjectSectionStatusEnum.BUDGETBYCOA.getStatus());
      } else {
        this.saveMissingFields(project, APConstants.PLANNING, action.getPlanningYear(),
          ProjectSectionStatusEnum.BUDGETBYCOA.getStatus());
      }
      // Saving missing fields.

    }
  }

  public void validateBudgets(BaseAction action, List<ProjectBudgetsCluserActvity> projectBudgetsCluserActvities,
    long type, double genderTotal) {
    List<String> params = new ArrayList<String>();
    params.add(budgetTypeManager.getBudgetTypeById(type).getName());
    double amount = 0;
    double gender = 0;

    for (ProjectBudgetsCluserActvity projectBudgetsCluserActvity : projectBudgetsCluserActvities) {
      if (projectBudgetsCluserActvity.getAmount() == null) {
        projectBudgetsCluserActvity.setAmount(new Double(0));
      }
      if (projectBudgetsCluserActvity.getGenderPercentage() == null) {
        projectBudgetsCluserActvity.setGenderPercentage(new Double(0));
      }
      amount = amount + projectBudgetsCluserActvity.getAmount().doubleValue();
      gender = gender + projectBudgetsCluserActvity.getGenderPercentage().doubleValue();

    }

    if (amount != 100) {
      action.getInvalidFields().put("project.budget.coa.amount", "project.budget.coa.amount");
      this.addMessage(action.getText("project.budget.coa.amount", params));
    }
    if (genderTotal > 0) {
      if (gender != 100) {

        action.getInvalidFields().put("project.budget.coa.gender", "project.budget.coa.gender");
        this.addMessage(action.getText("project.budget.coa.gender", params));
      }
    }


  }
}
