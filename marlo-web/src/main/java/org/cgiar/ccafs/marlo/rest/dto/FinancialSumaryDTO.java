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
  private List<FinancialSummaryBudgetDTO> flagshipSummaryBudgets;

  @ApiModelProperty(notes = "Strategic competitive research grant", position = 5)
  private FinancialSummaryBudgetAreaDTO strategicCompetitiveResearchGrant;


  @ApiModelProperty(notes = "CRP Management and Support Cost", position = 6)
  private FinancialSummaryBudgetAreaDTO crpManagementSupportCost;


  @ApiModelProperty(notes = "Reporting year", position = 7)
  private int year;

  public FinancialSummaryBudgetAreaDTO getCrpManagementSupportCost() {
    return crpManagementSupportCost;
  }


  public List<FinancialSummaryBudgetDTO> getFlagshipSummaryBudgets() {
    return flagshipSummaryBudgets;
  }


  public Long getId() {
    return id;
  }


  public String getNarrative() {
    return narrative;
  }


  public FinancialSummaryBudgetAreaDTO getStrategicCompetitiveResearchGrant() {
    return strategicCompetitiveResearchGrant;
  }


  public int getYear() {
    return year;
  }


  public void setCrpManagementSupportCost(FinancialSummaryBudgetAreaDTO crpManagementSupportCost) {
    this.crpManagementSupportCost = crpManagementSupportCost;
  }


  public void setFlagshipSummaryBudgets(List<FinancialSummaryBudgetDTO> flagshipSummaryBudgets) {
    this.flagshipSummaryBudgets = flagshipSummaryBudgets;
  }


  public void setId(Long id) {
    this.id = id;
  }


  public void setNarrative(String narrative) {
    this.narrative = narrative;
  }


  public void setStrategicCompetitiveResearchGrant(FinancialSummaryBudgetAreaDTO strategicCompetitiveResearchGrant) {
    this.strategicCompetitiveResearchGrant = strategicCompetitiveResearchGrant;
  }


  public void setYear(int year) {
    this.year = year;
  }


}
