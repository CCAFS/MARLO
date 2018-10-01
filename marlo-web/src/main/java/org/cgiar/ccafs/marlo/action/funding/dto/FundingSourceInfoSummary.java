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

import java.util.Date;

import com.google.gson.annotations.Expose;

public class FundingSourceInfoSummary {

  @Expose
  private String title;

  @Expose
  private String budgetTypeName;

  @Expose
  private String financeCode;

  @Expose
  private Date startDate;

  @Expose
  private Date endDate;

  @Expose
  private Date extensionDate;

  @Expose
  private String directDonorName;

  @Expose
  private String originalDonorName;

  @Expose
  private String directDonorAcronym;


  @Expose
  private String originalDonorAcronym;


  @Expose
  private Date syncedDate;


  @Expose
  private Boolean synced;


  @Expose
  private Boolean w1w2;


  @Expose
  private Integer status;

  public FundingSourceInfoSummary() {
  }

  public String getBudgetTypeName() {
    return budgetTypeName;
  }

  public String getDirectDonorAcronym() {
    return directDonorAcronym;
  }

  public String getDirectDonorName() {
    return directDonorName;
  }


  public Date getEndDate() {
    return endDate;
  }


  public Date getExtensionDate() {
    return extensionDate;
  }


  public String getFinanceCode() {
    return financeCode;
  }


  public String getOriginalDonorAcronym() {
    return originalDonorAcronym;
  }


  public String getOriginalDonorName() {
    return originalDonorName;
  }


  public Date getStartDate() {
    return startDate;
  }


  public Integer getStatus() {
    return status;
  }


  public Boolean getSynced() {
    return synced;
  }


  public Date getSyncedDate() {
    return syncedDate;
  }


  public String getTitle() {
    return title;
  }


  public Boolean getW1w2() {
    return w1w2;
  }


  public void setBudgetTypeName(String budgetTypeName) {
    this.budgetTypeName = budgetTypeName;
  }


  public void setDirectDonorAcronym(String directDonorAcronym) {
    this.directDonorAcronym = directDonorAcronym;
  }


  public void setDirectDonorName(String directDonorName) {
    this.directDonorName = directDonorName;
  }


  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }


  public void setExtensionDate(Date extensionDate) {
    this.extensionDate = extensionDate;
  }


  public void setFinanceCode(String financeCode) {
    this.financeCode = financeCode;
  }


  public void setOriginalDonorAcronym(String originalDonorAcronym) {
    this.originalDonorAcronym = originalDonorAcronym;
  }


  public void setOriginalDonorName(String originalDonorName) {
    this.originalDonorName = originalDonorName;
  }


  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }


  public void setStatus(Integer status) {
    this.status = status;
  }


  public void setSynced(Boolean synced) {
    this.synced = synced;
  }


  public void setSyncedDate(Date syncedDate) {
    this.syncedDate = syncedDate;
  }


  public void setTitle(String title) {
    this.title = title;
  }


  public void setW1w2(Boolean w1w2) {
    this.w1w2 = w1w2;
  }

}
