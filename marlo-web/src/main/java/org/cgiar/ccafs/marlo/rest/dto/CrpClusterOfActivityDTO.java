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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CrpClusterOfActivityDTO implements java.io.Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 3652807154306957170L;
  private CrpProgramDTO crpProgram;
  private String description;
  private PhaseDTO phase;
  private String identifier;
  private List<CrpClusterKeyOutputDTO> keyOutputs;
  private List<CrpClusterActivityLeaderDTO> leaders;

  private Set<ProjectClusterActivityDTO> projectClusterActivities = new HashSet<ProjectClusterActivityDTO>(0);


  public CrpProgramDTO getCrpProgram() {
    return crpProgram;
  }


  public String getDescription() {
    return description;
  }


  public String getIdentifier() {
    return identifier;
  }


  public List<CrpClusterKeyOutputDTO> getKeyOutputs() {
    return keyOutputs;
  }


  public List<CrpClusterActivityLeaderDTO> getLeaders() {
    return leaders;
  }


  public PhaseDTO getPhase() {
    return phase;
  }


  public Set<ProjectClusterActivityDTO> getProjectClusterActivities() {
    return projectClusterActivities;
  }


  public void setCrpProgram(CrpProgramDTO crpProgram) {
    this.crpProgram = crpProgram;
  }


  public void setDescription(String description) {
    this.description = description;
  }


  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }


  public void setKeyOutputs(List<CrpClusterKeyOutputDTO> keyOutputs) {
    this.keyOutputs = keyOutputs;
  }


  public void setLeaders(List<CrpClusterActivityLeaderDTO> leaders) {
    this.leaders = leaders;
  }


  public void setPhase(PhaseDTO phase) {
    this.phase = phase;
  }


  public void setProjectClusterActivities(Set<ProjectClusterActivityDTO> projectClusterActivities) {
    this.projectClusterActivities = projectClusterActivities;
  }


}
