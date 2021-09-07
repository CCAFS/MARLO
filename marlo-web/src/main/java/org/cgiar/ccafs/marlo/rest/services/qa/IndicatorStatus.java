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

import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class IndicatorStatus {

  @SerializedName(value = "indicator_name", alternate = {"indicatorName"})
  private String indicatorName;
  private Long id;
  @SerializedName(value = "crp_id", alternate = {"crpId"})
  private String crpId;
  @SerializedName(value = "assessment_status", alternate = {"assessmentStatus"})
  private String assessmentStatus;
  private String updatedAt;


  public IndicatorStatus() {
  }


  public String getAssessmentStatus() {
    return assessmentStatus;
  }

  public String getCrpId() {
    return crpId;
  }

  public Long getId() {
    return id;
  }

  public String getIndicatorName() {
    return indicatorName;
  }

  public String getUpdatedAt() {
    return updatedAt;
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this, false);
  }

  public void setAssessmentStatus(String assessmentStatus) {
    this.assessmentStatus = assessmentStatus;
  }

  public void setCrpId(String crpId) {
    this.crpId = crpId;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setIndicatorName(String indicatorName) {
    this.indicatorName = indicatorName;
  }

  public void setUpdatedAt(String updatedAt) {
    this.updatedAt = updatedAt;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(this.getClass().getSimpleName()).append('@').append(this.hashCode()).append('{').append("indicatorName=")
      .append(this.getIndicatorName()).append(", id=").append(this.getId()).append(", crpId=").append(this.getCrpId())
      .append(", assessmentStatus=").append(this.getAssessmentStatus()).append(", updatedAt=")
      .append(this.getUpdatedAt()).append('}');
    return sb.toString();
  }
}
