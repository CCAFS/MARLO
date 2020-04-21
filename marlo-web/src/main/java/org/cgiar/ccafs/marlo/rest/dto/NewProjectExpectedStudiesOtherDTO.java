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
 */
public class NewProjectExpectedStudiesOtherDTO {

  @ApiModelProperty(notes = "Project ID", position = 1)
  private Long project;

  @ApiModelProperty(notes = "Project expected studies other info", position = 2)
  private NewProjectExpectedStudiesOtherInfoDTO newProjectExpectedStudiesOtherInfo;

  @ApiModelProperty(notes = "Geographic scope code list", position = 3)
  private List<String> geographicScopes;

  @ApiModelProperty(notes = "Country ISO code list", position = 4)
  private List<String> countries;

  @ApiModelProperty(notes = "Region AN94 code list", position = 5)
  private List<String> regions;

  @ApiModelProperty(notes = "Other geographic areas", position = 6)
  private String scopeComments;


  @ApiModelProperty(notes = "CGIAR SLO target code list", position = 8)
  private List<String> srfSloTargetList;


  @ApiModelProperty(notes = "CGIAR Sub-IDOs SMO code List", position = 10)
  private List<NewSrfSubIdoDTO> srfSubIdoList;


  @ApiModelProperty(notes = "Comments", position = 9)
  private String comments;

  @ApiModelProperty(notes = "phase (POWB, AR, UpKeep)", position = 14)
  private PhaseDTO phase;

  public String getComments() {
    return comments;
  }

  public List<String> getCountries() {
    return countries;
  }


  public List<String> getGeographicScopes() {
    return geographicScopes;
  }


  public NewProjectExpectedStudiesOtherInfoDTO getNewProjectExpectedStudiesOtherInfo() {
    return newProjectExpectedStudiesOtherInfo;
  }


  public PhaseDTO getPhase() {
    return phase;
  }


  public Long getProject() {
    return project;
  }


  public List<String> getRegions() {
    return regions;
  }


  public String getScopeComments() {
    return scopeComments;
  }


  public List<String> getSrfSloTargetList() {
    return srfSloTargetList;
  }


  public List<NewSrfSubIdoDTO> getSrfSubIdoList() {
    return srfSubIdoList;
  }


  public void setComments(String comments) {
    this.comments = comments;
  }


  public void setCountries(List<String> countries) {
    this.countries = countries;
  }


  public void setGeographicScopes(List<String> geographicScopes) {
    this.geographicScopes = geographicScopes;
  }


  public void
    setNewProjectExpectedStudiesOtherInfo(NewProjectExpectedStudiesOtherInfoDTO newProjectExpectedStudiesOtherInfo) {
    this.newProjectExpectedStudiesOtherInfo = newProjectExpectedStudiesOtherInfo;
  }


  public void setPhase(PhaseDTO phase) {
    this.phase = phase;
  }


  public void setProject(Long project) {
    this.project = project;
  }


  public void setRegions(List<String> regions) {
    this.regions = regions;
  }


  public void setScopeComments(String scopeComments) {
    this.scopeComments = scopeComments;
  }


  public void setSrfSloTargetList(List<String> srfSloTargetList) {
    this.srfSloTargetList = srfSloTargetList;
  }


  public void setSrfSubIdoList(List<NewSrfSubIdoDTO> srfSubIdoList) {
    this.srfSubIdoList = srfSubIdoList;
  }


}
