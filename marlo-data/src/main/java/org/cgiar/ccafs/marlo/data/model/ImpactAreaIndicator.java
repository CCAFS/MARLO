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

public class ImpactAreaIndicator extends MarloAuditableEntity implements java.io.Serializable {


  private static final long serialVersionUID = 4099172748348781498L;


  private String indicatorStatement;
  private ImpactArea impactArea;
  private Long targetYear;
  private String targetUnit;
  private String targetValue;
  private Boolean isProjectedBenefits;

  public ImpactAreaIndicator() {
    super();
  }


  public ImpactAreaIndicator(String indicatorStatement, ImpactArea impactArea, Long targetYear, String targetUnit,
    String targetValue, Boolean isProjectedBenefits) {
    super();
    this.indicatorStatement = indicatorStatement;
    this.impactArea = impactArea;
    this.targetYear = targetYear;
    this.targetUnit = targetUnit;
    this.targetValue = targetValue;
    this.isProjectedBenefits = isProjectedBenefits;
  }

  public String getComposedName() {
    return this.getIndicatorStatement();
  }

  public ImpactArea getImpactArea() {
    return impactArea;
  }

  public String getIndicatorStatement() {
    return indicatorStatement;
  }

  public Boolean getIsProjectedBenefits() {
    return isProjectedBenefits;
  }

  public String getTargetUnit() {
    return targetUnit;
  }

  public String getTargetValue() {
    return targetValue;
  }

  public Long getTargetYear() {
    return targetYear;
  }


  public void setImpactArea(ImpactArea impactArea) {
    this.impactArea = impactArea;
  }

  public void setIndicatorStatement(String indicatorStatement) {
    this.indicatorStatement = indicatorStatement;
  }

  public void setIsProjectedBenefits(Boolean isProjectedBenefits) {
    this.isProjectedBenefits = isProjectedBenefits;
  }

  public void setTargetUnit(String targetUnit) {
    this.targetUnit = targetUnit;
  }

  public void setTargetValue(String targetValue) {
    this.targetValue = targetValue;
  }

  public void setTargetYear(Long targetYear) {
    this.targetYear = targetYear;
  }
}
