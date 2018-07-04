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

package org.cgiar.ccafs.marlo.validation.annualreport;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFinancialSummaryBudget;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisSectionStatusEnum;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import javax.inject.Named;

/**
 * @author Andres Valencia - CIAT/CCAFS
 */
@Named
public class FinancialSummaryValidator extends BaseValidator {

  private final GlobalUnitManager crpManager;
  private final ReportSynthesisManager reportSynthesisManager;

  public FinancialSummaryValidator(GlobalUnitManager crpManager, ReportSynthesisManager reportSynthesisManager) {
    this.crpManager = crpManager;
    this.reportSynthesisManager = reportSynthesisManager;
  }


  private Path getAutoSaveFilePath(ReportSynthesis reportSynthesis, long crpID, BaseAction baseAction) {
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
    String composedClassName = reportSynthesis.getClass().getSimpleName();
    String actionFile = ReportSynthesisSectionStatusEnum.FINANCIAL_SUMMARY.getStatus().replace("/", "_");
    String autoSaveFile =
      reportSynthesis.getId() + "_" + composedClassName + "_" + baseAction.getActualPhase().getDescription() + "_"
        + baseAction.getActualPhase().getYear() + "_" + crp.getAcronym() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public LiaisonInstitution getLiaisonInstitution(BaseAction action, long synthesisID) {
    ReportSynthesis reportSynthesis = reportSynthesisManager.getReportSynthesisById(synthesisID);
    LiaisonInstitution liaisonInstitution = reportSynthesis.getLiaisonInstitution();
    return liaisonInstitution;
  }

  public boolean isPMU(LiaisonInstitution liaisonInstitution) {
    boolean isFP = false;
    if (liaisonInstitution != null) {
      if (liaisonInstitution.getCrpProgram() == null) {
        isFP = true;
      }
    }
    return isFP;
  }


  public void validate(BaseAction action, ReportSynthesis reportSynthesis, boolean saving) {
    action.setInvalidFields(new HashMap<>());
    if (reportSynthesis != null) {
      if (!saving) {
        Path path = this.getAutoSaveFilePath(reportSynthesis, action.getCrpID(), action);
        if (path.toFile().exists()) {
          action.addMissingField("draft");
        }
      }


      if (this.isPMU(this.getLiaisonInstitution(action, reportSynthesis.getId()))) {

        // Validate Budgets
        if (reportSynthesis.getReportSynthesisFinancialSummary().getBudgets() != null
          || !reportSynthesis.getReportSynthesisFinancialSummary().getBudgets().isEmpty()) {
          for (int i = 0; i < reportSynthesis.getReportSynthesisFinancialSummary().getBudgets().size(); i++) {
            this.validateBudgets(action, reportSynthesis.getReportSynthesisFinancialSummary().getBudgets().get(i), i);
          }
        } else {
          action.addMessage(action.getText("Budgets"));
          action.addMissingField("reportSynthesis.reportSynthesisFinancialSummary.budgets");
          action.getInvalidFields().put("list-reportSynthesis.reportSynthesisFinancialSummary.budgets",
            action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Budgets"}));
        }

      }

      if (!action.getFieldErrors().isEmpty()) {
        action.addActionError(action.getText("saving.fields.required"));
      } else if (action.getValidationMessage().length() > 0) {
        action.addActionMessage(
          " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));
      }

      this.saveMissingFields(reportSynthesis, action.getActualPhase().getDescription(),
        action.getActualPhase().getYear(), ReportSynthesisSectionStatusEnum.FINANCIAL_SUMMARY.getStatus(), action);
    }

  }

  public void validateBudgets(BaseAction action, ReportSynthesisFinancialSummaryBudget budget, int i) {


    // Validate W1 Planned
    if (!this.isValidNumber(String.valueOf(budget.getW1Planned()))) {
      action.addMessage(action.getText("reportSynthesis.reportSynthesisFinancialSummary.budgets[" + i + "].w1Planned"));
      action.getInvalidFields().put(
        "input-reportSynthesis.reportSynthesisFinancialSummary.budgets[" + i + "].w1Planned",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate W3 Planned
    if (!this.isValidNumber(String.valueOf(budget.getW3Planned()))) {
      action.addMessage(action.getText("reportSynthesis.reportSynthesisFinancialSummary.budgets[" + i + "].w3Planned"));
      action.getInvalidFields().put(
        "input-reportSynthesis.reportSynthesisFinancialSummary.budgets[" + i + "].w3Planned",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Bilateral Planned
    if (!this.isValidNumber(String.valueOf(budget.getBilateralPlanned()))) {
      action.addMessage(
        action.getText("reportSynthesis.reportSynthesisFinancialSummary.budgets[" + i + "].bilateralPlanned"));
      action.getInvalidFields().put(
        "input-reportSynthesis.reportSynthesisFinancialSummary.budgets[" + i + "].bilateralPlanned",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate W1 Actual
    if (!this.isValidNumber(String.valueOf(budget.getW1Actual()))) {
      action.addMessage(action.getText("reportSynthesis.reportSynthesisFinancialSummary.budgets[" + i + "].w1Actual"));
      action.getInvalidFields().put("input-reportSynthesis.reportSynthesisFinancialSummary.budgets[" + i + "].w1Actual",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate W3 Actual
    if (!this.isValidNumber(String.valueOf(budget.getW3Actual()))) {
      action.addMessage(action.getText("reportSynthesis.reportSynthesisFinancialSummary.budgets[" + i + "].w3Actual"));
      action.getInvalidFields().put("input-reportSynthesis.reportSynthesisFinancialSummary.budgets[" + i + "].w3Actual",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Bilateral Actual
    if (!this.isValidNumber(String.valueOf(budget.getBilateralActual()))) {
      action.addMessage(
        action.getText("reportSynthesis.reportSynthesisFinancialSummary.budgets[" + i + "].bilateralActual"));
      action.getInvalidFields().put(
        "input-reportSynthesis.reportSynthesisFinancialSummary.budgets[" + i + "].bilateralActual",
        InvalidFieldsMessages.EMPTYFIELD);
    }
  }

}
