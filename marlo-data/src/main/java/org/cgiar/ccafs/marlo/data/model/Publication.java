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

import java.util.List;

import com.google.gson.annotations.Expose;

public class Publication implements java.io.Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 3913774940243899976L;

  @Expose
  private Long Id;
  @Expose
  private String title;
  @Expose
  private String journal;
  @Expose
  private String volume;
  @Expose
  private String issue;
  @Expose
  private String npages;
  @Expose
  private Boolean isOpenAccess;
  @Expose
  private boolean isISIJournal;
  @Expose
  private String doi;
  @Expose
  private String handle;
  @Expose
  private Phase phase;
  @Expose
  private int year;
  @Expose
  private String authors;


  private List<DeliverableUser> authorlist;


  public List<DeliverableUser> getAuthorlist() {
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


  public Long getId() {
    return Id;
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


  public Phase getPhase() {
    return phase;
  }


  public String getTitle() {
    return title;
  }


  public String getVolume() {
    return volume;
  }


  public int getYear() {
    return year;
  }


  public boolean isISIJournal() {
    return isISIJournal;
  }


  public void setAuthorlist(List<DeliverableUser> authorlist) {
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


  public void setId(Long id) {
    Id = id;
  }


  public void setISIJournal(boolean isISIJournal) {
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


  public void setPhase(Phase phase) {
    this.phase = phase;
  }


  public void setTitle(String title) {
    this.title = title;
  }


  public void setVolume(String volume) {
    this.volume = volume;
  }


  public void setYear(int year) {
    this.year = year;
  }


}
