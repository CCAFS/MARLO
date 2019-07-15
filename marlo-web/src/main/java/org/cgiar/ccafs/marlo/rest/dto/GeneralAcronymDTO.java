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

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */

public class GeneralAcronymDTO {

  @ApiModelProperty(notes = "Acronym ID", position = 1)
  @NotNull
  private Long code;

  @ApiModelProperty(notes = "Acronym", position = 2)
  @NotNull
  private String acronym;

  @ApiModelProperty(notes = "Description", position = 3)
  @NotNull
  private String description;


  public String getAcronym() {
    return this.acronym;
  }

  public Long getCode() {
    return this.code;
  }

  public String getDescription() {
    return this.description;
  }

  public void setAcronym(String acronym) {
    this.acronym = acronym;
  }

  public void setCode(Long code) {
    this.code = code;
  }

  public void setDescription(String description) {
    this.description = description;
  }


}
