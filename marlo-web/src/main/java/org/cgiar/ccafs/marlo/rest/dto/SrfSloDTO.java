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
public class SrfSloDTO {


  @ApiModelProperty(notes = "The Generated Srf-SLO ID")
  private Long id;


  @ApiModelProperty(notes = "The Srf-SLO title")
  private String title;


  @ApiModelProperty(notes = "The Srf-SLO Description")
  private String description;


  @ApiModelProperty(notes = "The Generated SMO Code for the Srf-SLO")
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


  public String getTitle() {
    return title;
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

  public void setTitle(String title) {
    this.title = title;
  }

}
