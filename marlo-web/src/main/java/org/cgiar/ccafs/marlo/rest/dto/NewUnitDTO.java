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

package org.cgiar.ccafs.marlo.rest.dto;


/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class NewUnitDTO {

  private String description;

  private String financialCode;

  private String unitTypeCode;

  private String parentCode;

  private String scienceGroupCode;


  public String getDescription() {
    return description;
  }

  public String getFinancialCode() {
    return financialCode;
  }

  public String getParentCode() {
    return parentCode;
  }

  public String getScienceGroupCode() {
    return scienceGroupCode;
  }

  public String getUnitTypeCode() {
    return unitTypeCode;
  }


  public void setDescription(String description) {
    this.description = description;
  }

  public void setFinancialCode(String financialCode) {
    this.financialCode = financialCode;
  }

  public void setParentCode(String parentCode) {
    this.parentCode = parentCode;
  }

  public void setScienceGroupCode(String scienceGroupCode) {
    this.scienceGroupCode = scienceGroupCode;
  }

  public void setUnitTypeCode(String unitTypeCode) {
    this.unitTypeCode = unitTypeCode;
  }
}