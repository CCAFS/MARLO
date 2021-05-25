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

public class CrpGeoLocationMapFundingSourcesDTO {

  private long id;
  private String title;
  private String type;
  private long totalBudget;
  private String directDonor;
  private String originalDonor;
  private List<CrpGeoLocationMapFundingSourceBudgetDTO> budgetList;


  public List<CrpGeoLocationMapFundingSourceBudgetDTO> getBudgetList() {
    return budgetList;
  }


  public String getDirectDonor() {
    return directDonor;
  }


  public long getId() {
    return id;
  }


  public String getOriginalDonor() {
    return originalDonor;
  }


  public String getTitle() {
    return title;
  }


  public long getTotalBudget() {
    return totalBudget;
  }


  public String getType() {
    return type;
  }


  public void setBudgetList(List<CrpGeoLocationMapFundingSourceBudgetDTO> budgetList) {
    this.budgetList = budgetList;
  }


  public void setDirectDonor(String directDonor) {
    this.directDonor = directDonor;
  }


  public void setId(long id) {
    this.id = id;
  }

  public void setOriginalDonor(String originalDonor) {
    this.originalDonor = originalDonor;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setTotalBudget(long totalBudget) {
    this.totalBudget = totalBudget;
  }

  public void setType(String type) {
    this.type = type;
  }

}
