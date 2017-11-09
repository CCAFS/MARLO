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
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.OtherContribution;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectCrpContribution;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import com.google.inject.Inject;

public class ProjectOtherContributionsValidator extends BaseValidator {


  // GlobalUnit Manager
  private GlobalUnitManager crpManager;

  @Inject
  public ProjectOtherContributionsValidator(GlobalUnitManager crpManager) {
    this.crpManager = crpManager;
  }

  private Path getAutoSaveFilePath(Project project, long crpID) {
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
    String composedClassName = project.getClass().getSimpleName();
    String actionFile = ProjectSectionStatusEnum.OTHERCONTRIBUTIONS.getStatus().replace("/", "_");
    String autoSaveFile =
      project.getId() + "_" + composedClassName + "_" + crp.getAcronym() + "_" + actionFile + ".json";

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

  public void validate(BaseAction action, Project project, boolean saving) {

    action.setInvalidFields(new HashMap<>());
    if (!saving) {
      Path path = this.getAutoSaveFilePath(project, action.getCrpID());

      if (path.toFile().exists()) {
        this.addMissingField("draft");
      }
    }
    if (project != null) {

      if (project.getOtherContributionsList() != null) {
        int i = 0;
        for (OtherContribution otherContribution : project.getOtherContributionsList()) {
          if (otherContribution != null) {


            if (otherContribution.getIpProgram() == null || otherContribution.getIpProgram().getId() == null
              || otherContribution.getIpProgram().getId().intValue() == -1) {
              otherContribution.setIpProgram(null);
              this.addMessage("Other contriburion ##" + otherContribution.getId() + ": Brief");
              action.getInvalidFields().put("input-project.otherContributionsList[" + i + "].ipProgram.id",
                InvalidFieldsMessages.EMPTYFIELD);
            }


            if (otherContribution.getIpIndicator() == null || otherContribution.getIpIndicator().getId() == null
              || otherContribution.getIpIndicator().getId().intValue() == -1) {
              otherContribution.setIpIndicator(null);
              this.addMessage("Other contriburion ##" + otherContribution.getId() + ": Brief");
              action.getInvalidFields().put("input-project.otherContributionsList[" + i + "].ipIndicator.id",
                InvalidFieldsMessages.EMPTYFIELD);
            }

            if (!(this.isValidString(otherContribution.getDescription())
              && this.wordCount(otherContribution.getDescription()) <= 100)) {
              this.addMessage("otherContribution ##" + otherContribution.getId() + ": description");
              action.getInvalidFields().put("input-project.otherContributionsList[" + i + "].description",
                InvalidFieldsMessages.EMPTYFIELD);
            }
          }
          i++;
        }
      }


      if (project.getCrpContributions() != null) {
        int i = 0;
        for (ProjectCrpContribution projectCrpContribution : project.getCrpContributions()) {
          if (projectCrpContribution != null) {


            if (!(this.isValidString(projectCrpContribution.getCollaborationNature())
              && this.wordCount(projectCrpContribution.getCollaborationNature()) <= 50)) {
              this.addMessage("project crp contribution ##" + projectCrpContribution.getId() + ": description");
              action.getInvalidFields().put("input-project.crpContributions[" + i + "].collaborationNature",
                InvalidFieldsMessages.EMPTYFIELD);
            }
          }
          i++;
        }

      }
      if (!action.getFieldErrors().isEmpty()) {
        action.addActionError(action.getText("saving.fields.required"));
      } else if (validationMessage.length() > 0) {
        action
          .addActionMessage(" " + action.getText("saving.missingFields", new String[] {validationMessage.toString()}));
      }

      if (action.isReportingActive()) {
        this.saveMissingFields(project, APConstants.REPORTING, action.getReportingYear(),
          ProjectSectionStatusEnum.OTHERCONTRIBUTIONS.getStatus());
      } else {
        this.saveMissingFields(project, APConstants.PLANNING, action.getPlanningYear(),
          ProjectSectionStatusEnum.OTHERCONTRIBUTIONS.getStatus());
      }
    }
  }
}
