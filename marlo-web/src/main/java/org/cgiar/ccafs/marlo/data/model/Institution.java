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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
  private LocElement locElement;

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


  public Institution() {
  }


  public Institution(InstitutionType institutionType, String name, Date added) {
    this.institutionType = institutionType;
    this.name = name;
    this.added = added;
  }


  public Institution(InstitutionType institutionType, String name, String acronym, String city, String websiteLink,
    Long programId, Long countryId, Date added, Set<CrpPpaPartner> crpPpaPartners, LocElement locElement) {
    this.institutionType = institutionType;
    this.name = name;
    this.acronym = acronym;
    this.city = city;
    this.websiteLink = websiteLink;
    this.programId = programId;
    this.added = added;
    this.crpPpaPartners = crpPpaPartners;
    this.locElement = locElement;
  }


  public String getAcronym() {
    return this.acronym;
  }

  public Date getAdded() {
    return this.added;
  }

  public String getCity() {
    return this.city;
  }

  public String getComposedName() {
    try {
      if (this.getLocElement() == null) {
        return this.getAcronym() + " - " + this.getName();
      }

      if (this.getLocElement().getName() == null) {
        this.getLocElement().setName("");
      }
      if (this.getAcronym() != null) {
        if (this.getAcronym().length() != 0) {
          try {
            return this.getAcronym() + " - " + this.getName() + " - " + this.getLocElement().getName();
          } catch (Exception e) {
            return this.getAcronym() + " - " + this.getName();
          }

        }
      }
      try {
        return this.getName() + "-" + this.getLocElement().getName();
      } catch (Exception e) {
        return this.getName();
      }
    } catch (Exception e) {
      return this.getAcronym() + " - " + this.getName();
    }


  }

  public Set<CrpPpaPartner> getCrpPpaPartners() {
    return crpPpaPartners;
  }

  @Override
  public Long getId() {
    return this.id;
  }

  public InstitutionType getInstitutionType() {
    return institutionType;
  }

  public Set<LiaisonInstitution> getLiaisonInstitutions() {
    return liaisonInstitutions;
  }

  public LocElement getLocElement() {
    return locElement;
  }

  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();

    sb.append("Id : ").append(this.getId());


    return sb.toString();
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

  public Set<ProjectPartner> getProjectPartners() {
    return projectPartners;
  }

  public String getWebsiteLink() {
    return this.websiteLink;
  }

  @Override
  public boolean isActive() {
    return true;
  }

  public void setAcronym(String acronym) {
    this.acronym = acronym;
  }

  public void setAdded(Date added) {
    this.added = added;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public void setCrpPpaPartners(Set<CrpPpaPartner> crpPpaPartners) {
    this.crpPpaPartners = crpPpaPartners;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setInstitutionType(InstitutionType institutionType) {
    this.institutionType = institutionType;
  }

  public void setLiaisonInstitutions(Set<LiaisonInstitution> liaisonInstitutions) {
    this.liaisonInstitutions = liaisonInstitutions;
  }

  public void setLocElement(LocElement locElement) {
    this.locElement = locElement;
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

