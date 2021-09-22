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

public class WorkPackagesDTO {

  @ApiModelProperty(notes = "Workpackage Identifier", position = 1)
  private Long id;

  @ApiModelProperty(notes = "Workpackage name", position = 2)
  private String name;

  @ApiModelProperty(notes = "Initiative Id", position = 3)
  private Long initiativeId;

  @ApiModelProperty(notes = "Initiative stage", position = 4)
  private Long stageId;

  @ApiModelProperty(notes = "Workpackage result", position = 5)
  private String results;

  @ApiModelProperty(notes = "Workpackage pathway content", position = 6)
  private String pathway_content;

  @ApiModelProperty(notes = "Workpackage acronym", position = 7)
  private String acronym;

  public String getAcronym() {
    return acronym;
  }

  public Long getId() {
    return id;
  }

  public Long getInitiativeId() {
    return initiativeId;
  }

  public String getName() {
    return name;
  }

  public String getPathway_content() {
    return pathway_content;
  }

  public String getResults() {
    return results;
  }

  public Long getStageId() {
    return stageId;
  }

  public void setAcronym(String acronym) {
    this.acronym = acronym;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setInitiativeId(Long initiativeId) {
    this.initiativeId = initiativeId;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPathway_content(String pathway_content) {
    this.pathway_content = pathway_content;
  }

  public void setResults(String results) {
    this.results = results;
  }

  public void setStageId(Long stageId) {
    this.stageId = stageId;
  }


}
