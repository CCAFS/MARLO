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

package org.cgiar.ccafs.marlo.utils.handle;


/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public enum HandleHttpResponseCode {

  /**
   * Success. (HTTP 200 OK)
   */
  SUCCESS(1),

  /**
   * Error. Something unexpected went wrong during handle resolution. (HTTP 500 Internal Server Error)
   */
  ERROR(2),

  /**
   * Handle Not Found. (HTTP 404 Not Found)
   */
  HANDLE_NOT_FOUND(100),

  /**
   * The handle exists but has no values (or no values according to the types and indices specified). (HTTP 200 OK)
   */
  VALUES_NOT_FOUND(200);

  public static HandleHttpResponseCode getByErrorCode(int errorCode) {
    for (HandleHttpResponseCode responseCode : HandleHttpResponseCode.values()) {
      if (responseCode.getErrorCode() == errorCode) {
        return responseCode;
      }
    }

    return null;
  }

  private int errorCode;

  private HandleHttpResponseCode(int errorCode) {
    this.errorCode = errorCode;
  }

  public int getErrorCode() {
    return this.errorCode;
  }
}