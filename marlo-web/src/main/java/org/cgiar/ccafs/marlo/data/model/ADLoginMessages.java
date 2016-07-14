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

  LOGON_SUCCESS("Login successful"), ERROR_NO_SUCH_USER("CGIAR User not exist"),
  ERROR_LOGON_FAILURE("Invalid CGIAR username or password"),
  ERROR_INVALID_LOGON_HOURS("CGIAR User account with restricted access in specific hours"),
  ERROR_PASSWORD_EXPIRED("CGIAR User password expired"), ERROR_ACCOUNT_DISABLED("CGIAR User account disabled"),
  ERROR_ACCOUNT_EXPIRED("CGIAR User account expired"), ERROR_ACCOUNT_LOCKED_OUT("CGIAR User account blocked");

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
