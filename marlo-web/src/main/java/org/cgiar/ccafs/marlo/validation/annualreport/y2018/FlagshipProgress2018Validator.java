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
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis2018SectionStatusEnum;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import javax.inject.Named;

/**
 * @author Andrés Valencia - CIAT/CCAFS
 */
@Named
public class FlagshipProgress2018Validator extends BaseValidator {

  private final GlobalUnitManager crpManager;
  private final ReportSynthesisManager reportSynthesisManager;
  private final LiaisonInstitutionManager liaisonInstitutionManager;
  private final CrpProgramManager crpProgramManager;

  public FlagshipProgress2018Validator(GlobalUnitManager crpManager, ReportSynthesisManager reportSynthesisManager,
    LiaisonInstitutionManager liaisonInstitutionManager, CrpProgramManager crpProgramManager) {
    this.crpManager = crpManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.crpProgramManager = crpProgramManager;
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

      // Validate flagship fields
      if (this.isFlagship(liaisonInstitution)) {
        if (!(this.isValidString(reportSynthesis.getReportSynthesisFlagshipProgress().getProgressByFlagships()))) {
          action.addMissingField(action.getText("annualReport2018.flagshipProgress.progressByFlagships.readText"));
          action.getInvalidFields().put("input-reportSynthesis.reportSynthesisFlagshipProgress.progressByFlagships",
            InvalidFieldsMessages.EMPTYFIELD);
        }
      } else {
        // Validate PMU fields
        if (!(this.isValidString(reportSynthesis.getReportSynthesisFlagshipProgress().getOverallProgress()))) {
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


}
