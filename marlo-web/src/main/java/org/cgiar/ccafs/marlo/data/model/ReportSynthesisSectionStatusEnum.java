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
public enum ReportSynthesisSectionStatusEnum {


  CRP_PROGRESS("crpProgress"), GOVERNANCE("governance"), FINANCIAL_SUMMARY("financial"), RISKS("risks"),
  CROSS_CUTTING("ccDimensions"), FLAGSHIP_PROGRESS("flagshipProgress"), VARIANCE("plannedVariance"),
  FUNDING_USE("fundingUse"), EXTERNAL_PARTNERSHIP("externalPartnerships"), EFFICIENCY("efficiency"),
  INFLUENCE("influence"), CONTROL("control"), CROSS_CGIAR("crossPartnerships"), MELIA("melia");

  public static ReportSynthesisSectionStatusEnum value(String status) {
    ReportSynthesisSectionStatusEnum[] lst = ReportSynthesisSectionStatusEnum.values();
    for (ReportSynthesisSectionStatusEnum reportSynthesisSectionStatusEnum : lst) {
      if (reportSynthesisSectionStatusEnum.getStatus().equalsIgnoreCase(status)) {
        return reportSynthesisSectionStatusEnum;
      }
    }
    return null;
  }

  private String status;

  private ReportSynthesisSectionStatusEnum(String status) {
    this.status = status;
  }

  public String getStatus() {
    return status;
  }

}
