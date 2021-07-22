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

public class GlossaryDTO {


  @ApiModelProperty(notes = "Application Glossary", position = 1)
  @NotNull
  private String applicationName;

  @ApiModelProperty(notes = "Item name", position = 2)
  private String title;
  @ApiModelProperty(notes = "Item definition", position = 3)
  private String definition;


  public String getApplicationName() {
    return applicationName;
  }


  public String getDefinition() {
    return definition;
  }


  public String getTitle() {
    return title;
  }

  public void setApplicationName(String applicationName) {
    this.applicationName = applicationName;
  }


  public void setDefinition(String definition) {
    this.definition = definition;
  }

  public void setTitle(String title) {
    this.title = title;
  }


}
