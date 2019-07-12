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

import java.util.List;

import io.swagger.annotations.ApiModelProperty;

public class NewInnovationDTO {

  @ApiModelProperty(notes = "Name of innovation", position = 1)
  private String title;

  @ApiModelProperty(notes = "Description of the Innovation", position = 2)
  private String narrative;

  @ApiModelProperty(notes = "Stage of innovation code", position = 3)
  private Long stageOfInnovation;

  @ApiModelProperty(notes = "Description of stage reached", position = 4)
  private String descriptionStage;

  @ApiModelProperty(notes = "Next users organization types codes", position = 5)
  private List<Long> nextUserOrganizationTypes;

  @ApiModelProperty(notes = "Innovation Type code", position = 6)
  private Long innovationType;

  @ApiModelProperty(notes = "Geographic Scopes code", position = 7)
  private List<Long> geographicScopes;

  @ApiModelProperty(notes = "Regions codes", position = 8)
  private List<Long> regions;

  @ApiModelProperty(notes = "Countries Alpha 2 codes", position = 9)
  private List<String> countries;

  @ApiModelProperty(notes = "id of Lead organization/entity to take innovation to this stage", position = 10)
  private Long leadOrganization;

  @ApiModelProperty(notes = "List of top five contributing organizations/entities codes to this stage", position = 11)
  private List<Long> contributingInstitutions;

  @ApiModelProperty(notes = "Evidence Link", position = 12)
  private String evidenceLink;

  @ApiModelProperty(notes = "Contributing CRPs/Platforms codes", position = 13)
  private List<String> contributingCGIAREntities;


  public List<String> getContributingCGIAREntities() {
    return this.contributingCGIAREntities;
  }


  public List<Long> getContributingInstitutions() {
    return this.contributingInstitutions;
  }


  public List<String> getCountries() {
    return this.countries;
  }


  public String getDescriptionStage() {
    return this.descriptionStage;
  }


  public String getEvidenceLink() {
    return this.evidenceLink;
  }


  public List<Long> getGeographicScopes() {
    return this.geographicScopes;
  }


  public Long getInnovationType() {
    return this.innovationType;
  }


  public Long getLeadOrganization() {
    return this.leadOrganization;
  }


  public String getNarrative() {
    return this.narrative;
  }


  public List<Long> getNextUserOrganizationTypes() {
    return this.nextUserOrganizationTypes;
  }


  public List<Long> getRegions() {
    return this.regions;
  }


  public Long getStageOfInnovation() {
    return this.stageOfInnovation;
  }


  public String getTitle() {
    return this.title;
  }


  public void setContributingCGIAREntities(List<String> contributingCGIAREntities) {
    this.contributingCGIAREntities = contributingCGIAREntities;
  }


  public void setContributingInstitutions(List<Long> contributingInstitutions) {
    this.contributingInstitutions = contributingInstitutions;
  }


  public void setCountries(List<String> countries) {
    this.countries = countries;
  }


  public void setDescriptionStage(String descriptionStage) {
    this.descriptionStage = descriptionStage;
  }


  public void setEvidenceLink(String evidenceLink) {
    this.evidenceLink = evidenceLink;
  }


  public void setGeographicScopes(List<Long> geographicScopes) {
    this.geographicScopes = geographicScopes;
  }


  public void setInnovationType(Long innovationType) {
    this.innovationType = innovationType;
  }


  public void setLeadOrganization(Long leadOrganization) {
    this.leadOrganization = leadOrganization;
  }


  public void setNarrative(String narrative) {
    this.narrative = narrative;
  }


  public void setNextUserOrganizationTypes(List<Long> nextUserOrganizationTypes) {
    this.nextUserOrganizationTypes = nextUserOrganizationTypes;
  }


  public void setRegions(List<Long> regions) {
    this.regions = regions;
  }


  public void setStageOfInnovation(Long stageOfInnovation) {
    this.stageOfInnovation = stageOfInnovation;
  }


  public void setTitle(String title) {
    this.title = title;
  }


  // TODO: Deliverables
  // TODO: Shared projects


}
