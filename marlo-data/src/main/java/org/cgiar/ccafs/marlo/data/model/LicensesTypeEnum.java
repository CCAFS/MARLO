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
public enum LicensesTypeEnum {
  MIT("MIT"), GNU("GNU"), CC_LICENSES("CC_LICENSES"), CC_PUBLIC("CC_PUBLIC"), OPEN_DATA("OPEN_DATA"), CC_BY("CC_BY"),
  CC_BY_SA("CC_BY_SA"), CC_BY_ND("CC_BY_ND"), CC_BY_NC("CC_BY_NC"), CC_BY_NC_SA("CC_BY_NC_SA"),
  CC_BY_NC_ND("CC_BY_NC_ND"), OTHER("OTHER");

  public static LicensesTypeEnum license(String status) {
    LicensesTypeEnum[] lst = LicensesTypeEnum.values();
    for (LicensesTypeEnum licensesTypeEnum : lst) {
      if (licensesTypeEnum.getValue().equalsIgnoreCase(status)) {
        return licensesTypeEnum;
      }
    }
    return null;
  }

  private String value;

  private LicensesTypeEnum(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
