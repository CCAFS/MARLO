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


/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class ProjectHomeDTO {

  private Long projectId;
  private String title;


  public ProjectHomeDTO() {
  }

  public ProjectHomeDTO(Long projectId, String title) {
    super();
    this.projectId = projectId;
    this.title = title;
  }

  public Long getProjectId() {
    return projectId;
  }

  public String getTitle() {
    return title;
  }

  public void setProjectId(Long projectId) {
    this.projectId = projectId;
  }

  public void setTitle(String title) {
    this.title = title;
  }


}
