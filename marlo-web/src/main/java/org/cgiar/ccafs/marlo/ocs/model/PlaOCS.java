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

import java.util.List;

public class PlaOCS {

  private String id;

  private String description;
  private List<PartnerOCS> partners;
  private List<CountryOCS> countries;

  public List<CountryOCS> getCountries() {
    return countries;
  }

  public String getDescription() {
    return description;
  }

  public String getId() {
    return id;
  }

  public List<PartnerOCS> getPartners() {
    return partners;
  }

  public void setCountries(List<CountryOCS> countries) {
    this.countries = countries;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setPartners(List<PartnerOCS> partners) {
    this.partners = partners;
  }

}
