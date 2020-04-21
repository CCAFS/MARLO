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

/**
 * @author Luis Benavides - CIAT/CCAFS
 * @author Modified by Diego Perez - CIAT/CCAFS
 */
public class ProjectExpectedStudiesOtherDTO {

  @ApiModelProperty(notes = "Project ID", position = 1)
  private Long project;

  @ApiModelProperty(notes = "Project expected studies other info", position = 2)
  private ProjectExpectedStudiesOtherInfoDTO projectExpectedStudiesOtherInfo;

  @ApiModelProperty(notes = "CGIAR Sub-IDOs List", position = 3)
  private List<ProjectExpectedStudySubIdoDTO> srfSubIdoList;

  @ApiModelProperty(notes = "Geographic scope code list", position = 3)
  private List<GeographicScopeDTO> geographicScopes;

  @ApiModelProperty(notes = "Country ISO code list", position = 4)
  private List<CountryDTO> countries;

  @ApiModelProperty(notes = "Region AN94 code list", position = 5)
  private List<RegionDTO> regions;

  @ApiModelProperty(notes = "CGIAR SLO target code list", position = 8)
  private List<ProjectExpectedStudySrfSloTargetDTO> srfSloTargetList;

  @ApiModelProperty(notes = "phase (POWB, AR, UpKeep)", position = 14)
  private PhaseDTO phase;

  public List<CountryDTO> getCountries() {
    return countries;
  }


  public List<GeographicScopeDTO> getGeographicScopes() {
    return geographicScopes;
  }


  public PhaseDTO getPhase() {
    return phase;
  }


  public Long getProject() {
    return project;
  }


  public ProjectExpectedStudiesOtherInfoDTO getProjectExpectedStudiesOtherInfo() {
    return projectExpectedStudiesOtherInfo;
  }


  public List<RegionDTO> getRegions() {
    return regions;
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


  public void setGeographicScopes(List<GeographicScopeDTO> geographicScopes) {
    this.geographicScopes = geographicScopes;
  }


  public void setPhase(PhaseDTO phase) {
    this.phase = phase;
  }


  public void setProject(Long project) {
    this.project = project;
  }


  public void setProjectExpectedStudiesOtherInfo(ProjectExpectedStudiesOtherInfoDTO projectExpectedStudiesOtherInfo) {
    this.projectExpectedStudiesOtherInfo = projectExpectedStudiesOtherInfo;
  }

  public void setRegions(List<RegionDTO> regions) {
    this.regions = regions;
  }


  public void setSrfSloTargetList(List<ProjectExpectedStudySrfSloTargetDTO> srfSloTargetList) {
    this.srfSloTargetList = srfSloTargetList;
  }


  public void setSrfSubIdoList(List<ProjectExpectedStudySubIdoDTO> srfSubIdoList) {
    this.srfSubIdoList = srfSubIdoList;
  }

}
