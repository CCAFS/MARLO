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

import io.swagger.annotations.ApiModelProperty;

public class NewParticipantsCapDevDTO {

  @ApiModelProperty(notes = "Trainess short term female", position = 10)
  private Long traineesShortTermFemale;

  @ApiModelProperty(notes = "Trainees short term male", position = 20)
  private Long traineesShortTermMale;

  @ApiModelProperty(notes = "Trainees long term female", position = 25)
  private Long traineesLongTermFemale;

  @ApiModelProperty(notes = "Trainees long term male", position = 30)
  private Long traineesLongTermMale;

  @ApiModelProperty(notes = "Trainees phd female", position = 40)
  private Long traineesPhdFemale;

  @ApiModelProperty(notes = "Trainees phd male", position = 50)
  private Long traineesPhdMale;

  @ApiModelProperty(notes = "Link to Evidence", position = 60)
  private String evidencelink;


  @ApiModelProperty(notes = "Phase year/section", position = 140)
  private PhaseDTO phase;


  public String getEvidencelink() {
    return evidencelink;
  }


  public PhaseDTO getPhase() {
    return phase;
  }

  public Long getTraineesLongTermFemale() {
    return traineesLongTermFemale;
  }


  public Long getTraineesLongTermMale() {
    return traineesLongTermMale;
  }


  public Long getTraineesPhdFemale() {
    return traineesPhdFemale;
  }


  public Long getTraineesPhdMale() {
    return traineesPhdMale;
  }


  public Long getTraineesShortTermFemale() {
    return traineesShortTermFemale;
  }


  public Long getTraineesShortTermMale() {
    return traineesShortTermMale;
  }


  public void setEvidencelink(String evidencelink) {
    this.evidencelink = evidencelink;
  }


  public void setPhase(PhaseDTO phase) {
    this.phase = phase;
  }


  public void setTraineesLongTermFemale(Long traineesLongTermFemale) {
    this.traineesLongTermFemale = traineesLongTermFemale;
  }


  public void setTraineesLongTermMale(Long traineesLongTermMale) {
    this.traineesLongTermMale = traineesLongTermMale;
  }


  public void setTraineesPhdFemale(Long traineesPhdFemale) {
    this.traineesPhdFemale = traineesPhdFemale;
  }


  public void setTraineesPhdMale(Long traineesPhdMale) {
    this.traineesPhdMale = traineesPhdMale;
  }


  public void setTraineesShortTermFemale(Long traineesShortTermFemale) {
    this.traineesShortTermFemale = traineesShortTermFemale;
  }


  public void setTraineesShortTermMale(Long traineesShortTermMale) {
    this.traineesShortTermMale = traineesShortTermMale;
  }


}
