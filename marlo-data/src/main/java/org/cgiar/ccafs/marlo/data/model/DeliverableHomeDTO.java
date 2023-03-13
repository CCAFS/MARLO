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

package org.cgiar.ccafs.marlo.data.model;


/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class DeliverableHomeDTO {

  private long deliverableId;
  private long expectedYear;
  private long newExpectedYear;
  private long projectId;
  private String deliverableType;
  private String deliverableTitle;
  private String projectAcronym;

  public DeliverableHomeDTO() {
  }

  public DeliverableHomeDTO(long deliverableId, long newExpectedYear, long expectedYear, long projectId,
    String deliverableType, String deliverableTitle, String projectAcronym) {
    super();
    this.deliverableId = deliverableId;
    this.expectedYear = expectedYear;
    this.newExpectedYear = newExpectedYear;
    this.projectId = projectId;
    this.deliverableType = deliverableType;
    this.deliverableTitle = deliverableTitle;
    this.projectAcronym = projectAcronym;
  }

  public long getDeliverableId() {
    return deliverableId;
  }

  public String getDeliverableTitle() {
    return deliverableTitle;
  }

  public String getDeliverableType() {
    return deliverableType;
  }

  public long getExpectedYear() {
    return expectedYear;
  }

  public long getNewExpectedYear() {
    return newExpectedYear;
  }

  public String getProjectAcronym() {
    return projectAcronym;
  }

  public long getProjectId() {
    return projectId;
  }

  public void setDeliverableId(long deliverableId) {
    this.deliverableId = deliverableId;
  }

  public void setDeliverableTitle(String deliverableTitle) {
    this.deliverableTitle = deliverableTitle;
  }

  public void setDeliverableType(String deliverableType) {
    this.deliverableType = deliverableType;
  }

  public void setExpectedYear(long expectedYear) {
    this.expectedYear = expectedYear;
  }

  public void setNewExpectedYear(long newExpectedYear) {
    this.newExpectedYear = newExpectedYear;
  }


  public void setProjectAcronym(String projectAcronym) {
    this.projectAcronym = projectAcronym;
  }

  public void setProjectId(long projectId) {
    this.projectId = projectId;
  }
}
