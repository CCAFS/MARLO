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

public class ReportSynthesisStudiesByCrpProgramDTO implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1904047692010861597L;

  private CrpProgram crpProgram;
  private List<ProjectExpectedStudy> projectStudies;

  public ReportSynthesisStudiesByCrpProgramDTO() {
  }


  public CrpProgram getCrpProgram() {
    return crpProgram;
  }

  public List<ProjectExpectedStudy> getProjectStudies() {
    return projectStudies;
  }


  public void setCrpProgram(CrpProgram crpProgram) {
    this.crpProgram = crpProgram;
  }

  public void setProjectStudies(List<ProjectExpectedStudy> projectStudies) {
    this.projectStudies = projectStudies;
  }


  @Override
  public String toString() {
    return "ReportSynthesisStudiesByCrpProgramDTO [crpProgram=" + crpProgram + ", projectStudies=" + projectStudies
      + "]";
  }

}