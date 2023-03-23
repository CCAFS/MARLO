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

import com.google.common.base.MoreObjects;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class DeliverableAltmetricInfo extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {

  /**
   * 
   */
  private static final long serialVersionUID = -4912986085954492502L;

  private Deliverable deliverable;
  private Phase phase;

  private String title;
  private String doi;
  private String uri;
  private String altmetricJid;
  private String journal;
  private String authors;
  private String type;
  private String handle;
  private String altmetricId;
  private String isOpenAccess;
  private Integer citedByPosts;
  private Integer citedByDelicious;
  private Integer citedByFacebookPages;
  private Integer citedByBlogs;
  private Integer citedByForumUsers;
  private Integer citedByGooglePlusUsers;
  private Integer citedByLinkedinUsers;
  private Integer citedByNewsOutlets;
  private Integer citedByPeerReviewSites;
  private Integer citedByPinterestUsers;
  private Integer citedByPolicies;
  private Integer citedByStackExchangeResources;
  private Integer citedByRedditUsers;
  private Integer citedByResearchHighlightPlatforms;
  private Integer citedByTwitterUsers;
  private Integer citedByYoutubeChannels;
  private Integer citedByWeiboUsers;
  private Integer citedByWikipediaPages;
  private Date lastUpdated;
  private String score;
  private String url;
  private Date addedOn;
  private Date publishedOn;
  private String imageSmall;
  private String imageMedium;
  private String imageLarge;
  private String detailsUrl;
  private Date lastSync;
  private String mendeleyReaders;

  public DeliverableAltmetricInfo() {

  }

  public void copyFields(DeliverableAltmetricInfo other) {
    this.setTitle(other.getTitle());
    this.setDoi(other.getDoi());
    this.setUri(other.getUri());
    this.setAltmetricJid(other.getAltmetricJid());
    this.setJournal(other.getJournal());
    this.setAuthors(other.getAuthors());
    this.setType(other.getType());
    this.setHandle(other.getHandle());
    this.setAltmetricId(other.getAltmetricId());
    this.setIsOpenAccess(other.getIsOpenAccess());
    this.setCitedByPosts(other.getCitedByPosts());
    this.setCitedByDelicious(other.getCitedByDelicious());
    this.setCitedByFacebookPages(other.getCitedByFacebookPages());
    this.setCitedByBlogs(other.getCitedByBlogs());
    this.setCitedByForumUsers(other.getCitedByForumUsers());
    this.setCitedByGooglePlusUsers(other.getCitedByGooglePlusUsers());
    this.setCitedByLinkedinUsers(other.getCitedByLinkedinUsers());
    this.setCitedByNewsOutlets(other.getCitedByNewsOutlets());
    this.setCitedByPeerReviewSites(other.getCitedByPeerReviewSites());
    this.setCitedByPinterestUsers(other.getCitedByPinterestUsers());
    this.setCitedByPolicies(other.getCitedByPolicies());
    this.setCitedByStackExchangeResources(other.getCitedByStackExchangeResources());
    this.setCitedByRedditUsers(other.getCitedByRedditUsers());
    this.setCitedByResearchHighlightPlatforms(other.getCitedByResearchHighlightPlatforms());
    this.setCitedByTwitterUsers(other.getCitedByTwitterUsers());
    this.setCitedByYoutubeChannels(other.getCitedByYoutubeChannels());
    this.setCitedByWeiboUsers(other.getCitedByWeiboUsers());
    this.setCitedByWikipediaPages(other.getCitedByWikipediaPages());
    this.setLastUpdated(other.getLastUpdated());
    this.setScore(other.getScore());
    this.setUrl(other.getUrl());
    this.setAddedOn(other.getAddedOn());
    this.setPublishedOn(other.getPublishedOn());
    this.setImageSmall(other.getImageSmall());
    this.setImageMedium(other.getImageMedium());
    this.setImageLarge(other.getImageLarge());
    this.setDetailsUrl(other.getDetailsUrl());
    this.setLastSync(other.getLastSync());
    this.setActive(other.isActive());
    this.setCreatedBy(other.getCreatedBy());
    this.setMendeleyReaders(other.mendeleyReaders);
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

  public Date getAddedOn() {
    return addedOn;
  }

  public String getAltmetricId() {
    return altmetricId;
  }

  public String getAltmetricJid() {
    return altmetricJid;
  }

  public String getAuthors() {
    return authors;
  }

  public Integer getCitedByBlogs() {
    return citedByBlogs;
  }

  public Integer getCitedByDelicious() {
    return citedByDelicious;
  }

  public Integer getCitedByFacebookPages() {
    return citedByFacebookPages;
  }

  public Integer getCitedByForumUsers() {
    return citedByForumUsers;
  }

  public Integer getCitedByGooglePlusUsers() {
    return citedByGooglePlusUsers;
  }

  public Integer getCitedByLinkedinUsers() {
    return citedByLinkedinUsers;
  }

  public Integer getCitedByNewsOutlets() {
    return citedByNewsOutlets;
  }

  public Integer getCitedByPeerReviewSites() {
    return citedByPeerReviewSites;
  }

  public Integer getCitedByPinterestUsers() {
    return citedByPinterestUsers;
  }

  public Integer getCitedByPolicies() {
    return citedByPolicies;
  }

  public Integer getCitedByPosts() {
    return citedByPosts;
  }

  public Integer getCitedByRedditUsers() {
    return citedByRedditUsers;
  }

  public Integer getCitedByResearchHighlightPlatforms() {
    return citedByResearchHighlightPlatforms;
  }

  public Integer getCitedByStackExchangeResources() {
    return citedByStackExchangeResources;
  }

  public Integer getCitedByTwitterUsers() {
    return citedByTwitterUsers;
  }

  public Integer getCitedByWeiboUsers() {
    return citedByWeiboUsers;
  }

  public Integer getCitedByWikipediaPages() {
    return citedByWikipediaPages;
  }

  public Integer getCitedByYoutubeChannels() {
    return citedByYoutubeChannels;
  }

  public Deliverable getDeliverable() {
    return deliverable;
  }

  public String getDetailsUrl() {
    return detailsUrl;
  }

  public String getDoi() {
    return doi;
  }

  public String getHandle() {
    return handle;
  }

  public String getImageLarge() {
    return imageLarge;
  }

  public String getImageMedium() {
    return imageMedium;
  }

  public String getImageSmall() {
    return imageSmall;
  }

  public String getIsOpenAccess() {
    return isOpenAccess;
  }

  public String getJournal() {
    return journal;
  }

  public Date getLastSync() {
    return lastSync;
  }

  public Date getLastUpdated() {
    return lastUpdated;
  }

  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }

  public String getMendeleyReaders() {
    return mendeleyReaders;
  }

  public Phase getPhase() {
    return phase;
  }

  public Date getPublishedOn() {
    return publishedOn;
  }

  public String getScore() {
    return score;
  }

  public String getTitle() {
    return title;
  }

  public String getType() {
    return type;
  }

  public String getUri() {
    return uri;
  }

  public String getUrl() {
    return url;
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
    return result;
  }

  public void setAddedOn(Date addedOn) {
    this.addedOn = addedOn;
  }

  public void setAltmetricId(String altmetricId) {
    this.altmetricId = altmetricId;
  }

  public void setAltmetricJid(String altmetricJid) {
    this.altmetricJid = altmetricJid;
  }

  public void setAuthors(String authors) {
    this.authors = authors;
  }

  public void setCitedByBlogs(Integer citedByBlogs) {
    this.citedByBlogs = citedByBlogs;
  }

  public void setCitedByDelicious(Integer citedByDelicious) {
    this.citedByDelicious = citedByDelicious;
  }

  public void setCitedByFacebookPages(Integer citedByFacebookPages) {
    this.citedByFacebookPages = citedByFacebookPages;
  }

  public void setCitedByForumUsers(Integer citedByForumUsers) {
    this.citedByForumUsers = citedByForumUsers;
  }

  public void setCitedByGooglePlusUsers(Integer citedByGooglePlusUsers) {
    this.citedByGooglePlusUsers = citedByGooglePlusUsers;
  }

  public void setCitedByLinkedinUsers(Integer citedByLinkedinUsers) {
    this.citedByLinkedinUsers = citedByLinkedinUsers;
  }

  public void setCitedByNewsOutlets(Integer citedByNewsOutlets) {
    this.citedByNewsOutlets = citedByNewsOutlets;
  }

  public void setCitedByPeerReviewSites(Integer citedByPeerReviewSites) {
    this.citedByPeerReviewSites = citedByPeerReviewSites;
  }

  public void setCitedByPinterestUsers(Integer citedByPinterestUsers) {
    this.citedByPinterestUsers = citedByPinterestUsers;
  }

  public void setCitedByPolicies(Integer citedByPolicies) {
    this.citedByPolicies = citedByPolicies;
  }

  public void setCitedByPosts(Integer citedByPosts) {
    this.citedByPosts = citedByPosts;
  }

  public void setCitedByRedditUsers(Integer citedByRedditUsers) {
    this.citedByRedditUsers = citedByRedditUsers;
  }

  public void setCitedByResearchHighlightPlatforms(Integer citedByResearchHighlightPlatforms) {
    this.citedByResearchHighlightPlatforms = citedByResearchHighlightPlatforms;
  }

  public void setCitedByStackExchangeResources(Integer citedByStackExchangeResources) {
    this.citedByStackExchangeResources = citedByStackExchangeResources;
  }

  public void setCitedByTwitterUsers(Integer citedByTweeterUsers) {
    this.citedByTwitterUsers = citedByTweeterUsers;
  }

  public void setCitedByWeiboUsers(Integer citedByWeiboUsers) {
    this.citedByWeiboUsers = citedByWeiboUsers;
  }

  public void setCitedByWikipediaPages(Integer citedByWikipediaPages) {
    this.citedByWikipediaPages = citedByWikipediaPages;
  }

  public void setCitedByYoutubeChannels(Integer citedByYoutubeChannels) {
    this.citedByYoutubeChannels = citedByYoutubeChannels;
  }

  public void setDeliverable(Deliverable deliverable) {
    this.deliverable = deliverable;
  }

  public void setDetailsUrl(String detailsUrl) {
    this.detailsUrl = detailsUrl;
  }

  public void setDoi(String doi) {
    this.doi = doi;
  }

  public void setHandle(String handle) {
    this.handle = handle;
  }

  public void setImageLarge(String imageLarge) {
    this.imageLarge = imageLarge;
  }

  public void setImageMedium(String imageMedium) {
    this.imageMedium = imageMedium;
  }

  public void setImageSmall(String imageSmall) {
    this.imageSmall = imageSmall;
  }

  public void setIsOpenAccess(String isOpenAccess) {
    this.isOpenAccess = isOpenAccess;
  }

  public void setJournal(String journal) {
    this.journal = journal;
  }

  public void setLastSync(Date lastSync) {
    this.lastSync = lastSync;
  }

  public void setLastUpdated(Date lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  public void setMendeleyReaders(String mendeleyReaders) {
    this.mendeleyReaders = mendeleyReaders;
  }

  public void setPhase(Phase phase) {
    this.phase = phase;
  }

  public void setPublishedOn(Date publishedOn) {
    this.publishedOn = publishedOn;
  }

  public void setScore(String score) {
    this.score = score;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("title", this.getTitle()).add("doi", this.getDoi())
      .add("uri", this.getUri()).add("altmetricJid", this.getAltmetricJid()).add("journal", this.getJournal())
      .add("authors", this.getAuthors()).add("type", this.getType()).add("handle", this.getHandle())
      .add("altmetricId", this.getAltmetricId()).add("isOpenAccess", this.getIsOpenAccess())
      .add("citedByPosts", this.getCitedByPosts()).add("citedByDelicious", this.getCitedByDelicious())
      .add("citedByFacebookPages", this.getCitedByFacebookPages()).add("citedByBlogs", this.getCitedByBlogs())
      .add("citedByForumUsers", this.getCitedByForumUsers())
      .add("citedByGooglePlusUsers", this.getCitedByGooglePlusUsers())
      .add("citedByLinkedinUsers", this.getCitedByLinkedinUsers())
      .add("citedByNewsOutlets", this.getCitedByNewsOutlets())
      .add("citedByPeerReviewSites", this.getCitedByPeerReviewSites())
      .add("citedByPinterestUsers", this.getCitedByPinterestUsers()).add("citedByPolicies", this.getCitedByPolicies())
      .add("citedByStackExchangeResources", this.getCitedByStackExchangeResources())
      .add("citedByRedditUsers", this.getCitedByRedditUsers())
      .add("citedByResearchHighlightPlatforms", this.getCitedByResearchHighlightPlatforms())
      .add("citedByTwitterUsers", this.getCitedByTwitterUsers())
      .add("citedByYoutubeChannels", this.getCitedByYoutubeChannels())
      .add("citedByWeiboUsers", this.getCitedByWeiboUsers())
      .add("citedByWikipediaPages", this.getCitedByWikipediaPages()).add("lastUpdated", this.getLastUpdated())
      .add("score", this.getScore()).add("url", this.getUrl()).add("addedOn", this.getAddedOn())
      .add("publishedOn", this.getPublishedOn()).add("imageSmall", this.getImageSmall())
      .add("imageMedium", this.getImageMedium()).add("imageLarge", this.getImageLarge())
      .add("detailsUrl", this.getDetailsUrl()).add("lastSync", this.getLastSync()).toString();
  }
}