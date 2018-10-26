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
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFundingUseExpendituryArea;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisSectionStatusEnum;
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
public class FundingUseValidator extends BaseValidator {

  private final GlobalUnitManager crpManager;
  private final ReportSynthesisManager reportSynthesisManager;

  public FundingUseValidator(GlobalUnitManager crpManager, ReportSynthesisManager reportSynthesisManager) {
    this.crpManager = crpManager;
    this.reportSynthesisManager = reportSynthesisManager;
  }


  private Path getAutoSaveFilePath(ReportSynthesis reportSynthesis, long crpID, BaseAction baseAction) {
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
    String composedClassName = reportSynthesis.getClass().getSimpleName();
    String actionFile = ReportSynthesisSectionStatusEnum.FUNDING_USE.getStatus().replace("/", "_");
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
      action.setInvalidFields(new HashMap<>());
      if (!saving) {
        Path path = this.getAutoSaveFilePath(reportSynthesis, action.getCrpID(), action);
        if (path.toFile().exists()) {
          action.addMissingField("draft");
        }
      }
      if (!this.isValidString(reportSynthesis.getReportSynthesisFundingUseSummary().getMainArea())) {
        action.addMessage(action.getText("annualReport.fundingUse.summarize"));
        action.getInvalidFields().put("input-reportSynthesis.reportSynthesisFundingUseSummary.mainArea",
          InvalidFieldsMessages.EMPTYFIELD);
      }
      int i = 0;
      for (ReportSynthesisFundingUseExpendituryArea fundingUseExpendituryArea : reportSynthesis
        .getReportSynthesisFundingUseSummary().getExpenditureAreas()) {
        this.validateFundingUseExpendituryArea(fundingUseExpendituryArea, action, i);
        i++;
      }
      if (!action.getFieldErrors().isEmpty()) {
        action.addActionError(action.getText("saving.fields.required"));
      } else if (action.getValidationMessage().length() > 0) {
        action.addActionMessage(
          " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));
      }

      this.saveMissingFields(reportSynthesis, action.getActualPhase().getDescription(),
        action.getActualPhase().getYear(), action.getActualPhase().getUpkeep(),
        ReportSynthesisSectionStatusEnum.FUNDING_USE.getStatus(), action);
    }

  }


  private void validateFundingUseExpendituryArea(ReportSynthesisFundingUseExpendituryArea fundingUseExpendituryArea,
    BaseAction action, int i) {
    if (fundingUseExpendituryArea.getW1w2Percentage() != null && fundingUseExpendituryArea.getW1w2Percentage() < 0) {
      action.addMissingField(action.getText("annualReport.fundingUse.tableF.percentage") + "[" + i + "]");
      action.getInvalidFields().put(
        "input-reportSynthesis.reportSynthesisFundingUseSummary.expenditureAreas[" + i + "].w1w2Percentage",
        InvalidFieldsMessages.INVALID_NUMBER);
    }
    if (!(this.isValidString(fundingUseExpendituryArea.getComments()))) {
      action.addMessage(action.getText("annualReport.fundingUse.tableF.comments") + "[" + i + "]");
      action.getInvalidFields().put(
        "input-reportSynthesis.reportSynthesisFundingUseSummary.expenditureAreas[" + i + "].comments",
        InvalidFieldsMessages.EMPTYFIELD);
    }

  }

}
