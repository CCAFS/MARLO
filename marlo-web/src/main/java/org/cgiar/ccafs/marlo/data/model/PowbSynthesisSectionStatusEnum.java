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
public enum PowbSynthesisSectionStatusEnum {

  TOC_ADJUSTMENTS("adjustmentsChanges"), CRP_PROGRESS("expectedProgress"), EVIDENCES("evidenceRelevant"),
  FLAGSHIP_PLANS("plansByFlagship"), CROSS_CUTTING_DIMENSIONS("crossCuttingDimensions"), STAFFING("crpStaffing"),
  FINANCIAL_PLAN("financialPlan"), COLABORATION_INTEGRATION("collaborationIntegration"), MEL("mel"),
  COLLABORATION("collaborationIntegration"), MANAGEMENT_RISK("managementRisks"),
  MANAGEMENT_GOVERNANCE("managementGovernance");

  public static PowbSynthesisSectionStatusEnum value(String status) {
    PowbSynthesisSectionStatusEnum[] lst = PowbSynthesisSectionStatusEnum.values();
    for (PowbSynthesisSectionStatusEnum powbSynthesisSectionStatusEnum : lst) {
      if (powbSynthesisSectionStatusEnum.getStatus().equalsIgnoreCase(status)) {
        return powbSynthesisSectionStatusEnum;
      }
    }
    return null;
  }

  private String status;

  private PowbSynthesisSectionStatusEnum(String status) {
    this.status = status;
  }

  public String getStatus() {
    return status;
  }

}
