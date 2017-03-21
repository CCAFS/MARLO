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

package org.cgiar.ccafs.marlo.data.model;


/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public enum ADLoginMessages {

  LOGON_SUCCESS("You have successfully logged in"),
  ERROR_NO_SUCH_USER("The CGIAR email provided does not exist, please try again"),
  ERROR_LOGON_FAILURE("Invalid CGIAR email or password, please try again"),
  ERROR_INVALID_LOGON_HOURS(
    "Your CGIAR account has a restricted access at this time, please contact the IT staff from your Center"),
  ERROR_PASSWORD_EXPIRED("Your CGIAR account password has expired, please contact the IT staff from your Center"),
  ERROR_ACCOUNT_DISABLED("Your CGIAR account is disabled, please contact the IT staff from your Center"),
  ERROR_ACCOUNT_EXPIRED("Your CGIAR User account has expired, please contact the IT staff from your Center"),
  ERROR_ACCOUNT_LOCKED_OUT("Your CGIAR account is currently blocked, please contact the IT staff from your Center"),
  ERROR_LDAP_CONNECTION("The Platform can not connect to CGIAR authentication service"),
  USER_DISABLED("Your account is disabled, please contact your  and/or the MARLO staff");

  private String value;

  private ADLoginMessages(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

}
