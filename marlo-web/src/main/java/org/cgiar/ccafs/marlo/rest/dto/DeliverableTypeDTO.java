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

import java.io.Serializable;

public class DeliverableTypeDTO implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1428487578221873894L;
  private String name;
  private String description;
  private Integer timeline;
  private Boolean fair;
  private Boolean adminType;
  private CGIAREntityDTO crp;

  public Boolean getAdminType() {
    return adminType;
  }

  public CGIAREntityDTO getCrp() {
    return crp;
  }

  public String getDescription() {
    return description;
  }

  public Boolean getFair() {
    return fair;
  }

  public String getName() {
    return name;
  }

  public Integer getTimeline() {
    return timeline;
  }

  public void setAdminType(Boolean adminType) {
    this.adminType = adminType;
  }

  public void setCrp(CGIAREntityDTO crp) {
    this.crp = crp;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setFair(Boolean fair) {
    this.fair = fair;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setTimeline(Integer timeline) {
    this.timeline = timeline;
  }
}
