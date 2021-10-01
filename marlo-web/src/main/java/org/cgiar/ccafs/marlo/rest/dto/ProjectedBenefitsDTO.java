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

public class ProjectedBenefitsDTO {

  private Long impactAreaId;
  private String impactAreaName;
  private Long impactAreaIndicator;
  private String impactAreaIndicatorName;
  public Long targetYear;
  public String targetUnit;
  public String value;


  private List<ProjectedBenefitsDepthScaleDTO> depthScales;


  private List<ProjectedBenefitsWeightingDTO> weightingValues;


  public List<ProjectedBenefitsDepthScaleDTO> getDepthScales() {
    return depthScales;
  }


  public Long getImpactAreaId() {
    return impactAreaId;
  }


  public Long getImpactAreaIndicator() {
    return impactAreaIndicator;
  }

  public String getImpactAreaIndicatorName() {
    return impactAreaIndicatorName;
  }

  public String getImpactAreaName() {
    return impactAreaName;
  }

  public String getTargetUnit() {
    return targetUnit;
  }


  public Long getTargetYear() {
    return targetYear;
  }


  public String getValue() {
    return value;
  }


  public List<ProjectedBenefitsWeightingDTO> getWeightingValues() {
    return weightingValues;
  }


  public void setDepthScales(List<ProjectedBenefitsDepthScaleDTO> depthScales) {
    this.depthScales = depthScales;
  }


  public void setImpactAreaId(Long impactAreaId) {
    this.impactAreaId = impactAreaId;
  }


  public void setImpactAreaIndicator(Long impactAreaIndicator) {
    this.impactAreaIndicator = impactAreaIndicator;
  }

  public void setImpactAreaIndicatorName(String impactAreaIndicatorName) {
    this.impactAreaIndicatorName = impactAreaIndicatorName;
  }

  public void setImpactAreaName(String impactAreaName) {
    this.impactAreaName = impactAreaName;
  }

  public void setTargetUnit(String targetUnit) {
    this.targetUnit = targetUnit;
  }

  public void setTargetYear(Long targetYear) {
    this.targetYear = targetYear;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public void setWeightingValues(List<ProjectedBenefitsWeightingDTO> weightingValues) {
    this.weightingValues = weightingValues;
  }

}
