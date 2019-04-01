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

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */

public class MilestoneDTO {

  @ApiModelProperty(notes = "Id of milestone")
  @NotNull
  private Long id;

  @ApiModelProperty(notes = "Milestone title")
  @NotNull
  private String title;

  @ApiModelProperty(notes = "Milestone target year")
  private Integer year;

  @ApiModelProperty(notes = "Target unit")
  private TargetUnitDTO targetUnitDTO;

  @ApiModelProperty(notes = "Target Unit value")
  private BigDecimal value;

  @ApiModelProperty(notes = "Outcome of the milestone")
  private OutcomeDTO outcomeDTO;

  public Long getId() {
    return this.id;
  }

  public OutcomeDTO getOutcomeDTO() {
    return this.outcomeDTO;
  }

  public TargetUnitDTO getTargetUnitDTO() {
    return this.targetUnitDTO;
  }

  public String getTitle() {
    return this.title;
  }

  public BigDecimal getValue() {
    return this.value;
  }

  public Integer getYear() {
    return this.year;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setOutcomeDTO(OutcomeDTO outcomeDTO) {
    this.outcomeDTO = outcomeDTO;
  }

  public void setTargetUnitDTO(TargetUnitDTO targetUnitDTO) {
    this.targetUnitDTO = targetUnitDTO;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setValue(BigDecimal value) {
    this.value = value;
  }

  public void setYear(Integer year) {
    this.year = year;
  }

}
