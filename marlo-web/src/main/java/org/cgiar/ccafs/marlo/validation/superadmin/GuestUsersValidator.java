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
import org.cgiar.ccafs.marlo.security.directory.DirectoryPerson;
import org.cgiar.ccafs.marlo.security.directory.DirectoryService;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class GuestUsersValidator extends BaseValidator {

  private final DirectoryService directoryService;

  @Inject
  public GuestUsersValidator(DirectoryService directoryService) {
    super();
    this.directoryService = directoryService;
  }

  public void validate(BaseAction action, User user, String selectedGlobalUnitAcronym, boolean isCGIARUser,
    boolean saving) {
    action.setInvalidFields(new HashMap<>());
    DirectoryPerson person = this.directoryService.findByEmail(user.getEmail());
    if (person.isFound()) {
      isCGIARUser = true;
    } else {
      isCGIARUser = false;
    }
    this.validateGuestUsers(action, user, selectedGlobalUnitAcronym, isCGIARUser);
    if (!action.getFieldErrors().isEmpty()) {
      action.addActionError(action.getText("saving.fields.required"));
    } else if (action.getValidationMessage().length() > 0) {
      action.addActionMessage(
        " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));
    }
  }

  public void validateGuestUsers(BaseAction action, User user, String selectedGlobalUnitAcronym, boolean isCGIARUser) {
    if ((user.getFirstName() == null || user.getFirstName().isEmpty()) && isCGIARUser == false) {
      action.addMessage(action.getText("guestUsers.firstName"));
      action.getInvalidFields().put("input-user.firstName", InvalidFieldsMessages.EMPTYFIELD);
    }

    if ((user.getLastName() == null || user.getLastName().isEmpty()) && isCGIARUser == false) {
      action.addMessage(action.getText("guestUsers.lastName"));
      action.getInvalidFields().put("input-user.lastName", InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate email
    if (user.getEmail() == null) {
      action.addMessage(action.getText("guestUsers.email"));
      action.getInvalidFields().put("input-user.email", InvalidFieldsMessages.EMPTYFIELD);
    }
  }
}
