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
import org.cgiar.ccafs.marlo.data.manager.BudgetTypeManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudget;
import org.cgiar.ccafs.marlo.data.model.ProjectBudgetsCluserActvity;
import org.cgiar.ccafs.marlo.data.model.ProjectClusterActivity;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.validation.BaseValidator;
import org.cgiar.ccafs.marlo.validation.model.ProjectValidator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;


/**
 * @author Christian Garcia. - CIAT/CCAFS
 */
@Named
public class ProjectBudgetsCoAValidator extends BaseValidator {

  private BudgetTypeManager budgetTypeManager;
  private ProjectManager projectManager;


  // GlobalUnit Manager
  private GlobalUnitManager crpManager;

  @Inject
  public ProjectBudgetsCoAValidator(ProjectValidator projectValidator, BudgetTypeManager budgetTypeManager,
    ProjectManager projectManager, GlobalUnitManager crpManager) {
    super();
    this.crpManager = crpManager;
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
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
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

  public void replaceAll(StringBuilder builder, String from, String to) {
    int index = builder.indexOf(from);
    while (index != -1) {
      builder.replace(index, index + from.length(), to);
      index += to.length(); // Move to the end of the replacement
      index = builder.indexOf(from, index);
    }
  }


  public double round(double value, int places) {
    if (places < 0) {
      throw new IllegalArgumentException();
    }

    BigDecimal bd = new BigDecimal(value);
    bd = bd.setScale(places, RoundingMode.HALF_UP);
    return bd.doubleValue();
  }


  public void validate(BaseAction action, Project project, boolean saving) {
    action.setInvalidFields(new HashMap<>());
    if (project != null) {
      if (!saving) {
        Path path = this.getAutoSaveFilePath(project, action.getCrpID());

        if (path.toFile().exists()) {
          action.addMissingField("draft");
        }
      }
      Project projectDB = projectManager.getProjectById(project.getId());
      List<ProjectClusterActivity> activities =
        projectDB.getProjectClusterActivities().stream().filter(c -> c.isActive()).collect(Collectors.toList());
      if (!activities.isEmpty()) {
        if (CollectionUtils.isNotEmpty(project.getBudgetsCluserActvities())) {
          if (this.hasBudgets(new Long(1), action.getCurrentCycleYear(), project.getId())) {
            List<ProjectBudgetsCluserActvity> w1w2List = project
              .getBudgetsCluserActvities().stream().filter(c -> c != null
                && (c.getBudgetType().getId().longValue() == 1) && (c.getYear() == action.getCurrentCycleYear()))
              .collect(Collectors.toList());
            this.validateBudgets(action, w1w2List, new Long(1),
              this.calculateGender(new Long(1), action.getCurrentCycleYear(), project.getId()));
          }
          if (this.hasBudgets(new Long(2), action.getCurrentCycleYear(), project.getId())) {
            List<ProjectBudgetsCluserActvity> w3List = project.getBudgetsCluserActvities().stream()
              .filter(c -> c.getBudgetType().getId().longValue() == 2 && c.getYear() == action.getCurrentCycleYear())
              .collect(Collectors.toList());
            this.validateBudgets(action, w3List, new Long(2),
              this.calculateGender(new Long(2), action.getCurrentCycleYear(), project.getId()));
          }
          if (this.hasBudgets(new Long(3), action.getCurrentCycleYear(), project.getId())) {
            List<ProjectBudgetsCluserActvity> bilateralList = project.getBudgetsCluserActvities().stream()
              .filter(c -> c.getBudgetType().getId().longValue() == 3 && c.getYear() == action.getCurrentCycleYear())
              .collect(Collectors.toList());
            this.validateBudgets(action, bilateralList, new Long(3),
              this.calculateGender(new Long(3), action.getCurrentCycleYear(), project.getId()));
          }
          if (this.hasBudgets(new Long(4), action.getCurrentCycleYear(), project.getId())) {
            List<ProjectBudgetsCluserActvity> centerFundsList = project.getBudgetsCluserActvities().stream()
              .filter(c -> c.getBudgetType().getId().longValue() == 4 && c.getYear() == action.getCurrentCycleYear())
              .collect(Collectors.toList());
            this.validateBudgets(action, centerFundsList, new Long(4),
              this.calculateGender(new Long(4), action.getCurrentCycleYear(), project.getId()));
          }

        } else {
          action.addMessage(action.getText("project.budgets"));
        }
      }

      if (!action.getFieldErrors().isEmpty()) {
        action.addActionError(action.getText("saving.fields.required"));
      } else if (action.getValidationMessage().length() > 0) {
        action.addActionMessage(
          " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));
      }


      this.saveMissingFields(project, action.getActualPhase().getDescription(), action.getActualPhase().getYear(),
        ProjectSectionStatusEnum.BUDGETBYCOA.getStatus(), action);


      // Saving missing fields.

    }
  }


  public void validateBudgets(BaseAction action, List<ProjectBudgetsCluserActvity> projectBudgetsCluserActvities,
    long type, double genderTotal) {
    List<String> params = new ArrayList<String>();
    params.add(budgetTypeManager.getBudgetTypeById(type).getName());
    double amount = 0;
    double gender = 0;
    DecimalFormat df = new DecimalFormat("0.00");
    for (ProjectBudgetsCluserActvity projectBudgetsCluserActvity : projectBudgetsCluserActvities) {
      if (projectBudgetsCluserActvity.getAmount() == null) {
        projectBudgetsCluserActvity.setAmount(new Double(0));
      }
      if (projectBudgetsCluserActvity.getGenderPercentage() == null) {
        projectBudgetsCluserActvity.setGenderPercentage(new Double(0));
      }
      amount = amount + projectBudgetsCluserActvity.getAmount().doubleValue();
      amount = this.round(amount, 2);
      gender = gender + projectBudgetsCluserActvity.getGenderPercentage().doubleValue();
      gender = this.round(gender, 2);

    }

    if (amount != 100) {
      action.getInvalidFields().put("project.budget.coa.amount", "project.budget.coa.amount");
      action.addMessage(action.getText("project.budget.coa.amount", params));
    }
    if (genderTotal > 0) {
      if (gender != 100) {

        action.getInvalidFields().put("project.budget.coa.gender", "project.budget.coa.gender");
        action.addMessage(action.getText("project.budget.coa.gender", params));
      }
    }


  }
}
