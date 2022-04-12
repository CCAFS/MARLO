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

import io.swagger.annotations.ApiModelProperty;

public class PublicationsWOSDTO {

  @ApiModelProperty(notes = "Publication URL", position = 1)
  private String url;
  @ApiModelProperty(notes = "Publication Title", position = 2)
  private String title;
  @ApiModelProperty(notes = "Publication DOI", position = 3)
  private String doi;
  @ApiModelProperty(notes = "WOS publication type", position = 4)
  private String publicationType;

  @ApiModelProperty(notes = "Source", position = 4)
  private String source;


  @ApiModelProperty(notes = "Publication year", position = 5)
  private int publicationYear;


  @ApiModelProperty(notes = "Publication is open access", position = 6)
  private String is_oa;


  @ApiModelProperty(notes = "Publication open access link", position = 7)
  private String oa_link;

  @ApiModelProperty(notes = "Publication is ISI", position = 8)
  private String is_isi;
  @ApiModelProperty(notes = "Publication Journal name", position = 9)
  private String journalName;
  @ApiModelProperty(notes = "Publication Volume", position = 10)
  private String volume;
  @ApiModelProperty(notes = "Publication Issue", position = 11)
  private String issue;

  @ApiModelProperty(notes = "Publication Pages", position = 12)
  private String pages;
  @ApiModelProperty(notes = "Publication Authors", position = 13)
  private List<AuthorsWOSDTO> authors;
  @ApiModelProperty(notes = "Publication Institutions", position = 14)
  private List<InstitutionsWOSDTO> organizations;
  @ApiModelProperty(notes = "Publication Altmetrics Details", position = 15)
  private PublicationAltmetricsWOSDTO altmetric;
  @ApiModelProperty(notes = "Publication gardian", position = 16)
  private PublicationGardianWOSDTO gardian;

  public PublicationAltmetricsWOSDTO getAltmetric() {
    return altmetric;
  }

  public List<AuthorsWOSDTO> getAuthors() {
    return authors;
  }


  public String getDoi() {
    return doi;
  }

  public PublicationGardianWOSDTO getGardian() {
    return gardian;
  }

  public String getIs_isi() {
    return is_isi;
  }


  public String getIs_oa() {
    return is_oa;
  }


  public String getIssue() {
    return issue;
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


  public void setAltmetric(PublicationAltmetricsWOSDTO altmetric) {
    this.altmetric = altmetric;
  }


  public void setAuthors(List<AuthorsWOSDTO> authors) {
    this.authors = authors;
  }


  public void setDoi(String doi) {
    this.doi = doi;
  }


  public void setGardian(PublicationGardianWOSDTO gardian) {
    this.gardian = gardian;
  }


  public void setIs_isi(String is_isi) {
    this.is_isi = is_isi;
  }


  public void setIs_oa(String is_oa) {
    this.is_oa = is_oa;
  }


  public void setIssue(String issue) {
    this.issue = issue;
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

}
