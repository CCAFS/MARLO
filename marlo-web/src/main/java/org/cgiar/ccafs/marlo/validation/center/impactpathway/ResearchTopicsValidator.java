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

package org.cgiar.ccafs.marlo.validation.center.impactpathway;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.ICenterManager;
import org.cgiar.ccafs.marlo.data.model.Center;
import org.cgiar.ccafs.marlo.data.model.CenterProgram;
import org.cgiar.ccafs.marlo.data.model.CenterTopic;
import org.cgiar.ccafs.marlo.data.model.ImpactPathwaySectionsEnum;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.inject.Inject;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ResearchTopicsValidator extends BaseValidator {

  ICenterManager centerService;

  @Inject
  public ResearchTopicsValidator(ICenterManager centerService) {
    this.centerService = centerService;
  }

  private Path getAutoSaveFilePath(CenterProgram program, long centerID) {
    Center center = centerService.getCrpById(centerID);
    String composedClassName = program.getClass().getSimpleName();
    String actionFile = ImpactPathwaySectionsEnum.TOPIC.getStatus().replace("/", "_");
    String autoSaveFile =
      program.getId() + "_" + composedClassName + "_" + center.getAcronym() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public void validate(BaseAction baseAction, List<CenterTopic> researchTopics, CenterProgram selectedProgram,
    boolean saving) {

    baseAction.setInvalidFields(new HashMap<>());

    if (!saving) {
      Path path = this.getAutoSaveFilePath(selectedProgram, baseAction.getCenterID());

      if (path.toFile().exists()) {
        this.addMissingField("researchTopic.action.draft");
      }
    }

    if (!baseAction.getFieldErrors().isEmpty()) {
      baseAction.addActionError(baseAction.getText("saving.fields.required"));
    }

    if (researchTopics.size() == 0) {
      this.addMessage(baseAction.getText("researchTopic.action.required"));
      baseAction.getInvalidFields().put("list-researchTopics",
        baseAction.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Research Topics"}));
    }

    for (int i = 0; i < researchTopics.size(); i++) {
      CenterTopic researchTopic = researchTopics.get(i);
      this.validateResearchTopic(baseAction, researchTopic, i);
    }

    this.saveMissingFields(selectedProgram, "researchTopics");

  }

  public void validateResearchTopic(BaseAction baseAction, CenterTopic researchTopic, int i) {

    List<String> params = new ArrayList<String>();
    params.add(String.valueOf(i + 1));

    if (researchTopic.getResearchTopic() != null) {
      if (!this.isValidString(researchTopic.getResearchTopic())
        && this.wordCount(researchTopic.getResearchTopic()) <= 15) {
        this.addMessage(baseAction.getText("researchTopic.action.description.required", params));
        baseAction.getInvalidFields().put("input-topics[" + i + "].researchTopic", InvalidFieldsMessages.EMPTYFIELD);
      }
    } else {
      this.addMessage(baseAction.getText("researchTopic.action.description.required", params));
      baseAction.getInvalidFields().put("input-topics[" + i + "].researchTopic", InvalidFieldsMessages.EMPTYFIELD);
    }

  }

}
