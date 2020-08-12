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

import io.swagger.annotations.ApiModelProperty;

public class NewW1W2ExpenditureDTO {

  @ApiModelProperty(notes = "Expenditure example summary", position = 1)
  private String exampleExpenditure;

  @ApiModelProperty(notes = "Expenditure Area", position = 2)
  private Long expenditureAreaID;

  @ApiModelProperty(notes = "Phase POWB/AR", position = 3)
  private PhaseDTO phase;


  public String getExampleExpenditure() {
    return exampleExpenditure;
  }


  public Long getExpenditureAreaID() {
    return expenditureAreaID;
  }


  public PhaseDTO getPhase() {
    return phase;
  }


  public void setExampleExpenditure(String exampleExpenditure) {
    this.exampleExpenditure = exampleExpenditure;
  }


  public void setExpenditureAreaID(Long expenditureAreaID) {
    this.expenditureAreaID = expenditureAreaID;
  }


  public void setPhase(PhaseDTO phase) {
    this.phase = phase;
  }


}
