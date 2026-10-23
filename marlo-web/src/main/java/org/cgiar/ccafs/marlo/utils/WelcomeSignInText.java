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

package org.cgiar.ccafs.marlo.utils;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.CustomParameterManager;
import org.cgiar.ccafs.marlo.data.manager.ParameterManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.CognitoAuthSpecificity;
import org.cgiar.ccafs.marlo.security.otp.OtpEligibility;
import org.cgiar.ccafs.marlo.security.otp.SignInMethodRule.Methods;

import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The text of the welcome email that the seven account-creating screens send (CHG-PWD-SWITCH-001-T07, FN-008, FN-009,
 * UI-003, DD-7).
 * <p>
 * It returns today's {@code email.newUser.part1}, with the password as {@code {3}}, when the password is among the
 * methods of the new account in the Global Unit, and a Cognito account keeps it too ("Your Outlook Password"). When the
 * password is not available it returns {@code email.newUser.part1.codeSignIn}, which has no password and names the
 * unit as {@code {3}}. "Available" is not decided here: it is the answer of {@link OtpEligibility#methodsFor}, the one
 * rule (NF-001), asked for the unit the sender passes. This class only supplies the Cognito flag of the account in
 * that unit, as {@code OtpEligibility} does for its own callers.
 * <p>
 * Password generation and storage stay with the senders (FN-009): this class never sees how the password came to be,
 * and it never stores or logs it. A failing read keeps today's text and writes an ERROR line, because an email that
 * cannot say how to sign in is worse than one that carries a password the unit may not accept.
 * <p>
 * Call it from the action method that sends the email, never from {@code validate()}: the reads go through
 * transactional managers. It is a container singleton; inject it.
 */
@Named
public class WelcomeSignInText {

  private static final Logger LOG = LoggerFactory.getLogger(WelcomeSignInText.class);

  private static final String KEY_TODAY = "email.newUser.part1";
  private static final String KEY_CODE_SIGN_IN = "email.newUser.part1.codeSignIn";

  private final OtpEligibility otpEligibility;
  private final CustomParameterManager customParameterManager;
  private final ParameterManager parameterManager;

  @Inject
  public WelcomeSignInText(OtpEligibility otpEligibility, CustomParameterManager customParameterManager,
    ParameterManager parameterManager) {
    this.otpEligibility = otpEligibility;
    this.customParameterManager = customParameterManager;
    this.parameterManager = parameterManager;
  }

  /**
   * @param action the action that sends the email; it resolves the text and the arguments
   * @param user the new account
   * @param unit the Global Unit the account is created for: {@code globalUnit} in {@code CrpUsersAction}, the
   *        {@code loggedCrp} of every other sender
   * @param password the password the sender generated and stored; carried only when the password is available
   * @param supportText the support sentence, as the sender builds it
   * @return the text of the email. Arguments: {@code {0}} the roles, {@code {1}} the MARLO URL, {@code {2}} the email,
   *         {@code {3}} the password, or the acronym of the unit in the code text, and {@code {4}} the support text
   */
  public String part1(BaseAction action, User user, GlobalUnit unit, String password, String supportText) {
    boolean passwordAvailable = this.isPasswordAvailable(user, unit);
    String email = user == null ? null : user.getEmail();
    String fourth = passwordAvailable ? password : this.unitLabel(unit);
    return action.getText(passwordAvailable ? KEY_TODAY : KEY_CODE_SIGN_IN, new String[] {
      action.getText("email.newUser.listRoles"), action.getBaseUrl(), email, fourth, supportText});
  }

  /** The acronym of the unit, else its name, else the empty string: the text never prints {@code null}. */
  private String unitLabel(GlobalUnit unit) {
    if (unit.getAcronym() != null && !unit.getAcronym().isEmpty()) {
      return unit.getAcronym();
    }
    return unit.getName() == null ? "" : unit.getName();
  }

  /**
   * Today's text unless the rule says the password is not among the methods of an account without Cognito in the unit.
   */
  private boolean isPasswordAvailable(User user, GlobalUnit unit) {
    if (user == null || unit == null || unit.getId() == null) {
      return true;
    }
    try {
      boolean cognitoAccount = user.isCgiarUser()
        && CognitoAuthSpecificity.isActiveFor(unit, this.customParameterManager, this.parameterManager);
      Methods methods = this.otpEligibility
        .methodsFor(user.getEmail(), Collections.singletonList(unit),
          Collections.singletonMap(unit.getId(), Boolean.valueOf(cognitoAccount)))
        .get(unit.getId());
      return methods == null || methods.isPassword() || methods.isCognito();
    } catch (RuntimeException e) {
      LOG.error("welcome.email outcome=settings_unavailable fallback=password_text gu={}",
        LogSanitizer.sanitizeForLog(unit.getAcronym()), e);
      return true;
    }
  }
}
