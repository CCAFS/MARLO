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

public enum DeliverableStatusEnum {

  ON_GOING("2", "On-going"), COMPLETE("3", "Complete"), EXTENDED("4", "Extended"), CANCELLED("5", "Cancelled"),
  PARTIALLY_COMPLETE("7", "Partially complete");

  /**
   * Look for the DeliverableStatusEnum with id
   * 
   * @param id the id to search
   * @return Object DeliverableStatusEnum if no exist null
   */
  public static DeliverableStatusEnum getValue(int id) {
    DeliverableStatusEnum[] lst = DeliverableStatusEnum.values();
    for (DeliverableStatusEnum deliverableStatusEnum : lst) {
      if (deliverableStatusEnum.getStatusId().equals(String.valueOf(id))) {
        return deliverableStatusEnum;
      }
    }
    return null;
  }

  private String status;

  private String statusId;

  private DeliverableStatusEnum(String statusId, String status) {
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
