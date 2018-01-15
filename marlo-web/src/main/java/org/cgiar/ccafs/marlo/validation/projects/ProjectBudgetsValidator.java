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
import org.cgiar.ccafs.marlo.data.manager.FundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.model.FundingSource;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudget;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;


/**
 * @author Christian Garcia. - CIAT/CCAFS
 */
@Named
public class ProjectBudgetsValidator extends BaseValidator {

  // This is not thread safe
  private boolean hasErros;

  private final GlobalUnitManager crpManager;
  private final FundingSourceManager fundingSourceManager;

  @Inject
  public ProjectBudgetsValidator(GlobalUnitManager crpManager, FundingSourceManager fundingSourceManager) {
    super();
    this.crpManager = crpManager;
    this.fundingSourceManager = fundingSourceManager;
  }

  private Path getAutoSaveFilePath(Project project, long crpID) {
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
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
    // BaseValidator does not Clean this variables.. so before validate the section, it be clear these variables
    this.missingFields.setLength(0);
    this.validationMessage.setLength(0);
    action.setInvalidFields(new HashMap<>());
    hasErros = false;
    if (project != null) {
      if (!saving) {
        Path path = this.getAutoSaveFilePath(project, action.getCrpID());

        if (path.toFile().exists()) {
          this.addMissingField("draft");
        }
      }
      action.getFieldErrors().clear();
      if (project.getBudgets() != null && project.getBudgets().size() > 0) {
        long total = 0;
        int i = 0;
        for (ProjectBudget projectBudget : project.getBudgets()) {
          if (projectBudget != null) {
            if (projectBudget.getAmount() != null) {
              if (projectBudget.getYear() == action.getCurrentCycleYear()) {
                FundingSource fundingSource =
                  fundingSourceManager.getFundingSourceById(projectBudget.getFundingSource().getId());
                // calculate the remaing. If the budget is new, calculate it with the budgets associated with this FS in
                // the year evaluated. If it is not new this budget is excluded from the calculation
                double remaining = 0;
                if (projectBudget.getId() == null) {
                  remaining = fundingSource.getRemaining(projectBudget.getYear(), action.getActualPhase());
                } else {
                  remaining = fundingSource.getRemainingExcludeBudget(projectBudget.getYear(),
                    projectBudget.getId().longValue(), action.getActualPhase());
                }
                double currentAmount = projectBudget.getAmount().doubleValue();
                double subBudgets = remaining - currentAmount;
                int intValue = (int) subBudgets;
                if (intValue < 0) {
                  this.addMessage(action.getText("projectBudgets.fundig"));
                  action.getInvalidFields().put("input-project.budgets[" + i + "].amount",
                    InvalidFieldsMessages.EMPTYFIELD);

                }

              }
              total = total + projectBudget.getAmount().longValue();
            }

          }
          i++;
        }
        if (total < 0) {
          this.addMessage(action.getText("projectBudgets.amount"));
          i = 0;
          for (ProjectBudget projectBudget : project.getBudgets()) {
            action.getInvalidFields().put("input-project.budgets[" + i + "].amount", InvalidFieldsMessages.EMPTYFIELD);
            i++;
          }
        }
      } else {
        this.addMessage(action.getText("projectBudgets"));
        action.getInvalidFields().put("list-project.budgets",
          action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Budgets"}));
      }


      if (!action.getFieldErrors().isEmpty()) {
        System.out.println(action.getFieldErrors());
        hasErros = true;
        action.addActionError(action.getText("saving.fields.required"));
      } else if (validationMessage.length() > 0) {
        action
          .addActionMessage(" " + action.getText("saving.missingFields", new String[] {validationMessage.toString()}));
      }

      this.saveMissingFields(project, action.getActualPhase().getDescription(), action.getActualPhase().getYear(),
        ProjectSectionStatusEnum.BUDGET.getStatus());

      // Saving missing fields.

    }
  }


}
