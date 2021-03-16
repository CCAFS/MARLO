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

public class PublicationGardianWOSDTO {

  @ApiModelProperty(notes = "Findability principles")
  private String findability;

  @ApiModelProperty(notes = "Accessibility principles")
  private String accessibility;

  @ApiModelProperty(notes = "Interoperability principles")
  private String interoperability;

  @ApiModelProperty(notes = "Reusability principles")
  private String reusability;


  @ApiModelProperty(notes = "Publication title")
  private String title;


  public String getAccessibility() {
    return accessibility;
  }


  public String getFindability() {
    return findability;
  }


  public String getInteroperability() {
    return interoperability;
  }


  public String getReusability() {
    return reusability;
  }


  public String getTitle() {
    return title;
  }


  public void setAccessibility(String accessibility) {
    this.accessibility = accessibility;
  }


  public void setFindability(String findability) {
    this.findability = findability;
  }


  public void setInteroperability(String interoperability) {
    this.interoperability = interoperability;
  }


  public void setReusability(String reusability) {
    this.reusability = reusability;
  }

  public void setTitle(String title) {
    this.title = title;
  }

}
