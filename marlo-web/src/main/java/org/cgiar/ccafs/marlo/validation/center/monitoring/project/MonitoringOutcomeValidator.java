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

package org.cgiar.ccafs.marlo.validation.center.monitoring.project;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.model.CenterMonitoringMilestone;
import org.cgiar.ccafs.marlo.data.model.CenterMonitoringOutcome;
import org.cgiar.ccafs.marlo.data.model.CenterMonitoringOutcomeEvidence;
import org.cgiar.ccafs.marlo.data.model.CenterOutcome;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
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
public class MonitoringOutcomeValidator extends BaseValidator {

  // GlobalUnit Manager
  private GlobalUnitManager centerService;

  public MonitoringOutcomeValidator(GlobalUnitManager centerService) {
    this.centerService = centerService;
  }

  private Path getAutoSaveFilePath(CenterOutcome outcome, long centerID, BaseAction baseAction) {
    GlobalUnit center = centerService.getGlobalUnitById(centerID);
    String composedClassName = outcome.getClass().getSimpleName();
    String actionFile = "monitoringOutcome".replace("/", "_");
    String autoSaveFile = outcome.getId() + "_" + composedClassName + "_" + baseAction.getActualPhase().getName() + "_"
      + baseAction.getActualPhase().getYear() + "_" + center.getAcronym() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public void validate(BaseAction baseAction, CenterOutcome outcome, CrpProgram selectedProgram, boolean saving) {
    baseAction.setInvalidFields(new HashMap<>());

    if (!saving) {
      Path path = this.getAutoSaveFilePath(outcome, baseAction.getCenterID(), baseAction);

      if (path.toFile().exists()) {
        baseAction.addMissingField(baseAction.getText("outcome.action.draft"));
      }
    }

    if (!baseAction.getFieldErrors().isEmpty()) {
      baseAction.addActionError(baseAction.getText("saving.fields.required"));
    }

    this.validateOutcome(baseAction, outcome);

    this.saveMissingFields(selectedProgram, outcome, "monitoringOutcome", baseAction);

  }

  public void validateEvidences(BaseAction baseAction, CenterMonitoringOutcomeEvidence evidence, int i, int j) {
    if (!this.isValidUrl(evidence.getEvidenceLink())) {
      baseAction.addMessage(baseAction.getText("Evidences Link"));
      baseAction.getInvalidFields().put("input-outcome.monitorings[" + i + "].evidences[" + j + "].evidenceLink",
        InvalidFieldsMessages.EMPTYFIELD);
    }

  }

  public void validateMilestones(BaseAction baseAction, CenterMonitoringMilestone milestones, int i, int j, int year) {
    if (milestones.getYear() == year) {
      if (!(this.isValidString(milestones.getNarrative()) && this.wordCount(milestones.getNarrative()) <= 100)) {
        baseAction.addMessage(baseAction.getText("Milestone Narrative"));
        baseAction.getInvalidFields().put("input-outcome.monitorings[" + i + "].milestones[" + j + "].narrative",
          InvalidFieldsMessages.EMPTYFIELD);
      }
    }

  }

  public void validateOutcome(BaseAction baseAction, CenterOutcome outcome) {

    int year = baseAction.getCurrentCycleYear();
    int i = 0;
    if (outcome.getMonitorings() != null || !outcome.getMonitorings().isEmpty()) {
      for (CenterMonitoringOutcome monitoringOutcome : outcome.getMonitorings()) {
        if (monitoringOutcome.getYear() == year) {
          if (monitoringOutcome.getStatusQuo() != null) {
            if (!this.isValidString(monitoringOutcome.getStatusQuo())
              && this.wordCount(monitoringOutcome.getStatusQuo()) <= 100) {
              baseAction.addMessage(baseAction.getText("outcome.monitoring.statusQuo"));
              baseAction.getInvalidFields().put("input-outcome.monitorings[" + i + "].statusQuo",
                InvalidFieldsMessages.EMPTYFIELD);
            }
          } else {
            baseAction.addMessage(baseAction.getText("outcome.monitoring.statusQuo"));
            baseAction.getInvalidFields().put("input-outcome.monitorings[" + i + "].statusQuo",
              InvalidFieldsMessages.EMPTYFIELD);
          }

          if (!this.isValidString(monitoringOutcome.getCiatRole())
            && this.wordCount(monitoringOutcome.getCiatRole()) <= 100) {
            baseAction.addMessage(baseAction.getText("outcome.monitoring.ciatRole"));
            baseAction.getInvalidFields().put("input-outcome.monitorings[" + i + "].ciatRole",
              InvalidFieldsMessages.EMPTYFIELD);
          }

          if (!this.isValidString(monitoringOutcome.getWhatChanged())
            && this.wordCount(monitoringOutcome.getWhatChanged()) <= 100) {
            baseAction.addMessage(baseAction.getText("outcome.monitoring.whatChanged"));
            baseAction.getInvalidFields().put("input-outcome.monitorings[" + i + "].whatChanged",
              InvalidFieldsMessages.EMPTYFIELD);
          }


          if (monitoringOutcome.getMilestones() != null) {
            for (int j = 0; j < monitoringOutcome.getMilestones().size(); j++) {
              this.validateMilestones(baseAction, monitoringOutcome.getMilestones().get(j), i, j, year);
            }
          }

          if (monitoringOutcome.getEvidences() != null) {
            for (int j = 0; j < monitoringOutcome.getEvidences().size(); j++) {
              this.validateEvidences(baseAction, monitoringOutcome.getEvidences().get(j), i, j);
            }
          }
        }
        i++;
      }
    }


  }

}
