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
public enum ImpactPathwaySectionsEnum {

  PROGRAM_IMPACT("programimpacts"), TOPIC("researchTopics"), OUTCOMES("outcomesList"), OUTPUTS("outputsList"),
  OUTCOME("outcomes"), OUTPUT("outputs");

  public static ImpactPathwaySectionsEnum getValue(String section) {
    ImpactPathwaySectionsEnum[] lst = ImpactPathwaySectionsEnum.values();
    for (ImpactPathwaySectionsEnum sectionStatusEnum : lst) {
      if (sectionStatusEnum.getStatus().equals(section)) {
        return sectionStatusEnum;
      }
    }
    return null;
  }

  private String status;


  private ImpactPathwaySectionsEnum(String status) {
    this.status = status;
  }

  public String getStatus() {
    return status;
  }

}
