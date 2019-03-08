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
 * @author Andrés Valencia - CIAT/CCAFS
 */
public enum ReportSynthesis2018SectionStatusEnum {


  CRP_PROGRESS("crpProgress"), FLAGSHIP_PROGRESS("flagshipProgress"), POLICIES("policies"), OICR("oicr"),
  INNOVATIONS("innovations"), OUTOMESMILESTONES("outomesMilestones"), PUBLICATIONS("publications"),
  CC_DIMENSIONS("ccDimensions"), GOVERNANCE("governance"), EXTERNAL_PARTNERSHIPS("externalPartnerships"),
  INTELLECTUAL_ASSETS("intellectualAssets"), MELIA("melia"), EFFICIENCY("efficiency"), RISKS("risks"),
  FUNDING_USE("fundingUse"), FINANCIAL("financial"), INFLUENCE("influence"), CONTROL("control"), NARRATIVE("narrative");


  public static ReportSynthesis2018SectionStatusEnum value(String status) {
    ReportSynthesis2018SectionStatusEnum[] lst = ReportSynthesis2018SectionStatusEnum.values();
    for (ReportSynthesis2018SectionStatusEnum reportSynthesisSectionStatusEnum : lst) {
      if (reportSynthesisSectionStatusEnum.getStatus().equalsIgnoreCase(status)) {
        return reportSynthesisSectionStatusEnum;
      }
    }
    return null;
  }

  private String status;

  private ReportSynthesis2018SectionStatusEnum(String status) {
    this.status = status;
  }

  public String getStatus() {
    return status;
  }

}
