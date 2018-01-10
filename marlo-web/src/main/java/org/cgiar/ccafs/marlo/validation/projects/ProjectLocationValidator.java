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
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@Named
public class ProjectLocationValidator extends BaseValidator {

  private final CrpManager crpManager;

  @Inject
  public ProjectLocationValidator(CrpManager crpManager) {
    super();
    this.crpManager = crpManager;
  }

  private Path getAutoSaveFilePath(Project project, long crpID) {
    Crp crp = crpManager.getCrpById(crpID);
    String composedClassName = project.getClass().getSimpleName();
    String actionFile = ProjectSectionStatusEnum.LOCATIONS.getStatus().replace("/", "_");
    String autoSaveFile =
      project.getId() + "_" + composedClassName + "_" + crp.getAcronym() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public void validate(BaseAction action, Project project, boolean saving) {
    // BaseValidator does not Clean this variables.. so before validate the section, it be clear these variables
    this.missingFields.setLength(0);
    this.validationMessage.setLength(0);
    action.setInvalidFields(new HashMap<>());

    if (!saving) {
      Path path = this.getAutoSaveFilePath(project, action.getCrpID());

      if (path.toFile().exists()) {
        this.addMissingField("draft");
      }
    }

    this.validateLocation(action, project);
    if (!action.getFieldErrors().isEmpty()) {
      action.addActionError(action.getText("saving.fields.required"));
    } else if (validationMessage.length() > 0) {
      action
        .addActionMessage(" " + action.getText("saving.missingFields", new String[] {validationMessage.toString()}));
    }

    if (action.isReportingActive()) {
      this.saveMissingFields(project, APConstants.REPORTING, action.getReportingYear(),
        ProjectSectionStatusEnum.LOCATIONS.getStatus());
    } else {
      this.saveMissingFields(project, APConstants.PLANNING, action.getPlanningYear(),
        ProjectSectionStatusEnum.LOCATIONS.getStatus());
    }
  }

  public void validateLocation(BaseAction action, Project project) {


    if (!action.hasSpecificities(APConstants.CRP_OTHER_LOCATIONS)) {


      if (!project.isLocationGlobal()) {
        if (project.getCountryFS() != null) {

          if (project.getCountryFS().stream().filter(c -> c.isSelected()).collect(Collectors.toList()).isEmpty()) {
            action.getInvalidFields().put("list-project.locationsData",
              action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Locations"}));
            this.addMessage(action.getText("project.countries"));
          }
        } else {
          action.getInvalidFields().put("list-project.locationsData",
            action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Locations"}));
          this.addMessage(action.getText("project.countries"));
        }
      }

      if (project.getLocationRegional() != null && project.getLocationRegional()) {
        if (project.getRegionFS() != null) {
          if (project.getRegionFS().stream().filter(c -> c.isSelected()).collect(Collectors.toList()).isEmpty()) {
            action.getInvalidFields().put("list-project.locationsData",
              action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Locations"}));
            this.addMessage(action.getText("project.regions"));
          } else {
            this.validationMessage = new StringBuilder();
            this.missingFields.setLength(0);
            action.getInvalidFields().clear();
          }
        } else {
          action.getInvalidFields().put("list-project.locationsData",
            action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Locations"}));
          this.addMessage(action.getText("project.regions"));
        }
      }


    } else

    {
      if (project.getLocationsData() == null || project.getLocationsData().isEmpty()) {
        if (!project.isLocationGlobal()) {
          action.getInvalidFields().put("list-project.locationsData",
            action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Locations"}));
          this.addMessage(action.getText("project.locationsData"));
          if (!project.isLocationGlobal()) {
            if (project.getCountryFS() != null) {

              if (project.getCountryFS().stream().filter(c -> c.isSelected()).collect(Collectors.toList()).isEmpty()) {
                action.getInvalidFields().put("list-project.locationsData",
                  action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Locations"}));
                this.addMessage(action.getText("project.countries"));
              } else {
                this.validationMessage = new StringBuilder();
                action.getInvalidFields().clear();
                this.missingFields.setLength(0);
              }
            } else {
              action.getInvalidFields().put("list-project.locationsData",
                action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Locations"}));
              this.addMessage(action.getText("project.countries"));
            }
          }
          if (project.getLocationRegional() != null && project.getLocationRegional()) {
            if (project.getRegionFS() != null) {
              if (project.getRegionFS().stream().filter(c -> c.isSelected()).collect(Collectors.toList()).isEmpty()) {
                action.getInvalidFields().put("list-project.locationsData",
                  action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Locations"}));
                this.addMessage(action.getText("project.regions"));
              } else {
                this.validationMessage = new StringBuilder();
                action.getInvalidFields().clear();
                this.missingFields.setLength(0);
              }
            } else {
              action.getInvalidFields().put("list-project.locationsData",
                action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Locations"}));
              this.addMessage(action.getText("project.regions"));
            }
          }
        }
      }


    }


  }

}
