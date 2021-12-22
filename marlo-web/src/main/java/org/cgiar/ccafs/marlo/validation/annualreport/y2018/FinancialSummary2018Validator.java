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

package org.cgiar.ccafs.marlo.validation.annualreport.y2018;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis2018SectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFinancialSummaryBudget;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Named;

/**
 * @author Andres Valencia - CIAT/CCAFS
 */
@Named
public class FinancialSummary2018Validator extends BaseValidator {

  private final GlobalUnitManager crpManager;
  private final ReportSynthesisManager reportSynthesisManager;

  public FinancialSummary2018Validator(GlobalUnitManager crpManager, ReportSynthesisManager reportSynthesisManager) {
    this.crpManager = crpManager;
    this.reportSynthesisManager = reportSynthesisManager;
  }


  private Path getAutoSaveFilePath(ReportSynthesis reportSynthesis, long crpID, BaseAction baseAction) {
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
    String composedClassName = reportSynthesis.getClass().getSimpleName();
    String actionFile = ReportSynthesis2018SectionStatusEnum.FINANCIAL.getStatus().replace("/", "_");
    String autoSaveFile =
      reportSynthesis.getId() + "_" + composedClassName + "_" + baseAction.getActualPhase().getName() + "_"
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

      if (action.isSelectedPhaseAR2021()) {
        if (reportSynthesis.getReportSynthesisCrpFinancialReport() != null) {
          // Validate Narrative
          if (!this
            .isValidString(reportSynthesis.getReportSynthesisCrpFinancialReport().getFinancialStatusNarrative())) {
            action
              .addMessage(action.getText("reportSynthesis.reportSynthesisCrpFinancialReport.financialStatusNarrative"));
            action.getInvalidFields().put(
              "input-reportSynthesis.reportSynthesisCrpFinancialReport.financialStatusNarrative",
              InvalidFieldsMessages.EMPTYFIELD);
          }

          // Validate capital comments
          if (!this
            .isValidString(reportSynthesis.getReportSynthesisCrpFinancialReport().getCapitalEquipmentComments())) {
            action
              .addMessage(action.getText("reportSynthesis.reportSynthesisCrpFinancialReport.capitalEquipmentComments"));
            action.getInvalidFields().put(
              "input-reportSynthesis.reportSynthesisCrpFinancialReport.capitalEquipmentComments",
              InvalidFieldsMessages.EMPTYFIELD);
          }

          // Validate closeout comments
          if (!this.isValidString(reportSynthesis.getReportSynthesisCrpFinancialReport().getCloseoutComments())) {
            action.addMessage(action.getText("reportSynthesis.reportSynthesisCrpFinancialReport.closeoutComments"));
            action.getInvalidFields().put("input-reportSynthesis.reportSynthesisCrpFinancialReport.closeoutComments",
              InvalidFieldsMessages.EMPTYFIELD);
          }

          // Validate collaborator & partnerships comments
          if (!this.isValidString(
            reportSynthesis.getReportSynthesisCrpFinancialReport().getCollaboratorPartnershipsComments())) {
            action.addMessage(
              action.getText("reportSynthesis.reportSynthesisCrpFinancialReport.collaboratorPartnershipsComments"));
            action.getInvalidFields().put(
              "input-reportSynthesis.reportSynthesisCrpFinancialReport.collaboratorPartnershipsComments",
              InvalidFieldsMessages.EMPTYFIELD);
          }

          // Validate consultancy comments
          if (!this.isValidString(reportSynthesis.getReportSynthesisCrpFinancialReport().getConsultancyComments())) {
            action.addMessage(action.getText("reportSynthesis.reportSynthesisCrpFinancialReport.consultancyComments"));
            action.getInvalidFields().put("input-reportSynthesis.reportSynthesisCrpFinancialReport.consultancyComments",
              InvalidFieldsMessages.EMPTYFIELD);
          }

          // Validate crp total comments
          if (!this.isValidString(reportSynthesis.getReportSynthesisCrpFinancialReport().getCrpTotalComments())) {
            action.addMessage(action.getText("reportSynthesis.reportSynthesisCrpFinancialReport.crpTotalComments"));
            action.getInvalidFields().put("input-reportSynthesis.reportSynthesisCrpFinancialReport.crpTotalComments",
              InvalidFieldsMessages.EMPTYFIELD);
          }

          // Validate operation comments
          if (!this.isValidString(reportSynthesis.getReportSynthesisCrpFinancialReport().getOperationComments())) {
            action.addMessage(action.getText("reportSynthesis.reportSynthesisCrpFinancialReport.operationComments"));
            action.getInvalidFields().put("input-reportSynthesis.reportSynthesisCrpFinancialReport.operationComments",
              InvalidFieldsMessages.EMPTYFIELD);
          }

          // Validate personnel comments
          if (!this.isValidString(reportSynthesis.getReportSynthesisCrpFinancialReport().getPersonnelComments())) {
            action.addMessage(action.getText("reportSynthesis.reportSynthesisCrpFinancialReport.personnelComments"));
            action.getInvalidFields().put("input-reportSynthesis.reportSynthesisCrpFinancialReport.personnelComments",
              InvalidFieldsMessages.EMPTYFIELD);
          }

          // Validate travel comments
          if (!this.isValidString(reportSynthesis.getReportSynthesisCrpFinancialReport().getTravelComments())) {
            action.addMessage(action.getText("reportSynthesis.reportSynthesisCrpFinancialReport.travelComments"));
            action.getInvalidFields().put("input-reportSynthesis.reportSynthesisCrpFinancialReport.travelComments",
              InvalidFieldsMessages.EMPTYFIELD);
          }
        }
      } else {
        if (this.isPMU(this.getLiaisonInstitution(action, reportSynthesis.getId()))) {

          // Validate Narrative
          if (!this.isValidString(reportSynthesis.getReportSynthesisFinancialSummary().getNarrative())) {
            action.addMessage(action.getText(
              "reportSynthesis.reportSynthesisFinancialSummary.annualReport2018.financial.financialStatus.readText"));
            action.getInvalidFields().put("input-reportSynthesis.reportSynthesisFinancialSummary.narrative",
              InvalidFieldsMessages.EMPTYFIELD);
          }

          List<ReportSynthesisFinancialSummaryBudget> budget = new ArrayList<>(
            reportSynthesis.getReportSynthesisFinancialSummary().getReportSynthesisFinancialSummaryBudgets());
          // Validate Budgets
          if (budget != null && !budget.isEmpty()) {
            for (int i = 0; i < budget.size(); i++) {
              this.validateBudgets(action, budget.get(i), i);
            }
          } else {
            action.addMessage(action.getText("Budgets"));
            action.addMissingField("reportSynthesis.reportSynthesisFinancialSummary.budgets");
            action.getInvalidFields().put("list-reportSynthesis.reportSynthesisFinancialSummary.budgets",
              action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Budgets"}));
          }

        }

        // Save Synthesis Flagship
        if (reportSynthesis.getLiaisonInstitution() != null
          && reportSynthesis.getLiaisonInstitution().getAcronym() != null && !action.isPMU()) {

          String sSynthesisFlagships = action.getSynthesisFlagships().toString();


          if (reportSynthesis.getLiaisonInstitution().getAcronym().contains("1")) {
            if (action.getSynthesisFlagships() != null && action.getSynthesisFlagships().toString().length() > 0) {
              if (!sSynthesisFlagships.contains("1")) {
                action.addSynthesisFlagship("F1");
              }
            } else {
              action.addSynthesisFlagship("F1");
            }
          }
          if (reportSynthesis.getLiaisonInstitution().getAcronym().contains("2")) {
            if (action.getSynthesisFlagships() != null && action.getSynthesisFlagships().toString().length() > 0) {
              if (!sSynthesisFlagships.contains("2")) {
                action.addSynthesisFlagship("F2");
              }
            } else {
              action.addSynthesisFlagship("F2");
            }
          }
          if (reportSynthesis.getLiaisonInstitution().getAcronym().contains("3")) {
            if (action.getSynthesisFlagships() != null && action.getSynthesisFlagships().toString().length() > 0) {
              if (!sSynthesisFlagships.contains("3")) {
                action.addSynthesisFlagship("F3");
              }
            } else {
              action.addSynthesisFlagship("F3");
            }
          }
          if (reportSynthesis.getLiaisonInstitution().getAcronym().contains("4")) {
            if (action.getSynthesisFlagships() != null && action.getSynthesisFlagships().toString().length() > 0) {
              if (!sSynthesisFlagships.contains("4")) {
                action.addSynthesisFlagship("F4");
              }
            } else {
              action.addSynthesisFlagship("F4");
            }
          }
          if (reportSynthesis.getLiaisonInstitution().getAcronym().contains("5")) {
            if (action.getSynthesisFlagships() != null && action.getSynthesisFlagships().toString().length() > 0) {
              if (!sSynthesisFlagships.contains("5")) {
                action.addSynthesisFlagship("F5");
              }
            } else {
              action.addSynthesisFlagship("F5");
            }
          }
          if (reportSynthesis.getLiaisonInstitution().getAcronym().contains("6")) {
            if (action.getSynthesisFlagships() != null && action.getSynthesisFlagships().toString().length() > 0) {
              if (!sSynthesisFlagships.contains("6")) {
                action.addSynthesisFlagship("F6");
              }
            } else {
              action.addSynthesisFlagship("F6");
            }
          }
          if (reportSynthesis.getLiaisonInstitution().getAcronym().contains("PMU")) {
            if (action.getSynthesisFlagships() != null && action.getSynthesisFlagships().toString().length() > 0) {
              if (!sSynthesisFlagships.contains("PMU")) {
                action.addSynthesisFlagship("PMU");
              }
            } else {
              action.addSynthesisFlagship("PMU");
            }
          }
        }
      }

      if (!action.getFieldErrors().isEmpty()) {
        action.addActionError(action.getText("saving.fields.required"));
      } else if (action.getValidationMessage().length() > 0) {
        action.addActionMessage(
          " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));
      }

      this.saveMissingFields(reportSynthesis, action.getActualPhase().getDescription(),
        action.getActualPhase().getYear(), action.getActualPhase().getUpkeep(),
        ReportSynthesis2018SectionStatusEnum.FINANCIAL.getStatus(), action);
    }

  }

  public void validateBudgets(BaseAction action, ReportSynthesisFinancialSummaryBudget budget, int i) {
    // Validate Comments
    if (!this.isValidString(budget.getComments())) {
      action.addMessage(action.getText("annualReport2018.financial.comments") + "[" + i + "]");
      action.getInvalidFields().put("input-reportSynthesis.reportSynthesisFinancialSummary.budgets[" + i + "].comments",
        InvalidFieldsMessages.EMPTYFIELD);
    }
    // Validate W1 Planned
    if (!this.isValidNumber(String.valueOf(budget.getW1Planned()))) {
      action.addMessage("w1Planned" + "[" + i + "]");
      action.getInvalidFields().put(
        "input-reportSynthesis.reportSynthesisFinancialSummary.budgets[" + i + "].w1Planned",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate W3 Planned
    if (!this.isValidNumber(String.valueOf(budget.getW3Planned()))) {
      action.addMessage("w3Planned" + "[" + i + "]");
      action.getInvalidFields().put(
        "input-reportSynthesis.reportSynthesisFinancialSummary.budgets[" + i + "].w3Planned",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Bilateral Planned
    if (!this.isValidNumber(String.valueOf(budget.getBilateralPlanned()))) {
      // action.addMessage("bilateralPlanned" + "[" + i + "]");
      // action.getInvalidFields().put(
      // "input-reportSynthesis.reportSynthesisFinancialSummary.budgets[" + i + "].bilateralPlanned",
      // InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate W1 Actual
    if (!this.isValidNumber(String.valueOf(budget.getW1Actual()))) {
      action.addMessage("w1Actual" + "[" + i + "]");
      action.getInvalidFields().put("input-reportSynthesis.reportSynthesisFinancialSummary.budgets[" + i + "].w1Actual",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate W3 Actual
    if (!this.isValidNumber(String.valueOf(budget.getW3Actual()))) {
      action.addMessage("w3Actual" + "[" + i + "]");
      action.getInvalidFields().put("input-reportSynthesis.reportSynthesisFinancialSummary.budgets[" + i + "].w3Actual",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Bilateral Actual
    if (!this.isValidNumber(String.valueOf(budget.getBilateralActual()))) {
      // action.addMessage("bilateralActual" + "[" + i + "]");
      // action.getInvalidFields().put(
      // "input-reportSynthesis.reportSynthesisFinancialSummary.budgets[" + i + "].bilateralActual",
      // InvalidFieldsMessages.EMPTYFIELD);
    }

  }

}
