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

import java.util.Date;

import javax.persistence.MappedSuperclass;

import com.google.gson.annotations.Expose;

/**
 * An abstract class that standardizes how the Audit columns are handled in MARLO.
 * 
 * @author GrantL
 */
@MappedSuperclass
public abstract class MarloAuditableEntity extends MarloSoftDeleteableEntity {


  @Expose
  private User createdBy;

  @Expose
  private Date activeSince;

  @Expose
  private String modificationJustification;

  @Expose
  private User modifiedBy;


  public Date getActiveSince() {
    return this.activeSince;
  }


  public User getCreatedBy() {
    return this.createdBy;
  }


  public String getModificationJustification() {
    return this.modificationJustification;
  }


  public User getModifiedBy() {
    return modifiedBy;
  }


  public void setActiveSince(Date activeSince) {
    this.activeSince = activeSince;
  }


  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }


  public void setModificationJustification(String modificationJustification) {
    this.modificationJustification = modificationJustification;
  }


  public void setModifiedBy(User modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

}
