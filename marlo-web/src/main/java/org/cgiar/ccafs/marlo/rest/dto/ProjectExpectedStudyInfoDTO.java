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

  @ApiModelProperty(notes = "Expected study title", position = 1)
  private String title;

  @ApiModelProperty(notes = "Expected study year", position = 2)
  private long year;

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

  @ApiModelProperty(notes = "Gender Description", position = 8)
  private String genderDescribe;

  @ApiModelProperty(notes = "Youth Level", position = 9)
  private CrossCuttingMarkerScoreDTO youthLevel;

  @ApiModelProperty(notes = "Youth Description", position = 9)
  private String youthDescribe;

  @ApiModelProperty(notes = "ClimateChange Level", position = 10)
  private CrossCuttingMarkerScoreDTO climateChangeLevel;

  @ApiModelProperty(notes = "ClimateChange Description", position = 10)
  private String climateChangeDescribe;

  @ApiModelProperty(notes = "Capdev Level", position = 11)
  private CrossCuttingMarkerScoreDTO capdevLevel;

  @ApiModelProperty(notes = "Capdev Description", position = 11)
  private String capdevDescribe;

  @ApiModelProperty(notes = "Short outcome/impact statement", position = 12)
  private String outcomeImpactStatement;

  @ApiModelProperty(notes = "Outcome story for communications use", position = 13)
  private String comunicationsMaterial;


  @ApiModelProperty(notes = "Elaboration of Outcome/Impact Statement", position = 14)
  private String elaborationOutcomeImpactStatement;


  @ApiModelProperty(notes = "References cited", position = 15)
  private String referencesText;


  @ApiModelProperty(notes = "CGIAR Innovations found", position = 16)
  private String cgiarInnovation;

  public String getCapdevDescribe() {
    return capdevDescribe;
  }


  public CrossCuttingMarkerScoreDTO getCapdevLevel() {
    return capdevLevel;
  }

  public String getCgiarInnovation() {
    return cgiarInnovation;
  }


  public String getClimateChangeDescribe() {
    return climateChangeDescribe;
  }


  public CrossCuttingMarkerScoreDTO getClimateChangeLevel() {
    return climateChangeLevel;
  }


  public String getComunicationsMaterial() {
    return comunicationsMaterial;
  }


  public String getContacts() {
    return contacts;
  }


  public String getElaborationOutcomeImpactStatement() {
    return elaborationOutcomeImpactStatement;
  }

  public String getGenderDescribe() {
    return genderDescribe;
  }

  public CrossCuttingMarkerScoreDTO getGenderLevel() {
    return genderLevel;
  }

  public MaturityOfChangeDTO getMaturityOfChange() {
    return maturityOfChange;
  }


  public String getOutcomeImpactStatement() {
    return outcomeImpactStatement;
  }

  public String getReferencesText() {
    return referencesText;
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

  public String getYouthDescribe() {
    return youthDescribe;
  }

  public CrossCuttingMarkerScoreDTO getYouthLevel() {
    return youthLevel;
  }

  public void setCapdevDescribe(String capdevDescribe) {
    this.capdevDescribe = capdevDescribe;
  }

  public void setCapdevLevel(CrossCuttingMarkerScoreDTO capdevLevel) {
    this.capdevLevel = capdevLevel;
  }

  public void setCgiarInnovation(String cgiarInnovation) {
    this.cgiarInnovation = cgiarInnovation;
  }

  public void setClimateChangeDescribe(String climateChangeDescribe) {
    this.climateChangeDescribe = climateChangeDescribe;
  }

  public void setClimateChangeLevel(CrossCuttingMarkerScoreDTO climateChangeLevel) {
    this.climateChangeLevel = climateChangeLevel;
  }

  public void setComunicationsMaterial(String comunicationsMaterial) {
    this.comunicationsMaterial = comunicationsMaterial;
  }


  public void setContacts(String contacts) {
    this.contacts = contacts;
  }

  public void setElaborationOutcomeImpactStatement(String elaborationOutcomeImpactStatement) {
    this.elaborationOutcomeImpactStatement = elaborationOutcomeImpactStatement;
  }

  public void setGenderDescribe(String genderDescribe) {
    this.genderDescribe = genderDescribe;
  }

  public void setGenderLevel(CrossCuttingMarkerScoreDTO genderLevel) {
    this.genderLevel = genderLevel;
  }

  public void setMaturityOfChange(MaturityOfChangeDTO maturityOfChange) {
    this.maturityOfChange = maturityOfChange;
  }

  public void setOutcomeImpactStatement(String outcomeImpactStatement) {
    this.outcomeImpactStatement = outcomeImpactStatement;
  }

  public void setReferencesText(String referencesText) {
    this.referencesText = referencesText;
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

  public void setYouthDescribe(String youthDescribe) {
    this.youthDescribe = youthDescribe;
  }

  public void setYouthLevel(CrossCuttingMarkerScoreDTO youthLevel) {
    this.youthLevel = youthLevel;
  }

}
