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

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class SrfIdoDTO {

  @ApiModelProperty(notes = "Cross cutting Issue associated")
  private SrfCrossCuttingIssueDTO srfCrossCuttingIssue;

  @ApiModelProperty(notes = "TheCode for SRF IDO")
  private String code;

  @ApiModelProperty(notes = "The SRF IDO description ")
  private String description;

  @ApiModelProperty(notes = "Indicator if the SRF IDO is a Cross Cutting Issue")
  private Boolean isCrossCutting;

  public String getCode() {
    return this.code;
  }

  public String getDescription() {
    return this.description;
  }

  public Boolean getIsCrossCutting() {
    return this.isCrossCutting;
  }

  public SrfCrossCuttingIssueDTO getSrfCrossCuttingIssue() {
    return this.srfCrossCuttingIssue;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public void setCrossCutting(boolean isCrossCutting) {
    this.setIsCrossCutting(isCrossCutting);
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setIsCrossCutting(Boolean isCrossCutting) {
    this.isCrossCutting = isCrossCutting;
  }

  public void setSrfCrossCuttingIssue(SrfCrossCuttingIssueDTO srfCrossCuttingIssue) {
    this.srfCrossCuttingIssue = srfCrossCuttingIssue;
  }

}
