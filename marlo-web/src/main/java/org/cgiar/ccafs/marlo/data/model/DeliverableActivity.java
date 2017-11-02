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

import java.util.Date;

import com.google.gson.annotations.Expose;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class DeliverableActivity implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = 5926712975974248056L;

  @Expose
  private Long id;

  @Expose
  private User modifiedBy;

  @Expose
  private User createdBy;

  @Expose
  private Activity activity;

  @Expose
  private Deliverable deliverable;

  @Expose
  private boolean active;


  @Expose
  private Date activeSince;

  @Expose
  private String modificationJustification;

  public DeliverableActivity() {
  }


  public DeliverableActivity(User modifiedBy, User createdBy, Activity activity, Deliverable deliverable,
    boolean active, Date activeSince, String modificationJustification) {
    this.modifiedBy = modifiedBy;
    this.createdBy = createdBy;
    this.activity = activity;
    this.deliverable = deliverable;
    this.active = active;
    this.activeSince = activeSince;
    this.modificationJustification = modificationJustification;
  }


  public DeliverableActivity(User modifiedBy, User createdBy, boolean active, Date activeSince,
    String modificationJustification) {
    this.modifiedBy = modifiedBy;
    this.createdBy = createdBy;
    this.active = active;
    this.activeSince = activeSince;
    this.modificationJustification = modificationJustification;
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
    DeliverableActivity other = (DeliverableActivity) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    return true;
  }


  public Date getActiveSince() {
    return this.activeSince;
  }

  public Activity getActivity() {
    return activity;
  }

  public User getCreatedBy() {
    return createdBy;
  }

  public Deliverable getDeliverable() {
    return deliverable;
  }

  @Override
  public Long getId() {
    return this.id;
  }

  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }

  @Override
  public String getModificationJustification() {
    return this.modificationJustification;
  }

  @Override
  public User getModifiedBy() {
    return modifiedBy;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  @Override
  public boolean isActive() {
    return active;
  }


  public void setActive(boolean active) {
    this.active = active;
  }

  public void setActiveSince(Date activeSince) {
    this.activeSince = activeSince;
  }

  public void setActivity(Activity activity) {
    this.activity = activity;
  }

  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }

  public void setDeliverable(Deliverable deliverable) {
    this.deliverable = deliverable;
  }

  public void setId(Long id) {
    this.id = id;
  }


  public void setModificationJustification(String modificationJustification) {
    this.modificationJustification = modificationJustification;
  }

  public void setModifiedBy(User modifiedBy) {
    this.modifiedBy = modifiedBy;
  }


  @Override
  public String toString() {
    return "DeliverableActivity [id=" + id + ", activity=" + activity + ", deliverable=" + deliverable + "]";
  }


}