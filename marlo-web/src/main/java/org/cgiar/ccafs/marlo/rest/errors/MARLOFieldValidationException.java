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

import java.util.List;

/**
 * A custom exception for when trying to update resources that don't exist.
 * 
 * @author GrantL
 */
public class MARLOFieldValidationException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private final ErrorDTO errorDTO;

  public MARLOFieldValidationException(ErrorDTO errorDTO) {
    this.errorDTO = errorDTO;
  }

  public MARLOFieldValidationException(String message, String description, List<FieldErrorDTO> fieldErrors) {
    this.errorDTO = new ErrorDTO(message, description, ErrorConstants.SEVERITY_ERROR, fieldErrors);
  }

  public ErrorDTO getErrorDTO() {
    return this.errorDTO;
  }


}