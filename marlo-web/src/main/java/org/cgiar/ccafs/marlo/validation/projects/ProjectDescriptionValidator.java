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
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import com.google.inject.Inject;

/**
 * @author Christian David Garcia -CIAT/CCAFS
 */
public class ProjectDescriptionValidator extends BaseValidator

{

  @Inject
  public ProjectDescriptionValidator() {

  }

  public void validate(BaseAction action, Project project) {

    this.validateDescription(action, project);
    if (!action.getFieldErrors().isEmpty()) {
      action.addActionError(action.getText("saving.fields.required"));
    } else if (validationMessage.length() > 0) {
      action
        .addActionMessage(" " + action.getText("saving.missingFields", new String[] {validationMessage.toString()}));
    }
    this.saveMissingFieldsProject(project, "description");
  }

  public void validateDescription(BaseAction action, Project project) {
    if (!(this.isValidString(project.getTitle()) && this.wordCount(project.getTitle()) <= 20)) {
      this.addMessage(action.getText("project.title"));
    }

    if (!(this.isValidString(project.getSummary()) && this.wordCount(project.getSummary()) <= 20)) {
      this.addMessage(action.getText("project.summary"));
    }

    if (project.getLiaisonUser() == null) {
      this.addMessage(action.getText("project.liaisonUser"));
    }

    if (project.getLiaisonInstitution() == null) {
      this.addMessage(action.getText("project.liaisonInstitution"));
    }
    if (project.getStartDate() == null) {
      this.addMessage(action.getText("project.startDate"));
    }
    if (project.getEndDate() == null) {
      this.addMessage(action.getText("project.endDate"));
    }

    if (project.getEndDate() == null) {
      this.addMessage(action.getText("project.endDate"));
    }


    if (project.getFlagships().size() == 0) {
      this.addMessage(action.getText("projectDescription.flagships"));
    }
  }

}
