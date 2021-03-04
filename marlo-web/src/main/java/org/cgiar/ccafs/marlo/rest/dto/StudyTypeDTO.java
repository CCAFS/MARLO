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

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;

public class StudyTypeDTO {

  @ApiModelProperty(notes = "Study Type ID")
  @NotNull
  private Long code;

  @ApiModelProperty(notes = "Study Type name")
  @NotNull
  private String name;

  @ApiModelProperty(notes = "Key Identifier")
  private String key_identifier;


  @ApiModelProperty(notes = "Study Narrative")
  private String narrative;


  @ApiModelProperty(notes = "Study Example")
  private String example;


  public Long getCode() {
    return this.code;
  }


  public String getExample() {
    return example;
  }


  public String getKey_identifier() {
    return key_identifier;
  }


  public String getName() {
    return this.name;
  }

  public String getNarrative() {
    return narrative;
  }

  public void setCode(Long code) {
    this.code = code;
  }

  public void setExample(String example) {
    this.example = example;
  }

  public void setKey_identifier(String key_identifier) {
    this.key_identifier = key_identifier;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setNarrative(String narrative) {
    this.narrative = narrative;
  }

}
