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

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class CrossCGIARCollaborationDTO {

  @ApiModelProperty(notes = "The Generated identification Code", position = 1)
  private Long code;

  @ApiModelProperty(notes = "Brief description of the collaboration", position = 2)
  private String description;

  @ApiModelProperty(notes = "Value added,", position = 3)
  private String valueAdded;

  @ApiModelProperty(notes = "Id(s) of collaborating CRP(s), Platform(s) or Center(s)", position = 4)
  private List<CGIAREntityDTO> collaborationCrps;

  @ApiModelProperty(notes = "Flagship / Module", position = 5)
  private FlagshipProgramDTO flagshipProgram;

  @ApiModelProperty(notes = "CRP information", position = 6)
  private CGIAREntityDTO cgiarEntity;

  @ApiModelProperty(notes = "Phase (AR, POWB) - Year", position = 7)
  private PhaseDTO phase;


  public CGIAREntityDTO getCgiarEntity() {
    return cgiarEntity;
  }

  public Long getCode() {
    return code;
  }

  public List<CGIAREntityDTO> getCollaborationCrps() {
    return collaborationCrps;
  }

  public String getDescription() {
    return description;
  }

  public FlagshipProgramDTO getFlagshipProgram() {
    return flagshipProgram;
  }

  public PhaseDTO getPhase() {
    return phase;
  }

  public String getValueAdded() {
    return valueAdded;
  }


  public void setCgiarEntity(CGIAREntityDTO cgiarEntity) {
    this.cgiarEntity = cgiarEntity;
  }

  public void setCode(Long code) {
    this.code = code;
  }

  public void setCollaborationCrps(List<CGIAREntityDTO> collaborationCrps) {
    this.collaborationCrps = collaborationCrps;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setFlagshipProgram(FlagshipProgramDTO flagshipProgram) {
    this.flagshipProgram = flagshipProgram;
  }

  public void setPhase(PhaseDTO phase) {
    this.phase = phase;
  }

  public void setValueAdded(String valueAdded) {
    this.valueAdded = valueAdded;
  }

}
