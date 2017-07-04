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


/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public enum AgreementStatusEnum {

  ONGOING("2", "On-going"), PIPELINE("3", "Concept Note/Pipeline"), INFORMALLY("4", "Informally Confirmed");

  /**
   * Look for the AgreementStatusEnum with id
   * 
   * @param id the id to search
   * @return Object AgreementStatusEnum if no exist null
   */
  public static AgreementStatusEnum getValue(int id) {
    AgreementStatusEnum[] lst = AgreementStatusEnum.values();
    for (AgreementStatusEnum agreementStatusEnum : lst) {
      if (agreementStatusEnum.getStatusId().equals(String.valueOf(id))) {
        return agreementStatusEnum;
      }
    }
    return null;
  }


  private String status;

  private String statusId;

  private AgreementStatusEnum(String statusId, String status) {
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
