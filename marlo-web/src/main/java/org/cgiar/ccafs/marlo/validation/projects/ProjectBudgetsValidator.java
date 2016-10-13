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
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudget;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;
import org.cgiar.ccafs.marlo.validation.model.ProjectValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.inject.Inject;


/**
 * @author Christian Garcia. - CIAT/CCAFS
 */

public class ProjectBudgetsValidator extends BaseValidator {

  private boolean hasErros;
  private ProjectValidator projectValidator;
  private InstitutionManager institutionManager;

  @Inject
  private CrpManager crpManager;

  @Inject
  public ProjectBudgetsValidator(ProjectValidator projectValidator, InstitutionManager institutionManager) {
    super();
    this.projectValidator = projectValidator;
    this.institutionManager = institutionManager;
  }

  private Path getAutoSaveFilePath(Project project, long crpID) {
    Crp crp = crpManager.getCrpById(crpID);
    String composedClassName = project.getClass().getSimpleName();
    String actionFile = ProjectSectionStatusEnum.BUDGET.getStatus().replace("/", "_");
    String autoSaveFile =
      project.getId() + "_" + composedClassName + "_" + crp.getAcronym() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
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
      if ((project.isCoreProject() || project.isCoFundedProject())) {
        if (project.getBudgets() != null && project.getBudgets().size() > 0) {
          long total = 0;
          for (ProjectBudget projectBudget : project.getBudgets()) {
            if (projectBudget != null) {
              if (projectBudget.getAmount() != null) {
                total = total + projectBudget.getAmount().longValue();
              }

            }

          }
          if (total == 0) {
            this.addMessage(action.getText("projectBudgets.amount"));
            int i = 0;
            for (ProjectBudget projectBudget : project.getBudgets()) {
              action.getInvalidFields().put("input-project.budgets[" + i + "].amount",
                InvalidFieldsMessages.EMPTYFIELD);
              i++;
            }
          }
        } else {
          this.addMessage(action.getText("projectBudgets"));
          action.getInvalidFields().put("list-project.budgets",
            action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Budgets"}));
        }


        if (project.getBudgetsCofinancing() != null && project.getBudgetsCofinancing().size() > 0) {
          int i = 0;


          for (ProjectBudget projectBudget : project.getBudgetsCofinancing()) {
            List<String> params = new ArrayList<String>();

            if (projectBudget != null) {
              params.add(institutionManager.getInstitutionById(projectBudget.getInstitution().getId()).getAcronym());
              params.add(String.valueOf(projectBudget.getProjectBilateralCofinancing().getId()));

              if (projectBudget.getYear() == action.getCurrentCycleYear()) {
                if (!this.isValidNumber(String.valueOf(projectBudget.getAmount())) || projectBudget.getAmount() == 0) {
                  this.addMessage(action.getText("projectBudgetCofinaning.requeried.amount", params));
                  action.getInvalidFields().put("input-project.budgetsCofinancing[" + i + "].amount",
                    InvalidFieldsMessages.EMPTYFIELD);
                }
              }

              if (projectBudget.getBudgetType() == null || projectBudget.getBudgetType().getId() == null
                || projectBudget.getBudgetType().getId() == -1) {
                action.addFieldError("project.budgetsCofinancing[" + i + "].budgetType.id",
                  action.getText("validation.required", new String[] {action.getText("budget.budgetType")}));
                this.addMessage(action.getText("projectBudgetCofinaning.requeried.type", params));
              }
            }


            i++;
          }
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
        this.saveMissingFields(project, APConstants.REPORTING, action.getPlanningYear(),
          ProjectSectionStatusEnum.BUDGET.getStatus());
      } else {
        this.saveMissingFields(project, APConstants.PLANNING, action.getPlanningYear(),
          ProjectSectionStatusEnum.BUDGET.getStatus());
      }
      // Saving missing fields.

    }
  }


}
