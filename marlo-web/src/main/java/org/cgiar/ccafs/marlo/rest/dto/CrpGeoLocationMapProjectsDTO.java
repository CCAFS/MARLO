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

public class CrpGeoLocationMapProjectsDTO {

  private long id;
  private String title;
  private String status;
  private List<DefaultFieldStringDTO> flagships;
  private List<DefaultFieldStringDTO> regionalPrograms;
  private Boolean gender;
  private Boolean youth;
  private Boolean capdev;
  private Boolean climateChange;

  public Boolean getCapdev() {
    return capdev;
  }

  public Boolean getClimateChange() {
    return climateChange;
  }

  public List<DefaultFieldStringDTO> getFlagships() {
    return flagships;
  }

  public Boolean getGender() {
    return gender;
  }

  public long getId() {
    return id;
  }

  public List<DefaultFieldStringDTO> getRegionalPrograms() {
    return regionalPrograms;
  }

  public String getStatus() {
    return status;
  }

  public String getTitle() {
    return title;
  }

  public Boolean getYouth() {
    return youth;
  }

  public void setCapdev(Boolean capdev) {
    this.capdev = capdev;
  }

  public void setClimateChange(Boolean climateChange) {
    this.climateChange = climateChange;
  }

  public void setFlagships(List<DefaultFieldStringDTO> flagships) {
    this.flagships = flagships;
  }

  public void setGender(Boolean gender) {
    this.gender = gender;
  }

  public void setId(long id) {
    this.id = id;
  }

  public void setRegionalPrograms(List<DefaultFieldStringDTO> regionalPrograms) {
    this.regionalPrograms = regionalPrograms;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setYouth(Boolean youth) {
    this.youth = youth;
  }

}
