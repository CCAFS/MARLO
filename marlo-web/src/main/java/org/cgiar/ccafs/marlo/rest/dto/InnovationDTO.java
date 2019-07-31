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

public class InnovationDTO {

  @ApiModelProperty(notes = "Innovation ID", position = 10)
  private Long id;

  @ApiModelProperty(notes = "Name of innovation", position = 20)
  private String title;

  @ApiModelProperty(notes = "Description of the Innovation", position = 30)
  private String narrative;

  @ApiModelProperty(notes = "Stage of innovation", position = 40)
  private StageOfInnovationDTO stageOfInnovation;

  @ApiModelProperty(notes = "Next user organization type", position = 50)
  private List<InstitutionTypeDTO> nextUserOrganizationTypes;

  @ApiModelProperty(notes = "Description of stage reached", position = 60)
  private String descriptionStage;

  @ApiModelProperty(notes = "Innovation Type", position = 70)
  private InnovationTypeDTO innovationType;

  @ApiModelProperty(notes = "Other Innovation Type description", position = 75)
  private String otherInnovationType;

  @ApiModelProperty(notes = "Geographic Scope", position = 80)
  private List<GeographicScopeDTO> geographicScopes;

  @ApiModelProperty(notes = "Regions", position = 90)
  private List<RegionDTO> regions;

  @ApiModelProperty(notes = "Countries", position = 100)
  private List<CountryDTO> countries;

  @ApiModelProperty(notes = "Is an equitatable effort of the partners", position = 110)
  private Boolean equitativeEffort;

  @ApiModelProperty(notes = "Lead organization/entity to take innovation to this stage", position = 110)
  private InstitutionDTO leadOrganization;

  @ApiModelProperty(notes = "List of top five contributing organizations/entities to this stage", position = 120)
  private List<InstitutionDTO> contributingInstitutions;

  @ApiModelProperty(notes = "Evidence Link", position = 130)
  private String evidenceLink;

  @ApiModelProperty(notes = "Contributing CRPs/Platforms", position = 140)
  private List<CGIAREntityDTO> contributingCGIAREntities;


  public List<CGIAREntityDTO> getContributingCGIAREntities() {
    return this.contributingCGIAREntities;
  }


  public List<InstitutionDTO> getContributingInstitutions() {
    return this.contributingInstitutions;
  }


  public List<CountryDTO> getCountries() {
    return this.countries;
  }


  public String getDescriptionStage() {
    return this.descriptionStage;
  }


  public String getEvidenceLink() {
    return this.evidenceLink;
  }


  public List<GeographicScopeDTO> getGeographicScopes() {
    return this.geographicScopes;
  }


  public Long getId() {
    return this.id;
  }


  public InnovationTypeDTO getInnovationType() {
    return this.innovationType;
  }


  public InstitutionDTO getLeadOrganization() {
    return this.leadOrganization;
  }


  public String getNarrative() {
    return this.narrative;
  }


  public List<InstitutionTypeDTO> getNextUserOrganizationTypes() {
    return this.nextUserOrganizationTypes;
  }


  public String getOtherInnovationType() {
    return this.otherInnovationType;
  }


  public List<RegionDTO> getRegions() {
    return this.regions;
  }


  public StageOfInnovationDTO getStageOfInnovation() {
    return this.stageOfInnovation;
  }


  public String getTitle() {
    return this.title;
  }


  public void setContributingCGIAREntities(List<CGIAREntityDTO> contributingCGIAREntities) {
    this.contributingCGIAREntities = contributingCGIAREntities;
  }


  public void setContributingInstitutions(List<InstitutionDTO> contributingInstitutions) {
    this.contributingInstitutions = contributingInstitutions;
  }


  public void setCountries(List<CountryDTO> countries) {
    this.countries = countries;
  }


  public void setDescriptionStage(String descriptionStage) {
    this.descriptionStage = descriptionStage;
  }


  public void setEvidenceLink(String evidenceLink) {
    this.evidenceLink = evidenceLink;
  }


  public void setGeographicScopes(List<GeographicScopeDTO> geographicScopes) {
    this.geographicScopes = geographicScopes;
  }


  public void setId(Long id) {
    this.id = id;
  }


  public void setInnovationType(InnovationTypeDTO innovationType) {
    this.innovationType = innovationType;
  }


  public void setLeadOrganization(InstitutionDTO leadOrganization) {
    this.leadOrganization = leadOrganization;
  }


  public void setNarrative(String narrative) {
    this.narrative = narrative;
  }


  public void setNextUserOrganizationTypes(List<InstitutionTypeDTO> nextUserOrganizationTypes) {
    this.nextUserOrganizationTypes = nextUserOrganizationTypes;
  }


  public void setOtherInnovationType(String otherInnovationType) {
    this.otherInnovationType = otherInnovationType;
  }


  public void setRegions(List<RegionDTO> regions) {
    this.regions = regions;
  }


  public void setStageOfInnovation(StageOfInnovationDTO stageOfInnovation) {
    this.stageOfInnovation = stageOfInnovation;
  }


  public void setTitle(String title) {
    this.title = title;
  }


  public Boolean getEquitativeEffort() {
    return equitativeEffort;
  }


  public void setEquitativeEffort(Boolean equitativeEffort) {
    this.equitativeEffort = equitativeEffort;
  }

  // TODO: Deliverables
  // TODO: Shared projects


}
