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

package org.cgiar.ccafs.marlo.rest.dto;

import io.swagger.annotations.ApiModelProperty;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class CrosscuttingMarkersJustificationDTO {

  @ApiModelProperty(notes = "CrossCutting marker identifier", position = 1)
  private CrossCuttingMarkerDTO crossCuttingmarker;

  @ApiModelProperty(notes = "CrossCutting marker score", position = 2)
  private CrossCuttingMarkerScoreDTO crossCuttingmarkerScore;

  @ApiModelProperty(notes = "CrossCutting marker score justification", position = 3)
  private String scoreJustification;


  public CrossCuttingMarkerDTO getCrossCuttingmarker() {
    return crossCuttingmarker;
  }

  public CrossCuttingMarkerScoreDTO getCrossCuttingmarkerScore() {
    return crossCuttingmarkerScore;
  }

  public String getScoreJustification() {
    return scoreJustification;
  }


  public void setCrossCuttingmarker(CrossCuttingMarkerDTO crossCuttingmarker) {
    this.crossCuttingmarker = crossCuttingmarker;
  }

  public void setCrossCuttingmarkerScore(CrossCuttingMarkerScoreDTO crossCuttingmarkerScore) {
    this.crossCuttingmarkerScore = crossCuttingmarkerScore;
  }

  public void setScoreJustification(String scoreJustification) {
    this.scoreJustification = scoreJustification;
  }

}
