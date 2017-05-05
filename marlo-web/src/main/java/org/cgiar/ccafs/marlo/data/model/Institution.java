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
package org.cgiar.ccafs.marlo.data.model;

import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.annotations.Expose;

public class Institution implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = 3635585962414755020L;


  @Expose
  private Long id;

  @Expose
  private InstitutionType institutionType;


  @Expose
  private String name;


  @Expose
  private String acronym;

  @Expose
  private String city;

  @Expose
  private String websiteLink;
  @Expose
  private Long programId;


  @Expose
  private Date added;

  private Set<CrpPpaPartner> crpPpaPartners = new HashSet<CrpPpaPartner>(0);

  private Set<LiaisonInstitution> liaisonInstitutions = new HashSet<LiaisonInstitution>(0);

  private Set<ProjectPartner> projectPartners = new HashSet<ProjectPartner>(0);


  private Set<ProjectBudget> projectBudgets = new HashSet<ProjectBudget>(0);

  private Set<FundingSource> fundingSources = new HashSet<FundingSource>(0);


  private Set<Institution> branches = new HashSet<Institution>(0);


  private Set<ProjectPartnerPerson> projectPartnerPersons = new HashSet<>(0);
  private Set<InstitutionLocation> institutionsLocations = new HashSet<InstitutionLocation>(0);


  public Institution() {
  }


  public Institution(InstitutionType institutionType, String name, Date added) {
    this.institutionType = institutionType;
    this.name = name;
    this.added = added;
  }

  public Institution(InstitutionType institutionType, String name, String acronym, String city, String websiteLink,
    Long programId, Long countryId, Date added, Set<CrpPpaPartner> crpPpaPartners,
    Set<ProjectPartnerPerson> projectPartnerPersons, Set<FundingSource> fundingSources) {
    this.institutionType = institutionType;
    this.name = name;
    this.acronym = acronym;
    this.city = city;
    this.websiteLink = websiteLink;
    this.programId = programId;
    this.added = added;
    this.crpPpaPartners = crpPpaPartners;

    this.projectPartnerPersons = projectPartnerPersons;
    this.fundingSources = fundingSources;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    Institution other = (Institution) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    return true;
  }

  public String getAcronym() {
    return this.acronym;
  }

  public Date getAdded() {
    return this.added;
  }

  public Set<Institution> getBranches() {
    return branches;
  }
  /*
   * public String getBranchName() {
   * try {
   * String composedAcronym = this.acronym != null ? this.acronym : "";
   * if (this.headquarter == null) {
   * // Verify if there exist a city to show
   * if (this.city != null && this.city != "") {
   * return "HQ: " + composedAcronym + " - " + this.city + ", " + this.locElement.getName();
   * }
   * return "HQ: " + composedAcronym + " - " + this.locElement.getName();
   * } else {
   * // Verify if there exist a city to show
   * if (this.city != null && this.city != "") {
   * return composedAcronym + " - " + this.city + ", " + this.locElement.getName();
   * }
   * return composedAcronym + " - " + this.locElement.getName();
   * }
   * } catch (Exception e) {
   * return this.name;
   * }
   * }
   */

  public String getCity() {
    return this.city;
  }

  /*
   * public String getComposedLocation() {
   * try {
   * if (this.headquarter == null) {
   * // Verify if there exist a city to show
   * if (this.city != null && this.city != "") {
   * return this.city + ", " + this.locElement.getName();
   * }
   * return this.locElement.getName();
   * } else {
   * // Verify if there exist a city to show
   * if (this.city != null && this.city != "") {
   * return this.city + ", " + this.locElement.getName();
   * }
   * return this.locElement.getName();
   * }
   * } catch (Exception e) {
   * return this.name;
   * }
   * }
   */

  public String getComposedName() {
    if (this.getAcronym() != null) {
      if (this.getAcronym().length() != 0) {
        try {
          return this.getAcronym() + " - " + this.getName();
        } catch (Exception e) {
          return this.getName();
        }

      }
    }
    return this.getName();


  }

  public String getComposedNameLoc() {
    if (this.getAcronym() != null) {
      if (this.getAcronym().length() != 0) {
        try {
          return this.getAcronym() + " - " + this.getName();
        } catch (Exception e) {
          return this.getName();
        }

      }
    }
    return this.getName();
  }

  public Set<CrpPpaPartner> getCrpPpaPartners() {
    return crpPpaPartners;
  }

  public Set<FundingSource> getFundingSources() {
    return fundingSources;
  }


  @Override
  public Long getId() {
    return this.id;
  }

  public Set<InstitutionLocation> getInstitutionsLocations() {
    return institutionsLocations;
  }


  public InstitutionType getInstitutionType() {
    return institutionType;
  }

  public List<Institution> getInstitutuionsBranches() {
    List<Institution> list = new ArrayList<Institution>();
    list.add(this);
    list.addAll(this.getBranches());
    return list;
  }


  public Set<LiaisonInstitution> getLiaisonInstitutions() {
    return liaisonInstitutions;
  }


  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();

    sb.append("Id : ").append(this.getId());


    return sb.toString();
  }


  @Override
  public String getModificationJustification() {

    return "";
  }


  @Override
  public User getModifiedBy() {
    User u = new User();
    u.setId(new Long(3));
    return null;
  }

  public String getName() {
    return this.name;
  }

  public Long getProgramId() {
    return this.programId;
  }

  public Set<ProjectBudget> getProjectBudgets() {
    return projectBudgets;
  }

  public Set<ProjectPartnerPerson> getProjectPartnerPersons() {
    return projectPartnerPersons;
  }

  public Set<ProjectPartner> getProjectPartners() {
    return projectPartners;
  }

  public String getWebsiteLink() {
    return this.websiteLink;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  @Override
  public boolean isActive() {
    return true;
  }

  public boolean isPPA(long crpID) {
    if (this.getCrpPpaPartners().stream().filter(c -> c.getCrp().getId().longValue() == crpID && c.isActive())
      .collect(Collectors.toList()).size() > 0) {
      return true;
    }
    return false;
  }

  public void setAcronym(String acronym) {
    this.acronym = acronym;
  }

  public void setAdded(Date added) {
    this.added = added;
  }

  public void setBranches(Set<Institution> branches) {
    this.branches = branches;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public void setCrpPpaPartners(Set<CrpPpaPartner> crpPpaPartners) {
    this.crpPpaPartners = crpPpaPartners;
  }

  public void setFundingSources(Set<FundingSource> fundingSources) {
    this.fundingSources = fundingSources;
  }


  public void setId(Long id) {
    this.id = id;
  }

  public void setInstitutionsLocations(Set<InstitutionLocation> institutionsLocations) {
    this.institutionsLocations = institutionsLocations;
  }

  public void setInstitutionType(InstitutionType institutionType) {
    this.institutionType = institutionType;
  }

  public void setLiaisonInstitutions(Set<LiaisonInstitution> liaisonInstitutions) {
    this.liaisonInstitutions = liaisonInstitutions;
  }


  public void setName(String name) {
    this.name = name;
  }

  public void setProgramId(Long programId) {
    this.programId = programId;
  }

  public void setProjectBudgets(Set<ProjectBudget> projectBudgets) {
    this.projectBudgets = projectBudgets;
  }

  public void setProjectPartnerPersons(Set<ProjectPartnerPerson> projectPartnerPersons) {
    this.projectPartnerPersons = projectPartnerPersons;
  }

  public void setProjectPartners(Set<ProjectPartner> projectPartners) {
    this.projectPartners = projectPartners;
  }

  public void setWebsiteLink(String websiteLink) {
    this.websiteLink = websiteLink;
  }

  @Override
  public String toString() {
    return id.toString();
  }
}

