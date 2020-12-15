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

public class NewFinancialSummaryDTO {


  @ApiModelProperty(notes = "Financial summary narrative", position = 2)
  private String narrative;

  @ApiModelProperty(notes = "Flagship budget List", position = 3)
  private List<NewFinancialSummaryBudgetDTO> flagshipSummaryBudgets;

  @ApiModelProperty(notes = "Strategic competitive research grant", position = 4)
  private NewFinancialSummaryBudgetDTO strategicCompetitiveResearchGrant;


  @ApiModelProperty(notes = "CRP Management and Support Cost", position = 5)
  private NewFinancialSummaryBudgetDTO crpManagementSupportCost;


  @ApiModelProperty(notes = "Phase identifier", position = 1)
  private PhaseDTO phase;


  public NewFinancialSummaryBudgetDTO getCrpManagementSupportCost() {
    return crpManagementSupportCost;
  }


  public List<NewFinancialSummaryBudgetDTO> getFlagshipSummaryBudgets() {
    return flagshipSummaryBudgets;
  }


  public String getNarrative() {
    return narrative;
  }


  public PhaseDTO getPhase() {
    return phase;
  }


  public NewFinancialSummaryBudgetDTO getStrategicCompetitiveResearchGrant() {
    return strategicCompetitiveResearchGrant;
  }


  public void setCrpManagementSupportCost(NewFinancialSummaryBudgetDTO crpManagementSupportCost) {
    this.crpManagementSupportCost = crpManagementSupportCost;
  }


  public void setFlagshipSummaryBudgets(List<NewFinancialSummaryBudgetDTO> flagshipSummaryBudgets) {
    this.flagshipSummaryBudgets = flagshipSummaryBudgets;
  }


  public void setNarrative(String narrative) {
    this.narrative = narrative;
  }


  public void setPhase(PhaseDTO phase) {
    this.phase = phase;
  }


  public void setStrategicCompetitiveResearchGrant(NewFinancialSummaryBudgetDTO strategicCompetitiveResearchGrant) {
    this.strategicCompetitiveResearchGrant = strategicCompetitiveResearchGrant;
  }

}
