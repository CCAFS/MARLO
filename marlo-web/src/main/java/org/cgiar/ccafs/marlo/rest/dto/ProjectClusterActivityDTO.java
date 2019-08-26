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

import org.cgiar.ccafs.marlo.data.model.Project;

public class ProjectClusterActivityDTO {

  private CrpClusterOfActivityDTO crpClusterOfActivity;
  private Project projectDTO;
  private PhaseDTO phase;

  public ProjectClusterActivityDTO() {
    super();
  }

  public CrpClusterOfActivityDTO getCrpClusterOfActivity() {
    return crpClusterOfActivity;
  }

  public PhaseDTO getPhase() {
    return phase;
  }

  public Project getProjectDTO() {
    return projectDTO;
  }

  public void setCrpClusterOfActivity(CrpClusterOfActivityDTO crpClusterOfActivity) {
    this.crpClusterOfActivity = crpClusterOfActivity;
  }

  public void setPhase(PhaseDTO phase) {
    this.phase = phase;
  }

  public void setProjectDTO(Project projectDTO) {
    this.projectDTO = projectDTO;
  }


}
