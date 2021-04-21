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

package org.cgiar.ccafs.marlo.data.model;

import java.io.Serializable;
import java.util.List;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class ReportSynthesisStudiesByRepIndStageStudyDTO implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -6973619401048268992L;

  private RepIndStageStudy repIndStageStudy;


  private List<ProjectExpectedStudy> projectStudies;


  public ReportSynthesisStudiesByRepIndStageStudyDTO() {
  }


  public List<ProjectExpectedStudy> getProjectStudies() {
    return projectStudies;
  }


  public RepIndStageStudy getRepIndStageStudy() {
    return repIndStageStudy;
  }


  public void setProjectStudies(List<ProjectExpectedStudy> projectStudies) {
    this.projectStudies = projectStudies;
  }


  public void setRepIndStageStudy(RepIndStageStudy repIndStageProcess) {
    this.repIndStageStudy = repIndStageProcess;
  }


  @Override
  public String toString() {
    return "ReportSynthesisStudiesByRepIndStageStudyDTO [repIndStageStudy=" + repIndStageStudy + ", projectStudies="
      + projectStudies + "]";
  }


}

