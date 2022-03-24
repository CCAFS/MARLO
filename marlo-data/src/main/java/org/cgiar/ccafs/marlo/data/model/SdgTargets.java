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

package org.cgiar.ccafs.marlo.data.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SdgTargets implements java.io.Serializable {

  private static final long serialVersionUID = 6128814462106164029L;
  private Long id;
  private String target_code;
  private String target;
  private Sdg sdg;
  private Set<ProjectExpectedStudySdgTarget> projectExpectedStudySdgTargets =
    new HashSet<ProjectExpectedStudySdgTarget>(0);
  private List<ProjectExpectedStudySdgTarget> studySdgTargets;

  public SdgTargets() {
    super();
  }


  public SdgTargets(long id, String target_code, String target, Sdg sdg) {
    super();
    this.id = id;
    this.target_code = target_code;
    this.target = target;
    this.sdg = sdg;
  }


  public String getComposedName() {
    String composedName = "";
    if (this.getId() == null || this.getId() == -1) {
      return "<Not defined>";
    } else {
      if (this.getTarget_code() != null && !this.getTarget_code().isEmpty()) {
        composedName = this.getTarget_code();
      }
      if (this.getTarget() != null && !this.getTarget().isEmpty()) {
        composedName += " -  " + this.getTarget();
      }
    }

    return composedName;
  }

  public Long getId() {
    return id;
  }

  public Set<ProjectExpectedStudySdgTarget> getProjectExpectedStudySdgTargets() {
    return projectExpectedStudySdgTargets;
  }

  public Sdg getSdg() {
    return sdg;
  }

  public List<ProjectExpectedStudySdgTarget> getStudySdgTargets() {
    return studySdgTargets;
  }

  public List<ProjectExpectedStudySdgTarget> getStudySdgTargets(Phase phase) {
    return new ArrayList<>(this.getProjectExpectedStudySdgTargets().stream()
      .filter(pp -> pp.isActive() && pp.getPhase().equals(phase) && pp.getProjectExpectedStudy() != null
        && pp.getProjectExpectedStudy().getProjectExpectedStudyInfo(phase) != null)
      .collect(Collectors.toList()));
  }

  public String getTarget() {
    return target;
  }

  public String getTarget_code() {
    return target_code;
  }


  public void setId(Long id) {
    this.id = id;
  }

  public void setProjectExpectedStudySdgTargets(Set<ProjectExpectedStudySdgTarget> projectExpectedStudySdgTargets) {
    this.projectExpectedStudySdgTargets = projectExpectedStudySdgTargets;
  }

  public void setSdg(Sdg sdg) {
    this.sdg = sdg;
  }

  public void setStudySdgTargets(List<ProjectExpectedStudySdgTarget> studySdgTargets) {
    this.studySdgTargets = studySdgTargets;
  }

  public void setTarget(String target) {
    this.target = target;
  }

  public void setTarget_code(String target_code) {
    this.target_code = target_code;
  }
}
