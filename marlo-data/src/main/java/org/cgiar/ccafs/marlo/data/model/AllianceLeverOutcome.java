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

import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.annotations.Expose;
import org.apache.commons.lang3.StringUtils;

public class AllianceLeverOutcome extends MarloBaseEntity implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = 2591192504126616140L;

  @Expose
  private AllianceLever leverCategory;

  @Expose
  private String name;

  @Expose
  private String indicator;

  private Set<ProjectExpectedStudyLeverOutcome> projectExpectedStudyLeverOutcomes =
    new HashSet<ProjectExpectedStudyLeverOutcome>(0);

  private List<ProjectExpectedStudyLeverOutcome> studyLeverOutcomes;

  public AllianceLeverOutcome() {
  }

  public String getComposedName() {
    String composedName = "";
    if (this.getId() == null || this.getId() == -1) {
      return "<Not defined>";
    } else {
      if (StringUtils.isNotBlank(this.getIndicator())) {
        composedName += "Lever " + StringUtils.trimToEmpty(this.getIndicator());
      }
      if (StringUtils.isNotBlank(this.getName())) {
        composedName += " - " + StringUtils.trimToEmpty(this.getName());
      }
    }

    return composedName;
  }

  public String getIndicator() {
    return indicator;
  }

  public AllianceLever getLeverCategory() {
    return leverCategory;
  }

  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }

  @Override
  public String getModificationJustification() {

    return "";
  }

  @Override
  public User getModifiedBy() {
    User u = new User();
    u.setId(new Long(3));
    return u;
  }

  public String getName() {
    return this.name;
  }

  public Set<ProjectExpectedStudyLeverOutcome> getProjectExpectedStudyLeverOutcomes() {
    return projectExpectedStudyLeverOutcomes;
  }

  public List<ProjectExpectedStudyLeverOutcome> getStudyLeverOutcomes() {
    return studyLeverOutcomes;
  }

  public List<ProjectExpectedStudyLeverOutcome> getStudyLeverOutcomes(Phase phase) {
    return new ArrayList<>(this.getProjectExpectedStudyLeverOutcomes().stream()
      .filter(pp -> pp.isActive() && pp.getPhase().equals(phase) && pp.getProjectExpectedStudy() != null
        && pp.getProjectExpectedStudy().getProjectExpectedStudyInfo(phase) != null)
      .collect(Collectors.toList()));
  }

  @Override
  public boolean isActive() {
    return true;
  }


  public void setIndicator(String indicator) {
    this.indicator = indicator;
  }

  public void setLeverCategory(AllianceLever leverCategory) {
    this.leverCategory = leverCategory;
  }

  @Override
  public void setModifiedBy(User modifiedBy) {
  }

  public void setName(String name) {
    this.name = name;
  }

  public void
    setProjectExpectedStudyLeverOutcomes(Set<ProjectExpectedStudyLeverOutcome> projectExpectedStudyLeverOutcomes) {
    this.projectExpectedStudyLeverOutcomes = projectExpectedStudyLeverOutcomes;
  }

  public void setStudyLeverOutcomes(List<ProjectExpectedStudyLeverOutcome> studyLeverOutcomes) {
    this.studyLeverOutcomes = studyLeverOutcomes;
  }

}
