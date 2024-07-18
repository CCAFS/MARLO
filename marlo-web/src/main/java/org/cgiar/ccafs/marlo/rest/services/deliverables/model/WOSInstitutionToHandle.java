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


public class WOSInstitutionToHandle implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 2633617960096822831L;


  public Prediction prediction;


  @SerializedName("name")
  private String fullName;


  public WOSInstitutionToHandle() {
  }

  public String getFullName() {
    return fullName;
  }


  public Prediction getPrediction() {
    return prediction;
  }


  public void setFullName(String fullName) {
    this.fullName = fullName;
  }


  public void setPrediction(Prediction prediction) {
    this.prediction = prediction;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
