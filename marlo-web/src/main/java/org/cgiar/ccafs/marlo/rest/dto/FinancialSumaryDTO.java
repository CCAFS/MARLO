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

/**************
 * @author Diego Perez - CIAT/CCAFS
 **************/

package org.cgiar.ccafs.marlo.rest.dto;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;

public class FinancialSumaryDTO {

  @ApiModelProperty(notes = "Financial Sumary ID", position = 1)
  private Long id;

  @ApiModelProperty(notes = "Financial summary narrative", position = 3)
  private String narrative;

  @ApiModelProperty(notes = "Flagship budget List", position = 4)
  private List<NewFinancialSummaryBudgetDTO> flagshipSummaryBudgets;


  @ApiModelProperty(notes = "Reporting year", position = 2)
  private int year;

  public List<NewFinancialSummaryBudgetDTO> getFlagshipSummaryBudgets() {
    return flagshipSummaryBudgets;
  }


  public String getNarrative() {
    return narrative;
  }


  public void setFlagshipSummaryBudgets(List<NewFinancialSummaryBudgetDTO> flagshipSummaryBudgets) {
    this.flagshipSummaryBudgets = flagshipSummaryBudgets;
  }


  public void setNarrative(String narrative) {
    this.narrative = narrative;
  }

}
