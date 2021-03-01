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

public class NewSrfProgressTowardsTargetDTO {

  /*
   * @ApiModelProperty(notes = "Flagship / Module SMO Code", position = 1)
   * private String flagshipProgramId;
   */

  @ApiModelProperty(notes = "SLO indicator ID", position = 2)
  private String srfSloIndicatorId;

  @ApiModelProperty(notes = "Brief summary of new evidence of CGIAR contribution", position = 3)
  private String briefSummary;

  @ApiModelProperty(notes = "Expected additional contribution", position = 4)
  private String additionalContribution;

  @ApiModelProperty(notes = "Geographic Scope", position = 5)
  private List<GeographicScopeDTO> geographicScope;

  @ApiModelProperty(notes = "Countries", position = 6)
  private List<CountryDTO> countries;

  @ApiModelProperty(notes = "Regions", position = 7)
  private List<RegionDTO> regions;


  @ApiModelProperty(notes = "Phase (AR, POWB) - Year", position = 8)
  private PhaseDTO phase;


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
   * public String getFlagshipProgramId() {
   * return flagshipProgramId;
   * }
   */


  public List<GeographicScopeDTO> getGeographicScope() {
    return geographicScope;
  }

  public PhaseDTO getPhase() {
    return phase;
  }


  public List<RegionDTO> getRegions() {
    return regions;
  }

  public String getSrfSloIndicatorId() {
    return srfSloIndicatorId;
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
   * public void setFlagshipProgramId(String flagshipProgramId) {
   * this.flagshipProgramId = flagshipProgramId;
   * }
   */

  public void setGeographicScope(List<GeographicScopeDTO> geographicScope) {
    this.geographicScope = geographicScope;
  }

  public void setPhase(PhaseDTO phase) {
    this.phase = phase;
  }

  public void setRegions(List<RegionDTO> regions) {
    this.regions = regions;
  }

  public void setSrfSloIndicatorId(String srfSloIndicatorId) {
    this.srfSloIndicatorId = srfSloIndicatorId;
  }
}
