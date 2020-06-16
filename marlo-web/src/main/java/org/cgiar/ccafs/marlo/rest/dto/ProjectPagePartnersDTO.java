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

public class ProjectPagePartnersDTO {

  @ApiModelProperty(notes = "Institution Identifier", position = 1)
  private Long institutionID;

  @ApiModelProperty(notes = "Institution name", position = 2)
  private String instutitionName;

  @ApiModelProperty(notes = "Institution acronym", position = 3)
  private String institutionAcronym;

  @ApiModelProperty(notes = "Instutution Locations", position = 4)
  private List<LocElementDTO> partnerLocations;

  @ApiModelProperty(notes = "Project Leader", position = 5)
  private UserDTO projectLeader;

  @ApiModelProperty(notes = "Project Coordinator List", position = 6)
  private List<UserDTO> projectCoordinator;


  public String getInstitutionAcronym() {
    return institutionAcronym;
  }


  public Long getInstitutionID() {
    return institutionID;
  }


  public String getInstutitionName() {
    return instutitionName;
  }


  public List<LocElementDTO> getPartnerLocations() {
    return partnerLocations;
  }


  public List<UserDTO> getProjectCoordinator() {
    return projectCoordinator;
  }


  public UserDTO getProjectLeader() {
    return projectLeader;
  }


  public void setInstitutionAcronym(String institutionAcronym) {
    this.institutionAcronym = institutionAcronym;
  }


  public void setInstitutionID(Long institutionID) {
    this.institutionID = institutionID;
  }


  public void setInstutitionName(String instutitionName) {
    this.instutitionName = instutitionName;
  }


  public void setPartnerLocations(List<LocElementDTO> partnerLocations) {
    this.partnerLocations = partnerLocations;
  }


  public void setProjectCoordinator(List<UserDTO> projectCoordinator) {
    this.projectCoordinator = projectCoordinator;
  }


  public void setProjectLeader(UserDTO projectLeader) {
    this.projectLeader = projectLeader;
  }


}
