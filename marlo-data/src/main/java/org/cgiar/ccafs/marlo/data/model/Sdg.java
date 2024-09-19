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

import com.google.gson.annotations.Expose;

public class Sdg extends MarloAuditableEntity implements java.io.Serializable {

  private static final long serialVersionUID = -1544587780872590887L;
  @Expose
  private String smoCode;
  @Expose
  private String shortName;
  @Expose
  private String fullName;
  @Expose
  private String description;
  @Expose
  private String icon;

  public Sdg() {
    super();
  }


  public Sdg(String smoCode, String shortName, String fullName, String description) {
    super();
    this.smoCode = smoCode;
    this.shortName = shortName;
    this.fullName = fullName;
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public String getFullName() {
    return fullName;
  }

  public String getIcon() {
    return icon;
  }

  public String getShortName() {
    return shortName;
  }

  public String getSmoCode() {
    return smoCode;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public void setShortName(String shortName) {
    this.shortName = shortName;
  }

  public void setSmoCode(String smoCode) {
    this.smoCode = smoCode;
  }
}
