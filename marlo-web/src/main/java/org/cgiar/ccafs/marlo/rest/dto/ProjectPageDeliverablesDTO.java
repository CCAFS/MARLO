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

import io.swagger.annotations.ApiModelProperty;

public class ProjectPageDeliverablesDTO {

  @ApiModelProperty(notes = "Publication ID", position = 1)
  private Long id;

  @ApiModelProperty(notes = "Publication Subcategory", position = 2)
  private String subcategory;

  @ApiModelProperty(notes = "Publication Title", position = 3)
  private String title;

  @ApiModelProperty(notes = "Publication Year", position = 4)
  private String year;


  @ApiModelProperty(notes = "FAIR principles (Findable)", position = 5)
  private Boolean findable;


  @ApiModelProperty(notes = "FAIR principles (Accesible)", position = 6)
  private Boolean accesible;

  @ApiModelProperty(notes = "FAIR principles (Findable)", position = 7)
  private Boolean interoperable;

  @ApiModelProperty(notes = "FAIR principles (Findable)", position = 8)
  private Boolean reusable;

  @ApiModelProperty(notes = "External Link", position = 9)
  private String externalLink;

  public Boolean getAccesible() {
    return accesible;
  }


  public String getExternalLink() {
    return externalLink;
  }


  public Boolean getFindable() {
    return findable;
  }

  public Long getId() {
    return id;
  }

  public Boolean getInteroperable() {
    return interoperable;
  }


  public Boolean getReusable() {
    return reusable;
  }


  public String getSubcategory() {
    return subcategory;
  }


  public String getTitle() {
    return title;
  }


  public String getYear() {
    return year;
  }


  public void setAccesible(Boolean accesible) {
    this.accesible = accesible;
  }


  public void setExternalLink(String externalLink) {
    this.externalLink = externalLink;
  }


  public void setFindable(Boolean findable) {
    this.findable = findable;
  }


  public void setId(Long id) {
    this.id = id;
  }


  public void setInteroperable(Boolean interoperable) {
    this.interoperable = interoperable;
  }


  public void setReusable(Boolean reusable) {
    this.reusable = reusable;
  }


  public void setSubcategory(String subcategory) {
    this.subcategory = subcategory;
  }


  public void setTitle(String title) {
    this.title = title;
  }

  public void setYear(String year) {
    this.year = year;
  }
}
