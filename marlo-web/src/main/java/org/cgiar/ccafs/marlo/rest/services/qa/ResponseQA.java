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

package org.cgiar.ccafs.marlo.rest.services.qa;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class ResponseQA<T> {

  private T data;
  private String message;


  public ResponseQA() {
  }

  public ResponseQA(T data, String message) {
    super();
    this.data = data;
    this.message = message;
  }

  public T getData() {
    return data;
  }

  public String getMessage() {
    return message;
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this, false);
  }

  public void setData(T data) {
    this.data = data;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(this.getClass().getSimpleName()).append('@').append(this.hashCode()).append('{').append("data=")
      .append(this.getData().toString()).append(", message=").append(this.getMessage()).append('}');
    return sb.toString();
  }
}
