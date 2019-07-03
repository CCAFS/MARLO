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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for transfering error message with a list of field errors.
 */
public class ErrorDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private final String message;
  private final String description;
  private final String severity;

  private List<FieldErrorDTO> fieldErrors;

  public ErrorDTO(String message) {
    this(message, null, null);
  }

  public ErrorDTO(String message, String description, String severity) {
    this.message = message;
    this.description = description;
    this.severity = severity;
  }

  public ErrorDTO(String message, String description, String severity, List<FieldErrorDTO> fieldErrors) {
    this.message = message;
    this.description = description;
    this.fieldErrors = fieldErrors;
    this.severity = severity;
  }

  public void add(String objectName, String field, String message) {
    if (this.fieldErrors == null) {
      this.fieldErrors = new ArrayList<>();
    }
    this.fieldErrors.add(new FieldErrorDTO(objectName, field, message));
  }

  public String getDescription() {
    return this.description;
  }

  public List<FieldErrorDTO> getFieldErrors() {
    return this.fieldErrors;
  }

  public String getMessage() {
    return this.message;
  }

  public String getSeverity() {
    return this.severity;
  }
}
