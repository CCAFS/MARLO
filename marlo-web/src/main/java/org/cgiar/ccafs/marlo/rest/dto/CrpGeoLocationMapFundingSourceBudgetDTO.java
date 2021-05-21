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

public class CrpGeoLocationMapFundingSourceBudgetDTO {

  @ApiModelProperty(notes = "The Generated funding source id")
  private Long fundingSourceId;
  @ApiModelProperty(notes = "Funding source year mapped")
  private Long year;
  @ApiModelProperty(notes = "Ammount assigned")
  private Double amount;

  public CrpGeoLocationMapFundingSourceBudgetDTO() {
    super();
  }

  public CrpGeoLocationMapFundingSourceBudgetDTO(Long fundingSourceId, Long year, Double amount) {
    super();
    this.fundingSourceId = fundingSourceId;
    this.year = year;
    this.amount = amount;
  }

  public Double getAmount() {
    return amount;
  }

  public Long getFundingSourceId() {
    return fundingSourceId;
  }

  public Long getYear() {
    return year;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public void setFundingSourceId(Long fundingSourceId) {
    this.fundingSourceId = fundingSourceId;
  }

  public void setYear(Long year) {
    this.year = year;
  }


}
