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

import com.fasterxml.jackson.annotation.JsonInclude;

public class ProjectPageStudiesDTO {

  private Long id;

  private String title;

  private Integer year;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String externalLink;


  public String getExternalLink() {
    return externalLink;
  }

  public Long getId() {
    return id;
  }


  public String getTitle() {
    return title;
  }


  public Integer getYear() {
    return year;
  }


  public void setExternalLink(String externalLink) {
    this.externalLink = externalLink;
  }


  public void setId(Long id) {
    this.id = id;
  }


  public void setTitle(String title) {
    this.title = title;
  }


  public void setYear(Integer year) {
    this.year = year;
  }

}
