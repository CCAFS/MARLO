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

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

@ApiModel(description = "Model for a new CGIAR Center, CGIAR Research Program (CRPs) or CGIAR Platform (PTFs)")
public class NewCGIAREntityDTO {

  @ApiModelProperty(notes = "SMO Code of CGIAR entity")
  private String code;

  @ApiModelProperty(notes = "CGIAR entity name")
  private String name;

  @ApiModelProperty(notes = "Acronym of CGIAR entity")
  private String acronym;

  @ApiModelProperty(notes = "Finacial Code of CGIAR")
  private String financialCode;

  @ApiModelProperty(notes = "CGIAR entity type ID")
  private Long cgiarEntityTypeId;

  @ApiModelProperty(notes = "ID from institution linked to this CGIAR Entity")
  private Long institutionId;

  public String getAcronym() {
    return this.acronym;
  }

  public Long getCgiarEntityTypeId() {
    return cgiarEntityTypeId;
  }

  public String getCode() {
    return this.code;
  }

  public String getFinancialCode() {
    return financialCode;
  }

  public Long getInstitutionId() {
    return institutionId;
  }

  public String getName() {
    return this.name;
  }


  public void setAcronym(String acronym) {
    this.acronym = acronym;
  }

  public void setCgiarEntityTypeId(Long cgiarEntityTypeId) {
    this.cgiarEntityTypeId = cgiarEntityTypeId;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public void setFinancialCode(String financialCode) {
    this.financialCode = financialCode;
  }

  public void setInstitutionId(Long institutionId) {
    this.institutionId = institutionId;
  }

  public void setName(String name) {
    this.name = name;
  }
}
