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

  @ApiModelProperty(notes = "Name of innovation", position = 10)
  private String title;

  @ApiModelProperty(notes = "Description of the Innovation", position = 20)
  private String narrative;

  @ApiModelProperty(notes = "Project identifier", position = 25)
  private ProjectDTO project;

  @ApiModelProperty(notes = "Stage of innovation code", position = 30)
  private StageOfInnovationDTO stageOfInnovation;

  @ApiModelProperty(notes = "Description of stage reached", position = 40)
  private String descriptionStage;

  @ApiModelProperty(notes = "Next users organization types identifier", position = 50)
  private List<OrganizationTypeDTO> nextUserOrganizationTypes;

  @ApiModelProperty(notes = "Innovation Type identifier", position = 60)
  private InnovationTypeDTO innovationType;

  @ApiModelProperty(notes = "Number of individual improved lines/varietiesr", position = 61)
  private Long innovationNumber;


  @ApiModelProperty(notes = "Other Innovation Type description", position = 65)
  private String otherInnovationType;


  @ApiModelProperty(notes = "Geographic Scopes identifier", position = 70)
  private List<GeographicScopeDTO> geographicScopes;


  @ApiModelProperty(notes = "Regions codes", position = 80)
  private List<RegionDTO> regions;

  @ApiModelProperty(notes = "Countries codes", position = 90)
  private List<CountryDTO> countries;

  @ApiModelProperty(notes = "Is a leadership equitatable effort of the partners", position = 95)
  private Boolean equitativeEffort;

  @ApiModelProperty(notes = "id of Lead organization/entity to take innovation to this stage", position = 100)
  private InstitutionDTO leadOrganization;

  @ApiModelProperty(notes = "List of top five contributing organizations/entities codes to this stage", position = 110)
  private List<InstitutionDTO> contributingInstitutions;

  @ApiModelProperty(notes = "Evidence Link", position = 120)
  private String evidenceLink;

  @ApiModelProperty(notes = "Evidence Link", position = 121)
  private List<String> evidenceLinkList;


  @ApiModelProperty(notes = "Contributing CRPs/Platforms codes", position = 130)
  private List<CGIAREntityDTO> contributingCGIAREntities;


  @ApiModelProperty(notes = "Contributing milestones code list", position = 135)
  private List<NewMilestonesDTO> milestonesCodeList;


  @ApiModelProperty(notes = "CGIAR Sub-IDOs SMO code List", position = 134)
  private List<NewSrfSubIdoDTO> srfSubIdoList;

  @ApiModelProperty(notes = "Outcome Impact Case Report List", position = 92)
  private List<String> projectExpectedStudyList;

  @ApiModelProperty(notes = "Phase year/section", position = 140)
  private PhaseDTO phase;

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


  public Boolean getEquitativeEffort() {
    return this.equitativeEffort;
  }


  public String getEvidenceLink() {
    return this.evidenceLink;
  }


  public List<String> getEvidenceLinkList() {
    return evidenceLinkList;
  }


  public List<GeographicScopeDTO> getGeographicScopes() {
    return this.geographicScopes;
  }

  public Long getInnovationNumber() {
    return innovationNumber;
  }


  public InnovationTypeDTO getInnovationType() {
    return this.innovationType;
  }


  public InstitutionDTO getLeadOrganization() {
    return this.leadOrganization;
  }


  public List<NewMilestonesDTO> getMilestonesCodeList() {
    return milestonesCodeList;
  }


  public String getNarrative() {
    return this.narrative;
  }


  public List<OrganizationTypeDTO> getNextUserOrganizationTypes() {
    return this.nextUserOrganizationTypes;
  }


  public String getOtherInnovationType() {
    return this.otherInnovationType;
  }


  public PhaseDTO getPhase() {
    return phase;
  }


  public ProjectDTO getProject() {
    return this.project;
  }


  public List<String> getProjectExpectedStudyList() {
    return projectExpectedStudyList;
  }


  public List<RegionDTO> getRegions() {
    return this.regions;
  }


  public List<NewSrfSubIdoDTO> getSrfSubIdoList() {
    return srfSubIdoList;
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


  public void setEquitativeEffort(Boolean equitativeEffort) {
    this.equitativeEffort = equitativeEffort;
  }


  public void setEvidenceLink(String evidenceLink) {
    this.evidenceLink = evidenceLink;
  }


  public void setEvidenceLinkList(List<String> evidenceLinkList) {
    this.evidenceLinkList = evidenceLinkList;
  }


  public void setGeographicScopes(List<GeographicScopeDTO> geographicScopes) {
    this.geographicScopes = geographicScopes;
  }


  public void setInnovationNumber(Long innovationNumber) {
    this.innovationNumber = innovationNumber;
  }


  public void setInnovationType(InnovationTypeDTO innovationType) {
    this.innovationType = innovationType;
  }


  public void setLeadOrganization(InstitutionDTO leadOrganization) {
    this.leadOrganization = leadOrganization;
  }


  public void setMilestonesCodeList(List<NewMilestonesDTO> milestonesCodeList) {
    this.milestonesCodeList = milestonesCodeList;
  }


  public void setNarrative(String narrative) {
    this.narrative = narrative;
  }


  public void setNextUserOrganizationTypes(List<OrganizationTypeDTO> nextUserOrganizationTypes) {
    this.nextUserOrganizationTypes = nextUserOrganizationTypes;
  }


  public void setOtherInnovationType(String otherInnovationType) {
    this.otherInnovationType = otherInnovationType;
  }


  public void setPhase(PhaseDTO phase) {
    this.phase = phase;
  }


  public void setProject(ProjectDTO project) {
    this.project = project;
  }


  public void setProjectExpectedStudyList(List<String> projectExpectedStudyList) {
    this.projectExpectedStudyList = projectExpectedStudyList;
  }


  public void setRegions(List<RegionDTO> regions) {
    this.regions = regions;
  }


  public void setSrfSubIdoList(List<NewSrfSubIdoDTO> srfSubIdoList) {
    this.srfSubIdoList = srfSubIdoList;
  }


  public void setStageOfInnovation(StageOfInnovationDTO stageOfInnovation) {
    this.stageOfInnovation = stageOfInnovation;
  }


  public void setTitle(String title) {
    this.title = title;
  }


  // TODO: Deliverables
  // TODO: Shared projects


}
