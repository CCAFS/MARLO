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
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrpProgressTarget;
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
public class CrpProgressValidator extends BaseValidator {

  private final GlobalUnitManager crpManager;
  private final ReportSynthesisManager reportSynthesisManager;

  public CrpProgressValidator(GlobalUnitManager crpManager, ReportSynthesisManager reportSynthesisManager) {
    this.crpManager = crpManager;
    this.reportSynthesisManager = reportSynthesisManager;
  }


  private Path getAutoSaveFilePath(ReportSynthesis reportSynthesis, long crpID, BaseAction baseAction) {
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
    String composedClassName = reportSynthesis.getClass().getSimpleName();
    String actionFile = ReportSynthesisSectionStatusEnum.CRP_PROGRESS.getStatus().replace("/", "_");
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

      // Validate Overall CRP Progress Towars SLOs
      if (!this.isValidString(reportSynthesis.getReportSynthesisCrpProgress().getOverallProgress())) {
        action.addMessage(action.getText("annualReport.crpProgress.overallProgress"));
        action.getInvalidFields().put("input-reportSynthesis.reportSynthesisCrpProgress.overallProgress",
          InvalidFieldsMessages.EMPTYFIELD);
      }

      // Validate Summaries of Outcomes
      if (!this.isValidString(reportSynthesis.getReportSynthesisCrpProgress().getSummaries())) {
        action.addMessage(action.getText("annualReport.crpProgress.summariesOutcomes"));
        action.getInvalidFields().put("input-reportSynthesis.reportSynthesisCrpProgress.summaries",
          InvalidFieldsMessages.EMPTYFIELD);
      }

      if (!this.isPMU(this.getLiaisonInstitution(action, reportSynthesis.getId()))) {

        // Validate Slo Targets
        if (reportSynthesis.getReportSynthesisCrpProgress().getSloTargets() != null
          && !reportSynthesis.getReportSynthesisCrpProgress().getSloTargets().isEmpty()) {
          for (int i = 0; i < reportSynthesis.getReportSynthesisCrpProgress().getSloTargets().size(); i++) {
            this.validateTargets(action, reportSynthesis.getReportSynthesisCrpProgress().getSloTargets().get(i), i);
          }
        } else {
          action.addMessage(action.getText("SLO Targets"));
          action.addMissingField("annualReport.crpProgress");
          action.getInvalidFields().put("list-reportSynthesis.reportSynthesisCrpProgress.sloTargets",
            action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Crps"}));
        }
      }
    }

    if (!action.getFieldErrors().isEmpty()) {
      action.addActionError(action.getText("saving.fields.required"));
    } else if (action.getValidationMessage().length() > 0) {
      action.addActionMessage(
        " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));
    }

    this.saveMissingFields(reportSynthesis, action.getActualPhase().getDescription(), action.getActualPhase().getYear(),
      ReportSynthesisSectionStatusEnum.CRP_PROGRESS.getStatus(), action);

  }

  public void validateTargets(BaseAction action, ReportSynthesisCrpProgressTarget target, int i) {

    // Validate BriefSummary
    if (!this.isValidString(target.getBirefSummary())) {
      action.addMessage(action.getText("annualReport.crpProgress.overallProgress"));
      action.getInvalidFields().put(
        "input-reportSynthesis.reportSynthesisCrpProgress.sloTargets[" + i + "].birefSummary",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Additional Contribution
    if (!this.isValidString(target.getAdditionalContribution())) {
      action.addMessage(action.getText("annualReport.crpProgress.overallProgress"));
      action.getInvalidFields().put(
        "input-reportSynthesis.reportSynthesisCrpProgress.sloTargets[" + i + "].additionalContribution",
        InvalidFieldsMessages.EMPTYFIELD);
    }


  }

}
