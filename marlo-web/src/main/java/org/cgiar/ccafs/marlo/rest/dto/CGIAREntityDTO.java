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

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Official list of CGIAR Centers, CGIAR Research Programs (CRPs) and CGIAR Platforms (PTFs)")
public class CGIAREntityDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  @ApiModelProperty(notes = "Code of CGIAR entity")
  private String code;

  @ApiModelProperty(notes = "CGIAR entity name")
  private String name;

  @ApiModelProperty(notes = "Acronym of CGIAR entity")
  @NotNull
  private String acronym;

  @ApiModelProperty(notes = "Finacial Code of CGIAR")
  @NotNull
  private String financialCode;


  @ApiModelProperty(notes = "CGIAR entity type")
  private CGIAREntityTypeDTO cgiarEntityTypeDTO;


  public String getAcronym() {
    return this.acronym;
  }

  public CGIAREntityTypeDTO getCgiarEntityTypeDTO() {
    return this.cgiarEntityTypeDTO;
  }

  public String getCode() {
    return this.code;
  }

  public String getFinancialCode() {
    return financialCode;
  }

  public String getName() {
    return this.name;
  }

  public void setAcronym(String acronym) {
    this.acronym = acronym;
  }

  public void setCgiarEntityTypeDTO(CGIAREntityTypeDTO cgiarEntityTypeDTO) {
    this.cgiarEntityTypeDTO = cgiarEntityTypeDTO;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public void setFinancialCode(String financialCode) {
    this.financialCode = financialCode;
  }

  public void setName(String name) {
    this.name = name;
  }

}