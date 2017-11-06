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

import java.util.Date;

import org.apache.struts2.json.annotations.JSON;

public class MetadataModel {

  private Author[] author;

  private String citation;

  private String title;

  private String handle;

  private String keywords;

  private String description;

  private String rights;

  private String language;

  private String openAccess;

  private String doi;

  private Date publicationDate;

  private String country;

  public Author[] getAuthors() {
    return author;
  }

  public String getCitation() {
    return citation;
  }

  public String getCountry() {
    return country;
  }

  public String getDescription() {
    return description;
  }

  public String getDoi() {
    return doi;
  }

  public String getHandle() {
    return handle;
  }

  public String getKeywords() {
    return keywords;
  }

  public String getLanguage() {
    return language;
  }

  public String getOpenAccess() {
    return openAccess;
  }

  @JSON(format = "yyyy-MM-dd")
  public Date getPublicationDate() {
    return publicationDate;
  }

  public String getRights() {
    return rights;
  }

  public String getTitle() {
    return title;
  }

  public void setAuthors(Author[] authors) {
    this.author = authors;
  }

  public void setCitation(String citation) {
    this.citation = citation;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setDoi(String doi) {
    this.doi = doi;
  }

  public void setHandle(String handle) {
    this.handle = handle;
  }

  public void setKeywords(String keywords) {
    this.keywords = keywords;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public void setOpenAccess(String openAccess) {
    this.openAccess = openAccess;
  }

  public void setPublicationDate(Date publicationDate) {
    this.publicationDate = publicationDate;
  }

  public void setRights(String rights) {
    this.rights = rights;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public String toString() {
    return "MetadataModel [author = " + author + ", citation = " + citation + ", title = " + title + ", handle = "
      + handle + ", keywords = " + keywords + ", description = " + description + ", rights = " + rights
      + ", language = " + language + ", openAccess = " + openAccess + ", doi = " + doi + ", publicationDate = "
      + publicationDate + ", country = " + country + "]";
  }
}
