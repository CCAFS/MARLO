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

public class InitiativesDTO {

  @ApiModelProperty(notes = "Initiative ID", position = 1)
  public Long id;
  public String official_code;
  public String name;
  public String status;
  public String action_area_id;
  public String action_area_description;
  public int active;
  public String short_name;


  public Long stageId;


  public String description;


  public List<StagesDTO> stages;

  public String getAction_area_description() {
    return action_area_description;
  }


  public String getAction_area_id() {
    return action_area_id;
  }

  public int getActive() {
    return active;
  }

  public String getDescription() {
    return description;
  }


  public Long getId() {
    return id;
  }


  public String getName() {
    return name;
  }


  public String getOfficial_code() {
    return official_code;
  }


  public String getShort_name() {
    return short_name;
  }


  public Long getStageId() {
    return stageId;
  }


  public List<StagesDTO> getStages() {
    return stages;
  }


  public String getStatus() {
    return status;
  }


  public void setAction_area_description(String action_area_description) {
    this.action_area_description = action_area_description;
  }


  public void setAction_area_id(String action_area_id) {
    this.action_area_id = action_area_id;
  }


  public void setActive(int active) {
    this.active = active;
  }


  public void setDescription(String description) {
    this.description = description;
  }


  public void setId(Long id) {
    this.id = id;
  }


  public void setName(String name) {
    this.name = name;
  }


  public void setOfficial_code(String official_code) {
    this.official_code = official_code;
  }


  public void setShort_name(String short_name) {
    this.short_name = short_name;
  }


  public void setStageId(Long stageId) {
    this.stageId = stageId;
  }


  public void setStages(List<StagesDTO> stages) {
    this.stages = stages;
  }


  public void setStatus(String status) {
    this.status = status;
  }


}
