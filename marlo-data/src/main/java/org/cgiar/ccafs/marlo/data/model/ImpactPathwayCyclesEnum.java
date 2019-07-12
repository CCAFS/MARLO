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
public enum ImpactPathwayCyclesEnum {

  IMPACT_PATHWAY(1), MONITORING(2);


  public static ImpactPathwayCyclesEnum getValue(long cycle) {
    ImpactPathwayCyclesEnum[] lst = ImpactPathwayCyclesEnum.values();
    for (ImpactPathwayCyclesEnum sectionStatusEnum : lst) {
      if (sectionStatusEnum.getId() == cycle) {
        return sectionStatusEnum;
      }
    }
    return null;
  }


  private long id;

  private ImpactPathwayCyclesEnum(long id) {
    this.id = id;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }


}
