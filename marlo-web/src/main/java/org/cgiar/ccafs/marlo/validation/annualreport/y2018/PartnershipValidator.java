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
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipCollaboration;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipExternal;
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
public class PartnershipValidator extends BaseValidator {

  private final GlobalUnitManager crpManager;
  private final ReportSynthesisManager reportSynthesisManager;

  public PartnershipValidator(GlobalUnitManager crpManager, ReportSynthesisManager reportSynthesisManager) {
    this.crpManager = crpManager;
    this.reportSynthesisManager = reportSynthesisManager;
  }


  private Path getAutoSaveFilePath(ReportSynthesis reportSynthesis, long crpID, BaseAction baseAction) {
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
    String composedClassName = reportSynthesis.getClass().getSimpleName();
    String actionFile = ReportSynthesis2018SectionStatusEnum.EXTERNAL_PARTNERSHIPS.getStatus().replace("/", "_");
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


      if (this.isPMU(this.getLiaisonInstitution(action, reportSynthesis.getId()))) {

        // Validate Summary
        if (!(this.isValidString(reportSynthesis.getReportSynthesisKeyPartnership().getSummary())
          && this.wordCount(reportSynthesis.getReportSynthesisKeyPartnership().getSummary()) <= 300)) {
          action.addMessage(action.getText("reportSynthesis.reportSynthesisKeyPartnership.summary"));
          action.getInvalidFields().put("input-reportSynthesis.reportSynthesisKeyPartnership.summary",
            InvalidFieldsMessages.EMPTYFIELD);
        }

        // Validate Cgiar Summary
        if (!(this.isValidString(reportSynthesis.getReportSynthesisKeyPartnership().getSummaryCgiar())
          && this.wordCount(reportSynthesis.getReportSynthesisKeyPartnership().getSummaryCgiar()) <= 300)) {
          action.addMessage(action.getText("reportSynthesis.reportSynthesisKeyPartnership.summaryCgiar"));
          action.getInvalidFields().put("input-reportSynthesis.reportSynthesisKeyPartnership.summaryCgiar",
            InvalidFieldsMessages.EMPTYFIELD);
        }

      } else {

        if (reportSynthesis.getReportSynthesisKeyPartnership().getPartnerships() == null
          || reportSynthesis.getReportSynthesisKeyPartnership().getPartnerships().isEmpty()) {

          action.addMessage(action.getText("Key External partnerships"));
          action.addMissingField("reportSynthesis.reportSynthesisKeyPartnership.partnerships");
          action.getInvalidFields().put("list-reportSynthesis.reportSynthesisKeyPartnership.partnerships",
            action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"partnerships"}));

        } else {
          // Validate Key external partnerships
          if (reportSynthesis.getReportSynthesisKeyPartnership().getPartnerships() != null) {
            for (int i = 0; i < reportSynthesis.getReportSynthesisKeyPartnership().getPartnerships().size(); i++) {
              this.validateExternal(action, reportSynthesis.getReportSynthesisKeyPartnership().getPartnerships().get(i),
                i);
            }
          }
        }

      }

      if (reportSynthesis.getReportSynthesisKeyPartnership().getCollaborations() == null
        || reportSynthesis.getReportSynthesisKeyPartnership().getCollaborations().isEmpty()) {

        if (!action.isPMU()) {
          action.addMessage(action.getText("CGIAR Collaborations"));
          action.addMissingField("reportSynthesis.reportSynthesisKeyPartnership.collaborations");
          action.getInvalidFields().put("list-reportSynthesis.reportSynthesisKeyPartnership.collaborations",
            action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"collaborations"}));
        }

      } else {
        // Validate Key external partnerships
        if (reportSynthesis.getReportSynthesisKeyPartnership().getCollaborations() != null) {
          for (int i = 0; i < reportSynthesis.getReportSynthesisKeyPartnership().getCollaborations().size(); i++) {
            this.validateCollaboration(action,
              reportSynthesis.getReportSynthesisKeyPartnership().getCollaborations().get(i), i);
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
        ReportSynthesis2018SectionStatusEnum.EXTERNAL_PARTNERSHIPS.getStatus(), action);
    }

  }

  private void validateCollaboration(BaseAction action, ReportSynthesisKeyPartnershipCollaboration collaboration,
    int i) {

    // Validate Crps
    if (collaboration.getCrps() == null || collaboration.getCrps().isEmpty()) {
      action.addMessage(action.getText("Crps"));
      action.addMissingField("reportSynthesis.reportSynthesisKeyPartnership.collaborations[" + i + "].crps");
      action.getInvalidFields().put("list-reportSynthesis.reportSynthesisKeyPartnership.collaborations[" + i + "].crps",
        action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"crps"}));
    }

    // Validate Brief
    if (!(this.isValidString(collaboration.getDescription()))) {
      action.addMessage(
        action.getText("reportSynthesis.reportSynthesisKeyPartnership.collaborations[" + i + "].description"));
      action.getInvalidFields().put(
        "input-reportSynthesis.reportSynthesisKeyPartnership.collaborations[" + i + "].description",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Value added
    if (!(this.isValidString(collaboration.getValueAdded()))) {
      action.addMessage(
        action.getText("reportSynthesis.reportSynthesisKeyPartnership.collaborations[" + i + "].valueAdded"));
      action.getInvalidFields().put(
        "input-reportSynthesis.reportSynthesisKeyPartnership.collaborations[" + i + "].valueAdded",
        InvalidFieldsMessages.EMPTYFIELD);
    }
  }

  private void validateExternal(BaseAction action, ReportSynthesisKeyPartnershipExternal external, int i) {
    // Validate Description
    if (!(this.isValidString(external.getDescription()) && this.wordCount(external.getDescription()) <= 30)) {
      action.addMessage(
        action.getText("reportSynthesis.reportSynthesisKeyPartnership.partnerships[" + i + "].description"));
      action.getInvalidFields().put(
        "input-reportSynthesis.reportSynthesisKeyPartnership.partnerships[" + i + "].description",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Main Areas
    if (external.getMainAreas() == null || external.getMainAreas().isEmpty()) {
      action.addMessage(action.getText("Main Areas"));
      action.addMissingField("reportSynthesis.reportSynthesisKeyPartnership.partnerships[" + i + "].mainAreas");
      action.getInvalidFields().put(
        "list-reportSynthesis.reportSynthesisKeyPartnership.partnerships[" + i + "].mainAreas",
        action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"mainAreas"}));
    }

    // Validate partners
    if (external.getInstitutions() == null || external.getInstitutions().isEmpty()) {
      action.addMessage(action.getText("Partners"));
      action.addMissingField("reportSynthesis.reportSynthesisKeyPartnership.partnerships[" + i + "].institutions");
      action.getInvalidFields().put(
        "list-reportSynthesis.reportSynthesisKeyPartnership.partnerships[" + i + "].institutions",
        action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"institutions"}));
    }
  }

}