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


public class ResourceInfoOCS {

  private String id;
  private String firstName;
  private String lastName;
  private String gender;
  private String cityOfBirth;
  private String cityOfBirthISO;
  private String email;
  private String profession;
  private String institucion;
  private String countryofIntitution;
  private String countryofIntitutionISO;
  private String supervisor1;
  private String supervisor2;
  private String supervisor3;

  public String getCityOfBirth() {
    return cityOfBirth;
  }


  public String getCityOfBirthISO() {
    return cityOfBirthISO;
  }

  public String getCountryofIntitution() {
    return countryofIntitution;
  }

  public String getCountryofIntitutionISO() {
    return countryofIntitutionISO;
  }

  public String getEmail() {
    return email;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getGender() {
    return gender;
  }

  public String getId() {
    return id;
  }

  public String getInstitucion() {
    return institucion;
  }

  public String getLastName() {
    return lastName;
  }

  public String getProfession() {
    return profession;
  }


  public String getSupervisor1() {
    return supervisor1;
  }


  public String getSupervisor2() {
    return supervisor2;
  }


  public String getSupervisor3() {
    return supervisor3;
  }

  public void setCityOfBirth(String cityOfBirth) {
    this.cityOfBirth = cityOfBirth;
  }

  public void setCityOfBirthISO(String cityOfBirthISO) {
    this.cityOfBirthISO = cityOfBirthISO;
  }

  public void setCountryofIntitution(String countryofIntitution) {
    this.countryofIntitution = countryofIntitution;
  }

  public void setCountryofIntitutionISO(String countryofIntitutionISO) {
    this.countryofIntitutionISO = countryofIntitutionISO;
  }


  public void setEmail(String email) {
    this.email = email;
  }


  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }


  public void setGender(String gender) {
    this.gender = gender;
  }


  public void setId(String id) {
    this.id = id;
  }


  public void setInstitucion(String institucion) {
    this.institucion = institucion;
  }


  public void setLastName(String lastName) {
    this.lastName = lastName;
  }


  public void setProfession(String profession) {
    this.profession = profession;
  }


  public void setSupervisor1(String supervisor1) {
    this.supervisor1 = supervisor1;
  }


  public void setSupervisor2(String supervisor2) {
    this.supervisor2 = supervisor2;
  }


  public void setSupervisor3(String supervisor3) {
    this.supervisor3 = supervisor3;
  }


}
