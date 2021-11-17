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

public class ProjectExpectedStudiesARDTO {

  @ApiModelProperty(notes = "The Generated expected study id", position = 1)
  private Long id;


  @ApiModelProperty(notes = "Project id", position = 1)
  private String project;


  @ApiModelProperty(notes = "Project expected study info", position = 2)
  private ProjectExpectedStudyInfoDTO projectExpectedEstudyInfo;


  @ApiModelProperty(notes = "CGIAR Sub-IDOs List", position = 3)
  private List<DefaultFieldPrimaryDTO> srfSubIdoList;


  @ApiModelProperty(notes = "CGIAR SLO target List", position = 4)
  private List<DefaultFieldDTO> srfSloTargetList;

  @ApiModelProperty(notes = "Geographic Scope List", position = 5)
  private List<DefaultFieldDTO> geographicScopes;

  @ApiModelProperty(notes = "Geographic Scope Comment", position = 5)
  private String scopeComment;

  @ApiModelProperty(notes = "Contributing Flagship List", position = 8)
  private List<DefaultFieldStringDTO> flagshipsList;

  @ApiModelProperty(notes = "Contributing External Partners List", position = 9)
  private List<DefaultFieldDTO> institutionsList;


  @ApiModelProperty(notes = "Contributing CRP/Plataform List", position = 7)
  private List<DefaultFieldStringDTO> projectExpectedStudiesCrp;


  @ApiModelProperty(notes = "Regions", position = 6)
  private List<DefaultFieldDTO> regions;

  @ApiModelProperty(notes = "Countries", position = 6)
  private List<DefaultFieldDTO> countries;

  @ApiModelProperty(notes = "Quantification List List", position = 11)
  private List<QuantificationDTO> quantificationList;

  @ApiModelProperty(notes = "Contributing innovations list", position = 10)
  private List<DefaultFieldDTO> innovationCodeList;

  @ApiModelProperty(notes = "Contributing policies list", position = 14)
  private List<DefaultFieldDTO> policiesCodeList;

  @ApiModelProperty(notes = "Aditional link list", position = 15)
  private List<ProjectExpectedStudyLinkDTO> links;


  @ApiModelProperty(notes = "Contributing milestone list", position = 13)
  private List<MilestoneNameDTO> milestonesList;


  @ApiModelProperty(notes = "Reference Cited List", position = 16)
  private List<ReferenceCitedDTO> referenceList;


  public List<DefaultFieldDTO> getCountries() {
    return countries;
  }

  public List<DefaultFieldStringDTO> getFlagshipsList() {
    return flagshipsList;
  }


  public List<DefaultFieldDTO> getGeographicScopes() {
    return geographicScopes;
  }


  public Long getId() {
    return id;
  }


  public List<DefaultFieldDTO> getInnovationCodeList() {
    return innovationCodeList;
  }


  public List<DefaultFieldDTO> getInstitutionsList() {
    return institutionsList;
  }


  public List<ProjectExpectedStudyLinkDTO> getLinks() {
    return links;
  }


  public List<MilestoneNameDTO> getMilestonesList() {
    return milestonesList;
  }


  public List<DefaultFieldDTO> getPoliciesCodeList() {
    return policiesCodeList;
  }


  public String getProject() {
    return project;
  }


  public ProjectExpectedStudyInfoDTO getProjectExpectedEstudyInfo() {
    return projectExpectedEstudyInfo;
  }


  public List<DefaultFieldStringDTO> getProjectExpectedStudiesCrp() {
    return projectExpectedStudiesCrp;
  }


  public List<QuantificationDTO> getQuantificationList() {
    return quantificationList;
  }


  public List<ReferenceCitedDTO> getReferenceList() {
    return referenceList;
  }


  public List<DefaultFieldDTO> getRegions() {
    return regions;
  }


  public String getScopeComment() {
    return scopeComment;
  }


  public List<DefaultFieldDTO> getSrfSloTargetList() {
    return srfSloTargetList;
  }


  public List<DefaultFieldPrimaryDTO> getSrfSubIdoList() {
    return srfSubIdoList;
  }


  public void setCountries(List<DefaultFieldDTO> countries) {
    this.countries = countries;
  }


  public void setFlagshipsList(List<DefaultFieldStringDTO> flagshipsList) {
    this.flagshipsList = flagshipsList;
  }


  public void setGeographicScopes(List<DefaultFieldDTO> geographicScopes) {
    this.geographicScopes = geographicScopes;
  }


  public void setId(Long id) {
    this.id = id;
  }


  public void setInnovationCodeList(List<DefaultFieldDTO> innovationCodeList) {
    this.innovationCodeList = innovationCodeList;
  }


  public void setInstitutionsList(List<DefaultFieldDTO> institutionsList) {
    this.institutionsList = institutionsList;
  }


  public void setLinks(List<ProjectExpectedStudyLinkDTO> links) {
    this.links = links;
  }


  public void setMilestonesList(List<MilestoneNameDTO> milestonesList) {
    this.milestonesList = milestonesList;
  }


  public void setPoliciesCodeList(List<DefaultFieldDTO> policiesCodeList) {
    this.policiesCodeList = policiesCodeList;
  }


  public void setProject(String project) {
    this.project = project;
  }


  public void setProjectExpectedEstudyInfo(ProjectExpectedStudyInfoDTO projectExpectedEstudyInfo) {
    this.projectExpectedEstudyInfo = projectExpectedEstudyInfo;
  }


  public void setProjectExpectedStudiesCrp(List<DefaultFieldStringDTO> projectExpectedStudiesCrp) {
    this.projectExpectedStudiesCrp = projectExpectedStudiesCrp;
  }


  public void setQuantificationList(List<QuantificationDTO> quantificationList) {
    this.quantificationList = quantificationList;
  }


  public void setReferenceList(List<ReferenceCitedDTO> referenceList) {
    this.referenceList = referenceList;
  }


  public void setRegions(List<DefaultFieldDTO> regions) {
    this.regions = regions;
  }


  public void setScopeComment(String scopeComment) {
    this.scopeComment = scopeComment;
  }

  public void setSrfSloTargetList(List<DefaultFieldDTO> srfSloTargetList) {
    this.srfSloTargetList = srfSloTargetList;
  }

  public void setSrfSubIdoList(List<DefaultFieldPrimaryDTO> srfSubIdoList) {
    this.srfSubIdoList = srfSubIdoList;
  }
}
