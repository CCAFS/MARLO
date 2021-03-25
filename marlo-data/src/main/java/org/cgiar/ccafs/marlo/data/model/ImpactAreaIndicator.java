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

  public ImpactAreaIndicator() {
    super();
  }


  public ImpactAreaIndicator(String indicatorStatement, ImpactArea impactArea) {
    super();
    this.indicatorStatement = indicatorStatement;
    this.impactArea = impactArea;
  }


  public ImpactArea getImpactArea() {
    return impactArea;
  }


  public String getIndicatorStatement() {
    return indicatorStatement;
  }


  public void setImpactArea(ImpactArea impactArea) {
    this.impactArea = impactArea;
  }


  public void setIndicatorStatement(String indicatorStatement) {
    this.indicatorStatement = indicatorStatement;
  }

}
