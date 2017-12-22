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
import org.cgiar.ccafs.marlo.data.model.CenterOutput;
import org.cgiar.ccafs.marlo.data.model.CenterOutputsNextUser;
import org.cgiar.ccafs.marlo.data.model.CenterProgram;
import org.cgiar.ccafs.marlo.data.model.ImpactPathwaySectionsEnum;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@Named
public class OutputsValidator extends BaseValidator {

  private final ICenterManager centerService;

  @Inject
  public OutputsValidator(ICenterManager centerService) {
    this.centerService = centerService;
  }

  private Path getAutoSaveFilePath(CenterOutput output, long centerID) {
    Center center = centerService.getCrpById(centerID);
    String composedClassName = output.getClass().getSimpleName();
    String actionFile = ImpactPathwaySectionsEnum.OUTPUT.getStatus().replace("/", "_");
    String autoSaveFile =
      output.getId() + "_" + composedClassName + "_" + center.getAcronym() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public void validate(BaseAction baseAction, CenterOutput output, CenterProgram selectedProgram, boolean saving) {
    baseAction.setInvalidFields(new HashMap<>());
    this.missingFields.setLength(0);
    if (!saving) {
      Path path = this.getAutoSaveFilePath(output, baseAction.getCenterID());

      if (path.toFile().exists()) {
        this.addMissingField(baseAction.getText("output.action.draft"));
      }
    }

    if (!baseAction.getFieldErrors().isEmpty()) {
      baseAction.addActionError(baseAction.getText("saving.fields.required"));
    }

    this.validateOutput(baseAction, output);

    this.saveMissingFields(selectedProgram, output, "outputsList");
  }

  public void validateNextUser(BaseAction baseAction, CenterOutputsNextUser nextUser, int i) {

    List<String> params = new ArrayList<String>();
    params.add(String.valueOf(i + 1));

    if (nextUser.getNextuserType() != null) {
      if (nextUser.getNextuserType().getNextuserType().getId() != null) {
        if (nextUser.getNextuserType().getNextuserType().getId() == -1) {
          this.addMessage(baseAction.getText("output.action.nextusers.type", params));
          baseAction.getInvalidFields().put("input-output.nextUsers[" + i + "].nextuserType.nextuserType.id",
            InvalidFieldsMessages.EMPTYFIELD);
        }
      } else {
        this.addMessage(baseAction.getText("output.action.nextusers.type", params));
        baseAction.getInvalidFields().put("input-output.nextUsers[" + i + "].nextuserType.nextuserType.id",
          InvalidFieldsMessages.EMPTYFIELD);
      }
    } else {
      this.addMessage(baseAction.getText("output.action.nextusers.type", params));
      baseAction.getInvalidFields().put("input-output.nextUsers[" + i + "].nextuserType.nextuserType.id",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    if (nextUser.getNextuserType() != null) {
      if (nextUser.getNextuserType().getId() != null) {
        if (nextUser.getNextuserType().getId() == -1) {
          this.addMessage(baseAction.getText("output.action.nextusers.subType", params));
          baseAction.getInvalidFields().put("input-output.nextUsers[" + i + "].nextuserType.id",
            InvalidFieldsMessages.EMPTYFIELD);
        }
      } else {
        this.addMessage(baseAction.getText("output.action.nextusers.subType", params));
        baseAction.getInvalidFields().put("input-output.nextUsers[" + i + "].nextuserType.id",
          InvalidFieldsMessages.EMPTYFIELD);
      }
    } else {
      this.addMessage(baseAction.getText("output.action.nextusers.subType", params));
      baseAction.getInvalidFields().put("input-output.nextUsers[" + i + "].nextuserType.id",
        InvalidFieldsMessages.EMPTYFIELD);
    }

  }

  public void validateOutput(BaseAction baseAction, CenterOutput output) {

    if (output.getTitle() != null) {
      if (!this.isValidString(output.getTitle()) && this.wordCount(output.getTitle()) <= 50) {
        this.addMessage(baseAction.getText("output.action.title.required"));
        baseAction.getInvalidFields().put("input-output.title", InvalidFieldsMessages.EMPTYFIELD);
      }
    } else {
      this.addMessage(baseAction.getText("output.action.title.required"));
      baseAction.getInvalidFields().put("input-output.title", InvalidFieldsMessages.EMPTYFIELD);
    }


    if (output.getNextUsers() != null) {
      if (output.getNextUsers().size() == 0) {

        this.addMessage(baseAction.getText("output.action.nextusers"));
        baseAction.getInvalidFields().put("list-output.nextUsers",
          baseAction.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"nextUsers"}));
      } else {
        for (int i = 0; i < output.getNextUsers().size(); i++) {
          CenterOutputsNextUser nextuser = output.getNextUsers().get(i);
          this.validateNextUser(baseAction, nextuser, i);
        }
      }
    } else {

      this.addMessage(baseAction.getText("programImpact.action.beneficiary"));
      baseAction.getInvalidFields().put("list-output.nextUsers",
        baseAction.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"nextUsers"}));
    }

  }


}
