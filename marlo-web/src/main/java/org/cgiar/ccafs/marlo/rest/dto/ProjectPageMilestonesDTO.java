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

public class ProjectPageMilestonesDTO {

  @ApiModelProperty(notes = "ID", position = 1)
  @NotNull
  private String id;
  @ApiModelProperty(notes = "Milestone Name/Description", position = 2)
  @NotNull
  private String name;
  @ApiModelProperty(notes = "Milestone year", position = 3)
  private int year;

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public int getYear() {
    return year;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setYear(int year) {
    this.year = year;
  }
}
