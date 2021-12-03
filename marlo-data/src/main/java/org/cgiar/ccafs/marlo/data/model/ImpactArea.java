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


public class ImpactArea extends MarloAuditableEntity implements java.io.Serializable {


  private static final long serialVersionUID = -3622915554137211843L;

  private String name;
  private String description;
  private String financialCode;


  public ImpactArea() {
    super();
  }


  public ImpactArea(String name, String description) {
    super();
    this.name = name;
    this.description = description;
  }


  public String getDescription() {
    return description;
  }


  public String getFinancialCode() {
    return financialCode;
  }


  public String getName() {
    return name;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setFinancialCode(String financialCode) {
    this.financialCode = financialCode;
  }

  public void setName(String name) {
    this.name = name;
  }


}
