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

package org.cgiar.ccafs.marlo.rest.dto;

import java.util.List;

public class PublicationsWOSDTO {

  private String url;

  private String title;

  private String doi;

  private String publicationType;

  private int publicationYear;

  private boolean is_oa;

  private String oa_link;

  private boolean is_isi;

  private String journalName;

  private String volume;

  private String pages;

  private List<AuthorsWOSDTO> authors;

  private List<InstitutionsWOSDTO> organizations;


  public List<AuthorsWOSDTO> getAuthors() {
    return authors;
  }


  public String getDoi() {
    return doi;
  }


  public String getJournalName() {
    return journalName;
  }


  public String getOa_link() {
    return oa_link;
  }


  public List<InstitutionsWOSDTO> getOrganizations() {
    return organizations;
  }


  public String getPages() {
    return pages;
  }


  public String getPublicationType() {
    return publicationType;
  }


  public int getPublicationYear() {
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


  public boolean isIs_isi() {
    return is_isi;
  }


  public boolean isIs_oa() {
    return is_oa;
  }


  public void setAuthors(List<AuthorsWOSDTO> authors) {
    this.authors = authors;
  }


  public void setDoi(String doi) {
    this.doi = doi;
  }


  public void setIs_isi(boolean is_isi) {
    this.is_isi = is_isi;
  }


  public void setIs_oa(boolean is_oa) {
    this.is_oa = is_oa;
  }


  public void setJournalName(String journalName) {
    this.journalName = journalName;
  }


  public void setOa_link(String oa_link) {
    this.oa_link = oa_link;
  }


  public void setOrganizations(List<InstitutionsWOSDTO> organizations) {
    this.organizations = organizations;
  }


  public void setPages(String pages) {
    this.pages = pages;
  }


  public void setPublicationType(String publicationType) {
    this.publicationType = publicationType;
  }


  public void setPublicationYear(int publicationYear) {
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

}
