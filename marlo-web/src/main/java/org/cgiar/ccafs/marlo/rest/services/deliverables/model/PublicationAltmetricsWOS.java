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

/**************
 * @author Diego Perez - CIAT/CCAFS
 **************/

package org.cgiar.ccafs.marlo.rest.services.deliverables.model;

import java.io.Serializable;
import java.util.List;

public class PublicationAltmetricsWOS implements Serializable {


  private static final long serialVersionUID = 2737080734562088582L;


  private String title;


  private String doi;


  private String uri;

  private String altmetric_jid;

  private String journal;

  private List<String> authors;

  private String type;

  private String handle;

  private String altmetric_id;

  private boolean is_oa;

  private String cited_by_posts_count;

  private String cited_by_delicious_count;

  private String cited_by_fbwalls_count;

  private String cited_by_feeds_count;

  private String cited_by_forum_count;

  private String cited_by_gplus_count;

  private String cited_by_linkedin_count;

  private String cited_by_msm_count;

  private String cited_by_peer_review_sites_count;

  private String cited_by_pinners_count;

  private String cited_by_policies_count;

  private String cited_by_qs_count;

  private String cited_by_rdts_count;

  private String cited_by_rh_count;

  private String cited_by_tweeters_count;

  private String cited_by_videos_count;

  private String cited_by_weibo_count;

  private String cited_by_wikipedia_count;

  private String last_updated;

  private String score;

  private String url;

  private String added_on;

  private String published_on;

  private PublicationAlmetricsImages images;

  public PublicationAltmetricsWOS() {
    super();
  }

  public String getAdded_on() {
    return added_on;
  }


  public String getAltmetric_id() {
    return altmetric_id;
  }


  public String getAltmetric_jid() {
    return altmetric_jid;
  }


  public List<String> getAuthors() {
    return authors;
  }


  public String getCited_by_delicious_count() {
    return cited_by_delicious_count;
  }


  public String getCited_by_fbwalls_count() {
    return cited_by_fbwalls_count;
  }


  public String getCited_by_feeds_count() {
    return cited_by_feeds_count;
  }


  public String getCited_by_forum_count() {
    return cited_by_forum_count;
  }


  public String getCited_by_gplus_count() {
    return cited_by_gplus_count;
  }


  public String getCited_by_linkedin_count() {
    return cited_by_linkedin_count;
  }


  public String getCited_by_msm_count() {
    return cited_by_msm_count;
  }


  public String getCited_by_peer_review_sites_count() {
    return cited_by_peer_review_sites_count;
  }


  public String getCited_by_pinners_count() {
    return cited_by_pinners_count;
  }


  public String getCited_by_policies_count() {
    return cited_by_policies_count;
  }


  public String getCited_by_posts_count() {
    return cited_by_posts_count;
  }


  public String getCited_by_qs_count() {
    return cited_by_qs_count;
  }


  public String getCited_by_rdts_count() {
    return cited_by_rdts_count;
  }


  public String getCited_by_rh_count() {
    return cited_by_rh_count;
  }


  public String getCited_by_tweeters_count() {
    return cited_by_tweeters_count;
  }


  public String getCited_by_videos_count() {
    return cited_by_videos_count;
  }


  public String getCited_by_weibo_count() {
    return cited_by_weibo_count;
  }


  public String getCited_by_wikipedia_count() {
    return cited_by_wikipedia_count;
  }


  public String getDoi() {
    return doi;
  }


  public String getHandle() {
    return handle;
  }


  public PublicationAlmetricsImages getImages() {
    return images;
  }


  public String getJournal() {
    return journal;
  }


  public String getLast_updated() {
    return last_updated;
  }


  public String getPublished_on() {
    return published_on;
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


  public boolean isIs_oa() {
    return is_oa;
  }


  public void setAdded_on(String added_on) {
    this.added_on = added_on;
  }


  public void setAltmetric_id(String altmetric_id) {
    this.altmetric_id = altmetric_id;
  }


  public void setAltmetric_jid(String altmetric_jid) {
    this.altmetric_jid = altmetric_jid;
  }


  public void setAuthors(List<String> authors) {
    this.authors = authors;
  }


  public void setCited_by_delicious_count(String cited_by_delicious_count) {
    this.cited_by_delicious_count = cited_by_delicious_count;
  }


  public void setCited_by_fbwalls_count(String cited_by_fbwalls_count) {
    this.cited_by_fbwalls_count = cited_by_fbwalls_count;
  }


  public void setCited_by_feeds_count(String cited_by_feeds_count) {
    this.cited_by_feeds_count = cited_by_feeds_count;
  }


  public void setCited_by_forum_count(String cited_by_forum_count) {
    this.cited_by_forum_count = cited_by_forum_count;
  }


  public void setCited_by_gplus_count(String cited_by_gplus_count) {
    this.cited_by_gplus_count = cited_by_gplus_count;
  }


  public void setCited_by_linkedin_count(String cited_by_linkedin_count) {
    this.cited_by_linkedin_count = cited_by_linkedin_count;
  }


  public void setCited_by_msm_count(String cited_by_msm_count) {
    this.cited_by_msm_count = cited_by_msm_count;
  }


  public void setCited_by_peer_review_sites_count(String cited_by_peer_review_sites_count) {
    this.cited_by_peer_review_sites_count = cited_by_peer_review_sites_count;
  }


  public void setCited_by_pinners_count(String cited_by_pinners_count) {
    this.cited_by_pinners_count = cited_by_pinners_count;
  }


  public void setCited_by_policies_count(String cited_by_policies_count) {
    this.cited_by_policies_count = cited_by_policies_count;
  }


  public void setCited_by_posts_count(String cited_by_posts_count) {
    this.cited_by_posts_count = cited_by_posts_count;
  }


  public void setCited_by_qs_count(String cited_by_qs_count) {
    this.cited_by_qs_count = cited_by_qs_count;
  }


  public void setCited_by_rdts_count(String cited_by_rdts_count) {
    this.cited_by_rdts_count = cited_by_rdts_count;
  }


  public void setCited_by_rh_count(String cited_by_rh_count) {
    this.cited_by_rh_count = cited_by_rh_count;
  }


  public void setCited_by_tweeters_count(String cited_by_tweeters_count) {
    this.cited_by_tweeters_count = cited_by_tweeters_count;
  }


  public void setCited_by_videos_count(String cited_by_videos_count) {
    this.cited_by_videos_count = cited_by_videos_count;
  }


  public void setCited_by_weibo_count(String cited_by_weibo_count) {
    this.cited_by_weibo_count = cited_by_weibo_count;
  }


  public void setCited_by_wikipedia_count(String cited_by_wikipedia_count) {
    this.cited_by_wikipedia_count = cited_by_wikipedia_count;
  }


  public void setDoi(String doi) {
    this.doi = doi;
  }


  public void setHandle(String handle) {
    this.handle = handle;
  }


  public void setImages(PublicationAlmetricsImages images) {
    this.images = images;
  }


  public void setIs_oa(boolean is_oa) {
    this.is_oa = is_oa;
  }


  public void setJournal(String journal) {
    this.journal = journal;
  }


  public void setLast_updated(String last_updated) {
    this.last_updated = last_updated;
  }


  public void setPublished_on(String published_on) {
    this.published_on = published_on;
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
