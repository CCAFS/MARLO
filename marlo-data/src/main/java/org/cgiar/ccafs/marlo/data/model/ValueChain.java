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

import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;

/**
 * Agricultural value chain an innovation targets (A2-2613). "Not value-chain specific" is exclusive of every other
 * option and "Other" asks for the value chain in a free text on the innovation info.
 */
public class ValueChain extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = 4870322937517410451L;

  @Expose
  private String name;
  @Expose
  private Integer sortOrder;
  @Expose
  private boolean notSpecific;
  @Expose
  private boolean other;

  public ValueChain() {
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof ValueChain)) {
      return false;
    }

    ValueChain other = (ValueChain) obj;
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }

  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }

  public String getName() {
    return name;
  }

  public Integer getSortOrder() {
    return sortOrder;
  }

  @Override
  public int hashCode() {
    return this.getId() == null ? 0 : this.getId().hashCode();
  }

  public boolean isNotSpecific() {
    return notSpecific;
  }

  public boolean isOther() {
    return other;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setNotSpecific(boolean notSpecific) {
    this.notSpecific = notSpecific;
  }

  public void setOther(boolean other) {
    this.other = other;
  }

  public void setSortOrder(Integer sortOrder) {
    this.sortOrder = sortOrder;
  }

  @Override
  public String toString() {
    return "ValueChain [id=" + this.getId() + ", name=" + name + "]";
  }
}
