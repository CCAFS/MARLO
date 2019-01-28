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

import io.swagger.annotations.ApiModelProperty;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class PartnerRequestDTO {


  @ApiModelProperty(notes = "The Generated SRF IDO ID")
  private Long id;


  @ApiModelProperty(notes = "The Generated SRF IDO ID")
  private String partnerName;


  @ApiModelProperty(notes = "The Generated SRF IDO ID")
  private String acronym;


  @ApiModelProperty(notes = "The Generated SRF IDO ID")
  private String webPage;


  @ApiModelProperty(notes = "The Generated SRF IDO ID")
  private Boolean acepted;


  CountryDTO locElementDTO;


  InstitutionTypeDTO institutionTypeDTO;


  InstitutionDTO institutionDTO;


  public Boolean getAcepted() {
    return acepted;
  }


  public String getAcronym() {
    return acronym;
  }


  public Long getId() {
    return id;
  }


  public InstitutionDTO getInstitutionDTO() {
    return institutionDTO;
  }


  public InstitutionTypeDTO getInstitutionTypeDTO() {
    return institutionTypeDTO;
  }


  public CountryDTO getLocElementDTO() {
    return locElementDTO;
  }


  public String getPartnerName() {
    return partnerName;
  }


  public String getWebPage() {
    return webPage;
  }

  public void setAcepted(Boolean acepted) {
    this.acepted = acepted;
  }

  public void setAcronym(String acronym) {
    this.acronym = acronym;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setInstitutionDTO(InstitutionDTO institutionDTO) {
    this.institutionDTO = institutionDTO;
  }

  public void setInstitutionTypeDTO(InstitutionTypeDTO institutionTypeDTO) {
    this.institutionTypeDTO = institutionTypeDTO;
  }

  public void setLocElementDTO(CountryDTO locElementDTO) {
    this.locElementDTO = locElementDTO;
  }

  public void setPartnerName(String partnerName) {
    this.partnerName = partnerName;
  }

  public void setWebPage(String webPage) {
    this.webPage = webPage;
  }

}
