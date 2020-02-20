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

public class ProjectExpectedStudyInfoDTO {


  @ApiModelProperty(notes = "Expected study title", position = 2)
  private long year;

  @ApiModelProperty(notes = "Expected study title", position = 1)
  private String title;

  @ApiModelProperty(notes = "Expected study type code", position = 3)
  private StudyTypeDTO studyType;

  @ApiModelProperty(notes = "Expected study status", position = 4)
  private Long status;

  @ApiModelProperty(notes = "Expected study tag code", position = 5)
  private TagDTO tag;

  @ApiModelProperty(notes = "Maturity of change reported code", position = 6)
  private MaturityOfChangeDTO maturityOfChange;

  @ApiModelProperty(notes = "Contact person", position = 7)
  private String contacts;

  @ApiModelProperty(notes = "Gender Level", position = 8)
  private CrossCuttingMarkerScoreDTO genderLevel;

  @ApiModelProperty(notes = "Youth Level", position = 9)
  private CrossCuttingMarkerScoreDTO youthLevel;

  @ApiModelProperty(notes = "ClimateChange Level", position = 10)
  private CrossCuttingMarkerScoreDTO climateChangeLevel;

  @ApiModelProperty(notes = "Capdev Level", position = 1)
  private CrossCuttingMarkerScoreDTO capdevLevel;


  public CrossCuttingMarkerScoreDTO getCapdevLevel() {
    return capdevLevel;
  }


  public CrossCuttingMarkerScoreDTO getClimateChangeLevel() {
    return climateChangeLevel;
  }


  public String getContacts() {
    return contacts;
  }


  public CrossCuttingMarkerScoreDTO getGenderLevel() {
    return genderLevel;
  }


  public MaturityOfChangeDTO getMaturityOfChange() {
    return maturityOfChange;
  }


  public long getStatus() {
    return status;
  }


  public StudyTypeDTO getStudyType() {
    return studyType;
  }


  public TagDTO getTag() {
    return tag;
  }


  public String getTitle() {
    return title;
  }


  public long getYear() {
    return year;
  }


  public CrossCuttingMarkerScoreDTO getYouthLevel() {
    return youthLevel;
  }


  public void setCapdevLevel(CrossCuttingMarkerScoreDTO capdevLevel) {
    this.capdevLevel = capdevLevel;
  }


  public void setClimateChangeLevel(CrossCuttingMarkerScoreDTO climateChangeLevel) {
    this.climateChangeLevel = climateChangeLevel;
  }


  public void setContacts(String contacts) {
    this.contacts = contacts;
  }


  public void setGenderLevel(CrossCuttingMarkerScoreDTO genderLevel) {
    this.genderLevel = genderLevel;
  }


  public void setMaturityOfChange(MaturityOfChangeDTO maturityOfChange) {
    this.maturityOfChange = maturityOfChange;
  }


  public void setStatus(long status) {
    this.status = status;
  }


  public void setStatus(Long status) {
    this.status = status;
  }


  public void setStudyType(StudyTypeDTO studyType) {
    this.studyType = studyType;
  }


  public void setTag(TagDTO tag) {
    this.tag = tag;
  }


  public void setTitle(String title) {
    this.title = title;
  }


  public void setYear(long year) {
    this.year = year;
  }


  public void setYouthLevel(CrossCuttingMarkerScoreDTO youthLevel) {
    this.youthLevel = youthLevel;
  }


}
