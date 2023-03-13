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

package org.cgiar.ccafs.marlo.validation.superadmin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.model.Timeline;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.util.HashMap;
import java.util.List;

import javax.inject.Named;

@Named
public class TimelineManagementValidator extends BaseValidator {

  public TimelineManagementValidator() {
  }


  public void validate(BaseAction action, List<Timeline> timelineList) {
    action.setInvalidFields(new HashMap<>());

    this.validateTimelineManagement(action, timelineList);

    if (!action.getFieldErrors().isEmpty()) {
      action.addActionError(action.getText("saving.fields.required"));
    } else if (action.getValidationMessage().length() > 0) {
      action.addActionMessage(
        " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));
    }
  }

  public void validateTimelineManagement(BaseAction action, List<Timeline> timelineList) {

    if (timelineList != null && !timelineList.isEmpty()) {
      int index = 0;
      for (Timeline activity : timelineList) {
        if (!this.isValidString(activity.getDescription())) {
          action.addMessage(action.getText("timelineActivities[" + index + "].description"));
          action.getInvalidFields().put("input-timelineActivities[" + index + "].description",
            InvalidFieldsMessages.EMPTYFIELD);
        }
        index++;
      }
    }
  }


}
