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

package org.cgiar.ccafs.marlo.data.dto;

import java.util.List;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class ProjectHomeDTO {

  private Long projectId;
  private String title;
  private String status;
  private List<String> programType;

  public ProjectHomeDTO() {
  }

  public ProjectHomeDTO(Long projectId, String title, String status, List<String> programType) {
    super();
    this.projectId = projectId;
    this.title = title;
    this.programType = programType;
    this.status = status;
  }

  public List<String> getProgramType() {
    return programType;
  }

  public Long getProjectId() {
    return projectId;
  }

  public String getStatus() {
    return status;
  }

  public String getTitle() {
    return title;
  }

  public void setProgramType(List<String> programType) {
    this.programType = programType;
  }

  public void setProjectId(Long projectId) {
    this.projectId = projectId;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public void setTitle(String title) {
    this.title = title;
  }


}
