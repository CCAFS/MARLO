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
 * @author Andres Valencia - CIAT/CCAFS
 */
public enum DeliverableIntellectualAssetTypeEnum {

  Patent("1", "Patent"), PVP("2", "PVP");

  /**
   * Look for the DeliverableIntellectualAssetTypeEnum with id
   * 
   * @param id the id to search
   * @return Object DeliverableIntellectualAssetTypeEnum if no exist null
   */
  public static DeliverableIntellectualAssetTypeEnum getValue(int id) {
    DeliverableIntellectualAssetTypeEnum[] lst = DeliverableIntellectualAssetTypeEnum.values();
    for (DeliverableIntellectualAssetTypeEnum deliverableIntellectualAssetTypeEnum : lst) {
      if (deliverableIntellectualAssetTypeEnum.getTypeId().equals(String.valueOf(id))) {
        return deliverableIntellectualAssetTypeEnum;
      }
    }
    return null;
  }


  private String type;

  private String typeId;

  private DeliverableIntellectualAssetTypeEnum(String typeId, String type) {
    this.typeId = typeId;
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public String getTypeId() {
    return typeId;
  }

  public void setTypeId(String typeId) {
    this.typeId = typeId;
  }

}
