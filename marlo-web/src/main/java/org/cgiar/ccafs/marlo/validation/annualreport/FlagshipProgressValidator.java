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
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressMilestone;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisSectionStatusEnum;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Named;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@Named
public class FlagshipProgressValidator extends BaseValidator {

  private final GlobalUnitManager crpManager;
  private final ReportSynthesisManager reportSynthesisManager;
  private final LiaisonInstitutionManager liaisonInstitutionManager;

  public FlagshipProgressValidator(GlobalUnitManager crpManager, ReportSynthesisManager reportSynthesisManager,
    LiaisonInstitutionManager liaisonInstitutionManager) {
    this.crpManager = crpManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
  }


  private Path getAutoSaveFilePath(ReportSynthesis reportSynthesis, long crpID, BaseAction baseAction) {
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
    String composedClassName = reportSynthesis.getClass().getSimpleName();
    String actionFile = ReportSynthesisSectionStatusEnum.FLAGSHIP_PROGRESS.getStatus().replace("/", "_");
    String autoSaveFile =
      reportSynthesis.getId() + "_" + composedClassName + "_" + baseAction.getActualPhase().getName() + "_"
        + baseAction.getActualPhase().getYear() + "_" + crp.getAcronym() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public int getIndex(Long crpMilestoneID, ReportSynthesis reportSynthesis) {
    if (reportSynthesis.getReportSynthesisFlagshipProgress().getMilestones() != null) {
      int i = 0;
      for (ReportSynthesisFlagshipProgressMilestone expectedCrpProgress : reportSynthesis
        .getReportSynthesisFlagshipProgress().getMilestones()) {
        if (expectedCrpProgress != null) {
          if (expectedCrpProgress.getCrpMilestone().getId().longValue() == crpMilestoneID.longValue()) {
            return i;
          }
        }
        i++;
      }
    } else {
      reportSynthesis.getReportSynthesisFlagshipProgress().setMilestones(new ArrayList<>());
    }


    return -1;

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


        if (!(this.isValidString(reportSynthesis.getReportSynthesisFlagshipProgress().getSummary()))) {
          action.addMissingField(action.getText("reportSynthesis.reportSynthesisFlagshipProgress.summary"));
          action.getInvalidFields().put("input-reportSynthesis.reportSynthesisFlagshipProgress.summary",
            InvalidFieldsMessages.EMPTYFIELD);
        }

        if (liaisonInstitution.getCrpProgram() != null) {
          CrpProgram crpProgram = liaisonInstitution.getCrpProgram();
          if (crpProgram.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()) {
            if (reportSynthesis.getReportSynthesisFlagshipProgress().getMilestones() != null) {
              if (!reportSynthesis.getReportSynthesisFlagshipProgress().getMilestones().isEmpty()) {
                for (ReportSynthesisFlagshipProgressMilestone expectedCrpProgress : reportSynthesis
                  .getReportSynthesisFlagshipProgress().getMilestones()) {
                  if (expectedCrpProgress != null && expectedCrpProgress.getCrpMilestone() != null
                    && expectedCrpProgress.getCrpMilestone().getId() != null) {
                    int i = this.getIndex(expectedCrpProgress.getCrpMilestone().getId().longValue(), reportSynthesis);
                    if (!this.isValidString(expectedCrpProgress.getEvidence())) {
                      action.addMissingField(action
                        .getText("reportSynthesis.reportSynthesisFlagshipProgress.milestones[" + i + "].evidence"));
                      action.getInvalidFields().put(
                        "input-reportSynthesis.reportSynthesisFlagshipProgress.milestones[" + i + "].evidence",
                        InvalidFieldsMessages.EMPTYFIELD);
                    }
                  }
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
        ReportSynthesisSectionStatusEnum.FLAGSHIP_PROGRESS.getStatus(), action);
    }

  }


}
