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

public enum SectionStatusEnum {


  OUTCOMES("outcomes"), CLUSTERACTIVITES("clusterActivities");

  public static SectionStatusEnum getValue(String section) {
    SectionStatusEnum[] lst = SectionStatusEnum.values();
    for (SectionStatusEnum sectionStatusEnum : lst) {
      if (sectionStatusEnum.getStatus().equals(section)) {
        return sectionStatusEnum;
      }
    }
    return null;
  }

  private String status;


  private SectionStatusEnum(String status) {
    this.status = status;
  }

  public String getStatus() {
    return status;
  }


}
