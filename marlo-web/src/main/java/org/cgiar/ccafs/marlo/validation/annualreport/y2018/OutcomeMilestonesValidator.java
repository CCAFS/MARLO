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
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis2018SectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressCrossCuttingMarker;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressOutcome;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressOutcomeMilestone;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressOutcomeMilestoneLink;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.utils.Patterns;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@Named
public class OutcomeMilestonesValidator extends BaseValidator {

  private static Logger LOG = LoggerFactory.getLogger(OutcomeMilestonesValidator.class);

  private final GlobalUnitManager crpManager;
  private final ReportSynthesisManager reportSynthesisManager;
  private final LiaisonInstitutionManager liaisonInstitutionManager;
  private final SectionStatusManager sectionStatusManager;

  public OutcomeMilestonesValidator(GlobalUnitManager crpManager, ReportSynthesisManager reportSynthesisManager,
    LiaisonInstitutionManager liaisonInstitutionManager, SectionStatusManager sectionStatusManager) {
    this.crpManager = crpManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.sectionStatusManager = sectionStatusManager;
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
                // action.addMissingField(action.getText("Not Expected Crp Progress"));
              }
            } else {
              // action.addMissingField(action.getText("Not Expected Crp Progress"));
            }
          }
        }
      }


      // Validate Flagships
      if (action.isPMU()) {
        String flagshipsWithMisingInformation = "";
        // Get all liaison institutions for current CRP
        List<LiaisonInstitution> liaisonInstitutionsFromCrp = liaisonInstitutionManager.findAll().stream()
          .filter(l -> l != null && l.isActive() && l.getCrp() != null && l.getCrp().getId() != null
            && l.getCrp().getId().equals(action.getCurrentCrp().getId()) && l.getCrpProgram() != null
            && l.getAcronym() != null && !l.getAcronym().contains(" "))
          .collect(Collectors.toList());
        ReportSynthesis reportSynthesisAux = null;

        List<SectionStatus> statusOfEveryFlagship = new ArrayList<>();
        SectionStatus statusOfFlagship = null;
        if (liaisonInstitutionsFromCrp != null) {
          // Order liaisonInstitutionsFromCrp list by acronyms
          liaisonInstitutionsFromCrp = liaisonInstitutionsFromCrp.stream()
            .sorted((p1, p2) -> p1.getAcronym().compareTo(p2.getAcronym())).collect(Collectors.toList());
          for (LiaisonInstitution liaison : liaisonInstitutionsFromCrp) {
            // Get report synthesis for each liaison Instution
            reportSynthesisAux =
              reportSynthesisManager.findSynthesis(reportSynthesis.getPhase().getId(), liaison.getId());
            if (reportSynthesisAux != null) {
              statusOfFlagship = sectionStatusManager.getSectionStatusByReportSynthesis(reportSynthesisAux.getId(),
                "Reporting", 2019, false, "outomesMilestones");
            }

            // Add section status to statusOfEveryFlagship list if section status (statusOfFlagship) has missing fields
            SectionStatus statusOfFPMU = sectionStatusManager.getSectionStatusByReportSynthesis(reportSynthesis.getId(),
              "Reporting", 2019, false, "synthesis.AR2019Table5");

            if (statusOfFlagship != null && statusOfFlagship.getMissingFields() != null
              && !statusOfFlagship.getMissingFields().isEmpty()) {
              // Add flagship acronym with missing information to Section status in synthesis flagship field
              if (statusOfFPMU != null && statusOfFPMU.getSynthesisFlagships() != null
                && !statusOfFPMU.getSynthesisFlagships().isEmpty()) {
                if (!statusOfFPMU.getSynthesisFlagships().contains(liaison.getAcronym())) {
                  action.addSynthesisFlagship(liaison.getAcronym());
                }
              } else {
                action.addSynthesisFlagship(liaison.getAcronym());
              }
              if (flagshipsWithMisingInformation != null && !flagshipsWithMisingInformation.isEmpty()) {
                flagshipsWithMisingInformation += ", " + liaison.getAcronym();
              } else {
                flagshipsWithMisingInformation = liaison.getAcronym();
              }

              statusOfEveryFlagship.add(statusOfFlagship);
            }
          }
        }

        // boolean tableComplete = false;

        if (statusOfEveryFlagship == null || statusOfEveryFlagship.isEmpty()) {
          // tableComplete = true;
        } else {
          // If there are section status objects with missing information
          for (SectionStatus sectionStatus : statusOfEveryFlagship) {
            if ((sectionStatus != null && sectionStatus.getId() != null && sectionStatus.getMissingFields() != null
              && !sectionStatus.getMissingFields().isEmpty() && sectionStatus.getId() != 0)) {
              if (sectionStatus.getReportSynthesis().getLiaisonInstitution().getName().contains("PMU")) {
                // If section status is from PMU - missing fields is set to empty
                if (sectionStatus.getMissingFields() != null && !sectionStatus.getMissingFields().isEmpty()) {
                  sectionStatus.setMissingFields("");
                  sectionStatusManager.saveSectionStatus(sectionStatus);
                }
              } else {
                // If section status is from flagship
                // tableComplete = false;
                action.addMissingField("synthesis.AR2019Table5");
                action.addMessage("Flagships with missing information :" + flagshipsWithMisingInformation);
              }
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

      try {
        this.saveMissingFields(reportSynthesis, action.getActualPhase().getDescription(),
          action.getActualPhase().getYear(), action.getActualPhase().getUpkeep(),
          ReportSynthesis2018SectionStatusEnum.OUTOMESMILESTONES.getStatus(), action);
      } catch (Exception e) {
        LOG.error("Error getting innovations list: " + e.getMessage());
      }
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
        /*
         * if (!this.isValidString(crossCuttingMarker.getJust())) {
         * action.addMessage(action.getText("Brief Justification"));
         * action.addMissingField("input-reportSynthesis.reportSynthesisFlagshipProgress.outcomeList[" + i
         * + "].milestones[" + j + "].markers[" + k + "].just");
         * action.getInvalidFields().put("input-reportSynthesis.reportSynthesisFlagshipProgress.outcomeList[" + i
         * + "].milestones[" + j + "].markers[" + k + "].just", InvalidFieldsMessages.EMPTYFIELD);
         * }
         */
      } else {
        // AR 2021 adjustments: Validate Brief Justification -> Focus 2 = Significant; Focus 3 = Principal
        if (crossCuttingMarker.getFocus().getId() == 2L || crossCuttingMarker.getFocus().getId() == 3L) {
          if (!this.isValidString(crossCuttingMarker.getJust())
            || this.wordCount(this.removeHtmlTags(crossCuttingMarker.getJust())) > 100) {
            action.addMessage(action.getText("Brief Justification"));
            action.addMissingField("input-reportSynthesis.reportSynthesisFlagshipProgress.outcomeList[" + i
              + "].milestones[" + j + "].markers[" + k + "].just");
            action.getInvalidFields().put("input-reportSynthesis.reportSynthesisFlagshipProgress.outcomeList[" + i
              + "].milestones[" + j + "].markers[" + k + "].just", InvalidFieldsMessages.EMPTYFIELD);
          }
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
        + "].milestones[" + j + "].milestonesStatus.id", InvalidFieldsMessages.EMPTYFIELD);
    } else {
      if (StringUtils.equals(String.valueOf(milestone.getMilestonesStatus().getId()),
        ProjectStatusEnum.Complete.getStatusId())) {
        // Validate Milestone Reasons
        if (milestone.getReason() != null) {
          if (milestone.getReason().getId() != null && milestone.getReason().getId() != -1) {
            // reason 7 = OTHER
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

    // Extended year
    if (milestone.getMilestonesStatus() != null && milestone.getMilestonesStatus().getId() == 4
      && (milestone.getExtendedYear() == null || milestone.getExtendedYear() == 0
        || milestone.getExtendedYear() == -1)) {
      action.addMessage(action.getText("extendedYear"));
      action.addMissingField(
        "reportSynthesis.reportSynthesisFlagshipProgress.outcomeList[" + i + "].milestones[" + j + "].extendedYear");
      action.getInvalidFields().put("input-reportSynthesis.reportSynthesisFlagshipProgress.outcomeList[" + i
        + "].milestones[" + j + "].extendedYear",
        action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"extendedYear"}));
    }

    // Validate Milestone Evidence
    if ((!(this.isValidString(milestone.getEvidence())))
      || (milestone.getEvidence() == null || milestone.getEvidence().isEmpty())) {
      if (!action.isSelectedPhaseAR2021() || (action.isSelectedPhaseAR2021() && milestone.getMilestonesStatus() != null
        && milestone.getMilestonesStatus().getId() != null && milestone.getMilestonesStatus().getId() == 3L)) {
        action.addMessage(action.getText("Evidence"));
        action.addMissingField("input-reportSynthesis.reportSynthesisFlagshipProgress.outcomeList[" + i
          + "].milestones[" + j + "].evidence");
        action.getInvalidFields().put(
          "input-reportSynthesis.reportSynthesisFlagshipProgress.outcomeList[" + i + "].milestones[" + j + "].evidence",
          InvalidFieldsMessages.EMPTYFIELD);
      }
    } else {
      if (this.wordCount(this.removeHtmlTags(milestone.getEvidence())) > 200) {
        if (!action.isSelectedPhaseAR2021()
          || (action.isSelectedPhaseAR2021() && milestone.getMilestonesStatus() != null
            && milestone.getMilestonesStatus().getId() != null && milestone.getMilestonesStatus().getId() != 3L)) {
          action.addMessage(action.getText("Evidence"));
          action.addMissingField("input-reportSynthesis.reportSynthesisFlagshipProgress.outcomeList[" + i
            + "].milestones[" + j + "].evidence");
          action.getInvalidFields().put("input-reportSynthesis.reportSynthesisFlagshipProgress.outcomeList[" + i
            + "].milestones[" + j + "].evidence", InvalidFieldsMessages.EMPTYFIELD);
        }
      }
    }

    // Validate Milestone Evidence Link
    if (!action.isSelectedPhaseAR2021()) {
      if ((!(this.isValidString(milestone.getEvidenceLink())))
        || (milestone.getEvidence() == null || milestone.getEvidenceLink().isEmpty())) {
        action.addMessage(action.getText("Evidence Link"));
        action.addMissingField("input-reportSynthesis.reportSynthesisFlagshipProgress.outcomeList[" + i
          + "].milestones[" + j + "].evidenceLink");
        action.getInvalidFields().put("input-reportSynthesis.reportSynthesisFlagshipProgress.outcomeList[" + i
          + "].milestones[" + j + "].evidenceLink", InvalidFieldsMessages.EMPTYFIELD);
      } else {
        if (this.wordCount(this.removeHtmlTags(milestone.getEvidence())) > 200) {
          action.addMessage(action.getText("Evidence Link"));
          action.addMissingField("input-reportSynthesis.reportSynthesisFlagshipProgress.outcomeList[" + i
            + "].milestones[" + j + "].evidenceLink");
          action.getInvalidFields().put("input-reportSynthesis.reportSynthesisFlagshipProgress.outcomeList[" + i
            + "].milestones[" + j + "].evidenceLink", InvalidFieldsMessages.EMPTYFIELD);
        }
      }
    } else {
      if (milestone.getMilestonesStatus() != null && milestone.getMilestonesStatus().getId() != null
        && (milestone.getMilestonesStatus().getId() == 3L || milestone.getMilestonesStatus().getId() == 6L)) {
        if (action.isNotEmpty(milestone.getLinks())) {
          boolean validLinks = true;
          for (ReportSynthesisFlagshipProgressOutcomeMilestoneLink link : milestone.getLinks()) {
            if (link == null || link.getLink() == null || !Patterns.WEB_URL.matcher(link.getLink()).find()) {
              validLinks = false;
            }
          }

          if (!validLinks) {
            action.addMessage(action.getText("Evidence Link"));
            action.addMissingField("input-reportSynthesis.reportSynthesisFlagshipProgress.outcomeList[" + i
              + "].milestones[" + j + "].evidenceLink");
            action.getInvalidFields().put("input-reportSynthesis.reportSynthesisFlagshipProgress.outcomeList[" + i
              + "].milestones[" + j + "].evidenceLink", InvalidFieldsMessages.WRONGVALUE);
          }
        } else {
          action.addMessage(action.getText("Evidence Link"));
          action.addMissingField("input-reportSynthesis.reportSynthesisFlagshipProgress.outcomeList[" + i
            + "].milestones[" + j + "].evidenceLink");
          action.getInvalidFields().put("input-reportSynthesis.reportSynthesisFlagshipProgress.outcomeList[" + i
            + "].milestones[" + j + "].evidenceLink", InvalidFieldsMessages.EMPTYLIST);
        }
      }
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

  public void validatePMU(BaseAction action, ReportSynthesis reportSynthesis, boolean saving) {
    action.setInvalidFields(new HashMap<>());
    if (reportSynthesis != null) {
      if (!saving) {
        Path path = this.getAutoSaveFilePath(reportSynthesis, action.getCrpID(), action);
        if (path.toFile().exists()) {
          action.addMissingField("draft");
        }
      }
      // Validate Flagships
      // sectionStatusManager.

      boolean tableComplete = false;
      SectionStatus sectionStatus = sectionStatusManager.getSectionStatusByReportSynthesis(reportSynthesis.getId(),
        "Reporting", 2019, false, "outomesMilestones");

      if (sectionStatus == null) {
        tableComplete = true;
        // sectionStatusManager.deleteSectionStatus(sectionStatusID);
      } else if (sectionStatus != null && sectionStatus.getMissingFields() != null
        && sectionStatus.getMissingFields().length() != 0) {
        if (sectionStatus.getMissingFields().contains("synthesis.AR2019Table5") && sectionStatus.getId() != 0) {
          sectionStatusManager.deleteSectionStatus(sectionStatus.getId());
          tableComplete = true;
        } else {
          tableComplete = false;
        }
      } else {
        tableComplete = true;
        if (sectionStatus.getId() != 0) {
          sectionStatusManager.deleteSectionStatus(sectionStatus.getId());
        }
      }

      if (tableComplete == false) {
        // action.addMessage(action.getText("Incomplete Outcomes and Milestones"));
        action.addMissingField("synthesis.AR2019Table5");
      }

      /*
       * action.addMessage(action.getText("Title"));
       * action.addMissingField("projectPolicy.title");
       * action.getInvalidFields().put("input-reportSynthesis.reportSynthesisFlagshipProgress.outcomeList.summary",
       * InvalidFieldsMessages.EMPTYFIELD);
       */

      if (!action.getFieldErrors().isEmpty()) {
        action.addActionError(action.getText("saving.fields.required"));
      } else if (action.getValidationMessage().length() > 0) {
        action.addActionMessage(
          " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));
      }


      try {
        this.saveMissingFields(reportSynthesis, action.getActualPhase().getDescription(),
          action.getActualPhase().getYear(), action.getActualPhase().getUpkeep(),
          ReportSynthesis2018SectionStatusEnum.OUTOMESMILESTONES.getStatus(), action);
      } catch (Exception e) {
        LOG.error("Error getting innovations list: " + e.getMessage());
      }
    }

  }

  public void validateTable5(BaseAction action, List<String> missingFieldsList) {
    if (missingFieldsList != null) {
      for (String missingField : missingFieldsList) {
        action.addMessage(missingField);
      }
    }
  }
}