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
public enum GlobalScopeEnum {

  GLOBAL(1, "Global"), REGIONAL(2, "Regional"), MULTINATIONAL(3, "Multi-national"), NATIONAL(4, "National"),
  SUBNATIONALM(5, "Sub-National: Multiple provinces or states"),
  SUBNATIONALS(6, "Sub-National: Single province or state"),
  SUBNATIONALSD(7, "Sub-national: Single district or municipality"), SINGLELOCATION(8, "Single location");

  /**
   * Look for the ProjectStatusEnum with id
   * 
   * @param id the id to search
   * @return Object ProjectStatusEnum if no exist null
   */
  public static GlobalScopeEnum getValue(int id) {
    GlobalScopeEnum[] lst = GlobalScopeEnum.values();
    for (GlobalScopeEnum projectStatusEnum : lst) {
      if (projectStatusEnum.getId() == id) {
        return projectStatusEnum;
      }
    }
    return null;
  }


  private String type;


  private int id;


  private GlobalScopeEnum(int id, String type) {
    this.id = id;
    this.type = type;
  }


  public int getId() {
    return id;
  }


  public String getType() {
    return type;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setType(String type) {
    this.type = type;
  }


}
