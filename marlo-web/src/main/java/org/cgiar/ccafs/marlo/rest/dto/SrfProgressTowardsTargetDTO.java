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

import java.util.List;

import io.swagger.annotations.ApiModelProperty;

public class SrfProgressTowardsTargetDTO {

  @ApiModelProperty(notes = "The Generated identification Code", position = 1)
  private Long id;

  /*
   * @ApiModelProperty(notes = "Flagship / Module", position = 2)
   * private FlagshipProgramDTO flagshipProgramDTO;
   */

  @ApiModelProperty(notes = "SLO target", position = 3)
  private SrfSloIndicatorTargetDTO srfSloTarget;

  @ApiModelProperty(notes = "Brief summary of new evidence of CGIAR contribution", position = 4)
  private String briefSummary;

  @ApiModelProperty(notes = "Progress Towards additional contribution", position = 5)
  private String additionalContribution;

  @ApiModelProperty(notes = "geographic Scope", position = 6)
  private List<GeographicScopeDTO> geographicScope;

  @ApiModelProperty(notes = "Regios", position = 6)
  private List<RegionDTO> regions;

  @ApiModelProperty(notes = "Contries", position = 6)
  private List<CountryDTO> countries;


  public String getAdditionalContribution() {
    return additionalContribution;
  }


  public String getBriefSummary() {
    return briefSummary;
  }


  public List<CountryDTO> getCountries() {
    return countries;
  }


  /*
   * public FlagshipProgramDTO getFlagshipProgramDTO() {
   * return flagshipProgramDTO;
   * }
   */


  public List<GeographicScopeDTO> getGeographicScope() {
    return geographicScope;
  }


  public Long getId() {
    return id;
  }

  public List<RegionDTO> getRegions() {
    return regions;
  }

  public SrfSloIndicatorTargetDTO getSrfSloTarget() {
    return srfSloTarget;
  }

  public void setAdditionalContribution(String additionalContribution) {
    this.additionalContribution = additionalContribution;
  }

  public void setBriefSummary(String briefSummary) {
    this.briefSummary = briefSummary;
  }

  public void setCountries(List<CountryDTO> countries) {
    this.countries = countries;
  }


  /*
   * public void setFlagshipProgramDTO(FlagshipProgramDTO flagshipProgramDTO) {
   * this.flagshipProgramDTO = flagshipProgramDTO;
   * }
   */

  public void setGeographicScope(List<GeographicScopeDTO> geographicScope) {
    this.geographicScope = geographicScope;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setRegions(List<RegionDTO> regions) {
    this.regions = regions;
  }

  public void setSrfSloTarget(SrfSloIndicatorTargetDTO srfSloTarget) {
    this.srfSloTarget = srfSloTarget;
  }

}
