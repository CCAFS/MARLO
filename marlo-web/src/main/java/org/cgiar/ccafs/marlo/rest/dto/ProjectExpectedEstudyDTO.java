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

import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCrp;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;

public class ProjectExpectedEstudyDTO {

  @ApiModelProperty(notes = "The Generated Project expected study id", position = 1)
  private Long id;

  @ApiModelProperty(notes = "Project expected study info", position = 2)
  private ProjectExpectedEstudyInfoDTO projectExpectedEstudyInfo;

  @ApiModelProperty(notes = "CGIAR Sub-IDOs List", position = 3)
  private List<SrfSubIdoDTO> srfSubIdoList;

  @ApiModelProperty(notes = "CGIAR SLO target List", position = 4)
  private List<SrfSloTargetDTO> srfSloTargetList;

  @ApiModelProperty(notes = "Geographic Scope List", position = 5)
  private List<GeographicScopeDTO> geographicScopes;

  @ApiModelProperty(notes = "Contributing Flagship List", position = 8)
  private List<CrpProgramDTO> flagshipsList;

  @ApiModelProperty(notes = "Contributing External Partners List", position = 9)
  private List<InstitutionDTO> institutionsList;

  @ApiModelProperty(notes = "Contributing CRP/Plataform List", position = 7)
  private List<ProjectExpectedStudyCrp> projectExpectedStudiesCrpDTO;

  @ApiModelProperty(notes = "Regions", position = 6)
  private List<RegionDTO> regions;

  @ApiModelProperty(notes = "Countries", position = 6)
  private List<CountryDTO> countries;

  @ApiModelProperty(notes = "Crosscutting Markers List", position = 12)
  private List<CrosscuttingMarkersDTO> crossCuttingMarkers;

  @ApiModelProperty(notes = "Quantification List List", position = 11)
  private List<QuantificationDTO> quantificationList;

  @ApiModelProperty(notes = "Contributing innovations code list", position = 10)
  private List<String> innovationCodeList;

  @ApiModelProperty(notes = "Contributing policies code list", position = 3)
  private List<String> policiesCodeList;

  @ApiModelProperty(notes = "Aditional link list", position = 4)
  private List<String> links;


  public List<CountryDTO> getCountries() {
    return countries;
  }


  public List<CrosscuttingMarkersDTO> getCrossCuttingMarkers() {
    return crossCuttingMarkers;
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


  public List<String> getInnovationCodeList() {
    return innovationCodeList;
  }


  public List<InstitutionDTO> getInstitutionsList() {
    return institutionsList;
  }


  public List<String> getLinks() {
    return links;
  }


  public List<String> getPoliciesCodeList() {
    return policiesCodeList;
  }


  public ProjectExpectedEstudyInfoDTO getProjectExpectedEstudyInfo() {
    return projectExpectedEstudyInfo;
  }


  public List<ProjectExpectedStudyCrp> getProjectExpectedStudiesCrpDTO() {
    return projectExpectedStudiesCrpDTO;
  }


  public List<QuantificationDTO> getQuantificationList() {
    return quantificationList;
  }


  public List<RegionDTO> getRegions() {
    return regions;
  }


  public List<SrfSloTargetDTO> getSrfSloTargetList() {
    return srfSloTargetList;
  }


  public List<SrfSubIdoDTO> getSrfSubIdoList() {
    return srfSubIdoList;
  }


  public void setCountries(List<CountryDTO> countries) {
    this.countries = countries;
  }


  public void setCrossCuttingMarkers(List<CrosscuttingMarkersDTO> crossCuttingMarkers) {
    this.crossCuttingMarkers = crossCuttingMarkers;
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


  public void setInnovationCodeList(List<String> innovationCodeList) {
    this.innovationCodeList = innovationCodeList;
  }


  public void setInstitutionsList(List<InstitutionDTO> institutionsList) {
    this.institutionsList = institutionsList;
  }


  public void setLinks(List<String> links) {
    this.links = links;
  }


  public void setPoliciesCodeList(List<String> policiesCodeList) {
    this.policiesCodeList = policiesCodeList;
  }


  public void setProjectExpectedEstudyInfo(ProjectExpectedEstudyInfoDTO projectExpectedEstudyInfo) {
    this.projectExpectedEstudyInfo = projectExpectedEstudyInfo;
  }


  public void setProjectExpectedStudiesCrpDTO(List<ProjectExpectedStudyCrp> projectExpectedStudiesCrpDTO) {
    this.projectExpectedStudiesCrpDTO = projectExpectedStudiesCrpDTO;
  }


  public void setQuantificationList(List<QuantificationDTO> quantificationList) {
    this.quantificationList = quantificationList;
  }


  public void setRegions(List<RegionDTO> regions) {
    this.regions = regions;
  }


  public void setSrfSloTargetList(List<SrfSloTargetDTO> srfSloTargetList) {
    this.srfSloTargetList = srfSloTargetList;
  }


  public void setSrfSubIdoList(List<SrfSubIdoDTO> srfSubIdoList) {
    this.srfSubIdoList = srfSubIdoList;
  }


}
