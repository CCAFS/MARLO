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


public class CrpGeoLocationMapDeliverablesDTO {

  private long id;
  private String title;
  private String deliverableType;
  private String projectId;
  private String status;
  private String link;

  public String getDeliverableType() {
    return deliverableType;
  }

  public long getId() {
    return id;
  }

  public String getLink() {
    return link;
  }

  public String getProjectId() {
    return projectId;
  }

  public String getStatus() {
    return status;
  }

  public String getTitle() {
    return title;
  }

  public void setDeliverableType(String deliverableType) {
    this.deliverableType = deliverableType;
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

  public void setStatus(String status) {
    this.status = status;
  }

  public void setTitle(String title) {
    this.title = title;
  }


}
