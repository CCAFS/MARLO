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
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisIndicator;
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
public class IndicatorsValidator extends BaseValidator {

  private final GlobalUnitManager crpManager;
  private final ReportSynthesisManager reportSynthesisManager;

  public IndicatorsValidator(GlobalUnitManager crpManager, ReportSynthesisManager reportSynthesisManager) {
    this.crpManager = crpManager;
    this.reportSynthesisManager = reportSynthesisManager;
  }


  private Path getAutoSaveFilePath(ReportSynthesis reportSynthesis, long crpID, BaseAction baseAction,
    String actionName) {
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
    String composedClassName = reportSynthesis.getClass().getSimpleName();
    String actionFile = actionName.replace("/", "_");

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


  public void validate(BaseAction action, ReportSynthesis reportSynthesis, boolean saving, boolean isInfluence) {
    action.setInvalidFields(new HashMap<>());
    if (reportSynthesis != null) {
      String actionName = "";
      if (isInfluence) {
        actionName = ReportSynthesisSectionStatusEnum.INFLUENCE.getStatus();
      } else {
        actionName = ReportSynthesisSectionStatusEnum.CONTROL.getStatus();
      }
      if (!saving) {
        Path path = this.getAutoSaveFilePath(reportSynthesis, action.getCrpID(), action, actionName);
        if (path.toFile().exists()) {
          action.addMissingField("draft");
        }
      }

      int i = 0;
      for (ReportSynthesisIndicator synthesisIndicator : reportSynthesis.getReportSynthesisIndicatorGeneral()
        .getSynthesisIndicators()) {
        this.validateSynthesisIndicator(synthesisIndicator, action, i);
        i++;
      }

      if (!action.getFieldErrors().isEmpty()) {
        action.addActionError(action.getText("saving.fields.required"));
      } else if (action.getValidationMessage().length() > 0) {
        action.addActionMessage(
          " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));
      }

      this.saveMissingFields(reportSynthesis, action.getActualPhase().getDescription(),
        action.getActualPhase().getYear(), action.getActualPhase().getUpkeep(), actionName, action);

    }
  }


  private void validateSynthesisIndicator(ReportSynthesisIndicator synthesisIndicator, BaseAction action, int i) {
    if (!(this.isValidString(synthesisIndicator.getComment()))) {
      action.addMessage(action.getText("annualReport.influence.indicatorI3.comments.readText") + "[" + i + "]");
      action.getInvalidFields().put(
        "input-reportSynthesis.reportSynthesisIndicatorGeneral.synthesisIndicators[" + i + "].comment",
        InvalidFieldsMessages.EMPTYFIELD);
    }
    if (!(this.isValidString(synthesisIndicator.getData()))) {
      action.addMessage(action.getText("annualReport.influence.indicatorI3.data.readText") + "[" + i + "]");
      action.getInvalidFields().put(
        "input-reportSynthesis.reportSynthesisIndicatorGeneral.synthesisIndicators[" + i + "].data",
        InvalidFieldsMessages.EMPTYFIELD);
    }
  }

}
