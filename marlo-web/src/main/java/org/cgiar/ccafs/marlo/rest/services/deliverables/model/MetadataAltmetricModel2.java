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

package org.cgiar.ccafs.marlo.rest.services.deliverables.model;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class MetadataAltmetricModel2 implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -8164203866320185115L;

  private String title;
  private String doi;
  private String uri;

  @SerializedName("altmetric_jid")
  private String altmetricJid;

  private String journal;
  private List<String> authors;
  private String type;
  private String handle;

  @SerializedName("altmetric_id")
  private String altmetricId;

  @SerializedName("is_oa")
  private String isOpenAccess;

  @SerializedName("cited_by_posts_count")
  private Integer citedByPosts;

  @SerializedName("cited_by_delicious_count")
  private Integer citedByDelicious;

  @SerializedName("cited_by_fbwalls_count")
  private Integer citedByFacebookPages;

  @SerializedName("cited_by_feeds_count")
  private Integer citedByBlogs;

  @SerializedName("cited_by_forum_count")
  private Integer citedByForumUsers;

  @SerializedName("cited_by_gplus_count")
  private Integer citedByGooglePlusUsers;

  @SerializedName("cited_by_linkedin_count")
  private Integer citedByLinkedinUsers;

  @SerializedName("cited_by_msm_count")
  private Integer citedByNewsOutlets;

  @SerializedName("cited_by_peer_review_sites_count")
  private Integer citedByPeerReviewSites;

  @SerializedName("cited_by_pinners_count")
  private Integer citedByPinterestUsers;

  @SerializedName("cited_by_policies_count")
  private Integer citedByPolicies;

  @SerializedName("cited_by_qs_count")
  private Integer citedByStackExchangeResources;

  @SerializedName("cited_by_rdts_count")
  private Integer citedByRedditUsers;

  @SerializedName("cited_by_rh_count")
  private Integer citedByResearchHighlightPlatforms;

  @SerializedName("cited_by_tweeters_count")
  private Integer citedByTwitterUsers;

  @SerializedName("cited_by_videos_count")
  private Integer citedByYoutubeChannels;

  @SerializedName("cited_by_weibo_count")
  private Integer citedByWeiboUsers;

  @SerializedName("cited_by_wikipedia_count")
  private Integer citedByWikipediaPages;

  @SerializedName("last_updated")
  private Long lastUpdated;

  private String score;
  private String url;

  @SerializedName("added_on")
  private Long addedOn;

  @SerializedName("published_on")
  private Long publishedOn;

  @SerializedName("image_small")
  private String imageSmall;

  @SerializedName("image_medium")
  private String imageMedium;

  @SerializedName("image_large")
  private String imageLarge;

  HandleImageModel images;


  public MetadataAltmetricModel2() {

  }


  public Long getAddedOn() {
    return addedOn;
  }

  public String getAltmetricId() {
    return altmetricId;
  }

  public String getAltmetricJid() {
    return altmetricJid;
  }

  public List<String> getAuthors() {
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

  public HandleImageModel getImages() {
    return images;
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

  public Long getLastUpdated() {
    return lastUpdated;
  }

  public Long getPublishedOn() {
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

  public void setAddedOn(Long addedOn) {
    this.addedOn = addedOn;
  }


  public void setAltmetricId(String altmetricId) {
    this.altmetricId = altmetricId;
  }

  public void setAltmetricJid(String altmetricJid) {
    this.altmetricJid = altmetricJid;
  }

  public void setAuthors(List<String> authors) {
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

  public void setCitedByTwitterUsers(Integer citedByTwitterUsers) {
    this.citedByTwitterUsers = citedByTwitterUsers;
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

  public void setImages(HandleImageModel images) {
    this.images = images;
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

  public void setLastUpdated(Long lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  public void setPublishedOn(Long publishedOn) {
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

}
