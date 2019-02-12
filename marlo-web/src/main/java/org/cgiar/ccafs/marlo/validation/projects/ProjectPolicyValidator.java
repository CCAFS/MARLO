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

package org.cgiar.ccafs.marlo.validation.projects;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicy;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@Named
public class ProjectPolicyValidator extends BaseValidator {

  private final GlobalUnitManager crpManager;
  private BaseAction baseAction;

  @Inject
  public ProjectPolicyValidator(GlobalUnitManager crpManager) {
    super();
    this.crpManager = crpManager;
  }

  private Path getAutoSaveFilePath(ProjectPolicy policy, long crpID, BaseAction action) {
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
    String composedClassName = policy.getClass().getSimpleName();
    String actionFile = ProjectSectionStatusEnum.POLICY.getStatus().replace("/", "_");
    String autoSaveFile = policy.getId() + "_" + composedClassName + "_" + action.getActualPhase().getName() + "_"
      + action.getActualPhase().getYear() + "_" + crp.getAcronym() + "_" + actionFile + ".json";
    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public void validate(BaseAction action, Project project, ProjectPolicy policy, boolean saving) {

    action.setInvalidFields(new HashMap<>());
    baseAction = action;

    if (!saving) {
      Path path = this.getAutoSaveFilePath(policy, action.getCrpID(), action);
      if (path.toFile().exists()) {
        action.addMissingField("draft");
      }
    }


    if (!action.getFieldErrors().isEmpty()) {
      action.addActionError(action.getText("saving.fields.required"));
    } else if (action.getValidationMessage().length() > 0) {
      action.addActionMessage(
        " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));
    }

    this.saveMissingFields(project, policy, action.getActualPhase().getDescription(), action.getActualPhase().getYear(),
      action.getActualPhase().getUpkeep(), ProjectSectionStatusEnum.POLICIES.getStatus(), action);
  }

  private void validateProjectPolicy(BaseAction action, ProjectPolicy projectPolicy) {

    // Validate Title
    if (!(this.isValidString(projectPolicy.getProjectPolicyInfo(baseAction.getActualPhase()).getTitle())
      && this.wordCount(projectPolicy.getProjectPolicyInfo(baseAction.getActualPhase()).getTitle()) <= 50)) {
      action.addMessage(action.getText("Title"));
      action.addMissingField("projectPolicy.title");
      action.getInvalidFields().put("input-policy.projectPolicyInfo.title", InvalidFieldsMessages.EMPTYFIELD);
    }
  }


}
