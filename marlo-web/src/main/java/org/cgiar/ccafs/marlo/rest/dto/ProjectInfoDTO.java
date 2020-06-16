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

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */


public class ProjectInfoDTO {


  @ApiModelProperty(notes = "Project title", position = 1)
  private String title;

  @ApiModelProperty(notes = "Project Sumary", position = 2)
  private String summary;

  @ApiModelProperty(notes = "Project start date", position = 3)
  @JsonFormat(pattern = "yyyy-MM-dd")
  private Date startDate;

  @ApiModelProperty(notes = "Project end date", position = 4)
  @JsonFormat(pattern = "yyyy-MM-dd")
  private Date endDate;

  @ApiModelProperty(notes = "Gender Analisys", position = 10)
  private String genderAnalysis;

  @ApiModelProperty(notes = "Gender Dimension", position = 5)
  private Boolean crossCuttingGender;

  @ApiModelProperty(notes = "Youth Dimension", position = 6)
  private Boolean crossCuttingYouth;

  @ApiModelProperty(notes = "Capacity development dimension", position = 7)
  private Boolean crossCuttingCapacity;

  @ApiModelProperty(notes = "Climate change dimension", position = 8)
  private Boolean crossCuttingClimate;

  @ApiModelProperty(notes = "Not aplicable dimension", position = 9)
  private Boolean crossCuttingNa;

  public Boolean getCrossCuttingCapacity() {
    return crossCuttingCapacity;
  }

  public Boolean getCrossCuttingClimate() {
    return crossCuttingClimate;
  }


  public Boolean getCrossCuttingGender() {
    return crossCuttingGender;
  }


  public Boolean getCrossCuttingNa() {
    return crossCuttingNa;
  }


  public Boolean getCrossCuttingYouth() {
    return crossCuttingYouth;
  }


  public Date getEndDate() {
    return endDate;
  }


  public String getGenderAnalysis() {
    return genderAnalysis;
  }


  public Date getStartDate() {
    return startDate;
  }


  public String getSummary() {
    return summary;
  }


  public String getTitle() {
    return title;
  }


  public void setCrossCuttingCapacity(Boolean crossCuttingCapacity) {
    this.crossCuttingCapacity = crossCuttingCapacity;
  }


  public void setCrossCuttingClimate(Boolean crossCuttingClimate) {
    this.crossCuttingClimate = crossCuttingClimate;
  }


  public void setCrossCuttingGender(Boolean crossCuttingGender) {
    this.crossCuttingGender = crossCuttingGender;
  }


  public void setCrossCuttingNa(Boolean crossCuttingNa) {
    this.crossCuttingNa = crossCuttingNa;
  }


  public void setCrossCuttingYouth(Boolean crossCuttingYouth) {
    this.crossCuttingYouth = crossCuttingYouth;
  }


  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }


  public void setGenderAnalysis(String genderAnalysis) {
    this.genderAnalysis = genderAnalysis;
  }


  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }


  public void setSummary(String summary) {
    this.summary = summary;
  }


  public void setTitle(String title) {
    this.title = title;
  }

}
