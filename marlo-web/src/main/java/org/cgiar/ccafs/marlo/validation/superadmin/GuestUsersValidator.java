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

import org.cgiar.ciat.auth.LDAPService;
import org.cgiar.ciat.auth.LDAPUser;

import java.util.HashMap;

import javax.inject.Named;

@Named
public class GuestUsersValidator extends BaseValidator {

  public GuestUsersValidator() {
  }

  public LDAPUser getOutlookUser(String email) {
    LDAPService service = new LDAPService();
    if (config.isProduction()) {
      service.setInternalConnection(false);
    } else {
      service.setInternalConnection(true);
    }
    LDAPUser user = null;
    try {
      user = service.searchUserByEmail(email);
    } catch (Exception e) {
      user = null;
    }
    return user;
  }

  public void validate(BaseAction action, User user, String selectedGlobalUnitAcronym, boolean isCGIARUser,
    boolean saving) {
    action.setInvalidFields(new HashMap<>());
    LDAPUser LDAPUser = this.getOutlookUser(user.getEmail());
    if (LDAPUser != null) {
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

    if (selectedGlobalUnitAcronym == null) {
      action.addMessage(action.getText("login.error.selectCrp"));
      action.getInvalidFields().put("input-selectedGlobalUnitID", InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate email
    if (user.getEmail() == null) {
      action.addMessage(action.getText("guestUsers.email"));
      action.getInvalidFields().put("input-user.email", InvalidFieldsMessages.EMPTYFIELD);
    }

  }


}
