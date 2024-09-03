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

public class PrimaryLeversRelatedSdgContribution extends MarloAuditableEntity
  implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = -963914989396761020L;

  @Expose
  private PrimaryAllianceLever primaryAllianceLever;
  @Expose
  private SdgContribution sdgContribution;
  @Expose
  private Phase phase;

  public PrimaryLeversRelatedSdgContribution() {
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }

    PrimaryLeversRelatedSdgContribution other = (PrimaryLeversRelatedSdgContribution) obj;
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


  public Phase getPhase() {
    return phase;
  }

  public PrimaryAllianceLever getPrimaryAllianceLever() {
    return primaryAllianceLever;
  }

  public SdgContribution getSdgContribution() {
    return sdgContribution;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
    return result;
  }


  public void setPhase(Phase phase) {
    this.phase = phase;
  }

  public void setPrimaryAllianceLever(PrimaryAllianceLever primaryAllianceLever) {
    this.primaryAllianceLever = primaryAllianceLever;
  }

  public void setSdgContribution(SdgContribution sdgContribution) {
    this.sdgContribution = sdgContribution;
  }

  @Override
  public String toString() {
    return "Activity [id=" + this.getId() + "]";
  }
}

