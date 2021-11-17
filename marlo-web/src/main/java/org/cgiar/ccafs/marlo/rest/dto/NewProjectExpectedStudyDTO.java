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

public class NewProjectExpectedStudyDTO {

  @ApiModelProperty(notes = "Project ID", position = 1)
  private Long project;


  @ApiModelProperty(notes = "Project expected study info", position = 2)
  private NewProjectExpectedStudyInfoDTO projectExpectedStudyInfo;


  @ApiModelProperty(notes = "CGIAR Sub-IDOs SMO code List", position = 3)
  // private List<SrfSubIdoDTO> srfSubIdoList;
  private List<NewSrfSubIdoDTO> srfSubIdoList;


  @ApiModelProperty(notes = "CGIAR SLO target code List", position = 4)
  // private List<SrfSloTargetDTO> srfSloTargetList;
  private List<String> srfSloTargetList;


  @ApiModelProperty(notes = "Geographic Scope code List", position = 5)
  // private List<GeographicScopeDTO> geographicScopes;
  private List<String> geographicScopes;

  @ApiModelProperty(notes = "Geographic Scope comment box", position = 5)
  private String geographicScopeComment;


  @ApiModelProperty(notes = "Contributing Flagship SMO code List", position = 8)
  // private List<CrpProgramDTO> flagshipsList;
  private List<String> flagshipsList;


  @ApiModelProperty(notes = "Contributing External Partners code List", position = 9)
  // private List<InstitutionDTO> institutionsList;
  private List<String> institutionsList;


  @ApiModelProperty(notes = "Contributing CRP/Plataform SMO code List", position = 7)
  // private List<CGIAREntityDTO> projectExpectedStudiesCrpDTO;
  private List<String> projectExpectedStudiesCrpDTO;

  @ApiModelProperty(notes = "Region AN94 code list", position = 6)
  // private List<RegionDTO> regions;
  private List<String> regions;

  @ApiModelProperty(notes = "Country ISO code list", position = 6)
  private List<String> countries;
  // private List<CountryDTO> countries;

  @ApiModelProperty(notes = "Crosscutting Markers List", position = 12)
  private List<NewCrosscuttingMarkersDTO> crossCuttingMarkers;

  @ApiModelProperty(notes = "Quantification List List", position = 11)
  private List<QuantificationDTO> quantificationList;

  @ApiModelProperty(notes = "Contributing innovations code list", position = 10)
  private List<String> innovationCodeList;

  @ApiModelProperty(notes = "Contributing policies code list", position = 10)
  private List<String> policiesCodeList;

  @ApiModelProperty(notes = "Aditional link list", position = 4)
  private List<String> links;

  @ApiModelProperty(notes = "phase (POWB, AR, UpKeep)", position = 4)
  private PhaseDTO phase;

  @ApiModelProperty(notes = "Contributing milestones code list", position = 10)
  private List<NewMilestonesDTO> milestonesCodeList;


  @ApiModelProperty(notes = "Reference Cited list", position = 13)
  private List<ReferenceCitedDTO> referenceCitedList;


  public List<String> getCountries() {
    return countries;
  }


  public List<NewCrosscuttingMarkersDTO> getCrossCuttingMarkers() {
    return crossCuttingMarkers;
  }

  public List<String> getFlagshipsList() {
    return flagshipsList;
  }

  public String getGeographicScopeComment() {
    return geographicScopeComment;
  }


  public List<String> getGeographicScopes() {
    return geographicScopes;
  }


  public List<String> getInnovationCodeList() {
    return innovationCodeList;
  }

  public List<String> getInstitutionsList() {
    return institutionsList;
  }

  public List<String> getLinks() {
    return links;
  }

  public List<NewMilestonesDTO> getMilestonesCodeList() {
    return milestonesCodeList;
  }


  public PhaseDTO getPhase() {
    return phase;
  }


  public List<String> getPoliciesCodeList() {
    return policiesCodeList;
  }


  public Long getProject() {
    return project;
  }


  public List<String> getProjectExpectedStudiesCrpDTO() {
    return projectExpectedStudiesCrpDTO;
  }


  public NewProjectExpectedStudyInfoDTO getProjectExpectedStudyInfo() {
    return projectExpectedStudyInfo;
  }


  public List<QuantificationDTO> getQuantificationList() {
    return quantificationList;
  }


  public List<ReferenceCitedDTO> getReferenceCitedList() {
    return referenceCitedList;
  }


  public List<String> getRegions() {
    return regions;
  }


  public List<String> getSrfSloTargetList() {
    return srfSloTargetList;
  }


  public List<NewSrfSubIdoDTO> getSrfSubIdoList() {
    return srfSubIdoList;
  }


  public void setCountries(List<String> countries) {
    this.countries = countries;
  }


  public void setCrossCuttingMarkers(List<NewCrosscuttingMarkersDTO> crossCuttingMarkers) {
    this.crossCuttingMarkers = crossCuttingMarkers;
  }


  public void setFlagshipsList(List<String> flagshipsList) {
    this.flagshipsList = flagshipsList;
  }


  public void setGeographicScopeComment(String geographicScopeComment) {
    this.geographicScopeComment = geographicScopeComment;
  }


  public void setGeographicScopes(List<String> geographicScopes) {
    this.geographicScopes = geographicScopes;
  }


  public void setInnovationCodeList(List<String> innovationCodeList) {
    this.innovationCodeList = innovationCodeList;
  }


  public void setInstitutionsList(List<String> institutionsList) {
    this.institutionsList = institutionsList;
  }


  public void setLinks(List<String> links) {
    this.links = links;
  }


  public void setMilestonesCodeList(List<NewMilestonesDTO> milestonesCodeList) {
    this.milestonesCodeList = milestonesCodeList;
  }


  public void setPhase(PhaseDTO phase) {
    this.phase = phase;
  }


  public void setPoliciesCodeList(List<String> policiesCodeList) {
    this.policiesCodeList = policiesCodeList;
  }


  public void setProject(Long project) {
    this.project = project;
  }


  public void setProjectExpectedStudiesCrpDTO(List<String> projectExpectedStudiesCrpDTO) {
    this.projectExpectedStudiesCrpDTO = projectExpectedStudiesCrpDTO;
  }


  public void setProjectExpectedStudyInfo(NewProjectExpectedStudyInfoDTO projectExpectedStudyInfo) {
    this.projectExpectedStudyInfo = projectExpectedStudyInfo;
  }


  public void setQuantificationList(List<QuantificationDTO> quantificationList) {
    this.quantificationList = quantificationList;
  }


  public void setReferenceCitedList(List<ReferenceCitedDTO> referenceCitedList) {
    this.referenceCitedList = referenceCitedList;
  }


  public void setRegions(List<String> regions) {
    this.regions = regions;
  }


  public void setSrfSloTargetList(List<String> srfSloTargetList) {
    this.srfSloTargetList = srfSloTargetList;
  }


  public void setSrfSubIdoList(List<NewSrfSubIdoDTO> srfSubIdoList) {
    this.srfSubIdoList = srfSubIdoList;
  }


}
