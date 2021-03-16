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


public class InstitutionsWOSDTO {

  private Long clarisa_id;


  private String fullName;

  private String full_address;

  private String country;

  private Long confidant;


  public InstitutionsWOSDTO() {
    super();
  }


  public InstitutionsWOSDTO(Long clarisa_id, String fullName, String full_address, String country) {
    super();
    this.clarisa_id = clarisa_id;
    this.fullName = fullName;
    this.full_address = full_address;
    this.country = country;
  }


  public Long getClarisa_id() {
    return clarisa_id;
  }


  public Long getConfidant() {
    return confidant;
  }


  public String getCountry() {
    return country;
  }


  public String getFull_address() {
    return full_address;
  }


  public String getFullName() {
    return fullName;
  }


  public void setClarisa_id(Long clarisa_id) {
    this.clarisa_id = clarisa_id;
  }


  public void setConfidant(Long confidant) {
    this.confidant = confidant;
  }


  public void setCountry(String country) {
    this.country = country;
  }


  public void setFull_address(String full_address) {
    this.full_address = full_address;
  }


  public void setFullName(String fullName) {
    this.fullName = fullName;
  }


}
