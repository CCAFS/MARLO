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
import java.util.Set;

import com.google.common.base.MoreObjects;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class DeliverableMetadataExternalSources extends MarloAuditableEntity
  implements java.io.Serializable, IAuditLog {

  /**
   * 
   */
  private static final long serialVersionUID = 2177525326623937388L;

  private Deliverable deliverable;
  private Phase phase;
  private String source;
  private String url;
  private String doi;
  private String title;
  private String publicationType;
  private Integer publicationYear;
  private String openAccessStatus;
  private String openAccessLink;
  private String isiStatus;
  private String journalName;
  private String volume;
  private String issue;
  private String pages;
  private Date createDate;

  // Gardian fields
  private String gardianFindability;
  private String gardianAccessibility;
  private String gardianInteroperability;
  private String gardianReusability;
  private String gardianTitle;

  private Set<DeliverableAffiliation> deliverableAffiliations;
  private Set<DeliverableAffiliationsNotMapped> deliverableAffiliationsNotMapped;
  private Set<ExternalSourceAuthor> externalSourceAuthors;


  public DeliverableMetadataExternalSources() {

  }

  public void copyFields(DeliverableMetadataExternalSources other) {
    this.setDeliverable(other.getDeliverable());
    this.setDoi(other.getDoi());
    this.setIsiStatus(other.getIsiStatus());
    this.setOpenAccessStatus(other.getOpenAccessStatus());
    this.setOpenAccessLink(other.getOpenAccessLink());
    this.setJournalName(other.getJournalName());
    this.setPages(other.getPages());
    this.setPhase(other.getPhase());
    this.setPublicationType(other.getPublicationType());
    this.setPublicationYear(other.getPublicationYear());
    this.setSource(other.getSource());
    this.setTitle(other.getTitle());
    this.setUrl(other.getUrl());
    this.setVolume(other.getVolume());
    this.setIssue(other.getIssue());
    this.setActive(other.isActive());
    this.setCreatedBy(other.getCreatedBy());
    this.setCreateDate(other.getCreateDate());
    this.setGardianFindability(other.getGardianFindability());
    this.setGardianAccessibility(other.getGardianAccessibility());
    this.setGardianInteroperability(other.getGardianInteroperability());
    this.setGardianReusability(other.getGardianReusability());
    this.setGardianTitle(other.getGardianTitle());
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
    DeliverableMetadataExternalSources other = (DeliverableMetadataExternalSources) obj;
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }

  public Date getCreateDate() {
    return createDate;
  }

  public Deliverable getDeliverable() {
    return deliverable;
  }


  public Set<DeliverableAffiliation> getDeliverableAffiliations() {
    return deliverableAffiliations;
  }

  public Set<DeliverableAffiliationsNotMapped> getDeliverableAffiliationsNotMapped() {
    return deliverableAffiliationsNotMapped;
  }

  public String getDoi() {
    return doi;
  }

  public Set<ExternalSourceAuthor> getExternalSourceAuthors() {
    return externalSourceAuthors;
  }

  public String getGardianAccessibility() {
    return gardianAccessibility;
  }

  public String getGardianFindability() {
    return gardianFindability;
  }

  public String getGardianInteroperability() {
    return gardianInteroperability;
  }

  public String getGardianReusability() {
    return gardianReusability;
  }

  public String getGardianTitle() {
    return gardianTitle;
  }

  public String getIsiStatus() {
    return isiStatus;
  }

  public String getIssue() {
    return issue;
  }

  public String getJournalName() {
    return journalName;
  }

  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }

  public String getOpenAccessLink() {
    return openAccessLink;
  }

  public String getOpenAccessStatus() {
    return openAccessStatus;
  }

  public String getPages() {
    return pages;
  }

  public Phase getPhase() {
    return phase;
  }

  public String getPublicationType() {
    return publicationType;
  }

  public Integer getPublicationYear() {
    return publicationYear;
  }

  public String getSource() {
    return source;
  }

  public String getTitle() {
    return title;
  }

  public String getUrl() {
    return url;
  }

  public String getVolume() {
    return volume;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
    return result;
  }

  public void setCreateDate(Date createDate) {
    this.createDate = createDate;
  }

  public void setDeliverable(Deliverable deliverable) {
    this.deliverable = deliverable;
  }

  public void setDeliverableAffiliations(Set<DeliverableAffiliation> deliverableAffiliations) {
    this.deliverableAffiliations = deliverableAffiliations;
  }

  public void
    setDeliverableAffiliationsNotMapped(Set<DeliverableAffiliationsNotMapped> deliverableAffiliationsNotMapped) {
    this.deliverableAffiliationsNotMapped = deliverableAffiliationsNotMapped;
  }

  public void setDoi(String doi) {
    this.doi = doi;
  }

  public void setExternalSourceAuthors(Set<ExternalSourceAuthor> externalSourceAuthors) {
    this.externalSourceAuthors = externalSourceAuthors;
  }

  public void setGardianAccessibility(String gardianAccessibility) {
    this.gardianAccessibility = gardianAccessibility;
  }

  public void setGardianFindability(String gardianFindability) {
    this.gardianFindability = gardianFindability;
  }

  public void setGardianInteroperability(String gardianInteroperability) {
    this.gardianInteroperability = gardianInteroperability;
  }

  public void setGardianReusability(String gardianReusability) {
    this.gardianReusability = gardianReusability;
  }

  public void setGardianTitle(String gardianTitle) {
    this.gardianTitle = gardianTitle;
  }

  public void setIsiStatus(String isISI) {
    this.isiStatus = isISI;
  }

  public void setIssue(String issue) {
    this.issue = issue;
  }

  public void setJournalName(String journalName) {
    this.journalName = journalName;
  }

  public void setOpenAccessLink(String openAccessLink) {
    this.openAccessLink = openAccessLink;
  }

  public void setOpenAccessStatus(String isOpenAccess) {
    this.openAccessStatus = isOpenAccess;
  }

  public void setPages(String pages) {
    this.pages = pages;
  }

  public void setPhase(Phase phase) {
    this.phase = phase;
  }

  public void setPublicationType(String publicationType) {
    this.publicationType = publicationType;
  }

  public void setPublicationYear(Integer publicationYear) {
    this.publicationYear = publicationYear;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public void setVolume(String volume) {
    this.volume = volume;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("deliverable", this.getDeliverable()).add("phase", this.getPhase())
      .add("source", this.getSource()).add("url", this.getUrl()).add("doi", this.getDoi()).add("title", this.getTitle())
      .add("publicationType", this.getPublicationType()).add("publicationYear", this.getPublicationYear())
      .add("openAccessStatus", this.getOpenAccessStatus()).add("openAccessLink", this.getOpenAccessLink())
      .add("isiStatus", this.getIsiStatus()).add("journalName", this.getJournalName()).add("volume", this.getVolume())
      .add("issue", this.getIssue()).add("pages", this.getPages())
      .add("gardianFindability", this.getGardianFindability())
      .add("gardianAccessibility", this.getGardianAccessibility())
      .add("gardianInteroperability", this.getGardianInteroperability())
      .add("gardianReusability", this.getGardianReusability()).add("gardianTitle", this.getGardianTitle()).toString();
  }
}