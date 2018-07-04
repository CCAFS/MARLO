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
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrossCgiarCollaboration;
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
public class CrossCgiarPartnershipValidator extends BaseValidator {

  private final GlobalUnitManager crpManager;
  private final ReportSynthesisManager reportSynthesisManager;

  public CrossCgiarPartnershipValidator(GlobalUnitManager crpManager, ReportSynthesisManager reportSynthesisManager) {
    this.crpManager = crpManager;
    this.reportSynthesisManager = reportSynthesisManager;
  }


  private Path getAutoSaveFilePath(ReportSynthesis reportSynthesis, long crpID, BaseAction baseAction) {
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
    String composedClassName = reportSynthesis.getClass().getSimpleName();
    String actionFile = ReportSynthesisSectionStatusEnum.CROSS_CGIAR.getStatus().replace("/", "_");
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
        // Validate highlights
        if (!this.isValidString(reportSynthesis.getReportSynthesisCrossCgiar().getHighlights())) {
          action.addMessage(action.getText("reportSynthesis.reportSynthesisCrossCgiar.highlights"));
          action.getInvalidFields().put("input-reportSynthesis.reportSynthesisCrossCgiar.highlights",
            InvalidFieldsMessages.EMPTYFIELD);
        }
      } else {

        // Validate Collaborations
        if (reportSynthesis.getReportSynthesisCrossCgiar().getCollaborations() != null
          || !reportSynthesis.getReportSynthesisCrossCgiar().getCollaborations().isEmpty()) {
          for (int i = 0; i < reportSynthesis.getReportSynthesisCrossCgiar().getCollaborations().size(); i++) {
            this.validateCollaborations(action,
              reportSynthesis.getReportSynthesisCrossCgiar().getCollaborations().get(i), i);
          }
        } else {
          action.addMessage(action.getText("Collaborations"));
          action.addMissingField("reportSynthesis.reportSynthesisCrossCgiar.collaborations");
          action.getInvalidFields().put("list-reportSynthesis.reportSynthesisCrossCgiar.collaborations",
            action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Collaborations"}));
        }

      }


      if (!action.getFieldErrors().isEmpty()) {
        action.addActionError(action.getText("saving.fields.required"));
      } else if (action.getValidationMessage().length() > 0) {
        action.addActionMessage(
          " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));
      }

      this.saveMissingFields(reportSynthesis, action.getActualPhase().getDescription(),
        action.getActualPhase().getYear(), ReportSynthesisSectionStatusEnum.CROSS_CGIAR.getStatus(), action);
    }


  }

  public void validateCollaborations(BaseAction action, ReportSynthesisCrossCgiarCollaboration collaboration, int i) {

    // Validate Crps
    if (collaboration.getGlobalUnit() == null || collaboration.getGlobalUnit().getId() == -1) {
      action
        .addMessage(action.getText("reportSynthesis.reportSynthesisCrossCgiar.collaborations[" + i + "].globalUnit"));
      action.getInvalidFields().put(
        "input-reportSynthesis.reportSynthesisCrossCgiar.collaborations[" + i + "].globalUnit",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Flagship
    if (!this.isValidString(collaboration.getFlagship())) {
      action.addMessage(action.getText("reportSynthesis.reportSynthesisCrossCgiar.collaborations[" + i + "].flagship"));
      action.getInvalidFields().put(
        "input-reportSynthesis.reportSynthesisCrossCgiar.collaborations[" + i + "].flagship",
        InvalidFieldsMessages.EMPTYFIELD);
    }


    // Validate Collaboration Type
    if (collaboration.getRepIndCollaborationType() == null
      || collaboration.getRepIndCollaborationType().getId() == -1) {
      action.addMessage(
        action.getText("reportSynthesis.reportSynthesisCrossCgiar.collaborations[" + i + "].repIndCollaborationType"));
      action.getInvalidFields().put(
        "input-reportSynthesis.reportSynthesisCrossCgiar.collaborations[" + i + "].repIndCollaborationType",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Status
    if (collaboration.getStatus() == null || collaboration.getStatus() == -1) {
      action.addMessage(action.getText("reportSynthesis.reportSynthesisCrossCgiar.collaborations[" + i + "].status"));
      action.getInvalidFields().put("input-reportSynthesis.reportSynthesisCrossCgiar.collaborations[" + i + "].status",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Description
    if (!this.isValidString(collaboration.getDescription())) {
      action
        .addMessage(action.getText("reportSynthesis.reportSynthesisCrossCgiar.collaborations[" + i + "].description"));
      action.getInvalidFields().put(
        "input-reportSynthesis.reportSynthesisCrossCgiar.collaborations[" + i + "].description",
        InvalidFieldsMessages.EMPTYFIELD);
    }


  }

}
