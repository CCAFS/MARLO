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

import com.google.common.base.MoreObjects;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class ExternalSourceAuthor extends MarloBaseEntity {

  /**
   * 
   */
  private static final long serialVersionUID = 5863046560456485161L;

  private DeliverableMetadataExternalSources deliverableMetadataExternalSources;
  private String fullName;


  public ExternalSourceAuthor() {
  }

  public void copyFields(ExternalSourceAuthor other) {
    other.setDeliverableMetadataExternalSources(this.getDeliverableMetadataExternalSources());
    other.setFullName(this.getFullName());
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
    ExternalSourceAuthor other = (ExternalSourceAuthor) obj;
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }

  public DeliverableMetadataExternalSources getDeliverableMetadataExternalSources() {
    return deliverableMetadataExternalSources;
  }

  public String getFullName() {
    return fullName;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
    return result;
  }

  public void
    setDeliverableMetadataExternalSources(DeliverableMetadataExternalSources deliverableMetadataExternalSources) {
    this.deliverableMetadataExternalSources = deliverableMetadataExternalSources;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("deliverableMetadataExternalSources", this.getDeliverableMetadataExternalSources())
      .add("fullName", this.getFullName()).toString();
  }

}
