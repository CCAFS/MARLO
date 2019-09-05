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

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */


public class ProjectDTO {

  @ApiModelProperty(notes = "Project id", position = 1)
  private Long id;
  @JsonFormat(pattern = "yyyy-MM-dd")
  private Date createDate;

  private ProjectInfoDTO projectInfo;

  private List<CrpProgramDTO> regions;
  private List<CrpProgramDTO> flagships;


  public Date getCreateDate() {
    return createDate;
  }

  public List<CrpProgramDTO> getFlagships() {
    return flagships;
  }

  public Long getId() {
    return id;
  }


  public ProjectInfoDTO getProjectInfo() {
    return projectInfo;
  }

  public List<CrpProgramDTO> getRegions() {
    return regions;
  }

  public void setCreateDate(Date createDate) {
    this.createDate = createDate;
  }

  public void setFlagships(List<CrpProgramDTO> flagships) {
    this.flagships = flagships;
  }


  public void setId(Long id) {
    this.id = id;
  }


  public void setProjectInfo(ProjectInfoDTO projectInfo) {
    this.projectInfo = projectInfo;
  }

  public void setRegions(List<CrpProgramDTO> regions) {
    this.regions = regions;
  }


}
