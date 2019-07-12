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
public enum PowbSynthesis2019SectionStatusEnum {

  TOC("adjustmentsChanges"), PROGRAM_CHANGES("programChanges"), PROGRESS_OUTCOMES("progressOutcomes"),
  PLANNED_STUDIES("plannedStudies"), PLANNED_COLLABORATIONS("plannedCollaborations"), PLANNED_BUDGET("plannedBudget");

  public static PowbSynthesis2019SectionStatusEnum value(String status) {
    PowbSynthesis2019SectionStatusEnum[] lst = PowbSynthesis2019SectionStatusEnum.values();
    for (PowbSynthesis2019SectionStatusEnum powbSynthesisSectionStatusEnum : lst) {
      if (powbSynthesisSectionStatusEnum.getStatus().equalsIgnoreCase(status)) {
        return powbSynthesisSectionStatusEnum;
      }
    }
    return null;
  }

  private String status;

  private PowbSynthesis2019SectionStatusEnum(String status) {
    this.status = status;
  }

  public String getStatus() {
    return status;
  }

}
