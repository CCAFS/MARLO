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

public class CrpGeoLocationMapDTO {

  private String country;
  private String countryIso;
  private String countryIsoAlpha2;

  private List<CrpGeoLocationMapDeliverablesDTO> deliverables;
  private List<CrpGeoLocationMapPartnersDTO> partners;
  private List<CrpGeoLocationMapProjectsDTO> projects;
  private List<CrpGeoLocationMapInnovationsDTO> innovations;
  private List<CrpGeoLocationMapFundingSourcesDTO> fundingSources;
  private List<CrpGeoLocationMapOICRDTO> expectedStudies;


  public String getCountry() {
    return country;
  }


  public String getCountryIso() {
    return countryIso;
  }


  public String getCountryIsoAlpha2() {
    return countryIsoAlpha2;
  }


  public List<CrpGeoLocationMapDeliverablesDTO> getDeliverables() {
    return deliverables;
  }

  public List<CrpGeoLocationMapOICRDTO> getExpectedStudies() {
    return expectedStudies;
  }

  public List<CrpGeoLocationMapFundingSourcesDTO> getFundingSources() {
    return fundingSources;
  }

  public List<CrpGeoLocationMapInnovationsDTO> getInnovations() {
    return innovations;
  }

  public List<CrpGeoLocationMapPartnersDTO> getPartners() {
    return partners;
  }

  public List<CrpGeoLocationMapProjectsDTO> getProjects() {
    return projects;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public void setCountryIso(String countryIso) {
    this.countryIso = countryIso;
  }

  public void setCountryIsoAlpha2(String countryIsoAlpha2) {
    this.countryIsoAlpha2 = countryIsoAlpha2;
  }

  public void setDeliverables(List<CrpGeoLocationMapDeliverablesDTO> deliverables) {
    this.deliverables = deliverables;
  }

  public void setExpectedStudies(List<CrpGeoLocationMapOICRDTO> expectedStudies) {
    this.expectedStudies = expectedStudies;
  }

  public void setFundingSources(List<CrpGeoLocationMapFundingSourcesDTO> fundingSources) {
    this.fundingSources = fundingSources;
  }

  public void setInnovations(List<CrpGeoLocationMapInnovationsDTO> innovations) {
    this.innovations = innovations;
  }

  public void setPartners(List<CrpGeoLocationMapPartnersDTO> partners) {
    this.partners = partners;
  }

  public void setProjects(List<CrpGeoLocationMapProjectsDTO> projects) {
    this.projects = projects;
  }


}
