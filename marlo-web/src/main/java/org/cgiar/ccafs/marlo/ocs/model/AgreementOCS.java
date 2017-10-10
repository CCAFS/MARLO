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


package org.cgiar.ccafs.marlo.ocs.model;

import java.util.Date;
import java.util.List;

import org.apache.struts2.json.annotations.JSON;

public class AgreementOCS {


  private String id;
  private String description;
  private DonorOCS originalDonor;
  private DonorOCS directDonor;
  private List<CountryOCS> countries;
  private List<CrpOCS> crps;
  private ResearcherOCS researcher;
  private String shortTitle;
  private String objectives;
  private String grantAmount;
  private Date startDate;
  private Date endDate;
  private Date extensionDate;
  private String contractStatus;
  private String fundingType;
  private List<PlaOCS> plas;

  public String getContractStatus() {
    return contractStatus;
  }

  public List<CountryOCS> getCountries() {
    return countries;
  }

  public List<CrpOCS> getCrps() {
    return crps;
  }

  public String getDescription() {
    return description;
  }


  public DonorOCS getDirectDonor() {
    return directDonor;
  }


  @JSON(format = "yyyy-MM-dd")
  public Date getEndDate() {
    return endDate;
  }


  @JSON(format = "yyyy-MM-dd")
  public Date getExtensionDate() {
    try {
      return extensionDate;
    } catch (Exception e) {
      return null;
    }
  }


  public String getFundingType() {
    return fundingType;
  }


  public String getGrantAmount() {
    return grantAmount;
  }

  public String getId() {
    return id;
  }


  public String getObjectives() {
    return objectives;
  }


  public DonorOCS getOriginalDonor() {
    return originalDonor;
  }


  public List<PlaOCS> getPlas() {
    return plas;
  }


  public ResearcherOCS getResearcher() {
    return researcher;
  }


  public String getShortTitle() {
    return shortTitle;
  }


  @JSON(format = "yyyy-MM-dd")
  public Date getStartDate() {
    return startDate;
  }


  public void setContractStatus(String contractStatus) {
    this.contractStatus = contractStatus;
  }

  public void setCountries(List<CountryOCS> countries) {
    this.countries = countries;
  }


  public void setCrps(List<CrpOCS> crps) {
    this.crps = crps;
  }


  public void setDescription(String description) {
    this.description = description;
  }


  public void setDirectDonor(DonorOCS directDonor) {
    this.directDonor = directDonor;
  }


  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }


  public void setExtensionDate(Date extensionDate) {
    this.extensionDate = extensionDate;
  }


  public void setFundingType(String fundingType) {
    this.fundingType = fundingType;
  }


  public void setGrantAmount(String grantAmount) {
    this.grantAmount = grantAmount;
  }


  public void setId(String id) {
    this.id = id;
  }


  public void setObjectives(String objectives) {
    this.objectives = objectives;
  }


  public void setOriginalDonor(DonorOCS originalDonor) {
    this.originalDonor = originalDonor;
  }

  public void setPlas(List<PlaOCS> plas) {
    this.plas = plas;
  }

  public void setResearcher(ResearcherOCS researcher) {
    this.researcher = researcher;
  }

  public void setShortTitle(String shortTitle) {
    this.shortTitle = shortTitle;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }


}