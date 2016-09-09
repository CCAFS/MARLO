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


package org.cgiar.ccafs.marlo.action.json.project;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.Date;

import com.google.inject.Inject;

public class ProjectBilateralCofinancingAddAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = 6371525009473087268L;


  private String title;
  private Date startDate;
  private Date endDate;
  private Integer agreement;
  private Long budget;
  private int participation;
  private String financeCode;
  private long donnor;
  private long createdBy;
  private String contactPerson;
  private String contactEmail;


  @Inject
  public ProjectBilateralCofinancingAddAction(APConfig config) {
    super(config);
    // TODO Auto-generated constructor stub
  }

  @Override
  public String execute() throws Exception {
    // TODO Auto-generated method stub
    return super.execute();
  }

  public Integer getAgreement() {
    return agreement;
  }


  public Long getBudget() {
    return budget;
  }


  public String getContactEmail() {
    return contactEmail;
  }


  public String getContactPerson() {
    return contactPerson;
  }


  public long getCreatedBy() {
    return createdBy;
  }


  public long getDonnor() {
    return donnor;
  }


  public Date getEndDate() {
    return endDate;
  }


  public String getFinanceCode() {
    return financeCode;
  }


  public int getParticipation() {
    return participation;
  }


  public Date getStartDate() {
    return startDate;
  }


  public String getTitle() {
    return title;
  }


  public void setAgreement(Integer agreement) {
    this.agreement = agreement;
  }


  public void setBudget(Long budget) {
    this.budget = budget;
  }


  public void setContactEmail(String contactEmail) {
    this.contactEmail = contactEmail;
  }


  public void setContactPerson(String contactPerson) {
    this.contactPerson = contactPerson;
  }


  public void setCreatedBy(long createdBy) {
    this.createdBy = createdBy;
  }


  public void setDonnor(long donnor) {
    this.donnor = donnor;
  }


  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }


  public void setFinanceCode(String financeCode) {
    this.financeCode = financeCode;
  }


  public void setParticipation(int participation) {
    this.participation = participation;
  }


  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public void setTitle(String title) {
    this.title = title;
  }

}
