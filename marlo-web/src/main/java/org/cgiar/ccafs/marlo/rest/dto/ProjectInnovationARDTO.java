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

import java.util.List;

import io.swagger.annotations.ApiModelProperty;

public class ProjectInnovationARDTO {

  @ApiModelProperty(notes = "Innovation ID", position = 10)
  private Long id;

  @ApiModelProperty(notes = "Name of innovation", position = 20)
  private String title;

  @ApiModelProperty(notes = "Project identifier", position = 25)
  private Long project;

  @ApiModelProperty(notes = "Project identifier", position = 27)
  private int year;

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
  private List<DefaultFieldDTO> geographicScopes;

  @ApiModelProperty(notes = "Regions", position = 90)
  private List<DefaultFieldDTO> regions;

  @ApiModelProperty(notes = "Countries", position = 100)
  private List<DefaultFieldDTO> countries;

  @ApiModelProperty(notes = "Is an equitatable effort of the partners", position = 110)
  private Boolean equitativeEffort;

  @ApiModelProperty(notes = "Lead organization/entity to take innovation to this stage", position = 110)
  private DefaultFieldDTO leadOrganization;


  @ApiModelProperty(notes = "List of top five contributing organizations/entities to this stage", position = 120)
  private List<DefaultFieldDTO> contributingInstitutions;

  @ApiModelProperty(notes = "Evidence Link", position = 130)
  private String evidenceLink;

  @ApiModelProperty(notes = "Evidence Link", position = 131)
  private List<EvidenceLinkDTO> evidenceLinkList;


  @ApiModelProperty(notes = "Contributing CRPs/Platforms", position = 140)
  private List<DefaultFieldStringDTO> contributingCGIAREntities;


  @ApiModelProperty(notes = "Contributing milestone list", position = 142)
  private List<MilestoneNameDTO> milestonesList;

  @ApiModelProperty(notes = "CGIAR Sub-IDOs List", position = 143)
  private List<DefaultFieldPrimaryDTO> srfSubIdoList;

  @ApiModelProperty(notes = "OCIR List", position = 144)
  private List<ProjectExpectedStudyNameDTO> projectExpetedStudyList;

  public List<DefaultFieldStringDTO> getContributingCGIAREntities() {
    return contributingCGIAREntities;
  }


  public List<DefaultFieldDTO> getContributingInstitutions() {
    return contributingInstitutions;
  }


  public List<DefaultFieldDTO> getCountries() {
    return countries;
  }


  public String getDescriptionStage() {
    return descriptionStage;
  }


  public Boolean getEquitativeEffort() {
    return equitativeEffort;
  }


  public String getEvidenceLink() {
    return evidenceLink;
  }


  public List<EvidenceLinkDTO> getEvidenceLinkList() {
    return evidenceLinkList;
  }


  public List<DefaultFieldDTO> getGeographicScopes() {
    return geographicScopes;
  }


  public Long getId() {
    return id;
  }


  public Long getInnovationNumber() {
    return innovationNumber;
  }


  public InnovationTypeDTO getInnovationType() {
    return innovationType;
  }


  public DefaultFieldDTO getLeadOrganization() {
    return leadOrganization;
  }


  public List<MilestoneNameDTO> getMilestonesList() {
    return milestonesList;
  }


  public String getNarrative() {
    return narrative;
  }


  public List<InstitutionTypeDTO> getNextUserOrganizationTypes() {
    return nextUserOrganizationTypes;
  }


  public String getOtherInnovationType() {
    return otherInnovationType;
  }


  public Long getProject() {
    return project;
  }


  public List<ProjectExpectedStudyNameDTO> getProjectExpetedStudyList() {
    return projectExpetedStudyList;
  }


  public List<DefaultFieldDTO> getRegions() {
    return regions;
  }


  public List<DefaultFieldPrimaryDTO> getSrfSubIdoList() {
    return srfSubIdoList;
  }


  public StageOfInnovationDTO getStageOfInnovation() {
    return stageOfInnovation;
  }


  public String getTitle() {
    return title;
  }


  public int getYear() {
    return year;
  }


  public void setContributingCGIAREntities(List<DefaultFieldStringDTO> contributingCGIAREntities) {
    this.contributingCGIAREntities = contributingCGIAREntities;
  }


  public void setContributingInstitutions(List<DefaultFieldDTO> contributingInstitutions) {
    this.contributingInstitutions = contributingInstitutions;
  }


  public void setCountries(List<DefaultFieldDTO> countries) {
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


  public void setGeographicScopes(List<DefaultFieldDTO> geographicScopes) {
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


  public void setLeadOrganization(DefaultFieldDTO leadOrganization) {
    this.leadOrganization = leadOrganization;
  }


  public void setMilestonesList(List<MilestoneNameDTO> milestonesList) {
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


  public void setProject(Long project) {
    this.project = project;
  }


  public void setProjectExpetedStudyList(List<ProjectExpectedStudyNameDTO> projectExpetedStudyList) {
    this.projectExpetedStudyList = projectExpetedStudyList;
  }


  public void setRegions(List<DefaultFieldDTO> regions) {
    this.regions = regions;
  }


  public void setSrfSubIdoList(List<DefaultFieldPrimaryDTO> srfSubIdoList) {
    this.srfSubIdoList = srfSubIdoList;
  }


  public void setStageOfInnovation(StageOfInnovationDTO stageOfInnovation) {
    this.stageOfInnovation = stageOfInnovation;
  }


  public void setTitle(String title) {
    this.title = title;
  }

  public void setYear(int year) {
    this.year = year;
  }
}
