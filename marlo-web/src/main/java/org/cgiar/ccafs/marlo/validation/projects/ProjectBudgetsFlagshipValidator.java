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

import org.apache.commons.collections.CollectionUtils;

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

  private Path getAutoSaveFilePath(Project project, long crpID, BaseAction action) {
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
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


    if (project != null) {
      if (!saving) {
        Path path = this.getAutoSaveFilePath(project, action.getCrpID(), action);

        if (path.toFile().exists()) {
          action.addMissingField("draft");
        }
      }

      project.getBudgetsFlagship().removeIf(Objects::isNull);

      Project projectDB = projectManager.getProjectById(project.getId());
      List<ProjectFocus> projectFocuses = new ArrayList<>(projectDB.getProjectFocuses().stream()
        .filter(pf -> pf.isActive()
          && pf.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()
            & pf.getCrpProgram().getResearchArea() == null
          && pf.getPhase() != null && pf.getPhase().equals(action.getActualPhase()))
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
            List<ProjectBudgetsFlagship> w1w2List = project.getBudgetsFlagship().stream()
              .filter(c -> c != null && (c.getBudgetType().getId().longValue() == 1)
                && (c.getYear() == action.getCurrentCycleYear())
                && (c.getPhase() != null && c.getPhase().equals(action.getActualPhase())))
              .collect(Collectors.toList());
            this.validateBudgets(action, w1w2List, new Long(1));
          }
          if (hasW3Budget) {
            List<ProjectBudgetsFlagship> w3List =
              project.getBudgetsFlagship().stream()
                .filter(c -> c.getBudgetType().getId().longValue() == 2 && c.getYear() == action.getCurrentCycleYear()
                  && (c.getPhase() != null && c.getPhase().equals(action.getActualPhase())))
                .collect(Collectors.toList());
            this.validateBudgets(action, w3List, new Long(2));
          }
          if (hasBilateralBudget) {
            List<ProjectBudgetsFlagship> bilateralList =
              project.getBudgetsFlagship().stream()
                .filter(c -> c.getBudgetType().getId().longValue() == 3 && c.getYear() == action.getCurrentCycleYear()
                  && (c.getPhase() != null && c.getPhase().equals(action.getActualPhase())))
                .collect(Collectors.toList());
            this.validateBudgets(action, bilateralList, new Long(3));
          }
          if (hasCenterFundsBudget) {
            List<ProjectBudgetsFlagship> centerFundsList =
              project.getBudgetsFlagship().stream()
                .filter(c -> c.getBudgetType().getId().longValue() == 4 && c.getYear() == action.getCurrentCycleYear()
                  && (c.getPhase() != null && c.getPhase().equals(action.getActualPhase())))
                .collect(Collectors.toList());
            this.validateBudgets(action, centerFundsList, new Long(4));
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

  public void validateBudgets(BaseAction action, List<ProjectBudgetsFlagship> projectBudgetsFlagships, long type) {
    List<String> params = new ArrayList<String>();
    params.add(budgetTypeManager.getBudgetTypeById(type).getName());
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
