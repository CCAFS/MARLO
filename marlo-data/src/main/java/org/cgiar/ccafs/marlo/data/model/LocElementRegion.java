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

import java.io.Serializable;

public class LocElementRegion extends MarloBaseEntity implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1326603108113439411L;

  private LocElement locElement;
  private Region region;

  public LocElement getLocElement() {
    return locElement;
  }

  public Region getRegion() {
    return region;
  }

  public void setLocElement(LocElement locElement) {
    this.locElement = locElement;
  }

  public void setRegion(Region region) {
    this.region = region;
  }
}
