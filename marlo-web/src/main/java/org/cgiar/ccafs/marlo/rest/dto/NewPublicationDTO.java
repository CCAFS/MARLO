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

package org.cgiar.ccafs.marlo.rest.dto;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;

public class NewPublicationDTO {

  @ApiModelProperty(notes = "Deliverable Author List")
  private List<DeliverableUserDTO> authorlist;
  @ApiModelProperty(notes = "Deliverable title")
  private String title;
  @ApiModelProperty(notes = "Jorunal/Publish Name")
  private String journal;
  @ApiModelProperty(notes = "Deliverable Volume")
  private String volume;
  @ApiModelProperty(notes = "Deliverable issue")
  private String issue;
  @ApiModelProperty(notes = "Number of pages")
  private String npages;
  @ApiModelProperty(notes = "Is open access")
  private Boolean isOpenAccess;
  @ApiModelProperty(notes = "Is this journal article an ISI publication?")
  private Boolean isISIJournal;
  @ApiModelProperty(notes = "Digital object identifier")
  private String doi;
  @ApiModelProperty(notes = "Handle URL")
  private String handle;
  @ApiModelProperty(notes = "Article URL")
  private String articleURL;


  @ApiModelProperty(notes = "Phase")
  private PhaseDTO phase;


  @ApiModelProperty(notes = "Year Of publication")
  private Integer year;


  @ApiModelProperty(notes = "Publication Authors Field")
  private String authors;


  public String getArticleURL() {
    return articleURL;
  }


  public List<DeliverableUserDTO> getAuthorList() {
    return authorlist;
  }


  public String getAuthors() {
    return authors;
  }

  public String getDoi() {
    return doi;
  }

  public String getHandle() {
    return handle;
  }

  public Boolean getIsISIJournal() {
    return isISIJournal;
  }

  public Boolean getIsOpenAccess() {
    return isOpenAccess;
  }

  public String getIssue() {
    return issue;
  }

  public String getJournal() {
    return journal;
  }

  public String getNpages() {
    return npages;
  }

  public PhaseDTO getPhase() {
    return phase;
  }

  public String getTitle() {
    return title;
  }


  public String getVolume() {
    return volume;
  }

  public Integer getYear() {
    return year;
  }


  public void setArticleURL(String articleURL) {
    this.articleURL = articleURL;
  }

  public void setAuthorlist(List<DeliverableUserDTO> authorlist) {
    this.authorlist = authorlist;
  }


  public void setAuthors(String authors) {
    this.authors = authors;
  }

  public void setDoi(String doi) {
    this.doi = doi;
  }

  public void setHandle(String handle) {
    this.handle = handle;
  }


  public void setIsISIJournal(Boolean isISIJournal) {
    this.isISIJournal = isISIJournal;
  }


  public void setIsOpenAccess(Boolean isOpenAccess) {
    this.isOpenAccess = isOpenAccess;
  }

  public void setIssue(String issue) {
    this.issue = issue;
  }


  public void setJournal(String journal) {
    this.journal = journal;
  }


  public void setNpages(String npages) {
    this.npages = npages;
  }


  public void setPhase(PhaseDTO phase) {
    this.phase = phase;
  }


  public void setTitle(String title) {
    this.title = title;
  }


  public void setVolume(String volume) {
    this.volume = volume;
  }


  public void setYear(Integer year) {
    this.year = year;
  }


}
