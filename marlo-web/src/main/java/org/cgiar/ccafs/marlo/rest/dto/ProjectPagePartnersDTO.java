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

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;

public class ProjectPagePartnersDTO {

  @ApiModelProperty(notes = "Institution Identifier", position = 1)
  private Long institutionID;

  @ApiModelProperty(notes = "Institution name", position = 2)
  private String instutitionName;

  @ApiModelProperty(notes = "Institution acronym", position = 3)
  private String institutionAcronym;

  @ApiModelProperty(notes = "Institution website", position = 4)
  private String website;

  @ApiModelProperty(notes = "Instutution Locations", position = 5)
  private List<LocElementDTO> partnerOfficeLocations;

  @ApiModelProperty(notes = "Project Leader", position = 6)
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private UserDTO projectLeader;


  public String getInstitutionAcronym() {
    return institutionAcronym;
  }


  public Long getInstitutionID() {
    return institutionID;
  }


  public String getInstutitionName() {
    return instutitionName;
  }


  public List<LocElementDTO> getPartnerOfficeLocations() {
    return partnerOfficeLocations;
  }


  public UserDTO getProjectLeader() {
    return projectLeader;
  }


  public String getWebsite() {
    return website;
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


  public void setPartnerOfficeLocations(List<LocElementDTO> partnerOfficeLocations) {
    this.partnerOfficeLocations = partnerOfficeLocations;
  }


  public void setProjectLeader(UserDTO projectLeader) {
    this.projectLeader = projectLeader;
  }


  public void setWebsite(String website) {
    this.website = website;
  }


}
