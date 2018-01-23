/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
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
 * @author Christian David Garcia Oviedo. - CIAT/CCAFS
 */
@Named
public class ProjectLeverageValidator extends BaseValidator {

  private final GlobalUnitManager crpManager;

  @Inject
  public ProjectLeverageValidator(GlobalUnitManager crpManager) {
    super();
    this.crpManager = crpManager;
  }

  private Path getAutoSaveFilePath(Project project, long crpID) {
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
    String composedClassName = project.getClass().getSimpleName();
    String actionFile = ProjectSectionStatusEnum.LEVERAGES.getStatus().replace("/", "_");
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
    if (project != null) {
      // Does the project have any nextUser?
      if (project.getLeverages() != null && !project.getLeverages().isEmpty()) {


        for (int c = 0; c < project.getLeverages().size(); c++) {

          this.validateTitleLeverage(action, project.getLeverages().get(c).getTitle(), c);

          if (project.getLeverages().get(c).getInstitution() != null) {
            this.validatePartner(action, project.getLeverages().get(c).getInstitution().getId(), c);
          } else {
            action.addMessage("Leverage #" + (c + 1) + ": Partner");
            action.addMissingField("project.leverages[" + c + "].Partner");
            action.getInvalidFields().put("input-project.leverages[" + c + "].institution.id",
              InvalidFieldsMessages.EMPTYFIELD);
          }

          if (project.getLeverages().get(c).getCrpProgram() != null) {
            this.validateFlagship(action, project.getLeverages().get(c).getCrpProgram().getId(), c);
          } else {
            action.addMessage("Leverage #" + (c + 1) + ": FlagShip");
            action.addMissingField("project.leverages[" + c + ".flagship");
            action.getInvalidFields().put("input-project.leverages[" + c + "].crpProgram.id",
              InvalidFieldsMessages.EMPTYFIELD);
          }

          this.validateBudget(action, project.getLeverages().get(c).getBudget(), c);


        }
      }

    }
    if (!action.getFieldErrors().isEmpty()) {
      action.addActionError(action.getText("saving.fields.required"));
    } else if (action.getValidationMessage().length() > 0) {
      action.addActionMessage(
        " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));
    }

    this.saveMissingFields(project, action.getActualPhase().getDescription(), action.getActualPhase().getYear(),
      ProjectSectionStatusEnum.LEVERAGES.getStatus(), action);
  }


  public void validateBudget(BaseAction action, Double budget, int c) {
    if (budget == null || budget < 0) {
      action.addMessage("Leverage #" + (c + 1) + ": Budget");
      action.addMissingField("project.leverages[" + c + ".budget");
      action.getInvalidFields().put("input-project.leverages[" + c + "].budget", InvalidFieldsMessages.EMPTYFIELD);
    }
  }


  public void validateFlagship(BaseAction action, Long flagship, int c) {
    if (flagship.longValue() == -1 || flagship == null) {
      action.addMessage("Leverage #" + (c + 1) + ": FlagShip");
      action.addMissingField("project.leverages[" + c + ".flagship");
      action.getInvalidFields().put("input-project.leverages[" + c + "].crpProgram.id",
        InvalidFieldsMessages.EMPTYFIELD);
    }
  }


  public void validatePartner(BaseAction action, Long partner, int c) {
    if (partner.intValue() == -1 || partner == null) {
      action.addMessage("Leverage #" + (c + 1) + ": Partner");
      action.addMissingField("project.leverages[" + c + "].Partner");
      action.getInvalidFields().put("input-project.leverages[" + c + "].institution.id",
        InvalidFieldsMessages.EMPTYFIELD);
    }
  }


  public void validateTitleLeverage(BaseAction action, String title, int c) {
    if (!(this.isValidString(title) && this.wordCount(title) <= 50)) {
      action.addMessage("Leverage #" + (c + 1) + ": Title");
      action.addMissingField("project.leverages[" + c + "].Title");
      action.getInvalidFields().put("input-project.leverages[" + c + "].title", InvalidFieldsMessages.EMPTYFIELD);
    }
  }


}
