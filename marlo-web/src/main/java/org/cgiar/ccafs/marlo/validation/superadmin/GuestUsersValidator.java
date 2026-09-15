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

  /**
   * Validates the Guest User being created and <b>returns the directory's answer for their email</b>.
   * <p>
   * The lookup was always performed here -- it is what decides whether the names are asked for -- but the
   * {@link DirectoryPerson} was discarded, so {@code CrpUsersAction.save()} asked the directory a second
   * time for the same address. Two lookups mean two LDAP connections with no cache between them, and they
   * can disagree on one submission: the first answers, the names are hidden, the second times out, and the
   * account is created as non-CGIAR with blank names. Returning the answer lets the caller reuse it, so one
   * save asks once and every decision in it comes from the same reply.
   *
   * @param action the action being validated, where the invalid fields and messages are reported
   * @param user the user the administrator is creating
   * @param selectedGlobalUnitAcronym the Global Unit selected on the screen
   * @param isCGIARUser unused: reassigned below from the directory's answer before it is passed on
   * @param saving whether this runs as part of a save
   * @return the directory's answer for {@code user}'s email, never {@code null}
   */
  public DirectoryPerson validate(BaseAction action, User user, String selectedGlobalUnitAcronym,
    boolean isCGIARUser, boolean saving) {
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
    return person;
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
