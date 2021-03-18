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
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis2018SectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrés Valencia - CIAT/CCAFS
 */
@Named
public class FlagshipProgress2018Validator extends BaseValidator {

  private static Logger LOG = LoggerFactory.getLogger(FlagshipProgress2018Validator.class);

  private final GlobalUnitManager crpManager;
  private final ReportSynthesisManager reportSynthesisManager;
  private final LiaisonInstitutionManager liaisonInstitutionManager;
  private final CrpProgramManager crpProgramManager;
  private final SectionStatusManager sectionStatusManager;

  public FlagshipProgress2018Validator(GlobalUnitManager crpManager, ReportSynthesisManager reportSynthesisManager,
    LiaisonInstitutionManager liaisonInstitutionManager, CrpProgramManager crpProgramManager,
    SectionStatusManager sectionStatusManager) {
    this.crpManager = crpManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.crpProgramManager = crpProgramManager;
    this.sectionStatusManager = sectionStatusManager;
  }


  private Path getAutoSaveFilePath(ReportSynthesis reportSynthesis, long crpID, BaseAction baseAction) {
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
    String composedClassName = reportSynthesis.getClass().getSimpleName();
    String actionFile = ReportSynthesis2018SectionStatusEnum.FLAGSHIP_PROGRESS.getStatus().replace("/", "_");
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


  public boolean isFlagship(LiaisonInstitution liaisonInstitution) {
    boolean isFP = false;
    if (liaisonInstitution != null) {
      if (liaisonInstitution.getCrpProgram() != null) {
        CrpProgram crpProgram =
          crpProgramManager.getCrpProgramById(liaisonInstitution.getCrpProgram().getId().longValue());
        if (crpProgram.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()) {
          isFP = true;
        }
      }
    }
    return isFP;
  }


  public void validate(BaseAction action, ReportSynthesis reportSynthesis, boolean saving,
    boolean hasFlagshipsProgress) {
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

      // Validate Relevance to covid field - for Flagships and PMU
      if (reportSynthesis != null && reportSynthesis.getReportSynthesisFlagshipProgress() != null
        && reportSynthesis.getReportSynthesisFlagshipProgress().getRelevanceCovid() != null) {
        if (!(this.isValidString(reportSynthesis.getReportSynthesisFlagshipProgress().getRelevanceCovid()))
          && reportSynthesis.getReportSynthesisFlagshipProgress().getRelevanceCovid().length() < 300) {
          action.addMissingField(action.getText("annualReport2018.flagshipProgress.relevanceCovid"));
          action.getInvalidFields().put("input-reportSynthesis.reportSynthesisFlagshipProgress.relevanceCovid",
            InvalidFieldsMessages.EMPTYFIELD);
        }
      }

      // Validate flagship fields
      if (this.isFlagship(liaisonInstitution)) {
        /*
         * if (!(this.isValidString(reportSynthesis.getReportSynthesisFlagshipProgress().getProgressByFlagships()))) {
         * action.addMissingField(action.getText("annualReport2018.flagshipProgress.progressByFlagships.readText"));
         * action.getInvalidFields().put("input-reportSynthesis.reportSynthesisFlagshipProgress.progressByFlagships",
         * InvalidFieldsMessages.EMPTYFIELD);
         * }
         */
      } else {
        // Validate PMU fields

        /*
         * if (hasFlagshipsProgress == true) {
         * if (!(this.isValidString(reportSynthesis.getReportSynthesisFlagshipProgress().getOverallProgress()))
         * && reportSynthesis.getReportSynthesisFlagshipProgress().getOverallProgress().length() > 250) {
         * action.addMissingField(action.getText("annualReport2018.flagshipProgress.overallProgress.readText"));
         * action.getInvalidFields().put("input-reportSynthesis.reportSynthesisFlagshipProgress.overallProgress",
         * InvalidFieldsMessages.EMPTYFIELD);
         * } else {
         * if (!(this.isValidString(reportSynthesis.getReportSynthesisFlagshipProgress().getOverallProgress()))
         * && reportSynthesis.getReportSynthesisFlagshipProgress().getOverallProgress().length() > 1000) {
         * action.addMissingField(action.getText("annualReport2018.flagshipProgress.overallProgress.readText"));
         * action.getInvalidFields().put("input-reportSynthesis.reportSynthesisFlagshipProgress.overallProgress",
         * InvalidFieldsMessages.EMPTYFIELD);
         * }
         * }
         * }
         */

        if (!(this.isValidString(reportSynthesis.getReportSynthesisFlagshipProgress().getOverallProgress()))
          && reportSynthesis.getReportSynthesisFlagshipProgress().getOverallProgress().length() > 1000) {
          action.addMissingField(action.getText("annualReport2018.flagshipProgress.overallProgress.readText"));
          action.getInvalidFields().put("input-reportSynthesis.reportSynthesisFlagshipProgress.overallProgress",
            InvalidFieldsMessages.EMPTYFIELD);
        }
      }

      if (!(this.isValidString(reportSynthesis.getReportSynthesisFlagshipProgress().getExpandedResearchAreas()))) {
        action.addMissingField(action.getText("annualReport2018.flagshipProgress.expandedResearchAreas.readText"));
        action.getInvalidFields().put("input-reportSynthesis.reportSynthesisFlagshipProgress.expandedResearchAreas",
          InvalidFieldsMessages.EMPTYFIELD);
      }
      if (!(this.isValidString(reportSynthesis.getReportSynthesisFlagshipProgress().getDroppedResearchLines()))) {
        action.addMissingField(action.getText("annualReport2018.flagshipProgress.droppedResearchLines.readText"));
        action.getInvalidFields().put("input-reportSynthesis.reportSynthesisFlagshipProgress.droppedResearchLines",
          InvalidFieldsMessages.EMPTYFIELD);
      }
      if (!(this.isValidString(reportSynthesis.getReportSynthesisFlagshipProgress().getChangedDirection()))) {
        action.addMissingField(action.getText("annualReport2018.flagshipProgress.changedDirection.readText"));
        action.getInvalidFields().put("input-reportSynthesis.reportSynthesisFlagshipProgress.changedDirection",
          InvalidFieldsMessages.EMPTYFIELD);
      }

      if (action.isPMU()) {
        if (!(this.isValidString(reportSynthesis.getReportSynthesisFlagshipProgress().getAltmetricScore()))) {
          action.addMissingField(action.getText("annualReport2018.flagshipProgress.altmetricScore.readText"));
          action.getInvalidFields().put("input-reportSynthesis.reportSynthesisFlagshipProgress.altmetricScore",
            InvalidFieldsMessages.EMPTYFIELD);
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

      if (!action.getFieldErrors().isEmpty()) {
        action.addActionError(action.getText("saving.fields.required"));
      } else if (action.getValidationMessage().length() > 0) {
        action.addActionMessage(
          " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));
      }

      this.saveMissingFields(reportSynthesis, action.getActualPhase().getDescription(),
        action.getActualPhase().getYear(), action.getActualPhase().getUpkeep(),
        ReportSynthesis2018SectionStatusEnum.FLAGSHIP_PROGRESS.getStatus(), action);
    }

  }

  public void validateCheckButton(BaseAction action, ReportSynthesis reportSynthesis, boolean saving,
    boolean hasFlagshipsProgress) {
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


      // Validate Relevance to covid field - for Flagships and PMU
      if (reportSynthesis != null && reportSynthesis.getReportSynthesisFlagshipProgress() != null
        && reportSynthesis.getReportSynthesisFlagshipProgress().getRelevanceCovid() != null) {
        if (!(this.isValidString(reportSynthesis.getReportSynthesisFlagshipProgress().getRelevanceCovid()))
          && reportSynthesis.getReportSynthesisFlagshipProgress().getRelevanceCovid().length() < 300) {
          action.addMissingField(action.getText("annualReport2018.flagshipProgress.relevanceCovid"));
          action.getInvalidFields().put("input-reportSynthesis.reportSynthesisFlagshipProgress.relevanceCovid",
            InvalidFieldsMessages.EMPTYFIELD);
        }
      }

      // Validate flagship fields
      if (this.isFlagship(liaisonInstitution)) {
        /*
         * if (!(this.isValidString(reportSynthesis.getReportSynthesisFlagshipProgress().getProgressByFlagships()))) {
         * action.addMissingField(action.getText("annualReport2018.flagshipProgress.progressByFlagships.readText"));
         * action.getInvalidFields().put("input-reportSynthesis.reportSynthesisFlagshipProgress.progressByFlagships",
         * InvalidFieldsMessages.EMPTYFIELD);
         * }
         */
      } else {
        // Validate PMU fields

        /*
         * if (hasFlagshipsProgress == true) {
         * if (!(this.isValidString(reportSynthesis.getReportSynthesisFlagshipProgress().getOverallProgress()))
         * && reportSynthesis.getReportSynthesisFlagshipProgress().getOverallProgress().length() > 250) {
         * action.addMissingField(action.getText("annualReport2018.flagshipProgress.overallProgress.readText"));
         * action.getInvalidFields().put("input-reportSynthesis.reportSynthesisFlagshipProgress.overallProgress",
         * InvalidFieldsMessages.EMPTYFIELD);
         * } else {
         * if (!(this.isValidString(reportSynthesis.getReportSynthesisFlagshipProgress().getOverallProgress()))
         * && reportSynthesis.getReportSynthesisFlagshipProgress().getOverallProgress().length() > 1000) {
         * action.addMissingField(action.getText("annualReport2018.flagshipProgress.overallProgress.readText"));
         * action.getInvalidFields().put("input-reportSynthesis.reportSynthesisFlagshipProgress.overallProgress",
         * InvalidFieldsMessages.EMPTYFIELD);
         * }
         * }
         * }
         */
        if (reportSynthesis != null && reportSynthesis.getReportSynthesisFlagshipProgress() != null
          && reportSynthesis.getReportSynthesisFlagshipProgress().getOverallProgress() != null) {
          if (!(this.isValidString(reportSynthesis.getReportSynthesisFlagshipProgress().getOverallProgress()))
            && reportSynthesis.getReportSynthesisFlagshipProgress().getOverallProgress().length() > 1000) {
            action.addMissingField(action.getText("annualReport2018.flagshipProgress.overallProgress.readText"));
            action.getInvalidFields().put("input-reportSynthesis.reportSynthesisFlagshipProgress.overallProgress",
              InvalidFieldsMessages.EMPTYFIELD);
          }
        }
      }

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
              "Reporting", 2019, false, "flagshipProgress");
          }

          // Add section status to statusOfEveryFlagship list if section status (statusOfFlagship) has missing fields
          SectionStatus statusOfFPMU = sectionStatusManager.getSectionStatusByReportSynthesis(reportSynthesis.getId(),
            "Reporting", 2019, false, "flagshipProgress1");

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

      boolean tableComplete = false;

      if (statusOfEveryFlagship == null || statusOfEveryFlagship.isEmpty()) {
        tableComplete = true;
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
              tableComplete = false;
              action.addMissingField("flagshipProgress1");
              action.addMessage("Flagships with missing information :" + flagshipsWithMisingInformation);
            }
          }
        }
      }


      if (!(this.isValidString(reportSynthesis.getReportSynthesisFlagshipProgress().getExpandedResearchAreas()))) {
        action.addMissingField(action.getText("annualReport2018.flagshipProgress.expandedResearchAreas.readText"));
        action.getInvalidFields().put("input-reportSynthesis.reportSynthesisFlagshipProgress.expandedResearchAreas",
          InvalidFieldsMessages.EMPTYFIELD);
      }
      if (!(this.isValidString(reportSynthesis.getReportSynthesisFlagshipProgress().getDroppedResearchLines()))) {
        action.addMissingField(action.getText("annualReport2018.flagshipProgress.droppedResearchLines.readText"));
        action.getInvalidFields().put("input-reportSynthesis.reportSynthesisFlagshipProgress.droppedResearchLines",
          InvalidFieldsMessages.EMPTYFIELD);
      }
      if (!(this.isValidString(reportSynthesis.getReportSynthesisFlagshipProgress().getChangedDirection()))) {
        action.addMissingField(action.getText("annualReport2018.flagshipProgress.changedDirection.readText"));
        action.getInvalidFields().put("input-reportSynthesis.reportSynthesisFlagshipProgress.changedDirection",
          InvalidFieldsMessages.EMPTYFIELD);
      }

      if (action.isPMU()) {
        if (!(this.isValidString(reportSynthesis.getReportSynthesisFlagshipProgress().getAltmetricScore()))) {
          action.addMissingField(action.getText("annualReport2018.flagshipProgress.altmetricScore.readText"));
          action.getInvalidFields().put("input-reportSynthesis.reportSynthesisFlagshipProgress.altmetricScore",
            InvalidFieldsMessages.EMPTYFIELD);
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
          ReportSynthesis2018SectionStatusEnum.FLAGSHIP_PROGRESS.getStatus(), action);
      } catch (Exception e) {
        LOG.error("Error getting Flagships progress validators: " + e.getMessage());

      }
    }

  }


}
