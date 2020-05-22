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

public class ProjectPolicyARDTO {

  @ApiModelProperty(notes = "The Generated policy id")
  private Long id;

  @ApiModelProperty(notes = "Project identifier", position = 2)
  private Long project;

  @ApiModelProperty(notes = "Project Policy info", position = 3)
  private ProjectPoliciesInfoDTO projectPolicyInfo;

  @ApiModelProperty(notes = "CRPs/Platforms identifier", position = 4)
  private List<DefaultFieldDTO> crps;

  @ApiModelProperty(notes = "CGIAR Sub-IDOs List", position = 5)
  private List<DefaultFieldPrimaryDTO> subIdos;

  @ApiModelProperty(notes = "Geographic Scope List", position = 6)
  private List<DefaultFieldDTO> geographicScopes;

  @ApiModelProperty(notes = "Crosscutting Markers List", position = 7)
  private List<CrosscuttingMarkersDTO> crossCuttingMarkers;

  @ApiModelProperty(notes = "Policy Owners List", position = 8)
  private List<PolicyOwnerTypeDTO> owners;

  @ApiModelProperty(notes = "Regions", position = 9)
  private List<DefaultFieldDTO> regions;

  @ApiModelProperty(notes = "Countries", position = 10)
  private List<DefaultFieldDTO> countries;

  @ApiModelProperty(notes = "Milestones", position = 11)
  private List<MilestoneNameDTO> milestones;

  @ApiModelProperty(notes = "OCIR List", position = 12)
  private List<ProjectExpectedStudyNameDTO> projectExpetedStudyList;

  @ApiModelProperty(notes = "Innovations List", position = 13)
  private List<ProjectInnovationNameDTO> projectInnovationList;


  public List<DefaultFieldDTO> getCountries() {
    return countries;
  }


  public List<CrosscuttingMarkersDTO> getCrossCuttingMarkers() {
    return crossCuttingMarkers;
  }


  public List<DefaultFieldDTO> getCrps() {
    return crps;
  }


  public List<DefaultFieldDTO> getGeographicScopes() {
    return geographicScopes;
  }


  public Long getId() {
    return id;
  }


  public List<MilestoneNameDTO> getMilestones() {
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


  public ProjectPoliciesInfoDTO getProjectPolicyInfo() {
    return projectPolicyInfo;
  }


  public List<DefaultFieldDTO> getRegions() {
    return regions;
  }


  public List<DefaultFieldPrimaryDTO> getSubIdos() {
    return subIdos;
  }


  public void setCountries(List<DefaultFieldDTO> countries) {
    this.countries = countries;
  }


  public void setCrossCuttingMarkers(List<CrosscuttingMarkersDTO> crossCuttingMarkers) {
    this.crossCuttingMarkers = crossCuttingMarkers;
  }


  public void setCrps(List<DefaultFieldDTO> crps) {
    this.crps = crps;
  }


  public void setGeographicScopes(List<DefaultFieldDTO> geographicScopes) {
    this.geographicScopes = geographicScopes;
  }


  public void setId(Long id) {
    this.id = id;
  }


  public void setMilestones(List<MilestoneNameDTO> milestones) {
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


  public void setProjectPolicyInfo(ProjectPoliciesInfoDTO projectPolicyInfo) {
    this.projectPolicyInfo = projectPolicyInfo;
  }


  public void setRegions(List<DefaultFieldDTO> regions) {
    this.regions = regions;
  }


  public void setSubIdos(List<DefaultFieldPrimaryDTO> subIdos) {
    this.subIdos = subIdos;
  }


}
