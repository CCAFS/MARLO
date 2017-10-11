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


public enum ChannelEnum {

  //
  CGSPACE("cgspace", "CGSpace"), OTHER("other", "Other"), DATAVERSE("dataverse", "Dataverse (Harvard)"),
  IFPRI("ifpri", "IFPRI E-BRARY"), ILRI("ilri", "ILRI Datasets"), CIMMYT("cimmyt", "CIMMYT Dataverse");

  /**
   * Get ChannelEnum value from the id parameter
   * 
   * @param id
   * @return ChannelEnum
   */
  public static ChannelEnum getValue(String id) {
    ChannelEnum[] lst = ChannelEnum.values();
    for (ChannelEnum channelEnum : lst) {
      if (channelEnum.getId().equals(id)) {
        return channelEnum;
      }
    }
    return null;
  }

  private String id;
  private String desc;


  private ChannelEnum(String id, String desc) {
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
