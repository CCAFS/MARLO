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
  private List<String> flagships;
  private List<String> regionalPrograms;
  private String gender;
  private String youth;

  private String capdev;

  private String climateChange;

  public String getCapdev() {
    return capdev;
  }

  public String getClimateChange() {
    return climateChange;
  }

  public List<String> getFlagships() {
    return flagships;
  }

  public String getGender() {
    return gender;
  }

  public long getId() {
    return id;
  }

  public List<String> getRegionalPrograms() {
    return regionalPrograms;
  }

  public String getStatus() {
    return status;
  }

  public String getTitle() {
    return title;
  }

  public String getYouth() {
    return youth;
  }

  public void setCapdev(String capdev) {
    this.capdev = capdev;
  }

  public void setClimateChange(String climateChange) {
    this.climateChange = climateChange;
  }

  public void setFlagships(List<String> flagships) {
    this.flagships = flagships;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public void setId(long id) {
    this.id = id;
  }

  public void setRegionalPrograms(List<String> regionalPrograms) {
    this.regionalPrograms = regionalPrograms;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setYouth(String youth) {
    this.youth = youth;
  }

}
