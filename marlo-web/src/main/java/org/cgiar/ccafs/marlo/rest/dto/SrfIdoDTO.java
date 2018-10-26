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


  @ApiModelProperty(notes = "The Generated SRF IDO ID")
  private Long id;


  private SrfCrossCuttingIssueDTO srfCrossCuttingIssue;


  @ApiModelProperty(notes = "The SRF IDO description ")
  private String description;


  @ApiModelProperty(notes = "Indicator if the SRF IDO is a Cross Cutting Issue")
  private boolean isCrossCutting;


  @ApiModelProperty(notes = "The Generated SMO Code for SRF IDO")
  private String smoCode;


  public String getDescription() {
    return description;
  }


  public Long getId() {
    return id;
  }


  public String getSmoCode() {
    return smoCode;
  }


  public SrfCrossCuttingIssueDTO getSrfCrossCuttingIssue() {
    return srfCrossCuttingIssue;
  }


  public boolean isCrossCutting() {
    return isCrossCutting;
  }

  public void setCrossCutting(boolean isCrossCutting) {
    this.isCrossCutting = isCrossCutting;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setSmoCode(String smoCode) {
    this.smoCode = smoCode;
  }

  public void setSrfCrossCuttingIssue(SrfCrossCuttingIssueDTO srfCrossCuttingIssue) {
    this.srfCrossCuttingIssue = srfCrossCuttingIssue;
  }

}
