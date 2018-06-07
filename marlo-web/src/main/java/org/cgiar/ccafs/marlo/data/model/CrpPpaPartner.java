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

import java.util.List;

import com.google.gson.annotations.Expose;

public class CrpPpaPartner extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = 9208364810110651075L;

  @Expose
  private Institution institution;

  @Expose
  private Phase phase;

  private GlobalUnit crp;

  private List<LiaisonUser> contactPoints;


  public CrpPpaPartner() {
  }


  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    CrpPpaPartner other = (CrpPpaPartner) obj;
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }

  public List<LiaisonUser> getContactPoints() {
    return contactPoints;
  }

  public GlobalUnit getCrp() {
    return crp;
  }

  public Institution getInstitution() {
    return institution;
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

  public void setContactPoints(List<LiaisonUser> contactPoints) {
    this.contactPoints = contactPoints;
  }


  public void setCrp(GlobalUnit crp) {
    this.crp = crp;
  }


  public void setInstitution(Institution institution) {
    this.institution = institution;
  }


  public void setPhase(Phase phase) {
    this.phase = phase;
  }

  @Override
  public String toString() {
    return "CrpPpaPartner [id=" + this.getId() + ", institution=" + institution + ", crp=" + crp + "]";
  }

}

