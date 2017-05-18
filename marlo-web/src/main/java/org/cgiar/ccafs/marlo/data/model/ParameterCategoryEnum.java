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


public enum ParameterCategoryEnum {

  Roles(1, "Roles"), Specificities(2, "Specificities"), Settings(3, "Settings");

  public static ParameterCategoryEnum getValue(int id) {
    ParameterCategoryEnum[] lst = ParameterCategoryEnum.values();
    for (ParameterCategoryEnum projectStatusEnum : lst) {
      if (projectStatusEnum.getId() == id) {
        return projectStatusEnum;
      }
    }
    return null;
  }


  private String format;


  private Integer id;


  private ParameterCategoryEnum(int id, String format) {
    this.id = id;
    this.format = format;
  }


  public String getFormat() {
    return format;
  }

  public Integer getId() {
    return id;
  }

  public void setFormat(String format) {
    this.format = format;
  }

  public void setId(Integer id) {
    this.id = id;
  }


}
