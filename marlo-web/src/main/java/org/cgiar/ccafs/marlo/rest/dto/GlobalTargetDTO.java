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


public class GlobalTargetDTO {


  private Long impactAreaId;

  private String impactAreaName;

  private Long targetId;

  private String target;

  public Long getImpactAreaId() {
    return impactAreaId;
  }

  public String getImpactAreaName() {
    return impactAreaName;
  }

  public String getTarget() {
    return target;
  }

  public Long getTargetId() {
    return targetId;
  }

  public void setImpactAreaId(Long impactAreaId) {
    this.impactAreaId = impactAreaId;
  }

  public void setImpactAreaName(String impactAreaName) {
    this.impactAreaName = impactAreaName;
  }

  public void setTarget(String target) {
    this.target = target;
  }

  public void setTargetId(Long targetId) {
    this.targetId = targetId;
  }

}
