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

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;

public class ImpactAreasIndicatorsDTO {


  @ApiModelProperty(notes = "Impact Area Indicator ID", position = 1)
  @NotNull
  public long indicatorId;


  @ApiModelProperty(notes = "Indicator Statement", position = 2)
  public String indicatorStatement;


  @ApiModelProperty(notes = "Impact Area ID", position = 3)
  public long impactAreaId;


  @ApiModelProperty(notes = "Impact Area Name", position = 4)
  public String impactAreaName;


  public long getImpactAreaId() {
    return impactAreaId;
  }


  public String getImpactAreaName() {
    return impactAreaName;
  }


  public long getIndicatorId() {
    return indicatorId;
  }


  public String getIndicatorStatement() {
    return indicatorStatement;
  }

  public void setImpactAreaId(long impactAreaId) {
    this.impactAreaId = impactAreaId;
  }

  public void setImpactAreaName(String impactAreaName) {
    this.impactAreaName = impactAreaName;
  }

  public void setIndicatorId(long indicatorId) {
    this.indicatorId = indicatorId;
  }

  public void setIndicatorStatement(String indicatorStatement) {
    this.indicatorStatement = indicatorStatement;
  }
}
