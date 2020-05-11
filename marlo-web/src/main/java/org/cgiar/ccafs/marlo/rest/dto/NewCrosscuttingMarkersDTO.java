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

/**************
 * @author Diego Perez - CIAT/CCAFS
 **************/

package org.cgiar.ccafs.marlo.rest.dto;

import io.swagger.annotations.ApiModelProperty;

public class NewCrosscuttingMarkersDTO {


  @ApiModelProperty(
    notes = "CrossCutting marker identifier (1-Gender," + "2-Youth," + "3-CapDev," + "4-Climate Change)", position = 1)
  private String crossCuttingmarker;

  @ApiModelProperty(
    notes = "CrossCutting marker score (1-Not Targeted," + "2-Significant," + "3-Principal," + "4-Not applicable)",
    position = 2)
  private String crossCuttingmarkerScore;


  @ApiModelProperty("Crosscuting Marker Description (Optional)")
  private String description;


  public String getCrossCuttingmarker() {
    return crossCuttingmarker;
  }


  public String getCrossCuttingmarkerScore() {
    return crossCuttingmarkerScore;
  }


  public String getDescription() {
    return description;
  }


  public void setCrossCuttingmarker(String crossCuttingmarker) {
    this.crossCuttingmarker = crossCuttingmarker;
  }


  public void setCrossCuttingmarkerScore(String crossCuttingmarkerScore) {
    this.crossCuttingmarkerScore = crossCuttingmarkerScore;
  }


  public void setDescription(String description) {
    this.description = description;
  }

}
