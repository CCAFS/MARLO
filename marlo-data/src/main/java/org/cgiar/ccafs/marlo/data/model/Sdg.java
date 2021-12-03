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

package org.cgiar.ccafs.marlo.data.model;


public class Sdg implements java.io.Serializable {


  private static final long serialVersionUID = -1544587780872590887L;
  private Long id;
  private String smoCode;
  private String shortName;
  private String fullName;
  private String description;
  private String financialCode;


  public Sdg() {
    super();
  }


  public Sdg(Long id, String smoCode, String shortName, String fullName, String description) {
    super();
    this.id = id;
    this.smoCode = smoCode;
    this.shortName = shortName;
    this.fullName = fullName;
    this.description = description;
  }


  public String getDescription() {
    return description;
  }


  public String getFinancialCode() {
    return financialCode;
  }


  public String getFullName() {
    return fullName;
  }


  public Long getId() {
    return id;
  }


  public String getShortName() {
    return shortName;
  }


  public String getSmoCode() {
    return smoCode;
  }


  public void setDescription(String description) {
    this.description = description;
  }

  public void setFinancialCode(String financialCode) {
    this.financialCode = financialCode;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public void setId(Long id) {
    this.id = id;
  }


  public void setShortName(String shortName) {
    this.shortName = shortName;
  }

  public void setSmoCode(String smoCode) {
    this.smoCode = smoCode;
  }
}
