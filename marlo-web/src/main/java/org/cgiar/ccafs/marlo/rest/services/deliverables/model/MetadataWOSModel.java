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
import org.apache.commons.lang3.builder.ToStringBuilder;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class MetadataWOSModel implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 4540658318954192431L;

  private String url;
  private String title;
  private String doi;
  private String publicationType;
  private Integer publicationYear;

  @SerializedName("is_oa")
  private String isOpenAccess;

  @SerializedName("oa_link")
  private String openAcessLink;

  @SerializedName("is_isi")
  private String isISI;

  private String journalName;
  private String volume;
  private String issue;
  private String pages;
  private String source;
  private List<WOSAuthor> authors;

  @SerializedName("organizations")
  private List<WOSInstitution> institutions;

  @SerializedName("gardian")
  MetadataGardianModel gardianInfo;

  @SerializedName("altmetric")
  MetadataAltmetricModel altmetricInfo;

  @SerializedName("handle_altmetric")
  MetadataAltmetricModel2 altmetricInfo2;

  public MetadataWOSModel() {

  }

  public MetadataAltmetricModel getAltmetricInfo() {
    return altmetricInfo;
  }

  public MetadataAltmetricModel2 getAltmetricInfo2() {
    return altmetricInfo2;
  }

  public List<WOSAuthor> getAuthors() {
    return authors;
  }

  public String getDoi() {
    return doi;
  }

  public MetadataGardianModel getGardianInfo() {
    return gardianInfo;
  }

  public List<WOSInstitution> getInstitutions() {
    return institutions;
  }

  public String getIsISI() {
    return isISI;
  }

  public String getIsOpenAccess() {
    return isOpenAccess;
  }

  public String getIssue() {
    return issue;
  }

  public String getJournalName() {
    return journalName;
  }

  public String getOpenAcessLink() {
    return openAcessLink;
  }

  public String getPages() {
    return pages;
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

  public void setAltmetricInfo(MetadataAltmetricModel altmetricInfo) {
    this.altmetricInfo = altmetricInfo;
  }

  public void setAltmetricInfo2(MetadataAltmetricModel2 altmetricInfo2) {
    this.altmetricInfo2 = altmetricInfo2;
  }

  public void setAuthors(List<WOSAuthor> authors) {
    this.authors = authors;
  }

  public void setDoi(String doi) {
    this.doi = doi;
  }

  public void setGardianInfo(MetadataGardianModel gardianInfo) {
    this.gardianInfo = gardianInfo;
  }

  public void setInstitutions(List<WOSInstitution> organizations) {
    this.institutions = organizations;
  }

  public void setIsISI(String isISI) {
    this.isISI = isISI;
  }

  public void setIsOpenAccess(String isOA) {
    this.isOpenAccess = isOA;
  }

  public void setIssue(String issue) {
    this.issue = issue;
  }

  public void setJournalName(String journalName) {
    this.journalName = journalName;
  }

  public void setOpenAcessLink(String openAcessLink) {
    this.openAcessLink = openAcessLink;
  }

  public void setPages(String pages) {
    this.pages = pages;
  }

  public void setPublicationType(String publicationTitle) {
    this.publicationType = publicationTitle;
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
    return ToStringBuilder.reflectionToString(this);
  }
}