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

public class W1W2ExpenditureDTO {

  @ApiModelProperty(notes = "Expenditure example ID", position = 1)
  private Long expenditureID;

  @ApiModelProperty(notes = "Expenditure example summary", position = 2)
  private String exampleExpenditure;

  @ApiModelProperty(notes = "Expenditure Area", position = 3)
  private DefaultFieldDTO expenditureArea;


  public String getExampleExpenditure() {
    return exampleExpenditure;
  }


  public DefaultFieldDTO getExpenditureArea() {
    return expenditureArea;
  }


  public Long getExpenditureID() {
    return expenditureID;
  }


  public void setExampleExpenditure(String exampleExpenditure) {
    this.exampleExpenditure = exampleExpenditure;
  }


  public void setExpenditureArea(DefaultFieldDTO expenditureArea) {
    this.expenditureArea = expenditureArea;
  }


  public void setExpenditureID(Long expenditureID) {
    this.expenditureID = expenditureID;
  }
}
