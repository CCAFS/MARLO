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
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
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

/**
 * @author Andrés Valencia - CIAT/CCAFS
 */
@Named
public class CCDimension2018Validator extends BaseValidator {

  private final GlobalUnitManager crpManager;
  private final ReportSynthesisManager reportSynthesisManager;
  private final LiaisonInstitutionManager liaisonInstitutionManager;
  private final SectionStatusManager sectionStatusManager;

  public CCDimension2018Validator(GlobalUnitManager crpManager, ReportSynthesisManager reportSynthesisManager,
    LiaisonInstitutionManager liaisonInstitutionManager, SectionStatusManager sectionStatusManager) {
    this.crpManager = crpManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.sectionStatusManager = sectionStatusManager;
  }


  private Path getAutoSaveFilePath(ReportSynthesis reportSynthesis, long crpID, BaseAction baseAction) {
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
    String composedClassName = reportSynthesis.getClass().getSimpleName();
    String actionFile = ReportSynthesis2018SectionStatusEnum.CC_DIMENSIONS.getStatus().replace("/", "_");
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

      // Validate Gender
      if (!this.isValidString(reportSynthesis.getReportSynthesisCrossCuttingDimension().getGenderResearchFindings())) {
        action.addMessage(action.getText("annualReport2018.ccDimensions.gender.researchFindings.readText"));
        action.getInvalidFields().put(
          "input-reportSynthesis.reportSynthesisCrossCuttingDimension.genderResearchFindings",
          InvalidFieldsMessages.EMPTYFIELD);
      }
      if (!this.isValidString(reportSynthesis.getReportSynthesisCrossCuttingDimension().getGenderLearned())) {
        action.addMessage(action.getText("annualReport2018.ccDimensions.gender.learned.readText"));
        action.getInvalidFields().put("input-reportSynthesis.reportSynthesisCrossCuttingDimension.genderLearned",
          InvalidFieldsMessages.EMPTYFIELD);
      }
      if (!this.isValidString(reportSynthesis.getReportSynthesisCrossCuttingDimension().getGenderProblemsArisen())) {
        action.addMessage(action.getText("annualReport2018.ccDimensions.gender.problemsArisen.readText"));
        action.getInvalidFields().put("input-reportSynthesis.reportSynthesisCrossCuttingDimension.genderProblemsArisen",
          InvalidFieldsMessages.EMPTYFIELD);
      }

      // Validate Youth
      /*
       * if (!this.isValidString(reportSynthesis.getReportSynthesisCrossCuttingDimension().getYouthContribution())) {
       * action.addMessage(action.getText("annualReport2018.ccDimensions.youth.youthContribution.readText"));
       * action.getInvalidFields().put("input-reportSynthesis.reportSynthesisCrossCuttingDimension.youthContribution",
       * InvalidFieldsMessages.EMPTYFIELD);
       * }
       */

      if (!this.isValidString(reportSynthesis.getReportSynthesisCrossCuttingDimension().getYouthResearchFindings())) {
        action.addMessage(action.getText("annualReport2018.ccDimensions.youth.researchFindings.readText"));
        action.getInvalidFields().put(
          "input-reportSynthesis.reportSynthesisCrossCuttingDimension.youthResearchFindings",
          InvalidFieldsMessages.EMPTYFIELD);
      }

      if (!this.isValidString(reportSynthesis.getReportSynthesisCrossCuttingDimension().getYouthLearned())) {
        action.addMessage(action.getText("annualReport2018.ccDimensions.youth.learned.readText"));
        action.getInvalidFields().put("input-reportSynthesis.reportSynthesisCrossCuttingDimension.youthLearned",
          InvalidFieldsMessages.EMPTYFIELD);
      }
      if (!this.isValidString(reportSynthesis.getReportSynthesisCrossCuttingDimension().getYouthProblemsArisen())) {
        action.addMessage(action.getText("annualReport2018.ccDimensions.youth.problemsArisen.readText"));
        action.getInvalidFields().put("input-reportSynthesis.reportSynthesisCrossCuttingDimension.youthProblemsArisen",
          InvalidFieldsMessages.EMPTYFIELD);
      }

      // Validate CapDev
      if (!this.isValidString(reportSynthesis.getReportSynthesisCrossCuttingDimension().getCapDevKeyAchievements())) {
        action.addMessage(action.getText("annualReport2018.ccDimensions.capDev.keyAchievements.readText"));
        action.getInvalidFields().put(
          "input-reportSynthesis.reportSynthesisCrossCuttingDimension.capDevKeyAchievements",
          InvalidFieldsMessages.EMPTYFIELD);
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
            statusOfFlagship = sectionStatusManager.getSectionStatusByReportSynthesis(reportSynthesisAux.getId(),
              "Reporting", 2019, false, "ccDimensions");

            // Add section status to statusOfEveryFlagship list if section status (statusOfFlagship) has missing fields
            SectionStatus statusOfFPMU = sectionStatusManager.getSectionStatusByReportSynthesis(reportSynthesis.getId(),
              "Reporting", 2019, false, "ccDimensions1");

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
                action.addMissingField("ccDimensions1");
                action.addMessage("ccDimensions with missing information :" + flagshipsWithMisingInformation);
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

      this.saveMissingFields(reportSynthesis, action.getActualPhase().getDescription(),
        action.getActualPhase().getYear(), action.getActualPhase().getUpkeep(),
        ReportSynthesis2018SectionStatusEnum.CC_DIMENSIONS.getStatus(), action);
    }

  }

}