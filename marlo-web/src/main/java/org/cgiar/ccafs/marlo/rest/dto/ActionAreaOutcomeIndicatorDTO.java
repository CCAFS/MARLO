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

public class ActionAreaOutcomeIndicatorDTO {

  @ApiModelProperty(notes = "Action Area ID", position = 1)
  private Long actionAreaId;
  @ApiModelProperty(notes = "Action Area Name", position = 2)
  private String actionAreaName;
  @ApiModelProperty(notes = "Outcome ID", position = 3)
  private Long outcomeId;
  @ApiModelProperty(notes = "Outcome Statement", position = 4)
  private String outcomeStatement;
  @ApiModelProperty(notes = "Outcome Indicator ID", position = 5)
  private Long outcomeIndicatorId;
  @ApiModelProperty(notes = "Outcome Indicator Statement", position = 6)
  private String outcomeIndicatorStatement;

  public Long getActionAreaId() {
    return actionAreaId;
  }

  public String getActionAreaName() {
    return actionAreaName;
  }

  public Long getOutcomeId() {
    return outcomeId;
  }

  public Long getOutcomeIndicatorId() {
    return outcomeIndicatorId;
  }

  public String getOutcomeIndicatorStatement() {
    return outcomeIndicatorStatement;
  }

  public String getOutcomeStatement() {
    return outcomeStatement;
  }

  public void setActionAreaId(Long actionAreaId) {
    this.actionAreaId = actionAreaId;
  }

  public void setActionAreaName(String actionAreaName) {
    this.actionAreaName = actionAreaName;
  }

  public void setOutcomeId(Long outcomeId) {
    this.outcomeId = outcomeId;
  }

  public void setOutcomeIndicatorId(Long outcomeIndicatorId) {
    this.outcomeIndicatorId = outcomeIndicatorId;
  }

  public void setOutcomeIndicatorStatement(String outcomeIndicatorStatement) {
    this.outcomeIndicatorStatement = outcomeIndicatorStatement;
  }

  public void setOutcomeStatement(String outcomeStatement) {
    this.outcomeStatement = outcomeStatement;
  }


}
