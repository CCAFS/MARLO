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
public enum DeliverableGenderTypeEnum {
  COLLECTION(1, "Collection of sex-disaggregated data"), ANALYSYS(2, "Analysis of sex-disaggregated data "),
  DIAGNOSTICS(3, "Diagnostics/analysis to understand gender issues"),
  DEVELOPMENT(4, "Development of innovations/ interventions/ policies with explicit gender targeting"),
  MONITORING(5, "Monitoring/impact assessment of gender outcomes of research/innovations/interventions/polices");

  public static DeliverableGenderTypeEnum getValue(long id) {
    DeliverableGenderTypeEnum[] lst = DeliverableGenderTypeEnum.values();
    for (DeliverableGenderTypeEnum projectStatusEnum : lst) {
      if (projectStatusEnum.getId() == id) {
        return projectStatusEnum;
      }
    }
    return null;
  }

  private long id;

  private String value;

  private DeliverableGenderTypeEnum(long id, String value) {
    this.id = id;
    this.value = value;
  }

  public long getId() {
    return id;
  }


  public String getValue() {
    return value;
  }


  public void setId(long id) {
    this.id = id;
  }


  public void setValue(String value) {
    this.value = value;
  }

}
