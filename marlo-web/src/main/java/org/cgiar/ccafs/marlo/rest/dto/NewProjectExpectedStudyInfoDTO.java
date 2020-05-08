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

public class NewProjectExpectedStudyInfoDTO {

  @ApiModelProperty(notes = "Expected study title", position = 1)
  private String title;

  @ApiModelProperty(notes = "Expected year", position = 2)
  private int year;

  @ApiModelProperty(notes = "Expected study type code", position = 3)
  private Long studyType;

  @ApiModelProperty(notes = "Expected study status", position = 4)
  private long status;

  @ApiModelProperty(notes = "Expected study tag code", position = 5)
  private Long tag;

  @ApiModelProperty(notes = "Maturity of change reported code", position = 6)
  private Long maturityOfChange;

  @ApiModelProperty(notes = "Contact person", position = 7)
  private String contacts;

  @ApiModelProperty(notes = "Short outcome/impact statement (80 words max)", position = 8)
  private String outcomeImpactStatement;

  @ApiModelProperty(notes = "Outcome story for communications use (400 words max)", position = 9)
  private String comunicationsMaterial;

  @ApiModelProperty(notes = "Elaboration of Outcome/Impact Statement (400 words max)", position = 10)
  private String elaborationOutcomeImpactStatement;

  @ApiModelProperty(notes = "References cited", position = 11)
  private String referencesText;

  @ApiModelProperty(notes = "CGIAR Innovations found", position = 12)
  private String cgiarInnovation;


  public String getCgiarInnovation() {
    return cgiarInnovation;
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

  public Long getMaturityOfChange() {
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

  public Long getStudyType() {
    return studyType;
  }

  public Long getTag() {
    return tag;
  }

  public String getTitle() {
    return title;
  }

  public int getYear() {
    return year;
  }

  public void setCgiarInnovation(String cgiarInnovation) {
    this.cgiarInnovation = cgiarInnovation;
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

  public void setMaturityOfChange(Long maturityOfChange) {
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

  public void setStudyType(Long studyType) {
    this.studyType = studyType;
  }

  public void setTag(Long tag) {
    this.tag = tag;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setYear(int year) {
    this.year = year;
  }

}
