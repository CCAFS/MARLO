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
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis2018SectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisSrfProgressTarget;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@Named
public class SrfProgressValidator extends BaseValidator {

  private static Logger LOG = LoggerFactory.getLogger(SrfProgressValidator.class);

  private final GlobalUnitManager crpManager;
  private final ReportSynthesisManager reportSynthesisManager;
  private final SectionStatusManager sectionStatusManager;


  public SrfProgressValidator(GlobalUnitManager crpManager, ReportSynthesisManager reportSynthesisManager,
    SectionStatusManager sectionStatusManager) {
    this.crpManager = crpManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.sectionStatusManager = sectionStatusManager;
  }


  private Path getAutoSaveFilePath(ReportSynthesis reportSynthesis, long crpID, BaseAction baseAction) {
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
    String composedClassName = reportSynthesis.getClass().getSimpleName();
    String actionFile = ReportSynthesis2018SectionStatusEnum.CRP_PROGRESS.getStatus().replace("/", "_");
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
      if (!(this.isValidString(reportSynthesis.getReportSynthesisSrfProgress().getSummary())
        && this.wordCount(this.removeHtmlTags(reportSynthesis.getReportSynthesisSrfProgress().getSummary())) <= 400)) {
        action.addMessage(action.getText("reportSynthesis.reportSynthesisSrfProgress.overallContribution"));
        action.getInvalidFields().put("input-reportSynthesis.reportSynthesisSrfProgress.summary",
          InvalidFieldsMessages.EMPTYFIELD);
      }

      // Validate Targets
      if (reportSynthesis.getReportSynthesisSrfProgress().getSloTargets() != null) {
        for (int i = 0; i < reportSynthesis.getReportSynthesisSrfProgress().getSloTargets().size(); i++) {
          this.validateTargets(action, reportSynthesis.getReportSynthesisSrfProgress().getSloTargets().get(i), i);
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
        ReportSynthesis2018SectionStatusEnum.CRP_PROGRESS.getStatus(), action);
    }
  }

  public void validateCheckButton(BaseAction action, ReportSynthesis reportSynthesis, boolean saving) {
    action.setInvalidFields(new HashMap<>());
    if (reportSynthesis != null) {
      if (!saving) {
        Path path = this.getAutoSaveFilePath(reportSynthesis, action.getCrpID(), action);
        if (path.toFile().exists()) {
          action.addMissingField("draft");
        }
      }

      // Validate Summary
      if (!(this.isValidString(reportSynthesis.getReportSynthesisSrfProgress().getSummary())
        && this.wordCount(this.removeHtmlTags(reportSynthesis.getReportSynthesisSrfProgress().getSummary())) <= 400)) {
        action.addMessage(action.getText("reportSynthesis.reportSynthesisSrfProgress.overallContribution"));
        action.getInvalidFields().put("input-reportSynthesis.reportSynthesisSrfProgress.summary",
          InvalidFieldsMessages.EMPTYFIELD);
      }

      // Validate Targets
      if (reportSynthesis.getReportSynthesisSrfProgress().getSloTargets() != null) {
        for (int i = 0; i < reportSynthesis.getReportSynthesisSrfProgress().getSloTargets().size(); i++) {
          this.validateTargets(action, reportSynthesis.getReportSynthesisSrfProgress().getSloTargets().get(i), i);
        }
      }


      if (!action.getFieldErrors().isEmpty()) {
        action.addActionError(action.getText("saving.fields.required"));
      } else if (action.getValidationMessage().length() > 0) {
        action.addActionMessage(
          " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));
      }


      boolean tableComplete = false;
      SectionStatus sectionStatus = sectionStatusManager.getSectionStatusByReportSynthesis(reportSynthesis.getId(),
        "Reporting", 2019, false, "crpProgress");

      if (sectionStatus == null) {
        tableComplete = true;
        // sectionStatusManager.deleteSectionStatus(sectionStatus.getId());
      } else

      if (sectionStatus != null && sectionStatus.getMissingFields() != null && sectionStatus.getId() != null
        && sectionStatus.getMissingFields().length() != 0) {
        if (sectionStatus.getMissingFields().contains("crpProgress1")) {
          sectionStatusManager.deleteSectionStatus(sectionStatus.getId());
          tableComplete = true;
        } else {
          tableComplete = false;
        }
      } else {
        if (sectionStatus != null && sectionStatus.getId() != null) {
          tableComplete = true;
          sectionStatusManager.deleteSectionStatus(sectionStatus.getId());
        }
      }

      try {
        this.saveMissingFields(reportSynthesis, action.getActualPhase().getDescription(),
          action.getActualPhase().getYear(), action.getActualPhase().getUpkeep(),
          ReportSynthesis2018SectionStatusEnum.CRP_PROGRESS.getStatus(), action);
      } catch (Exception e) {
        LOG.error("Error getting srfProgress  validators: " + e.getMessage());

      }
    }

  }

  public void validateTargets(BaseAction action, ReportSynthesisSrfProgressTarget target, int i) {

    // Validate Brief Summary
    if (!(this.isValidString(target.getBirefSummary())
      && this.wordCount(this.removeHtmlTags(target.getBirefSummary())) <= 150)) {
      action.addMessage(action.getText("Brief Summary"));
      action.addMissingField("Brief Summary");
      action.getInvalidFields().put(
        "input-reportSynthesis.reportSynthesisSrfProgress.sloTargets[" + i + "].birefSummary",
        InvalidFieldsMessages.EMPTYFIELD);;
    }

    // Validate Brief Summary
    /*
     * if (!(this.isValidString(target.getAdditionalContribution())
     * && this.wordCount(target.getAdditionalContribution()) <= 100)) {
     * action.addMessage(action.getText("Additional Contribution"));
     * action.addMissingField("Additional Contribution");
     * action.getInvalidFields().put(
     * "input-reportSynthesis.reportSynthesisSrfProgress.sloTargets[" + i + "].additionalContribution",
     * InvalidFieldsMessages.EMPTYFIELD);;
     * }
     */
  }

}