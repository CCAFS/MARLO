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


public class SDGIndicators extends MarloBaseEntity implements java.io.Serializable {


  private static final long serialVersionUID = -7251588611670769992L;
  private String UNSDIndicatorCode;
  private String indicatorCode;
  private String indicator;

  private SDGTargets sdgTarget;

  public SDGIndicators() {
    super();
  }


  public SDGIndicators(String uNSDIndicatorCode, String indicatorCode, String indicator, SDGTargets sdgTarget) {
    super();
    UNSDIndicatorCode = uNSDIndicatorCode;
    this.indicatorCode = indicatorCode;
    this.indicator = indicator;
    this.sdgTarget = sdgTarget;
  }


  public String getIndicator() {
    return indicator;
  }

  public String getIndicatorCode() {
    return indicatorCode;
  }

  public SDGTargets getSdgTarget() {
    return sdgTarget;
  }

  public String getUNSDIndicatorCode() {
    return UNSDIndicatorCode;
  }

  public void setIndicator(String indicator) {
    this.indicator = indicator;
  }

  public void setIndicatorCode(String indicatorCode) {
    this.indicatorCode = indicatorCode;
  }

  public void setSdgTarget(SDGTargets sdgTarget) {
    this.sdgTarget = sdgTarget;
  }

  public void setUNSDIndicatorCode(String uNSDIndicatorCode) {
    UNSDIndicatorCode = uNSDIndicatorCode;
  }

}
