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

public class NewFinancialSummaryBudgetDTO {

  @ApiModelProperty(notes = "Flagship program identifier")
  private long flagshipID;

  @ApiModelProperty(notes = "Planned Budget W1/W2 value")
  private double plannedBudgetW1W2;

  @ApiModelProperty(notes = "Planned Budget W3 and Biltareal value")
  private double plannedBudgetW3Bilateral;

  @ApiModelProperty(notes = "Actual expenditure for W1/W2")
  private double actualExpenditureW1W2;

  @ApiModelProperty(notes = "Actual expenditure for W3 and Bilateral")
  private double actualExpendituretW3Bilateral;

  @ApiModelProperty(notes = "Commments")
  private String comments;


  public double getActualExpendituretW3Bilateral() {
    return actualExpendituretW3Bilateral;
  }


  public double getActualExpenditureW1W2() {
    return actualExpenditureW1W2;
  }


  public String getComments() {
    return comments;
  }


  public long getFlagshipID() {
    return flagshipID;
  }


  public double getPlannedBudgetW1W2() {
    return plannedBudgetW1W2;
  }


  public double getPlannedBudgetW3Bilateral() {
    return plannedBudgetW3Bilateral;
  }


  public void setActualExpendituretW3Bilateral(double actualExpendituretW3Bilateral) {
    this.actualExpendituretW3Bilateral = actualExpendituretW3Bilateral;
  }


  public void setActualExpenditureW1W2(double actualExpenditureW1W2) {
    this.actualExpenditureW1W2 = actualExpenditureW1W2;
  }


  public void setComments(String comments) {
    this.comments = comments;
  }


  public void setFlagshipID(long flagshipID) {
    this.flagshipID = flagshipID;
  }


  public void setPlannedBudgetW1W2(double plannedBudgetW1W2) {
    this.plannedBudgetW1W2 = plannedBudgetW1W2;
  }


  public void setPlannedBudgetW3Bilateral(double plannedBudgetW3Bilateral) {
    this.plannedBudgetW3Bilateral = plannedBudgetW3Bilateral;
  }

}
