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

package org.cgiar.ccafs.marlo.data.model;

import java.io.Serializable;

/**
 * @author Christian Garcia- CIAT/CCAFS
 */
public class CustomLevelSelect implements Serializable {


  /**
   * 
   */
  private static final long serialVersionUID = -3426216373887145149L;
  private LocElementType locElementType;
  private Boolean check;

  public Boolean getCheck() {
    return check;
  }


  public LocElementType getLocElementType() {
    return locElementType;
  }


  public void setCheck(Boolean check) {
    this.check = check;
  }


  public void setLocElementType(LocElementType locElementType) {
    this.locElementType = locElementType;
  }


  @Override
  public String toString() {
    return "CustomLevelSelect [locElementType=" + locElementType + ", check=" + check + "]";
  }


}
