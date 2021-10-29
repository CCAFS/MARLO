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

import org.apache.commons.lang3.builder.ToStringBuilder;

public class OneCGIARUnit extends MarloAuditableEntity implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 8767263248966536250L;

  private String financialCode;
  private String description;

  private OneCGIARUnit parentUnit;
  private OneCGIARScienceGroup scienceGroup;

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }

    OneCGIARUnit other = (OneCGIARUnit) obj;

    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }

    return true;
  }

  public String getDescription() {
    return description;
  }

  public String getFinancialCode() {
    return financialCode;
  }

  public OneCGIARUnit getParentUnit() {
    return parentUnit;
  }

  public OneCGIARScienceGroup getScienceGroup() {
    return scienceGroup;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
    return result;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setFinancialCode(String financialCode) {
    this.financialCode = financialCode;
  }

  public void setParentUnit(OneCGIARUnit parentUnit) {
    this.parentUnit = parentUnit;
  }

  public void setScienceGroup(OneCGIARScienceGroup scienceGroup) {
    this.scienceGroup = scienceGroup;
  }


  @Override
  public String toString() {
    return new ToStringBuilder(this).append("id", this.getId()).append("description", this.getDescription())
      .append("financialCode", this.getFinancialCode())
      .append("parentUnit", (this.getParentUnit() != null ? this.getParentUnit().getId() : null))
      .append("scienceGroup", (this.getScienceGroup() != null ? this.getScienceGroup().getId() : null)).toString();
  }
}
