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


public enum CrpCategoryEnum {

  //
  CRP("1", "Crps"), CENTER("2", "Centers"), PLATAFORMS("3", "Plataforms");

  /**
   * Get ChannelEnum value from the id parameter
   * 
   * @param id
   * @return ChannelEnum
   */
  public static CrpCategoryEnum getValue(String id) {
    CrpCategoryEnum[] lst = CrpCategoryEnum.values();
    for (CrpCategoryEnum channelEnum : lst) {
      if (channelEnum.getId().equals(id)) {
        return channelEnum;
      }
    }
    return null;
  }

  private String id;
  private String desc;


  private CrpCategoryEnum(String id, String desc) {
    this.desc = desc;
    this.id = id;
  }


  public String getDesc() {
    return desc;
  }


  public String getId() {
    return id;
  }


  public void setDesc(String desc) {
    this.desc = desc;
  }

  public void setId(String id) {
    this.id = id;
  }
}
