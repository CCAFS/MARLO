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

public enum FeedbackStatusEnum {

  Approved("1", "Approved"), ClarificatioNeeded("2", "Clarification needed"), Pending("3", "Pending"),
  Accepted("4", "Accepted"), Rejected("5", "Rejected"), NoAccepted("6", "No Accepted"), Draft("7", "Draft");

  /**
   * Look for the FeedbackStatusEnum with id
   * 
   * @param id the id to search
   * @return Object FeedbackStatusEnum if no exist null
   */
  public static FeedbackStatusEnum getValue(int id) {
    FeedbackStatusEnum[] lst = FeedbackStatusEnum.values();
    for (FeedbackStatusEnum feedbackStatusEnum : lst) {
      if (feedbackStatusEnum.getStatusId().equals(String.valueOf(id))) {
        return feedbackStatusEnum;
      }
    }
    return null;
  }


  private String status;

  private String statusId;

  private FeedbackStatusEnum(String statusId, String status) {
    this.statusId = statusId;
    this.status = status;
  }

  public String getStatus() {
    return status;
  }

  public String getStatusId() {
    return statusId;
  }

  public void setStatusId(String statusId) {
    this.statusId = statusId;
  }

}
