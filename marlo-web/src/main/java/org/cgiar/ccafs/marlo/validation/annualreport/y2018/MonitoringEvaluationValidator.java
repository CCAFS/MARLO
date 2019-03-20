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
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisMeliaEvaluation;
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
public class MonitoringEvaluationValidator extends BaseValidator {

  private final GlobalUnitManager crpManager;
  private final ReportSynthesisManager reportSynthesisManager;

  public MonitoringEvaluationValidator(GlobalUnitManager crpManager, ReportSynthesisManager reportSynthesisManager) {
    this.crpManager = crpManager;
    this.reportSynthesisManager = reportSynthesisManager;
  }


  private Path getAutoSaveFilePath(ReportSynthesis reportSynthesis, long crpID, BaseAction baseAction) {
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
    String composedClassName = reportSynthesis.getClass().getSimpleName();
    String actionFile = ReportSynthesis2018SectionStatusEnum.MELIA.getStatus().replace("/", "_");
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

      // Validate Summary
      if (!this.isValidString(reportSynthesis.getReportSynthesisMelia().getSummary())) {
        action.addMessage(action.getText("reportSynthesis.reportSynthesisMelia.summary"));
        action.getInvalidFields().put("input-reportSynthesis.reportSynthesisMelia.summary",
          InvalidFieldsMessages.EMPTYFIELD);
      }

      // if (this.isPMU(this.getLiaisonInstitution(action, reportSynthesis.getId()))) {
      //
      // // Validate Evaluations
      // if (reportSynthesis.getReportSynthesisMelia().getEvaluations() != null
      // && !reportSynthesis.getReportSynthesisMelia().getEvaluations().isEmpty()) {
      // for (int i = 0; i < reportSynthesis.getReportSynthesisMelia().getEvaluations().size(); i++) {
      // this.validateEvaluations(action, reportSynthesis.getReportSynthesisMelia().getEvaluations().get(i), i);
      // }
      // } else {
      // action.addMessage(action.getText("Evaluations"));
      // action.addMissingField("reportSynthesis.reportSynthesisMelia.evaluations");
      // action.getInvalidFields().put("list-reportSynthesis.reportSynthesisMelia.evaluations",
      // action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Evaluations"}));
      // }
      //
      // }

      if (!action.getFieldErrors().isEmpty()) {
        action.addActionError(action.getText("saving.fields.required"));
      } else if (action.getValidationMessage().length() > 0) {
        action.addActionMessage(
          " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));
      }


      this.saveMissingFields(reportSynthesis, action.getActualPhase().getDescription(),
        action.getActualPhase().getYear(), action.getActualPhase().getUpkeep(),
        ReportSynthesis2018SectionStatusEnum.MELIA.getStatus(), action);
    }

  }

  public void validateEvaluations(BaseAction action, ReportSynthesisMeliaEvaluation evaluation, int i) {

    // Validate Name Evaluation
    if (!this.isValidString(evaluation.getNameEvaluation())) {
      action.addMessage(action.getText("reportSynthesis.reportSynthesisMelia.evaluations[" + i + "].nameEvaluation"));
      action.getInvalidFields().put("input-reportSynthesis.reportSynthesisMelia.evaluations[" + i + "].nameEvaluation",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Recommendation
    if (!this.isValidString(evaluation.getRecommendation())) {
      action.addMessage(action.getText("reportSynthesis.reportSynthesisMelia.evaluations[" + i + "].recommendation"));
      action.getInvalidFields().put("input-reportSynthesis.reportSynthesisMelia.evaluations[" + i + "].recommendation",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Manage Response
    if (!this.isValidString(evaluation.getManagementResponse())) {
      action
        .addMessage(action.getText("reportSynthesis.reportSynthesisMelia.evaluations[" + i + "].managementResponse"));
      action.getInvalidFields().put(
        "input-reportSynthesis.reportSynthesisMelia.evaluations[" + i + "].managementResponse",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Status
    if (evaluation.getStatus() == null || evaluation.getStatus() == -1) {
      action.addMessage(action.getText("reportSynthesis.reportSynthesisMelia.evaluations[" + i + "].status"));
      action.getInvalidFields().put("input-reportSynthesis.reportSynthesisMelia.evaluations[" + i + "].status",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Whom
    if (!this.isValidString(evaluation.getTextWhom())) {
      action.addMessage(action.getText("reportSynthesis.reportSynthesisMelia.evaluations[" + i + "].whom"));
      action.getInvalidFields().put("input-reportSynthesis.reportSynthesisMelia.evaluations[" + i + "].whom",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate When
    if (!this.isValidString(evaluation.getTextWhen())) {
      action.addMessage(action.getText("reportSynthesis.reportSynthesisMelia.evaluations[" + i + "].when"));
      action.getInvalidFields().put("input-reportSynthesis.reportSynthesisMelia.evaluations[" + i + "].when",
        InvalidFieldsMessages.EMPTYFIELD);
    }

  }

}
