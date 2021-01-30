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
  private static final long serialVersionUID = 1L;

  private String url;
  private String title;
  private String doi;
  private String publicationType;
  private Integer publicationYear;

  @SerializedName("is_oa")
  private Boolean isOpenAccess;

  @SerializedName("oa_link")
  private String openAcessLink;

  @SerializedName("is_isi")
  private Boolean isISI;

  private String journalName;
  private String volume;
  private String pages;
  private List<WOSAuthor> authors;

  @SerializedName("organizations")
  private List<WOSInstitution> institutions;

  public MetadataWOSModel() {
  }

  public List<WOSAuthor> getAuthors() {
    return authors;
  }

  public String getDoi() {
    return doi;
  }

  public List<WOSInstitution> getInstitutions() {
    return institutions;
  }

  public Boolean getIsISI() {
    return isISI;
  }

  public Boolean getIsOpenAccess() {
    return isOpenAccess;
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

  public String getTitle() {
    return title;
  }

  public String getUrl() {
    return url;
  }

  public String getVolume() {
    return volume;
  }

  public void setAuthors(List<WOSAuthor> authors) {
    this.authors = authors;
  }

  public void setDoi(String doi) {
    this.doi = doi;
  }

  public void setInstitutions(List<WOSInstitution> organizations) {
    this.institutions = organizations;
  }

  public void setIsISI(Boolean isISI) {
    this.isISI = isISI;
  }

  public void setIsOpenAccess(Boolean isOA) {
    this.isOpenAccess = isOA;
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
