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

package org.cgiar.ccafs.marlo.validation.projects;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.BudgetTypeManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.BudgetType;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudget;
import org.cgiar.ccafs.marlo.data.model.ProjectBudgetsFlagship;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.validation.BaseValidator;
import org.cgiar.ccafs.marlo.validation.model.ProjectValidator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections4.CollectionUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@Named
public class ProjectBudgetsFlagshipValidator extends BaseValidator {

  private final BudgetTypeManager budgetTypeManager;
  private final ProjectManager projectManager;
  private final GlobalUnitManager crpManager;
  private boolean sMessage;

  @Inject
  public ProjectBudgetsFlagshipValidator(ProjectValidator projectValidator, BudgetTypeManager budgetTypeManager,
    ProjectManager projectManager, GlobalUnitManager crpManager) {
    super();
    this.crpManager = crpManager;
    this.projectManager = projectManager;
    this.budgetTypeManager = budgetTypeManager;
  }

  /**
   * @return the path of the section's autosave file, or null when its name cannot be built: no Global Unit or phase.
   */
  private Path getAutoSaveFilePath(Project project, Long crpID, BaseAction action) {
    if (crpID == null || action.getActualPhase() == null) {
      return null;
    }
    GlobalUnit crp = this.getAutoSaveGlobalUnit(action, crpID.longValue(), crpManager);
    if (crp == null) {
      return null;
    }
    String composedClassName = project.getClass().getSimpleName();
    String actionFile = ProjectSectionStatusEnum.BUDGETBYFLAGSHIP.getStatus().replace("/", "_");
    String autoSaveFile = project.getId() + "_" + composedClassName + "_" + action.getActualPhase().getName() + "_"
      + action.getActualPhase().getYear() + "_" + crp.getAcronym() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public boolean hasBudgets(Long type, int year, long projectID, Phase actualPhase) {
    Project projectBD = projectManager.getProjectById(projectID);
    List<ProjectBudget> budgets = projectBD.getProjectBudgets().stream()
      .filter(c -> c != null && c.isActive() && c.getYear() == year && c.getBudgetType() != null
        && c.getBudgetType().getId() != null && c.getBudgetType().getId().longValue() == type.longValue()
        && (c.getAmount() != null && c.getAmount() > 0) && c.getPhase() != null && c.getPhase().equals(actualPhase))
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

  public void validate(BaseAction action, Project project, boolean saving, boolean sMessage) {

    if (sMessage) {
      action.setMissingFields(new StringBuilder());
      action.setInvalidFields(new HashMap<>());
    }

    this.sMessage = sMessage;


    // The section status is stored per phase, so there is nothing to validate without one
    if (project != null && project.getId() != null && action.getActualPhase() != null) {
      if (!saving) {
        Path path = this.getAutoSaveFilePath(project, action.getCrpID(), action);

        if (path != null && path.toFile().exists()) {
          action.addMissingField("draft");
        }
      }

      // A save that posts no budget row binds no list at all
      if (project.getBudgetsFlagship() != null) {
        project.getBudgetsFlagship().removeIf(Objects::isNull);
      }

      Project projectDB = projectManager.getProjectById(project.getId());
      if (projectDB == null) {
        return;
      }
      List<ProjectFocus> projectFocuses = new ArrayList<>(projectDB.getProjectFocuses().stream()
        .filter(pf -> pf.isActive() && pf.getCrpProgram() != null
          && pf.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()
          && pf.getCrpProgram().getResearchArea() == null && pf.getPhase() != null
          && pf.getPhase().equals(action.getActualPhase()))
        .collect(Collectors.toList()));
      if (!projectFocuses.isEmpty()) {
        Boolean hasW1W2Budget =
          this.hasBudgets(new Long(1), action.getCurrentCycleYear(), project.getId(), action.getActualPhase());
        Boolean hasW3Budget =
          this.hasBudgets(new Long(2), action.getCurrentCycleYear(), project.getId(), action.getActualPhase());
        Boolean hasBilateralBudget =
          this.hasBudgets(new Long(3), action.getCurrentCycleYear(), project.getId(), action.getActualPhase());
        Boolean hasCenterFundsBudget =
          this.hasBudgets(new Long(4), action.getCurrentCycleYear(), project.getId(), action.getActualPhase());
        if (CollectionUtils.isNotEmpty(project.getBudgetsFlagship())) {
          if (hasW1W2Budget) {
            this.validateBudgets(action, this.budgetsOfType(action, project, 1L), 1L);
          }
          if (hasW3Budget) {
            this.validateBudgets(action, this.budgetsOfType(action, project, 2L), 2L);
          }
          if (hasBilateralBudget) {
            this.validateBudgets(action, this.budgetsOfType(action, project, 3L), 3L);
          }
          if (hasCenterFundsBudget) {
            this.validateBudgets(action, this.budgetsOfType(action, project, 4L), 4L);
          }

        } else {
          // Check if there are budget allocated
          if (hasW1W2Budget || hasW3Budget || hasBilateralBudget || hasCenterFundsBudget) {

            action.addMessage(action.getText("project.budgets.flagship"));
            if (sMessage) {
              action.getInvalidFields().put("project.budgets", action.getText("project.budgets.flagship"));
            }
          }
        }
      }

      if (!action.getFieldErrors().isEmpty()) {
        if (sMessage) {
          action.addActionError(action.getText("saving.fields.required"));
        }
      } else if (action.getValidationMessage().length() > 0) {
        if (sMessage) {
          action.addActionMessage(
            " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));
        }
      }

      this.saveMissingFields(project, action.getActualPhase().getDescription(), action.getActualPhase().getYear(),
        action.getActualPhase().getUpkeep(), ProjectSectionStatusEnum.BUDGETBYFLAGSHIP.getStatus(), action);
    }
  }

  /**
   * The flagship budgets of one budget type for the current cycle year and phase. A row without a budget type cannot
   * belong to any type, so it is left out instead of failing the whole validation.
   */
  private List<ProjectBudgetsFlagship> budgetsOfType(BaseAction action, Project project, long type) {
    return project.getBudgetsFlagship().stream()
      .filter(c -> c != null && c.getBudgetType() != null && c.getBudgetType().getId() != null
        && c.getBudgetType().getId().longValue() == type && c.getYear() == action.getCurrentCycleYear()
        && c.getPhase() != null && c.getPhase().equals(action.getActualPhase()))
      .collect(Collectors.toList());
  }

  public void validateBudgets(BaseAction action, List<ProjectBudgetsFlagship> projectBudgetsFlagships, long type) {
    List<String> params = new ArrayList<String>();
    BudgetType budgetType = budgetTypeManager.getBudgetTypeById(type);
    // The message names the budget type; its id stands in for a type that is not in the catalog
    params.add(budgetType != null && budgetType.getName() != null ? budgetType.getName() : String.valueOf(type));
    double amount = 0;
    for (ProjectBudgetsFlagship projectBudgetsFlagship : projectBudgetsFlagships) {
      if (projectBudgetsFlagship.getAmount() == null) {
        projectBudgetsFlagship.setAmount(new Double(0));
      }
      amount = amount + projectBudgetsFlagship.getAmount().doubleValue();
      amount = this.round(amount, 2);
    }
    if (amount != 100) {
      if (sMessage) {
        action.getInvalidFields().put("project.budget.flagship.amount", "project.budget.flagship.amount");
      }
      action.addMessage(action.getText("project.budget.flagship.amount", params));

    }
  }

}
