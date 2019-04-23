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
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis2018SectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressCrossCuttingMarker;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressOutcome;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressOutcomeMilestone;
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
public class OutcomeMilestonesValidator extends BaseValidator {

  private final GlobalUnitManager crpManager;
  private final ReportSynthesisManager reportSynthesisManager;
  private final LiaisonInstitutionManager liaisonInstitutionManager;

  public OutcomeMilestonesValidator(GlobalUnitManager crpManager, ReportSynthesisManager reportSynthesisManager,
    LiaisonInstitutionManager liaisonInstitutionManager) {
    this.crpManager = crpManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
  }


  private Path getAutoSaveFilePath(ReportSynthesis reportSynthesis, long crpID, BaseAction baseAction) {
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
    String composedClassName = reportSynthesis.getClass().getSimpleName();
    String actionFile = ReportSynthesis2018SectionStatusEnum.OUTOMESMILESTONES.getStatus().replace("/", "_");
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
      LiaisonInstitution liaisonInstitution =
        liaisonInstitutionManager.getLiaisonInstitutionById(reportSynthesis.getLiaisonInstitution().getId());

      if (!this.isPMU(liaisonInstitution)) {


        if (liaisonInstitution.getCrpProgram() != null) {
          CrpProgram crpProgram = liaisonInstitution.getCrpProgram();

          if (crpProgram.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()) {

            if (reportSynthesis.getReportSynthesisFlagshipProgress().getOutcomeList() != null) {

              if (!reportSynthesis.getReportSynthesisFlagshipProgress().getOutcomeList().isEmpty()) {

                for (int i = 0; i < reportSynthesis.getReportSynthesisFlagshipProgress().getOutcomeList().size(); i++) {
                  this.validateOutcomes(action,
                    reportSynthesis.getReportSynthesisFlagshipProgress().getOutcomeList().get(i), i);
                }


              } else {
                action.addMissingField(action.getText("Not Expected Crp Progress"));
              }
            } else {
              action.addMissingField(action.getText("Not Expected Crp Progress"));
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
        ReportSynthesis2018SectionStatusEnum.OUTOMESMILESTONES.getStatus(), action);
    }

  }

  private void validateCrossCuttingMarkers(BaseAction action,
    ReportSynthesisFlagshipProgressCrossCuttingMarker crossCuttingMarker, int i, int j, int k) {

    // Validate each Cross Cutting Markers
    if (crossCuttingMarker.getFocus() != null) {
      if (crossCuttingMarker.getFocus().getId() == null || crossCuttingMarker.getFocus().getId() == -1) {
        action.addMessage(action.getText("CrossCutting Markers"));
        action.addMissingField("policy.crossCuttingMarkers");
        action.getInvalidFields().put("input-reportSynthesis.reportSynthesisFlagshipProgress.outcomeList[" + i
          + "].milestones[" + j + "].markers[" + k + "].focus.id", InvalidFieldsMessages.EMPTYFIELD);

        // Validate Brief Justification
        if (!this.isValidString(crossCuttingMarker.getJust())) {
          action.addMessage(action.getText("Brief Justification"));
          action.addMissingField("input-reportSynthesis.reportSynthesisFlagshipProgress.outcomeList[" + i
            + "].milestones[" + j + "].markers[" + k + "].just");
          action.getInvalidFields().put("input-reportSynthesis.reportSynthesisFlagshipProgress.outcomeList[" + i
            + "].milestones[" + j + "].markers[" + k + "].just", InvalidFieldsMessages.EMPTYFIELD);
        }
      } else {
        // Validate Brief Justification
        if (!this.isValidString(crossCuttingMarker.getJust())) {
          action.addMessage(action.getText("Brief Justification"));
          action.addMissingField("input-reportSynthesis.reportSynthesisFlagshipProgress.outcomeList[" + i
            + "].milestones[" + j + "].markers[" + k + "].just");
          action.getInvalidFields().put("input-reportSynthesis.reportSynthesisFlagshipProgress.outcomeList[" + i
            + "].milestones[" + j + "].markers[" + k + "].just", InvalidFieldsMessages.EMPTYFIELD);
        }
      }
    } else {
      action.addMessage(action.getText("CrossCutting Markers"));
      action.addMissingField("policy.crossCuttingMarkers");
      action.getInvalidFields().put("input-reportSynthesis.reportSynthesisFlagshipProgress.outcomeList[" + i
        + "].milestones[" + j + "].markers[" + k + "].focus.id", InvalidFieldsMessages.EMPTYFIELD);
    }

  }

  public void validateMilestones(BaseAction action, ReportSynthesisFlagshipProgressOutcomeMilestone milestone, int i,
    int j) {

    // Validate Milestone Status
    if (milestone.getMilestonesStatus() == null) {
      action.addMessage(action.getText("Milestone Status"));
      action.addMissingField("Milestone Status");
      action.getInvalidFields().put("input-reportSynthesis.reportSynthesisFlagshipProgress.outcomeList[" + i
        + "].milestones[" + j + "].milestonesStatus", InvalidFieldsMessages.EMPTYFIELD);;
    } else {
      if (milestone.getMilestonesStatus() != 1) {
        // Validate Milestone Reasons
        if (milestone.getReason() != null) {

          if (milestone.getReason().getId() != null && milestone.getReason().getId() != -1) {

            if (milestone.getReason().getId() == 7) {
              // Validate Other Reason
              if (!this.isValidString(milestone.getOtherReason())) {
                action.addMessage(action.getText("Other Reason"));
                action.addMissingField("input-reportSynthesis.reportSynthesisFlagshipProgress.outcomeList[" + i
                  + "].milestones[" + j + "].otherReason");
                action.getInvalidFields().put("input-reportSynthesis.reportSynthesisFlagshipProgress.outcomeList[" + i
                  + "].milestones[" + j + "].otherReason", InvalidFieldsMessages.EMPTYFIELD);
              }
            }

          } else {
            action.addMessage(action.getText("Milestone Reason"));
            action.addMissingField("input-reportSynthesis.reportSynthesisFlagshipProgress.outcomeList[" + i
              + "].milestones[" + j + "].reason.id");
            action.getInvalidFields().put("input-reportSynthesis.reportSynthesisFlagshipProgress.outcomeList[" + i
              + "].milestones[" + j + "].reason.id", InvalidFieldsMessages.EMPTYFIELD);
          }

        } else {
          action.addMessage(action.getText("Milestone Reason"));
          action.addMissingField("input-reportSynthesis.reportSynthesisFlagshipProgress.outcomeList[" + i
            + "].milestones[" + j + "].reason.id");
          action.getInvalidFields().put("input-reportSynthesis.reportSynthesisFlagshipProgress.outcomeList[" + i
            + "].milestones[" + j + "].reason.id", InvalidFieldsMessages.EMPTYFIELD);
        }
      }
    }

    // Validate Milestone Evidence
    if (!this.isValidString(milestone.getEvidence())
      && this.wordCount(this.removeHtmlTags(milestone.getEvidence())) <= 50) {
      action.addMessage(action.getText("Evidence"));
      action.addMissingField(
        "input-reportSynthesis.reportSynthesisFlagshipProgress.outcomeList[" + i + "].milestones[" + j + "].evidence");
      action.getInvalidFields().put(
        "input-reportSynthesis.reportSynthesisFlagshipProgress.outcomeList[" + i + "].milestones[" + j + "].evidence",
        InvalidFieldsMessages.EMPTYFIELD);
    }


    // Validate Cross Cutting
    if (milestone.getMarkers() == null || milestone.getMarkers().isEmpty()) {
      action.addMessage(action.getText("crossCuttingMarkers"));
      action.getInvalidFields().put(
        "list-reportSynthesis.reportSynthesisFlagshipProgress.outcomeList[" + i + "].milestones[" + j + "].markers",
        action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"markers"}));
    } else {
      for (int k = 0; k < milestone.getMarkers().size(); k++) {
        this.validateCrossCuttingMarkers(action, milestone.getMarkers().get(k), i, j, k);
      }

    }
  }

  private void validateOutcomes(BaseAction action, ReportSynthesisFlagshipProgressOutcome outcome, int i) {

    // Validate Summary
    if (!(this.isValidString(outcome.getSummary())
      && this.wordCount(this.removeHtmlTags(outcome.getSummary())) <= 100)) {
      action.addMessage(action.getText("Title"));
      action.addMissingField("projectPolicy.title");
      action.getInvalidFields().put(
        "input-reportSynthesis.reportSynthesisFlagshipProgress.outcomeList[" + i + "].summary",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    for (int j = 0; j < outcome.getMilestones().size(); j++) {
      this.validateMilestones(action, outcome.getMilestones().get(j), i, j);
    }


  }


}
