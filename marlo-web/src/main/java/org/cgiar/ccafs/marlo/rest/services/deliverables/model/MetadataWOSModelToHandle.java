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

public class MetadataWOSModelToHandle implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 4540658318954192431L;


  @SerializedName("Title")
  private String title;

  @SerializedName("DOI")
  private String doi;

  @SerializedName("Type")
  private String publicationType;

  @SerializedName("Online publication date")
  private String publicationYear;

  @SerializedName("Open Access")
  private String isOpenAccess;


  private String openAcessLink;

  @SerializedName("ISI")
  private String isISI;

  @SerializedName("Journal")
  private String journalName;

  private String volume;

  @SerializedName("name")
  private String issue;

  @SerializedName("Pages")
  private String pages;

  @SerializedName("repo")
  private String source;

  @SerializedName("Authors")
  private List<String> authors;

  @SerializedName("Affiliation")
  private List<WOSInstitutionToHandle> institutions;

  @SerializedName("gardian")
  MetadataGardianModel gardianInfo;

  @SerializedName("altmetric")
  MetadataAltmetricModel altmetricInfo;

  @SerializedName("handle_altmetric")
  MetadataAltmetricModel2 altmetricInfo2;

  @SerializedName("Handle")
  private String url;


  public MetadataWOSModelToHandle() {

  }


  public MetadataAltmetricModel getAltmetricInfo() {
    return altmetricInfo;
  }


  public MetadataAltmetricModel2 getAltmetricInfo2() {
    return altmetricInfo2;
  }


  public List<String> getAuthors() {
    return authors;
  }

  public String getDoi() {
    return doi;
  }

  public MetadataGardianModel getGardianInfo() {
    return gardianInfo;
  }

  public String getHandle() {
    return url;
  }

  public List<WOSInstitutionToHandle> getInstitutions() {
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

  public String getPublicationYear() {
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

  public void setAuthors(List<String> authors) {
    this.authors = authors;
  }

  public void setDoi(String doi) {
    this.doi = doi;
  }

  public void setGardianInfo(MetadataGardianModel gardianInfo) {
    this.gardianInfo = gardianInfo;
  }

  public void setHandle(String handle) {
    this.url = handle;
  }

  public void setInstitutions(List<WOSInstitutionToHandle> organizations) {
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

  public void setPublicationYear(String publicationYear) {
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