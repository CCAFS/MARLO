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


public class CrpGeoLocationMapPartnersDTO {

  private long id;
  private String institutionName;
  private String acronym;
  private String institutionType;
  private String website;

  public String getAcronym() {
    return acronym;
  }

  public long getId() {
    return id;
  }

  public String getInstitutionName() {
    return institutionName;
  }

  public String getInstitutionType() {
    return institutionType;
  }

  public String getWebsite() {
    return website;
  }

  public void setAcronym(String acronym) {
    this.acronym = acronym;
  }

  public void setId(long id) {
    this.id = id;
  }

  public void setInstitutionName(String institutionName) {
    this.institutionName = institutionName;
  }

  public void setInstitutionType(String institutionType) {
    this.institutionType = institutionType;
  }

  public void setWebsite(String website) {
    this.website = website;
  }


}
