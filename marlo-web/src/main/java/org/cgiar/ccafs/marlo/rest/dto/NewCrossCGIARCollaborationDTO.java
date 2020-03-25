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

public class NewCrossCGIARCollaborationDTO {

  @ApiModelProperty(notes = "Flagship / Module SMO Code", position = 1)
  private String flagshipProgramId;

  @ApiModelProperty(notes = "Brief description of the collaboration", position = 2)
  private String description;

  @ApiModelProperty(notes = "Value added (e.g. scientific or efficiency benefits)", position = 3)
  private String valueAdded;

  @ApiModelProperty(notes = "Id(s) of collaborating CRP(s), Platform(s) or Center(s)", position = 4)
  private List<String> collaborationCrpIds;

  @ApiModelProperty(notes = "Phase (AR, POWB) - Year", position = 5)
  private PhaseDTO phase;


  public List<String> getCollaborationCrpIds() {
    return collaborationCrpIds;
  }

  public String getDescription() {
    return description;
  }

  public String getFlagshipProgramId() {
    return flagshipProgramId;
  }

  public PhaseDTO getPhase() {
    return phase;
  }

  public String getValueAdded() {
    return valueAdded;
  }


  public void setCollaborationCrpIds(List<String> collaborationCrpIds) {
    this.collaborationCrpIds = collaborationCrpIds;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setFlagshipProgramId(String flagshipProgramId) {
    this.flagshipProgramId = flagshipProgramId;
  }

  public void setPhase(PhaseDTO phase) {
    this.phase = phase;
  }

  public void setValueAdded(String valueAdded) {
    this.valueAdded = valueAdded;
  }

}
