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

package org.cgiar.ccafs.marlo.validation.powb.y2019;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.PowbFinancialExpenditure;
import org.cgiar.ccafs.marlo.data.model.PowbFinancialPlannedBudget;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesis;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesis2019SectionStatusEnum;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import javax.inject.Named;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@Named
public class PlannedBudgetValidator extends BaseValidator {

  private final GlobalUnitManager crpManager;

  public PlannedBudgetValidator(GlobalUnitManager crpManager) {
    super();
    this.crpManager = crpManager;
  }

  private Path getAutoSaveFilePath(PowbSynthesis powbSynthesis, long crpID, BaseAction baseAction) {
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
    String composedClassName = powbSynthesis.getClass().getSimpleName();
    String actionFile = PowbSynthesis2019SectionStatusEnum.PLANNED_BUDGET.getStatus().replace("/", "_");
    String autoSaveFile = powbSynthesis.getId() + "_" + composedClassName + "_" + baseAction.getActualPhase().getName()
      + "_" + baseAction.getActualPhase().getYear() + "_" + crp.getAcronym() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public void validate(BaseAction action, PowbSynthesis powbSynthesis, boolean saving) {
    action.setInvalidFields(new HashMap<>());
    if (!saving) {
      Path path = this.getAutoSaveFilePath(powbSynthesis, action.getCrpID(), action);
      if (path.toFile().exists()) {
        action.addMissingField("draft");
      }
    }
    if (powbSynthesis != null) {
      if (!(this.isValidString(powbSynthesis.getFinancialPlan().getFinancialPlanIssues()))) {
        action.addMessage(action.getText("powbSynthesis.financialPlan.highlight.readText"));
        action.getInvalidFields().put("input-powbSynthesis.financialPlan.financialPlanIssues",
          InvalidFieldsMessages.EMPTYFIELD);
      }


      int i = 0;
      for (PowbFinancialPlannedBudget powbFinancialPlannedBudget : powbSynthesis.getPowbFinancialPlannedBudgetList()) {
        this.validateFinancialPlannedBudget(powbFinancialPlannedBudget, action, i);
        i++;
      }
      i = 0;

      if (!action.getFieldErrors().isEmpty()) {
        action.addActionError(action.getText("saving.fields.required"));
      } else if (action.getValidationMessage().length() > 0) {
        action.addActionMessage(
          " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));
      }

      this.saveMissingFields(powbSynthesis, action.getActualPhase().getDescription(), action.getActualPhase().getYear(),
        action.getActualPhase().getUpkeep(), PowbSynthesis2019SectionStatusEnum.PLANNED_BUDGET.getStatus(), action);
    }

  }

  private void validateFinancialExpenditure(PowbFinancialExpenditure powbFinancialExpenditure, BaseAction action,
    int i) {
    if (powbFinancialExpenditure.getW1w2Percentage() != null && powbFinancialExpenditure.getW1w2Percentage() < 0) {
      action.addMissingField(action.getText("financialPlan.tableF.estimatedPercentage"));
      action.getInvalidFields().put("input-powbSynthesis.powbFinancialExpendituresList[" + i + "].w1w2Percentage",
        InvalidFieldsMessages.INVALID_NUMBER);
    }
    if (!(this.isValidString(powbFinancialExpenditure.getComments()))) {
      action.addMessage(action.getText("financialPlan.tableF.comments"));
      action.getInvalidFields().put("input-powbSynthesis.powbFinancialExpendituresList[" + i + "].comments",
        InvalidFieldsMessages.EMPTYFIELD);
    }
  }

  private void validateFinancialPlannedBudget(PowbFinancialPlannedBudget powbFinancialPlannedBudget, BaseAction action,
    int i) {
    if (powbFinancialPlannedBudget.getW1w2() != null && powbFinancialPlannedBudget.getW1w2() < 0) {
      action.addMissingField(action.getText("financialPlan.tableE.w1w2"));
      action.getInvalidFields().put("input-powbSynthesis.powbFinancialPlannedBudgetList[" + i + "].w1w2",
        InvalidFieldsMessages.INVALID_NUMBER);
    }
    if (powbFinancialPlannedBudget.getW3Bilateral() != null && powbFinancialPlannedBudget.getW3Bilateral() < 0) {
      action.addMissingField(action.getText("financialPlan.tableE.w3bilateral"));
      action.getInvalidFields().put("input-powbSynthesis.powbFinancialPlannedBudgetList[" + i + "].w3Bilateral",
        InvalidFieldsMessages.INVALID_NUMBER);
    }
    if (!(this.isValidString(powbFinancialPlannedBudget.getComments()))) {
      action.addMessage(action.getText("financialPlan.tableE.comments"));
      action.getInvalidFields().put("input-powbSynthesis.powbFinancialPlannedBudgetList[" + i + "].comments",
        InvalidFieldsMessages.EMPTYFIELD);
    }
  }

}
