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
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.IpProjectContributionOverview;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class ProjectOutputsValidator extends BaseValidator {

  private final CrpManager crpManager;

  @Inject
  public ProjectOutputsValidator(CrpManager crpManager) {
    this.crpManager = crpManager;
  }

  private Path getAutoSaveFilePath(Project project, long crpID) {
    Crp crp = crpManager.getCrpById(crpID);
    String composedClassName = project.getClass().getSimpleName();
    String actionFile = ProjectSectionStatusEnum.OUTPUTS.getStatus().replace("/", "_");
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

      if (project.getOverviews() != null) {
        int i = 0;
        for (IpProjectContributionOverview ipProjectContributionOverview : project.getOverviews()) {
          if (ipProjectContributionOverview != null) {
            if (ipProjectContributionOverview.getYear() == action.getCurrentCycleYear()) {


              if (!(this.isValidString(ipProjectContributionOverview.getBriefSummary())
                && this.wordCount(ipProjectContributionOverview.getBriefSummary()) <= 50)) {
                this.addMessage("Project  Output ##" + ipProjectContributionOverview.getId() + ": Brief");
                action.getInvalidFields().put("input-project.overviews[" + i + "].briefSummary",
                  InvalidFieldsMessages.EMPTYFIELD);
              }
              if (!(this.isValidString(ipProjectContributionOverview.getSummaryGender())
                && this.wordCount(ipProjectContributionOverview.getSummaryGender()) <= 50)) {
                this.addMessage("Project  Output ##" + ipProjectContributionOverview.getId() + ": SummaryGender");
                action.getInvalidFields().put("input-project.overviews[" + i + "].summaryGender",
                  InvalidFieldsMessages.EMPTYFIELD);
              }


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
      this.saveMissingFields(project, action.getActualPhase().getDescription(), action.getActualPhase().getYear(),
        ProjectSectionStatusEnum.OUTPUTS.getStatus());

    }
  }
}
