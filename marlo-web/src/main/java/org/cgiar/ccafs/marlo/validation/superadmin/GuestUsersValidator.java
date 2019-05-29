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
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Named;

/**
 * @author Andrés Valencia - CIAT/CCAFS
 */
@Named
public class GuestUsersValidator extends BaseValidator {


  public GuestUsersValidator() {
  }


  public void validate(BaseAction action, User user, long selectedGlobalUnitID, boolean isCGIARUser, boolean saving) {
    action.setInvalidFields(new HashMap<>());

    if (!isCGIARUser) {
      if (user.getFirstName() != null || !user.getFirstName().isEmpty()) {
        action.addMessage(action.getText("guestUsers.firstName"));
        action.getInvalidFields().put("input-user.firstName", InvalidFieldsMessages.EMPTYFIELD);
      }

      if (user.getLastName() != null || !user.getLastName().isEmpty()) {
        action.addMessage(action.getText("guestUsers.lastName"));
        action.getInvalidFields().put("input-user.lastName", InvalidFieldsMessages.EMPTYFIELD);
      }
    }

    if (selectedGlobalUnitID == -1) {
      action.addMessage(action.getText("login.error.selectCrp"));
      action.getInvalidFields().put("input-selectedGlobalUnitID", InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate email
    if (!(this.isValidEmail(user.getEmail()) && this.wordCount(user.getEmail()) <= 5)) {
      action.addMessage(action.getText("guestUsers.email"));
      action.getInvalidFields().put("input-user.email", InvalidFieldsMessages.EMPTYFIELD);
    }

    if (!action.getFieldErrors().isEmpty()) {
      action.addActionError(action.getText("saving.fields.required"));
      action.addActionMessage(
        " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));

      if (!action.getInvalidFields().isEmpty()) {
        List<String> keys = new ArrayList<String>(action.getInvalidFields().keySet());
        for (String key : keys) {
          action.addActionError(key + ": " + action.getInvalidFields().get(key));
        }
      }
    }

    if (!action.getFieldErrors().isEmpty()) {
      action.addActionError(action.getText("saving.fields.required"));
    } else if (action.getValidationMessage().length() > 0) {
      action.addActionMessage(
        " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));
    }
  }
}
