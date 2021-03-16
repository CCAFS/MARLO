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

  /**
   * 
   */
  private static final long serialVersionUID = 2633617960096822831L;

  @SerializedName("clarisa_id")
  private Long clarisaId;

  private String fullName;

  @SerializedName("full_address")
  private String fullAddress;

  private String country;

  @SerializedName("confidant")
  private Integer clarisaMatchConfidence;

  public WOSInstitution() {
  }

  public Long getClarisaId() {
    return clarisaId;
  }

  public Integer getClarisaMatchConfidence() {
    return clarisaMatchConfidence;
  }

  public String getCountry() {
    return country;
  }

  public String getFullAddress() {
    return fullAddress;
  }

  public String getFullName() {
    return fullName;
  }

  public void setClarisaId(Long clarisaId) {
    this.clarisaId = clarisaId;
  }

  public void setClarisaMatchConfidence(Integer clarisaMatchConfidence) {
    this.clarisaMatchConfidence = clarisaMatchConfidence;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public void setFullAddress(String fullAddress) {
    this.fullAddress = fullAddress;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
