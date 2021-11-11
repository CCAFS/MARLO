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

public class ProjectExpectedStudyDTO {

  @ApiModelProperty(notes = "The Generated expected study id", position = 1)
  private Long id;

  @ApiModelProperty(notes = "Project id", position = 1)
  private String project;


  @ApiModelProperty(notes = "Project expected study info", position = 2)
  private ProjectExpectedStudyInfoDTO projectExpectedEstudyInfo;


  @ApiModelProperty(notes = "CGIAR Sub-IDOs List", position = 3)
  private List<ProjectExpectedStudySubIdoDTO> srfSubIdoList;


  @ApiModelProperty(notes = "CGIAR SLO target List", position = 4)
  private List<ProjectExpectedStudySrfSloTargetDTO> srfSloTargetList;

  @ApiModelProperty(notes = "Geographic Scope List", position = 5)
  private List<GeographicScopeDTO> geographicScopes;

  @ApiModelProperty(notes = "Geographic Scope Comments", position = 5)
  private String scopeComments;


  @ApiModelProperty(notes = "Contributing Flagship List", position = 8)
  private List<CrpProgramDTO> flagshipsList;


  @ApiModelProperty(notes = "Contributing External Partners List", position = 9)
  private List<InstitutionDTO> institutionsList;


  @ApiModelProperty(notes = "Contributing CRP/Plataform List", position = 7)
  private List<CGIAREntityDTO> projectExpectedStudiesCrp;

  @ApiModelProperty(notes = "Regions", position = 6)
  private List<RegionDTO> regions;

  @ApiModelProperty(notes = "Countries", position = 6)
  private List<CountryDTO> countries;

  @ApiModelProperty(notes = "Quantification List List", position = 11)
  private List<QuantificationDTO> quantificationList;

  @ApiModelProperty(notes = "Contributing innovations list", position = 10)
  private List<ProjectExpectedStudyInnovationDTO> innovationCodeList;

  @ApiModelProperty(notes = "Contributing policies list", position = 3)
  private List<ProjectExpectedStudyPolicyDTO> policiesCodeList;

  @ApiModelProperty(notes = "Aditional link list", position = 4)
  private List<ProjectExpectedStudyLinkDTO> links;


  @ApiModelProperty(notes = "Contributing milestone list", position = 13)
  private List<ProjectExpectedStudyMilestoneDTO> milestonesList;

  @ApiModelProperty(notes = "phase (POWB, AR, UpKeep)", position = 14)
  private PhaseDTO phase;

  @ApiModelProperty(notes = "phase id", position = 14)
  private Long phaseID;

  @ApiModelProperty(notes = "Reference Cited List", position = 16)
  private List<ReferenceCitedDTO> referenceList;


  public List<CountryDTO> getCountries() {
    return countries;
  }


  public List<CrpProgramDTO> getFlagshipsList() {
    return flagshipsList;
  }

  public List<GeographicScopeDTO> getGeographicScopes() {
    return geographicScopes;
  }

  public Long getId() {
    return id;
  }


  public List<ProjectExpectedStudyInnovationDTO> getInnovationCodeList() {
    return innovationCodeList;
  }

  public List<InstitutionDTO> getInstitutionsList() {
    return institutionsList;
  }

  public List<ProjectExpectedStudyLinkDTO> getLinks() {
    return links;
  }

  public List<ProjectExpectedStudyMilestoneDTO> getMilestonesList() {
    return milestonesList;
  }


  public PhaseDTO getPhase() {
    return phase;
  }


  public Long getPhaseID() {
    return phaseID;
  }


  public List<ProjectExpectedStudyPolicyDTO> getPoliciesCodeList() {
    return policiesCodeList;
  }


  public String getProject() {
    return project;
  }


  public ProjectExpectedStudyInfoDTO getProjectExpectedEstudyInfo() {
    return projectExpectedEstudyInfo;
  }


  public List<CGIAREntityDTO> getProjectExpectedStudiesCrp() {
    return projectExpectedStudiesCrp;
  }


  public List<QuantificationDTO> getQuantificationList() {
    return quantificationList;
  }


  public List<ReferenceCitedDTO> getReferenceList() {
    return referenceList;
  }


  public List<RegionDTO> getRegions() {
    return regions;
  }


  public String getScopeComments() {
    return scopeComments;
  }


  public List<ProjectExpectedStudySrfSloTargetDTO> getSrfSloTargetList() {
    return srfSloTargetList;
  }


  public List<ProjectExpectedStudySubIdoDTO> getSrfSubIdoList() {
    return srfSubIdoList;
  }


  public void setCountries(List<CountryDTO> countries) {
    this.countries = countries;
  }


  public void setFlagshipsList(List<CrpProgramDTO> flagshipsList) {
    this.flagshipsList = flagshipsList;
  }


  public void setGeographicScopes(List<GeographicScopeDTO> geographicScopes) {
    this.geographicScopes = geographicScopes;
  }


  public void setId(Long id) {
    this.id = id;
  }


  public void setInnovationCodeList(List<ProjectExpectedStudyInnovationDTO> innovationCodeList) {
    this.innovationCodeList = innovationCodeList;
  }


  public void setInstitutionsList(List<InstitutionDTO> institutionsList) {
    this.institutionsList = institutionsList;
  }


  public void setLinks(List<ProjectExpectedStudyLinkDTO> links) {
    this.links = links;
  }


  public void setMilestonesList(List<ProjectExpectedStudyMilestoneDTO> milestonesList) {
    this.milestonesList = milestonesList;
  }


  public void setPhase(PhaseDTO phase) {
    this.phase = phase;
  }


  public void setPhaseID(Long phaseID) {
    this.phaseID = phaseID;
  }


  public void setPoliciesCodeList(List<ProjectExpectedStudyPolicyDTO> policiesCodeList) {
    this.policiesCodeList = policiesCodeList;
  }


  public void setProject(String project) {
    this.project = project;
  }


  public void setProjectExpectedEstudyInfo(ProjectExpectedStudyInfoDTO projectExpectedEstudyInfo) {
    this.projectExpectedEstudyInfo = projectExpectedEstudyInfo;
  }


  public void setProjectExpectedStudiesCrp(List<CGIAREntityDTO> projectExpectedStudiesCrp) {
    this.projectExpectedStudiesCrp = projectExpectedStudiesCrp;
  }


  public void setQuantificationList(List<QuantificationDTO> quantificationList) {
    this.quantificationList = quantificationList;
  }


  public void setReferenceList(List<ReferenceCitedDTO> referenceList) {
    this.referenceList = referenceList;
  }


  public void setRegions(List<RegionDTO> regions) {
    this.regions = regions;
  }


  public void setScopeComments(String scopeComments) {
    this.scopeComments = scopeComments;
  }


  public void setSrfSloTargetList(List<ProjectExpectedStudySrfSloTargetDTO> srfSloTargetList) {
    this.srfSloTargetList = srfSloTargetList;
  }


  public void setSrfSubIdoList(List<ProjectExpectedStudySubIdoDTO> srfSubIdoList) {
    this.srfSubIdoList = srfSubIdoList;
  }


}
