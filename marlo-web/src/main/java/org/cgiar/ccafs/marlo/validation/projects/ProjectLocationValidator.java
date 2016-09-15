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
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import com.google.inject.Inject;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ProjectLocationValidator extends BaseValidator {

  @Inject
  public ProjectLocationValidator() {
  }

  public void validate(BaseAction action, Project project) {

    this.validateLocation(action, project);
    if (!action.getFieldErrors().isEmpty()) {
      action.addActionError(action.getText("saving.fields.required"));
    } else if (validationMessage.length() > 0) {
      action
        .addActionMessage(" " + action.getText("saving.missingFields", new String[] {validationMessage.toString()}));
    }

    if (action.isReportingActive()) {
      this.saveMissingFields(project, APConstants.REPORTING, action.getPlanningYear(),
        ProjectSectionStatusEnum.LOCATIONS.getStatus());
    } else {
      this.saveMissingFields(project, APConstants.PLANNING, action.getPlanningYear(),
        ProjectSectionStatusEnum.LOCATIONS.getStatus());
    }
  }

  public void validateLocation(BaseAction action, Project project) {

    if (project.getLocations() == null) {
      this.addMessage(action.getText("project.locations"));
    }

  }

}
