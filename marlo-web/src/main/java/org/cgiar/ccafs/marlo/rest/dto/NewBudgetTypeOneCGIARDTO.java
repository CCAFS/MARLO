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

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class NewBudgetTypeOneCGIARDTO {

  @ApiModelProperty(notes = "Budget type  name", position = 1)
  @NotNull
  private String name;

  @ApiModelProperty(notes = "Budget type  description", position = 2)
  @NotNull
  private String description;

  @ApiModelProperty(notes = "Budget type  financial code", position = 3)
  private String financialCode;

  @ApiModelProperty(notes = "Budget type  parent tree", position = 4)
  private String parent;


  public String getDescription() {
    return this.description;
  }

  public String getFinancialCode() {
    return financialCode;
  }

  public String getName() {
    return this.name;
  }

  public String getParent() {
    return parent;
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

  public void setParent(String parent) {
    this.parent = parent;
  }
}