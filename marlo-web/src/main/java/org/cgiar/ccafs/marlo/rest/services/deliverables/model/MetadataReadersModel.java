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

package org.cgiar.ccafs.marlo.rest.services.deliverables.model;

import java.io.Serializable;

public class MetadataReadersModel implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -8164203866320185115L;

  private String citeulike;
  private String mendeley;
  private String connotea;

  public MetadataReadersModel() {

  }

  public String getCiteulike() {
    return citeulike;
  }

  public String getConnotea() {
    return connotea;
  }

  public String getMendeley() {
    return mendeley;
  }

  public void setCiteulike(String citeulike) {
    this.citeulike = citeulike;
  }

  public void setConnotea(String connotea) {
    this.connotea = connotea;
  }

  public void setMendeley(String mendeley) {
    this.mendeley = mendeley;
  }

}
