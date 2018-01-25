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

package org.cgiar.ccafs.marlo.rest.errors;

public final class ErrorConstants {

  public static final String ERR_CONCURRENCY_FAILURE = "error.concurrencyFailure";
  public static final String ERR_ACCESS_DENIED = "error.accessDenied";
  public static final String ERR_VALIDATION = "error.validation";
  public static final String ERR_METHOD_NOT_SUPPORTED = "error.methodNotSupported";

  public static final String ERR_RESOURCE_ALREADY_EXISTS = "error.resourceAlreadyExists";
  public static final String ERR_INTERNAL_SERVER = "error.internalServerError";
  public static final String ERR_RESOURCE_NOT_FOUND = "error.resourceNotFound";

  private ErrorConstants() {
  }

}
