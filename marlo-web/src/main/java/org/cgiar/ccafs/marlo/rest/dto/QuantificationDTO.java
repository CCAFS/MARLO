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

public class QuantificationDTO {

  @ApiModelProperty(notes = "Quantification type (A/B)", position = 2)
  private String quantificationType;

  @ApiModelProperty(notes = "Quantification unit", position = 2)
  private String targetUnit;

  @ApiModelProperty(notes = "Quantification unit value", position = 2)
  private long number;

  @ApiModelProperty(notes = "Quantification unit comment", position = 2)
  private String comment;


  public String getComment() {
    return comment;
  }


  public long getNumber() {
    return number;
  }


  public String getQuantificationType() {
    return quantificationType;
  }


  public String getTargetUnit() {
    return targetUnit;
  }


  public void setComment(String comment) {
    this.comment = comment;
  }


  public void setNumber(long number) {
    this.number = number;
  }


  public void setQuantificationType(String quantificationType) {
    this.quantificationType = quantificationType;
  }


  public void setTargetUnit(String targetUnit) {
    this.targetUnit = targetUnit;
  }


}
