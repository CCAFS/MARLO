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

package org.cgiar.ccafs.marlo.action.funding.dto;

import java.math.BigInteger;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This is used for the Funding Source Search popup dialog
 * 
 * @author GrantL
 */
public class FundingSourceSearchSummary {

  private BigInteger id;

  private String name;

  private String type;

  private BigInteger typeId;

  private String financeCode;

  private Boolean w1w2;

  /**
   * This needs to be determined by the securityContext in some cases where the specificity applies
   */
  private boolean canSelect = true;

  private Double budget = new Double(0);

  private Double usedAmount = new Double(0);

  public FundingSourceSearchSummary() {

  }


  public Map<String, Object> convertToMap() {
    ObjectMapper oMapper = new ObjectMapper();

    Map<String, Object> map = oMapper.convertValue(this, Map.class);

    return map;

  }


  public Double getAmount() {
    return budget - usedAmount;
  }

  public Double getBudget() {
    return budget;
  }


  public String getFinanceCode() {
    return financeCode;
  }


  public BigInteger getId() {
    return id;
  }


  public String getName() {
    return name;
  }


  public String getType() {
    return type;
  }


  public BigInteger getTypeId() {
    return typeId;
  }


  public Double getUsedAmount() {
    return usedAmount;
  }


  public Boolean getW1w2() {
    return w1w2;
  }


  public boolean isCanSelect() {
    return canSelect;
  }


  public void setBudget(Double budget) {

    if (budget == null) {
      this.budget = 0.0D;
      return;
    }
    this.budget = budget;
  }


  public void setCanSelect(boolean canSelect) {
    this.canSelect = canSelect;
  }


  public void setFinanceCode(String financeCode) {
    this.financeCode = financeCode;
  }


  public void setId(BigInteger id) {
    this.id = id;
  }


  public void setName(String name) {
    this.name = name;
  }


  public void setType(String type) {
    this.type = type;
  }


  public void setTypeId(BigInteger typeId) {
    this.typeId = typeId;
  }


  public void setUsedAmount(Double usedAmount) {
    if (usedAmount == null) {
      this.usedAmount = 0.0D;
      return;
    }
    this.usedAmount = usedAmount;
  }


  public void setW1w2(Boolean w1w2) {
    this.w1w2 = w1w2;
  }


}
