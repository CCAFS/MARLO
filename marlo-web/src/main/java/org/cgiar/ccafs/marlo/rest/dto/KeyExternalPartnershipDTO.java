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

public class KeyExternalPartnershipDTO {

  @ApiModelProperty(notes = "The Generated identification Code", position = 1)
  private Long code;

  @ApiModelProperty(notes = "CRP information", position = 2)
  private CGIAREntityDTO cgiarEntity;

  @ApiModelProperty(notes = "Partnership Main Area(s)", position = 5)
  private List<PartnershipMainAreaDTO> partnershipMainAreas;

  @ApiModelProperty(notes = "Description of partnership aim", position = 3)
  private String description;

  @ApiModelProperty(notes = "Flagship / Module", position = 4)
  private FlagshipProgramDTO flagshipProgram;

  @ApiModelProperty(notes = "Phase (AR, POWB) - Year", position = 6)
  private PhaseDTO phase;

  @ApiModelProperty(notes = "Institutions linked to the partnership", position = 7)
  private List<InstitutionDTO> institutions;


  public CGIAREntityDTO getCgiarEntity() {
    return cgiarEntity;
  }

  public Long getCode() {
    return code;
  }

  public String getDescription() {
    return description;
  }

  public FlagshipProgramDTO getFlagshipProgram() {
    return flagshipProgram;
  }

  public List<InstitutionDTO> getInstitutions() {
    return institutions;
  }

  public List<PartnershipMainAreaDTO> getPartnershipMainAreas() {
    return partnershipMainAreas;
  }

  public PhaseDTO getPhase() {
    return phase;
  }

  public void setCgiarEntity(CGIAREntityDTO cgiarEntity) {
    this.cgiarEntity = cgiarEntity;
  }

  public void setCode(Long code) {
    this.code = code;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setFlagshipProgram(FlagshipProgramDTO flagshipProgram) {
    this.flagshipProgram = flagshipProgram;
  }

  public void setInstitutions(List<InstitutionDTO> institutions) {
    this.institutions = institutions;
  }

  public void setPartnershipMainAreas(List<PartnershipMainAreaDTO> partnershipMainAreas) {
    this.partnershipMainAreas = partnershipMainAreas;
  }

  public void setPhase(PhaseDTO phase) {
    this.phase = phase;
  }

}
