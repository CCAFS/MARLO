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

public class InvalidHandleException extends IllegalArgumentException {

  /**
   * Constructs an <code>InvalidHandleException</code> with no detail message.
   */
  public InvalidHandleException() {
    super();
  }

  /**
   * Constructs an <code>InvalidHandleException</code> with the specified detail message (DOI String).
   *
   * @param s the detail message.
   */
  public InvalidHandleException(String s) {
    super(String.format("From input string: %s", s));
  }
}