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

public class MeliaInfoDTO {

  @ApiModelProperty(notes = "Expected study title", position = 1)
  private String title;

  @ApiModelProperty(notes = "Expected study year", position = 2)
  private Long year;

  @ApiModelProperty(notes = "Expected study type code", position = 3)
  private StudyTypeDTO studyType;

  @ApiModelProperty(notes = "Expected study status", position = 4)
  private MilestoneStatusDTO status;

  @ApiModelProperty(notes = "Description Study", position = 5)
  private String description;

  @ApiModelProperty(notes = "Melia Publications Links", position = 6)
  private String publications;

  @ApiModelProperty(notes = "Scope comments", position = 7)
  private String scopeComments;


  public String getDescription() {
    return description;
  }


  public String getPublications() {
    return publications;
  }


  public String getScopeComments() {
    return scopeComments;
  }


  public MilestoneStatusDTO getStatus() {
    return status;
  }


  public StudyTypeDTO getStudyType() {
    return studyType;
  }


  public String getTitle() {
    return title;
  }


  public Long getYear() {
    return year;
  }


  public void setDescription(String description) {
    this.description = description;
  }


  public void setPublications(String publications) {
    this.publications = publications;
  }


  public void setScopeComments(String scopeComments) {
    this.scopeComments = scopeComments;
  }


  public void setStatus(MilestoneStatusDTO status) {
    this.status = status;
  }


  public void setStudyType(StudyTypeDTO studyType) {
    this.studyType = studyType;
  }


  public void setTitle(String title) {
    this.title = title;
  }


  public void setYear(Long year) {
    this.year = year;
  }
}
