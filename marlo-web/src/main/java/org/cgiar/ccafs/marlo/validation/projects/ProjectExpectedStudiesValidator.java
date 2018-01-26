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
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
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
 * @author Christian Garcia - CIAT/CCAFS
 */
@Named
public class ProjectExpectedStudiesValidator extends BaseValidator {

  private final GlobalUnitManager crpManager;

  @Inject
  public ProjectExpectedStudiesValidator(GlobalUnitManager crpManager) {
    this.crpManager = crpManager;
  }

  private Path getAutoSaveFilePath(Project project, long crpID) {
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
    String composedClassName = project.getClass().getSimpleName();
    String actionFile = ProjectSectionStatusEnum.EXPECTEDSTUDIES.getStatus().replace("/", "_");
    String autoSaveFile =
      project.getId() + "_" + composedClassName + "_" + crp.getAcronym() + "_" + actionFile + ".json";
    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public void validate(BaseAction action, Project project, boolean saving) {
    action.setInvalidFields(new HashMap<>());
    if (!saving) {
      Path path = this.getAutoSaveFilePath(project, action.getCrpID());

      if (path.toFile().exists()) {
        action.addMissingField("draft");
      }
    }


    if (project.getExpectedStudies() == null) {
      project.setExpectedStudies(new ArrayList<>());
    }

    if (project.getExpectedStudies().isEmpty()) {
      action.addMessage(action.getText("empty expected"));
    }
    int i = 0;
    for (ProjectExpectedStudy projectExpectedStudy : project.getExpectedStudies()) {

      this.validateProjectExpectedStudy(projectExpectedStudy, i, "expectedStudies", action);
      i++;

    }


    if (!action.getFieldErrors().isEmpty())

    {
      action.addActionError(action.getText("saving.fields.required"));
    } else if (action.getValidationMessage().length() > 0)

    {
      action.addActionMessage(
        " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));
    }

    this.saveMissingFields(project, action.getActualPhase().getDescription(), action.getActualPhase().getYear(),
      ProjectSectionStatusEnum.EXPECTEDSTUDIES.getStatus(), action);

  }

  public void validateProjectExpectedStudy(ProjectExpectedStudy projectExpectedStudy, int index, String listName,
    BaseAction action) {

    List<String> params = new ArrayList<>();
    params.add(String.valueOf(projectExpectedStudy.getId()));

    if (!(this.isValidString(projectExpectedStudy.getTopicStudy())
      && this.wordCount(projectExpectedStudy.getTopicStudy()) <= 10)) {
      action.addMessage(action.getText("projectExpectedStudy.topic", params));
      action.getInvalidFields().put("input-project." + listName + "[" + index + "].topicStudy",
        InvalidFieldsMessages.EMPTYFIELD);

    }

    if (!(this.isValidString(projectExpectedStudy.getComments())
      && this.wordCount(projectExpectedStudy.getComments()) <= 100)) {
      action.addMessage(action.getText("projectExpectedStudy.comments", params));
      action.getInvalidFields().put("input-project." + listName + "[" + index + "].comments",
        InvalidFieldsMessages.EMPTYFIELD);
    }
    if (projectExpectedStudy.getType().intValue() <= 0) {
      action.addMessage(action.getText("projectExpectedStudy.type", params));
      action.getInvalidFields().put("input-project." + listName + "[" + index + "].type",
        InvalidFieldsMessages.EMPTYFIELD);
    }
    if (projectExpectedStudy.getScope().intValue() <= 0) {
      action.addMessage(action.getText("projectExpectedStudy.scope", params));
      action.getInvalidFields().put("input-project." + listName + "[" + index + "].scope",
        InvalidFieldsMessages.EMPTYFIELD);
    }
    if (projectExpectedStudy.getSrfSubIdo() == null) {
      action.addMessage(action.getText("projectExpectedStudy.srfSubIdo", params));
      action.getInvalidFields().put("input-project." + listName + "[" + index + "].srfSubIdo.id",
        InvalidFieldsMessages.EMPTYFIELD);
    }
  }
}
