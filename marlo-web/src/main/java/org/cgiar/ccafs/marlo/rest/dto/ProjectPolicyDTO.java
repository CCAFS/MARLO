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

/**
 * @author Diego Perez - CIAT/CCAFS
 **/

package org.cgiar.ccafs.marlo.rest.dto;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;

public class ProjectPolicyDTO {

  @ApiModelProperty(notes = "The Generated policy id")
  private Long id;

  @ApiModelProperty(notes = "Project Policy info")
  private ProjectPoliciesInfoDTO projectPoliciesInfo;

  @ApiModelProperty(notes = "Project identifier", position = 2)
  private Long project;

  @ApiModelProperty(notes = "Contributing CRP/Plataform List", position = 3)
  private List<ProjectPolicyCrpDTO> projectPolicyCrpDTO;

  @ApiModelProperty(notes = "CGIAR Sub-IDOs List", position = 1)
  private List<ProjectPolicySubIdoDTO> srfSubIdoList;

  @ApiModelProperty(notes = "Geographic Scope List", position = 4)
  private List<ProjectPolicyGeographicScopeDTO> geographicScopes;

  @ApiModelProperty(notes = "Crosscutting Markers List", position = 5)
  private List<CrosscuttingMarkersDTO> crossCuttingMarkers;

  @ApiModelProperty(notes = "Policy Owners List", position = 6)
  private List<PolicyOwnerTypeDTO> owners;

  @ApiModelProperty(notes = "Regions", position = 90)
  private List<RegionDTO> regions;

  @ApiModelProperty(notes = "Countries", position = 100)
  private List<CountryDTO> countries;

  @ApiModelProperty(notes = "Milestones", position = 110)
  private List<PolicyMilestoneDTO> milestones;

  @ApiModelProperty(notes = "OCIR List", position = 120)
  private List<ProjectExpectedStudyNameDTO> projectExpetedStudyList;


  @ApiModelProperty(notes = "Innovations List", position = 120)
  private List<ProjectInnovationNameDTO> projectInnovationList;


  public List<CountryDTO> getCountries() {
    return countries;
  }


  public List<CrosscuttingMarkersDTO> getCrossCuttingMarkers() {
    return crossCuttingMarkers;
  }


  public List<ProjectPolicyGeographicScopeDTO> getGeographicScopes() {
    return geographicScopes;
  }


  public Long getId() {
    return id;
  }

  public List<PolicyMilestoneDTO> getMilestones() {
    return milestones;
  }

  public List<PolicyOwnerTypeDTO> getOwners() {
    return owners;
  }

  public Long getProject() {
    return project;
  }

  public List<ProjectExpectedStudyNameDTO> getProjectExpetedStudyList() {
    return projectExpetedStudyList;
  }

  public List<ProjectInnovationNameDTO> getProjectInnovationList() {
    return projectInnovationList;
  }

  public ProjectPoliciesInfoDTO getProjectPoliciesInfo() {
    return projectPoliciesInfo;
  }

  public List<ProjectPolicyCrpDTO> getProjectPolicyCrpDTO() {
    return projectPolicyCrpDTO;
  }

  public List<RegionDTO> getRegions() {
    return regions;
  }

  public List<ProjectPolicySubIdoDTO> getSrfSubIdoList() {
    return srfSubIdoList;
  }

  public void setCountries(List<CountryDTO> countries) {
    this.countries = countries;
  }

  public void setCrossCuttingMarkers(List<CrosscuttingMarkersDTO> crossCuttingMarkers) {
    this.crossCuttingMarkers = crossCuttingMarkers;
  }


  public void setGeographicScopes(List<ProjectPolicyGeographicScopeDTO> geographicScopes) {
    this.geographicScopes = geographicScopes;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setMilestones(List<PolicyMilestoneDTO> milestones) {
    this.milestones = milestones;
  }

  public void setOwners(List<PolicyOwnerTypeDTO> owners) {
    this.owners = owners;
  }

  public void setProject(Long project) {
    this.project = project;
  }

  public void setProjectExpetedStudyList(List<ProjectExpectedStudyNameDTO> projectExpetedStudyList) {
    this.projectExpetedStudyList = projectExpetedStudyList;
  }

  public void setProjectInnovationList(List<ProjectInnovationNameDTO> projectInnovationList) {
    this.projectInnovationList = projectInnovationList;
  }

  public void setProjectPoliciesInfo(ProjectPoliciesInfoDTO projectPoliciesInfo) {
    this.projectPoliciesInfo = projectPoliciesInfo;
  }

  public void setProjectPolicyCrpDTO(List<ProjectPolicyCrpDTO> projectPolicyCrpDTO) {
    this.projectPolicyCrpDTO = projectPolicyCrpDTO;
  }

  public void setRegions(List<RegionDTO> regions) {
    this.regions = regions;
  }

  public void setSrfSubIdoList(List<ProjectPolicySubIdoDTO> srfSubIdoList) {
    this.srfSubIdoList = srfSubIdoList;
  }


}
