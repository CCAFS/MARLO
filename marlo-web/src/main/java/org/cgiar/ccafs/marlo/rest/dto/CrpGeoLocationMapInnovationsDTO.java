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


public class CrpGeoLocationMapInnovationsDTO {

  private long id;
  private String title;
  private String type;
  private String projectId;
  private int year;
  private String stage;
  private String link;

  public long getId() {
    return id;
  }

  public String getLink() {
    return link;
  }

  public String getProjectId() {
    return projectId;
  }

  public String getStage() {
    return stage;
  }

  public String getTitle() {
    return title;
  }

  public String getType() {
    return type;
  }

  public int getYear() {
    return year;
  }

  public void setId(long id) {
    this.id = id;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }

  public void setStage(String stage) {
    this.stage = stage;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setYear(int year) {
    this.year = year;
  }

}
