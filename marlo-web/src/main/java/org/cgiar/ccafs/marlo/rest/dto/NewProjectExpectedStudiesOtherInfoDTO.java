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
 * @author Luis Benavides - CIAT/CCAFS
 * @author Modified by Diego Perez - CIAT/CCAFS
 */
public class NewProjectExpectedStudiesOtherInfoDTO {

  @ApiModelProperty(notes = "Expected year", position = 3)
  private int year;

  @ApiModelProperty(notes = "Expected study title", position = 4)
  private String title;

  @ApiModelProperty(notes = "Expected study type code", position = 1)
  private Long studyType;

  @ApiModelProperty(notes = "Expected study status", position = 2)
  private long status;

  @ApiModelProperty(notes = "Expected study commissioning", position = 5)
  private String commissioningStudy;

  @ApiModelProperty(notes = "Other geographic areas", position = 6)
  private String scopeComments;


  @ApiModelProperty(notes = "Expected study description", position = 6)
  private String studyDescription;

  @ApiModelProperty(notes = "Expected study Publications Link", position = 6)
  private String MELIAPublications;


  public String getCommissioningStudy() {
    return commissioningStudy;
  }


  public String getMELIAPublications() {
    return MELIAPublications;
  }


  public String getScopeComments() {
    return scopeComments;
  }


  public long getStatus() {
    return status;
  }


  public String getStudyDescription() {
    return studyDescription;
  }


  public Long getStudyType() {
    return studyType;
  }


  public String getTitle() {
    return title;
  }


  public int getYear() {
    return year;
  }


  public void setCommissioningStudy(String commissioningStudy) {
    this.commissioningStudy = commissioningStudy;
  }


  public void setMELIAPublications(String mELIAPublications) {
    MELIAPublications = mELIAPublications;
  }


  public void setScopeComments(String scopeComments) {
    this.scopeComments = scopeComments;
  }


  public void setStatus(long status) {
    this.status = status;
  }


  public void setStudyDescription(String studyDescription) {
    this.studyDescription = studyDescription;
  }


  public void setStudyType(Long studyType) {
    this.studyType = studyType;
  }


  public void setTitle(String title) {
    this.title = title;
  }


  public void setYear(int year) {
    this.year = year;
  }

}
