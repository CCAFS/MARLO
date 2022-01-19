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

public class ActionAreaOutcomeDTO {

  @ApiModelProperty(notes = "Outcome ID", position = 3)
  private Long outcomeId;
  @ApiModelProperty(notes = "Outcome SMO code", position = 4)
  private String outcomeSMOcode;

  @ApiModelProperty(notes = "Outcome Statement", position = 5)
  private String outcomeStatement;


  public Long getOutcomeId() {
    return outcomeId;
  }

  public String getOutcomeSMOcode() {
    return outcomeSMOcode;
  }

  public String getOutcomeStatement() {
    return outcomeStatement;
  }


  public void setOutcomeId(Long outcomeId) {
    this.outcomeId = outcomeId;
  }

  public void setOutcomeSMOcode(String outcomeSMOcode) {
    this.outcomeSMOcode = outcomeSMOcode;
  }

  public void setOutcomeStatement(String outcomeStatement) {
    this.outcomeStatement = outcomeStatement;
  }

}
