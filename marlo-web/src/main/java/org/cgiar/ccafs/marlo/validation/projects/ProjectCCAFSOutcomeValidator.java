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
import org.cgiar.ccafs.marlo.data.model.IpProjectIndicator;
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
public class ProjectCCAFSOutcomeValidator extends BaseValidator {


  private final CrpManager crpManager;

  @Inject
  public ProjectCCAFSOutcomeValidator(CrpManager crpManager) {
    this.crpManager = crpManager;
  }

  private Path getAutoSaveFilePath(Project project, long crpID) {
    Crp crp = crpManager.getCrpById(crpID);
    String composedClassName = project.getClass().getSimpleName();
    String actionFile = ProjectSectionStatusEnum.CCAFSOUTCOMES.getStatus().replace("/", "_");
    String autoSaveFile =
      project.getId() + "_" + composedClassName + "_" + crp.getAcronym() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
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

      if (project.getProjectIndicators() != null) {
        int i = 0;
        for (IpProjectIndicator ipProjectIndicator : project.getProjectIndicators()) {
          if (ipProjectIndicator != null) {
            if (ipProjectIndicator.getYear() == action.getCurrentCycleYear()) {

              if (ipProjectIndicator.getArchived() == null || ipProjectIndicator.getArchived().doubleValue() < 0) {
                this.addMessage(" CCAFS Outcome #" + ipProjectIndicator.getId() + ": Target achieved");
                action.getInvalidFields().put("input-project.projectIndicators[" + i + "].archived",
                  InvalidFieldsMessages.EMPTYFIELD);
              }

              if (!(this.isValidString(ipProjectIndicator.getNarrativeTargets())
                && this.wordCount(ipProjectIndicator.getNarrativeTargets()) <= 100)) {
                this.addMessage("CCAFS Outcome ##" + ipProjectIndicator.getId() + ": Narrative Target");
                action.getInvalidFields().put("input-project.projectIndicators[" + i + "].narrativeTargets",
                  InvalidFieldsMessages.EMPTYFIELD);
              }

              if (!(this.isValidString(ipProjectIndicator.getNarrativeGender())
                && this.wordCount(ipProjectIndicator.getNarrativeGender()) <= 100)) {
                this.addMessage("CCAFS Outcome ##" + ipProjectIndicator.getId() + ": Narrative Gender");
                action.getInvalidFields().put("input-project.projectIndicators[" + i + "].narrativeGender",
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
        ProjectSectionStatusEnum.CCAFSOUTCOMES.getStatus());

    }
  }
}
