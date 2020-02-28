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

public class SrfSloIndicatorDTO {

  @ApiModelProperty(notes = "ID")
  private Long id;

  @ApiModelProperty(notes = "SLO of indicator")
  private SrfSloDTO srfSlo;

  @ApiModelProperty(notes = "title of indicator")
  private String title;

  @ApiModelProperty(notes = "Description of indicator")
  private String description;

  public SrfSloIndicatorDTO() {
    // TODO Auto-generated constructor stub
  }

  public String getDescription() {
    return description;
  }

  public Long getId() {
    return id;
  }

  public SrfSloDTO getSrfSlo() {
    return srfSlo;
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

  public void setSrfSlo(SrfSloDTO srfSlo) {
    this.srfSlo = srfSlo;
  }

  public void setTitle(String title) {
    this.title = title;
  }

}
