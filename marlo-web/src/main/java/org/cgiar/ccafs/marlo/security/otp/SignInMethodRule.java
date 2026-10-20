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

package org.cgiar.ccafs.marlo.security.otp;

/**
 * The one rule that says which sign-in methods an account in a Global Unit may use: Cognito, the emailed code or the
 * password (CHG-PWD-SWITCH-001-T02, design 1, DD-1, DD-2; FN-003 to FN-007, FN-010, SEC-003, NF-001).
 * <p>
 * Every consumer reads this rule, so that the pre-auth lookup, the two password endpoints, the two code endpoints
 * and the welcome email cannot drift apart. For an account in Global Unit G, with every setting read for G:
 * <table summary="the rule">
 * <tr>
 * <th>Method</th>
 * <th>Available when</th>
 * </tr>
 * <tr>
 * <td>Cognito</td>
 * <td>the account is a Cognito account in G</td>
 * </tr>
 * <tr>
 * <td>Code</td>
 * <td>the identifier is an email <b>and</b> the code key is configured <b>and</b> either the code switch is on and
 * the domain is not excluded, or the password switch is off and the account is not a Cognito account in G (the
 * default method of rule 3d)</td>
 * </tr>
 * <tr>
 * <td>Password</td>
 * <td>the account is not a Cognito account in G <b>and</b> either the password switch is on or the code key is not
 * configured (the last resort)</td>
 * </tr>
 * </table>
 * Consequences: a Cognito account always has Cognito and never the password (SEC-003); an account without Cognito
 * has the code or the password, or, when only the code is left and the identifier is a username, {@code needsEmail}
 * (FN-010); no account has more than two methods.
 * <p>
 * The class is pure and stateless. The exclusion match is the shared exact-domain rule of {@link OtpDomainPolicy}.
 * The password switch is <b>not</b> parsed with {@link OtpDomainPolicy#isSwitchOn(String)}: see
 * {@link #isPasswordSwitchOn(String)}.
 */
public final class SignInMethodRule {

  /**
   * The methods the rule grants one account in one Global Unit. Immutable.
   */
  public static final class Methods {

    private final boolean cognito;
    private final boolean code;
    private final boolean password;
    private final boolean needsEmail;

    private Methods(boolean cognito, boolean code, boolean password, boolean needsEmail) {
      this.cognito = cognito;
      this.code = code;
      this.password = password;
      this.needsEmail = needsEmail;
    }

    /**
     * @return {@code true} when the emailed code is available
     */
    public boolean isCode() {
      return this.code;
    }

    /**
     * @return {@code true} when "Sign in with CGIAR" is available
     */
    public boolean isCognito() {
      return this.cognito;
    }

    /**
     * @return {@code true} when the code is the only way in for this account but the identifier typed in step 1 is
     *         not an email, so the user must restart with the email address (FN-010). Then no method is available
     */
    public boolean isNeedsEmail() {
      return this.needsEmail;
    }

    /**
     * @return {@code true} when the password is available
     */
    public boolean isPassword() {
      return this.password;
    }
  }

  private SignInMethodRule() {
    throw new AssertionError("utility class, never instantiated");
  }

  /**
   * Reads the raw value of {@code system_password_login_active} (FN-001, DD-2). A missing value is on.
   * <p>
   * {@link OtpDomainPolicy#isSwitchOn(String)} must not be used here: it reads the empty value as off, which is safe
   * for the code switch and a lock-out for this one, and the shared specificity reader returns the empty string for
   * a missing unit, type row or value.
   *
   * @param value the raw effective value of {@code system_password_login_active}, possibly {@code null}
   * @return {@code false} only when the trimmed value equals {@code false}, in any case. Anything else, the empty
   *         value and {@code null} included, is on
   */
  public static boolean isPasswordSwitchOn(String value) {
    return value == null || !"false".equalsIgnoreCase(value.trim());
  }

  /**
   * Applies the rule of the class description to one account in one Global Unit.
   *
   * @param keyConfigured whether the code key is valid on this server ({@link OtpKeys#isConfigured()})
   * @param cognitoAccountInG whether the account is a Cognito account in G ({@code is_cgiar_user = 1} and
   *        {@code cognito_auth_active} on for G)
   * @param passwordSwitchValue the raw effective value of {@code system_password_login_active} for G, possibly
   *        {@code null}
   * @param codeSwitchValue the raw effective value of {@code system_otp_login_active} for G, possibly {@code null}
   * @param exclusionValue the raw effective value of {@code system_otp_excluded_email_domains} for G, possibly
   *        {@code null}
   * @param identifier the identifier typed in step 1, raw, possibly {@code null}. It is an email when
   *        {@link OtpDomainPolicy#domainOf(String)} finds a domain in it, otherwise a username
   * @return the methods granted. Never {@code null}
   */
  public static Methods methodsFor(boolean keyConfigured, boolean cognitoAccountInG, String passwordSwitchValue,
    String codeSwitchValue, String exclusionValue, String identifier) {
    boolean passwordOn = isPasswordSwitchOn(passwordSwitchValue);
    boolean isEmail = OtpDomainPolicy.domainOf(identifier) != null;

    // Rule 3d: the password is off, the code key is valid and the account has no Cognito, so the code is its way in.
    boolean codeIsTheDefault = keyConfigured && !passwordOn && !cognitoAccountInG;

    boolean cognito = cognitoAccountInG;
    boolean password = !cognitoAccountInG && (passwordOn || !keyConfigured);
    boolean offeredByTheUnit = keyConfigured && OtpDomainPolicy.isOffered(codeSwitchValue, exclusionValue, identifier);
    boolean code = offeredByTheUnit || (codeIsTheDefault && isEmail);
    boolean needsEmail = codeIsTheDefault && !isEmail;
    return new Methods(cognito, code, password, needsEmail);
  }
}
