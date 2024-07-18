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

public class Prediction implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @SerializedName("value")
  private PredictionValue value;
  private int confidant;

  public int getConfidant() {
    return confidant;
  }

  // Getters and setters
  public PredictionValue getValue() {
    return value;
  }

  public void setConfidant(int confidant) {
    this.confidant = confidant;
  }

  public void setValue(PredictionValue value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "Prediction [value=" + value + ", confidant=" + confidant + "]";
  }


}
