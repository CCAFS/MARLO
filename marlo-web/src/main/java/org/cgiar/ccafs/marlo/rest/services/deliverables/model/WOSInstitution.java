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

package org.cgiar.ccafs.marlo.rest.services.deliverables.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class WOSInstitution implements Serializable {

  @SerializedName("clarisa_id")
  private Long clarisaId;
  private String fullName;


  public WOSInstitution() {
  }

  public Long getClarisaId() {
    return clarisaId;
  }

  public String getFullName() {
    return fullName;
  }

  public void setClarisaId(Long clarisaId) {
    this.clarisaId = clarisaId;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
