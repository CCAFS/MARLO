/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/
package org.cgiar.ccafs.marlo.data.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.annotations.Expose;

public class Institution implements java.io.Serializable {


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
  private Long countryId;
  @Expose
  private Date added;
  private Set<CrpPpaPartner> crpPpaPartners = new HashSet<CrpPpaPartner>(0);

  public Institution() {
  }

  public Institution(InstitutionType institutionType, String name, Date added) {
    this.institutionType = institutionType;
    this.name = name;
    this.added = added;
  }

  public Institution(InstitutionType institutionType, String name, String acronym, String city, String websiteLink,
    Long programId, Long countryId, Date added, Set<CrpPpaPartner> crpPpaPartners) {
    this.institutionType = institutionType;
    this.name = name;
    this.acronym = acronym;
    this.city = city;
    this.websiteLink = websiteLink;
    this.programId = programId;
    this.countryId = countryId;
    this.added = added;
    this.crpPpaPartners = crpPpaPartners;
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
    if (this.getAcronym() != null) {

      return acronym + " - " + name;
    }
    return name;
  }

  public Long getCountryId() {
    return this.countryId;
  }

  public Set<CrpPpaPartner> getCrpPpaPartners() {
    return crpPpaPartners;
  }

  public Long getId() {
    return this.id;
  }

  public InstitutionType getInstitutionType() {
    return institutionType;
  }

  public String getName() {
    return this.name;
  }

  public Long getProgramId() {
    return this.programId;
  }

  public String getWebsiteLink() {
    return this.websiteLink;
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

  public void setCountryId(Long countryId) {
    this.countryId = countryId;
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

  public void setName(String name) {
    this.name = name;
  }

  public void setProgramId(Long programId) {
    this.programId = programId;
  }

  public void setWebsiteLink(String websiteLink) {
    this.websiteLink = websiteLink;
  }

}

