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
public class ProjectLocationValidator extends BaseValidator {

  private final GlobalUnitManager crpManager;

  @Inject
  public ProjectLocationValidator(GlobalUnitManager crpManager) {
    super();
    this.crpManager = crpManager;
  }

  private Path getAutoSaveFilePath(Project project, long crpID, BaseAction action) {
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
    String composedClassName = project.getClass().getSimpleName();
    String actionFile = ProjectSectionStatusEnum.LOCATIONS.getStatus().replace("/", "_");
    String autoSaveFile = project.getId() + "_" + composedClassName + "_" + action.getActualPhase().getDescription()
      + "_" + action.getActualPhase().getYear() + "_" + crp.getAcronym() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public void validate(BaseAction action, Project project, boolean saving) {
    action.setInvalidFields(new HashMap<>());

    if (!saving) {
      Path path = this.getAutoSaveFilePath(project, action.getCrpID(), action);

      if (path.toFile().exists()) {
        action.addMissingField("draft");
      }
    }

    this.validateLocation(action, project);
    if (!action.getFieldErrors().isEmpty()) {
      action.addActionError(action.getText("saving.fields.required"));
    } else if (action.getValidationMessage().length() > 0) {
      action.addActionMessage(
        " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));
    }

    this.saveMissingFields(project, action.getActualPhase().getDescription(), action.getActualPhase().getYear(),
      ProjectSectionStatusEnum.LOCATIONS.getStatus(), action);
  }

  public void validateLocation(BaseAction action, Project project) {

    if (project.getLocationsData() == null || project.getLocationsData().isEmpty()) {
      if (project.getRegionFS() == null || project.getRegionFS().isEmpty()) {
        if (project.getCountryFS() == null || project.getCountryFS().isEmpty()) {
          if (project.getProjecInfoPhase(action.getActualPhase()).getLocationGlobal() != null
            && project.getProjecInfoPhase(action.getActualPhase()).getLocationGlobal().booleanValue() == false) {
            action.getInvalidFields().put("list-project.locationsData",
              action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Locations"}));
            action.addMessage(action.getText("project.locationsData"));
          }


        }

      }

    }


  }

}