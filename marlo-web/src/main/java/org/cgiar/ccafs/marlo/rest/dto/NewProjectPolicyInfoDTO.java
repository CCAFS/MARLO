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

import io.swagger.annotations.ApiModelProperty;

public class NewProjectPolicyInfoDTO {

  @ApiModelProperty(notes = "policy Title", position = 1)
  private String title;

  @ApiModelProperty(notes = "Description", position = 2)
  private String description;

  @ApiModelProperty(notes = "Narrative Evidence", position = 3)
  private String narrativeEvidence;

  @ApiModelProperty(notes = "Policy Year", position = 4)
  private long year;

  @ApiModelProperty(notes = "Policy Investment Type", position = 5)
  private PolicyInvestmentTypeDTO repIndPolicyInvestimentType;

  @ApiModelProperty(notes = "Maturity level", position = 6)
  private PolicyMaturityLevelDTO repIndStageProcess;


  public String getDescription() {
    return description;
  }


  public String getNarrativeEvidence() {
    return narrativeEvidence;
  }


  public PolicyInvestmentTypeDTO getRepIndPolicyInvestimentType() {
    return repIndPolicyInvestimentType;
  }


  public PolicyMaturityLevelDTO getRepIndStageProcess() {
    return repIndStageProcess;
  }


  public String getTitle() {
    return title;
  }


  public long getYear() {
    return year;
  }


  public void setDescription(String description) {
    this.description = description;
  }


  public void setNarrativeEvidence(String narrativeEvidence) {
    this.narrativeEvidence = narrativeEvidence;
  }


  public void setRepIndPolicyInvestimentType(PolicyInvestmentTypeDTO repIndPolicyInvestimentType) {
    this.repIndPolicyInvestimentType = repIndPolicyInvestimentType;
  }


  public void setRepIndStageProcess(PolicyMaturityLevelDTO repIndStageProcess) {
    this.repIndStageProcess = repIndStageProcess;
  }


  public void setTitle(String title) {
    this.title = title;
  }


  public void setYear(long year) {
    this.year = year;
  }

}
