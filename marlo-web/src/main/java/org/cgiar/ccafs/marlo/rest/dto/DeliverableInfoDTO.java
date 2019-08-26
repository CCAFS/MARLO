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

public class DeliverableInfoDTO {

  @ApiModelProperty(notes = "Global Unit Cluster KeyOutput", position = 1)
  private CrpClusterKeyOutputDTO crpClusterKeyOutput;

  @ApiModelProperty(notes = "Global Unit Program Outcome", position = 2)
  private CrpProgramOutcomeDTO crpProgramOutcome;

  @ApiModelProperty(notes = "Deliverable type", position = 2)
  private DeliverableTypeDTO deliverableType;

  private DeliverableDTO deliverable;

  private PhaseDTO phase;

  private String title;

  private String description;

  private String typeOther;

  private Integer newExpectedYear;

  private int year;

  private Integer status;

  private String statusDescription;

  private Boolean adoptedLicense;

  private String license;

  private String otherLicense;

  private Boolean allowModifications;

  private Boolean isLocationGlobal;

  private GeographicScopeDTO geographicScope;

  private RegionDTO region;


  public Boolean getAdoptedLicense() {
    return adoptedLicense;
  }


  public Boolean getAllowModifications() {
    return allowModifications;
  }


  public CrpClusterKeyOutputDTO getCrpClusterKeyOutput() {
    return crpClusterKeyOutput;
  }


  public CrpProgramOutcomeDTO getCrpProgramOutcome() {
    return crpProgramOutcome;
  }


  public DeliverableDTO getDeliverable() {
    return deliverable;
  }


  public DeliverableTypeDTO getDeliverableType() {
    return deliverableType;
  }


  public String getDescription() {
    return description;
  }


  public GeographicScopeDTO getGeographicScope() {
    return geographicScope;
  }


  public Boolean getIsLocationGlobal() {
    return isLocationGlobal;
  }


  public String getLicense() {
    return license;
  }


  public Integer getNewExpectedYear() {
    return newExpectedYear;
  }


  public String getOtherLicense() {
    return otherLicense;
  }


  public PhaseDTO getPhase() {
    return phase;
  }


  public RegionDTO getRegion() {
    return region;
  }


  public Integer getStatus() {
    return status;
  }


  public String getStatusDescription() {
    return statusDescription;
  }


  public String getTitle() {
    return title;
  }


  public String getTypeOther() {
    return typeOther;
  }


  public int getYear() {
    return year;
  }


  public void setAdoptedLicense(Boolean adoptedLicense) {
    this.adoptedLicense = adoptedLicense;
  }


  public void setAllowModifications(Boolean allowModifications) {
    this.allowModifications = allowModifications;
  }


  public void setCrpClusterKeyOutput(CrpClusterKeyOutputDTO crpClusterKeyOutput) {
    this.crpClusterKeyOutput = crpClusterKeyOutput;
  }


  public void setCrpProgramOutcome(CrpProgramOutcomeDTO crpProgramOutcome) {
    this.crpProgramOutcome = crpProgramOutcome;
  }


  public void setDeliverable(DeliverableDTO deliverable) {
    this.deliverable = deliverable;
  }


  public void setDeliverableType(DeliverableTypeDTO deliverableType) {
    this.deliverableType = deliverableType;
  }


  public void setDescription(String description) {
    this.description = description;
  }


  public void setGeographicScope(GeographicScopeDTO geographicScope) {
    this.geographicScope = geographicScope;
  }


  public void setIsLocationGlobal(Boolean isLocationGlobal) {
    this.isLocationGlobal = isLocationGlobal;
  }


  public void setLicense(String license) {
    this.license = license;
  }


  public void setNewExpectedYear(Integer newExpectedYear) {
    this.newExpectedYear = newExpectedYear;
  }


  public void setOtherLicense(String otherLicense) {
    this.otherLicense = otherLicense;
  }


  public void setPhase(PhaseDTO phase) {
    this.phase = phase;
  }


  public void setRegion(RegionDTO region) {
    this.region = region;
  }


  public void setStatus(Integer status) {
    this.status = status;
  }


  public void setStatusDescription(String statusDescription) {
    this.statusDescription = statusDescription;
  }


  public void setTitle(String title) {
    this.title = title;
  }


  public void setTypeOther(String typeOther) {
    this.typeOther = typeOther;
  }


  public void setYear(int year) {
    this.year = year;
  }


}
