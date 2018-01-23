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

package org.cgiar.ccafs.marlo.validation.sythesis;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.IpElement;
import org.cgiar.ccafs.marlo.data.model.IpProgram;
import org.cgiar.ccafs.marlo.data.model.OutcomeSynthesy;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@Named
public class SynthesisByOutcomeValidator extends BaseValidator {

  @Inject
  // GlobalUnit Manager
  private GlobalUnitManager crpManager;

  @Inject
  public SynthesisByOutcomeValidator(GlobalUnitManager crpManager) {
    super();
    this.crpManager = crpManager;
  }

  private Path getAutoSaveFilePath(IpProgram ipProgram, long crpID) {
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
    String composedClassName = ipProgram.getClass().getSimpleName();
    String actionFile = ProjectSectionStatusEnum.SYNTHESISOUTCOME.getStatus().replace("/", "_");
    String autoSaveFile =
      ipProgram.getId() + "_" + composedClassName + "_" + crp.getAcronym() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public void replaceAll(StringBuilder builder, String from, String to) {
    int index = builder.indexOf(from);
    while (index != -1) {
      builder.replace(index, index + from.length(), to);
      index += to.length(); // Move to the end of the replacement
      index = builder.indexOf(from, index);
    }
  }

  public void validate(BaseAction action, List<OutcomeSynthesy> synthesis, IpProgram ipProgram, boolean saving) {
    // BaseValidator does not Clean this variables.. so before validate the section, it be clear these variables
    action.setInvalidFields(new HashMap<>());

    if (!saving) {
      Path path = this.getAutoSaveFilePath(ipProgram, action.getCrpID());

      if (path.toFile().exists()) {
        action.addMissingField("draft");
      }
    }

    int index = 0;
    for (OutcomeSynthesy outcomeSynthesy : synthesis) {
      IpElement ipElement = outcomeSynthesy.getIpElement();

      this.validateSynthesisAnual(action, outcomeSynthesy.getSynthesisAnual(), ipElement.getComposedId(), 250, index);
      this.validateSynthesisGender(action, outcomeSynthesy.getSynthesisGender(), ipElement.getComposedId(), 200, index);

      if (outcomeSynthesy.getAchieved() == null || outcomeSynthesy.getAchieved().doubleValue() < 0) {
        action.addMessage(action.getText("synthesisByMog.validator.achieved", ipElement.getComposedId()));
        action.getInvalidFields().put("input-program.synthesisOutcome[" + index + "].achieved",
          InvalidFieldsMessages.EMPTYFIELD);
      }
      this.validateLessonsLearn(action, ipProgram);
      if (action.getValidationMessage().toString().contains("Lessons")) {
        this.replaceAll(action.getValidationMessage(), "Lessons",
          "Lessons regarding partnerships and possible implications for the coming planning cycle");
        action.getInvalidFields().put("input-program.projectComponentLesson.lessons", InvalidFieldsMessages.EMPTYFIELD);
      }

      index++;
    }


    if (!action.getFieldErrors().isEmpty()) {
      action.addActionError(action.getText("saving.fields.required"));
    } else if (action.getValidationMessage().length() > 0) {
      action.addActionMessage(
        " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));
    }
    if (action.isReportingActive()) {
      this.saveMissingFields(ipProgram, APConstants.REPORTING, action.getReportingYear(),
        ProjectSectionStatusEnum.SYNTHESISOUTCOME.getStatus(), action);
    } else {
      this.saveMissingFields(ipProgram, APConstants.PLANNING, action.getPlanningYear(),
        ProjectSectionStatusEnum.SYNTHESISOUTCOME.getStatus(), action);
    }

  }

  private void validateSynthesisAnual(BaseAction action, String synthesisAnual, String midOutcome, int numberWords,
    int i) {
    if (!(this.isValidString(synthesisAnual) && this.wordCount(synthesisAnual) <= numberWords)) {
      action.addMessage(action.getText("synthesisByMog.validator.anual", midOutcome));
      action.getInvalidFields().put("input-program.synthesisOutcome[" + i + "].synthesisAnual",
        InvalidFieldsMessages.EMPTYFIELD);

    }
  }

  private void validateSynthesisGender(BaseAction action, String synthesisGender, String midOutcome, int numberWords,
    int i) {
    if (!(this.isValidString(synthesisGender) && this.wordCount(synthesisGender) <= numberWords)) {
      action.addMessage(action.getText("synthesisByMog.validator.gender", midOutcome));
      action.getInvalidFields().put("input-program.synthesisOutcome[" + i + "].synthesisGender",
        InvalidFieldsMessages.EMPTYFIELD);

    }
  }

}
