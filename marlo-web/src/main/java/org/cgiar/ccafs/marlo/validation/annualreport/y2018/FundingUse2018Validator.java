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
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisExpenditureCategoryManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis2018SectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisExpenditureCategory;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFundingUseExpendituryArea;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import javax.inject.Named;

/**
 * @author Andrés Valencia - CIAT/CCAFS
 */
@Named
public class FundingUse2018Validator extends BaseValidator {

  private final GlobalUnitManager crpManager;
  private final ReportSynthesisManager reportSynthesisManager;
  private final ReportSynthesisExpenditureCategoryManager reportSynthesisExpenditureCategoryManager;

  public FundingUse2018Validator(GlobalUnitManager crpManager, ReportSynthesisManager reportSynthesisManager,
    ReportSynthesisExpenditureCategoryManager reportSynthesisExpenditureCategoryManager) {
    this.crpManager = crpManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.reportSynthesisExpenditureCategoryManager = reportSynthesisExpenditureCategoryManager;
  }


  private Path getAutoSaveFilePath(ReportSynthesis reportSynthesis, long crpID, BaseAction baseAction) {
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
    String composedClassName = reportSynthesis.getClass().getSimpleName();
    String actionFile = ReportSynthesis2018SectionStatusEnum.FUNDING_USE.getStatus().replace("/", "_");
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

      if (!this.isValidString(reportSynthesis.getReportSynthesisFundingUseSummary().getInterestingPoints())) {
        action.addMessage(action.getText("annualReport2018.fundingUse.interestingPoints.readText"));
        action.getInvalidFields().put("input-reportSynthesis.reportSynthesisFundingUseSummary.interestingPoints",
          InvalidFieldsMessages.EMPTYFIELD);
      }

      // Validate expenditure areas
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
        ReportSynthesis2018SectionStatusEnum.FUNDING_USE.getStatus(), action);
    }

  }


  private void validateFundingUseExpendituryArea(ReportSynthesisFundingUseExpendituryArea fundingUseExpendituryArea,
    BaseAction action, int i) {

    if (!(this.isValidString(fundingUseExpendituryArea.getExampleExpenditure()))) {
      action.addMessage(action.getText("annualReport2018.fundingUse.table11.examples.readText") + "[" + i + "]");
      action.getInvalidFields().put(
        "input-reportSynthesis.reportSynthesisFundingUseSummary.expenditureAreas[" + i + "].exampleExpenditure",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    if (fundingUseExpendituryArea.getExpenditureCategory() == null
      || fundingUseExpendituryArea.getExpenditureCategory().getId() == null
      || fundingUseExpendituryArea.getExpenditureCategory().getId().longValue() == -1) {
      action.addMissingField(action.getText("annualReport2018.fundingUse.table11.broadArea") + "[" + i + "]");
      action.getInvalidFields().put(
        "input-reportSynthesis.reportSynthesisFundingUseSummary.expenditureAreas[" + i + "].expenditureCategory.id",
        InvalidFieldsMessages.INVALID_NUMBER);
    } else {
      // Validate other category
      ReportSynthesisExpenditureCategory reportSynthesisExpenditureCategory = reportSynthesisExpenditureCategoryManager
        .getReportSynthesisExpenditureCategoryById(fundingUseExpendituryArea.getExpenditureCategory().getId());

      if (reportSynthesisExpenditureCategory.getName().equals("Other")) {
        if (!(this.isValidString(fundingUseExpendituryArea.getOtherCategory()))) {
          action.addMessage(action.getText("annualReport2018.fundingUse.table11.otherArea") + "[" + i + "]");
          action.getInvalidFields().put(
            "input-reportSynthesis.reportSynthesisFundingUseSummary.expenditureAreas[" + i + "].otherCategory",
            InvalidFieldsMessages.EMPTYFIELD);
        }
      }
    }

  }

}
