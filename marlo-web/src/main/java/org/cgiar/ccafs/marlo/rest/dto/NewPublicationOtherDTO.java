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

public class NewPublicationOtherDTO {

  @ApiModelProperty(notes = "Deliverable title", position = 1)
  private String title;
  @ApiModelProperty(notes = "Deliverable type", position = 2)
  private Long type;
  @ApiModelProperty(notes = "Deliverable Author List", position = 3)
  private List<DeliverableUserDTO> authorlist;
  @ApiModelProperty(notes = "Publication Authors Field", position = 3)
  private String authors;
  @ApiModelProperty(notes = "Year Of publication", position = 4)
  private Integer year;
  @ApiModelProperty(notes = "Is open access", position = 5)
  private Boolean isOpenAccess;
  @ApiModelProperty(notes = "Digital object identifier", position = 6)
  private String doi;
  @ApiModelProperty(notes = "Handle URL", position = 7)
  private String handle;
  @ApiModelProperty(notes = "Article URL", position = 8)
  private String articleURL;
  @ApiModelProperty(notes = "Phase", position = 9)
  private PhaseDTO phase;


  public String getArticleURL() {
    return articleURL;
  }


  public List<DeliverableUserDTO> getAuthorlist() {
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

  public Boolean getIsOpenAccess() {
    return isOpenAccess;
  }

  public PhaseDTO getPhase() {
    return phase;
  }

  public String getTitle() {
    return title;
  }

  public Long getType() {
    return type;
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

  public void setIsOpenAccess(Boolean isOpenAccess) {
    this.isOpenAccess = isOpenAccess;
  }

  public void setPhase(PhaseDTO phase) {
    this.phase = phase;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setType(Long type) {
    this.type = type;
  }

  public void setYear(Integer year) {
    this.year = year;
  }

}
