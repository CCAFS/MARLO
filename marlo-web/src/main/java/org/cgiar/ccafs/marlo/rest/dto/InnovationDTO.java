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

  @ApiModelProperty(notes = "Project identifier", position = 25)
  private ProjectDTO project;

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

  @ApiModelProperty(notes = "Number of individual improved lines/varietiesr", position = 71)
  private Long innovationNumber;


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

  @ApiModelProperty(notes = "Evidence Link list", position = 131)
  private List<EvidenceLinkDTO> evidenceLinkList;


  @ApiModelProperty(notes = "Contributing CRPs/Platforms", position = 140)
  private List<CGIAREntityDTO> contributingCGIAREntities;


  @ApiModelProperty(notes = "Contributing milestone list", position = 142)
  private List<ProjectInnovationMilestoneDTO> milestonesList;


  @ApiModelProperty(notes = "CGIAR Sub-IDOs List", position = 143)
  private List<ProjectInnovationSubIdoDTO> srfSubIdoList;

  @ApiModelProperty(notes = "OCIR List", position = 144)
  private List<ProjectExpectedStudyNameDTO> projectExpetedStudyList;

  @ApiModelProperty(notes = "Phase year/section", position = 160)
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
    return equitativeEffort;
  }


  public String getEvidenceLink() {
    return this.evidenceLink;
  }


  public List<EvidenceLinkDTO> getEvidenceLinkList() {
    return evidenceLinkList;
  }


  public List<GeographicScopeDTO> getGeographicScopes() {
    return this.geographicScopes;
  }


  public Long getId() {
    return this.id;
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


  public List<ProjectInnovationMilestoneDTO> getMilestonesList() {
    return milestonesList;
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


  public PhaseDTO getPhase() {
    return phase;
  }


  public ProjectDTO getProject() {
    return project;
  }


  public List<ProjectExpectedStudyNameDTO> getProjectExpetedStudyList() {
    return projectExpetedStudyList;
  }


  public List<RegionDTO> getRegions() {
    return this.regions;
  }


  public List<ProjectInnovationSubIdoDTO> getSrfSubIdoList() {
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


  public void setEvidenceLinkList(List<EvidenceLinkDTO> evidenceLinkList) {
    this.evidenceLinkList = evidenceLinkList;
  }


  public void setGeographicScopes(List<GeographicScopeDTO> geographicScopes) {
    this.geographicScopes = geographicScopes;
  }


  public void setId(Long id) {
    this.id = id;
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


  public void setMilestonesList(List<ProjectInnovationMilestoneDTO> milestonesList) {
    this.milestonesList = milestonesList;
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


  public void setPhase(PhaseDTO phase) {
    this.phase = phase;
  }


  public void setProject(ProjectDTO project) {
    this.project = project;
  }


  public void setProjectExpetedStudyList(List<ProjectExpectedStudyNameDTO> projectExpetedStudyList) {
    this.projectExpetedStudyList = projectExpetedStudyList;
  }


  public void setRegions(List<RegionDTO> regions) {
    this.regions = regions;
  }


  public void setSrfSubIdoList(List<ProjectInnovationSubIdoDTO> srfSubIdoList) {
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
