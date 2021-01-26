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

public class DeliverableAffiliationsNotMapped extends MarloBaseEntity {

  /**
   * 
   */
  private static final long serialVersionUID = 3062384310343326529L;

  private DeliverableMetadataExternalSources deliverableMetadataExternalSources;

  private String name;
  private String country;
  private String fullAddress;
  private Integer institutionMatchConfidence;

  public DeliverableAffiliationsNotMapped() {
  }

  public void copyFields(DeliverableAffiliationsNotMapped other) {
    other.setCountry(this.getCountry());
    other.setDeliverableMetadataExternalSources(this.getDeliverableMetadataExternalSources());
    other.setFullAddress(this.getFullAddress());
    other.setInstitutionMatchConfidence(this.getInstitutionMatchConfidence());
    other.setName(this.getName());
  }

  public String getCountry() {
    return country;
  }

  public DeliverableMetadataExternalSources getDeliverableMetadataExternalSources() {
    return deliverableMetadataExternalSources;
  }

  public String getFullAddress() {
    return fullAddress;
  }

  public Integer getInstitutionMatchConfidence() {
    return institutionMatchConfidence;
  }

  public String getName() {
    return name;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public void
    setDeliverableMetadataExternalSources(DeliverableMetadataExternalSources deliverableMetadataExternalSources) {
    this.deliverableMetadataExternalSources = deliverableMetadataExternalSources;
  }

  public void setFullAddress(String fullAddress) {
    this.fullAddress = fullAddress;
  }

  public void setInstitutionMatchConfidence(Integer institutionMatchConfidence) {
    this.institutionMatchConfidence = institutionMatchConfidence;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("deliverableMetadataExternalSources", this.getDeliverableMetadataExternalSources())
      .add("name", this.getName()).add("country", this.getCountry()).add("fullAddress", this.getFullAddress())
      .add("institutionMatchConfidence", this.getInstitutionMatchConfidence()).toString();
  }

}
