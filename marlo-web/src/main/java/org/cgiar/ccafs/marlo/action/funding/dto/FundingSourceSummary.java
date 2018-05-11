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

import java.util.List;

import com.google.gson.annotations.Expose;

/**
 * This is a read-only view of the FundingSource which significantly improves the performance of
 * displaying the FundingSource data especially when needing to display a list of FundingSources.
 * Only the data that needs to be displayed should be included within this object.
 * 
 * @author GrantL
 */
public class FundingSourceSummary {

  @Expose
  private Long id;

  private FundingSourceInfoSummary fundingSourceInfo;

  @Expose
  private List<FundingSourceInstitutionSummary> institutions;

  @Expose
  private boolean hasRequiredFields;

  @Expose
  private Double fundingSourceBudgetPerPhase;


  public FundingSourceSummary() {
  }


  public FundingSourceInfoSummary getFundingSourceInfo() {
    return fundingSourceInfo;
  }


  public Long getId() {
    return id;
  }


  public List<FundingSourceInstitutionSummary> getInstitutions() {
    return institutions;
  }


  public Double isFundingSourceBudgetPerPhase() {
    return fundingSourceBudgetPerPhase;
  }

  public boolean isHasRequiredFields() {
    return hasRequiredFields;
  }


  public void setFundingSourceBudgetPerPhase(Double fundingSourceBudgetPerPhase) {
    this.fundingSourceBudgetPerPhase = fundingSourceBudgetPerPhase;
  }


  public void setFundingSourceInfo(FundingSourceInfoSummary fundingSourceInfo) {
    this.fundingSourceInfo = fundingSourceInfo;
  }


  public void setHasRequiredFields(boolean hasRequiredFields) {
    this.hasRequiredFields = hasRequiredFields;
  }


  public void setId(Long id) {
    this.id = id;
  }


  public void setInstitutions(List<FundingSourceInstitutionSummary> institutions) {
    this.institutions = institutions;
  }


}
